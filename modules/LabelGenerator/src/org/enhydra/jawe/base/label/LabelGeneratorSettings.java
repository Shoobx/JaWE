/**
 * Miroslav Popov, Dec 8, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.base.label;

import java.util.Properties;

import org.enhydra.jawe.AdditionalResourceManager;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;

/**
 * @author Miroslav Popov
 *
 */
public class LabelGeneratorSettings extends JaWEComponentSettings {

   public void init(JaWEComponent comp) {
      PROPERTYFILE_PATH = "org/enhydra/jawe/base/label/properties/";
      PROPERTYFILE_NAME = "labelgenerator.properties";      
      loadDefault(comp, new Properties());
//      super.init(comp);
   }

   public void loadDefault (JaWEComponent comp,Properties properties) {
      // defaults
      arm = new AdditionalResourceManager(properties);
   }

}
