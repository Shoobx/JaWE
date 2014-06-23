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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import org.enhydra.jawe.JaWEConstants;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Activities;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ActivitySet;
import org.enhydra.jxpdl.elements.Artifact;
import org.enhydra.jxpdl.elements.Artifacts;
import org.enhydra.jxpdl.elements.Association;
import org.enhydra.jxpdl.elements.Associations;
import org.enhydra.jxpdl.elements.Lane;
import org.enhydra.jxpdl.elements.Lanes;
import org.enhydra.jxpdl.elements.NestedLane;
import org.enhydra.jxpdl.elements.NestedLanes;
import org.enhydra.jxpdl.elements.Participant;
import org.enhydra.jxpdl.elements.Pool;
import org.enhydra.jxpdl.elements.Transition;
import org.enhydra.jxpdl.elements.Transitions;
import org.enhydra.jxpdl.elements.WorkflowProcess;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellView;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.Edge;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.ParentMap;
import org.jgraph.graph.Port;

/**
 * Class intended to serve as a control center for creation, removal, resizing and
 * changing position of Participants as well as for doing the same things with Activity
 * objects and Transitions. Class manages this actions in such a way that undoing of
 * operations are possible. It incorporates multiple view and model changes into one by
 * doing them virtually, and after all changes are done, in interaction with
 * JaWEGraphModel class applies this changes so that undo is possible. Instance of this
 * class is joined to to all objects of classes derived from Graph class.
 * <p>
 * This class also handles the relationships between visual and logical representation of
 * workflow graph.
 * <p>
 * When reading a package from an XML file, this class creates imported objects and
 * establishes relationship between graph objects (classes within jawe and jawe.graph
 * package) and 'graph logic' objects ( classes within jawe.xml package).
 * 
 * @author Sasa Bojanic
 * @author Miroslav Popov
 */
public class GraphManager implements Serializable {

   /** Graph reference */
   private transient Graph graph;

   /**
    * Offset for drawing - no meaning (it was used in previous versions)
    */
   private int horizontalOffset = 0;

   private int verticalOffset = 0;

   /** Variable that holds minimum width for any participant */
   private static int minLaneWidth;

   /** Variable that holds minimum height for any participant */
   private static int minLaneHeight;

   /** Variable that holds the width for participant name section */
   private static int defLaneNameWidth;

   /** Variable that holds width of activities */
   private static int defActivityWidth;

   /** Variable that holds height of activities */
   private static int defActivityHeight;

   private boolean creatingGraph = false;

   protected boolean pLoaded = false;

   public void init() {
      if (!pLoaded) {

         defActivityWidth = GraphUtilities.getGraphController().getGraphSettings().getActivityWidth();
         defActivityHeight = GraphUtilities.getGraphController().getGraphSettings().getActivityHeight();
         minLaneWidth = GraphUtilities.getGraphController().getGraphSettings().getMinLaneWidth();
         minLaneHeight = GraphUtilities.getGraphController().getGraphSettings().getMinLaneHeight();
         defLaneNameWidth = GraphUtilities.getGraphController().getGraphSettings().getLaneNameWidth();

         pLoaded = true;
      }
   }

   /**
    * Creates new workflow manager for given graph.
    * 
    * @param g The graph that manager manages.
    */
   public GraphManager(Graph g) {
      this.graph = g;
      init();
   }

   /** Returns the graph which is managed. */
   public Graph getGraph() {
      return graph;
   }

   public GraphController getGraphController() {
      return graph.getGraphController();
   }

   /**
    * Returns the graph model - the model that represents the graph view. (See JGraph
    * documentation).
    */
   public JaWEGraphModel graphModel() {
      return (JaWEGraphModel) graph.getModel();
   }

   /**
    * Creates graph representation of given workflow process. It creates a graph entities
    * (participants, activities, transitions) and associates the workflow logic to it. The
    * graph entities are inserted according to the data that <tt>wp</tt> object holds (the
    * data from XML file).
    * <p>
    * This is used when reading a workflow definition from an XML file.
    * 
    * @param wpOrAs Object that mapps the logic of WorkflowProcess element of XML -
    *           defines a particular Workflow process.
    */
   public void createWorkflowGraph(XMLCollectionElement wpOrAs) {
      creatingGraph = true;
      // checks if it is graph made by JaWE

      // ********* the creation other is very important and shouldn't be changed
      // System.out.println("Creating graph for WP "+wp+" and
      // VO="+getVisualOwner());

      Pool p = JaWEManager.getInstance().getXPDLUtils().getPoolForProcessOrActivitySet(wpOrAs);
      insertParticipantAndArrangeParticipants(p, null);

      List participantsToInsert = GraphUtilities.gatherLanes(wpOrAs);

      // show Participants and their activities
      Iterator it = participantsToInsert.iterator();
      while (it.hasNext()) {
         LaneInfo pi = (LaneInfo) it.next();
         // GraphSwimlaneInterface gpar =
         // insertParticipantAndArrangeParticipants(pi.getParticipant());
         insertParticipantAndArrangeParticipants(pi.getLane(), null);
         // System.out.println("Participant " + gpar + " inserted for xpdl par " +
         // pi.getParticipant().getId());
         Iterator actsAndArts = pi.getActivitiesAndArtifacts().iterator();
         while (actsAndArts.hasNext()) {
            XMLCollectionElement actOrArt = (XMLCollectionElement) actsAndArts.next();
            insertActivityOrArtifact(actOrArt);
         }
      }

      // show transitions
      // System.out.println("Inserting transitions for " + getXPDLOwner().getId());
      it = ((Transitions) wpOrAs.get("Transitions")).toElements().iterator();
      while (it.hasNext()) {
         Transition tra = (Transition) it.next();
         insertTransitionOrAssociation(tra);
      }

      it = XMLUtil.getPackage(wpOrAs).getAssociations().toElements().iterator();
      while (it.hasNext()) {
         Association asoc = (Association) it.next();
         insertTransitionOrAssociation(asoc);
      }

      creatingGraph = false;
      try {
         graph.setPreferredSize(getGraphsPreferredSize());
      } catch (Exception ex) {
      }
   }

   /**
    * Returns the object (part of mapped XML logic) that is represented by the graph
    * managed by WorklowManager. That object can be instance of the Package,
    * WorkflowProcess or ...xml.elements.BlockActivity class, and is held as a property of
    * the manager's graph object.
    * 
    * @return The object (representing XML logic) that is represented by this manager's
    *         graph.
    */
   public XMLCollectionElement getXPDLOwner() {
      return graph.getXPDLObject();
   }

   /**
    * Returns the (XML logic) WorkflowProcess object that is represented by the manager's
    * graph, or (if the graph represents the (XML logic) BlockActivity content) the
    * WorkflowProcess object that holds BlockActivity represented by manager's graph. If
    * graph represents (XML logic) Package object, <tt>null</tt> is returned.
    */
   private WorkflowProcess getWorkflowProcess() {
      return graph.getWorkflowProcess();
   }

   public List getDisplayedParticipants() {
      List dps = new ArrayList();
      List graphParts = JaWEGraphModel.getAllParticipantsInModel(graphModel());
      if (graphParts != null) {
         GraphSwimlaneInterface graphPart;
         Iterator it = graphParts.iterator();
         while (it.hasNext()) {
            graphPart = (GraphSwimlaneInterface) it.next();
            Object gp = graphPart.getPropertyObject();
            if (gp instanceof Pool)
               continue;
            Participant par = GraphUtilities.getParticipantForLane((Lane) gp, FreeTextExpressionParticipant.getInstance());
            dps.add(par);
         }
      }
      return dps;
   }

   public boolean doesRootParticipantExist() {
      Set rootParticipants = JaWEGraphModel.getRootParticipants(graphModel());
      if (rootParticipants != null && rootParticipants.size() > 0) {
         return true;
      }
      return false;
   }

   public boolean hasLane() {
      Set rootParticipants = JaWEGraphModel.getRootParticipants(graphModel());
      if (rootParticipants != null && rootParticipants.size() > 0) {
         GraphSwimlaneInterface gp = (GraphSwimlaneInterface) rootParticipants.toArray()[0];
         return gp.hasAnySwimlane();
      }
      return false;
   }

   public String getParticipantId(Point pos) {
      GraphSwimlaneInterface gpar = findParentParticipantForLocation(pos, null, null);
      if (gpar != null) {
         return gpar.getPropertyObject().get("Id").toValue();
      }
      return "";
   }

   // ----------------------- Activity handling
   public GraphCommonInterface insertActivityOrArtifact(XMLCollectionElement xpdla) {
      if (xpdla instanceof Artifact && !getGraph().shouldShowArtifacts())
         return null;
      // System.out.println("Inserting activity "+xpdla+", id=" + xpdla.getId() +
      // ", name=" + xpdla.getName() + ", parId="
      // + GraphUtilities.getParticipantId(xpdla));
      Map viewMap = new HashMap();

      GraphSwimlaneInterface gpar = getGraphParticipant(GraphUtilities.getLaneForActivityOrArtifact(xpdla, graph.getXPDLObject()));
      if (gpar == null)
         return null;
      // if (gpar==null) {
      // Point p=JaWEEAHandler.getOffsetPoint(xpdla);
      // gpar=findParentActivityParticipantForLocation(p,null,null);
      // }
      Point p = getCBounds(gpar, null).getBounds().getLocation();
      GraphCommonInterface gact = null;
      if (xpdla instanceof Activity) {
         gact = getGraphController().getGraphObjectFactory().createActivity(viewMap, (Activity) xpdla, p);
      } else {
         gact = getGraphController().getGraphObjectFactory().createArtifact(viewMap, (Artifact) xpdla, p);
      }
      // updateModelAndArrangeParticipants(new Object[] {act},viewMap,null,null,
      updateModelAndArrangeParticipants(new Object[] {
         gact
      }, null, null, viewMap, getGraphController().getSettings().getLanguageDependentString("MessageInsertingGenericActivity"), null, true);
      return gact;
   }

   public void removeActivityOrArtifact(XMLCollectionElement xpdla) {
      if (xpdla instanceof Artifact && !getGraph().shouldShowArtifacts())
         return;

      Set edges = null;
      GraphCommonInterface gora = null;
      if (xpdla instanceof Activity) {
         gora = getGraphActivity((Activity) xpdla);
      } else {
         gora = getGraphArtifact((Artifact) xpdla);
      }
      if (gora == null)
         return;
      edges = gora.getPort().getEdges();
      Iterator it = edges.iterator();
      Set toRem = new HashSet();
      while (it.hasNext()) {
         GraphTransitionInterface gt = (GraphTransitionInterface) it.next();
         toRem.add(gt);
      }
      toRem.add(gora);
      Object[] remove = toRem.toArray();
      graphModel().removeAndEdit(remove, null);

      Map propertyMap = new HashMap();
      ParentMap parentMap = new JaWEParentMap();
      arrangeParticipants(propertyMap, parentMap);
      updateModelAndArrangeParticipants(new Object[] {}, propertyMap, parentMap, new HashMap(), "an", null, true);

   }

   public void arrangeActivityOrArtifactPosition(XMLCollectionElement actOrArt) {
      // System.out.println("Searching graph act for id "+act.getId());
      GraphCommonInterface gact = actOrArt instanceof Activity ? getGraphActivity((Activity) actOrArt) : getGraphArtifact((Artifact) actOrArt);
      // System.out.println("agact "+gact);
      GraphSwimlaneInterface newPar = getGraphParticipant(GraphUtilities.getLaneForActivityOrArtifact(actOrArt, getXPDLOwner()));
      // System.out.println("newpar "+newPar);
      GraphSwimlaneInterface oldPar = (GraphSwimlaneInterface) gact.getParent();
      // System.out.println("odlpar "+oldPar);
      // if (newPar==null || oldPar==null) return; // can happen when participant Id is
      // updated
      ParentMap parentMap = new JaWEParentMap();
      if (!newPar.equals(oldPar)) {
         parentMap.addEntry(gact, newPar);
      }

      Map propertyMap = new HashMap();
      Point actRealPos = getRealPosition(gact, newPar);
      // GraphSwimlaneViewInterface
      // gpvi=(GraphSwimlaneViewInterface)getView(newPar);
      // Rectangle parBounds=getBounds(newPar, propertyMap);
      // if (actRealPos.x>parBounds.width) {
      // // gpvi.
      // resizeAllParticipantsHorizontally(propertyMap, parentMap);
      // }
      Rectangle oldb = getCBounds(gact, propertyMap);
      changeBounds(gact, propertyMap, new Rectangle(actRealPos, new Dimension(oldb.width, oldb.height)));
      if (!newPar.equals(oldPar)) {
         if (isGraphRotated()) {
            arrangeParticipantHorizontally(newPar, propertyMap, parentMap);
            if (oldPar != null) {
               arrangeParticipantHorizontally(oldPar, propertyMap, parentMap);
            }
         } else {
            arrangeParticipantVertically(newPar, propertyMap, parentMap);
            if (oldPar != null) {
               arrangeParticipantVertically(oldPar, propertyMap, parentMap);
            }
         }
      }
      updateModelAndArrangeParticipants(null,
                                        propertyMap,
                                        parentMap,
                                        null,
                                        getGraphController().getSettings().getLanguageDependentString("MessageMovingObjects"),
                                        null,
                                        true);

   }

   // --------------------- transition handling
   public GraphTransitionInterface insertTransitionOrAssociation(XMLCollectionElement xpdltra) {
      Map viewMap = new HashMap();
      // System.out.println("Inserting xpdltra " + xpdltra.getId() + ", F=" +
      // xpdltra.getFrom() + ", T=" + xpdltra.getTo());
      List breakPoints = GraphUtilities.getBreakpoints(xpdltra);
      GraphCommonInterface source = null;
      GraphCommonInterface target = null;
      if (xpdltra instanceof Transition) {
         source = getGraphActivity(((Transition) xpdltra).getFrom());
         target = getGraphActivity(((Transition) xpdltra).getTo());
      } else {
         source = getGraphActivity(((Association) xpdltra).getSource());
         if (source == null) {
            source = getGraphArtifact(((Association) xpdltra).getSource());
         }
         target = getGraphActivity(((Association) xpdltra).getTarget());
         if (target == null) {
            target = getGraphArtifact(((Association) xpdltra).getTarget());
         }
      }
      if (source == null || target == null)
         return null;
      Point p11 = getCenter(source);
      Point p21 = getCenter(target);
      Point p = (Point) graph.fromScreen(new Point(p11));
      Point p2 = (Point) graph.fromScreen(new Point(p21));
      List points = new ArrayList();
      points.add(p);
      points.addAll(breakPoints);
      points.add(p2);

      GraphTransitionInterface gtra = getGraphController().getGraphObjectFactory().createTransition(points, viewMap, xpdltra);
      // System.out.println("\ngTra11="+getView(gtra).getBounds());
      Object[] insert = new Object[] {
         gtra
      };
      ConnectionSet cs = new ConnectionSet();

      cs.connect(gtra, source.getPort(), target.getPort());

      // cs.connect(transition,source.getCell(),true);
      // cs.connect(transition,target.getCell(),false);
      // graphModel().insertAndEdit(insert,cs,null,null,viewMap,undoMsg);

      graphModel().insertAndEdit(insert, viewMap, cs, null, null);

      return gtra;
   }

