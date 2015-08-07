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

import java.awt.Component;
import java.util.List;

import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLElement;

/**
 * Creates a tabbed panel.
 * 
 * @author Sasa Bojanic
 */
public class XMLTabbedPanel extends XMLBasicPanel {
   private JTabbedPane tabbed;

   public XMLTabbedPanel(PanelContainer pc,
                         XMLElement myOwnerL,
                         List panels,
                         String title,
                         boolean showTitle,
                         String tooltip) {
      super(pc,
            myOwnerL,
            title,
            true,
            showTitle && title != null && !title.equals(""),
            true,
            tooltip);

      tabbed = new JTabbedPane(SwingConstants.TOP);
      // tabbed.setLayout(new BoxLayout(this.tabbed,BoxLayout.Y_AXIS));
      // tabbed.setLayout(new GridLayout(1,1));
      tabbed.setAlignmentX(Component.LEFT_ALIGNMENT);
      tabbed.setAlignmentY(Component.TOP_ALIGNMENT);

      for (int i = 0; i < panels.size(); i++) {
         XMLPanel pnl = (XMLPanel) panels.get(i);
         if (pnl == null)
            continue;
         if (pnl.getTitle() != null && !pnl.getTitle().equals("")) {
            String t = pnl.getTitle();
            if (pnl.getOwner() instanceof XMLCollection) {
               t = pc.getSettings().getLanguageDependentString(pnl.getOwner().toName()
                                                               + "Key");
            }
            tabbed.addTab(t, pnl);
         } else {
            String t = pnl.getTitle();
            if (pnl.getOwner() instanceof XMLCollection) {
               t = pc.getSettings().getLanguageDependentString(pnl.getOwner().toName()
                                                               + "Key");
            } else {
               t = pc.getLabelGenerator().getLabel(pnl.getOwner());
            }
            tabbed.addTab(t, pnl);
         }
      }
      if (tabbed.getTabCount() > 0) {
         tabbed.setSelectedIndex(0); // Harald Meister
      }
      // tabbed.setPreferredSize(tabbed.getMinimumSize());
      // elementPanel.setPreferredSize(elementPanel.getMinimumSize())
      add(tabbed);
   }

   public int getTabCount() {
      return tabbed.getTabCount();
   }

   public XMLPanel getTabbedPanel(int no) {
      if (no >= tabbed.getTabCount()) {
         return null;
      }
      return (XMLPanel) tabbed.getComponentAt(no);
   }

   public int getActiveTab() {
      return tabbed.getSelectedIndex();
   }

   public XMLPanel getSelectedTabPanel() {
      return (XMLPanel) tabbed.getComponentAt(getSelectedTab());
   }

   public void setActiveTab(int tab) {
      tabbed.setSelectedIndex(tab);
   }

   public boolean validateEntry() {
      boolean isOK = true;
      for (int i = 0; i < tabbed.getComponentCount(); i++) {
         Component c = tabbed.getComponent(i);
         if (c instanceof XMLPanel) {
            isOK = isOK && ((XMLPanel) c).validateEntry();
         }
      }
      return isOK;
   }

   public void setElements() {
      if (!getOwner().isReadOnly()) {
         for (int i = 0; i < tabbed.getComponentCount(); i++) {
            Component c = tabbed.getComponent(i);
            if (c instanceof XMLPanel) {
               ((XMLPanel) c).setElements();
            }
         }
      }
   }

   public XMLPanel getPanelForElement(XMLElement el) {
      for (int i = 0; i < tabbed.getComponentCount(); i++) {
         Component c = tabbed.getComponent(i);
         if (c instanceof XMLPanel) {
            XMLPanel p = (XMLPanel) c;
            if (p instanceof XMLGroupPanel) {
               p = ((XMLGroupPanel) p).getPanelForElement(el);
               if (p != null) {
                  return p;
               }
            } else if (p.getOwner() == el) {
               return p;
            }
         }
      }
      return null;
   }

   public int getSelectedTab() {
      return tabbed.getSelectedIndex();
   }

   public void setSelectedTab(int selIndex) {
      tabbed.setSelectedIndex(selIndex);
   }

   public void cleanup() {
      for (int i = 0; i < tabbed.getComponentCount(); i++) {
         Component c = tabbed.getComponent(i);
         if (c instanceof XMLPanel) {
            ((XMLPanel) c).cleanup();
         }
      }
   }

}
