/**
 * Miroslav Popov, Dec 2, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.components.detailedpackagenavigator;

import java.awt.Color;
import java.util.List;
import java.util.Properties;

import javax.swing.ImageIcon;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.AdditionalResourceManager;
import org.enhydra.jawe.JaWEAction;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.components.detailedpackagenavigator.actions.CollapseAll;
import org.enhydra.jawe.components.detailedpackagenavigator.actions.ExpandAll;

/**
 * @author Miroslav Popov
 *
 */
public class DetailedPackageNavigatorSettings extends JaWEComponentSettings {

   public void init(JaWEComponent comp) {
      PROPERTYFILE_PATH = "org/enhydra/jawe/components/detailedpackagenavigator/properties/";
      PROPERTYFILE_NAME = "detailedpackagenavigator.properties";
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

      try {
         color = Utils.getColor(ResourceManager.getResourceString(properties, "SelectionColor"));
      } catch (Exception e) {
         color = Utils.getColor("R=40,G=150,B=240");
      }
      componentSettings.put("SelectionColor", color);

      List hideEls = ResourceManager.getResourceStrings(properties, "HideSubElements.", false);
      for (int i = 0; i < hideEls.size(); i++) {
         componentSettings.put("HideSubElements." + (String) hideEls.get(i), ResourceManager.getResourceString(properties, "HideSubElements." + (String)hideEls.get(i)));
      }

      componentSettings.put("HideElements.ExtendedAttributes", "Name");
      componentSettings.put("HideElements.ExtendedAttributes.Name", "JaWE_CONFIGURATION JaWE_TYPE EDITING_TOOL EDITING_TOOL_VERSION JaWE_GRAPH_WORKFLOW_PARTICIPANT_ORIENTATION JaWE_GRAPH_BLOCK_PARTICIPANT_ORIENTATION JaWE_GRAPH_WORKFLOW_PARTICIPANT_ORDER JaWE_GRAPH_BLOCK_PARTICIPANT_ORDER JaWE_GRAPH_START_OF_WORKFLOW JaWE_GRAPH_END_OF_WORKFLOW JaWE_GRAPH_START_OF_BLOCK JaWE_GRAPH_END_OF_BLOCK JaWE_GRAPH_PARTICIPANT_ID JaWE_GRAPH_OFFSET JaWE_GRAPH_BREAK_POINTS JaWE_GRAPH_TRANSITION_STYLE");

      List hide = ResourceManager.getResourceStrings(properties, "HideElements.", false);
      for (int i = 0; i < hide.size(); i++) {
         String line = (String) hide.get(i);
         int indexOf = line.indexOf(".");
         if (indexOf < 1)
            continue;

         String elName = line.substring(0, indexOf);
         String flName = line.substring(indexOf + 1);

         if ("ExtendedAttributes".equals(elName) && "Name".equals(flName)) {
            String elHiddenEl = (String) componentSettings.get("HideElements." + elName);
            elHiddenEl = elHiddenEl.replaceAll(flName, "");
            if (elHiddenEl.length() == 0)
               componentSettings.remove("HideElements.ExtendedAttributes");
            else
               componentSettings.put("HideElements.ExtendedAttributes", elHiddenEl);
            componentSettings.remove("HideElements.ExtendedAttributes.Name");
         }

         String elHiddenEl = (String) componentSettings.get("HideElements." + elName);
         if (elHiddenEl == null)
            elHiddenEl = flName;
         else
            elHiddenEl += " " + flName;

         String hel = ResourceManager.getResourceString(properties, "HideElements." + elName + "." + flName);
         componentSettings.put("HideElements." + elName, elHiddenEl);
         componentSettings.put("HideElements." + elName + "." + flName, hel);
      }

      loadDefaultMenusToolbarsAndActions(comp);
      componentSettings.putAll(Utils.loadAllMenusAndToolbars(properties));
      componentAction.putAll(Utils.loadActions(properties, comp, componentAction));
   }

   protected void loadDefaultMenusToolbarsAndActions(JaWEComponent comp) {
      // menu
      componentSettings.put("defaultMenu", "ExpandAll CollapseAll - jaweAction_Duplicate jaweAction_Cut jaweAction_Copy jaweAction_Paste jaweAction_Delete - jaweAction_EditProperties jaweAction_References");

      // toolbar
      componentSettings.put("defaultToolbarToolbar", "ExpandAll CollapseAll");

      // actions
      ActionBase action;
      ImageIcon icon;
      String langDepName;
      JaWEAction ja;

      // CollapseAll
      action = new CollapseAll(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/collapseall.png"));
      langDepName = "CollapseAll";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("CollapseAll", ja);

      // Exit
      action = new ExpandAll(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/expandall.png"));
      langDepName = "ExpandAll";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("ExpandAll", ja);
   }

   public String getMenuActionOrder(String menuName) {
      return (String) componentSettings.get(menuName + "Menu");
   }

   public String getToolbarActionOrder(String toolbarName) {
      return (String) componentSettings.get(toolbarName + "Toolbar");
   }

   public Color getBackGroundColor() {
      return (Color) componentSettings.get("BackgroundColor");
   }

   public Color getSelectionColor() {
      return (Color) componentSettings.get("SelectionColor");
   }
}
