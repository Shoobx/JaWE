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

package org.enhydra.jawe.components.simplenavigator;

import java.awt.Color;
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
import org.enhydra.jawe.components.simplenavigator.actions.CollapseAll;
import org.enhydra.jawe.components.simplenavigator.actions.ExpandAll;

/**
 * Settings for {@link SimpleNavigator} component.
 */
public class SimpleNavigatorSettings extends JaWEComponentSettings {

   public void init(JaWEComponent comp) {
      super.init(comp);
   }

   public void loadDefault(JaWEComponent comp, Properties properties) {
      // defaults
      arm = new AdditionalResourceManager(properties);

      Color color;
      try {
         color = Utils.getColor(ResourceManager.getResourceString(properties,
                                                                  "BackgroundColor"));
      } catch (Exception e) {
         color = Utils.getColor("R=245,G=245,B=245");
      }
      componentSettings.put("BackgroundColor", color);

      try {
         color = Utils.getColor(ResourceManager.getResourceString(properties,
                                                                  "SelectionColor"));
      } catch (Exception e) {
         color = Utils.getColor("R=40,G=150,B=240");
      }
      componentSettings.put("SelectionColor", color);

      loadDefaultMenusToolbarsAndActions(comp);
      componentSettings.putAll(Utils.loadAllMenusAndToolbars(properties));
      componentAction.putAll(Utils.loadActions(properties, comp, componentAction));
   }

   /**
    * Loads default settings for the menus, toolbars and actions for the given
    * {@link JaWEComponent}.
    * 
    * @param comp {@link JaWEComponent} instance.
    */
   protected void loadDefaultMenusToolbarsAndActions(JaWEComponent comp) {

      // menu
      componentSettings.put("defaultMenu",
                            "ExpandAll CollapseAll - jaweAction_Duplicate jaweAction_Cut jaweAction_Copy jaweAction_Paste jaweAction_Delete - jaweAction_EditProperties");

      // toolbar
      componentSettings.put("defaultToolbarToolbar", "ExpandAll CollapseAll");

      // actions
      ActionBase action;
      ImageIcon icon;
      String langDepName;
      JaWEAction ja;

      // CollapseAll
      action = new CollapseAll(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/collapseall.png"));
      langDepName = "CollapseAll";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // ExpandAll
      action = new ExpandAll(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/expandall.png"));
      langDepName = "ExpandAll";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);
   }

   public String getMenuActionOrder(String menuName) {
      return (String) componentSettings.get(menuName + "Menu");
   }

   public String getToolbarActionOrder(String toolbarName) {
      return (String) componentSettings.get(toolbarName + "Toolbar");
   }

   /**
    * @return The background color setting.
    */
   public Color getBackGroundColor() {
      return (Color) componentSettings.get("BackgroundColor");
   }

   /**
    * @return The selection color setting.
    */
   public Color getSelectionColor() {
      return (Color) componentSettings.get("SelectionColor");
   }

}
