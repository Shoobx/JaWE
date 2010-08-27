/**
 * Miroslav Popov, Aug 2, 2005
 */
package org.enhydra.jawe.base.logger.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.base.logger.LogPanel;
import org.enhydra.jawe.base.logger.LoggingManagerComponent;

/**
 * @author Miroslav Popov
 *
 */
public class CleanPage extends ActionBase {

   public CleanPage(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {  
      LoggingManagerComponent lg = (LoggingManagerComponent)jawecomponent;
      
      if ( ((LogPanel)lg.getView()).getTextSize() != 0)
         setEnabled(true); 
      else
         setEnabled(false);      
   }
   
   public void actionPerformed(ActionEvent e) {      
      LoggingManagerComponent con = (LoggingManagerComponent)jawecomponent;      
      
      ((LogPanel)(con.getView())).clearText();
      
      setEnabled(false);
   }
}
