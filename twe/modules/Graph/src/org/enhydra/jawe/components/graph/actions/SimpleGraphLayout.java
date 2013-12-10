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

package org.enhydra.jawe.components.graph.actions;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphEAConstants;
import org.enhydra.jawe.components.graph.GraphUtilities;
import org.enhydra.jawe.components.graph.JaWEGraphModel;
import org.enhydra.shark.utilities.SequencedHashMap;
import org.enhydra.shark.xpdl.XMLCollectionElement;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.elements.Activity;
import org.enhydra.shark.xpdl.elements.ActivitySet;
import org.enhydra.shark.xpdl.elements.ExtendedAttribute;
import org.enhydra.shark.xpdl.elements.ExtendedAttributes;
import org.enhydra.shark.xpdl.elements.Package;
import org.enhydra.shark.xpdl.elements.Transition;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;
import org.jgraph.graph.DefaultGraphCell;

/**
 * @author Sasa Bojanic
 */
public class SimpleGraphLayout extends ActionBase {

   public SimpleGraphLayout(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      JaWEController jc = JaWEManager.getInstance().getJaWEController();
      GraphController gc = (GraphController) jawecomponent;

      if (gc.getSelectedGraph() != null)
         if (XMLUtil.getPackage(gc.getSelectedGraph().getXPDLObject()) == jc.getMainPackage()) {
            setEnabled(true);
            return;
         }

      setEnabled(false);
   }

   public void actionPerformed(ActionEvent e) {
      GraphController gcon = (GraphController) jawecomponent;
      Graph selectedGraph = gcon.getSelectedGraph();
      if (selectedGraph == null)
         return;

      gcon.setUpdateInProgress(true);
      JaWEManager.getInstance().getJaWEController().startUndouableChange();

      SimpleGraphLayout.layoutGraph(gcon, selectedGraph);

      selectedGraph.clearSelection();

      List toSelect = new ArrayList();
      toSelect.add(selectedGraph.getXPDLObject());
      JaWEManager.getInstance().getJaWEController().endUndouableChange(toSelect);
      gcon.setUpdateInProgress(false);
   }

   public static void layoutAllGraphs() {
      Graph selectedGraph = null;
      GraphController gc = GraphUtilities.getGraphController();
      gc.setUpdateInProgress(true);
      JaWEManager.getInstance().getJaWEController().startUndouableChange();
      Iterator pkgs = JaWEManager.getInstance()
         .getXPDLHandler()
         .getAllPackages()
         .iterator();
      while (pkgs.hasNext()) {
         Iterator wps = ((Package) pkgs.next()).getWorkflowProcesses()
            .toElements()
            .iterator();
         while (wps.hasNext()) {
            WorkflowProcess wp = (WorkflowProcess) wps.next();
            if (selectedGraph == null) {
               selectedGraph = gc.getGraph(wp);
            }
            SimpleGraphLayout.layoutGraph(gc, gc.getGraph(wp));
            Iterator ass = wp.getActivitySets().toElements().iterator();
            while (ass.hasNext()) {
               ActivitySet as = (ActivitySet) ass.next();
               SimpleGraphLayout.layoutGraph(gc, gc.getGraph(as));
            }
         }
      }
      List toSelect = new ArrayList();
      toSelect.add(selectedGraph.getXPDLObject());
      JaWEManager.getInstance().getJaWEController().endUndouableChange(toSelect);
      gc.setUpdateInProgress(false);
   }

