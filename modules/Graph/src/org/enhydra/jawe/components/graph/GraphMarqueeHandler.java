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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.enhydra.jawe.BarFactory;
import org.enhydra.jawe.JaWEConstants;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.base.controller.JaWEFrame;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Activities;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.Artifact;
import org.enhydra.jxpdl.elements.Artifacts;
import org.enhydra.jxpdl.elements.Association;
import org.enhydra.jxpdl.elements.Associations;
import org.enhydra.jxpdl.elements.Lane;
import org.enhydra.jxpdl.elements.NestedLane;
import org.enhydra.jxpdl.elements.Participant;
import org.enhydra.jxpdl.elements.Participants;
import org.enhydra.jxpdl.elements.Pool;
import org.enhydra.jxpdl.elements.Transition;
import org.enhydra.jxpdl.elements.Transitions;
import org.jgraph.JGraph;
import org.jgraph.graph.AbstractCellView;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

/**
 * Implementation of a marquee handler for Process Editor. This is also a place where
 * (after mouse click or release) participants, activities (normal, subflows, block
 * activities) and transitions are inserted, where persistent mode is achived and where
 * mouse cursors are changing, and where popup menu is implemented. When inserting cells
 * it calls WorkflowManager.
 */
public class GraphMarqueeHandler extends BasicMarqueeHandler {
   protected Point start; // Starting point (where mouse was pressed).

   protected Point current; // Current point (where mouse was dragged).

   protected GraphPortViewInterface port; // Current port (when mouse was pressed).

   protected GraphPortViewInterface firstPort; // First port (when mouse was pressed).

   protected GraphPortViewInterface lastPort; // Last port (when mouse was dragged).

   protected Vector points = new Vector();

   protected Point popupPoint; // A point where popup window has been created last time

   protected GraphController graphController;

   protected String mainType = GraphEAConstants.SELECT_TYPE;

   protected String subType = GraphEAConstants.SELECT_TYPE_DEFAULT;

   /**
    * Creates custom marquee handler.
    */
   public GraphMarqueeHandler(GraphController graphController) {
      this.graphController = graphController;
   }

   /** Return true if this handler should be preferred over other handlers. */
   public boolean isForceMarqueeEvent(MouseEvent e) {
      boolean isSelectButtonSelected = isSelectButtonSelected();
      return ((isSelectButtonSelected && SwingUtilities.isRightMouseButton(e))
              || !isSelectButtonSelected || super.isForceMarqueeEvent(e));
   }

   /**
    * We don't want special cursor
    */
   public void mousePressed(MouseEvent e) {
      startPoint = e.getPoint();
      marqueeBounds = new Rectangle2D.Double(startPoint.getX(), startPoint.getY(), 0, 0);
      if (e != null) {
         if (!(e.getSource() instanceof JGraph))
            throw new IllegalArgumentException("MarqueeHandler cannot handle event from unknown source: "
                                               + e);
      }
   }

   public void mouseReleased(MouseEvent ev) {
      try {
         if (ev != null && marqueeBounds != null) {
            Rectangle2D bounds = getGraph().fromScreen(marqueeBounds);// HM, JGraph3.4.1
            CellView[] rootViews = getGraph().getGraphLayoutCache().getRoots(bounds);

            // added - getting all views in model (except forbidden objects)
            CellView[] views = AbstractCellView.getDescendantViews(rootViews);
            ArrayList wholeList = new ArrayList();
            ArrayList participantList = new ArrayList();
            ArrayList otherList = new ArrayList();
            for (int i = 0; i < views.length; i++) {
               if (bounds.contains(views[i].getBounds())) {
                  if (views[i].getCell() instanceof DefaultGraphCell
                      && !(((DefaultGraphCell) views[i].getCell()).getUserObject() instanceof Participant)) {
                     otherList.add(views[i].getCell());
                  } else {
                     participantList.add(views[i].getCell());
                  }
                  wholeList.add(views[i].getCell());
               }
            }

            Object[] cells = wholeList.toArray();

            getGraph().getUI().selectCellsForEvent(getGraph(), cells, ev);
            Rectangle dirty = marqueeBounds.getBounds();// HM, JGraph3.4.1
            dirty.width++;
            dirty.height++;// HM, JGraph3.4.1
            getGraph().repaint(dirty);
         }
      } finally {
         currentPoint = null;
         startPoint = null;
         marqueeBounds = null;
      }
   }

