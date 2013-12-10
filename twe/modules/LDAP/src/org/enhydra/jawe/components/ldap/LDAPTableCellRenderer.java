package org.enhydra.jawe.components.ldap;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class LDAPTableCellRenderer extends DefaultTableCellRenderer {

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
         int row, int column) {

      if (column == 1) {

         setIcon((ImageIcon) value);

         return this;
      }
      
      return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
   }

}
