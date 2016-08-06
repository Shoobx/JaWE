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
import java.util.Properties;

import org.enhydra.jawe.AdditionalResourceManager;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.base.panel.PanelSettings;

/**
 * @author Miroslav Popov
 */
public class TableEditorSettings extends PanelSettings {

   public void init(JaWEComponent comp) {
      loadDefault(comp, new Properties());
   }

   public void loadDefault(JaWEComponent comp, Properties properties) {
      arm = new AdditionalResourceManager(properties);

      componentSettings.put("UseScrollBar",
                            new Boolean(properties.getProperty("InlinePanel.UseScrollBar",
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
      componentSettings.put("ApplyActionSavesXPDL",
                            new Boolean(properties.getProperty("ApplyActionSavesXPDL", "false")));

      componentSettings.put("ShowColumns.XMLTablePanel.Applications", "Id Name");
      componentSettings.put("ShowColumns.XMLTablePanel.Associations",
                            "Id Name Source Target AssociationDirection");
      componentSettings.put("ShowColumns.XMLTablePanel.Artifacts",
                            "Id Name ArtifactType TextAnnotation");
      componentSettings.put("ShowColumns.XMLTablePanel.Participants",
                            "Id Name ParticipantType");
      componentSettings.put("ShowColumns.XMLTablePanel.WorkflowProcesses",
                            "Id Name AccessLevel");

      Color color = null;
      try {
         color = Utils.getColor(ResourceManager.getResourceString(properties,
                                                                  "BackgroundColor"));
      } catch (Exception e) {
         color = Utils.getColor("R=245,G=245,B=245");
      }
      componentSettings.put("BackgroundColor", color);

   }
}
