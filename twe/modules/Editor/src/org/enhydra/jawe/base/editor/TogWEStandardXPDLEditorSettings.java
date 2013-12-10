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

/**
 * Miroslav Popov, Dec 6, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.base.editor;

import java.awt.Color;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.swing.ImageIcon;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.AdditionalResourceManager;
import org.enhydra.jawe.JaWEAction;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.base.controller.JaWEActions;
import org.enhydra.jawe.base.editor.actions.Apply;
import org.enhydra.jawe.base.editor.actions.ApplyAndClose;
import org.enhydra.jawe.base.editor.actions.NextPanel;
import org.enhydra.jawe.base.editor.actions.ParentElementPanel;
import org.enhydra.jawe.base.editor.actions.PreviousPanel;
import org.enhydra.jawe.base.editor.actions.Revert;
import org.enhydra.jawe.base.panel.PanelSettings;
import org.enhydra.jxpdl.XMLUtil;

/**
 * @author Miroslav Popov
 *
 */
public class TogWEStandardXPDLEditorSettings extends PanelSettings {

   public void init(JaWEComponent comp) {
      PROPERTYFILE_PATH = "org/enhydra/jawe/base/editor/properties/";
      PROPERTYFILE_NAME = "togweeditor.properties";
      super.init(comp);
   }

