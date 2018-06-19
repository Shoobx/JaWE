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

/**
 * Miroslav Popov, Sep 1, 2005
 */
package org.enhydra.jawe.base.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.XPDLElementChangeInfo;
import org.enhydra.jawe.base.xpdlhandler.XPDLHandler;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.Activities;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ActivitySet;
import org.enhydra.jxpdl.elements.ActivitySets;
import org.enhydra.jxpdl.elements.Application;
import org.enhydra.jxpdl.elements.Artifact;
import org.enhydra.jxpdl.elements.Artifacts;
import org.enhydra.jxpdl.elements.Association;
import org.enhydra.jxpdl.elements.Associations;
import org.enhydra.jxpdl.elements.BlockActivity;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.Lane;
import org.enhydra.jxpdl.elements.Lanes;
import org.enhydra.jxpdl.elements.NestedLane;
import org.enhydra.jxpdl.elements.NodeGraphicsInfo;
import org.enhydra.jxpdl.elements.Package;
import org.enhydra.jxpdl.elements.Participant;
import org.enhydra.jxpdl.elements.Pool;
import org.enhydra.jxpdl.elements.Pools;
import org.enhydra.jxpdl.elements.Transition;
import org.enhydra.jxpdl.elements.Transitions;
import org.enhydra.jxpdl.elements.WorkflowProcess;
import org.enhydra.jxpdl.elements.WorkflowProcesses;

/**
 * @author Miroslav Popov
 * @author Sasa Bojanic
 */
public class JaWEEdit {

   protected List clipboard = new ArrayList();

   protected boolean isPasteInProgress = false;

   public void copy() {
      copyOrCut(false);
   }

   public void cut() {
      copyOrCut(true);
   }

   protected void copyOrCut(boolean isCut) {
      clipboard = new ArrayList();
      JaWEController jc = JaWEManager.getInstance().getJaWEController();
      if (jc.getSelectionManager().getSelectedElements() != null) {
         clipboard.addAll(jc.getSelectionManager().getSelectedElements());

         XMLElement firstEl = (XMLElement) clipboard.get(0);
         XMLElement selOwner = firstEl.getParent();
         if (selOwner == null) {
            selOwner = firstEl;
         }
         XPDLElementChangeInfo info = jc.createInfo(selOwner,
                                                    clipboard,
                                                    isCut ? XPDLElementChangeInfo.CUT
                                                         : XPDLElementChangeInfo.COPY);

         jc.sendEvent(info);
         prepareForPaste();
         if (isCut) {
            delete();
         }
      }
      JaWEManager.getInstance().getJaWEController().adjustActions();
   }

   public void duplicate() {
      JaWEController jc = JaWEManager.getInstance().getJaWEController();

      XMLElement el = jc.getSelectionManager().getSelectedElement();
      if (el.getParent() instanceof XMLCollection) {
         XMLCollection col = (XMLCollection) el.getParent();
         XMLElement cloned = JaWEManager.getInstance()
            .getXPDLObjectFactory()
            .duplicateXPDLObject(col, el);
         List pools2Add = new ArrayList();
         List arts = new ArrayList();
         List assocs = new ArrayList();
         if (cloned instanceof ActivitySet || cloned instanceof WorkflowProcess) {
            Map wpasPoolMapping = new HashMap();
            prepareWorkflowProcessOrActivitySetForDuplicate((XMLCollectionElement) el,
                                                            (XMLCollectionElement) cloned,
                                                            arts,
                                                            assocs,
                                                            wpasPoolMapping,
                                                            pools2Add);
            duplicateWorkflowProcessOrActivitySetContext((XMLCollectionElement) cloned,
                                                         wpasPoolMapping,
                                                         arts,
                                                         assocs);
            if (cloned instanceof WorkflowProcess) {
               Iterator asi = ((WorkflowProcess) cloned).getActivitySets()
                  .toElements()
                  .iterator();
               while (asi.hasNext()) {
                  ActivitySet as = (ActivitySet) asi.next();
                  duplicateWorkflowProcessOrActivitySetContext(as,
                                                               wpasPoolMapping,
                                                               arts,
                                                               assocs);
               }

            }

         }
         jc.startUndouableChange();
         if (pools2Add.size() > 0) {
            Pools ps = XMLUtil.getPackage(el).getPools();
            for (int i = 0; i < pools2Add.size(); i++) {
               ps.add((Pool) pools2Add.get(i));
            }
         }

         Artifacts ac = XMLUtil.getPackage(el).getArtifacts();
         for (int i = 0; i < arts.size(); i++) {
            ac.add((XMLElement) arts.get(i));
         }
         Associations asc = XMLUtil.getPackage(el).getAssociations();
         for (int i = 0; i < assocs.size(); i++) {
            asc.add((XMLElement) assocs.get(i));
         }

         col.add(cloned);
         List toSelect = new ArrayList();
         toSelect.add(cloned);
         jc.endUndouableChange(toSelect);
      }
   }

