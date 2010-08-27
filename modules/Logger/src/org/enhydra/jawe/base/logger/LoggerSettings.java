/**
 * Miroslav Popov, Dec 2, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.base.logger;

import java.util.Properties;

import javax.swing.Action;
import javax.swing.ImageIcon;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEAction;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.base.logger.actions.CleanPage;

/**
 * @author Miroslav Popov
 *
 */
public class LoggerSettings extends JaWEComponentSettings {

   public void init(JaWEComponent comp) {      
      loadDefault(comp, new Properties());
   }
   
   public void loadDefault(JaWEComponent comp,Properties properties) {      
      loadDefaultMenusToolbarsAndActions(comp);
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
