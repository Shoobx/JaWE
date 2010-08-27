/**
 * Miroslav Popov, Sep 20, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.components.graph.actions;

import javax.swing.ImageIcon;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphEAConstants;
import org.enhydra.jawe.components.graph.GraphSettings;
import org.enhydra.shark.xpdl.XMLUtil;

/**
 * @author Miroslav Popov
 *
 */
public class SetStartMode extends SetToolboxMode {

   public SetStartMode (GraphController jawecomponent) {
      super(jawecomponent,GraphEAConstants.START_TYPE, GraphEAConstants.START_TYPE_DEFAULT);
   }

   public void enableDisableAction() {
      GraphController gc = (GraphController)jawecomponent;
      
      if (gc.getGraphSettings().shouldUseBubbles() && gc.getSelectedGraph() != null && XMLUtil.getPackage(gc.getSelectedGraph().getXPDLObject()) == JaWEManager.getInstance().getJaWEController().getMainPackage() &&
            gc.getSelectedGraph().getRoots().length != 0)      
         setEnabled(true);
      else      
         setEnabled(false);      
   }

   protected ImageIcon getIcon () {
      return ((GraphSettings)jawecomponent.getSettings()).getBubbleStartIcon();
   }

}