   public void references() {
      JaWEController jc = JaWEManager.getInstance().getJaWEController();
      XMLComplexElement el = (XMLComplexElement) jc.getSelectionManager()
         .getSelectedElement();
      XMLComplexElement pkgOrWPOrEAsParent = XMLUtil.getPackage(el);
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(el);
      if (wp != null && wp != el) {
         pkgOrWPOrEAsParent = wp;
      }
      if (el instanceof ExtendedAttribute) {
         pkgOrWPOrEAsParent = (XMLComplexElement) el.getParent().getParent();
      }

      XPDLElementChangeInfo info = null;
      if (el instanceof Activity) {
         info = jc.createInfo(el,
                              Utils.makeSearchResultList(JaWEManager.getInstance()
                                 .getXPDLUtils()
                                 .getReferences((Activity) el)),
                              XPDLElementChangeInfo.REFERENCES);
      } else if (el instanceof Transition) {
         info = jc.createInfo(el,
                              Utils.makeSearchResultList(JaWEManager.getInstance()
                                 .getXPDLUtils()
                                 .getReferences((Transition) el)),
                              XPDLElementChangeInfo.REFERENCES);
      } else if (el instanceof Lane) {
         info = jc.createInfo(el,
                              Utils.makeSearchResultList(JaWEManager.getInstance()
                                 .getXPDLUtils()
                                 .getReferences(pkgOrWPOrEAsParent, el)),
                              XPDLElementChangeInfo.REFERENCES);
      } else if (el instanceof Package) {
         info = jc.createInfo(el,
                              Utils.makeSearchResultList(JaWEManager.getInstance()
                                 .getXPDLUtils()
                                 .getReferences((Package) el)),
                              XPDLElementChangeInfo.REFERENCES);
      } else {
         if (el instanceof WorkflowProcess
             || el instanceof Application || el instanceof Participant) {
            if (pkgOrWPOrEAsParent instanceof Package) {
               info = jc.createInfo(el,
                                    getAllReferences(el),
                                    XPDLElementChangeInfo.REFERENCES);
            } else {
               info = jc.createInfo(el,
                                    Utils.makeSearchResultList(JaWEManager.getInstance()
                                       .getXPDLUtils()
                                       .getReferences(pkgOrWPOrEAsParent, el)),
                                    XPDLElementChangeInfo.REFERENCES);
            }
         } else {
            info = jc.createInfo(el,
                                 Utils.makeSearchResultList(JaWEManager.getInstance()
                                    .getXPDLUtils()
                                    .getReferences(pkgOrWPOrEAsParent, el)),
                                 XPDLElementChangeInfo.REFERENCES);
         }
      }

      if (info != null) {
         jc.sendEvent(info);
      }
   }

