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

package org.enhydra.jawe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

public class JaWESplash extends JWindow {

   public JaWESplash() {
      URL imageURL = null;
      String image = JaWEManager.getSplashScreenImage();
      if (image != null && !"".equals(image)) {
         imageURL = JaWEManager.class.getClassLoader().getResource(image);
      }

      JLabel l = null;
      if(imageURL!=null) {
         l=new JLabel(new ImageIcon(imageURL));
      } else {
         l=new JLabel("");
      }
      getContentPane().add(l, BorderLayout.CENTER);

      Utils.center(this, 100, 200);

      addMouseListener(new MouseAdapter() {
         public void mousePressed(MouseEvent e) {
            hideSplash();
         }
      });
      setVisible(true);
   }

   public void hideSplash () {
      if (isVisible()) {
         setVisible(false);
         dispose();
      }
   }
   
   public static JPanel getSplashPanel() {
      Hyperactive ha = new Hyperactive();
      
      JPanel p = new JPanel();
      p.setLayout(new BorderLayout());      
      p.setBackground(Color.WHITE);
      
      URL imageURL = null;

      // logo
      String image = JaWEManager.getSplashScreenImage();
      if (image != null && !"".equals(image)) {
         imageURL = JaWEManager.class.getClassLoader().getResource(image);
      }
      JLabel logo = null;
      if (imageURL!=null) {
         logo=new JLabel(new ImageIcon(imageURL), SwingConstants.CENTER);
      } else {
         logo=new JLabel("", SwingConstants.CENTER);
      }
         
      JEditorPane text = new JEditorPane();
      text.setAlignmentX(CENTER_ALIGNMENT);
      text.setAlignmentY(TOP_ALIGNMENT);
      text.addHyperlinkListener(ha);
      text.setContentType("text/html");
      text.setOpaque(false);
      String t = "<html><p align=\"center\"><b>Version:  " + JaWEManager.getVersion() + "-"+JaWEManager.getRelease()+"</b>"
            + "<br>Build Id: " /*+ JaWEManager.getBuildEdition()+ JaWEManager.getBuildEditionSuffix() + "-" */ + JaWEManager.getBuildNo() + "<br><br>" 
            + JaWEManager.getAboutMsg();   

      text.setText(t);
      text.setEditable(false);      
      
      p.add(logo, BorderLayout.NORTH);
      p.add(text, BorderLayout.CENTER);

      return p;
   }
}

