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

package org.enhydra.jawe.components.wfxml;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.enhydra.jawe.BarFactory;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.jawe.components.wfxml.actions.ListDefinitions;


/**
 * Creates a panel for WfXML.
 */
public class WfXMLPanel extends JPanel implements JaWEComponentView {

   protected JToolBar toolbar;
   protected JPanel innerPanel=new JPanel();
   protected WfXMLTablePanel tablePanel;
   protected JScrollPane scrollPane;
   protected WfXMLComboPanel cboPanel;
   protected DefInfos defInfos=new DefInfos();
   protected WfXML controller;
   
   protected ListDefinitions ld;
   
   public WfXMLPanel (WfXML controller) {
      this.controller=controller;
   }

   public void configure() {      
      ld=new ListDefinitions(controller);      
      init();
   }
   
   public void init () {
      setBorder(BorderFactory.createEtchedBorder());
      setLayout(new BorderLayout());
      toolbar = BarFactory.createToolbar("defaultToolbar", controller);   
      toolbar.setFloatable(false); 
      if (toolbar.getComponentCount() > 0) {
         add(toolbar,BorderLayout.NORTH);
      }
      
      cboPanel=new WfXMLComboPanel(controller,ld, null);
      tablePanel=new WfXMLTablePanel(controller, null);

      innerPanel.setLayout(new BorderLayout());

      innerPanel.add(cboPanel,BorderLayout.NORTH);
      innerPanel.add(tablePanel,BorderLayout.CENTER);

      add(innerPanel,BorderLayout.CENTER);      
   }
   
   protected boolean hasConnection () {
      return !cboPanel.getSelectedItem().equals("");
   }
   
   public WfXMLComboPanel getComboPanel () {
      return cboPanel;
   }

   public void cleanTable () {
      tablePanel.cleanup();
   }

   public DefInfo getSelectedDefInfo() {
      return tablePanel.getSelectedElement();
   }


   public String getSelectedConnection () {
      String url=cboPanel.getSelectedItem();
      url=url.replaceAll("&", "&amp;");
      return url;
   }

   public JaWEComponent getJaWEComponent() {
      return controller;
   }

   public JComponent getDisplay() {
      return this;
   }

   
}