   public void removeTransitionOrAssociation(XMLCollectionElement xpdltra) {
      // System.out.println("Graphmanager->removing transition "+xpdltra.getId());
      GraphTransitionInterface gtra = getGraphTransition(xpdltra);
      // System.out.println("              found graph transition "+gtra);
      removeTransition(gtra);
   }

   public void updateTransitionOrAssociation(XMLCollectionElement xpdltra) {
      GraphTransitionInterface gtra = getGraphTransition(xpdltra);
      if (gtra == null) {
         gtra = insertTransitionOrAssociation(xpdltra);
      } else {

         GraphCommonInterface s1 = gtra.getSourceActivityOrArtifact();
         GraphCommonInterface t1 = gtra.getTargetActivityOrArtifact();

         if (s1 == null || t1 == null) {
            removeTransition(gtra);
            gtra = insertTransitionOrAssociation(xpdltra);
            return;
         }
         String from = null;
         String to = null;
         if (xpdltra instanceof Transition) {
            from = ((Transition) xpdltra).getFrom();
            to = ((Transition) xpdltra).getTo();
         } else {
            from = ((Association) xpdltra).getSource();
            to = ((Association) xpdltra).getTarget();
         }
         if (!s1.getPropertyObject().get("Id").toValue().equals(from) || !t1.getPropertyObject().get("Id").toValue().equals(to)) {
            removeTransition(gtra);
            gtra = insertTransitionOrAssociation(xpdltra);
            return;
         }

         Map propertyMap = new HashMap();

         // breakpoints
         updateBreakPoints(gtra, propertyMap);

         // label position
         updateLabelPosition(gtra, propertyMap);

         // style
         updateStyle(gtra, propertyMap);

         ((JaWEGraphModel) graph.getModel()).insertAndEdit(null, propertyMap, null, null, null);

      }
   }

   public void updateBreakPoints(GraphTransitionInterface gtra, Map propertyMap) {
      List bps = GraphUtilities.getBreakpoints((XMLCollectionElement) gtra.getUserObject());
      GraphTransitionViewInterface gtraview = (GraphTransitionViewInterface) getView(gtra);
      if (gtraview == null)
         return;
      AttributeMap map = (AttributeMap) propertyMap.get(gtra);
      if (map == null) {
         map = new AttributeMap(gtra.getAttributes());
         propertyMap.put(gtra, map);
      }
      int pcnt = gtraview.getPointCount();
      Point2D ps = gtraview.getPoint(0);
      Point2D pt = gtraview.getPoint(pcnt - 1);
      List points = new ArrayList();
      points.add(ps);
      points.addAll(bps);
      points.add(pt);
      // JaWEManager.getInstance().getLoggingManager().debug("Updating breakpoints for transition: "+points);
      GraphConstants.setPoints(map, points);
   }

   public void updateLabelPosition(GraphTransitionInterface gtra, Map propertyMap) {
      Point lp = GraphUtilities.getLabelPosition((XMLCollectionElement) gtra.getUserObject());
      Point lpo = GraphUtilities.getLabelPositionOffset((XMLCollectionElement) gtra.getUserObject());
      GraphTransitionViewInterface gtraview = (GraphTransitionViewInterface) getView(gtra);
      if (gtraview == null)
         return;
      AttributeMap map = (AttributeMap) propertyMap.get(gtra);
      if (map == null) {
         map = new AttributeMap(gtra.getAttributes());
         propertyMap.put(gtra, map);
      }

      // JaWEManager.getInstance().getLoggingManager().debug("Updating label position for transition: "+points);
      if (lp!=null) {
         GraphConstants.setLabelPosition(map, lp);
         if (lpo!=null) {
            GraphConstants.setOffset(map, lpo);
         }
      } else {
         int center = GraphConstants.PERMILLE / 2;
         Point labelPosition = new Point(center, GraphConstants.PERMILLE / 100);
         GraphConstants.setLabelPosition(map, labelPosition);
         GraphConstants.setOffset(map, new Point(0,0));
      }
   }

   public List addOrRemoveBreakPoint(GraphTransitionInterface gtra, Point p, boolean toAdd) {
      CellView view = graph.getGraphLayoutCache().getMapping(gtra, false);
      GraphTransitionViewInterface tv = (GraphTransitionViewInterface) view;
      if (toAdd) {
         tv.addPoint(graph, p);
      } else {
         tv.removePoint(graph, p);
      }
      graph.refresh();

      int noOfPoints = tv.getPointCount();
      List pnts = new ArrayList();
      for (int i = 1; i < noOfPoints - 1; i++) {
         pnts.add(new Point((int) tv.getPoint(i).getX(), (int) tv.getPoint(i).getY()));// HM,
         // JGraph3.4.1
      }
      // JaWEManager.getInstance().getLoggingManager().debug("Adding/removing breakpoint for transition: "+pnts);
      return pnts;
   }

   public void updateStyle(GraphTransitionInterface gtra) {
      Map propertyMap = new HashMap();
      updateStyle(gtra, propertyMap);
      ((JaWEGraphModel) graph.getModel()).insertAndEdit(null, propertyMap, null, null, null);
   }

   protected void updateStyle(GraphTransitionInterface gtra, Map propertyMap) {
      String style = GraphUtilities.getStyle((XMLCollectionElement) gtra.getUserObject());
      AttributeMap map = (AttributeMap) propertyMap.get(gtra);
      if (map == null) {
         map = new AttributeMap(gtra.getAttributes());
         propertyMap.put(gtra, map);
      }

      if (style.equals(GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_BEZIER)) {
         GraphConstants.setRouting(map, new NoRouting());
         GraphConstants.setLineStyle(map, GraphConstants.STYLE_BEZIER);
      } else if (style.equals(GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_SPLINE)) {
         GraphConstants.setRouting(map, new NoRouting());
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
         GraphConstants.setRouting(map, new NoRouting());
         GraphConstants.setLineStyle(map, GraphConstants.STYLE_ORTHOGONAL);
      }

   }

   protected void removeTransition(GraphTransitionInterface gtra) {
      if (gtra == null)
         return;
      // System.out.println("\ngTra11="+getView(gtra).getBounds());
      Object[] remove = new Object[] {
         gtra
      };
      // cs.connect(transition,source.getCell(),true);
      // cs.connect(transition,target.getCell(),false);
      graphModel().removeAndEdit(remove, null);

      Map propertyMap = new HashMap();
      ParentMap parentMap = new JaWEParentMap();
      arrangeParticipants(propertyMap, parentMap);
      updateModelAndArrangeParticipants(new Object[] {}, propertyMap, parentMap, new HashMap(), "an", null, true);

   }

   // --------------------- start/end event handling
   public GraphActivityInterface insertStartOrEndEvent(Activity sea, String connectingActId) {
      Map viewMap = new HashMap();
      GraphActivityInterface gact = (GraphActivityInterface) insertActivityOrArtifact(sea);
      connectStartOrEndEvent(gact, connectingActId);
      return gact;
   }

   public GraphTransitionInterface connectStartOrEndEvent(GraphActivityInterface startOrEnd, String actId) {
      if (actId == null)
         return null;

      GraphActivityInterface source = startOrEnd;
      Activity sed = (Activity) startOrEnd.getPropertyObject();
      GraphActivityInterface target = getGraphActivity(actId);

      if (source == null || target == null)
         return null;

      if (sed.getActivityType() != XPDLConstants.ACTIVITY_TYPE_EVENT_START) {
         source = target;
         target = startOrEnd;
      }
      Transitions tras = (Transitions) getXPDLOwner().get("Transitions");
      Transition tra = JaWEManager.getInstance().getXPDLObjectFactory().createXPDLObject(tras, "", true);
      tra.setFrom(source.getPropertyObject().get("Id").toValue());
      tra.setTo(target.getPropertyObject().get("Id").toValue());

      return insertTransitionOrAssociation(tra);
   }

   // ----------------------- participant handling

   /**
    * Inserts new Participant cell into model. First, the parent of new Participant is
    * searched, and if found, put into ParentMap (it is not inserted into model at ones).
    * If parent participant isn't found -> root participant will be inserted. After that
    * model's view is arranged (Participants are moved and translated along with it's
    * children cells) to suite to the new model state - this is done "virtually" which
    * means that changes are not directly applied to view until all changes are made. At
    * the end, all changes are applied to model and view. Such procedure enables compound
    * undo support. <BR>
    * This method is called when inserting new Participant into model.
    */
   public GraphSwimlaneInterface insertParticipantAndArrangeParticipants(Object par, Point whereTo) {
      GraphSwimlaneInterface gparentpar = getParticipantForLocation(whereTo);
      if (par instanceof Lane) {
         Lane parentLane = GraphUtilities.getParentLane((Lane) par);
         if (parentLane != null) {
            gparentpar = getGraphParticipant(parentLane);
         } else {
            if (gparentpar == null) {
               gparentpar = getGraphParticipant(JaWEManager.getInstance().getXPDLUtils().getPoolForProcessOrActivitySet(getXPDLOwner()));
            }
         }
      }
      Map viewMap = new HashMap();
      ParentMap parentMap = new JaWEParentMap();
      Map propertyMap = null;

      // get the topmost participant at the location where
      // user want to insert participant
      Rectangle bounds = null;
      // System.err.println("Inserting participant "+par.getId()+", isGR="+isGraphRotated());
      if (gparentpar == null) {
         if (isGraphRotated()) {
            bounds = new Rectangle(getNewRootParXPos(null, null), verticalOffset, minLaneHeight, getRootParticipantHeight(null, null));
         } else {
            bounds = new Rectangle(horizontalOffset, getNewRootParYPos(null, null), getRootParticipantWidth(null, null), minLaneHeight);
         }
      } else {

         // get the number of existing child participants in parent par.
         int hmc = gparentpar.howManyChildSwimlanes();
         // get the number of parent participants of ppar
         int hmp = gparentpar.getLevel();

         if (!isGraphRotated()) {
            // calculate position and dimension of new participant
            boolean resizeAllhorizontally = false;
            int dhorizontally = 0;
            Rectangle r = getCBounds(gparentpar, null);
            int xPos = (1 + hmp) * defLaneNameWidth + horizontalOffset;
            int yPos = ((int) r.getY());
            int height = minLaneHeight;
            if (hmc > 0) {
               yPos += (int) r.getHeight();
            } else {
               height = (int) r.getHeight();
            }
            int dWidth = getRootParticipantWidth(null, null) + horizontalOffset - xPos;

            if (dWidth < minLaneWidth) {
               resizeAllhorizontally = true;
               dhorizontally = minLaneWidth - dWidth;
               dWidth = minLaneWidth;
            }

            // bounds of new Participant
            bounds = new Rectangle(xPos, yPos, dWidth, height);

            propertyMap = new HashMap();
            // if there is a need, resize all participants horizontally
            if (resizeAllhorizontally) {
               List participants = JaWEGraphModel.getAllParticipantsInModel(graphModel());
               if (participants != null) {
                  resize(participants.toArray(), propertyMap, dhorizontally, 0);
               }
            }

            // if there is a need, translate participants under yPos vertically, and
            // resize all parents of new participant
            if (hmc > 0) {
               translateVertically(propertyMap, null, yPos, minLaneHeight);
               resize(gparentpar.getPath(), propertyMap, 0, minLaneHeight);
            }

         } else {
            // calculate position and dimension of new participant
            boolean resizeAllVertically = false;
            int dvertically = 0;
            Rectangle r = getCBounds(gparentpar, null);
            int yPos = (1 + hmp) * defLaneNameWidth + verticalOffset;
            int xPos = ((int) r.getX());
            int width = minLaneHeight;
            if (hmc > 0) {
               xPos += (int) r.getWidth();
            } else {
               width = (int) r.getWidth();
            }
            int dHeight = getRootParticipantHeight(null, null) + verticalOffset - yPos;

            if (dHeight < minLaneWidth) {
               resizeAllVertically = true;
               dvertically = minLaneWidth - dHeight;
               dHeight = minLaneWidth;
            }

            // bounds of new Participant
            bounds = new Rectangle(xPos, yPos, width, dHeight);

            propertyMap = new HashMap();
            // if there is a need, resize all participants horizontally
            if (resizeAllVertically) {
               List participants = JaWEGraphModel.getAllParticipantsInModel(graphModel());
               if (participants != null) {
                  resize(participants.toArray(), propertyMap, 0, dvertically);
               }
            }

            // if there is a need, translate participants under yPos vertically, and
            // resize all parents of new participant
            if (hmc > 0) {
               translateHorizontally(propertyMap, null, xPos, minLaneHeight);
               resize(gparentpar.getPath(), propertyMap, minLaneHeight, 0);
            }
         }
         // par=createParticipant(rNew,viewMap);
         // // creating parent map and adding entry for this participant
         // parentMap = new PEParentMap();
         // parentMap.addEntry(par,pPar);
         //
         //
         // Rectangle b = getBounds(gparentpar, null);
         // if (isGraphRotated()) {
         // bounds = new Rectangle(b.x, b.y + defLaneNameWidth, b.width, b.height);
         // } else {
         // bounds = new Rectangle(b.x + defLaneNameWidth,
         // b.y,
         // b.width,
         // b.height);
         // }
      }
      GraphSwimlaneInterface gpar = getGraphController().getGraphObjectFactory().createParticipant(bounds, viewMap, par);

      if (gparentpar != null) {
         parentMap.addEntry(gpar, gparentpar);
         propertyMap.putAll(viewMap);
      } else {
         propertyMap = new HashMap(viewMap);
      }

      Object[] insert = new Object[] {
         gpar
      };
      // if (isGraphRotated()) {
      // resizeAllParticipantsVertically(propertyMap, parentMap);
      // } else {
      // resizeAllParticipantsHorizontally(propertyMap, parentMap);
      // }
      // graphModel().insertAndEdit(insert,null,propertyMap,parentMap,viewMap,
      graphModel().insertAndEdit(insert, propertyMap, null, parentMap, null);

      if (!creatingGraph) {
         try {
            graph.setPreferredSize(getGraphsPreferredSize());
         } catch (Exception ex) {
         }
      }

      return gpar;
   }

