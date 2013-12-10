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

package org.enhydra.jawe.base.editor;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.panel.InlinePanel;
import org.enhydra.jawe.base.panel.panels.XMLSimpleTablePanel;
import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.elements.Package;

/**
 * 
 */
public class TableEditor extends JDialog implements JaWEComponent {

   protected String type = JaWEComponent.OTHER_COMPONENT;

   protected Properties properties = new Properties();

   protected XMLSimpleTablePanel panelToEdit = null;

   protected InlinePanel inlinePanel;

   private TableEditorSettings settings;

   public TableEditor(TableEditorSettings ts) {
      super(JaWEManager.getInstance().getJaWEController().getJaWEFrame(), true);
      settings = ts;
      init();
   }

   public Properties getProperties() {
      return properties;
   }

   public JaWEComponentSettings getSettings() {
      return settings;
   }

   public JaWEComponentView getView() {
      return null;
   }

   public JComponent getDisplay() {
      return null;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getName() {
      return "TABLE_EDITOR";
   }

   public boolean adjustXPDL(Package pkg) {
      return false;
   }

   public List checkValidity(XMLElement el, boolean fullCheck) {
      return null;
   }

   public boolean canCreateElement(XMLCollection col) {
      return true;
   }

   public boolean canInsertElement(XMLCollection col, XMLElement el) {
      return true;
   }

   public boolean canModifyElement(XMLCollection col, XMLElement el) {
      return true;
   }

   public boolean canModifyElement(XMLElement col) {
      return true;
   }

   public boolean canRemoveElement(XMLCollection col, XMLElement el) {
      return true;
   }

   public boolean canDuplicateElement(XMLCollection col, XMLElement el) {
      return true;
   }

   public boolean canRepositionElement(XMLCollection col, XMLElement el) {
      return true;
   }

   public Action getAction(String cmd) {
      return null;
   }

   public Action[] getActions() {
      return null;
   }

   public void configure(Properties props) {
      this.properties = props;
   }

   protected void init() {
      try {

         initDialog();

      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   protected void initDialog() {
      Container cp = getContentPane();
      cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

      getRootPane().getInputMap(JComponent.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "Cancel");
      getRootPane().getActionMap().put("Cancel", new AbstractAction() {
         public void actionPerformed(ActionEvent e) {
            al.actionPerformed(e);
         }
      });
      addWindowListener(wl);

      try {
         ClassLoader cl = getClass().getClassLoader();
         inlinePanel = (InlinePanel) cl.loadClass(JaWEManager.getInstance()
            .getInlinePanelClassName()).newInstance();
      } catch (Exception ex) {
         String msg = "TableEditor --> Problems while instantiating InlinePanel class '"
                      + JaWEManager.getInstance().getInlinePanelClassName()
                      + "' - using default implementation!";
         JaWEManager.getInstance().getLoggingManager().error(msg, ex);
         inlinePanel = new InlinePanel();
      }
      try {

         inlinePanel.setJaWEComponent(this);
         // settings must be initialized after creation of InlinePanel
         settings.init(this);
         inlinePanel.init();

      } catch (Exception e) {
         e.printStackTrace();
      }
      setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      setResizable(true);

   }

   protected WindowListener wl = new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
         close();
      }
   };

   protected ActionListener al = new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
         close();
      }
   };

   public void close() {
      setVisible(false);
      if (panelToEdit != null) {
         panelToEdit.cleanup();
         Container cp = getContentPane();
         cp.remove(panelToEdit);
         panelToEdit = null;
      }
   }

   public void showTable(String title,
                         XMLCollection owner,
                         List elements,
                         List columnsToShow) {
      Container cp = getContentPane();
      if (panelToEdit != null) {
         cp.remove(panelToEdit);
      }
      panelToEdit = createSTP(title, owner, elements, columnsToShow);

      cp.add(panelToEdit, 0);
      pack();

      // panelToEdit.requestFocus();
      pack();

      setTitle(panelToEdit.getTitle());
      if (!isVisible()) {
         setLocationRelativeTo(JaWEManager.getInstance()
            .getJaWEController()
            .getJaWEFrame());
         setVisible(true);
      }
   }

   public void setUpdateInProgress(boolean inProgress) {
   }

   public boolean isUpdateInProgress() {
      return false;
   }

   protected XMLSimpleTablePanel createSTP(String title,
                                           XMLCollection owner,
                                           List elements,
                                           List columnsToShow) {
      return new XMLSimpleTablePanel(inlinePanel,
                                     owner,
                                     columnsToShow,
                                     elements,
                                     title,
                                     true,
                                     true,
                                     false);
   }

}
