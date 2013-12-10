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
