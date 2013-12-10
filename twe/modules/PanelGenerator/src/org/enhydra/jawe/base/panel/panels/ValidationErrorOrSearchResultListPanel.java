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
import org.enhydra.jxpdl.XMLElement;

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
