/**
* Together Workflow Editor
* Copyright (C) 2010 Together Teamsolutions Co., Ltd. 
* 
* This program is free software: you can redistribute it and/or modify 
* it under the terms of the GNU General Public License as published by 
* the Free Software Foundation, either version 3 of the License, or 
* (at your option) any later version. 
*
* This program is distributed in the hope that it will be useful, 
* but WITHOUT ANY WARRANTY; without even the implied warranty of 
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
* GNU General Public License for more details. 
*
* You should have received a copy of the GNU General Public License 
* along with this program. If not, see http://www.gnu.org/licenses
*/

package org.enhydra.jawe;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.enhydra.jxpdl.elements.ActivitySet;
import org.enhydra.jxpdl.elements.Package;
import org.enhydra.jxpdl.elements.WorkflowProcess;

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
