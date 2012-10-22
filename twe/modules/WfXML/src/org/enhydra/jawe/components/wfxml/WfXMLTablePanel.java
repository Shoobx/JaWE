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

package org.enhydra.jawe.components.wfxml;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.panel.panels.XMLBasicPanel;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLElementChangeInfo;
import org.enhydra.jxpdl.XMLElementChangeListener;

/**
 * Creates a panel for WfXML.
 */
public class WfXMLTablePanel extends XMLBasicPanel implements XMLElementChangeListener {

   protected JTable allItems;

   protected Vector columnNames;

   protected DefInfo dummy;

   protected WfXML wfxml;

   /**
    * Creates a table panel for displaying remote process definitions.
    * 
    * @param tooltip TODO
    */
   public WfXMLTablePanel(WfXML wfxml, String tooltip) {
      super(null,
            wfxml.getDefInfos(),
            wfxml.getSettings().getLanguageDependentString("ProcessDefinitionListKey"),
            true,
            true,
            false,
            tooltip);
      this.wfxml = wfxml;
      myOwner.addListener(this);
      myOwner.setNotifyListeners(true);
      dummy = (DefInfo) wfxml.getDefInfos().generateNewElement();
      setLayout(new BorderLayout());
      columnNames = getColumnNames(wfxml);
      allItems = createTable();
      setupTable();

      add(createScrollPane());
   }

   public JTable getTable() {
      return allItems;
   }

   public DefInfo getSelectedElement() {
      int row = allItems.getSelectedRow();
      if (row >= 0) {
         return (DefInfo) allItems.getValueAt(row, 0);
      }
      return null;

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

   public void setSelectedRow(int row) {
      try {
         allItems.setRowSelectionInterval(row, row);

         JaWEManager.getInstance()
            .getJaWEController()
            .getSelectionManager()
            .setSelection(getSelectedElement(), true);

      } catch (Exception e) {
      }
   }

   public void addRow(DefInfo dinfo) {
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
      DefInfo di = (DefInfo) dtm.getValueAt(row, 0);
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
      if (wfxml.getSettings() instanceof WfXMLSettings) {
         bkgCol = ((WfXMLSettings) wfxml.getSettings()).getBackgroundColor();
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
      column.setPreferredWidth(475);

      column = allItems.getColumnModel().getColumn(2);
      column.setPreferredWidth(250);

      column = allItems.getColumnModel().getColumn(3);
      column.setPreferredWidth(375);

      column = allItems.getColumnModel().getColumn(4);
      column.setPreferredWidth(50);

      column = allItems.getColumnModel().getColumn(5);
      column.setPreferredWidth(50);

      // allItems.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

      // setting some table properties
      allItems.setColumnSelectionAllowed(false);
      allItems.setRowSelectionAllowed(true);
      allItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      allItems.getTableHeader().setReorderingAllowed(false);

      // mouse listener for editing on double-click
      allItems.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent me) {
            if (me.getClickCount() > 1) {
               DefInfo di = getSelectedElement();
               if (di != null) {
                  new DefInfoEditor(di);
                  updateRow(allItems.getSelectedRow());
               }
            }
         }

      });

   }

   protected Vector getColumnNames(WfXML pWfxml) {
      // creating a table which do not allow cell editing
      Vector cnames = new Vector();
      cnames.add("Object");
      Iterator it = dummy.toElements().iterator();
      while (it.hasNext()) {
         XMLElement el = (XMLElement) it.next();
         cnames.add(pWfxml.getSettings().getLanguageDependentString(el.toName() + "Key"));
      }
      return cnames;
   }

   public void fillTableContent(List defInfos) {
      DefaultTableModel dtm = (DefaultTableModel) allItems.getModel();
      Iterator it = defInfos.iterator();

      while (it.hasNext()) {
         DefInfo di = (DefInfo) it.next();
         Vector v = getRow(di);
         dtm.addRow(v);
      }
   }

   protected Vector getRow(DefInfo di) {
      Vector v = new Vector();
      Iterator itAllElems = di.toElements().iterator();
      while (itAllElems.hasNext()) {
         XMLElement el = (XMLElement) itAllElems.next();
         v.add(el.toValue());
      }
      v.add(0, di);
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

   public void xmlElementChanged(XMLElementChangeInfo info) {
      if (info.getAction() == XMLElementChangeInfo.REMOVED) {
         Iterator it = info.getChangedSubElements().iterator();
         while (it.hasNext()) {
            XMLElement el = (XMLElement) it.next();
            int row = getElementRow(el);
            if (row != -1) {
               removeRow(row);
            }
         }
      } else if (info.getAction() == XMLElementChangeInfo.INSERTED) {
         Iterator it = info.getChangedSubElements().iterator();
         while (it.hasNext()) {
            DefInfo el = (DefInfo) it.next();
            addRow(el);
         }
      }
   }

}
