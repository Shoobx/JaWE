package org.enhydra.jawe.components.graph;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import org.enhydra.shark.xpdl.elements.Activity;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.VertexView;

/**
 * Represents a view for a model's Activity object.
 *
 * @author Sasa Bojanic
 */
public class DefaultGraphActivityView extends VertexView implements GraphActivityViewInterface {

   protected static Map renderers = new HashMap();

   /**
   * Constructs a activity view for the specified model object.
   *
   * @param cell reference to the model object
   */
   public DefaultGraphActivityView(Object cell) {
      super(cell);
   }

   /**
   * Returns a renderer for the class.
   */
   public CellViewRenderer getRenderer() {      
      String type=((GraphActivityInterface)super.getCell()).getType();
      GraphActivityRendererInterface garenderer=(GraphActivityRendererInterface)renderers.get(type);
      if (garenderer==null) {
         garenderer=createRenderer((Activity)((GraphActivityInterface)super.getCell()).getUserObject());
         renderers.put(type,garenderer);
      }
      return garenderer;
   }

   /**
   * Returns the bounding rectangle for this view.
   */
   public Rectangle2D getBounds() {//HM, JGraph3.4.1
      return bounds;
   }

   protected GraphActivityRendererInterface createRenderer (Activity act) {
      return GraphUtilities.getGraphController().getGraphObjectRendererFactory().createActivityRenderer(act);
   }
 
	public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
		return ((MultiLinedRenderer)getRenderer()).getPerimeterPoint(this, p);
	}
}
