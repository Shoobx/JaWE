/**
* Together Workflow Editor
* Copyright (C) 2011 Together Teamsolutions Co., Ltd. 
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
import org.enhydra.jawe.components.graph.GraphUtilities;
import org.enhydra.jxpdl.elements.Package;
import org.enhydra.jxpdl.elements.WorkflowProcess;

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
