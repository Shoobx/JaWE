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

package org.enhydra.jawe.components.reconfiguration;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;

import org.enhydra.jawe.BarFactory;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.Utils;

public class ReconfiguratorMenu extends JMenu implements
                                             JaWEComponentView,
                                             ActionListener {

   protected ReconfiguratorManager controller;

   public void configure() {
   }

   public ReconfiguratorMenu(ReconfiguratorManager controller) {
      this.controller = controller;
   }

   public void init() {
      setText(controller.getSettings()
         .getLanguageDependentString(controller.getName() + BarFactory.LABEL_POSTFIX));

      Map configInfo = JaWEManager.getInstance().getJaWEController().getConfigInfo();

      JRadioButtonMenuItem mItem;
      ButtonGroup bg = new ButtonGroup();

      String ccfg=JaWEManager.getInstance().getJaWEController().getCurrentConfig();
      Iterator it=configInfo.entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry me=(Map.Entry)it.next();         
         mItem = new JRadioButtonMenuItem((String)me.getValue());
         if (me.getKey().equals(ccfg)) {
            mItem.setSelected(true);
         }
         mItem.addActionListener(this);
         bg.add(mItem);
         add(mItem);
      }

   }

   public void actionPerformed(ActionEvent ae) {
      JMenuItem mi = (JMenuItem) ae.getSource();
      boolean reconf = Utils.reconfigure(JaWEManager.getInstance().getJaWEController().getConfigId(mi.getText()));
      if (!reconf) {
         JaWEManager.getInstance()
            .getJaWEController()
            .message(controller.getSettings()
                        .getLanguageDependentString("ErrorErrorWhileReconfiguring"),
                     JOptionPane.ERROR_MESSAGE);
      }
   }

   public JaWEComponent getJaWEComponent() {
      return controller;
   }

   public JComponent getDisplay() {
      return this;
   }
   
}
