/**
 * Miroslav Popov, Aug 2, 2005
 */
package org.enhydra.jawe.components.debug.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.components.debug.DebugComponent;
import org.enhydra.jawe.components.debug.DebugComponentPanel;

/**
 * @author Miroslav Popov
 *
 */
public class CleanPage extends ActionBase {

   public CleanPage(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      DebugComponent dc = (DebugComponent)jawecomponent;
      
      if ( ((DebugComponentPanel)dc.getView()).getTextSize() != 0)
         setEnabled(true); 
      else
         setEnabled(false);
   }
   
   public void actionPerformed(ActionEvent e) {      
      DebugComponent con = (DebugComponent)jawecomponent;      
      
      ((DebugComponentPanel)(con.getView())).clearText();
      
      setEnabled(false);
      }   
}
