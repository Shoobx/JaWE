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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.JComponent;
import javax.swing.tree.DefaultMutableTreeNode;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ActivitySet;
import org.enhydra.jxpdl.elements.Artifact;
import org.enhydra.jxpdl.elements.Association;
import org.enhydra.jxpdl.elements.Lane;
import org.enhydra.jxpdl.elements.Pool;
import org.enhydra.jxpdl.elements.Transition;
import org.enhydra.jxpdl.elements.WorkflowProcess;
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
 * 
 * @author Sasa Bojanic
 */
public class Graph extends JGraph implements Printable {

   protected WorkflowProcess wp;

   protected ActivitySet as;

   /** WorkflowManager that controls various things */
   protected transient GraphManager graphManager;

   protected GraphController graphController;

   protected boolean showTransitionConditions;

   protected boolean showArtifacts;

   public GraphController getGraphController() {
      return graphController;
   }

   /**
    * Constructs process graph based on a given model.
    */
   public Graph(GraphController gc, GraphModel model, BasicMarqueeHandler mh, WorkflowProcess wp) {
      init(gc, model, mh, wp);
      graphManager.createWorkflowGraph(wp);
   }

   public Graph(GraphController gc, GraphModel model, BasicMarqueeHandler mh, ActivitySet as) {
      init(gc, model, mh, as);
      graphManager.createWorkflowGraph(as);
   }

