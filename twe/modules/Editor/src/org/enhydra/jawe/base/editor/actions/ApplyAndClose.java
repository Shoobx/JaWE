package org.enhydra.jawe.base.editor.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.base.editor.TogWEStandardXPDLElementEditor;

/**
 * @author Zoran Milakovic
 */
public class ApplyAndClose extends Apply {

   public ApplyAndClose (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void actionPerformed(ActionEvent e) {
      super.actionPerformed(e);
      ((TogWEStandardXPDLElementEditor)jawecomponent).close();
   }
   
}