   public void paste() {
      JaWEManager.getInstance().getLoggingManager().debug("PASTE started");
      isPasteInProgress = true;
      JaWEController jc = JaWEManager.getInstance().getJaWEController();
      XMLElement firstSelected = jc.getSelectionManager().getSelectedElement();
      jc.startUndouableChange();

      Iterator it = clipboard.iterator();
      Transitions transCollection = null;
      Activities actsCollection = null;
      Associations assocsCollection = null;
      Artifacts artsCollection = null;
      Pools poolsCollection = null;
      WorkflowProcesses wpCollection = null;
      ActivitySets asCollection = null;
      XMLCollection destCollection = null;
      List duplicatedElements = new ArrayList();
      Map actIdMappings = new HashMap();
      List acts = new ArrayList();
      List trans = new ArrayList();
      List arts = new ArrayList();
      Map artIdMappings = new HashMap();
      List assocs = new ArrayList();
      Set skipIds = new HashSet();
      Set skipActIds = new HashSet();
      Set skipTransIds = new HashSet();
      Set skipArtIds = new HashSet();
      Set skipAsocsIds = new HashSet();
      Set pools = new HashSet();
      Set skipPoolsIds = new HashSet();
      Map wpIdMapping = new HashMap();
      Map asIdMapping = new HashMap();
      Map wpasPoolMapping = new HashMap();
      while (it.hasNext()) {
         XMLElement copied = (XMLElement) it.next();
         if (copied instanceof Pool) {
            pools.add(copied);
            poolsCollection = XMLUtil.getPackage(firstSelected).getPools();
            continue;
         }
         destCollection = null;
         XMLCollection parent = (XMLCollection) copied.getParent();
         // System.err.println("Searching for dest collection for el
         // "+copied.getClass()+", firstSelected="+firstSelected.getClass());
         if (copied instanceof Artifact) {
            destCollection = artsCollection = XMLUtil.getPackage(firstSelected)
               .getArtifacts();
         } else if (copied instanceof Association) {
            destCollection = assocsCollection = XMLUtil.getPackage(firstSelected)
               .getAssociations();
         } else if (copied instanceof WorkflowProcess) {
            destCollection = wpCollection = XMLUtil.getPackage(firstSelected)
               .getWorkflowProcesses();
         } else if (copied instanceof ActivitySet) {
            destCollection = asCollection = XMLUtil.getWorkflowProcess(firstSelected)
               .getActivitySets();
         } else if (parent.getClass() == firstSelected.getClass()) {
            // System.err.println("Dest coll found -> 1");
            destCollection = (XMLCollection) firstSelected;
         } else if (firstSelected.getClass() == copied.getClass()) {
            destCollection = (XMLCollection) firstSelected.getParent();
            // System.err.println("Dest coll found -> 2");
         } else {
            List subEls = ((XMLComplexElement) firstSelected).toElements();
            for (int i = 0; i < subEls.size(); i++) {
               if (subEls.get(i).getClass() == parent.getClass()) {
                  destCollection = (XMLCollection) subEls.get(i);
                  // System.err.println("Dest coll found -> 3");
                  break;
               }
            }
            if (destCollection == null) {
               if (firstSelected instanceof Activity) {
                  destCollection = (Transitions) ((XMLComplexElement) firstSelected.getParent()
                     .getParent()).get("Transitions");
                  // System.err.println("Dest coll found -> 4");
               } else if (firstSelected instanceof Transition) {
                  destCollection = (Activities) ((XMLComplexElement) firstSelected.getParent()
                     .getParent()).get("Activities");
                  // System.err.println("Dest coll found -> 5");
               }
            }
         }
         // System.err.println("Dest collection="+destCollection);
         if (destCollection instanceof Activities) {
            actsCollection = (Activities) destCollection;
         } else if (destCollection instanceof Transitions) {
            transCollection = (Transitions) destCollection;
         }
         XMLElement twin = JaWEManager.getInstance()
            .getXPDLObjectFactory()
            .makeIdenticalXPDLObject(parent, copied);
         if (twin instanceof Activity) {
            acts.add(twin);
         } else if (twin instanceof Transition) {
            trans.add(twin);
         } else if (twin instanceof Artifact) {
            arts.add(twin);
         } else if (twin instanceof Association) {
            assocs.add(twin);
         }
         if (twin instanceof XMLCollectionElement) {
            XMLCollectionElement cel = (XMLCollectionElement) twin;
            String oldid = cel.getId();
            Set sids = null;
            XMLCollection destCol = destCollection;
            if (cel instanceof Activity) {
               sids = skipActIds;
               destCol = actsCollection;
            } else if (cel instanceof Transition) {
               sids = skipTransIds;
               destCol = transCollection;
            } else if (cel instanceof Artifact) {
               sids = skipArtIds;
               destCol = artsCollection;
            } else if (cel instanceof Association) {
               sids = skipArtIds;
               destCol = assocsCollection;
            } else {
               sids = skipIds;
            }
            String id = JaWEManager.getInstance()
               .getIdFactory()
               .generateSimilarOrIdenticalUniqueId(destCol, sids, oldid);
            // if (!oldid.equals(id)) {
            if (cel instanceof WorkflowProcess) {
               wpIdMapping.put(oldid, id);
            }
            if (cel instanceof ActivitySet) {
               asIdMapping.put(oldid, id);
            }
            if (cel instanceof Activity) {
               actIdMappings.put(oldid, cel);
            }
            if (cel instanceof Artifact) {
               artIdMappings.put(oldid, cel);
            }
            cel.setId(id);
            sids.add(id);
            // }
         }
         duplicatedElements.add(twin);
      }

      it = pools.iterator();
      while (it.hasNext()) {
         Pool p = (Pool) it.next();
         Pools ps = (Pools) p.getParent();
         String oldid = p.getId();
         Pool twin = (Pool) JaWEManager.getInstance()
            .getXPDLObjectFactory()
            .makeIdenticalXPDLObject(ps, p);
         String id = JaWEManager.getInstance()
            .getIdFactory()
            .generateSimilarOrIdenticalUniqueId(poolsCollection, skipIds, oldid);
         twin.setParent(poolsCollection);
         twin.setId(id);
         skipIds.add(id);
         String newId = (String) wpIdMapping.get(twin.getProcess());
         if (newId != null) {
            twin.setProcess(newId);
         } else {
            newId = (String) asIdMapping.get(twin.getProcess());
            if (newId != null) {
               twin.setProcess(newId);
            } else {
               newId = twin.getProcess();
            }
         }
         duplicatedElements.add(twin);
         wpasPoolMapping.put(newId, twin);
      }

      it = trans.iterator();
      while (it.hasNext()) {
         Transition t = (Transition) it.next();
         if (actIdMappings.containsKey(t.getFrom())) {
            Activity act = (Activity) actIdMappings.get(t.getFrom());
            t.setFrom(act.getId());
         }
         if (actIdMappings.containsKey(t.getTo())) {
            Activity act = (Activity) actIdMappings.get(t.getTo());
            t.setTo(act.getId());
         }
      }

      it = assocs.iterator();
      while (it.hasNext()) {
         Association a = (Association) it.next();
         if (artIdMappings.containsKey(a.getSource())) {
            Artifact art = (Artifact) artIdMappings.get(a.getSource());
            a.setSource(art.getId());
         } else if (actIdMappings.containsKey(a.getSource())) {
            Activity act = (Activity) actIdMappings.get(a.getSource());
            a.setSource(act.getId());
         }
         if (artIdMappings.containsKey(a.getTarget())) {
            Artifact art = (Artifact) artIdMappings.get(a.getTarget());
            a.setTarget(art.getId());
         } else if (actIdMappings.containsKey(a.getTarget())) {
            Activity act = (Activity) actIdMappings.get(a.getTarget());
            a.setTarget(act.getId());
         }
      }

      it = duplicatedElements.iterator();
      while (it.hasNext()) {
         XMLElement el = (XMLElement) it.next();
         if (el instanceof ActivitySet || el instanceof WorkflowProcess) {
            el.setParent(destCollection);
            duplicateWorkflowProcessOrActivitySetContext((XMLCollectionElement) el,
                                                         wpasPoolMapping,
                                                         arts,
                                                         assocs);
            if (el instanceof WorkflowProcess) {
               Iterator asi = ((WorkflowProcess) el).getActivitySets()
                  .toElements()
                  .iterator();
               while (asi.hasNext()) {
                  ActivitySet as = (ActivitySet) asi.next();
                  duplicateWorkflowProcessOrActivitySetContext(as,
                                                               wpasPoolMapping,
                                                               arts,
                                                               assocs);
               }

            }
         }
      }

      it = duplicatedElements.iterator();
      while (it.hasNext()) {
         XMLElement duplicated = (XMLElement) it.next();
         if (!(duplicated instanceof Transition)) {
            if (duplicated instanceof Activity) {
               actsCollection.add(duplicated);
            } else if (duplicated instanceof Artifact) {
               artsCollection.add(duplicated);
            } else if (duplicated instanceof Association) {
               assocsCollection.add(duplicated);
            } else if (duplicated instanceof Pool) {
               poolsCollection.add(duplicated);
            } else if (duplicated instanceof WorkflowProcess) {
               wpCollection.add(duplicated);
            } else if (duplicated instanceof ActivitySet) {
               asCollection.add(duplicated);
            } else {
               destCollection.add(duplicated);
            }
         }
      }
      it = duplicatedElements.iterator();
      while (it.hasNext()) {
         XMLElement duplicated = (XMLElement) it.next();
         if (duplicated instanceof Transition) {
            transCollection.add(duplicated);
         }
      }

      it = acts.iterator();
      while (it.hasNext()) {
         XMLElement duplicated = (XMLElement) it.next();
         if (duplicated instanceof Activity) {
            JaWEManager.getInstance()
               .getXPDLUtils()
               .correctSplitAndJoin((Activity) duplicated);
         }
      }

      JaWEManager.getInstance().getLoggingManager().debug("Paste ended");
      jc.endUndouableChange(new ArrayList(duplicatedElements));
      isPasteInProgress = false;

   }