   public GraphSwimlaneInterface getParticipantForLocation(Point whereTo) {
      GraphSwimlaneInterface gparentpar = null;
      if (whereTo != null) {
         Object fc = graph.getFirstCellForLocation(whereTo.x, whereTo.y);
         if (fc == null || !(fc instanceof GraphSwimlaneInterface)) {
            if (isGraphRotated()) {
               gparentpar = getLeafParticipantForXPos(whereTo.x, null, null);
            } else {
               gparentpar = getLeafParticipantForYPos(whereTo.y, null, null);
            }
         } else {
            gparentpar = (GraphSwimlaneInterface) fc;
         }
      }
      return gparentpar;
   }

   /**
    * Removes cells <b>cellsToDelete </b> from model. This means that given cells and all
    * of their descendants as well as all transitions that connects given cells, will be
    * removed from model. First, all remained participants are moved and resized according
    * to participants that are beeing removed and ParentMap for all removed cells is
    * created (all these things are made "virtually" - not applied to model and view).
    * After that model's new view is arranged (Participants are moved and translated
    * (along with it's children cells) according to the remained children - this is also
    * done "virtually". At the end, all changes are applied to model and view. Such
    * procedure enables compound undo support. <BR>
    * This method is called when deleting or cutting cells from graph.
    */
   public void removeCellsAndArrangeParticipants(Object[] cellsToDelete) {
      Set participantsToArrange = new HashSet();
      Map propertyMap = new HashMap();
      ParentMap parentMap = new JaWEParentMap();

      Set ports = new HashSet();
      // begining arrangement of parent of cells that will be deleted
      if (cellsToDelete != null && cellsToDelete.length > 0) {
         for (int i = 0; i < cellsToDelete.length; i++) {
            if (cellsToDelete[i] instanceof GraphSwimlaneInterface) {
               GraphSwimlaneInterface par = (GraphSwimlaneInterface) cellsToDelete[i];

               // getting bounds of rectangle
               Rectangle2D r = getCBounds(par, propertyMap);// HM, JGraph3.4.1
               int yPos = r.getBounds().y + r.getBounds().height - 1;// HM,
               int xPos = r.getBounds().x + r.getBounds().width - 1;// HM,
               // JGraph3.4.1

               // resize all parent participants (reduce their sizes) if needed,
               // and
               // also if needed, translate up all participants under yPos
               GraphSwimlaneInterface ppar = (GraphSwimlaneInterface) par.getParent();
               if (ppar != null) {
                  // resizing and translating is needed only if "first" parent
                  // has other children except one that is beeing removed
                  // or if its height can be reduced
                  if (ppar.getChildCount() > 1 || getParticipantHeight(ppar, propertyMap) > minLaneHeight) {
                     // gets all parents and resizes it
                     Object[] allParents = ppar.getPath();
                     // calculates resize value
                     if (!isGraphRotated()) {
                        int resizeValue = r.getBounds().height;// HM, JGraph3.4.1
                        int pHeight = getParticipantHeight(ppar, propertyMap);
                        // do not allow to resize participant under it's minimal
                        // height
                        if (pHeight - resizeValue < minLaneHeight) {// HM,
                           // JGraph3.4.1
                           resizeValue = pHeight - minLaneHeight;
                        }
                        resize(allParents, propertyMap, 0, -resizeValue);
                        // translate up all participants under yPos
                        translateVertically(propertyMap, null, yPos, -resizeValue);
                     } else {
                        int resizeValue = r.getBounds().width;// HM, JGraph3.4.1
                        int pWidth = getParticipantWidth(ppar, propertyMap);
                        // do not allow to resize participant under it's minimal
                        // height
                        if (pWidth - resizeValue < minLaneHeight) {// HM,
                           // JGraph3.4.1
                           resizeValue = pWidth - minLaneHeight;
                        }
                        resize(allParents, propertyMap, -resizeValue, 0);
                        // translate up all participants under yPos
                        translateHorizontally(propertyMap, null, xPos, -resizeValue);
                     }
                  }
               }
               // if participant is root there is a need of translating
               // participants under it
               else {
                  // translate up all participants under yPos
                  if (!isGraphRotated()) {
                     translateVertically(propertyMap, null, yPos, -r.getBounds().height);// HM,
                     // JGraph3.4.1
                  } else {
                     // JGraph3.4.1
                     translateHorizontally(propertyMap, null, xPos, -r.getBounds().width);
                  }
               }
               // if some port is originally set to be deleted it must be
               // removed
            } else if (cellsToDelete[i] instanceof Port) {
               ports.add(cellsToDelete[i]);
            }
         }

         // removing ports if they were somehow selected
         Set ctd = new HashSet(Arrays.asList(cellsToDelete));
         ctd.removeAll(ports);
         if (ctd.size() == 0)
            return;
         cellsToDelete = ctd.toArray();

         // after previous, participant's are transleted and resized to certain
         // extent,
         // now final resizing and translation takes place (translation and
         // resizing
         // that calculates minimal possible height and width of participants
         // according
         // to the activities that are contained within participant after
         // deletion)

         // All cells in model to be deleted
         List cellsToDel = JaWEGraphModel.getDescendants(graphModel(), cellsToDelete);
         // getting transitions(edges) which are connected to the cellsForDel ->
         // also has to be deleted
         Set edges = JaWEGraphModel.getEdges(graphModel(), cellsToDel.toArray());

         // putting all items for deletation (edges and cells) together - thats
         // ALL FOR DELETION
         cellsToDel.addAll(edges);

         // Separate cells and edges
         Set allEdgesToDelete = new HashSet();
         Set allCellsToDelete = new HashSet();
         Iterator it = cellsToDel.iterator();
         while (it.hasNext()) {
            Object cell = it.next();
            if (cell instanceof Edge) {
               allEdgesToDelete.add(cell);
            } else if (!(cell instanceof Port)) {
               allCellsToDelete.add(cell);
            }
         }

         // working resize and translation only with cells (edges doesn't count)
         cellsToDelete = allCellsToDelete.toArray();

         for (int i = 0; i < cellsToDelete.length; i++) {
            // adding parent of removing Activity cell (if there is one and if
            // it is Participant) into array for arranging participants, and
            // creating
            // entry in parentMap -> this will be of use after basic resizing
            // and translating operations
            Object parent = ((DefaultMutableTreeNode) cellsToDelete[i]).getParent();
            if ((parent != null) && (parent instanceof GraphSwimlaneInterface)) {
               if (cellsToDelete[i] instanceof GraphActivityInterface) {
                  participantsToArrange.add(parent);
               }
            }
            parentMap.addEntry(cellsToDelete[i], null);
         }

         // resizing remained participants
         if (!isGraphRotated()) {
            resizeAllParticipantsHorizontally(propertyMap, parentMap);
            arrangeParticipantsVertically(participantsToArrange.toArray(), propertyMap, parentMap);
         } else {
            resizeAllParticipantsVertically(propertyMap, parentMap);
            arrangeParticipantsHorizontally(participantsToArrange.toArray(), propertyMap, parentMap);
         }

         graphModel().removeAndEdit(cellsToDel.toArray(), propertyMap);

      }

      try {
         graph.setPreferredSize(getGraphsPreferredSize());
      } catch (Exception ex) {
      }
   }

   /**
    * Returns horizontal offset for inserting participants.
    */
   public int getHorizontalOffset() {
      return horizontalOffset;
   }

   /**
    * Returns the point within the graph where the upper-left corner of given graph
    * activity is placed. The point origin is the upper-left corner of participant graph
    * object that holds given activity.
    */
   public Point getOffset(GraphCommonInterface a, Map propertyMap) {
      if (a != null) {
         GraphSwimlaneInterface par = (GraphSwimlaneInterface) a.getParent();
         if (par != null) {
            Rectangle2D rpar = getCBounds(par, propertyMap);// HM, JGraph3.4.1
            Rectangle2D ract = getCBounds(a, propertyMap);// HM, JGraph3.4.1
            int yOff = ract.getBounds().y - rpar.getBounds().y;// HM,
            // JGraph3.4.1
            int xOff = ract.getBounds().x - rpar.getBounds().x;// HM,
            // JGraph3.4.1
            return new Point(xOff, yOff);
         }
      }
      return new Point(0, 0);
   }

   public Point getOffset(Point actPoint) {
      if (actPoint != null) {
         GraphSwimlaneInterface par = findParentParticipantForLocation(actPoint, null, null);
         // System.err.println("Handling relative offset for point "+actPoint+", found par="+par);
         if (par != null) {
            Rectangle2D rpar = getCBounds(par, new HashMap());// HM, JGraph3.4.1
            int yOff = actPoint.y - rpar.getBounds().y;// HM, JGraph3.4.1
            int xOff = actPoint.x - rpar.getBounds().x;// HM, JGraph3.4.1
            // System.err.println("               ..........par  bounds "+rpar+", offset="+new
            // Point(xOff,yOff));
            return new Point(xOff, yOff);
         }
      }
      return new Point(0, 0);
   }

   public Point getRealPosition(GraphCommonInterface a, GraphSwimlaneInterface gpar) {
      if (a != null) {
         if (gpar != null) {
            CellView view = getView(gpar);
            Rectangle2D rpar = view.getBounds();
            Point aoffset = a.getOffset();
            int y = aoffset.y + rpar.getBounds().y;
            int x = aoffset.x + rpar.getBounds().x;
            // System.out.println("NP1="+new Point(x,y));
            //
            // if (y>rpar.getHeight()) y=(int)rpar.getHeight()-defActivityHeight-5;
            // if (x>rpar.getWidth()) x=(int)rpar.getWidth()-defActivityWidth-10;
            Point p = new Point(x, y);
            // System.out.println("NP2="+p);
            return p;
         }
      }
      return new Point(0, 0);
   }

   /**
    * Inserts new activities into model, changes positions and sizes of participants due
    * to a insertion of new activities or due to a moving of activities. Also, finds and
    * changes parent's of inserted/changed-position activities. This method is called when
    * inserting new activities, when pasting activities, and when moving activities. All
    * changes to the model and to the view are at first made virtually (to the parentMap
    * and to the propertyMap) and when all is done, method insertAndEdit of PEGraphModel
    * class is called to actually make changes. The updateCollection parameter is used
    * when some objects are inserted into model, to update collection of XML elements.
    * 
    * @param arrangeParticipants TODO
    */
   public void updateModelAndArrangeParticipants(Object[] insert,
                                                 Map propertyMap,
                                                 ParentMap parentMap,
                                                 Map viewMap,
                                                 String actionName,
                                                 ConnectionSet cs,
                                                 boolean arrangeParticipants) {

      if (propertyMap == null && viewMap == null)
         return;
      if (propertyMap == null) {
         propertyMap = new HashMap(viewMap);
      }
      if (parentMap == null) {
         parentMap = new JaWEParentMap();
      }

      if (arrangeParticipants) {
         arrangeParticipants(propertyMap, parentMap);
      } else {
         arrangeParticipantRelationsOnly(propertyMap, parentMap);
      }
      // extracting viewMap elements out of propertyMap (if any), applying
      // change to viewMap element bounds (this must be done, because user
      // could press mouse when inserting or pasting activities at the
      // forbiden position so position of activity is changed during
      // updateActivityParent method) and applying change to model and view

      // if (viewMap != null) { Iterator it = viewMap.keySet().iterator(); while
      // (it.hasNext()) { Object cell=it.next();
      // // removing entry for cell that is contained within viewMap
      // Map mapP=(Map)propertyMap.remove(cell);
      // // apply position changes to corresponding viewMap element
      // Map mapV=(Map)viewMap.get(cell);
      // if (!(cell instanceof Port)) {
      // Rectangle2D r=GraphConstants.getBounds(mapP);
      // GraphConstants.setBounds(mapV,r);
      // }
      // }
      // }

      if (insert != null && ((JaWEParentMap) parentMap).entryCount() != insert.length) {
         // return; //HM: enable Transition-copy/paste
      }

      // makes all changes (if there was any) - to model and to view
      // graphModel().insertAndEdit(insert,null,propertyMap,parentMap,viewMap,actionName);
      graphModel().insertAndEdit(insert, propertyMap, cs, parentMap, null);

      Dimension prefSize = null;
      if (!creatingGraph) {
         try {
            prefSize = getGraphsPreferredSize();
         } catch (Exception ex) {
         }
      }

      if (!creatingGraph && prefSize != null) {
         try {
            graph.setPreferredSize(prefSize);
         } catch (Exception ex) {
         }
      }
   }

   /**
    * Arranges participants according to the given property and parent maps.
    */
   public void arrangeParticipants(Map propertyMap, ParentMap parentMap) {
      Set parsToArrange = new HashSet();
      // going through given propertyMap keys, and if key is
      // activity->updating it's parent if needed.
      // WARNING: must extract keys and put it in a array because
      // propertyMap changes -> ConcurrentModificationException
      // can happend
      Object[] cellsToManage = propertyMap.keySet().toArray();
      for (int i = 0; i < cellsToManage.length; i++) {
         Object cell = cellsToManage[i];
         if (cell instanceof GraphCommonInterface) {
            Set oldAndNewParentPar = updateParent((GraphCommonInterface) cell, propertyMap, parentMap);
            parsToArrange.addAll(oldAndNewParentPar);
         }
      }

      if (isGraphRotated()) {
         arrangeParticipantsHorizontally(parsToArrange.toArray(), propertyMap, parentMap);
         resizeAllParticipantsVertically(propertyMap, parentMap);
      } else {
         // arrange participants vertically
         arrangeParticipantsVertically(parsToArrange.toArray(), propertyMap, parentMap);
         resizeAllParticipantsHorizontally(propertyMap, parentMap);
      }
   }

   protected void arrangeParticipantRelationsOnly(Map propertyMap, ParentMap parentMap) {
      Set parsToArrange = new HashSet();
      // going through given propertyMap keys, and if key is
      // activity->updating it's parent if needed.
      // WARNING: must extract keys and put it in a array because
      // propertyMap changes -> ConcurrentModificationException
      // can happend
      Object[] cellsToManage = propertyMap.keySet().toArray();
      for (int i = 0; i < cellsToManage.length; i++) {
         Object cell = cellsToManage[i];
         if (cell instanceof GraphActivityInterface || cell instanceof GraphArtifactInterface) {
            Set oldAndNewParentPar = updateParent((GraphActivityInterface) cell, propertyMap, parentMap);
            parsToArrange.addAll(oldAndNewParentPar);
         }
      }
   }

