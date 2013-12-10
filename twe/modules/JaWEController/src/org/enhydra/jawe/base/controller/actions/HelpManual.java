package org.enhydra.jawe.base.controller.actions;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.help.HelpBroker;
import javax.help.HelpSet;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.misc.PFLocale;

/**
 * Class that realizes <B>help manual</B> action.
 */
public class HelpManual extends ActionBase {

   private HelpSet hs = null;
   private HelpBroker hb = null;

   public HelpManual (JaWEComponent jawecomponent) {
      super(jawecomponent);
      createHelp();
   }

   public void enableDisableAction() {      
   }
   
   public void actionPerformed(ActionEvent e) {
      if (hb!=null) {
         hb.setDisplayed(true);
      }

   }

   public void createHelp() {
      String defHelpSetName="jhelpset.hs";
      PFLocale pfl=new PFLocale(ResourceManager.getChoosenLocale());
      String helpSetName="jhelpset_"+pfl.getLocaleString()+".hs";
      ClassLoader loader = getClass().getClassLoader();

      try {
         URL url = HelpSet.findHelpSet(loader,helpSetName);
         if (url==null) {
            url=HelpSet.findHelpSet(loader,defHelpSetName);
         }
         if (url != null) {
            hs = new HelpSet(loader, url);
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      }

      if (hs!=null) {
         hb = hs.createHelpBroker();
      }
      setEnabled(hs!=null && hb!=null);
   }

}
