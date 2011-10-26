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
 * Miroslav Popov, Dec 5, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.base.panel;

import java.awt.Color;

import javax.swing.ImageIcon;

import org.enhydra.jawe.JaWEComponentSettings;

/**
 * @author Miroslav Popov
 *
 */
public abstract class PanelSettings extends JaWEComponentSettings {
   
   public boolean useScrollBar() {
      return ((Boolean) componentSettings.get("UseScrollBar")).booleanValue();
   }
   
   public boolean shouldXMLBasicPanelUseRightAllignment() {
      return ((Boolean) componentSettings.get("XMLBasicPanel.RightAllignment")).booleanValue();
   }
   
   public boolean doesXMLDataTypesPanelHasBorder() {
      return ((Boolean) componentSettings.get("XMLDataTypesPanel.HasBorder")).booleanValue();
   }
   
   public boolean shouldShowModifiedWarning() {
      return ((Boolean) componentSettings.get("ShowModifiedWarning")).booleanValue();
   }
   
   public boolean shouldDisplayTitle() {
      return ((Boolean) componentSettings.get("DisplayTitle")).booleanValue();
   }
   
   public int getEmptyBorderTop() {
      return ((Integer) componentSettings.get("EmptyBorder.TOP")).intValue();
   }
   
   public int getEmptyBorderLeft() {
      return ((Integer) componentSettings.get("EmptyBorder.LEFT")).intValue();
   }
   
   public int getEmptyBorderBottom() {
      return ((Integer) componentSettings.get("EmptyBorder.BOTTOM")).intValue();
   }
   
   public int getEmptyBorderRight() {
      return ((Integer) componentSettings.get("EmptyBorder.RIGHT")).intValue();
   }
   
   public int getSimplePanelTextWidth() {
      return ((Integer) componentSettings.get("SimplePanelTextWidth")).intValue();
   }
   
   public int getSimplePanelTextHeight() {
      return ((Integer) componentSettings.get("SimplePanelTextHeight")).intValue();
   }

   public int getXMLDataTypesPanelWidth() {
      return ((Integer) componentSettings.get("XMLDataTypesPanel.Dimension.WIDTH")).intValue();
   }
   
   public int getXMLDataTypesPanelHeight() {
      return ((Integer) componentSettings.get("XMLDataTypesPanel.Dimension.HEIGHT")).intValue();
   }
   
   public String disableComboInXMLComboPanelFor() {
      return (String) componentSettings.get("XMLComboPanel.DisableCombo");
   }
   
   public ImageIcon getArrowRightImageIcon() {
      return (ImageIcon) componentSettings.get("ArrowRightImage");
   }
   
   public ImageIcon getArrowUpImageIcon() {
      return (ImageIcon) componentSettings.get("ArrowUpImage");
   }
   
   public ImageIcon getArrowDownImageIcon() {
      return (ImageIcon) componentSettings.get("ArrowDownImage");
   }
   
   public ImageIcon getInsertVariableDefaultIcon() {
      return (ImageIcon) componentSettings.get("InsertVariableDefault");
   }
   
   public ImageIcon getInsertVariablePressedIcon() {
      return (ImageIcon) componentSettings.get("InsertVariablePressed");
   }
   
   public String historyManagerClass () {
      return (String) componentSettings.get("HistoryManager.Class");      
   }

   public int historySize () {
      return ((Integer) componentSettings.get("HistorySize")).intValue();      
   }
   
   public Color getBackgroundColor() {
      return (Color) componentSettings.get("BackgroundColor");
   }
   
}
