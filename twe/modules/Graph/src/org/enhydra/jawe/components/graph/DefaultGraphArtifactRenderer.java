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

import javax.swing.ImageIcon;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Artifact;
import org.enhydra.jxpdl.elements.NodeGraphicsInfo;
import org.jgraph.graph.GraphConstants;

/**
 * Class used to render artifact object.
 */
public class DefaultGraphArtifactRenderer extends MultiLinedRenderer implements
                                                                    GraphArtifactRendererInterface {

   /** The size of arc. */
   protected static int arc = 8;

   public void paint(Graphics g) {
      GraphArtifactInterface gact = (GraphArtifactInterface) view.getCell();
      Artifact art = (Artifact) gact.getUserObject();
      String type = art.getArtifactType();
      Dimension d = ((GraphArtifactViewInterface) view).getOriginalBounds().getSize();
      int actW = d.width;
      int actH = d.height;
      Color c = getFillColor();
      bordercolor = getBorderColor();
      if (selected)
         c = GraphUtilities.getGraphController()
            .getGraphSettings()
            .getSelectedActivityColor();

      int r = c.getRed();
      int gr = c.getGreen();
      int b = c.getBlue();
      Color c1 = new Color(r, gr, b, 255);
      Color c2 = new Color(r, gr, b, 0);
      GradientPaint gp = new GradientPaint(0, 0, c1, actW, actH, c2);
      if (type.equals(XPDLConstants.ARTIFACT_TYPE_DATAOBJECT)) {
         Rectangle ra = getBounds();
         Rectangle rg = ((GraphArtifactViewInterface) view).getOriginalBounds();
         Graphics gl = g.create((int) (rg.getX() - ra.getX()),
                                (int) (rg.getY() - ra.getY()),
                                (int) rg.getWidth(),
                                (int) rg.getHeight());
         Graphics2D g2 = (Graphics2D) gl;
         Object AntiAlias = RenderingHints.VALUE_ANTIALIAS_ON;// Harald Meister
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, AntiAlias);// Harald Meister

         int[] x = new int[] {
               0, actW - arc, actW - 1, actW - 1, 0, 0
         };
         int[] y = new int[] {
               0, 0, arc, actH - 1, actH - 1, 0
         };
         int[] x2 = new int[] {
               actW - arc, actW - 1, actW - arc, actW - arc
         };
         int[] y2 = new int[] {
               0, arc, arc, 0
         };

         if (super.isOpaque()) {
            g2.setPaint(gp);
            g2.fillPolygon(x, y, 6);
            gp = new GradientPaint(0, 0, Color.WHITE, actW, actH, Color.WHITE);
            g2.setPaint(gp);
            g2.fillPolygon(x2, y2, 4);
         }
         g2.setColor(bordercolor);
         g2.drawPolygon(x, y, 6);
         g2.drawPolygon(x2, y2, 4);
         String label = gact.toString();
         paintLabel(g, label, new Point(0, rg.height));
      } else {
         // drawing panel
         super.setOpaque(false);
         Graphics2D gl = (Graphics2D) g.create(1, 1, actW - 2, actH - 2);
         gl.setColor(c1);
         gl.fillRect(0, 0, actW - 2, actH - 2);
         super.paint(gl);

         // draw border
         g.setColor(bordercolor);
         ((Graphics2D) g).setStroke(borderStroke);
         g.drawLine(0, 0, actW / 4, 0);
         g.drawLine(0, 0, 0, actH - 1);
         g.drawLine(0, actH - 1, actW / 4, actH - 1);
         if (selected || hasFocus) {
            ((Graphics2D) g).setStroke(GraphConstants.SELECTION_STROKE);
            // if (hasFocus) g.setColor(graph.getGridColor());
            // else if (selected) g.setColor(graph.getHighlightColor());
            g.setColor(graph.getHighlightColor());
            g.drawLine(0, 0, actW / 4, 0);
            g.drawLine(0, 0, 0, actH - 1);
            g.drawLine(0, actH - 1, actW / 4, actH - 1);
         }
      }

   }

   /**
    * Paints the label below the artifact "box".
    * 
    * @param g Graphics object.
    * @param label Label text to paint.
    * @param middle The point of artifact necessary to calculate label position.
    */
   protected void paintLabel(Graphics g, String label, Point middle) {
      Graphics2D g2 = (Graphics2D) g;
      g2.setStroke(new BasicStroke(1));
      g.setFont(getFont());
      if (label != null && label.length() > 0) {
         Dimension d = getLabelDimension((GraphArtifactViewInterface) view);
         g.setColor(getBackground());
         g.setColor(Color.BLACK);
         g.drawString(label, middle.x, middle.y + d.height);
      }
   }

   public Dimension getLabelDimension(GraphArtifactViewInterface view) {
      GraphArtifactInterface gact = (GraphArtifactInterface) view.getCell();
      String label = gact.toString();
      Font f = GraphConstants.getFont(view.getAllAttributes());
      FontMetrics metrics = getFontMetrics(f);
      int sw = metrics.stringWidth(label);
      int sh = metrics.getHeight();
      return new Dimension(sw, sh);
   }

   public ImageIcon getIcon() {
      return null;
   }

   /**
    * @return The border color for artifact object.
    */
   public Color getBorderColor() {
      GraphArtifactInterface gart = (GraphArtifactInterface) view.getCell();
      Artifact art = (Artifact) gart.getUserObject();
      NodeGraphicsInfo ngi = JaWEManager.getInstance()
         .getXPDLUtils()
         .getNodeGraphicsInfo(art);
      Color bc = null;
      if (ngi != null) {
         bc = Utils.getColor(ngi.getBorderColor());
      }
      if (bc == null) {
         bc = Color.BLACK;
      }
      return bc;
   }

   /**
    * @return The fill color for artifact object.
    */
   public Color getFillColor() {
      GraphArtifactInterface gart = (GraphArtifactInterface) view.getCell();
      Artifact art = (Artifact) gart.getUserObject();
      NodeGraphicsInfo ngi = JaWEManager.getInstance()
         .getXPDLUtils()
         .getNodeGraphicsInfo(art);
      Color fc = null;
      if (ngi != null) {
         fc = Utils.getColor(ngi.getFillColor());
      }

      if (fc == null) {
         fc = JaWEManager.getInstance()
            .getJaWEController()
            .getTypeResolver()
            .getJaWEType(art)
            .getColor();
      }
      return fc;
   }

}
