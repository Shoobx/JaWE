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
package org.enhydra.jawe.shark;

import org.enhydra.jawe.base.controller.ControllerSettings;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.shark.business.XPDLStringVariable;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLElement;

public class SharkJaWEController extends JaWEController {

   public SharkJaWEController(ControllerSettings settings) {
      super(settings);
   }

   public boolean canDuplicateElement(XMLCollection col,
                                      XMLElement el,
                                      boolean checkReadOnly) {
      if (el instanceof XPDLStringVariable) {
         return false;
      }
      return super.canDuplicateElement(col, el, checkReadOnly);
   }
}
