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

package org.enhydra.jawe.components.wfxml;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.base.panel.panels.XMLGroupPanel;
import org.enhydra.jawe.base.panel.panels.XMLMultiLineTextPanelWithOptionalChoiceButtons;
import org.enhydra.jawe.base.panel.panels.XMLPanel;
import org.enhydra.jawe.base.panel.panels.XMLTextPanel;

/**
 * The dialog for showing WfXML definition info.
 * 
 * @author Sasa Bojanic
 */
public class DefInfoEditor extends JDialog {
   public final static int STATUS_OK = 0;

   public final static int STATUS_CANCEL = 1;

   protected XMLPanel panelToEdit;

   protected JButton buttonOK;

   protected JButton buttonCancel;

   protected int status = STATUS_OK;

   protected DefInfo di;

   public DefInfoEditor(DefInfo di) {
      super(JaWEManager.getInstance().getJaWEController().getJaWEFrame(), true);
      this.di = di;
      panelToEdit = getDefInfoPanel();

      initDialog();
   }

   public boolean canApplyChanges() {
      if (panelToEdit.validateEntry()) {
         return true;
      }

      return false;
   }

   public void applyChanges() {
      panelToEdit.setElements();
   }

   public void requestFocus() {
      try {
         panelToEdit.requestFocus();
      } catch (Exception ex) {
         super.requestFocus();
      }
   }

   public int getStatus() {
      return status;
   }

   protected void initDialog() {
      try {
         JPanel buttonPanel = new JPanel();
         buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

         buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
         buttonPanel.setAlignmentY(Component.TOP_ALIGNMENT);

         buttonOK = new JButton(ResourceManager.getLanguageDependentString("OKKey"));

         buttonCancel = new JButton(ResourceManager.getLanguageDependentString("CancelKey"));

         buttonPanel.add(Box.createHorizontalGlue());
         buttonPanel.add(buttonOK);
         buttonPanel.add(Box.createHorizontalStrut(4));
         buttonPanel.add(buttonCancel);
         buttonPanel.add(Box.createHorizontalStrut(4));

         Container cp = getContentPane();
         cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

         cp.add(panelToEdit);
         cp.add(Box.createVerticalStrut(5));
         cp.add(buttonPanel);

         // action listener for confirming
         buttonOK.addActionListener(okl);

         // action listener for cancel
         buttonCancel.addActionListener(cl);
         addWindowListener(wl);

         getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "Cancel");
         getRootPane().getActionMap().put("Cancel", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
               cl.actionPerformed(e);
            }
         });

      } catch (Exception e) {
         e.printStackTrace();
      }
      setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      setResizable(true);
      Utils.center(this, 100, 100);
      setLocationRelativeTo(getParent());
      buttonOK.setDefaultCapable(true);
      getRootPane().setDefaultButton(buttonOK);
      pack();
      setVisible(true);
   }

   protected WindowListener wl = new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
         // make configurable?
         status = STATUS_CANCEL;
         dispose();
      }
   };

   protected ActionListener cl = new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
         status = STATUS_CANCEL;
         dispose();
      }
   };

   protected ActionListener okl = new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
         if (panelToEdit.getOwner().isReadOnly()) {
            status = STATUS_CANCEL;
            dispose();
         } else if (canApplyChanges()) {
            applyChanges();
            status = STATUS_OK;
            dispose();
            if (getParent() != null) {
               getParent().repaint();// do repaint
            }
         }
      }
   };

   protected XMLGroupPanel getDefInfoPanel() {
      List lst = new ArrayList();
      boolean definitionKeyEnabled = true;
      if (di.getDefinitionKey().indexOf("procMgr=") != -1) {
         definitionKeyEnabled = false;
      }
      lst.add(new XMLMultiLineTextPanelWithOptionalChoiceButtons(null,
                                                                 di.get("DefinitionKey"),
                                                                 true,
                                                                 2,
                                                                 false,
                                                                 definitionKeyEnabled,
                                                                 null,
                                                                 null));
      lst.add(new XMLTextPanel(null, di.get("Name"), false, false, definitionKeyEnabled));
      lst.add(new XMLMultiLineTextPanelWithOptionalChoiceButtons(null,
                                                                 di.get("Description"),
                                                                 true,
                                                                 3,
                                                                 true,
                                                                 definitionKeyEnabled,
                                                                 null,
                                                                 null));
      lst.add(new XMLTextPanel(null, di.get("Version"), false, false, definitionKeyEnabled));
      lst.add(new XMLTextPanel(null, di.get("Status"), false, false, definitionKeyEnabled));
      XMLGroupPanel gp = new XMLGroupPanel(null, di, lst, "", true, false, true, null);
      return gp;
   }

}
