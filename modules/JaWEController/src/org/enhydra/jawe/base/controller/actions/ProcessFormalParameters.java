package org.enhydra.jawe.base.controller.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.base.editor.XPDLElementEditor;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;

/**
* Shows formal parameters for the process.
*/
public class ProcessFormalParameters extends ActionBase {

   public ProcessFormalParameters (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {      
      if (getWorkflowProcess() != null)
         setEnabled(true);
      else 
         setEnabled(false);
   }
   
   public void actionPerformed(ActionEvent e) {
      WorkflowProcess wp=getWorkflowProcess();
      if (wp==null) return;
      JaWEController jc = (JaWEController)jawecomponent;
      jc.getSelectionManager().setSelection(getWorkflowProcess().getFormalParameters(), true);            
      XPDLElementEditor ed=JaWEManager.getInstance().getXPDLElementEditor();
      ed.editXPDLElement(wp.getFormalParameters());            
   }
}
