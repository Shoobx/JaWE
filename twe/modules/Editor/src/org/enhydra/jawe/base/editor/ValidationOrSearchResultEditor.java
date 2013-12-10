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
import java.util.Observable;
import java.util.Observer;
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
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.XPDLElementChangeInfo;
import org.enhydra.jawe.base.panel.InlinePanel;
import org.enhydra.jawe.base.panel.panels.ValidationErrorOrSearchResultListPanel;
import org.enhydra.jawe.base.panel.panels.XMLBasicPanel;
import org.enhydra.jawe.base.panel.panels.XMLPanel;
import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.elements.Package;

/**
 *  
 */
public class ValidationOrSearchResultEditor extends JDialog implements
                                                           JaWEComponent,
                                                           Observer {

   protected String type = JaWEComponent.OTHER_COMPONENT;

   protected Properties properties = new Properties();

   protected XMLPanel panelToEdit = new XMLBasicPanel();

   protected boolean validationDisplayEnabled = true;

   protected boolean searchDisplayEnabled = true;

   public String getLanguageDependentString(String nm) {
      return ResourceManager.getLanguageDependentString(nm);
   }

   public ValidationOrSearchResultEditor() {
      super(JaWEManager.getInstance().getJaWEController().getJaWEFrame(), false);
      init();
   }

   public JaWEComponentSettings getSettings() {
      return null;
   }

   public Properties getProperties() {
      return properties;
   }

   public void setValidationDisplayEnabled(boolean enabled) {
      this.validationDisplayEnabled = enabled;
   }

   public void setSearchDisplayEnabled(boolean enabled) {
      this.searchDisplayEnabled = enabled;
   }

   public void update(Observable o, Object arg) {
      if (!(arg instanceof XPDLElementChangeInfo))
         return;

      XPDLElementChangeInfo info = (XPDLElementChangeInfo) arg;
      if (info.getChangedElement() == null)
         return;
      int action = info.getAction();

      if (!((action == XPDLElementChangeInfo.VALIDATION_ERRORS
             && validationDisplayEnabled && (info.getOldValue() instanceof Boolean && !((Boolean) info.getOldValue()).booleanValue())) || ((action == XPDLElementChangeInfo.SEARCH_RESULT) || action == XPDLElementChangeInfo.REFERENCES)
                                                                                                                                          && searchDisplayEnabled))
         return;

      String t = "";
      if (action == XPDLElementChangeInfo.VALIDATION_ERRORS) {
         t = getLanguageDependentString("DialogValidationReport");
      } else {
         t = getLanguageDependentString("DialogSearchResult");
      }
      setTitle(t);
      setDisplayPanel(t, info.getChangedElement(), info.getChangedSubElements());
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
      return "VALIDATION_EDITOR";
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
         JaWEManager.getInstance().getJaWEController().addObserver(this);

         initDialog();

      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   protected void initDialog() {
      try {

         Container cp = getContentPane();
         cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

         cp.add(panelToEdit);
         getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "Cancel");
         getRootPane().getInputMap(JComponent.WHEN_FOCUSED)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "Cancel");
         getRootPane().getActionMap().put("Cancel", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
               cl.actionPerformed(e);
            }
         });
         addWindowListener(wl);

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

   protected ActionListener cl = new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
         close();
      }
   };

   public InlinePanel getInlinePanel() {
      return null;
   }

   public void close() {
      setVisible(false);
      panelToEdit.cleanup();
      Container cp = getContentPane();
      cp.remove(panelToEdit);
      panelToEdit = new XMLBasicPanel();
      cp.add(panelToEdit, 0);
   }

   public void setDisplayPanel(String t, XMLElement el, List errors) {
      setModal(true);
      showPanel(new ValidationErrorOrSearchResultListPanel(el, errors, t, true, true));
   }

   public void showPanel(XMLPanel newPanel) {
      Container cp = getContentPane();
      cp.remove(panelToEdit);
      panelToEdit = newPanel;
      cp.add(panelToEdit, 0);
      pack();

      panelToEdit.requestFocus();
      pack();

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

}
