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

package org.enhydra.jawe;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.WString;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;

/**
 * The main editor class.
 * 
 * @author Sasa Bojanic
 */
public class JaWE {

   static int timeLeft = 10; // in seconds

   public static void main(String[] args) throws Throwable {
      System.out.println("Starting JAWE ....");
      System.out.println("JaWE -> JaWE is being initialized ...");

      File cfgf = null;
      System.out.println("JaWE_CONF_HOME=" + JaWEConstants.JAWE_CONF_HOME);
      if (JaWEConstants.JAWE_CONF_HOME != null) {
         File mainConfig = new File(JaWEConstants.JAWE_CONF_HOME + "/" + "defaultconfig");
         Properties props = new Properties();
         if (mainConfig.exists()) {
            FileInputStream fis = null;
            try {
               fis = new FileInputStream(mainConfig);
               props.load(fis);
               fis.close();
            } catch (Exception ex) {
               System.err.println("Something went wrong while reading configuration from the file "
                                  + mainConfig.getAbsolutePath());
            }
         }
         String conf_home = JaWEConstants.JAWE_CONF_HOME
                            + "/"
                            + props.getProperty(JaWEConstants.JAWE_CURRENT_CONFIG_HOME);
         File cfh = new File(conf_home);
         if (cfh.exists()) {
            System.setProperty(JaWEConstants.JAWE_CURRENT_CONFIG_HOME, conf_home);
            if (Utils.checkFileExistence(JaWEManager.TOGWE_BASIC_PROPERTYFILE_NAME)
                || Utils.checkResourceExistence(JaWEManager.TOGWE_BASIC_PROPERTYFILE_PATH,
                                                JaWEManager.TOGWE_BASIC_PROPERTYFILE_NAME)) {
               cfgf = new File(conf_home
                               + "/" + JaWEManager.TOGWE_BASIC_PROPERTYFILE_NAME);
            } else {
               cfgf = new File(conf_home
                               + "/" + JaWEConstants.JAWE_BASIC_PROPERTYFILE_NAME);
            }
         }
      }
      if (cfgf != null && cfgf.exists()) {
         JaWEManager.configure(cfgf);
      } else {
         JaWEManager.configure();
      }

      // Starting file name
      String fn = null;

      // check if there is a file that should be open at the startup
      if (args != null && args.length > 0) {
         fn = args[0];
      }

      JaWEManager.getInstance().start(fn);

      try {
         setAppUserModelID();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Set AppUserModelID for application
    * 
    * @throws Exception
    */
   private static void setAppUserModelID() throws Exception {
      // Setting AppUserModelID
      String OS = System.getProperty("os.name");
      boolean isWindows = OS.startsWith("Windows");
      if (isWindows) {
         // AppUsermodelID_Start
         final Map<String, Object> WIN32API_OPTIONS = new HashMap<String, Object>() {
            {
               put(Library.OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
               put(Library.OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
            }
         };
         Shell32 shell32 = (Shell32) Native.loadLibrary("shell32",
                                                        Shell32.class,
                                                        WIN32API_OPTIONS);
         WString wAppId = new WString("Together.Workflow.Editor");
         shell32.SetCurrentProcessExplicitAppUserModelID(wAppId);
         // AppUsermodelID_End
      }
   }

}
