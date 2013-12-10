package org.enhydra.jawe;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

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
            if (Utils.checkFileExistence(JaWEManager.TOGWE_BASIC_PROPERTYFILE_NAME) || Utils.checkResourceExistence(JaWEManager.TOGWE_BASIC_PROPERTYFILE_PATH, JaWEManager.TOGWE_BASIC_PROPERTYFILE_NAME)) {
               cfgf = new File(conf_home + "/" + JaWEManager.TOGWE_BASIC_PROPERTYFILE_NAME);               
            } else {
               cfgf = new File(conf_home + "/" + JaWEConstants.JAWE_BASIC_PROPERTYFILE_NAME);
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

   }

}