   public void loadDefault (JaWEComponent comp,Properties properties) {
      // defaults
        arm = new AdditionalResourceManager(properties);

        componentSettings.put("UseScrollBar", new Boolean(properties.getProperty("InlinePanel.UseScrollBar","false").equals("true")));
        componentSettings.put("ShowModifiedWarning", new Boolean(properties.getProperty("InlinePanel.ShowModifiedWarning","true").equals("true")));
        componentSettings.put("DisplayTitle", new Boolean(properties.getProperty("InlinePanel.DisplayTitle","false").equals("true")));
        componentSettings.put("XMLBasicPanel.RightAllignment", new Boolean(properties.getProperty("XMLBasicPanel.RightAllignment","false").equals("true")));
        componentSettings.put("XMLDataTypesPanel.HasBorder", new Boolean(properties.getProperty("XMLDataTypesPanel.HasBorder","false").equals("true")));


        componentSettings.put("EmptyBorder.TOP", new Integer(properties.getProperty("XMLBasicPanel.EmptyBorder.TOP","0")));
        componentSettings.put("EmptyBorder.LEFT", new Integer(properties.getProperty("XMLBasicPanel.EmptyBorder.LEFT","3")));
        componentSettings.put("EmptyBorder.BOTTOM", new Integer(properties.getProperty("XMLBasicPanel.EmptyBorder.BOTTOM","4")));
        componentSettings.put("EmptyBorder.RIGHT", new Integer(properties.getProperty("XMLBasicPanel.EmptyBorder.RIGHT","3")));
        componentSettings.put("SimplePanelTextWidth", new Integer(properties.getProperty("XMLBasicPanel.SimplePanelTextWidth","250")));
        componentSettings.put("SimplePanelTextHeight", new Integer(properties.getProperty("XMLBasicPanel.SimplePanelTextHeight","20")));
        componentSettings.put("XMLDataTypesPanel.Dimension.WIDTH", new Integer(properties.getProperty("XMLDataTypesPanel.Dimension.WIDTH","400")));
        componentSettings.put("XMLDataTypesPanel.Dimension.HEIGHT", new Integer(properties.getProperty("XMLDataTypesPanel.Dimension.HEIGHT","125")));

        componentSettings.put("XMLComboPanel.DisableCombo", properties.getProperty("XMLComboPanel.DisableCombo",""));

        componentSettings.put("HistoryManager.Class", properties.getProperty("HistoryManager.Class","org.enhydra.jawe.HistoryMgr"));
        componentSettings.put("HistorySize", new Integer(properties.getProperty("HistorySize","15")));

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
           hicon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/navigate_right.png"));
        componentSettings.put("ArrowRightImage", hicon);

        iconURL = ResourceManager.getResource(properties, "ArrowUpImage");
        if (iconURL != null)
           hicon = new ImageIcon(iconURL);
        else
           hicon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/arrowup.gif"));
        componentSettings.put("ArrowUpImage", hicon);

        iconURL = ResourceManager.getResource(properties, "ArrowDownImage");
        if (iconURL != null)
           hicon = new ImageIcon(iconURL);
        else
           hicon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/arrowdown.gif"));
        componentSettings.put("ArrowDownImage", hicon);

        iconURL = ResourceManager.getResource(properties, "InsertVariableDefault.Icon");
        if (iconURL != null)
           hicon = new ImageIcon(iconURL);
        else
           hicon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/navigate_right2.png"));
        componentSettings.put("InsertVariableDefault", hicon);

        iconURL = ResourceManager.getResource(properties, "InsertVariablePressed.Icon");
        if (iconURL != null)
           hicon = new ImageIcon(iconURL);
        else
           hicon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/navigate_down2.png"));
        componentSettings.put("InsertVariablePressed", hicon);

        iconURL = ResourceManager.getResource(properties, "DefaultAction.Image.New");
        if (iconURL != null)
           hicon = new ImageIcon(iconURL);
        else
           hicon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/new_small.gif"));
        componentSettings.put("DefaultAction.Icon." + JaWEActions.NEW_ACTION, hicon);

        iconURL = ResourceManager.getResource(properties, "DefaultAction.Image.EditProperties");
        if (iconURL != null)
           hicon = new ImageIcon(iconURL);
        else
           hicon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/edit_small.gif"));
        componentSettings.put("DefaultAction.Icon." + JaWEActions.EDIT_PROPERTIES_ACTION, hicon);

        iconURL = ResourceManager.getResource(properties, "DefaultAction.Image.Delete");
        if (iconURL != null)
           hicon = new ImageIcon(iconURL);
        else
           hicon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/delete_small.gif"));
        componentSettings.put("DefaultAction.Icon." + JaWEActions.DELETE_ACTION, hicon);

        iconURL = ResourceManager.getResource(properties, "DefaultAction.Image.Duplicate");
        if (iconURL != null)
           hicon = new ImageIcon(iconURL);
        else
           hicon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/duplicate_small.gif"));
        componentSettings.put("DefaultAction.Icon." + JaWEActions.DUPLICATE_ACTION, hicon);

        iconURL = ResourceManager.getResource(properties, "DefaultAction.Image.References");
        if (iconURL != null)
           hicon = new ImageIcon(iconURL);
        else
           hicon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/references_small.gif"));
        componentSettings.put("DefaultAction.Icon." + JaWEActions.REFERENCES, hicon);

        componentSettings.put("HideSubElements.XMLGroupPanel.Activity", "NodeGraphicsInfos");
        componentSettings.put("HideSubElements.XMLGroupPanel.Association", "Object ConnectorGraphicsInfos");
        componentSettings.put("HideSubElements.XMLGroupPanel.Artifact", "NodeGraphicsInfos");
        componentSettings.put("HideSubElements.XMLGroupPanel.Transition", "ConnectorGraphicsInfos");
        componentSettings.put("HideSubElements.XMLGroupPanel.Lane", "NodeGraphicsInfos");
        componentSettings.put("HideSubElements.XMLGroupPanel.Pool", "NodeGraphicsInfos");

        List hideEls = ResourceManager.getResourceStrings(properties, "HideSubElements.", false);
        for (int i = 0; i < hideEls.size(); i++) {
           componentSettings.put("HideSubElements." + (String) hideEls.get(i), ResourceManager.getResourceString(properties, "HideSubElements." + (String)hideEls.get(i)));
        }

        componentSettings.put("HideElements.XMLTablePanel.ExtendedAttributes", "Name");
        componentSettings.put("HideElements.XMLTablePanel.ExtendedAttributes.Name", "JaWE_CONFIGURATION JaWE_TYPE EDITING_TOOL EDITING_TOOL_VERSION");

        List hide = ResourceManager.getResourceStrings(properties, "HideElements.", false);
        for (int i = 0; i < hide.size(); i++) {
           String line = (String) hide.get(i);
           String[] parts = XMLUtil.tokenize(line, ".");

           if (parts.length != 3)
              continue;

           String panelName = parts[0];
           String elName = parts[1];
           String flName = parts[2];

           if ("XMLTablePanel".equals(panelName) && "ExtendedAttributes".equals(elName) && "Name".equals(flName)) {
              String elHiddenEl = (String) componentSettings.get("HideElements." + panelName + "." +  elName);
              elHiddenEl = elHiddenEl.replaceAll(flName, "");
              if (elHiddenEl.length() == 0)
                 componentSettings.remove("HideElements.XMLTablePanel.ExtendedAttributes");
              else
                 componentSettings.put("HideElements.XMLTablePanel.ExtendedAttributes", elHiddenEl);
              componentSettings.remove("HideElements.XMLTablePanel.ExtendedAttributes.Name");
           }

           String elHiddenEl = (String) componentSettings.get("HideElements." + panelName + "." +  elName);
           if (elHiddenEl == null)
              elHiddenEl = flName;
           else
              elHiddenEl += " " + flName;

           String hel = ResourceManager.getResourceString(properties, "HideElements." + panelName + "." + elName + "." + flName);
           componentSettings.put("HideElements." + panelName + "." + elName, elHiddenEl);
           componentSettings.put("HideElements." + panelName + "." + elName + "." + flName, hel);
        }

        componentSettings.put("ShowColumns.XMLTablePanel.Activities", "Id Name Performer Type StartMode FinishMode Deadlines");
        componentSettings.put("ShowColumns.XMLTablePanel.ActivitySets", "Id Activities Transitions");
        componentSettings.put("ShowColumns.XMLTablePanel.ActualParameters","ElementValue");
        componentSettings.put("ShowColumns.XMLTablePanel.Applications", "Id Name");
        componentSettings.put("ShowColumns.XMLTablePanel.Associations", "Id Name Source Target AssociationDirection");
        componentSettings.put("ShowColumns.XMLTablePanel.Artifacts", "Id Name ArtifactType TextAnnotation");
        componentSettings.put("ShowColumns.XMLTablePanel.DataFields", "Id Name DataType InitialValue");
        componentSettings.put("ShowColumns.XMLTablePanel.ExtendedAttributes", "Name Value");
        componentSettings.put("ShowColumns.XMLTablePanel.FormalParameters", "Id Name Mode DataType");
        componentSettings.put("ShowColumns.XMLTablePanel.Namespaces", "Name location");
        componentSettings.put("ShowColumns.XMLTablePanel.Participants", "Id Name ParticipantType");
        componentSettings.put("ShowColumns.XMLTablePanel.Pools", "Id Name Orientation Process Lanes");
        componentSettings.put("ShowColumns.XMLTablePanel.Lanes", "Id Name Performers NestedLanes");
        componentSettings.put("ShowColumns.XMLTablePanel.Transitions", "Id From To Condition");
        componentSettings.put("ShowColumns.XMLTablePanel.TypeDeclarations", "Id Name DataTypes");
        componentSettings.put("ShowColumns.XMLTablePanel.WorkflowProcesses", "Id Name AccessLevel DataFields FormalParameters");

        List showCols = ResourceManager.getResourceStrings(properties, "ShowColumns.", false);
        for (int i = 0; i < showCols.size(); i++) {
           componentSettings.put("ShowColumns." + (String) showCols.get(i), ResourceManager.getResourceString(properties, "ShowColumns." + (String)showCols.get(i)));
        }

        loadDefaultMenusToolbarsAndActions(comp);
        loadDefaultMenusToolbarsAndActions(comp);
        componentSettings.putAll(Utils.loadAllMenusAndToolbars(properties));
        componentAction.putAll(Utils.loadActions(properties, comp, componentAction));
     }

