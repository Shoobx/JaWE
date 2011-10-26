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

import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ToolTipManager;

import org.enhydra.jawe.ChoiceButton;
import org.enhydra.jawe.ChoiceButtonListener;
import org.enhydra.jawe.HistoryManager;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.XPDLElementChangeInfo;
import org.enhydra.jawe.base.xpdlhandler.XPDLHandler;
import org.enhydra.jawe.components.graph.actions.SimpleGraphLayout;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLElementChangeInfo;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ActivitySet;
import org.enhydra.jxpdl.elements.Artifact;
import org.enhydra.jxpdl.elements.Association;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.Lane;
import org.enhydra.jxpdl.elements.NestedLane;
import org.enhydra.jxpdl.elements.Package;
import org.enhydra.jxpdl.elements.Participant;
import org.enhydra.jxpdl.elements.Pool;
import org.enhydra.jxpdl.elements.Transition;
import org.enhydra.jxpdl.elements.WorkflowProcess;
import org.enhydra.jxpdl.utilities.SequencedHashMap;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.DefaultGraphCell;

/**
 * Used to handle process graphs.
 * 
 * @author Sasa Bojanic
 * @author Miroslav Popov
 */
public class GraphController extends Observable implements
                                               Observer,
                                               JaWEComponent,
                                               ChoiceButtonListener,
                                               GraphSelectionListener {

   protected String type = JaWEComponent.MAIN_COMPONENT;

   protected boolean updateInProgress = false;

   protected GraphObjectFactory graphObjectFactory;

   protected GraphObjectRendererFactory graphObjectRendererFactory;

   protected GraphMarqueeHandler graphMarqueeHandler;

   protected Graph selectedGraph;

   protected GraphControllerPanel panel;

   protected GraphOverview overview;

   protected Map graphMap = new SequencedHashMap();

   protected CopyOrCutInfo copyOrCutInfo;

   protected GraphSettings settings;

   protected HistoryManager hm;

   public GraphController(JaWEComponentSettings settings) throws Exception {
      this.settings = (GraphSettings) settings;
      this.settings.init(this);

      try {
         String hmc = this.settings.historyManagerClass();
         if (hmc != null && !hmc.equals("")) {
            hm = (HistoryManager) Class.forName(hmc).newInstance();
            hm.init(this.settings.historySize());
         }
      } catch (Exception ex) {
         System.err.println("Failed to instantiate history manager for " + this);
      }

      graphObjectFactory = (GraphObjectFactory) Class.forName(this.settings.getGraphObjectFactory())
         .newInstance();
      graphObjectRendererFactory = (GraphObjectRendererFactory) Class.forName(this.settings.getGraphObjectRendererFactory())
         .newInstance();
      Constructor c = Class.forName(this.settings.getGraphMarqueeHandler())
         .getConstructor(new Class[] {
            GraphController.class
         });
      graphMarqueeHandler = (GraphMarqueeHandler) c.newInstance(new Object[] {
         this
      });
      c = Class.forName(this.settings.getGraphControllerPanel())
         .getConstructor(new Class[] {
            GraphController.class
         });
      panel = (GraphControllerPanel) c.newInstance(new Object[] {
         this
      });

      if (this.settings.shouldShowGraphOverview()) {
         try {
            String ovc = this.settings.overviewClass();
            if (ovc != null && !ovc.equals("")) {
               overview = (GraphOverview) Class.forName(ovc).newInstance();
               overview.init(this);
            }
         } catch (Exception ex) {
            System.err.println("Failed to instantiate GraphOverview!");
         }
      }

      init();

      JaWEManager.getInstance().getJaWEController().addObserver(this);
      if (overview != null) {
         JaWEManager.getInstance().getComponentManager().addComponent(overview);
         JaWEManager.getInstance().getJaWEController().registerJaWEComponent(overview);
      }

   }

   // ********************** Observable
   // **********************

   // ********************** Observer
   public void update(Observable o, Object arg) {
      // JaWEManager.getInstance().getLoggingManager().info("GraphController -> trying to start update for event "+arg+", update in progress="+updateInProgress);
      if (updateInProgress)
         return;
      if (!(arg instanceof XPDLElementChangeInfo))
         return;
      XPDLElementChangeInfo info = (XPDLElementChangeInfo) arg;
      XMLElement changedElement = info.getChangedElement();
      int action = info.getAction();
      if (info.getSource() == this
          || (changedElement == null && action != XPDLElementChangeInfo.SELECTED))
         return;
      if (!(action == XMLElementChangeInfo.UPDATED
            || action == XMLElementChangeInfo.INSERTED
            || action == XMLElementChangeInfo.REMOVED
            || action == XMLElementChangeInfo.REPOSITIONED
            || action == XPDLElementChangeInfo.SELECTED
            || action == XPDLElementChangeInfo.ADJUST_UNDOABLE_ACTION
            || action == XPDLElementChangeInfo.UNDO
            || action == XPDLElementChangeInfo.REDO
            || action == XPDLElementChangeInfo.COPY || action == XPDLElementChangeInfo.CUT))
         return;

      long start = System.currentTimeMillis();
      JaWEManager.getInstance()
         .getLoggingManager()
         .info("GraphController -> update for event " + info + " started ...");

      updateInProgress = true;
      try {
         if (action == XPDLElementChangeInfo.COPY || action == XPDLElementChangeInfo.CUT) {
            copyOrCutInfo = null;
            if (selectedGraph != null) {
               GraphManager gm = selectedGraph.getGraphManager();

               Map actRectangles = new HashMap();
               Iterator it = info.getChangedSubElements().iterator();
               while (it.hasNext()) {
                  XMLElement toCopyOrCut = (XMLElement) it.next();
                  if (!(toCopyOrCut instanceof Activity || toCopyOrCut instanceof Artifact))
                     continue;
                  XMLCollectionElement a = (XMLCollectionElement) toCopyOrCut;
                  DefaultGraphCell ga = null;
                  if (a instanceof Activity) {
                     ga = gm.getGraphActivity((Activity) a);
                  } else {
                     ga = gm.getGraphArtifact((Artifact) a);
                  }

                  if (ga != null) {
                     CopiedActivityOrArtifactInfo ai = new CopiedActivityOrArtifactInfo(JaWEManager.getInstance()
                                                                       .getXPDLUtils()
                                                                       .getLaneId(a),
                                                                    GraphUtilities.getOffsetPoint(a));
                     Rectangle rect = gm.getCBounds(ga, new HashMap());
                     actRectangles.put(ai, rect);
                  }
               }

               if (actRectangles.size() > 0) {
                  Rectangle[] rarr = new Rectangle[actRectangles.size()];
                  actRectangles.values().toArray(rarr);
                  Point referencePoint = gm.getUnionBounds(rarr).getLocation();
                  copyOrCutInfo = new CopyOrCutInfo(referencePoint, actRectangles);
               }
            }
         } else if (action == XPDLElementChangeInfo.UNDO
                    || action == XPDLElementChangeInfo.REDO
                    || action == XPDLElementChangeInfo.ADJUST_UNDOABLE_ACTION) {
            if (action == XPDLElementChangeInfo.ADJUST_UNDOABLE_ACTION) {
               GraphUtilities.adjustPackageOnUndoableChangeEvent(info.getChangedSubElements());
            } else {
               GraphUtilities.adjustPackageOnUndoOrRedoEvent(info.getChangedSubElements(),
                                                             action == XPDLElementChangeInfo.UNDO);
            }
         } else {
            update(info);
         }
      } finally {
         updateInProgress = false;
      }
      JaWEManager.getInstance()
         .getLoggingManager()
         .info("GraphController -> update ended");
      long end = System.currentTimeMillis();
      double diffs = (end - start) / 1000.0;
      JaWEManager.getInstance()
         .getLoggingManager()
         .debug("THE UPDATE OF GRAPH COMPONENT LASTED FOR " + diffs + " SECONDS!");
   }

   // **********************

   // ********************** JaWEComponent
   public JaWEComponentSettings getSettings() {
      return settings;
   }

   public JaWEComponentView getView() {
      return panel;
   }

   public String getName() {
      return "GraphComponent";
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public boolean adjustXPDL(Package pkg) {
      return GraphUtilities.scanExtendedAttributes(pkg);
   }

   public List checkValidity(XMLElement el, boolean fullCheck) {
      return null;
   }

   public boolean canCreateElement(XMLCollection col) {
      return true;
   }

   public boolean canInsertElement(XMLCollection col, XMLElement el) {
      if (el == null)
         return true;
      ExtendedAttribute ea = null;
      if (el instanceof ExtendedAttribute) {
         ea = (ExtendedAttribute) el;
      } else if (el.getParent() instanceof ExtendedAttribute) {
         ea = (ExtendedAttribute) el.getParent();
      }
      if (ea != null) {
         return !GraphUtilities.isMyKindOfExtendedAttribute(ea);
      }
      return true;
   }

   public boolean canModifyElement(XMLElement el) {
      if (el == null)
         return true;
      ExtendedAttribute ea = null;
      if (el instanceof ExtendedAttribute) {
         ea = (ExtendedAttribute) el;
      } else if (el.getParent() instanceof ExtendedAttribute) {
         ea = (ExtendedAttribute) el.getParent();
      }
      if (ea != null) {
         return !GraphUtilities.isMyKindOfExtendedAttribute(ea);
      }

      return true;
   }

   public boolean canRemoveElement(XMLCollection col, XMLElement el) {
      if (el == null)
         return true;
      ExtendedAttribute ea = null;
      if (el instanceof ExtendedAttribute) {
         ea = (ExtendedAttribute) el;
      } else if (el.getParent() instanceof ExtendedAttribute) {
         ea = (ExtendedAttribute) el.getParent();
      }
      if (ea != null) {
         boolean isMyEA = GraphUtilities.isMyKindOfExtendedAttribute(ea);
         if (isMyEA) {
            String eaName = ea.getName();
            if (eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_END_OF_WORKFLOW)
                || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_START_OF_WORKFLOW)
                || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_START_OF_BLOCK)
                || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_END_OF_BLOCK)) {
               return true;
            }
            return false;
         }
      } else if (el instanceof FreeTextExpressionParticipant
                 || el instanceof CommonExpressionParticipant) {
         return false;
      }
      return true;
   }

   public boolean canDuplicateElement(XMLCollection col, XMLElement el) {
      if (el == null)
         return true;
      ExtendedAttribute ea = null;
      if (el instanceof ExtendedAttribute) {
         ea = (ExtendedAttribute) el;
      } else if (el.getParent() instanceof ExtendedAttribute) {
         ea = (ExtendedAttribute) el.getParent();
      }
      if (ea != null) {
         return !GraphUtilities.isMyKindOfExtendedAttribute(ea);
      } else if (el instanceof FreeTextExpressionParticipant
                 || el instanceof CommonExpressionParticipant) {
         return false;
      }
      return true;
   }

   public boolean canRepositionElement(XMLCollection col, XMLElement el) {
      if (el == null)
         return true;
      ExtendedAttribute ea = null;
      if (el instanceof ExtendedAttribute) {
         ea = (ExtendedAttribute) el;
      } else if (el.getParent() instanceof ExtendedAttribute) {
         ea = (ExtendedAttribute) el.getParent();
      }
      if (ea != null) {
         return !GraphUtilities.isMyKindOfExtendedAttribute(ea);
      } else if (el instanceof FreeTextExpressionParticipant
                 || el instanceof CommonExpressionParticipant) {
         return false;
      }
      return true;
   }

   // **********************

   // ********************** ChoiceButtonListener
   public void selectionChanged(ChoiceButton cbutton, Object change) {
      if (updateInProgress)
         return;
      if (getSelectedGraph() == null)
         return;
      if (cbutton.getChoiceType() == Participant.class) {
         Participant par = (Participant) change;

         setUpdateInProgress(true);
         JaWEManager.getInstance().getJaWEController().startUndouableChange();
         Pool pool = JaWEManager.getInstance()
            .getXPDLUtils()
            .getPoolForProcessOrActivitySet(getSelectedGraph().getXPDLObject());
         Lane lane = null;
         if (par instanceof FreeTextExpressionParticipant) {
            lane = GraphUtilities.createDefaultLane(pool);
         } else {
            lane = GraphUtilities.createLaneForPerformer(pool, par.getId());
         }
         GraphUtilities.createNodeGraphicsInfo(lane, null, null, true);
         Object selEl = JaWEManager.getInstance()
            .getJaWEController()
            .getSelectionManager()
            .getSelectedElement();
         Point whereTo = null;
         if (selEl instanceof Participant) {
            Lane parentL = (Lane) GraphUtilities.getLaneForPerformer(pool,
                                                                     ((Participant) selEl).getId());
            GraphSwimlaneInterface gpar = getSelectedGraph().getGraphManager()
               .getGraphParticipant(parentL);
            if (gpar != null) {
               Rectangle b = getSelectedGraph().getGraphManager()
                  .getCBounds(gpar, new HashMap());
               whereTo = new Point(b.x + 11, b.y + 11);

               NestedLane nl = (NestedLane) parentL.getNestedLanes().generateNewElement();
               nl.setLaneId(lane.getId());
               parentL.getNestedLanes().add(nl);
            }
         }
         getSelectedGraph().getGraphManager()
            .insertParticipantAndArrangeParticipants(lane, whereTo);
         List toSelect = new ArrayList();
         toSelect.add(par);
         JaWEManager.getInstance().getJaWEController().endUndouableChange(toSelect);
         selectedGraph.clearSelection();
         selectedGraph.selectParticipant(lane);
         setUpdateInProgress(false);

         adjustActions();

         // List
         // vo=GraphUtilities.getParticipantVisualOrder(selectedGraph.getXPDLObject());
         // vo.add(par.getId());
         // GraphUtilities.setParticipantVisualOrder(selectedGraph.getXPDLObject(),vo);
         // getSelectedGraph().getGraphManager().insertParticipantAndArrangeParticipants(par);
      } else if (cbutton.getChoiceType() == ActivitySet.class) {
         ActivitySet aset = (ActivitySet) change;
         setSelectedGraph(getGraph(aset));

         JaWEManager.getInstance()
            .getJaWEController()
            .getSelectionManager()
            .setSelection(aset, true);
      }
   }

   public Object getSelectedObject(ChoiceButton cbutton) {
      if (cbutton.getChoiceType() == Participant.class)
         return null;

      return null;
   }

   public List getChoices(ChoiceButton cbutton) {
      List choices = new ArrayList();
      if (selectedGraph == null)
         return choices;

      if (cbutton.getChoiceType() == Participant.class) {
         WorkflowProcess wp = selectedGraph.getWorkflowProcess();
         SequencedHashMap choiceMap = XMLUtil.getPossibleParticipants(wp,
                                                                      JaWEManager.getInstance()
                                                                         .getXPDLHandler());
         List toRemove = selectedGraph.getGraphManager().getDisplayedParticipants();

         Iterator it = choiceMap.entrySet().iterator();
         while (it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            if (toRemove.contains(me.getValue())) {
               it.remove();
            }
         }

         choices.addAll(choiceMap.values());
      } else if (cbutton.getChoiceType() == ActivitySet.class) {
         WorkflowProcess wp = selectedGraph.getWorkflowProcess();
         choices = wp.getActivitySets().toElements();
      }

      return choices;
   }

   // **********************

   // ********************** GraphSelectionListener
   public void valueChanged(GraphSelectionEvent e) {
      if (updateInProgress)
         return;
      updateInProgress = true;
      Object[] selectedCells = getSelectedGraph().getSelectionCells();
      Lane par = null;
      List selectedElements = new ArrayList();
      if (selectedCells != null) {
         for (int i = 0; i < selectedCells.length; i++) {
            if (selectedCells[i] instanceof WorkflowElement) {
               WorkflowElement we = (WorkflowElement) selectedCells[i];
               XMLElement el = we.getPropertyObject();
               if (el != null) {
                  if (el instanceof Lane) {
                     par = (Lane) el;
                  }
                  selectedElements.add(el);
               }
            }
         }
      }
      if (selectedElements.size() != 0) {
         if (par != null) {
            Participant p = GraphUtilities.getParticipantForLane(par, null);
            if (p == null) {
               setUpdateInProgress(true);
               JaWEManager.getInstance()
                  .getJaWEController()
                  .getSelectionManager()
                  .setSelection((XMLElement) null, false);
               setUpdateInProgress(false);
            } else {
               JaWEManager.getInstance()
                  .getJaWEController()
                  .getSelectionManager()
                  .setSelection(p, false);
            }
         } else {
            // setUpdateInProgress(true);
            // JaWEManager.getInstance().getJaWEController().getSelectionManager().setSelection((XMLElement)null,
            // false);
            // setUpdateInProgress(false);
            if (selectedElements.get(0) instanceof Pool) {
               JaWEManager.getInstance()
                  .getJaWEController()
                  .getSelectionManager()
                  .setSelection(JaWEManager.getInstance()
                                   .getXPDLUtils()
                                   .getProcessForPool((Pool) selectedElements.get(0)),
                                true);
            } else {
               JaWEManager.getInstance()
                  .getJaWEController()
                  .getSelectionManager()
                  .setSelection(selectedElements, true);
            }
         }
      } else {
         JaWEManager.getInstance()
            .getJaWEController()
            .getSelectionManager()
            .setSelection(selectedGraph.getXPDLObject(), true);
         // JaWEManager.getInstance().getJaWEController().getSelectionManager().setSelection((XMLElement)null,
         // false);
         // if (freeOrCommon==null) {
         // JaWEManager.getInstance().getJaWEController().getSelectionManager().setSelection(selectedElements);
         // }
      }
      adjustActions();
      updateInProgress = false;
   }

   // **********************

   protected void init() throws Exception {
      panel = createPanel();
      adjustActions();
   }

   public GraphSettings getGraphSettings() {
      return settings;
   }

   public void update(XPDLElementChangeInfo info) {
      XMLElement changedElement = info.getChangedElement();
      List changedElements = info.getChangedSubElements();
      int action = info.getAction();

      // ********************************* START OF SELECTION EVENT
      if (action == XPDLElementChangeInfo.SELECTED) {
         // clean old selection
         if (selectedGraph != null) {
            if (overview != null) {
               if (overview.getGraph() != null) {
                  overview.getGraph().clearSelection();
               }
            }

            selectedGraph.clearSelection();
         }

         if (changedElement != null) {
            // ***************************** find graph
            Graph g = null;
            XMLElement lastSel = changedElement;
            if (changedElements.size() > 0) {
               lastSel = (XMLElement) changedElements.get(changedElements.size() - 1);
            }

            if (graphMap.containsKey(lastSel)) {
               g = getGraph((XMLCollectionElement) lastSel);
            }

            if (g == null && selectedGraph != null) {
               if (lastSel instanceof Participant) {
                  Lane l = GraphUtilities.getLaneForPerformer(JaWEManager.getInstance()
                                                                 .getXPDLUtils()
                                                                 .getPoolForProcessOrActivitySet(selectedGraph.getXPDLObject()),
                                                              ((Participant) lastSel).getId());
                  if (l != null) {
                     lastSel = l;
                  }
               }
               if (lastSel instanceof Lane
                   && selectedGraph.getGraphManager().getGraphParticipant((Lane) lastSel) != null) {
                  g = selectedGraph;
               } else if (lastSel instanceof Activity
                          && selectedGraph.getGraphManager()
                             .getGraphActivity((Activity) lastSel) != null) {
                  g = selectedGraph;
               } else if (lastSel instanceof Transition
                          && selectedGraph.getGraphManager()
                             .getGraphTransition((Transition) lastSel) != null) {
                  g = selectedGraph;
               } else if (lastSel instanceof Association
                          && selectedGraph.getGraphManager()
                             .getGraphTransition((Association) lastSel) != null) {
                  g = selectedGraph;
               }
            }

            if (g == null) {
               XMLElement parent = lastSel;
               do {
                  parent = parent.getParent();
               } while (parent != null
                        && !(parent instanceof ActivitySet || parent instanceof WorkflowProcess));
               if (parent != null) {
                  g = getGraph((XMLCollectionElement) parent);
               } else {
                  if (selectedGraph != null) {
                     if (XMLUtil.getPackage(lastSel) == XMLUtil.getPackage(selectedGraph.getXPDLObject()))
                        g = selectedGraph;
                  }
               }
            }

            if (g == null) {
               Package sp = XMLUtil.getPackage(lastSel);
               if (sp != null) {
                  List wps = sp.getWorkflowProcesses().toElements();
                  if (wps.size() != 0) {
                     g = getGraph((XMLCollectionElement) wps.get(0));
                  }
               }
            }

            if (g != selectedGraph) {
               setSelectedGraph(g);
            }

            if (selectedGraph != null)
               selectedGraph.clearSelection();
            // ***************************** end of find graph

            // **************************** select elements
            WorkflowElement selectedElement = null;
            WorkflowElement lastSelected = null;
            if (selectedGraph != null) {
               List selectedElements = new ArrayList();
               List overviewElements = new ArrayList();
               List toCheck = new ArrayList();
               if (changedElements == null || changedElements.size() == 0) {
                  toCheck.add(changedElement);
               } else {
                  toCheck.addAll(changedElements);
               }
               for (Iterator it = toCheck.iterator(); it.hasNext();) {
                  XMLElement el = (XMLElement) it.next();
                  if (el instanceof Participant) {
                     Lane l = GraphUtilities.getLaneForPerformer(JaWEManager.getInstance()
                                                                    .getXPDLUtils()
                                                                    .getPoolForProcessOrActivitySet(selectedGraph.getXPDLObject()),
                                                                 ((Participant) el).getId());
                     if (l != null) {
                        el = l;
                     }
                  }
                  selectedElement = selectedGraph.getGraphInterface(el);
                  if (overview != null
                      && selectedElement != null
                      && !(selectedElement instanceof GraphTransitionInterface)) {
                     overviewElements.add(selectedElement);
                  }

                  if (selectedElement != null) {
                     lastSelected = selectedElement;
                     selectedElements.add(selectedElement);
                  }
               }

               selectedGraph.selectElements(selectedElements.toArray(), true, true);
               if (overview != null)
                  overview.getGraph().selectElements(overviewElements.toArray(),
                                                     true,
                                                     true);
            }

            if (lastSelected != null) {
               selectedGraph.scrollCellToVisible(lastSelected);
            }

         }
         // ********************************* END OF SELECTION EVENT
      } else {
         if (changedElement instanceof Package
             && (action == XMLElementChangeInfo.INSERTED || action == XMLElementChangeInfo.REMOVED)) {
            List toHandle = new ArrayList();
            if (changedElements != null) {
               toHandle.addAll(changedElements);
            }
            if (changedElements.size() == 0) {
               toHandle.add(changedElement);
            }
            Package mainPkg = JaWEManager.getInstance()
               .getJaWEController()
               .getMainPackage();
            boolean adjustOtherPkgs = mainPkg != null && !(toHandle.contains(mainPkg));

            for (int i = 0; i < changedElements.size(); i++) {
               Package pkg = (Package) changedElements.get(i);
               if (action == XMLElementChangeInfo.INSERTED) {
                  insertPackage(pkg);
               } else if (action == XMLElementChangeInfo.REMOVED) {
                  // System.err.println("Removing package "+pkg.getId());
                  removePackage(pkg);
               }
            }

            // System.err.println("ADJ OTHER PKGs="+adjustOtherPkgs);
            if (adjustOtherPkgs) {
               XPDLHandler xpdlh = JaWEManager.getInstance().getXPDLHandler();
               List allPkgs = new ArrayList(xpdlh.getAllPackages());
               allPkgs.removeAll(changedElements);
               Map allExtPkgPars = getAllPackageParticipants(changedElements);
               List allGraphElements = getAllWorkflowProcessesAndActivitySetsForAdjustment(allPkgs);

               // System.err.println("AIPLGPARS="+allExtPkgPars);
               // System.err.println("ALLGETUPD="+allGraphElements);
               Iterator it = allGraphElements.iterator();
               while (it.hasNext()) {
                  XMLCollectionElement wpOrAs = (XMLCollectionElement) it.next();
                  // System.err.println("ADJUSTING WPORAS "+wpOrAs.getId()+" AFTER EPCHANGE");
                  GraphUtilities.adjustWorkflowProcessOrActivitySetOnUndoableChangeEvent(new ArrayList(),
                                                                                         wpOrAs,
                                                                                         allExtPkgPars,
                                                                                         action == XMLElementChangeInfo.INSERTED);
               }
            }
         } else {
            if (action == XMLElementChangeInfo.INSERTED
                || action == XMLElementChangeInfo.REMOVED
                || action == XMLElementChangeInfo.UPDATED) {
               List l = new ArrayList();
               l.add(info);

               GraphUtilities.adjustPackageOnUndoableChangeEvent(l);
            }
         }
      }

      adjustActions();
   }

   protected List getAllWorkflowProcessesAndActivitySetsForAdjustment(List pkgs) {
      List l = new ArrayList();
      Iterator it = pkgs.iterator();
      while (it.hasNext()) {
         Package pkg = (Package) it.next();
         if (pkg.isReadOnly())
            continue;
         l.addAll(pkg.getWorkflowProcesses().toElements());
         Iterator itWPs = pkg.getWorkflowProcesses().toElements().iterator();
         while (itWPs.hasNext()) {
            WorkflowProcess wp = (WorkflowProcess) itWPs.next();
            l.addAll(wp.getActivitySets().toElements());
         }
      }
      return l;
   }

   protected Map getAllPackageParticipants(List pkgs) {
      Map m = new HashMap();
      Iterator it = pkgs.iterator();
      while (it.hasNext()) {
         Package pkg = (Package) it.next();
         Iterator itPars = pkg.getParticipants().toElements().iterator();
         while (itPars.hasNext()) {
            Participant par = (Participant) itPars.next();
            String parId = par.getId();
            if (!m.containsKey(parId)) {
               m.put(parId, par);
            }
         }
      }
      return m;
   }

   protected GraphControllerPanel createPanel() {
      panel.configure();
      panel.init();
      return panel;
   }

   public GraphMarqueeHandler getGraphMarqueeHandler() {
      return graphMarqueeHandler;
   }

   public GraphObjectFactory getGraphObjectFactory() {
      return graphObjectFactory;
   }

   public GraphObjectRendererFactory getGraphObjectRendererFactory() {
      return graphObjectRendererFactory;
   }

   public XMLCollectionElement getDisplayedXPDLObject() {
      if (selectedGraph != null)
         return selectedGraph.getXPDLObject();

      return null;
   }

   public void createGraph(WorkflowProcess wp) {
      if (graphMap.get(wp) != null)
         return;
      Graph g = new Graph(this, new JaWEGraphModel(), graphMarqueeHandler, wp);
      g.addGraphSelectionListener(this);
      graphMap.put(wp, g);
      g.clearSelection();
      ToolTipManager.sharedInstance().registerComponent(g);
      // selectedGraph=g;
      // panel.graphAdded(g);
   }

   public void createGraph(ActivitySet as) {
      if (graphMap.get(as) != null)
         return;
      Graph g = new Graph(this, new JaWEGraphModel(), graphMarqueeHandler, as);
      g.addGraphSelectionListener(this);
      graphMap.put(as, g);
      g.clearSelection();
      ToolTipManager.sharedInstance().registerComponent(g);
      // selectedGraph=g;
      // panel.graphSelected(selectedGraph);
   }

   // TODO: handle exception
   protected Graph createGraph() {
      try {
         String gmhc = settings.getGraphClass();
         Constructor c = Class.forName(gmhc).getConstructor(new Class[] {
               this.getClass(), JaWEGraphModel.class, GraphMarqueeHandler.class
         });
         return (Graph) c.newInstance(new Object[] {
               this, createGraphModel(), graphMarqueeHandler
         });
      } catch (Exception ex) {
         return null;
      }
   }

   protected JaWEGraphModel createGraphModel() {
      try {
         String gm = settings.getGraphModelClass();
         return (JaWEGraphModel) Class.forName(gm).newInstance();
      } catch (Exception ex) {
         return null;
      }
   }

   public Graph getSelectedGraph() {
      return selectedGraph;
   }

   public Graph getGraph(XMLCollectionElement wpOrAs) {
      return (Graph) graphMap.get(wpOrAs);
   }

   protected void setSelectedGraph(Graph graph) {
      Graph current = selectedGraph;
      if (selectedGraph != null)
         selectedGraph.clearSelection();
      selectedGraph = graph;
      panel.graphSelected(selectedGraph);
      if (selectedGraph != null) {
         getGraphMarqueeHandler().setSelectionMode();
      }
      if (overview != null) {
         overview.displayGraph();
      }

      if (hm != null) {
         hm.addToHistory((current != null) ? current.getXPDLObject() : null,
                         (graph != null) ? graph.getXPDLObject() : null);
      }
      adjustActions();
   }

   public void selectGraphForElement(XMLCollectionElement cel) {
      Graph current = selectedGraph;
      Graph graph = (Graph) graphMap.get(cel);
      selectedGraph = graph;
      panel.graphSelected(selectedGraph);
      if (selectedGraph != null) {
         JaWEManager.getInstance()
            .getJaWEController()
            .getSelectionManager()
            .setSelection(selectedGraph.getXPDLObject(), true);

         getGraphMarqueeHandler().setSelectionMode();

         if (overview != null) {
            overview.displayGraph();
         }

      }
      if (hm != null) {
         hm.addToHistory((current != null) ? current.getXPDLObject() : null,
                         (graph != null) ? graph.getXPDLObject() : null);
      }
      adjustActions();
   }

   protected void removeGraph(XMLCollectionElement wpOrAs) {
      Graph g = (Graph) graphMap.remove(wpOrAs);
      // System.out.println("Graph "+g+" for "+wpOrAs.getId()+" will be removed!");
      ToolTipManager.sharedInstance().unregisterComponent(g);
      g.removeGraphSelectionListener(this);

      // g.clearSelection();
      // Object[] elem = JaWEGraphModel.getAll(g.getModel());
      // g.getModel().remove(elem);
      // g.getGraphLayoutCache().removeCells(elem);

      if (selectedGraph == g) {
         selectedGraph = null;
         panel.graphSelected(selectedGraph);
      }

      g.clearXPDLObjectReferences();
      if (copyOrCutInfo != null) {
         copyOrCutInfo.removeGraphInfo(g);
      }
      if (overview != null) {
         overview.displayGraph();
      }

      if (hm != null) {
         hm.removeFromHistory(wpOrAs);
      }
      adjustActions();
   }

   public void displayPreviousGraph() {
      if (hm != null && hm.canGoBack()) {
         XMLCollectionElement el = (XMLCollectionElement) hm.getPrevious((selectedGraph != null) ? selectedGraph.getXPDLObject()
                                                                                                : null);
         Graph g = getGraph(el);
         if (g != selectedGraph) {
            selectedGraph = g;
            panel.graphSelected(selectedGraph);
            if (selectedGraph != null) {
               getGraphMarqueeHandler().setSelectionMode();
            }
            if (overview != null) {
               overview.displayGraph();
            }

            if (g != null) {
               JaWEManager.getInstance()
                  .getJaWEController()
                  .getSelectionManager()
                  .setSelection(g.getXPDLObject(), true);
            }
         }
      }
      adjustActions();
   }

   public void displayNextGraph() {
      if (hm != null && hm.canGoForward()) {
         XMLCollectionElement el = (XMLCollectionElement) hm.getNext((selectedGraph != null) ? selectedGraph.getXPDLObject()
                                                                                            : null);
         Graph g = getGraph(el);
         if (g != selectedGraph) {
            selectedGraph = g;
            panel.graphSelected(selectedGraph);
            if (selectedGraph != null) {
               getGraphMarqueeHandler().setSelectionMode();
            }
            if (overview != null) {
               overview.displayGraph();
            }

            if (g != null) {
               JaWEManager.getInstance()
                  .getJaWEController()
                  .getSelectionManager()
                  .setSelection(g.getXPDLObject(), true);
            }
         }
      }
      adjustActions();
   }

   protected void insertPackage(Package pkg) {
      Iterator it = pkg.getWorkflowProcesses().toElements().iterator();
      boolean isRO = pkg.isReadOnly();
      while (it.hasNext()) {
         WorkflowProcess wp = (WorkflowProcess) it.next();
         createGraph(wp);
         if (settings.performAutomaticLayoutOnInsertion()) {

            if (isRO) {
               wp.setReadOnly(false);
            }
            SimpleGraphLayout.layoutGraph(this, getGraph(wp));
            if (isRO) {
               wp.setReadOnly(true);
            }
         }
         Iterator asi = wp.getActivitySets().toElements().iterator();
         while (asi.hasNext()) {
            ActivitySet as = (ActivitySet) asi.next();
            createGraph(as);
            if (settings.performAutomaticLayoutOnInsertion()) {
               if (isRO) {
                  wp.setReadOnly(false);
               }
               SimpleGraphLayout.layoutGraph(this, getGraph(as));
               if (isRO) {
                  wp.setReadOnly(true);
               }
            }
         }
      }
   }

   protected void removePackage(Package pkg) {
      Iterator it = pkg.getWorkflowProcesses().toElements().iterator();
      while (it.hasNext()) {
         WorkflowProcess wp = (WorkflowProcess) it.next();
         removeGraph(wp);
         Iterator asi = wp.getActivitySets().toElements().iterator();
         while (asi.hasNext()) {
            ActivitySet as = (ActivitySet) asi.next();
            removeGraph(as);
         }
      }
   }

   protected XPDLElementChangeInfo createInfo(XMLElement main, List elements, int action) {
      XPDLElementChangeInfo info = new XPDLElementChangeInfo();
      info.setChangedElement(main);
      if (elements.size() != 0)
         info.setChangedElement((XMLElement) elements.iterator().next());
      info.setChangedSubElements(elements);
      info.setAction(action);
      info.setSource(this);
      return info;
   }

   public void setUpdateInProgress(boolean isInProgress) {
      updateInProgress = isInProgress;
   }

   public boolean isUpdateInProgress() {
      return updateInProgress;
   }

   public CopyOrCutInfo getCopyOrCutInfo() {
      return copyOrCutInfo;
   }

   public GraphOverview getOverview() {
      return overview;
   }

   public HistoryManager getHistoryManager() {
      return hm;
   }

   public void adjustActions() {
      settings.adjustActions();
      panel.enableDisableButtons();
   }
}