   //
   // PopupMenu
   //
   /**
    * Creates popup menu and adds a various actions (depending of where mouse was pressed
    * - which cell(s) is/are selected).
    */
   protected JPopupMenu createPopupMenu(final Object cell) {

      boolean isWorkflowElement = (cell instanceof WorkflowElement);

      String type = null;
      String subtype = null;
      String userSpec = null;
      if (isWorkflowElement) {
         XMLElement el = (XMLElement) ((DefaultGraphCell) cell).getUserObject();
         if (cell instanceof GraphActivityInterface) {
            type = JaWEConstants.ACTIVITY_TYPE;
            subtype = Utils.getActivityStringType(((Activity) el).getActivityType());
            userSpec = JaWEManager.getInstance()
               .getJaWEController()
               .getTypeResolver()
               .getJaWEType(el)
               .getTypeId();
         }
         if (cell instanceof GraphArtifactInterface) {
            type = JaWEConstants.ARTIFACT_TYPE;
            subtype = Utils.getArtifactStringType(((Artifact) el).getArtifactType());
            userSpec = JaWEManager.getInstance()
               .getJaWEController()
               .getTypeResolver()
               .getJaWEType(el)
               .getTypeId();
         }
         if (cell instanceof GraphSwimlaneInterface) {
            type = JaWEConstants.LANE_TYPE;
            // subtype = JaWEManager.getInstance()
            // .getJaWEController()
            // .getTypeResolver()
            // .getJaWEType(el)
            // .getTypeId();
         }
         if (cell instanceof GraphTransitionInterface) {
            type = JaWEConstants.TRANSITION_TYPE;
            subtype = JaWEManager.getInstance()
               .getJaWEController()
               .getTypeResolver()
               .getJaWEType(el)
               .getTypeId();
         }
      } else {
         type = GraphEAConstants.SELECT_TYPE;
      }

      JPopupMenu retMenu = BarFactory.createPopupMenu(type, graphController);

      if (subtype != null) {
         JPopupMenu specMenu = BarFactory.createPopupMenu(subtype, graphController);

         Component[] spec = specMenu.getComponents();

         if (spec.length != 0)
            retMenu.addSeparator();

         for (int i = 0; i < spec.length; i++) {
            retMenu.add(spec[i]);
         }
      }

      if (subtype != null && userSpec != null && !subtype.equals(userSpec)) {
         JPopupMenu specMenu = BarFactory.createPopupMenu(userSpec, graphController);

         Component[] spec = specMenu.getComponents();

         if (spec.length != 0)
            retMenu.addSeparator();

         for (int i = 0; i < spec.length; i++) {
            retMenu.add(spec[i]);
         }
      }

      if (cell instanceof GraphSwimlaneInterface
          && ((WorkflowElement) cell).getPropertyObject() instanceof Lane) {
         // System.err.println("CSPEC FIR CEP");
         Lane l = (Lane) ((WorkflowElement) cell).getPropertyObject();
         String perf = GraphUtilities.getLanesFirstPerformer(l);
         if (perf == null) {
            JMenuItem se = BarFactory.createMenuItem(getGraphController().getSettings()
                                                     .getAction(("SetLanesName")), getGraphController(), false);
                                                  retMenu.addSeparator();
                                                  retMenu.add(se);            
         }
         if (perf != null
             && XMLUtil.findParticipant(JaWEManager.getInstance().getXPDLHandler(),
                                        getGraph().getWorkflowProcess(),
                                        perf) == null) {
            JMenuItem se = BarFactory.createMenuItem(getGraphController().getSettings()
               .getAction(("SetPerformerExpression")), getGraphController(), false);
            retMenu.addSeparator();
            retMenu.add(se);
            // System.err.println("CSPEC FIR CEP CREATED");
         }
      }

      if (cell instanceof GraphTransitionInterface) {
         if (((WorkflowElement) cell).getPropertyObject() instanceof Transition) {
            JMenuItem se = BarFactory.createMenuItem(getGraphController().getSettings()
                                                        .getAction(("SelectConnectingActivitiesForSelectedTransitions")),
                                                     getGraphController(),
                                                     false);
            retMenu.addSeparator();
            retMenu.add(se);
         } else {
            JMenuItem se = BarFactory.createMenuItem(getGraphController().getSettings()
                                                        .getAction(("SelectConnectingActivitiesForSelectedAssociations")),
                                                     getGraphController(),
                                                     false);
            retMenu.addSeparator();
            retMenu.add(se);

            se = BarFactory.createMenuItem(getGraphController().getSettings()
                                              .getAction(("SelectConnectingArtifactsForSelectedAssociations")),
                                           getGraphController(),
                                           false);
            retMenu.addSeparator();
            retMenu.add(se);
         }
      }

      return retMenu;
   }

