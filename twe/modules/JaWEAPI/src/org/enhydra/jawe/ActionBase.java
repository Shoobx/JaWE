package org.enhydra.jawe;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.enhydra.shark.xpdl.elements.ActivitySet;
import org.enhydra.shark.xpdl.elements.Package;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;

/**
* The base class for JaWE actions.
*/
public abstract class ActionBase extends AbstractAction {

   protected JaWEComponent jawecomponent;
   /**
    * The Abstract action uses unqualified class name as action name.
    * @param jawecomponent
    *
    */
   public ActionBase(JaWEComponent jawecomponent) {
      this.jawecomponent=jawecomponent;
      putValue(Action.NAME,Utils.getUnqualifiedClassName(getClass()));
   }

   /**
    * Constructor which accepts the action name.
    * @param jawecomponent
    * @param name Name of this action
    */

   public ActionBase(JaWEComponent jawecomponent, String name) {
      super(name);
      this.jawecomponent=jawecomponent;
   }

   public abstract void enableDisableAction();
   
   public Package getPackage () {
      return JaWEManager.getInstance().getJaWEController().getSelectionManager().getWorkingPKG();
   }
   
   public WorkflowProcess getWorkflowProcess () {
      return JaWEManager.getInstance().getJaWEController().getSelectionManager().getWorkingProcess();
   }
   
   public ActivitySet getActivitySet () {
      return JaWEManager.getInstance().getJaWEController().getSelectionManager().getWorkingActivitySet();
   }
}
