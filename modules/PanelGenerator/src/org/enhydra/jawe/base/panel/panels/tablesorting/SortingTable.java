/**
* Together Workflow Editor
* Copyright (C) 2010 Together Teamsolutions Co., Ltd. 
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

package org.enhydra.jawe.base.panel.panels.tablesorting;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.base.panel.InlinePanel;
import org.enhydra.jawe.base.panel.panels.XMLTablePanel;
import org.enhydra.jawe.base.panel.panels.tablesorting.SortingTableModel;
import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLElement;

public class SortingTable extends JTable {

   protected int sortedColIndex = -1;
   protected boolean ascending = true;
   protected InlinePanel ipc;

   XMLTablePanel owner;

   public SortingTable(XMLTablePanel p,Vector data, Vector names) {
      super(new SortingTableModel((XMLCollection)p.getOwner(),data, names));
      this.owner=p;

      JTableHeader h = getTableHeader();
      h.setDefaultRenderer(new SHRenderer());
      
      MouseListener ml=new MouseAdapter () {
         public void mouseClicked(MouseEvent event) {
            performSorting(event);
         }
      };
      h.addMouseListener(ml);
      
   }
   
   public SortingTable(XMLTablePanel p,Vector data, Vector names, InlinePanel ipc) {
	   this(p, data,  names);
	   this.ipc = ipc;
   }

   public int getSortedColumnIndex() {
      return sortedColIndex;
   }

   public boolean isSortedColumnAscending() {
      return ascending;
   }


   public synchronized void performSorting (MouseEvent event) {
      if(owner.getOwner().isReadOnly() && ((XMLCollection)owner.getOwner()).getParent().isReadOnly()) return;
      if (event==null && sortedColIndex==-1) return;
      if (ipc != null)
    	  ipc.getJaWEComponent().setUpdateInProgress(true);
      TableColumnModel colModel = getColumnModel();
      int index = sortedColIndex;
      if (event!=null) {
         index = colModel.getColumnIndexAtX(event.getX());
      }
      int modelIndex = colModel.getColumn(index).getModelIndex();

      SortingTableModel model = (SortingTableModel)getModel();
 
      Object selEl=null;
      int sr=getSelectedRow();
      if (sr>=0) {
         selEl=model.getValueAt(sr,0);
      }

      if (sortedColIndex == index && event!=null) {
         ascending = !ascending;
      }
      sortedColIndex = index;

      model.sortByColumn(modelIndex, ascending);
      List toSelect = new ArrayList();
      JaWEController jc = null;
      if (ipc != null) {
	      jc = JaWEManager.getInstance().getJaWEController();
	      jc.startUndouableChange();
      }
      XMLCollection c=(XMLCollection)owner.getOwner();
      for (int i=0; i<model.getRowCount(); i++) {
         XMLElement el=(XMLElement)model.getValueAt(i,0);
         if (c.contains(el)) {
        	 
            c.reposition(el, i);
          }
      }
      if (ipc != null) {
	      toSelect.add(c);      
	      jc.endUndouableChange(toSelect);
      }
      if (selEl!=null) {
          try {
             owner.setSelectedElement(selEl);
          } catch (Exception ex) {}
       }
 
      update(getGraphics());
      getTableHeader().update(getTableHeader().getGraphics());
      if (ipc != null)
    	  ipc.getJaWEComponent().setUpdateInProgress(false);
 
   }

   static class SHRenderer extends DefaultTableCellRenderer {

      static Icon NONSORTED = null;
      static Icon ASCENDING = null;
      static Icon DESCENDING = null;

      static {
    	  URL url = SortingTable.class.getClassLoader()
    	  .getResource("org/enhydra/jawe/images/arrowup.gif");
    	  if ( url != null ) {
    		  ASCENDING = new ImageIcon( url );
    	  }else {
    		  
    	  }
    	  url = SortingTable.class.getClassLoader()
    	  .getResource("org/enhydra/jawe/images/arrowdown.gif");

    	  if ( url != null ) {
    		  DESCENDING = new ImageIcon( url );
    	  }
      }

      public SHRenderer() {
         setHorizontalTextPosition(LEFT);
         setHorizontalAlignment(CENTER);
      }

      public Component getTableCellRendererComponent(JTable table,
                                                     Object value,
                                                     boolean isSelected,
                                                     boolean hasFocus,
                                                     int row,
                                                     int col) {
         int index = -1;
         boolean ascending = true;
         SortingTable sortTable = (SortingTable)table;
         index = sortTable.getSortedColumnIndex();
         ascending = sortTable.isSortedColumnAscending();

         if (table != null) {
            JTableHeader header = table.getTableHeader();
            if (header != null) {
               setForeground(header.getForeground());
               setBackground(header.getBackground());
               setFont(header.getFont());
            }
         }

         Icon icon = null;
         if (ascending) {
            icon=ASCENDING;
         } else {
            icon=DESCENDING;
         }

         if (col==index) {
            setIcon(icon);
         } else {
            setIcon(NONSORTED);
         }

         if (value==null) {
            setText("");
         } else {
            setText(value.toString());
         }

         setBorder(UIManager.getBorder("TableHeader.cellBorder"));
         return this;
      }
   }

}

