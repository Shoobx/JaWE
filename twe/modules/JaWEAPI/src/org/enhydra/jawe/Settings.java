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

/**
 * Generic interface for retrieving different property settings. Setting is represented by
 * the key/value. The key uniquely identifies the setting, and the value is the
 * information the setting brings.
 */
public interface Settings {

   /**
    * Retrieves value for the setting.
    * 
    * @param key The key/Id of the setting.
    * @return The value for the setting (can be Integer, Boolean, Double, String, ...).
    */
   Object getSetting(String key);

   /**
    * Retrieves value for the "string" setting.
    * 
    * @param key The key/Id of the setting.
    * @return The String value for the setting.
    */
   String getSettingString(String key);

   /**
    * Retrieves value for the "int" setting.
    * 
    * @param key The key/Id of the setting.
    * @return The "int" value for the setting.
    */
   int getSettingInt(String key);

   /**
    * Retrieves value for the "boolean" setting.
    * 
    * @param key The key/Id of the setting.
    * @return The boolean value for the setting.
    */
   boolean getSettingBoolean(String key);

   /**
    * Retrieves value for the "double" setting.
    * 
    * @param key The key/Id of the setting.
    * @return The double value for the setting.
    */
   double getSettingDouble(String key);

   /**
    * Retrieves the language dependent (i18n) string for the given name/key.
    * 
    * @param nm The name/key for the string to be translated.
    * @return The language dependent (i18n) string for the given name/key.
    */
   String getLanguageDependentString(String nm);

}
