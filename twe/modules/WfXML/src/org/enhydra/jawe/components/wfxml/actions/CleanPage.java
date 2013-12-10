package org.enhydra.jawe.components.wfxml.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.components.wfxml.WfXML;

/**
 * Cleans search navigator.
 * 
 * @author Sasa Bojanic
 */
public class CleanPage extends ActionBase {

   public CleanPage(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      WfXML wfxml = (WfXML)jawecomponent;
      
      if (wfxml.hasConnectionsInHistory()) {
         setEnabled(true); 
      } else {
         setEnabled(false);
      }
   }
   
   public void actionPerformed(ActionEvent e) {      
      WfXML wfxml = (WfXML)jawecomponent;
      
      wfxml.clearConnectionHistory();
      
      setEnabled(false);
   }   
}
