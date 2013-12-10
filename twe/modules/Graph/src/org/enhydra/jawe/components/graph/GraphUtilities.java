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
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.XPDLElementChangeInfo;
import org.enhydra.shark.utilities.SequencedHashMap;
import org.enhydra.shark.xpdl.XMLAttribute;
import org.enhydra.shark.xpdl.XMLCollectionElement;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLElementChangeInfo;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.XPDLConstants;
import org.enhydra.shark.xpdl.elements.Activities;
import org.enhydra.shark.xpdl.elements.Activity;
import org.enhydra.shark.xpdl.elements.ActivitySet;
import org.enhydra.shark.xpdl.elements.ActivitySets;
import org.enhydra.shark.xpdl.elements.ExtendedAttribute;
import org.enhydra.shark.xpdl.elements.ExtendedAttributes;
import org.enhydra.shark.xpdl.elements.Package;
import org.enhydra.shark.xpdl.elements.Participant;
import org.enhydra.shark.xpdl.elements.Participants;
import org.enhydra.shark.xpdl.elements.Performer;
import org.enhydra.shark.xpdl.elements.Transition;
import org.enhydra.shark.xpdl.elements.Transitions;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;
import org.enhydra.shark.xpdl.elements.WorkflowProcesses;
import org.jgraph.graph.ParentMap;

public class GraphUtilities {