   /**
    * Determines old and new participant for activity, adjusts activities position if
    * needed and properly changes parent of activity (adds entry into the parentMap).
    */
   protected Set updateParent(GraphCommonInterface ac, Map propertyMap, ParentMap parentMap) {
      // must return old and new participant to the caller method
      Set oldAndNewPar = new HashSet();
      // old and new parent participant of given activity
      GraphSwimlaneInterface oldPar = (GraphSwimlaneInterface) ac.getParent();

      // adds oldPar to the set to be returned
      if (oldPar != null) {
         oldAndNewPar.add(oldPar);
      }

      GraphSwimlaneInterface newPar = null;
      // taking position elements of Activity
      // taking bounding rectangle
      Rectangle2D acRect = getCBounds(ac, propertyMap);// HM, JGraph3.4.1
      // taking upper-left corner, this will be reference for moving activitys
      Point acUpperLeft = acRect.getBounds().getLocation();// HM, JGraph3.4.1
      Point newAcUpperLeft = new Point(acUpperLeft);

      newPar = findParentParticipantForLocation(newAcUpperLeft, propertyMap, parentMap);

      // System.out.println("Old par for activity "+ac+" is "+oldPar+", and new par is "+newPar);
      // if previous method changed location of upper-left point,
      // move activity to new location
      if (!newAcUpperLeft.equals(acUpperLeft)) {
         Rectangle r = new Rectangle(acRect.getBounds());// HM, JGraph3.4.1
         r.setLocation(newAcUpperLeft);
         changeBounds(ac, propertyMap, r);
      }

      if (newPar != null) {
         // adds newPar to the set to be returned
         oldAndNewPar.add(newPar);

         // VERY IMPORTANT IS TO CHANGE THE PARENT
         // changing the parent participant of Activity cell if it has changed
         // position
         if (!newPar.equals(oldPar)) {
            parentMap.addEntry(ac, newPar);
         }
      }

      return oldAndNewPar;

   }

   public GraphSwimlaneInterface findParentParticipantForLocation(Point loc) {
      GraphSwimlaneInterface newPar = null;
      // if user put activity cell somewhere outside visible area, move it back
      if (loc.y <= 0) {
         loc.y = 1;
      }
      if (loc.x <= 0) {
         loc.x = 1;
      }

      // determining the container for new position
      if (isGraphRotated()) {
         newPar = getLeafParticipantForXPos(loc.x, null, null);
      } else {
         newPar = getLeafParticipantForYPos(loc.y, null, null);
      }

      return newPar;
   }

   /**
    * Finds new participant for activity after it's position changes. WARNING: this method
    * changes it's argument loc if not appropriate.
    */
   public GraphSwimlaneInterface findParentParticipantForLocation(Point loc, Map propertyMap, ParentMap parentMap) {
      GraphSwimlaneInterface newPar = null;
      // if user put activity cell somewhere outside visible area, move it back
      if (loc.y <= 0) {
         loc.y = 1;
      }
      if (loc.x <= 0) {
         loc.x = 1;
      }

      // determining the container for new position
      if (isGraphRotated()) {
         newPar = getLeafParticipantForXPos(loc.x, propertyMap, parentMap);

         // if new container isn't found -> activity is placed under all
         // participants
         // so it's new participant will be the leaf participant with the
         // highest y-coord
         if (newPar == null) {
            newPar = getLeafParticipantForXPos(getNewRootParXPos(propertyMap, null) - 10, propertyMap, parentMap);
         }

         if (newPar != null) {
            // Adjust activities x-pos if needed
            Rectangle2D newParRect = getCBounds(newPar, propertyMap);// HM,
            // JGraph3.4.1
            // if x-position is not OK, set appropriate position
            if (newParRect.getY() + defLaneNameWidth >= loc.y) {
               loc.y = (int) (newParRect.getY() + defLaneNameWidth + 1);
            }
         }
         // it is activity that belongs to an block activity
         else {
            // if x-position is not OK, set appropriate position
            if (verticalOffset >= loc.y) {
               loc.y = horizontalOffset;
            }
         }
      } else {
         newPar = getLeafParticipantForYPos(loc.y, propertyMap, parentMap);

         // if new container isn't found -> activity is placed under all
         // participants
         // so it's new participant will be the leaf participant with the
         // highest y-coord
         if (newPar == null) {
            newPar = getLeafParticipantForYPos(getNewRootParYPos(propertyMap, null) - 10, propertyMap, parentMap);
         }

         if (newPar != null) {
            // Adjust activities x-pos if needed
            Rectangle2D newParRect = getCBounds(newPar, propertyMap);// HM,
            // JGraph3.4.1
            // if x-position is not OK, set appropriate position
            if (newParRect.getX() + defLaneNameWidth >= loc.x) {
               loc.x = (int) newParRect.getX() + defLaneNameWidth + 1;
            }
         }
         // it is activity that belongs to an block activity
         else {
            // if x-position is not OK, set appropriate position
            if (horizontalOffset >= loc.x) {
               loc.x = horizontalOffset;
            }
         }
      }

      return newPar;
   }

   protected void arrangeParticipantsHorizontally(Object[] pars, Map propertyMap, ParentMap parentMap) {
      if ((pars == null) || (pars.length == 0))
         return;

      for (int i = 0; i < pars.length; i++) {
         arrangeParticipantHorizontally(pars[i], propertyMap, parentMap);
      }
   }

   /**
    * Arranging heights and positions of given participants and positions of participants
    * that must be translated due to a change of given participants.
    */
   protected void arrangeParticipantsVertically(Object[] pars, Map propertyMap, ParentMap parentMap) {
      if ((pars == null) || (pars.length == 0))
         return;

      for (int i = 0; i < pars.length; i++) {
         // arrange participants vertically
         arrangeParticipantVertically(pars[i], propertyMap, parentMap);
      }
   }

   protected void arrangeParticipantHorizontally(Object par, Map propertyMap, ParentMap parentMap) {
      // can't be null, must be instance of Participant,
      // and can't have other participants
      // also it can't be removed
      if (par == null)
         return;
      if (!(par instanceof GraphSwimlaneInterface))
         return;
      GraphSwimlaneInterface p = (GraphSwimlaneInterface) par;
      if (hasAnyParticipant(p, parentMap))
         return;
      ArrayList removedPars = ((JaWEParentMap) parentMap).getRemovedNodes();
      if (removedPars.contains(p))
         return;
      // getting optimal and current height for participant
      int optWidth = optimalParticipantWidth(p, propertyMap, parentMap);
      int curWidth = getParticipantWidth(p, propertyMap);
      // calculating value for vertical resizing of new participant
      int dwidth = optWidth - curWidth;

      if (dwidth != 0) {
         // translating horizontally participants under right edge
         // of participant for value of dwidth
         translateHorizontally(propertyMap, parentMap, getCBounds(p, propertyMap).getBounds().x + curWidth - 1, dwidth);// HM,
         // JGraph3.4.1

         // gets all parents participant (and given participant) into array
         // and resizes them for value of dheight
         Object[] allParentsAndPar = p.getPath();
         resize(allParentsAndPar, propertyMap, dwidth, 0);
      }
   }

   /**
    * Resizing participant par and it's parents to appropriate height, and translating
    * other participants accordingly to changes of participant par. The size's and
    * positions are calculated considering propertyMap and parentMap - which means for
    * future children state, and for bounds that are constantly changed during other
    * calculations. If propertyMap and parentMap are null, size's and positions are
    * calculated for current state. Also, if par that is to be arranged has entry in
    * parentMap as removed participant, it will not be arranged.
    */
   protected void arrangeParticipantVertically(Object par, Map propertyMap, ParentMap parentMap) {
      // can't be null, must be instance of Participant,
      // and can't have other participants
      // also it can't be removed
      if (par == null)
         return;
      if (!(par instanceof GraphSwimlaneInterface))
         return;
      GraphSwimlaneInterface p = (GraphSwimlaneInterface) par;
      if (hasAnyParticipant(p, parentMap))
         return;
      ArrayList removedPars = ((JaWEParentMap) parentMap).getRemovedNodes();
      if (removedPars.contains(p))
         return;
      // getting optimal and current height for participant
      int optHeight = optimalParticipantHeight(p, propertyMap, parentMap);
      int curHeight = getParticipantHeight(p, propertyMap);
      // calculating value for vertical resizing of new participant
      int dheight = optHeight - curHeight;

      if (dheight != 0) {
         // translating verticaly participants under bottom edge
         // of participant for value of dheight
         translateVertically(propertyMap, parentMap, getCBounds(p, propertyMap).getBounds().y + curHeight - 1, dheight);// HM,
         // JGraph3.4.1

         // gets all parents participant (and given participant) into array
         // and resizes them for value of dheight
         Object[] allParentsAndPar = p.getPath();
         resize(allParentsAndPar, propertyMap, 0, dheight);
      }

   }

   /**
    * Method that resizes all participants horizontally to get there minimal needed sizes.
    * The size is calculated considering propertyMap and parentMap - which means for
    * future children state, and for bounds that are constantly changed during other
    * calculations. If propertyMap and parentMap are null, size is calculated for current
    * state.
    */
   protected void resizeAllParticipantsHorizontally(Map propertyMap, ParentMap parentMap) {
      List participants = JaWEGraphModel.getAllParticipantsInModel(graphModel());
      // removing ones which parent in a parentMap is null
      if (parentMap != null && participants != null) {
         participants.removeAll(((JaWEParentMap) parentMap).getRemovedNodes());
      }
      // if there is a need for resizing
      int optimalRDW = optimalRootParticipantWidth(participants, propertyMap, parentMap);
      int rootParWidth = getRootParticipantWidth(propertyMap, parentMap);
      if (optimalRDW != rootParWidth) {
         // resize all participants for needed increment
         int dw = optimalRDW - rootParWidth;
         if (participants != null) {
            resize(participants.toArray(), propertyMap, dw, 0);
         }
      }
   }

   protected void resizeAllParticipantsVertically(Map propertyMap, ParentMap parentMap) {
      List participants = JaWEGraphModel.getAllParticipantsInModel(graphModel());
      // removing ones which parent in a parentMap is null
      if (parentMap != null && participants != null) {
         participants.removeAll(((JaWEParentMap) parentMap).getRemovedNodes());
      }
      // if there is a need for resizing
      int optimalBDW = optimalRootParticipantHeight(participants, propertyMap, parentMap);
      int rootParHeight = getRootParticipantHeight(propertyMap, parentMap);
      if (optimalBDW != rootParHeight) {
         // resize all participants for needed increment
         int dw = optimalBDW - rootParHeight;
         if (participants != null) {
            resize(participants.toArray(), propertyMap, 0, dw);
         }
      }
   }

   protected int optimalRootParticipantHeight(List participants, Map propertyMap, ParentMap parentMap) {
      // initial value for width stays the same
      int minHeight;
      if (isGraphRotated())
         minHeight = minLaneWidth;
      else
         minHeight = minLaneHeight;

      // exits if there are no participants created
      if (participants == null)
         return minHeight;

      // finds the leaf participants
      Set leafParticipants = new HashSet();

      Iterator it = participants.iterator();
      while (it.hasNext()) {
         GraphSwimlaneInterface par = (GraphSwimlaneInterface) it.next();
         // if participant doesn't have any other participants,
         // it is a leaf participant and should be added to collection
         if (!hasAnyParticipant(par, parentMap)) {
            leafParticipants.add(par);
         }
      }

      // max. right edge position minus horizontalOffset (of all leafs) becomes
      // minWidth
      it = leafParticipants.iterator();
      int maxBottomEdgePosition = 0;

      while (it.hasNext()) {
         GraphSwimlaneInterface lpar = (GraphSwimlaneInterface) it.next();
         int minBEdge;
         int minParBEdge;
         int minChildrenBEdge = 0;
         // getting future participant bounds
         if (isGraphRotated())
            minParBEdge = (int) getCBounds(lpar, propertyMap).getY() + minLaneWidth;// HM,
         else
            minParBEdge = (int) getCBounds(lpar, propertyMap).getY() + minLaneHeight;// HM,
         // JGraph3.4.1
         // getting the future child views bounding rectangle -> its right
         // edge plus some extra space is min. right edge for that participant
         Rectangle r = getBoundsOfParticipantFutureActivitiesAndArtifacts(lpar, propertyMap, parentMap);
         if (r != null) {
            minChildrenBEdge = r.y + r.height + defLaneNameWidth;
         }

         minBEdge = java.lang.Math.max(minParBEdge, minChildrenBEdge);
         // if entering first time, set the starting max.
         if (maxBottomEdgePosition == 0) {
            maxBottomEdgePosition = minBEdge;
         } else if (minBEdge > maxBottomEdgePosition) {
            maxBottomEdgePosition = minBEdge;
         }
      }

      int minH = maxBottomEdgePosition - verticalOffset;

      // can't allow that the minWidth<minLaneWidth
      if (minH > minLaneWidth) {
         minHeight = minH;
      }

      return minHeight;

   }

   /**
    * Calculates the minimal width of root participants. It depends of minimal allowed
    * width of leaf participants, which depends on the position of Activity cells in them.
    * The width is calculated considering propertyMap and parentMap - which means for
    * future children state, and for bounds that are constantly changed during other
    * calculations. If propertyMap and parentMap are null, size is calculated for current
    * state.
    */
   protected int optimalRootParticipantWidth(List participants, Map propertyMap, ParentMap parentMap) {
      // initial value for width stays the same
      int minWidth;
      if (isGraphRotated())
         minWidth = minLaneHeight;
      else
         minWidth = minLaneWidth;

      // exits if there are no participants created
      if (participants == null)
         return minWidth;

      // finds the leaf participants
      Set leafParticipants = new HashSet();

      Iterator it = participants.iterator();
      while (it.hasNext()) {
         GraphSwimlaneInterface par = (GraphSwimlaneInterface) it.next();
         // if participant doesn't have any other participants,
         // it is a leaf participant and should be added to collection
         if (!hasAnyParticipant(par, parentMap)) {
            leafParticipants.add(par);
         }
      }

      // max. right edge position minus horizontalOffset (of all leafs) becomes
      // minWidth
      it = leafParticipants.iterator();
      int maxRightEdgePosition = 0;

      while (it.hasNext()) {
         GraphSwimlaneInterface lpar = (GraphSwimlaneInterface) it.next();
         int minREdge;
         int minParREdge;
         int minChildrenREdge = 0;
         // getting future participant bounds
         if (isGraphRotated())
            minParREdge = (int) getCBounds(lpar, propertyMap).getX() + minLaneHeight;// HM,
         // JGraph3.4.1
         else
            minParREdge = (int) getCBounds(lpar, propertyMap).getX() + minLaneWidth;// HM,
         // JGraph3.4.1
         // getting the future child views bounding rectangle -> its right
         // edge plus some extra space is min. right edge for that participant
         Rectangle r = getBoundsOfParticipantFutureActivitiesAndArtifacts(lpar, propertyMap, parentMap);
         if (r != null) {
            minChildrenREdge = r.x + r.width + defLaneNameWidth;
         }

         minREdge = java.lang.Math.max(minParREdge, minChildrenREdge);
         // if entering first time, set the starting max.
         if (maxRightEdgePosition == 0) {
            maxRightEdgePosition = minREdge;
         } else if (minREdge > maxRightEdgePosition) {
            maxRightEdgePosition = minREdge;
         }
      }

      int minW = maxRightEdgePosition - horizontalOffset;

      // can't allow that the minWidth<minLaneWidth
      if (minW > minLaneWidth) {
         minWidth = minW;
      }

      return minWidth;

   }

