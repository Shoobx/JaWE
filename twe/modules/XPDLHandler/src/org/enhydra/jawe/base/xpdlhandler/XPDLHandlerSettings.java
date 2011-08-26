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
 * Miroslav Popov, Dec 8, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.base.xpdlhandler;

import java.util.Properties;

import org.enhydra.jawe.AdditionalResourceManager;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.JaWEManager;

/**
 * @author Miroslav Popov
 *
 */
public class XPDLHandlerSettings extends JaWEComponentSettings {

   public void init(JaWEComponent comp) {
      PROPERTYFILE_PATH = JaWEManager.TOGWE_BASIC_PROPERTYFILE_PATH;
      PROPERTYFILE_NAME = JaWEManager.TOGWE_BASIC_PROPERTYFILE_NAME;
      super.init(comp);
   }

   public void loadDefault (JaWEComponent comp,Properties properties) {
      // defaults
      arm = new AdditionalResourceManager(properties);

      componentSettings.put("FileLocking", new Boolean(properties.getProperty("FileLocking","false").equals("true")));
   }
   
   public boolean isFileLockingEnabled() {
      return ((Boolean) componentSettings.get("FileLocking")).booleanValue();
   }
   
}
