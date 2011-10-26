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

import java.util.Properties;

/**
 * Interface for handling Properties.
 */
public interface PropertyMgr {

   /**
    * Reads and returns the properties from the property file or other property resource.
    * 
    * @param path The path to the property resource.
    * @param name The name of the property resource
    * @return The properties.
    */
   Properties loadProperties(String path, String name);

   /**
    * Manages the properties for the given {@link JaWEComponent}.
    * 
    * @param comp {@link JaWEComponent} instance.
    * @param settings {@link JaWEComponentSettings} instance.
    * @param path The path to the property resource.
    * @param name The name of the property resource
    */
   void manageProperties(JaWEComponent comp,
                         JaWEComponentSettings settings,
                         String path,
                         String name);

}