   protected int optimalParticipantWidth(GraphSwimlaneInterface par, Map propertyMap, ParentMap parentMap) {
      // initial value for width
      int optWidth;
      if (isGraphRotated())
         optWidth = minLaneHeight;
      else
         optWidth = minLaneWidth;

      // exits returning minLaneHeight if there are no activity cells within
      // participant
      if (!hasAnyActivityOrArtifact(par, parentMap))
         return optWidth;

      // get bounds of par (either current or future)
      Rectangle2D rCurrent = getCBounds(par, propertyMap);// HM,
      // JGraph3.4.1
      // get preffered bounding rectangle of participant (according to it's
      // children)
      Rectangle rPreferred = getBoundsOfParticipantFutureActivitiesAndArtifacts(par, propertyMap, parentMap);

      // difference of these rectangle's bottom positions plus current par
      // height
      // plus some extra space is min. height for given participant

      // calculate difference in bottom edges of these rectangles, and optimal
      // height
      if (rPreferred != null) {
         int dRight = (rPreferred.x + rPreferred.width) - (int) (rCurrent.getX() + rCurrent.getWidth());
         int optW = (int) rCurrent.getWidth() + dRight + 10;

         // optimal height can't be less then minHeight
         if (optW > optWidth) {
            optWidth = optW;
         }
      } else {
      }

      return optWidth;
   }

   /**
    * Calculates minimal participant height, which depends of position of all of its
    * Activity cells. The height is calculated considering propertyMap and parentMap -
    * which means for future children state, and for bounds that are constantly changed
    * during other calculations. If propertyMap and parentMap are null, size is calculated
    * for current state.
    */
   protected int optimalParticipantHeight(GraphSwimlaneInterface par, Map propertyMap, ParentMap parentMap) {
      // initial value for height
      int optHeight;
      if (isGraphRotated())
         optHeight = minLaneWidth;
      else
         optHeight = minLaneHeight;

      // exits returning minLaneHeight if there are no activity cells within
      // participant
      if (!hasAnyActivityOrArtifact(par, parentMap))
         return optHeight;

      // get bounds of par (either current or future)
      Rectangle2D rCurrent = getCBounds(par, propertyMap);// HM, JGraph3.4.1
      // get preffered bounding rectangle of participant (according to it's
      // children)
      Rectangle rPreferred = getBoundsOfParticipantFutureActivitiesAndArtifacts(par, propertyMap, parentMap);

      // difference of these rectangle's bottom positions plus current par
      // height
      // plus some extra space is min. height for given participant

      // calculate difference in bottom edges of these rectangles, and optimal
      // height
      if (rPreferred != null) {
         int dBottom = (rPreferred.y + rPreferred.height) - (int) (rCurrent.getY() + rCurrent.getHeight());
         int optH = (int) rCurrent.getHeight() + dBottom + 10;

         // optimal height can't be less then minHeight
         if (optH > optHeight) {
            optHeight = optH;
         }
      } else {
         // System.err.println("OHINNNNNNNNNNNNNNNNNNNUUUUUUUUUUUUULLLLLLLLLL");
      }

      return optHeight;

   }

   /**
    * Resizes given participants. The resizing is done to propertyMap which will later
    * (after all needed operation) be applied.
    */
   protected void resize(Object[] cells, Map propertyMap, int dw, int dh) {
      if (cells != null && cells.length > 0) {
         Rectangle r;
         for (int i = 0; i < cells.length; i++) {
            r = new Rectangle(getCBounds(cells[i], propertyMap).getBounds());// HM,
            // JGraph3.4.1

            int newWidth = r.width + dw;
            int newHeight = r.height + dh;

            if (isGraphRotated()) {
               if (newWidth < minLaneHeight || newHeight < minLaneWidth) {
                  System.err.println("There was an error in calculating size of participant " + cells[i] + "!!!");
                  System.err.println("New width=" + newWidth + ", new height=" + newHeight);
               }
            } else {
               if (newWidth < minLaneWidth || newHeight < minLaneHeight) {
                  System.err.println("There was an error in calculating size of participant " + cells[i] + "!!!");
                  System.err.println("New width=" + newWidth + ", new height=" + newHeight);
               }
            }

            r.setSize(newWidth, newHeight);
            changeBounds(cells[i], propertyMap, r);
         }
      }
   }

   protected void translateHorizontally(Map propertyMap, ParentMap parentMap, int xPos, int dv) {
      GraphSwimlaneInterface[] pars = getParticipantsForXPos(xPos, 0, propertyMap, parentMap, true);
      translateParticipants(pars, propertyMap, parentMap, dv, 0);
   }

   /**
    * Translates participants under given position yPos vertically for a value of dv. The
    * translating is done to propertyMap which will later (after all needed operation) be
    * applied.
    * 
    * @see #translateParticipants
    */
   protected void translateVertically(Map propertyMap, ParentMap parentMap, int yPos, int dv) {
      GraphSwimlaneInterface[] pars = getParticipantsForYPos(yPos, 0, propertyMap, parentMap, true);
      translateParticipants(pars, propertyMap, parentMap, 0, dv);
   }

   /**
    * Translates given participants using propertyMap for bounds checking and parentMap
    * for future children checking. The translating is done to propertyMap which will
    * later (after all needed operation) be applied.
    * 
    * @see #translateParticipant
    */
   protected void translateParticipants(GraphSwimlaneInterface[] cells, Map propertyMap, ParentMap parentMap, int dx, int dy) {
      if (cells != null && cells.length > 0) {
         for (int i = 0; i < cells.length; i++) {
            translateParticipant(cells[i], propertyMap, parentMap, dx, dy, false);
         }
      }
   }

   /**
    * Translates single participant and its children.The method checks for bounds of cells
    * within propertyMap and uses parentMap to translate right children (the children that
    * will be it's after applying parentMap). The translating is done to propertyMap which
    * will later (after all needed operation) be applied.
    */
   protected void translateParticipant(GraphSwimlaneInterface par, Map propertyMap, ParentMap parentMap, int dx, int dy, boolean handleChildren) {
      Set participantAndItsFutureActivities = new HashSet();
      participantAndItsFutureActivities.add(par);

      // Get future activities of participant
      Set futureActivities = getParticipantFutureActivitiesAndArtifacts(par, parentMap);

      participantAndItsFutureActivities.addAll(futureActivities);

      // applying translations to the determined cells
      Rectangle r;
      Iterator it = participantAndItsFutureActivities.iterator();
      while (it.hasNext()) {
         Object cell = it.next();
         r = new Rectangle(getCBounds(cell, propertyMap).getBounds());// HM,
         // JGraph3.4.1
         r.translate(dx, dy);
         changeBounds(cell, propertyMap, r);
      }
      if (handleChildren) {
         it = getAllChildParticipants(par).iterator();
         while (it.hasNext()) {
            translateParticipant((GraphSwimlaneInterface) it.next(), propertyMap, parentMap, dx, dy, true);
         }
      }
   }

   /**
    * Gets the bounding rectangle of future children activities of given Participant par
    * (that will be participants activities after parent map is applied). Bounding
    * rectangle is union of previous mentioned activities.
    */
   protected Rectangle getBoundsOfParticipantFutureActivitiesAndArtifacts(GraphSwimlaneInterface par, Map propertyMap, ParentMap parentMap) {
      // simulate future state of children and get it's bounds
      Set futureActivities = getParticipantFutureActivitiesAndArtifacts(par, parentMap);
      Set futureActivityBounds = new HashSet();

      Iterator it = futureActivities.iterator();
      while (it.hasNext()) {
         Rectangle actBnd = getCBounds(it.next(), propertyMap).getBounds();// HM,
         // JGraph3.4.1
         futureActivityBounds.add(actBnd);
      }

      Rectangle[] fab = new Rectangle[futureActivityBounds.size()];
      futureActivityBounds.toArray(fab);
      Rectangle unionBounds = getUnionBounds(fab);

      return unionBounds;
   }

   /**
    * Gets future children activities of given participant par (that will be par's
    * activities after parent map is applied).
    */
   protected Set getParticipantFutureActivitiesAndArtifacts(GraphSwimlaneInterface par, ParentMap parentMap) {
      // if there is no parent map, or there is no entry for participant
      // in it, return current participants activities
      if (parentMap == null) {// || !parentMap.getChangedNodes().contains(par))
         // {
         return par.getChildActivitiesAndArtifacts();
      }
      Set futureActivities = new HashSet();
      // getting participants which will be empty after applying parentMap
      ArrayList emptyPars = ((JaWEParentMap) parentMap).emptyParentList();
      // returns empty set if there will be no activity cells within participant
      if (emptyPars.contains(par))
         return futureActivities;
      // get changed nodes from parent map (nodes which parent changed and
      // nodes which children changed)
      Set changedNodes = parentMap.getChangedNodes();
      // get all (previous) participants activities and make the future look
      // of participant activities
      futureActivities = new HashSet(par.getChildActivitiesAndArtifacts());
      Object[] previousActivities = futureActivities.toArray();
      // iterate through child activities and remove ones which are
      // contained in changed nodes of parent map - they will no longer
      // be the children of current participant
      for (int i = 0; i < previousActivities.length; i++) {
         if (changedNodes.contains(previousActivities[i])) {
            futureActivities.remove(previousActivities[i]);
         }
      }
      // get new children of current participant from parent map
      // and add it to futureActivities set
      ArrayList nc = ((JaWEParentMap) parentMap).getNewChildren(par);
      futureActivities.addAll(nc);

      return futureActivities;
   }

   protected GraphSwimlaneInterface getLeafParticipantForXPos(int xPos, Map propertyMap, ParentMap parentMap) {
      // getting all participants that contains yPos
      GraphSwimlaneInterface[] pars = getParticipantsForXPos(xPos, 2, propertyMap, parentMap, true);

      // if there is no participants at this location, return null
      if (pars == null)
         return null;

      // getting leaf participant(s)
      Set leafPars = new HashSet();
      for (int i = 0; i < pars.length; i++) {
         if (!hasAnyParticipant(pars[i], parentMap)) {
            leafPars.add(pars[i]);
         }
      }

      // if there is no leaf participant at this location (THIS SHOULD
      // NEVER HAPPEND, BUT ...) return null
      if (leafPars.size() == 0)
         return null;

      // taking first and making it the right one
      Iterator it = leafPars.iterator();
      Object rightPar = it.next();

      if (leafPars.size() > 1) {
         int upperLeftX;
         int minUpperLeftX;
         Rectangle parRect;

         // taking bounding rectangle of first par
         parRect = getCBounds(rightPar, propertyMap).getBounds();// HM,
         // JGraph3.4.1
         // taking upper-left corner y-coord
         upperLeftX = parRect.getLocation().x;
         minUpperLeftX = upperLeftX;

         // finding the participant with min. y-coord
         while (it.hasNext()) {
            Object curPar = it.next();
            parRect = getCBounds(curPar, propertyMap).getBounds();// HM,
            // JGraph3.4.1
            upperLeftX = parRect.getLocation().x;
            if (upperLeftX < minUpperLeftX) {
               minUpperLeftX = upperLeftX;
               rightPar = curPar;
            }
         }
      }

      // return found participant
      return (GraphSwimlaneInterface) rightPar;
   }

   /**
    * Returns leaf participant that bounds given y-coordinate. If y-coordinate is at
    * boundary of two participants, method returns one that is above. The method checks
    * for bounds of cells within propertyMap.
    */
   protected GraphSwimlaneInterface getLeafParticipantForYPos(int yPos, Map propertyMap, ParentMap parentMap) {
      // getting all participants that contains yPos
      GraphSwimlaneInterface[] pars = getParticipantsForYPos(yPos, 2, propertyMap, parentMap, true);

      // if there is no participants at this location, return null
      if (pars == null)
         return null;

      // getting leaf participant(s)
      Set leafPars = new HashSet();
      for (int i = 0; i < pars.length; i++) {
         if (!hasAnyParticipant(pars[i], parentMap)) {
            leafPars.add(pars[i]);
         }
      }

      // if there is no leaf participant at this location (THIS SHOULD
      // NEVER HAPPEND, BUT ...) return null
      if (leafPars.size() == 0)
         return null;

      // taking first and making it the right one
      Iterator it = leafPars.iterator();
      Object rightPar = it.next();

      // if there is more than one leaf participant, take the one that
      // has minimal y-coord of upper left corner
      if (leafPars.size() > 1) {
         int upperLeftY;
         int minUpperLeftY;
         Rectangle parRect;

         // taking bounding rectangle of first par
         parRect = getCBounds(rightPar, propertyMap).getBounds();// HM,
         // JGraph3.4.1
         // taking upper-left corner y-coord
         upperLeftY = parRect.getLocation().y;
         minUpperLeftY = upperLeftY;

         // finding the participant with min. y-coord
         while (it.hasNext()) {
            Object curPar = it.next();
            parRect = getCBounds(curPar, propertyMap).getBounds();// HM,
            // JGraph3.4.1
            upperLeftY = parRect.getLocation().y;
            if (upperLeftY < minUpperLeftY) {
               minUpperLeftY = upperLeftY;
               rightPar = curPar;
            }
         }
      }

      // return found participant
      return (GraphSwimlaneInterface) rightPar;

   }

