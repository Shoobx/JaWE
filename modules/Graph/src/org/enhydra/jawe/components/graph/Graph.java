package org.enhydra.jawe.components.graph;

import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.swing.JComponent;
import javax.swing.tree.DefaultMutableTreeNode;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.xpdlvalidator.ValidationError;
import org.enhydra.shark.xpdl.XMLCollectionElement;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.XMLValidationError;
import org.enhydra.shark.xpdl.elements.Activity;
import org.enhydra.shark.xpdl.elements.ActivitySet;
import org.enhydra.shark.xpdl.elements.ExtendedAttribute;
import org.enhydra.shark.xpdl.elements.Participant;
import org.enhydra.shark.xpdl.elements.Transition;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;
import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.CellView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.GraphSelectionModel;
import org.jgraph.graph.Port;

/**
 * Standard implementation of JGraph. Represents a WorkflowProcess or ActivitySet graph.
 * @author Sasa Bojanic
 */
public class Graph extends JGraph {
   
   protected WorkflowProcess wp;
   protected ActivitySet as;   
   
   /** WorkflowManager that controls various things */
   protected transient GraphManager graphManager;
   protected GraphController graphController;
   
   public GraphController getGraphController () {
      return graphController;
   }
   
   /**
    * Constructs process graph based on a given model.
    */
   public Graph(GraphController gc,GraphModel model,BasicMarqueeHandler mh,WorkflowProcess wp) {
      init(gc,model,mh,wp);
      graphManager.createWorkflowGraph(wp);
   }

   public Graph(GraphController gc,GraphModel model,BasicMarqueeHandler mh,ActivitySet as) {      
      init(gc,model,mh,as);
      graphManager.createWorkflowGraph(as);
   }

   public Graph(GraphModel model,GraphLayoutCache view) {      
      super(model,view);
   }

   public Graph(Graph g) {      
      super(g.getModel(), g.getGraphLayoutCache());
      this.graphController = g.graphController;
      this.setMarqueeHandler(g.getMarqueeHandler());
      this.wp = g.getWorkflowProcess();
      this.as = g.getActivitySet();
      
      initGraphBehavior();
      this.graphManager = g.graphManager;     
   }
     
   protected void init (GraphController gc,GraphModel model,BasicMarqueeHandler mh,XMLCollectionElement wpOrAs) {
      this.graphController=gc;
      selectionModel = new JaWEGraphSelectionModel(this);
      setLayout(null);
      GraphLayoutCache view = new GraphLayoutCache(model, new GraphObjectViewFactory());//HM, JGraph3.4.1
      setGraphLayoutCache(view);
      updateUI();
      if (model == null) {
         model = new JaWEGraphModel();
      }
      setModel(model);
      this.setMarqueeHandler(mh);

      if (wpOrAs instanceof WorkflowProcess) {
         this.wp=(WorkflowProcess)wpOrAs;
      } else {
         this.as=(ActivitySet)wpOrAs;
         this.wp=XMLUtil.getWorkflowProcess(wpOrAs);
      }
      initGraphBehavior();
      this.graphManager=createGraphManager();
   }
     
   // TODO: throw an exception. Maybe make only one instance of GraphManager for all graphs
   protected GraphManager createGraphManager () {
      try {
         String gmc = graphController.getGraphSettings().getGraphManager();
         Constructor c=Class.forName(gmc).getConstructor(new Class[] {Graph.class});
         return (GraphManager)c.newInstance(new Object[]{this});
      } catch (Exception ex) {
         return null;
      }
   }
   
   public GraphManager getGraphManager () {
      return graphManager;
   }
      
   public WorkflowProcess getWorkflowProcess () {
      return wp;
   }

   public ActivitySet getActivitySet () {
      return as;
   }
   
   public XMLCollectionElement getXPDLObject() {
      if (as != null) return as;

      return wp;
   }
   
