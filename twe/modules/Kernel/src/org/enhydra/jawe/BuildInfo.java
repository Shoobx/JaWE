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

package org.enhydra.jawe;

import java.util.ResourceBundle;

/**
 * Structure that holds information about the project version, release and build number.
 * This information is retrieved from buildinfo.properties file which is automatically
 * updated during the project build, and packed into the JAR file to be accessible to this
 * class in a run-time.
 */
public class BuildInfo {

   /** Holds the version information. */
   private static String version = "2.0";

   /** Holds release information. */
   private static String release = "1";

   /** Holds build number information. */
   private static String buildNo = "20100901-1513";

   static {
      try {
         ResourceBundle rb = ResourceBundle.getBundle("org.enhydra.jawe.buildinfo");
         version = rb.getString("version");
         release = rb.getString("release");
         buildNo = rb.getString("buildno");
      } catch (Exception e) {
         e.printStackTrace();
      }
      // System.err.println("version:"+version);
   }

   /**
    * Returns string representing build number information.
    * @return String representing build number information.
    */
   public static String getBuildNo() {
      return buildNo;
   }

   /** Returns string representing version information.
    * @return String representing version information.
    */
   public static String getVersion() {
      return version;
   }

   /** Returns string representing release information.
    * @return String representing release information.
    */
   public static String getRelease() {
      return release;
   }

}
