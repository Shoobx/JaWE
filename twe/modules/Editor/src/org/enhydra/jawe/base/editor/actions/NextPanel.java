package org.enhydra.jawe.base.editor.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.base.editor.TogWEStandardXPDLElementEditor;
import org.enhydra.jawe.base.panel.InlinePanel;

/**
 * @author Zoran Milakovic
 */
public class NextPanel extends ActionBase {

   protected InlinePanel ipc;
   
   public NextPanel (JaWEComponent jawecomponent) {
      super(jawecomponent);
      this.ipc=(InlinePanel)((TogWEStandardXPDLElementEditor)jawecomponent).getView();
      enabled=false;      
   }

   public void enableDisableAction() {      
      setEnabled(ipc.getHistoryManager()!=null && ipc.getHistoryManager().canGoForward());
   }
   
   public void actionPerformed(ActionEvent e) {
      ipc.displayNextElement();      
   }
}
