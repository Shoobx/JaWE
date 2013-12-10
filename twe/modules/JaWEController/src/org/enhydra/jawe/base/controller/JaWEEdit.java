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
import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLCollectionElement;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.elements.Activities;
import org.enhydra.shark.xpdl.elements.Activity;
import org.enhydra.shark.xpdl.elements.ActivitySet;
import org.enhydra.shark.xpdl.elements.Application;
import org.enhydra.shark.xpdl.elements.Package;
import org.enhydra.shark.xpdl.elements.Participant;
import org.enhydra.shark.xpdl.elements.Tool;
import org.enhydra.shark.xpdl.elements.Transition;
import org.enhydra.shark.xpdl.elements.Transitions;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;

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
         if (cloned instanceof ActivitySet) {
            duplicateActivitySetContext((ActivitySet) cloned,
                                        new HashSet(),
                                        new HashSet());
         }
         jc.startUndouableChange();
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
      XMLComplexElement pkgOrWP = XMLUtil.getPackage(el);
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(el);
      if (wp != null && wp != el) {
         pkgOrWP = wp;
      }

      XPDLElementChangeInfo info = null;
      if (el instanceof Activity) {
         info = jc.createInfo(el, Utils.makeSearchResultList(JaWEManager.getInstance()
            .getXPDLUtils()
            .getReferences((Activity) el)), XPDLElementChangeInfo.REFERENCES);
      } else if (el instanceof Transition) {
         info = jc.createInfo(el, Utils.makeSearchResultList(JaWEManager.getInstance()
            .getXPDLUtils()
            .getReferences((Transition) el)), XPDLElementChangeInfo.REFERENCES);
      } else if (el instanceof Package) {
         info = jc.createInfo(el, Utils.makeSearchResultList(JaWEManager.getInstance()
            .getXPDLUtils()
            .getReferences((Package) el)), XPDLElementChangeInfo.REFERENCES);
      } else {
         if (el instanceof WorkflowProcess
             || el instanceof Application || el instanceof Participant) {
            if (pkgOrWP instanceof Package) {
               info = jc.createInfo(el,
                                    getAllReferences(el),
                                    XPDLElementChangeInfo.REFERENCES);
            } else {
               info = jc.createInfo(el,
                                    Utils.makeSearchResultList(JaWEManager.getInstance()
                                       .getXPDLUtils()
                                       .getReferences(pkgOrWP, el)),
                                    XPDLElementChangeInfo.REFERENCES);
            }
         } else {
            info = jc.createInfo(el, Utils.makeSearchResultList(JaWEManager.getInstance()
               .getXPDLUtils()
               .getReferences(pkgOrWP, el)), XPDLElementChangeInfo.REFERENCES);
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
      XMLCollection destCollection = null;
      List duplicatedElements = new ArrayList();
      Map actIdMappings = new HashMap();
      List acts = new ArrayList();
      List trans = new ArrayList();
      Set skipIds = new HashSet();
      Set skipActIds = new HashSet();
      Set skipTransIds = new HashSet();
      while (it.hasNext()) {
         destCollection = null;
         XMLElement copied = (XMLElement) it.next();
         XMLCollection parent = (XMLCollection) copied.getParent();
         // System.err.println("Searching for dest collection for el
         // "+copied.getClass()+", firstSelected="+firstSelected.getClass());
         if (parent.getClass() == firstSelected.getClass()) {
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
         }
         if (twin instanceof XMLCollectionElement && !(twin instanceof Tool)) {
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
            } else {
               sids = skipIds;
            }
            String id = JaWEManager.getInstance()
               .getIdFactory()
               .generateSimilarOrIdenticalUniqueId(destCol, sids, oldid);
            if (!oldid.equals(id)) {
               if (cel instanceof Activity) {
                  actIdMappings.put(oldid, cel);
               }
               cel.setId(id);
               sids.add(id);
            }
         }
         duplicatedElements.add(twin);
      }

      it = duplicatedElements.iterator();
      while (it.hasNext()) {
         XMLElement el = (XMLElement) it.next();
         if (el instanceof ActivitySet) {
            ActivitySet as = (ActivitySet) el;
            duplicateActivitySetContext(as, skipActIds, skipTransIds);
         }
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

      it = duplicatedElements.iterator();
      while (it.hasNext()) {
         XMLElement duplicated = (XMLElement) it.next();
         if (!(duplicated instanceof Transition)) {
            if (duplicated instanceof Activity) {
               actsCollection.add(duplicated);
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
      }
      clipboard = new ArrayList(twinElements);
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

   protected void duplicateActivitySetContext(ActivitySet as,
                                              Set skipActIds,
                                              Set skipTransIds) {
      Map actIdMappings = new HashMap();
      List toIterate = new ArrayList(as.getActivities().toElements());
      toIterate.addAll(as.getTransitions().toElements());
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
      Iterator it = as.getTransitions().toElements().iterator();
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
      it = as.getActivities().toElements().iterator();
      while (it.hasNext()) {
         JaWEManager.getInstance()
            .getXPDLUtils()
            .correctSplitAndJoin((Activity) it.next());
      }
   }
}