   public static GraphController getGraphController() {
      GraphController gc=null;
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
//         System.err.println("VOVAL for wp=" + wp.getId() + " as=" + asId + " is " + ord + "!");
         String pId=null;
         while (true) {
            int ind=ord.indexOf(";");
            String tmpId=null;
            boolean clearPid=false;
            if (ind<0) {
               if (ord.length()==0) {
                  break;
               } 
               tmpId=ord;               
            } else {
               tmpId=ord.substring(0,ind);
               ord=ord.substring(ind+1);
            }            
            int cepPrefInd=tmpId.indexOf(GraphEAConstants.COMMON_EXPRESSION_PARTICIPANT_PREFIX);
            int cepSuffInd=tmpId.indexOf(GraphEAConstants.COMMON_EXPRESSION_PARTICIPANT_SUFIX);
            if (cepPrefInd>=0 && cepSuffInd<0) {
               if (pId==null) {
                  pId=tmpId+";";
               } else {
                  pId+=tmpId+";";
               }
               continue;
            } else if (cepPrefInd<0 && cepSuffInd>=0) {
               pId+=tmpId;
               clearPid=true;
            } else if (cepPrefInd<0 && cepSuffInd<0) {
               if (pId!=null) {
                  pId+=tmpId+";";
                  continue;
               } 
               pId=tmpId;
               clearPid=true;               
            } else {
               pId=tmpId;
               clearPid=true;
            }
            order.add(pId);
            if (clearPid) {
               pId=null;
            }
            if (tmpId.equals(ord)) {
               break;
            }
         }
         if (asId != null) {
            order.remove(0);
         }
      }
//      System.out.println("VOORD for wp=" + wp.getId() + " as=" + asId + " is " + order + "!");
      return order;
   }

   public static void setParticipantVisualOrder(XMLCollectionElement wpOrAs, List order) {
//      System.out.println("Setting pvo for " + wpOrAs.getId() + ", ord=" + order + "!");
      ExtendedAttribute ea = getParticipantVisualOrderEA(wpOrAs);
      String ord = GraphUtilities.createParticipantVisualOrderEAVal(wpOrAs, order);
      if (ea == null) {
         ea = GraphUtilities.createParticipantVisualOrderEA(wpOrAs, ord, true);
      } else {
         ea.setVValue(ord);
      }
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
      String[] strarr = Utils.tokenize(eaval, ";");
      if (strarr.length > 0) {
         return strarr[0];
      }
      return "";      
   }

   protected static ExtendedAttribute createParticipantVisualOrderEA(XMLCollectionElement wpOrAs, String val,
         boolean addToCollection) {
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

   protected static List getParticipantVisualOrderOld(XMLCollectionElement wpOrAs) {
      List order = new ArrayList();
      ExtendedAttribute ea = getParticipantVisualOrderEAOld(wpOrAs);
      if (ea != null) {
         String ord = ea.getVValue();
         String[] vosa = Utils.tokenize(ord, ";");
         for (int i = 0; i < vosa.length; i++) {
            order.add(vosa[i]);
         }
      }
      return order;
   }

   protected static ExtendedAttribute getParticipantVisualOrderEAOld(XMLCollectionElement wpOrAs) {
      ExtendedAttribute ea = null;
      String eaname = GraphEAConstants.EA_JAWE_GRAPH_WORKFLOW_PARTICIPANT_ORDER_OLD;
      if (wpOrAs instanceof WorkflowProcess) {
         ea = ((WorkflowProcess) wpOrAs).getExtendedAttributes().getFirstExtendedAttributeForName(eaname);
      } else {
         eaname = GraphEAConstants.EA_JAWE_GRAPH_BLOCK_PARTICIPANT_ORDER_OLD;
         Activity refAct = GraphUtilities.getReferencingBlockActivity((ActivitySet) wpOrAs);
         if (refAct != null) {
            ea = refAct.getExtendedAttributes().getFirstExtendedAttributeForName(eaname);
         }
      }
      return ea;
   }

   public static String getGraphParticipantOrientation(WorkflowProcess wp, XMLCollectionElement wpOrAs) {
      String asId = null;
      if (wpOrAs instanceof ActivitySet) {
         asId = wpOrAs.getId();
      }
      return GraphUtilities.getGraphParticipantOrientation(wp, asId);
   }

   public static String getGraphParticipantOrientation(WorkflowProcess wp, String asId) {
      String orientation = GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ORIENTATION_VALUE_HORIZONTAL;
      ExtendedAttribute ea = GraphUtilities.getGraphParticipantOrientationEA(wp, asId);
      if (ea != null) {
         orientation = ea.getVValue();
         if (asId != null) {
            String[] parts = Utils.tokenize(orientation, ";");
            orientation = parts[1];
         }
      }
      return orientation;
   }

   public static void setGraphParticipantOrientation(WorkflowProcess wp, XMLCollectionElement wpOrAs, String orientation) {
      if (wpOrAs instanceof ActivitySet) {
         orientation = wpOrAs.getId() + ";" + orientation;
      }
      ExtendedAttribute ea = GraphUtilities.getGraphParticipantOrientationEA(wp, wpOrAs);
      if (ea == null) {
         ea = GraphUtilities.createGraphParticipantOrientationEA(wp, wpOrAs, orientation, true);
      } else {
         ea.setVValue(orientation);
      }
   }

   protected static ExtendedAttribute getGraphParticipantOrientationEA(WorkflowProcess wp, XMLCollectionElement wpOrAs) {
      String asId = null;
      if (wpOrAs instanceof ActivitySet) {
         asId = wpOrAs.getId();
      }
      return GraphUtilities.getGraphParticipantOrientationEA(wp, asId);
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

   protected static ExtendedAttribute createGraphParticipantOrientationEA(WorkflowProcess wp,
         XMLCollectionElement wpOrAs, String val, boolean addToCollection) {
      ExtendedAttributes eas = wp.getExtendedAttributes();
      String eaname = GraphEAConstants.EA_JAWE_GRAPH_WORKFLOW_PARTICIPANT_ORIENTATION;
      if (wpOrAs instanceof ActivitySet) {
         eaname = GraphEAConstants.EA_JAWE_GRAPH_BLOCK_PARTICIPANT_ORIENTATION;
      }
      ExtendedAttribute ea = (ExtendedAttribute) eas.generateNewElement();
      ea.setName(eaname);
      ea.setVValue(val);
      if (addToCollection) {
         eas.add(0, ea);
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
//      System.out.println("Found " + l.size() + " eas with name " + eaname + " for wp " + wp.getId() + ", as=" + asId);
      if (asId == null) {
         ret.addAll(l);
      } else {
         Iterator it = l.iterator();
         while (it.hasNext()) {
            ExtendedAttribute ea = (ExtendedAttribute) it.next();
            String sedstr = ea.getVValue();
//            System.out.println("EA for BA = " + sedstr);
            String[] startOrEndD = Utils.tokenize(sedstr, ",");
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
      //      System.out.println("There are "+seeas.size()+" seeas in graph
      // "+wpOrAs.getId());
      List toRet = new ArrayList();
      Iterator it = seeas.iterator();
      while (it.hasNext()) {
         ExtendedAttribute ea = (ExtendedAttribute) it.next();
         StartEndDescription sed = new StartEndDescription(ea);
//         System.out.println("SED=" + sed.toString());
         // NOTE: sed.getXXXId() can be null
         if ((eapart.equals(GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID) && id.equals(sed.getParticipantId()))
               || (eapart.equals(GraphEAConstants.EA_PART_ACTIVITY_SET_ID) && id.equals(sed.getActSetId()))
               || (eapart.equals(GraphEAConstants.EA_PART_CONNECTING_ACTIVITY_ID) && id.equals(sed.getActId()))) {
            toRet.add(ea);
         }
      }
//      System.out.println("Found " + toRet.size() + " seds for " + eapart + ", id=" + id);
      return toRet;
   }

   public static ExtendedAttribute createStartOrEndExtendedAttribute(XMLCollectionElement wpOrAs, boolean isStart,
         String pId, Point offset, String type, boolean addToCollection) {

      WorkflowProcess wp = XMLUtil.getWorkflowProcess(wpOrAs);
      ExtendedAttributes eas = wp.getExtendedAttributes();
      String eaname = null;
      if (isStart) {
         if (wpOrAs instanceof WorkflowProcess) {
            eaname = GraphEAConstants.EA_JAWE_GRAPH_START_OF_WORKFLOW;
         } else {
            eaname = GraphEAConstants.EA_JAWE_GRAPH_START_OF_BLOCK;
         }
      } else {
         if (wpOrAs instanceof WorkflowProcess) {
            eaname = GraphEAConstants.EA_JAWE_GRAPH_END_OF_WORKFLOW;
         } else {
            eaname = GraphEAConstants.EA_JAWE_GRAPH_END_OF_BLOCK;
         }
      }
      StartEndDescription sed = new StartEndDescription();
      sed.setEAName(eaname);
      if (wpOrAs instanceof ActivitySet) {
         sed.setActSetId(wpOrAs.getId());
      }
      sed.setParticipantId(pId);
      sed.setOffset(offset);
      sed.setTransitonStyle(GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_SIMPLE_ROUTING_BEZIER);
      sed.setType(type);
      ExtendedAttribute ea = (ExtendedAttribute) eas.generateNewElement();
      ea.setName(eaname);
      ea.setVValue(sed.toString());
      if (addToCollection) {
         eas.add(0, ea);
      }
      return ea;
   }

   protected static List getStartOrEndDescriptionsOld(XMLCollectionElement wpOrAs, boolean isStart) {
      List startOrEndDescriptions = new ArrayList();
      List eas = GraphUtilities.getStartOrEndExtendedAttributesOld(wpOrAs, isStart);
      Iterator it = eas.iterator();
      while (it.hasNext()) {
         ExtendedAttribute ea = (ExtendedAttribute) it.next();
         String sedstr = ea.getVValue();
         StartEndDescription sed = new StartEndDescription();
         if (!isStart) {
            if (wpOrAs instanceof ActivitySet) {
               sed.setEAName(GraphEAConstants.EA_JAWE_GRAPH_END_OF_BLOCK);
            } else {
               sed.setEAName(GraphEAConstants.EA_JAWE_GRAPH_END_OF_WORKFLOW);
            }
         } else {
            if (wpOrAs instanceof ActivitySet) {
               sed.setEAName(GraphEAConstants.EA_JAWE_GRAPH_START_OF_BLOCK);
            } else {
               sed.setEAName(GraphEAConstants.EA_JAWE_GRAPH_START_OF_WORKFLOW);
            }
         }

         String[] startOrEndD = Utils.tokenize(sedstr, ";");
         int i = 0;

         if (wpOrAs instanceof ActivitySet) {
            sed.setActSetId(wpOrAs.getId());
         }
         try {
            sed.setParticipantId(startOrEndD[i++]);
            String actId = startOrEndD[i++];
            if (actId.equals("-1"))
               actId = null;
            sed.setActId(actId);
         } catch (Exception ex) {
            continue;
         }

         try {
            int ah=getGraphController().getGraphSettings().getActivityHeight();
            sed.setOffset(new Point(ah/5+Integer.parseInt(startOrEndD[i++]), ah/5+Integer.parseInt(startOrEndD[i++])));
            String style = GraphUtilities.getNewStyle(null, startOrEndD[i++]);
            sed.setTransitonStyle(style);
         } catch (Exception ex) {
         }

         String type = null;
         if (isStart) {
            type = GraphEAConstants.START_TYPE_DEFAULT;
         } else {
            type = GraphEAConstants.END_TYPE_DEFAULT;
         }
         sed.setType(type);
         startOrEndDescriptions.add(sed);
         ((ExtendedAttributes)ea.getParent()).remove(ea);
      }
      return startOrEndDescriptions;
   }

   protected static List getStartOrEndExtendedAttributesOld(XMLCollectionElement wpOrAs, boolean isStart) {
      List ret = new ArrayList();
      String eaname = null;
      if (isStart) {
         if (wpOrAs instanceof WorkflowProcess) {
            eaname = GraphEAConstants.EA_JAWE_GRAPH_START_OF_WORKFLOW_OLD;
         } else {
            eaname = GraphEAConstants.EA_JAWE_GRAPH_START_OF_BLOCK_OLD;
         }
      } else {
         if (wpOrAs instanceof WorkflowProcess) {
            eaname = GraphEAConstants.EA_JAWE_GRAPH_END_OF_WORKFLOW_OLD;
         } else {
            eaname = GraphEAConstants.EA_JAWE_GRAPH_END_OF_BLOCK_OLD;
         }
      }
      if (wpOrAs instanceof WorkflowProcess) {
         ret.addAll(((WorkflowProcess) wpOrAs).getExtendedAttributes().getElementsForName(eaname));
      } else {
         Activity refAct = GraphUtilities.getReferencingBlockActivity((ActivitySet) wpOrAs);
         if (refAct != null) {
            ret.addAll(refAct.getExtendedAttributes().getElementsForName(eaname));
         }
      }
      return ret;
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

   public static void setParticipantId(Activity act, String participantId) {
      if (participantId == null || participantId.equals("")) {
         participantId = FreeTextExpressionParticipant.getInstance().getId();
      }
      ExtendedAttribute ea = GraphUtilities.getParticipantIdEA(act);
      if (ea == null) {
         ea = GraphUtilities.createParticipantIdEA(act, participantId, true);
      } else {
         ea.setVValue(participantId);
      }
   }

   protected static ExtendedAttribute getParticipantIdEA(Activity act) {
      return act.getExtendedAttributes()
            .getFirstExtendedAttributeForName(GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID);
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

   protected static String getParticipantIdOld(Activity act) {
      String participantId = null;
      ExtendedAttribute ea = getParticipantIdEAOld(act);
      if (ea != null) {
         participantId = ea.getVValue();
      }
      return participantId;
   }

   protected static ExtendedAttribute getParticipantIdEAOld(Activity act) {
      return act.getExtendedAttributes().getFirstExtendedAttributeForName(
            GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID_OLD);
   }

   public static Point getOffsetPoint(Activity act) {
      Point offset = new Point(0, 0);
      ExtendedAttribute ea = GraphUtilities.getOffsetPointEA(act);
      if (ea != null) {
         String offsetstr = ea.getVValue();
         String[] offsetstrD = Utils.tokenize(offsetstr, ",");
         try {
            offset.x = Integer.parseInt(offsetstrD[0]);
            offset.y = Integer.parseInt(offsetstrD[1]);
         } catch (Exception ex) {
         }
      }
      return offset;
   }

   public static void setOffsetPoint(Activity act, Point offset) {
      if (offset == null) {
         offset = new Point(0, 0);
      }
      String ofs = offset.x + "," + offset.y;
      ExtendedAttribute ea = GraphUtilities.getOffsetPointEA(act);
      //      System.out.println("Act "+act.getId()+", eaxoff="+ea);
      if (ea == null) {
         ea = GraphUtilities.createOffsetPointEA(act, ofs, true);
      } else {
         ea.setVValue(ofs);
      }
   }

   protected static ExtendedAttribute getOffsetPointEA(Activity act) {
      return act.getExtendedAttributes().getFirstExtendedAttributeForName(GraphEAConstants.EA_JAWE_GRAPH_OFFSET);
   }

   protected static ExtendedAttribute createOffsetPointEA(Activity act, String val, boolean addToCollection) {
      ExtendedAttributes eas = act.getExtendedAttributes();
      ExtendedAttribute ea = (ExtendedAttribute) act.getExtendedAttributes().generateNewElement();
      ea.setName(GraphEAConstants.EA_JAWE_GRAPH_OFFSET);
      ea.setVValue(val);
      if (addToCollection) {
         eas.add(0, ea);
      }
      return ea;
   }

   public static Point getOffsetPointOld(Activity act) {
      Point offset = null;
      ExtendedAttributes eas=act.getExtendedAttributes();
      ExtendedAttribute eax = eas.getFirstExtendedAttributeForName(
            GraphEAConstants.EA_JAWE_GRAPH_OFFSET_OLD_X);
      ExtendedAttribute eay = eas.getFirstExtendedAttributeForName(
            GraphEAConstants.EA_JAWE_GRAPH_OFFSET_OLD_Y);
      if (eax != null && eay != null) {
         try {
            offset = new Point(Integer.parseInt(eax.getVValue()), Integer.parseInt(eay.getVValue()));
            eas.remove(eax);
            eas.remove(eay);            
         } catch (Exception ex) {

         }
      }
      return offset;
   }

   // --------------- TRANSITION
   public static String getStyle(Transition tra) {
      String style = GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_BEZIER;
      ExtendedAttribute ea = getStyleEA(tra);
      if (ea != null) {
         style = ea.getVValue();
      }
      return style;
   }

   public static void setStyle(Transition tra, String style) {
      ExtendedAttribute ea = GraphUtilities.getStyleEA(tra);
      if (ea == null) {
         ea = GraphUtilities.createStyleEA(tra, style, true);
      } else {
         ea.setVValue(style);
      }
   }

   protected static ExtendedAttribute getStyleEA(Transition tra) {
      return tra.getExtendedAttributes().getFirstExtendedAttributeForName(
            GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE);
   }

   protected static ExtendedAttribute createStyleEA(Transition tra, String val, boolean addToCollection) {
      ExtendedAttributes eas = tra.getExtendedAttributes();
      ExtendedAttribute ea = (ExtendedAttribute) eas.generateNewElement();
      ea.setName(GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE);
      ea.setVValue(val);
      if (addToCollection) {
         eas.add(0, ea);
      }
      return ea;
   }

   protected static String getStyleOld(Transition tra) {
      String style = "";
      ExtendedAttribute ea = getStyleEAOld(tra);
      if (ea != null) {
         style = ea.getVValue();
         ((ExtendedAttributes)ea.getParent()).remove(ea);
      }
      return style;
   }

   protected static ExtendedAttribute getStyleEAOld(Transition tra) {
      return tra.getExtendedAttributes().getFirstExtendedAttributeForName(
            GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_OLD);
   }

   public static List getBreakpoints(Transition tra) {
      List breakPoints = new ArrayList();
      ExtendedAttribute ea = GraphUtilities.getBreakpointsEA(tra);
      if (ea != null) {
         Point p;
         String[] pPos = Utils.tokenize(ea.getVValue(), "-");
         for (int i = 0; i < pPos.length; i++) {
            String pos = pPos[i];
            String[] posD = Utils.tokenize(pos, ",");
            try {
               p = new Point(Integer.parseInt(posD[0]), Integer.parseInt(posD[1]));
               breakPoints.add(p);
            } catch (Exception ex) {
            }
         }
      }
      return breakPoints;
   }

   public static void setBreakpoints(Transition tra, List breakPoints) {
      ExtendedAttribute ea = GraphUtilities.getBreakpointsEA(tra);
      String eaval = GraphUtilities.createBreakpointsEAVal(breakPoints);
      if (!eaval.equals("")) {
         if (ea == null) {
            ea = GraphUtilities.createBreakpointsEA(tra, eaval, true);
         } else {
            ea.setVValue(eaval);
         }
      } else {
         if (ea != null) {
            ((ExtendedAttributes) ea.getParent()).remove(ea);
         }
      }
   }

   public static ExtendedAttribute getBreakpointsEA(Transition tra) {
      return tra.getExtendedAttributes().getFirstExtendedAttributeForName(GraphEAConstants.EA_JAWE_GRAPH_BREAK_POINTS);
   }

   public static ExtendedAttribute createBreakpointsEA(Transition tra, String val, boolean addToCollection) {
      ExtendedAttributes eas = tra.getExtendedAttributes();
      ExtendedAttribute ea = (ExtendedAttribute) eas.generateNewElement();
      ea.setName(GraphEAConstants.EA_JAWE_GRAPH_BREAK_POINTS);
      ea.setVValue(val);
      if (addToCollection) {
         eas.add(0, ea);
      }
      return ea;
   }

   protected static String createBreakpointsEAVal(List breakPoints) {
      String eaval = "";
      if (breakPoints != null) {
         for (int i = 0; i < breakPoints.size(); i++) {
            Point p = (Point) breakPoints.get(i);
            String pPos = String.valueOf(p.x) + "," + String.valueOf(p.y);
            eaval += pPos;
            if (i != breakPoints.size() - 1) {
               eaval += "-";
            }
         }
      }
      return eaval;
   }

   protected static List getBreakpointsOld(Transition tra) {
      Map ordNoToPoint = new HashMap();
      ExtendedAttributes eas = tra.getExtendedAttributes();
      if (eas.size() > 0) {
         ExtendedAttribute ea;
         Iterator it = eas.toElements().iterator();
         Point p;
         String[] pPos;
         int i = 1;
         while (it.hasNext()) {
            ea = (ExtendedAttribute) it.next();
            if (ea.getName().equals(GraphEAConstants.EA_JAWE_GRAPH_BREAK_POINTS_OLD)) {
               pPos = Utils.tokenize(ea.getVValue(), ";");
               if (pPos == null || pPos.length != 3) {
                  continue;
               }
               try {
                  p = new Point(Integer.parseInt(pPos[0]), Integer.parseInt(pPos[1]));
                  int index;
                  try {
                     index = Integer.parseInt(pPos[2]);
                  } catch (Exception exInner) {
                     index = i;
                  }
                  ordNoToPoint.put(new Integer(index), p);
                  eas.remove(ea);
               } catch (Exception ex) {
               }
               i++;
            }
         }
      }
      List breakPoints = new ArrayList();
      for (int i = 1; i <= ordNoToPoint.size(); i++) {
         breakPoints.add(ordNoToPoint.get(new Integer(i)));
      }
      return breakPoints;
   }

   //----------------------------------------------------------------------------------------------

   /**
    * Returns the sorted set of participants for given object. The object can be activity set or
    * workflow process.
    */
   public static List gatherParticipants(XMLCollectionElement wpOrAs) {
      List ownedActivities = ((Activities) wpOrAs.get("Activities")).toElements();
      List gatherInto = new ArrayList();

      List vorder = GraphUtilities.getParticipantVisualOrder(wpOrAs);
//      System.out.println("VORDER for " + wpOrAs.getId() + "=" + vorder + ", size=" + vorder.size());
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(wpOrAs);

      // gathering participants in ordered way defined by ext. attrib
      if (vorder.size() > 0) {
         ParticipantInfo dpInfo = null;
         for (int i = 0; i < vorder.size(); i++) {
            String pId = (String) vorder.get(i);
//            System.err.println("Gathering for  par id "+pId);
            Participant p = XMLUtil.findParticipant(JaWEManager.getInstance().getXPDLHandler(),wp, pId);
            if (p==null) {
               if (CommonExpressionParticipants.getInstance().isCommonExpressionParticipantId(pId)) {
                  pId=CommonExpressionParticipants.getInstance().getIdFromVisualOrderEA(pId);
                  p=CommonExpressionParticipants.getInstance().getCommonExpressionParticipant(wpOrAs, pId);
               }
            }
            if (p != null) {
               ParticipantInfo pi = new ParticipantInfo(p);
               List afp = GraphUtilities.getAllActivitiesForParticipantId(ownedActivities, pId);
               pi.setActivities(afp);
               ownedActivities.removeAll(afp);
//               System.err.println("Gathered par "+pi);
               gatherInto.add(pi);
            } else {
               if (dpInfo==null) {
                  dpInfo = new ParticipantInfo(FreeTextExpressionParticipant.getInstance());
//                  System.err.println("Gathered par "+dpInfo);
                  gatherInto.add(dpInfo);
               }
            }
         }
         if (dpInfo != null) {
            dpInfo.setActivities(ownedActivities);
         }
      }
//      CommonExpressionParticipants.getInstance().printList(wpOrAs);
//System.err.println("all gathered participants for "+wpOrAs.getId()+" are:"+gatherInto);
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

   protected static Activity getReferencingBlockActivity(ActivitySet as) {
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(as);
      Activity act = GraphUtilities.getReferencingBlockActivity(wp.getActivities(), as.getId());
      if (act == null) {
         Iterator it = wp.getActivitySets().toElements().iterator();
         while (it.hasNext()) {
            ActivitySet aset = (ActivitySet) it.next();
            act = GraphUtilities.getReferencingBlockActivity(aset.getActivities(), as.getId());
            if (act != null)
               break;
         }
      }
      return act;
   }

   protected static Activity getReferencingBlockActivity(Activities acts, String asId) {
      Iterator it = acts.toElements().iterator();
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_BLOCK) {
            String bid = act.getActivityTypes().getBlockActivity().getBlockId();
            if (bid.equals(asId)) {
               return act;
            }
         }
      }
      return null;
   }

   public static boolean scanExtendedAttributes(Package pkg) {
//      JaWEManager.getInstance().getLoggingManager().debug("Scanning extended attributes for package " + pkg.getId());
      Iterator wps = pkg.getWorkflowProcesses().toElements().iterator();
      boolean changed = false;
      while (wps.hasNext()) {
         WorkflowProcess wp = (WorkflowProcess) wps.next();
         changed = GraphUtilities.scanExtendedAttributes(wp) || changed;
      }      
      return changed;
   }

   protected static boolean scanExtendedAttributes(WorkflowProcess wp) {
//      JaWEManager.getInstance().getLoggingManager().debug(
//            "Scanning extended attributes for workflow process " + wp.getId());
      boolean changed = GraphUtilities.scanExtendedAttributesForWPOrAs(wp);
      Iterator it = wp.getActivitySets().toElements().iterator();
      while (it.hasNext()) {
         ActivitySet as = (ActivitySet) it.next();
         changed = scanExtendedAttributes(as) || changed;
      }
      return changed;
   }

   protected static boolean scanExtendedAttributes(ActivitySet as) {
//      JaWEManager.getInstance().getLoggingManager()
//            .debug("Scanning extended attributes for activity set " + as.getId());
      return scanExtendedAttributesForWPOrAs(as);
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

   public static boolean scanExtendedAttributesForWPOrAs(XMLCollectionElement wpOrAs) {
      boolean changed = false;
      Participant defaultP = FreeTextExpressionParticipant.getInstance();
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(wpOrAs);
      ExtendedAttributes wpEAs=wp.getExtendedAttributes();
      Activities acts = (Activities) wpOrAs.get("Activities");
      List ownedActivities = acts.toElements();

      // participants required by XPDL model
      List participants = GraphUtilities.getParticipants(acts);

      Map pIdToPar = new HashMap();
      for (int i = 0; i < participants.size(); i++) {
         Participant par = (Participant) participants.get(i);
         pIdToPar.put(par.getId(), par);
      }

      // read visual order e.a. if any, and append participants that are contained there
      boolean newAttrib = true;
      List vo = new ArrayList();
      ExtendedAttribute eavo = GraphUtilities.getParticipantVisualOrderEA(wpOrAs);
      if (eavo == null) {
         newAttrib = false;
         eavo = GraphUtilities.getParticipantVisualOrderEAOld(wpOrAs);
         if (eavo != null) {
            vo = GraphUtilities.getParticipantVisualOrderOld(wpOrAs);
            ((ExtendedAttributes)eavo.getParent()).remove(eavo);
            changed=true;
         }
      } else {
         vo = GraphUtilities.getParticipantVisualOrder(wpOrAs);
      }

      List toAdd = new ArrayList(vo);
      toAdd.removeAll(pIdToPar.keySet());
      // do not add if appropriate participant exists
      for (int i = 0; i < toAdd.size(); i++) {
         String pId = (String) toAdd.get(i);
         Participant p = XMLUtil.findParticipant(JaWEManager.getInstance().getXPDLHandler(),wp, pId);
         if (p != null && !participants.contains(p)) {
            participants.add(p);
            pIdToPar.put(pId,p);
         } else if (CommonExpressionParticipants.getInstance().isCommonExpressionParticipantId(pId)) {
            String pIdForP=CommonExpressionParticipants.getInstance().getIdFromVisualOrderEA(pId);
            CommonExpressionParticipant cep=CommonExpressionParticipants.getInstance().getCommonExpressionParticipant(wpOrAs, pIdForP);
            if (cep==null) {
               cep=CommonExpressionParticipants.getInstance().generateCommonExpressionParticipant(wpOrAs);
               cep.setId(pIdForP);
            }
            participants.add(cep);
            pIdToPar.put(pIdForP,cep);
         } else if (defaultP.getId().equals(pId) && !participants.contains(defaultP)) {
            participants.add(defaultP);
            pIdToPar.put(pId,defaultP);
            
         }
      }

      SequencedHashMap gatherInto = new SequencedHashMap();
      // initial gathering of activities per participant, without considering
      // ext. attribs.
      boolean hasDefaultPerformer = false;
      for (int i = 0; i < participants.size(); i++) {
         Participant p = (Participant) participants.get(i);
//         System.out.println("Processing p: id=" + p.getId() + ", n=" + p.getName());
         if (p == defaultP) {
            hasDefaultPerformer = true;
            continue;
         }
         List afp = GraphUtilities.getActivitiesWithPerformer(ownedActivities, p.getId());
//         System.out.println("Acts for p: id=" + p.getId() + ", n=" + p.getName() + " are: " + afp);
         ownedActivities.removeAll(afp);
         ParticipantInfo pi = new ParticipantInfo(p);
         Iterator it = afp.iterator();
         while (it.hasNext()) {
            Activity act = (Activity) it.next();
            ExtendedAttribute ea = GraphUtilities.getParticipantIdEA(act);
            if (ea != null) {
               String pId = ea.getVValue();
               String perf = act.getPerformer();
               if (!pId.equals(perf)) {
                  if (!pIdToPar.containsKey(perf)) {
                     ea.setVValue(defaultP.getId());
                  } else {
                     ea.setVValue(perf);                     
                  }
                  changed = true;
               }
            } else {
               ea=GraphUtilities.getParticipantIdEAOld(act);
               if (ea!=null) {
                  ((ExtendedAttributes)ea.getParent()).remove(ea);
               }
               ea = GraphUtilities.createParticipantIdEA(act, act.getPerformer(), false);
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
         Set ceps=CommonExpressionParticipants.getInstance().getCommonExpressionParticipants(wpOrAs);
         Iterator it=ceps.iterator();
         while (it.hasNext()) {
            CommonExpressionParticipant cep=(CommonExpressionParticipant)it.next();            
            gatherInto.put(cep.getId(), new ParticipantInfo(cep));            
         }
         // read ext attribs to see if route/block/subflow acts are placed
         // somewhere else
//         System.out.println("Further processing acts " + ownedActivities);
         for (int i = 0; i < ownedActivities.size(); i++) {
            Activity act = (Activity) ownedActivities.get(i);
            ExtendedAttributes actEAs=act.getExtendedAttributes();
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
            // if there is no new JaWE e.a. try to convert from old JaWE e.a. if
            // any
            if (ea == null) {
               ea = GraphUtilities.getParticipantIdEAOld(act);
               if (ea != null) {
                  String pId = ea.getVValue();
                  ParticipantInfo pinf = (ParticipantInfo) gatherInto.get(pId);
                  if (pinf == null) {
                     actEAs.remove(ea);
//                     System.err.println("RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRr1");
                     ea = GraphUtilities.createParticipantIdEA(act, defaultP.getId(), false);
                     actEAs.add(0, ea);
                     changed = true;
                  } else {
                     actEAs.remove(ea);
//                     System.err.println("RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRr2");
                     ea = GraphUtilities.createParticipantIdEA(act, pId, false);
                     actEAs.add(0, ea);
                     changed = true;
                     if (pinf != pi) {
                        pi.removeActivity(act);
                        pinf.addActivity(act);
                     }
                  }
               }
            }
            // if there is no e.a (new or old) add e.a. for default expression
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
      if (eavo!=null) {
         List newVO = new ArrayList();
         Iterator it=gatherInto.sequence().iterator();
         while (it.hasNext()) {
            String pId=(String)it.next();
            Participant par=(Participant)pIdToPar.get(pId);
            if (par instanceof CommonExpressionParticipant) {
               pId=CommonExpressionParticipants.getInstance().getIdForVisualOrderEA(pId);
            }
            newVO.add(pId);
         }         
         List addToVo=new ArrayList(newVO);
         addToVo.removeAll(vo);
         List removeFromVo = new ArrayList(vo);
         removeFromVo.removeAll(newVO);
         // do not remove if appropriate participant exists
         it = removeFromVo.iterator();
         while (it.hasNext()) {
            String pId = (String) it.next();
            Participant p = XMLUtil.findParticipant(JaWEManager.getInstance().getXPDLHandler(),wp, pId);
            if (p==null) {
               p=CommonExpressionParticipants.getInstance().getCommonExpressionParticipant(wpOrAs, pId);
            }
            if (p != null || pId.equals(defaultP.getId())) {
               it.remove();
            }
         }
         vo.removeAll(removeFromVo);
         vo.addAll(addToVo);
         if (!newAttrib) {
            if (vo.size() > 0) {
               eavo = GraphUtilities.createParticipantVisualOrderEA(wpOrAs, GraphUtilities
                     .createParticipantVisualOrderEAVal(wpOrAs, vo), false);
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
         eavo = GraphUtilities.createParticipantVisualOrderEA(wpOrAs, GraphUtilities.createParticipantVisualOrderEAVal(
               wpOrAs, gatherInto.sequence()), false);
//         System.out.println("Created vo attr for " + wpOrAs.getId() + "=" + eavo.getVValue() + ", vosize="+ gatherInto.sequence().size());
         ((ExtendedAttributes) eavo.getParent()).add(0, eavo);
         changed = true;
      }
      

      // activity positions - read e.a. if exist, otherwise perform some kind of
      // layout
      Iterator it = gatherInto.values().iterator();
      while (it.hasNext()) {
         ParticipantInfo pi = (ParticipantInfo) it.next();
         List actsForParticipant = pi.getActivities();
//         System.out.println("Final acts for p:" + pi.getParticipant().getId() + " are " + actsForParticipant);
         int incX = 2 * 85;
         int incY = 55;
         int translateX = 10;
         int translateY = 10;
         double chngDir = (int) Math.sqrt(actsForParticipant.size());
         int cnt = 0;
         for (int i = 0; i < actsForParticipant.size(); i++) {
            Activity act = (Activity) actsForParticipant.get(i);
            ExtendedAttribute ea = GraphUtilities.getOffsetPointEA(act);
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
               ea = GraphUtilities.createOffsetPointEA(act, (off.x + "," + off.y), false);
               ((ExtendedAttributes) ea.getParent()).add(0, ea);
               changed = true;
            }
         }
      }

      // handle start/ends
      List sds=GraphUtilities.getStartOrEndDescriptions(wpOrAs, true);
      if (sds.size() == 0) {
         sds = GraphUtilities.getStartOrEndDescriptionsOld(wpOrAs, true);         
         it = sds.iterator();
         while (it.hasNext()) {
            StartEndDescription sed = (StartEndDescription) it.next();
            ExtendedAttribute ea = (ExtendedAttribute) wpEAs.generateNewElement();
            ea.setName(sed.getEAName());
            ea.setVValue(sed.toString());
            wpEAs.add(0, ea);
            changed = true;
         }
      }
      List eds=GraphUtilities.getStartOrEndDescriptions(wpOrAs, false);
      if (eds.size() == 0) {
         eds = GraphUtilities.getStartOrEndDescriptionsOld(wpOrAs, false);
         it = eds.iterator();
         while (it.hasNext()) {
            StartEndDescription sed = (StartEndDescription) it.next();
            ExtendedAttribute ea = (ExtendedAttribute) wpEAs.generateNewElement();
            ea.setName(sed.getEAName());
            ea.setVValue(sed.toString());
            wpEAs.add(0, ea);
            changed = true;
         }
      }

      String asId=null;
      if (wpOrAs instanceof ActivitySet) {
         asId=wpOrAs.getId();
      } 
      List bubbles=new ArrayList(sds);
      bubbles.addAll(eds);
      it=bubbles.iterator();
      Set eastoremove=new HashSet();
      while (it.hasNext()) {
         StartEndDescription sed = (StartEndDescription) it.next();
         if (!gatherInto.containsKey(sed.getParticipantId())) {
            eastoremove.addAll(GraphUtilities.getStartOrEndExtendedAttributes(wp, asId, sed.getParticipantId(),
                  GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID));            
//            System.err.println("Removing sed1 "+sed.toString()+"\n..... because gatherInto doesn't have key "+sed.getParticipantId());
         }
         if (sed.getActId()!=null && wp.getActivity(sed.getActId())==null) {
            eastoremove.addAll(GraphUtilities.getStartOrEndExtendedAttributes(wp, asId, sed.getActId(),
                  GraphEAConstants.EA_PART_CONNECTING_ACTIVITY_ID));            
//            System.err.println("Removing sed2 "+sed.toString());
         }
         if (sed.getActSetId()!=null && wp.getActivitySet(sed.getActSetId())==null) {
            eastoremove.addAll(GraphUtilities.getStartOrEndExtendedAttributes(wp, asId, sed.getActSetId(),
                  GraphEAConstants.EA_PART_ACTIVITY_SET_ID));            
//            System.err.println("Removing sed3 "+sed.toString());
         }
      }
      if (eastoremove.size()>0) {
         wpEAs.removeAll(new ArrayList(eastoremove));
         changed=true;
      }
      // handle transitions
      Transitions tras = (Transitions) wpOrAs.get("Transitions");
      it = tras.toElements().iterator();
      while (it.hasNext()) {
         Transition tra = (Transition) it.next();
         ExtendedAttribute ea = GraphUtilities.getStyleEA(tra);
         if (ea == null) {
            String oldStyle = GraphUtilities.getStyleOld(tra);
            ea = GraphUtilities.createStyleEA(tra, GraphUtilities.getNewStyle(tra, oldStyle), false);
            ((ExtendedAttributes) ea.getParent()).add(0, ea);
            changed = true;
         } else {
            String style = GraphUtilities.getStyle(tra);
            if (!GraphEAConstants.transitionStyles.contains(style)) {
               ea.setVValue(GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_ORTHOGONAL);
               changed = true;
            }
         }
         ea = null;
         ea = GraphUtilities.getBreakpointsEA(tra);
         if (ea == null) {
            List bps = GraphUtilities.getBreakpointsOld(tra);
            if (bps.size() > 0) {
               ea = GraphUtilities.createBreakpointsEA(tra, GraphUtilities.createBreakpointsEAVal(bps), false);
               ((ExtendedAttributes) ea.getParent()).add(0, ea);
               changed = true;
            }
         }
      }
      //System.err.println("FINAL GPDws="+gatherInto);

      return changed;
   }

   protected static List getParticipants(Activities acts) {
      List pars = new ArrayList();

      List performers = GraphUtilities.getAllPossiblePerformers(acts);

      WorkflowProcess wp = XMLUtil.getWorkflowProcess(acts);

      Participant defaultP = FreeTextExpressionParticipant.getInstance();
      for (int i = 0; i < performers.size(); i++) {
         String perf = (String) performers.get(i);
         Participant p = XMLUtil.findParticipant(JaWEManager.getInstance().getXPDLHandler(),wp, perf);
         if (p == null) {
            p = defaultP;
         }
         if (!pars.contains(p)) {
            pars.add(p);
         }
      }

      //      if (pars.size()==0 && acts.size()>0) {
      //         pars.add(defaultP);
      //      }

      return pars;
   }

   protected static List getAllPossiblePerformers(Activities acts) {
      List pps = new ArrayList();

      List types = new ArrayList();
      types.add(new Integer(XPDLConstants.ACTIVITY_TYPE_NO));
      types.add(new Integer(XPDLConstants.ACTIVITY_TYPE_TOOL));

      Iterator it = JaWEManager.getInstance().getXPDLUtils().getActivities(acts, types).iterator();
      while (it.hasNext()) {
         String perf = ((Activity) it.next()).getPerformer();
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
                  || eaname.equals(GraphEAConstants.EA_JAWE_GRAPH_BLOCK_PARTICIPANT_ORDER)
                  || eaname.equals(GraphEAConstants.EA_JAWE_GRAPH_END_OF_WORKFLOW)
                  || eaname.equals(GraphEAConstants.EA_JAWE_GRAPH_START_OF_WORKFLOW)
                  || eaname.equals(GraphEAConstants.EA_JAWE_GRAPH_END_OF_BLOCK) || eaname
                  .equals(GraphEAConstants.EA_JAWE_GRAPH_START_OF_BLOCK))) {
         isMK = true;
      } else if (eas.getParent() instanceof Activity
            && (eaname.equals(GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID) || eaname
                  .equals(GraphEAConstants.EA_JAWE_GRAPH_OFFSET))) {
         isMK = true;
      } else if (eas.getParent() instanceof Transition
            && (eaname.equals(GraphEAConstants.EA_JAWE_GRAPH_BREAK_POINTS) || eaname
                  .equals(GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE))) {
         isMK = true;
      }

      return isMK;
   }

   // MUST NOT CHANGE ANYTHING ON THE XPDL MODEL - JUST PERFORM GRAPH CHANGES
   public static void adjustPackageOnUndoOrRedoEvent(List allInfo) {
      Package pkg = JaWEManager.getInstance().getJaWEController().getMainPackage();
      GraphController graphController = GraphUtilities.getGraphController();

      XMLCollectionElement wpOrAs=GraphUtilities.getRotatedGraphObject(allInfo);
      if (wpOrAs!=null) {
         Graph g=graphController.getGraph(wpOrAs);
         Object[] elem = JaWEGraphModel.getAll(g.getModel());
         g.getModel().remove(elem);

         g.getGraphManager().createWorkflowGraph(g.getXPDLObject());
         return;
      }
      
      Set insertedProcesses = GraphUtilities.getInsertedOrRemovedWorkflowProcesses(allInfo, true);
      Set insertedActivitySets = GraphUtilities.getInsertedOrRemovedActivitySets(allInfo, true);
      Set removedProcesses = GraphUtilities.getInsertedOrRemovedWorkflowProcesses(allInfo, false);
      Set removedActivitySets = GraphUtilities.getInsertedOrRemovedActivitySets(allInfo, false);

//      LoggingManager lm = JaWEManager.getInstance().getLoggingManager();
//      lm.debug("GraphUtilities -> adjusting pkg " + pkg.getId() + " on undo/redo event");
//      lm.debug("    Inserted processes: " + insertedProcesses);
//      lm.debug("    Removed processes: " + removedProcesses);
//      lm.debug("    Inserted activity sets: " + insertedActivitySets);
//      lm.debug("    Removed activity sets: " + removedActivitySets);

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

         GraphUtilities.adjustWorkflowProcessOrActivitySetOnUndoOrRedoEvent(allInfo, wp);
         Iterator asi = wp.getActivitySets().toElements().iterator();
         while (asi.hasNext()) {
            ActivitySet as = (ActivitySet) asi.next();

            if (insertedActivitySets.contains(as))
               continue;

            GraphUtilities.adjustWorkflowProcessOrActivitySetOnUndoOrRedoEvent(allInfo, as);
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
   public static void adjustWorkflowProcessOrActivitySetOnUndoOrRedoEvent(List allInfo, XMLCollectionElement wpOrAs) {
      Graph graph = GraphUtilities.getGraphController().getGraph(wpOrAs);

      WorkflowProcess wp = XMLUtil.getWorkflowProcess(wpOrAs);

      Participant defaultPar = FreeTextExpressionParticipant.getInstance();

      GraphManager gmgr = graph.getGraphManager();

      List vo = GraphUtilities.getParticipantVisualOrder(wpOrAs);
      

//      org.enhydra.jawe.base.logger.LoggingManager lm = JaWEManager.getInstance().getLoggingManager();
//      lm.debug("GraphUtilities->adjusting wp or as " + wpOrAs.getId());
//      lm.debug("    Activities to update graph position: " + graphUpdPosActs);
//      lm.debug("    Inserted activities: " + insertedActivities);
//      lm.debug("    Removed activities: " + removedActs);
//      lm.debug("    Inserted transitions: " + insertedTrans);
//      lm.debug("    Updated transitions: " + updatedTrans);
//      lm.debug("    Removed transitions: " + removedTrans);
//      lm.debug("    PVO start: " + vo);

      List currentPars = new ArrayList();
      for (int i = 0; i < vo.size(); i++) {
         String parId = (String) vo.get(i);
//         System.err.println("Par to search="+parId);
         boolean isCEP=CommonExpressionParticipants.getInstance().isCommonExpressionParticipantId(parId);
         if (isCEP) {
            parId=CommonExpressionParticipants.getInstance().getIdFromVisualOrderEA(parId);            
         }
//         System.err.println("Par to search final="+parId);
         Participant par = XMLUtil.findParticipant(JaWEManager.getInstance().getXPDLHandler(),wp, parId);
//System.err.println("Handling par "+par);
         if (par == null) { // this must be default participant
            if (!isCEP) {
               par = defaultPar;
            } else {
               par=CommonExpressionParticipants.getInstance().getCommonExpressionParticipant(wpOrAs, parId);
               if (par==null) {
                  par=CommonExpressionParticipants.getInstance().getUpdatedCommonExpressionParticipant(vo, wpOrAs);
                  if (par==null) {
                     par=CommonExpressionParticipants.getInstance().generateCommonExpressionParticipant(wpOrAs);
                  }
                  par.setId(parId);
               }
            }
         }
         // if there is such participant in graph, switch user objects for any case.
         // It could be that the participants have the same Id, but are really
         // not the same
//         GraphParticipantInterface gpar = gmgr.getGraphParticipant(parId);
//         if (gpar != null) {
//            gpar.setUserObject(par);
//System.err.println("USER OBJECT REPLACED WITH "+par);
//Thread.dumpStack();
//         }
         currentPars.add(par);         
      }

      CommonExpressionParticipants.getInstance().removeUnusedCommonExpressionParticipants(vo, wpOrAs);
      
      Map pkgParsWithChangedIds = GraphUtilities.getPackageParticipantsWithChangedId(allInfo);
      Map wpParsWithChangedIds = GraphUtilities.getWorkflowProcessParticipantsWithChangedId(allInfo, XMLUtil
            .getWorkflowProcess(wpOrAs));
      for (int i=0; i<currentPars.size(); i++) {
         Participant par=(Participant)currentPars.get(i);
         String pId=par.getId();
         if (pkgParsWithChangedIds.containsKey(pId) || wpParsWithChangedIds.containsKey(pId)) {
            reloadGraph(graph);
            return;
         }
      }
      if (reloadGraphIfNeccessary(graph)) {
         return;
      }
            
      
      Set participantsToRemoveFromGraph = new HashSet();

      Set insertedActivities = GraphUtilities.getInsertedOrRemovedActivities(allInfo, wpOrAs, true);
      Set removedActs = GraphUtilities.getInsertedOrRemovedActivities(allInfo, wpOrAs, false);
      Set graphUpdPosActs = GraphUtilities.getActivitiesWithChangedOffset(allInfo, wpOrAs);
      graphUpdPosActs.addAll(GraphUtilities.getActivitiesWithChangedParticipantId(allInfo, wpOrAs));
      graphUpdPosActs.removeAll(removedActs);
      Set insertedTrans = GraphUtilities.getInsertedOrRemovedTransitions(allInfo, wpOrAs, true);
      Set removedTrans = GraphUtilities.getInsertedOrRemovedTransitions(allInfo, wpOrAs, false);
      Set updatedTrans = GraphUtilities.getUpdatedTransitions(allInfo, wpOrAs);
      updatedTrans.addAll(GraphUtilities.getTransitionsWithChangedBreakpointsOrStyle(allInfo, wpOrAs));
      updatedTrans.removeAll(removedTrans);
      Set insertedBubbles = GraphUtilities.getInsertedOrRemovedBubbles(allInfo, wpOrAs, true);
      Set removedBubbles = GraphUtilities.getInsertedOrRemovedBubbles(allInfo, wpOrAs, false);
      Set updatedBubbles = GraphUtilities.getUpdatedBubbles(allInfo, wpOrAs);
      updatedBubbles.removeAll(removedBubbles);
      
      List partsInGraph = new ArrayList();
      List allGraphParticipants = JaWEGraphModel.getAllParticipantsInModel(graph.getModel());
      if (allGraphParticipants != null) {
         Iterator it = allGraphParticipants.iterator();
         while (it.hasNext()) {
            GraphParticipantInterface gpar = (GraphParticipantInterface) it.next();
            partsInGraph.add(gpar.getUserObject());
         }
//System.err.println("PING="+partsInGraph);         
      }
      
      // get missing participants
      Set participantsToInsertIntoGraph = new HashSet(currentPars);
      participantsToInsertIntoGraph.removeAll(partsInGraph);
      participantsToRemoveFromGraph.addAll(partsInGraph);
      participantsToRemoveFromGraph.removeAll(currentPars);
      Map participantsToReplace=new HashMap();
      Iterator itp=participantsToRemoveFromGraph.iterator();
      while (itp.hasNext()) {
         Participant p=(Participant)itp.next();
         GraphParticipantInterface gpar=gmgr.getGraphParticipant(p);
         Set chas=gpar.getChildActivities();
         if (chas!=null && chas.size()>0) {
            Iterator ita=chas.iterator();
            while (ita.hasNext()) {               
               GraphActivityInterface ga=(GraphActivityInterface)ita.next();
               if (!(ga instanceof GraphBubbleActivityInterface) && 
                     !removedActs.contains(ga.getUserObject()) && !graphUpdPosActs.contains(ga.getUserObject())) {                  
                  String pId=GraphUtilities.getParticipantId((Activity)ga.getUserObject());
                  Participant toRep=XMLUtil.findParticipant(JaWEManager.getInstance().getXPDLHandler(),wp, pId);
                  if (toRep!=null) {
                     participantsToReplace.put(p, toRep);
                  }
               }
                     
            }
         }         
      }
//System.err.println("PTR="+participantsToReplace);
      Iterator itm=participantsToReplace.entrySet().iterator();
      while (itm.hasNext()) {
         Map.Entry me=(Map.Entry)itm.next();
         Participant pold=(Participant)me.getKey();
         Participant pnew=(Participant)me.getValue();
         GraphParticipantInterface gpar=gmgr.getGraphParticipant(pold);
         gpar.setUserObject(pnew);
         participantsToInsertIntoGraph.remove(pnew);
         participantsToRemoveFromGraph.remove(pold);         
      }
      
//      lm.debug("    Participants to insert into graph: " + participantsToInsertIntoGraph);
//      lm.debug("    Pars to remove from graph: " + participantsToRemoveFromGraph);
      
      
      // Insert graph participants
      Iterator it = participantsToInsertIntoGraph.iterator();
      while (it.hasNext()) {
         Participant par = (Participant) it.next();
         gmgr.insertParticipantAndArrangeParticipants(par);
      }

      // remove transitions that are not longer present
      it = removedTrans.iterator();
      while (it.hasNext()) {
         Transition tra = (Transition) it.next();
         gmgr.removeTransition(tra);
      }

      // remove activities that are not longer present
      it = removedActs.iterator();
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         gmgr.removeActivity(act);
      }

      // insert new activities
      it = insertedActivities.iterator();
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         gmgr.insertActivity(act);
         // adjust bubbles if neccessary (re-connect them)
//         List seeas = GraphUtilities.getStartOrEndExtendedAttributes(wp, asId, act.getId(),
//               GraphEAConstants.EA_PART_CONNECTING_ACTIVITY_ID);
//         for (int i = 0; i < seeas.size(); i++) {
//            ExtendedAttribute ea = (ExtendedAttribute) seeas.get(i);
//            GraphBubbleActivityInterface bubble = gmgr.getBubble(ea);
//            gmgr.connectStartOrEndBubble(bubble, act.getId());
//         }
      }

      // insert new transitions
      it = insertedTrans.iterator();
      while (it.hasNext()) {
         Transition tra = (Transition) it.next();
         gmgr.insertTransition(tra);
      }

      // adjusted activity position
//      lm.debug("    Activities to update position: " + graphUpdPosActs);
      it = graphUpdPosActs.iterator();
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         gmgr.arrangeActivityPosition(act);
      }

      // update transitions that changed source or target
      it = updatedTrans.iterator();
      while (it.hasNext()) {
         Transition tra = (Transition) it.next();
         gmgr.updateTransition(tra);
      }

      // remove bubbles
      it = removedBubbles.iterator();
      while (it.hasNext()) {
         ExtendedAttribute ea = (ExtendedAttribute) it.next();
         gmgr.removeBubble(ea);
      }

      // insert new bubbles
      it = insertedBubbles.iterator();
      while (it.hasNext()) {
         ExtendedAttribute ea = (ExtendedAttribute) it.next();
         String eaName=ea.getName();
         if (eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_START_OF_WORKFLOW)
               || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_START_OF_BLOCK)) {
            gmgr.insertStart(ea);
         } else {
            gmgr.insertEnd(ea);
         }
      }

      // update bubbles
      it = updatedBubbles.iterator();
      while (it.hasNext()) {
         ExtendedAttribute ea = (ExtendedAttribute) it.next();
         //         gmgr.removeBubble(ea);
         //         if (ea.getName().equals(GraphEAConstants.EA_JAWE_GRAPH_START_OF_WORKFLOW) ||
         //               ea.getName().equals(GraphEAConstants.EA_JAWE_GRAPH_START_OF_BLOCK)) {
         //            gmgr.insertStart(ea);
         //         } else {
         //            gmgr.insertEnd(ea);
         //         }
         gmgr.updateBubble(ea);
      }

      // remove participants that are not longer present
      if (participantsToRemoveFromGraph.size() > 0) {
         List gparstorem = new ArrayList();
         it = participantsToRemoveFromGraph.iterator();
         while (it.hasNext()) {
            GraphParticipantInterface gpar = gmgr.getGraphParticipant((Participant) it.next());
            gparstorem.add(gpar);
         }
         gmgr.removeCellsAndArrangeParticipants(gparstorem.toArray());
      }

      // sort graph participants
      allGraphParticipants = JaWEGraphModel.getAllParticipantsInModel(graph.getModel());
      if (allGraphParticipants != null) {
         GraphParticipantComparator gpc = new GraphParticipantComparator(gmgr);
         Collections.sort(allGraphParticipants, gpc);

         List helper = new ArrayList(allGraphParticipants);
         Map propertyMap = new HashMap();
         ParentMap parentMap = new JaWEParentMap();
         boolean updated = false;
         for (int i = helper.size() - 1; i >= 0; i--) {
            GraphParticipantInterface gpar = (GraphParticipantInterface) helper.get(i);
            Participant par = (Participant) gpar.getUserObject();
            String parIdForVO=par.getId();
            if (par instanceof CommonExpressionParticipant) {
               parIdForVO=CommonExpressionParticipants.getInstance().getIdForVisualOrderEA(parIdForVO);
            }

            int realInd = vo.indexOf(parIdForVO);
            int currentPos = allGraphParticipants.indexOf(gpar);
            List toMove = new ArrayList();
            toMove.add(gpar);
            if (realInd != currentPos) {
               int diff = realInd - currentPos;
               //            System.out.println("Repositioning participant "+gpar+" for "+diff+",
               // oi="+currentPos+", ni="+realInd);
               for (int j = 0; j < Math.abs(diff); j++) {
                  updated = gmgr.moveParticipants(toMove, (diff < 0), propertyMap, parentMap) || updated;
               }
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

//      LoggingManager lm = JaWEManager.getInstance().getLoggingManager();
//      lm.debug("GraphUtilities->adjusting pkg " + pkg.getId());
//      lm.debug("    Inserted processes: " + insertedProcesses);
//      lm.debug("    Removed processes: " + removedProcesses);
//      lm.debug("    Inserted activity sets: " + insertedActivitySets);
//      lm.debug("    Removed activity sets: " + removedActivitySets);

      // NOTE: order of insertion/removal/updating is VERY IMPORTANT
      //       because of activity set related extended attributes
      //       that are defined as e.attribs of its process (as does
      //       not have e.attribs)

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
         // first remove all extended attributes from activity set's process
         ExtendedAttributes eas = XMLUtil.getWorkflowProcess(as).getExtendedAttributes();
         List seds = GraphUtilities.getStartOrEndExtendedAttributes(as, false);
         seds.addAll(GraphUtilities.getStartOrEndExtendedAttributes(as, true));
         Iterator sedit = seds.iterator();
         while (sedit.hasNext()) {
            ExtendedAttribute ea = (ExtendedAttribute) sedit.next();
            eas.remove(ea);
         }
         eas.remove(GraphUtilities.getParticipantVisualOrderEA(as));
         graphController.removeGraph(as);
      }

      // insert added processes and their activity sets
      it = insertedProcesses.iterator();
      while (it.hasNext()) {
         WorkflowProcess wp = (WorkflowProcess) it.next();
         GraphUtilities.scanExtendedAttributesForWPOrAs(wp);
         graphController.createGraph(wp);
         Iterator asi = wp.getActivitySets().toElements().iterator();
         while (asi.hasNext()) {
            ActivitySet as = (ActivitySet) asi.next();

            GraphUtilities.scanExtendedAttributesForWPOrAs(as);
            graphController.createGraph(as);
         }
      }

      // update Id part for block visual order e.attribs for activity sets that changed Id
      // and also the same for start/end of block and participant orientation attributes
      it = activitySetsWithChangedId.entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry me = (Map.Entry) it.next();
         String oldId = (String) me.getKey();
         ActivitySet as = (ActivitySet) me.getValue();
         WorkflowProcess wp = XMLUtil.getWorkflowProcess(as);

         // visual order
         ExtendedAttribute ea = GraphUtilities.getParticipantVisualOrderEA(wp, oldId);
         List vo = GraphUtilities.getParticipantVisualOrder(wp, oldId);
         ea.setVValue(GraphUtilities.createParticipantVisualOrderEAVal(as, vo));

         // participant orientation
         ea = GraphUtilities.getGraphParticipantOrientationEA(wp, oldId);
         if (ea != null) {
            String[] parts = Utils.tokenize(ea.getVValue(), ";");
            String orientation = parts[1];
            ea.setVValue(as.getId() + ";" + orientation);
         }

         // start/end attributes
         GraphUtilities.adjustBubbles(wp, oldId, GraphEAConstants.EA_PART_ACTIVITY_SET_ID, oldId, as.getId());
      }

      // update other processes and activity sets
      it = pkg.getWorkflowProcesses().toElements().iterator();
      while (it.hasNext()) {
         WorkflowProcess wp = (WorkflowProcess) it.next();

         if (insertedProcesses.contains(wp))
            continue;

         GraphUtilities.adjustWorkflowProcessOrActivitySetOnUndoableChangeEvent(allInfo, wp,null,false);
         Iterator asi = wp.getActivitySets().toElements().iterator();
         while (asi.hasNext()) {
            ActivitySet as = (ActivitySet) asi.next();

            if (insertedActivitySets.contains(as))
               continue;

            GraphUtilities.adjustWorkflowProcessOrActivitySetOnUndoableChangeEvent(allInfo, as,null,false);
         }
      }

      // insert added activity sets
      it = insertedActivitySets.iterator();
      while (it.hasNext()) {
         ActivitySet as = (ActivitySet) it.next();
         GraphUtilities.scanExtendedAttributesForWPOrAs(as);
         graphController.createGraph(as);
      }

   }

   public static void adjustWorkflowProcessOrActivitySetOnUndoableChangeEvent(List allInfo, XMLCollectionElement wpOrAs,Map extPkgPars,boolean insertedExtPkg) {
      GraphController gc = GraphUtilities.getGraphController();
      Graph graph = gc.getGraph(wpOrAs);
      if (graph==null) {
         System.err.println("can't find graph for wporas "+wpOrAs.getId());         
      }
      GraphManager gmgr = graph.getGraphManager();
      boolean reloaded=GraphUtilities.reloadGraphIfNeccessary(graph);
      if (reloaded) {
         return;
      }
      
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(wpOrAs);
      String asId = null;
      if (wpOrAs instanceof ActivitySet) {
         asId = wpOrAs.getId();
      }

      Map insertedExtPkgPars=new HashMap();
      Map removedExtPkgPars=new HashMap();
      if (extPkgPars!=null) {
         if (insertedExtPkg) {
            insertedExtPkgPars.putAll(extPkgPars);            
         } else {
            removedExtPkgPars.putAll(extPkgPars);
         }
      }
      
      Map pkgParsWithChangedIds = GraphUtilities.getPackageParticipantsWithChangedId(allInfo);
      Map wpParsWithChangedIds = GraphUtilities.getWorkflowProcessParticipantsWithChangedId(allInfo, XMLUtil
            .getWorkflowProcess(wpOrAs));
      Map insertedPkgPars = GraphUtilities.getPackageInsertedOrRemovedParticipants(allInfo, true);
      Map insertedWPPars = GraphUtilities.getWorkflowProcessInsertedOrRemovedParticipants(allInfo, XMLUtil
            .getWorkflowProcess(wpOrAs), true);
      Map removedPkgPars = GraphUtilities.getPackageInsertedOrRemovedParticipants(allInfo, false);
      Map removedWPPars = GraphUtilities.getWorkflowProcessInsertedOrRemovedParticipants(allInfo, XMLUtil
            .getWorkflowProcess(wpOrAs), false);

      
      Map pkgParsWithChangedIds2=new HashMap();
      Iterator itp=pkgParsWithChangedIds.values().iterator();
      while (itp.hasNext()) {
         Participant p=(Participant)itp.next();
         pkgParsWithChangedIds2.put(p.getId(), p);
      }
      Map wpParsWithChangedIds2=new HashMap();
      itp=wpParsWithChangedIds.values().iterator();
      while (itp.hasNext()) {
         Participant p=(Participant)itp.next();
         wpParsWithChangedIds2.put(p.getId(), p);
      }
      
      Participant defaultPar = FreeTextExpressionParticipant.getInstance();
      String defaultParId = defaultPar.getId();

      Set graphParticipantsToRemoveFromGraph = new HashSet();

      Set bubblesToUpdatePosition = new HashSet();
      Set removedBubbles = GraphUtilities.getInsertedOrRemovedBubbles(allInfo, wpOrAs, false);

      Set insertedActivities = GraphUtilities.getInsertedOrRemovedActivities(allInfo, wpOrAs, true);
      Set removedActs = GraphUtilities.getInsertedOrRemovedActivities(allInfo, wpOrAs, false);
      Set activitiesToUpdatePosition = GraphUtilities.getActivitiesWithChangedPerformer(allInfo, wpOrAs);
      activitiesToUpdatePosition.removeAll(removedActs);
      Set graphUpdPosActs = GraphUtilities.getActivitiesWithChangedOffset(allInfo, wpOrAs);
      graphUpdPosActs.removeAll(removedActs);

      Map activitiesWithChangedId = GraphUtilities.getActivitiesWithChangedId(allInfo, wpOrAs);
      Set insertedTrans = GraphUtilities.getInsertedOrRemovedTransitions(allInfo, wpOrAs, true);
      Set removedTrans = GraphUtilities.getInsertedOrRemovedTransitions(allInfo, wpOrAs, false);
      Set updatedTrans = GraphUtilities.getUpdatedTransitions(allInfo, wpOrAs);
      updatedTrans.removeAll(removedTrans);

      List vo = GraphUtilities.getParticipantVisualOrder(wpOrAs);

//      org.enhydra.jawe.base.logger.LoggingManager lm = JaWEManager.getInstance().getLoggingManager();
//      lm.debug("GraphUtilities->adjusting wp or as " + wpOrAs.getId());
//      lm.debug("    pkg pars with changed ids: " + pkgParsWithChangedIds);
//      lm.debug("    wp pars with changed ids: " + wpParsWithChangedIds);
//      lm.debug("    inserted ext pkg pars: " + insertedExtPkgPars);
//      lm.debug("    inserted pkg pars: " + insertedPkgPars);
//      lm.debug("    inserted wp pars: " + insertedWPPars);
//      lm.debug("    Removed ext pkg pars: " + removedExtPkgPars);
//      lm.debug("    Removed pkg pars: " + removedPkgPars);
//      lm.debug("    Removed wp pars: " + removedWPPars);
//      lm.debug("    Activities with changed performer: " + activitiesToUpdatePosition);
//      lm.debug("    Activities with changed id: " + activitiesWithChangedId);
//      lm.debug("    Activities to update position via graph: " + graphUpdPosActs);
//      lm.debug("    Inserted activities: " + insertedActivities);
//      lm.debug("    Removed activities: " + removedActs);
//      lm.debug("    Inserted transitions: " + insertedTrans);
//      lm.debug("    Updated transitions: " + updatedTrans);
//      lm.debug("    Removed transitions: " + removedTrans);
//      lm.debug("    PVO start: " + vo);

      List newVo = new ArrayList(vo);
            
      for (int i = 0; i < vo.size(); i++) {
         String parId = (String) vo.get(i);
//System.err.println("Testing change of pid "+parId);
         Participant changedIdWPPar = (Participant) wpParsWithChangedIds.get(parId);
         Participant changedIdWPPar2 = (Participant) wpParsWithChangedIds2.get(parId);
         Participant changedIdPkgPar = (Participant) pkgParsWithChangedIds.get(parId);
         Participant changedIdPkgPar2 = (Participant) pkgParsWithChangedIds2.get(parId);
         Participant addedIdWPPar = (Participant) insertedWPPars.get(parId);
         Participant addedIdPkgPar = (Participant) insertedPkgPars.get(parId);
         Participant addedIdExtPkgPar = (Participant) insertedExtPkgPars.get(parId);
         Participant removedIdWPPar = (Participant) removedWPPars.get(parId);
         Participant removedIdPkgPar = (Participant) removedPkgPars.get(parId);
         Participant removedIdExtPkgPar=(Participant)removedExtPkgPars.get(parId);

//         System.err.println("CWPId="+changedIdWPPar);
//         System.err.println("CWPId2="+changedIdWPPar2);
//         System.err.println("CPkgId="+changedIdPkgPar);
//         System.err.println("CPkgId2="+changedIdPkgPar2);
//         System.err.println("AWPId="+addedIdWPPar);
//         System.err.println("APkgId="+addedIdPkgPar);
//         System.err.println("AExtPkgId="+addedIdExtPkgPar);
//         System.err.println("RWPId="+removedIdWPPar);
//         System.err.println("RPkgId="+removedIdPkgPar);
//         System.err.println("RExtPkgId="+removedIdExtPkgPar);
         
         List allActsForParId = getAllActivitiesForParticipantId(((Activities) wpOrAs.get("Activities")).toElements(),
               parId);

         Set awcp = GraphUtilities.getActivitiesWithChangedPerformer(allInfo, wpOrAs, parId);
         // wp participant changed Id -> update participantId e.a. for corresponding acts
         if (changedIdWPPar != null) {
            // check if performer change events were caused by the change of participant Id,
            // or the performer was changed in other way
            Iterator it = awcp.iterator();
            while (it.hasNext()) {
               Activity act = (Activity) it.next();
               String newPerf = act.getPerformer();
               // performer change event was caused by participant Id change
               if (!newPerf.equals(changedIdWPPar.getId())) {
                  allActsForParId.remove(act);
               }
            }

            // find participant in Graph - maybe it was package participant before
            GraphParticipantInterface gpar = gmgr.getGraphParticipant(changedIdWPPar.getId());
            if (gpar != null) {
               gpar.setUserObject(changedIdWPPar);
               int pos=newVo.indexOf(parId);
               newVo.remove(parId);
               newVo.add(pos,changedIdWPPar.getId());
               GraphUtilities.setNewParticipantId(allActsForParId, changedIdWPPar.getId());

               // arrange start/end bubbles also
               GraphUtilities.adjustBubbles(wp, asId, GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID, parId,
                     changedIdWPPar.getId());
            }
         } 
         else if (changedIdWPPar2!=null) {
            // find participant in Graph - maybe it was ext. pkg or pkg. participant before
            GraphParticipantInterface gpar = gmgr.getGraphParticipant(changedIdWPPar2.getId());
//System.err.println("CIDWPPAR, oldgpar ="+gpar);
            if (gpar != null) {
               gpar.setUserObject(changedIdWPPar2);

//System.err.println("CIDWPPAR, changed ogp");
            }
         }

         // pkg participant changed Id -> update participantId e.a. for corresponding acts
         else if (changedIdPkgPar != null) {
            // check if performer change events were caused by the change of participant Id,
            // or the performer was changed in other way
            Iterator it = awcp.iterator();
            while (it.hasNext()) {
               Activity act = (Activity) it.next();
               String newPerf = act.getPerformer();
               // performer change event was not caused by participant Id change
               if (!newPerf.equals(changedIdPkgPar.getId())) {
                  allActsForParId.remove(act);
               }
            }
            // change participant in Graph - maybe it was workflow participant before, and it was
            // removed now
            GraphParticipantInterface gpar = gmgr.getGraphParticipant(changedIdPkgPar.getId());
            if (gpar != null) {
               gpar.setUserObject(changedIdPkgPar);
               int pos=newVo.indexOf(parId);
               newVo.remove(parId);
               newVo.add(pos,changedIdPkgPar.getId());
               GraphUtilities.setNewParticipantId(allActsForParId, changedIdPkgPar.getId());

               // arrange start/end bubbles also
               GraphUtilities.adjustBubbles(wp, asId, GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID, parId,
                     changedIdPkgPar.getId());
            }
         }
         else if (changedIdPkgPar2!=null) {
            // find participant in Graph - maybe it was ext. pkg participant before and it was
            // removed now
            GraphParticipantInterface gpar = gmgr.getGraphParticipant(changedIdPkgPar2.getId());
            if (gpar != null) {
               gpar.setUserObject(changedIdPkgPar2);
            }
         }

         // if the participant with this Id was added to workflow process,
         // replace user object of participant graph object
         else if (addedIdWPPar != null) {
            // change participant in Graph - maybe it was package participant before
            GraphParticipantInterface gpar = gmgr.getGraphParticipant(parId);
            if (gpar != null) {
               gpar.setUserObject(addedIdWPPar);
            }
         }

         // if the participant with this Id was added to package ,
         // replace user object of participant graph object
         else if (addedIdPkgPar != null) {
            // change participant in Graph - maybe it was workfloe participant before, and it was
            // removed now
            GraphParticipantInterface gpar = gmgr.getGraphParticipant(parId);
            if (gpar != null) {
               gpar.setUserObject(addedIdPkgPar);
            }
         }

         // if workflow or package participant with this id was removed
         if (removedIdWPPar != null || removedIdPkgPar != null || removedIdExtPkgPar!=null) {
            GraphParticipantInterface gpar = gmgr.getGraphParticipant(parId);
//System.err.println("GPAR="+gpar+", gparuo="+gpar.getUserObject());
            Participant newPar = null;
            if (gpar != null) {
               if (gpar.getUserObject() == removedIdWPPar || gpar.getUserObject() == removedIdPkgPar || gpar.getUserObject() == removedIdExtPkgPar) {
                  // if process participant was removed, try to find if there is a participant
                  // with such Id somewhere in the package level
                  newPar = XMLUtil.findParticipant(JaWEManager.getInstance().getXPDLHandler(),wp, parId);
//System.err.println("NEWPAR for id "+parId+"="+newPar);
                  if (newPar == null) {
                     graphParticipantsToRemoveFromGraph.add(gpar);
                  } else {
                     gpar.setUserObject(newPar);
                  }
               } else {
                  newPar=(Participant)gpar.getUserObject();
               }
            }
            // remove participant from the order if there is no other participant
            // added before, or some participant changed it to be the same as
            // of the one we are removing
            // Also, in that case, add their activities for updating
            if (changedIdWPPar == null && changedIdPkgPar == null && addedIdWPPar == null && addedIdPkgPar == null && addedIdExtPkgPar==null
                  && newPar == null) {
               newVo.remove(parId);
//System.err.println("REMPIDFROMVO "+parId);
               // mark all the activities with participant id e.a. equal to removed performer
               // so they can be moved afterwards to another participant
               activitiesToUpdatePosition.addAll(getAllActivitiesForParticipantId(((Activities) wpOrAs
                     .get("Activities")).toElements(), parId));

               // arrange start/end bubbles also
               bubblesToUpdatePosition.addAll(GraphUtilities.getStartOrEndExtendedAttributes(wp, asId, parId,
                     GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID));
            }
         }

      }
//      lm.debug("    PVO 2: " + newVo);

      Set participantsToInsertIntoGraph = new HashSet();
      // get all participants required by the model, and check against visual order
      List acts=((Activities)wpOrAs.get("Activities")).toElements();
      List actsToMove=new ArrayList();
      for (int i=0; i<acts.size(); i++) {
         Activity act=(Activity)acts.get(i);
         int actType=act.getActivityType();
         if (actType==XPDLConstants.ACTIVITY_TYPE_NO || actType==XPDLConstants.ACTIVITY_TYPE_TOOL) {
            String actPerf=act.getPerformer();
            String actPId=GraphUtilities.getParticipantId(act);
            if (!actPerf.equals(actPId)) {
               if (newVo.contains(actPerf)) {            
                  actsToMove.add(act);
               } else {
                  Participant p=XMLUtil.findParticipant(JaWEManager.getInstance().getXPDLHandler(),wp, actPerf);
//                  System.err.println("Part for aperf "+actPerf+" is "+p);
                  if (p!=null) {
                     actsToMove.add(act);
                     newVo.add(actPerf);
                     participantsToInsertIntoGraph.add(p);
                  }
               }
            }
         }
      }
//      System.err.println("ACTSTOMOVE="+actsToMove);
      activitiesToUpdatePosition.addAll(actsToMove);
      
      bubblesToUpdatePosition.removeAll(removedBubbles);

//      lm.debug("    PVO 3: " + newVo);
//      lm.debug("    Activities with changed performer end: " + activitiesToUpdatePosition);

      // adjust newly inserted activities e.attribs, and activities with updated performer
      boolean pasteInProgress = JaWEManager.getInstance().getJaWEController().getEdit().isPasteInProgress();
      CopyOrCutInfo cci=gc.getCopyOrCutInfo();
      boolean graphPasteInProgress=pasteInProgress && cci!=null && (insertedActivities.size()>0 || insertedTrans.size()>0);
      
      Iterator it = insertedActivities.iterator();      
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         if (graphPasteInProgress) {
            GraphUtilities.adjustPastedActivity(act, newVo, participantsToInsertIntoGraph, cci, gmgr);
         } else {
            GraphUtilities.adjustInsertedOrUpdatedActivity(act, newVo, participantsToInsertIntoGraph);
         }
      }
      it = activitiesToUpdatePosition.iterator();
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         GraphUtilities.adjustInsertedOrUpdatedActivity(act, newVo, participantsToInsertIntoGraph);
      }

      // update visual order
      GraphUtilities.setParticipantVisualOrder(wpOrAs, newVo);
//      lm.debug("    PVO end: " + newVo);

      // Insert graph participants
      it = participantsToInsertIntoGraph.iterator();
      while (it.hasNext()) {
         Participant par = (Participant) it.next();
         gmgr.insertParticipantAndArrangeParticipants(par);
//         lm.debug("    Inserted new graph participant: " + gpar);
         // Insert newly created activities into graph participant
         List l = GraphUtilities.getAllActivitiesForParticipantId(insertedActivities, par.getId());
         for (int i = 0; i < l.size(); i++) {
            Activity act = (Activity) l.get(i);
            gmgr.insertActivity(act);
         }
         insertedActivities.removeAll(l);
         // Adjust position for repositioned activities
         l = GraphUtilities.getAllActivitiesForParticipantId(activitiesToUpdatePosition, par.getId());
         activitiesToUpdatePosition.removeAll(l);
         for (int i = 0; i < l.size(); i++) {
            Activity act = (Activity) l.get(i);
            gmgr.arrangeActivityPosition(act);
         }
      }

      // adjust position for the rest of the activities
      it = activitiesToUpdatePosition.iterator();
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         gmgr.arrangeActivityPosition(act);
      }

      // insert the rest of new activities (some of them were alredy inserted into newly inserted
      // participants)
      it = insertedActivities.iterator();
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         gmgr.insertActivity(act);
      }

      // make ea changes for inserted or updated transitions
      List toUpd = new ArrayList(updatedTrans);
      toUpd.addAll(insertedTrans);
      if (graphPasteInProgress) {
         GraphUtilities.adjustPastedTransitions(toUpd,cci,gmgr);
      } else {
         GraphUtilities.adjustInsertedOrUpdatedTransitions(toUpd,gmgr);
      }

      // remove transitions that are not longer present
      it = removedTrans.iterator();
      while (it.hasNext()) {
         Transition tra = (Transition) it.next();
         gmgr.removeTransition(tra);
      }

      // update transitions that changed source or target
      it = updatedTrans.iterator();
      while (it.hasNext()) {
         Transition tra = (Transition) it.next();
         gmgr.updateTransition(tra);
      }

      // insert new transitions
      it = insertedTrans.iterator();
      while (it.hasNext()) {
         Transition tra = (Transition) it.next();
         gmgr.insertTransition(tra);
      }

      // remove activities that are not longer present
      it = removedActs.iterator();
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         gmgr.removeActivity(act);
         // arrange start/end bubbles also
         GraphUtilities.adjustBubbles(wp, asId, GraphEAConstants.EA_PART_CONNECTING_ACTIVITY_ID, act.getId(), null);
      }

      // remove bubbles
      it = removedBubbles.iterator();
      while (it.hasNext()) {
         ExtendedAttribute ea = (ExtendedAttribute) it.next();
         gmgr.removeBubble(ea);
      }
      
      // handle start/end bubbles for the activities with changed Id
      it = activitiesWithChangedId.entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry me = (Map.Entry) it.next();
         String oldId = (String) me.getKey();
         Activity act = (Activity) me.getValue();
         GraphUtilities.adjustBubbles(wp, asId, GraphEAConstants.EA_PART_CONNECTING_ACTIVITY_ID, oldId, act.getId());
      }

      if (bubblesToUpdatePosition.size() > 0) {
         GraphParticipantInterface gpar = null;
         if (newVo.size() == 0) {
            newVo.add(defaultParId);
            gpar = gmgr.insertParticipantAndArrangeParticipants(defaultPar);
         }
         String parId = (String) newVo.get(0);

         GraphUtilities.adjustBubbles(bubblesToUpdatePosition, GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID, parId);
         it = bubblesToUpdatePosition.iterator();

         if (gpar == null) {
            gpar = gmgr.getGraphParticipant(parId);
         }
         while (it.hasNext()) {
            ExtendedAttribute ea = (ExtendedAttribute) it.next();
            gmgr.arrangeBubblePosition(ea, gpar);
         }
      }

      // remove participants that are not longer present
      if (graphParticipantsToRemoveFromGraph.size() > 0) {
         gmgr.removeCellsAndArrangeParticipants(graphParticipantsToRemoveFromGraph.toArray());
      }

      if (graphPasteInProgress) {
         cci.incrementOffsetPoint(graph);
      }
      graph.repaint();
   }

   public static void adjustInsertedOrUpdatedActivity(Activity act, List vo, Set participantsToInsertIntoGraph) {
      int type = act.getActivityType();
//      System.err.println("Adjusting act " + act + ", type=" + type + ", vo=" + vo);
      ExtendedAttribute ea = GraphUtilities.getParticipantIdEA(act);
      String pId=null;
      if (!(type == XPDLConstants.ACTIVITY_TYPE_NO || type == XPDLConstants.ACTIVITY_TYPE_TOOL)) {
         if (vo.size() == 0) {
            vo.add(FreeTextExpressionParticipant.getInstance().getId());
            participantsToInsertIntoGraph.add(FreeTextExpressionParticipant.getInstance());
         } 
         if (ea == null) {
            pId = (String) vo.get(0);
         } else {
            pId = ea.getVValue();
         }
         if (!vo.contains(pId)) {
            pId = (String) vo.get(0);
         }
      } else {
         Participant p = XMLUtil.findParticipant(JaWEManager.getInstance().getXPDLHandler(),XMLUtil.getWorkflowProcess(act), act.getPerformer());
         if (p == null) {
//            System.err.println("Can't find part for performer " + act.getPerformer());
            p = FreeTextExpressionParticipant.getInstance();
         }
         pId = p.getId();
         if (!vo.contains(pId)) {
            vo.add(pId);
            participantsToInsertIntoGraph.add(p);
         }
      }
      GraphUtilities.setParticipantId(act, pId);
//      System.err.println("VO after adj act is " + vo);
      ea = GraphUtilities.getOffsetPointEA(act);
      if (ea == null) {
         int inw = getGraphController().getGraphSettings().getParticipantNameWidth();
         ea = GraphUtilities.createOffsetPointEA(act, inw + "," + inw, true);
      }
   }

   public static void adjustPastedActivity(Activity act, List vo, Set participantsToInsertIntoGraph,CopyOrCutInfo cci,GraphManager gm) {
//      System.err.println("Adjusting pasted act " + act + ", vo=" + vo+" for proc/as "+gm.getXPDLOwner().getId());
//      System.err.println(cci);
      Point pasteTo=cci.getPastePoint();
      Point pasteOffset=cci.getOffsetPoint(gm.getGraph());
      Point referencePoint=cci.getReferencePoint();
//System.err.println("       ....pasteTo="+pasteTo+", pasteOffset="+pasteOffset+", refPoint="+referencePoint);      
      if (pasteTo!=null) {
         String pId=GraphUtilities.getParticipantId(act);
         Point off=GraphUtilities.getOffsetPoint(act);
         CopiedActivityInfo ai=new CopiedActivityInfo(pId,off);
//         System.err.println("..........Searching for rectangle for the info "+ai);
         Rectangle r=cci.getActivityBounds(ai);
//         System.err.println("..........Rectangle is "+r);
         Point refPoint=referencePoint;
         if (r!=null) {
            refPoint=r.getLocation();
         }
         Point diffPoint=new Point(refPoint.x+pasteTo.x-referencePoint.x,refPoint.y+pasteTo.y-referencePoint.y);
         GraphParticipantInterface par=gm.findParentActivityParticipantForLocation(diffPoint, null, null);
         String parId=((Participant)par.getPropertyObject()).getId();
//         System.err.println("..........RefPoint="+refPoint+", diffPoing="+diffPoint+", newparId="+parId+", newop="+gm.getOffset(diffPoint));
         GraphUtilities.setOffsetPoint(act, gm.getOffset(diffPoint));
         GraphUtilities.setParticipantId(act, parId);
         int type = act.getActivityType();
         if (type == XPDLConstants.ACTIVITY_TYPE_NO || type == XPDLConstants.ACTIVITY_TYPE_TOOL) {
            if (!parId.equals(FreeTextExpressionParticipant.getInstance().getId())) {
               act.setPerformer(parId);
            } else {
               act.setPerformer("");
            }
//            System.err.println("..........Perf changed to "+parId);
         }
      } else {
         Point oldPoint = GraphUtilities.getOffsetPoint(act);
         Point setPoint = new Point(pasteOffset.x + oldPoint.x, pasteOffset.y + oldPoint.y);            
//System.err.println("    ....... moving offset point from "+oldPoint+" to "+setPoint);
         GraphUtilities.setOffsetPoint(act, setPoint);
         String pId=GraphUtilities.getParticipantId(act);
         if (!vo.contains(pId) && !vo.contains(CommonExpressionParticipants.getInstance().getIdForVisualOrderEA(pId))) {
            boolean changePId=false;
            Participant p = XMLUtil.findParticipant(JaWEManager.getInstance().getXPDLHandler(),XMLUtil.getWorkflowProcess(act), pId);
            if (p == null) {
               p=CommonExpressionParticipants.getInstance().getCommonExpressionParticipant(gm.getXPDLOwner(), pId);
               if (p==null) {
                  p = FreeTextExpressionParticipant.getInstance();
               }
            }
            if (!p.getId().equals(pId)) {
               changePId=true;
            }
            pId = p.getId();
            if (!vo.contains(pId)) {
               vo.add(pId);
               participantsToInsertIntoGraph.add(p);
            }
            if (changePId) {
               GraphUtilities.setParticipantId(act, pId);
            }
         }
      }
   }

   public static void adjustPastedTransitions(List tras,CopyOrCutInfo cci,GraphManager gm) {
//System.err.println("Adjusting pasted transitions " + tras.size() + ", for proc/as "+gm.getXPDLOwner().getId());
      Iterator ittras = tras.iterator();
//      System.err.println(cci);
      Point pasteTo=cci.getPastePoint();
//      Point pasteOffset=cci.getOffsetPoint(gm.getGraph());
      Point referencePoint=cci.getReferencePoint();
//System.err.println("       ....pasteTo="+pasteTo+", pasteOffset="+pasteOffset+", refPoint="+referencePoint);      
      while (ittras.hasNext()) {
         Transition tra = (Transition) ittras.next();         
//         ExtendedAttribute bpea = GraphUtilities.getBreakpointsEA(tra);
         List bps=GraphUtilities.getBreakpoints(tra);
//System.err.println("       bps1="+bps);      
         if (bps.size()>0) {
            if (pasteTo!=null) {
               Iterator itbps=bps.iterator();
               while (itbps.hasNext()) {
                  Point bp=(Point)itbps.next();
                  bp.x+=(pasteTo.x-referencePoint.x);
                  bp.y+=(pasteTo.y-referencePoint.y);
//System.err.println("       changed bp");      
                  
               }
            } else {
               bps=new ArrayList();
            }
//System.err.println("       bps2="+bps);      
            GraphUtilities.setBreakpoints(tra, bps);            
         }
      }      
   }

   public static void adjustInsertedOrUpdatedTransitions(List tras,GraphManager gmgr) {
      Iterator ittras = tras.iterator();
      while (ittras.hasNext()) {
         Transition tra = (Transition) ittras.next();         
         ExtendedAttribute ea = GraphUtilities.getStyleEA(tra);
         if (ea == null) {
            String style = GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_ORTHOGONAL;
            if (tra.getFrom().equals(tra.getTo())) {
               style = GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_BEZIER;
            }
            ea = GraphUtilities.createStyleEA(tra, style, true);
         } else {
            String style = GraphUtilities.getStyle(tra);
            if (!GraphEAConstants.transitionStyles.contains(style)) {
               ea.setVValue(GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_ORTHOGONAL);
            }
         }

         if (tra.getFrom().equals(tra.getTo()) && !tra.getFrom().equals("")) {
            ExtendedAttribute bpea = GraphUtilities.getBreakpointsEA(tra);
            if (bpea == null) {
               GraphActivityInterface gact=gmgr.getGraphActivity(tra.getFrom());
               Point realP = new Point(50,50);
               if (gact!=null) {
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
               if (rp50y < 0) rp50y = realP.y + 50;

               breakpoints.add(new Point(Math.abs(rp50x1), Math.abs(rp50y)));
               breakpoints.add(new Point(Math.abs(rp50x2), Math.abs(rp50y)));
                              
               bpea = GraphUtilities.createBreakpointsEA(tra, GraphUtilities.createBreakpointsEAVal(breakpoints), true);
            }
         }
      }

   }

   public static void adjustBubbles(WorkflowProcess wp, String asId, String eapart, String oldId, String newId) {
      List seeas = GraphUtilities.getStartOrEndExtendedAttributes(wp, asId, oldId, eapart);
      GraphUtilities.adjustBubbles(seeas, eapart, newId);
   }

   public static void adjustBubbles(Collection seeas, String eapart, String newId) {
      Iterator it = seeas.iterator();
      while (it.hasNext()) {
         ExtendedAttribute ea = (ExtendedAttribute) it.next();
         StartEndDescription sed = new StartEndDescription(ea);
         if (eapart.equals(GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID)) {
            sed.setParticipantId(newId);
         } else if (eapart.equals(GraphEAConstants.EA_PART_CONNECTING_ACTIVITY_ID)) {
            sed.setActId(newId);
         } else {
            sed.setActSetId(newId);
         }
         ea.setVValue(sed.toString());
      }
   }

   public static Map getPackageParticipantsWithChangedId(List allInfo) {
      List parAttrChanges = GraphUtilities.findInfoList(allInfo, Participant.class, XMLAttribute.class);

      Map changedIdsPkgPar = new HashMap();
      for (int i = 0; i < parAttrChanges.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) parAttrChanges.get(i);
         XMLAttribute el = (XMLAttribute) info.getChangedElement();
         Participant par = XMLUtil.getParticipant(el);
         if (par.getParent().getParent() instanceof Package && el.toName().equals("Id")) {
            changedIdsPkgPar.put(info.getOldValue(), par);
         }
      }

      return changedIdsPkgPar;
   }

   public static Map getPackageInsertedOrRemovedParticipants(List allInfo, boolean inserted) {
      List pkgParInsertionOrRemoval = GraphUtilities.findInfoList(allInfo, Package.class, Participants.class);

      Map insertedOrRemovedIdsPkgPar = new HashMap();

      for (int i = 0; i < pkgParInsertionOrRemoval.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) pkgParInsertionOrRemoval.get(i);
         if ((inserted && info.getAction() == XMLElementChangeInfo.INSERTED)
               || (!inserted && info.getAction() == XMLElementChangeInfo.REMOVED)) {

            List pars = info.getChangedSubElements();
            if (pars != null) {
               for (int j = 0; j < pars.size(); j++) {
                  Participant par = (Participant) pars.get(j);
                  insertedOrRemovedIdsPkgPar.put(par.getId(), par);
               }
            }

         }
      }

      return insertedOrRemovedIdsPkgPar;
   }

   public static Map getWorkflowProcessParticipantsWithChangedId(List allInfo, WorkflowProcess wp) {
      List parAttrChanges = GraphUtilities.findInfoList(allInfo, Participant.class, XMLAttribute.class);

      Map changedIdsWpPar = new HashMap();
      for (int i = 0; i < parAttrChanges.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) parAttrChanges.get(i);
         XMLAttribute el = (XMLAttribute) info.getChangedElement();
         Participant par = XMLUtil.getParticipant(el);
         if (par.getParent().getParent() == wp && el.toName().equals("Id")) {
            changedIdsWpPar.put(info.getOldValue(), par);
         }
      }

      return changedIdsWpPar;
   }

   public static Map getWorkflowProcessInsertedOrRemovedParticipants(List allInfo, WorkflowProcess wp, boolean inserted) {
      List wpParInsertionOrRemoval = GraphUtilities.findInfoList(allInfo, WorkflowProcess.class, Participants.class);

      Map insertedOrRemovedIdsWPPar = new HashMap();

      for (int i = 0; i < wpParInsertionOrRemoval.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) wpParInsertionOrRemoval.get(i);
         if ((inserted && info.getAction() == XMLElementChangeInfo.INSERTED)
               || (!inserted && info.getAction() == XMLElementChangeInfo.REMOVED)) {

            if (info.getChangedElement().getParent() == wp) {
               List pars = info.getChangedSubElements();
               if (pars != null) {
                  for (int j = 0; j < pars.size(); j++) {
                     Participant par = (Participant) pars.get(j);
                     insertedOrRemovedIdsWPPar.put(par.getId(), par);
                  }
               }
            }

         }
      }

      return insertedOrRemovedIdsWPPar;
   }

   public static Set getInsertedOrRemovedWorkflowProcesses(List allInfo, boolean inserted) {
      Set s = new HashSet();
      List l = findInfoList(allInfo, Package.class, WorkflowProcesses.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         if ((inserted && info.getAction() == XMLElementChangeInfo.INSERTED)
               || (!inserted && info.getAction() == XMLElementChangeInfo.REMOVED)) {

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
         if ((inserted && info.getAction() == XMLElementChangeInfo.INSERTED)
               || (!inserted && info.getAction() == XMLElementChangeInfo.REMOVED)) {

            List ass = info.getChangedSubElements();
            if (ass != null && ass.size() > 0) {
               s.addAll(ass);
            }

         }
      }
      return s;
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

   public static Set getInsertedOrRemovedActivities(List allInfo, XMLCollectionElement wpOrAs, boolean inserted) {
      Set s = new HashSet();
      List l = findInfoList(allInfo, wpOrAs.getClass(), Activities.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         if ((inserted && info.getAction() == XMLElementChangeInfo.INSERTED)
               || (!inserted && info.getAction() == XMLElementChangeInfo.REMOVED)) {

            if (info.getChangedElement().getParent() == wpOrAs) {
               List acts = info.getChangedSubElements();
               if (acts != null && acts.size() > 0) {
                  s.addAll(acts);
               }
            }

         }
      }
      return s;
   }

   public static Map getActivitiesWithChangedId(List allInfo, XMLCollectionElement wpOrAs) {
      Map m = new HashMap();
      List l = findInfoList(allInfo, Activity.class, XMLAttribute.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         if (info.getChangedElement().toName().equals("Id")) {
            Activity act = XMLUtil.getActivity(info.getChangedElement());
            if (act.getParent().getParent() == wpOrAs) {
               m.put(info.getOldValue(), act);
            }
         }
      }
      return m;
   }

   public static Set getActivitiesWithChangedPerformer(List allInfo, XMLCollectionElement wpOrAs) {
      Set s = new HashSet();
      List l = findInfoList(allInfo, Activity.class, Performer.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         Activity act = XMLUtil.getActivity(info.getChangedElement());
         if (act.getParent().getParent() == wpOrAs) {
            s.add(act);
         }
      }
      return s;
   }

   public static Set getActivitiesWithChangedPerformer(List allInfo, XMLCollectionElement wpOrAs, String oldPerf) {
      Set s = new HashSet();
      List l = findInfoList(allInfo, Activity.class, Performer.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         if (info.getOldValue().equals(oldPerf)) {
            Activity act = XMLUtil.getActivity(info.getChangedElement());
            if (act.getParent().getParent() == wpOrAs) {
               s.add(act);
            }
         }
      }
      return s;
   }

   public static Set getInsertedOrRemovedTransitions(List allInfo, XMLCollectionElement wpOrAs, boolean inserted) {
      Set s = new HashSet();
      List l = findInfoList(allInfo, wpOrAs.getClass(), Transitions.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         if ((inserted && info.getAction() == XMLElementChangeInfo.INSERTED)
               || (!inserted && info.getAction() == XMLElementChangeInfo.REMOVED)) {

            if (info.getChangedElement().getParent() == wpOrAs) {
               List tras = info.getChangedSubElements();
               if (tras != null && tras.size() > 0) {
                  s.addAll(tras);
               }
            }

         }
      }
      return s;
   }

   public static Set getUpdatedTransitions(List allInfo, XMLCollectionElement wpOrAs) {
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
      return s;
   }

   // Extended attributes change
   public static Set getWorkflowProcessesAndActivitySetsWithChangedVisualParticipantOrder(List allInfo) {
      Set s = new HashSet();
      List l = findInfoList(allInfo, ExtendedAttribute.class, XMLAttribute.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         XMLElement el = info.getChangedElement();
         if (el.toName().equals("Value")) {
            ExtendedAttribute ea = (ExtendedAttribute) el.getParent();
            String eaname = ea.getName();
            if (eaname.equals(GraphEAConstants.EA_JAWE_GRAPH_WORKFLOW_PARTICIPANT_ORDER)
                  || eaname.equals(GraphEAConstants.EA_JAWE_GRAPH_BLOCK_PARTICIPANT_ORDER)) {

               WorkflowProcess wp = XMLUtil.getWorkflowProcess(ea);

               if (wp != null) {
                  if (eaname.equals(GraphEAConstants.EA_JAWE_GRAPH_WORKFLOW_PARTICIPANT_ORDER)) {
                     s.add(wp);
                  } else {
                     String asId = GraphUtilities.getActivitySetIdForBlockParticipantVisualOrderOrOrientationEA(ea);
                     ActivitySet as = wp.getActivitySet(asId);
                     s.add(as);
                  }
               }
            }
         }
      }
      return s;
   }

   public static Set getActivitiesWithChangedOffset(List allInfo, XMLCollectionElement wpOrAs) {
      Set s = new HashSet();
      List l = findInfoList(allInfo, ExtendedAttribute.class, XMLAttribute.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         XMLElement el = info.getChangedElement();
         if (el.toName().equals("Value")) {
            ExtendedAttribute ea = (ExtendedAttribute) el.getParent();
            if (ea.getName().equals(GraphEAConstants.EA_JAWE_GRAPH_OFFSET)) {
               Activity act = XMLUtil.getActivity(el);
               if (act != null && act.getParent().getParent() == wpOrAs) {
                  s.add(act);
               }
            }
         }
      }
      return s;
   }

   public static Set getActivitiesWithChangedParticipantId(List allInfo, XMLCollectionElement wpOrAs) {
      Set s = new HashSet();
      List l = findInfoList(allInfo, ExtendedAttribute.class, XMLAttribute.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         XMLElement el = info.getChangedElement();
         if (el.toName().equals("Value")) {
            ExtendedAttribute ea = (ExtendedAttribute) el.getParent();
            if (ea.getName().equals(GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID)) {
               Activity act = XMLUtil.getActivity(el);
               if (act != null && act.getParent().getParent() == wpOrAs) {
                  s.add(act);
               }
            }
         }
      }
      return s;
   }

   public static Set getTransitionsWithChangedBreakpointsOrStyle(List allInfo, XMLCollectionElement wpOrAs) {
      Set s = new HashSet();
      List l = findInfoList(allInfo, ExtendedAttribute.class, XMLAttribute.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         XMLElement el = info.getChangedElement();
         if (el.toName().equals("Value")) {
            ExtendedAttribute ea = (ExtendedAttribute) el.getParent();
            String eaName = ea.getName();
            if (eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE)
                  || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_BREAK_POINTS)) {
               Transition tra = XMLUtil.getTransition(el);
               if (tra != null && tra.getParent().getParent() == wpOrAs) {
                  s.add(tra);
               }
            }
         }
      }
      l = findInfoList(allInfo, Transition.class, ExtendedAttributes.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         ExtendedAttributes eas = (ExtendedAttributes) info.getChangedElement();
         if (eas.getParent().getParent().getParent() == wpOrAs) {
            List chngdeas = info.getChangedSubElements();
            if (chngdeas != null) {
               for (int j = 0; j < chngdeas.size(); j++) {
                  ExtendedAttribute ea = (ExtendedAttribute) chngdeas.get(j);
                  String eaName = ea.getName();
                  if (eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE)
                        || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_BREAK_POINTS)) {
                     s.add(XMLUtil.getTransition(eas));
                  }
               }
            }
         }         
      }

      return s;
   }

   public static Set getUpdatedBubbles(List allInfo, XMLCollectionElement wpOrAs) {
      Set s = new HashSet();
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(wpOrAs);
      List l = findInfoList(allInfo, ExtendedAttribute.class, XMLAttribute.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         XMLElement el = info.getChangedElement();
         if (el.toName().equals("Value")) {
            ExtendedAttribute ea = (ExtendedAttribute) el.getParent();
            if (ea.getParent().getParent() == wp) {
               String eaName=ea.getName();
               if ((wpOrAs instanceof WorkflowProcess && (eaName.equals(
                     GraphEAConstants.EA_JAWE_GRAPH_START_OF_WORKFLOW) || eaName.equals(
                     GraphEAConstants.EA_JAWE_GRAPH_END_OF_WORKFLOW)))
                     || ((wpOrAs instanceof ActivitySet) && (eaName.equals(
                           GraphEAConstants.EA_JAWE_GRAPH_START_OF_BLOCK) || eaName.equals(
                           GraphEAConstants.EA_JAWE_GRAPH_END_OF_BLOCK)))) {

                  s.add(ea);

               }
            }
         }
      }
      return s;
   }

   public static Set getInsertedOrRemovedBubbles(List allInfo, XMLCollectionElement wpOrAs, boolean inserted) {
      Set s = new HashSet();
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(wpOrAs);
      List l = findInfoList(allInfo, WorkflowProcess.class, ExtendedAttributes.class);
      for (int i = 0; i < l.size(); i++) {
         XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
         if ((inserted && info.getAction() == XMLElementChangeInfo.INSERTED)
               || (!inserted && info.getAction() == XMLElementChangeInfo.REMOVED)) {

            ExtendedAttributes eas = (ExtendedAttributes) info.getChangedElement();
            if (eas.getParent() == wp) {
               List chngdeas = info.getChangedSubElements();
               if (chngdeas != null) {
                  for (int j = 0; j < chngdeas.size(); j++) {
                     ExtendedAttribute ea = (ExtendedAttribute) chngdeas.get(j);
                     String eaName=ea.getName();
                     if ((wpOrAs instanceof WorkflowProcess && (eaName.equals(
                           GraphEAConstants.EA_JAWE_GRAPH_START_OF_WORKFLOW) || eaName.equals(
                           GraphEAConstants.EA_JAWE_GRAPH_END_OF_WORKFLOW)))
                           || ((wpOrAs instanceof ActivitySet) && (eaName.equals(
                                 GraphEAConstants.EA_JAWE_GRAPH_START_OF_BLOCK) || eaName.equals(
                                 GraphEAConstants.EA_JAWE_GRAPH_END_OF_BLOCK)))) {

                        s.add(ea);

                     }
                  }
               }
            }

         }
      }
      return s;
   }

   public static XMLCollectionElement getRotatedGraphObject (List allInfo) {
      XMLCollectionElement wpOrAs=null;
      List l=GraphUtilities.findInfoList(allInfo, WorkflowProcess.class, ExtendedAttributes.class);
      if (l.size()>0) {
         for (int i = 0; i < l.size(); i++) {
            XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
            if ((info.getAction() == XMLElementChangeInfo.INSERTED)
                  || (info.getAction() == XMLElementChangeInfo.REMOVED)) {

               ExtendedAttributes eas = (ExtendedAttributes) info.getChangedElement();
               List chngdeas = info.getChangedSubElements();
               if (chngdeas != null) {
                  for (int j = 0; j < chngdeas.size(); j++) {
                     ExtendedAttribute ea = (ExtendedAttribute) chngdeas.get(j);
                     String eaName=ea.getName();
                     if (eaName.equals(
                           GraphEAConstants.EA_JAWE_GRAPH_WORKFLOW_PARTICIPANT_ORIENTATION)) {
                        wpOrAs=(WorkflowProcess)eas.getParent();
                        break;
                     } else if (eaName.equals(
                                 GraphEAConstants.EA_JAWE_GRAPH_BLOCK_PARTICIPANT_ORIENTATION)) {
                        String asId=GraphUtilities.getActivitySetIdForBlockParticipantVisualOrderOrOrientationEA(ea.getVValue());
                        wpOrAs=((WorkflowProcess)eas.getParent()).getActivitySet(asId);
                        break;
                     }
                  }
               }
            }
         }         
      }
      if (wpOrAs==null) {
         l=GraphUtilities.findInfoList(allInfo, ExtendedAttribute.class, XMLAttribute.class);
         if (l.size()>0) {
            for (int i = 0; i < l.size(); i++) {
               XPDLElementChangeInfo info = (XPDLElementChangeInfo) l.get(i);
               if (info.getAction() == XMLElementChangeInfo.UPDATED) {
                  ExtendedAttribute ea = (ExtendedAttribute) info.getChangedElement().getParent();
                  String eaName=ea.getName();
                  if (eaName.equals(
                        GraphEAConstants.EA_JAWE_GRAPH_WORKFLOW_PARTICIPANT_ORIENTATION)) {
                     wpOrAs=(WorkflowProcess)ea.getParent().getParent();
                     break;
                  } else if (eaName.equals(
                              GraphEAConstants.EA_JAWE_GRAPH_BLOCK_PARTICIPANT_ORIENTATION)) {
                     String asId=GraphUtilities.getActivitySetIdForBlockParticipantVisualOrderOrOrientationEA(ea.getVValue());
                     wpOrAs=((WorkflowProcess)ea.getParent().getParent()).getActivitySet(asId);
                     break;
                  }
               }
            }         
         }
      }
//      System.err.println("ROTATED ELEMENT="+wpOrAs);
      return wpOrAs;
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

   public static void setNewParticipantId(List acts, String Id) {
      for (int i = 0; i < acts.size(); i++) {
         Activity act = (Activity) acts.get(i);
         GraphUtilities.setParticipantId(act, Id);
      }
   }

   protected static boolean reloadGraphIfNeccessary (Graph graph) {
      // if there are two parts with the same id within the graph (as a result of change of
      // more relevant participant Id), reload the graph
      List allGraphParticipants = JaWEGraphModel.getAllParticipantsInModel(graph.getModel());
      List partsInGraph = new ArrayList();
      boolean shouldReload=false;
      if (allGraphParticipants != null) {
         Iterator it = allGraphParticipants.iterator();
         while (it.hasNext()) {
            GraphParticipantInterface gpar = (GraphParticipantInterface) it.next();
            Participant p=(Participant)gpar.getUserObject();
            if (partsInGraph.contains(p.getId())) {
               shouldReload=true;
               break;
            } 
            partsInGraph.add(p.getId());
         }
      }      

      if (shouldReload) {
         GraphUtilities.reloadGraph(graph);
      }
      return shouldReload;      
   }
   
   protected static void reloadGraph (Graph graph) {
      Object[] elem = JaWEGraphModel.getAll(graph.getModel());
      graph.getModel().remove(elem);
      GraphUtilities.scanExtendedAttributesForWPOrAs(graph.getXPDLObject());
      graph.getGraphManager().createWorkflowGraph(graph.getXPDLObject());      
   }
   
   protected static List getActivitiesWithPerformer (List acts,String parId) {
      List l=new ArrayList();

      Iterator it=acts.iterator();
      while (it.hasNext()){
         Activity act=(Activity)it.next();
         if (act.getPerformer().equals(parId)) {
            l.add(act);
         }
      }
      
      return l;         
   }
     
}