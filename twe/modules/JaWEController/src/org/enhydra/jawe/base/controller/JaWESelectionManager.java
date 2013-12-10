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
 * Miroslav Popov, Sep 14, 2005 miroslav.popov@gmail.com
 */
package org.enhydra.jawe.base.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.enhydra.jawe.XPDLElementChangeInfo;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLComplexChoice;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLSimpleElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.Activities;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ActivitySet;
import org.enhydra.jxpdl.elements.Application;
import org.enhydra.jxpdl.elements.Artifact;
import org.enhydra.jxpdl.elements.Artifacts;
import org.enhydra.jxpdl.elements.Association;
import org.enhydra.jxpdl.elements.Associations;
import org.enhydra.jxpdl.elements.DataField;
import org.enhydra.jxpdl.elements.FormalParameter;
import org.enhydra.jxpdl.elements.Lane;
import org.enhydra.jxpdl.elements.Package;
import org.enhydra.jxpdl.elements.Participant;
import org.enhydra.jxpdl.elements.Pool;
import org.enhydra.jxpdl.elements.Transition;
import org.enhydra.jxpdl.elements.TypeDeclaration;
import org.enhydra.jxpdl.elements.WorkflowProcess;
import org.enhydra.jxpdl.elements.WorkflowProcesses;

/**
 * @author Miroslav Popov
 * @author Sasa Bojanic
 */
public class JaWESelectionManager {

   protected JaWEController jc;

   protected List selectedElements = new ArrayList();

   protected Package workingPKG;

   protected WorkflowProcess workingWP;

   protected ActivitySet workingAS;

   public JaWESelectionManager(JaWEController controller) {
      jc = controller;
   }

   public void clear() {
      selectedElements = new ArrayList();
      workingPKG = null;
      workingWP = null;
      workingAS = null;
   }

   public List getSelectedElements() {
      return new ArrayList(selectedElements);
   }

   public XMLElement getSelectedElement() {
      if (selectedElements.size() > 0) {
         return (XMLElement) selectedElements.get(0);
      }
      return null;
   }

   public void setSelection(XMLElement el, boolean setPkgAndWpAndas) {
      selectedElements = new ArrayList();
      if (el != null)
         selectedElements.add(el);

      if (setPkgAndWpAndas)
         setPkgWpAndAs();

      XPDLElementChangeInfo info = jc.createInfo(el, XPDLElementChangeInfo.SELECTED);
      jc.sendEvent(info);
   }

   public void setSelection(List selection, boolean setPkgAndWpAndas) {
      if (checkSelection(selection)) {
         if (selection == null) {
            selectedElements = new ArrayList();
         } else {
            selectedElements = selection;
         }
      } else {
         selectedElements = new ArrayList();
         selectedElements.add(selection.get(selection.size() - 1));
      }

      if (setPkgAndWpAndas)
         setPkgWpAndAs();

      if (selectedElements.size() == 0)
         return;

      List temp = new ArrayList(selectedElements);
      XMLElement firstEl = (XMLElement) temp.get(0);
      XMLElement selOwner = firstEl;
      if (temp.size() > 1) {
         selOwner = firstEl.getParent();
         if (selOwner == null) {
            selOwner = firstEl;
         }
      } else {
         temp.clear();
      }
      XPDLElementChangeInfo info = jc.createInfo(selOwner,
                                                 temp,
                                                 XPDLElementChangeInfo.SELECTED);
      jc.sendEvent(info);
   }

   public void addToSelection(XMLElement el) {
      List temp = new ArrayList();
      temp.add(el);
      addToSelection(temp);
   }

   public void addToSelection(List elements) {
      if (canBeAddedToSelection(elements)) {
         selectedElements.addAll(elements);
      } else {
         selectedElements = new ArrayList();
         selectedElements.add(elements.get(elements.size() - 1));
      }

      setPkgWpAndAs();

      if (selectedElements.size() == 0)
         return;

      List temp = new ArrayList(selectedElements);
      XMLElement firstEl = (XMLElement) temp.get(0);
      XMLElement selOwner = firstEl;
      if (temp.size() > 1) {
         selOwner = firstEl.getParent();
         if (selOwner == null) {
            selOwner = firstEl;
         }
      } else {
         temp.clear();
      }
      XPDLElementChangeInfo info = jc.createInfo(selOwner,
                                                 temp,
                                                 XPDLElementChangeInfo.SELECTED);
      jc.sendEvent(info);
   }

   public void removeFromSelection(XMLElement el) {
      List temp = new ArrayList();
      temp.add(el);
      removeFromSelection(temp);
   }

