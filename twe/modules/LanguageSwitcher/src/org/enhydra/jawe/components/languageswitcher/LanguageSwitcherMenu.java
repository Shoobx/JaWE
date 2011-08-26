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
 * Miroslav Popov, Apr 5, 2006 miroslav.popov@gmail.com
 */
package org.enhydra.jawe.components.languageswitcher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Locale;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;

import org.enhydra.jawe.BarFactory;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.jawe.JaWEConstants;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.misc.PFLocale;

/**
 * @author Miroslav Popov
 */
public class LanguageSwitcherMenu extends JMenu implements
                                               JaWEComponentView,
                                               ActionListener {

   public void configure() {
   }

   protected LanguageSwitcherManager controller;

   public LanguageSwitcherMenu(LanguageSwitcherManager controller) {
      this.controller = controller;
   }

   public void init() {
      setText(controller.getSettings()
         .getLanguageDependentString(controller.getName() + BarFactory.LABEL_POSTFIX));

      List l = Utils.findPropertyFiles();

      SMItem mItem;
      ButtonGroup bg=new ButtonGroup();
      for (int i = l.size() - 1; i >= 0; i--) {
         PFLocale pfl=(PFLocale)l.get(i);
         mItem = new SMItem(pfl.toString(),
                            pfl);
         if (pfl.getLocale().equals(ResourceManager.getChoosenLocale())) {
            mItem.setSelected(true);
         }
         mItem.addActionListener(this);
         bg.add(mItem);
         insert(mItem, 0);
      }

      // changing mnemonics to correspond to the ordinal number of items
//      for (int i = 0; i < getItemCount(); ++i) {
//         mItem = (SMItem) getMenuComponent(i);
//         String oldText = mItem.getText();
//         String ordNo = String.valueOf(i + 1);
//         String mnemonic = ordNo.substring(ordNo.length() - 1, ordNo.length());
//         mItem.setText(mnemonic + " " + oldText.substring(2));
//         BarFactory.setMnemonic(mItem, mnemonic);
//      }
   }

   public void actionPerformed(ActionEvent ae) {            
      File file = new File(System.getProperty(JaWEConstants.JAWE_CURRENT_CONFIG_HOME)
                           + "/"+JaWEManager.TOGWE_BASIC_PROPERTYFILE_NAME);
      
      if (!file.exists()) {
         file=new File(System.getProperty(JaWEConstants.JAWE_CURRENT_CONFIG_HOME)
                       + "/"+JaWEConstants.JAWE_BASIC_PROPERTYFILE_NAME);
      }

      if (!file.exists()) {
         return;
      }
      
      File newFile = new File(System.getProperty(JaWEConstants.JAWE_CURRENT_CONFIG_HOME) + "/temp");

      Locale loc = ((SMItem) ae.getSource()).getMyLocale().getLocale();

      try {
         newFile.createNewFile();

         BufferedReader reader = new BufferedReader(new FileReader(file));
         BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));

         String line = null;
         boolean done = false;
         while ((line = reader.readLine()) != null) {
            if (!done
                && (line.startsWith("#StartingLocale") || line.startsWith("StartingLocale"))) {
               if (loc.getLanguage() == "")
                  line = "StartingLocale = default";
               else
                  line = "StartingLocale = " + loc.getLanguage();
               if (!loc.getCountry().equals("")) {
                  line += "_" + loc.getCountry();
               }
               if (!loc.getVariant().equals("")) {
                  line += "_" + loc.getVariant();
               }

               done = true;
            }

            writer.write(line + "\n");
         }

         reader.close();
         writer.close();

         file.delete();
         newFile.renameTo(file);

         try {
            JaWEController jc=JaWEManager.getInstance().getJaWEController();
            Utils.reconfigure(jc.getConfigId(jc.getCurrentConfig()));
         } catch (Exception ex) {
            JaWEManager.getInstance()
            .getJaWEController()
            .message(controller.getSettings()
                        .getLanguageDependentString("InformationEffectAfterRestart"),
                     JOptionPane.INFORMATION_MESSAGE);            
         }
      } catch (Exception e) {
         JaWEManager.getInstance()
         .getJaWEController()
         .message(controller.getSettings()
                     .getLanguageDependentString("ErrorErrorWhileSaveFile"),
                  JOptionPane.ERROR_MESSAGE);
      }

      // ResourceManager.setChoosen(((SMItem) ae.getSource()).getMyLocale().getLocale());
   }

   public JaWEComponent getJaWEComponent() {
      return controller;
   }

   public JComponent getDisplay() {
      return this;
   }

   private class SMItem extends JRadioButtonMenuItem {

      PFLocale myLocale;

      public SMItem(String text, PFLocale loc) {
         super(text);

         myLocale = loc;
      }

      public void setMyLocale(PFLocale loc) {
         myLocale = loc;
      }

      public PFLocale getMyLocale() {
         return myLocale;
      }
   }
}
