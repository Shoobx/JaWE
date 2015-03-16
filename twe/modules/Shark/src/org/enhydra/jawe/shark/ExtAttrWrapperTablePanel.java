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

package org.enhydra.jawe.shark;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.panel.InlinePanel;
import org.enhydra.jawe.base.panel.PanelSettings;
import org.enhydra.jawe.base.panel.panels.XMLTablePanel;
import org.enhydra.jawe.base.panel.panels.tablesorting.SortingTable;
import org.enhydra.jawe.shark.business.SharkConstants;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.WorkflowProcess;

/**
 * Creates a table panel for a special ext. attr. element.
 * 
 * @author Sasa Bojanic
 */
public class ExtAttrWrapperTablePanel extends XMLTablePanel {

   public ExtAttrWrapperTablePanel(InlinePanel ipc,
                                   XMLCollection myOwner,
                                   String title,
                                   boolean hasBorder,
                                   boolean hasEmptyBorder,
                                   boolean automaticWidth,
                                   boolean miniDimension,
                                   final boolean colors,
                                   final boolean showArrows,
                                   boolean useBasicToolBar) {

      super(ipc,
            myOwner,
            new ArrayList(),
            myOwner.toElements(),
            title,
            hasBorder,
            hasEmptyBorder,
            automaticWidth,
            miniDimension,
            null,
            colors,
            showArrows,
            useBasicToolBar,
            false,
            null,
            false);

   }

   public void focusLost(FocusEvent ev) {
      int sc = allItems.getSelectedColumn();
      if (sc != 2) {
         super.focusLost(ev);
      }
   }

   protected Vector getColumnNames(List columnsToShow) {
      // creating a table which do not allow cell editing
      Vector cnames = new Vector();
      cnames.add("Object");
      cnames.add(pc.getLanguageDependentString("NameKey"));
      cnames.add(pc.getLanguageDependentString("ReadOnlyKey"));
      return cnames;
   }

   protected Vector getRow(XMLElement elem) {
      Vector v = new Vector();
      ExtendedAttribute ea = (ExtendedAttribute) elem;
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(ea);
      String varId = ea.getVValue();
      XMLCollectionElement var = (XMLCollectionElement) wp.getAllVariables().get(varId);
      String name = varId;
      if (var != null) {
         name = var.get("Name").toValue();
         if (name.equals("")) {
            name = varId;
         }
      }
      v.add(name);
      // v.add(new XMLElementView(ipc,var, XMLElementView.TOVALUE));
      if (ea.getName().equals(SharkConstants.VTP_UPDATE)) {
         v.add(new Boolean(false));
      } else {
         v.add(new Boolean(true));
      }
      v.add(0, elem);
      return v;
   }

   protected JTable createTable(final boolean colors) {
      JTable jt = new SortingTable(this, new Vector(), columnNames, ipc) {

         public boolean isCellEditable(int row, int col) {
            if (col == 2) {
               return true;
            }
            return false;
         }

         // This table colors elements depending on their owner
         public Component prepareRenderer(TableCellRenderer renderer,
                                          int rowIndex,
                                          int vColIndex) {
            Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
            if (!isCellSelected(rowIndex, vColIndex) && colors) {
               c.setBackground(getBackground());
            }

            return c;
         }
      };
      new EAWTMListener(jt);

      Color bkgCol = new Color(245, 245, 245);
      if (ipc.getSettings() instanceof PanelSettings) {
         bkgCol = ((PanelSettings) ipc.getSettings()).getBackgroundColor();
      }

      jt.setBackground(bkgCol);
      return jt;
   }

   public void cleanup() {
      super.cleanup();
      ((ExtendedAttributesWrapper) myOwner).unregister();
   }

   class EAWTMListener implements TableModelListener {
      public EAWTMListener(JTable t) {
         t.getModel().addTableModelListener(this);
      }

      public void tableChanged(TableModelEvent e) {
         int col = e.getColumn();
         if (col == 2) {
            int row = e.getFirstRow();
            TableModel model = (TableModel) e.getSource();
            ExtendedAttribute ea = (ExtendedAttribute) model.getValueAt(row, 0);
            Boolean readOnly = (Boolean) model.getValueAt(row, col);
            ipc.getJaWEComponent().setUpdateInProgress(true);
            JaWEManager.getInstance().getJaWEController().startUndouableChange();
            if (readOnly.booleanValue()) {
               ea.setName(SharkConstants.VTP_VIEW);
            } else {
               ea.setName(SharkConstants.VTP_UPDATE);
            }
            List toSel = new ArrayList();
            toSel.add(ea);
            JaWEManager.getInstance().getJaWEController().endUndouableChange(toSel);
            ipc.getJaWEComponent().setUpdateInProgress(false);
         }
      }
   }
}
