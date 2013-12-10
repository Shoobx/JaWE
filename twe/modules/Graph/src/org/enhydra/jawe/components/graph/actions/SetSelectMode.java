/**
 * Miroslav Popov, Sep 20, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.components.graph.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphEAConstants;
import org.enhydra.shark.xpdl.XMLUtil;

/**
 * @author Miroslav Popov
 *
 */
public class SetSelectMode extends SetToolboxMode {
   
   public SetSelectMode (GraphController jawecomponent) {
      super(jawecomponent, GraphEAConstants.SELECT_TYPE, GraphEAConstants.SELECT_TYPE_DEFAULT);
   }

   public void enableDisableAction() {
      GraphController gc = (GraphController)jawecomponent;
      
      if (gc.getSelectedGraph() != null && XMLUtil.getPackage(gc.getSelectedGraph().getXPDLObject()) == JaWEManager.getInstance().getJaWEController().getMainPackage())      
         setEnabled(true);
      else      
         setEnabled(false);      
   }
   
   public void actionPerformed(ActionEvent e) {    
      GraphController gc = (GraphController)jawecomponent;
      
      gc.getGraphMarqueeHandler().setType(type, subType, null);
   }
}
