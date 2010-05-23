package org.enhydra.jawe.components.graph;

import java.awt.geom.Point2D;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeView;

/**
 * Represents a view for a model's End object.
 * @author Sasa Bojanic
 */
public class DefaultGraphBubbleActivityView extends DefaultGraphActivityView {

   /**
   * Constructs a End view for the specified model object.
   *
   * @param cell reference to the model object
   */
   public DefaultGraphBubbleActivityView(Object cell) {
      super(cell);
   }

   public CellViewRenderer getRenderer() {
      String type=((GraphActivityInterface)super.getCell()).getType();
      GraphActivityRendererInterface garenderer=(GraphActivityRendererInterface)renderers.get(type);
      if (garenderer==null) {
         garenderer=createRenderer(type);
         renderers.put(type,garenderer);
      }
      return garenderer;
   }

   protected GraphActivityRendererInterface createRenderer (String type) {
      return GraphUtilities.getGraphController().getGraphObjectRendererFactory().createBubbleRenderer(type);
   }
   
   public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
      return ((DefaultGraphBubbleActivityRenderer)getRenderer()).getPerimeterPoint(this, p);
   }
}
