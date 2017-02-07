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
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEConstants;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.XPDLElementChangeInfo;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLElementChangeInfo;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Activities;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ActivitySet;
import org.enhydra.jxpdl.elements.ActivitySets;
import org.enhydra.jxpdl.elements.Artifact;
import org.enhydra.jxpdl.elements.Artifacts;
import org.enhydra.jxpdl.elements.Association;
import org.enhydra.jxpdl.elements.Associations;
import org.enhydra.jxpdl.elements.ConnectorGraphicsInfo;
import org.enhydra.jxpdl.elements.ConnectorGraphicsInfos;
import org.enhydra.jxpdl.elements.Coordinates;
import org.enhydra.jxpdl.elements.Coordinatess;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;
import org.enhydra.jxpdl.elements.Lane;
import org.enhydra.jxpdl.elements.Lanes;
import org.enhydra.jxpdl.elements.NestedLane;
import org.enhydra.jxpdl.elements.NestedLanes;
import org.enhydra.jxpdl.elements.NodeGraphicsInfo;
import org.enhydra.jxpdl.elements.NodeGraphicsInfos;
import org.enhydra.jxpdl.elements.Package;
import org.enhydra.jxpdl.elements.Participant;
import org.enhydra.jxpdl.elements.Performer;
import org.enhydra.jxpdl.elements.Performers;
import org.enhydra.jxpdl.elements.Pool;
import org.enhydra.jxpdl.elements.Pools;
import org.enhydra.jxpdl.elements.Transition;
import org.enhydra.jxpdl.elements.Transitions;
import org.enhydra.jxpdl.elements.WorkflowProcess;
import org.enhydra.jxpdl.elements.WorkflowProcesses;
import org.enhydra.jxpdl.utilities.SequencedHashMap;
import org.jgraph.graph.AbstractCellView;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.ParentMap;

public class GraphUtilities {

   public static GraphController getGraphController() {
      GraphController gc = null;
      List cs = JaWEManager.getInstance().getComponentManager().getComponents();
      Iterator it = cs.iterator();
      while (it.hasNext()) {
         JaWEComponent jc = (JaWEComponent) it.next();
         if (jc instanceof GraphController) {
            gc = (GraphController) jc;
            break;
         }
      }
      return gc;
   }

   // -----------------------------------------------------------------------------------------
   // ------------- WORKFLOW PROCESS
   public static List getParticipantVisualOrder(XMLCollectionElement wpOrAs) {
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(wpOrAs);
      String asId = null;
      if (wpOrAs instanceof ActivitySet) {
         asId = wpOrAs.getId();
      }
      return GraphUtilities.getParticipantVisualOrder(wp, asId);
   }

   public static List getParticipantVisualOrder(WorkflowProcess wp, String asId) {
      List order = new ArrayList();
      ExtendedAttribute ea = getParticipantVisualOrderEA(wp, asId);
      if (ea != null) {
         String ord = ea.getVValue();
         // System.err.println("VOVAL for wp=" + wp.getId() + " as=" + asId + " is " + ord
         // + "!");
         String pId = null;
         while (true) {
            int ind = ord.indexOf(";");
            String tmpId = null;
            boolean clearPid = false;
            if (ind < 0) {
               if (ord.length() == 0) {
                  break;
               }
               tmpId = ord;
            } else {
               tmpId = ord.substring(0, ind);
               ord = ord.substring(ind + 1);
            }
            int cepPrefInd = tmpId.indexOf(GraphEAConstants.COMMON_EXPRESSION_LANE_PREFIX);
            int cepSuffInd = tmpId.indexOf(GraphEAConstants.COMMON_EXPRESSION_LANE_SUFIX);
            if (cepPrefInd >= 0 && cepSuffInd < 0) {
               if (pId == null) {
                  pId = tmpId + ";";
               } else {
                  pId += tmpId + ";";
               }
               continue;
            } else if (cepPrefInd < 0 && cepSuffInd >= 0) {
               pId += tmpId;
               clearPid = true;
            } else if (cepPrefInd < 0 && cepSuffInd < 0) {
               if (pId != null) {
                  pId += tmpId + ";";
                  continue;
               }
               pId = tmpId;
               clearPid = true;
            } else {
               pId = tmpId;
               clearPid = true;
            }
            order.add(pId);
            if (clearPid) {
               pId = null;
            }
            if (tmpId.equals(ord)) {
               break;
            }
         }
         if (asId != null) {
            order.remove(0);
         }
      }
      // System.out.println("VOORD for wp=" + wp.getId() + " as=" + asId + " is " + order
      // + "!");
      return order;
   }

   public static List getLaneVisualOrder(XMLCollectionElement wpOrAs) {
      List order = new ArrayList();
      List lh = getLanesInVisualOrder(wpOrAs);
      for (int i = 0; i < lh.size(); i++) {
         Lane l = (Lane) lh.get(i);
         order.add(l.getId());
      }
      // System.out.println("VOORD for wp=" + wp.getId() + " as=" + asId + " is " + order
      // + "!");
      return order;
   }

   public static List getLanesInVisualOrder(XMLCollectionElement wpOrAs) {
      Pool p = JaWEManager.getInstance().getXPDLUtils().getPoolForProcessOrActivitySet(wpOrAs);
      List lh = getLaneHierarchy(p);
      // System.out.println("VOORD for wp=" + wp.getId() + " as=" + asId + " is " + order
      // + "!");
      return lh;
   }

   protected static ExtendedAttribute getParticipantVisualOrderEA(XMLCollectionElement wpOrAs) {
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(wpOrAs);
      String asId = null;
      if (wpOrAs instanceof ActivitySet) {
         asId = wpOrAs.getId();
      }
      return GraphUtilities.getParticipantVisualOrderEA(wp, asId);
   }

   protected static ExtendedAttribute getParticipantVisualOrderEA(WorkflowProcess wp, String asId) {
      ExtendedAttributes eas = wp.getExtendedAttributes();
      ExtendedAttribute ea = null;
      String eaname = GraphEAConstants.EA_JAWE_GRAPH_WORKFLOW_PARTICIPANT_ORDER;
      if (asId == null) {
         ea = eas.getFirstExtendedAttributeForName(eaname);
      } else {
         eaname = GraphEAConstants.EA_JAWE_GRAPH_BLOCK_PARTICIPANT_ORDER;
         List l = eas.getElementsForName(eaname);
         Iterator it = l.iterator();
         while (it.hasNext()) {
            ExtendedAttribute bvoea = (ExtendedAttribute) it.next();
            String casId = GraphUtilities.getActivitySetIdForBlockParticipantVisualOrderOrOrientationEA(bvoea);
            if (casId.equals(asId)) {
               ea = bvoea;
               break;
            }
         }
      }
      return ea;
   }

   protected static String getActivitySetIdForBlockParticipantVisualOrderOrOrientationEA(ExtendedAttribute ea) {
      return GraphUtilities.getActivitySetIdForBlockParticipantVisualOrderOrOrientationEA(ea.getVValue());
   }

   protected static String getActivitySetIdForBlockParticipantVisualOrderOrOrientationEA(String eaval) {
      String[] strarr = XMLUtil.tokenize(eaval, ";");
      if (strarr.length > 0) {
         return strarr[0];
      }
      return "";
   }

   protected static ExtendedAttribute createParticipantVisualOrderEA(XMLCollectionElement wpOrAs, String val, boolean addToCollection) {
      ExtendedAttributes eas = XMLUtil.getWorkflowProcess(wpOrAs).getExtendedAttributes();
      String eaname = GraphEAConstants.EA_JAWE_GRAPH_WORKFLOW_PARTICIPANT_ORDER;
      if (wpOrAs instanceof ActivitySet) {
         eaname = GraphEAConstants.EA_JAWE_GRAPH_BLOCK_PARTICIPANT_ORDER;
      }
      ExtendedAttribute ea = (ExtendedAttribute) eas.generateNewElement();
      ea.setName(eaname);
      ea.setVValue(val);
      if (addToCollection) {
         eas.add(0, ea);
      }
      return ea;
   }

   protected static String createParticipantVisualOrderEAVal(XMLCollectionElement wpOrAs, List order) {
      String ord = "";
      if (wpOrAs instanceof ActivitySet) {
         ord = wpOrAs.getId() + ";";
      }
      if (order != null && order.size() > 0) {
         for (int i = 0; i < order.size(); i++) {
            ord += (String) order.get(i);
            if (i != order.size() - 1) {
               ord += ";";
            }
         }
      }
      return ord;
   }

   public static String getGraphOrientation(XMLCollectionElement wpOrAs) {
      Pool p = JaWEManager.getInstance().getXPDLUtils().getPoolForProcessOrActivitySet(wpOrAs);
      return p != null ? p.getOrientation() : XPDLConstants.POOL_ORIENTATION_HORIZONTAL;
   }

   public static void setGraphOrientation(XMLCollectionElement wpOrAs, String orientation) {
      JaWEManager.getInstance().getXPDLUtils().getPoolForProcessOrActivitySet(wpOrAs).setOrientation(orientation);
   }

   public static String getGraphParticipantOrientation(WorkflowProcess wp, XMLCollectionElement wpOrAs) {
      String asId = null;
      if (wpOrAs instanceof ActivitySet) {
         asId = wpOrAs.getId();
      }
      return GraphUtilities.getGraphParticipantOrientation(wp, asId);
   }

   public static String getGraphParticipantOrientation(WorkflowProcess wp, String asId) {
      String orientation = XPDLConstants.POOL_ORIENTATION_HORIZONTAL;
      ExtendedAttribute ea = GraphUtilities.getGraphParticipantOrientationEA(wp, asId);
      if (ea != null) {
         orientation = ea.getVValue();
         if (asId != null) {
            String[] parts = XMLUtil.tokenize(orientation, ";");
            orientation = parts[1];
         }
      }
      return orientation;
   }

   protected static ExtendedAttribute getGraphParticipantOrientationEA(WorkflowProcess wp, String asId) {
      String eaname = GraphEAConstants.EA_JAWE_GRAPH_WORKFLOW_PARTICIPANT_ORIENTATION;
      if (asId != null) {
         eaname = GraphEAConstants.EA_JAWE_GRAPH_BLOCK_PARTICIPANT_ORIENTATION;
      }
      ExtendedAttributes eas = wp.getExtendedAttributes();
      ExtendedAttribute ea = null;
      if (asId == null) {
         ea = eas.getFirstExtendedAttributeForName(eaname);
      } else {
         Iterator it = eas.getElementsForName(eaname).iterator();
         while (it.hasNext()) {
            ExtendedAttribute eat = (ExtendedAttribute) it.next();
            String casId = GraphUtilities.getActivitySetIdForBlockParticipantVisualOrderOrOrientationEA(eat);
            if (!casId.equals(asId))
               continue;
            ea = eat;
            break;
         }
      }
      return ea;
   }

   public static List getStartOrEndDescriptions(XMLCollectionElement wpOrAs, boolean isStart) {
      List startOrEndDescriptions = new ArrayList();
      List eas = GraphUtilities.getStartOrEndExtendedAttributes(wpOrAs, isStart);
      Iterator it = eas.iterator();
      while (it.hasNext()) {
         ExtendedAttribute ea = (ExtendedAttribute) it.next();
         StartEndDescription sed = new StartEndDescription(ea);
         startOrEndDescriptions.add(sed);
      }
      return startOrEndDescriptions;
   }

   public static List getStartOrEndExtendedAttributes(XMLCollectionElement wpOrAs, boolean isStart) {
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(wpOrAs);
      String asId = null;
      if (wpOrAs instanceof ActivitySet) {
         asId = wpOrAs.getId();
      }

      return GraphUtilities.getStartOrEndExtendedAttributes(wp, asId, isStart);
   }

   public static List getStartOrEndExtendedAttributes(WorkflowProcess wp, String asId, boolean isStart) {
      List ret = new ArrayList();
      ExtendedAttributes eas = wp.getExtendedAttributes();
      String eaname = null;
      if (isStart) {
         if (asId == null) {
            eaname = GraphEAConstants.EA_JAWE_GRAPH_START_OF_WORKFLOW;
         } else {
            eaname = GraphEAConstants.EA_JAWE_GRAPH_START_OF_BLOCK;
         }
      } else {
         if (asId == null) {
            eaname = GraphEAConstants.EA_JAWE_GRAPH_END_OF_WORKFLOW;
         } else {
            eaname = GraphEAConstants.EA_JAWE_GRAPH_END_OF_BLOCK;
         }
      }
      List l = eas.getElementsForName(eaname);
      // System.out.println("Found " + l.size() + " eas with name " + eaname + " for wp "
      // + wp.getId() + ", as=" + asId);
      if (asId == null) {
         ret.addAll(l);
      } else {
         Iterator it = l.iterator();
         while (it.hasNext()) {
            ExtendedAttribute ea = (ExtendedAttribute) it.next();
            String sedstr = ea.getVValue();
            // System.out.println("EA for BA = " + sedstr);
            String[] startOrEndD = XMLUtil.tokenize(sedstr, ",");
            int ind = startOrEndD[0].indexOf(GraphEAConstants.EA_PART_ACTIVITY_SET_ID + "=");
            String asetId = startOrEndD[0].substring(ind + (GraphEAConstants.EA_PART_ACTIVITY_SET_ID + "=").length());
            if (!asetId.equals(asId))
               continue;
            ret.add(0, ea);
         }
      }
      return ret;
   }

   public static List getStartOrEndExtendedAttributes(XMLCollectionElement wpOrAs, String id, String eapart) {
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(wpOrAs);
      String asId = null;
      if (wpOrAs instanceof ActivitySet) {
         asId = wpOrAs.getId();
      }
      return GraphUtilities.getStartOrEndExtendedAttributes(wp, asId, id, eapart);
   }

   public static List getStartOrEndExtendedAttributes(WorkflowProcess wp, String asId, String id, String eapart) {
      List seeas = GraphUtilities.getStartOrEndExtendedAttributes(wp, asId, true);
      seeas.addAll(GraphUtilities.getStartOrEndExtendedAttributes(wp, asId, false));
      // System.out.println("There are "+seeas.size()+" seeas in graph
      // "+wpOrAs.getId());
      List toRet = new ArrayList();
      Iterator it = seeas.iterator();
      while (it.hasNext()) {
         ExtendedAttribute ea = (ExtendedAttribute) it.next();
         StartEndDescription sed = new StartEndDescription(ea);
         // System.out.println("SED=" + sed.toString());
         // NOTE: sed.getXXXId() can be null
         if ((eapart.equals(GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID) && id.equals(sed.getParticipantId()))
             || (eapart.equals(GraphEAConstants.EA_PART_ACTIVITY_SET_ID) && id.equals(sed.getActSetId()))
             || (eapart.equals(GraphEAConstants.EA_PART_CONNECTING_ACTIVITY_ID) && id.equals(sed.getActId()))) {
            toRet.add(ea);
         }
      }
      // System.out.println("Found " + toRet.size() + " seds for " + eapart + ", id=" +
      // id);
      return toRet;
   }

   // ---------------- ACTIVITY
   public static String getParticipantId(Activity act) {
      String participantId = FreeTextExpressionParticipant.getInstance().getId();
      ExtendedAttribute ea = getParticipantIdEA(act);
      if (ea != null) {
         participantId = ea.getVValue();
      }
      return participantId;
   }

   protected static ExtendedAttribute getParticipantIdEA(Activity act) {
      return act.getExtendedAttributes().getFirstExtendedAttributeForName(GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID);
   }

   protected static ExtendedAttribute createParticipantIdEA(Activity act, String val, boolean addToCollection) {
      ExtendedAttributes eas = act.getExtendedAttributes();
      ExtendedAttribute ea = (ExtendedAttribute) eas.generateNewElement();
      ea.setName(GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID);
      ea.setVValue(val);
      if (addToCollection) {
         eas.add(0, ea);
      }
      return ea;
   }

   public static Point getOffsetPoint(Activity act) {
      Point offset = new Point(0, 0);
      NodeGraphicsInfo ea = JaWEManager.getInstance().getXPDLUtils().getNodeGraphicsInfo(act);
      if (ea != null) {
         try {
            offset.x = (int) Double.parseDouble(ea.getCoordinates().getXCoordinate());
         } catch (Exception ex) {
         }
         try {
            offset.y = (int) Double.parseDouble(ea.getCoordinates().getYCoordinate());
         } catch (Exception ex) {
         }
      }
      return offset;
   }

   public static Point getOffsetPoint(XMLCollectionElement actOrArtif) {
      Point offset = new Point(0, 0);
      NodeGraphicsInfo ea = JaWEManager.getInstance().getXPDLUtils().getNodeGraphicsInfo(actOrArtif);
      if (ea != null) {
         try {
            offset.x = (int) Double.parseDouble(ea.getCoordinates().getXCoordinate());
         } catch (Exception ex) {
         }
         try {
            offset.y = (int) Double.parseDouble(ea.getCoordinates().getYCoordinate());
         } catch (Exception ex) {
         }
      }
      return offset;
   }

   public static void setOffsetPoint(XMLCollectionElement actOrArtif, Point offset, String laneId) {
      if (offset == null) {
         offset = new Point(0, 0);
      }
      NodeGraphicsInfo ea = JaWEManager.getInstance().getXPDLUtils().getNodeGraphicsInfo(actOrArtif);
      // System.out.println("Act "+act.getId()+", eaxoff="+ea);
      if (ea == null) {
         ea = GraphUtilities.createNodeGraphicsInfo(actOrArtif, offset, laneId, true);
      } else {
         if (laneId != null) {
            ea.setLaneId(laneId);
         }
         ea.getCoordinates().setXCoordinate(String.valueOf(offset.x));
         ea.getCoordinates().setYCoordinate(String.valueOf(offset.y));
      }
   }

