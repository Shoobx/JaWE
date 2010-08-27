/**
 * Miroslav Popov, Oct 14, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.base.editor.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.editor.TogWEStandardXPDLElementEditor;
import org.enhydra.jawe.base.panel.InlinePanel;
import org.enhydra.shark.xpdl.XMLElement;

/**
 * @author Miroslav Popov
 *
 */
public class Revert extends ActionBase {

   protected InlinePanel ipc;
   
   public Revert (JaWEComponent jawecomponent) {
      super(jawecomponent);
      this.ipc=(InlinePanel)((TogWEStandardXPDLElementEditor)jawecomponent).getView();
      setEnabled(false);
   }

   public void enableDisableAction() {
      if (ipc.isModified()) {
         setEnabled(true);
      } else {
         setEnabled(false);
      }
   }

   public void actionPerformed(ActionEvent e) {
      XMLElement el = ipc.getActiveElement();
      
      ipc.setActiveElement(el);
      JaWEManager.getInstance().getJaWEController().getSelectionManager().setSelection(el, true);
   }
}
