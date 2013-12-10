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

package org.enhydra.jawe.components.graph;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
import org.enhydra.jawe.base.panel.panels.XMLBasicPanel;
import org.enhydra.jawe.base.panel.panels.XMLGroupPanel;
import org.enhydra.jawe.base.panel.panels.XMLMultiLineTextPanelWithOptionalChoiceButtons;
import org.enhydra.jawe.base.panel.panels.XMLPanel;
import org.enhydra.jawe.base.panel.panels.XMLTextPanel;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ActivitySet;
import org.enhydra.jxpdl.elements.Lane;
import org.enhydra.jxpdl.elements.WorkflowProcess;

/**
 * The dialog for editing information about expression lanes. This information is
 * propagated to the performer of the activities contained within that lanes.
 */
public class ExpressionLaneEditor {
   /** Constant for OK status of dialog. */
   public final static int STATUS_OK = 0;

   /** Constant for Cancel status of dialog. */
   public final static int STATUS_CANCEL = 1;

   /** JDialog instance. */
   protected JDialog dialog;

   /** Properties for the editor. */
   protected Properties properties = new Properties();

   /** {@link Lane} element to edit. */
   protected Lane elementToEdit;

   /** Panel for editing lane. */
   protected XMLPanel panelToEdit = new XMLBasicPanel();

   /** OK button. */
   protected JButton buttonOK;

   /** Cancel button. */
   protected JButton buttonCancel;

   /** Dialog title. */
   protected String title = null;

   /** Panel for Id element. */
   protected XMLPanel pIdPanel;

   /** Dialog status. */
   protected int status = STATUS_OK;

   /**
    * Configures dialog.
    * 
    * @param props Settings for the editor.
    */
   public void configure(Properties props) {
      this.properties = props;
   }

   /**
    * Sets property for the editor.
    * 
    * @param key Property key.
    * @param value Property value.
    */
   public void setProperty(String key, String value) {
      properties.setProperty(key, value);
   }

   /**
    * Creates new instance of editor.
    * 
    * @param el {@link Lane} element to edit.
    */
   public ExpressionLaneEditor(Lane el) {
      elementToEdit = el;
      dialog = new JDialog(JaWEManager.getInstance().getJaWEController().getJaWEFrame(),
                           true);
      this.title = ResourceManager.getLanguageDependentString(el.toName() + "Key");

      initDialog();
   }

   /**
    * Sets editor title.
    * 
    * @param title The title.
    */
   public void setTitle(String title) {
      if (dialog != null) {
         dialog.setTitle(title);
      }
   }

   /**
    * Edits {@link Lane} element.
    */
   public void editXPDLElement() {
      editXPDLElement(this.getLanePanel());
   }

   /** Edits panel for the lane element. */
   public void editXPDLElement(XMLPanel panel) {
      Container cp = dialog.getContentPane();
      cp.remove(panelToEdit);
      panelToEdit = panel;

      cp.add(panelToEdit, 0);
      dialog.pack();

      dialog.setLocationRelativeTo(getParentWindow());
      dialog.pack();
      dialog.setVisible(true);
      // requestFocus();

   }

   /**
    * @return true if changes can be applied to the model.
    */
   public boolean canApplyChanges() {
      if (panelToEdit.validateEntry()) {
         return true;
      }
      return false;
   }

   /**
    * Applies changes to the XPDL model.
    */
   public void applyChanges() {
      GraphController gc = GraphUtilities.getGraphController();
      WorkflowProcess wp = JaWEManager.getInstance()
         .getXPDLUtils()
         .getProcessForPool(XMLUtil.getPool(elementToEdit));
      String oldId = GraphUtilities.getLanesFirstPerformer(elementToEdit);
      String newId = null;
      if (oldId != null) {
         newId = ((XMLMultiLineTextPanelWithOptionalChoiceButtons) pIdPanel).getText();
         if (!newId.equals(oldId)) {
            List acts = wp.getActivities().toElements();
            List ass = wp.getActivitySets().toElements();
            for (int i = 0; i < ass.size(); i++) {
               ActivitySet as = (ActivitySet) ass.get(i);
               acts.addAll(as.getActivities().toElements());
            }

            gc.setUpdateInProgress(true);
            JaWEManager.getInstance().getJaWEController().startUndouableChange();
            GraphUtilities.setLanesFirstPerformer(elementToEdit, newId);
            elementToEdit.setName(newId);
            acts = GraphUtilities.getAllActivitiesAndArtifactsForLaneId(acts,
                                                                        elementToEdit.getId());
            for (int i = 0; i < acts.size(); i++) {
               Activity act = (Activity) acts.get(i);
               int type = act.getActivityType();
               if (type == XPDLConstants.ACTIVITY_TYPE_NO
                   || type == XPDLConstants.ACTIVITY_TYPE_TASK_APPLICATION) {
                  act.setFirstPerformer(newId);
               }
            }
            List toSelect = new ArrayList();
            JaWEManager.getInstance().getJaWEController().endUndouableChange(toSelect);
            gc.getGraph(wp).refresh();
            gc.setUpdateInProgress(false);
         }
      } else {
         newId = ((XMLTextPanel) pIdPanel).getText();
         JaWEManager.getInstance().getJaWEController().startUndouableChange();
         elementToEdit.setName(newId);
         List toSelect = new ArrayList();
         toSelect.add(elementToEdit);
         JaWEManager.getInstance().getJaWEController().endUndouableChange(toSelect);
         gc.getGraph(wp).refresh();
      }
   }

