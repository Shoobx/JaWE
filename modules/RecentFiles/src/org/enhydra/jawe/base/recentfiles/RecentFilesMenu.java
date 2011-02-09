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

/**
 * Miroslav Popov, Aug 5, 2005
 */
package org.enhydra.jawe.base.recentfiles;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.enhydra.jawe.BarFactory;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.jawe.JaWEConstants;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jxpdl.XMLUtil;

/**
 * @author Miroslav Popov
 *
 */
public class RecentFilesMenu extends JMenu implements JaWEComponentView, ActionListener {

   public static final String RFL_FILENAME = "/.rfl";
   
   public void configure () {      
   }

   protected RecentFilesManager controller;
   public RecentFilesMenu (RecentFilesManager controller) {    
      this.controller=controller;
   }

   public void init () {      
      setText(controller.getSettings().getLanguageDependentString(controller.getName() + BarFactory.LABEL_POSTFIX));
      String rfl = XMLUtil.fileToString(JaWEConstants.JAWE_USER_HOME + RFL_FILENAME);
      if (rfl != null) {
         for (StringTokenizer st = new StringTokenizer(rfl, "\n"); st.hasMoreTokens();) {
            addToRecentFiles( st.nextToken());
         }
      }
   }

   public void addToRecentFiles(String filename) {
      if (filename==null) return;
      JMenuItem mItem;
      for (int i = 0; i < getItemCount(); ++i) {
         mItem = (JMenuItem) getMenuComponent(i);
         if (filename.equals(mItem.getText().substring(2))) {
            remove(i);
         }
      }
      
      int recentFileListSize = 10;
      if (getItemCount() == recentFileListSize) {
         remove(recentFileListSize - 1);
      }
      
      mItem = new JMenuItem("1 " + filename);
      mItem.addActionListener(this);         
      
      insert(mItem, 0);
      // changing mnemonics to correspond to the ordinal number of items
      for (int i = 0; i < getItemCount(); ++i) {
         mItem = (JMenuItem) getMenuComponent(i);
         String oldText = mItem.getText();
         String ordNo = String.valueOf(i + 1);
         String mnemonic = ordNo.substring(ordNo.length() - 1, ordNo.length());
         mItem.setText(mnemonic + " " + oldText.substring(2));
         BarFactory.setMnemonic(mItem, mnemonic);
      }   
   }

   public void saveRecentFiles() {
      try {
         String fileList = "";
         for (int i = getItemCount(); i > 0;) {
            JMenuItem mItem = (JMenuItem) getMenuComponent(--i);
            fileList += mItem.getText().substring(2);
            if (i > 0) fileList += "\n";
         }
         FileOutputStream fos = new FileOutputStream(JaWEConstants.JAWE_USER_HOME + RFL_FILENAME);
         fos.write(fileList.getBytes(JaWEManager.getInstance().getJaWEController().getControllerSettings().getEncoding()));
         // Write to file
         fos.flush();
         fos.close();
      } catch (Exception ex) {
      }
   }
   
   public void actionPerformed(ActionEvent ae) {
      String filename = ae.getActionCommand().substring(2);
      if (JaWEManager.getInstance().getJaWEController().tryToClosePackage(null, false)) {
         JaWEManager.getInstance().getJaWEController().openPackageFromFile(filename);
      }
   }

   public JaWEComponent getJaWEComponent () {
      return controller;
   }

   public JComponent getDisplay () {
      return this;
   }
}