   public void delete() {
      JaWEController jc = JaWEManager.getInstance().getJaWEController();
      jc.startUndouableChange();
      XMLCollection toSel = null;
      int lastElementIndex = 0;
      List sels = jc.getSelectionManager().getSelectedElements();
      for (int i = 0; i < sels.size(); i++) {
         XMLElement el = (XMLElement) sels.get(i);
         toSel = (XMLCollection) el.getParent();
         lastElementIndex = toSel.indexOf(el);
         toSel.remove(el);
      }

      List toSelect = new ArrayList();
      if (toSel.size() == 0)
         toSelect.add(toSel);
      else {
         if (toSel.size() <= lastElementIndex || lastElementIndex < 0) {
            toSelect.add(toSel.get(toSel.size() - 1));
         } else {
            toSelect.add(toSel.get(lastElementIndex));
         }
      }

      jc.endUndouableChange(toSelect);
   }

   protected void prepareForPaste() {
      Iterator it = clipboard.iterator();
      List twinElements = new ArrayList();
      while (it.hasNext()) {
         XMLElement copied = (XMLElement) it.next();
         XMLCollection parent = (XMLCollection) copied.getParent();
         XMLElement twin = JaWEManager.getInstance()
            .getXPDLObjectFactory()
            .makeIdenticalXPDLObject(parent, copied);
         twin.setReadOnly(false);
         twinElements.add(twin);
         if (twin instanceof WorkflowProcess || twin instanceof ActivitySet) {
            XMLCollectionElement wpOrAs = (XMLCollectionElement) twin;
            prepareWorkflowProcessOrActivitySetForPaste(wpOrAs, twinElements);
         }
      }
      clipboard = new ArrayList(twinElements);
   }