   public static int getLabelLocation(XMLCollectionElement actOrArtif) {
      int lp = GraphEAConstants.LABEL_POSITION_BOTTOM;
      NodeGraphicsInfo ea = JaWEManager.getInstance().getXPDLUtils().getNodeGraphicsInfo(actOrArtif);
      if (ea != null) {
         try {
            lp = Integer.parseInt(ea.getShape());
         } catch (Exception ex) {
         }
      }
      return lp;
   }

   public static void setLabelLocation(XMLCollectionElement actOrArtif, int location) {
      NodeGraphicsInfo ea = JaWEManager.getInstance().getXPDLUtils().getNodeGraphicsInfo(actOrArtif);
      if (ea != null) {
         ea.setShape(String.valueOf(location));
      }
   }

   protected static NodeGraphicsInfo createNodeGraphicsInfo(XMLCollectionElement actOrArtif, Point val, String laneId, boolean addToCollection) {
      NodeGraphicsInfos eas = (NodeGraphicsInfos) actOrArtif.get("NodeGraphicsInfos");
      NodeGraphicsInfo ea = JaWEManager.getInstance().getXPDLObjectFactory().createXPDLObject(eas, "", false);
      if (laneId != null) {
         ea.setLaneId(laneId);
      }
      if (val != null) {
         ea.getCoordinates().setXCoordinate(String.valueOf(val.x));
         ea.getCoordinates().setYCoordinate(String.valueOf(val.y));
      }
      int width = GraphUtilities.getGraphController().getGraphSettings().getActivityWidth();
      int height = GraphUtilities.getGraphController().getGraphSettings().getActivityHeight();

      if (actOrArtif instanceof Activity) {
         Activity act = (Activity) actOrArtif;
         if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_EVENT_START || act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_EVENT_END) {
            width = GraphUtilities.getGraphController().getGraphSettings().getEventRadius() * 2 + 1;
            height = GraphUtilities.getGraphController().getGraphSettings().getEventRadius() * 2 + 1;
         } else if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_ROUTE) {
            width = GraphUtilities.getGraphController().getGraphSettings().getGatewayWidth();
            height = GraphUtilities.getGraphController().getGraphSettings().getGatewayHeight();
         }
      } else if (actOrArtif instanceof Artifact) {
         Artifact art = (Artifact) actOrArtif;
         if (art.getArtifactType().equals(XPDLConstants.ARTIFACT_TYPE_ANNOTATION)) {
            width = GraphUtilities.getGraphController().getGraphSettings().getAnnotationWidth();
            height = GraphUtilities.getGraphController().getGraphSettings().getAnnotationHeight();
         } else {
            width = GraphUtilities.getGraphController().getGraphSettings().getDataObjectWidth();
            height = GraphUtilities.getGraphController().getGraphSettings().getDataObjectHeight();
         }
      }
      Color c = null;
      Color bc = GraphUtilities.getGraphController().getGraphSettings().getLaneBorderColor();
      if (actOrArtif instanceof Pool || actOrArtif instanceof Lane) {
         Object par = null;
         if (actOrArtif instanceof Lane) {
            Lane l = (Lane) actOrArtif;
            par = GraphUtilities.getParticipantForLane(l, null);
            if (par == null && l.getPerformers().size() > 0) {
               par = l;
            }
         }
         if (par == null) {
            par = FreeTextExpressionParticipant.getInstance();
         }

         if (par instanceof FreeTextExpressionParticipant) {
            c = GraphUtilities.getGraphController().getGraphSettings().getLaneFreeTextExpressionColor();
         } else if (par instanceof Lane) {
            c = GraphUtilities.getGraphController().getGraphSettings().getLaneCommonExpressionColor();
         } else {
            c = JaWEManager.getInstance().getJaWEController().getTypeResolver().getJaWEType((Participant) par).getColor();
         }
      } else {
         ea.setWidth(width);
         ea.setHeight(height);
         c = JaWEManager.getInstance().getJaWEController().getTypeResolver().getJaWEType(actOrArtif).getColor();
      }

      ea.setFillColor(Utils.getColorString(c));
      ea.setBorderColor(Utils.getColorString(bc));
      ea.setToolId("JaWE");
      if (addToCollection) {
         eas.add(ea);
      }
      return ea;
   }

   protected static NodeGraphicsInfo createNodeGraphicsInfo(XMLCollectionElement wpOrAs, XMLCollectionElement actOrArtif, NodeGraphicsInfo ngi) {
      NodeGraphicsInfos eas = (NodeGraphicsInfos) actOrArtif.get("NodeGraphicsInfos");
      NodeGraphicsInfo ngin = JaWEManager.getInstance().getXPDLObjectFactory().createXPDLObject(eas, "", false);
      ngin.setLaneId(ngi.getLaneId());
      int participantNameWidth = GraphUtilities.getGraphController().getGraphSettings().getLaneNameWidth();
      double x = participantNameWidth + 1;
      double y = participantNameWidth + 1;
      try {
         x = Double.parseDouble(ngi.getCoordinates().getXCoordinate());
      } catch (Exception ex) {
      }
      try {
         y = Double.parseDouble(ngi.getCoordinates().getYCoordinate());
      } catch (Exception ex) {
      }
      if (wpOrAs != null) {
         String gor = getGraphOrientation(wpOrAs);
         if (gor.equals(XPDLConstants.POOL_ORIENTATION_HORIZONTAL)) {
            if (x < participantNameWidth + 1) {
               x = participantNameWidth + 1;
            }
         } else {
            if (y < participantNameWidth + 1) {
               y = participantNameWidth + 1;
            }
         }
      }
      if (x < 1) {
         x = 1;
      }
      if (y < 1) {
         y = 1;
      }
      ngin.getCoordinates().setXCoordinate(String.valueOf(x));
      ngin.getCoordinates().setYCoordinate(String.valueOf(y));
      try {
         ngin.setWidth(ngi.getWidth());
      } catch (Exception ex) {
      }
      try {
         ngin.setHeight(ngi.getHeight());
      } catch (Exception ex) {
      }
      if (ngin.getWidth() == 0 || ngin.getHeight() == 0) {
         if (actOrArtif instanceof Activity) {
            Activity act = (Activity) actOrArtif;
            if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_EVENT_START || act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_EVENT_END) {
               if (ngin.getWidth() == 0) {
                  ngin.setWidth(GraphUtilities.getGraphController().getGraphSettings().getEventRadius() * 2 + 1);
               }
               if (ngin.getHeight() == 0) {
                  ngin.setHeight(GraphUtilities.getGraphController().getGraphSettings().getEventRadius() * 2 + 1);
               }
            } else if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_ROUTE) {
               if (ngin.getWidth() == 0) {
                  ngin.setWidth(GraphUtilities.getGraphController().getGraphSettings().getGatewayWidth());
               }
               if (ngin.getHeight() == 0) {
                  ngin.setHeight(GraphUtilities.getGraphController().getGraphSettings().getGatewayHeight());
               }
            } else {
               if (ngin.getWidth() == 0) {
                  ngin.setWidth(GraphUtilities.getGraphController().getGraphSettings().getActivityWidth());
               }
               if (ngin.getHeight() == 0) {
                  ngin.setHeight(GraphUtilities.getGraphController().getGraphSettings().getActivityHeight());
               }
            }
         } else if (actOrArtif instanceof Artifact) {
            Artifact art = (Artifact) actOrArtif;
            if (art.getArtifactType().equals(XPDLConstants.ARTIFACT_TYPE_ANNOTATION)) {
               if (ngin.getWidth() == 0) {
                  ngin.setWidth(GraphUtilities.getGraphController().getGraphSettings().getAnnotationWidth());
               }
               if (ngin.getHeight() == 0) {
                  ngin.setHeight(GraphUtilities.getGraphController().getGraphSettings().getAnnotationHeight());
               } else {
                  if (ngin.getWidth() == 0) {
                     ngin.setWidth(GraphUtilities.getGraphController().getGraphSettings().getDataObjectWidth());
                  }
                  if (ngin.getHeight() == 0) {
                     ngin.setHeight(GraphUtilities.getGraphController().getGraphSettings().getDataObjectHeight());
                  }
               }
            }
         }
      }

      if (!ngi.getFillColor().equals("")) {
         Color c = Utils.getColor(ngi.getFillColor());
         ngin.setFillColor(Utils.getColorString(c));
      }
      if (!ngi.getBorderColor().equals("")) {
         Color bc = Utils.getColor(ngi.getBorderColor());
         ngin.setBorderColor(Utils.getColorString(bc));
      }
      ngin.setToolId("JaWE");
      eas.add(ngin);
      return ngin;
   }

   public static Point getOffsetPointOld(Activity act) {
      Point offset = new Point(0, 0);
      ExtendedAttribute ea = act.getExtendedAttributes().getFirstExtendedAttributeForName(GraphEAConstants.EA_JAWE_GRAPH_OFFSET);

      if (ea != null) {
         String offsetstr = ea.getVValue();
         String[] offsetstrD = XMLUtil.tokenize(offsetstr, ",");
         try {
            offset.x = Integer.parseInt(offsetstrD[0]);
            offset.y = Integer.parseInt(offsetstrD[1]);

         } catch (Exception ex) {
         }
         ((ExtendedAttributes) ea.getParent()).remove(ea);
      }
      return offset;
   }

   protected static NodeGraphicsInfo updateNodeGraphicsInfoFromAnyOtherVendor(XMLCollectionElement actOrArtif, XMLCollectionElement wpOrAs) {
      Iterator it = ((NodeGraphicsInfos) actOrArtif.get("NodeGraphicsInfos")).toElements().iterator();
      NodeGraphicsInfo ngi = null;
      while (it.hasNext()) {
         ngi = (NodeGraphicsInfo) it.next();
         break;
      }
      if (ngi != null) {
         return createNodeGraphicsInfo(wpOrAs, actOrArtif, ngi);
      }
      return null;
   }

   // --------------- TRANSITION
   public static String getStyle(XMLCollectionElement tra) {
      String style = GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_SPLINE;
      ConnectorGraphicsInfo ea = JaWEManager.getInstance().getXPDLUtils().getConnectorGraphicsInfo(tra, false);
      if (ea != null) {
         style = ea.getStyle();
      }
      return style;
   }

   public static void setStyle(XMLCollectionElement tra, String style) {
      ConnectorGraphicsInfo ea = JaWEManager.getInstance().getXPDLUtils().getConnectorGraphicsInfo(tra, false);
      if (ea == null) {
         ea = GraphUtilities.createConnectorGraphicsInfo(tra, null, style, true);
      } else {
         ea.setStyle(style);
      }
   }

   protected static String getStyleOld(Transition tra) {
      String style = "";
      ExtendedAttribute ea = getStyleEAOld(tra);
      if (ea != null) {
         style = ea.getVValue();
         ((ExtendedAttributes) ea.getParent()).remove(ea);
      }
      return style;
   }

   protected static ExtendedAttribute getStyleEAOld(Transition tra) {
      return tra.getExtendedAttributes().getFirstExtendedAttributeForName(GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE);
   }

   public static List getBreakpoints(XMLCollectionElement tra) {
      List breakPoints = new ArrayList();
      ConnectorGraphicsInfo ea = JaWEManager.getInstance().getXPDLUtils().getConnectorGraphicsInfo(tra, false);
      if (ea != null) {
         Point p;
         List pPos = ea.getCoordinatess().toElements();
         for (int i = 0; i < pPos.size(); i++) {
            Coordinates c = (Coordinates) pPos.get(i);
            try {
               p = new Point((int) Double.parseDouble(c.getXCoordinate()), (int) Double.parseDouble(c.getYCoordinate()));
               breakPoints.add(p);
            } catch (Exception ex) {
            }
         }
      }
      return breakPoints;
   }

   public static void setBreakpoints(XMLCollectionElement tra, List breakPoints) {
      ConnectorGraphicsInfo ea = JaWEManager.getInstance().getXPDLUtils().getConnectorGraphicsInfo(tra, false);
      if (ea == null) {
         ea = GraphUtilities.createConnectorGraphicsInfo(tra, breakPoints, GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_SPLINE, true);
      } else {
         setBreakPointCoordinates(ea, breakPoints);
      }
   }

   public static Point getLabelPosition(XMLCollectionElement tra) {
      Point lp = null;
      ConnectorGraphicsInfo ea = JaWEManager.getInstance().getXPDLUtils().getConnectorGraphicsInfo(tra, true);
      if (ea != null) {
         Coordinates c = null;
         if (ea.getCoordinatess().size() > 0) {
            c = (Coordinates) ea.getCoordinatess().get(0);
            lp = new Point((int) Double.parseDouble(c.getXCoordinate()), (int) Double.parseDouble(c.getYCoordinate()));
         }
      }
      return lp;
   }

   public static Point getLabelPositionOffset(XMLCollectionElement tra) {
      Point lpo = null;
      ConnectorGraphicsInfo ea = JaWEManager.getInstance().getXPDLUtils().getConnectorGraphicsInfo(tra, true);
      if (ea != null) {
         Coordinates c = null;
         if (ea.getCoordinatess().size() > 1) {
            c = (Coordinates) ea.getCoordinatess().get(1);
            lpo = new Point((int) Double.parseDouble(c.getXCoordinate()), (int) Double.parseDouble(c.getYCoordinate()));
         }
      }
      return lpo;
   }

   public static void setLabelPosition(XMLCollectionElement tra, Point p, Point offset) {
      ConnectorGraphicsInfo ea = JaWEManager.getInstance().getXPDLUtils().getConnectorGraphicsInfo(tra, true);
      List<Point> ps = new ArrayList<Point>();
      if (p != null) {
         ps.add(p);
         if (offset != null) {
            ps.add(offset);
         }
      }
      if (ea == null) {
         if (p != null) {
            ea = GraphUtilities.createConnectorGraphicsInfo(tra, ps, GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_LABEL, true);
         }
      } else {
         if (p != null) {
            setBreakPointCoordinates(ea, ps);
         } else {
            ((ConnectorGraphicsInfos) ea.getParent()).remove(ea);
         }
      }
   }

   public static ConnectorGraphicsInfo createConnectorGraphicsInfo(XMLCollectionElement tra, List bps, String style, boolean addToCollection) {
      ConnectorGraphicsInfos eas = (ConnectorGraphicsInfos) tra.get("ConnectorGraphicsInfos");
      ConnectorGraphicsInfo ea = JaWEManager.getInstance().getXPDLObjectFactory().createXPDLObject(eas, "", false);
      setBreakPointCoordinates(ea, bps);
      ea.setStyle(style);
      Color c = JaWEManager.getInstance().getJaWEController().getTypeResolver().getJaWEType(tra).getColor();
      ea.setFillColor(Utils.getColorString(c));
      ea.setToolId("JaWE");
      if (addToCollection) {
         eas.add(ea);
      }
      return ea;
   }

   protected static void setBreakPointCoordinates(ConnectorGraphicsInfo cgi, List bps) {
      Coordinatess cs = cgi.getCoordinatess();
      boolean sameSize = cs != null && bps != null && cs.size() == bps.size();
      if (!sameSize) {
         cs.clear();
      }
      if (bps != null) {
         for (int i = 0; i < bps.size(); i++) {
            Point p = (Point) bps.get(i);
            Coordinates c = sameSize ? (Coordinates) cs.get(i) : (Coordinates) cs.generateNewElement();
            c.setXCoordinate(String.valueOf(p.x));
            c.setYCoordinate(String.valueOf(p.y));
            if (!sameSize) {
               cs.add(c);
            }
         }
      }
   }

   protected static List getBreakpointsOld(Transition tra) {
      List breakPoints = new ArrayList();
      ExtendedAttribute ea = tra.getExtendedAttributes().getFirstExtendedAttributeForName(GraphEAConstants.EA_JAWE_GRAPH_BREAK_POINTS);

      if (ea != null) {
         Point p;
         String[] pPos = XMLUtil.tokenize(ea.getVValue(), "-");
         int offsetx = 0;
         int offsety = 0;
         if (XPDLConstants.POOL_ORIENTATION_HORIZONTAL.equals(getGraphParticipantOrientation(XMLUtil.getWorkflowProcess(tra), XMLUtil.getActivitySet(tra)))) {
            offsetx = GraphUtilities.getGraphController().getGraphSettings().getLaneNameWidth();
         } else {
            offsety = GraphUtilities.getGraphController().getGraphSettings().getLaneNameWidth();
         }
         for (int i = 0; i < pPos.length; i++) {
            String pos = pPos[i];
            String[] posD = XMLUtil.tokenize(pos, ",");
            try {
               p = new Point(offsetx + Integer.parseInt(posD[0]), offsety + Integer.parseInt(posD[1]));
               breakPoints.add(p);
            } catch (Exception ex) {
            }
         }
         ((ExtendedAttributes) ea.getParent()).remove(ea);
      }
      return breakPoints;
   }

   protected static ConnectorGraphicsInfo updateConnectorGraphicsInfoFromAnyOtherVendor(XMLCollectionElement tra) {
      Iterator it = ((ConnectorGraphicsInfos) tra.get("ConnectorGraphicsInfos")).toElements().iterator();
      ConnectorGraphicsInfo cgi = null;
      while (it.hasNext()) {
         cgi = (ConnectorGraphicsInfo) it.next();
         break;
      }
      ConnectorGraphicsInfo cgin = null;
      if (cgi != null) {
         cgin = createConnectorGraphicsInfo(tra, new ArrayList(), GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_SPLINE, true);
         Coordinatess cs = cgin.getCoordinatess();
         List bps = cgi.getCoordinatess().toElements();
         if (bps.size() >= 3) {
            for (int i = 1; i < bps.size() - 1; i++) {
               Coordinates c = (Coordinates) bps.get(i);
               String x = c.getXCoordinate();
               String y = c.getYCoordinate();
               if (!((x.equals("0") && y.equals("0")) || (x.equals("0.0") && y.equals("0.0")))) {
                  Coordinates cn = (Coordinates) cs.generateNewElement();
                  cn.setXCoordinate(c.getXCoordinate());
                  cn.setYCoordinate(c.getYCoordinate());
                  cs.add(cn);
               }
            }
         }
         if (!cgi.getFillColor().equals("")) {
            Color c = Utils.getColor(cgi.getFillColor());
            cgin.setFillColor(Utils.getColorString(c));
         }
      }
      return cgin;
   }

   // ----------------------------------------------------------------------------------------------

   /**
    * Returns the sorted set of participants for given object. The object can be activity set or workflow process.
    */
   public static List gatherParticipants(XMLCollectionElement wpOrAs) {
      List ownedActivities = ((Activities) wpOrAs.get("Activities")).toElements();
      List gatherInto = new ArrayList();

      List vorder = GraphUtilities.getParticipantVisualOrder(wpOrAs);
      // System.out.println("VORDER for " + wpOrAs.getId() + "=" + vorder + ", size=" +
      // vorder.size());
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(wpOrAs);

      // gathering participants in ordered way defined by ext. attrib
      if (vorder.size() > 0) {
         ParticipantInfo dpInfo = null;
         for (int i = 0; i < vorder.size(); i++) {
            String pId = (String) vorder.get(i);
            // System.err.println("Gathering for  par id "+pId);
            Participant p = XMLUtil.findParticipant(JaWEManager.getInstance().getXPDLHandler(), wp, pId);
            if (p == null) {
               if (CommonExpressionParticipants.getInstance().isCommonExpressionParticipantId(pId)) {
                  pId = CommonExpressionParticipants.getInstance().getIdFromVisualOrderEA(pId);
                  p = CommonExpressionParticipants.getInstance().getCommonExpressionParticipant(wpOrAs, pId);
               }
            }
            if (p != null) {
               ParticipantInfo pi = new ParticipantInfo(p);
               List afp = GraphUtilities.getAllActivitiesForParticipantId(ownedActivities, pId);
               pi.setActivities(afp);
               ownedActivities.removeAll(afp);
               // System.err.println("Gathered par "+pi);
               gatherInto.add(pi);
            } else {
               if (dpInfo == null) {
                  dpInfo = new ParticipantInfo(FreeTextExpressionParticipant.getInstance());
                  // System.err.println("Gathered par "+dpInfo);
                  gatherInto.add(dpInfo);
               }
            }
         }
         if (dpInfo != null) {
            dpInfo.setActivities(ownedActivities);
         }
      }
      // CommonExpressionParticipants.getInstance().printList(wpOrAs);
      // System.err.println("all gathered participants for "+wpOrAs.getId()+" are:"+gatherInto);
      return gatherInto;
   }

   public static List getAllActivitiesForParticipantId(Collection acts, String pId) {
      List pacts = new ArrayList();
      Iterator it = acts.iterator();
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         String parid = GraphUtilities.getParticipantId(act);
         if (pId.equals(parid)) {
            pacts.add(act);
         }
      }
      return pacts;
   }

   /**
    * Returns the sorted set of participants for given object. The object can be activity set or workflow process.
    */
   public static List gatherLanes(XMLCollectionElement wpOrAs) {
      List ownedActivitiesAndArtifacts = new ArrayList(((Activities) wpOrAs.get("Activities")).toElements());
      ownedActivitiesAndArtifacts.addAll(XMLUtil.getPackage(wpOrAs).getArtifacts().toElements());
      List gatherInto = new ArrayList();

      List vorder = GraphUtilities.getLaneVisualOrder(wpOrAs);
      // System.out.println("VORDER for " + wpOrAs.getId() + "=" + vorder + ", size=" +
      // vorder.size());
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(wpOrAs);

      // gathering participants in ordered way defined by ext. attrib
      if (vorder.size() > 0) {
         for (int i = 0; i < vorder.size(); i++) {
            String pId = (String) vorder.get(i);
            // System.err.println("Gathering for  par id "+pId);
            Lane p = JaWEManager.getInstance().getXPDLUtils().getPoolForProcessOrActivitySet(wpOrAs).getLanes().getLane(pId);
            LaneInfo pi = new LaneInfo(p);
            List afp = GraphUtilities.getAllActivitiesAndArtifactsForLaneId(ownedActivitiesAndArtifacts, pId);
            pi.setActivitiesAndArtifacts(afp);
            ownedActivitiesAndArtifacts.removeAll(afp);
            // System.err.println("Gathered par "+pi);
            gatherInto.add(pi);
         }
      }
      // CommonExpressionParticipants.getInstance().printList(wpOrAs);
      // System.err.println("all gathered participants for "+wpOrAs.getId()+" are:"+gatherInto);
      return gatherInto;
   }

   public static List getAllActivitiesAndArtifactsForLaneId(Collection actsAndArtifs, String pId) {
      List pacts = new ArrayList();
      Iterator it = actsAndArtifs.iterator();
      while (it.hasNext()) {
         XMLCollectionElement actOrArtif = (XMLCollectionElement) it.next();
         String laneId = JaWEManager.getInstance().getXPDLUtils().getLaneId(actOrArtif);
         if (pId.equals(laneId)) {
            pacts.add(actOrArtif);
         }
      }
      return pacts;
   }

   public static boolean scanExtendedAttributes(Package pkg) {
      // JaWEManager.getInstance().getLoggingManager().debug("Scanning extended attributes for package "
      // + pkg.getId());
      Iterator wps = pkg.getWorkflowProcesses().toElements().iterator();
      boolean changed = false;
      while (wps.hasNext()) {
         WorkflowProcess wp = (WorkflowProcess) wps.next();
         changed = GraphUtilities.scanExtendedAttributes(wp) || changed;
      }
      return changed;
   }

   protected static boolean scanExtendedAttributes(WorkflowProcess wp) {
      // JaWEManager.getInstance().getLoggingManager().debug(
      // "Scanning extended attributes for workflow process " + wp.getId());
      boolean changed = false;
      boolean doMigration = false;

      Pool p = JaWEManager.getInstance().getXPDLUtils().getPoolForProcess(wp);
      if (p == null) {
         p = JaWEManager.getInstance().getXPDLUtils().createPoolForProcess(wp);
         createNodeGraphicsInfo(p, null, null, true);
         p.setOrientation(getGraphParticipantOrientation(wp, (String) null));
         changed = true;
         doMigration = true;
      }
      changed = GraphUtilities.scanExtendedAttributesForWPOrAs(wp, doMigration) || changed;
      Iterator it = wp.getActivitySets().toElements().iterator();
      while (it.hasNext()) {
         ActivitySet as = (ActivitySet) it.next();
         changed = scanExtendedAttributes(as, doMigration) || changed;
      }
      return changed;
   }

   protected static boolean scanExtendedAttributes(ActivitySet as, boolean doMigration) {
      // JaWEManager.getInstance().getLoggingManager()
      // .debug("Scanning extended attributes for activity set " + as.getId());
      Pool p = JaWEManager.getInstance().getXPDLUtils().getPoolForActivitySet(as);
      if (p == null) {
         p = JaWEManager.getInstance().getXPDLUtils().createPoolForActivitySet(as);
         p.setOrientation(getGraphParticipantOrientation(XMLUtil.getWorkflowProcess(as), as.getId()));
         createNodeGraphicsInfo(p, null, null, true);
         doMigration = true;
      }
      return scanExtendedAttributesForWPOrAs(as, doMigration);
   }

   protected static String getNewStyle(Transition tra, String oldStyle) {
      if (oldStyle.equals("SIMPLEROUTING")) {
         return GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_SIMPLE_ROUTING_ORTHOGONAL;
      } else if (oldStyle.equals("NOROUTING") || oldStyle.equals("")) {
         if (tra != null && tra.getFrom().equals(tra.getTo())) {
            return GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_BEZIER;
         }
         return GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_ORTHOGONAL;
      }
      return GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_ORTHOGONAL;
   }

   public static boolean scanExtendedAttributesForWPOrAs(XMLCollectionElement wpOrAs, boolean doMigration) {
      boolean changed = false;
      Participant defaultP = FreeTextExpressionParticipant.getInstance();
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(wpOrAs);
      ExtendedAttributes wpEAs = wp.getExtendedAttributes();
      Activities acts = (Activities) wpOrAs.get("Activities");
      Transitions tras = (Transitions) wpOrAs.get("Transitions");
      List ownedActivities = acts.toElements();

      if (doMigration) {
         int defwidth = GraphUtilities.getGraphController().getGraphSettings().getActivityWidth();
         int defheight = GraphUtilities.getGraphController().getGraphSettings().getActivityHeight();
         // participants required by XPDL model
         List participants = GraphUtilities.getParticipants(acts);

         Map pIdToPar = new HashMap();
         for (int i = 0; i < participants.size(); i++) {
            Participant par = (Participant) participants.get(i);
            pIdToPar.put(par.getId(), par);
         }

         // read visual order e.a. if any, and append participants that are contained
         // there
         boolean newAttrib = true;
         List vo = new ArrayList();
         ExtendedAttribute eavo = GraphUtilities.getParticipantVisualOrderEA(wpOrAs);
         if (eavo == null) {
            newAttrib = false;
         } else {
            vo = GraphUtilities.getParticipantVisualOrder(wpOrAs);
         }

         List toAdd = new ArrayList(vo);
         toAdd.removeAll(pIdToPar.keySet());
         // do not add if appropriate participant exists
         for (int i = 0; i < toAdd.size(); i++) {
            String pId = (String) toAdd.get(i);
            Participant p = XMLUtil.findParticipant(JaWEManager.getInstance().getXPDLHandler(), wp, pId);
            if (p != null && !participants.contains(p)) {
               participants.add(p);
               pIdToPar.put(pId, p);
            } else if (CommonExpressionParticipants.getInstance().isCommonExpressionParticipantId(pId)) {
               String pIdForP = CommonExpressionParticipants.getInstance().getIdFromVisualOrderEA(pId);
               CommonExpressionParticipant cep = CommonExpressionParticipants.getInstance().getCommonExpressionParticipant(wpOrAs, pIdForP);
               if (cep == null) {
                  cep = CommonExpressionParticipants.getInstance().generateCommonExpressionParticipant(wpOrAs);
                  cep.setId(pIdForP);
               }
               participants.add(cep);
               pIdToPar.put(pIdForP, cep);
            } else if (defaultP.getId().equals(pId) && !participants.contains(defaultP)) {
               participants.add(defaultP);
               pIdToPar.put(pId, defaultP);

            }
         }

         SequencedHashMap gatherInto = new SequencedHashMap();
         // initial gathering of activities per participant, without considering
         // ext. attribs.
         boolean hasDefaultPerformer = false;
         for (int i = 0; i < participants.size(); i++) {
            Participant p = (Participant) participants.get(i);
            // System.out.println("Processing p: id=" + p.getId() + ", n=" + p.getName());
            if (p == defaultP) {
               hasDefaultPerformer = true;
               continue;
            }
            List afp = GraphUtilities.getActivitiesWithPerformer(ownedActivities, p.getId());
            // System.out.println("Acts for p: id=" + p.getId() + ", n=" + p.getName() +
            // " are: " + afp);
            ownedActivities.removeAll(afp);
            ParticipantInfo pi = new ParticipantInfo(p);
            Iterator it = afp.iterator();
            while (it.hasNext()) {
               Activity act = (Activity) it.next();
               ExtendedAttribute ea = GraphUtilities.getParticipantIdEA(act);
               if (ea != null) {
                  String pId = ea.getVValue();
                  String perf = act.getFirstPerformer();
                  if (!pId.equals(perf)) {
                     if (!pIdToPar.containsKey(perf)) {
                        ea.setVValue(defaultP.getId());
                     } else {
                        ea.setVValue(perf);
                     }
                     changed = true;
                  }
               } else {
                  ea = GraphUtilities.createParticipantIdEA(act, act.getFirstPerformer(), false);
                  ((ExtendedAttributes) ea.getParent()).add(0, ea);
                  changed = true;
               }
            }

            pi.setActivities(afp);
            gatherInto.put(p.getId(), pi);

         }
         if (!hasDefaultPerformer && ownedActivities.size() > 0) {
            hasDefaultPerformer = true;
         }
         // now, for the activities other than NO and TOOL, consider
         // ext. attribs. if any, otherwise add them
         if (hasDefaultPerformer) {
            ParticipantInfo pi = new ParticipantInfo(defaultP);
            pi.setActivities(new ArrayList(ownedActivities));
            gatherInto.put(defaultP.getId(), pi);

            // adding common expression participants
            Set ceps = CommonExpressionParticipants.getInstance().getCommonExpressionParticipants(wpOrAs);
            Iterator it = ceps.iterator();
            while (it.hasNext()) {
               CommonExpressionParticipant cep = (CommonExpressionParticipant) it.next();
               if (!gatherInto.containsKey(cep.getId())) {
                  gatherInto.put(cep.getId(), new ParticipantInfo(cep));
               }
            }
            // read ext attribs to see if route/block/subflow acts are placed
            // somewhere else
            // System.out.println("Further processing acts " + ownedActivities);
            for (int i = 0; i < ownedActivities.size(); i++) {
               Activity act = (Activity) ownedActivities.get(i);
               ExtendedAttributes actEAs = act.getExtendedAttributes();
               // if new JaWE ext. attrib exists:
               ExtendedAttribute ea = GraphUtilities.getParticipantIdEA(act);
               if (ea != null) {
                  String pId = GraphUtilities.getParticipantId(act);
                  ParticipantInfo pinf = (ParticipantInfo) gatherInto.get(pId);
                  if (pinf == null) {
                     ea.setVValue(defaultP.getId());
                     changed = true;
                  } else if (pinf != pi) {
                     pi.removeActivity(act);
                     pinf.addActivity(act);
                  }
               }
               // if there is no e.a, add e.a. for default expression
               // participant
               if (ea == null) {
                  ea = GraphUtilities.createParticipantIdEA(act, defaultP.getId(), false);
                  actEAs.add(0, ea);
                  changed = true;
               }
            }
            // if there are no activities for default participant, and it is not in
            // visual order list remove it
            if (pi.getActivities().size() == 0 && !vo.contains(defaultP.getId())) {
               gatherInto.remove(defaultP.getId());
            }
         }

         // read visual order e.a. if any
         if (eavo != null) {
            List newVO = new ArrayList();
            Iterator it = gatherInto.sequence().iterator();
            while (it.hasNext()) {
               String pId = (String) it.next();
               Participant par = (Participant) pIdToPar.get(pId);
               if (par instanceof CommonExpressionParticipant) {
                  pId = CommonExpressionParticipants.getInstance().getIdForVisualOrderEA(pId);
               }
               newVO.add(pId);
            }
            List addToVo = new ArrayList(newVO);
            addToVo.removeAll(vo);
            List removeFromVo = new ArrayList(vo);
            removeFromVo.removeAll(newVO);
            // do not remove if appropriate participant exists
            it = removeFromVo.iterator();
            while (it.hasNext()) {
               String pId = (String) it.next();
               Participant p = XMLUtil.findParticipant(JaWEManager.getInstance().getXPDLHandler(), wp, pId);
               if (p == null) {
                  p = CommonExpressionParticipants.getInstance().getCommonExpressionParticipant(wpOrAs, pId);
               }
               if (p != null || pId.equals(defaultP.getId())) {
                  it.remove();
               }
            }
            vo.removeAll(removeFromVo);
            vo.addAll(addToVo);
            checkLanes(JaWEManager.getInstance().getXPDLUtils().getPoolForProcessOrActivitySet(wpOrAs), vo);

            if (!newAttrib) {
               if (vo.size() > 0) {
                  eavo = GraphUtilities.createParticipantVisualOrderEA(wpOrAs, GraphUtilities.createParticipantVisualOrderEAVal(wpOrAs, vo), false);
                  wpEAs.add(0, eavo);
                  changed = true;
               }
            } else {
               if (removeFromVo.size() != 0 || addToVo.size() != 0) {
                  String val = GraphUtilities.createParticipantVisualOrderEAVal(wpOrAs, vo);
                  eavo.setVValue(val);
                  changed = true;
               }
            }
         } else {
            eavo = GraphUtilities.createParticipantVisualOrderEA(wpOrAs, GraphUtilities.createParticipantVisualOrderEAVal(wpOrAs, gatherInto.sequence()), false);
            // System.out.println("Created vo attr for " + wpOrAs.getId() + "=" +
            // eavo.getVValue() + ", vosize="+ gatherInto.sequence().size());
            ((ExtendedAttributes) eavo.getParent()).add(0, eavo);
            checkLanes(JaWEManager.getInstance().getXPDLUtils().getPoolForProcessOrActivitySet(wpOrAs), gatherInto.sequence());

            changed = true;
         }

         if (eavo != null) {
            ((ExtendedAttributes) eavo.getParent()).remove(eavo);
            changed = true;
         }

         // activity positions - read e.a. if exist, otherwise perform some kind of
         // layout
         Iterator it = gatherInto.values().iterator();
         while (it.hasNext()) {
            ParticipantInfo pi = (ParticipantInfo) it.next();
            List actsForParticipant = pi.getActivities();
            // System.out.println("Final acts for p:" + pi.getParticipant().getId() +
            // " are "
            // + actsForParticipant);
            int incX = 2 * defwidth;
            int incY = defheight;
            int translateX = 10;
            int translateY = 10;
            double chngDir = (int) Math.sqrt(actsForParticipant.size());
            int cnt = 0;
            for (int i = 0; i < actsForParticipant.size(); i++) {
               Activity act = (Activity) actsForParticipant.get(i);
               String lid = getLaneIdForMigration(act);
               NodeGraphicsInfo ea = JaWEManager.getInstance().getXPDLUtils().getNodeGraphicsInfo(act);
               if (ea == null) {
                  ea = updateNodeGraphicsInfoFromAnyOtherVendor(act, wpOrAs);
                  if (ea != null) {
                     ea.setLaneId(lid);
                     changed = true;
                  }
               }
               if (ea == null) {
                  Point off = GraphUtilities.getOffsetPointOld(act);
                  if (off == null) {
                     cnt++;
                     if ((cnt / chngDir) == ((int) (cnt / chngDir))) {
                        incX = -incX;
                        translateY += incY;
                     } else {
                        translateX += incX;
                     }
                     off = new Point(translateX, translateY);
                  }

                  ea = GraphUtilities.createNodeGraphicsInfo(act, off, lid, false);
                  ((NodeGraphicsInfos) ea.getParent()).add(ea);
                  changed = true;
               }
               ExtendedAttribute pidea = getParticipantIdEA(act);
               if (pidea != null) {
                  ((ExtendedAttributes) pidea.getParent()).remove(pidea);
                  changed = true;
               }
            }
         }

         // handle start/ends
         List sds = GraphUtilities.getStartOrEndDescriptions(wpOrAs, true);
         List eds = GraphUtilities.getStartOrEndDescriptions(wpOrAs, false);

         String asId = null;
         if (wpOrAs instanceof ActivitySet) {
            asId = wpOrAs.getId();
         }
         List bubbles = new ArrayList(sds);
         bubbles.addAll(eds);
         it = bubbles.iterator();
         Set eastoremove = new HashSet();
         while (it.hasNext()) {
            StartEndDescription sed = (StartEndDescription) it.next();
            if (!gatherInto.containsKey(sed.getParticipantId())) {
               eastoremove.addAll(GraphUtilities.getStartOrEndExtendedAttributes(wp,
                                                                                 asId,
                                                                                 sed.getParticipantId(),
                                                                                 GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID));
               // System.err.println("Removing sed1 "+sed.toString()+"\n..... because gatherInto doesn't have key "+sed.getParticipantId());
            }
            if (sed.getActId() != null && wp.getActivity(sed.getActId()) == null) {
               eastoremove.addAll(GraphUtilities.getStartOrEndExtendedAttributes(wp, asId, sed.getActId(), GraphEAConstants.EA_PART_CONNECTING_ACTIVITY_ID));
               // System.err.println("Removing sed2 "+sed.toString());
            }
            if (sed.getActSetId() != null && wp.getActivitySet(sed.getActSetId()) == null) {
               eastoremove.addAll(GraphUtilities.getStartOrEndExtendedAttributes(wp, asId, sed.getActSetId(), GraphEAConstants.EA_PART_ACTIVITY_SET_ID));
               // System.err.println("Removing sed3 "+sed.toString());
            }

            String t = JaWEConstants.ACTIVITY_TYPE_START;
            if (!sed.isStart()) {
               t = JaWEConstants.ACTIVITY_TYPE_END;
            }
            Activity seev = JaWEManager.getInstance().getXPDLObjectFactory().createXPDLObject(acts, t, true);
            createParticipantIdEA(seev, sed.getParticipantId(), true);
            createNodeGraphicsInfo(seev, sed.getOffset(), getLaneIdForMigration(seev), true);
            ExtendedAttribute pidea = getParticipantIdEA(seev);
            if (pidea != null) {
               ((ExtendedAttributes) pidea.getParent()).remove(pidea);
               changed = true;
            }

            Transition tra = JaWEManager.getInstance().getXPDLObjectFactory().createXPDLObject(tras, "", false);
            Activity startingOrEndingAct = null;
            if (sed.isStart()) {
               tra.setFrom(seev.getId());
               String toAct = sed.getActId();
               if (!"".equals(toAct)) {
                  startingOrEndingAct = wp.getActivity(toAct);
                  if (startingOrEndingAct != null) {
                     toAct = getProperActIdForStart(wp, startingOrEndingAct, startingOrEndingAct.getId(), 1);
                  }
               }
               tra.setTo(toAct);
            } else {
               tra.setTo(seev.getId());
               String fromAct = sed.getActId();
               if (!"".equals(fromAct)) {
                  startingOrEndingAct = wp.getActivity(fromAct);
                  if (startingOrEndingAct != null) {
                     fromAct = getProperActIdForEnd(wp, startingOrEndingAct, startingOrEndingAct.getId(), 1);
                  }
               }
               tra.setFrom(fromAct);
            }
            tras.add(tra);
            createConnectorGraphicsInfo(tra, null, sed.getTransitionStyle(), true);
            eastoremove.addAll(getStartOrEndExtendedAttributes(wpOrAs, true));
            eastoremove.addAll(getStartOrEndExtendedAttributes(wpOrAs, false));
            if (startingOrEndingAct != null) {
               XMLUtil.correctSplitAndJoin(startingOrEndingAct);
            }
         }
         if (eastoremove.size() > 0) {
            wpEAs.removeAll(new ArrayList(eastoremove));
            changed = true;
         }

         // handle transitions
         it = tras.toElements().iterator();
         while (it.hasNext()) {
            Transition tra = (Transition) it.next();
            ConnectorGraphicsInfo ea = JaWEManager.getInstance().getXPDLUtils().getConnectorGraphicsInfo(tra, false);
            if (ea == null) {
               ea = updateConnectorGraphicsInfoFromAnyOtherVendor(tra);
               changed = true;
            }
            if (ea == null) {
               String oldStyle = GraphUtilities.getStyleOld(tra);
               ea = GraphUtilities.createConnectorGraphicsInfo(tra, null, oldStyle, false);
               List bps = GraphUtilities.getBreakpointsOld(tra);
               if (bps.size() > 0) {
                  setBreakPointCoordinates(ea, bps);
               }
               ((ConnectorGraphicsInfos) ea.getParent()).add(ea);
               changed = true;
            } else {
               String style = GraphUtilities.getStyle(tra);
               if (!GraphEAConstants.transitionStyles.contains(style)) {
                  ea.setStyle(GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_SPLINE);
                  changed = true;
               }
            }
         }
         // System.err.println("FINAL GPDws="+gatherInto);
         // for the duplicated activities which are the consequence of the migration,
         // adjust the positions
         it = gatherInto.values().iterator();
         while (it.hasNext()) {
            ParticipantInfo pi = (ParticipantInfo) it.next();
            List actsForParticipant = pi.getActivities();
            Map pnt2Act = new HashMap();
            for (int i = 0; i < actsForParticipant.size(); i++) {
               Activity act = (Activity) actsForParticipant.get(i);
               Point p = GraphUtilities.getOffsetPoint(act);
               if (pnt2Act.containsKey(p)) {
                  Activity oa = (Activity) pnt2Act.get(p);
                  Point pr = GraphUtilities.getOffsetPoint(oa);
                  pr.x += defwidth + 30;
                  pr.y += defheight + 15;
                  GraphUtilities.setOffsetPoint(act, pr, JaWEManager.getInstance().getXPDLUtils().getLaneId(act));
                  changed = true;
               }
               pnt2Act.put(p, act);
            }
         }

      } else {
         Pool p = JaWEManager.getInstance().getXPDLUtils().getPoolForProcessOrActivitySet(wpOrAs);
         Iterator it = acts.toElements().iterator();
         while (it.hasNext()) {
            Activity act = (Activity) it.next();
            NodeGraphicsInfo ea = JaWEManager.getInstance().getXPDLUtils().getNodeGraphicsInfo(act);
            if (ea == null) {
               ea = updateNodeGraphicsInfoFromAnyOtherVendor(act, wpOrAs);
               if (ea == null || ea.getLaneId().equals("")) {
                  String lId = null;
                  if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_NO || act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_TASK_APPLICATION) {
                     String perf = act.getFirstPerformer();
                     Lane l = getLaneForPerformer(p, perf);
                     if (l == null) {
                        l = getDefaultLane(p);
                        if (l == null) {
                           l = createDefaultLane(p);
                        }
                     }
                     lId = l.getId();
                  } else {
                     if (p.getLanes().size() > 0) {
                        lId = ((Lane) p.getLanes().get(0)).getId();
                     } else {
                        Lane l = createDefaultLane(p);
                        lId = l.getId();
                     }
                  }
                  if (ea == null) {
                     ea = createNodeGraphicsInfo(act, new Point(10 + (int) (300 * Math.random()), (int) (10 + 200 * Math.random())), lId, true);
                  } else {
                     ea.setLaneId(lId);
                  }
               }
               changed = true;
            }
         }
         it = tras.toElements().iterator();
         while (it.hasNext()) {
            Transition tra = (Transition) it.next();
            ConnectorGraphicsInfo ea = JaWEManager.getInstance().getXPDLUtils().getConnectorGraphicsInfo(tra, false);
            if (ea == null) {
               ea = updateConnectorGraphicsInfoFromAnyOtherVendor(tra);
               if (ea == null) {
                  ea = createConnectorGraphicsInfo(tra, null, GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_SPLINE, true);
               }
               changed = true;
            }
         }

         it = XMLUtil.getPackage(wpOrAs).getArtifacts().toElements().iterator();
         while (it.hasNext()) {
            Artifact art = (Artifact) it.next();
            NodeGraphicsInfo ea = JaWEManager.getInstance().getXPDLUtils().getNodeGraphicsInfo(art);
            if (ea == null) {
               ea = updateNodeGraphicsInfoFromAnyOtherVendor(art, wpOrAs);
               if (ea == null || ea.getLaneId().equals("")) {
                  List refs = XMLUtil.getArtifactReferences(XMLUtil.getPackage(p), art.getId());
                  String lId = null;
                  if (refs.size() > 0) {
                     for (int i = 0; i < refs.size(); i++) {
                        Association assoc = XMLUtil.getAssociation((XMLElement) refs.get(i));
                        String actId = assoc.getSource().equals(art.getId()) ? assoc.getTarget() : assoc.getSource();
                        Activity act = ((Activities) wpOrAs.get("Activities")).getActivity(actId);
                        if (act != null) {
                           lId = JaWEManager.getInstance().getXPDLUtils().getNodeGraphicsInfo(act).getLaneId();
                           break;
                        }
                     }
                  }
                  if (lId != null) {
                     if (ea == null) {
                        ea = createNodeGraphicsInfo(art, new Point(10 + (int) (300 * Math.random()), (int) (10 + 200 * Math.random())), lId, true);
                     } else {
                        ea.setLaneId(lId);
                     }
                     changed = true;
                  }
               } else {
                  changed = true;
               }
            }
         }

         it = XMLUtil.getPackage(wpOrAs).getAssociations().toElements().iterator();
         while (it.hasNext()) {
            Association ass = (Association) it.next();
            ConnectorGraphicsInfo ea = JaWEManager.getInstance().getXPDLUtils().getConnectorGraphicsInfo(ass, false);
            if (ea == null) {
               ea = updateConnectorGraphicsInfoFromAnyOtherVendor(ass);
               if (ea == null) {
                  ea = createConnectorGraphicsInfo(ass, null, GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_SPLINE, true);
               }
               changed = true;
            }
         }

         NodeGraphicsInfo ea = JaWEManager.getInstance().getXPDLUtils().getNodeGraphicsInfo(p);
         if (ea == null) {
            ea = updateNodeGraphicsInfoFromAnyOtherVendor(p, null);
            if (ea == null) {
               ea = createNodeGraphicsInfo(p, null, null, true);
            }
            changed = true;
         }
         it = p.getLanes().toElements().iterator();
         while (it.hasNext()) {
            Lane l = (Lane) it.next();
            ea = JaWEManager.getInstance().getXPDLUtils().getNodeGraphicsInfo(l);
            if (ea == null) {
               ea = updateNodeGraphicsInfoFromAnyOtherVendor(l, null);
               if (ea == null) {
                  ea = createNodeGraphicsInfo(l, null, null, true);
               }
               changed = true;
            }
         }

      }
      return changed;
   }

   protected static String getProperActIdForEnd(WorkflowProcess wp, Activity startingAct, String firstActId, int cnt) {
      if (cnt > 1 && startingAct.getId().equals(firstActId)) {
         return firstActId;
      }
      Set tras = XMLUtil.getNonExceptionalOutgoingTransitions(startingAct);
      if (tras.size() == 1) {
         String actTo = ((Transition) tras.toArray()[0]).getTo();
         Activity nextAct = wp.getActivity(actTo);
         if (nextAct != null && !nextAct.getId().equals(startingAct.getId())) {
            return getProperActIdForEnd(wp, nextAct, firstActId, ++cnt);
         }
      }
      return startingAct.getId();
   }

   protected static String getProperActIdForStart(WorkflowProcess wp, Activity startingAct, String firstActId, int cnt) {
      if (cnt > 1 && startingAct.getId().equals(firstActId)) {
         return firstActId;
      }
      Set tras = XMLUtil.getIncomingTransitions(startingAct);
      if (tras.size() == 1) {
         String actFrom = ((Transition) tras.toArray()[0]).getFrom();
         Activity prevAct = wp.getActivity(actFrom);
         if (prevAct != null && !prevAct.getId().equals(startingAct.getId())) {
            return getProperActIdForStart(wp, prevAct, firstActId, ++cnt);
         }
      }
      return startingAct.getId();
   }

   protected static String getLaneIdForMigration(Activity act) {
      String perf = act.getFirstPerformer();
      if (perf.equals("")) {
         perf = getParticipantId(act);
      }
      XMLCollectionElement wpOrAs = XMLUtil.getActivitySetOrWorkflowProcess(act);
      Pool p = JaWEManager.getInstance().getXPDLUtils().getPoolForProcessOrActivitySet(wpOrAs);
      Lane l = getLaneForPerformer(p, perf);
      if (l == null) {
         l = getDefaultLane(p);
         if (l == null) {
            l = createDefaultLane(p);
         }
      }
      return l.getId();
   }

   protected static List getParticipants(Activities acts) {
      List pars = new ArrayList();

      List performers = GraphUtilities.getAllPossiblePerformers(acts);

      WorkflowProcess wp = XMLUtil.getWorkflowProcess(acts);

      Participant defaultP = FreeTextExpressionParticipant.getInstance();
      for (int i = 0; i < performers.size(); i++) {
         String perf = (String) performers.get(i);
         Participant p = XMLUtil.findParticipant(JaWEManager.getInstance().getXPDLHandler(), wp, perf);
         if (p == null) {
            p = defaultP;
         }
         if (!pars.contains(p)) {
            pars.add(p);
         }
      }

      // if (pars.size()==0 && acts.size()>0) {
      // pars.add(defaultP);
      // }

      return pars;
   }

   protected static List getAllPossiblePerformers(Activities acts) {
      List pps = new ArrayList();

      List types = new ArrayList();
      types.add(new Integer(XPDLConstants.ACTIVITY_TYPE_NO));
      types.add(new Integer(XPDLConstants.ACTIVITY_TYPE_TASK_APPLICATION));
      types.add(new Integer(XPDLConstants.ACTIVITY_TYPE_TASK_SCRIPT));

      Iterator it = JaWEManager.getInstance().getXPDLUtils().getActivities(acts, types).iterator();
      while (it.hasNext()) {
         String perf = ((Activity) it.next()).getFirstPerformer();
         if (!perf.equals("")) {
            if (!pps.contains(perf)) {
               pps.add(perf);
            }
         }
      }

      return pps;
   }

   public static boolean isMyKindOfExtendedAttribute(ExtendedAttribute ea) {
      boolean isMK = false;
      ExtendedAttributes eas = (ExtendedAttributes) ea.getParent();
      String eaname = ea.getName();
      if (eas.getParent() instanceof WorkflowProcess
          && (eaname.equals(GraphEAConstants.EA_JAWE_GRAPH_WORKFLOW_PARTICIPANT_ORIENTATION)
              || eaname.equals(GraphEAConstants.EA_JAWE_GRAPH_BLOCK_PARTICIPANT_ORIENTATION)
              || eaname.equals(GraphEAConstants.EA_JAWE_GRAPH_WORKFLOW_PARTICIPANT_ORDER)
              || eaname.equals(GraphEAConstants.EA_JAWE_GRAPH_BLOCK_PARTICIPANT_ORDER) || eaname.equals(GraphEAConstants.EA_JAWE_GRAPH_END_OF_WORKFLOW)
              || eaname.equals(GraphEAConstants.EA_JAWE_GRAPH_START_OF_WORKFLOW) || eaname.equals(GraphEAConstants.EA_JAWE_GRAPH_END_OF_BLOCK) || eaname.equals(GraphEAConstants.EA_JAWE_GRAPH_START_OF_BLOCK))) {
         isMK = true;
      } else if (eas.getParent() instanceof Activity
                 && (eaname.equals(GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID) || eaname.equals(GraphEAConstants.EA_JAWE_GRAPH_OFFSET))) {
         isMK = true;
      } else if (eas.getParent() instanceof Transition
                 && (eaname.equals(GraphEAConstants.EA_JAWE_GRAPH_BREAK_POINTS) || eaname.equals(GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE))) {
         isMK = true;
      }

      return isMK;
   }

   // MUST NOT CHANGE ANYTHING ON THE XPDL MODEL - JUST PERFORM GRAPH CHANGES
   public static void adjustPackageOnUndoOrRedoEvent(List allInfo, boolean undo) {
      Package pkg = JaWEManager.getInstance().getJaWEController().getMainPackage();
      GraphController graphController = GraphUtilities.getGraphController();

      Pool rpool = GraphUtilities.getRotatedGraphObject(allInfo);
      if (rpool != null) {
         XMLCollectionElement wpOrAs = JaWEManager.getInstance().getXPDLUtils().getProcessForPool(rpool);
         if (wpOrAs == null) {
            wpOrAs = JaWEManager.getInstance().getXPDLUtils().getActivitySetForPool(rpool);
         }
         if (wpOrAs != null) {
            Graph g = graphController.getGraph(wpOrAs);
            Object[] elem = JaWEGraphModel.getAll(g.getModel());
            g.getModel().remove(elem);

            g.getGraphManager().createWorkflowGraph(g.getXPDLObject());
            return;
         }
      }
      Set insertedProcesses = GraphUtilities.getInsertedOrRemovedWorkflowProcesses(allInfo, true);
      Set insertedActivitySets = GraphUtilities.getInsertedOrRemovedActivitySets(allInfo, true);
      Set removedProcesses = GraphUtilities.getInsertedOrRemovedWorkflowProcesses(allInfo, false);
      Set removedActivitySets = GraphUtilities.getInsertedOrRemovedActivitySets(allInfo, false);

      // LoggingManager lm = JaWEManager.getInstance().getLoggingManager();
      // lm.debug("GraphUtilities -> adjusting pkg " + pkg.getId() +
      // " on undo/redo event");
      // lm.debug("    Inserted processes: " + insertedProcesses);
      // lm.debug("    Removed processes: " + removedProcesses);
      // lm.debug("    Inserted activity sets: " + insertedActivitySets);
      // lm.debug("    Removed activity sets: " + removedActivitySets);

      // remove removed processes and their activity sets
      Iterator it = removedProcesses.iterator();
      while (it.hasNext()) {
         WorkflowProcess wp = (WorkflowProcess) it.next();
         graphController.removeGraph(wp);
         Iterator asi = wp.getActivitySets().toElements().iterator();
         while (asi.hasNext()) {
            ActivitySet as = (ActivitySet) asi.next();
            graphController.removeGraph(as);
         }
      }

      // remove removed activity sets
      it = removedActivitySets.iterator();
      while (it.hasNext()) {
         ActivitySet as = (ActivitySet) it.next();
         graphController.removeGraph(as);
      }

      // insert added processes and their activity sets
      it = insertedProcesses.iterator();
      while (it.hasNext()) {
         WorkflowProcess wp = (WorkflowProcess) it.next();
         graphController.createGraph(wp);
         Iterator asi = wp.getActivitySets().toElements().iterator();
         while (asi.hasNext()) {
            ActivitySet as = (ActivitySet) asi.next();
            graphController.createGraph(as);
         }
      }

      // update other processes and activity sets
      it = pkg.getWorkflowProcesses().toElements().iterator();
      while (it.hasNext()) {
         WorkflowProcess wp = (WorkflowProcess) it.next();

         if (insertedProcesses.contains(wp))
            continue;

         GraphUtilities.adjustWorkflowProcessOrActivitySetOnUndoOrRedoEvent(allInfo, wp, undo);
         Iterator asi = wp.getActivitySets().toElements().iterator();
         while (asi.hasNext()) {
            ActivitySet as = (ActivitySet) asi.next();

            if (insertedActivitySets.contains(as))
               continue;

            GraphUtilities.adjustWorkflowProcessOrActivitySetOnUndoOrRedoEvent(allInfo, as, undo);
         }
      }

      // insert added activity sets
      it = insertedActivitySets.iterator();
      while (it.hasNext()) {
         ActivitySet as = (ActivitySet) it.next();
         graphController.createGraph(as);
      }

   }

   // MUST NOT CHANGE ANYTHING ON THE XPDL MODEL - JUST PERFORM GRAPH CHANGES
   public static void adjustWorkflowProcessOrActivitySetOnUndoOrRedoEvent(List allInfo, XMLCollectionElement wpOrAs, boolean undo) {
      Graph graph = GraphUtilities.getGraphController().getGraph(wpOrAs);

      WorkflowProcess wp = XMLUtil.getWorkflowProcess(wpOrAs);

      GraphManager gmgr = graph.getGraphManager();

      // org.enhydra.jawe.base.logger.LoggingManager lm =
      // JaWEManager.getInstance().getLoggingManager();
      // lm.debug("GraphUtilities->adjusting wp or as " + wpOrAs.getId());
      // lm.debug("    Activities to update graph position: " + graphUpdPosActs);
      // lm.debug("    Inserted activities: " + insertedActivities);
      // lm.debug("    Removed activities: " + removedActs);
      // lm.debug("    Inserted transitions: " + insertedTrans);
      // lm.debug("    Updated transitions: " + updatedTrans);
      // lm.debug("    Removed transitions: " + removedTrans);
      // lm.debug("    PVO start: " + vo);

      List currentPars = GraphUtilities.getLanesInVisualOrder(wpOrAs);

      // Map pkgParsWithChangedIds =
      // GraphUtilities.getPackageParticipantsWithChangedId(allInfo);
      // Map wpParsWithChangedIds =
      // GraphUtilities.getWorkflowProcessParticipantsWithChangedId(allInfo,
      // XMLUtil.getWorkflowProcess(wpOrAs));
      // for (int i = 0; i < currentPars.size(); i++) {
      // Participant par = (Participant) currentPars.get(i);
      // String pId = par.getId();
      // if (pkgParsWithChangedIds.containsKey(pId)
      // || wpParsWithChangedIds.containsKey(pId)) {
      // reloadGraph(graph);
      // return;
      // }
      // }
      // if (reloadGraphIfNeccessary(graph)) {
      // return;
      // }

      Set participantsToRemoveFromGraph = new HashSet();

      Set insertedActivities = GraphUtilities.getInsertedOrRemovedActivitiesAndArtifacts(allInfo, wpOrAs, true, !undo);
      Set removedActs = GraphUtilities.getInsertedOrRemovedActivitiesAndArtifacts(allInfo, wpOrAs, false, !undo);
      updateJGraphForActivityAndArtifactBounds(graph, allInfo, wpOrAs);
      Set graphUpdPosActs = GraphUtilities.getActivitiesAndArtifactsWithChangedOffset(allInfo, wpOrAs);
      graphUpdPosActs.addAll(GraphUtilities.getActivitiesAndArtifactsWithChangedLaneId(allInfo, wpOrAs));
      graphUpdPosActs.removeAll(removedActs);
      Set insertedTrans = GraphUtilities.getInsertedOrRemovedTransitionsAndAssociations(allInfo, wpOrAs, true);
      Set removedTrans = GraphUtilities.getInsertedOrRemovedTransitionsAndAssociations(allInfo, wpOrAs, false);
      Set updatedTrans = GraphUtilities.getUpdatedTransitionsAndAssociations(allInfo, wpOrAs);
      updatedTrans.addAll(GraphUtilities.getTransitionsWithChangedBreakpointsOrStyle(allInfo, wpOrAs));
      updatedTrans.removeAll(removedTrans);

      List partsInGraph = new ArrayList();
      List allGraphParticipants = JaWEGraphModel.getAllParticipantsInModel(graph.getModel());
      if (allGraphParticipants != null) {
         Iterator it = allGraphParticipants.iterator();
         while (it.hasNext()) {
            GraphSwimlaneInterface gpar = (GraphSwimlaneInterface) it.next();
            if (gpar.getUserObject() instanceof Lane) {
               partsInGraph.add(gpar.getUserObject());
            }
         }
         // System.err.println("PING="+partsInGraph);
      }

      // get missing participants
      Set participantsToInsertIntoGraph = new HashSet(currentPars);
      participantsToInsertIntoGraph.removeAll(partsInGraph);
      participantsToRemoveFromGraph.addAll(partsInGraph);
      participantsToRemoveFromGraph.removeAll(currentPars);
      Map participantsToReplace = new HashMap();
      Iterator itp = participantsToRemoveFromGraph.iterator();
      while (itp.hasNext()) {
         Lane p = (Lane) itp.next();
         GraphSwimlaneInterface gpar = gmgr.getGraphParticipant(p);
         Set chas = gpar.getChildActivitiesAndArtifacts();
         if (chas != null && chas.size() > 0) {
            Iterator ita = chas.iterator();
            while (ita.hasNext()) {
               WorkflowElement ga = (WorkflowElement) ita.next();
               if (!removedActs.contains(ga.getPropertyObject()) && !graphUpdPosActs.contains(ga.getPropertyObject())) {
                  String pId = JaWEManager.getInstance().getXPDLUtils().getLaneId((XMLCollectionElement) ga.getPropertyObject());
                  Lane toRep = getLane(wpOrAs, pId);
                  if (toRep != null) {
                     participantsToReplace.put(p, toRep);
                  }
               }

            }
         }
      }
      // System.err.println("PTR="+participantsToReplace);
      Iterator itm = participantsToReplace.entrySet().iterator();
      while (itm.hasNext()) {
         Map.Entry me = (Map.Entry) itm.next();
         Lane pold = (Lane) me.getKey();
         Lane pnew = (Lane) me.getValue();
         GraphSwimlaneInterface gpar = gmgr.getGraphParticipant(pold);
         gpar.setUserObject(pnew);
         participantsToInsertIntoGraph.remove(pnew);
         participantsToRemoveFromGraph.remove(pold);
      }

      // lm.debug("    Participants to insert into graph: " +
      // participantsToInsertIntoGraph);
      // lm.debug("    Pars to remove from graph: " + participantsToRemoveFromGraph);

      // Insert graph participants
      List pti = new ArrayList(currentPars);
      pti.retainAll(participantsToInsertIntoGraph);
      Iterator it = pti.iterator();
      while (it.hasNext()) {
         Lane par = (Lane) it.next();
         gmgr.insertParticipantAndArrangeParticipants(par, null);
      }

      // remove transitions that are not longer present
      it = removedTrans.iterator();
      while (it.hasNext()) {
         XMLCollectionElement tra = (XMLCollectionElement) it.next();
         gmgr.removeTransitionOrAssociation(tra);
      }

      // remove activities that are not longer present
      it = removedActs.iterator();
      while (it.hasNext()) {
         XMLCollectionElement act = (XMLCollectionElement) it.next();
         gmgr.removeActivityOrArtifact(act);
      }

      // insert new activities
      it = insertedActivities.iterator();
      while (it.hasNext()) {
         XMLCollectionElement act = (XMLCollectionElement) it.next();
         gmgr.insertActivityOrArtifact(act);
      }

      // insert new transitions
      it = insertedTrans.iterator();
      while (it.hasNext()) {
         XMLCollectionElement tra = (XMLCollectionElement) it.next();
         gmgr.insertTransitionOrAssociation(tra);
      }

      // adjusted activity position
      // lm.debug("    Activities to update position: " + graphUpdPosActs);
      it = graphUpdPosActs.iterator();
      while (it.hasNext()) {
         XMLCollectionElement act = (XMLCollectionElement) it.next();
         gmgr.arrangeActivityOrArtifactPosition(act);
      }

      // update transitions that changed source or target
      it = updatedTrans.iterator();
      while (it.hasNext()) {
         XMLCollectionElement tra = (XMLCollectionElement) it.next();
         gmgr.updateTransitionOrAssociation(tra);
      }

      // remove participants that are not longer present
      if (participantsToRemoveFromGraph.size() > 0) {
         List gparstorem = new ArrayList();
         it = participantsToRemoveFromGraph.iterator();
         while (it.hasNext()) {
            GraphSwimlaneInterface gpar = gmgr.getGraphParticipant((Lane) it.next());
            gparstorem.add(gpar);
         }
         gmgr.removeCellsAndArrangeParticipants(gparstorem.toArray());
      }

      ParentMap parentMap = new JaWEParentMap();
      Map propertyMap = new HashMap();
      // TODO: sort graph participants
      allGraphParticipants = JaWEGraphModel.getAllParticipantsInModel(graph.getModel());
      if (allGraphParticipants != null) {
         allGraphParticipants.remove(graph.getGraphManager().getGraphParticipant(JaWEManager.getInstance()
            .getXPDLUtils()
            .getPoolForProcessOrActivitySet(wpOrAs)));
         GraphParticipantComparator gpc = new GraphParticipantComparator(gmgr);
         Collections.sort(allGraphParticipants, gpc);

         Set lanes = getLanesWithChangedOrder(allInfo);
         Set nestedlanes = getNestedLanesWithChangedOrder(allInfo);
         List helper = new ArrayList();
         List helper2 = new ArrayList();
         it = lanes.iterator();
         while (it.hasNext()) {
            Lane l = (Lane) it.next();
            GraphSwimlaneInterface gpi = graph.getGraphManager().getGraphParticipant(l);
            if (gpi != null) {
               if (gpi.getParent() instanceof GraphSwimlaneInterface && ((GraphSwimlaneInterface) gpi.getParent()).getPropertyObject() instanceof Pool) {
                  helper.add(gpi);
               }
            } else {
               System.out.println("Can't find GPI for lane " + l);
            }
         }
         it = nestedlanes.iterator();
         while (it.hasNext()) {
            Lane l = (Lane) it.next();
            GraphSwimlaneInterface gpi = graph.getGraphManager().getGraphParticipant(l);
            if (gpi != null) {
               helper2.add(gpi);
            } else {
               System.out.println("Can't find GPI for lane " + l);
            }
         }
         boolean updated = false;
         // System.out.println("CPARS=" + currentPars);
         // List agp = new ArrayList(allGraphParticipants);
         // for (int i = 0; i < agp.size(); i++) {
         // System.out.println("GPARS="+allGraphParticipants);
         // GraphSwimlaneInterface gpar = (GraphSwimlaneInterface)agp.get(i);
         // int realInd = currentPars.indexOf(gpar.getPropertyObject());
         // int currentPos = allGraphParticipants.indexOf(gpar);
         // if (realInd != currentPos) {
         // int diff = realInd - currentPos;
         // System.out.println("Repositioning participant "+gpar+" for "+diff+",oi="+currentPos+", ni="+realInd);
         // for (int j = 0; j < Math.abs(diff); j++) {
         // // updated = gmgr.moveParticipant(gpar, (diff < 0), propertyMap, parentMap)
         // // || updated;
         // updated = gmgr.moveParticipant(gpar, (diff < 0), propertyMap, parentMap)
         // || updated;
         // }
         // allGraphParticipants.remove(currentPos);
         // allGraphParticipants.add(realInd, gpar);
         // // break;
         // }
         // }

         for (int i = 0; i < currentPars.size(); i++) {
            // System.out.println("GPARS=" + allGraphParticipants);
            GraphSwimlaneInterface gpar = gmgr.getGraphParticipant(currentPars.get(i));
            if (!helper.contains(gpar))
               continue;
            int realInd = i;
            int currentPos = allGraphParticipants.indexOf(gpar);
            if (realInd != currentPos) {
               int diff = realInd - currentPos;
               System.out.println("Repositioning participant " + gpar + " for " + diff + ",oi=" + currentPos + ", ni=" + realInd);
               for (int j = 0; j < Math.abs(diff); j++) {
                  updated = gmgr.moveParticipant(gpar, (diff < 0), propertyMap, parentMap) || updated;
               }
               allGraphParticipants.remove(currentPos);
               allGraphParticipants.add(realInd, gpar);
            } else {
               System.out.println("participant " + gpar + ",oi=" + currentPos + ", ni=" + realInd + ", needs no repositioning");

            }
         }
         for (int i = 0; i < currentPars.size(); i++) {
            // System.out.println("GPARS=" + allGraphParticipants);
            GraphSwimlaneInterface gpar = gmgr.getGraphParticipant(currentPars.get(i));
            if (!helper2.contains(gpar))
               continue;
            int realInd = i;
            int currentPos = allGraphParticipants.indexOf(gpar);
            if (realInd != currentPos) {
               int diff = realInd - currentPos;
               System.out.println("Repositioning nested participant " + gpar + " for " + diff + ",oi=" + currentPos + ", ni=" + realInd);
               // for (int j = 0; j < Math.abs(diff); j++) {
               updated = gmgr.moveParticipant(gpar, (diff < 0), propertyMap, parentMap) || updated;
               // }
               allGraphParticipants.remove(currentPos);
               allGraphParticipants.add(realInd, gpar);
            }
         }
         if (updated) {
            gmgr.graphModel().insertAndEdit(null, propertyMap, null, parentMap, null);
         }
      }
      graph.repaint();
   }

   public static void adjustPackageOnUndoableChangeEvent(List allInfo) {
      Package pkg = JaWEManager.getInstance().getJaWEController().getMainPackage();
      GraphController graphController = GraphUtilities.getGraphController();

      Set insertedProcesses = GraphUtilities.getInsertedOrRemovedWorkflowProcesses(allInfo, true);
      Set insertedActivitySets = GraphUtilities.getInsertedOrRemovedActivitySets(allInfo, true);
      Set removedProcesses = GraphUtilities.getInsertedOrRemovedWorkflowProcesses(allInfo, false);
      Set removedActivitySets = GraphUtilities.getInsertedOrRemovedActivitySets(allInfo, false);
      Map activitySetsWithChangedId = GraphUtilities.getActivitySetsWithChangedId(allInfo);

      // LoggingManager lm = JaWEManager.getInstance().getLoggingManager();
      // lm.debug("GraphUtilities->adjusting pkg " + pkg.getId());
      // lm.debug("    Inserted processes: " + insertedProcesses);
      // lm.debug("    Removed processes: " + removedProcesses);
      // lm.debug("    Inserted activity sets: " + insertedActivitySets);
      // lm.debug("    Removed activity sets: " + removedActivitySets);

      // NOTE: order of insertion/removal/updating is VERY IMPORTANT
      // because of activity set related extended attributes
      // that are defined as e.attribs of its process (as does
      // not have e.attribs)

      // remove removed processes and their activity sets
      Iterator it = removedProcesses.iterator();
      while (it.hasNext()) {
         WorkflowProcess wp = (WorkflowProcess) it.next();
         graphController.removeGraph(wp);
         Pool p = JaWEManager.getInstance().getXPDLUtils().getPoolForProcess(wp);
         if (p != null) {
            ((Pools) p.getParent()).remove(p);
         }
         Iterator asi = wp.getActivitySets().toElements().iterator();
         while (asi.hasNext()) {
            ActivitySet as = (ActivitySet) asi.next();
            graphController.removeGraph(as);
            p = JaWEManager.getInstance().getXPDLUtils().getPoolForActivitySet(as);
            if (p != null) {
               ((Pools) p.getParent()).remove(p);
            }
         }
      }

      // remove removed activity sets
      it = removedActivitySets.iterator();
      while (it.hasNext()) {
         ActivitySet as = (ActivitySet) it.next();
         // first remove all extended attributes from activity set's process
         // ExtendedAttributes eas =
         // XMLUtil.getWorkflowProcess(as).getExtendedAttributes();
         // List seds = GraphUtilities.getStartOrEndExtendedAttributes(as, false);
         // seds.addAll(GraphUtilities.getStartOrEndExtendedAttributes(as, true));
         // Iterator sedit = seds.iterator();
         // while (sedit.hasNext()) {
         // ExtendedAttribute ea = (ExtendedAttribute) sedit.next();
         // eas.remove(ea);
         // }
         // eas.remove(GraphUtilities.getParticipantVisualOrderEA(as));
         graphController.removeGraph(as);
      }

      // insert added processes and their activity sets
      it = insertedProcesses.iterator();
      while (it.hasNext()) {
         WorkflowProcess wp = (WorkflowProcess) it.next();
         Pool p = JaWEManager.getInstance().getXPDLUtils().getPoolForProcess(wp);
         if (JaWEManager.getInstance().getXPDLUtils().getNodeGraphicsInfo(p) == null) {
            GraphUtilities.createNodeGraphicsInfo(p, null, null, true);
         }

         // GraphUtilities.scanExtendedAttributesForWPOrAs(wp, false);
         graphController.createGraph(wp);
         Iterator asi = wp.getActivitySets().toElements().iterator();
         while (asi.hasNext()) {
            ActivitySet as = (ActivitySet) asi.next();
            p = JaWEManager.getInstance().getXPDLUtils().getPoolForActivitySet(as);
            if (JaWEManager.getInstance().getXPDLUtils().getNodeGraphicsInfo(p) == null) {
               GraphUtilities.createNodeGraphicsInfo(p, null, null, true);
            }
            // GraphUtilities.scanExtendedAttributesForWPOrAs(as, false);
            graphController.createGraph(as);
         }
      }

      // update other processes and activity sets
      if (insertedProcesses.size() == 0) {
         it = pkg.getWorkflowProcesses().toElements().iterator();
         while (it.hasNext()) {
            WorkflowProcess wp = (WorkflowProcess) it.next();

            // if (insertedProcesses.contains(wp))
            // continue;

            GraphUtilities.adjustWorkflowProcessOrActivitySetOnUndoableChangeEvent(allInfo, wp, null, false);
            if (insertedActivitySets.size() == 0) {
               Iterator asi = wp.getActivitySets().toElements().iterator();
               while (asi.hasNext()) {
                  ActivitySet as = (ActivitySet) asi.next();

                  // if (insertedActivitySets.contains(as))
                  // continue;

                  GraphUtilities.adjustWorkflowProcessOrActivitySetOnUndoableChangeEvent(allInfo, as, null, false);
               }
            }
         }
      }

      // insert added activity sets
      it = insertedActivitySets.iterator();
      while (it.hasNext()) {
         ActivitySet as = (ActivitySet) it.next();
         Pool p = JaWEManager.getInstance().getXPDLUtils().getPoolForActivitySet(as);
         GraphUtilities.createNodeGraphicsInfo(p, null, null, true);
         // GraphUtilities.scanExtendedAttributesForWPOrAs(as, false);
         graphController.createGraph(as);
      }

   }

   public static void adjustWorkflowProcessOrActivitySetOnUndoableChangeEvent(List allInfo, XMLCollectionElement wpOrAs, Map extPkgPars, boolean insertedExtPkg) {
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(wpOrAs);
      Pool pool = JaWEManager.getInstance().getXPDLUtils().getPoolForProcessOrActivitySet(wpOrAs);

      GraphController gc = GraphUtilities.getGraphController();
      Graph graph = gc.getGraph(wpOrAs);
      if (graph == null) {
         System.err.println("can't find graph for wporas " + wpOrAs.getId());
      }
      GraphManager gmgr = graph.getGraphManager();

      boolean reloaded = GraphUtilities.reloadGraphIfNeccessary(graph);
      if (reloaded) {
         return;
      }

      if (GraphUtilities.hasPoolOrientationChanged(allInfo, wpOrAs)) {
         GraphUtilities.rotateProcess(graph);
         return;
      }
      String asId = null;
      if (wpOrAs instanceof ActivitySet) {
         asId = wpOrAs.getId();
      }

      List currentPars = GraphUtilities.getLanesInVisualOrder(wpOrAs);
      List partsInGraph = new ArrayList();
      List allGraphParticipants = JaWEGraphModel.getAllParticipantsInModel(graph.getModel());
      if (allGraphParticipants != null) {
         Iterator it = allGraphParticipants.iterator();
         while (it.hasNext()) {
            GraphSwimlaneInterface gpar = (GraphSwimlaneInterface) it.next();
            if (gpar.getUserObject() instanceof Lane) {
               partsInGraph.add(gpar.getUserObject());
            }
         }
         // System.err.println("PING="+partsInGraph);
      }

      // get missing participants
      Set participantsToInsertIntoGraph = new HashSet(currentPars);
      Set participantsToRemoveFromGraph = new HashSet(partsInGraph);
      participantsToInsertIntoGraph.removeAll(partsInGraph);
      participantsToRemoveFromGraph.removeAll(currentPars);

      Set insertedActivities = GraphUtilities.getInsertedOrRemovedActivitiesAndArtifacts(allInfo, wpOrAs, true, false);
      Set removedActs = GraphUtilities.getInsertedOrRemovedActivitiesAndArtifacts(allInfo, wpOrAs, false, false);
      Map propertyMap = updateJGraphForActivityAndArtifactBounds(graph, allInfo, wpOrAs);
      Set activitiesToUpdatePosition = GraphUtilities.getActivitiesWithChangedPerformer(allInfo, wpOrAs);
      activitiesToUpdatePosition.removeAll(removedActs);
      Set graphUpdPosActs = GraphUtilities.getActivitiesAndArtifactsWithChangedOffset(allInfo, wpOrAs);
      graphUpdPosActs.removeAll(removedActs);

      Set insertedTrans = GraphUtilities.getInsertedOrRemovedTransitionsAndAssociations(allInfo, wpOrAs, true);
      Set removedTrans = GraphUtilities.getInsertedOrRemovedTransitionsAndAssociations(allInfo, wpOrAs, false);
      Set updatedTrans = GraphUtilities.getUpdatedTransitionsAndAssociations(allInfo, wpOrAs);
      updatedTrans.removeAll(removedTrans);

      List vo = GraphUtilities.getLaneVisualOrder(wpOrAs);

      // org.enhydra.jawe.base.logger.LoggingManager lm =
      // JaWEManager.getInstance().getLoggingManager();
      // lm.debug("GraphUtilities->adjusting wp or as " + wpOrAs.getId());
      // lm.debug("    Activities with changed performer: " + activitiesToUpdatePosition);
      // lm.debug("    Activities with changed id: " + activitiesWithChangedId);
      // lm.debug("    Activities to update position via graph: " + graphUpdPosActs);
      // lm.debug("    Inserted activities: " + insertedActivities);
      // lm.debug("    Removed activities: " + removedActs);
      // lm.debug("    Inserted transitions: " + insertedTrans);
      // lm.debug("    Updated transitions: " + updatedTrans);
      // lm.debug("    Removed transitions: " + removedTrans);
      // lm.debug("    PVO start: " + vo);

      // lm.debug("    PVO 2: " + newVo);

      boolean pasteInProgress = JaWEManager.getInstance().getJaWEController().getEdit().isPasteInProgress();
      CopyOrCutInfo cci = gc.getCopyOrCutInfo();
      boolean graphPasteInProgress = pasteInProgress && cci != null && (insertedActivities.size() > 0 || insertedTrans.size() > 0);

      if (graphPasteInProgress && getGraphController().getSelectedGraph().getXPDLObject() != wpOrAs) {
         return;
      }
      // get all participants required by the model, and check against visual order
      List acts = ((Activities) wpOrAs.get("Activities")).toElements();
      List actsToMove = new ArrayList();
      for (int i = 0; i < acts.size(); i++) {
         Activity act = (Activity) acts.get(i);
         Lane lfp = null;
         if (activitiesToUpdatePosition.contains(act)) {
            lfp = getLaneForPerformer(pool, act.getFirstPerformer());
         } else {
            String lId = JaWEManager.getInstance().getXPDLUtils().getLaneId(act);
            lfp = getLane(wpOrAs, lId);
         }
         if (lfp == null) {
            if (!graphPasteInProgress) {
               actsToMove.add(act);
               lfp = getDefaultLane(pool);
               if (lfp == null) {
                  lfp = createDefaultLane(pool);
               }
               if (!vo.contains(lfp.getId())) {
                  participantsToInsertIntoGraph.add(lfp);
                  vo.add(lfp.getId());
               }
               setLaneId(act, lfp.getId());
            }
         } else {
            setLaneId(act, lfp.getId());
            boolean isInGraph = graph.getGraphManager().getGraphParticipant(lfp) != null;
            if (!isInGraph) {
               participantsToInsertIntoGraph.add(lfp);
               actsToMove.add(act);
            } else {
               GraphActivityInterface ga = graph.getGraphManager().getGraphActivity(act);
               if (ga != null) {
                  Lane inGraph = (Lane) ((GraphSwimlaneInterface) ga.getParent()).getUserObject();
                  if (inGraph != lfp) {
                     actsToMove.add(act);
                  }
               }
            }
         }
      }
      // System.err.println("ACTSTOMOVE="+actsToMove);
      activitiesToUpdatePosition.addAll(actsToMove);
      actsToMove.removeAll(insertedActivities);
      // lm.debug("    PVO 3: " + newVo);
      // lm.debug("    Activities with changed performer end: " +
      // activitiesToUpdatePosition);

      // adjust newly inserted activities e.attribs, and activities with updated performer

      Iterator it = insertedActivities.iterator();
      while (it.hasNext()) {
         XMLCollectionElement act = (XMLCollectionElement) it.next();
         if (graphPasteInProgress) {
            GraphUtilities.adjustPastedActivityOrArtifact(act, vo, participantsToInsertIntoGraph, cci, gmgr);
         } else {
            GraphUtilities.adjustInsertedOrUpdatedActivityOrArtifact(wpOrAs, act, vo, participantsToInsertIntoGraph);
         }
      }
      it = activitiesToUpdatePosition.iterator();
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         GraphUtilities.adjustInsertedOrUpdatedActivityOrArtifact(wpOrAs, act, vo, participantsToInsertIntoGraph);
      }

      // Insert graph participants
      it = participantsToInsertIntoGraph.iterator();
      while (it.hasNext()) {
         Lane par = (Lane) it.next();
         GraphUtilities.createNodeGraphicsInfo(par, null, null, true);
         gmgr.insertParticipantAndArrangeParticipants(par, null);
         // lm.debug("    Inserted new graph participant: " + gpar);
         // Insert newly created activities into graph participant
         List l = GraphUtilities.getAllActivitiesAndArtifactsForLaneId(insertedActivities, par.getId());
         for (int i = 0; i < l.size(); i++) {
            XMLCollectionElement act = (XMLCollectionElement) l.get(i);
            gmgr.insertActivityOrArtifact(act);
         }
         insertedActivities.removeAll(l);
         // Adjust position for repositioned activities
         l = GraphUtilities.getAllActivitiesAndArtifactsForLaneId(activitiesToUpdatePosition, par.getId());
         for (int i = 0; i < l.size(); i++) {
            XMLCollectionElement act = (XMLCollectionElement) l.get(i);
            gmgr.arrangeActivityOrArtifactPosition(act);
         }
         activitiesToUpdatePosition.removeAll(l);
      }

      // insert the rest of new activities (some of them were alredy inserted into newly
      // inserted
      // participants)
      it = insertedActivities.iterator();
      while (it.hasNext()) {
         XMLCollectionElement act = (XMLCollectionElement) it.next();
         gmgr.insertActivityOrArtifact(act);
      }

      // adjust position for the rest of the activities
      it = activitiesToUpdatePosition.iterator();
      while (it.hasNext()) {
         XMLCollectionElement act = (XMLCollectionElement) it.next();
         gmgr.arrangeActivityOrArtifactPosition(act);
      }

      // make ea changes for inserted or updated transitions
      List toUpd = new ArrayList(updatedTrans);
      toUpd.addAll(insertedTrans);
      if (graphPasteInProgress) {
         GraphUtilities.adjustPastedTransitionsOrAssociations(toUpd, cci, gmgr);
      } else {
         GraphUtilities.adjustInsertedOrUpdatedTransitionsOrAssociations(toUpd, gmgr);
      }

      // remove transitions that are not longer present
      it = removedTrans.iterator();
      while (it.hasNext()) {
         XMLCollectionElement tra = (XMLCollectionElement) it.next();
         gmgr.removeTransitionOrAssociation(tra);
      }

      // update transitions that changed source or target
      it = updatedTrans.iterator();
      while (it.hasNext()) {
         XMLCollectionElement tra = (XMLCollectionElement) it.next();
         gmgr.updateTransitionOrAssociation(tra);
      }

      // insert new transitions
      it = insertedTrans.iterator();
      while (it.hasNext()) {
         XMLCollectionElement tra = (XMLCollectionElement) it.next();
         gmgr.insertTransitionOrAssociation(tra);
      }

      // remove activities that are not longer present
      it = removedActs.iterator();
      while (it.hasNext()) {
         XMLCollectionElement act = (XMLCollectionElement) it.next();
         gmgr.removeActivityOrArtifact(act);
      }

      // remove participants that are not longer present
      if (participantsToRemoveFromGraph.size() > 0) {
         Set graphParticipantsToRemoveFromGraph = new HashSet();
         it = participantsToRemoveFromGraph.iterator();
         while (it.hasNext()) {
            Lane l = (Lane) it.next();
            graphParticipantsToRemoveFromGraph.add(gmgr.getGraphParticipant(l));
         }
         gmgr.removeCellsAndArrangeParticipants(graphParticipantsToRemoveFromGraph.toArray());
      }

      if (graphPasteInProgress) {
         cci.incrementOffsetPoint(graph);
      }
      if (propertyMap.size() > 0) {
         gmgr.updateModelAndArrangeParticipants(null, propertyMap, new JaWEParentMap(), null, "", null, true);
      }
      graph.repaint();
      // System.out.println("AGP2="
      // + JaWEGraphModel.getAllParticipantsInModel(graph.getModel()));
   }

   public static void adjustInsertedOrUpdatedActivityOrArtifact(XMLCollectionElement wpOrAs,
                                                                XMLCollectionElement actOrArtif,
                                                                List vo,
                                                                Set participantsToInsertIntoGraph) {
      Pool pool = JaWEManager.getInstance().getXPDLUtils().getPoolForProcessOrActivitySet(wpOrAs);
      NodeGraphicsInfo ngi = JaWEManager.getInstance().getXPDLUtils().getNodeGraphicsInfo(actOrArtif);
      Lane l = null;
      if (ngi == null) {
         int inw = getGraphController().getGraphSettings().getLaneNameWidth();
         if (actOrArtif instanceof Activity) {
            String perf = ((Activity) actOrArtif).getFirstPerformer();
            if (perf.equals("")) {
               l = getDefaultLane(pool);
               if (l == null) {
                  l = createDefaultLane(pool);
               }
            } else {
               l = getLaneForPerformer(pool, ((Activity) actOrArtif).getFirstPerformer());
               if (l == null) {
                  l = createLaneForPerformer(pool, ((Activity) actOrArtif).getFirstPerformer());
               }
            }
         } else {
            l = getDefaultLane(pool);
            if (l == null) {
               l = createDefaultLane(pool);
            }
         }
         ngi = GraphUtilities.createNodeGraphicsInfo(actOrArtif, new Point(inw, inw), l.getId(), true);
      } else {
         l = pool.getLanes().getLane(ngi.getLaneId());
         if (l == null) {
            l = getDefaultLane(pool);
            if (l == null) {
               l = createDefaultLane(pool);
            }
            ngi.setLaneId(l.getId());
         }
      }
      if (!vo.contains(l.getId())) {
         participantsToInsertIntoGraph.add(l);
      }
   }

   public static void adjustPastedActivityOrArtifact(XMLCollectionElement actOrArt,
                                                     List vo,
                                                     Set participantsToInsertIntoGraph,
                                                     CopyOrCutInfo cci,
                                                     GraphManager gm) {
      // System.err.println("Adjusting pasted act " + act + ", vo=" +
      // vo+" for proc/as "+gm.getXPDLOwner().getId());
      // System.err.println(cci);
      Point pasteTo = cci.getPastePoint();
      Point pasteOffset = cci.getOffsetPoint(gm.getGraph());
      Point referencePoint = cci.getReferencePoint();
      // System.err.println("       ....pasteTo="+pasteTo+", pasteOffset="+pasteOffset+", refPoint="+referencePoint);
      if (pasteTo != null) {
         String pId = JaWEManager.getInstance().getXPDLUtils().getLaneId(actOrArt);
         Point off = GraphUtilities.getOffsetPoint(actOrArt);
         CopiedActivityOrArtifactInfo ai = new CopiedActivityOrArtifactInfo(pId, off);
         // System.err.println("..........Searching for rectangle for the info "+ai);
         Rectangle r = cci.getActivityBounds(ai);
         // System.err.println("..........Rectangle is "+r);
         Point refPoint = referencePoint;
         if (r != null) {
            refPoint = r.getLocation();
         }
         Point diffPoint = new Point(refPoint.x + pasteTo.x - referencePoint.x, refPoint.y + pasteTo.y - referencePoint.y);
         GraphSwimlaneInterface par = gm.findParentParticipantForLocation(diffPoint, null, null);
         String parId = ((Lane) par.getPropertyObject()).getId();
         // System.err.println("..........RefPoint="+refPoint+", diffPoing="+diffPoint+", newparId="+parId+", newop="+gm.getOffset(diffPoint));
         GraphUtilities.setOffsetPoint(actOrArt, gm.getOffset(diffPoint), parId);
         if (actOrArt instanceof Activity) {
            Activity act = (Activity) actOrArt;
            int type = act.getActivityType();
            if (type == XPDLConstants.ACTIVITY_TYPE_NO || type == XPDLConstants.ACTIVITY_TYPE_TASK_APPLICATION) {
               Lane defL = getDefaultLane(JaWEManager.getInstance().getXPDLUtils().getPoolForProcessOrActivitySet(gm.getGraph().getXPDLObject()));
               if (defL == null || !parId.equals(defL.getId())) {
                  String lp = getLanesFirstPerformer((Lane) par.getPropertyObject());
                  if (lp == null) {
                     lp = "";
                  }
                  act.setFirstPerformer(lp);
               } else {
                  act.setFirstPerformer("");
               }
               // System.err.println("..........Perf changed to "+parId);
            }
         }
      } else {
         Point oldPoint = GraphUtilities.getOffsetPoint(actOrArt);
         Point setPoint = new Point(pasteOffset.x + oldPoint.x, pasteOffset.y + oldPoint.y);
         // System.err.println("    ....... moving offset point from "+oldPoint+" to "+setPoint);
         Lane l = GraphUtilities.getLaneForActivityOrArtifact(actOrArt, gm.getGraph().getXPDLObject());
         if (l == null || !vo.contains(l.getId())) {
            Pool pool = JaWEManager.getInstance().getXPDLUtils().getPoolForProcessOrActivitySet(gm.getGraph().getXPDLObject());
            if (actOrArt instanceof Activity) {
               l = getLaneForPerformer(pool, ((Activity) actOrArt).getFirstPerformer());
               if (l != null) {
                  if (!vo.contains(l.getId())) {
                     vo.add(l.getId());
                     participantsToInsertIntoGraph.add(l);
                  }
               } else {
                  l = createLaneForPerformer(pool, ((Activity) actOrArt).getFirstPerformer());
                  vo.add(l.getId());
                  participantsToInsertIntoGraph.add(l);
               }
            } else {
               if (l == null) {
                  l = getDefaultLane(pool);
                  if (l == null) {
                     l = createDefaultLane(pool);
                     vo.add(l.getId());
                     participantsToInsertIntoGraph.add(l);
                  }
               }

            }
         }
         GraphUtilities.setOffsetPoint(actOrArt, setPoint, l.getId());
      }
   }

   public static void adjustPastedTransitionsOrAssociations(List tras, CopyOrCutInfo cci, GraphManager gm) {
      // System.err.println("Adjusting pasted transitions " + tras.size() +
      // ", for proc/as "+gm.getXPDLOwner().getId());
      Iterator ittras = tras.iterator();
      // System.err.println(cci);
      Point pasteTo = cci.getPastePoint();
      // Point pasteOffset=cci.getOffsetPoint(gm.getGraph());
      Point referencePoint = cci.getReferencePoint();
      // System.err.println("       ....pasteTo="+pasteTo+", pasteOffset="+pasteOffset+", refPoint="+referencePoint);
      while (ittras.hasNext()) {
         XMLCollectionElement tra = (XMLCollectionElement) ittras.next();
         // ExtendedAttribute bpea = GraphUtilities.getBreakpointsEA(tra);
         List bps = GraphUtilities.getBreakpoints(tra);
         // System.err.println("       bps1="+bps);
         if (bps.size() > 0) {
            if (pasteTo != null) {
               Iterator itbps = bps.iterator();
               while (itbps.hasNext()) {
                  Point bp = (Point) itbps.next();
                  bp.x += (pasteTo.x - referencePoint.x);
                  bp.y += (pasteTo.y - referencePoint.y);
                  // System.err.println("       changed bp");

               }
            } else {
               bps = new ArrayList();
            }
            // System.err.println("       bps2="+bps);
            GraphUtilities.setBreakpoints(tra, bps);
         }
      }
   }

   public static void adjustInsertedOrUpdatedTransitionsOrAssociations(List tras, GraphManager gmgr) {
      Iterator ittras = tras.iterator();
      while (ittras.hasNext()) {
         XMLCollectionElement tra = (XMLCollectionElement) ittras.next();
         ConnectorGraphicsInfo ea = JaWEManager.getInstance().getXPDLUtils().getConnectorGraphicsInfo(tra, false);
         String from = null;
         String to = null;
         if (tra instanceof Transition) {
            from = ((Transition) tra).getFrom();
            to = ((Transition) tra).getTo();
         } else {
            from = ((Association) tra).getSource();
            to = ((Association) tra).getTarget();
         }
         if (ea == null) {
            String style = GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_ORTHOGONAL;
            if (tra instanceof Transition) {
               if (from.equals(to)) {
                  style = GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_BEZIER;
               }
            }
            ea = GraphUtilities.createConnectorGraphicsInfo(tra, null, style, true);
         } else {
            String style = GraphUtilities.getStyle(tra);
            if (!GraphEAConstants.transitionStyles.contains(style)) {
               ea.setStyle(GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_ORTHOGONAL);
            }
         }

         if (from.equals(to) && !from.equals("")) {
            ConnectorGraphicsInfo bpea = JaWEManager.getInstance().getXPDLUtils().getConnectorGraphicsInfo(tra, false);
            if (bpea == null) {
               GraphActivityInterface gact = gmgr.getGraphActivity(from);
               Point realP = new Point(50, 50);
               if (gact != null) {
                  realP = gmgr.getCenter(gact);
               }
               List breakpoints = new ArrayList();
               int rp50x1 = realP.x - 50;
               int rp50x2 = realP.x + 50;
               if (rp50x1 < 0) {
                  rp50x2 = rp50x2 - rp50x1;
                  rp50x1 = 0;
               }
               int rp50y = realP.y - 50;
               if (rp50y < 0)
                  rp50y = realP.y + 50;

               breakpoints.add(new Point(Math.abs(rp50x1), Math.abs(rp50y)));
               breakpoints.add(new Point(Math.abs(rp50x2), Math.abs(rp50y)));

               bpea = GraphUtilities.createConnectorGraphicsInfo(tra,
                                                                 breakpoints,
                                                                 GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_SPLINE,
                                                                 true);
            }
         }
      }

   }

   public static Set getInsertedOrRemovedWorkflowProcesses(List allInfo, boolean inserted) {
      Set s = new HashSet();
      List l = findInfoList(allInfo, Package.class, WorkflowProcesses.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         if ((inserted && info.getAction() == XMLElementChangeInfo.INSERTED) || (!inserted && info.getAction() == XMLElementChangeInfo.REMOVED)) {

            List wps = info.getChangedSubElements();
            if (wps != null) {
               s.addAll(wps);
            }

         }
      }
      return s;
   }

   public static Set getInsertedOrRemovedActivitySets(List allInfo, boolean inserted) {
      Set s = new HashSet();
      List l = findInfoList(allInfo, WorkflowProcess.class, ActivitySets.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         if ((inserted && info.getAction() == XMLElementChangeInfo.INSERTED) || (!inserted && info.getAction() == XMLElementChangeInfo.REMOVED)) {

            List ass = info.getChangedSubElements();
            if (ass != null && ass.size() > 0) {
               s.addAll(ass);
            }

         }
      }
      return s;
   }

   public static boolean hasPoolOrientationChanged(List allInfo, XMLCollectionElement wpOrAs) {
      Map m = new HashMap();
      List l = findInfoList(allInfo, Pool.class, XMLAttribute.class);
      Pool p = JaWEManager.getInstance().getXPDLUtils().getPoolForProcessOrActivitySet(wpOrAs);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         if (info.getChangedElement().toName().equals("Orientation") && XMLUtil.getPool(info.getChangedElement()) == p) {
            return true;
         }
      }
      return false;
   }

   public static Map getActivitySetsWithChangedId(List allInfo) {
      Map m = new HashMap();
      List l = findInfoList(allInfo, ActivitySet.class, XMLAttribute.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         ActivitySet as = XMLUtil.getActivitySet(info.getChangedElement());
         m.put(info.getOldValue(), as);
      }
      return m;
   }

   public static Set getInsertedOrRemovedActivitiesAndArtifacts(List allInfo, XMLCollectionElement wpOrAs, boolean inserted, boolean isRedo) {
      Set s = new HashSet();
      List l = findInfoList(allInfo, wpOrAs.getClass(), Activities.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         if ((inserted && info.getAction() == XMLElementChangeInfo.INSERTED) || (!inserted && info.getAction() == XMLElementChangeInfo.REMOVED)) {

            if (info.getChangedElement().getParent() == wpOrAs) {
               List acts = info.getChangedSubElements();
               if (acts != null && acts.size() > 0) {
                  s.addAll(acts);
               }
            }

         }
      }
      l = findInfoList(allInfo, Package.class, Artifacts.class);
      Pool p = JaWEManager.getInstance().getXPDLUtils().getPoolForProcessOrActivitySet(wpOrAs);
      if (p != null) {
         Package pkg = XMLUtil.getPackage(wpOrAs);
         for (int i = 0; i < l.size(); i++) {
            XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
            if ((inserted && info.getAction() == XMLElementChangeInfo.INSERTED) || (!inserted && info.getAction() == XMLElementChangeInfo.REMOVED)) {

               if (info.getChangedElement().getParent() == pkg) {

                  List arts = info.getChangedSubElements();
                  if (arts != null && arts.size() > 0) {
                     if (isRedo) {
                        for (int j = 0; j < arts.size(); j++) {
                           Artifact art = (Artifact) arts.get(j);
                           String laneId = JaWEManager.getInstance().getXPDLUtils().getLaneId(art);
                           if (p.getLanes().getLane(laneId) != null) {
                              s.add(art);
                           }
                        }
                     } else {
                        s.addAll(arts);
                     }
                  }
               }

            }
         }
      }

      return s;
   }

   public static Set getActivitiesWithChangedPerformer(List allInfo, XMLCollectionElement wpOrAs) {
      Set s = new HashSet();
      List l = findInfoList(allInfo, Performers.class, Performer.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         Activity act = XMLUtil.getActivity(info.getChangedElement());
         if (act != null && act.getParent().getParent() == wpOrAs) {
            s.add(act);
         }
      }
      return s;
   }

   public static Set getActivitiesWithChangedPerformer(List allInfo, XMLCollectionElement wpOrAs, String oldPerf) {
      Set s = new HashSet();
      List l = findInfoList(allInfo, Performers.class, Performer.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         if (info.getOldValue().equals(oldPerf)) {
            Activity act = XMLUtil.getActivity(info.getChangedElement());
            if (act != null && act.getParent().getParent() == wpOrAs) {
               s.add(act);
            }
         }
      }
      return s;
   }

   public static Set getInsertedOrRemovedTransitionsAndAssociations(List allInfo, XMLCollectionElement wpOrAs, boolean inserted) {
      Set s = new HashSet();
      List l = findInfoList(allInfo, wpOrAs.getClass(), Transitions.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         if ((inserted && info.getAction() == XMLElementChangeInfo.INSERTED) || (!inserted && info.getAction() == XMLElementChangeInfo.REMOVED)) {

            if (info.getChangedElement().getParent() == wpOrAs) {
               List tras = info.getChangedSubElements();
               if (tras != null && tras.size() > 0) {
                  s.addAll(tras);
               }
            }

         }
      }
      l = findInfoList(allInfo, Package.class, Associations.class);
      Package pkg = XMLUtil.getPackage(wpOrAs);
      Activities acts = (Activities) wpOrAs.get("Activities");
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         if ((inserted && info.getAction() == XMLElementChangeInfo.INSERTED) || (!inserted && info.getAction() == XMLElementChangeInfo.REMOVED)) {

            if (info.getChangedElement().getParent() == pkg) {
               List asocs = info.getChangedSubElements();
               if (asocs != null && asocs.size() > 0) {
                  for (int j = 0; j < asocs.size(); j++) {
                     Association asoc = (Association) asocs.get(j);
                     if (acts.getActivity(asoc.getSource()) != null || acts.getActivity(asoc.getTarget()) != null) {
                        s.add(asoc);
                     }
                  }
               }
            }

         }
      }

      return s;
   }

   public static Set getUpdatedTransitionsAndAssociations(List allInfo, XMLCollectionElement wpOrAs) {
      Set s = new HashSet();
      List l = findInfoList(allInfo, Transition.class, XMLAttribute.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         XMLAttribute el = (XMLAttribute) info.getChangedElement();
         if (el.getParent().getParent().getParent() == wpOrAs) {
            String elName = el.toName();
            if (elName.equals("From") || elName.equals("To")) {
               s.add(el.getParent());
            }
         }
      }

      l = findInfoList(allInfo, Association.class, XMLAttribute.class);
      Activities acts = (Activities) wpOrAs.get("Activities");
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         XMLAttribute el = (XMLAttribute) info.getChangedElement();
         String elName = el.toName();
         if (elName.equals("Source") || elName.equals("Target")) {
            Association asoc = (Association) el.getParent();
            if (acts.getActivity(asoc.getSource()) != null || acts.getActivity(asoc.getTarget()) != null) {
               s.add(asoc);
            }
         }
      }

      return s;
   }

   // Extended attributes change
   public static Set getLanesWithChangedOrder(List allInfo) {
      Set s = new HashSet();
      List l = findInfoList(allInfo, Pool.class, Lanes.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         List l2 = info.getChangedSubElements();
         for (int j = 0; j < l2.size(); j++) {
            XMLElement el = (XMLElement) l2.get(j);
            if (el instanceof Lane) {
               s.add(el);
            }
         }
      }
      return s;
   }

   public static Set getNestedLanesWithChangedOrder(List allInfo) {
      Set s = new HashSet();
      List l = findInfoList(allInfo, Lane.class, NestedLanes.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         List l2 = info.getChangedSubElements();
         for (int j = 0; j < l2.size(); j++) {
            XMLElement el = (XMLElement) l2.get(j);
            if (el instanceof NestedLane) {
               Lane lane = XMLUtil.getPool(el).getLanes().getLane(((NestedLane) el).getLaneId());
               if (lane != null) {
                  s.add(lane);
               }
            }
         }
      }

      return s;
   }

   public static Set getActivitiesAndArtifactsWithChangedOffset(List allInfo, XMLCollectionElement wpOrAs) {
      Set s = new HashSet();
      List l = findInfoList(allInfo, Coordinates.class, XMLAttribute.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         XMLElement el = info.getChangedElement();
         Coordinates ea = (Coordinates) el.getParent();
         if (ea.getParent() instanceof NodeGraphicsInfo) {
            Activity act = XMLUtil.getActivity(el);
            if (act != null && act.getParent().getParent() == wpOrAs) {
               s.add(act);
            } else {
               Artifact art = XMLUtil.getArtifact(el);
               if (art != null) {
                  Pool p = JaWEManager.getInstance().getXPDLUtils().getPoolForProcessOrActivitySet(wpOrAs);
                  if (p != null) {
                     String laneId = JaWEManager.getInstance().getXPDLUtils().getLaneId(art);
                     if (p.getLanes().getLane(laneId) != null) {
                        s.add(art);
                     }
                  }
               }
            }
         }
      }
      return s;
   }

   public static Map updateJGraphForActivityAndArtifactBounds(Graph graph, List allInfo, XMLCollectionElement wpOrAs) {
      Map propertyMap = new HashMap();
      GraphLayoutCache glc = graph.getGraphLayoutCache();
      Set s = new HashSet();
      List l = findInfoList(allInfo, Coordinates.class, XMLAttribute.class);
      l.addAll(findInfoList(allInfo, NodeGraphicsInfo.class, XMLAttribute.class));

      Map a2cx = new HashMap();
      Map a2cy = new HashMap();
      Map a2sw = new HashMap();
      Map a2sh = new HashMap();
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         XMLElement el = info.getChangedElement();
         if ((el.toName().equals("XCoordinate") || el.toName().equals("YCoordinate") || el.toName().equals("Width") || el.toName().equals("Height"))
             && (el.getParent() instanceof NodeGraphicsInfo || el.getParent().getParent() instanceof NodeGraphicsInfo)) {
            Activity act = XMLUtil.getActivity(el);
            if (act != null && act.getParent().getParent() == wpOrAs) {
               s.add(act);
               if (el.toName().equals("XCoordinate")) {
                  a2cx.put(act, info.getOldValue());
               } else if (el.toName().equals("YCoordinate")) {
                  a2cy.put(act, info.getOldValue());
               } else if (el.toName().equals("Width")) {
                  a2sw.put(act, info.getOldValue());
               } else {
                  a2sh.put(act, info.getOldValue());
               }
            } else {
               Artifact art = XMLUtil.getArtifact(el);
               if (art != null) {
                  Pool p = JaWEManager.getInstance().getXPDLUtils().getPoolForProcessOrActivitySet(wpOrAs);
                  if (p != null) {
                     String laneId = JaWEManager.getInstance().getXPDLUtils().getLaneId(art);
                     if (p.getLanes().getLane(laneId) != null) {
                        s.add(art);
                        if (el.toName().equals("XCoordinate")) {
                           a2cx.put(act, info.getOldValue());
                        } else if (el.toName().equals("YCoordinate")) {
                           a2cy.put(act, info.getOldValue());
                        } else if (el.toName().equals("Width")) {
                           a2sw.put(act, info.getOldValue());
                        } else {
                           a2sh.put(act, info.getOldValue());
                        }
                     }
                  }
               }
            }
         }
      }

      Iterator it = s.iterator();
      while (it.hasNext()) {
         XMLCollectionElement a = (XMLCollectionElement) it.next();
         GraphCell graphCell = null;
         if (a instanceof Activity) {
            graphCell = graph.getGraphManager().getGraphActivity((Activity) a);
         } else {
            graphCell = graph.getGraphManager().getGraphArtifact((Artifact) a);
         }
         AbstractCellView view = (AbstractCellView) glc.getMapping(graphCell, false);
         if (view == null)
            continue;
         Rectangle origr = null;
         if (view instanceof GraphActivityViewInterface) {
            origr = ((GraphActivityViewInterface) view).getOriginalBounds();
         } else {
            origr = ((GraphArtifactViewInterface) view).getOriginalBounds();
         }
         NodeGraphicsInfo ngi = JaWEManager.getInstance().getXPDLUtils().getNodeGraphicsInfo(a);
         int width = ngi.getWidth();
         int height = ngi.getHeight();
         int x = (int) Double.parseDouble(ngi.getCoordinates().getXCoordinate());
         int y = (int) Double.parseDouble(ngi.getCoordinates().getYCoordinate());

         String oldws = (String) a2sw.get(a);
         int oldw = oldws == null ? width : (int) Double.parseDouble(oldws);
         String oldhs = (String) a2sh.get(a);
         int oldh = oldhs == null ? height : (int) Double.parseDouble(oldhs);
         String oldxs = (String) a2cx.get(a);
         int oldx = oldxs == null ? x : (int) Double.parseDouble(oldxs);
         String oldys = (String) a2cy.get(a);
         int oldy = oldys == null ? y : (int) Double.parseDouble(oldys);

         double dx = oldx - x;
         double dy = oldy - y;

         Rectangle newR = new Rectangle((int) (origr.getX() - dx), (int) (origr.getY() - dy), width, height);
         Map attributes = new HashMap();
         attributes.put(GraphConstants.BOUNDS, newR);
         glc.editCell(graphCell, attributes);
         AttributeMap map = new AttributeMap(graphCell.getAttributes());
         propertyMap.put(graphCell, map);
      }
      return propertyMap;
   }

   public static Set getActivitiesAndArtifactsWithChangedLaneId(List allInfo, XMLCollectionElement wpOrAs) {
      Set s = new HashSet();
      List l = findInfoList(allInfo, NodeGraphicsInfo.class, XMLAttribute.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         XMLElement el = info.getChangedElement();
         if (el.toName().equals("LaneId")) {
            Activity act = XMLUtil.getActivity(el);
            if (act != null && act.getParent().getParent() == wpOrAs) {
               s.add(act);
            }
            Artifact art = XMLUtil.getArtifact(el);
            if (art != null) {
               Pool p = JaWEManager.getInstance().getXPDLUtils().getPoolForProcessOrActivitySet(wpOrAs);
               if (p != null) {
                  String laneId = JaWEManager.getInstance().getXPDLUtils().getLaneId(art);
                  if (p.getLanes().getLane(laneId) != null) {
                     s.add(art);
                  }
               }
            }
         }
      }
      return s;
   }

   public static Set getTransitionsWithChangedBreakpointsOrStyle(List allInfo, XMLCollectionElement wpOrAs) {
      Set s = new HashSet();
      Activities acts = (Activities) wpOrAs.get("Activities");
      List l = findInfoList(allInfo, Transition.class, ConnectorGraphicsInfos.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         if ((info.getAction() == XMLElementChangeInfo.INSERTED) || (info.getAction() == XMLElementChangeInfo.REMOVED)) {
            XMLCollectionElement tmp = XMLUtil.getActivitySetOrWorkflowProcess(info.getChangedElement());
            if (tmp == wpOrAs) {
               List cgis = info.getChangedSubElements();
               if (cgis != null && cgis.size() > 0) {
                  for (int j = 0; j < cgis.size(); j++) {
                     Transition tra = XMLUtil.getTransition((XMLElement) cgis.get(i));
                     if (tra != null) {
                        System.out.println("ADDED TRA FOR CHANGED LABEL POSITION");
                        s.add(tra);
                     }
                  }
               }
            }

         }
      }
      l = findInfoList(allInfo, ConnectorGraphicsInfo.class, XMLAttribute.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         XMLElement el = info.getChangedElement();
         if (el.toName().equals("Style")) {
            Transition tra = XMLUtil.getTransition(el);
            if (tra != null && tra.getParent().getParent() == wpOrAs) {
               s.add(tra);
            }
            Association asoc = XMLUtil.getAssociation(el);
            if (asoc != null && (acts.getActivity(asoc.getSource()) != null || acts.getActivity(asoc.getTarget()) != null)) {
               s.add(asoc);
            }
         }
      }
      l = findInfoList(allInfo, Coordinates.class, XMLAttribute.class);
      l.addAll(findInfoList(allInfo, ConnectorGraphicsInfo.class, Coordinatess.class));
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         XMLElement el = info.getChangedElement();
         if (el instanceof Coordinatess || el.getParent().getParent().getParent() instanceof ConnectorGraphicsInfo) {
            Transition tra = XMLUtil.getTransition(el);
            if (tra != null && tra.getParent().getParent() == wpOrAs) {
               s.add(tra);
            }
            Association asoc = XMLUtil.getAssociation(el);
            if (asoc != null && (acts.getActivity(asoc.getSource()) != null || acts.getActivity(asoc.getTarget()) != null)) {
               s.add(asoc);
            }
         }
      }

      return s;
   }

   public static Pool getRotatedGraphObject(List allInfo) {
      List l = GraphUtilities.findInfoList(allInfo, Pool.class, XMLAttribute.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         if (info.getChangedElement().toName().equals("Orientation")) {
            return XMLUtil.getPool(info.getChangedElement());
         }
      }
      return null;
   }

   public static List findInfoList(List allInfo, Class parentObjClass, Class objClass) {
      List toRet = new ArrayList();

      for (int i = 0; i < allInfo.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) allInfo.get(i);
         XMLElement el = info.getChangedElement();
         if (el.getClass() == objClass && el.getParent().getClass() == parentObjClass) {
            toRet.add(info);
         }
      }
      return toRet;
   }

   protected static boolean reloadGraphIfNeccessary(Graph graph) {
      // if there are two parts with the same id within the graph (as a result of change
      // of
      // more relevant participant Id), reload the graph
      List allGraphParticipants = JaWEGraphModel.getAllParticipantsInModel(graph.getModel());
      List partsInGraph = new ArrayList();
      boolean shouldReload = false;
      // if (allGraphParticipants != null) {
      // Iterator it = allGraphParticipants.iterator();
      // while (it.hasNext()) {
      // GraphSwimlaneInterface gpar = (GraphSwimlaneInterface) it.next();
      // Participant p = (Participant) gpar.getUserObject();
      // if (partsInGraph.contains(p.getId())) {
      // shouldReload = true;
      // break;
      // }
      // partsInGraph.add(p.getId());
      // }
      // }

      if (shouldReload) {
         GraphUtilities.reloadGraph(graph);
      }
      return shouldReload;
   }

   protected static void reloadGraph(Graph graph) {
      Object[] elem = JaWEGraphModel.getAll(graph.getModel());
      graph.getModel().remove(elem);
      GraphUtilities.scanExtendedAttributesForWPOrAs(graph.getXPDLObject(), false);
      graph.getGraphManager().createWorkflowGraph(graph.getXPDLObject());
   }

   protected static List getActivitiesWithPerformer(List acts, String parId) {
      List l = new ArrayList();

      Iterator it = acts.iterator();
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         if (act.getFirstPerformer().equals(parId)) {
            l.add(act);
         }
      }

      return l;
   }

   protected static Lane getLaneForActivityOrArtifact(XMLCollectionElement actOrArtif, XMLCollectionElement wpOrAs) {
      NodeGraphicsInfo ngi = JaWEManager.getInstance().getXPDLUtils().getNodeGraphicsInfo(actOrArtif);
      if (ngi != null) {
         String laneId = ngi.getLaneId();
         if (wpOrAs == null) {
            wpOrAs = XMLUtil.getActivitySetOrWorkflowProcess(actOrArtif);
         }
         Pool p = JaWEManager.getInstance().getXPDLUtils().getPoolForProcessOrActivitySet(wpOrAs);
         return p.getLanes().getLane(laneId);
      }
      return null;
   }

   protected static Lane getLaneForPerformer(Pool p, String perf) {
      Iterator it = p.getLanes().toElements().iterator();
      while (it.hasNext()) {
         Lane l = (Lane) it.next();
         if (hasPerformer(l, perf)) {
            return l;
         }
      }
      return null;
   }

   protected static Lane createLaneForActivity(Activity act, boolean addToCollection) {
      String perf = act.getFirstPerformer();
      XMLCollectionElement wpOrAs = XMLUtil.getActivitySetOrWorkflowProcess(act);
      Pool p = JaWEManager.getInstance().getXPDLUtils().getPoolForProcessOrActivitySet(wpOrAs);
      Lanes ls = p.getLanes();
      Lane l = JaWEManager.getInstance().getXPDLObjectFactory().createXPDLObject(ls, "", false);
      Performer pf = JaWEManager.getInstance().getXPDLObjectFactory().createXPDLObject(l.getPerformers(), "", true);
      pf.setValue(perf);
      Participant par = getParticipantForLane(l, FreeTextExpressionParticipant.getInstance());
      l.setName(par.getName().equals("") ? par.getId() : par.getName());
      createNodeGraphicsInfo(l, null, null, true);
      if (addToCollection) {
         ls.add(l);
      }
      return l;
   }

   protected static Lane createLaneForPerformer(Pool p, String perf) {
      System.out.println("CREATING LANE for perf " + perf);
      Lanes ls = p.getLanes();
      Lane l = JaWEManager.getInstance().getXPDLObjectFactory().createXPDLObject(ls, "", false);
      String laneName = "";
      if (perf.equals("") || perf.equals(FreeTextExpressionParticipant.getInstance().getId())) {
         laneName = FreeTextExpressionParticipant.getInstance().getName();
      } else {
         Performer pf = JaWEManager.getInstance().getXPDLObjectFactory().createXPDLObject(l.getPerformers(), "", true);
         if (CommonExpressionParticipants.getInstance().isCommonExpressionParticipantId(perf)) {
            laneName = CommonExpressionParticipants.getInstance().getIdFromVisualOrderEA(perf);
            pf.setValue(laneName);
         } else {
            pf.setValue(perf);
            Participant parForLane = getParticipantForLane(l, null);
            laneName = perf;
            if (parForLane != null) {
               laneName = parForLane.getName().equals("") ? parForLane.getId() : parForLane.getName();
            }
         }
      }
      createNodeGraphicsInfo(l, null, null, true);
      l.setName(laneName);
      ls.add(l);
      return l;
   }

   public static void setLanesFirstPerformer(Lane l, String perf) {
      List perfs = l.getPerformers().toElements();
      if (perfs.size() > 0) {
         Performer p = (Performer) perfs.get(0);
         p.setValue(perf);
      }
   }

   protected static String getLanesFirstPerformer(Lane lane) {
      Iterator it = lane.getPerformers().toElements().iterator();
      while (it.hasNext()) {
         Performer perf = (Performer) it.next();
         return perf.toValue();
      }
      return null;
   }

   protected static boolean hasPerformer(Lane l, String perf) {
      Iterator it = l.getPerformers().toElements().iterator();
      while (it.hasNext()) {
         Performer p = (Performer) it.next();
         if (p.toValue().equals(perf)) {
            return true;
         }
      }
      return false;
   }

   protected static Lane getDefaultLane(Pool p) {
      if (p == null)
         return null;
      Iterator it = p.getLanes().toElements().iterator();
      while (it.hasNext()) {
         Lane l = (Lane) it.next();
         if (l.getPerformers().size() == 0) {
            return l;
         }
      }
      return null;
   }

   protected static Lane createDefaultLane(Pool p) {
      return createLaneForPerformer(p, FreeTextExpressionParticipant.getInstance().getId());
   }

   protected static void checkLanes(Pool p, List pids) {
      for (int i = 0; i < pids.size(); i++) {
         String pid = (String) pids.get(i);
         if (getLaneForPerformer(p, pid) == null) {
            createLaneForPerformer(p, pid);
         }
      }
   }

   public static void setNewLaneId(List acts, String Id) {
      for (int i = 0; i < acts.size(); i++) {
         Activity act = (Activity) acts.get(i);
         GraphUtilities.setLaneId(act, Id);
      }
   }

   public static void setNewPerformer(List acts, String perf) {
      for (int i = 0; i < acts.size(); i++) {
         Activity act = (Activity) acts.get(i);
         act.setFirstPerformer(perf);
      }
   }

   public static void setLaneId(XMLCollectionElement actOrArt, String laneId) {
      if (laneId == null || laneId.equals("")) {
         laneId = FreeTextExpressionParticipant.getInstance().getId();
      }
      NodeGraphicsInfo ngi = JaWEManager.getInstance().getXPDLUtils().getNodeGraphicsInfo(actOrArt);
      if (ngi != null) {
         ngi.setLaneId(laneId);
      }
   }

   public static Lane getParentLane(Lane lane) {
      Lanes lanes = (Lanes) lane.getParent();
      Iterator it = lanes.toElements().iterator();
      while (it.hasNext()) {
         Lane l = (Lane) it.next();
         if (l.equals(lane))
            continue;
         Iterator it2 = l.getNestedLanes().toElements().iterator();
         while (it2.hasNext()) {
            NestedLane nl = (NestedLane) it2.next();
            if (nl.getLaneId().equals(lane.getId())) {
               return l;
            }
         }
      }
      return null;
   }

   protected static List getLaneHierarchy(Pool pool) {
      List ret = new ArrayList();
      List rls = getRootLanes(pool);
      for (int i = 0; i < rls.size(); i++) {
         Lane l = (Lane) rls.get(i);
         ret.addAll(getLaneHierarchy(l));
      }
      return ret;
   }

   protected static List getLaneHierarchy(Lane lane) {
      List ret = new ArrayList();
      ret.add(lane);
      List nestedL = getNestedLanes(lane);
      for (int j = 0; j < nestedL.size(); j++) {
         ret.addAll(getLaneHierarchy((Lane) nestedL.get(j)));
      }
      return ret;
   }

   protected static List getRootLanes(Pool pool) {
      List ret = new ArrayList();
      List lanes = pool.getLanes().toElements();
      for (int i = 0; i < lanes.size(); i++) {
         Lane l = (Lane) lanes.get(i);
         if (getParentLane(l) == null) {
            ret.add(l);
         }
      }
      return ret;
   }

   protected static List getNestedLanes(Lane l) {
      List ret = new ArrayList();
      Lanes ls = (Lanes) l.getParent();
      Iterator it = l.getNestedLanes().toElements().iterator();
      while (it.hasNext()) {
         NestedLane nl = (NestedLane) it.next();
         Lane rnl = ls.getLane(nl.getLaneId());
         ret.add(rnl);
      }
      return ret;
   }

   protected static void fillParentLanes(Lane l, List pls) {
      Lane pl = getParentLane(l);
      if (pl != null) {
         pls.add(pl);
         fillParentLanes(pl, pls);
      }
   }

   public static Participant getParticipantForLane(Lane l, Participant defaultToReturn) {
      Participant par = null;
      String perf = "";
      Iterator it = l.getPerformers().toElements().iterator();
      while (it.hasNext()) {
         perf = ((Performer) it.next()).toValue();
         break;
      }
      Pool pool = XMLUtil.getPool(l);
      WorkflowProcess wp = JaWEManager.getInstance().getXPDLUtils().getProcessForPool(pool);
      if (wp == null) {
         ActivitySet as = JaWEManager.getInstance().getXPDLUtils().getActivitySetForPool(pool);
         if (as != null) {
            wp = XMLUtil.getWorkflowProcess(as);
         }
      }
      if (wp != null) {
         par = XMLUtil.findParticipant(JaWEManager.getInstance().getXPDLHandler(), wp, perf);
      }
      if (par == null) {
         par = defaultToReturn;
      }

      return par;
   }

   public static boolean isCommonExpressionLane(Lane l) {
      return getParticipantForLane(l, null) == null && l.getPerformers().size() > 0;
   }

   public static Lane getLane(XMLCollectionElement wpOrAs, String laneId) {
      Pool p = JaWEManager.getInstance().getXPDLUtils().getPoolForProcessOrActivitySet(wpOrAs);
      return p.getLanes().getLane(laneId);
   }

   public static Set getArtifacts(XMLCollectionElement wpOrAs) {
      Set s = new HashSet();
      Pool p = JaWEManager.getInstance().getXPDLUtils().getPoolForProcessOrActivitySet(wpOrAs);
      if (p != null) {
         Package pkg = XMLUtil.getPackage(wpOrAs);
         List arts = pkg.getArtifacts().toElements();

         if (arts != null && arts.size() > 0) {
            for (int j = 0; j < arts.size(); j++) {
               Artifact art = (Artifact) arts.get(j);
               String laneId = JaWEManager.getInstance().getXPDLUtils().getLaneId(art);
               if (p.getLanes().getLane(laneId) != null) {
                  s.add(art);
               }
            }
         }
      }

      return s;
   }

   public static Set getAssociations(XMLCollectionElement wpOrAs) {
      Set s = new HashSet();
      Package pkg = XMLUtil.getPackage(wpOrAs);
      Activities acts = (Activities) wpOrAs.get("Activities");
      List asocs = pkg.getAssociations().toElements();
      if (asocs != null && asocs.size() > 0) {
         for (int j = 0; j < asocs.size(); j++) {
            Association asoc = (Association) asocs.get(j);
            if (acts.getActivity(asoc.getSource()) != null || acts.getActivity(asoc.getTarget()) != null) {
               s.add(asoc);
            }
         }
      }

      return s;
   }

   public static void rotateProcess(Graph selectedGraph) {
      Object[] elem = JaWEGraphModel.getAll(selectedGraph.getModel());
      selectedGraph.getModel().remove(elem);
      // GraphUtilities.rotateCoordinates(elem);
      for (int i = 0; i < elem.length; i++) {
         if (elem[i] instanceof DefaultGraphCell) {
            Object uo = ((DefaultGraphCell) elem[i]).getUserObject();
            if (uo instanceof Activity || uo instanceof Artifact) {
               XMLCollectionElement act = (XMLCollectionElement) uo;

               Point p = GraphUtilities.getOffsetPoint(act);
               Utils.flipCoordinates(p);
               GraphUtilities.setOffsetPoint(act, p, null);
            } else if (uo instanceof Transition || uo instanceof Association) {
               XMLCollectionElement tr = (XMLCollectionElement) uo;
               List bpoi = GraphUtilities.getBreakpoints(tr);
               for (int j = 0; j < bpoi.size(); j++) {
                  Point p = (Point) bpoi.get(j);
                  Utils.flipCoordinates(p);
               }
               GraphUtilities.setBreakpoints(tr, bpoi);
            }
         }
      }

      selectedGraph.getGraphManager().createWorkflowGraph(selectedGraph.getXPDLObject());
   }
}