   /**
    * Gets the point of last popup menu creation.
    */
   public Point getPopupPoint() {
      return popupPoint;
   }

   public boolean validateSource(GraphPortViewInterface pPort) {
      // if port is a valid
      if (pPort != null && pPort.getCell() != null
      // and it is a port
      && (pPort.getCell() instanceof GraphPortInterface)) {
         // return if it accepts to be a source or a target
         GraphCommonInterface sourceActivity = pPort.getGraphActivityOrArtifact();
         if (isTransitionButtonSelected()
             && (sourceActivity instanceof GraphArtifactInterface)) {
            return false;
         }
         if (!sourceActivity.acceptsSource())
            return false;

         if (isAssociationButtonSelected()) {
            return true;
         }

         boolean isExceptionalTrans = getSubType().equals(JaWEConstants.TRANSITION_TYPE_EXCEPTION);

         if (!JaWEManager.getInstance()
            .getTransitionHandler()
            .acceptsSource((Activity) sourceActivity.getPropertyObject(),
                           isExceptionalTrans)) {
            JOptionPane.showMessageDialog(getJaWEFrame(),
                                          getGraphController().getSettings()
                                             .getLanguageDependentString("WarningCannotAcceptMoreOutgoingTransitions"),
                                          getJaWEController().getAppTitle(),
                                          JOptionPane.INFORMATION_MESSAGE);

            return false;
         }
         return true;
      }

      return false;
   }

