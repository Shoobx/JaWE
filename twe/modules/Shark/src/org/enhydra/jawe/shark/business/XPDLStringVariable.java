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

package org.enhydra.jawe.shark.business;

import org.enhydra.jxpdl.elements.ExtendedAttribute;

public class XPDLStringVariable extends ExtendedAttribute {

   protected ExtendedAttribute ea;

   public XPDLStringVariable(XPDLStringVariables parent, ExtendedAttribute ea) {
      super(parent);
      this.ea = ea;
      notifyMainListeners = false;
      notifyListeners = false;
      handleStructure();
      setReadOnly(ea.isReadOnly());
   }

   public void setValue(String v) {
      if (isReadOnly) {
         throw new RuntimeException("Can't set the value of read only element!");
      }
      if (v == null) {
         ea.setName(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX + getName());
         ea.setVValue(getVValue());
      } else {
         this.value = v;
      }
   }

   protected void handleStructure() {
      if (ea != null) {
         setName(ea.getName().substring(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX.length()));
         setVValue(ea.getVValue());
      }
   }

   public ExtendedAttribute getExtendedAttribute() {
      return ea;
   }
}
