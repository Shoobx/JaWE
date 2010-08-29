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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JScrollPane;

import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jawe.base.panel.PanelSettings;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.elements.DataTypes;

/**
* Creates a combo panel and a panel which type is determined by the
* choosen element from combo panel.
* @author Sasa Bojanic
* @author Zoran Milakovic
*/
public class XMLDataTypesPanel extends XMLBasicPanel {

   protected XMLPanel prevPanel=null;
   protected XMLPanel emptyPanel;

   protected XMLComboPanel pCombo;
   
   public XMLDataTypesPanel (
         PanelContainer pc,
         DataTypes myOwner,
         List choices,
         String title,
         boolean isEnabled) {

      super(pc,myOwner,title,true,false,true);

      PanelSettings ps=(PanelSettings)pc.getSettings();
      boolean hasBorder = ps.doesXMLDataTypesPanelHasBorder();

      Dimension dim=new Dimension(ps.getXMLDataTypesPanelWidth(), ps.getXMLDataTypesPanelHeight());
      
      final JScrollPane jsp=new JScrollPane();
      jsp.setAlignmentX(Component.LEFT_ALIGNMENT);
      jsp.setAlignmentY(Component.TOP_ALIGNMENT);


      pCombo=new XMLComboPanel(pc,myOwner,choices,false,false,false,false,isEnabled);
      add(pCombo);
      jsp.setPreferredSize(new Dimension(dim));
      jsp.setMinimumSize(new Dimension(dim));
      if (hasBorder) {
         jsp.setBorder(BorderFactory.createEtchedBorder());
      } else {
         jsp.setBorder(BorderFactory.createEmptyBorder());
      }
      add(jsp);

      emptyPanel=new XMLBasicPanel(pc,myOwner,"",true,false,false);
      jsp.setViewportView(emptyPanel);


      pCombo.getComboBox().addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            if (prevPanel!=null) {
//               prevPanel.setElements();
               emptyPanel.remove(prevPanel);
            }
            XMLElement choosen=(XMLElement)pCombo.getSelectedItem();
            if (choosen!=null) {
               prevPanel=getPanelContainer().getPanelGenerator().getPanel(choosen);
               emptyPanel.add(prevPanel);
            }
            jsp.paintAll(jsp.getGraphics());
         }
      });
      pCombo.getComboBox().setSelectedItem(new XMLElementView(pc,myOwner.getChoosen(),XMLElementView.TONAME));

      add(Box.createVerticalGlue());

   }

   public boolean validateEntry () {
      boolean isOK=pCombo.validateEntry();
      for (int i=0; i<emptyPanel.getComponentCount(); i++) {
         XMLPanel p=(XMLPanel)emptyPanel.getComponent(i);
         isOK=isOK && p.validateEntry();
      }

      return isOK;
   }


   public void setElements () {
      if (!getOwner().isReadOnly()) {
         pCombo.setElements();
         for (int i=0; i<emptyPanel.getComponentCount(); i++) {
            XMLPanel p=(XMLPanel)emptyPanel.getComponent(i);
            p.setElements();
         }
      }
   }

   public Object getValue () {
      return pCombo.getValue();
   }

   public void cleanup () {
      pCombo.cleanup();
      for (int i=0; i<emptyPanel.getComponentCount(); i++) {
         XMLPanel p=(XMLPanel)emptyPanel.getComponent(i);
         p.cleanup();
      }
   }
   
}
