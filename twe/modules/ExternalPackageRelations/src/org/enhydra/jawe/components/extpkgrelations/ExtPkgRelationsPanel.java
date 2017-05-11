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

package org.enhydra.jawe.components.extpkgrelations;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.enhydra.jawe.BarFactory;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.XPDLElementChangeInfo;
import org.enhydra.jawe.components.XPDLTreeCellRenderer;
import org.enhydra.jawe.components.XPDLTreeNode;
import org.enhydra.jawe.components.XPDLTreeUtil;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLElementChangeInfo;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.ExternalPackages;
import org.enhydra.jxpdl.elements.Package;

/**
 * Panel for displaying search results.
 * 
 * @author Sasa Bojanic
 */
public class ExtPkgRelationsPanel extends JPanel implements JaWEComponentView {

   protected ExtPkgRelationsTreeModel treeModel;

   protected JTree tree;

   protected JToolBar toolbar;

   protected JLabel epInfo = new JLabel();

   protected JPanel innerPanel = new JPanel();

   protected JScrollPane scrollPane;

   protected ExtPkgRelations controller;

   protected XPDLTreeCellRenderer renderer;

   public ExtPkgRelationsPanel(ExtPkgRelations treeViewCtrl) {
      this.controller = treeViewCtrl;
   }

   public void configure() {
      setBorder(BorderFactory.createEtchedBorder());
      setLayout(new BorderLayout());
      toolbar = BarFactory.createToolbar("defaultToolbar", controller);
      toolbar.setFloatable(false);
      if (toolbar.getComponentCount() > 0) {
         add(toolbar, BorderLayout.NORTH);
      }

      innerPanel.setLayout(new BorderLayout());
      innerPanel.add(epInfo, BorderLayout.NORTH);
      add(innerPanel, BorderLayout.CENTER);
      init();
   }

   public void init() {
      treeModel = new ExtPkgRelationsTreeModel(controller);
      tree = new JTree(treeModel) {
         public void scrollRectToVisible(Rectangle aRect) {
            aRect.x = scrollPane.getHorizontalScrollBar().getValue();
            super.scrollRectToVisible(aRect);
         }
      };

      // setting some tree properties
      tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
      tree.setRootVisible(false);
      tree.setShowsRootHandles(true);
      renderer = new XPDLTreeCellRenderer(controller);

      Color bckColor = controller.getExtSettings().getBackgroundColor();

      renderer.setBackgroundNonSelectionColor(bckColor);
      tree.setBackground(bckColor);
      tree.setCellRenderer(renderer);

      scrollPane = new JScrollPane();
      scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
      scrollPane.setViewportView(tree);
      JViewport port = scrollPane.getViewport();
      port.setScrollMode(JViewport.BLIT_SCROLL_MODE);
      scrollPane.setBackground(Color.lightGray);
      innerPanel.add(scrollPane, BorderLayout.CENTER);
   }

   public TreeModel getTreeModel() {
      return treeModel;
   }

   public JTree getTree() {
      return tree;
   }

   public JaWEComponent getJaWEComponent() {
      return controller;
   }

   public JComponent getDisplay() {
      return this;
   }

   public void refreshExtPkgPanel(XPDLElementChangeInfo info) {
      int action = info.getAction();

      if (action == XPDLElementChangeInfo.VALIDATION_ERRORS) {
         renderer.setValidationErrors(info.getChangedElement(), info.getChangedSubElements());
         tree.repaint();
      }

      if (action != XPDLElementChangeInfo.SELECTED && action != XMLElementChangeInfo.REMOVED && action != XMLElementChangeInfo.INSERTED) {
         return;
      }

      Set errs = renderer.getErrors();
      Set wrns = renderer.getWarnings();

      XMLElement el = info.getChangedElement();

      tree.clearSelection();

      if (el == null || (action != XPDLElementChangeInfo.SELECTED && (el instanceof Package || el instanceof ExternalPackages))) {
         reinitialize();
         if (action != XMLElementChangeInfo.INSERTED) {
            return;
         }
      }
      Package pkg = XMLUtil.getPackage(el);

      if (pkg != null && !pkg.isTransient() && !treeModel.getRootNode().isLeaf()) {
         XPDLTreeNode tn = (XPDLTreeNode) treeModel.getRootNode().getFirstChild();
         if (tn != null && tn.getXPDLElement() == pkg && action == XPDLElementChangeInfo.SELECTED) {
            return;
         }
      }

      reinitialize();
      renderer.setErrors(errs);
      renderer.setWarnings(wrns);

      if (pkg != null && !pkg.isTransient()) {
         treeModel.insertNode(pkg);

         XPDLTreeUtil.expandOrCollapsToLevel(tree, new TreePath(treeModel.getRootNode()), 2, true);
      }

      String t = " ";
      if (pkg != null) {
         t += JaWEManager.getInstance().getLabelGenerator().getLabel(pkg) + ", Id='" + pkg.getId() + "'";
      }
      epInfo.setText(t);

   }

   public boolean hasRelations() {
      return !treeModel.getRootNode().isLeaf();
   }

   protected void reinitialize() {
      innerPanel.remove(scrollPane);
      treeModel.clearTree();
      tree.getSelectionModel().clearSelection();
      tree.setCellRenderer(null);
      epInfo.setText("");
      init();
   }

}
