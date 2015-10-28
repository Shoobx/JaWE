package org.enhydra.jawe.base.panel.panels;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;

public class TooltipComboRenderer extends DefaultListCellRenderer {
   public Component getListCellRendererComponent(JList list,
                                                 Object value,
                                                 int index,
                                                 boolean isSelected,
                                                 boolean cellHasFocus) {
      JComponent c = (JComponent) super.getListCellRendererComponent(list,
                                                                     value,
                                                                     index,
                                                                     isSelected,
                                                                     cellHasFocus);
      if (value != null && isSelected) {
         list.setToolTipText(value.toString());
      } else if (isSelected) {
         list.setToolTipText("");
      }
      return c;
   }
}