   public static void layoutGraph(GraphController gcon, Graph selectedGraph) {
      boolean isHorizontal = GraphUtilities.getGraphParticipantOrientation(selectedGraph.getWorkflowProcess(),
                                                                           selectedGraph.getXPDLObject())
         .equals(GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ORIENTATION_VALUE_HORIZONTAL);

      Object[] elem = JaWEGraphModel.getAll(selectedGraph.getModel());
      selectedGraph.getModel().remove(elem);

      for (int i = 0; i < elem.length; i++) {
         if (elem[i] instanceof DefaultGraphCell) {
            if (((DefaultGraphCell) elem[i]).getUserObject() instanceof Transition) {
               Transition tr = (Transition) ((DefaultGraphCell) elem[i]).getUserObject();
               if (!tr.getTo().equals(tr.getFrom())) {
                  GraphUtilities.setBreakpoints(tr, new ArrayList());
               }
            } else if (((DefaultGraphCell) elem[i]).getUserObject() instanceof ExtendedAttribute) {
               ExtendedAttribute ea = (ExtendedAttribute) ((DefaultGraphCell) elem[i]).getUserObject();
               ((ExtendedAttributes) ea.getParent()).remove(ea);
            }
         }
      }

      SimpleGraphLayout.sortItOut(selectedGraph.getXPDLObject(), isHorizontal);

      selectedGraph.getGraphManager().createWorkflowGraph(selectedGraph.getXPDLObject());
      if (gcon.getGraphSettings().shouldUseBubbles()) {
         List easToAdd = selectedGraph.getGraphManager().insertMissingStartEndBubbles();
         XMLUtil.getWorkflowProcess(selectedGraph.getXPDLObject())
            .getExtendedAttributes()
            .addAll(easToAdd);
      }
   }

   protected static void sortItOut(XMLCollectionElement wpOrAs, boolean isHorizontal) {
      List starts = new ArrayList(XMLUtil.getStartingActivities(wpOrAs));
      List inserted = new ArrayList();

      Map posX = new HashMap();
      Map posY = new HashMap();
      Map actX = new HashMap();
      Map actY = new HashMap();

      for (int i = 0; i < starts.size(); i++) {
         Activity act = (Activity) starts.get(i);
         Point p = SimpleGraphLayout.getYPos(posX,
                                             posY,
                                             actX,
                                             actY,
                                             act,
                                             null,
                                             isHorizontal);
         GraphUtilities.setOffsetPoint(act, p);
         inserted.add(act);
      }

      SequencedHashMap toInsert = new SequencedHashMap();
      for (int i = 0; i < starts.size(); i++) {
         toInsert.put(starts.get(i), null);
      }
      while (toInsert.size() > 0) {
         Activity act = (Activity) toInsert.keySet().toArray()[0];
         List l = SimpleGraphLayout.insertActivity(posX,
                                                   actX,
                                                   act,
                                                   (Activity) toInsert.get(act),
                                                   inserted,
                                                   isHorizontal);
         toInsert.remove(act);
         // System.out.println("Inserted act "+act+", TOINS="+toInsert.keySet());
         if (l.size() == 0) {
            String pid = GraphUtilities.getParticipantId(act);
            Point prevP = (Point) posX.get(pid);
            Point p = null;
            if (isHorizontal) {
               p = new Point(prevP.x
                             + (int) (1.5 * GraphUtilities.getGraphController()
                                .getGraphSettings()
                                .getActivityWidth()), prevP.y);
            } else {
               p = new Point(prevP.x, prevP.y
                                      + (int) (1.5 * GraphUtilities.getGraphController()
                                         .getGraphSettings()
                                         .getActivityHeight()));
            }
            posX.put(pid, p);
         } else {
            // System.out.println("LINVOKED, lsize="+l.size());
            for (int i = 0; i < l.size(); i++) {
               Object an = l.get(i);
               if (!toInsert.containsKey(an)) {
                  toInsert.put(an, act);
               }
            }
         }
         // System.out.println("Inserted act "+act+", TOINS2="+toInsert.keySet());
      }
   }

   protected static List insertActivity(Map posX,
                                        Map actX,
                                        Activity act,
                                        Activity prev,
                                        List inserted,
                                        boolean isHorizontal) {
      List ret = new ArrayList();
      if (inserted.contains(act) && prev != null)
         return ret;
      List ordOGT = XMLUtil.getOrderedOutgoingTransitions(act,
                                                          XMLUtil.getOutgoingTransitions(act));
      for (int i = 0; i < ordOGT.size(); i++) {
         Transition t = (Transition) ordOGT.get(i);
         Activity a = XMLUtil.getWorkflowProcess(act).getActivity(t.getTo());
         if (!inserted.contains(a)) {
            ret.add(a);
         }
      }
      if (prev != null) {
         Point p = SimpleGraphLayout.getXPos(posX, actX, act, prev, isHorizontal);
         GraphUtilities.setOffsetPoint(act, p);
         inserted.add(act);
      }

      return ret;
   }

