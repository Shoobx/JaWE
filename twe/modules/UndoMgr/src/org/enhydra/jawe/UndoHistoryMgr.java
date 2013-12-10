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

package org.enhydra.jawe;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLComplexChoice;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLElementChangeInfo;
import org.enhydra.jxpdl.elements.Package;

/**
 * Used to handle undo history.
 * 
 * @author Sasa Bojanic
 */
public class UndoHistoryMgr implements UndoHistoryManager {

   protected Stack undoHistory = new Stack();

   protected Stack redoHistory = new Stack();

   protected int maxHistory = -1;

   protected JaWEController jc;

   protected boolean undoOrRedoInProgress = false;   

   public UndoHistoryMgr() {
      this.jc=JaWEManager.getInstance().getJaWEController();      
   }

   public void init (int pMaxHistory) {
      if (pMaxHistory>=0) {
         this.maxHistory = pMaxHistory;
      } else {
         this.maxHistory=Integer.MAX_VALUE;
      }
   }
   
   public void registerEvents (List xpdlInfoList,XPDLElementChangeInfo selEv) {
      if (maxHistory<=0) return;
      if (xpdlInfoList==null || xpdlInfoList.size()==0) return;
      List undoEventList=this.convertList(xpdlInfoList);
      undoEventList.add(selEv);
      if (undoHistory.size()>=maxHistory) {
         undoHistory.remove(0);
      }
      undoHistory.push(undoEventList);
      redoHistory.clear();
   }
   
   public void undo() {
      if (!undoHistory.empty()) {
         List events = (List) undoHistory.pop();

         XPDLElementChangeInfo lastEvent=(XPDLElementChangeInfo)events.remove(events.size()-1);

         // calculate redo events
         XPDLElementChangeInfo selectEventForRedo=jc.getCurrentSelectionEvent();
//            System.err.println("SELECTION EVENT FOR REDO size is "+selectEventForRedo.getChangedSubElements().size()+", sel="+selectEventForRedo.getChangedSubElements()+", main="+selectEventForRedo.getChangedElement());
         List redoEventList=convertList(events);
         redoEventList.add(selectEventForRedo);
         
         if (redoHistory.size()>=maxHistory) {
            redoHistory.remove(0);
         }
         redoHistory.push(redoEventList);

         undoOrRedoInProgress = true;
         
         for (int i=0; i<events.size(); i++) {
            XPDLElementChangeInfo info = (XPDLElementChangeInfo) events.get(i);
            changeModel(info);
         }

         undoOrRedoInProgress = false;

         // making changes
         jc.setUpdateInProgress(true);
         Package mp = jc.getMainPackage();
         XPDLElementChangeInfo ucInfo = jc.createInfo(mp, XPDLElementChangeInfo.UNDO);         
         ucInfo.setChangedSubElements(events);
         jc.sendEvent(ucInfo);
         
         jc.setUpdateInProgress(false);
         JaWEManager.getInstance().getLoggingManager()
               .debug("All events after UNDO:\n" + events);
         
//            System.err.println("SELECTION EVENT AFTER UNDO size is "+lastEvent.getChangedSubElements().size()+", sel="+lastEvent.getChangedSubElements()+", main="+lastEvent.getChangedElement());            
         if (lastEvent.getChangedSubElements().size()>0) {            
            jc.getSelectionManager().setSelection(lastEvent.getChangedSubElements(), true);
         } else {
            jc.getSelectionManager().setSelection(lastEvent.getChangedElement(), true);
         }
         
      }
//         printGH("UNDO");
   }