   /**
    * Returns <code>true</code> if parent cell of given port accepts source or target,
    * depending on <code>source</code> parameter.
    */
   public boolean validateConnection(GraphPortViewInterface pFirstPort,
                                     GraphPortViewInterface pSecondPort,
                                     XMLCollectionElement tOrA) {
      // if ports are valid
      if (pFirstPort != null
          && pFirstPort.getCell() != null
          && (pFirstPort.getCell() instanceof GraphPortInterface) && pSecondPort != null
          && pSecondPort.getCell() != null
          && (pSecondPort.getCell() instanceof GraphPortInterface)) {
         // return if it accepts to be a source or a target
         GraphCommonInterface sourceActivity = pFirstPort.getGraphActivityOrArtifact();
         GraphCommonInterface targetActivity = pSecondPort.getGraphActivityOrArtifact();
         if (isTransitionButtonSelected() || tOrA instanceof Transition) {
            if (sourceActivity instanceof GraphArtifactInterface
                || targetActivity instanceof GraphArtifactInterface)
               return false;
         } else {
            if ((sourceActivity.getPropertyObject() instanceof Artifact && targetActivity.getPropertyObject() instanceof Artifact)
                || (sourceActivity.getPropertyObject() instanceof Activity && targetActivity.getPropertyObject() instanceof Activity)) {
               return false;
            }
            if (sourceActivity != targetActivity && targetActivity.acceptsTarget()) {

               Iterator it = XMLUtil.getPackage(getGraph().getXPDLObject())
                  .getAssociations()
                  .toElements()
                  .iterator();
               String sId = sourceActivity.getPropertyObject().get("Id").toValue();
               String tId = targetActivity.getPropertyObject().get("Id").toValue();
               while (it.hasNext()) {
                  Association asoc = (Association) it.next();
                  if ((asoc.getSource().equals(sId) && asoc.getTarget().equals(tId))
                      || (asoc.getSource().equals(tId) && asoc.getTarget().equals(sId))) {
                     return false;
                  }
               }
               return true;
            }
            return false;
         }
         Transition t = (Transition) tOrA;
         // System.out.println("Processing accept source for act "+sourceActivity+", target act="+targetActivity);
         if (!sourceActivity.acceptsSource()) {
            // System.err.println("acceptTarget = false");
            return false;
         }
         if (!targetActivity.acceptsTarget()) {
            // System.err.println("acceptTarget = false");
            return false;
         }

         if (!JaWEManager.getInstance()
            .getTransitionHandler()
            .acceptsTarget((Activity) targetActivity.getUserObject())) {
            JOptionPane.showMessageDialog(getJaWEFrame(),
                                          getGraphController().getSettings()
                                             .getLanguageDependentString("WarningCannotAcceptMoreIncomingTransitions"),
                                          getJaWEController().getAppTitle(),
                                          JOptionPane.WARNING_MESSAGE);

            return false;
         }

         Activity a = (Activity) sourceActivity.getUserObject();
         Activity b = (Activity) targetActivity.getUserObject();

         int actSType = a.getActivityType();
         int actTType = b.getActivityType();

         if (t.getCondition().getType().equals(XPDLConstants.CONDITION_TYPE_CONDITION)
             && actSType == XPDLConstants.ACTIVITY_TYPE_ROUTE
             && ((Activity) sourceActivity.getPropertyObject()).getActivityTypes()
                .getRoute()
                .getGatewayType()
                .equals(XPDLConstants.JOIN_SPLIT_TYPE_PARALLEL)) {
            JOptionPane.showMessageDialog(getJaWEFrame(),
                                          getGraphController().getSettings()
                                             .getLanguageDependentString("WarningCannotInsertElement"),
                                          getJaWEController().getAppTitle(),
                                          JOptionPane.WARNING_MESSAGE);
            return false;
         }

         List status = new ArrayList(1);

         boolean isExceptionalTrans = XMLUtil.isExceptionalTransition(t)
                                      || getSubType().equals(JaWEConstants.TRANSITION_TYPE_EXCEPTION);
         if (!JaWEManager.getInstance()
            .getTransitionHandler()
            .allowsConnection(a, b, t, isExceptionalTrans, status)) {
            String errorMsg = "WarningSourceActivityCannotHaveMoreOutgoingTransitions";
            boolean isError = false;
            if (((Integer) status.get(0)).intValue() == 2) {
               errorMsg = "WarningTargetActivityCannotHaveMoreIncomingTransitions";
            } else if (((Integer) status.get(0)).intValue() == 3) {
               isError = true;
               errorMsg = "ErrorActivityCannotHaveMoreThenOneIncomingOutgoingTransitionFromToTheSameActivity";
            }
            JOptionPane.showMessageDialog(getJaWEFrame(),
                                          getGraphController().getSettings()
                                             .getLanguageDependentString(errorMsg),
                                          getJaWEController().getAppTitle(),
                                          isError ? JOptionPane.ERROR_MESSAGE
                                                 : JOptionPane.INFORMATION_MESSAGE);
            return false;
         }

         return true;
      }

      return false;
   }

   protected GraphController getGraphController() {
      return graphController;
   }

   protected Graph getGraph() {
      return getGraphController().getSelectedGraph();
   }

   protected GraphManager getGraphManager() {
      return getGraph().getGraphManager();
   }

   protected JaWEController getJaWEController() {
      return JaWEManager.getInstance().getJaWEController();
   }

   protected JaWEFrame getJaWEFrame() {
      return getJaWEController().getJaWEFrame();
   }

   public boolean isSelectButtonSelected() {
      return mainType.equals(GraphEAConstants.SELECT_TYPE);
   }

   protected boolean isParticipantButtonSelected() {
      return mainType.equals(JaWEConstants.LANE_TYPE);
   }

   protected boolean isArtifactButtonSelected() {
      return mainType.equals(JaWEConstants.ARTIFACT_TYPE);
   }

   protected boolean isActivityButtonSelected() {
      return mainType.equals(JaWEConstants.ACTIVITY_TYPE);
   }

