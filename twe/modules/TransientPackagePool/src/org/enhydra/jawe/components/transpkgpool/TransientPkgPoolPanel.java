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

package org.enhydra.jawe.components.transpkgpool;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.enhydra.jawe.BarFactory;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.XPDLElementChangeInfo;
import org.enhydra.jawe.base.controller.JaWEActions;
import org.enhydra.jawe.components.XPDLTreeCellRenderer;
import org.enhydra.jawe.components.XPDLTreeNode;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLElementChangeInfo;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.Package;

/**
 * Panel for displaying transient package pool.
 * 
 * @author Sasa Bojanic
 */
public class TransientPkgPoolPanel extends JPanel implements JaWEComponentView {

   protected TransientPkgPoolTreeModel treeModel;

   protected JTree tree;

   protected JToolBar toolbar;

   protected JScrollPane scrollPane;

   protected TransientPkgPool controller;

   protected int xClick, yClick;

   protected MouseListener mouseListener;

   protected XPDLTreeCellRenderer renderer;

   public TransientPkgPoolPanel(TransientPkgPool treeViewCtrl) {
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
      init();
   }

   public void init() {
      controller.getSettings().adjustActions();
      treeModel = new TransientPkgPoolTreeModel(controller);
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
      tree.addTreeSelectionListener(controller);

      /** MouseListener for JTree */
      mouseListener = new MouseAdapter() {
         public void mouseClicked(MouseEvent me) {
            xClick = me.getX();
            yClick = me.getY();
            TreePath path = tree.getPathForLocation(xClick, yClick);

            if (path != null) {
               tree.setAnchorSelectionPath(path);

               if (SwingUtilities.isRightMouseButton(me)) {
                  if (!tree.isPathSelected(path)) {
                     tree.setSelectionPath(path);
                  }

                  JPopupMenu popup = BarFactory.createPopupMenu("default", controller);

                  popup.show(tree, me.getX(), me.getY());
               }

               XPDLTreeNode node = (XPDLTreeNode) path.getLastPathComponent();

               if (me.getClickCount() > 1 && !SwingUtilities.isRightMouseButton(me) && tree.getModel().isLeaf(node)) {
                  JaWEManager.getInstance().getJaWEController().getJaWEActions().getAction(JaWEActions.EDIT_PROPERTIES_ACTION).actionPerformed(null);
               }
            } else {
               TreePath close = tree.getClosestPathForLocation(xClick, yClick);
               Rectangle rect = tree.getPathBounds(close);
               if (rect == null || !(rect.y < yClick && rect.y + rect.height > yClick)) {
                  JaWEManager.getInstance().getJaWEController().getSelectionManager().setSelection((XMLElement) null, false);
                  tree.clearSelection();
               }
            }

            tree.getParent().requestFocus();
         }
      };

      tree.addMouseListener(mouseListener);

      // creates panel
      scrollPane = new JScrollPane();
      scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
      scrollPane.setViewportView(tree);
      JViewport port = scrollPane.getViewport();
      port.setScrollMode(JViewport.BLIT_SCROLL_MODE);
      scrollPane.setBackground(Color.lightGray);
      add(scrollPane, BorderLayout.CENTER);
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

   public Point getMouseClickLocation() {
      return new Point(xClick, yClick);
   }

   public void refreshTransientPkgPanel(XPDLElementChangeInfo info) {
      int action = info.getAction();
      XMLElement el = info.getChangedElement();
      List l = info.getChangedSubElements();

      tree.removeTreeSelectionListener(controller);

      if (action == XPDLElementChangeInfo.SELECTED) {
         tree.clearSelection();
      }

      if (action == XPDLElementChangeInfo.VALIDATION_ERRORS) {
         renderer.setValidationErrors(info.getChangedElement(), info.getChangedSubElements());
         tree.repaint();
      }

      if (el instanceof Package) {
         if (action == XMLElementChangeInfo.INSERTED) {
            if (l != null && l.size() > 0) {
               Iterator it1 = l.iterator();
               while (it1.hasNext()) {
                  Package pkg = (Package) it1.next();
                  if (pkg.isTransient()) {
                     treeModel.insertNode(pkg);
                  }
               }
            } else {
               Package pkg = (Package) el;
               if (pkg.isTransient()) {
                  treeModel.insertNode(pkg);
               }
            }
            controller.getSettings().adjustActions();
         } else if (action == XMLElementChangeInfo.REMOVED) {
            if (l != null && l.size() > 0) {
               Iterator it1 = l.iterator();
               while (it1.hasNext()) {
                  Package pkg = (Package) it1.next();
                  if (pkg.isTransient()) {
                     treeModel.removeNode(pkg);
                  }
               }
            } else {
               Package pkg = (Package) el;
               if (pkg.isTransient()) {
                  treeModel.removeNode(el);
               }
            }
            if (treeModel.getRootNode().getChildCount() == 0) {
               reinitialize();
               return;
            }
            controller.getSettings().adjustActions();
         }
      }
      if (action == XPDLElementChangeInfo.SELECTED) {
         if (el != null) {
            List toSelect = new ArrayList();
            if (l != null) {
               toSelect.addAll(l);
            }
            if (toSelect.size() == 0) {
               toSelect.add(el);
            }
            for (int i = 0; i < toSelect.size(); i++) {
               Package toSel = XMLUtil.getPackage((XMLElement) toSelect.get(i));
               if (toSel != null && toSel.isTransient()) {
                  XPDLTreeNode n = treeModel.findNode(toSel);
                  TreePath tp = null;
                  if (n != null) {
                     tp = new TreePath(n.getPath());
                     tree.addSelectionPath(tp);
                  }
                  if (tp != null) {
                     tree.scrollPathToVisible(tp);
                     break;
                  }
               }
            }
         }
         controller.getSettings().adjustActions();
      }

      tree.addTreeSelectionListener(controller);
   }

   protected void reinitialize() {
      remove(scrollPane);
      treeModel.clearTree();
      tree.getSelectionModel().clearSelection();
      tree.removeMouseListener(mouseListener);
      tree.removeTreeSelectionListener(controller);
      tree.setCellRenderer(null);
      init();
   }

   // before doing this, listener has to be removed
   public void setCurrentSelection() {
      List toSelect = JaWEManager.getInstance().getJaWEController().getSelectionManager().getSelectedElements();
      for (int i = 0; i < toSelect.size(); i++) {
         XMLElement toSel = (XMLElement) toSelect.get(i);
         if (toSel instanceof Package) {
            XPDLTreeNode n = treeModel.findNode(toSel);
            TreePath tp = null;
            if (n != null) {
               tp = new TreePath(n.getPath());
               tree.addSelectionPath(tp);
            }
            if (tp != null) {
               tree.scrollPathToVisible(tp);
            }
         }
      }
   }

}
