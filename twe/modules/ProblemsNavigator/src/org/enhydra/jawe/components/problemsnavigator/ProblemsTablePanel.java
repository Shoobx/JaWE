package org.enhydra.jawe.components.problemsnavigator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.base.controller.JaWEActions;
import org.enhydra.jawe.base.xpdlvalidator.ValidationError;
import org.enhydra.shark.xpdl.ParsingErrors;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.XMLValidationError;
import org.enhydra.shark.xpdl.elements.ActivitySet;
import org.enhydra.shark.xpdl.elements.Package;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;

/**
 * Creates a table panel for displaying problems.
 * 
 * @author Sasa Bojanic
 */
public class ProblemsTablePanel extends JPanel {

   protected JTable allItems;

   protected ProblemsNavigator controller;

   protected Vector columnNames;

   protected int sortingColumn = -1;

   protected ValidationErrorComparator vec = new ValidationErrorComparator();

   protected String veId="-1";
      
   protected boolean scInProgress=false;
   
   public ProblemsTablePanel(ProblemsNavigator pn) {
      controller = pn;
      setLayout(new BorderLayout());
      columnNames = getColumnNames(pn);
      allItems = createTable();
      setupTable();

      add(createScrollPane());
      // add(allItems.getTableHeader(),BorderLayout.NORTH);
      // add(allItems,BorderLayout.CENTER);

   }

   public JTable getTable() {
      return allItems;
   }

   public String getSelectedErrId() {
      return veId;
   }

   public ValidationError getSelectedElement() {
      int row = allItems.getSelectedRow();
      if (row >= 0) {
         return (ValidationError) allItems.getValueAt(row, 0);
      }
      return null;

   }

   public boolean setSelectedElement(String verrId) {
      try {
         int rc = allItems.getRowCount();
         if (rc > 0) {
            for (int i = 0; i < rc; i++) {
               if (verrId.equals(allItems.getValueAt(i, 7))) {
                  scInProgress=true;
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
                  scrollRectToVisible(rect);
                  veId=verrId;
//                  System.err.println("SSL veid="+veId);                  
                  scInProgress=false;
                  return true;
               }
            }
         }
      } catch (Exception ex) {
      }
      scInProgress=true;
      allItems.clearSelection();
      scInProgress=false;      
      return false;
   }

   public void addRow(ValidationError verr) {
      int rowpos = allItems.getRowCount();
      DefaultTableModel dtm = (DefaultTableModel) allItems.getModel();
      Vector v = getRow(verr);
      dtm.insertRow(rowpos, v);
   }

   public void removeRow(int row) {
      DefaultTableModel dtm = (DefaultTableModel) allItems.getModel();
      dtm.removeRow(row);
   }

   protected JTable createTable() {
      JTable t=new JTable(new Vector(), columnNames) {
         public boolean isCellEditable(int row, int col) {
            return false;
         }

         public String getToolTipText(MouseEvent e) {
            java.awt.Point p = e.getPoint();
            int rowIndex = rowAtPoint(p);
            int colIndex = columnAtPoint(p);
            int realColumnIndex = convertColumnIndexToModel(colIndex);
            DefaultTableModel dtm = (DefaultTableModel) this.getModel();
            String tooltip = null;
            if (realColumnIndex > 1 && realColumnIndex < 6) {
               try {
                  tooltip = dtm.getValueAt(rowIndex, realColumnIndex).toString();
               } catch (Exception ex) {
                  tooltip = super.getToolTipText(e);
               }
            } else {
               tooltip = super.getToolTipText(e);
            }
            return tooltip;
         }

      };
      
      Color bkgCol=new Color(245,245,245);
      if (controller.getSettings() instanceof ProblemsNavigatorSettings) {
         bkgCol=((ProblemsNavigatorSettings)controller.getSettings()).getBackgroundColor();
      }
      t.setBackground(bkgCol);
      
      return t;
   }