   public void removeFromSelection(List elements) {
      Set elementsToRemove = new HashSet();

      for (int i = 0; i < elements.size(); i++) {
         XMLElement el = (XMLElement) elements.get(i);

         // for (int j = 0; j < selectedElements.size(); j++) {
         // XMLElement element = (XMLElement)selectedElements.get(j);
         //
         // XMLElement parent = element.getParent();
         // while (parent != null && parent != el) {
         // parent = parent.getParent();
         // }
         //
         // if (parent != null)
         // elementsToRemove.add(element);
         // }

         elementsToRemove.add(el);
      }

      for (Iterator it = elementsToRemove.iterator(); it.hasNext();) {
         selectedElements.remove(it.next());
      }

      setPkgWpAndAs();
      jc.adjustActions();
   }

   protected void setPkgWpAndAs() {
      if (selectedElements.size() != 0) {
         XMLElement el = (XMLElement) selectedElements.get(0);
         workingPKG = XMLUtil.getPackage(el);
         if (workingPKG != null) {
            WorkflowProcess oldWp = workingWP;
            if (oldWp != null) {
               WorkflowProcesses wprs = (WorkflowProcesses) oldWp.getParent();
               if (!wprs.contains(oldWp)) {
                  oldWp = null;
               }
            }
            workingWP = XMLUtil.getWorkflowProcess(el);
            if (workingWP == null) {
               if (oldWp != null && XMLUtil.getPackage(oldWp) == workingPKG) {
                  workingWP = oldWp;
               } else {
                  List wps = workingPKG.getWorkflowProcesses().toElements();
                  if (wps.size() != 0) {
                     workingWP = (WorkflowProcess) wps.get(0);
                  }
               }
            }
            workingAS = XMLUtil.getActivitySet(el);
         } else {
            workingWP = null;
            workingAS = null;
         }
      } else {
         workingPKG = null;
         workingWP = null;
         workingAS = null;
      }
   }

   public Package getWorkingPKG() {
      return workingPKG;
   }

   public String getWorkingPackageId() {
      if (workingPKG != null) {
         return workingPKG.getId();
      }
      return null;
   }

   public WorkflowProcess getWorkingProcess() {
      return workingWP;
   }

   public String getWorkingWorkflowProcessId() {
      if (workingWP != null) {
         return workingWP.getId();
      }
      return null;
   }

   public ActivitySet getWorkingActivitySet() {
      return workingAS;
   }

   public String getWorkingActivitySetId() {
      if (workingAS != null) {
         return workingAS.getId();
      }
      return null;
   }

   public boolean canBeAddedToSelection(List selection) {
      List temp = new ArrayList(selectedElements);
      temp.addAll(selection);

      return checkSelection(temp);
   }

   public boolean checkSelection(List selection) {
      if (selection == null) {
         return true;
      }

      if (selection.size() <= 1) {
         return true;
      }

      boolean hasActivity = false;
      boolean hasTransition = false;
      boolean hasArtifact = false;
      boolean hasAssociation = false;
      boolean hasSingleSelectionElement = false;
      boolean hasOther = false;

      Set parents = new HashSet();
      Set classes = new HashSet();

      for (int i = 0; i < selection.size(); i++) {
         XMLElement el = (XMLElement) selection.get(i);
         XMLElement parent = el.getParent();
         Class cls = el.getClass();
         if (el instanceof Activity) {
            hasActivity = true;
         } else if (el instanceof Transition) {
            hasTransition = true;
         } else if (el instanceof Artifact) {
            hasArtifact = true;
         } else if (el instanceof Association) {
            hasAssociation = true;
         } else if (!(parent instanceof XMLCollection)) {
            hasSingleSelectionElement = true;
         } else {
            hasOther = true;
         }
         if (parent != null) {
            parents.add(parent);
         }
         classes.add(cls);
      }

      // only one other component is allowed
      if (hasSingleSelectionElement) {
         return false;
      }

      if (hasActivity && hasTransition && hasAssociation && !hasOther) {
         Set parentsParents = new HashSet();         
         for (int i = 0; i < selection.size(); i++) {
            XMLElement el = (XMLElement) selection.get(i);
            if (el instanceof Activity || el instanceof Transition) {
               XMLElement elParentParent = el.getParent().getParent();
               parentsParents.add(elParentParent);
            }
         }
         if (parentsParents.size() != 1) {
            return false;
         }
         
         XMLCollectionElement wpOrAs = (XMLCollectionElement)parentsParents.toArray()[0];
         Activities acts = (Activities)wpOrAs.get("Activities");
         for (int i = 0; i < selection.size(); i++) {
            XMLElement el = (XMLElement) selection.get(i);
            if (el instanceof Association) {
               Association asoc = (Association)selection.get(i);
               Activity from = acts.getActivity(asoc.getSource());
               Activity to = acts.getActivity(asoc.getTarget());
               if (!((from!=null && from.getParent().getParent()==wpOrAs) || (to!=null && to.getParent().getParent()==wpOrAs))) {
                  return false;
               }
            }
         }
         
         return true;
      }

      // if only activities and transitions are selected, check if they
      // are contained in the same WorkflowProcess/ActivitySet
      if (hasActivity && hasTransition && !hasOther) {
         Set parentsParents = new HashSet();
         for (int i = 0; i < selection.size(); i++) {
            XMLElement elParentParent = ((XMLElement) selection.get(i)).getParent()
               .getParent();
            parentsParents.add(elParentParent);
         }
         if (parentsParents.size() != 1) {
            return false;
         }

         return true;
      }
      if ((hasArtifact || hasAssociation) && !hasOther) {
         return true;
      }
      // if there are more than one parent, or more than one type of object class, return
      // false
      return !(parents.size() > 1 || classes.size() > 1);
   }

