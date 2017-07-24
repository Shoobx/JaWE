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

package org.enhydra.jawe.base.panel.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jawe.base.panel.PanelSettings;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;

/**
 * Creates a table panel.
 * 
 * @author Sasa Bojanic
 * @author Zoran Milakovic
 * @author Miroslav Popov
 */
public class XMLSimpleTablePanel extends XMLBasicPanel {

   protected static Dimension miniTableDimension = new Dimension(450, 125);

   protected static Dimension smallTableDimension = new Dimension(450, 200);

   protected static Dimension mediumTableDimension = new Dimension(550, 200);

   protected static Dimension largeTableDimension = new Dimension(650, 200);

   protected JTable allItems;

   protected Vector columnNames;

   protected List columnsToShow;

   public XMLSimpleTablePanel(PanelContainer pc,
                              XMLCollection myOwner,
                              List columnsToShow,
                              List elementsToShow,
                              String title,
                              boolean hasBorder,
                              boolean hasEmptyBorder,
                              boolean automaticWidth,
                              String tooltip) {
      super(pc, myOwner, title, true, hasBorder, hasEmptyBorder, tooltip);

      columnNames = getColumnNames(columnsToShow);
      this.columnsToShow = columnsToShow;
      allItems = new JTable(new Vector(), columnNames) {
         public boolean isCellEditable(int row, int col) {
            return false;
         }
      };

      Color bkgCol = new Color(245, 245, 245);
      if (pc.getSettings() instanceof PanelSettings) {
         bkgCol = ((PanelSettings) pc.getSettings()).getBackgroundColor();
      }
      allItems.setBackground(bkgCol);

      setupTable(automaticWidth);
      fillTableContent(elementsToShow);

      add(createScrollPane());

   }

   public JTable getTable() {
      return allItems;
   }

   public XMLElement getSelectedElement() {
      int row = allItems.getSelectedRow();
      if (row >= 0) {
         return (XMLElement) allItems.getValueAt(row, 0);
      }
      return null;

   }

   public void addRow(XMLElement e) {
      int rowpos = allItems.getRowCount();
      DefaultTableModel dtm = (DefaultTableModel) allItems.getModel();
      Vector v = getRow(e);
      dtm.insertRow(rowpos, v);
   }

   public void removeRow(XMLElement e) {
      int row = getElementRow(e);
      if (row >= 0) {
         DefaultTableModel dtm = (DefaultTableModel) allItems.getModel();
         dtm.removeRow(row);
      }
   }

   public List getElements() {
      List els = new ArrayList();
      for (int i = 0; i < allItems.getRowCount(); i++) {
         els.add(allItems.getValueAt(i, 0));
      }
      return els;
   }

   protected Vector getColumnNames(List columnsToShow) {
      // creating a table which do not allow cell editing
      Vector cnames = new Vector();
      cnames.add("Object");
      XMLElement cel = ((XMLCollection) getOwner()).generateNewElement();
      if (cel instanceof XMLComplexElement) {
         Iterator it = columnsToShow.iterator();
         while (it.hasNext()) {
            String elemName = (String) it.next();
            XMLElement el = ((XMLComplexElement) cel).get(elemName);
            if (el != null) {
               cnames.add(pc.getLabelGenerator().getLabel(el));
            } else {
               it.remove();
            }
         }
      } else {
         cnames.add(pc.getLabelGenerator().getLabel(cel));
      }
      return cnames;
   }

   protected void setupTable(boolean automaticWidth) {
      TableColumn column;
      // setting the first column (object column) to be invisible
      column = allItems.getColumnModel().getColumn(0);
      column.setMinWidth(0);
      column.setMaxWidth(0);
      column.setPreferredWidth(0);
      column.setResizable(false);
      // setting fields that will not be displayed within the table

      // setting some table properties
      allItems.setColumnSelectionAllowed(false);
      allItems.setRowSelectionAllowed(true);
      allItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      allItems.getTableHeader().setReorderingAllowed(false);

      Dimension tDim;
      int noOfVisibleColumns = columnNames.size() - 1;
      if (noOfVisibleColumns <= 3) {
         tDim = new Dimension(smallTableDimension);
      } else if (noOfVisibleColumns <= 5) {
         tDim = new Dimension(mediumTableDimension);
      } else {
         tDim = new Dimension(largeTableDimension);
      }

      if (automaticWidth) {
         tDim.width = allItems.getPreferredScrollableViewportSize().width;
      }
      allItems.setPreferredScrollableViewportSize(new Dimension(tDim));

   }

   protected void fillTableContent(List elementsToShow) {
      DefaultTableModel dtm = (DefaultTableModel) allItems.getModel();
      Iterator it = elementsToShow.iterator();

      while (it.hasNext()) {
         XMLElement elem = (XMLElement) it.next();
         Vector v = getRow(elem);
         dtm.addRow(v);
      }
   }

   protected Vector getRow(XMLElement elem) {
      Vector v = new Vector();
      if (elem instanceof XMLComplexElement) {
         Iterator itAllElems = columnsToShow.iterator();
         v = new Vector();
         XMLComplexElement cmel = (XMLComplexElement) elem;
         while (itAllElems.hasNext()) {
            String elName = (String) itAllElems.next();
            XMLElement el = cmel.get(elName);
            if (el != null) {
               v.add(new XMLElementView(pc, el, XMLElementView.TOVALUE));
            }
         }
      } else {
         v.add(new XMLElementView(pc, elem, XMLElementView.TOVALUE));
      }
      v.add(0, elem);
      return v;
   }

   protected JScrollPane createScrollPane() {
      // creates panel
      JScrollPane allItemsPane = new JScrollPane();
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
   }
}
