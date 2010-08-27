/**
 * Miroslav Popov, Dec 2, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.components.debug;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.swing.Action;
import javax.swing.ImageIcon;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.AdditionalResourceManager;
import org.enhydra.jawe.JaWEAction;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.components.debug.actions.CleanPage;

/**
 * @author Miroslav Popov
 *
 */
public class DefaultDebugComponentSettings extends JaWEComponentSettings {

   public void init(JaWEComponent comp) {
      PROPERTYFILE_PATH = "org/enhydra/jawe/components/debug/properties/";
      PROPERTYFILE_NAME = "debugcomponent.properties";
      super.init(comp);
   }

   public void loadDefault (JaWEComponent comp,Properties properties) {
      // defaults
      arm = new AdditionalResourceManager(properties);

      loadDefaultMenusToolbarsAndActions(comp);

      // ******** actions
      List actions = ResourceManager.getResourceStrings(properties, "Action.Name.", false);
      for (int i = 0; i < actions.size(); i++) {
         try {
            // Action Base
            String className = ResourceManager.getResourceString(properties, "Action.Class." + actions.get(i));
            Constructor c = Class.forName(className).getConstructor(new Class[] { JaWEComponent.class });
            ActionBase action = (ActionBase) c.newInstance(new Object[] { comp });

            // Icon
            URL url = ResourceManager.getResource(properties, "Action.Image." + actions.get(i));
            ImageIcon icon = null;
            if (url != null)
               icon = new ImageIcon(url);

            // Language dependent name
            String langDepName = ResourceManager.getResourceString(properties, "Action.Name." + actions.get(i));

            JaWEAction jaction = new JaWEAction(action, icon, langDepName);
            componentAction.put(action.getValue(Action.NAME), jaction);

            JaWEManager.getInstance().getLoggingManager().info("Created DebugComponent action for class " + actions.get(i));
         } catch (Throwable thr) {
            JaWEManager.getInstance().getLoggingManager().error("Can't create DebugComponent action for class " + actions.get(i));
         }
      }
      // ********
   }

   public String getToolbarActionOrder(String toolbarName) {
      return (String) componentSettings.get(toolbarName + "Toolbar");
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
      componentAction.put(action.getValue(Action.NAME), ja);
   }

}
