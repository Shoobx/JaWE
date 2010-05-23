package org.enhydra.jawe.base.editor.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.base.editor.TogWEStandardXPDLElementEditor;
import org.enhydra.jawe.base.panel.InlinePanel;
import org.enhydra.shark.xpdl.XMLElement;

/**
 * @author Sasa Bojanic
 */
public class ParentElementPanel extends ActionBase {

   protected InlinePanel ipc;
	
   public ParentElementPanel (JaWEComponent jawecomponent) {
      super(jawecomponent);
      this.ipc=(InlinePanel)((TogWEStandardXPDLElementEditor)jawecomponent).getView();
      setEnabled(false);
   }

   public void enableDisableAction() {
      XMLElement el=ipc.getActiveElement();
      if (el!=null && el.getParent()!=null) {
         setEnabled(true);
         return;
      } 
      
      setEnabled(false);      
   }
   
   public void actionPerformed(ActionEvent e) {
      ipc.displayParentPanel();
   }
}