   protected void setupTable() {
      TableColumn column;
      // setting the first column (object column) to be invisible
      column = allItems.getColumnModel().getColumn(0);
      column.setMinWidth(0);
      column.setMaxWidth(0);
      column.setPreferredWidth(0);
      column.setResizable(false);

      column = allItems.getColumnModel().getColumn(1);
      column.setCellRenderer(new ProblemsTableCellRenderer());
      column.setMinWidth(20);
      column.setMaxWidth(20);
      column.setPreferredWidth(20);
      column.setResizable(false);

      column = allItems.getColumnModel().getColumn(2);
      column.setMinWidth(0);
      // column.setMaxWidth(1000);
      column.setPreferredWidth(100);
      column.setResizable(true);

      column = allItems.getColumnModel().getColumn(3);
      column.setMinWidth(0);
      // column.setMaxWidth(1000);
      column.setPreferredWidth(450);
      column.setResizable(true);

      column = allItems.getColumnModel().getColumn(4);
      column.setMinWidth(0);
      // column.setMaxWidth(1000);
      column.setPreferredWidth(150);
      column.setResizable(true);

      column = allItems.getColumnModel().getColumn(5);
      column.setMinWidth(0);
      // column.setMaxWidth(1000);
      column.setPreferredWidth(300);
      column.setResizable(true);

      // column = allItems.getColumnModel().getColumn(6);
      // column.setMinWidth(0);
      // column.setMaxWidth(10000);
      // column.setPreferredWidth(100);
      // column.setResizable(false);

      column = allItems.getColumnModel().getColumn(6);
      column.setMinWidth(0);
      column.setMaxWidth(0);
      column.setPreferredWidth(0);
      column.setResizable(false);

      column = allItems.getColumnModel().getColumn(7);
      column.setMinWidth(0);
      column.setMaxWidth(0);
      column.setPreferredWidth(0);
      column.setResizable(false);

      allItems.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

      // setting some table properties
      allItems.setColumnSelectionAllowed(false);
      allItems.setRowSelectionAllowed(true);
      allItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      allItems.getTableHeader().setReorderingAllowed(false);

      ListSelectionListener lsl = new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent e) {
            // Ignore extra messages.
            if (e.getValueIsAdjusting())
               return;

            ListSelectionModel ls = (ListSelectionModel) e.getSource();
            if (ls.isSelectionEmpty()) {
               veId="-1";
//               System.err.println("LSL1 veid="+veId);               
            } else {
               int selectedRow = ls.getMinSelectionIndex();
               int col = allItems.getSelectedColumn();
               XMLElement el = null;
               ValidationError verr=(ValidationError)allItems.getValueAt(selectedRow, 0);
               if (col == 4) {
                  el = verr.getElement();
               } else {
                  el = (XMLElement) allItems.getValueAt(selectedRow, 6);
               }
               veId=getErrorCode(verr);
//               System.err.println("LSL veid="+veId);
               if (!scInProgress) {
                  JaWEManager.getInstance().getJaWEController().getSelectionManager().setSelection(el, true);
               }
            }
         }
      };
      allItems.getSelectionModel().addListSelectionListener(lsl);

      allItems.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
         public void columnAdded(TableColumnModelEvent e) {

         }

         public void columnMarginChanged(ChangeEvent e) {

         }

         public void columnMoved(TableColumnModelEvent e) {

         }

         public void columnRemoved(TableColumnModelEvent e) {

         }

         public void columnSelectionChanged(ListSelectionEvent e) {
            changeSelection();
         }
      });

      allItems.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent me) {
            changeSelection();
            if (me.getClickCount() > 1) {
               ActionBase a = JaWEManager.getInstance().getJaWEController().getJaWEActions().getAction(
                     JaWEActions.EDIT_PROPERTIES_ACTION);
               if (a.isEnabled()) {
                  a.actionPerformed(null);
               }
            }
         }
      });

      JTableHeader h = allItems.getTableHeader();

      h.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent event) {
            TableColumnModel colModel = allItems.getColumnModel();
            int index = vec.getIndex();
            if (event != null) {
               index = colModel.getColumnIndexAtX(event.getX());
            }
            // int modelIndex = colModel.getColumn(index).getModelIndex();

            performSorting(index);
         }
      });
   }

   protected void changeSelection () {
      int row = allItems.getSelectedRow();
      int col = allItems.getSelectedColumn();
      XMLElement el = null;
      try {
         if (col == 4) {
            el = ((ValidationError) allItems.getValueAt(row, 0)).getElement();
         } else {
            el = (XMLElement) allItems.getValueAt(row, 6);
         }
         if (!scInProgress) {
            JaWEManager.getInstance().getJaWEController().getSelectionManager().setSelection(el, true);
         }
      } catch (Exception ex) {
      }      
   }
   
   protected Vector getColumnNames(ProblemsNavigator pn) {
      // creating a table which do not allow cell editing
      Vector cnames = new Vector();
      cnames.add("Object");
      cnames.add("");
      cnames.add(pn.getSettings().getLanguageDependentString("TypeKey"));
      cnames.add(pn.getSettings().getLanguageDependentString("DescriptionKey"));
      cnames.add(pn.getSettings().getLanguageDependentString("ElementKey"));
      cnames.add(pn.getSettings().getLanguageDependentString("locationKey"));
      cnames.add("Location");
      cnames.add("ErrId");
      return cnames;
   }

   public void fillTableContent(List elementsToShow) {
      List l = new ArrayList();
      Iterator it = elementsToShow.iterator();

      while (it.hasNext()) {
         ValidationError verr = (ValidationError) it.next();
         Vector v = getRow(verr);
         l.add(v);
      }
      sortTableContent(l);
   }

   public void sortTableContent(List l) {
      if (vec.getIndex() != -1) {
         Collections.sort(l, vec);
      }

      DefaultTableModel dtm = (DefaultTableModel) allItems.getModel();
      Iterator it = l.iterator();
      while (it.hasNext()) {
         Vector v = (Vector) it.next();
         dtm.addRow(v);
      }

   }

   protected Vector getRow(ValidationError verr) {
      Vector v = new Vector();
      XMLElement el = verr.getElement();
      v.add(verr);

      if (verr.getType().equals(XMLValidationError.TYPE_ERROR)) {
         v.add(((ProblemsNavigatorSettings) controller.getSettings()).getErrorIcon());
      } else {
         v.add(((ProblemsNavigatorSettings) controller.getSettings()).getWarningIcon());
      }  

      v.add(controller.getSettings().getLanguageDependentString(verr.getSubType()+"TypeKey"));
      String err = verr.getId();
      if (err.length() > 0) {
         String r = controller.getSettings().getLanguageDependentString(err);
         if (r != null) {
            err = r;
         }
      }
      if (verr.getDescription().length() > 0) {
         String desc=verr.getDescription();
         if (verr.getSubType().equals(XMLValidationError.SUB_TYPE_SCHEMA)) {
            if (desc.indexOf(ParsingErrors.ERROR)==0) {
               String ld=controller.getSettings().getLanguageDependentString(ParsingErrors.ERROR+"Key")+" "+controller.getSettings().getLanguageDependentString("AtLineNumberKey")+" ";
               desc=ld+desc.substring(ParsingErrors.ERROR.length()+ParsingErrors.AT_LINE_NO_STRING.length());
            } else if (desc.indexOf(ParsingErrors.WARNING)==0) {
               String ld=controller.getSettings().getLanguageDependentString(ParsingErrors.WARNING+"Key")+" "+controller.getSettings().getLanguageDependentString("AtLineNumberKey")+" ";
               desc=ld+desc.substring(ParsingErrors.WARNING.length()+ParsingErrors.AT_LINE_NO_STRING.length());
            } else if (desc.indexOf(ParsingErrors.FATAL_ERROR)==0) {
               String ld=controller.getSettings().getLanguageDependentString(ParsingErrors.FATAL_ERROR+"Key")+" "+controller.getSettings().getLanguageDependentString("AtLineNumberKey")+" ";
               desc=ld+desc.substring(ParsingErrors.FATAL_ERROR.length()+ParsingErrors.AT_LINE_NO_STRING.length());
            }
         } else {
            err += ": ";
         }
         err += desc;
      }
      v.add(err);
      v.add(JaWEManager.getInstance().getLabelGenerator().getLabel(el) + " - "
            + JaWEManager.getInstance().getDisplayNameGenerator().getDisplayName(el));

      XMLElement location = Utils.getLocation(el);

      ActivitySet as = XMLUtil.getActivitySet(el);
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(el);
      Package pkg = XMLUtil.getPackage(el);

      String loc = controller.getSettings().getLanguageDependentString("PackageKey") + " '" + pkg.getId() + "'";
      if (wp != null) {
         loc += ", " + controller.getSettings().getLanguageDependentString("WorkflowProcessKey") + " '" + wp.getId()
               + "'";
      }
      if (as != null) {
         loc += ", " + controller.getSettings().getLanguageDependentString("ActivitySetKey") + " '" + as.getId() + "'";
      }
      if (location != as && location != wp && location != pkg) {
         loc += ", " + controller.getSettings().getLanguageDependentString(location.toName() + "Key") + " '"
               + ((XMLComplexElement) location).get("Id").toValue()+"'";
      }
      if (el != location && el != as && el != wp && el != pkg) {
         loc += " -> " + controller.getSettings().getLanguageDependentString(el.toName() + "Key");
      }

      v.add(loc);
      v.add(location);
      v.add(getErrorCode(verr));
      return v;
   }

   protected String getErrorCode (ValidationError verr) {
      return String.valueOf(verr.getElement().hashCode())+"-"+verr.getType()+"-"+verr.getSubType()+"-"+verr.getId();      
   }
   
   protected JScrollPane createScrollPane() {
      // creates panel
      JScrollPane allItemsPane = new JScrollPane();

      allItemsPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      allItemsPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

      allItemsPane.setViewportView(allItems);
      return allItemsPane;
   }

   protected int getElementRow(XMLElement el) {
      int row = -1;
      for (int i = 0; i < allItems.getRowCount(); i++) {
         XMLElement toCompare = ((ValidationError) allItems.getValueAt(i, 0)).getElement();
         if (el==toCompare) {
            row = i;
            break;
         }
      }
      return row;
   }

   public void cleanup() {
      DefaultTableModel dtm = (DefaultTableModel) allItems.getModel();
      for (int i = dtm.getRowCount() - 1; i >= 0; i--) {
         dtm.removeRow(i);
      }
      veId="-1";
//      System.err.println("CLN veid="+veId);
   }

   protected void performSorting(int index) {
      DefaultTableModel dtm = (DefaultTableModel) allItems.getModel();
      List l = new ArrayList(dtm.getDataVector());
      String serr=veId;

      cleanup();
      vec.setIndex(index);
      sortTableContent(l);
      if (!serr.equals("-1")) {
//         System.err.println("PSRTOLD veid="+serr);
         setSelectedElement(serr);
      }
   }

   class ValidationErrorComparator implements Comparator {

      protected int index = -1;

      protected boolean ascending;

      public ValidationErrorComparator() {
      }

      public void setIndex(int index) {
         if (index != this.index) {
            ascending = false;
         } else {
            ascending = !ascending;
         }
         this.index = index;
      }

      public int getIndex() {
         return index;
      }

      public int compare(Object first, Object second) {
         if (first instanceof Vector && second instanceof Vector) {
            try {
               String str1 = ((Vector) first).elementAt(index).toString();
               String str2 = ((Vector) second).elementAt(index).toString();
               if (ascending) {
                  return str1.compareTo(str2);
               }
               return str2.compareTo(str1);
            } catch (Exception ex) {
               // exception can happen if there's no value in sorted column
            }
         }

         return 1;
      }

   }

}
