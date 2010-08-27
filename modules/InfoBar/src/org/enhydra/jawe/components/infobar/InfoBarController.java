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
import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLElementChangeInfo;
import org.enhydra.shark.xpdl.elements.Package;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;

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
