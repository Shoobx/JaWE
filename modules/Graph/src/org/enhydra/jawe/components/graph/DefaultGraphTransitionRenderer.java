package org.enhydra.jawe.components.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.shark.xpdl.elements.Transition;
import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.EdgeRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;

/**
 * Represents a view for a model's Transition object.
 * @author Sasa Bojanic
 */
public class DefaultGraphTransitionRenderer extends EdgeRenderer implements GraphTransitionRendererInterface {


   public DefaultGraphTransitionRenderer () {
      super();
   }

   public void paint(Graphics g) {
      GraphTransitionInterface tr=(GraphTransitionInterface)view.getCell();
      if (tr.hasCondition()) {
         lineWidth=3;
      } else {
         lineWidth=1;
      }  
      
      Transition t = (Transition) tr.getUserObject();
      Color clr = GraphUtilities.getGraphController().getGraphSettings().getBubbleConectionColor(); 
      if (t != null)
         clr = JaWEManager.getInstance().getJaWEController().getTypeResolver().getJaWEType(t).getColor();

      Shape edgeShape = view.getShape();
      // Sideeffect: beginShape, lineShape, endShape
      if (edgeShape != null) {
         Graphics2D g2 = (Graphics2D) g;
         int c = BasicStroke.CAP_BUTT;
         int j = BasicStroke.JOIN_MITER;
         g2.setStroke(new BasicStroke(lineWidth, c, j));
         translateGraphics(g);
         //g.setColor(getForeground());
         g.setColor(clr);
         if (view.beginShape != null) {
            if (beginFill)
               g2.fill(view.beginShape);
            g2.draw(view.beginShape);
         }
         if (view.endShape != null) {
            if (endFill)
               g2.fill(view.endShape);
            g2.draw(view.endShape);
         }
         if (lineDash != null) // Dash For Line Only
            g2.setStroke(
               new BasicStroke(lineWidth, c, j, 10.0f, lineDash, 0.0f));
         if (view.lineShape != null)
            g2.draw(view.lineShape);

         if (selected) { // Paint Selected
            g2.setStroke(GraphConstants.SELECTION_STROKE);
            g2.setColor(graph.getHighlightColor());
            if (view.beginShape != null)
               g2.draw(view.beginShape);
            if (view.lineShape != null)
               g2.draw(view.lineShape);
            if (view.endShape != null)
               g2.draw(view.endShape);
         }
         if (graph.getEditingCell() != view.getCell() && GraphUtilities.getGraphController().getGraphSettings().shouldShowTransitionCondition()) {
            Object label = graph.convertValueToString(view);
            if (label != null) {
               g2.setStroke(new BasicStroke(1));
               g.setFont(getFont());
					paintLabel(g, label.toString(), getLabelPosition(view),true);
            }
         }
      }
   }
   
   public boolean intersects(JGraph pGraph, CellView value, Rectangle rect) {
      if (value instanceof EdgeView && pGraph != null && value != null) {
         setView(value);

         // If we have two control points, we can get rid of hit
         // detection on do an intersection test on the two diagonals
         // of rect and the line between the two points
         EdgeView edgeView = (EdgeView) value;
         if (edgeView.getPointCount() == 2) {
            Point2D p0 = edgeView.getPoint(0);
            Point2D p1 = edgeView.getPoint(1);
            if (rect.intersectsLine(p0.getX(), p0.getY(), p1.getX(), p1
                  .getY()))
               return true;
         } else {
            Graphics2D g2 = (Graphics2D) pGraph.getGraphics();
            if (g2.hit(rect, view.getShape(), true))
               return true;
         }
      }
      return false;
   }

   void setView(CellView value) {
      if (value instanceof EdgeView) {
         view = (EdgeView) value;
         installAttributes(view);
      } else {
         view = null;
      }
   }
   
}

/* End of TransitionRenderer.java */
