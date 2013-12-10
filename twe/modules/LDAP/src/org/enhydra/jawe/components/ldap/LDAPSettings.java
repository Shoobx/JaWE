/**
* Together Workflow Editor
* Copyright (C) 2010 Together Teamsolutions Co., Ltd. 
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

import java.net.URL;
import java.util.Properties;

import javax.swing.Action;
import javax.swing.ImageIcon;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.AdditionalResourceManager;
import org.enhydra.jawe.JaWEAction;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.components.ldap.actions.ConfigureLDAP;
import org.enhydra.jawe.components.ldap.actions.ImportAll;
import org.enhydra.jawe.components.ldap.actions.ImportSelected;
import org.enhydra.jawe.components.ldap.actions.SearchLDAP;
import org.enhydra.jxpdl.XMLUtil;

public class LDAPSettings extends JaWEComponentSettings {

   public void init(JaWEComponent comp) {
      PROPERTYFILE_PATH = "org/enhydra/jawe/components/ldap/properties/";
      PROPERTYFILE_NAME = "ldap.properties";
      super.init(comp);
   }

   public void loadDefault(JaWEComponent comp, Properties properties) {
      // defaults
      arm = new AdditionalResourceManager(properties);

      componentSettings.put("LDAPReferralHandling",
                            properties.getProperty("LDAPReferralHandling", "follow"));
      componentSettings.put("LDAPCountLimit",
                            new Integer(properties.getProperty("LDAPCountLimit", "0")));
      componentSettings.put("LDAPTimeLimit",
                            new Integer(properties.getProperty("LDAPTimeLimit", "0")));
      componentSettings.put("LDAPPageSize",
                            new Integer(properties.getProperty("LDAPPageSize", "1000")));
      componentSettings.put("LDAPHost", properties.getProperty("LDAPHost", "localhost"));
      componentSettings.put("LDAPPort", properties.getProperty("LDAPPort", "389"));
      componentSettings.put("LDAPBaseDN", properties.getProperty("LDAPBaseDN", ""));
      componentSettings.put("LDAPObjectClassFilter",
                            properties.getProperty("LDAPObjectClassFilter", "group"));
      componentSettings.put("LDAPSearchScope", properties.getProperty("LDAPSearchScope",
                                                                      "SCOPE_SUB"));
      componentSettings.put("LDAPSecurityLevel",
                            properties.getProperty("LDAPSecurityLevel", "UserAndPassword"));
      componentSettings.put("LDAPSecurityUserDN",
                            properties.getProperty("LDAPSecurityUserDN",
                                                   "username@company.com"));
      componentSettings.put("LDAPSecurityPassword",
                            properties.getProperty("LDAPSecurityPassword", "somepwd"));
      componentSettings.put("LDAPShowConfigDialog",
                            properties.getProperty("LDAPShowConfigDialog", "true"));
      componentSettings.put("LDAPObjectClassFilterChoices",
                            properties.getProperty("LDAPObjectClassFilterChoices",
                                                   "group,organizationalUnit,organizationalRole,user,person,organizationalPerson,inetOrgPerson,AllListed"));
      componentSettings.put("LDAPGroupUniqueAttributeName", "sAMAccountName");
      componentSettings.put("LDAPGroupNameAttributeName", "displayName");
      componentSettings.put("LDAPGroupDescriptionAttributeName", "description");
      componentSettings.put("LDAPUserUniqueAttributeName", "sAMAccountName");
      componentSettings.put("LDAPUserNameAttributeName", "displayName");
      componentSettings.put("LDAPUserDescriptionAttributeName", "description");
      componentSettings.put("LDAPDistinguishedNameAttributeName", "distinguishedName");

      ImageIcon actionIcon;
      URL iconURL = ResourceManager.getResource(properties, "Role.Icon");
      if (iconURL != null)
         actionIcon = new ImageIcon(iconURL);
      else
         actionIcon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/participantrole.gif"));
      componentSettings.put("Role.Icon", actionIcon);

      iconURL = ResourceManager.getResource(properties, "Group.Icon");
      if (iconURL != null)
         actionIcon = new ImageIcon(iconURL);
      else
         actionIcon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/participantorgunit.png"));
      componentSettings.put("Group.Icon", actionIcon);

      iconURL = ResourceManager.getResource(properties, "User.Icon");
      if (iconURL != null)
         actionIcon = new ImageIcon(iconURL);
      else
         actionIcon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/participant.gif"));
      componentSettings.put("User.Icon", actionIcon);

      loadDefaultMenusToolbarsAndActions(comp);

      componentSettings.putAll(Utils.loadAllMenusAndToolbars(properties));
      componentAction.putAll(Utils.loadActions(properties, comp, componentAction));

   }

   public String getToolbarActionOrder(String toolbarName) {
      return (String) componentSettings.get(toolbarName + "Toolbar");
   }

   public void loadDefaultMenusToolbarsAndActions(JaWEComponent comp) {
      // toolbar
      componentSettings.put("defaultToolbarToolbar",
                            "ConfigureLDAP SearchLDAP - ImportSelected ImportAll");

      // actions
      ActionBase action;
      ImageIcon icon;
      String langDepName;
      JaWEAction ja;

      // Configure
      action = new ConfigureLDAP(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/config.gif"));
      langDepName = "ConfigureLDAP";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // Search
      action = new SearchLDAP(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/searchldap.png"));
      langDepName = "SearchLDAP";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // Import selected
      action = new ImportSelected(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/importselected.png"));
      langDepName = "ImportSelected";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // Import all
      action = new ImportAll(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/importexternalparticipants.gif"));
      langDepName = "ImportAll";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

   }

   public String getLDAPReferralHandling() {
      return getSettingString("LDAPReferralHandling");
   }

   public String getLDAPCountLimit() {
      return getSettingString("LDAPCountLimit");
   }

   public String getLDAPTimeLimit() {
      return getSettingString("LDAPTimeLimit");
   }

   public String getLDAPPageSize() {
      return getSettingString("LDAPPageSize");
   }

   public String getLDAPHost() {
      return getSettingString("LDAPHost");
   }

   public String getLDAPPort() {
      return getSettingString("LDAPPort");
   }

   public String getLDAPBaseDN() {
      return getSettingString("LDAPBaseDN");
   }

   public String getLDAPObjectClassFilter() {
      return getSettingString("LDAPObjectClassFilter");
   }

   public String getLDAPSearchScope() {
      return getSettingString("LDAPSearchScope");
   }

   public String getLDAPSecurityLevel() {
      return getSettingString("LDAPSecurityLevel");
   }

   public String getLDAPSecurityUserDN() {
      return getSettingString("LDAPSecurityUserDN");
   }

   public String getLDAPSecurityPassword() {
      return getSettingString("LDAPSecurityPassword");
   }

   public String[] getLDAPObjectClassFilterChoices() {
      String str = getSettingString("LDAPObjectClassFilterChoices");
      return XMLUtil.tokenize(str, ",");
   }

   public String getLDAPGroupUniqueAttributeName() {
      return getSettingString("LDAPGroupUniqueAttributeName");
   }

   public String getLDAPGroupNameAttributeName() {
      return getSettingString("LDAPGroupNameAttributeName");
   }

   public String getLDAPGroupDescriptionAttributeName() {
      return getSettingString("LDAPGroupDescriptionAttributeName");
   }

   public String getLDAPUserUniqueAttributeName() {
      return getSettingString("LDAPUserUniqueAttributeName");
   }

   public String getLDAPUserNameAttributeName() {
      return getSettingString("LDAPUserNameAttributeName");
   }

   public String getLDAPUserDescriptionAttributeName() {
      return getSettingString("LDAPUserDescriptionAttributeName");
   }

   public String getLDAPDistinguishedNameAttributeName() {
      return getSettingString("LDAPDistinguishedNameAttributeName");
   }

   public ImageIcon getRoleIcon() {
      return (ImageIcon) componentSettings.get("Role.Icon");
   }

   public ImageIcon getGroupIcon() {
      return (ImageIcon) componentSettings.get("Group.Icon");
   }

   public ImageIcon getUserIcon() {
      return (ImageIcon) componentSettings.get("User.Icon");
   }

}
