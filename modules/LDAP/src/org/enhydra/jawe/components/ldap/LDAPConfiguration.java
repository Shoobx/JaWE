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

import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLComplexElement;

// This is not a real WfMC DTD element, but it is introduced to help
public class LDAPConfiguration extends XMLComplexElement {

   private LDAPSettings settings;

   public LDAPConfiguration(LDAPSettings settings) {
      super(null, true);
      this.settings = settings;
      setConfigValues();
   }

   protected void fillStructure() {
      XMLAttribute host = new XMLAttribute(this, "Host", true);
      XMLAttribute port = new XMLAttribute(this, "Port", true);
      XMLAttribute baseDN = new XMLAttribute(this, "BaseDN", false);
      XMLAttribute searchScope = new XMLAttribute(this,
                                                  "SearchScope",
                                                  true,
                                                  new String[] {
                                                        "SCOPE_ONE", "SCOPE_SUB"
                                                  },
                                                  1);
      XMLAttribute gUniqueAttr = new XMLAttribute(this,
                                                  "LDAPGroupUniqueAttributeName",
                                                  true);
      XMLAttribute gNameAttr = new XMLAttribute(this, "LDAPGroupNameAttributeName", true);
      XMLAttribute gDescAttr = new XMLAttribute(this,
                                                "LDAPGroupDescriptionAttributeName",
                                                true);
      XMLAttribute uUniqueAttr = new XMLAttribute(this,
                                                  "LDAPUserUniqueAttributeName",
                                                  true);
      XMLAttribute uNameAttr = new XMLAttribute(this, "LDAPUserNameAttributeName", true);
      XMLAttribute uDescAttrName = new XMLAttribute(this,
                                                    "LDAPUserDescriptionAttributeName",
                                                    true);

      XMLAttribute distinguishedName = new XMLAttribute(this,
                                                    "LDAPDistinguishedNameAttributeName",
                                                    true);
      XMLAttribute referralHandling = new XMLAttribute(this,
                                                       "LDAPReferralHandling",
                                                       true,
                                                       new String[]{"follow","ignore","throw"},
                                                       0);
      XMLAttribute countLimit = new XMLAttribute(this, "LDAPCountLimit", true);
      XMLAttribute timeLimit = new XMLAttribute(this, "LDAPTimeLimit", true);
      XMLAttribute pageSize = new XMLAttribute(this, "LDAPPageSize", true);

      super.add(host);
      super.add(port);
      super.add(baseDN);
      super.add(searchScope);
      super.add(gUniqueAttr);
      super.add(gNameAttr);
      super.add(gDescAttr);
      super.add(uUniqueAttr);
      super.add(uNameAttr);
      super.add(uDescAttrName);
      super.add(distinguishedName);

      super.add(referralHandling);
      super.add(countLimit);
      super.add(timeLimit);
      super.add(pageSize);

      super.add(new LDAPSecurity(this));
   }

   protected void setConfigValues() {
      set("Host", settings.getLDAPHost());
      set("Port", settings.getLDAPPort());
      set("BaseDN", settings.getLDAPBaseDN());
      set("SearchScope", settings.getLDAPSearchScope());
      set("LDAPGroupUniqueAttributeName", settings.getLDAPGroupUniqueAttributeName());
      set("LDAPGroupNameAttributeName", settings.getLDAPGroupNameAttributeName());
      set("LDAPGroupDescriptionAttributeName",
          settings.getLDAPGroupDescriptionAttributeName());
      set("LDAPUserUniqueAttributeName", settings.getLDAPUserUniqueAttributeName());
      set("LDAPUserNameAttributeName", settings.getLDAPUserNameAttributeName());
      set("LDAPUserDescriptionAttributeName",
          settings.getLDAPUserDescriptionAttributeName());
      set("LDAPDistinguishedNameAttributeName",
          settings.getLDAPDistinguishedNameAttributeName());
      set("LDAPReferralHandling", settings.getLDAPReferralHandling());
      set("LDAPCountLimit", settings.getLDAPCountLimit());
      set("LDAPTimeLimit", settings.getLDAPTimeLimit());      
      set("LDAPPageSize", settings.getLDAPPageSize());

      LDAPSecurity security = (LDAPSecurity) get("LDAPSecurity");
      security.set("Level", settings.getLDAPSecurityLevel());
      security.set("UserDN", settings.getLDAPSecurityUserDN());
      security.set("Password", settings.getLDAPSecurityPassword());
   }
}
