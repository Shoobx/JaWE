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

package org.enhydra.jawe.components.graph.actions;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.components.graph.GraphController;

/**
 * @author Sasa Bojanic
 */
public abstract class SetToolboxMode extends ActionBase {

   protected Cursor cursor;

   protected String type;

   protected String subType;

   public SetToolboxMode(GraphController jawecomponent, String type, String subType) {
      super(jawecomponent);
      this.type=type;
      this.subType=subType;
   }

   public void actionPerformed(ActionEvent e) {
      GraphController gc = (GraphController) jawecomponent;

      if (cursor == null) {
         ImageIcon curIc;
         Point hotSpot;
         Image curIm;

         JFrame fr = JaWEManager.getInstance().getJaWEController().getJaWEFrame();
         try {
            curIc=getIcon();
            hotSpot = new Point(curIc.getIconWidth() / 2, curIc.getIconHeight() / 2);
            curIm = curIc.getImage();
            cursor = fr.getToolkit().createCustomCursor(curIm,
                                                        hotSpot,
                                                        "ToolboxMode"+subType+"20x20");
         } catch (Exception ex) {
            JaWEManager.getInstance()
               .getLoggingManager()
               .error("Missing cursor image for ToolboxMode["+type+","+subType+"]!");
         }
      }

      gc.getGraphMarqueeHandler().setType(type,
                                          subType,
                                          cursor);
   }
   
   protected ImageIcon getIcon () {
      return JaWEManager.getInstance().getJaWEController().getJaWETypes().getType(subType).getIcon();
   }
}