   public WorkflowElement selectActivity (Activity act,boolean add) {
      GraphActivityInterface ga=getGraphManager().getGraphActivity(act);
      selectElement(ga, add, true);
      return ga;
   }
   
   
   public WorkflowElement selectTransition (Transition tra, boolean add) {
      GraphTransitionInterface gt=getGraphManager().getGraphTransition(tra);
      selectElement(gt, add, true);
      return gt;
   }

   public WorkflowElement selectParticipant (Participant par) {
      GraphParticipantInterface gpar=getGraphManager().getGraphParticipant(par);
      selectElement(gpar, false, false);
      return gpar;
   }

   public WorkflowElement selectBubble (ExtendedAttribute ea,boolean add) {
      GraphBubbleActivityInterface gb=getGraphManager().getBubble(ea);
      selectElement(gb, add, true);
      return gb;
   }
   
   public void selectElement (WorkflowElement we,boolean add,boolean toFront) {
      if (we!=null) {
         if (add) {
            addSelectionCell(we);
         } else {
            setSelectionCell(we);
         }
         if (toFront) {
            getModel().toFront(new Object[] {we});
         }
      }      
   }
   
   public WorkflowElement getGraphInterface(XMLElement el) {
      if (el instanceof Activity)
         return getGraphManager().getGraphActivity((Activity)el);
      
      if (el instanceof Transition) 
         return getGraphManager().getGraphTransition((Transition) el);
      
      if (el instanceof Participant)
         return getGraphManager().getGraphParticipant((Participant) el);
      
      if (el instanceof ExtendedAttribute)
         return getGraphManager().getBubble((ExtendedAttribute) el);
         
      return null;
   }
   
   public void selectElements(Object[] elements, boolean add, boolean toFront) {
      if (elements !=null) {
         if (add) {
            addSelectionCells(elements);
         } else {
            setSelectionCells(elements);
         }
         if (toFront) {
            getModel().toFront(elements);
         }
      }
   }
   
   /**
    * Overrides Superclass method.
    */
   public String convertValueToString(Object value) {
      if (value instanceof CellView) {
         value=((CellView)value).getCell();
      }
      
      if (value instanceof DefaultMutableTreeNode &&
          ((DefaultMutableTreeNode)value).getUserObject() != null &&
          !(value instanceof GraphBubbleActivityInterface)) {
         if (value instanceof GraphTransitionInterface) {
            Transition tra=(Transition)((GraphTransitionInterface)value).getPropertyObject();
            if (graphController.getGraphSettings().shouldShowTransitionNameForCondition()) {
               return tra.getName();
            } 
            return tra.getCondition().toValue();            
         }
         return value.toString();      
      } else if (value != null) {
         return value.toString();
      }
      return null;
   }

   /**
    * Finds the topmost Participant at specified location.
    */
   public Object getFirstParticipantForLocation(int x,int y) {
      x /= scale; y /= scale; // FIX: Consistency with other methods?
      CellView[] cells = getOrderedAllSelectableCells();
      if (cells != null) {
         Rectangle r = new Rectangle(x-tolerance,y-tolerance,2*tolerance,2*tolerance);
         // Iterate through cells and find first Participant at
         // if current is traversed. Cache first cell.
         for (int i=0; i<cells.length; i++) {
            if (cells[i] instanceof GraphParticipantViewInterface) {
               boolean intersects=cells[i].getBounds().intersects(r);
               if (intersects) {
                  return cells[i].getCell();
               }
            }
         }
      }
      
      return null;
   }

   /**
    *  Modified from original to support all views
    */
   public CellView getNextViewAt(CellView current, double x, double y) {//HM, JGraph3.4.1
      CellView[] sel = getOrderedAllSelectableCells();
      CellView cell = getNextViewAt(sel, current, x, y);
      return cell;
   }

