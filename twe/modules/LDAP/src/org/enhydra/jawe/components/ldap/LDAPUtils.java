/**
 * Together Workflow Editor
 * Copyright (C) 2011 Together Teamsolutions Co., Ltd. 
 * 
 * This program is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 *
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * GNU General Public License for more details. 
 *
 * You should have received a copy of the GNU General Public License 
 * along with this program. If not, see http://www.gnu.org/licenses
 */

package org.enhydra.jawe.components.ldap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.ReferralException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;
import org.enhydra.jxpdl.elements.Participant;
import org.enhydra.jxpdl.elements.Participants;

public class LDAPUtils {

   public static void getEntries(LDAPConfiguration ldc,
                                 List objClassChoices,
                                 String selectedObjectClassFilter,
                                 String searchCriteria,
                                 List entries) throws Exception {
      int ldapPort;
      try {
         ldapPort = Integer.valueOf(ldc.get("Port").toValue()).intValue();
      } catch (Exception ex) {
         ldapPort = 389;
      }

      int searchScope;
      if (ldc.get("SearchScope").toValue().equals("SCOPE_ONE")) {
         searchScope = SearchControls.ONELEVEL_SCOPE;
      } else {
         searchScope = SearchControls.SUBTREE_SCOPE;
      }

      // boolean attributeOnly = true; // returns attributes
      // boolean attributeOnly = false; // returns attributes and their values

      String ldapProtocol = ldc.get("Protocol").toValue();
      String ldapHost = ldc.get("Host").toValue();
      String searchBase = ldc.get("BaseDN").toValue();
      String searchFilter;
      int searchType = getSearchType(selectedObjectClassFilter);
      if (searchType == -1) {
         searchFilter = "(|";
         for (int i = 0; i < objClassChoices.size(); i++) {
            String ch = (String) objClassChoices.get(i);
            if (!ch.equals("AllListed")) {
               searchFilter += "(objectClass=" + objClassChoices.get(i) + ")";
            }
         }
         searchFilter += ")";
      } else {
         searchFilter = "(objectClass=" + selectedObjectClassFilter + ")";
      }

      if (!searchCriteria.trim().equals("")) {
         searchFilter = "(&" + searchFilter;
         if (searchType == -1) {
            searchFilter += "(|("
                            + ldc.get("LDAPGroupUniqueAttributeName").toValue() + "="
                            + searchCriteria + ")("
                            + ldc.get("LDAPUserUniqueAttributeName").toValue() + "="
                            + searchCriteria + "))";
         } else {
            String uan = (searchType == 0) ? ldc.get("LDAPUserUniqueAttributeName")
               .toValue() : ldc.get("LDAPGroupUniqueAttributeName").toValue();
            searchFilter += "(" + uan + "=" + searchCriteria + ")";

         }
         searchFilter = searchFilter + ")";
      }

      Properties env = new Properties();
      env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

      String referralHandling = "follow";
      try {
         String rh = JaWEManager.getInstance()
            .getJaWEController()
            .getControllerSettings()
            .getSettingString("LDAPReferralHandling");
         if (rh.equals("throw") || rh.equals("ignore")) {
            referralHandling = rh;
         }
      } catch (Exception ex) {
      }

      env.put(Context.REFERRAL, referralHandling);

      env.put(Context.PROVIDER_URL, ldapProtocol+"://" + ldapHost + ":" + ldapPort);
      env.put("java.naming.ldap.version", "3"); // always use ldap v3

      LDAPSecurity security = (LDAPSecurity) ldc.get("LDAPSecurity");
      // System.err.println("SECURITY="+security.get("Level").toValue());
      if (security.get("Level").toValue().equals("Anonymous")) {
         env.put(Context.SECURITY_AUTHENTICATION, "none");
      } else {
         env.put(Context.SECURITY_AUTHENTICATION, "simple");
         String ldapUser = security.get("UserDN").toValue();
         String ldapPassword = security.get("Password").toValue();
         env.put(Context.SECURITY_PRINCIPAL, ldapUser);
         env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
      }

      int cl = 0;
      try {
         cl = Integer.parseInt(ldc.get("LDAPCountLimit").toValue());
      } catch (Exception ex) {
      }
      int tl = 0;
      try {
         tl = Integer.parseInt(ldc.get("LDAPTimeLimit").toValue());
      } catch (Exception ex) {
      }
      int ps = 1000;
      try {
         ps = Integer.parseInt(ldc.get("LDAPPageSize").toValue());
         if (ps <= 0) {
            ps = Integer.MAX_VALUE;
         }
      } catch (Exception ex) {
      }

      /* Create the initial directory context. */
      LdapContext ctx = new InitialLdapContext(env, null);
      try {

         SearchControls constraints = new SearchControls();
         constraints.setSearchScope(searchScope);
         constraints.setCountLimit(cl);
         constraints.setTimeLimit(tl);

         for (boolean moreReferrals = true; moreReferrals;) {
            byte[] cookie = null;
            ctx.setRequestControls(new Control[] {
               new PagedResultsControl(ps, Control.CRITICAL)
            });

            do {
               try {
                  NamingEnumeration result = ctx.search(searchBase,
                                                        searchFilter,
                                                        constraints);

                  while (result.hasMore()) {
                     SearchResult sr = (SearchResult) result.next();
                     LDAPEntryInfo li = convertLDAPEntryToInfoObject(sr, ldc);
                     if (li != null) {
                        entries.add(li);
                     }
                  }

                  // Examine the paged results control response
                  Control[] controls = ctx.getResponseControls();
                  if (controls != null) {
                     for (int i = 0; i < controls.length; i++) {
                        if (controls[i] instanceof PagedResultsResponseControl) {
                           PagedResultsResponseControl prrc = (PagedResultsResponseControl) controls[i];
                           cookie = prrc.getCookie();
                        }
                     }
                  }
                  // Re-activate paged results
                  ctx.setRequestControls(new Control[] {
                     new PagedResultsControl(ps, cookie, Control.CRITICAL)
                  });

                  // The search completes with no more referrals
                  moreReferrals = false;

               } catch (ReferralException e) {

                  moreReferrals = e.skipReferral();

                  // Point to the new context
                  if (moreReferrals) {
                     ctx = (LdapContext) e.getReferralContext();
                  }
                  break;
               }
            } while (cookie != null);

         }
      } finally {
         ctx.close();
      }

   }

