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

package org.enhydra.jawe.base.idfactory;

import java.util.HashSet;
import java.util.Set;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLUtil;

/**
 * Factory for generating XPDL objects.
 * 
 * @author Sasa Bojanic
 */
public class IdFactory {

   protected IdFactorySettings settings;

   public IdFactory() {
      settings = new IdFactorySettings();
      settings.init((JaWEComponent) null);
   }

   public IdFactory(IdFactorySettings settings) {
      this.settings = settings;
      this.settings.init((JaWEComponent) null);
   }

   public String generateUniqueId(XMLCollection cel) {
      return generateUniqueId(cel, new HashSet());
   }

   public String generateUniqueId(XMLCollection cel, Set skipIds) {
      return XMLUtil.generateUniqueId(cel, skipIds);
   }

   public String generateSimilarOrIdenticalUniqueId(XMLCollection cel,
                                                    Set skipIds,
                                                    String origId) {
      return XMLUtil.generateSimilarOrIdenticalUniqueId(cel, skipIds, origId);
   }

   public boolean isIdUnique(XMLCollectionElement el, String newId) {
      return XMLUtil.isIdUnique(el, newId);
   }

}
