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

package org.enhydra.jawe.components.ldap;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.enhydra.jawe.base.controller.JaWEActions;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XPDLConstants;

/**
 * Creates a panel for LDAP component.
 */
public class LDAPTablePanel extends JPanel {

   protected JTable allItems;

   protected Vector columnNames;

   protected LDAPController controller;

   /**
    * Creates a table panel for displaying remote process definitions.
    */
   public LDAPTablePanel(LDAPController controller) {
      this.controller = controller;
      setLayout(new BorderLayout());
      columnNames = getColumnNames();
      allItems = createTable();
      setupTable();

      add(createScrollPane());
   }

   public JTable getTable() {
      return allItems;
   }

   public List getSelectedElements() {
      List els = new ArrayList();
      int[] row = allItems.getSelectedRows();
      if (row != null) {
         for (int i = 0; i < row.length; i++) {
            els.add((LDAPEntryInfo) allItems.getValueAt(row[i], 0));
         }
      }
      return els;

   }

   public List getAllElements() {
      List els = new ArrayList();
      for (int i = 0; i < allItems.getRowCount(); i++) {
         els.add((LDAPEntryInfo) allItems.getValueAt(i, 0));
      }
      return els;
   }

   public boolean setSelectedElement(Object el) {
      try {
         int rc = allItems.getRowCount();
         if (rc > 0) {
            for (int i = 0; i < rc; i++) {
               if (el == allItems.getValueAt(i, 0)) {
                  allItems.setRowSelectionInterval(i, i);

                  // focus the row

                  JViewport viewport = (JViewport) allItems.getParent();
                  // This rectangle is relative to the table where the
                  // northwest corner of cell (0,0) is always (0,0).
                  Rectangle rect = allItems.getCellRect(i, 0, true);
                  // The location of the viewport relative to the table
                  Point pt = viewport.getViewPosition();
                  // Translate the cell location so that it is relative
                  // to the view, assuming the northwest corner of the
                  // view is (0,0)
                  rect.setLocation(rect.x - pt.x, rect.y - pt.y);
                  // Scroll the area into view
                  viewport.scrollRectToVisible(rect);
                  scrollRectToVisible(rect);
                  return true;
               }
            }
         }
      } catch (Exception ex) {
      }
      return false;
   }

   public void addRow(LDAPEntryInfo dinfo) {
      int rowpos = allItems.getRowCount();
      DefaultTableModel dtm = (DefaultTableModel) allItems.getModel();
      Vector v = getRow(dinfo);
      dtm.insertRow(rowpos, v);
   }

   public void removeRow(int row) {
      DefaultTableModel dtm = (DefaultTableModel) allItems.getModel();
      dtm.removeRow(row);
   }

   public void updateRow(int row) {
      DefaultTableModel dtm = (DefaultTableModel) allItems.getModel();
      LDAPEntryInfo di = (LDAPEntryInfo) dtm.getValueAt(row, 0);
      for (int i = 0; i < di.toElements().size(); i++) {
         dtm.setValueAt(di.get(i).toValue(), row, i + 1);
      }
   }

   protected JTable createTable() {
      // return new SortingTable(this, new Vector(), columnNames) {

      JTable t = new JTable(new Vector(), columnNames) {
         public boolean isCellEditable(int row, int col) {
            return false;
         }

         public String getToolTipText(MouseEvent e) {
            java.awt.Point p = e.getPoint();
            int rowIndex = rowAtPoint(p);
            int colIndex = columnAtPoint(p);
            int realColumnIndex = convertColumnIndexToModel(colIndex);
            DefaultTableModel dtm = (DefaultTableModel) this.getModel();
            String tooltip = null;
            try {
               tooltip = dtm.getValueAt(rowIndex, realColumnIndex).toString();
            } catch (Exception ex) {
               tooltip = super.getToolTipText(e);
            }
            return tooltip;
         }

      };
      
      Color bkgCol = new Color(245, 245, 245);
      if (controller.getSettings() instanceof LDAPSettings) {
         bkgCol = ((LDAPSettings) controller.getSettings()).getBackgroundColor();
      }
      t.setBackground(bkgCol);
      return t;
      
   }

