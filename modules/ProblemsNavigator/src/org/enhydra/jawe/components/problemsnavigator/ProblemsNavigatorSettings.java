/**
 * Miroslav Popov, Dec 2, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.components.problemsnavigator;

import java.awt.Color;
import java.net.URL;
import java.util.Properties;

import javax.swing.ImageIcon;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.AdditionalResourceManager;
import org.enhydra.jawe.JaWEAction;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.components.problemsnavigator.actions.CleanPage;

/**
 * @author Miroslav Popov
 *
 */
public class ProblemsNavigatorSettings extends JaWEComponentSettings {

   public void init(JaWEComponent comp) {
      PROPERTYFILE_PATH = "org/enhydra/jawe/components/problemsnavigator/properties/";
      PROPERTYFILE_NAME = "problemsnavigator.properties";
      super.init(comp);
   }

   public void loadDefault (JaWEComponent comp,Properties properties) {
      // defaults
      arm = new AdditionalResourceManager(properties);

      Color color;
      try {
         color = Utils.getColor(ResourceManager.getResourceString(properties, "BackgroundColor"));
      } catch (Exception e) {
         color = Utils.getColor("R=245,G=245,B=245");
      }
      componentSettings.put("BackgroundColor", color);

      ImageIcon actionIcon;
      URL iconURL = ResourceManager.getResource(properties, "Warning.Icon");
      if (iconURL != null)
         actionIcon = new ImageIcon(iconURL);
      else
         actionIcon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/warning_icon.gif"));
      componentSettings.put("Warning.Icon", actionIcon);

      iconURL = ResourceManager.getResource(properties, "Error.Icon");
      if (iconURL != null)
         actionIcon = new ImageIcon(iconURL);
      else
         actionIcon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/error_icon.gif"));
      componentSettings.put("Error.Icon", actionIcon);

      loadDefaultMenusToolbarsAndActions(comp);
      componentSettings.putAll(Utils.loadAllMenusAndToolbars(properties));
      componentAction.putAll(Utils.loadActions(properties, comp, componentAction));
   }

   protected void loadDefaultMenusToolbarsAndActions(JaWEComponent comp) {
      // toolbar
      componentSettings.put("defaultToolbarToolbar", "CleanPage");

      // actions
      ActionBase action;
      ImageIcon icon;
      String langDepName;
      JaWEAction ja;

      // CleanPage
      action = new CleanPage(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/cleanpage.gif"));
      langDepName = "CleanPage";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("CleanPage", ja);
   }

   public String getMenuActionOrder(String menuName) {
      return (String) componentSettings.get(menuName + "Menu");
   }

   public String getToolbarActionOrder(String toolbarName) {
      return (String) componentSettings.get(toolbarName + "Toolbar");
   }

   public Color getBackgroundColor() {
      return (Color) componentSettings.get("BackgroundColor");
   }

   public ImageIcon getWarningIcon() {
      return (ImageIcon) componentSettings.get("Warning.Icon");
   }

   public ImageIcon getErrorIcon() {
      return (ImageIcon) componentSettings.get("Error.Icon");
   }
}