   protected void prepareWorkflowProcessOrActivitySetForDuplicate(XMLCollectionElement wpOrAs,
                                                                  XMLCollectionElement cloned,
                                                                  List arts,
                                                                  List assocs,
                                                                  Map wpasPoolMapping,
                                                                  List pools2Add) {
      Pool p = XMLUtil.getPoolForProcessOrActivitySet(wpOrAs);
      Pools ps = (Pools) p.getParent();
      String oldid = p.getId();
      Pool twin = (Pool) JaWEManager.getInstance()
         .getXPDLObjectFactory()
         .makeIdenticalXPDLObject(ps, p);
      String id = JaWEManager.getInstance()
         .getIdFactory()
         .generateSimilarOrIdenticalUniqueId(ps, new HashSet(), oldid);
      twin.setId(id);
      String newId = ((XMLCollectionElement) cloned).getId();
      if (newId != null) {
         twin.setProcess(newId);
      }
      List artsAndAssocs = XMLUtil.getAllArtifactsAndAssociationsForWorkflowProcessOrActivitySet(wpOrAs);
      Set sids = new HashSet();
      Map artIdMappings = new HashMap();
      for (int i = 0; i < artsAndAssocs.size(); i++) {
         XMLCollectionElement artOrAsoc = (XMLCollectionElement) artsAndAssocs.get(i);
         XMLCollection parent = (XMLCollection) artOrAsoc.getParent();
         String oldaid = artOrAsoc.getId();
         XMLCollectionElement atwin = (XMLCollectionElement) JaWEManager.getInstance()
            .getXPDLObjectFactory()
            .makeIdenticalXPDLObject(parent, artOrAsoc);
         String aid = JaWEManager.getInstance()
            .getIdFactory()
            .generateSimilarOrIdenticalUniqueId((XMLCollection) artOrAsoc.getParent(),
                                                sids,
                                                oldaid);
         sids.add(aid);
         atwin.setId(aid);
         atwin.setReadOnly(false);
         if (atwin instanceof Artifact) {
            arts.add(atwin);
            artIdMappings.put(oldaid, (Artifact) atwin);
         }
         if (atwin instanceof Association) {
            assocs.add(atwin);
         }
      }
      for (int i = 0; i < assocs.size(); i++) {
         Association a = (Association) assocs.get(i);
         if (artIdMappings.containsKey(a.getSource())) {
            Artifact art = (Artifact) artIdMappings.get(a.getSource());
            a.setSource(art.getId());
         }
         if (artIdMappings.containsKey(a.getTarget())) {
            Artifact art = (Artifact) artIdMappings.get(a.getTarget());
            a.setTarget(art.getId());
         }
      }

      wpasPoolMapping.put(cloned.getId(), twin);
      if (wpOrAs instanceof WorkflowProcess) {
         Iterator asi = ((WorkflowProcess) wpOrAs).getActivitySets()
            .toElements()
            .iterator();
         while (asi.hasNext()) {
            ActivitySet as = (ActivitySet) asi.next();
            prepareWorkflowProcessOrActivitySetForDuplicate(as,
                                                            as,
                                                            arts,
                                                            assocs,
                                                            wpasPoolMapping,
                                                            pools2Add);
         }
      }
      pools2Add.add(twin);
   }