   public void redo() {
      if (!redoHistory.empty()) {
         List events = (List) redoHistory.pop();

         XPDLElementChangeInfo lastEvent=(XPDLElementChangeInfo)events.remove(events.size()-1);

         // calculate undo events
         XPDLElementChangeInfo selectEventForUndo=jc.getCurrentSelectionEvent();
//            System.err.println("SELECTION EVENT FOR UNDO size is "+selectEventForUndo.getChangedSubElements().size()+", sel="+selectEventForUndo.getChangedSubElements()+", main="+selectEventForUndo.getChangedElement());
         List undoEventList=this.convertList(events);
         undoEventList.add(selectEventForUndo);

         if (undoHistory.size()>=maxHistory) {
            undoHistory.remove(0);
         }
         undoHistory.push(undoEventList);

         undoOrRedoInProgress = true;
          
         for (int i=0; i<events.size(); i++) {
            XPDLElementChangeInfo info = (XPDLElementChangeInfo) events.get(i);
            changeModel(info);
         }

         undoOrRedoInProgress = false;

         // making changes
         jc.setUpdateInProgress(true);
         Package mp = jc.getMainPackage();
         XPDLElementChangeInfo ucInfo = jc.createInfo(mp, XPDLElementChangeInfo.REDO);
         ucInfo.setChangedSubElements(events);

         jc.sendEvent(ucInfo);

         jc.setUpdateInProgress(false);
         JaWEManager.getInstance().getLoggingManager()
               .debug("All events after REDO:\n" + events);
                     
//            System.err.println("SELECTION EVENT AFTER REDO size is "+lastEvent.getChangedSubElements().size()+", sel="+lastEvent.getChangedSubElements()+", main="+lastEvent.getChangedElement());
         
         if (lastEvent.getChangedSubElements().size()>0) {            
            jc.getSelectionManager().setSelection(lastEvent.getChangedSubElements(), true);
         } else {
            jc.getSelectionManager().setSelection(lastEvent.getChangedElement(), true);
         }
      }
//         printGH("DNG");
   }
   
   
   public boolean canUndo() {
      return !undoHistory.isEmpty();
   }

   public boolean canRedo() {
      return !redoHistory.isEmpty();
   }
   
   public boolean isUndoOrRedoInProgress () {
      return undoOrRedoInProgress;
   }

   public void cleanHistory () {
      undoHistory.clear();
      redoHistory.clear();
   }
   
   protected List convertList(List lst) {
      List changed = new ArrayList();
      // the last stored event is selection event
      for (int i=lst.size()-1; i>=0; i--) {
         XPDLElementChangeInfo ch = (XPDLElementChangeInfo) lst.get(i);
         XPDLElementChangeInfo chNew = new XPDLElementChangeInfo(jc, ch);
         if ((chNew.getAction() == XMLElementChangeInfo.INSERTED)) {
            chNew.setAction(XMLElementChangeInfo.REMOVED);
         } else if ((chNew.getAction() == XMLElementChangeInfo.REMOVED)) {
            chNew.setAction(XMLElementChangeInfo.INSERTED);
         } else if ((chNew.getAction() == XMLElementChangeInfo.UPDATED)
               || chNew.getAction() == XMLElementChangeInfo.REPOSITIONED) {
            chNew.setNewValue(ch.getOldValue());
            chNew.setOldValue(ch.getNewValue());
         }
         changed.add(chNew);
      }

      return changed;
   }

   protected boolean changeModel(XPDLElementChangeInfo info) {
      try {
         if (info.getAction() == XMLElementChangeInfo.INSERTED) {
            if (info.getChangedElement() instanceof XMLCollection) {
               XMLCollection col = (XMLCollection) info.getChangedElement();
               List lst = info.getChangedSubElements();
               for (int i = 0; i < lst.size(); i++) {
                  col.add((XMLElement) lst.get(i));
               }
            }
         } else if (info.getAction() == XMLElementChangeInfo.REMOVED) {
            if (info.getChangedElement() instanceof XMLCollection) {
               XMLCollection col = (XMLCollection) info.getChangedElement();
               List lst = info.getChangedSubElements();
               for (int i = 0; i < lst.size(); i++) {
                  col.remove((XMLElement) lst.get(i));
               }
            }
         } else if (info.getAction() == XMLElementChangeInfo.UPDATED) {
//            System.err.println("CM->Setting value of element "+info.getChangedElement()+" to "+info.getNewValue()+", ov was  "+info.getChangedElement().toValue());
            XMLElement el=info.getChangedElement();
            if (el instanceof XMLComplexChoice) {
               ((XMLComplexChoice)el).setChoosen((XMLElement)info.getNewValue());
            } else {
               el.setValue(info.getNewValue().toString());
            }
         } else if (info.getAction() == XMLElementChangeInfo.REPOSITIONED) {
            XMLCollection col = (XMLCollection) info.getChangedElement();
            List lst = info.getChangedSubElements();
            List newPositions = (List)info.getNewValue();
            for (int i = 0; i < lst.size(); i++) {
               col.reposition((XMLElement) lst.get(i),((Integer)newPositions.get(i)).intValue());
            }            
         }
      } catch (Exception e) {
         return false;
      }
      return true;
   }
   
   protected void printGH(String info) {
      System.out.println(info + "- BACK:" + undoHistory);
      System.out.println(info + "- FORW:" + redoHistory);
   }

}
