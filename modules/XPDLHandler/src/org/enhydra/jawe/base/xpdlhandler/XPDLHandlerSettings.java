/**
 * Miroslav Popov, Dec 8, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.base.xpdlhandler;

import java.util.Properties;

import org.enhydra.jawe.AdditionalResourceManager;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.JaWEManager;

/**
 * @author Miroslav Popov
 *
 */
public class XPDLHandlerSettings extends JaWEComponentSettings {

   public void init(JaWEComponent comp) {
      PROPERTYFILE_PATH = JaWEManager.TOGWE_BASIC_PROPERTYFILE_PATH;
      PROPERTYFILE_NAME = JaWEManager.TOGWE_BASIC_PROPERTYFILE_NAME;
      super.init(comp);
   }

   public void loadDefault (JaWEComponent comp,Properties properties) {
      // defaults
      arm = new AdditionalResourceManager(properties);

      componentSettings.put("FileLocking", new Boolean(properties.getProperty("FileLocking","false").equals("true")));
   }
   
   public boolean isFileLockingEnabled() {
      return ((Boolean) componentSettings.get("FileLocking")).booleanValue();
   }
   
}
