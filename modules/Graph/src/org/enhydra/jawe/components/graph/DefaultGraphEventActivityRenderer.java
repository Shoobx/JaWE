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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.NodeGraphicsInfo;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

/**
 * Class used to display end object.
 */
public class DefaultGraphEventActivityRenderer extends VertexRenderer implements
                                                                     GraphActivityRendererInterface {

   /**
    * Paints End. Overrides super class paint to add specific painting.
    */
   public void paint(Graphics g) {
      GraphActivityInterface gact = (GraphActivityInterface) view.getCell();
      Activity act = (Activity) gact.getUserObject();
      int lineWidth = 1;
      if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_EVENT_END) {
         lineWidth = 3;
      }
      Rectangle ra = getBounds();
      Rectangle rg = ((GraphActivityViewInterface) view).getOriginalBounds();
      Graphics gl = g.create((int) (rg.getX() - ra.getX()),
                             (int) (rg.getY() - ra.getY()),
                             (int) rg.getWidth(),
                             (int) rg.getHeight());
      Graphics2D g2 = (Graphics2D) gl;

      Color c = getFillColor();
      bordercolor = getBorderColor();
      
      if (selected) {
         c = GraphUtilities.getGraphController()
            .getGraphSettings()
            .getSelectedActivityColor();
      }
      setText(null);

      Object AntiAlias = RenderingHints.VALUE_ANTIALIAS_ON;// Harald Meister
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, AntiAlias);// Harald Meister
      Dimension d = rg.getSize();

      if (super.isOpaque()) {
         int r = c.getRed();
         int gr = c.getGreen();
         int b = c.getBlue();
         Color c1 = new Color(r, gr, b, 255);
         Color c2 = new Color(r, gr, b, 0);
         GradientPaint gp = new GradientPaint(0, 0, c1, d.width - 1, d.height - 1, c2);
         g2.setPaint(gp);
         g2.fillOval(1, 1, d.width - 2, d.height - 2);
      }
      g2.setColor(bordercolor);
      drawOwal(g2, d.width, d.height, lineWidth);

      String label = act.getName();
      if (!label.trim().equals("")) {
         paintLabel(g, label, d.height);
      }
   }

   public Color getBorderColor() {
      GraphActivityInterface gact = (GraphActivityInterface) view.getCell();
      Activity act = (Activity) gact.getUserObject();
      NodeGraphicsInfo ngi = JaWEManager.getInstance()
         .getXPDLUtils()
         .getNodeGraphicsInfo(act);
      Color bc = null;
      if (ngi != null) {
         bc = Utils.getColor(ngi.getBorderColor());
      }
      if (bc == null) {
         bc = Color.BLACK;
      }
      return bc;
   }

   public Color getFillColor() {
      GraphActivityInterface gact = (GraphActivityInterface) view.getCell();
      Activity act = (Activity) gact.getUserObject();
      NodeGraphicsInfo ngi = JaWEManager.getInstance()
         .getXPDLUtils()
         .getNodeGraphicsInfo(act);
      Color fc = null;
      if (ngi != null) {
         fc = Utils.getColor(ngi.getFillColor());
      }

      if (fc == null) {
         fc = GraphUtilities.getGraphController().getGraphSettings().getStartEventColor();
         if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_EVENT_END) {
            fc = GraphUtilities.getGraphController().getGraphSettings().getEndEventColor();
         }
      }

      return fc;
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
         Font f = getFont();
         FontMetrics metrics = getFontMetrics(f);
         int sw = metrics.stringWidth(label);
         int sh = metrics.getHeight();
         return new Dimension(sw, sh);
      }
      return new Dimension(0, 0);
   }

   public Point2D getPerimeterPoint(VertexView pView, Point2D p) {
      Rectangle2D r = pView.getBounds();

      double x = r.getX();
      double y = r.getY();
      double a = (r.getWidth() + 1) / 2;
      double b = (r.getHeight() + 1) / 2;

      // x0,y0 - center of ellipse
      double x0 = x + a;
      double y0 = y + b;

      // x1, y1 - point
      double x1 = p.getX();
      double y1 = p.getY();

      // Calculates straight line equation through point and ellipse center
      // y = d * x + h
      double dx = x1 - x0;
      double dy = y1 - y0;

      if (dx == 0)
         return new Point((int) x0, (int) (y0 + b * dy / Math.abs(dy)));

      double d = dy / dx;
      double h = y0 - d * x0;

      // Calculates intersection
      double e = a * a * d * d + b * b;
      double f = -2 * x0 * e;
      double g = a * a * d * d * x0 * x0 + b * b * x0 * x0 - a * a * b * b;

      double det = Math.sqrt(f * f - 4 * e * g);

      // Two solutions (perimeter points)
      double xout1 = (-f + det) / (2 * e);
      double xout2 = (-f - det) / (2 * e);
      double yout1 = d * xout1 + h;
      double yout2 = d * xout2 + h;

      double dist1 = Math.sqrt(Math.pow((xout1 - x1), 2) + Math.pow((yout1 - y1), 2));
      double dist2 = Math.sqrt(Math.pow((xout2 - x1), 2) + Math.pow((yout2 - y1), 2));

      // Correct solution
      double xout, yout;

      if (dist1 < dist2) {
         xout = xout1;
         yout = yout1;
      } else {
         xout = xout2;
         yout = yout2;
      }

      return new Point2D.Double(xout, yout);
   }

   protected static void drawOwal(Graphics g, int w, int h, int lineWidth) {
      int x = w / 2;
      int y = h / 2;
      int rx = w / 2;
      int ry = h / 2;
//      if (2*rx==w) {
         rx--;
//      }
//      if (2*ry==h) {
         ry--;
//      }

      for (int i = 0; i < lineWidth; i++) {
         g.drawOval(x - (rx - i), y - (ry - i), 2 * (rx - i), 2 * (ry - i));
      }
   }

}