   protected static LDAPEntryInfo convertLDAPEntryToInfoObject(SearchResult entry,
                                                               LDAPConfiguration ldc) {
      try {
         LDAPEntryInfo di = new LDAPEntryInfo();
         Attributes attributes = entry.getAttributes();

         // Iterate through all object classes for entry, and if
         // one with person in it's name is found, take it,
         java.util.Enumeration allObjClasses = attributes.get("objectClass").getAll();
         if (allObjClasses != null) {
            while (allObjClasses.hasMoreElements()) {
               String val = (String) allObjClasses.nextElement();
               if (val.toLowerCase().endsWith("person")
                   || val.toLowerCase().equals("user")) {
                  di.setType(XPDLConstants.PARTICIPANT_TYPE_HUMAN);
                  break;
               } else if (val.equals("organizationalUnit")) {
                  di.setType(XPDLConstants.PARTICIPANT_TYPE_ORGANIZATIONAL_UNIT);
                  break;
               }
            }
         }
         if (di.getType().equals("")) {
            di.setType(XPDLConstants.PARTICIPANT_TYPE_ROLE);
         }

         String id = null;
         String name = "";
         String description = "";

         if (di.getType().equals(XPDLConstants.PARTICIPANT_TYPE_HUMAN)) {
            id = (String) attributes.get(ldc.get("LDAPUserUniqueAttributeName").toValue())
               .get();
         } else {
            id = (String) attributes.get(ldc.get("LDAPGroupUniqueAttributeName")
               .toValue()).get();
         }
         di.setId(id);
         JaWEManager.getInstance()
            .getLoggingManager()
            .debug("LDAPUtils -> Handling entry with Id " + id);
         try {
            if (di.getType().equals(XPDLConstants.PARTICIPANT_TYPE_HUMAN)) {
               name = (String) attributes.get(ldc.get("LDAPUserNameAttributeName")
                  .toValue()).get();
            } else {
               name = (String) attributes.get(ldc.get("LDAPGroupNameAttributeName")
                  .toValue()).get();
            }
         } catch (Exception ex) {
         }
         try {
            if (di.getType().equals(XPDLConstants.PARTICIPANT_TYPE_HUMAN)) {
               description = (String) attributes.get(ldc.get("LDAPUserDescriptionAttributeName")
                  .toValue())
                  .get();
            } else {
               description = (String) attributes.get(ldc.get("LDAPGroupDescriptionAttributeName")
                  .toValue())
                  .get();
            }
         } catch (Exception ex) {
         }
         di.setDescription(description);
         if (name == null) {
            // set name attribute to be the bottom of the tree-structure of DN
            try {
               int ind1 = entry.getName().indexOf("=") + 1;
               int ind2 = entry.getName().indexOf(",");
               if (ind2 == -1)
                  ind2 = entry.getName().length();
               name = entry.getName().substring(ind1, ind2);
            } catch (Exception ex) {
            }
         }
         di.setName(name);
         String dn = "";
         try {
            dn = (String) attributes.get(ldc.get("LDAPDistinguishedNameAttributeName")
               .toValue()).get();
         } catch (Exception ex) {
         }
         di.setDN(dn);
         di.setDetails(entry.toString());

         return di;
      } catch (Exception ex) {
         ex.printStackTrace();
      }
      return null;
   }

