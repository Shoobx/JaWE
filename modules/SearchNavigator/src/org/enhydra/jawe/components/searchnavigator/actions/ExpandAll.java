package org.enhydra.jawe.components.searchnavigator.actions;

import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;
import javax.swing.tree.TreePath;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.components.XPDLTreeUtil;
import org.enhydra.jawe.components.searchnavigator.SearchNavigator;
import org.enhydra.jawe.components.searchnavigator.SearchNavigatorPanel;

/**
 * @author Miroslav Popov
 */
public class ExpandAll extends ActionBase {

   public ExpandAll(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }
   
   public void enableDisableAction() {      
      if (((SearchNavigator) jawecomponent).hasMatches()) {
         setEnabled(true);
      } else {
         setEnabled(false);
      }
   }
   
   public void actionPerformed(ActionEvent e) {      
      SearchNavigator tcon = (SearchNavigator) jawecomponent;

      SearchNavigatorPanel panel = (SearchNavigatorPanel) (tcon.getView());

      TreePath tp=null;
      if (e.getSource() instanceof JMenuItem) {
         tp=panel.getTree().getPathForLocation(panel.getMouseClickLocation().x, panel.getMouseClickLocation().y);
      }
      if (tp==null) {
         tp=panel.getTree().getSelectionPath();
         if (tp==null) {
            tp=new TreePath(panel.getTree().getModel().getRoot());
         }
      }
      panel.getTree().removeTreeSelectionListener(tcon);
      XPDLTreeUtil.expandOrCollapsToLevel(panel.getTree(), tp, -1, true);
      panel.getTree().addTreeSelectionListener(tcon);
   }
}
