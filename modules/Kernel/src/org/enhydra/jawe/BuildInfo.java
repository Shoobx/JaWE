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

import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class BuildInfo {

   private static String version="2.0";
   private static String release="1";
   private static long buildNo = 1138194850828L;
   private static String jreSuffix = "";

   static {
      try {
         ResourceBundle rb = ResourceBundle.getBundle("org.enhydra.jawe.buildinfo");
         version = rb.getString("version");
         release = rb.getString("release");
         jreSuffix = rb.getString("jresuff");
         String bn = rb.getString("buildno");
         SimpleDateFormat a = new SimpleDateFormat("yy/MM/dd HH:mm:ss");      
         buildNo = a.parse(bn).getTime();
      } catch (Exception e) {
         e.printStackTrace();
      }
      //System.err.println("version:"+version);
   }

   public static long getBuildNo() {
      return buildNo;
   }

   public static String getVersion () {
      return version;
   }
   
   public static String getRelease () {
      return release;
   }

   public static String getJRESuffix () {
      return jreSuffix;
   }
   
}