   public boolean canEditProperties() {
      if (getSelectedElements().size() == 1) {
         XMLElement firstSelected = (XMLElement) selectedElements.get(0);
         if (XMLUtil.getPackage(firstSelected) != null) {
            return true;
         }
      }
      return false;
   }

   public boolean canCut() {
      return checkCutOrCopySelection(true);
   }

   public boolean canCopy() {
      return checkCutOrCopySelection(false);
   }

   public boolean canDuplicate() {
      if (selectedElements.size() != 1) {
         return false;
      }

      XMLElement firstSelected = (XMLElement) selectedElements.get(0);
      XMLElement parent = firstSelected.getParent();
      if (!(parent instanceof XMLCollection)) {
         return false;
      }

      return jc.canDuplicateElement((XMLCollection) parent, firstSelected);
   }

   public boolean canInsertNew() {
      if (selectedElements.size() == 0) {
         return false;
      }

      XMLElement firstSelected = (XMLElement) selectedElements.get(0);
      XMLElement parent = firstSelected.getParent();
      if (!(firstSelected instanceof XMLCollection || parent instanceof XMLCollection || firstSelected instanceof Package)) {
         return false;
      }

      if (firstSelected instanceof XMLCollection) {
         return jc.canCreateElement((XMLCollection) firstSelected);
      } else if (parent instanceof XMLCollection) {
         return jc.canCreateElement((XMLCollection) parent);
      } else if (firstSelected instanceof Package
                 && !((Package) firstSelected).isReadOnly()) {
         return true;
      }
      return false;
   }

   public boolean canPaste() {
      if (jc.getEdit().getClipboard().size() == 0) {
         return false;
      }

      if (selectedElements.size() == 0) {
         return false;
      }

      Class clipboardParentsClass1 = null;
      Class clipboardParentsClass2 = null;
      Class clipboardElementsClass1 = null;
      Class clipboardElementsClass2 = null;
      for (int i = 0; i < jc.getEdit().getClipboard().size(); i++) {
         XMLElement el = (XMLElement) jc.getEdit().getClipboard().get(i);
         if (el instanceof Pool)
            continue;
         XMLElement parent = el.getParent();
         if (el instanceof Transition) {
            clipboardParentsClass2 = parent.getClass();
            clipboardElementsClass2 = el.getClass();
         } else {
            clipboardParentsClass1 = parent.getClass();
            clipboardElementsClass1 = el.getClass();
         }
      }

      Class selectionElementsClass1 = null;
      Class selectionElementsClass2 = null;
      for (int i = 0; i < selectedElements.size(); i++) {
         XMLElement el = (XMLElement) selectedElements.get(i);
         if (el.isReadOnly()) {
            return false;
         }

         if (el instanceof Transition) {
            selectionElementsClass2 = el.getClass();
         } else {
            selectionElementsClass1 = el.getClass();
         }

      }
      // System.err.println("CEC1="+clipboardElementsClass1+",
      // CEC2="+clipboardElementsClass2);
      // System.err.println("SEC1="+selectionElementsClass1+",
      // SEC2="+selectionElementsClass2);
      boolean allowPaste1 = clipboardElementsClass1 == null ? false
                                                           : (selectionElementsClass1 == null ? false
                                                                                             : clipboardElementsClass1 == selectionElementsClass1);
      // System.err.println("AP1="+allowPaste1);
      boolean allowPaste2 = clipboardElementsClass2 == null ? false
                                                           : (selectionElementsClass2 == null ? false
                                                                                             : clipboardElementsClass2 == selectionElementsClass2);

      // System.err.println("AP2="+allowPaste2);
      boolean allowPaste = (allowPaste1 || allowPaste2);

      if (allowPaste)
         return true;
      if (selectedElements.size() > 1)
         return false;

      XMLElement firstSelected = (XMLElement) selectedElements.get(0);

      if ((firstSelected instanceof WorkflowProcess || firstSelected instanceof ActivitySet) && (clipboardElementsClass1==Artifact.class || clipboardElementsClass1==Association.class)) return true;
      if (firstSelected instanceof XMLAttribute
          || firstSelected instanceof XMLSimpleElement
          || firstSelected instanceof XMLComplexChoice) {
         return false;
      }
      if (firstSelected instanceof XMLCollection) {
         Class selectionElementClass = selectionElementsClass1;
         if (selectionElementClass == null) {
            selectionElementClass = selectionElementsClass2;
         }
         if (selectionElementClass == clipboardParentsClass1
             || selectionElementClass == clipboardParentsClass2)
            return true;

         return false;
      }

      if (firstSelected instanceof XMLComplexElement) {
         Class clipboardParentsClass = clipboardParentsClass1;
         if (clipboardParentsClass == null) {
            clipboardParentsClass = clipboardParentsClass2;
         }
         List subEls = ((XMLComplexElement) firstSelected).toElements();
         for (int i = 0; i < subEls.size(); i++) {
            if (subEls.get(i).getClass() == clipboardParentsClass) {
               return true;
            }
         }
         return false;
      }

      return false;
   }

