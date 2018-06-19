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

package org.enhydra.jawe.base.panel.panels;

import java.awt.Window;

import javax.swing.JPanel;

import org.enhydra.jawe.base.editor.XPDLElementEditor;
import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jxpdl.XMLElement;

/**
 * Standard interface for creating all panels for editing XPDL content.
 * 
 * @author Sasa Bojanic
 */
public abstract class XMLPanel extends JPanel {

   public abstract void setOwner(XMLElement el);

   public abstract XPDLElementEditor getEditor();

   public abstract XMLElement getOwner();

   // public abstract XMLElement getOrginalOwner ();

   public abstract void setBorder(String title, boolean hasBorder, boolean hasEmptyBorder, boolean displayTitleForEmptyBorder);

   public abstract String getTitle();

   public abstract boolean validateEntry();

   // Always returns true, this is set because of panels that are never empty
   // but this method is used when checking emptiness of group panel,
   // and panels that do not override this method should not be ever considered
   public abstract boolean isEmpty();

   public abstract void setElements();

   public abstract Object getValue();

   public abstract void updateView();

   public abstract void cleanup();
   // public abstract JComponent getMainComponent();

   public abstract Window getWindow();

   public abstract PanelContainer getPanelContainer();

   public abstract void canceled();
}
