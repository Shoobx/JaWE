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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.List;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLComplexChoice;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;

/**
* Creates a panel with radio buttons.
* @author Sasa Bojanic
* @author Zoran Milakovic
*/
public class XMLRadioPanel extends XMLBasicPanel {
   protected ButtonGroup rGroup;

   public XMLRadioPanel (
         PanelContainer pc,
         XMLElement myOwner,
         String title,
         boolean isVertical,
         boolean hasBorder,
         boolean hasEmptyBorder,
         boolean isEnabled) {

      super(pc,myOwner,title,false,hasBorder,hasEmptyBorder);
      
      boolean rightAllignment = false;
      if (pc!=null) {
         rightAllignment=pc.getSettings().getSettingBoolean("XMLBasicPanel.RightAllignment");
      }

      List choices=null;
      Object moChoosen=null;
      if (myOwner instanceof XMLAttribute) {
         choices=PanelUtilities.toXMLElementViewList(pc,((XMLAttribute)myOwner).getChoices(),true);
         moChoosen=new XMLElementView(pc,myOwner.toValue(),true);
      } else {
         choices=PanelUtilities.toXMLElementViewList(pc,((XMLComplexChoice)myOwner).getChoices(),true);
         XMLElement choosen=((XMLComplexChoice)myOwner).getChoosen();
         if (choosen instanceof XMLComplexElement) { 
            moChoosen=new XMLElementView(pc,choosen, XMLElementView.TONAME );
         } else {
            moChoosen=new XMLElementView(pc,choosen, XMLElementView.TOVALUE );
         }
      }
      XMLPanel bgPanel=new XMLBasicPanel(pc,myOwner,"",isVertical,false,false);

      int noOfElements=choices.size();

      if (noOfElements>2) {
         int half=noOfElements/2;
         if (half!=noOfElements/2) half++;
         if (half>1) {
            bgPanel.setLayout(new GridLayout(half,half));
         }
         else {
            int m,n;
            if (isVertical) {
               m=2*half; n=1;
            }
            else {
               m=1; n=2*half;
            }
            bgPanel.setLayout(new GridLayout(m,n));
         }
      }

      rGroup = new ButtonGroup();
      final XMLPanel p=this;
      
      for (int i=0; i<choices.size(); i++) {
         JRadioButton jr=new JRadioButton(choices.get(i).toString());
         if (choices.get(i).equals(moChoosen)) {
            jr.setSelected(true);
         }
         jr.setEnabled( isEnabled );
         jr.addActionListener(new ActionListener () {
          public void actionPerformed(ActionEvent evt){
             if (getPanelContainer()==null) return;
             getPanelContainer().panelChanged(p, evt);
          }            
         });
         // Group the radio buttons.
         rGroup.add(jr);
         bgPanel.add(jr);
      }
            
      if (rightAllignment) {
         add(Box.createHorizontalGlue());
      }
      add(bgPanel);
      if (!rightAllignment) {
         add(Box.createHorizontalGlue());
      }
   }

   
   public void setElements () {
      if (!getOwner().isReadOnly()) {
         if (myOwner instanceof XMLAttribute) {
             myOwner.setValue(getSelectedItem().toString());
         } else {
            ((XMLComplexChoice)getOwner()).setChoosen((XMLElement)getSelectedItem());

         }
      }
   }

   public Object getSelectedItem() {
      Enumeration e=rGroup.getElements();
      int i=0;
      List choices=null;
      if (getOwner() instanceof XMLAttribute) {
         choices=((XMLAttribute)getOwner()).getChoices();
      } else {
         choices=((XMLComplexChoice)getOwner()).getChoices();
      }
      while (e.hasMoreElements()) {
         JRadioButton jr=(JRadioButton)e.nextElement();
         if (jr.isSelected()) {
            return choices.get(i);
         }
         i++;
      }
      return null;
   }

   public ButtonGroup getButtonGroup () {
      return rGroup;
   }

   public Object getValue () {
      return getSelectedItem();
   }
   
}
