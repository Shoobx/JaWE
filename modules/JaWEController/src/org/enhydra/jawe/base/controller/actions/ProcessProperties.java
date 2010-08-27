package org.enhydra.jawe.base.controller.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.base.editor.XPDLElementEditor;


/**
 * Class that realizes <B>process properties</B> action.
 */
public class ProcessProperties extends ActionBase {

   public ProcessProperties (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {    
      if (getWorkflowProcess() != null)
         setEnabled(true);
      else 
         setEnabled(false);
   }
   
   public void actionPerformed(ActionEvent e) {
      if (getWorkflowProcess()==null) return;
      JaWEController jc = (JaWEController)jawecomponent;
      jc.getSelectionManager().setSelection(getWorkflowProcess(), false);
      XPDLElementEditor ed=JaWEManager.getInstance().getXPDLElementEditor();
      ed.editXPDLElement(getWorkflowProcess());
   }
   
}