   /**
    * Modified from original to suite our needs. This method makes a Participant
    * to be selected only when it's name part is pressed, and to give it's
    * tooltip only when you want to insert some cells in it.
    */
   public CellView getNextViewAt(CellView[] cells, CellView c, double x, double y) {//HM, JGraph3.4.1
      if (cells != null) {
         
         Rectangle r = new Rectangle((int)x-tolerance,(int)y-tolerance,2*tolerance,2*tolerance);//HM, JGraph3.4.1
         // Iterate through cells and switch to active
         // if current is traversed. Cache first cell.
         CellView first = null;
         boolean active = (c == null);

         for (int i=0; i<cells.length; i++) {
            boolean intersects=false;
            boolean wholeArea=true;
            GraphMarqueeHandler mh=(GraphMarqueeHandler)marquee;
            if (mh.isSelectButtonSelected() || mh.isTransitionButtonSelected()) {
               wholeArea=false;
            }
            if ((cells[i] instanceof GraphParticipantViewInterface) && wholeArea) {
               intersects=cells[i].getBounds().intersects(r);
            }
            else {
               intersects=cells[i].intersects(this, r);
            }

            if (intersects) {
               if (active) {
                  return cells[i];
               }
               else if (first == null) {
                  first = cells[i];
               }
               active = active | (cells[i] == c);
            }
         }
         return first;
      }
      return null;
   }

   /**
    * This method gets all selectable views and puts it in an order that suites to
    * our needs (first comes activities and transitions(edges), and then Participants sorted
    * by level - root Participants comes last)
    */
   private CellView[] getOrderedAllSelectableCells () {
      // Get Roots in View Order
      CellView[] views =  graphLayoutCache.getRoots();
      // Add Roots to Stack
      Stack s = new Stack();
      for (int i = 0; i < views.length; i++) {
         s.add(views[i]);
      }
      java.util.List result = new ArrayList();
      // Traverse All Children In View Order
      while (!s.isEmpty()) {
         CellView view = (CellView)s.pop();
         Object cell=view.getCell();
         // Add To List if it is not a port or forbidden object
         if (!(cell instanceof Port)) {// && !(cell instanceof SubflowPort)) {
            result.add(view);
         }
         // Add Children to Stack
         CellView[] children = view.getChildViews();
         for (int i = 0; i < children.length; i++) {
            s.add(children[i]);
         }
      }
      // Order so that all activities comes first, after that Participants in ordered view

      // first iteration - separating Participants and others
      java.util.List activitiesAndEdges = new ArrayList();
      java.util.List participants = new ArrayList();
      Iterator it = result.iterator();
      while (it.hasNext()) {
         CellView cv=(CellView)it.next();
         if (cv.getCell() instanceof GraphParticipantInterface) {
            participants.add(cv);
         }
         else {
            activitiesAndEdges.add(cv);
         }
      }

      // second iteration - order, first adding activities & edges and then
      // Participants in reversed order: it must be done that way because the
      // child views of Participants (activities) that had focus more recently
      // has higher number and are placed closer to the begining of
      // activitiesAndEdges set, but on contrary, Participants that has higher
      // depth (and should have focus before their parents) are placed closer
      // to the end of Participants set
      int i = -1;
      int j=participants.size()+activitiesAndEdges.size();
      CellView[] tmp = new CellView[j];

      it=activitiesAndEdges.iterator();
      while (it.hasNext()) {
         tmp[++i] = (CellView)it.next();
      }

      it=participants.iterator();
      while (it.hasNext()) {
         tmp[--j] = (CellView)it.next();
      }

      return tmp;
   }

   /**
    * Only for debugging purpose.
    */
   public void printOrderedAllSelectables() {
      CellView[] sel = getOrderedAllSelectableCells();
      for (int i=0; i<sel.length; i++)
         System.out.println("view"+i+"="+sel[i].getCell());
   }

