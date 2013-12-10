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

/**
 * Miroslav Popov, Dec 8, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.base.transitionhandler;

import java.util.List;
import java.util.Properties;

import org.enhydra.jawe.AdditionalResourceManager;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.ResourceManager;

/**
 * @author Miroslav Popov
 *
 */
public class TransitionHandlerSettings extends JaWEComponentSettings {

   public void init(JaWEComponent comp) {
      PROPERTYFILE_PATH = "org/enhydra/jawe/base/transitionhandler/properties/";
      PROPERTYFILE_NAME = "transitionhandler.properties";
      super.init(comp);
   }

   public void loadDefault (JaWEComponent comp,Properties properties) {
      // defaults
      arm = new AdditionalResourceManager(properties);

      componentSettings.put("ACTIVITY_BLOCKCanHaveMoreOutgoingTranstion", new Boolean(true));
      componentSettings.put("ACTIVITY_BLOCKCanHaveMoreIncomingTranstion", new Boolean(true));
      componentSettings.put("ACTIVITY_NOCanHaveMoreOutgoingTranstion", new Boolean(true));
      componentSettings.put("ACTIVITY_NOCanHaveMoreIncomingTranstion", new Boolean(true));
      componentSettings.put("ACTIVITY_ROUTECanHaveMoreOutgoingTranstion", new Boolean(true));
      componentSettings.put("ACTIVITY_ROUTECanHaveMoreIncomingTranstion", new Boolean(true));
      componentSettings.put("ACTIVITY_SUBFLOWCanHaveMoreOutgoingTranstion", new Boolean(true));
      componentSettings.put("ACTIVITY_SUBFLOWCanHaveMoreIncomingTranstion", new Boolean(true));
      componentSettings.put("ACTIVITY_TOOLCanHaveMoreOutgoingTranstion", new Boolean(true));
      componentSettings.put("ACTIVITY_TOOLCanHaveMoreIncomingTranstion", new Boolean(true));

      List out = ResourceManager.getResourceStrings(properties, "Transitions.moreOutgoing.Activity.Type.", false);
      for (int i = 0; i < out.size(); i++) {
         componentSettings.put(out.get(i) + "CanHaveMoreOutgoingTranstion", new Boolean(ResourceManager.getResourceString(properties, "Transitions.moreOutgoing.Activity.Type." + out.get(i))));
      }

      List in = ResourceManager.getResourceStrings(properties, "Transitions.moreIncoming.Activity.Type.", false);
      for (int i = 0; i < in.size(); i++) {
         componentSettings.put(in.get(i) + "CanHaveMoreIncomingTranstion", new Boolean(ResourceManager.getResourceString(properties, "Transitions.moreIncoming.Activity.Type." + in.get(i))));
      }
   }
   
   public boolean canHaveMoreOutgoingTransition(String type) {
      try {
         return ((Boolean) componentSettings.get(type + "CanHaveMoreOutgoingTranstion")).booleanValue();
      } catch (Exception ex) {
         return true;
      }
   }
   
   public boolean canHaveMoreIncomingTransition(String type) {
      try {
         return ((Boolean) componentSettings.get(type + "CanHaveMoreIncomingTranstion")).booleanValue();
      } catch (Exception ex) {
         return true;
      }
   }
}
