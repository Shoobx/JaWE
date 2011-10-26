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
 * Miroslav Popov, Nov 29, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.components.infobar;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.enhydra.jawe.ChoiceButton;
import org.enhydra.jawe.ChoiceButtonListener;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.XPDLElementChangeInfo;
import org.enhydra.jawe.base.controller.JaWESelectionManager;
import org.enhydra.jawe.base.xpdlhandler.XPDLHandler;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLElementChangeInfo;
import org.enhydra.jxpdl.elements.Package;
import org.enhydra.jxpdl.elements.WorkflowProcess;

/**
 * @author Miroslav Popov
 *
 */
public class InfoBarController implements JaWEComponent, Observer, ChoiceButtonListener {

   protected String type = JaWEComponent.UPPER_STATUS_COMPONENT;    
   protected boolean updateInProgress=false;
     
   protected InfoBarSettings settings;
   
   public InfoBarController(JaWEComponentSettings settings) throws Exception {
     this.settings = (InfoBarSettings)settings;
     this.settings.init(this);
     init();
     JaWEManager.getInstance().getJaWEController().addObserver(this);
   }
   
   public JaWEComponentSettings getSettings() {
      return settings;
   }
   
   
   public InfoBarSettings getInfoBarSettings() {
      return settings;
   }
   
   public void update(Observable o, Object arg) {
      if (!(arg instanceof XPDLElementChangeInfo)) return;
      XPDLElementChangeInfo info = (XPDLElementChangeInfo) arg;
      int action = info.getAction();
      if (!(action == XMLElementChangeInfo.UPDATED ||
            action == XMLElementChangeInfo.INSERTED ||
            action == XMLElementChangeInfo.REMOVED ||
            action == XPDLElementChangeInfo.SELECTED ||
            action == XPDLElementChangeInfo.UNDOABLE_ACTION_ENDED ||
            action == XPDLElementChangeInfo.UNDO ||
            action == XPDLElementChangeInfo.REDO)) 
         return;            
      
      long start = System.currentTimeMillis();
      JaWEManager.getInstance().getLoggingManager().info(
            "InfoBarController -> update for event " + info + " started ...");

      JaWESelectionManager selectionMng = JaWEManager.getInstance().getJaWEController().getSelectionManager();

      if (selectionMng.getWorkingPKG() != null) {
         panel.setPackageName(selectionMng.getWorkingPKG());
         panel.setProcessName(selectionMng.getWorkingProcess());
      } else {
         panel.setPackageName(null);
         panel.setProcessName(null);
      }

      panel.setFileName(JaWEManager.getInstance().getXPDLHandler().getAbsoluteFilePath(selectionMng.getWorkingPKG()));     

      JaWEManager.getInstance().getLoggingManager().info("InfoBarController -> update ended");
      long end=System.currentTimeMillis();
      double diffs=(end-start)/1000.0;
      JaWEManager.getInstance().getLoggingManager().debug("THE UPDATE OF InfoBar COMPONENT LASTED FOR "+diffs+" SECONDS!");

   }

   protected InfoBar panel;

   protected void init () {
      panel=new InfoBar (this);
      panel.configure();
      panel.init();
   }

   public JaWEComponentView getView () {
      return panel;
   }

   public String getType() {
      return type;
   }   

   public void setType(String type) {
      this.type = type; 
   }
   
   public String getName () {
      return "InfoBar";
   }

   public boolean adjustXPDL (Package pkg) {
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
   
   // button choice
   public void selectionChanged(ChoiceButton cbutton, Object change) {
         JaWEManager.getInstance().getJaWEController().getSelectionManager().setSelection((XMLElement)change, true);
   }
   
   public Object getSelectedObject(ChoiceButton cbutton) {
      if (cbutton.getChoiceType() == Package.class) {
         return JaWEManager.getInstance().getJaWEController().getSelectionManager().getWorkingPKG();
      } else if (cbutton.getChoiceType() == WorkflowProcess.class) {
         return JaWEManager.getInstance().getJaWEController().getSelectionManager().getWorkingProcess();
      } else {
         return null;
      }
   }

   public List getChoices(ChoiceButton cbutton) {
      ArrayList toRet = new ArrayList();
      XPDLHandler xpdlhandler = JaWEManager.getInstance().getXPDLHandler();
      if (cbutton.getChoiceType() == Package.class) {
         toRet.addAll(xpdlhandler.getAllPackages());
      } else if (cbutton.getChoiceType().equals(WorkflowProcess.class)) {
         if (JaWEManager.getInstance().getJaWEController().getSelectionManager().getWorkingPKG() != null) {
            toRet.addAll(JaWEManager.getInstance().getJaWEController().getSelectionManager().getWorkingPKG().getWorkflowProcesses().toElements());
         }
      } 
      
      return toRet;
   }
   
   public void setUpdateInProgress(boolean inProgress) {
      updateInProgress=inProgress;
   }
   
   public boolean isUpdateInProgress() {
      return updateInProgress;
   }
   
}