   /**
    * Overrides <code>JComponent</code>'s <code>getToolTipText</code>
    * method in order to allow the graph controller to create a tooltip
    * for the topmost cell under the mousepointer. This differs from JTree
    * where the renderers tooltip is used.
    * <p>
    * NOTE: For <code>JGraph</code> to properly display tooltips of its
    * renderers, <code>JGraph</code> must be a registered component with the
    * <code>ToolTipManager</code>.  This can be done by invoking
    * <code>ToolTipManager.sharedInstance().registerComponent(graph)</code>.
    * This is not done automatically!
    * @param event the <code>MouseEvent</code> that initiated the
    * <code>ToolTip</code> display
    * @return a string containing the  tooltip or <code>null</code>
    * if <code>event</code> is null
    */
   public String getToolTipText(MouseEvent event) {
      if(event != null) {
         Object cell;
         // if activity or Participant is to be inserted, show
         // underlying Participant, else show other
         GraphMarqueeHandler mh=(GraphMarqueeHandler)marquee;
         Point p=(Point) fromScreen(event.getPoint());
         if (!(mh.isSelectButtonSelected() || mh.isTransitionButtonSelected())) {
            cell=getFirstParticipantForLocation((int)p.getX(),(int)p.getY());
         }
         else {
            cell=getFirstCellForLocation(p.getX(),p.getY());
         }
         if (cell != null) {
            String s=convertValueToString(cell);
            if (cell instanceof WorkflowElement) {
               s=((WorkflowElement)cell).getTooltip();
            }
            return s;
         }
      }
      return null;
   }

   public boolean validateAgainsXPDLSchema () {
      return true;
   }


//   /**
//    * Reacts upon the XML element change by setting isModified flag
//    * of PackageEditor if needed.
//    */
//   public void xmlElementChanged (XMLElement el) {
//      if (el instanceof org.enhydra.shark.xpdl.elements.Activity) {
//         editor.getStatusBar().updateMessage();
//      }
//   }
//

   public void initGraphBehavior() {
      setHandleSize(4);
      setTolerance(4);
      setSizeable(false);
      setMoveable(!getXPDLObject().isReadOnly());
      setDisconnectable(true);
      setDisconnectOnMove(false);
      setAntiAliased(true);
      setAutoscrolls(true);
      selectionModel.setSelectionMode(GraphSelectionModel.MULTIPLE_GRAPH_SELECTION);
      refreshGraphConfiguration();

   }

   public void refreshGraphConfiguration () {
      GraphSettings gval = GraphUtilities.getGraphController().getGraphSettings();
      boolean gs = gval.shouldShowGrid();
      setGridEnabled(gs);
      setGridVisible(gs);
      setGridSize(gval.getGridSize());
      setBackground(gval.getBackgroundColor());      
      setHighlightColor(gval.getSelectedActivityColor());
      setGridColor(gval.getGridColor());
      setHandleColor(gval.getHandleColor());
      setMarqueeColor(gval.getMarqueeColor());
      setFontSize();
//      ((JaWEMarqueeHandler)getMarqueeHandler()).enableBubblesButtons(JaWEConfig.getInstance().getUseBubblesStatus());
//      updateStartEndBubbles();
   }

   protected void setFontSize () {
//      boolean isModified=JaWE.getInstance().isModified();
      List cellList=JaWEGraphModel.getAllCellsInModel(getModel());
      if (cellList==null) return;
      //Filter ports out
      java.util.List list = new ArrayList();
      for (Iterator i=cellList.iterator(); i.hasNext();) {
         Object cell=i.next();
         if (!(cell instanceof Port)) {
            list.add(cell);
         }
      }
      Object[] cells = list.toArray();

      
      String fntn = JaWEManager.getFontName();
      int fntsize=graphController.getGraphSettings().getGraphFontSize();
      javax.swing.plaf.FontUIResource f;
      try {
         try {
            f = new javax.swing.plaf.FontUIResource(fntn, Font.PLAIN, fntsize);
         } catch (Exception ex) {
            f = new javax.swing.plaf.FontUIResource("Label.font", Font.PLAIN, fntsize);
         }
         Map nested = new HashMap();
         for (int i = 0; i < cells.length; i++) {
            CellView view = getGraphLayoutCache().getMapping(cells[i], false);
            if (view != null) {
               Font font = GraphConstants.getFont(view.getAllAttributes());
               if (font.getSize() == fntsize) {
                  return;
               }
               AttributeMap attr = new AttributeMap();
               GraphConstants.setFont(attr, f.deriveFont(fntsize));
               nested.put(cells[i], attr);
            }
         }      
         //getGraphLayoutCache().edit(nested, null, null, null);
         //graphModel.edit(nested,null,null,null);
         ((JaWEGraphModel)graphModel).editFonts(nested);
      } catch (Exception ex) {
         
      }
   }