   protected GraphSwimlaneInterface getLeafestParticipantForPos(Point pos, Map propertyMap, ParentMap parentMap) {
      GraphSwimlaneInterface[] pars = null;
      if (isGraphRotated()) {
         pars = getParticipantsForXPos(pos.x, 2, propertyMap, parentMap, true);

      } else {
         pars = getParticipantsForYPos(pos.y, 2, propertyMap, parentMap, true);
      }

      // if there is no participants at this location, return null
      if (pars == null)
         return null;

      // getting leaf participant(s)
      Set leafPars = new HashSet();
      Set nonLeafPars = new HashSet();
      for (int i = 0; i < pars.length; i++) {
         if (!hasAnyParticipant(pars[i], parentMap)) {
            leafPars.add(pars[i]);
         } else {
            nonLeafPars.add(pars[i]);
         }
      }

      // if there is no leaf participant at this location (THIS SHOULD
      // NEVER HAPPEND, BUT ...) return null
      if (leafPars.size() == 0)
         return null;

      // taking first and making it the right one
      Iterator it = leafPars.iterator();
      Object rightPar = it.next();

      if (leafPars.size() > 1) {
         int upperLeftX;
         int minUpperLeftX;
         Rectangle parRect;

         // taking bounding rectangle of first par
         parRect = getCBounds(rightPar, propertyMap).getBounds();// HM,
         // JGraph3.4.1
         // taking upper-left corner y-coord
         upperLeftX = parRect.getLocation().x;
         minUpperLeftX = upperLeftX;

         // finding the participant with min. y-coord
         while (it.hasNext()) {
            Object curPar = it.next();
            parRect = getCBounds(curPar, propertyMap).getBounds();// HM,
            // JGraph3.4.1
            upperLeftX = parRect.getLocation().x;
            if (upperLeftX < minUpperLeftX) {
               minUpperLeftX = upperLeftX;
               rightPar = curPar;
            }
         }
      }

      // return found participant
      return (GraphSwimlaneInterface) rightPar;
   }

   protected GraphSwimlaneInterface[] getParticipantsForXPos(int xPos, int direction, Map propertyMap, ParentMap parentMap, boolean returnRootPart) {
      // getting all participants that are in collection
      List participants = JaWEGraphModel.getAllParticipantsInModel(graphModel());

      // if there are no participants, return null
      if (participants == null)
         return null;

      // removing ones which parent in a parentMap is null
      if (parentMap != null) {
         participants.removeAll(((JaWEParentMap) parentMap).getRemovedNodes());
      }

      // making an array of participants
      GraphSwimlaneInterface[] pars = new GraphSwimlaneInterface[participants.size()];
      participants.toArray(pars);

      // Set of participants that will satisfy needs
      Set xPosPars = new HashSet();

      for (int i = 0; i < pars.length; i++) {
         if (pars[i].getPropertyObject() instanceof Pool && !returnRootPart)
            continue;
         Rectangle2D r = getCBounds(pars[i], propertyMap);// HM,
         // JGraph3.4.1
         switch (direction) {
            case 0:
               if (r.getX() >= xPos) {
                  xPosPars.add(pars[i]);
               }
               break;
            case 1:
               if (r.getX() < xPos) {
                  xPosPars.add(pars[i]);
               }
               break;
            case 2:
               if ((r.getX() <= xPos) && (r.getX() + r.getWidth() >= xPos)) {
                  xPosPars.add(pars[i]);
               }
               break;
         }
      }

      if (xPosPars.size() > 0) {
         pars = new GraphSwimlaneInterface[xPosPars.size()];
         xPosPars.toArray(pars);
         return pars;
      }
      return null;
   }

   /**
    * Returns participants that are under or above given yPos, or contains that
    * y-position: <BR>
    * direction=0 -> under, <BR>
    * direction=1 -> above, <BR>
    * direction=2 -> contains. The method checks for bounds of cells within propertyMap.
    * If some of model's participants is entered in parentMap as removed participant, this
    * method doesn't consider that participant.
    */
   protected GraphSwimlaneInterface[] getParticipantsForYPos(int yPos, int direction, Map propertyMap, ParentMap parentMap, boolean returnRootPart) {
      // getting all participants that are in collection
      List participants = JaWEGraphModel.getAllParticipantsInModel(graphModel());

      // if there are no participants, return null
      if (participants == null)
         return null;

      // removing ones which parent in a parentMap is null
      if (parentMap != null) {
         participants.removeAll(((JaWEParentMap) parentMap).getRemovedNodes());
      }

      // making an array of participants
      GraphSwimlaneInterface[] pars = new GraphSwimlaneInterface[participants.size()];
      participants.toArray(pars);

      // Set of participants that will satisfy needs
      Set yPosPars = new HashSet();

      for (int i = 0; i < pars.length; i++) {
         if (pars[i].getPropertyObject() instanceof Pool && !returnRootPart)
            continue;
         Rectangle2D r = getCBounds(pars[i], propertyMap);// HM, JGraph3.4.1
         switch (direction) {
            case 0:
               if (r.getY() >= yPos) {
                  yPosPars.add(pars[i]);
               }
               break;
            case 1:
               if (r.getY() < yPos) {
                  yPosPars.add(pars[i]);
               }
               break;
            case 2:
               if ((r.getY() <= yPos) && (r.getY() + r.getHeight() >= yPos)) {
                  yPosPars.add(pars[i]);
               }
               break;
         }
      }

      if (yPosPars.size() > 0) {
         pars = new GraphSwimlaneInterface[yPosPars.size()];
         yPosPars.toArray(pars);
         return pars;
      }
      return null;
   }

   protected int getNewRootParXPos(Map propertyMap, ParentMap parentMap) {
      int newRootParXPos = 0;

      Set rootPars = JaWEGraphModel.getRootParticipants(graphModel());

      if (propertyMap != null) {
         Iterator it = propertyMap.keySet().iterator();
         while (it.hasNext()) {
            Object rootPar = it.next();
            if ((rootPar instanceof GraphSwimlaneInterface) && (((DefaultGraphCell) rootPar).getParent() == null)) {
               rootPars.add(rootPar);
            }
         }
      }

      if (parentMap != null) {
         rootPars.removeAll(((JaWEParentMap) parentMap).getRemovedNodes());
      }

      // if there is no root participants, new Y-position is 0
      if (rootPars == null || rootPars.size() == 0)
         return newRootParXPos;

      newRootParXPos = minLaneHeight;
      // find the bottom of bottom most root participant
      Iterator it = rootPars.iterator();
      while (it.hasNext()) {
         Rectangle2D bounds = getCBounds(it.next(), propertyMap);// HM,
         // JGraph3.4.1
         if (bounds.getX() + bounds.getWidth() > newRootParXPos) {
            newRootParXPos = (int) (bounds.getX() + bounds.getWidth());
         }
      }

      return newRootParXPos;
   }

   /**
    * Gets the insertation point (y-coordinate) of new root participant.
    */
   protected int getNewRootParYPos(Map propertyMap, ParentMap parentMap) {
      int newRootParYPos = 0;

      Set rootPars = JaWEGraphModel.getRootParticipants(graphModel());

      // adding to rootPars set a root participants from propertyMap -> that is
      // done because of proper calculation of y-position of in/out ports when
      // new root participant is added to graph

      if (propertyMap != null) {
         Iterator it = propertyMap.keySet().iterator();
         while (it.hasNext()) {
            Object rootPar = it.next();
            if ((rootPar instanceof GraphSwimlaneInterface) && (((DefaultGraphCell) rootPar).getParent() == null)) {
               rootPars.add(rootPar);
            }
         }
      }

      // removing from rootPars set a root participants from parentMap -> that
      // is
      // done because of proper calculation of y-position of in/out ports when
      // root participant is removed from graph

      if (parentMap != null) {
         rootPars.removeAll(((JaWEParentMap) parentMap).getRemovedNodes());
      }

      // if there is no root participants, new Y-position is 0
      if (rootPars == null || rootPars.size() == 0)
         return newRootParYPos;

      newRootParYPos = minLaneHeight;
      // find the bottom of bottom most root participant
      Iterator it = rootPars.iterator();
      while (it.hasNext()) {
         Rectangle bounds = getCBounds(it.next(), propertyMap).getBounds();// HM,
         // JGraph3.4.1
         if (bounds.getY() + bounds.getHeight() > newRootParYPos) {
            newRootParYPos = (int) (bounds.getY() + bounds.getHeight());
         }
      }

      return newRootParYPos;
   }

   protected int getRootParticipantHeight(Map propertyMap, ParentMap parentMap) {
      int rootParHeight;
      if (isGraphRotated())
         rootParHeight = minLaneWidth;
      else
         rootParHeight = minLaneHeight;

      Set rootPars = JaWEGraphModel.getRootParticipants(graphModel());

      // if there is no root participants, width is equal to minLaneWidth
      if (rootPars == null)
         return rootParHeight;

      // removing ones which parent in a parentMap is null (this means they
      // will be removed from a graph)
      if (parentMap != null) {
         rootPars.removeAll(((JaWEParentMap) parentMap).getRemovedNodes());
      }

      // if there is no root participants, width is equal to minLaneWidth
      if (rootPars.size() == 0)
         return rootParHeight;

      // all root participants has same width, so take the first
      Iterator it = rootPars.iterator();
      Object firstPar = it.next();
      rootParHeight = getParticipantHeight(firstPar, propertyMap);

      return rootParHeight;
   }

   /**
    * Gets the width of root participants. The method checks for bounds of cells within
    * propertyMap. If some of model's participants has entry within propertyMap as
    * removed, this participant doesn't count.
    */
   protected int getRootParticipantWidth(Map propertyMap, ParentMap parentMap) {
      int rootParWidth;
      if (isGraphRotated())
         rootParWidth = minLaneHeight;
      else
         rootParWidth = minLaneWidth;
      Set rootPars = JaWEGraphModel.getRootParticipants(graphModel());

      // if there is no root participants, width is equal to minLaneWidth
      if (rootPars == null)
         return rootParWidth;

      // removing ones which parent in a parentMap is null (this means they
      // will be removed from a graph)
      if (parentMap != null) {
         rootPars.removeAll(((JaWEParentMap) parentMap).getRemovedNodes());
      }

      // if there is no root participants, width is equal to minLaneWidth
      if (rootPars.size() == 0)
         return rootParWidth;

      // all root participants has same width, so take the first
      Iterator it = rootPars.iterator();
      Object firstPar = it.next();
      rootParWidth = getParticipantWidth(firstPar, propertyMap);

      return rootParWidth;
   }

   /**
    * Checks if given participant par has other participants. If parentMap is null, or
    * there is no entry for this this participant within a parentMap, the current state is
    * checked, elsewhere parentMap is checked-in other words future state of participant
    * participants (state after aplying parentMap) is returned.
    * <p>
    * This method has meaning in previous versions of JaWE, now it always returns
    * <tt>false</tt>.
    */
   protected boolean hasAnyParticipant(GraphSwimlaneInterface par, ParentMap parentMap) {
      // if there is no parent map, or there is no entry for participant
      // in it, return original participant state
      if (parentMap == null || !parentMap.getChangedNodes().contains(par)) {
         return par.hasAnySwimlane();
      }
      // else, check if participant will be empty after applying parent map
      return ((JaWEParentMap) parentMap).hasAnyParticipant(par);
   }

   /**
    * Checks if given participant par has activities. If parentMap is null, or there is no
    * entry for this this participant within a parentMap, the current state is checked,
    * elsewhere parentMap is checked-in other words future state of participant activities
    * (state after aplying parentMap) is returned.
    */
   protected boolean hasAnyActivityOrArtifact(GraphSwimlaneInterface par, ParentMap parentMap) {
      // if there is no parent map, or there is no entry for participant
      // in it, return original participant state
      if (parentMap == null || !parentMap.getChangedNodes().contains(par)) {
         return par.hasAnyActivityOrArtifact();
      }

      // else, check if participant will be empty after applying parent map

      // getting participants which will be empty after applying parentMap
      ArrayList emptyPars = ((JaWEParentMap) parentMap).emptyParentList();
      // returns empty set if there will be no activity cells within
      // participant
      if (emptyPars.contains(par)) {
         return false;
      }
      return true;
   }

   /**
    * Returns start events.
    */
   protected Set getStartEvents() {
      Set starts = new HashSet();
      List allActivities = JaWEGraphModel.getAllActivitiesInModel(graphModel());

      if (allActivities != null) {
         Iterator it = allActivities.iterator();
         while (it.hasNext()) {
            Object act = it.next();
            int actType = ((Activity) ((GraphActivityInterface) act).getPropertyObject()).getActivityType();
            if (actType == XPDLConstants.ACTIVITY_TYPE_EVENT_START) {
               starts.add(act);
            }
         }
      }
      return starts;
   }

   /**
    * Returns end events.
    */
   protected Set getEndEvents() {
      Set ends = new HashSet();
      List allActivities = JaWEGraphModel.getAllActivitiesInModel(graphModel());

      if (allActivities != null) {
         Iterator it = allActivities.iterator();
         while (it.hasNext()) {
            Object act = it.next();
            int actType = ((Activity) ((GraphActivityInterface) act).getPropertyObject()).getActivityType();
            if (actType == XPDLConstants.ACTIVITY_TYPE_EVENT_END) {
               ends.add(act);
            }
         }
      }
      return ends;
   }

   /**
    * Gets view of given object.
    */
   public CellView getView(Object cell) {
      // return graph.getView().getMapping(cell,false);
      return graph.getGraphLayoutCache().getMapping(cell, false);
   }

   /**
    * Gets width of given participant (from it's current view or from propertyMap).
    */
   protected int getParticipantWidth(Object par, Map propertyMap) {
      return getCBounds(par, propertyMap).getBounds().width;// HM, JGraph3.4.1
   }

   /**
    * Gets height of given participant (from it's current view or from propertyMap).
    */
   protected int getParticipantHeight(Object par, Map propertyMap) {
      return getCBounds(par, propertyMap).getBounds().height;// HM, JGraph3.4.1
   }

   /**
    * Replaces bounding rectangle of given cell in propertyMap object with rectangle r if
    * cell is contained within the propertyMap object, otherwise adds new entry to the
    * propertyMap that consists of given cell and a map containing given rectangle r.
    */
   protected void changeBounds(Object cell, Map propertyMap, Rectangle r) {
      AttributeMap map;
      // System.out.println("Changing cell " + cell + " bounds to " + r);
      if (propertyMap == null || !propertyMap.containsKey(cell)) {
         map = new AttributeMap();
         GraphConstants.setBounds(map, r);
         propertyMap.put(cell, map);
      } else {
         map = (AttributeMap) propertyMap.get(cell);
         GraphConstants.setBounds(map, r);
      }
   }

   /**
    * Gets bounding rectangle of given cell. The rectangle is either current rectangle of
    * cellView either from propertyMap where are held bounding rectangles of various cells
    * during multiple resizing and/or translating of cells.
    */
   public Rectangle getCBounds(Object cell, Map propertyMap) {// HM,
      // JGraph3.4.1
      Rectangle2D bounds = null;
      if (propertyMap != null && propertyMap.containsKey(cell)) {
         Map map = (Map) propertyMap.get(cell);
         bounds = GraphConstants.getBounds(map);
      } else {
         CellView view = getView(cell);
         bounds = view.getBounds();
      }
      if (bounds != null) {
         return bounds.getBounds();
      }
      return null;
   }