   /**
    * Creates participants based on LDAP entries.
    */
   public static void createParticipants(List allEntries) {
      JaWEController jc = JaWEManager.getInstance().getJaWEController();
      Participants ps = jc.getMainPackage().getParticipants();

      Iterator iter = allEntries.iterator();

      List toAdd = new ArrayList();
      while (iter.hasNext()) {
         LDAPEntryInfo nextEntry = (LDAPEntryInfo) iter.next();

         boolean isNew = false;
         Participant p = ps.getParticipant(nextEntry.getId());
         if (p == null) {
            p = (Participant) ps.generateNewElement();
            isNew = true;
         }
         p.setId(nextEntry.getId());
         p.setName(nextEntry.getName());
         p.getParticipantType().getTypeAttribute().setValue(nextEntry.getType());
         p.setDescription(nextEntry.getDescription());

         ExtendedAttributes eas = p.getExtendedAttributes();
         // put the DN of entry into extended attributes
         List l = eas.getElementsForName("DN");
         ExtendedAttribute ea = null;
         if (l != null && l.size() > 0) {
            ea = (ExtendedAttribute) l.get(0);
         } else {
            ea = new ExtendedAttribute(eas);
            eas.add(ea);
         }
         ea.setName("DN");
         String eaVal = nextEntry.getDN();
         ea.setVValue(eaVal);

         if (isNew) {
            toAdd.add(p);
         }
      }
      jc.startUndouableChange();
      for (int i = 0; i < toAdd.size(); i++) {
         ps.add((XMLElement) toAdd.get(i));
      }
      List toSelect = new ArrayList();
      toSelect.add(ps);
      jc.endUndouableChange(toSelect);
   }

   protected static int getSearchType(String ocf) {
      if (ocf.equals("AllListed"))
         return -1;
      if (ocf.toLowerCase().endsWith("person") || ocf.toLowerCase().equals("user"))
         return 0;
      return 1;
   }

}
