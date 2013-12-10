/**
 * Miroslav Popov, Dec 7, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.base.editor;

import java.awt.Color;
import java.net.URL;
import java.util.Properties;

import javax.swing.Action;
import javax.swing.ImageIcon;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.AdditionalResourceManager;
import org.enhydra.jawe.JaWEAction;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.base.controller.JaWEActions;
import org.enhydra.jawe.base.editor.actions.ApplyChanges;
import org.enhydra.jawe.base.editor.actions.DisplayParentElementPanel;
import org.enhydra.jawe.base.panel.PanelSettings;

/**
 * @author Miroslav Popov
 * 
 */
public class NewStandardXPDLEditorSettings extends PanelSettings {

   public void init(JaWEComponent comp) {
      loadDefault(comp, new Properties());
   }

   public void loadDefault(JaWEComponent comp, Properties properties) {
      arm = new AdditionalResourceManager(properties);

      componentSettings.put("UseScrollBar", new Boolean(properties.getProperty("InlinePanel.UseScrollBar", "false")
            .equals("true")));
      componentSettings.put("ShowModifiedWarning", new Boolean(properties.getProperty(
            "InlinePanel.ShowModifiedWarning", "true").equals("true")));
      componentSettings.put("DisplayTitle", new Boolean(properties.getProperty("InlinePanel.DisplayTitle", "false")
            .equals("true")));
      componentSettings.put("XMLBasicPanel.RightAllignment", new Boolean(properties.getProperty(
            "XMLBasicPanel.RightAllignment", "false").equals("true")));
      componentSettings.put("XMLDataTypesPanel.HasBorder", new Boolean(properties.getProperty(
            "XMLDataTypesPanel.HasBorder", "false").equals("true")));

      componentSettings.put("EmptyBorder.TOP",
            new Integer(properties.getProperty("XMLBasicPanel.EmptyBorder.TOP", "0")));
      componentSettings.put("EmptyBorder.LEFT", new Integer(properties.getProperty("XMLBasicPanel.EmptyBorder.LEFT",
            "3")));
      componentSettings.put("EmptyBorder.BOTTOM", new Integer(properties.getProperty(
            "XMLBasicPanel.EmptyBorder.BOTTOM", "4")));
      componentSettings.put("EmptyBorder.RIGHT", new Integer(properties.getProperty("XMLBasicPanel.EmptyBorder.RIGHT",
            "3")));
      componentSettings.put("SimplePanelTextWidth", new Integer(properties.getProperty(
            "XMLBasicPanel.SimplePanelTextWidth", "250")));
      componentSettings.put("SimplePanelTextHeight", new Integer(properties.getProperty(
            "XMLBasicPanel.SimplePanelTextHeight", "20")));
      componentSettings.put("XMLDataTypesPanel.Dimension.WIDTH", new Integer(properties.getProperty(
            "XMLDataTypesPanel.Dimension.WIDTH", "400")));
      componentSettings.put("XMLDataTypesPanel.Dimension.HEIGHT", new Integer(properties.getProperty(
            "XMLDataTypesPanel.Dimension.HEIGHT", "125")));

      componentSettings.put("XMLComboPanel.DisableCombo", properties.getProperty("XMLComboPanel.DisableCombo", ""));

      componentSettings.put("HideElements.XMLTablePanel.ExtendedAttributes", "Name");
      componentSettings
            .put(
                  "HideElements.XMLTablePanel.ExtendedAttributes.Name",
                  "JaWE_CONFIGURATION JaWE_TYPE EDITING_TOOL EDITING_TOOL_VERSION JaWE_GRAPH_WORKFLOW_PARTICIPANT_ORIENTATION JaWE_GRAPH_BLOCK_PARTICIPANT_ORIENTATION JaWE_GRAPH_WORKFLOW_PARTICIPANT_ORDER JaWE_GRAPH_BLOCK_PARTICIPANT_ORDER JaWE_GRAPH_START_OF_WORKFLOW JaWE_GRAPH_END_OF_WORKFLOW JaWE_GRAPH_START_OF_BLOCK JaWE_GRAPH_END_OF_BLOCK JaWE_GRAPH_PARTICIPANT_ID JaWE_GRAPH_OFFSET JaWE_GRAPH_BREAK_POINTS JaWE_GRAPH_TRANSITION_STYLE");

      componentSettings.put("ShowColumns.XMLTablePanel.Activities",
            "Id Name Performer Type StartMode FinishMode Deadlines");
      componentSettings.put("ShowColumns.XMLTablePanel.ActivitySets", "Id Activities Transitions");
      componentSettings.put("ShowColumns.XMLTablePanel.Applications", "Id Name");
      componentSettings.put("ShowColumns.XMLTablePanel.DataFields", "Id Name DataType");
      componentSettings.put("ShowColumns.XMLTablePanel.ExtendedAttributes", "Name Value");
      componentSettings.put("ShowColumns.XMLTablePanel.FormalParameters", "Id Mode DataType");
      componentSettings.put("ShowColumns.XMLTablePanel.Namespaces", "Name location");
      componentSettings.put("ShowColumns.XMLTablePanel.Participants", "Id Name ParticipantType");
      componentSettings.put("ShowColumns.XMLTablePanel.Tools", "Id Type");
      componentSettings.put("ShowColumns.XMLTablePanel.Transitions", "Id From To Condition");
      componentSettings.put("ShowColumns.XMLTablePanel.TypeDeclarations", "Id Name DataTypes");
      componentSettings.put("ShowColumns.XMLTablePanel.WorkflowProcesses", "Id Name AccessLevel");

      componentSettings.put("defaultToolbarToolbar", "Apply");

      Color color=null;
      try {
         color = Utils.getColor(ResourceManager.getResourceString(properties, "BackgroundColor"));
      } catch (Exception e) {
         color = Utils.getColor("R=245,G=245,B=245");
      }
      componentSettings.put("BackgroundColor", color);
      
      ImageIcon hicon;
      URL iconURL = ResourceManager.getResource(properties, "ArrowRightImage");
      if (iconURL != null)
         hicon = new ImageIcon(iconURL);
      else
         hicon = new ImageIcon(ResourceManager.class.getClassLoader().getResource(
               "org/enhydra/jawe/images/navigate_right.png"));
      componentSettings.put("ArrowRightImage", hicon);

      iconURL = ResourceManager.getResource(properties, "ArrowUpImage");
      if (iconURL != null)
         hicon = new ImageIcon(iconURL);
      else
         hicon = new ImageIcon(ResourceManager.class.getClassLoader()
               .getResource("org/enhydra/jawe/images/arrowup.gif"));
      componentSettings.put("ArrowUpImage", hicon);

      iconURL = ResourceManager.getResource(properties, "ArrowDownImage");
      if (iconURL != null)
         hicon = new ImageIcon(iconURL);
      else
         hicon = new ImageIcon(ResourceManager.class.getClassLoader().getResource(
               "org/enhydra/jawe/images/arrowdown.gif"));
      componentSettings.put("ArrowDownImage", hicon);

      iconURL = ResourceManager.getResource(properties, "InsertVariableDefault.Icon");
      if (iconURL != null)
         hicon = new ImageIcon(iconURL);
      else
         hicon = new ImageIcon(ResourceManager.class.getClassLoader().getResource(
               "org/enhydra/jawe/images/navigate_right2.png"));
      componentSettings.put("InsertVariableDefault", hicon);

      iconURL = ResourceManager.getResource(properties, "InsertVariablePressed.Icon");
      if (iconURL != null)
         hicon = new ImageIcon(iconURL);
      else
         hicon = new ImageIcon(ResourceManager.class.getClassLoader().getResource(
               "org/enhydra/jawe/images/navigate_down2.png"));
      componentSettings.put("InsertVariablePressed", hicon);

      iconURL = ResourceManager.getResource(properties, "DefaultAction.Image.New");
      if (iconURL != null)
         hicon = new ImageIcon(iconURL);
      else
         hicon = new ImageIcon(ResourceManager.class.getClassLoader().getResource(
               "org/enhydra/jawe/images/new_small.gif"));
      componentSettings.put("DefaultAction.Icon." + JaWEActions.NEW_ACTION, hicon);

      iconURL = ResourceManager.getResource(properties, "DefaultAction.Image.EditProperties");
      if (iconURL != null)
         hicon = new ImageIcon(iconURL);
      else
         hicon = new ImageIcon(ResourceManager.class.getClassLoader().getResource(
               "org/enhydra/jawe/images/edit_small.gif"));
      componentSettings.put("DefaultAction.Icon." + JaWEActions.EDIT_PROPERTIES_ACTION, hicon);

      iconURL = ResourceManager.getResource(properties, "DefaultAction.Image.Delete");
      if (iconURL != null)
         hicon = new ImageIcon(iconURL);
      else
         hicon = new ImageIcon(ResourceManager.class.getClassLoader().getResource(
               "org/enhydra/jawe/images/delete_small.gif"));
      componentSettings.put("DefaultAction.Icon." + JaWEActions.DELETE_ACTION, hicon);

      iconURL = ResourceManager.getResource(properties, "DefaultAction.Image.Duplicate");
      if (iconURL != null)
         hicon = new ImageIcon(iconURL);
      else
         hicon = new ImageIcon(ResourceManager.class.getClassLoader().getResource(
               "org/enhydra/jawe/images/duplicate_small.gif"));
      componentSettings.put("DefaultAction.Icon." + JaWEActions.DUPLICATE_ACTION, hicon);

      iconURL = ResourceManager.getResource(properties, "DefaultAction.Image.References");
      if (iconURL != null)
         hicon = new ImageIcon(iconURL);
      else
         hicon = new ImageIcon(ResourceManager.class.getClassLoader().getResource(
               "org/enhydra/jawe/images/references_small.gif"));
      componentSettings.put("DefaultAction.Icon." + JaWEActions.REFERENCES, hicon);

      loadDefaultMenusToolbarsAndActions(comp);

   }

   protected void loadDefaultMenusToolbarsAndActions(JaWEComponent comp) {
      // toolbar
      componentSettings.put("defaultToolbarToolbar", "ApplyChanges - DisplayParentElementPanel");
      // actions
      ActionBase action;
      ImageIcon icon;
      String langDepName;
      JaWEAction ja;

      // ApplyChanges
      action = new ApplyChanges(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/check.gif"));
      langDepName = "Apply";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // DisplayParentElementPanel
      action = new DisplayParentElementPanel(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource(
            "org/enhydra/jawe/images/parentelementpanel.gif"));
      langDepName = "ParentElementPanel";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);
   }

   public String getToolbarActionOrder(String toolbarName) {
      return (String) componentSettings.get(toolbarName + "Toolbar");
   }

}
