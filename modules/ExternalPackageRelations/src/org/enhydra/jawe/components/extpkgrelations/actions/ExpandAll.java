package org.enhydra.jawe.components.extpkgrelations.actions;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.components.XPDLTreeUtil;
import org.enhydra.jawe.components.extpkgrelations.ExtPkgRelations;
import org.enhydra.jawe.components.extpkgrelations.ExtPkgRelationsPanel;

/**
 * @author Sasa Bojanic
 */
public class ExpandAll extends ActionBase {

   public ExpandAll(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }
   
   public void enableDisableAction() {      
      if (((ExtPkgRelations) jawecomponent).hasRelations()) {
         setEnabled(true);
      } else {
         setEnabled(false);
      }
   }
   
   public void actionPerformed(ActionEvent e) {      
      ExtPkgRelations tcon = (ExtPkgRelations) jawecomponent;

      ExtPkgRelationsPanel panel = (ExtPkgRelationsPanel) (tcon.getView());
      TreePath tp=panel.getTree().getSelectionPath();
      
      if (tp==null) {
         tp=new TreePath(panel.getTree().getModel().getRoot());
      }
      XPDLTreeUtil.expandOrCollapsToLevel(panel.getTree(), tp, -1, true);
   }
}
