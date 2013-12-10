package org.enhydra.jawe.components.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexView;

/**
 * @author Sasa Bojanic
 * @author Miroslav Popov
 */
public class MultiLinedRenderer extends DefaultCellPanel implements CellViewRenderer {

   public static boolean loaded = false;

   private static float borderWidth = 1f;
   protected BasicStroke borderStroke = new BasicStroke(borderWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);

   /** Cache the current graph for drawing. */
   transient protected JGraph graph;

   /** Cache the current shape for drawing. */
   transient protected VertexView view;

   /** Cached hasFocus and selected value. */
   transient protected boolean hasFocus, selected, preview, opaque;

   /** Cached default foreground and default background. */
   transient protected Color defaultForeground, defaultBackground, bordercolor;

   /** Cached value of the double buffered state */
   transient boolean isDoubleBuffered = false;

   public MultiLinedRenderer() {
      defaultForeground = UIManager.getColor("Tree.textForeground");
      defaultBackground = UIManager.getColor("Tree.textBackground");
   }

   public Component getRendererComponent(JGraph pGgraph, CellView pView, boolean pSel, boolean pFocus, boolean pPreview) {
      this.graph = pGgraph;
      wrapName(GraphUtilities.getGraphController().getGraphSettings().isNameWrappingEnabled());
      wrapStyle(GraphUtilities.getGraphController().getGraphSettings().isWordWrappingEnabled());

      isDoubleBuffered = graph.isDoubleBuffered();
      if (pView instanceof VertexView) {
         this.view = (VertexView) pView;
         setMainIcon(getIcon());
         setTextPosition(GraphUtilities.getGraphController().getGraphSettings().getTextPos());
         showIcon(GraphUtilities.getGraphController().getGraphSettings().shouldShowIcons());
         
         setDisplayName(view.getCell().toString());

         if (graph.getEditingCell() != view.getCell()) {
            Object label = graph.convertValueToString(view);
            if (label != null)
               setDisplayName(label.toString());
            else
               setDisplayName(null);
         } else
            setDisplayName(null);
         this.hasFocus = pFocus;
         this.selected = pSel;
         this.preview = pPreview;
         Map attributes = view.getAllAttributes();
         installAttributes(graph, attributes);
         return this;
      }

      return null;
   }

   protected void installAttributes(JGraph pGraph, Map pAttributes) {
      setOpaque(GraphConstants.isOpaque(pAttributes));
      Color foreground = GraphConstants.getForeground(pAttributes);
      setForeground((foreground != null) ? foreground : pGraph.getForeground());
      Color background = GraphConstants.getBackground(pAttributes);
      setBackground((background != null) ? background : pGraph.getBackground());
      Font font = GraphConstants.getFont(pAttributes);
      setFont((font != null) ? font : pGraph.getFont());
      Border border = GraphConstants.getBorder(pAttributes);
      bordercolor = GraphConstants.getBorderColor(pAttributes);
      if (border != null)
         setBorder(border);
      else if (bordercolor != null) {
         borderWidth = Math.max(1, Math.round(GraphConstants.getLineWidth(pAttributes)));
      }
   }

   public ImageIcon getIcon() {
      return GraphUtilities.getGraphController().getGraphSettings().getDefaultActivityIcon();
   }

   public Point2D getPerimeterPoint(VertexView pView, Point2D p) {
      Rectangle2D bounds = pView.getBounds();
      double x = bounds.getX();
      double y = bounds.getY();
      double width = bounds.getWidth();
      double height = bounds.getHeight();
      double xCenter = x + width / 2;
      double yCenter = y + height / 2;
      double dx = p.getX() - xCenter; // Compute Angle
      double dy = p.getY() - yCenter;
      double alpha = Math.atan2(dy, dx);
      double xout = 0, yout = 0;
      double pi = Math.PI;
      double pi2 = Math.PI / 2.0;
      double beta = pi2 - alpha;
      double t = Math.atan2(height, width);
      if (alpha < -pi + t || alpha > pi - t) { // Left edge
         xout = x;
         yout = yCenter - width * Math.tan(alpha) / 2;
      } else if (alpha < -t) { // Top Edge
         yout = y;
         xout = xCenter - height * Math.tan(beta) / 2;
      } else if (alpha < t) { // Right Edge
         xout = x + width;
         yout = yCenter + width * Math.tan(alpha) / 2;
      } else { // Bottom Edge
         yout = y + height;
         xout = xCenter + height * Math.tan(beta) / 2;
      }
      return new Point2D.Double(xout, yout);
   }
}