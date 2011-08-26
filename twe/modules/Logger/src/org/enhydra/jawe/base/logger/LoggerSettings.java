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