   protected void prepareWorkflowProcessOrActivitySetForPaste(XMLCollectionElement wpOrAs,
                                                              List twinElements) {
      Pool p = JaWEManager.getInstance()
         .getXPDLUtils()
         .getPoolForProcessOrActivitySet(wpOrAs);
      XMLCollection parent = (XMLCollection) p.getParent();
      XMLElement twin = JaWEManager.getInstance()
         .getXPDLObjectFactory()
         .makeIdenticalXPDLObject(parent, p);
      twin.setReadOnly(false);
      ((Pool) twin).setProcess(wpOrAs.getId());
      twinElements.add(twin);
      List artsAndAssocs = XMLUtil.getAllArtifactsAndAssociationsForWorkflowProcessOrActivitySet(wpOrAs);
      for (int i = 0; i < artsAndAssocs.size(); i++) {
         XMLCollectionElement artOrAsoc = (XMLCollectionElement) artsAndAssocs.get(i);
         parent = (XMLCollection) artOrAsoc.getParent();
         twin = JaWEManager.getInstance()
            .getXPDLObjectFactory()
            .makeIdenticalXPDLObject(parent, artOrAsoc);
         twin.setReadOnly(false);
         twinElements.add(twin);
      }
      if (wpOrAs instanceof WorkflowProcess) {
         Iterator asi = ((WorkflowProcess) wpOrAs).getActivitySets()
            .toElements()
            .iterator();
         while (asi.hasNext()) {
            ActivitySet as = (ActivitySet) asi.next();
            prepareWorkflowProcessOrActivitySetForPaste(as, twinElements);
         }
      }
   }

