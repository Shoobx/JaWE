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