   public boolean canDelete() {
      if (selectedElements.size() == 0) {
         return false;
      }

      for (int i = 0; i < selectedElements.size(); i++) {
         XMLElement selected = (XMLElement) selectedElements.get(i);
         XMLElement parent = selected.getParent();
         if (!(parent instanceof XMLCollection)) {
            return false;
         }
         if (!jc.canRemoveElement((XMLCollection) parent, selected)) {
            return false;
         }
      }
      return true;
   }

   public boolean canGetReferences() {
      if (selectedElements.size() != 1) {
         return false;
      }
      XMLElement selected = (XMLElement) selectedElements.get(0);

      if (selected instanceof TypeDeclaration
          || selected instanceof Participant
          || selected instanceof Application
          || selected instanceof WorkflowProcess
          || selected instanceof ActivitySet
          || selected instanceof DataField
          || (selected instanceof FormalParameter && selected.getParent().getParent() instanceof WorkflowProcess)
          || selected instanceof Activity || selected instanceof Transition
          || selected instanceof Package || selected instanceof Lane) {
         return true;
      }
      return false;
   }

   protected boolean checkCutOrCopySelection(boolean isCut) {
      if (selectedElements.size() == 0) {
         return false;
      }

      for (int i = 0; i < selectedElements.size(); i++) {
         XMLElement el = (XMLElement) selectedElements.get(i);
         XMLElement parent = el.getParent();
         if (el instanceof Transition) {
            Activities acts = (Activities) ((XMLComplexElement) el.getParent()
               .getParent()).get("Activities");

            Activity from = acts.getActivity(((Transition) el).getFrom());
            Activity to = acts.getActivity(((Transition) el).getTo());

            if (from == null
                || to == null
                || !(selectedElements.contains(from) && selectedElements.contains(to))) {
               return false;
            }
         } else if (el instanceof Association) {
            Package pkg = XMLUtil.getPackage(el);

            XMLCollectionElement from = pkg.getArtifact(((Association) el).getSource());
            XMLCollectionElement to = pkg.getArtifact(((Association) el).getTarget());
            if (from == null) {
               from = pkg.getActivity(((Association) el).getSource());
            }
            if (to == null) {
               to = pkg.getActivity(((Association) el).getTarget());
            }
            if (from == null
                || to == null
                || !(selectedElements.contains(from) && selectedElements.contains(to))) {
               return false;
            }
         } else if (!(parent instanceof XMLCollection)) {
            return false;
         }
         XMLCollection col = (XMLCollection) parent;
         if ((!(el instanceof Transition || el instanceof Association) && !jc.canDuplicateElement(col, el, false))
             || ((!jc.canInsertElement(col, el, false) || !jc.canCreateElement(col, false)) && !(col instanceof Associations || col instanceof Artifacts))) {
            return false;
         }

         if (isCut && !jc.canRemoveElement(col, el)) {
            return false;
         }
      }

      return true;
   }

   protected void removeNonExistingElementsFromSelection(List selection,
                                                         List removedElements) {
      Set removeFromSelection = new HashSet();
      for (int i = 0; i < removedElements.size(); i++) {
         XMLElement removedEl = (XMLElement) removedElements.get(i);
         for (int j = 0; j < selection.size(); j++) {
            XMLElement selEl = (XMLElement) selection.get(j);
            if (XMLUtil.isParentsChild(removedEl, selEl)) {
               removeFromSelection.add(selEl);
            }
         }
      }
      selection.removeAll(removeFromSelection);
   }
}
