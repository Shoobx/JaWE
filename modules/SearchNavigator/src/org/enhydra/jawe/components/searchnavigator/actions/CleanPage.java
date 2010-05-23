package org.enhydra.jawe.components.searchnavigator.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.components.searchnavigator.SearchNavigator;

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
      SearchNavigator nav = (SearchNavigator)jawecomponent;
      
      if (nav.hasMatches())
         setEnabled(true); 
      else
         setEnabled(false);
   }
   
   public void actionPerformed(ActionEvent e) {      
      SearchNavigator nav = (SearchNavigator)jawecomponent;      
      
      nav.cleanMatches();
      
      setEnabled(false);
   }   
}
