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

package org.enhydra.jawe.components.packagenavigator;

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
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLElementChangeInfo;
import org.enhydra.jxpdl.elements.Activities;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ActivitySet;
import org.enhydra.jxpdl.elements.ActivitySets;
import org.enhydra.jxpdl.elements.Package;
import org.enhydra.jxpdl.elements.Transition;
import org.enhydra.jxpdl.elements.Transitions;
import org.enhydra.jxpdl.elements.WorkflowProcess;
import org.enhydra.jxpdl.elements.WorkflowProcesses;

/**
 * Used to display Package hierarchy tree.
 *
 * @author Sasa Bojanic
 */
public class PackageNavigatorPanel extends JPanel implements JaWEComponentView {

   protected PackageNavigatorTreeModel treeModel;

   protected JTree tree;

   protected JToolBar toolbar;

   protected JScrollPane scrollPane;

   protected PackageNavigator controller;

   protected int xClick, yClick;

   protected MouseListener mouseListener;

   protected XPDLTreeCellRenderer renderer;

   public PackageNavigatorPanel(PackageNavigator controller) {
      this.controller = controller;
   }

   public void configure() {
      // setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
      setBorder(BorderFactory.createEtchedBorder());
      setLayout(new BorderLayout());
      toolbar = BarFactory.createToolbar("defaultToolbar", controller);
      toolbar.setFloatable(false);
      if (toolbar.getComponentCount() > 0) {
         add(toolbar, BorderLayout.NORTH);
      }
      init();
   }

   public void printTreeModel() {
      printTreeModel();
   }

   public void printTreeModel(XPDLTreeNode n) {
      System.err.println("There are " + n.getChildCount() + " children for " + n.getXPDLElement());
      for (int i = 0; i < n.getChildCount(); i++) {
         printTreeModel((XPDLTreeNode) n.getChildAt(i));
      }

   }

   public void init() {
      controller.getSettings().adjustActions();
      treeModel = new PackageNavigatorTreeModel(controller);

      tree = new JTree(treeModel) {
         public void scrollRectToVisible(Rectangle aRect) {
            aRect.x = scrollPane.getHorizontalScrollBar().getValue();
            super.scrollRectToVisible(aRect);
         }
      };

      // setting some tree properties
      tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
      tree.setRootVisible(false);
      tree.setShowsRootHandles(true);
      renderer = new XPDLTreeCellRenderer(controller);
      Color bckColor = controller.getNavigatorSettings().getBackGroundColor();

      renderer.setBackgroundNonSelectionColor(bckColor);
      renderer.setBackgroundSelectionColor(controller.getNavigatorSettings().getSelectionColor());
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
      // scrollPane.getVerticalScrollBar().setUnitIncrement(20);
      // scrollPane.getHorizontalScrollBar().setUnitIncrement(40);

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

   public void handleXPDLChangeEvent(XPDLElementChangeInfo info) {
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

      if (el instanceof Activity
          || el instanceof Package || el instanceof WorkflowProcesses || el instanceof WorkflowProcess || el instanceof ActivitySets
          || el instanceof ActivitySet || el instanceof Activities || (el instanceof Transitions && action == XPDLElementChangeInfo.SELECTED)) {
         if (action == XMLElementChangeInfo.INSERTED) {
            if (l != null && l.size() > 0) {
               Iterator it1 = l.iterator();
               while (it1.hasNext()) {
                  treeModel.insertNode((XMLElement) it1.next());
               }
            } else {
               if (el instanceof Package) {
                  treeModel.insertNode(el);
               }
            }
            if (el instanceof Package) {
               controller.getSettings().adjustActions();
            }
         } else if (action == XMLElementChangeInfo.REMOVED) {
            if (l != null && l.size() > 0) {
               Iterator it1 = l.iterator();
               while (it1.hasNext()) {
                  treeModel.removeNode((XMLElement) it1.next());
               }
            } else {
               treeModel.removeNode(el);
            }
            if (treeModel.getRootNode().getChildCount() == 0) {
               reinitialize();
               return;
            }
         } else if (action == XPDLElementChangeInfo.SELECTED) {
            if (el != null) {
               List toSelect = new ArrayList();
               if (l != null) {
                  toSelect.addAll(l);
               }
               if (toSelect.size() == 0) {
                  toSelect.add(el);
               }
               for (int i = 0; i < toSelect.size(); i++) {
                  XMLElement toSel = (XMLElement) toSelect.get(i);
                  if (toSelect instanceof Transitions || toSelect instanceof Transition) {
                     continue;
                  }
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
         } else if (action == XMLElementChangeInfo.REPOSITIONED) {
            List elsToReposition = new ArrayList();
            List newPositions = new ArrayList();
            if (el instanceof XMLCollection) {
               if (l != null) {
                  elsToReposition.addAll(l);
                  newPositions.addAll((List) info.getNewValue());
               }
               for (int j = 0; j < elsToReposition.size(); j++) {
                  XMLElement eltr = (XMLElement) elsToReposition.get(j);
                  treeModel.repositionNode(eltr, ((Integer) newPositions.get(j)).intValue());
               }
            }
         }
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
         if (toSel instanceof Package || toSel instanceof WorkflowProcess || toSel instanceof ActivitySet || toSel instanceof Activity) {
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
