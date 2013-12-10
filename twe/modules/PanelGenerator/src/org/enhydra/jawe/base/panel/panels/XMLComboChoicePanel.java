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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLElement;

/**
* Creates a combo panel and a panel which type is determined by the
* chosen element from combo panel.
* @author Sasa Bojanic
* @author Zoran Milakovic
*/
public class XMLComboChoicePanel extends XMLBasicPanel{

   protected XMLPanel prevPanel=null;
   protected XMLComboPanel pCombo;
   protected JScrollPane jsp;

   public XMLComboChoicePanel (PanelContainer pc,XMLElement myOwnerL,boolean isEnabled) {
      this(pc,myOwnerL,false,"",true,false,true,false,isEnabled);
   }

   public XMLComboChoicePanel (PanelContainer pc,XMLElement myOwner,boolean addListener,String title,
      boolean isVertical,boolean isChoiceVertical,boolean hasBorder,boolean isEditable,boolean isEnabled) {

      super(pc,myOwner,title,isVertical,hasBorder,true);


      int noOfPanels=2;
      if (myOwner instanceof XMLAttribute) {
         noOfPanels=1;
      }

      
      pCombo=new XMLComboPanel(pc,myOwner,null,false,false,isChoiceVertical,isEditable,isEnabled);
//System.err.println(pCombo.getSelectedItem().getClass().getName());
      XMLElement choosen=(XMLElement)pCombo.getSelectedItem();         
      add(pCombo);

      if (noOfPanels==2) {
         jsp=new JScrollPane();
         jsp.setAlignmentX(Component.LEFT_ALIGNMENT);
         jsp.setAlignmentY(Component.TOP_ALIGNMENT);
         jsp.setMinimumSize(new Dimension(250,200));
         
         add(jsp);

         prevPanel= pc.getPanelGenerator().getPanel(choosen);
         jsp.setViewportView(prevPanel);
         
         final JComboBox jcb=pCombo.getComboBox();
         jcb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
//               if (prevPanel!=null) {
//                  prevPanel.setElements();
//               }
               XMLElement chosen = (XMLElement)pCombo.getSelectedItem();
               if (chosen!=null) {
                  prevPanel= getPanelContainer().getPanelGenerator().getPanel(chosen);
                  jsp.setViewportView(prevPanel);
               }
            }
         });

      }

      if (isVertical) {
         add(Box.createVerticalGlue());
      }
      else {
         add(Box.createHorizontalGlue());
      }

   }
   
   public XMLComboPanel getComboPanel () {
      return pCombo;
   }

   public boolean validateEntry () {
      // checking Combo panel
      boolean isOK=pCombo.validateEntry();
      // checking implementation panel
      if (!(getOwner() instanceof XMLAttribute)) {
         JViewport jvp=(JViewport)jsp.getComponent(0);
         if (jvp.getComponentCount()>0) {
            XMLPanel p=(XMLPanel)jvp.getComponent(0);
            isOK=isOK && p.validateEntry();
         }
      }
      return isOK;
   }


   public void setElements () {
      if (!getOwner().isReadOnly()) {
         // setting Combo panel
         pCombo.setElements();
         // setting implementation panel
         if (!(myOwner instanceof XMLAttribute)) {
            JViewport jvp=jsp.getViewport();
            if (jvp.getComponentCount()>0) {
               XMLPanel p=(XMLPanel)jvp.getComponent(0);
               p.setElements();
            }
         }
      }
   }

   public Object getValue () {
      return pCombo.getValue();
   }
   
   public void cleanup () {
      pCombo.cleanup();
      if (!(myOwner instanceof XMLAttribute)) {
         JViewport jvp=jsp.getViewport();
         if (jvp.getComponentCount()>0) {
            XMLPanel p=(XMLPanel)jvp.getComponent(0);
            p.cleanup();
         }
      }      
   }

   public void requestFocus () {
      pCombo.requestFocus();
   }
   
}
