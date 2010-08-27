package org.enhydra.jawe;

import java.util.Properties;



public interface PropertyMgr {

   Properties loadProperties(String path, String name);
   
   void manageProperties(JaWEComponent comp,JaWEComponentSettings settings,String path,String name);
      
}
