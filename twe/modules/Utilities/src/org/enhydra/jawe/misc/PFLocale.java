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

package org.enhydra.jawe.misc;

import java.util.Locale;

import org.enhydra.jawe.ResourceManager;

/**
 * Represents a locale that will be shown within the list. Contains a factory
 * method that creates instance of the class by parsing the locale string that
 * could be made of language, country and variant part.
 * This class is introduced mainly because of presenting the default locale by
 * "Default" key.
 */
public class PFLocale {
   private Locale loc;

   /**
   * Parses given string representing property file extendion. The given string
   * could be made of language, country and variant part.
   */
   public static PFLocale createPFLocale (String pfExt) {
      PFLocale pel=null;
      try {
         pfExt=pfExt.trim();
         if (pfExt.equals("default")) {
            pel=new PFLocale();
         } else {
            boolean hasCountry=pfExt.length()>2 && pfExt.substring(2,3).equals("_");
            if (hasCountry) {
               boolean hasVariant=pfExt.length()>5 && pfExt.substring(5,6).equals("_");
               if (hasVariant) {
                  pel=new PFLocale(pfExt.substring(0,2),
                        pfExt.substring(3,5),
                        pfExt.substring(6,pfExt.length()));
               } else {
                  if (pfExt.length()==5) {
                     pel=new PFLocale(pfExt.substring(0,2),pfExt.substring(3,5));
                  }
               }
            } else {
               if (pfExt.length()==2) {
                  pel=new PFLocale(pfExt);
               }
            }
         }
      } catch (Exception ex) {}

      return pel;
   }

   public PFLocale () {
      loc=ResourceManager.getDefaultLocale();
   }

   public PFLocale (Locale l) {
      loc=l;
   }

   public PFLocale (String lang) {
      loc=new Locale(lang);
   }

   public PFLocale (String lang,String country) {
      loc=new Locale(lang,country);
   }

   public PFLocale (String lang,String country,String variant) {
      loc=new Locale(lang,country,variant);
   }

   public String toString () {
      if (loc!=ResourceManager.getDefaultLocale()) {
         return loc.getDisplayName(ResourceManager.getChoosenLocale());
      }
      
      return ResourceManager.getLanguageDependentString("EnglishDefaultKey");
   }

   public Locale getLocale () {
     return loc;
   }

   public String getLanguage () {
      return loc.getLanguage();
   }

   public String getCountry () {
      return loc.getCountry();
   }

   public String getVariant () {
      return loc.getVariant();
   }

   public String getLocaleString () {
      String locStr="";
      if (loc==ResourceManager.getDefaultLocale()) {
         return "default";
      }
      
      locStr=loc.getLanguage();
      if (loc.getCountry().length()>0){
         locStr+="_"+loc.getCountry();
         if (loc.getVariant().length()>0) {
            locStr+="_"+loc.getVariant();
         }
      }
      
      return locStr;
   }

   public boolean equals(Object pfLoc) {
      if (pfLoc instanceof PFLocale) {
         return loc.equals(((PFLocale)pfLoc).loc);
      }
      
      return false;
   }

}

