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

package org.enhydra.jawe.base.panel;

import java.util.EventObject;

import org.enhydra.jawe.Settings;
import org.enhydra.jawe.base.display.DisplayNameGenerator;
import org.enhydra.jawe.base.label.LabelGenerator;
import org.enhydra.jawe.base.panel.panels.XMLPanel;
import org.enhydra.jawe.base.tooltip.TooltipGenerator;


/**
 * Listener of the panel events.
 *
 *  @author Sasa Bojanic
 */
public interface PanelContainer {

   void panelChanged (XMLPanel panel,EventObject ev);
   
   String getLanguageDependentString (String nm);
   
   Settings getSettings ();
   
   PanelGenerator getPanelGenerator ();
   
   LabelGenerator getLabelGenerator();
   
   DisplayNameGenerator getDisplayNameGenerator();
   
   PanelValidator getPanelValidator ();
   
   TooltipGenerator getTooltipGenerator ();
   
}
   
