package org.enhydra.jawe.components.propertiespanel.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.base.panel.InlinePanel;
import org.enhydra.jawe.components.propertiespanel.PropertiesPanelComponent;

/**
 * @author Zoran Milakovic
 */
public class PreviousPanel extends ActionBase {

	
   protected InlinePanel ipc;
   
   public PreviousPanel (JaWEComponent jawecomponent) {
      super(jawecomponent);
      this.ipc=(InlinePanel)((PropertiesPanelComponent)jawecomponent).getView();
      enabled=false;      
   }

   public void enableDisableAction() {
      setEnabled(ipc.getHistoryManager()!=null && ipc.getHistoryManager().canGoBack());
   }
   
   public void actionPerformed(ActionEvent e) {
      ipc.displayPreviousElement();      
   }
}