   public Graph(GraphModel model, GraphLayoutCache view) {
      super(model, view);
      setShowTransAndArtsProperties();
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

   protected void init(GraphController gc, GraphModel model, BasicMarqueeHandler mh, XMLCollectionElement wpOrAs) {
      this.graphController = gc;
      selectionModel = new JaWEGraphSelectionModel(this);
      setLayout(null);
      GraphLayoutCache view = new GraphLayoutCache(model, new GraphObjectViewFactory());// HM,
                                                                                        // JGraph3.4.1
      setGraphLayoutCache(view);
      updateUI();
      if (model == null) {
         model = new JaWEGraphModel();
      }
      setModel(model);
      this.setMarqueeHandler(mh);

      if (wpOrAs instanceof WorkflowProcess) {
         this.wp = (WorkflowProcess) wpOrAs;
      } else {
         this.as = (ActivitySet) wpOrAs;
         this.wp = XMLUtil.getWorkflowProcess(wpOrAs);
      }
      initGraphBehavior();
      this.graphManager = createGraphManager();
   }

   // TODO: throw an exception. Maybe make only one instance of GraphManager for all
   // graphs
   protected GraphManager createGraphManager() {
      try {
         String gmc = graphController.getGraphSettings().getGraphManager();
         Constructor c = Class.forName(gmc).getConstructor(new Class[] {
            Graph.class
         });
         return (GraphManager) c.newInstance(new Object[] {
            this
         });
      } catch (Exception ex) {
         return null;
      }
   }

   public GraphManager getGraphManager() {
      return graphManager;
   }

   public WorkflowProcess getWorkflowProcess() {
      return wp;
   }

   public ActivitySet getActivitySet() {
      return as;
   }

   public XMLCollectionElement getXPDLObject() {
      if (as != null)
         return as;

      return wp;
   }

   public WorkflowElement selectActivity(Activity act, boolean add) {
      GraphActivityInterface ga = getGraphManager().getGraphActivity(act);
      selectElement(ga, add, true);
      return ga;
   }

   public WorkflowElement selectArtifact(Artifact art, boolean add) {
      GraphArtifactInterface ga = getGraphManager().getGraphArtifact(art);
      selectElement(ga, add, true);
      return ga;
   }

   public WorkflowElement selectTransition(XMLCollectionElement tra, boolean add) {
      GraphTransitionInterface gt = getGraphManager().getGraphTransition(tra);
      selectElement(gt, add, true);
      return gt;
   }

   public WorkflowElement selectParticipant(Object par) {
      GraphSwimlaneInterface gpar = getGraphManager().getGraphParticipant(par);
      selectElement(gpar, false, false);
      return gpar;
   }

   public void selectElement(WorkflowElement we, boolean add, boolean toFront) {
      if (we != null) {
         if (add) {
            addSelectionCell(we);
         } else {
            setSelectionCell(we);
         }
         if (toFront) {
            getModel().toFront(new Object[] {
               we
            });
         }
      }
   }

   public WorkflowElement getGraphInterface(XMLElement el) {
      if (el instanceof Activity)
         return getGraphManager().getGraphActivity((Activity) el);

      if (el instanceof Artifact)
         return getGraphManager().getGraphArtifact((Artifact) el);

      if (el instanceof Transition)
         return getGraphManager().getGraphTransition((Transition) el);

      if (el instanceof Association)
         return getGraphManager().getGraphTransition((Association) el);

      if (el instanceof Lane || el instanceof Pool)
         return getGraphManager().getGraphParticipant(el);

      return null;
   }

   public void selectElements(Object[] elements, boolean add, boolean toFront) {
      if (elements != null) {
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
         value = ((CellView) value).getCell();
      }

      if (value instanceof DefaultMutableTreeNode && ((DefaultMutableTreeNode) value).getUserObject() != null) {
         if (value instanceof GraphTransitionInterface) {
            XMLCollectionElement tOrA = (XMLCollectionElement) ((GraphTransitionInterface) value).getPropertyObject();
            if (tOrA instanceof Transition) {
               Transition tra = (Transition) tOrA;
               if (graphController.getGraphSettings().shouldShowTransitionNameForCondition()) {
                  return tra.getName();
               }
               return tra.getCondition().toValue();
            }
            return "";
         } else if (value instanceof GraphActivityInterface) {
            Activity act = (Activity) ((GraphActivityInterface) value).getUserObject();
            if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_ROUTE) {
               return "";
            }
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
   public Object getFirstParticipantForLocation(int x, int y) {
      x /= scale;
      y /= scale; // FIX: Consistency with other methods?
      CellView[] cells = getOrderedAllSelectableCells();
      if (cells != null) {
         Rectangle r = new Rectangle(x - tolerance, y - tolerance, 2 * tolerance, 2 * tolerance);
         // Iterate through cells and find first Participant at
         // if current is traversed. Cache first cell.
         for (int i = 0; i < cells.length; i++) {
            if (cells[i] instanceof GraphSwimlaneViewInterface) {
               boolean intersects = cells[i].getBounds().intersects(r);
               if (intersects) {
                  return cells[i].getCell();
               }
            }
         }
      }

      return null;
   }

   /**
    * Modified from original to support all views
    */
   public CellView getNextViewAt(CellView current, double x, double y) {// HM, JGraph3.4.1
      CellView[] sel = getOrderedAllSelectableCells();
      CellView cell = getNextViewAt(sel, current, x, y);
      return cell;
   }

   /**
    * Modified from original to suite our needs. This method makes a Participant to be selected only when it's name part is pressed, and to give it's tooltip
    * only when you want to insert some cells in it.
    */
   public CellView getNextViewAt(CellView[] cells, CellView c, double x, double y) {// HM,
                                                                                    // JGraph3.4.1
      if (cells != null) {

         Rectangle r = new Rectangle((int) x - tolerance, (int) y - tolerance, 2 * tolerance, 2 * tolerance);// HM, JGraph3.4.1
         // Iterate through cells and switch to active
         // if current is traversed. Cache first cell.
         CellView first = null;
         boolean active = (c == null);

         for (int i = 0; i < cells.length; i++) {
            boolean intersects = false;
            boolean wholeArea = true;
            GraphMarqueeHandler mh = (GraphMarqueeHandler) marquee;
            if (mh.isSelectButtonSelected() || mh.isTransitionButtonSelected() || mh.isAssociationButtonSelected()) {
               wholeArea = false;
            }
            if ((cells[i] instanceof GraphSwimlaneViewInterface) && wholeArea) {
               intersects = cells[i].getBounds().intersects(r);
            } else {
               intersects = cells[i].intersects(this, r);
            }

            if (intersects) {
               if (active) {
                  return cells[i];
               } else if (first == null) {
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
    * This method gets all selectable views and puts it in an order that suites to our needs (first comes activities and transitions(edges), and then
    * Participants sorted by level - root Participants comes last)
    */
   private CellView[] getOrderedAllSelectableCells() {
      // Get Roots in View Order
      CellView[] views = graphLayoutCache.getRoots();
      // Add Roots to Stack
      Stack s = new Stack();
      for (int i = 0; i < views.length; i++) {
         s.add(views[i]);
      }
      java.util.List result = new ArrayList();
      // Traverse All Children In View Order
      while (!s.isEmpty()) {
         CellView view = (CellView) s.pop();
         Object cell = view.getCell();
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
      java.util.List activities = new ArrayList();
      java.util.List edges = new ArrayList();
      java.util.List others = new ArrayList();
      java.util.List participants = new ArrayList();
      Iterator it = result.iterator();
      while (it.hasNext()) {
         CellView cv = (CellView) it.next();
         if (cv.getCell() instanceof GraphSwimlaneInterface) {
            participants.add(cv);
         } else {
            if (cv.getCell() instanceof GraphActivityInterface) {
               activities.add(cv);
            } else if (cv.getCell() instanceof GraphTransitionInterface) {
               edges.add(cv);
            } else {
               others.add(cv);
            }
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
      int j = participants.size() + activities.size() + edges.size() + others.size();
      CellView[] tmp = new CellView[j];

      it = others.iterator();
      while (it.hasNext()) {
         tmp[++i] = (CellView) it.next();
      }

      it = edges.iterator();
      while (it.hasNext()) {
         tmp[++i] = (CellView) it.next();
      }

      it = activities.iterator();
      while (it.hasNext()) {
         tmp[++i] = (CellView) it.next();
      }

      it = participants.iterator();
      while (it.hasNext()) {
         tmp[--j] = (CellView) it.next();
      }

      return tmp;
   }

   /**
    * Only for debugging purpose.
    */
   public void printOrderedAllSelectables() {
      CellView[] sel = getOrderedAllSelectableCells();
      for (int i = 0; i < sel.length; i++)
         System.out.println("view" + i + "=" + sel[i].getCell());
   }

   /**
    * Overrides <code>JComponent</code>'s <code>getToolTipText</code> method in order to allow the graph controller to create a tooltip for the topmost cell
    * under the mousepointer. This differs from JTree where the renderers tooltip is used.
    * <p>
    * NOTE: For <code>JGraph</code> to properly display tooltips of its renderers, <code>JGraph</code> must be a registered component with the
    * <code>ToolTipManager</code>. This can be done by invoking <code>ToolTipManager.sharedInstance().registerComponent(graph)</code>. This is not done
    * automatically!
    * 
    * @param event the <code>MouseEvent</code> that initiated the <code>ToolTip</code> display
    * @return a string containing the tooltip or <code>null</code> if <code>event</code> is null
    */
   public String getToolTipText(MouseEvent event) {
      if (event != null) {
         Object cell;
         // if activity or Participant is to be inserted, show
         // underlying Participant, else show other
         GraphMarqueeHandler mh = (GraphMarqueeHandler) marquee;
         Point p = (Point) fromScreen(event.getPoint());
         if (!(mh.isSelectButtonSelected() || mh.isTransitionButtonSelected() || mh.isAssociationButtonSelected())) {
            cell = getFirstParticipantForLocation((int) p.getX(), (int) p.getY());
         } else {
            cell = getFirstCellForLocation(p.getX(), p.getY());
         }
         if (cell != null) {
            String s = convertValueToString(cell);
            if (cell instanceof WorkflowElement) {
               s = ((WorkflowElement) cell).getTooltip();
            }
            return s;
         }
      }
      return null;
   }

   public boolean validateAgainsXPDLSchema() {
      return true;
   }

   // /**
   // * Reacts upon the XML element change by setting isModified flag
   // * of PackageEditor if needed.
   // */
   // public void xmlElementChanged (XMLElement el) {
   // if (el instanceof org.enhydra.jxpdl.elements.Activity) {
   // editor.getStatusBar().updateMessage();
   // }
   // }
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
      setEdgeLabelsMovable(true);
   }

   public void refreshGraphConfiguration() {
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
      setShowTransAndArtsProperties();
   }

   protected void setShowTransAndArtsProperties() {
      boolean show = ((Boolean) graphController.getSettings().getSetting("ShowTransitionCondition")).booleanValue();
      setShowTransitionConditions(show);
      show = ((Boolean) graphController.getSettings().getSetting("ShowArtifacts")).booleanValue();
      setShowArtifacts(show);
   }

   protected void setFontSize() {
      // boolean isModified=JaWE.getInstance().isModified();
      List cellList = JaWEGraphModel.getAllCellsInModel(getModel());
      if (cellList == null)
         return;
      // Filter ports out
      java.util.List list = new ArrayList();
      for (Iterator i = cellList.iterator(); i.hasNext();) {
         Object cell = i.next();
         if (!(cell instanceof Port)) {
            list.add(cell);
         }
      }
      Object[] cells = list.toArray();

      String fntn = JaWEManager.getFontName();
      int fntsize = graphController.getGraphSettings().getGraphFontSize();
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
         // getGraphLayoutCache().edit(nested, null, null, null);
         // graphModel.edit(nested,null,null,null);
         ((JaWEGraphModel) graphModel).editFonts(nested);
      } catch (Exception ex) {

      }
   }

   /**
    * Overrides super method - don't need to waste a time for this.
    */
   public static void addSampleData(GraphModel model) {
      return;
   }

   /**
    * Notification from the <code>UIManager</code> that the LF has changed. Replaces the current UI object with the latest version from the
    * <code>UIManager</code>. Subclassers can override this to support different GraphUIs.
    * 
    * @see JComponent#updateUI
    */
   public void updateUI() {
      setUI(new JaWEGraphUI());
      invalidate();
   }

   public String toString() {
      XMLCollectionElement xpdlo = getXPDLObject();
      if (xpdlo != null) {
         return JaWEManager.getInstance().getDisplayNameGenerator().getDisplayName(xpdlo);
      }
      return "";
   }

   // TO AVOID MEMORY LEAK -> RENDERERS HOLD A REFERENCE TO GRAPHS
   public void clearXPDLObjectReferences() {
      // Object[] elem = JaWEGraphModel.getAll(graphModel);
      // graphModel.remove(elem);
      // setUI(null);
      // wp=null;
      // as=null;
      // graphLayoutCache=null;
      // graphModel=null;
      // selectionModel=null;
      DefaultGraphActivityView.renderers.clear();
      DefaultGraphArtifactView.renderers.clear();
      DefaultGraphSwimlaneView.renderers.clear();
      DefaultGraphTransitionView.renderers.clear();
      DefaultGraphPortView.renderers.clear();
   }

   public boolean shouldShowTransitionConditions() {
      return showTransitionConditions;
   }

   public void setShowTransitionConditions(boolean show) {
      showTransitionConditions = show;
   }

   public boolean shouldShowArtifacts() {
      return showArtifacts;
   }

   public void setShowArtifacts(boolean show) {
      showArtifacts = show;
   }

   /**
    * Prints the specified page on the specified graphics using <code>pageFormat</code> for the page format.
    * 
    * @param g The graphics to paint the graph on.
    * @param printFormat The page format to use for printing.
    * @param page The page to print
    * @return Returns {@link Printable#PAGE_EXISTS} or {@link Printable#NO_SUCH_PAGE}.
    */
   public int print(Graphics g, PageFormat printFormat, int page) {
      Dimension pSize = new Dimension((int) Math.ceil(getBounds().getX() + getBounds().getWidth()) + 1, (int) Math.ceil(getBounds().getY()
                                                                                                                        + getBounds().getHeight()) + 1);

      int w = (int) printFormat.getImageableWidth();
      int h = (int) printFormat.getImageableHeight();

      int cols = (int) Math.max(Math.ceil((double) (pSize.width - 5) / (double) w), 1);
      int rows = (int) Math.max(Math.ceil((double) (pSize.height - 5) / (double) h), 1);
      int pageCount = cols * rows;
      if (page >= pageCount)
         return NO_SUCH_PAGE;

      int dx = (int) ((page % cols) * w);
      int dy = (int) (Math.floor(page / cols) * h);

      g.translate(-dx, -dy);
      g.setClip(dx, dy, w, h);
      paint(g);
      return PAGE_EXISTS;
   }

   protected PageFormat pageFormat = new PageFormat();

   public PageFormat getPageFormat() {
      return pageFormat;
   }

   public void setPageFormat(PageFormat pageFormat) {
      this.pageFormat = pageFormat;
   }

}
