/**
 * Miroslav Popov, Aug 29, 2005
 */
package org.enhydra.jawe.components.graph.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEConstants;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphUtilities;
import org.enhydra.shark.xpdl.XMLCollectionElement;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.elements.ActivitySet;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;

/**
 * @author Miroslav Popov
 *
 */
public class InsertActivitySet extends ActionBase {

   public InsertActivitySet(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      JaWEController jc = JaWEManager.getInstance().getJaWEController();
      GraphController gc = (GraphController)jawecomponent;
      
      if (gc.getSelectedGraph() != null)
         if (gc.getSelectedGraph().getXPDLObject() instanceof WorkflowProcess)
            if (XMLUtil.getPackage(gc.getSelectedGraph().getXPDLObject()) == jc.getMainPackage()) {      
               setEnabled(true);
               return;
            }
            
      setEnabled(false);      
   }
   
   public void actionPerformed(ActionEvent e) {
      GraphController gc=GraphUtilities.getGraphController();
      XMLCollectionElement wpOrAs=gc.getDisplayedXPDLObject();
      if (wpOrAs!=null) {
         WorkflowProcess wp=XMLUtil.getWorkflowProcess(wpOrAs);
         if (wp!=null) {            
            JaWEManager.getInstance().getJaWEController().startUndouableChange();                 
            ActivitySet as=JaWEManager.getInstance().getXPDLObjectFactory().createXPDLObject(wp.getActivitySets(), JaWEConstants.ACTIVITY_SET_TYPE_DEFAULT, true);
            List toSelect=new ArrayList();
            toSelect.add(as);      
            JaWEManager.getInstance().getJaWEController().endUndouableChange(toSelect);
//            ((GraphController) jawecomponent).selectGraphForElement(as);
         }
      }
   }
}
