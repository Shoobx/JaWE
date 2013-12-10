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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.shark.xpdl.elements.Participant;
import org.jgraph.graph.GraphConstants;

/**
 * Class used to display participant object. It differs from other renderers because the
 * super class paint method isn't called.
 * 
 * @author Sasa Bojanic
 * @author Miroslav Popov
 */
public class DefaultGraphParticipantRenderer extends MultiLinedRenderer implements
                                                                       GraphParticipantRendererInterface {

   /**
    * Paints participant. Overrides superclass paint to add specific painting. Basically,
    * it draws a rectangle and vertical line, it draws it twice for selected participants.
    * Eventually departmetns name is displayed rotated and in color to indicate selection.
    * 
    * @param g Graphics to paint to
    */
   public void paint(Graphics g) {
      boolean rotateParticipant = GraphUtilities.getGraphParticipantOrientation(((Graph) graph).getWorkflowProcess(),
                                                                                ((Graph) graph).getXPDLObject())
         .equals(GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ORIENTATION_VALUE_VERTICAL);

      int participantNameWidth = GraphUtilities.getGraphController()
         .getGraphSettings()
         .getParticipantNameWidth();

      String label = graph.convertValueToString(view.getCell());
      Graphics2D g2 = (Graphics2D) g;
      Dimension d = view.getBounds().getBounds().getSize();// HM, JGraph3.4.1

      g2.setStroke(GraphSettings.DEPARTMENT_STROKE);

      Color bordCol = GraphUtilities.getGraphController()
         .getGraphSettings()
         .getParticipantBorderColor();
      g.setColor(bordCol);

      // paint bounds
      g.drawRect(0, 0, d.width - 1, d.height - 1);

      // width of name part
      GraphParticipantInterface p = (GraphParticipantInterface) view.getCell();
      Color pc;
      Participant par = (Participant) p.getPropertyObject();
      if (par instanceof FreeTextExpressionParticipant) {
         pc = GraphUtilities.getGraphController()
            .getGraphSettings()
            .getParticipantFreeTextExpressionColor();
      } else if (par instanceof CommonExpressionParticipant) {
         pc = GraphUtilities.getGraphController()
            .getGraphSettings()
            .getParticipantCommonExpressionColor();
      } else {
         pc = JaWEManager.getInstance()
            .getJaWEController()
            .getTypeResolver()
            .getJaWEType(par)
            .getColor();
      }

      g.setColor(pc);
      if (rotateParticipant)
         g.fillRect(1, 1, d.width - 3, participantNameWidth - 2);
      else
         g.fillRect(1, 1, participantNameWidth - 2, d.height - 3);
      g.setColor(bordCol);
      // }

      if (rotateParticipant)
         g.drawLine(0, participantNameWidth, d.width - 1, participantNameWidth);
      else
         g.drawLine(participantNameWidth, 0, participantNameWidth, d.height - 1);

      // this section paints participant in a case when object is selected
      // NOTE: hasFocus condition is removed because participant has focus
      // when any of it's children has
      if (selected) {// || hasFocus) {
         g2.setStroke(GraphConstants.SELECTION_STROKE);
         // if (hasFocus) g.setColor(graph.getGridColor());
         // else if (selected) g.setColor(graph.getHighlightColor());
         g.setColor(graph.getHighlightColor());
         g.drawRect(0, 0, d.width - 1, d.height - 1);

         if (rotateParticipant)
            g.drawLine(0, participantNameWidth, d.width - 1, participantNameWidth);
         else
            g.drawLine(participantNameWidth, 0, participantNameWidth, d.height - 1);

      }

      // Eventually, participants (participants) name comes on. For selected
      // one label is shown in highlight color (previously set), for others
      // label color is same as for border. Translate and rotate calls set
      // the drawing origin and direction, so label is displayed sidewise and
      // vertically centered.
      if (label != null) {
         if (!selected) {
            g2.setStroke(GraphSettings.DEPARTMENT_STROKE);
            // g.setColor(GraphUtilities.getGraphController().getGraphSettings().getParticipantTextColor());
         }

         Font f = GraphConstants.getFont(view.getAllAttributes());
         g2.setFont(f);
         int textL = getFontMetrics(f).stringWidth(label);
         int textH = getFontMetrics(f).getHeight();

         Graphics2D pg;
         if (rotateParticipant) {
            if (textL > d.width)
               textL = d.width;

            textL = (d.width - textL) / 2 - 10;
            pg = (Graphics2D) g.create(1, 1, d.width - 3, participantNameWidth - 2);
            pg.translate(textL, (participantNameWidth - textH) / 2);
            super.setBounds(new Rectangle(1, 1, d.width - 3, participantNameWidth - 2));
         } else {
            if (textL > d.height - 20)
               textL = d.height - 20;

            textL = d.height / 2 - textL / 2 - 10;
            pg = (Graphics2D) g.create(1, 1, participantNameWidth - 2, d.height - 3);
            pg.translate(1, d.height - textL);
            // pg.translate((participantNameWidth - textH) / 2, d.height - textL);
            pg.rotate(Math.toRadians(-90));
            super.setBounds(new Rectangle(1, 1, d.height - 4, participantNameWidth - 5));
         }

         super.paint(pg);
         // super.paint(g2);
      }
   }

   public ImageIcon getIcon() {
      GraphParticipantInterface p = (GraphParticipantInterface) view.getCell();

      Participant par = (Participant) p.getPropertyObject();
      if (par instanceof FreeTextExpressionParticipant) {
         return GraphUtilities.getGraphController()
            .getGraphSettings()
            .getFreeTextParticipantIcon();
      } else if (par instanceof CommonExpressionParticipant) {
         return GraphUtilities.getGraphController()
            .getGraphSettings()
            .getCommonExpresionParticipantIcon();
      } else {
         return JaWEManager.getInstance()
            .getJaWEController()
            .getTypeResolver()
            .getJaWEType(par)
            .getIcon();
      }
   }

   public void setTextPosition(int place) {
   }

}