     protected void loadDefaultMenusToolbarsAndActions(JaWEComponent comp) {
        // toolbar
        componentSettings.put("defaultToolbarToolbar", "PreviousPanel NextPanel - Revert Apply ApplyAndClose - ParentElementPanel");

        // actions
        ActionBase action;
        ImageIcon icon;
        String langDepName;
        JaWEAction ja;

        // PreviousPanel
        action = new PreviousPanel(comp);
        icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/nav_left_red.png"));
        langDepName = "PreviousPanel";
        ja = new JaWEAction(action, icon, langDepName);
        componentAction.put("PreviousPanel", ja);

        // NextPanel
        action = new NextPanel(comp);
        icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/nav_right_red.png"));
        langDepName = "NextPanel";
        ja = new JaWEAction(action, icon, langDepName);
        componentAction.put("NextPanel", ja);

        // Apply
        action = new Apply(comp);
        icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/check.gif"));
        langDepName = "Apply";
        ja = new JaWEAction(action, icon, langDepName);
        componentAction.put("Apply", ja);

        // ApplyAndClose
        action = new ApplyAndClose(comp);
        icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/applyandclose.gif"));
        langDepName = "ApplyAndClose";
        ja = new JaWEAction(action, icon, langDepName);
        componentAction.put("ApplyAndClose", ja);

        // ParentElementPanel
        action = new ParentElementPanel(comp);
        icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/parentelementpanel.gif"));
        langDepName = "ParentElementPanel";
        ja = new JaWEAction(action, icon, langDepName);
        componentAction.put("ParentElementPanel", ja);

        // Revert
        action = new Revert(comp);
        icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/revert.png"));
        langDepName = "Revert";
        ja = new JaWEAction(action, icon, langDepName);
        componentAction.put("Revert", ja);

     }

     public String getToolbarActionOrder(String toolbarName) {
        return (String) componentSettings.get(toolbarName + "Toolbar");
     }

}
