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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.NodeGraphicsInfo;
import org.jgraph.graph.AbstractCellView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexView;

/**
 * Class used to display activity object.
 * 
 * @author Sasa Bojanic
 * @author Miroslav Popov
 */
public class DefaultGraphActivityRenderer extends MultiLinedRenderer implements
                                                                    GraphActivityRendererInterface {

   protected static int arc = 15;

   /**
    * Paints activity. Overrides super class paint to add specific painting. First it
    * fills inner with color. Then it adds specific drawing for join type. Then it apply
    * JPanel with name and icon. At the end it draws border
    */
   public void paint(Graphics g) {
      GraphActivityInterface gact = (GraphActivityInterface) view.getCell();
      Activity act = (Activity) gact.getUserObject();
      int type = act.getActivityType();
      if (type == XPDLConstants.ACTIVITY_TYPE_ROUTE) {
         paintRoot(act, g);
         return;
      }
      int actW = GraphUtilities.getGraphController()
         .getGraphSettings()
         .getActivityWidth();
      int actH = GraphUtilities.getGraphController()
         .getGraphSettings()
         .getActivityHeight();

      NodeGraphicsInfo ngi = JaWEManager.getInstance()
         .getXPDLUtils()
         .getNodeGraphicsInfo(act);
      Color bckgC = null;
      if (ngi != null) {
         if (ngi.getWidth() != 0) {
            actW = ngi.getWidth();
         }
         if (ngi.getHeight() != 0) {
            actH = ngi.getHeight();
         }
         bckgC = Utils.getColor(ngi.getFillColor());
         bordercolor = Utils.getColor(ngi.getBorderColor());
      }

      Graphics2D g2 = (Graphics2D) g;
      if (bckgC == null) {
         bckgC = JaWEManager.getInstance()
            .getJaWEController()
            .getTypeResolver()
            .getJaWEType(act)
            .getColor();
      }
      if (bordercolor == null) {
         bordercolor = Color.BLACK;
      }
      if (selected)
         bckgC = GraphUtilities.getGraphController()
            .getGraphSettings()
            .getSelectedActivityColor();

      // fill activity
      int r = bckgC.getRed();
      int gr = bckgC.getGreen();
      int b = bckgC.getBlue();
      Color c1 = new Color(r, gr, b, 255);
      Color c2 = new Color(r, gr, b, 0);
      GradientPaint gp = new GradientPaint(0, 0, c1, actW, actH, c2);
      g2.setPaint(gp);
      g.fillRoundRect(0, 0, actW - 1, actH - 1, arc, arc);

      // drawing panel
      super.setOpaque(false);
      Graphics gl = g.create(1, 1, actW - 2, actH - 2);
      graph.setHighlightColor(bckgC);
      super.paint(gl);
      setForeground(bordercolor);

      // draw border
      g.setColor(bordercolor);
      g2.setStroke(borderStroke);
      g.drawRoundRect(0, 0, actW - 1, actH - 1, arc, arc);

      //
      if (type == XPDLConstants.ACTIVITY_TYPE_BLOCK
          || type == XPDLConstants.ACTIVITY_TYPE_SUBFLOW) {
         g2.setStroke(new BasicStroke(2));
         g.setColor(Color.BLACK);
         g.drawRoundRect(actW / 2 - 7, actH - 16, 15, 15, 3, 3);
         g.drawLine(actW / 2, actH - 11, actW / 2, actH - 5);
         g.drawLine(actW / 2 - 3, actH - 8, actW / 2 + 3, actH - 8);
      }

   }

   protected void paintRoot(Activity act, Graphics g) {
      Rectangle ra = getBounds();
      Rectangle rg = ((GraphActivityViewInterface) view).getOriginalBounds();
      Graphics gl = g.create((int) (rg.getX() - ra.getX()),
                             (int) (rg.getY() - ra.getY()),
                             (int) rg.getWidth(),
                             (int) rg.getHeight());
      Graphics2D g2 = (Graphics2D) gl;

      Dimension d = rg.getSize();

      int actW = d.width;
      int actH = d.height;

      NodeGraphicsInfo ngi = JaWEManager.getInstance()
         .getXPDLUtils()
         .getNodeGraphicsInfo(act);
      Color bckgC = null;
      if (ngi != null) {
         if (ngi.getWidth() != 0) {
            actW = ngi.getWidth();
         }
         if (ngi.getHeight() != 0) {
            actH = ngi.getHeight();
         }
         bckgC = Utils.getColor(ngi.getFillColor());
         bordercolor = Utils.getColor(ngi.getBorderColor());
      }
      String gt = act.getActivityTypes().getRoute().getGatewayType();

      if (bckgC == null) {
         bckgC = JaWEManager.getInstance()
            .getJaWEController()
            .getTypeResolver()
            .getJaWEType(act)
            .getColor();
      }
      if (bordercolor == null) {
         bordercolor = Color.BLACK;
      }
      if (selected)
         bckgC = GraphUtilities.getGraphController()
            .getGraphSettings()
            .getSelectedActivityColor();

      // fill activity
      int[] x = new int[] {
            0, actW / 2, actW - 1, actW / 2, 0
      };
      int[] y = new int[] {
            actH / 2, 0, actH / 2, actH - 1, actH / 2
      };
      int r = bckgC.getRed();
      int gr = bckgC.getGreen();
      int b = bckgC.getBlue();
      Color c1 = new Color(r, gr, b, 255);
      Color c2 = new Color(r, gr, b, 0);
      GradientPaint gp = new GradientPaint(0, 0, c1, actW - 1, actH - 1, c2);
      g2.setPaint(gp);
      g2.fillPolygon(x, y, 5);

      // drawing panel
      super.setOpaque(false);
      setBorder(BorderFactory.createLineBorder(bordercolor, 0));
      setForeground(bordercolor);

      // draw border
      g2.setColor(bordercolor);
      g2.setStroke(borderStroke);
      g2.drawPolygon(x, y, 5);

      g2.setColor(Color.BLACK);
      if (gt.equals(XPDLConstants.JOIN_SPLIT_TYPE_PARALLEL)) {
         drawLine(g2, actW / 2, actH / 4 + 2, actW / 2, 3 * actH / 4 - 2, 3, true);
         drawLine(g2, actW / 4 + 2, actH / 2, 3 * actW / 4 - 2, actH / 2, 3, false);
      } else if (gt.equals(XPDLConstants.JOIN_SPLIT_TYPE_INCLUSIVE)) {
         drawCircle(g2, actW / 2, actH / 2, actW / 4 - 1, 3);
      } else {
         drawLine(g2,
                  actW / 4 + 5,
                  actH / 4 + 3,
                  3 * actW / 4 - 5,
                  3 * actH / 4 - 3,
                  3,
                  true);
         drawLine(g2,
                  actW / 4 + 5,
                  3 * actH / 4 - 3,
                  3 * actW / 4 - 5,
                  actH / 4 + 3,
                  3,
                  true);
      }
      String label = act.getName();
      if (!label.trim().equals("")) {
         paintLabel(g, label, d.height);
      }
   }

   public ImageIcon getIcon() {
      Activity act = (Activity) ((GraphActivityInterface) view.getCell()).getUserObject();
      if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_ROUTE) {
         return null;
      }
      String icon = act.getIcon();
      if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_NO && icon.equals("")) {
         return null;
      }

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

   protected void paintLabel(Graphics g, String label, int actH) {
      // Rectangle rb = ((GraphArtifactViewInterface)view).getOriginalBounds();
      // view.setBounds(new Rectangle((int)rb.getX()-50, (int)rb.getY(),
      // (int)rb.getWidth()+100, (int)rb.getHeight()+50));
      // Dimension d = getSize();
      // setSize((int)rb.getWidth()+100, (int)rb.getHeight()+50);
      Graphics2D g2 = (Graphics2D) g;
      g2.setStroke(new BasicStroke(1));
      g.setFont(getFont());
      if (label != null && label.length() > 0) {
         Dimension d = getLabelDimension((GraphActivityViewInterface) view);
         int w = (int) g.getClipBounds().getWidth();
         Font f = getFont();
         FontMetrics metrics = getFontMetrics(f);
         int sh = metrics.getHeight();
         g.setFont(f);
         g.setColor(getBackground());
         g.setColor(Color.BLACK);
         g.drawString(label, d.width < w ? (w - d.width) / 2 : 0, actH + sh - 3);
      }
   }

   public Dimension getLabelDimension(GraphActivityViewInterface view) {
      GraphActivityInterface gact = (GraphActivityInterface) view.getCell();
      Activity act = (Activity) gact.getUserObject();
      if ((act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_ROUTE
           || act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_EVENT_END || act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_EVENT_START)
          && !act.getName().trim().equals("")) {
         String label = act.getName();
         Font f = GraphConstants.getFont(view.getAllAttributes());
         FontMetrics metrics = getFontMetrics(f);
         int sw = metrics.stringWidth(label);
         int sh = metrics.getHeight();
         return new Dimension(sw, sh);
      }
      return new Dimension(0, 0);
   }

   public Point2D getPerimeterPoint(VertexView pView, Point2D p) {
      GraphActivityInterface gact = (GraphActivityInterface) pView.getCell();
      Activity act = (Activity) gact.getUserObject();
      int type = act.getActivityType();
      Point2D ret = null;
      if (type != XPDLConstants.ACTIVITY_TYPE_ROUTE) {
         ret = super.getPerimeterPoint(pView, p);
      } else {
         ret = _getPerimeterPoint(pView, p);
      }
      return ret;

   }

   public Point2D _getPerimeterPoint(VertexView view, Point2D p) {
      GraphActivityViewInterface v = (GraphActivityViewInterface) view;
      Point2D center = AbstractCellView.getCenterPoint(view);
      double halfwidth = v.getOriginalBounds().getWidth() / 2;
      double halfheight = v.getOriginalBounds().getHeight() / 2;
      Point2D top = new Point2D.Double(center.getX(), center.getY() - halfheight);
      Point2D bottom = new Point2D.Double(center.getX(), center.getY() + halfheight);
      Point2D left = new Point2D.Double(center.getX() - halfwidth, center.getY());
      Point2D right = new Point2D.Double(center.getX() + halfwidth, center.getY());
      // Special case for intersecting the diamond's points
      if (center.getX() == p.getX()) {
         if (center.getY() > p.getY()) // top point
            return (top);
         return bottom;
      }
      if (center.getY() == p.getY()) {
         if (center.getX() > p.getX()) // left point
            return (left);
         // right point
         return right;
      }
      // In which quadrant will the intersection be?
      // set the slope and offset of the border line accordingly
      Point2D i;
      if (p.getX() < center.getX())
         if (p.getY() < center.getY())
            i = intersection(view, p, center, top, left);
         else
            i = intersection(view, p, center, bottom, left);
      else if (p.getY() < center.getY())
         i = intersection(view, p, center, top, right);
      else
         i = intersection(view, p, center, bottom, right);
      System.out.println("PP="+i);
      return i;
   }

   /**
    * Find the point of intersection of two straight lines (which follow the equation
    * y=mx+b) one line is an incoming edge and the other is one side of the diamond.
    */
   private Point2D intersection(VertexView view,
                                Point2D lineOneStart,
                                Point2D lineOneEnd,
                                Point2D lineTwoStart,
                                Point2D lineTwoEnd) {
      // m = delta y / delta x, the slope of a line
      // b = y - mx, the axis intercept
      double m1 = (lineOneEnd.getY() - lineOneStart.getY())
                  / (lineOneEnd.getX() - lineOneStart.getX());
      double b1 = lineOneStart.getY() - m1 * lineOneStart.getX();
      double m2 = (lineTwoEnd.getY() - lineTwoStart.getY())
                  / (lineTwoEnd.getX() - lineTwoStart.getX());
      double b2 = lineTwoStart.getY() - m2 * lineTwoStart.getX();
      double xinter = (b1 - b2) / (m2 - m1);
      double yinter = m1 * xinter + b1;
      Point2D intersection = view.getAttributes().createPoint(xinter, yinter);
      return intersection;
   }

   protected static void drawCircle(Graphics g, int x, int y, int r, int lineWidth) {
      for (int i = 0; i < lineWidth; i++) {
         g.drawOval(x - (r - i), y - (r - i), 2 * (r - i), 2 * (r - i));
      }
   }

   protected static void drawLine(Graphics g,
                                  int x1,
                                  int y1,
                                  int x2,
                                  int y2,
                                  int lineWidth,
                                  boolean xhandle) {
      g.drawLine(x1, y1, x2, y2);
      int dx = 0, dy = 0;
      for (int i = 1; i < lineWidth; i++) {
         if (xhandle) {
            dx = i;
         } else {
            dy = i;
         }
         g.drawLine(x1 - dx, y1 - dy, x2 - dx, y2 - dy);
         g.drawLine(x1 + dx, y1 + dy, x2 + dx, y2 + dy);
      }
   }

}
