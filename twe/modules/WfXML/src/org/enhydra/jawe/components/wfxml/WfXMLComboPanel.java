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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.enhydra.jawe.JaWEConstants;
import org.enhydra.jawe.base.panel.panels.XMLBasicPanel;

/**
 * Creates panel with JLabel and JComboBox.
 */
public class WfXMLComboPanel extends XMLBasicPanel {

   private static final int MAX_URL_HISTORY_SIZE=16;
   public static final String RURL_FILENAME = "/.rurls";

   private static String URL_HISTORY_FILE_PATH;
   private static ArrayList urls;

   protected JComboBox jcb;
   protected JButton jb;
   protected JLabel jl;
   
   protected Dimension textDim=new Dimension(250,20);
   private static Dimension refButDimension=new Dimension(25,20);
   
   protected WfXML wfxml;
   
   public WfXMLComboPanel (WfXML wfxml,ActionListener al) {

      super(null,null,"",false,false,true);
      this.wfxml=wfxml;
      
      jl=new JLabel(wfxml.getSettings().getLanguageDependentString("RegistryServiceURL")+": ");
      jl.setAlignmentX(Component.LEFT_ALIGNMENT);
      jl.setAlignmentY(Component.BOTTOM_ALIGNMENT);
      jl.setHorizontalAlignment(SwingConstants.LEFT);

      

      if (URL_HISTORY_FILE_PATH==null) {
         URL_HISTORY_FILE_PATH=JaWEConstants.JAWE_USER_HOME + RURL_FILENAME;
         File extR=new File(URL_HISTORY_FILE_PATH);
         if (extR.exists()) {
            urls=(ArrayList)readFile(URL_HISTORY_FILE_PATH);
         }
         if (urls==null) {
            urls=new ArrayList();
         }
      }
      
      jcb=new JComboBox(new Vector(urls));
      
      if (urls.size()>0) {
         jcb.setSelectedIndex(0);
      }
      jcb.setEditable(true);
      jcb.setAlignmentX(Component.LEFT_ALIGNMENT);
      jcb.setAlignmentY(Component.BOTTOM_ALIGNMENT);

      add(jl);
      add(Box.createHorizontalGlue());
      add(jcb);

      
      jb = new JButton(wfxml.getWfXMLSettings().getConnectImage());      
      if (jb!=null) {
         jb.setBorderPainted(false);
         jb.setAlignmentX(Component.LEFT_ALIGNMENT);
         jb.setAlignmentY(Component.BOTTOM_ALIGNMENT);
         jb.setMinimumSize(new Dimension(refButDimension));
         jb.setMaximumSize(new Dimension(refButDimension));
         jb.setPreferredSize(new Dimension(refButDimension));
         jb.setRolloverEnabled(true);         
         jb.setContentAreaFilled(false);
         jb.setToolTipText(wfxml.getSettings().getLanguageDependentString("ConnectKey"));
         
         jb.addActionListener(al);
         jb.addActionListener(new ActionListener() {
            public void actionPerformed( ActionEvent ae ){
               String selected=(String)jcb.getSelectedItem();
               if (selected!=null) {
                  addItemToCombo(selected);                  
               }
            }            
         });
         add(jb);      
      }
      

   }

   public String getSelectedItem() {
      try {
         Object el = jcb.getEditor().getItem();
         if (el != null) {
            return el.toString();
         }
         return "";
      } catch (Exception ex) {
         ex.printStackTrace();
         return "";
      }
   }

   public Object getValue () {
      return getSelectedItem();
   }
   
   public void cleanup () {
      jcb.removeAllItems();
      urls.clear();
      writeFile(urls,URL_HISTORY_FILE_PATH);      
   }   

   
   public JComboBox getComboBox () {
      return jcb;
   }
   
   /**
    * Adds a text into combo box if it hasn't been there previously.
    */
   protected void addItemToCombo (String url) {
      if (url==null || url.trim().length()==0) return;
      for (int i=0; i<jcb.getItemCount(); i++) {
         if (jcb.getItemAt(i).toString().equals(url)) {
            jcb.removeItemAt(i);
            jcb.insertItemAt(url,0);
            jcb.setSelectedIndex(0);
            urls.remove(i);
            urls.add(0,url);
            writeFile(urls,URL_HISTORY_FILE_PATH);
            wfxml.getSettings().adjustActions();
            return;
         }
      }
      jcb.insertItemAt(url,0);
      urls.add(0,url);
      if (jcb.getItemCount()>MAX_URL_HISTORY_SIZE) {
         jcb.removeItemAt(MAX_URL_HISTORY_SIZE);
         urls.remove(MAX_URL_HISTORY_SIZE);
      }
      jcb.setSelectedIndex(0);
      writeFile(urls,URL_HISTORY_FILE_PATH);
      wfxml.getSettings().adjustActions();
   }

   private void writeFile (Object obj,String fName) {
      try {
         OutputStream fos = new FileOutputStream(fName);
         ObjectOutputStream oout = new ObjectOutputStream(fos);
         oout.writeObject(obj);
         oout.flush();
         oout.close();
         fos.close();
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   private Object readFile (String fName) {
      try {
         InputStream fis = new FileInputStream(fName);
         ObjectInputStream oin = new ObjectInputStream(fis);
         Object obj=oin.readObject();
         oin.close();
         return obj;
      } catch (Exception ex) {
         ex.printStackTrace();
         return null;
      }
   }


}

