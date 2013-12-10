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
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.Artifact;
import org.enhydra.jxpdl.elements.Lane;
import org.enhydra.jxpdl.elements.NodeGraphicsInfo;
import org.enhydra.jxpdl.elements.Pool;
import org.enhydra.jxpdl.elements.Transition;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

/**
 * Factory for generating graph objects.
 * 
 * @author Sasa Bojanic
 */
public class GraphObjectFactory {

   protected Properties properties;

   public void configure(Properties props) throws Exception {
      this.properties = props;
   }

   public GraphActivityInterface createActivity(Map viewMap, Activity act, Point partPoint) {
      String type = JaWEManager.getInstance()
         .getJaWEController()
         .getTypeResolver()
         .getJaWEType(act)
         .getTypeId();
      Point offset = GraphUtilities.getOffsetPoint(act);
      GraphActivityInterface gact = createActivityCell(act, type);

      Map m = initActivityProperties(partPoint, offset, act, type);

      viewMap.put(gact, m);
      return gact;
   }

   protected GraphActivityInterface createActivityCell(Activity act, String type) {
      return new DefaultGraphActivity(act);
   }

   protected Map initActivityProperties(Point partPoint,
                                        Point offset,
                                        Activity act,
                                        String type) {
      AttributeMap map = new AttributeMap();

      Rectangle bounds = null;
      int width = GraphUtilities.getGraphController()
         .getGraphSettings()
         .getActivityWidth();
      int height = GraphUtilities.getGraphController()
         .getGraphSettings()
         .getActivityHeight();
      NodeGraphicsInfo ngi = JaWEManager.getInstance()
         .getXPDLUtils()
         .getNodeGraphicsInfo(act);
      if (ngi != null) {
         if (ngi.getWidth() != 0) {
            width = ngi.getWidth();
         }
         if (ngi.getHeight() != 0) {
            height = ngi.getHeight();
         }
      }
      if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_EVENT_START
          || act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_EVENT_END) {
         if (ngi == null || ngi.getWidth() == 0) {
            if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_ROUTE) {
               width = GraphUtilities.getGraphController()
                  .getGraphSettings()
                  .getGatewayWidth();
            } else {
               width = GraphUtilities.getGraphController()
                  .getGraphSettings()
                  .getEventRadius() * 2 + 1;
            }

         }
         if (ngi == null || ngi.getHeight() == 0) {
            if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_ROUTE) {
               height = GraphUtilities.getGraphController()
                  .getGraphSettings()
                  .getGatewayWidth();
            } else {
               height = GraphUtilities.getGraphController()
                  .getGraphSettings()
                  .getEventRadius() * 2 + 1;
            }
         }
         bounds = new Rectangle(partPoint.x + offset.x,
                                partPoint.y + offset.y,
                                width,
                                height);
      } else {
         bounds = new Rectangle(partPoint.x + offset.x,
                                partPoint.y + offset.y,
                                width,
                                height);
      }

      GraphConstants.setBounds(map, bounds);
      GraphConstants.setOpaque(map, true);
      GraphConstants.setBorderColor(map, Color.darkGray);
      String fntn = JaWEManager.getFontName();
      int fntsize = GraphUtilities.getGraphController()
         .getGraphSettings()
         .getGraphFontSize();
      javax.swing.plaf.FontUIResource f;
      try {
         try {
            f = new javax.swing.plaf.FontUIResource(fntn, Font.PLAIN, fntsize);
         } catch (Exception ex) {
            f = new javax.swing.plaf.FontUIResource("Label.font", Font.PLAIN, fntsize);
         }
         GraphConstants.setFont(map, f);
      } catch (Exception ex) {
      }
      return map;
   }

   public GraphArtifactInterface createArtifact(Map viewMap, Artifact art, Point partPoint) {
      String type = JaWEManager.getInstance()
         .getJaWEController()
         .getTypeResolver()
         .getJaWEType(art)
         .getTypeId();
      Point offset = GraphUtilities.getOffsetPoint(art);
      GraphArtifactInterface gact = createArtifactCell(art, type);

      Map m = initArtifactProperties(partPoint, offset, art, type);

      viewMap.put(gact, m);
      return gact;
   }

   protected GraphArtifactInterface createArtifactCell(Artifact art, String type) {
      return new DefaultGraphArtifact(art);
   }

   protected Map initArtifactProperties(Point partPoint,
                                        Point offset,
                                        Artifact art,
                                        String type) {
      AttributeMap map = new AttributeMap();

      Rectangle bounds = null;
      int width = GraphUtilities.getGraphController()
         .getGraphSettings()
         .getDataObjectWidth();
      int height = GraphUtilities.getGraphController()
         .getGraphSettings()
         .getDataObjectHeight();
      if (art.getArtifactType().equals(XPDLConstants.ARTIFACT_TYPE_ANNOTATION)) {
         width = GraphUtilities.getGraphController()
            .getGraphSettings()
            .getAnnotationWidth();
         height = GraphUtilities.getGraphController()
            .getGraphSettings()
            .getAnnotationHeight();
      }
      NodeGraphicsInfo ngi = JaWEManager.getInstance()
         .getXPDLUtils()
         .getNodeGraphicsInfo(art);
      if (ngi != null) {
         if (ngi.getWidth() != 0) {
            width = ngi.getWidth();
         }
         if (ngi.getHeight() != 0) {
            height = ngi.getHeight();
         }
      }
      bounds = new Rectangle(partPoint.x + offset.x,
                             partPoint.y + offset.y,
                             width,
                             height);

      GraphConstants.setBounds(map, bounds);
      GraphConstants.setOpaque(map, true);
      GraphConstants.setBorderColor(map, Color.darkGray);
      String fntn = JaWEManager.getFontName();
      int fntsize = GraphUtilities.getGraphController()
         .getGraphSettings()
         .getGraphFontSize();
      javax.swing.plaf.FontUIResource f;
      try {
         try {
            f = new javax.swing.plaf.FontUIResource(fntn, Font.PLAIN, fntsize);
         } catch (Exception ex) {
            f = new javax.swing.plaf.FontUIResource("Label.font", Font.PLAIN, fntsize);
         }
         GraphConstants.setFont(map, f);
      } catch (Exception ex) {
      }
      return map;
   }

   public GraphSwimlaneInterface createParticipant(Rectangle bounds,
                                                      Map viewMap,
                                                      Object par) {
      GraphSwimlaneInterface gpar = createParticipantCell(par);

      Map m = initParticipantProperties(bounds, par);

      viewMap.put(gpar, m);
      return gpar;
   }

   protected GraphSwimlaneInterface createParticipantCell(Object par) {
      if (par instanceof Pool)
         return new DefaultGraphSwimlane((Pool) par);
      else if (par instanceof Lane)
         return new DefaultGraphSwimlane((Lane) par);
      return null;
   }

   protected Map initParticipantProperties(Rectangle bounds, Object par) {
      AttributeMap map = new AttributeMap();
      GraphConstants.setBounds(map, bounds);
      GraphConstants.setOpaque(map, false);
      GraphConstants.setBorderColor(map, Color.black);
      GraphConstants.setMoveable(map, false);
      GraphConstants.setSizeable(map, false);
      String fntn = JaWEManager.getFontName();
      int fntsize = GraphUtilities.getGraphController()
         .getGraphSettings()
         .getGraphFontSize();
      javax.swing.plaf.FontUIResource f;
      try {
         try {
            f = new javax.swing.plaf.FontUIResource(fntn, Font.PLAIN, fntsize);
         } catch (Exception ex) {
            f = new javax.swing.plaf.FontUIResource("Label.font", Font.PLAIN, fntsize);
         }
         GraphConstants.setFont(map, f);
      } catch (Exception ex) {
      }
      return map;
   }

   public GraphTransitionInterface createTransition(List points,
                                                    Map viewMap,
                                                    XMLCollectionElement tra) {
      GraphTransitionInterface gtra = createTransitionCell(tra);

      Map m = initTransitionProperties(points, tra);

      viewMap.put(gtra, m);

      return gtra;
   }

   protected GraphTransitionInterface createTransitionCell(XMLCollectionElement tra) {
      return new DefaultGraphTransition(tra);
   }

   protected Map initTransitionProperties(List points, XMLCollectionElement tra) {
      AttributeMap map = new AttributeMap();
      // if (points!=null && points.size()>0)
      // System.out.println("Setting points "+points);
      GraphConstants.setPoints(map, points);
      setTransitionStyle(GraphUtilities.getStyle(tra), map);

      // GraphConstants.setLineColor(map,Utils.getColor(JaWEConfig.getInstance().getTransitionColor()));
      GraphConstants.setLineEnd(map, GraphConstants.ARROW_TECHNICAL);
      GraphConstants.setEndFill(map, true);
      GraphConstants.setEndSize(map, 7);
      GraphConstants.setBeginFill(map, true);
      GraphConstants.setBeginSize(map, 7);
//      GraphConstants.setLabelAlongEdge(map, true);

      String fntn = JaWEManager.getFontName();
      int fntsize = GraphUtilities.getGraphController()
         .getGraphSettings()
         .getGraphFontSize();
      javax.swing.plaf.FontUIResource f;
      try {
         try {
            f = new javax.swing.plaf.FontUIResource(fntn, Font.PLAIN, fntsize);
         } catch (Exception ex) {
            f = new javax.swing.plaf.FontUIResource("Label.font", Font.PLAIN, fntsize);
         }
         GraphConstants.setFont(map, f);
      } catch (Exception ex) {
      }

      return map;
   }

   protected void setTransitionStyle(String style, AttributeMap map) {
      if (style.equals(GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_BEZIER)) {
         GraphConstants.setLineStyle(map, GraphConstants.STYLE_BEZIER);
      } else if (style.equals(GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_SPLINE)) {
         GraphConstants.setLineStyle(map, GraphConstants.STYLE_SPLINE);
      } else if (style.equals(GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_SIMPLE_ROUTING_BEZIER)) {
         GraphConstants.setRouting(map, GraphConstants.ROUTING_SIMPLE);
         GraphConstants.setLineStyle(map, GraphConstants.STYLE_BEZIER);
      } else if (style.equals(GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_SIMPLE_ROUTING_ORTHOGONAL)) {
         GraphConstants.setRouting(map, GraphConstants.ROUTING_SIMPLE);
         GraphConstants.setLineStyle(map, GraphConstants.STYLE_ORTHOGONAL);
      } else if (style.equals(GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_SIMPLE_ROUTING_SPLINE)) {
         GraphConstants.setRouting(map, GraphConstants.ROUTING_SIMPLE);
         GraphConstants.setLineStyle(map, GraphConstants.STYLE_SPLINE);
      } else {
         GraphConstants.setLineStyle(map, GraphConstants.STYLE_ORTHOGONAL);
      }
   }

   public GraphPortInterface createPort(String name, String type) {
      GraphPortInterface gpor = createPortCell(name, type);
      return gpor;
   }

   protected GraphPortInterface createPortCell(String name, String type) {
      return new DefaultGraphPort(name, type);
   }

   protected Map initPortProperties(String type) {
      AttributeMap map = new AttributeMap();
      return map;
   }
}
