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
 * Miroslav Popov, Dec 2, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.base.editor;

import java.awt.Color;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.swing.ImageIcon;

import org.enhydra.jawe.AdditionalResourceManager;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.base.controller.JaWEActions;
import org.enhydra.jawe.base.panel.PanelSettings;
import org.enhydra.jxpdl.XMLUtil;

/**
 * @author Miroslav Popov
 */
public class StandardXPDLElementEditorSettings extends PanelSettings {

   public void init(JaWEComponent comp) {
      loadDefault(comp, new Properties());
   }

   public void loadDefault(JaWEComponent comp, Properties properties) {
      arm = new AdditionalResourceManager(properties);

      componentSettings.put("UseScrollBar",
                            new Boolean(properties.getProperty("InlinePanel.UseScrollBar",
                                                               "true")
                               .equals("true")));
      componentSettings.put("ShowModifiedWarning",
                            new Boolean(properties.getProperty("InlinePanel.ShowModifiedWarning",
                                                               "true")
                               .equals("true")));
      componentSettings.put("DisplayTitle",
                            new Boolean(properties.getProperty("InlinePanel.DisplayTitle",
                                                               "false")
                               .equals("true")));
      componentSettings.put("XMLActualParametersPanel.useAdvanced",
                            new Boolean(properties.getProperty("XMLActualParametersPanel.useAdvanced",
                                                               "true")
                               .equals("true")));
      componentSettings.put("XMLActualParametersPanel.preferredNumberOfLinesForExpression",
                            new Integer(properties.getProperty("XMLActualParametersPanel.preferredNumberOfLinesForExpression",
                                                               "4")));
      componentSettings.put("PreferredNumberOfLinesForExpression",
                            new Integer(properties.getProperty("PreferredNumberOfLinesForExpression",
                                                               "8")));
      componentSettings.put("XMLBasicPanel.RightAllignment",
                            new Boolean(properties.getProperty("XMLBasicPanel.RightAllignment",
                                                               "false")
                               .equals("true")));
      componentSettings.put("XMLDataTypesPanel.HasBorder",
                            new Boolean(properties.getProperty("XMLDataTypesPanel.HasBorder",
                                                               "false")
                               .equals("true")));

      componentSettings.put("EmptyBorder.TOP",
                            new Integer(properties.getProperty("XMLBasicPanel.EmptyBorder.TOP",
                                                               "0")));
      componentSettings.put("EmptyBorder.LEFT",
                            new Integer(properties.getProperty("XMLBasicPanel.EmptyBorder.LEFT",
                                                               "3")));
      componentSettings.put("EmptyBorder.BOTTOM",
                            new Integer(properties.getProperty("XMLBasicPanel.EmptyBorder.BOTTOM",
                                                               "4")));
      componentSettings.put("EmptyBorder.RIGHT",
                            new Integer(properties.getProperty("XMLBasicPanel.EmptyBorder.RIGHT",
                                                               "3")));
      componentSettings.put("SimplePanelTextWidth",
                            new Integer(properties.getProperty("XMLBasicPanel.SimplePanelTextWidth",
                                                               "400")));
      componentSettings.put("SimplePanelTextHeight",
                            new Integer(properties.getProperty("XMLBasicPanel.SimplePanelTextHeight",
                                                               "20")));
      componentSettings.put("XMLDataTypesPanel.Dimension.WIDTH",
                            new Integer(properties.getProperty("XMLDataTypesPanel.Dimension.WIDTH",
                                                               "500")));
      componentSettings.put("XMLDataTypesPanel.Dimension.HEIGHT",
                            new Integer(properties.getProperty("XMLDataTypesPanel.Dimension.HEIGHT",
                                                               "125")));

      componentSettings.put("XMLComboPanel.DisableCombo",
                            properties.getProperty("XMLComboPanel.DisableCombo", ""));

      componentSettings.put("ApplyActionSavesXPDL",
                            new Boolean(properties.getProperty("ApplyActionSavesXPDL", "false")));
      
      Color color = null;
      try {
         color = Utils.getColor(ResourceManager.getResourceString(properties,
                                                                  "BackgroundColor"));
      } catch (Exception e) {
         color = Utils.getColor("R=245,G=245,B=245");
      }
      componentSettings.put("BackgroundColor", color);

      ImageIcon hicon;
      URL iconURL = ResourceManager.getResource(properties, "ArrowRightImage");
      if (iconURL != null)
         hicon = new ImageIcon(iconURL);
      else
         hicon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/navigate_right.png"));
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
         hicon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/arrowdown.gif"));
      componentSettings.put("ArrowDownImage", hicon);

      iconURL = ResourceManager.getResource(properties, "InsertVariableDefault.Icon");
      if (iconURL != null)
         hicon = new ImageIcon(iconURL);
      else
         hicon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/navigate_right2.png"));
      componentSettings.put("InsertVariableDefault", hicon);

      iconURL = ResourceManager.getResource(properties, "InsertVariablePressed.Icon");
      if (iconURL != null)
         hicon = new ImageIcon(iconURL);
      else
         hicon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/navigate_down2.png"));
      componentSettings.put("InsertVariablePressed", hicon);

      iconURL = ResourceManager.getResource(properties, "DefaultAction.Image.New");
      if (iconURL != null)
         hicon = new ImageIcon(iconURL);
      else
         hicon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/new_small.gif"));
      componentSettings.put("DefaultAction.Icon." + JaWEActions.NEW_ACTION, hicon);

      iconURL = ResourceManager.getResource(properties,
                                            "DefaultAction.Image.EditProperties");
      if (iconURL != null)
         hicon = new ImageIcon(iconURL);
      else
         hicon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/edit_small.gif"));
      componentSettings.put("DefaultAction.Icon." + JaWEActions.EDIT_PROPERTIES_ACTION,
                            hicon);

      iconURL = ResourceManager.getResource(properties, "DefaultAction.Image.Delete");
      if (iconURL != null)
         hicon = new ImageIcon(iconURL);
      else
         hicon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/delete_small.gif"));
      componentSettings.put("DefaultAction.Icon." + JaWEActions.DELETE_ACTION, hicon);

      iconURL = ResourceManager.getResource(properties, "DefaultAction.Image.Duplicate");
      if (iconURL != null)
         hicon = new ImageIcon(iconURL);
      else
         hicon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/duplicate_small.gif"));
      componentSettings.put("DefaultAction.Icon." + JaWEActions.DUPLICATE_ACTION, hicon);

      iconURL = ResourceManager.getResource(properties, "DefaultAction.Image.References");
      if (iconURL != null)
         hicon = new ImageIcon(iconURL);
      else
         hicon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/references_small.gif"));
      componentSettings.put("DefaultAction.Icon." + JaWEActions.REFERENCES, hicon);

      componentSettings.put("HideSubElements.XMLGroupPanel.Activity",
                            "Deadlines ExtendedAttributes SimulationInformation TransitionRestrictions");
      componentSettings.put("HideSubElements.XMLGroupPanel.Association",
                            "ConnectorGraphicsInfos");
      componentSettings.put("HideSubElements.XMLGroupPanel.Artifact", "NodeGraphicsInfos");
      componentSettings.put("HideSubElements.XMLGroupPanel.Package",
                            "PackageHeader RedefinableHeader WorkflowProcesses Applications Associations Artifacts Pools Participants DataFields TypeDeclarations ExternalPackages Namespaces ExtendedAttributes");
      componentSettings.put("HideSubElements.XMLGroupPanel.Transition",
                            "ExtendedAttributes");
      componentSettings.put("HideSubElements.XMLGroupPanel.WorkflowProcess",
                            "ProcessHeader RedefinableHeader Applications Participants DataFields FormalParameters ActivitySets Activities Transitions ExtendedAttributes");

      List hideEls = ResourceManager.getResourceStrings(properties,
                                                        "HideSubElements.",
                                                        false);
      for (int i = 0; i < hideEls.size(); i++) {
         componentSettings.put("HideSubElements." + (String) hideEls.get(i),
                               ResourceManager.getResourceString(properties,
                                                                 "HideSubElements."
                                                                       + (String) hideEls.get(i)));
      }

      componentSettings.put("HideElements.XMLTablePanel.ExtendedAttributes", "Name");
      componentSettings.put("HideElements.XMLTablePanel.ExtendedAttributes.Name",
                            "JaWE_CONFIGURATION JaWE_TYPE EDITING_TOOL EDITING_TOOL_VERSION");

      List hide = ResourceManager.getResourceStrings(properties, "HideElements.", false);
      for (int i = 0; i < hide.size(); i++) {
         String line = (String) hide.get(i);
         String[] parts = XMLUtil.tokenize(line, ".");

         if (parts.length != 3)
            continue;

         String panelName = parts[0];
         String elName = parts[1];
         String flName = parts[2];

         if ("XMLTablePanel".equals(panelName)
             && "ExtendedAttributes".equals(elName) && "Name".equals(flName)) {
            String elHiddenEl = (String) componentSettings.get("HideElements."
                                                               + panelName + "." + elName);
            elHiddenEl = elHiddenEl.replaceAll(flName, "");
            if (elHiddenEl.length() == 0)
               componentSettings.remove("HideElements.XMLTablePanel.ExtendedAttributes");
            else
               componentSettings.put("HideElements.XMLTablePanel.ExtendedAttributes",
                                     elHiddenEl);
            componentSettings.remove("HideElements.XMLTablePanel.ExtendedAttributes.Name");
         }

         String elHiddenEl = (String) componentSettings.get("HideElements."
                                                            + panelName + "." + elName);
         if (elHiddenEl == null)
            elHiddenEl = flName;
         else
            elHiddenEl += " " + flName;

         String hel = ResourceManager.getResourceString(properties, "HideElements."
                                                                    + panelName + "."
                                                                    + elName + "."
                                                                    + flName);
         componentSettings.put("HideElements." + panelName + "." + elName, elHiddenEl);
         componentSettings.put("HideElements." + panelName + "." + elName + "." + flName,
                               hel);
      }

      componentSettings.put("ShowColumns.XMLTablePanel.Activities",
                            "Id Name Performer Type StartMode FinishMode Deadlines");
      componentSettings.put("ShowColumns.XMLTablePanel.ActivitySets",
                            "Id Activities Transitions");
      componentSettings.put("ShowColumns.XMLTablePanel.ActualParameters", "ElementValue");
      componentSettings.put("ShowColumns.XMLTablePanel.Applications", "Id Name");
      componentSettings.put("ShowColumns.XMLTablePanel.Associations",
                            "Id Name Source Target AssociationDirection");
      componentSettings.put("ShowColumns.XMLTablePanel.Artifacts",
                            "Id Name ArtifactType TextAnnotation");
      componentSettings.put("ShowColumns.XMLTablePanel.DataFields",
                            "Id Name DataType IsArray InitialValue");
      componentSettings.put("ShowColumns.XMLTablePanel.ExtendedAttributes", "Name Value");
      componentSettings.put("ShowColumns.XMLTablePanel.FormalParameters",
                            "Id Name Mode DataType IsArray InitialValue");
      componentSettings.put("ShowColumns.XMLTablePanel.Namespaces", "Name location");
      componentSettings.put("ShowColumns.XMLTablePanel.Participants",
                            "Id Name ParticipantType");
      componentSettings.put("ShowColumns.XMLTablePanel.Pools",
                            "Id Name Orientation Process Lanes");
      componentSettings.put("ShowColumns.XMLTablePanel.Lanes",
                            "Id Name Performers NestedLanes");
      componentSettings.put("ShowColumns.XMLTablePanel.Transitions",
                            "Id From To Condition");
      componentSettings.put("ShowColumns.XMLTablePanel.TypeDeclarations",
                            "Id Name DataTypes");
      componentSettings.put("ShowColumns.XMLTablePanel.WorkflowProcesses",
                            "Id Name AccessLevel DataFields FormalParameters");

      List showCols = ResourceManager.getResourceStrings(properties,
                                                         "ShowColumns.",
                                                         false);
      for (int i = 0; i < showCols.size(); i++) {
         componentSettings.put("ShowColumns." + (String) showCols.get(i),
                               ResourceManager.getResourceString(properties,
                                                                 "ShowColumns."
                                                                       + (String) showCols.get(i)));
      }

      loadDefaultMenusToolbarsAndActions(comp);

   }

   protected void loadDefaultMenusToolbarsAndActions(JaWEComponent comp) {
   }

   public String getToolbarActionOrder(String toolbarName) {
      return (String) componentSettings.get(toolbarName + "Toolbar");
   }

}
