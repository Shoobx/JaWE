package org.enhydra.jawe.base.controller.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.NewActionBase;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.base.controller.JaWETypeChoiceButton;
import org.enhydra.jawe.base.xpdlobjectfactory.XPDLObjectFactory;
import org.enhydra.shark.xpdl.elements.Package;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;

/**
 * Class that realizes <B>new</B> action.
 * 
 * @author Sasa Bojanic
 */
public class PackageNewProcess extends NewActionBase {

   public PackageNewProcess(JaWEComponent jawecomponent) {
      super(jawecomponent, WorkflowProcess.class, null);
   }

   public void enableDisableAction() {
      JaWEController jc = (JaWEController) jawecomponent;
      if (jc.getMainPackage() != null)
         setEnabled(true);
      else
         setEnabled(false);
   }

   public void actionPerformed(ActionEvent e) {
      if (!(e.getSource() instanceof JaWETypeChoiceButton)) {
         JaWEController jc = JaWEManager.getInstance().getJaWEController();
         Package pkg = jc.getMainPackage();
         if (pkg == null)
            return;
         jc.startUndouableChange();
         XPDLObjectFactory of = JaWEManager.getInstance().getXPDLObjectFactory();
         WorkflowProcess wp = of.createXPDLObject(pkg.getWorkflowProcesses(),
                                                  jc.getJaWETypes()
                                                     .getDefaultType(WorkflowProcess.class,null),
                                                  true);
         List toSelect = new ArrayList();
         toSelect.add(wp);
         jc.endUndouableChange(toSelect);
      }
   }

}