   /**
    * Gets union of given rectangles.
    */
   public Rectangle getUnionBounds(Rectangle[] rects) {
      if (rects != null && rects.length > 0) {
         Rectangle unionRect = null;
         for (int i = 0; i < rects.length; i++) {
            if (unionRect == null) {
               unionRect = new Rectangle(rects[i]);
            } else {
               SwingUtilities.computeUnion(rects[i].x, rects[i].y, rects[i].width, rects[i].height, unionRect);
            }
         }
         return unionRect;
      }
      return null;
   }

   /**
    * Returns the central point of given graph object.
    */
   public Point getCenter(Object go) {
      if (go == null)
         return null;
      Rectangle2D r = getCBounds(go, null);// HM, JGraph3.4.1
      if (!(go instanceof GraphSwimlaneInterface)) {
         return new Point((int) (r.getX() + (int) (r.getWidth() / 2)), (int) (r.getY() + (int) (r.getHeight() / 2)));
      }
      return new Point((int) (r.getX() + defLaneNameWidth / 2), (int) (r.getY() + (int) (r.getHeight() / 2)));
   }

   // /////////////////////////// retreival of graph object for corresponding
   // XPDL object
   /**
    * Returns graph Activity object which represents XPDL activity with given Id
    * attribute.
    */
   public GraphActivityInterface getGraphActivity(String id) {
      List allActs = JaWEGraphModel.getAllActivitiesInModel(graphModel());
      if (allActs != null) {
         Iterator it = allActs.iterator();
         GraphActivityInterface gact;
         while (it.hasNext()) {
            gact = (GraphActivityInterface) it.next();
            if (gact.getPropertyObject().get("Id").toValue().equals(id)) {
               return gact;
            }
         }
      }
      return null;
   }

   public GraphActivityInterface getGraphActivity(Activity act) {
      List allActs = JaWEGraphModel.getAllActivitiesInModel(graphModel());
      if (allActs != null) {
         Iterator it = allActs.iterator();
         GraphActivityInterface gact;
         while (it.hasNext()) {
            gact = (GraphActivityInterface) it.next();
            if (gact.getUserObject() == act) {
               return gact;
            }
         }
      }
      return null;
   }

   /**
    * Returns graph Artifact object which represents XPDL artifact with given Id
    * attribute.
    */
   public GraphArtifactInterface getGraphArtifact(String id) {
      List allActs = JaWEGraphModel.getAllArtifactsInModel(graphModel());
      if (allActs != null) {
         Iterator it = allActs.iterator();
         GraphArtifactInterface gact;
         while (it.hasNext()) {
            gact = (GraphArtifactInterface) it.next();
            if (gact.getPropertyObject().get("Id").toValue().equals(id)) {
               return gact;
            }
         }
      }
      return null;
   }

   public GraphArtifactInterface getGraphArtifact(Artifact art) {
      List allActs = JaWEGraphModel.getAllArtifactsInModel(graphModel());
      if (allActs != null) {
         Iterator it = allActs.iterator();
         GraphArtifactInterface gact;
         while (it.hasNext()) {
            gact = (GraphArtifactInterface) it.next();
            if (gact.getUserObject() == art) {
               return gact;
            }
         }
      }
      return null;
   }

   public GraphTransitionInterface getGraphTransition(XMLCollectionElement tra) {
      List allTrans = JaWEGraphModel.getAllTransitionsInModel(graphModel());
      if (allTrans != null) {
         Iterator it = allTrans.iterator();
         GraphTransitionInterface gtra;
         while (it.hasNext()) {
            gtra = (GraphTransitionInterface) it.next();
            if (gtra.getUserObject() == tra) {
               return gtra;
            }
         }
      }
      return null;
   }

   /**
    * Returns graph Participant object which represents XPDL participant with given Id
    * attribute.
    */
   public GraphSwimlaneInterface getGraphParticipant(String id) {
      List allPartic = JaWEGraphModel.getAllParticipantsInModel(graphModel());
      if (allPartic != null) {
         Iterator it = allPartic.iterator();
         GraphSwimlaneInterface gpar;
         while (it.hasNext()) {
            gpar = (GraphSwimlaneInterface) it.next();
            if (gpar.getPropertyObject().get("Id").toValue().equals(id)) {
               return gpar;
            }
         }
      }
      return null;
   }

   public GraphSwimlaneInterface getGraphParticipant(Object par) {
      List allPartic = JaWEGraphModel.getAllParticipantsInModel(graphModel());
      if (allPartic != null) {
         Iterator it = allPartic.iterator();
         GraphSwimlaneInterface gpar;
         while (it.hasNext()) {
            gpar = (GraphSwimlaneInterface) it.next();
            if (gpar.getUserObject() == par) {
               return gpar;
            }
         }
      }
      return null;
   }

   public boolean isFreeTextExpressionParticipantShown() {
      List allPartic = JaWEGraphModel.getAllParticipantsInModel(graphModel());
      if (allPartic != null) {
         Iterator it = allPartic.iterator();
         GraphSwimlaneInterface gpar;
         Lane defL = GraphUtilities.getDefaultLane(JaWEManager.getInstance().getXPDLUtils().getPoolForProcessOrActivitySet(getXPDLOwner()));
         if (defL == null)
            return false;
         while (it.hasNext()) {
            gpar = (GraphSwimlaneInterface) it.next();
            if (gpar.getUserObject() == defL) {
               return true;
            }
         }
      }
      return false;
   }

   // /////////////////// Position handling
   // //////////////////////////////////////

   /**
    * Finds the nearest cell to the given cell in given direction.
    * 
    * @param selectedCell first currently selected cell
    * @param direction 0-Up, 1-Down, 2-Left, 3-Right (if given some other no, the Up is
    *           assumed)
    * @return The nearest cell to the given one in given direction, or null if no such
    *         cell.
    */
   public Object findNearestCell(Object selectedCell, int direction) {
      // retrieve all cells from model
      List cellSet = JaWEGraphModel.getAllCellsInModel(graphModel());
      // Set cellSet=PEGraphModel.getAllActivitiesInModel(graphModel());
      // if there is no any return null
      if (cellSet == null || cellSet.size() == 0)
         return null;

      Object[] cells = cellSet.toArray();
      // if current cell is null (nothing is selected), return
      // the first cell from array
      if (selectedCell == null)
         return cells[0];

      // get the center point of currently selected cell
      Point centerOfSelectedCell = getCenter(selectedCell);
      // if something went wrong, return the first cell from array
      if (centerOfSelectedCell == null)
         return cells[0];

      // initialy set the nearest cell to null, and min distance
      // to max. double value
      Object nearestCell = null;
      double minDistance = Double.MAX_VALUE;

      // search for the nearest cell considering the wanted direction
      for (int i = 0; i < cells.length; i++) {
         // skip the cell that we check for the nearest cell
         // and skip port cells
         if (cells[i] == selectedCell || cells[i] instanceof Port)
            continue;

         // the center of the next cell
         Point centerOfCell = getCenter(cells[i]);
         Point ref = new Point(centerOfCell.x, centerOfSelectedCell.y);
         double dist = centerOfSelectedCell.distance(centerOfCell);
         double absOfTan = centerOfCell.distance(ref) / ref.distance(centerOfSelectedCell);

         switch (direction) {
            case 1: // DOWN
               if (centerOfCell.y >= centerOfSelectedCell.y) {
                  if (dist < minDistance && absOfTan >= 1) {
                     minDistance = dist;
                     nearestCell = cells[i];
                  }
               }
               break;
            case 2: // LEFT
               if (centerOfCell.x <= centerOfSelectedCell.x) {
                  if (dist < minDistance && absOfTan <= 1) {
                     minDistance = dist;
                     nearestCell = cells[i];
                  }
               }
               break;
            case 3: // RIGHT
               if (centerOfCell.x >= centerOfSelectedCell.x) {
                  if (dist < minDistance && absOfTan <= 1) {
                     minDistance = dist;
                     nearestCell = cells[i];
                  }
               }
               break;
            default: // UP
               if (centerOfCell.y <= centerOfSelectedCell.y) {
                  if (dist < minDistance && absOfTan >= 1) {
                     minDistance = dist;
                     nearestCell = cells[i];
                  }
               }
               break;
         }
      }
      return nearestCell;
   }

   public boolean moveParticipant(GraphSwimlaneInterface parSource, boolean direction, Map propertyMap, ParentMap parentMap) {
      boolean updated = false;
      Rectangle rSource = getCBounds(parSource, propertyMap).getBounds();// getting
      // bounds
      // of
      // rectangle//HM,
      // JGraph3.4.1

      Rectangle matching = new Rectangle(rSource); // added by SB

      int yPos;
      int xPos;
      GraphSwimlaneInterface[] parts;
      if (direction) {// move up
         yPos = rSource.y - 1;
         xPos = rSource.x - 1;
         if (!isGraphRotated()) {
            parts = getParticipantsForYPos(yPos, 2, propertyMap, parentMap, false);
            GraphSwimlaneInterface parTarget = getCommonParentParticipant(parts, parSource.getParent());
            if (parTarget != null) {
               Rectangle rTarget = getCBounds(parTarget, propertyMap);// HM,
               // JGraph3.4.1

               translateParticipant(parSource, propertyMap, parentMap, 0, -rTarget.height, true);// source
               // part
               // up

               translateParticipant(parTarget, propertyMap, parentMap, 0, rSource.height, true);// target
               // part
               // down
            }
         } else {
            parts = getParticipantsForXPos(xPos, 2, propertyMap, parentMap, false);
            GraphSwimlaneInterface parTarget = getCommonParentParticipant(parts, parSource.getParent());
            if (parTarget != null) {
               Rectangle rTarget = getCBounds(parTarget, propertyMap);// HM,
               // JGraph3.4.1

               translateParticipant(parSource, propertyMap, parentMap, -rTarget.width, 0, true);// source
               // part
               // up

               translateParticipant(parTarget, propertyMap, parentMap, rSource.width, 0, true);// target
               // part
               // down
            }
         }
      } else {// move down
         yPos = rSource.y + rSource.height + 1;
         xPos = rSource.x + rSource.width + 1;
         if (!isGraphRotated()) {
            parts = getParticipantsForYPos(yPos, 2, propertyMap, parentMap, false);
            GraphSwimlaneInterface parTarget = getCommonParentParticipant(parts, parSource.getParent());
            if (parTarget != null) {
               Rectangle rTarget = getCBounds(parTarget, propertyMap);// HM,
               // JGraph3.4.1

               translateParticipant(parSource, propertyMap, parentMap, 0, rTarget.height, true);// source
               // part
               // down

               translateParticipant(parTarget, propertyMap, parentMap, 0, -rSource.height, true);// target
               // part
               // up
            }
         } else {
            parts = getParticipantsForXPos(xPos, 2, propertyMap, parentMap, false);
            GraphSwimlaneInterface parTarget = getCommonParentParticipant(parts, parSource.getParent());
            if (parTarget != null) {
               Rectangle rTarget = getCBounds(parTarget, propertyMap);// HM,
               // JGraph3.4.1

               translateParticipant(parSource, propertyMap, parentMap, rTarget.width, 0, true);// source
               // part
               // down

               translateParticipant(parTarget, propertyMap, parentMap, -rSource.width, 0, true);// target
               // part
               // up
            }
         }
      }

      Rectangle toMatch = getCBounds(parSource, propertyMap); // added
      // by
      // SB//HM,
      // JGraph3.4.1
      arrangeParticipants(propertyMap, parentMap);

      if (!toMatch.equals(matching)) {
         updated = true;
      }
      return updated;
   }

   // Harald Meister

   public Dimension getGraphsPreferredSize() {
      if (!isGraphRotated()) {
         return new Dimension(getRootParticipantWidth(null, null) + 50, getNewRootParYPos(null, null) + 50);
      }
      return new Dimension(getNewRootParXPos(null, null) + 50, getRootParticipantHeight(null, null) + 50);
   }

   protected List getPoints(GraphTransitionInterface cell, Map propertyMap) {// HM,
      // JGraph3.4.1
      if (propertyMap != null && propertyMap.containsKey(cell)) {
         Map map = (Map) propertyMap.get(cell);
         List points = GraphConstants.getPoints(map);
         List toRet = new ArrayList();
         for (int i = 1; i < points.size() - 1; i++) {
            Point2D p2d = (Point2D) points.get(i);
            Point p = new Point();
            p.setLocation(p2d);
            toRet.add(p);
         }
         return toRet;
      }
      return new ArrayList();
   }

   public boolean isGraphRotated() {
      return GraphUtilities.getGraphOrientation(getXPDLOwner()).equals(XPDLConstants.POOL_ORIENTATION_VERTICAL);
   }

   // // THE FOLLOWING METHODS ARE MODIFYING XPDL MODEL !!!
   public void moveCellsAndArrangeParticipants(Map propertyMap) {
      if (getGraphController().isUpdateInProgress() == true)
         Thread.dumpStack();
      JaWEManager.getInstance().getJaWEController().startUndouableChange();
      getGraphController().setUpdateInProgress(true);
      updateModelAndArrangeParticipants(null,
                                        propertyMap,
                                        null,
                                        null,
                                        getGraphController().getSettings().getLanguageDependentString("MessageMovingObjects"),
                                        null,
                                        true);
      JaWEManager.getInstance().getJaWEController().endUndouableChange(updateXPDLActivitiesAndArtifactsPosition(propertyMap));
      getGraphController().setUpdateInProgress(false);
   }

   protected List updateXPDLActivitiesAndArtifactsPosition(Map propertyMap) {
      List updatedPositions = new ArrayList();
      if (propertyMap == null) {
         return updatedPositions;
      }
      Object[] cellsToManage = propertyMap.keySet().toArray();
      for (int i = 0; i < cellsToManage.length; i++) {
         Object cell = cellsToManage[i];
         if (cell instanceof GraphCommonInterface) {
            GraphCommonInterface gact = (GraphCommonInterface) cell;
            GraphSwimlaneInterface gpar = (GraphSwimlaneInterface) gact.getParent();
            Point offset = getOffset(gact, propertyMap);
            XMLCollectionElement el = (XMLCollectionElement) ((WorkflowElement) gact).getPropertyObject();
            if (!GraphUtilities.getOffsetPoint(el).equals(offset)) {
               GraphUtilities.setOffsetPoint(el, offset, null);
               updatedPositions.add(el);
            }
            Lane newPar = (Lane) gpar.getPropertyObject();
            if (el instanceof Activity || el instanceof Artifact) {
               String parIdOld = JaWEManager.getInstance().getXPDLUtils().getLaneId(el);
               GraphUtilities.setLaneId(el, newPar.getId());
               // Setting the new performer for activity
               if (el instanceof Activity) {
                  Activity act = (Activity) el;
                  int actType = act.getActivityType();
                  if ((actType == XPDLConstants.ACTIVITY_TYPE_NO || actType == XPDLConstants.ACTIVITY_TYPE_TASK_APPLICATION)) {
                     if (!newPar.getId().equals(parIdOld)) {
                        String perf = GraphUtilities.getLanesFirstPerformer(newPar);
                        act.setFirstPerformer((perf != null) ? perf : "");
                     }
                  }
               }
            }
         } else if (cell instanceof GraphTransitionInterface) {
            GraphTransitionInterface gtra = (GraphTransitionInterface) cell;
            XMLCollectionElement tra = (XMLCollectionElement) gtra.getPropertyObject();
            if (tra == null) {
               continue;
            }
            List points = getPoints(gtra, propertyMap);
            List bps = GraphUtilities.getBreakpoints(tra);
            if (!points.equals(bps)) {
               GraphUtilities.setBreakpoints(tra, points);
               updatedPositions.add(tra);
            }
         }
      }
      return updatedPositions;
   }

