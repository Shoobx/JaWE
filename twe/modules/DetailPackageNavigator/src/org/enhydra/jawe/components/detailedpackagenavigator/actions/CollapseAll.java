/**
 * Miroslav Popov, Aug 2, 2005
 */
package org.enhydra.jawe.components.detailedpackagenavigator.actions;

import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;
import javax.swing.tree.TreePath;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.components.XPDLTreeUtil;
import org.enhydra.jawe.components.detailedpackagenavigator.DetailedPackageNavigator;
import org.enhydra.jawe.components.detailedpackagenavigator.DetailedPackageNavigatorPanel;

/**
 * @author Miroslav Popov
 */
public class CollapseAll extends ActionBase {

   public CollapseAll(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {      
      if (JaWEManager.getInstance().getJaWEController().getMainPackage()!=null) {
         setEnabled(true);
      } else {
         setEnabled(false);
      }
   }
   
   public void actionPerformed(ActionEvent e) {      
      DetailedPackageNavigator tcon = (DetailedPackageNavigator)jawecomponent;
      
      DetailedPackageNavigatorPanel panel = (DetailedPackageNavigatorPanel)(tcon.getView());
      TreePath tp=null;
      if (e.getSource() instanceof JMenuItem) {
         tp=panel.getTree().getPathForLocation(panel.getMouseClickLocation().x, panel.getMouseClickLocation().y);
      }
      int clevel=-1;
      if (tp==null) {
         tp=panel.getTree().getSelectionPath();
         if (tp==null) {
            tp=new TreePath(panel.getTree().getModel().getRoot());
            clevel=1;
         }
      }
      panel.getTree().removeTreeSelectionListener(tcon);
      XPDLTreeUtil.expandOrCollapsToLevel(panel.getTree(),tp,clevel,false);
      if (clevel==1) {
         panel.getTree().clearSelection();
      }
      panel.getTree().addTreeSelectionListener(tcon);
   }
}