   public boolean isAssociationButtonSelected() {
      return mainType.equals(JaWEConstants.ASSOCIATION_TYPE);
   }

   public boolean isTransitionButtonSelected() {
      return mainType.equals(JaWEConstants.TRANSITION_TYPE);
   }

   protected boolean isStartButtonSelected() {
      return mainType.equals(GraphEAConstants.START_TYPE);
   }

   protected boolean isEndButtonSelected() {
      return mainType.equals(GraphEAConstants.END_TYPE);
   }

   public void addPoint(Point p) {
      points.add(p);
   }

   public int getStatus() {
      if (isSelectButtonSelected()) {
         return JaWEGraphUI.SELECTION;
      } else if (isParticipantButtonSelected()) {
         return JaWEGraphUI.INSERT_PARTICIPANT;
      } else if (isTransitionButtonSelected()) {
         return JaWEGraphUI.INSERT_TRANSITION_START;
      } else if (isAssociationButtonSelected()) {
         return JaWEGraphUI.INSERT_ASSOCIATION_START;
      } else {
         return JaWEGraphUI.INSERT_ELEMENT;
      }
   }

   public void setSelectionMode() {
      mainType = GraphEAConstants.SELECT_TYPE;
      subType = GraphEAConstants.SELECT_TYPE_DEFAULT;

      getGraph().setCursor(Cursor.getDefaultCursor());
      reset();
   }

   public void reset() {
      firstPort = null;
      port = null;
      start = null;
      current = null;
      getGraph().repaint();

      ((JaWEGraphUI) getGraph().getUI()).reset();
   }

   public void popupMenu(Point pPopupPoint) {
      double scale = getGraph().getScale();
      Point p = new Point();
      p.setLocation(pPopupPoint.getX() / scale, pPopupPoint.getY() / scale);
      Object cell = getGraph().getFirstCellForLocation(p.x, p.y);
      // needed for addPoint, etc.
      this.popupPoint = new Point(p);
      JPopupMenu menu = createPopupMenu(cell);
      menu.show(getGraph(), (int) pPopupPoint.getX(), (int) pPopupPoint.getY());
   }

   public void insertParticipant(Point whereTo) {
      graphController.setUpdateInProgress(true);
      Participant toInsert = null;
      Participants pars = XMLUtil.getPackage(getGraph().getWorkflowProcess())
         .getParticipants();
      Pool pool = JaWEManager.getInstance()
         .getXPDLUtils()
         .getPoolForProcessOrActivitySet(getGraph().getXPDLObject());
      Lane laneToInsert = null;
      GraphSwimlaneInterface ppar = getGraph().getGraphManager()
         .getParticipantForLocation(whereTo);
      if (ppar != null
          && ppar.getPropertyObject() instanceof Lane
          && (ppar.howManyChildActivitiesOrArtifacts() > 0)) {
         JOptionPane.showMessageDialog(getJaWEFrame(),
                                       getGraphController().getSettings()
                                          .getLanguageDependentString("WarningInvalidOperation"),
                                       getJaWEController().getAppTitle(),
                                       JOptionPane.WARNING_MESSAGE);
         return;
      }

      JaWEManager.getInstance().getJaWEController().startUndouableChange();
      if (GraphEAConstants.LANE_TYPE_FREE_TEXT_EXPRESSION.equals(subType)) {
         laneToInsert = GraphUtilities.createDefaultLane(pool);
      } else if (GraphEAConstants.LANE_TYPE_COMMON_EXPRESSION.equals(subType)) {
         laneToInsert = GraphUtilities.createLaneForPerformer(pool,
                                                              "Common expression lane");
      } else {
         toInsert = JaWEManager.getInstance()
            .getXPDLObjectFactory()
            .createXPDLObject(pars, subType, false);
         pars.add(toInsert);
         laneToInsert = GraphUtilities.createLaneForPerformer(pool, toInsert.getId());
      }
      if (ppar != null && ppar.getPropertyObject() instanceof Lane) {
         Lane parentL = (Lane) ppar.getPropertyObject();
         NestedLane nl = (NestedLane) parentL.getNestedLanes().generateNewElement();
         nl.setLaneId(laneToInsert.getId());
         parentL.getNestedLanes().add(nl);
      }
      GraphUtilities.createNodeGraphicsInfo(laneToInsert, null, null, true);
      getGraphManager().insertParticipantAndArrangeParticipants(laneToInsert, whereTo);
      List toSelect = new ArrayList();
      if (toInsert != null) {
         toSelect.add(toInsert);
      }
      JaWEManager.getInstance().getJaWEController().endUndouableChange(toSelect);
      graphController.setUpdateInProgress(false);
      // getGraph().selectParticipant(toInsert);
      graphController.adjustActions();
   }

