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

package org.enhydra.jawe.components.debug;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import org.enhydra.jawe.BarFactory;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentView;

/**
 *  Used to display debug information.
 *
 *  @author Sasa Bojanic
 */
public class DebugComponentPanel extends JPanel implements JaWEComponentView {

   public void configure () {      
   }

   protected DebugComponent controller;
   public DebugComponentPanel (DebugComponent controller) {
      this.controller=controller;
   }

   protected JTextArea jta;
   protected JButton clear;

   public int getTextSize() {
      return jta.getText().length();
   }
   
   public void appendText (String txt) {
      jta.append(txt);
   }

   public void clearText () {
      jta.setText("");
   }

   public void init () {
      setBorder(BorderFactory.createEtchedBorder());
      setLayout(new BorderLayout());



      JToolBar toolbar = BarFactory.createToolbar("defaultToolbar", controller);   
      toolbar.setFloatable(false); 
      if (toolbar.getComponentCount() > 0)
         add(toolbar, BorderLayout.NORTH);
      
      JScrollPane jsp=new JScrollPane();
      jsp.setAlignmentX(Component.LEFT_ALIGNMENT);
      jsp.setAlignmentY(Component.TOP_ALIGNMENT);

      jta=new JTextArea();      
      jta.setTabSize(4);
      jta.getCaret().setDot(0);
      jta.setLineWrap(true);
      jta.setWrapStyleWord(true);

      jta.setEnabled(true);
      jta.setEditable(false);

      jsp.setViewportView(jta);
      add(jsp,BorderLayout.CENTER);
   }

   public JaWEComponent getJaWEComponent () {
      return controller;
   }

   public JComponent getDisplay () {
      return this;
   }
}