   /**
    * Overrides  super method - don't need to waste a time for this.
    */
   public static void addSampleData(GraphModel model) {
      return;
   }
   
   /**
    * Notification from the <code>UIManager</code> that the L&F has changed.
    * Replaces the current UI object with the latest version from the
    * <code>UIManager</code>. Subclassers can override this to support
    * different GraphUIs.
    * @see JComponent#updateUI
    *
    */
   public void updateUI() {
      setUI(new JaWEGraphUI());
      invalidate();
   }
   
   public String toString () {
      XMLCollectionElement xpdlo=getXPDLObject();
      if (xpdlo!=null) {
         return JaWEManager.getInstance().getDisplayNameGenerator().getDisplayName(xpdlo);
      }
      return "";
   }
   
   public List checkConnections (boolean fullCheck) {
      boolean wellConnected=true;
      List verrors=new ArrayList();
      
      // check start's and end's connections
      Set icStarts=getImproperlyConnectedStarts(fullCheck);
      if (icStarts.size()>0) {
         wellConnected=false;
         for (Iterator i=icStarts.iterator(); i.hasNext();) {
            GraphActivityInterface s=(GraphActivityInterface)i.next();
            ValidationError ve=null;
            if (s instanceof GraphBubbleActivityInterface) {
               ve=new ValidationError(
                     new XMLValidationError(
                           XMLValidationError.TYPE_ERROR,
                           XMLValidationError.SUB_TYPE_CONNECTION,
                           getGraphController().getSettings().getLanguageDependentString("ErrorConnectionToStartingActivityIsMissing"),
                           "",
                           s.getPropertyObject())
                           );
            } else {
               ve=new ValidationError(
                     new XMLValidationError(
                           XMLValidationError.TYPE_ERROR,
                           XMLValidationError.SUB_TYPE_CONNECTION,
                           getGraphController().getSettings().getLanguageDependentString("ErrorIncomingTransitionOrConnectionFromStartBubbleIsMissing"),
                           "",
                           s.getPropertyObject())
                           );
            }
            verrors.add(ve);
            if (!(wellConnected || fullCheck)) {
               break;
            }

         }
      }

      if (fullCheck || wellConnected) {
         Set icEnds=getImproperlyConnectedEnds(fullCheck);
         if (icEnds.size()>0) {
            wellConnected=false;
            for (Iterator i=icEnds.iterator(); i.hasNext();) {
               GraphActivityInterface e=(GraphActivityInterface)i.next();
               ValidationError ve=null;
               if (e instanceof GraphBubbleActivityInterface) {
                  if (e.getReferencingActivities().size()==0) {
                     ve=new ValidationError(
                           new XMLValidationError(
                                 XMLValidationError.TYPE_ERROR,
                                 XMLValidationError.SUB_TYPE_CONNECTION,
                                 getGraphController().getSettings().getLanguageDependentString("ErrorConnectionFromEndingActivityIsMissing"),
                                 "",
                                 e.getPropertyObject())
                                 );
                  } else {
                     ve=new ValidationError(
                           new XMLValidationError(
                                 XMLValidationError.TYPE_ERROR,
                                 XMLValidationError.SUB_TYPE_CONNECTION,
                                 getGraphController().getSettings().getLanguageDependentString("ErrorNotConnectedToEndingActivity"),
                                 "",
                                 e.getPropertyObject())
                                 );
                  }
               } else {
                  ve=new ValidationError(
                        new XMLValidationError(
                              XMLValidationError.TYPE_ERROR,
                              XMLValidationError.SUB_TYPE_CONNECTION,
                              getGraphController().getSettings().getLanguageDependentString("ErrorOutgoingTransitionOrConnectionToEndBubbleIsMissing"),
                              "",
                              e.getPropertyObject())
                              );
                  
               }
               verrors.add(ve);
               if (!(wellConnected || fullCheck)) {
                  break;
               }
            }
         }
      }

      return verrors;
   }