   public void insertSpecialElement() {
      // if (isActivitySetButtonSelected()) {
      // Graph g = getGraph();
      // if (g != null) {
      // JaWEManager.getInstance().getJaWEController().startUndouableChange();
      // ActivitySets ass=g.getWorkflowProcess().getActivitySets();
      // ActivitySet
      // as=JaWEManager.getInstance().getXPDLObjectFactory().createXPDLObject(ass,
      // getSelectedButtonType(), true);
      // JaWEManager.getInstance().getJaWEController().endUndouableChange();
      // }
      // }
   }

   public void insertElement(Point whereTo) {
      // if activity is selected
      if (isActivityButtonSelected()
          || isStartButtonSelected() || isEndButtonSelected()
          || isArtifactButtonSelected()) {
         if (!getGraphManager().hasLane()) {
            JOptionPane.showMessageDialog(getJaWEFrame(),
                                          getGraphController().getSettings()
                                             .getLanguageDependentString("WarningInvalidOperation"),
                                          getJaWEController().getAppTitle(),
                                          JOptionPane.WARNING_MESSAGE);

         } else {
            GraphSwimlaneInterface gpar = getGraphManager().findParentParticipantForLocation(whereTo,
                                                                                                null,
                                                                                                null);
            if (!(gpar.getPropertyObject() instanceof Lane)) {
               JOptionPane.showMessageDialog(getJaWEFrame(),
                                             getGraphController().getSettings()
                                                .getLanguageDependentString("WarningInvalidOperation"),
                                             getJaWEController().getAppTitle(),
                                             JOptionPane.WARNING_MESSAGE);
               return;
            }
            Point partLoc = getGraphManager().getCBounds(gpar, null)
               .getBounds()
               .getLocation();
            Point off = new Point(whereTo.x - partLoc.x, whereTo.y - partLoc.y);
            Lane lane = (Lane) gpar.getPropertyObject();
            XMLCollection col = null;
            XMLCollectionElement actOrArt = null;
            if (!isArtifactButtonSelected()) {
               Activities acts = (Activities) getGraph().getXPDLObject()
                  .get("Activities");
               Activity act = JaWEManager.getInstance()
                  .getXPDLObjectFactory()
                  .createXPDLObject(acts, subType, false);
               int acttype = act.getActivityType();
               if (acttype == XPDLConstants.ACTIVITY_TYPE_NO
                   || acttype == XPDLConstants.ACTIVITY_TYPE_TASK_APPLICATION) {
                  String perf = GraphUtilities.getLanesFirstPerformer(lane);
                  act.setFirstPerformer((perf != null) ? perf : "");
               }
               actOrArt = act;
               col = acts;
            } else {
               Artifacts arts = XMLUtil.getPackage(getGraph().getXPDLObject())
                  .getArtifacts();
               Artifact art = JaWEManager.getInstance()
                  .getXPDLObjectFactory()
                  .createXPDLObject(arts, subType, false);
               actOrArt = art;
               col = arts;
            }
            GraphUtilities.setOffsetPoint(actOrArt, off, lane.getId());
            graphController.setUpdateInProgress(true);
            JaWEManager.getInstance().getJaWEController().startUndouableChange();
            col.add(actOrArt);
            getGraphManager().insertActivityOrArtifact(actOrArt);
            List toSelect = new ArrayList();
            toSelect.add(actOrArt);
            JaWEManager.getInstance().getJaWEController().endUndouableChange(toSelect);
            if (actOrArt instanceof Activity) {
               getGraph().selectActivity((Activity) actOrArt, false);
            } else {
               getGraph().selectArtifact((Artifact) actOrArt, false);
            }

            graphController.setUpdateInProgress(false);
         }
      }
   }

