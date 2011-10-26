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

package org.enhydra.jawe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Abstract class that represents the settings for the {@link JaWEComponent}
 */
public abstract class JaWEComponentSettings implements Settings {

   /**
    * Map where keys are strings representing component action Ids and values
    * {@link JaWEAction} objects representing component action.
    */
   protected Map componentAction = new HashMap();

   /**
    * Map where keys are strings representing component setting Ids and values objects
    * (String, Double, Boolean) representing the setting itself.
    */
   protected Map componentSettings = new HashMap();

   /** The instance of class for handling properties. */
   protected PropertyMgr propertyMgr;

   /**
    * The instance of class for handling additional resources (Language dependent strings
    * defined in the configuration).
    */
   protected AdditionalResourceManager arm;

   /** The path to property file resource. */
   public String PROPERTYFILE_PATH = JaWEConstants.JAWE_BASIC_PROPERTYFILE_PATH;

   /** The property file name. */
   public String PROPERTYFILE_NAME = JaWEConstants.JAWE_BASIC_PROPERTYFILE_NAME;

   /**
    * Initializes component settings.
    * 
    * @param comp {@link JaWEComponent} instance where this settings are applied.
    */
   public void init(JaWEComponent comp) {
      if (propertyMgr == null) {
         if (Utils.checkFileExistence(PROPERTYFILE_NAME)
             || Utils.checkResourceExistence(PROPERTYFILE_PATH, PROPERTYFILE_NAME)) {
            manageProperties(comp, PROPERTYFILE_PATH, PROPERTYFILE_NAME);
         } else {
            manageProperties(comp,
                             JaWEConstants.JAWE_BASIC_PROPERTYFILE_PATH,
                             JaWEConstants.JAWE_BASIC_PROPERTYFILE_NAME);
         }
      } else {
         if (Utils.checkFileExistence(PROPERTYFILE_NAME)
             || Utils.checkResourceExistence(PROPERTYFILE_PATH, PROPERTYFILE_NAME)) {
            propertyMgr.manageProperties(comp, this, PROPERTYFILE_PATH, PROPERTYFILE_NAME);
         } else {
            propertyMgr.manageProperties(comp,
                                         this,
                                         JaWEConstants.JAWE_BASIC_PROPERTYFILE_PATH,
                                         JaWEConstants.JAWE_BASIC_PROPERTYFILE_NAME);
         }
      }
   }

   /**
    * Loads default settings.
    * 
    * @param comp {@link JaWEComponent} instance where the settings are applied.
    * @param properties The settings.
    */
   public abstract void loadDefault(JaWEComponent comp, Properties properties);

   /**
    * Sets the instance of {@link PropertyMgr} to use for this settings.
    * 
    * @param pm {@link PropertyMgr} instance.
    */
   public void setPropertyMgr(PropertyMgr pm) {
      this.propertyMgr = pm;
   }

   /**
    * @return {@link PropertyMgr} instance used for this settings.
    */
   public PropertyMgr getPropertyMgr() {
      return propertyMgr;
   }

   /**
    * Clears the settings.
    */
   public void clear() {
      componentAction.clear();
      componentSettings.clear();
   }

   /**
    * Adds new component setting.
    * 
    * @param key The key for the setting.
    * @param setting The value for the setting.
    */
   public void addSetting(String key, Object setting) {
      componentSettings.put(key, setting);
   }

   public Object getSetting(String key) {
      return componentSettings.get(key);
   }

   public String getSettingString(String key) {
      Object s = componentSettings.get(key);
      if (s instanceof String) {
         return (String) s;
      } else if (s != null) {
         return s.toString();
      } else {
         return null;
      }
   }

