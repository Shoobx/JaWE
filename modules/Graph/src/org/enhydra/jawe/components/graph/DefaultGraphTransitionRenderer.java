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
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.Association;
import org.enhydra.jxpdl.elements.ConnectorGraphicsInfo;
import org.enhydra.jxpdl.elements.Transition;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.EdgeRenderer;
import org.jgraph.graph.GraphConstants;

/**
 * Represents a renderer for a model's Transition object.
 * 
 * @author Sasa Bojanic
 */
public class DefaultGraphTransitionRenderer extends EdgeRenderer implements
                                                                GraphTransitionRendererInterface {

   /**
    * Creates new renderer.
    */
   public DefaultGraphTransitionRenderer() {
      super();
   }

   public void paint(Graphics g) {
      GraphTransitionInterface tr = (GraphTransitionInterface) view.getCell();
      XMLCollectionElement tOrA = (XMLCollectionElement) tr.getUserObject();
      Color clr = getFillColor();
      AttributeMap am = view.getAttributes();
      am.put(GraphConstants.FOREGROUND, clr);
      am.put(GraphConstants.LABELENABLED, new Boolean(GraphUtilities.getGraphController()
         .getSelectedGraph()
         .shouldShowTransitionConditions()));
      if (tOrA instanceof Transition) {
         Transition t = (Transition) tOrA;
         am.put(GraphConstants.LINEEND, GraphConstants.ARROW_TECHNICAL);
         if (t.getCondition().getType().equals(XPDLConstants.CONDITION_TYPE_OTHERWISE)
             || t.getCondition()
                .getType()
                .equals(XPDLConstants.CONDITION_TYPE_DEFAULTEXCEPTION)) {
            am.put(GraphConstants.LINEBEGIN, GraphConstants.ARROW_LINE);
         }
         if (t.getCondition().getType().equals(XPDLConstants.CONDITION_TYPE_CONDITION) || !t.getCondition().toValue().equals("")) {
            Activity from = XMLUtil.getFromActivity(t);
            if (from != null
                && from.getActivityType() != XPDLConstants.ACTIVITY_TYPE_ROUTE) {
               am.put(GraphConstants.LINEBEGIN, GraphConstants.ARROW_DIAMOND);
               am.put(GraphConstants.BEGINFILL, false);
               am.put(GraphConstants.BEGINSIZE, 11);
            }
         }
      } else {
         am.put(GraphConstants.DASHPATTERN, new float[] {
            2
         });
         Association a = (Association) tOrA;
         GraphCommonInterface s1 = tr.getSourceActivityOrArtifact();

         if (a.getAssociationDirection().equals(XPDLConstants.ASSOCIATION_DIRECTION_NONE)) {
            am.put(GraphConstants.LINEBEGIN, GraphConstants.ARROW_NONE);
            am.put(GraphConstants.LINEEND, GraphConstants.ARROW_NONE);
         } else if (a.getAssociationDirection()
            .equals(XPDLConstants.ASSOCIATION_DIRECTION_BOTH)) {
            am.put(GraphConstants.LINEBEGIN, GraphConstants.ARROW_TECHNICAL);
            am.put(GraphConstants.LINEEND, GraphConstants.ARROW_TECHNICAL);
         } else if (a.getAssociationDirection()
            .equals(XPDLConstants.ASSOCIATION_DIRECTION_FROM)) {
            am.put(GraphConstants.LINEBEGIN,
                   (s1 instanceof GraphArtifactInterface) ? GraphConstants.ARROW_TECHNICAL
                                                         : GraphConstants.ARROW_NONE);
            am.put(GraphConstants.LINEEND,
                   (s1 instanceof GraphArtifactInterface) ? GraphConstants.ARROW_NONE
                                                         : GraphConstants.ARROW_TECHNICAL);
         } else if (a.getAssociationDirection()
            .equals(XPDLConstants.ASSOCIATION_DIRECTION_TO)) {
            am.put(GraphConstants.LINEBEGIN,
                   (s1 instanceof GraphArtifactInterface) ? GraphConstants.ARROW_NONE
                                                         : GraphConstants.ARROW_TECHNICAL);
            am.put(GraphConstants.LINEEND,
                   (s1 instanceof GraphArtifactInterface) ? GraphConstants.ARROW_TECHNICAL
                                                         : GraphConstants.ARROW_NONE);
         }
      }
      // ((JGraph)graph.get()).getGraphLayoutCache().edit(new Object[]{view.getCell()},
      // am);
      view.setAttributes(am);
      if (view instanceof DefaultGraphTransitionView) {
         ((DefaultGraphTransitionView) view).mergeAttributes();
      }
      installAttributes(view);
      createShape();
      super.paint(g);
   }

   /**
    * Overrides default method to draw Otherwise/Default exception transition lines under
    * an angle.
    */
   protected Shape createLineEnd(int size, int style, Point2D src, Point2D dst) {
      if (src == null || dst == null)
         return null;
      if (style == GraphConstants.ARROW_LINE || style == GraphConstants.ARROW_DOUBLELINE) {
         GeneralPath path = new GeneralPath(GeneralPath.WIND_NON_ZERO, 4);

         int n = view.getPointCount();
         if (n <= 1)
            return super.createLineEnd(size, style, src, dst);

         Point2D[] p = null;
         p = new Point2D[n];
         for (int i = 0; i < n; i++) {
            Point2D pt = view.getPoint(i);
            if (pt == null)
               return null; // exit
            p[i] = new Point2D.Double(pt.getX(), pt.getY());
         }

         Point2D p0 = p[0];
         Point2D p1 = p[1];
         Point2D p2 = p[n - 2];
         src = (p2.equals(p0) || GraphConstants.getRouting(view.getAllAttributes()) == GraphConstants.ROUTING_SIMPLE) ? p1
                                                                                                                     : p2;
         dst = p0;
         int n1 = (int) Math.min(7, dst.distance(src));
         if (n1 == 0)
            n1 = 1;
         double alpha = Math.atan((src.getY() - dst.getY()) / (src.getX() - dst.getX()));
         double dx = Math.abs(n1 * Math.cos(alpha));
         double dy = Math.abs(n1 * Math.sin(alpha));

         double xp = dst.getX() + ((dst.getX() > src.getX()) ? -dx : dx);
         double yp = dst.getY() + ((dst.getY() > src.getY()) ? -dy : dy);

         int n2 = 11;
         double dx2 = n2 / 2 * Math.sin(Math.PI / 4 - alpha);
         double dy2 = n2 / 2 * Math.cos(Math.PI / 4 - alpha);

         double xs = xp - dx2;
         double ys = yp - dy2;
         double xe = xp + dx2;
         double ye = yp + dy2;

         path.moveTo(xs, ys);
         path.lineTo(xe, ye);

         if (style == GraphConstants.ARROW_DOUBLELINE) {
            double dx3 = 3 * Math.cos(alpha);
            double dy3 = 3 * Math.sin(alpha);
            path.moveTo(xs + dx3, ys + dy3);
            path.lineTo(xe + dx3, ye + dy3);
         }
         return path;
      } else {
         return super.createLineEnd(size, style, src, dst);
      }
   }

   public Color getForeground() {
      Color c = GraphConstants.getForeground(view.getAttributes());
      return c;
   }

   /**
    * @return The color used to fill transition object.
    */
   public Color getFillColor() {
      GraphTransitionInterface tr = (GraphTransitionInterface) view.getCell();
      XMLCollectionElement tOrA = (XMLCollectionElement) tr.getUserObject();
      ConnectorGraphicsInfo cgi = JaWEManager.getInstance()
         .getXPDLUtils()
         .getConnectorGraphicsInfo(tOrA);
      Color clr = null;
      if (cgi != null) {
         clr = Utils.getColor(cgi.getFillColor());

      }
      if (clr == null) {
         clr = JaWEManager.getInstance()
            .getJaWEController()
            .getTypeResolver()
            .getJaWEType(tOrA)
            .getColor();
      }
      return clr;
   }

   
}
