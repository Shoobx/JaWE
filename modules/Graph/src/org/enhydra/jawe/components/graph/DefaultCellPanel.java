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

/**
 * Miroslav Popov, Jul 20, 2005
 */
package org.enhydra.jawe.components.graph;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

/**
 * Default panel for jawe object such as activity, route etc. It has icon and name.
 * 
 * @author Miroslav Popov
 */
public class DefaultCellPanel extends JPanel {

   public static final int TEXT_POSITION_ALL = 0;

   public static final int TEXT_POSITION_UP = 1;

   public static final int TEXT_POSITION_DOWN = 2;

   public static final int TEXT_POSITION_LEFT = 3;

   public static final int TEXT_POSITION_RIGHT = 4;

   // 0 - divLocation = icon space, divLocation - with = name space
   protected int divLocation = 16;

   protected JSplitPane split;

   protected JTextArea name = new JTextArea();

   protected JLabel mainIcon = new JLabel();

   protected int orientation = 0;

   protected DefaultCellPanel() {
      name.setText("-");
      name.setOpaque(false);
//      name.setAlignmentX(Component.CENTER_ALIGNMENT);
//      name.setAlignmentY(Component.CENTER_ALIGNMENT);
      mainIcon.setIcon(null);
      mainIcon.setVerticalAlignment(SwingConstants.TOP);
      mainIcon.setHorizontalAlignment(SwingConstants.CENTER);
      mainIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
      mainIcon.setAlignmentY(Component.TOP_ALIGNMENT);

      setOpaque(false);
      setLayout(new BorderLayout());
      split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mainIcon, name);
      split.setDividerLocation(divLocation);
      split.setBorder(null);
      split.setDividerSize(0);
      split.setOpaque(false);

      add(split);
   }

   public void showIcon(boolean show) {
      mainIcon.setVisible(show);
   }

   public Icon getMainIcon() {
      return mainIcon.getIcon();
   }

   public void setMainIcon(Icon mainIcon) {
      this.mainIcon.setIcon(mainIcon);
   }

   public String getDisplayName() {
      return name.getText();
   }

   public void setDisplayName(String name) {
      this.name.setForeground(GraphUtilities.getGraphController()
         .getGraphSettings()
         .getTextColor());
      this.name.setText(name);
   }

   public void wrapName(boolean wrap) {
      name.setLineWrap(wrap);
   }

   public void wrapStyle(boolean word) {
      name.setWrapStyleWord(word);
   }

   public void setFont(Font font) {
      if (name != null)
         name.setFont(font);
   }

   /**
    * Set text and icon on panel depending on parameter place 1 - icon bottom, text up 2 -
    * icon top, text bottom 3 - icon right, text left default - icon left, text right
    * 
    * @param place
    */
   public void setTextPosition(int place) {
      orientation = place;
      arrangeSplit();
   }

   public void arrangeSplit() {
      remove(split);
//      if (orientation==0) {
//         add(name);
//         return;
//      }
      switch (orientation) {
         case 0:
            split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainIcon, name);
            split.setDividerLocation(0);
            break;
         case 1:
            split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, name, mainIcon);
            split.setDividerLocation(GraphUtilities.getGraphController()
               .getGraphSettings()
               .getActivityHeight()
                                     - divLocation-3);
            break;
         case 2:
            split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainIcon, name);
            split.setDividerLocation(divLocation);
            break;
         case 3:
            split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, name, mainIcon);
            split.setDividerLocation(GraphUtilities.getGraphController()
               .getGraphSettings()
               .getActivityWidth()
                                     - divLocation-5);
            break;
         default:
            split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mainIcon, name);
            split.setDividerLocation(divLocation);
            break;
      }
      split.setBorder(null);
      split.setDividerSize(0);
      split.setOpaque(false);
      add(split);
   }

   public void setBounds(Rectangle rect) {
      super.setBounds(rect);
      if (split != null) {
         int iconSize = 0;

         if (mainIcon.isVisible() && mainIcon.getIcon()!=null) {
            iconSize = mainIcon.getIcon().getIconWidth();
         }
         name.setBounds(name.getX(), name.getY(), rect.width, rect.height - iconSize - 3);
         // switch (orientation) {
         // case DefaultCellPanel.TEXT_POSITION_UP:
         //
         // mainIcon.setLocation(0, rect.height - iconSize);
         // break;
         // case DefaultCellPanel.TEXT_POSITION_DOWN:
         // break;
         // case DefaultCellPanel.TEXT_POSITION_LEFT:
         // mainIcon.setLocation(rect.width - iconSize, 0);
         // break;
         // default:
         // break;
         // }

         split.setBounds(rect);
      }
   }
}
