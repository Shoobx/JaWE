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

package org.enhydra.jawe.components.searchnavigator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
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
import org.enhydra.jawe.SearchResult;
import org.enhydra.jawe.XPDLElementChangeInfo;
import org.enhydra.jawe.base.controller.JaWEActions;
import org.enhydra.jawe.components.XPDLTreeCellRenderer;
import org.enhydra.jawe.components.XPDLTreeNode;
import org.enhydra.jawe.components.XPDLTreeUtil;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLElementChangeInfo;

/**
 * Panel for displaying search results.
 * 
 * @author Sasa Bojanic
 */
public class SearchNavigatorPanel extends JPanel implements JaWEComponentView {

   protected SearchNavigatorTreeModel treeModel;

   protected JTree tree;

   protected JToolBar toolbar;

   protected JLabel searchInfo = new JLabel();

   protected JPanel innerPanel = new JPanel();

   protected JScrollPane scrollPane;

   protected SearchNavigator controller;

   protected int xClick, yClick;

   protected MouseListener mouseListener;

   protected XPDLTreeCellRenderer renderer;

   public SearchNavigatorPanel(SearchNavigator treeViewCtrl) {
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
      innerPanel.add(searchInfo, BorderLayout.NORTH);
      add(innerPanel, BorderLayout.CENTER);
      init();
   }

   public void init() {
      treeModel = new SearchNavigatorTreeModel(controller);
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
         }
      };

      tree.addMouseListener(mouseListener);

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

   public Point getMouseClickLocation() {
      return new Point(xClick, yClick);
   }

   public void refreshDisplay(XMLElement el, List verrors) {
      renderer.setValidationErrors(el, verrors);
      tree.repaint();
   }

   public void refreshSearchPanel(XMLElement el, List referencesOrSearchResults, int action) {
      Set errs = renderer.getErrors();
      Set wrns = renderer.getWarnings();
      reinitialize();

      if (el != null) {
         if (referencesOrSearchResults != null) {
            renderer.setErrors(errs);
            renderer.setWarnings(wrns);
            if (action == XPDLElementChangeInfo.REFERENCES) {
               String elId = null;
               if (el instanceof XMLComplexElement) {
                  XMLElement idEl = ((XMLComplexElement) el).get("Id");
                  if (idEl != null) {
                     elId = idEl.toValue();
                  }
               }
               String t = " "
                          + JaWEManager.getInstance().getLabelGenerator().getLabel(el) + " '"
                          + JaWEManager.getInstance().getDisplayNameGenerator().getDisplayName(el) + "'";
               if (elId != null) {
                  t += ", Id='" + elId + "'";
               }
               t += " - " + referencesOrSearchResults.size() + " " + controller.getSettings().getLanguageDependentString("ReferencesFound");
               searchInfo.setText(t);
            } else {
               String t = " '" + el.toName() + "'";
               t += " - " + referencesOrSearchResults.size() + " " + controller.getSettings().getLanguageDependentString("MatchesFound");
               searchInfo.setText(t);
            }
            for (int i = 0; i < referencesOrSearchResults.size(); i++) {
               XMLElement referenceOrSearchResult = ((SearchResult) referencesOrSearchResults.get(i)).getElement();
               treeModel.insertNode(referenceOrSearchResult);
            }
            if (this.getParent() instanceof JTabbedPane) {
               JTabbedPane jtp = (JTabbedPane) this.getParent();
               Component c = jtp.getSelectedComponent();
               if (c != this) {
                  for (int i = 0; i < jtp.getTabCount(); i++) {
                     c = jtp.getComponentAt(i);
                     if (c == this) {
                        jtp.setSelectedIndex(i);
                        break;
                     }
                  }
               }
            }
            this.requestFocus();
         }
      }
      XPDLTreeUtil.expandOrCollapsToLevel(tree, new TreePath(treeModel.getRootNode()), 3, true);
   }

   public void removeLostReferences(XPDLElementChangeInfo toRem) {
      if (toRem.getAction() == XMLElementChangeInfo.REMOVED) {
         List l = toRem.getChangedSubElements();
         if (l != null) {
            for (int i = 0; i < l.size(); i++) {
               treeModel.removeNode((XMLElement) l.get(i));
            }
         }
      }
   }

   protected boolean hasMatches() {
      return treeModel.getChildCount(treeModel.getRoot()) > 0;
   }

   protected void reinitialize() {
      tree.removeMouseListener(mouseListener);
      tree.removeTreeSelectionListener(controller);
      innerPanel.remove(scrollPane);
      treeModel.clearTree();
      tree.getSelectionModel().clearSelection();
      tree.setCellRenderer(null);
      searchInfo.setText("");
      init();
   }

}
