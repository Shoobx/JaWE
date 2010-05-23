/**
 * Miroslav Popov, Dec 1, 2005 miroslav.popov@gmail.com
 */
package org.enhydra.jawe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * @author Miroslav Popov
 */
public abstract class JaWEComponentSettings implements Settings {

   protected Map componentAction = new HashMap();

   protected Map componentSettings = new HashMap();

   protected PropertyMgr propertyMgr;

   protected AdditionalResourceManager arm;

   public String PROPERTYFILE_PATH = JaWEConstants.JAWE_BASIC_PROPERTYFILE_PATH;

   public String PROPERTYFILE_NAME = JaWEConstants.JAWE_BASIC_PROPERTYFILE_NAME;

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

   public abstract void loadDefault(JaWEComponent comp, Properties properties);

   public void setPropertyMgr(PropertyMgr pm) {
      this.propertyMgr = pm;
   }

   public PropertyMgr getPropertyMgr() {
      return propertyMgr;
   }

   public void clear() {
      componentAction.clear();
      componentSettings.clear();
   }

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

   public Map getActions() {
      return componentAction;
   }

   /**
    * Method to get component's action corresponding to the given string.
    * 
    * @param actionName String representation of controller's action.
    * @return action specified by the string cmd.
    */
   public JaWEAction getAction(String actionName) {
      return (JaWEAction) componentAction.get(actionName);
   }

   public void addAction(String actionName, JaWEAction action) {
      componentAction.put(actionName, action);
   }

   public void adjustActions() {
      for (Iterator it = componentAction.values().iterator(); it.hasNext();) {
         ActionBase ab = ((JaWEAction) it.next()).getAction();
         if (ab != null) {
            ab.enableDisableAction();
         }
      }
   }

   public void changeActionState(String actionName, boolean state) {
      ActionBase ab = ((JaWEAction) componentAction.get(actionName)).getAction();

      ab.setEnabled(state);
   }

   public String getLanguageDependentString(String nm) {
      return ResourceManager.getLanguageDependentString(arm, nm);
   }

   public String getMainMenuActionOrder() {
      return "";
   }

   public String getMenuActionOrder(String menuName) {
      return "";
   }

   public String getToolbarActionOrder(String toolbarName) {
      return "";
   }

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
