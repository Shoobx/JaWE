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

package org.enhydra.jawe.base.panel.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.enhydra.jawe.ButtonPropertyChangedListener;
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
import org.enhydra.jawe.base.panel.panels.tablesorting.SortingTable;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLElementChangeInfo;
import org.enhydra.jxpdl.XMLElementChangeListener;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ExtendedAttributes;
import org.enhydra.jxpdl.elements.Performer;

/**
 * Creates a table panel.
 * 
 * @author Sasa Bojanic
 * @author Zoran Milakovic
 * @author Miroslav Popov
 * @author Danijel Predarski
 */
public class XMLTablePanel extends XMLBasicPanel implements
                                                XMLElementChangeListener,
                                                FocusListener,
                                                ChoiceButtonListener {

   public static Color FOREIGN_EL_COLOR_BKG = Color.lightGray;

   public static Color SPEC_EL_COLOR_BKG = Color.orange;

   protected static Dimension miniTableDimension = new Dimension(450, 125);

   protected static Dimension smallTableDimension = new Dimension(450, 200);

   protected static Dimension mediumTableDimension = new Dimension(550, 200);

   protected static Dimension largeTableDimension = new Dimension(650, 200);

   /**
    * Object which we are replacing from one place to another within the list by dragging
    * it.
    */
   protected XMLElement movingElement;

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

   protected int replacingElementPosition;

   /** Indicates if object is being dragged. */
   protected boolean dragging = false;

   /**
    * Indicates if the code for changing object position within the list is executed.
    */
   protected boolean changing = false;

   protected boolean lostFocusHandle = true;

   protected JTable allItems;

   protected JPanel toolbox;

   protected Vector columnNames;

   protected List columnsToShow;

   protected InlinePanel ipc;

   protected Dimension customDim;

   protected NewActionBase newElementAction = null;

   protected boolean notifyPanel;

   protected int newPos;

   protected int oldPos;

   public XMLTablePanel(InlinePanel ipc,
                        XMLCollection myOwner,
                        List columnsToShow,
                        List elementsToShow,
                        String title,
                        boolean hasBorder,
                        boolean hasEmptyBorder,
                        boolean automaticWidth,
                        boolean miniDimension,
                        final boolean colors,
                        final boolean showArrows) {
      this(ipc,
           myOwner,
           columnsToShow,
           elementsToShow,
           title,
           hasBorder,
           hasEmptyBorder,
           automaticWidth,
           miniDimension,
           null,
           colors,
           showArrows,
           false,
           false);
   }

   public XMLTablePanel(final InlinePanel ipc,
                        final XMLCollection myOwner,
                        List columnsToShow,
                        List elementsToShow,
                        String title,
                        boolean hasBorder,
                        boolean hasEmptyBorder,
                        boolean automaticWidth,
                        boolean miniDimension,
                        Dimension customDim,
                        final boolean colors,
                        final boolean showArrows,
                        boolean useBasicToolbar,
                        boolean notifyPanel) {

      super(ipc, myOwner, title, true, hasBorder, hasEmptyBorder);

      this.ipc = ipc;
      this.customDim = customDim;
      this.notifyPanel = notifyPanel;

      Class pfea = null;
      if (myOwner instanceof ExtendedAttributes) {
         pfea = myOwner.getParent().getClass();
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
            allItems.clearSelection();
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

      columnNames = getColumnNames(columnsToShow);
      this.columnsToShow = columnsToShow;

      // System.err.println("CNMS="+columnNames);
      // System.err.println("CTS="+columnsToShow);
      allItems = createTable(colors);
      setupTable(miniDimension, automaticWidth, showArrows);
      fillTableContent(elementsToShow);

      toolbox = createToolbar(useBasicToolbar);
      JPanel paneAndArrows = new JPanel();
      paneAndArrows.setLayout(new BoxLayout(paneAndArrows, BoxLayout.X_AXIS));
      paneAndArrows.add(createScrollPane());

      // if (showArrows) {
      // JPanel p = createArrowPanel();
      // paneAndArrows.add(Box.createRigidArea(new Dimension(5, 0)));
      // paneAndArrows.add(p);
      // }

      add(toolbox);
      add(Box.createVerticalStrut(3));
      add(paneAndArrows);

      allItems.addFocusListener(this);
      adjustActions();
   }

   public JTable getTable() {
      return allItems;
   }

   public XMLElement getSelectedElement() {
      int row = allItems.getSelectedRow();
      if (row >= 0) {
         return (XMLElement) allItems.getValueAt(row, 0);
      }
      return null;

   }

   public boolean setSelectedElement(Object el) {
      try {
         int rc = allItems.getRowCount();
         if (rc > 0) {
            for (int i = 0; i < rc; i++) {
               if (el == allItems.getValueAt(i, 0)) {
                  allItems.setRowSelectionInterval(i, i);

                  // focus the row

                  JViewport viewport = (JViewport) allItems.getParent();
                  // This rectangle is relative to the table where the
                  // northwest corner of cell (0,0) is always (0,0).
                  Rectangle rect = allItems.getCellRect(i, 0, true);
                  // The location of the viewport relative to the table
                  Point pt = viewport.getViewPosition();
                  // Translate the cell location so that it is relative
                  // to the view, assuming the northwest corner of the
                  // view is (0,0)
                  rect.setLocation(rect.x - pt.x, rect.y - pt.y);
                  // Scroll the area into view
                  viewport.scrollRectToVisible(rect);

                  return true;
               }
            }
         }
      } catch (Exception ex) {
      }
      return false;
   }

   public void setSelectedRow(int row) {
      try {
         allItems.setRowSelectionInterval(row, row);

         ipc.getJaWEComponent().setUpdateInProgress(true);
         JaWEManager.getInstance()
            .getJaWEController()
            .getSelectionManager()
            .setSelection(getSelectedElement(), false);
         ipc.getJaWEComponent().setUpdateInProgress(false);

         adjustActions();
      } catch (Exception e) {
      }
   }

   public void addRow(XMLElement e) {
      int rowpos = allItems.getRowCount();
      DefaultTableModel dtm = (DefaultTableModel) allItems.getModel();
      Vector v = getRow(e);
      dtm.insertRow(rowpos, v);
   }

   public void removeRow(int row) {
      DefaultTableModel dtm = (DefaultTableModel) allItems.getModel();
      dtm.removeRow(row);
   }

   protected void moveItem(int upOrDown) {
      newMovingElementPosition = movingElementPosition;
      if (newMovingElementPosition == -1) {
         return;
      }
      lostFocusHandle = false;
      if (upOrDown == 0) {
         newMovingElementPosition--;
      } else {
         newMovingElementPosition++;
      }
      lostFocusHandle = true;
      moveItem();
   }

   protected void moveItem() {
      changing = true;
      XMLCollection owncol = (XMLCollection) getOwner();
      int rowCnt = allItems.getRowCount();
      if (movingElement == null
          || movingElementPosition == -1 || newMovingElementPosition == -1
          || newMovingElementPosition == movingElementPosition
          || (rowCnt - 1) < movingElementPosition
          || (rowCnt - 1) < newMovingElementPosition || !owncol.contains(movingElement)) {
         changing = false;
         return;
      }

      if (JaWEManager.getInstance()
         .getJaWEController()
         .canRepositionElement(owncol, movingElement)) {
         ipc.getJaWEComponent().setUpdateInProgress(true);
         XMLElement currentElementAtPosition = (XMLElement) allItems.getValueAt(replacingElementPosition,
                                                                                0);
         int newpos = owncol.indexOf(currentElementAtPosition);

         // DefaultTableModel dtm = (DefaultTableModel) allItems.getModel();
         Vector v = getRow(movingElement);
         /*
          * dtm.removeRow(movingElementPosition); dtm.insertRow(newMovingElementPosition,
          * v);
          */

         JaWEController jc = JaWEManager.getInstance().getJaWEController();
         jc.startUndouableChange();
         owncol.reposition(movingElement, newpos);
         List toSelect = new ArrayList();
         toSelect.add(movingElement);
         jc.endUndouableChange(toSelect);

         setSelectedRow(newMovingElementPosition);

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
            allItems.clearSelection();
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
            Window w = getWindow();
            if (w instanceof JDialog && ((JDialog) w).isModal()) {
               JaWEManager.getInstance()
                  .getXPDLElementEditor()
                  .editXPDLElement(editElement);
            } else {
               JaWEManager.getInstance()
                  .getJaWEController()
                  .getSelectionManager()
                  .setSelection(editElement, true);
            }

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
         moveItem(0);
         adjustActions();
      }
   };

   protected Action moveDownAction = new AbstractAction("MoveDown") {
      public void actionPerformed(ActionEvent ae) {
         moveItem(1);
         adjustActions();
      }
   };

   protected Vector getColumnNames(List columnsToShow) {
      // creating a table which do not allow cell editing
      Vector cnames = new Vector();
      cnames.add("Object");
      XMLElement cel = ((XMLCollection) getOwner()).generateNewElement();
      if (cel instanceof XMLComplexElement) {
         Iterator it = columnsToShow.iterator();
         while (it.hasNext()) {
            String elemName = (String) it.next();
            if (!elemName.equals("ElementValue")) {
               XMLElement el = ((XMLComplexElement) cel).get(elemName);
               if (el == null && elemName.equals("Performer") && cel instanceof Activity) {
                  el = new Performer((Activity) cel);
               }
               if (el != null) {
                  cnames.add(JaWEManager.getInstance().getLabelGenerator().getLabel(el));
               } else {
                  // System.err.println("RRRRRRRRRRRRrrr EF elname "+elemName);
                  it.remove();
               }
            } else {
               cnames.add(ipc.getSettings().getLanguageDependentString("ElementValueKey"));
            }
         }
      } else {
         cnames.add(JaWEManager.getInstance().getLabelGenerator().getLabel(cel));
      }
      return cnames;
   }

   protected JTable createTable(final boolean colors) {
      JTable t = new SortingTable(this, new Vector(), columnNames, ipc) {

         public boolean isCellEditable(int row, int col) {
            return false;
         }

         // This table colors elements depending on their owner
         public Component prepareRenderer(TableCellRenderer renderer,
                                          int rowIndex,
                                          int vColIndex) {
            Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
            if (!isCellSelected(rowIndex, vColIndex) && colors) {
               XMLElement el = (XMLElement) getValueAt(rowIndex, 0);
               if (el instanceof XMLCollectionElement) {
                  XMLCollectionElement cel = (XMLCollectionElement) el;
                  XMLCollection celOwner = (XMLCollection) cel.getParent();
                  if (celOwner == null) {
                     c.setBackground(SPEC_EL_COLOR_BKG);
                  } else if (celOwner != getOwner()) {
                     c.setBackground(FOREIGN_EL_COLOR_BKG);
                  } else {
                     c.setBackground(getBackground());
                  }
               } else {
                  c.setBackground(getBackground());
               }
            }

            return c;
         }
      };

      Color bkgCol = new Color(245, 245, 245);
      if (ipc.getSettings() instanceof PanelSettings) {
         bkgCol = ((PanelSettings) ipc.getSettings()).getBackgroundColor();
      }
      t.setBackground(bkgCol);

      return t;
   }

   protected void setupTable(boolean miniDimension,
                             boolean automaticWidth,
                             final boolean showArrows) {
      TableColumn column;
      // setting the first column (object column) to be invisible
      column = allItems.getColumnModel().getColumn(0);
      column.setMinWidth(0);
      column.setMaxWidth(0);
      column.setPreferredWidth(0);
      column.setResizable(false);
      // setting fields that will not be displayed within the table

      // setting some table properties
      allItems.setColumnSelectionAllowed(false);
      allItems.setRowSelectionAllowed(true);
      allItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      allItems.getTableHeader().setReorderingAllowed(false);

      Dimension tDim;
      int noOfVisibleColumns = columnNames.size() - 1;
      if (customDim != null) {
         tDim = customDim;
      } else if (miniDimension) {
         tDim = new Dimension(miniTableDimension);
      } else if (noOfVisibleColumns <= 3) {
         tDim = new Dimension(smallTableDimension);
      } else if (noOfVisibleColumns <= 5) {
         tDim = new Dimension(mediumTableDimension);
      } else {
         tDim = new Dimension(largeTableDimension);
      }

      if (automaticWidth) {
         tDim.width = allItems.getPreferredScrollableViewportSize().width;
      }
      allItems.setPreferredScrollableViewportSize(new Dimension(tDim));

      allItems.getInputMap(JComponent.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "edit");
      allItems.getActionMap().put("edit", editElementAction);

      allItems.getInputMap(JComponent.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false), "delete");
      allItems.getActionMap().put("delete", deleteElementAction);

      final XMLCollection col = (XMLCollection) getOwner();
      final boolean canRepos = JaWEManager.getInstance()
         .getJaWEController()
         .canRepositionElement(col, null);

      if (!getOwner().isReadOnly())
         allItems.setToolTipText(pc.getLanguageDependentString("MessageDragItemToChangeItsPosition"));

      // mouse listener for editing on double-click
      allItems.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent me) {
            if (me.getClickCount() > 1) {
               editElementAction.actionPerformed(null);
            }
         }

         /** Marks the object which place within the table will be changed. */
         public void mousePressed(MouseEvent me) {
            movingElement = null;
            movingElementPosition = -1;
            if (showArrows && !getOwner().isReadOnly() && canRepos) {
               dragging = true;
            }
            try {
               movingElementPosition = allItems.getSelectedRow();
               oldPos = movingElementPosition;
               if (movingElementPosition >= 0) {
                  movingElement = (XMLElement) allItems.getValueAt(movingElementPosition,
                                                                   0);
                  ipc.getJaWEComponent().setUpdateInProgress(true);
                  JaWEManager.getInstance()
                     .getJaWEController()
                     .getSelectionManager()
                     .setSelection(movingElement, false);
                  ipc.getJaWEComponent().setUpdateInProgress(false);
                  adjustActions();
               }
            } catch (Exception ex) {
            }
         }

         /** Just indicates that dragging is over. */
         public void mouseReleased(MouseEvent me) {
            dragging = false;
            newMovingElementPosition = allItems.getSelectedRow();
            moveItem();
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
               ex.printStackTrace();
            }
         }
      });

      /** Changes position of object within the list. */
      if (showArrows && !myOwner.isReadOnly() && canRepos) {// &&
         // ((XMLCollection)getOwner()).getParent().isReadOnly())))
         // {
         ListSelectionModel rowSM = allItems.getSelectionModel();
         rowSM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse) {
               if (dragging) {
                  // oldPos = movingElementPosition;
                  newPos = allItems.getSelectedRow();
                  if (oldPos != newPos) {
                     for (int i = 0; i < allItems.getColumnCount(); i++) {
                        Object oldVal = allItems.getValueAt(oldPos, i);
                        Object newVal = allItems.getValueAt(newPos, i);
                        allItems.setValueAt(oldVal, newPos, i);
                        allItems.setValueAt(newVal, oldPos, i);
                     }
                     replacingElementPosition = oldPos;
                  }
                  oldPos = newPos;
               }
            }
         });
      }
      /*
       * JTableHeader h = allItems.getTableHeader(); h.addMouseListener(new MouseAdapter()
       * { public void mouseClicked(MouseEvent event) { performSorting(event); } });
       */

   }

   protected void fillTableContent(List elementsToShow) {
      DefaultTableModel dtm = (DefaultTableModel) allItems.getModel();
      Iterator it = elementsToShow.iterator();

      while (it.hasNext()) {
         XMLElement elem = (XMLElement) it.next();
         Vector v = getRow(elem);
         dtm.addRow(v);
      }
   }

   protected Vector getRow(XMLElement elem) {
      Vector v = new Vector();
      if (elem instanceof XMLComplexElement) {
         Iterator itAllElems = columnsToShow.iterator();
         v = new Vector();
         XMLComplexElement cmel = (XMLComplexElement) elem;
         while (itAllElems.hasNext()) {
            String elName = (String) itAllElems.next();
            if (!elName.equals("ElementValue")) {
               XMLElement el = cmel.get(elName);
               if (el == null && elName.equals("Performer") && cmel instanceof Activity) {
                  el = ((Activity) cmel).getFirstPerformerObj();
                  if (el == null) {
                     el = new Performer((Activity) cmel);
                  }
               }
               if (el != null) {
                  v.add(new XMLElementView(ipc, el, XMLElementView.TOVALUE));
               }
            } else {
               v.add(new XMLElementView(ipc, elem.toValue(), false));
            }
         }
      } else {
         v.add(new XMLElementView(ipc, elem, XMLElementView.TOVALUE));
      }
      v.add(0, elem);
      return v;
   }

   protected JScrollPane createScrollPane() {
      // creates panel
      JScrollPane allItemsPane = new JScrollPane();
      allItemsPane.setViewportView(allItems);
      return allItemsPane;
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

      JButton buttonUp = new JButton();
      buttonUp.setIcon(ipc.getPanelSettings().getArrowUpImageIcon());
      buttonUp.setPreferredSize(new Dimension(16, 16));
      buttonUp.setEnabled(false);
      buttonUp.addActionListener(moveUpAction);
      moveUpAction.addPropertyChangeListener(new ButtonPropertyChangedListener(buttonUp));
      buttonUp.addFocusListener(this);

      JButton buttonDown = new JButton();
      buttonDown.setIcon(ipc.getPanelSettings().getArrowDownImageIcon());
      buttonDown.setPreferredSize(new Dimension(16, 16));
      buttonDown.setEnabled(false);
      buttonDown.addActionListener(moveDownAction);
      moveDownAction.addPropertyChangeListener(new ButtonPropertyChangedListener(buttonDown));
      buttonDown.addFocusListener(this);

      p.add(buttonUp);
      p.add(Box.createVerticalGlue());
      p.add(buttonDown);
      return p;
   }

   public void xmlElementChanged(XMLElementChangeInfo info) {
      if (info.getAction() == XMLElementChangeInfo.REMOVED) {
         Iterator it = info.getChangedSubElements().iterator();
         while (it.hasNext()) {
            XMLElement el = (XMLElement) it.next();
            int row = getElementRow(el);
            // System.out.println("Removing row " + row + " for element " + el);
            if (row != -1) {
               removeRow(row);
            }
         }
         if (notifyPanel) {
            getPanelContainer().panelChanged(this, null);
         }
      } else if (info.getAction() == XMLElementChangeInfo.INSERTED) {
         Iterator it = info.getChangedSubElements().iterator();
         while (it.hasNext()) {
            XMLElement el = (XMLElement) it.next();
            addRow(el);
         }
         if (notifyPanel) {
            getPanelContainer().panelChanged(this, null);
         }
      }
   }

   protected int getElementRow(XMLElement el) {
      int row = -1;
      for (int i = 0; i < allItems.getRowCount(); i++) {
         XMLElement toCompare = (XMLElement) allItems.getValueAt(i, 0);
         if (el == toCompare) {
            row = i;
            break;
         }
      }
      return row;
   }

   protected void adjustActions() {
      JaWEController jc = JaWEManager.getInstance().getJaWEController();
      JaWESelectionManager sm = jc.getSelectionManager();

      newElementAction.enableDisableAction();
      referencesElementAction.setEnabled((getSelectedElement() != null && sm.canGetReferences()));
      editElementAction.setEnabled((getSelectedElement() != null && sm.canEditProperties()));
      duplicateElementAction.setEnabled((getSelectedElement() != null && sm.canDuplicate()));
      deleteElementAction.setEnabled((getSelectedElement() != null && sm.canDelete()));

      // boolean canRepos =
      // JaWEManager.getInstance().getJaWEController().canRepositionElement((XMLCollection)getOwner(),
      // null);
      // moveUpAction.setEnabled(getSelectedElement()!=null && allItems.getSelectedRow()>0
      // && canRepos);
      // moveDownAction.setEnabled(getSelectedElement()!=null &&
      // allItems.getSelectedRow()<allItems.getRowCount()-1 && canRepos);

   }

   public void focusGained(FocusEvent ev) {
      adjustActions();
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

         if (allItems != null) {
            allItems.clearSelection();
         }
      }

      adjustActions();
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

         XMLElement parent = sm.getSelectedElement();
         if (!(parent instanceof XMLCollection))
            parent = parent.getParent();

         if (parent instanceof XMLCollection) {
            XMLCollection col = (XMLCollection) parent;
            XMLElement newEl = JaWEManager.getInstance()
               .getXPDLObjectFactory()
               .createXPDLObject(col, type, false);
            boolean isForModal = PanelUtilities.isForModalDialog(newEl);
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

   public void cleanup() {
      myOwner.removeListener(this);
      allItems.removeFocusListener(this);
   }

   public boolean isEmpty() {
      return allItems.getRowCount() == 0;
   }

   public boolean validateEntry() {
      if (isEmpty() && getOwner().isRequired() && !getOwner().isReadOnly()) {
         XMLBasicPanel.defaultErrorMessage(this.getWindow(), getTitle() + ": ");
         allItems.requestFocus();
         return false;
      }
      return true;
   }

}
