package org.enhydra.jawe.base.panel.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.enhydra.jawe.SearchResult;
import org.enhydra.jawe.base.xpdlvalidator.ValidationError;
import org.enhydra.shark.xpdl.XMLElement;

/**
 * Creates a references panel.
 * @author Sasa Bojanic
 */
public class ValidationErrorOrSearchResultListPanel extends XMLBasicPanel {

   protected static Dimension listDimension=new Dimension(750,250);

   protected JList allParam;
   
   public ValidationErrorOrSearchResultListPanel (
         XMLElement checked,
         List errors,
         String title,
         boolean hasBorder,
         boolean hasEmptyBorder) {

      super(null,checked,title,true,hasBorder,hasEmptyBorder);

      allParam=createList();      
      setupList();

      fillListContent(errors);
      
      JScrollPane scrollParam=new JScrollPane();
      scrollParam.setAlignmentX(Component.LEFT_ALIGNMENT);
      //scrollParam.setAlignmentY(Component.TOP_ALIGNMENT);

      scrollParam.setViewportView(allParam);
      scrollParam.setPreferredSize(new Dimension(listDimension));
      
      add(scrollParam);
   }

   public JList getList () {
      return allParam;
   }

   protected XMLElement getElementToShow () {
      XMLElement editElement=null;
      if (allParam.getModel().getSize()>0) {
         try {
            Object el=allParam.getSelectedValue();
            if (el instanceof ValidationError) {
               editElement=((ValidationError) el).getElement();
            } else {
               editElement=((SearchResult) el).getElement();
            }
         } catch (Exception ex) {
             ex.printStackTrace();
         }
      }         
      return editElement;      
   }
   
   protected JList createList () {
      DefaultListModel listModel=new DefaultListModel();

      JList l=new JList(listModel);
      return l;      
   }
   
   protected void setupList () {
      allParam.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      allParam.setAlignmentX(Component.LEFT_ALIGNMENT);
      allParam.setAlignmentY(Component.TOP_ALIGNMENT);
   }
   
   protected void fillListContent (List errors) {
      DefaultListModel listModel=(DefaultListModel)allParam.getModel();      
      Iterator it = errors.iterator();
      while (it.hasNext()) {
         listModel.addElement(it.next());      
      }      
   }
   
   public void cleanup () {
      allParam=null;
   }
   
   public List getElements () {
      List l=new ArrayList();
      DefaultListModel listModel=(DefaultListModel)allParam.getModel();
      for (int i=0; i<listModel.getSize(); i++) {         
         l.add(listModel.getElementAt(i));
      }
      return l;
   }
   
   public void removeElement (Object el) {
      DefaultListModel listModel=(DefaultListModel)allParam.getModel();
      listModel.removeElement(el);
   }
   
}