   public boolean insertTransitionFirstPort(GraphPortViewInterface pPort) {
      if (pPort != null) {
         if (firstPort == null) {
            // start the transition only if start is valid
            if (validateSource(pPort)) {
               points = new Vector();
               firstPort = pPort;
               double scale = getGraph().getScale();
               start = firstPort.getBounds().getBounds().getLocation();// HM, JGraph3.4.1
               start.x += firstPort.getPortsSize().width / 2;
               start.y += firstPort.getPortsSize().height / 2;
               start = new Point((int) (start.getX() * scale),
                                 (int) (start.getY() * scale));

               return true;
            }
         }
      }

      return false;
   }

   public boolean insertTransitionSecondPort(GraphPortViewInterface pPort) {
      if (pPort != null) {
         // normal
         XMLCollection col = null;
         XMLCollectionElement traOrAsoc = null;
         if (isTransitionButtonSelected()) {
            Transitions tras = (Transitions) getGraph().getXPDLObject()
               .get("Transitions");
            traOrAsoc = JaWEManager.getInstance()
               .getXPDLObjectFactory()
               .createXPDLObject(tras, subType, false);
            col = tras;
         } else {
            Associations asocs = XMLUtil.getPackage(getGraph().getXPDLObject())
               .getAssociations();
            traOrAsoc = JaWEManager.getInstance()
               .getXPDLObjectFactory()
               .createXPDLObject(asocs, subType, false);
            col = asocs;
         }
         if (pPort != firstPort) {
            if (validateConnection(firstPort, pPort, traOrAsoc)) {
               GraphCommonInterface s = ((GraphPortInterface) firstPort.getCell()).getActivityOrArtifact();
               GraphCommonInterface t = ((GraphPortInterface) pPort.getCell()).getActivityOrArtifact();
               XMLCollectionElement sxpdl = (XMLCollectionElement) s.getPropertyObject();
               XMLCollectionElement txpdl = (XMLCollectionElement) t.getPropertyObject();
               String fromId = sxpdl.getId();
               String toId = txpdl.getId();
               if (traOrAsoc instanceof Transition) {
                  ((Transition) traOrAsoc).setFrom(fromId);
                  ((Transition) traOrAsoc).setTo(toId);
               } else {
                  traOrAsoc.set("Source", fromId);
                  traOrAsoc.set("Target", toId);
                  if (JaWEConstants.ASSOCIATION_TYPE_DEFAULT.equals(subType)) {
                     if (t instanceof GraphArtifactInterface) {
                        traOrAsoc.set("AssociationDirection",
                                      XPDLConstants.ASSOCIATION_DIRECTION_FROM);
                     } else {
                        traOrAsoc.set("AssociationDirection",
                                      XPDLConstants.ASSOCIATION_DIRECTION_TO);
                     }
                  }
               }
               if (fromId.equals(toId)) {
                  GraphUtilities.setStyle(traOrAsoc,
                                          GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_BEZIER);
               } else {
                  GraphUtilities.setStyle(traOrAsoc,
                                          getGraphController().getGraphSettings()
                                             .getDefaultTransitionStyle());
               }
               GraphUtilities.setBreakpoints(traOrAsoc, points);
               points.clear();
               graphController.setUpdateInProgress(true);
               JaWEManager.getInstance().getJaWEController().startUndouableChange();
               col.add(traOrAsoc);
               getGraphManager().insertTransitionOrAssociation(traOrAsoc);
               List toSelect = new ArrayList();
               toSelect.add(traOrAsoc);
               JaWEManager.getInstance().getJaWEController().endUndouableChange(toSelect);
               getGraph().selectTransition(traOrAsoc, false);
               graphController.setUpdateInProgress(false);
               getGraph().refresh();
               return true;
            }
            // circular
         } else {
            if (validateConnection(pPort, pPort, traOrAsoc)) {
               Transition tra = (Transition) traOrAsoc;
               Point realP = (Point) getGraph().fromScreen(new Point(start));
               List breakpoints = new ArrayList();
               if (points.size() == 0) {
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
               } else {
                  breakpoints.addAll(points);
                  points.clear();
               }

               Activity act = (Activity) ((GraphActivityInterface) ((GraphPortInterface) firstPort.getCell()).getActivityOrArtifact()).getPropertyObject();

               tra.setFrom(act.getId());
               tra.setTo(act.getId());
               GraphUtilities.setStyle(tra,
                                       GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_BEZIER);
               GraphUtilities.setBreakpoints(tra, breakpoints);
               getGraphController().setUpdateInProgress(true);
               JaWEManager.getInstance().getJaWEController().startUndouableChange();
               col.add(tra);
               getGraphManager().insertTransitionOrAssociation(tra);
               List toSelect = new ArrayList();
               toSelect.add(tra);
               JaWEManager.getInstance().getJaWEController().endUndouableChange(toSelect);
               getGraph().selectTransition(tra, false);
               getGraphController().setUpdateInProgress(false);
               getGraph().refresh();

               return true;
            }
         }
      }

      return false;
   }

