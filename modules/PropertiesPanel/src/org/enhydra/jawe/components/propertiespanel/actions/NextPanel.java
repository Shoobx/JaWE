package org.enhydra.jawe.components.propertiespanel.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.base.panel.InlinePanel;
import org.enhydra.jawe.components.propertiespanel.PropertiesPanelComponent;

/**
 * @author Zoran Milakovic
 */
public class NextPanel extends ActionBase {

   protected InlinePanel ipc;
   
   public NextPanel (JaWEComponent jawecomponent) {
      super(jawecomponent);
      this.ipc=(InlinePanel)((PropertiesPanelComponent)jawecomponent).getView();
      enabled=false;      
   }

   public void enableDisableAction() {      
      setEnabled(ipc.getHistoryManager()!=null && ipc.getHistoryManager().canGoForward());
   }
   
   public void actionPerformed(ActionEvent e) {
      ipc.displayNextElement();      
   }
}