   public void removeCells(Object[] cellsToDelete) {
      boolean updated = false;
      Set transitionsToDelete = new HashSet();
      Set associationsToDelete = new HashSet();
      Set activitiesToDelete = new HashSet();
      Set artifactsToDelete = new HashSet();
      Set participantsToDelete = new HashSet();

      // begining arrangement of parent of cells that will be deleted
      if (cellsToDelete != null && cellsToDelete.length > 0) {

         // All cells in model to be deleted
         Set allCellsToDelete = new HashSet(JaWEGraphModel.getDescendants(graphModel(), cellsToDelete));

         // getting transitions(edges) which are connected to the cellsForDel ->
         // also has to be deleted
         Set edges = new HashSet(JaWEGraphModel.getEdges(graphModel(), allCellsToDelete.toArray()));

         // putting all items for deletion (edges and cells) together - thats
         // ALL FOR DELETION
         allCellsToDelete.addAll(edges);

         int i = 0;
         // Separate cells and edges
         Iterator it = allCellsToDelete.iterator();
         while (it.hasNext()) {
            i++;
            Object cell = it.next();
            // System.out.println("Cell "+i+"="+cell+",
            // cn="+cell.getClass().getName()+", hc="+cell.hashCode());
            if (cell instanceof GraphTransitionInterface) {
               if (((GraphTransitionInterface) cell).getPropertyObject() instanceof Transition) {
                  transitionsToDelete.add(cell);
               } else {
                  associationsToDelete.add(cell);
               }
            } else if (cell instanceof GraphActivityInterface) {
               activitiesToDelete.add(cell);
            } else if (cell instanceof GraphArtifactInterface) {
               artifactsToDelete.add(cell);
            } else if (cell instanceof GraphSwimlaneInterface) {
               participantsToDelete.add(cell);
            }
         }
      }

      updated = updated
                || transitionsToDelete.size() > 0 || associationsToDelete.size() > 0 || activitiesToDelete.size() > 0 || artifactsToDelete.size() > 0
                || participantsToDelete.size() > 0;

      if (updated) {
         GraphController gcon = getGraph().getGraphController();
         JaWEController jc = JaWEManager.getInstance().getJaWEController();
         gcon.setUpdateInProgress(true);
         jc.startUndouableChange();
         removeCellsAndArrangeParticipants(cellsToDelete);

         Iterator it = transitionsToDelete.iterator();
         Transitions tras = (Transitions) getXPDLOwner().get("Transitions");
         while (it.hasNext()) {
            GraphTransitionInterface gt = (GraphTransitionInterface) it.next();
            Transition t = (Transition) gt.getPropertyObject();
            tras.remove(t);
         }

         it = associationsToDelete.iterator();
         Associations assocs = XMLUtil.getPackage(getWorkflowProcess()).getAssociations();
         while (it.hasNext()) {
            GraphTransitionInterface gt = (GraphTransitionInterface) it.next();
            Association a = (Association) gt.getPropertyObject();
            assocs.remove(a);
         }

         it = activitiesToDelete.iterator();
         Activities acts = (Activities) getXPDLOwner().get("Activities");
         while (it.hasNext()) {
            GraphActivityInterface ga = (GraphActivityInterface) it.next();
            Activity a = (Activity) ga.getPropertyObject();
            acts.remove(a);
         }

         it = artifactsToDelete.iterator();
         Artifacts arts = XMLUtil.getPackage(getWorkflowProcess()).getArtifacts();
         while (it.hasNext()) {
            GraphArtifactInterface ga = (GraphArtifactInterface) it.next();
            Artifact a = (Artifact) ga.getPropertyObject();
            arts.remove(a);
         }

         it = participantsToDelete.iterator();
         Lanes ls = JaWEManager.getInstance().getXPDLUtils().getPoolForProcessOrActivitySet(getXPDLOwner()).getLanes();
         while (it.hasNext()) {
            GraphSwimlaneInterface gp = (GraphSwimlaneInterface) it.next();
            Object p = gp.getPropertyObject();
            if (p instanceof Lane) {
               ls.remove((Lane) p);
            }
         }

         List toSelect = new ArrayList();
         toSelect.add(getXPDLOwner());
         jc.endUndouableChange(toSelect);
         gcon.setUpdateInProgress(false);
      }
   }

   // method for moving participant in graph
   // direction=true for up, false for down
   public void moveParticipant(Object cellToMove, boolean direction) {
      GraphParticipantComparator pc = new GraphParticipantComparator(this);
      GraphSwimlaneInterface partToMove;
      Lane laneToMove;
      System.out.println("MOVING PARTICIPANT1");
      if (cellToMove instanceof GraphSwimlaneInterface && ((GraphSwimlaneInterface) cellToMove).getPropertyObject() instanceof Lane) {
         partToMove = (GraphSwimlaneInterface) cellToMove;
         laneToMove = (Lane) partToMove.getPropertyObject();
      } else {
         return;
      }
      Map propertyMap = new HashMap();
      ParentMap parentMap = new JaWEParentMap();
      boolean updated = moveParticipant(partToMove, direction, propertyMap, parentMap);
      // changed by Sasa Bojanic - so that undo/redo actions are compound
      // and that there is no action if position doesn't change
      if (updated) {
         getGraphController().setUpdateInProgress(true);
         graphModel().insertAndEdit(null, propertyMap, null, parentMap, null);
         // make new visual order list

         Pool pool = JaWEManager.getInstance().getXPDLUtils().getPoolForProcessOrActivitySet(getXPDLOwner());
         List allGraphParticipants = JaWEGraphModel.getAllParticipantsInModel(graph.getModel());
         allGraphParticipants.remove(getGraphParticipant(pool));
         if (allGraphParticipants != null) {
            GraphParticipantComparator gpc = new GraphParticipantComparator(this);
            Collections.sort(allGraphParticipants, gpc);
         }
         JaWEManager.getInstance().getJaWEController().startUndouableChange();
         Object parentObj = ((GraphSwimlaneInterface) partToMove.getParent()).getPropertyObject();
         if (parentObj instanceof Pool) {
            Lanes lanes = pool.getLanes();
            int pos = lanes.indexOf(laneToMove);
            if (direction) {
               pos--;
            } else {
               pos++;
            }
            lanes.reposition(laneToMove, pos);
         } else {
            Lane pl = (Lane) parentObj;
            NestedLanes nls = pl.getNestedLanes();
            NestedLane nl = nls.getNestedLane(laneToMove.getId());
            int pos = nls.indexOf(nl);
            if (direction) {
               pos--;
            } else {
               pos++;
            }
            nls.reposition(nl, pos);

         }
         List toSelect = new ArrayList();
         toSelect.add(laneToMove);

         // GraphUtilities.setParticipantVisualOrder(getXPDLOwner(), vo);
         JaWEManager.getInstance().getJaWEController().endUndouableChange(toSelect);
         getGraphController().adjustActions();
         getGraphController().setUpdateInProgress(false);
      }
   }

   public void removeStartAndEndEvents() {
      XMLCollectionElement wpOrAs = getXPDLOwner();

      Set sas = XMLUtil.getStartingActivities(wpOrAs);
      sas.addAll(XMLUtil.getEndingActivities(wpOrAs));
      List toRem = new ArrayList();
      if (sas.size() > 0) {
         getGraphController().setUpdateInProgress(true);
         Iterator it = sas.iterator();
         while (it.hasNext()) {
            Activity act = (Activity) it.next();
            if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_EVENT_START || act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_EVENT_END) {
               removeActivityOrArtifact(act);
               if (!toRem.contains(act)) {
                  toRem.add(act);
               }
            }
         }
         JaWEManager.getInstance().getJaWEController().startUndouableChange();
         it = toRem.iterator();
         Activities acts = (Activities) wpOrAs.get("Activities");
         while (it.hasNext()) {
            acts.remove((Activity) it.next());
         }
         List toSelect = new ArrayList();
         toSelect.add(wpOrAs);
         JaWEManager.getInstance().getJaWEController().endUndouableChange(toSelect);
         getGraphController().setUpdateInProgress(false);
      }
   }

   public void insertMissingStartAndEndEvents() {
      XMLCollectionElement wpOrAs = getXPDLOwner();
      WorkflowProcess wp = getWorkflowProcess();
      String asId = null;
      if (wpOrAs instanceof ActivitySet) {
         asId = wpOrAs.getId();
      }
      GraphController gc = getGraphController();
      Dimension defActDim = new Dimension(gc.getGraphSettings().getActivityWidth(), gc.getGraphSettings().getActivityHeight());
      Set sas = XMLUtil.getStartingActivities(wpOrAs);
      Set eas = XMLUtil.getEndingActivities(wpOrAs);

      List easToAdd = new ArrayList();
      Iterator it = sas.iterator();
      int ah = gc.getGraphSettings().getActivityHeight();
      int aw = gc.getGraphSettings().getActivityWidth();
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         if (act.getActivityType() != XPDLConstants.ACTIVITY_TYPE_EVENT_START && act.getActivityType() != XPDLConstants.ACTIVITY_TYPE_EVENT_END) {
            GraphActivityInterface gact = getGraphActivity(act);
            Set racts = gact.getReferencingActivities();
            if (!GraphManager.containsStartOrEndEvent(racts, true)) {
               Activity start = JaWEManager.getInstance()
                  .getXPDLObjectFactory()
                  .createXPDLObject((Activities) wpOrAs.get("Activities"), JaWEConstants.ACTIVITY_TYPE_START, true);
               Point op = GraphManager.getStartOrEndEventOffsetPointForInsertion(GraphUtilities.getOffsetPoint(act), true, defActDim, isGraphRotated());
               // if (!isGraphRotated()) {
               op.x += (aw - 0.6 * ah) / 2;
               op.y += ah / 5;
               // } else {
               // op.x+=(aw-0.6*ah)/2;
               // op.y+=ah/5;
               // }
               GraphUtilities.createNodeGraphicsInfo(start, op, JaWEManager.getInstance().getXPDLUtils().getLaneId(act), true);
               insertStartOrEndEvent(start, act.getId());
            }
         }
      }

      it = eas.iterator();
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         if (act.getActivityType() != XPDLConstants.ACTIVITY_TYPE_EVENT_START && act.getActivityType() != XPDLConstants.ACTIVITY_TYPE_EVENT_END) {
            GraphActivityInterface gact = getGraphActivity(act);
            Set racts = gact.getReferencedActivities();
            if (!GraphManager.containsStartOrEndEvent(racts, false) && !XMLUtil.hasCircularTransitions(XMLUtil.getNonExceptionalOutgoingTransitions(act))) {
               Activity end = JaWEManager.getInstance()
                  .getXPDLObjectFactory()
                  .createXPDLObject((Activities) wpOrAs.get("Activities"), JaWEConstants.ACTIVITY_TYPE_END, true);
               Point op = GraphManager.getStartOrEndEventOffsetPointForInsertion(GraphUtilities.getOffsetPoint(act), false, defActDim, isGraphRotated());
               // if (!isGraphRotated()) {
               op.x += (aw - 0.6 * ah) / 2;
               op.y += ah / 5;
               // } else {
               // op.x+=(aw-0.6*ah)/2;
               // op.y+=ah/5;
               // }
               GraphUtilities.createNodeGraphicsInfo(end, op, JaWEManager.getInstance().getXPDLUtils().getLaneId(act), true);
               insertStartOrEndEvent(end, act.getId());
            }
         }
      }
   }

   protected static boolean containsStartOrEndEvent(Set gacts, boolean start) {
      Iterator it = gacts.iterator();
      while (it.hasNext()) {
         GraphActivityInterface gact = (GraphActivityInterface) it.next();
         Activity act = (Activity) gact.getPropertyObject();
         if ((start && act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_EVENT_START)
             || (!start && act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_EVENT_END)) {
            return true;
         }
      }

      return false;
   }

   protected static Point getStartOrEndEventOffsetPointForInsertion(Point actOffs, boolean start, Dimension defActDim, boolean isGraphRotated) {
      Point p = new Point(actOffs);

      if (start) {
         if (!isGraphRotated) {
            p.x -= defActDim.width * 1.5;
         } else {
            p.y -= defActDim.height * 1.5;
         }
      } else {
         if (!isGraphRotated) {
            p.x += defActDim.width * 1.5;
         } else {
            p.y += defActDim.height * 1.5;
         }
      }
      if (!isGraphRotated) {
         if (p.x < 0) {
            p.x = 0;
         }
      } else {
         if (p.y < 0) {
            p.y = 0;
         }
      }
      return p;
   }

   protected static List getAllChildParticipants(GraphSwimlaneInterface gpar) {
      List toRet = new ArrayList();
      Set cpars = gpar.getChildSwimlanes();
      toRet.addAll(cpars);
      Iterator it = cpars.iterator();
      while (it.hasNext()) {
         GraphSwimlaneInterface cp = (GraphSwimlaneInterface) it.next();
         toRet.addAll(cp.getChildSwimlanes());
      }
      return toRet;
   }

   protected static GraphSwimlaneInterface getCommonParentParticipant(GraphSwimlaneInterface[] parts, Object parent) {
      if (parts != null) {
         for (int i = 0; i < parts.length; i++) {
            if (parts[i].getParent() == parent) {
               return parts[i];
            }
         }
      }
      return null;
   }

   public void showArtifacts(boolean show) {
      Set arts = GraphUtilities.getArtifacts(getXPDLOwner());
      Set assocs = GraphUtilities.getAssociations(getXPDLOwner());
      Iterator it = arts.iterator();
      while (it.hasNext()) {
         Artifact art = (Artifact) it.next();
         if (show) {
            insertActivityOrArtifact(art);
         } else {
            removeActivityOrArtifact(art);
         }
      }
      it = assocs.iterator();
      while (it.hasNext()) {
         Association assoc = (Association) it.next();
         if (show) {
            insertTransitionOrAssociation(assoc);
         } else {
            removeTransitionOrAssociation(assoc);
         }
      }

   }

}
