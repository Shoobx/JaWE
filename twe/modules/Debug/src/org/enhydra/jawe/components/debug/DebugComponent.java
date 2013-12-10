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

package org.enhydra.jawe.components.debug;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.XPDLElementChangeInfo;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ActivitySet;
import org.enhydra.jxpdl.elements.Package;
import org.enhydra.jxpdl.elements.WorkflowProcess;

/**
 * Used to handle process graphs.
 * 
 * @author Sasa Bojanic
 */
public class DebugComponent implements Observer, JaWEComponent {
   
   protected String type = JaWEComponent.OTHER_COMPONENT; 

   public final static String DATE_TIME_PATTERN = "yyyy.MM.dd HH:mm:ss:SSS";

   public static SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN);

   protected DebugComponentPanel panel;
   protected DefaultDebugComponentSettings settings;
   
   public DebugComponent(JaWEComponentSettings settings) throws Exception {
      this.settings = (DefaultDebugComponentSettings)settings;
      this.settings.init(this);

      init();
      JaWEManager.getInstance().getJaWEController().addObserver(this);
   }

   public JaWEComponentSettings getSettings() {
      return settings;
   }
   
   public void update(Observable o, Object arg) {      
      if (!(arg instanceof XPDLElementChangeInfo)) return;
      XPDLElementChangeInfo info=(XPDLElementChangeInfo)arg;
      long start=System.currentTimeMillis();
      update(info,true);
      long end=System.currentTimeMillis();
      double diffs=(end-start)/1000.0;
      JaWEManager.getInstance().getLoggingManager().debug("THE UPDATE OF DEBUG COMPONENT LASTED FOR "+diffs+" SECONDS!");
   }
   
   public void update (XPDLElementChangeInfo info,boolean updateMsg) {
      XMLElement changedElement = info.getChangedElement();
      if (changedElement == null)
         return;
      
      int action=info.getAction();
      String msg=""; 
      if (action==XPDLElementChangeInfo.UNDOABLE_ACTION_STARTED) {
         msg = "\n---------------------- STARTED Undoable action for XPDL Element " + changedElement.toName()+", hc="+changedElement.hashCode()+", ohc="+changedElement.getOriginalElementHashCode()+"-----------------------";
      } else if (action==XPDLElementChangeInfo.ADJUST_UNDOABLE_ACTION) {
         msg = "\n------- Controller asked components to adjust undoable action for XPDL Element " + changedElement.toName()+", hc="+changedElement.hashCode()+", ohc="+changedElement.getOriginalElementHashCode()+"--------";
      } else if (action==XPDLElementChangeInfo.UNDOABLE_ACTION_ENDED) {
         for (int i=0; i<info.getChangedSubElements().size(); i++) {            
            update((XPDLElementChangeInfo)info.getChangedSubElements().get(i),false);
         }
         msg = "----------------------- ENDED Undoable action for XPDL Element " + changedElement.toName()+", hc="+changedElement.hashCode()+", ohc="+changedElement.getOriginalElementHashCode()+"------------------------";
      } else if (action==XPDLElementChangeInfo.UNDO) {
         msg = "----------------------- UNDO ACTION WAS PERFORMED for XPDL Element " + changedElement.toName()+", hc="+changedElement.hashCode()+"------------------------";
         panel.appendText(updateMessage(msg));
         for (int i=0; i<info.getChangedSubElements().size(); i++) {            
            update((XPDLElementChangeInfo)info.getChangedSubElements().get(i),false);
         }
         msg = "-----------------------------------------------------------------------------------------------------------------";
      } else if (action==XPDLElementChangeInfo.REDO) {
         msg = "----------------------- REDO ACTION WAS PERFORMED for XPDL Element " + changedElement.toName()+", hc="+changedElement.hashCode()+", ohc="+changedElement.getOriginalElementHashCode()+"-----------------------";
         panel.appendText(updateMessage(msg));
         for (int i=0; i<info.getChangedSubElements().size(); i++) {            
            update((XPDLElementChangeInfo)info.getChangedSubElements().get(i),false);
         }
         msg = "-----------------------------------------------------------------------------------------------------------------";
      } else {      
         String changedSubElements="";
         if (info.getChangedSubElements() != null) {
            changedSubElements+=info.getChangedSubElements().size()+" -> {";
            for (int i=0; i<info.getChangedSubElements().size(); i++) {
               Object changed=info.getChangedSubElements().get(i);
               if (changed instanceof XMLElement) {
                  changedSubElements += "[hc="+changed.hashCode()+",ohc="+((XMLElement)changed).getOriginalElementHashCode()+"]";
                  if (i<info.getChangedSubElements().size()-1) changedSubElements+=",";
               } else {
                  changedSubElements += changed.toString();
                  if (i<info.getChangedSubElements().size()-1) changedSubElements+=",";
               }
            }
            changedSubElements+="}";
         }
         Package pkg = XMLUtil.getPackage(changedElement);
         WorkflowProcess wp = XMLUtil.getWorkflowProcess(changedElement);
         ActivitySet as = XMLUtil.getActivitySet(changedElement);
         Activity act = XMLUtil.getActivity(changedElement);
         msg = "\nChanged XPDL Element name=" + changedElement.toName()+", hc="+changedElement.hashCode() +", ohc="+changedElement.getOriginalElementHashCode()+"\nsource="
               + info.getSource().getClass().getName() + ", " + "\naction="
               + info.getActionName() + ", " + "oldValue="
               + info.getOldValue() + ", " + "newValue=" + info.getNewValue() + ", "
               + "changed sub-elements=" + changedSubElements + ", " + "\nmypackage="
               + ((pkg != null) ? pkg.getId() : "null") + ", " + "myworkflowprocess="
               + ((wp != null) ? wp.getId() : "null") + ", " + "myactivityset="
               + ((as != null) ? as.getId() : "null") + ", " + "myactivity="
               + ((act != null) ? act.getId() : "null") + ", " + "myparent="
               + ((changedElement.getParent() != null) ? changedElement.getParent().toName() : "null")
               + "\n";
      }
      if (updateMsg) {
         msg=updateMessage(msg);
      }
      panel.appendText(msg);
      
      settings.adjustActions();
   }

   protected void init() {
      panel = new DebugComponentPanel(this);
      panel.configure();
      panel.init();
   }

   public JaWEComponentView getView() {
      return panel;
   }

   public JComponent getDisplay() {
      return panel.getDisplay();
   }

   public String getComponentType() {
      return type;
   }   

   public void setComponentType(String type) {
      this.type = type; 
   }
   
   public String getName() {
      return "DebugComponent";
   }

   public boolean adjustXPDL(Package pkg) {
      return false;
   }

   public List checkValidity (XMLElement el,boolean fullCheck) {
      return null;
   }

   public boolean canCreateElement(XMLCollection col) {
      return true;
   }

   public boolean canInsertElement(XMLCollection col, XMLElement el) {
      return true;
   }

   public boolean canModifyElement(XMLElement el) {
      return true;
   }

   public boolean canRemoveElement(XMLCollection col, XMLElement el) {
      return true;
   }

   public boolean canDuplicateElement(XMLCollection col, XMLElement el) {
      return true;
   }

   public boolean canRepositionElement(XMLCollection col, XMLElement el) {
      return true;
   }

   protected String updateMessage(String msg) {
      return "\n" + sdf.format(new java.util.Date()) + ":" + msg;
   }
   
   public void setUpdateInProgress(boolean inProgress) {
      
   }
   
   public boolean isUpdateInProgress() {
      return false;
   }
   
}