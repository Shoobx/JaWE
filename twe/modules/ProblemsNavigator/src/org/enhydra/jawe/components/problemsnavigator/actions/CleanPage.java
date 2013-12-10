package org.enhydra.jawe.components.problemsnavigator.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.components.problemsnavigator.ProblemsNavigator;

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
      ProblemsNavigator nav = (ProblemsNavigator)jawecomponent;
      
      if (nav.hasMatches())
         setEnabled(true); 
      else
         setEnabled(false);
   }
   
   public void actionPerformed(ActionEvent e) {      
      ProblemsNavigator nav = (ProblemsNavigator)jawecomponent;      
      
      nav.cleanMatches();
      
      setEnabled(false);
   }   
}
