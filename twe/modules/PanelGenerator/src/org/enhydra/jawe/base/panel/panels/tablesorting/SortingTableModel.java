package org.enhydra.jawe.base.panel.panels.tablesorting;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLCollectionElement;
import org.enhydra.shark.xpdl.XMLElement;

public class SortingTableModel extends DefaultTableModel {

   XMLCollection owner;

   public SortingTableModel(XMLCollection c,Vector data, Vector names) {
      super(data, names);
      this.owner=c;
   }

   public void sortByColumn(int col, boolean ascending) {
      Vector dv=getDataVector();
      Vector v=new Vector(dv);
      int vs=v.size();
      for (int i=vs-1; i>=0; i--) {
         XMLElement el=(XMLElement)((Vector)v.elementAt(i)).elementAt(0);
         if (el instanceof XMLCollectionElement) {
            XMLCollectionElement cel=(XMLCollectionElement)el;
            XMLCollection celOwner=(XMLCollection)cel.getParent();
            if (celOwner==null || celOwner!=owner) {
               v.remove(i);
            }
         }
      }
      vs=v.size();
      if (vs>0) {
         Collections.sort(v,new ColumnSortingComparator(col, ascending));
         for (int i=0; i<vs; i++) {
            dv.set(i,v.get(i));
         }
      }
   }

   public Class getColumnClass(int c) {
      return getValueAt(0, c).getClass();
   }

   static class ColumnSortingComparator implements Comparator {

      protected int index;
      protected boolean ascending;

      public ColumnSortingComparator(int index, boolean ascending) {
         this.index = index;
         this.ascending = ascending;
      }

      public int compare(Object first, Object second) {
         if (first instanceof Vector && second instanceof Vector) {
            try {
               String str1 = ((Vector)first).elementAt(index).toString();
               String str2 = ((Vector)second).elementAt(index).toString();
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
