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

/**
 * Miroslav Popov, Dec 26, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.components.problemsnavigator;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author Miroslav Popov
 *
 */
public class ProblemsTableCellRenderer extends DefaultTableCellRenderer {

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
         int row, int column) {

      if (column == 1) {

         setIcon((ImageIcon) value);

         return this;
      }
      
      return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
   }

}