   /**
    * Sets the panel into the focus.
    */
   public void requestFocus() {
      try {
         // if (panelToEdit instanceof XMLGroupPanel) {
         // if (panelToEdit.getComponent(0).isEnabled()) {
         // panelToEdit.getComponent(0).requestFocus();
         // } else {
         // panelToEdit.getComponent(1).requestFocus();
         // }
         // }
      } catch (Exception ex) {
         panelToEdit.requestFocus();
      }
   }

   /**
    * @return The status of the editor.
    */
   public int getStatus() {
      return status;
   }

   /**
    * @return The window of the editor.
    */
   public Window getWindow() {
      return dialog;
   }

   /**
    * @return Parent window of the editor.
    */
   public Window getParentWindow() {
      return JaWEManager.getInstance().getJaWEController().getJaWEFrame();
   }

   /**
    * Initializes editor's dialog.
    */
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

         Container cp = dialog.getContentPane();
         cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

         cp.add(panelToEdit);
         cp.add(Box.createVerticalStrut(5));
         cp.add(buttonPanel);

         // action listener for confirming
         buttonOK.addActionListener(okl);

         // action listener for cancel
         buttonCancel.addActionListener(cl);
         dialog.addWindowListener(wl);

         dialog.getRootPane()
            .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "Cancel");
         dialog.getRootPane().getActionMap().put("Cancel", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
               cl.actionPerformed(e);
            }
         });

      } catch (Exception e) {
         e.printStackTrace();
      }
      dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      dialog.setResizable(true);
      // dialog.setLocationRelativeTo(getParentWindow());
      buttonOK.setDefaultCapable(true);
      dialog.getRootPane().setDefaultButton(buttonOK);
      dialog.setTitle(this.title);

   }

   /**
    * Listener for the window closing event.
    */
   protected WindowListener wl = new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
         // make configurable?
         status = STATUS_CANCEL;
         dialog.dispose();
      }
   };

   /**
    * Listener for the editor's action.
    */
   protected ActionListener cl = new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
         status = STATUS_CANCEL;
         dialog.dispose();
      }
   };

   /** Listener for OK button. */
   protected ActionListener okl = new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
         if (elementToEdit == null || panelToEdit.getOwner().isReadOnly()) {
            status = STATUS_CANCEL;
            dialog.dispose();
         } else if (canApplyChanges()) {
            applyChanges();
            status = STATUS_OK;
            dialog.dispose();
            if (dialog.getParent() != null) {
               dialog.getParent().repaint();// do repaint
            }
         }
      }
   };

   /**
    * @return The panel for editing lane.
    */
   protected XMLGroupPanel getLanePanel() {
      List toShow = new ArrayList();
      if (elementToEdit instanceof Lane) {
         if (elementToEdit.getPerformers().size() > 0) {
            pIdPanel = new XMLMultiLineTextPanelWithOptionalChoiceButtons(null,
                                                                          elementToEdit.getPerformers()
                                                                             .get(0),
                                                                          "Expression",
                                                                          false,
                                                                          true,
                                                                          XMLMultiLineTextPanelWithOptionalChoiceButtons.SIZE_MEDIUM,
                                                                          false,
                                                                          null,
                                                                          null,
                                                                          !elementToEdit.isReadOnly(), null);
         } else {
            pIdPanel = new XMLTextPanel(null,
                                        elementToEdit.get("Name"),
                                        false,
                                        false,
                                        !elementToEdit.isReadOnly());
         }
         toShow.add(pIdPanel);
         // XMLPanel descPnl=new XMLMultiLineTextPanel(null,
         // elementToEdit.get("Description"),true,XMLMultiLineTextPanel.SIZE_MEDIUM,true,false);
         // toShow.add(descPnl);
      }
      XMLGroupPanel gp = new XMLGroupPanel(null,
                                           elementToEdit,
                                           toShow,
                                           "",
                                           true,
                                           false,
                                           true, null);
      return gp;
   }

}