   public Set getImproperlyConnectedStarts (boolean fullCheck) {
      Set icStarts=new HashSet();

      List allGraphActivities=JaWEGraphModel.getAllActivitiesInModel(graphModel);
      if (allGraphActivities!=null) {
         Iterator it=allGraphActivities.iterator();
         while (it.hasNext()) {
            GraphActivityInterface gact=(GraphActivityInterface)it.next();            
            if (gact instanceof GraphBubbleActivityInterface && ((GraphBubbleActivityInterface)gact).isStart()) {
               if (gact.getReferencedActivities().size()==0) {
                  icStarts.add(gact);
                  if (!fullCheck) {
                     break;
                  }
               }
            } else if (!(gact instanceof GraphBubbleActivityInterface)) {
               Set incomingTrans=gact.getReferencingActivities();
               if (incomingTrans.size()==0) {
                  icStarts.add(gact);
                  if (!fullCheck) {
                     break;
                  }
               } 
//               else if (incomingTrans.size()==1) {
//                  if (Utils.hasCircularTransitions(incomingTrans)) {
//                     icStarts.add(act);
//                     if (!fullCheck) {
//                        break;
//                     }
//                  }
//               }
            }
         }
      }
      return icStarts;
   }

   public Set getImproperlyConnectedEnds (boolean fullCheck) {
      Set icEnds=new HashSet();

      List allGraphActivities=JaWEGraphModel.getAllActivitiesInModel(graphModel);
      if (allGraphActivities!=null) {
         Iterator it=allGraphActivities.iterator();
         while (it.hasNext()) {
            GraphActivityInterface gact=(GraphActivityInterface)it.next();            
            if (gact instanceof GraphBubbleActivityInterface && !((GraphBubbleActivityInterface)gact).isStart()) {
               Set incT=gact.getReferencingActivities();
               if (incT.size()==0) {
                  icEnds.add(gact);
                  if (!fullCheck) {
                     break;
                  }
               } 
//               else if (incT.size()==1) {
//                  Transition t=(Transition)incT.toArray()[0];
//                  Activity a=(Activity)((DefaultPort)t.getSource()).getParent();
//                  Set eas=XMLUtil.getEndingActivities((XMLCollectionElement)getXPDLObject());
//                  if (!eas.contains(a.getPropertyObject())) {
//                     icEnds.add(act);
//                     if (!fullCheck) {
//                        break;
//                     }
//                  }
//               }
            } else if (!(gact instanceof GraphBubbleActivityInterface)) {
               Set excTrans=XMLUtil.getExceptionalOutgoingTransitions((Activity)gact.getPropertyObject());
               Set outgoingTrans=gact.getReferencedActivities();
               if (outgoingTrans.size()==0 || (outgoingTrans.size()==1 && excTrans.size()>0)) {
                  icEnds.add(gact);
                  if (!fullCheck) {
                     break;
                  }
               }
//               if (outgoingTrans.size()==1) {
//                  if (Utils.hasCircularTransitions(outgoingTrans)) {
//                     icEnds.add(act);
//                     if (!fullCheck) {
//                        break;
//                     }
//                  }
//               }
            }
         }
      }

      return icEnds;
   }
   
   // TO AVOID MEMORY LEAK -> RENDERERS HOLD A REFERENCE TO GRAPHS
   public void clearXPDLObjectReferences () {
//      Object[] elem = JaWEGraphModel.getAll(graphModel);
//      graphModel.remove(elem);
//      setUI(null);
//      wp=null;
//      as=null;
//      graphLayoutCache=null;
//      graphModel=null;
//      selectionModel=null;
      DefaultGraphActivityView.renderers.clear();
//      DefaultGraphBubbleActivityView.renderers.clear();
      DefaultGraphParticipantView.renderers.clear();
      DefaultGraphTransitionView.renderers.clear();
      DefaultGraphPortView.renderers.clear();
   }

}