   protected void setupTable() {
      TableColumn column;
      // setting the first column (object column) to be invisible
      column = allItems.getColumnModel().getColumn(0);
      column.setMinWidth(0);
      column.setMaxWidth(0);
      column.setPreferredWidth(0);
      column.setResizable(false);

      column = allItems.getColumnModel().getColumn(1);
      column.setCellRenderer(new LDAPTableCellRenderer());
      column.setMinWidth(20);
      column.setMaxWidth(20);
      column.setPreferredWidth(20);
      column.setResizable(false);

      // allItems.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

      // setting some table properties
      allItems.setColumnSelectionAllowed(false);
      allItems.setRowSelectionAllowed(true);
      allItems.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      allItems.getTableHeader().setReorderingAllowed(false);

      // mouse listener for editing on double-click
      allItems.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent me) {
            if (me.getClickCount() > 1) {
               editElementAction.actionPerformed(null);
            }
         }

      });
      allItems.getInputMap(JComponent.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "edit");
      allItems.getActionMap().put("edit", editElementAction);

   }

   protected Action editElementAction = new AbstractAction(JaWEActions.EDIT_PROPERTIES_ACTION) {
      public void actionPerformed(ActionEvent ae) {
         List l = getSelectedElements();
         if (l.size() > 0) {
            LDAPEntryInfo di = (LDAPEntryInfo) getSelectedElements().get(0);
            if (di != null) {
               new LDAPEntryInfoEditor(di);
            }
         }
      }
   };

   protected Vector getColumnNames() {
      // creating a table which do not allow cell editing
      Vector cnames = new Vector();
      cnames.add("Object");
      cnames.add("");
      cnames.add(controller.getSettings().getLanguageDependentString("IdKey"));
      cnames.add(controller.getSettings().getLanguageDependentString("NameKey"));
      cnames.add(controller.getSettings().getLanguageDependentString("DescriptionKey"));
      return cnames;
   }

   public void fillTableContent(List defInfos) {
      DefaultTableModel dtm = (DefaultTableModel) allItems.getModel();
      Iterator it = defInfos.iterator();

      while (it.hasNext()) {
         LDAPEntryInfo di = (LDAPEntryInfo) it.next();
         Vector v = getRow(di);
         dtm.addRow(v);
      }
   }

   protected Vector getRow(LDAPEntryInfo di) {
      Vector v = new Vector();
      v.add(di);
      if (di.getType().equals(XPDLConstants.PARTICIPANT_TYPE_HUMAN)) {
         v.add(((LDAPSettings) controller.getSettings()).getUserIcon());
      } else if (di.getType().equals(XPDLConstants.PARTICIPANT_TYPE_ORGANIZATIONAL_UNIT)) {
         v.add(((LDAPSettings) controller.getSettings()).getGroupIcon());
      } else {
         v.add(((LDAPSettings) controller.getSettings()).getRoleIcon());
      }
      v.add(di.getId());
      v.add(di.getName());
      v.add(di.getDescription());
      return v;
   }

   protected JScrollPane createScrollPane() {
      // creates panel
      JScrollPane allItemsPane = new JScrollPane();

      allItemsPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      allItemsPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

      allItemsPane.setViewportView(allItems);
      return allItemsPane;
   }

   protected int getElementRow(XMLElement el) {
      int row = -1;
      for (int i = 0; i < allItems.getRowCount(); i++) {
         XMLElement toCompare = (XMLElement) allItems.getValueAt(i, 0);
         if (el == toCompare) {
            row = i;
            break;
         }
      }
      return row;
   }

   public void cleanup() {
      DefaultTableModel dtm = (DefaultTableModel) allItems.getModel();
      for (int i = dtm.getRowCount() - 1; i >= 0; i--) {
         dtm.removeRow(i);
      }
   }

}
