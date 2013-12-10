/**
 * Miroslav Popov, Sep 20, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.components.graph.actions;

import org.enhydra.jawe.JaWEConstants;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.shark.xpdl.XMLUtil;

/**
 * @author Miroslav Popov
 *
 */
public class SetTransitionMode extends SetToolboxMode {

   public SetTransitionMode (GraphController jawecomponent,String subType) {
      super(jawecomponent, JaWEConstants.TRANSITION_TYPE, subType);
   }

   public void enableDisableAction() {
      GraphController gc = (GraphController)jawecomponent;
      
      if (gc.getSelectedGraph() != null && XMLUtil.getPackage(gc.getSelectedGraph().getXPDLObject()) == JaWEManager.getInstance().getJaWEController().getMainPackage()
            && gc.getSelectedGraph().getRoots().length != 0)      
         setEnabled(true);
      else      
         setEnabled(false);      
   }
   
}