   protected static Point getYPos(Map posX,
                                  Map posY,
                                  Map actX,
                                  Map actY,
                                  Activity act,
                                  Activity prev,
                                  boolean isHorizontal) {
      int xdiff = GraphUtilities.getGraphController()
         .getGraphSettings()
         .getActivityWidth();
      int ydiff = GraphUtilities.getGraphController()
         .getGraphSettings()
         .getActivityHeight();

      int xoff = GraphUtilities.getGraphController()
         .getGraphSettings()
         .getParticipantNameWidth();

      if (isHorizontal) {
         xoff += (xdiff * 1.5);
      } else {
         xoff += (ydiff * 1.5);
      }
      int yoff = 15;

      Point p = null;
      String pid = GraphUtilities.getParticipantId(act);
      Point prevP = (Point) posY.get(pid);
      Point prevActP = (prev != null) ? GraphUtilities.getOffsetPoint(prev) : null;
      if (prevP == null) {
         prevP = prevActP;
         if (prevP == null) {
            if (isHorizontal) {
               p = new Point(xoff, yoff);
            } else {
               p = new Point(yoff, xoff);
            }
         }
         posX.put(pid, p);
         actX.put(pid, act);
      } else {
         if (prevActP != null) {
            if (isHorizontal) {
               prevP = new Point(prevP.x, Math.max(prevP.y, prevActP.y));
            } else {
               prevP = new Point(Math.max(prevP.x, prevActP.x), prevP.y);
            }
         }
      }
      if (p == null) {
         if (isHorizontal) {
            p = new Point(prevP.x, prevP.y + (int) (1.5 * ydiff));
         } else {
            p = new Point(prevP.x + (int) (1.5 * xdiff), prevP.y);
         }
      }
      posY.put(pid, p);
      actY.put(pid, act);
      return p;
   }

   protected static Point getXPos(Map posX,
                                  Map actX,
                                  Activity act,
                                  Activity prev,
                                  boolean isHorizontal) {
      int xdiff = GraphUtilities.getGraphController()
         .getGraphSettings()
         .getActivityWidth();
      int ydiff = GraphUtilities.getGraphController()
         .getGraphSettings()
         .getActivityHeight();

      int yoff = 15;

      Point p = null;
      String pid = GraphUtilities.getParticipantId(act);
      Point prevP = (Point) posX.get(pid);
      Point prevActP = GraphUtilities.getOffsetPoint(prev);
      boolean changeMap = true;
      if (prevP == null) {
         prevP = prevActP;
         if (isHorizontal) {
            prevP = new Point(prevActP.x, yoff);
         } else {
            prevP = new Point(yoff, prevActP.y);
         }
      } else {
         if (isHorizontal) {
            prevP = new Point(Math.max(prevP.x, prevActP.x), prevP.y);
         } else {
            prevP = new Point(prevP.x, Math.max(prevP.y, prevActP.y));
         }
      }
      Activity pointAct = (Activity) actX.get(pid);
      if (pointAct != null) {
         Set s = XMLUtil.getIncomingTransitions(pointAct);
         Iterator it = s.iterator();
         while (it.hasNext()) {
            Transition t = (Transition) it.next();
            if (t.getFrom().equals(prev.getId())) {
               changeMap = false;
               break;
            }
         }
      }
      if (isHorizontal) {
         if (changeMap) {
            p = new Point(prevP.x + (int) (1.5 * xdiff), prevP.y);
         } else {
            p = GraphUtilities.getOffsetPoint(pointAct);
            p.y += (int) (1.5 * ydiff);
         }
      } else {
         if (changeMap) {
            p = new Point(prevP.x, prevP.y + (int) (1.5 * ydiff));
         } else {
            p = GraphUtilities.getOffsetPoint(pointAct);
            p.x += (int) (1.5 * xdiff);
         }
      }
      if (changeMap) {
         posX.put(pid, p);
      }
      actX.put(pid, act);
      return p;
   }

}
