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

package org.enhydra.jawe.components.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import org.enhydra.jawe.JaWEConstants;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.base.controller.JaWEType;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Lane;
import org.enhydra.jxpdl.elements.NodeGraphicsInfo;
import org.enhydra.jxpdl.elements.Participant;
import org.enhydra.jxpdl.elements.Pool;
import org.enhydra.jxpdl.elements.WorkflowProcess;
import org.jgraph.graph.GraphConstants;

/**
 * Class used to render graph swimlane object.
 */
public class DefaultGraphSwimlaneRenderer extends MultiLinedRenderer implements
                                                                    GraphSwimlaneRendererInterface {

   public void paint(Graphics g) {
      boolean rotateParticipant = GraphUtilities.getGraphOrientation(((Graph) graph).getXPDLObject())
         .equals(XPDLConstants.POOL_ORIENTATION_VERTICAL);

      int participantNameWidth = GraphUtilities.getGraphController()
         .getGraphSettings()
         .getLaneNameWidth();

      String label = graph.convertValueToString(view.getCell());
      Graphics2D g2 = (Graphics2D) g;
      Dimension d = view.getBounds().getBounds().getSize();// HM, JGraph3.4.1
      g2.setStroke(GraphSettings.DEPARTMENT_STROKE);

      Color pc = getFillColor();
      bordercolor = getBorderColor();

      g.setColor(bordercolor);

      // paint bounds
      g.drawRect(0, 0, d.width - 1, d.height - 1);

      // width of name part
      int r = pc.getRed();
      int gr = pc.getGreen();
      int b = pc.getBlue();
      Color c1 = new Color(r, gr, b, 255);
      Color c2 = new Color(r, gr, b, 0);
      Color c3 = new Color(r, gr, b, 32);

      if (rotateParticipant) {
         GradientPaint gp = new GradientPaint(0,
                                              0,
                                              c1,
                                              d.width - 3,
                                              participantNameWidth - 2,
                                              c2);
         g2.setPaint(gp);
         g.fillRect(1, 1, d.width - 3, participantNameWidth - 2);
         gp = new GradientPaint(1,
                                participantNameWidth,
                                c3,
                                d.width - 3,
                                d.height - 2,
                                c2);
         g2.setPaint(gp);
         g.fillRect(1, participantNameWidth, d.width - 3, d.height - 2);
      } else {
         GradientPaint gp = new GradientPaint(1,
                                              1,
                                              c1,
                                              participantNameWidth - 2,
                                              d.height - 3,
                                              c2);
         g2.setPaint(gp);
         g.fillRect(1, 1, participantNameWidth - 2, d.height - 3);
         // g.setColor(c2);
         gp = new GradientPaint(participantNameWidth,
                                1,
                                c3,
                                d.width - 2,
                                d.height - 3,
                                c2);
         g2.setPaint(gp);
         g.fillRect(participantNameWidth, 1, d.width - 2, d.height - 3);
      }
      g.setColor(bordercolor);

      if (rotateParticipant) {
         g.drawLine(0, participantNameWidth, d.width - 1, participantNameWidth);
      } else {
         g.drawLine(participantNameWidth, 0, participantNameWidth, d.height - 1);
      }
      // this section paints participant in a case when object is selected
      // NOTE: hasFocus condition is removed because participant has focus
      // when any of it's children has
      if (selected || hasFocus) {
         g2.setStroke(GraphConstants.SELECTION_STROKE);
         // if (hasFocus) g.setColor(graph.getGridColor());
         // else if (selected) g.setColor(graph.getHighlightColor());
         g.setColor(graph.getHighlightColor());
         g.drawRect(0, 0, d.width - 1, d.height - 1);

         if (rotateParticipant) {
            g.drawLine(0, participantNameWidth, d.width - 1, participantNameWidth);
         } else {
            g.drawLine(participantNameWidth, 0, participantNameWidth, d.height - 1);
         }
      }

      // Eventually, participants (participants) name comes on. For selected
      // one label is shown in highlight color (previously set), for others
      // label color is same as for border. Translate and rotate calls set
      // the drawing origin and direction, so label is displayed sidewise and
      // vertically centered.
      if (label != null) {
         if (!selected) {
            g2.setStroke(GraphSettings.DEPARTMENT_STROKE);
         }

         Graphics2D pg;
         Font f = getFont();
         int textL = getFontMetrics(f).stringWidth(label);
         if (rotateParticipant) {
            if (textL > d.width - 20)
               textL = d.width - 20;

            textL = (d.width - textL) / 2 - 10;
            pg = (Graphics2D) g.create(1, 1, d.width - 2, participantNameWidth - 3);
            pg.translate(textL, 1);
            super.setBounds(new Rectangle(1, 1, d.width - 5, participantNameWidth + 12));
         } else {
            if (textL > d.height - 20)
               textL = d.height - 20;
            textL = (d.height - textL) / 2 - 10;
            pg = (Graphics2D) g.create(1, 1, participantNameWidth - 2, d.height - 3);
            pg.translate(1, d.height - textL);
            pg.rotate(Math.toRadians(-90));
            super.setBounds(new Rectangle(1, 1, d.height - 5, participantNameWidth + 12));
         }

         super.paint(pg);
      }
   }

   public ImageIcon getIcon() {
      GraphSwimlaneInterface p = (GraphSwimlaneInterface) view.getCell();
      if (p.getPropertyObject() instanceof Pool) {
         return ((JaWEType) JaWEManager.getInstance()
            .getJaWEController()
            .getJaWETypes()
            .getTypes(WorkflowProcess.class, null)
            .get(0)).getIcon();
      }
      Object par = getParticipantType(p.getPropertyObject());
      if (par instanceof FreeTextExpressionParticipant) {
         return GraphUtilities.getGraphController()
            .getGraphSettings()
            .getFreeTextLaneIcon();
      } else if (par instanceof Lane) {
         return GraphUtilities.getGraphController()
            .getGraphSettings()
            .getCommonExpresionLaneIcon();
      } else {
         return JaWEManager.getInstance()
            .getJaWEController()
            .getTypeResolver()
            .getJaWEType((Participant) par)
            .getIcon();
      }
   }

   /**
    * @return The border color for swimlane object.
    */
   public Color getBorderColor() {
      GraphSwimlaneInterface p = (GraphSwimlaneInterface) view.getCell();
      NodeGraphicsInfo ngi = JaWEManager.getInstance()
         .getXPDLUtils()
         .getNodeGraphicsInfo((XMLCollectionElement) p.getPropertyObject());
      Color bc = null;
      if (ngi != null) {
         bc = Utils.getColor(ngi.getBorderColor());
      }
      if (bc == null) {
         bc = GraphUtilities.getGraphController().getGraphSettings().getLaneBorderColor();
      }
      return bc;
   }

   /**
    * @return The fill color for swimlane object.
    */
   public Color getFillColor() {
      GraphSwimlaneInterface p = (GraphSwimlaneInterface) view.getCell();
      NodeGraphicsInfo ngi = JaWEManager.getInstance()
         .getXPDLUtils()
         .getNodeGraphicsInfo((XMLCollectionElement) p.getPropertyObject());
      Color pc = null;
      if (ngi != null) {
         pc = Utils.getColor(ngi.getFillColor());
      }
      if (pc == null) {
         Object par = getParticipantType(p.getPropertyObject());
         if (par instanceof FreeTextExpressionParticipant) {
            pc = GraphUtilities.getGraphController()
               .getGraphSettings()
               .getLaneFreeTextExpressionColor();
         } else if (par instanceof Lane) {
            pc = GraphUtilities.getGraphController()
               .getGraphSettings()
               .getLaneCommonExpressionColor();
         } else {
            pc = JaWEManager.getInstance()
               .getJaWEController()
               .getTypeResolver()
               .getJaWEType((Participant) par)
               .getColor();
         }
      }
      return pc;
   }

   /**
    * Searches for the Participant object represented by the the given Lane/Pool.
    * 
    * @param po {@link Pool} or {@link Lane} instance.
    * @return The {@link Participant} representing given {@link Pool} or {@link Lane}, or
    *         if lane does not represent any participant object, returns the {@link Lane}
    *         object itself.
    */
   protected Object getParticipantType(XMLElement po) {
      Object par = null;
      if (!(po instanceof Participant)) {
         if (po instanceof Lane) {
            Lane l = (Lane) po;
            par = GraphUtilities.getParticipantForLane(l, null);
            if (par == null && l.getPerformers().size() > 0) {
               par = l;
            }
         }
         if (par == null) {
            par = FreeTextExpressionParticipant.getInstance();
         }

      } else {
         par = po;
      }
      return par;
   }
}
