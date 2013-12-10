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
      
      cboPanel=new WfXMLComboPanel(controller,ld);
      tablePanel=new WfXMLTablePanel(controller);

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

