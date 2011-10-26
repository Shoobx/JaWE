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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Utility for implementing multi language support.
 * 
 * @author Sasa Bojanic
 */
public class AdditionalResourceManager {
   public static final String ADDITIONAL_LANGUAGE_PROPERTY_FILE = "AdditionalLanguagePropertyFile.";

   protected List propertyFiles = new ArrayList();

   public AdditionalResourceManager(Properties props) {
      List apfs = ResourceManager.getResourceStrings(props,
                                                     ADDITIONAL_LANGUAGE_PROPERTY_FILE);
      if (apfs != null) {
         for (int i = 0; i < apfs.size(); i++) {
            String pf = (String) apfs.get(i);
            try {
               ResourceBundle bundle = ResourceBundle.getBundle(pf,
                                                                ResourceManager.getChoosenLocale());
               propertyFiles.add(bundle);
            } catch (Exception mre) {
               Properties eps = new Properties();
               try {
                  Utils.manageProperties(eps, getCurrentConfigFolder(), pf);
               } catch (Exception ex) {
               }
               String locStrOrig = pf.substring(0, pf.indexOf(".properties"));
               Locale loc = ResourceManager.getChoosenLocale();
               if (loc.getLanguage().length() > 0) {
                  try {
                     Utils.manageProperties(eps,
                                            getCurrentConfigFolder(),
                                            locStrOrig
                                                  + "_" + loc.getLanguage()
                                                  + ".properties");
                  } catch (Exception ex) {
                  }
                  if (loc.getCountry().length() > 0) {
                     try {
                        Utils.manageProperties(eps,
                                               getCurrentConfigFolder(),
                                               locStrOrig
                                                     + "_" + loc.getLanguage() + "_"
                                                     + loc.getCountry() + ".properties");
                     } catch (Exception ex) {
                     }
                     if (loc.getVariant().length() > 0) {
                        try {
                           Utils.manageProperties(eps,
                                                  getCurrentConfigFolder(),
                                                  locStrOrig
                                                        + "_" + loc.getLanguage() + "_"
                                                        + loc.getCountry() + "_"
                                                        + loc.getVariant()
                                                        + ".properties");
                        } catch (Exception ex) {
                        }
                     }
                  }
               }
               propertyFiles.add(eps);
            }

         }
      }

   }

   public String getLanguageDependentString(String nm) {
      String str = null;
      Iterator it = propertyFiles.iterator();
      while (it.hasNext()) {
         Object o = it.next();
         if (o instanceof ResourceBundle) {
            try {
               ResourceBundle bundle = (ResourceBundle)o;
               str = bundle.getString(nm);
               if (str != null)
                  break;
            } catch (Exception mre) {
            }
         } else {
            Properties eps=(Properties)o;
            str=eps.getProperty(nm);
         }
      }
      return str;
   }

   protected String getCurrentConfigFolder() {
      String currentConfig = JaWEConstants.JAWE_USER_HOME;
      String cch = System.getProperty(JaWEConstants.JAWE_CURRENT_CONFIG_HOME);
      if (cch != null) {
         currentConfig = cch;
      }
      return currentConfig;
   }

}
