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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.enhydra.jawe.ChoiceButton;
import org.enhydra.jawe.ChoiceButtonListener;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.NewActionBase;
import org.enhydra.jawe.base.controller.JaWEActions;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.base.controller.JaWESelectionManager;
import org.enhydra.jawe.base.controller.JaWEType;
import org.enhydra.jawe.base.editor.StandardXPDLElementEditor;
import org.enhydra.jawe.base.panel.InlinePanel;
import org.enhydra.jawe.base.panel.PanelSettings;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLElementChangeInfo;
import org.enhydra.jxpdl.XMLElementChangeListener;
import org.enhydra.jxpdl.elements.ExtendedAttributes;

/**
 * Creates a list panel.
 * 
 * @author Sasa Bojanic
 * @author Zoran Milakovic
 * @author Miroslav Popov
 * 
 */
public class XMLListPanel extends XMLBasicPanel implements
                                               XMLElementChangeListener,
                                               FocusListener,
                                               ChoiceButtonListener {

   protected static Dimension minimalDimension = new Dimension(450, 60);

   protected static Dimension listDimension = new Dimension(450, 150);

   /**
    * Object which we are replacing from one place to another within the list by dragging
    * it.
    */
   protected XMLElementView movingElement;

   /**
    * Index of the object which we are replacing from one place to another within the list
    * by dragging it.
    */
   protected int movingElementPosition;

   /**
    * The new index of the object which we are replacing from one place to another within
    * the list by dragging it.
    */
   protected int newMovingElementPosition;

   /** Indicates if object is being dragged. */
   protected boolean dragging = false;

   /**
    * Indicates if the code for changing object position within the list is executed.
    */
   protected boolean changing = false;

   protected boolean lostFocusHandle = true;

   protected JList allParam;

   protected JPanel toolbox;

   protected InlinePanel ipc;

   protected NewActionBase newElementAction = null;

   protected boolean notifyPanel;

   public XMLListPanel(final InlinePanel ipc,
                       final XMLCollection myOwner,
                       List elementsToShow,
                       String title,
                       boolean hasBorder,
                       boolean hasEmptyBorder,
                       final boolean enableEditing,
                       boolean minDimension,
                       boolean useBasicToolbar,
                       boolean notifyPanel) {

      super(ipc, myOwner, title, true, hasBorder, hasEmptyBorder);
      this.ipc = ipc;
      this.notifyPanel = notifyPanel;

      Class pfea = null;
      if (myOwner instanceof ExtendedAttributes) {
         if (myOwner.getParent() != null) {
            pfea = myOwner.getParent().getClass();
         }
      }
      newElementAction = new NewActionBase(ipc.getJaWEComponent(),
                                           JaWEActions.NEW_ACTION,
                                           myOwner.getClass(),
                                           pfea) {
         public void actionPerformed(ActionEvent ae) {
            ipc.getJaWEComponent().setUpdateInProgress(true);
            lostFocusHandle = false;
            JaWEManager.getInstance()
               .getJaWEController()
               .getSelectionManager()
               .setSelection(myOwner, false);
            allParam.clearSelection();
            adjustActions();
         }

         public void enableDisableAction() {
            setEnabled(JaWEManager.getInstance()
               .getJaWEController()
               .canCreateElement((XMLCollection) getOwner()));
         }

      };

      myOwner.addListener(this);
      myOwner.setNotifyListeners(true);

      allParam = createList();
      setupList(enableEditing);
      fillListContent(elementsToShow);

      JScrollPane scrollParam = new JScrollPane();
      scrollParam.setAlignmentX(Component.LEFT_ALIGNMENT);
      // scrollParam.setAlignmentY(Component.TOP_ALIGNMENT);

      scrollParam.setViewportView(allParam);
      if (!minDimension) {
         scrollParam.setPreferredSize(new Dimension(listDimension));
      } else {
         scrollParam.setPreferredSize(new Dimension(minimalDimension));
      }

      toolbox = createToolbar(useBasicToolbar);
      JPanel paneAndArrows = new JPanel();
      paneAndArrows.setLayout(new BoxLayout(paneAndArrows, BoxLayout.X_AXIS));
      paneAndArrows.add(scrollParam);

      // JPanel p = createArrowPanel();
      // paneAndArrows.add(Box.createRigidArea(new Dimension(5, 0)));
      // paneAndArrows.add(p);

      add(toolbox);
      add(Box.createVerticalStrut(3));
      add(paneAndArrows);

      allParam.addFocusListener(this);

      adjustActions();
   }

   public boolean isItemChangingPosition() {
      return (changing || dragging);
   }

   public JList getList() {
      return allParam;
   }

   public XMLElement getSelectedElement() {
      if (allParam.getSelectedIndex() == -1)
         return null;

      return ((XMLElementView) allParam.getSelectedValue()).getElement();
   }

   public boolean setSelectedElement(XMLElement el) {
      int selIndex = -1;
      XMLElementView ev = getRow(el);
      for (int i = 0; i < allParam.getModel().getSize(); i++) {
         XMLElementView elat = (XMLElementView) allParam.getModel().getElementAt(i);
         if (ev.equals(elat)) {
            selIndex = i;
            break;
         }
      }

      if (selIndex != -1) {
         allParam.setSelectedIndex(selIndex);
      }
      return (selIndex != -1);
   }

   protected void moveItem(int upOrDown) {
      newMovingElementPosition = movingElementPosition;
      if (newMovingElementPosition == -1) {
         return;
      } else if (upOrDown == 0) {
         newMovingElementPosition--;
      } else {
         newMovingElementPosition++;
      }

      moveItem();
   }

   protected void moveItem() {
      changing = true;
      DefaultListModel listModel = (DefaultListModel) allParam.getModel();
      XMLCollection owncol = (XMLCollection) getOwner();
      int rowCnt = listModel.getSize();
      if (movingElement == null
          || movingElementPosition == -1 || newMovingElementPosition == -1
          || newMovingElementPosition == movingElementPosition
          || (rowCnt - 1) < movingElementPosition
          || (rowCnt - 1) < newMovingElementPosition
          || !owncol.contains(movingElement.getElement())) {
         changing = false;
         return;
      }

      XMLCollection col = (XMLCollection) getOwner();
      if (JaWEManager.getInstance()
         .getJaWEController()
         .canRepositionElement(col, movingElement.getElement())) {
         ipc.getJaWEComponent().setUpdateInProgress(true);
         XMLElement currentElementAtPosition = ((XMLElementView) listModel.getElementAt(newMovingElementPosition)).getElement();
         int newpos = owncol.indexOf(currentElementAtPosition);

         listModel.remove(movingElementPosition);
         listModel.add(newMovingElementPosition, movingElement);

         JaWEController jc = JaWEManager.getInstance().getJaWEController();
         jc.startUndouableChange();
         owncol.reposition(movingElement.getElement(), newpos);
         List toSelect = new ArrayList();
         toSelect.add(movingElement.getElement());
         jc.endUndouableChange(toSelect);

         try {
            allParam.setSelectedIndex(newMovingElementPosition);
         } catch (Exception ex) {
         }

         movingElementPosition = newMovingElementPosition;
         ipc.getJaWEComponent().setUpdateInProgress(false);
      }
      changing = false;
   }

   protected Action duplicateElementAction = new AbstractAction(JaWEActions.DUPLICATE_ACTION) {
      public void actionPerformed(ActionEvent ae) {
         XMLElement editElement = getSelectedElement();
         if (editElement != null) {
            JaWESelectionManager sm = JaWEManager.getInstance()
               .getJaWEController()
               .getSelectionManager();
            ipc.getJaWEComponent().setUpdateInProgress(true);
            allParam.clearSelection();
            sm.setSelection(editElement, false);
            JaWEManager.getInstance().getJaWEController().getEdit().duplicate();
            if (!setSelectedElement(sm.getSelectedElement())) {
               sm.setSelection(ipc.getActiveElement(), false);
            }
            adjustActions();
            ipc.getJaWEComponent().setUpdateInProgress(false);
         }
      }
   };

   protected Action editElementAction = new AbstractAction(JaWEActions.EDIT_PROPERTIES_ACTION) {
      public void actionPerformed(ActionEvent ae) {
         XMLElement editElement = getSelectedElement();
         if (editElement != null) {
            // if (!PanelUtilities.isForModalDialog(editElement)) {
            if (ipc.isModified()) {
               lostFocusHandle = false;
               int sw = ipc.showModifiedWarning();
               if (sw == JOptionPane.CANCEL_OPTION
                   || (sw == JOptionPane.YES_OPTION && ipc.isModified())) {
                  lostFocusHandle = true;
                  return;
               }
            }
            JaWEManager.getInstance()
               .getJaWEController()
               .getSelectionManager()
               .setSelection(editElement, true);
            // } else {
            // ipc.setUpdateInProgress(true);
            // StandardXPDLElementEditor ed = new StandardXPDLElementEditor(true);
            // ed.editXPDLElement(editElement);
            // repaint();
            // ipc.setUpdateInProgress(false);
            // }
         }
      }
   };

   protected Action deleteElementAction = new AbstractAction(JaWEActions.DELETE_ACTION) {
      public void actionPerformed(ActionEvent ae) {
         XMLElement deleteElement = getSelectedElement();
         if (deleteElement != null) {
            XMLElement parent = deleteElement.getParent();
            ipc.getJaWEComponent().setUpdateInProgress(true);
            lostFocusHandle = false;
            JaWESelectionManager sm = JaWEManager.getInstance()
               .getJaWEController()
               .getSelectionManager();
            sm.setSelection(deleteElement, false);
            JaWEManager.getInstance()
               .getJaWEController()
               .getJaWEActions()
               .getAction(JaWEActions.DELETE_ACTION)
               .actionPerformed(null);
            ipc.getHistoryManager().removeFromHistory(deleteElement);
            ipc.getPanelSettings().adjustActions();

            XMLElement newSelection = sm.getSelectedElement();

            if (newSelection != parent) {
               if (!setSelectedElement(newSelection)) {
                  sm.setSelection(ipc.getActiveElement(), true);
               }
            }

            lostFocusHandle = true;
            ipc.getJaWEComponent().setUpdateInProgress(false);

            adjustActions();
         }
      }
   };

   protected Action referencesElementAction = new AbstractAction(JaWEActions.REFERENCES) {
      public void actionPerformed(ActionEvent ae) {
         JaWEManager.getInstance()
            .getJaWEController()
            .getJaWEActions()
            .getAction(JaWEActions.REFERENCES)
            .actionPerformed(null);
      }
   };

   protected Action moveUpAction = new AbstractAction("MoveUp") {
      public void actionPerformed(ActionEvent ae) {
         ipc.getJaWEComponent().setUpdateInProgress(true);
         lostFocusHandle = false;
         moveItem(0);
         lostFocusHandle = true;
         ipc.getJaWEComponent().setUpdateInProgress(false);
         adjustActions();
      }
   };

   protected Action moveDownAction = new AbstractAction("MoveDown") {
      public void actionPerformed(ActionEvent ae) {
         ipc.getJaWEComponent().setUpdateInProgress(true);
         lostFocusHandle = false;
         moveItem(1);
         lostFocusHandle = true;
         ipc.getJaWEComponent().setUpdateInProgress(false);
         adjustActions();
      }
   };

   protected JList createList() {
      DefaultListModel listModel = new DefaultListModel();

      JList l = new JList(listModel);

      Color bkgCol = new Color(245, 245, 245);
      if (ipc.getSettings() instanceof PanelSettings) {
         bkgCol = ((PanelSettings) ipc.getSettings()).getBackgroundColor();
      }
      l.setBackground(bkgCol);

      return l;
   }

   protected void setupList(final boolean enableEditing) {
      allParam.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      allParam.setAlignmentX(Component.LEFT_ALIGNMENT);
      allParam.setAlignmentY(Component.TOP_ALIGNMENT);

      final XMLCollection col = (XMLCollection) getOwner();
      final boolean canRepos = JaWEManager.getInstance()
         .getJaWEController()
         .canRepositionElement(col, null);

      // mouse listener for editing on double-click and dragging list items
      allParam.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent me) {
            // implement some action only if editing is enabled
            if (enableEditing && me.getClickCount() > 1) {
               editElementAction.actionPerformed(null);
            }
         }

         /** Marks the object which place within the list will be changed. */
         public void mousePressed(MouseEvent me) {
            if (!getOwner().isReadOnly() && canRepos) {
               dragging = true;
            }
            movingElement = null;
            movingElementPosition = -1;
            try {
               movingElementPosition = allParam.getSelectedIndex();
               if (movingElementPosition >= 0) {
                  movingElement = (XMLElementView) allParam.getSelectedValue();
                  ipc.getJaWEComponent().setUpdateInProgress(true);
                  JaWEManager.getInstance()
                     .getJaWEController()
                     .getSelectionManager()
                     .setSelection(getSelectedElement(), false);
                  ipc.getJaWEComponent().setUpdateInProgress(false);
                  adjustActions();
               }
            } catch (Exception ex) {
            }

            if (changing) {
               changing = false;
               return;
            }
         }

         /** Just indicates that dragging is over. */
         public void mouseReleased(MouseEvent me) {
            dragging = false;
         }

      });

      /** Changes position of object within the list. */
      if (!myOwner.isReadOnly() && canRepos) {
         allParam.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse) {
               if (dragging) {
                  if (!changing) {

                     newMovingElementPosition = -1;
                     try {
                        newMovingElementPosition = allParam.getSelectedIndex();
                     } catch (Exception ex) {
                     }

                     moveItem();
                  }
                  return;
               }

               // Ignore extra messages.
               if (lse.getValueIsAdjusting())
                  return;

               try {
                  XMLElement el = getSelectedElement();
                  if (el != null) {
                     ipc.getJaWEComponent().setUpdateInProgress(true);
                     JaWEManager.getInstance()
                        .getJaWEController()
                        .getSelectionManager()
                        .setSelection(el, true);
                     ipc.getJaWEComponent().setUpdateInProgress(false);
                     adjustActions();
                  }
               } catch (Exception ex) {
               }

            }
         });
      }

      if (enableEditing) {
         allParam.getInputMap(JComponent.WHEN_FOCUSED)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "edit");
         allParam.getActionMap().put("edit", editElementAction);

         allParam.getInputMap(JComponent.WHEN_FOCUSED)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false), "delete");
         allParam.getActionMap().put("delete", deleteElementAction);
      }

      if (!getOwner().isReadOnly() && canRepos) {
         allParam.setToolTipText(pc.getLanguageDependentString("MessageDragItemToChangeItsPosition"));
      }
   }

   protected void fillListContent(List elementsToShow) {
      // fills list
      DefaultListModel listModel = (DefaultListModel) allParam.getModel();
      Iterator it = elementsToShow.iterator();
      while (it.hasNext()) {
         XMLElement elem = (XMLElement) it.next();
         XMLElementView ev = getRow(elem);
         listModel.addElement(ev);
      }

   }

   protected XMLElementView getRow(XMLElement el) {
      // if (el instanceof XMLComplexElement) {
      return new XMLElementView(ipc, el, XMLElementView.TONAME);
      // } else {
      // return new XMLElementView( el, XMLElementView.TOVALUE);
      // }
   }

   protected JPanel createToolbar(boolean useBasicToolbar) {
      JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
      JButton buttonNew = PanelUtilities.createToolbarButton(ipc.getSettings(),
                                                             newElementAction,
                                                             this);
      buttonNew.setRolloverEnabled(true);
      JButton buttonEdit = PanelUtilities.createToolbarButton(ipc.getSettings(),
                                                              editElementAction);
      buttonEdit.setRolloverEnabled(true);
      JButton buttonReferences = PanelUtilities.createToolbarButton(ipc.getSettings(),
                                                                    referencesElementAction);
      buttonReferences.setRolloverEnabled(true);
      JButton buttonDelete = PanelUtilities.createToolbarButton(ipc.getSettings(),
                                                                deleteElementAction);
      buttonDelete.setRolloverEnabled(true);
      JButton buttonDuplicate = PanelUtilities.createToolbarButton(ipc.getSettings(),
                                                                   duplicateElementAction);
      buttonDuplicate.setRolloverEnabled(true);

      panel.add(buttonNew);
      panel.add(Box.createRigidArea(new Dimension(3, 3)));
      panel.add(buttonEdit);
      panel.add(Box.createRigidArea(new Dimension(3, 3)));
      panel.add(buttonDelete);
      panel.add(Box.createRigidArea(new Dimension(5, 3)));
      if (!useBasicToolbar) {
         panel.add(buttonDuplicate);
         panel.add(Box.createRigidArea(new Dimension(3, 3)));
         panel.add(buttonReferences);
      }
      panel.add(Box.createHorizontalGlue());
      return panel;
   }

   protected JPanel createArrowPanel() {
      JPanel p = new JPanel();
      p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
      XMLCollection col = (XMLCollection) getOwner();
      boolean canRepos = JaWEManager.getInstance()
         .getJaWEController()
         .canRepositionElement(col, null);

      JButton buttonUp = new JButton();
      buttonUp.setIcon(ipc.getPanelSettings().getArrowUpImageIcon());

      buttonUp.setPreferredSize(new Dimension(16, 16));
      buttonUp.setEnabled(!myOwner.isReadOnly() && canRepos);
      buttonUp.addActionListener(moveUpAction);
      JButton buttonDown = new JButton();

      buttonDown.setIcon(ipc.getPanelSettings().getArrowDownImageIcon());

      buttonDown.setPreferredSize(new Dimension(16, 16));
      buttonDown.setEnabled(!myOwner.isReadOnly() && canRepos);
      buttonDown.addActionListener(moveDownAction);
      p.add(buttonUp);
      p.add(Box.createVerticalGlue());
      p.add(buttonDown);
      return p;
   }

   protected void adjustActions() {
      JaWEController jc = JaWEManager.getInstance().getJaWEController();
      JaWESelectionManager sm = jc.getSelectionManager();

      newElementAction.enableDisableAction();
      editElementAction.setEnabled((getSelectedElement() != null && sm.canEditProperties()));
      duplicateElementAction.setEnabled((getSelectedElement() != null && sm.canDuplicate()));
      deleteElementAction.setEnabled((getSelectedElement() != null && sm.canDelete()));
      referencesElementAction.setEnabled((getSelectedElement() != null && sm.canGetReferences()));

      boolean canRepos = JaWEManager.getInstance()
         .getJaWEController()
         .canRepositionElement((XMLCollection) getOwner(), null);
      moveUpAction.setEnabled(getSelectedElement() != null
                              && allParam.getSelectedIndex() > 0 && canRepos);
      moveDownAction.setEnabled(getSelectedElement() != null
                                && allParam.getSelectedIndex() < allParam.getModel()
                                   .getSize() - 1 && canRepos);
   }

   public void focusGained(FocusEvent ev) {

   }

   public void focusLost(FocusEvent ev) {
      if (lostFocusHandle && !ipc.getJaWEComponent().isUpdateInProgress()) {
         JaWESelectionManager sm = JaWEManager.getInstance()
            .getJaWEController()
            .getSelectionManager();
         List sel = sm.getSelectedElements();
         XMLElement firstSel = null;
         if (sel != null && sel.size() > 0) {
            firstSel = (XMLElement) sel.get(0);
         }
         if (firstSel != null && ((XMLCollection) getOwner()).contains(firstSel)) {
            ipc.getJaWEComponent().setUpdateInProgress(true);
            sm.setSelection(ipc.getActiveElement(), false);
            ipc.getJaWEComponent().setUpdateInProgress(false);
         }

         allParam.clearSelection();
      }

      adjustActions();
   }

   public void xmlElementChanged(XMLElementChangeInfo info) {
      if (info.getAction() == XMLElementChangeInfo.REMOVED) {
         Iterator it = info.getChangedSubElements().iterator();
         while (it.hasNext()) {
            XMLElement el = (XMLElement) it.next();
            removeElement(el);
         }
         if (notifyPanel) {
            getPanelContainer().panelChanged(this, null);
         }
      } else if (info.getAction() == XMLElementChangeInfo.INSERTED) {
         Iterator it = info.getChangedSubElements().iterator();
         while (it.hasNext()) {
            XMLElement el = (XMLElement) it.next();
            addElement(el);
         }
         if (notifyPanel) {
            getPanelContainer().panelChanged(this, null);
         }
      }
   }

   public void addElement(XMLElement el) {
      DefaultListModel listModel = (DefaultListModel) allParam.getModel();
      XMLElementView ev = getRow(el);
      listModel.addElement(ev);
   }

   public void removeElement(XMLElement el) {
      DefaultListModel listModel = (DefaultListModel) allParam.getModel();
      XMLElementView ev = getRow(el);
      if (ev != null)
         listModel.removeElement(ev);
   }

   public void cleanup() {
      myOwner.removeListener(this);
      allParam.removeFocusListener(this);
      allParam = null;
   }

   // button choice
   public void selectionChanged(ChoiceButton cbutton, Object change) {
      ipc.getJaWEComponent().setUpdateInProgress(false);
      lostFocusHandle = true;

      JaWEController jc = JaWEManager.getInstance().getJaWEController();
      JaWESelectionManager sm = jc.getSelectionManager();

      if (cbutton.getChoiceType().equals(JaWEType.class)) {
         JaWEType jtype = (JaWEType) change;

         String type = null;
         if (jtype != null) {
            type = jtype.getTypeId();
         }

         XMLElement parent = (XMLElement) jc.getSelectionManager()
            .getSelectedElements()
            .toArray()[0];
         if (!(parent instanceof XMLCollection))
            parent = parent.getParent();

         if (parent instanceof XMLCollection) {
            XMLCollection col = (XMLCollection) parent;
            XMLElement newEl = JaWEManager.getInstance()
               .getXPDLObjectFactory()
               .createXPDLObject(col, type, false);
            boolean isForModal = PanelUtilities.isForModalDialog(newEl, false);
            if (!isForModal && ipc.isModified()) {
               int sw = ipc.showModifiedWarning();
               if (sw == JOptionPane.CANCEL_OPTION
                   || (sw == JOptionPane.YES_OPTION && ipc.isModified())) {
                  ipc.getJaWEComponent().setUpdateInProgress(true);
                  sm.setSelection(ipc.getActiveElement(), true);
                  ipc.getJaWEComponent().setUpdateInProgress(false);
                  return;
               }
            }

            boolean updInProg = false;
            if (isForModal) {
               lostFocusHandle = false;
               ipc.getJaWEComponent().setUpdateInProgress(true);
               StandardXPDLElementEditor ed = new StandardXPDLElementEditor();
               ed.editXPDLElement(newEl);
               ipc.getJaWEComponent().setUpdateInProgress(false);
               lostFocusHandle = true;
               boolean statOK = (ed.getStatus() == StandardXPDLElementEditor.STATUS_OK);
               boolean canIns = true;
               if (statOK) {
                  canIns = jc.canInsertElement(col, newEl);
               }
               if (!statOK || !canIns) {
                  if (!canIns) {
                     jc.message(ed.getLanguageDependentString("WarningCannotInsertElement"),
                                 JOptionPane.WARNING_MESSAGE);
                  }
                  ipc.getJaWEComponent().setUpdateInProgress(true);
                  sm.setSelection(ipc.getActiveElement(), true);
                  ipc.getJaWEComponent().setUpdateInProgress(false);
                  return;
               }
               updInProg = true;
            }
            if (updInProg) {
               ipc.getJaWEComponent().setUpdateInProgress(true);
            }
            jc.startUndouableChange();
            col.add(newEl);
            List temp = new ArrayList();
            temp.add(newEl);
            jc.endUndouableChange(temp);
            if (updInProg) {
               setSelectedElement(newEl);
               adjustActions();
               ipc.getJaWEComponent().setUpdateInProgress(false);
            }
         }
      }
   }

   public Object getSelectedObject(ChoiceButton cbutton) {
      return null;
   }

   public List getChoices(ChoiceButton cbutton) {
      JaWEController jc = JaWEManager.getInstance().getJaWEController();

      ArrayList toRet = new ArrayList();
      if (cbutton.getChoiceType().equals(JaWEType.class)) {
         XMLElement el = (XMLElement) jc.getSelectionManager()
            .getSelectedElements()
            .toArray()[0];

         toRet.addAll(jc.getJaWETypes().getTypes(el));
      }

      return toRet;
   }

   public boolean isEmpty() {
      return allParam.getModel().getSize() == 0;
   }

   public boolean validateEntry() {
      if (isEmpty() && getOwner().isRequired() && !getOwner().isReadOnly()) {
         XMLBasicPanel.defaultErrorMessage(this.getWindow(), getTitle() + ": ");
         allParam.requestFocus();
         return false;
      }
      return true;
   }
}
