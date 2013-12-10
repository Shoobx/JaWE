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

package org.enhydra.jawe.components.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.Utils;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.XPDLConstants;
import org.enhydra.shark.xpdl.elements.Activity;

/**
 * Class used to display activity object.
 * 
 * @author Sasa Bojanic
 * @author Miroslav Popov
 */
public class DefaultGraphActivityRenderer extends MultiLinedRenderer implements
                                                                    GraphActivityRendererInterface {

   protected static int arc = 5;

   /**
    * Paints activity. Overrides super class paint to add specific painting. First it
    * fills inner with color. Then it adds specific drawing for join type. Then it apply
    * JPanel with name and icon. At the end it draws shadow and border
    */
   public void paint(Graphics g) {
      int actW = GraphUtilities.getGraphController()
         .getGraphSettings()
         .getActivityWidth();
      int actH = GraphUtilities.getGraphController()
         .getGraphSettings()
         .getActivityHeight();
      int shadowWidth = GraphUtilities.getGraphController()
         .getGraphSettings()
         .getShadowWidth();
      boolean showShadow = GraphUtilities.getGraphController()
         .getGraphSettings()
         .isShadowEnabled();

      GraphActivityInterface gact = (GraphActivityInterface) view.getCell();
      Activity act = (Activity) gact.getUserObject();
      boolean frontJoin = false;
      if (XMLUtil.isANDTypeSplitOrJoin(act, 1))
         frontJoin = true;
      boolean backJoin = false;
      if (XMLUtil.isANDTypeSplitOrJoin(act, 0))
         backJoin = true;

      Color bckgC = getFillColor();
      if (selected)
         bckgC = GraphUtilities.getGraphController()
            .getGraphSettings()
            .getSelectedActivityColor();

      // fill activity
      g.setColor(bckgC);
      g.fillRoundRect(0, 0, actW - shadowWidth, actH - shadowWidth, arc, arc);

      // drawing panel
      super.setOpaque(false);
      Graphics gl = g.create(5, 5, actW - 9 - shadowWidth, actH - 9 - shadowWidth);
      Rectangle panelRect = new Rectangle(0, 0, actW - 9 - shadowWidth, actH
                                                                        - 9 - shadowWidth);
      super.setBounds(panelRect);
      graph.setHighlightColor(bckgC);
      setBorder(BorderFactory.createLineBorder(bckgC, 0));
      super.paint(gl);
      setBorder(BorderFactory.createLineBorder(bordercolor, 0));
      setForeground(bordercolor);

      // shadow
      if (showShadow) {
         g.setColor(new Color(192, 192, 192));
         ((Graphics2D) g).setStroke(new BasicStroke(shadowWidth,
                                                    BasicStroke.CAP_BUTT,
                                                    BasicStroke.JOIN_ROUND));
         g.drawLine(shadowWidth, actH - shadowWidth, actW - shadowWidth, actH
                                                                         - shadowWidth);
         if (!backJoin)
            g.drawLine(actW - shadowWidth,
                       actH - shadowWidth,
                       actW - shadowWidth,
                       shadowWidth);
      }

      // draw border
      g.setColor(bordercolor);
      ((Graphics2D) g).setStroke(borderStroke);
      g.drawRoundRect(0, 0, actW - 1 - shadowWidth, actH - 1 - shadowWidth, arc, arc);

      // add > to front
      Color gCol = GraphUtilities.getGraphController()
         .getGraphSettings()
         .getBackgroundColor();
      if (frontJoin) {
         g.setColor(gCol);
         int[] x = {
               0, 4, 0
         };
         int[] y = {
               arc, actH / 2, actH - arc
         };
         g.fillPolygon(x, y, 3);
         g.setColor(bordercolor);
         ((Graphics2D) g).setStroke(borderStroke);
         g.drawLine(x[0], y[0], x[1], y[1]);
         g.drawLine(x[1], y[1], x[2], y[2]);
      }
      // add > to back
      if (backJoin) {
         g.setColor(gCol);
         // clean
         int[] x = {
               actW - shadowWidth - 4,
               actW,
               actW,
               actW - shadowWidth - 4,
               actW - shadowWidth
         };
         int[] y = {
               0, 0, actH, actH, actH / 2
         };
         g.fillPolygon(x, y, 5);
         g.setColor(new Color(192, 192, 192));
         ((Graphics2D) g).setStroke(new BasicStroke(shadowWidth,
                                                    BasicStroke.CAP_BUTT,
                                                    BasicStroke.JOIN_ROUND));
         g.drawLine(x[0] + 1, y[0], x[4] + 1, y[4]);
         g.drawLine(x[4] + 1, y[4], x[3] + 1, y[3] - shadowWidth);
         g.setColor(bordercolor);
         ((Graphics2D) g).setStroke(borderStroke);
         g.drawLine(x[0], y[0], x[4], y[4]);
         g.drawLine(x[4], y[4], x[3], y[3] - shadowWidth);
      }

      int type = act.getActivityType();
      if (type == XPDLConstants.ACTIVITY_TYPE_BLOCK
          && GraphUtilities.getGraphController()
             .getGraphSettings()
             .shouldDrawBlockLines()) {
         g.setColor(bordercolor);
         g.drawLine(3, 0, 3, actH - 2 - shadowWidth);
         g.drawLine(actW - 4 - shadowWidth, 0, actW - 4 - shadowWidth, actH
                                                                       - 2 - shadowWidth);
      } else if (type == XPDLConstants.ACTIVITY_TYPE_SUBFLOW
                 && GraphUtilities.getGraphController()
                    .getGraphSettings()
                    .shouldDrawSubflowLines()) {
         g.setColor(bordercolor);
         ((Graphics2D) g).setStroke(borderStroke);
         g.drawRect(3, 3, actW - 7 - shadowWidth, actH - 7 - shadowWidth);
      }
   }

   protected Color getFillColor() {
      Activity act = (Activity) ((GraphActivityInterface) view.getCell()).getUserObject();
      Color c = JaWEManager.getInstance()
         .getJaWEController()
         .getTypeResolver()
         .getJaWEType(act)
         .getColor();
      GraphSettings gv = GraphUtilities.getGraphController().getGraphSettings();
      if (!gv.shouldUseBubbles()) {
         boolean isStartingAct = JaWEManager.getInstance()
            .getXPDLUtils()
            .isStartingActivity(act);
         boolean isEndingAct = JaWEManager.getInstance()
            .getXPDLUtils()
            .isEndingActivity(act);
         if (isStartingAct && isEndingAct) {
            c = gv.getStartEndActivityColor();
         } else if (isStartingAct) {
            c = gv.getStartActivityColor();
         } else if (isEndingAct) {
            c = gv.getEndActivityColor();
         }
      }
      return c;
   }

   public ImageIcon getIcon() {
      Activity act = (Activity) ((GraphActivityInterface) view.getCell()).getUserObject();

      String icon = act.getIcon();

      ImageIcon ii = null;
      if (!icon.equals("")) {
         ii = (ImageIcon) Utils.getOriginalActivityIconsMap().get(icon);
      }

      if (ii == null) {
         ii = JaWEManager.getInstance()
            .getJaWEController()
            .getTypeResolver()
            .getJaWEType(act)
            .getIcon();
      }

      return ii;
   }
   
}
