/**
 * Miroslav Popov, Dec 2, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.components.infobar;

import java.awt.Color;
import java.net.URL;
import java.util.Properties;

import javax.swing.ImageIcon;

import org.enhydra.jawe.AdditionalResourceManager;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.base.controller.JaWEActions;

/**
 * @author Miroslav Popov
 *
 */
public class InfoBarSettings extends JaWEComponentSettings {

   public void init(JaWEComponent comp) {
      PROPERTYFILE_PATH = "org/enhydra/jawe/components/infobar/properties/";
      PROPERTYFILE_NAME = "infobar.properties";
      super.init(comp);
   }

   public void loadDefault (JaWEComponent comp,Properties properties) {
      arm = new AdditionalResourceManager(properties);

      ImageIcon icon;
      URL iconURL = ResourceManager.getResource(properties, "Image.File." + JaWEActions.NEW_ACTION);
      if (iconURL != null)
         icon = new ImageIcon(iconURL);
      else
         icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/fileselect.gif"));
      componentSettings.put("FileIcon", icon);

      iconURL = ResourceManager.getResource(properties, "Image.PackageSelection" + JaWEActions.DUPLICATE_ACTION);
      if (iconURL != null)
         icon = new ImageIcon(iconURL);
      else
         icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/packageselect.gif"));
      componentSettings.put("PackageIcon", icon);

      iconURL = ResourceManager.getResource(properties, "Image.ProcessSelection" + JaWEActions.REFERENCES);
      if (iconURL != null)
         icon = new ImageIcon(iconURL);
      else
         icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/processselect.gif"));
      componentSettings.put("ProcessIcon", icon);

      Color backgroundColor;
      try {
         backgroundColor = Utils.getColor(ResourceManager.getResourceString(properties, "Color.Background"));
      } catch (Exception e) {
         backgroundColor = Color.WHITE;
      }
      componentSettings.put("BackgroundColor", backgroundColor);
   }

   public ImageIcon getPackageSelectionIcon() {
      return (ImageIcon) componentSettings.get("PackageIcon");
   }

   public ImageIcon getProcessSelectionIcon() {
      return (ImageIcon) componentSettings.get("ProcessIcon");
   }

   public ImageIcon getFileIcon() {
      return (ImageIcon) componentSettings.get("FileIcon");
   }

   public Color getBackgroundColor() {
      return (Color)componentSettings.get("BackgroundColor");
   }
}