   public void overlay(JGraph graph, Graphics g, boolean clear) {
      super.overlay(graph, g, clear);
      if (getGraph() != null) {
         paintPort(getGraph().getGraphics());
      }
      if (start != null) {
         if (isTransitionButtonSelected() || isAssociationButtonSelected()) {
            drawTransition(g);
         }
      }
   }

   protected void drawTransition(MouseEvent ev) {
      Graphics g = getGraph().getGraphics();
      Color bg = getGraph().getBackground();
      Color fg = Color.black;
      g.setColor(fg);
      g.setXORMode(bg);
      overlay(getGraph(), g, false);
      current = (Point) getGraph().snap(ev.getPoint());
      double scale = getGraph().getScale();
      port = (GraphPortViewInterface) getGraph().getPortViewAt(ev.getX(), ev.getY());
      if (port != null) {
         current = port.getBounds().getBounds().getLocation();// HM, JGraph3.4.1
         // current=lastPort.getLocation(null);
         current = new Point((int) (current.x * scale), (int) (current.y * scale));
         current.x += port.getPortsSize().width / 2;
         current.y += port.getPortsSize().height / 2;

      }
      g.setColor(bg);
      g.setXORMode(fg);
      overlay(getGraph(), g, false);
   }

   protected void drawTransition(Graphics g) {
      Point l = start;
      if (points.size() != 0)
         l = (Point) points.get(points.size() - 1);
      if (current != null) {
         if (isAssociationButtonSelected()) {
            ((Graphics2D) g).setStroke(new BasicStroke(1,
                                                       BasicStroke.CAP_BUTT,
                                                       BasicStroke.JOIN_MITER,
                                                       10.0f,
                                                       new float[] {
                                                          2
                                                       },
                                                       0));
         } else {
            ((Graphics2D) g).setStroke(new BasicStroke(1));
         }
         g.drawLine(l.x, l.y, current.x, current.y);
      }
   }

   protected void paintPort(Graphics g) {
      if (port != null) {
         boolean offset = (GraphConstants.getOffset(port.getAttributes()) != null);
         CellView v = port.getParentView();
         Rectangle bounds = v.getBounds().getBounds();
         Rectangle r = (offset) ? port.getBounds().getBounds()// HM, JGraph3.4.1
                               : bounds;// HM,
         // JGraph3.4.1
         r = (Rectangle) getGraph().toScreen(new Rectangle(r));// HM, JGraph3.4.1
         int s = 3;
         r.translate(-s, -s);
         r.setSize(r.width + 2 * s, r.height + 2 * s);
         JaWEGraphUI ui = (JaWEGraphUI) getGraph().getUI();
         ui.paintCell(g, port, r, true);
      }
   }

   public String getMainType() {
      return mainType;
   }

   public String getSubType() {
      return subType;
   }

   public void setType(String mainType, String subType, Cursor cursor) {
      this.mainType = mainType;
      this.subType = subType;

      if (cursor != null)
         getGraph().setCursor(cursor);
      else
         getGraph().setCursor(Cursor.getDefaultCursor());

      reset();
   }
}