   public void clear() {
      clipboard.clear();
   }

   public List getClipboard() {
      return clipboard;
   }

   public boolean isPasteInProgress() {
      return isPasteInProgress;
   }

   public List getAllReferences(XMLComplexElement referencedWpOrParOrApp) {
      List references = new ArrayList();

      XPDLHandler xpdlh = JaWEManager.getInstance().getXPDLHandler();
      Iterator it = xpdlh.getAllPackages().iterator();
      while (it.hasNext()) {
         Package pkg = (Package) it.next();
         references.addAll(Utils.makeSearchResultList(JaWEManager.getInstance()
            .getXPDLUtils()
            .getReferences(pkg, referencedWpOrParOrApp)));
      }

      return references;
   }

   protected List duplicateWorkflowProcessOrActivitySetContext(XMLCollectionElement wpOrAs,
                                                               Map wpasPoolMapping,
                                                               List duplArtifacts,
                                                               List duplAssocs) {
      List duplicates = new ArrayList();
      Pool p = (Pool) wpasPoolMapping.get(((XMLCollectionElement) wpOrAs).getId());
      Set skipActIds = new HashSet();
      Set skipTransIds = new HashSet();

      Lanes ls = p.getLanes();
      Iterator it = ls.toElements().iterator();
      Map lnIdMapping = new HashMap();
      List asocs = new ArrayList();
      while (it.hasNext()) {
         Lane ln = (Lane) it.next();
         String oldId = ln.getId();
         String id = JaWEManager.getInstance()
            .getIdFactory()
            .generateSimilarOrIdenticalUniqueId(ls, new HashSet(), oldId);
         List refs = XMLUtil.tGetLaneReferences(XMLUtil.getPackage(wpOrAs), oldId);
         refs.addAll(XMLUtil.tGetLaneReferences(wpOrAs, oldId));
         Iterator li = p.getLanes().toElements().iterator();
         while (li.hasNext()) {
            Lane l = (Lane) li.next();
            if (l != ln) {
               Iterator nli = l.getNestedLanes().toElements().iterator();
               while (nli.hasNext()) {
                  NestedLane nl = (NestedLane) nli.next();
                  if (nl.getLaneId().equals(oldId)) {
                     refs.add(nl);
                  }
               }
            }
         }
         ln.setId(id);
         lnIdMapping.put(oldId, id);
         for (int i = 0; i < refs.size(); i++) {
            XMLComplexElement ref = (XMLComplexElement) refs.get(i);
            if (XMLUtil.getArtifact(ref) == null) {
               XMLElement pl = XMLUtil.getPool(ref);
               if (ref instanceof NestedLane && pl != p)
                  continue;
               ref.set("LaneId", id);
            }
         }
      }
      it = duplArtifacts.iterator();
      while (it.hasNext()) {
         Artifact a = (Artifact) it.next();
         Iterator ngit = a.getNodeGraphicsInfos().toElements().iterator();
         while (ngit.hasNext()) {
            NodeGraphicsInfo ngi = (NodeGraphicsInfo) ngit.next();
            String mappedId = (String) lnIdMapping.get(ngi.getLaneId());
            if (mappedId != null) {
               ngi.setLaneId(mappedId);
            }
         }
      }

      Activities acts = ((Activities) wpOrAs.get("Activities"));
      Transitions tras = ((Transitions) wpOrAs.get("Transitions"));
      Map actIdMappings = new HashMap();
      List toIterate = new ArrayList(acts.toElements());
      toIterate.addAll(tras.toElements());
      Iterator itat = toIterate.iterator();
      while (itat.hasNext()) {
         XMLCollectionElement actOrTrans = (XMLCollectionElement) itat.next();
         String oldid = actOrTrans.getId();
         Set sids = null;
         if (actOrTrans instanceof Activity) {
            sids = skipActIds;
         } else {
            sids = skipTransIds;
         }
         String id = JaWEManager.getInstance()
            .getIdFactory()
            .generateSimilarOrIdenticalUniqueId((XMLCollection) actOrTrans.getParent(),
                                                sids,
                                                oldid);
         if (!oldid.equals(id)) {
            if (actOrTrans instanceof Activity) {
               actIdMappings.put(oldid, actOrTrans);
            }
            actOrTrans.setId(id);
            sids.add(id);
         }
      }
      it = tras.toElements().iterator();
      while (it.hasNext()) {
         Transition t = (Transition) it.next();
         if (actIdMappings.containsKey(t.getFrom())) {
            Activity act = (Activity) actIdMappings.get(t.getFrom());
            t.setFrom(act.getId());
         }
         if (actIdMappings.containsKey(t.getTo())) {
            Activity act = (Activity) actIdMappings.get(t.getTo());
            t.setTo(act.getId());
         }
      }
      it = acts.toElements().iterator();
      while (it.hasNext()) {
         JaWEManager.getInstance()
            .getXPDLUtils()
            .correctSplitAndJoin((Activity) it.next());
      }
      it = duplAssocs.iterator();
      while (it.hasNext()) {
         Association a = (Association) it.next();
         if (actIdMappings.containsKey(a.getSource())) {
            Activity act = (Activity) actIdMappings.get(a.getSource());
            a.setSource(act.getId());
         }
         if (actIdMappings.containsKey(a.getTarget())) {
            Activity act = (Activity) actIdMappings.get(a.getTarget());
            a.setTarget(act.getId());
         }
      }

      if (wpOrAs instanceof WorkflowProcess) {
         Iterator asi = ((WorkflowProcess) wpOrAs).getActivitySets()
            .toElements()
            .iterator();
         while (asi.hasNext()) {
            ActivitySet as = (ActivitySet) asi.next();
            List refs = XMLUtil.getActivitySetReferences((WorkflowProcess) wpOrAs,
                                                         as.getId());
            String id = JaWEManager.getInstance()
               .getIdFactory()
               .generateSimilarOrIdenticalUniqueId((ActivitySets) as.getParent(),
                                                   new HashSet(),
                                                   as.getId());
            p = (Pool) wpasPoolMapping.remove(as.getId());
            as.setId(id);
            p.setProcess(id);
            for (int i = 0; i < refs.size(); i++) {
               XMLElement ref = (XMLElement) refs.get(i);
               if (ref instanceof BlockActivity) {
                  ((BlockActivity) ref).setActivitySetId(id);
               }
            }
            wpasPoolMapping.put(id, p);
         }
      }
      return duplicates;
   }

}