   public int getSettingInt(String key) {
      Object s = componentSettings.get(key);
      if (s instanceof Integer) {
         return ((Integer) s).intValue();
      } else if (s != null) {
         try {
            int i = Integer.parseInt(getSettingString(key));
            return i;
         } catch (Exception e) {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public boolean getSettingBoolean(String key) {
      Object s = componentSettings.get(key);
      if (s instanceof Boolean) {
         return ((Boolean) s).booleanValue();
      } else if (s != null) {
         return new Boolean(getSettingString(key)).booleanValue();
      } else {
         return false;
      }
   }

   public double getSettingDouble(String key) {
      Object s = componentSettings.get(key);
      if (s instanceof Double) {
         return ((Double) s).doubleValue();
      } else if (s != null) {
         try {
            double d = Double.parseDouble(getSettingString(key));
            return d;
         } catch (Exception e) {
            return -1;
         }
      } else {
         return -1;
      }
   }

   /**
    * @return The Map where keys are strings representing component action Ids and values
    *         {@link JaWEAction} objects representing component action.
    */
   public Map getActions() {
      return componentAction;
   }

   /**
    * Method to get component's action corresponding to the given string.
    * 
    * @param actionName String representation of controller's action.
    * @return {@link JaWEAction} specified by the string actionName.
    */
   public JaWEAction getAction(String actionName) {
      return (JaWEAction) componentAction.get(actionName);
   }

   /**
    * Adds new {@link JaWEAction} to this component settings action map.
    * 
    * @param actionName Uniquely represents the action.
    * @param action The {@link JaWEAction} instance.
    */
   public void addAction(String actionName, JaWEAction action) {
      componentAction.put(actionName, action);
   }

   /**
    * Adjusts the enabled/disabled state for every {@link JaWEAction} defined for this
    * component settings by calling its 'enableDisableAction' method.
    */
   public void adjustActions() {
      for (Iterator it = componentAction.values().iterator(); it.hasNext();) {
         ActionBase ab = ((JaWEAction) it.next()).getAction();
         if (ab != null) {
            ab.enableDisableAction();
         }
      }
   }

   /**
    * Changes the {@link JaWEAction} state to the given value.
    * 
    * @param actionName Uniquely represents {@link JaWEAction}.
    * @param state New action state.
    */
   public void changeActionState(String actionName, boolean state) {
      ActionBase ab = ((JaWEAction) componentAction.get(actionName)).getAction();

      ab.setEnabled(state);
   }

   public String getLanguageDependentString(String nm) {
      return ResourceManager.getLanguageDependentString(arm, nm);
   }

   /**
    * @return String representing the order of Main menu actions.
    */
   public String getMainMenuActionOrder() {
      return "";
   }

   /**
    * Gets the order of actions for the given menu.
    * 
    * @param menuName The name of the menu.
    * @return String representing the order of the actions for the given menu.
    */
   public String getMenuActionOrder(String menuName) {
      return "";
   }

   /**
    * Gets the order of actions for the given toolbar.
    * 
    * @param toolbarName The name of the toolbar.
    * @return String representing the order of the actions for the given menu.
    */
   public String getToolbarActionOrder(String toolbarName) {
      return "";
   }

   /**
    * Manages the properties for the given component based on the given path and name.
    * 
    * @param comp {@link JaWEComponent} which properties needs to be managed.
    * @param path The resource path to the properties.
    * @param name The name of the resource containing the properties.
    */
   public void manageProperties(JaWEComponent comp, String path, String name) {
      Properties properties = new Properties();

      try {
         Utils.manageProperties(properties, path, name);
      } catch (Exception e) {
      }

      try {
         loadDefault(comp, properties);
      } catch (Exception e) {
         try {
            clear();
            properties = new Properties();
            Utils.copyPropertyFile(JaWEConstants.JAWE_BASIC_PROPERTYFILE_PATH,
                                   JaWEConstants.JAWE_BASIC_PROPERTYFILE_NAME,
                                   true);
            System.err.println("Invalid configuration setting(s) found in "
                               + name
                               + ", the file has been overwritten by the default one!");
            loadDefault(comp, properties);
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
   }

}
