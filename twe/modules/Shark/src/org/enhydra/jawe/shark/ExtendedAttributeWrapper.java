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

import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;

public class ExtendedAttributeWrapper extends XMLComplexElement {

   protected ExtendedAttributes eas;

   protected boolean isDecisionAttribute;
   
   protected boolean isPersisted = false;

   public ExtendedAttributeWrapper(ExtendedAttributes eas, String name, boolean isDecisionAttribute) {
      super(eas.getParent(), name, true);
      this.eas = eas;
      this.isDecisionAttribute  = isDecisionAttribute;
      notifyMainListeners = false;
      notifyListeners = false;
      handleStructure();
      setReadOnly(eas.isReadOnly());
   }

   public boolean isDecisionAttribute () {
      return isDecisionAttribute;
   }
   
   public void setValue(String v) {
      if (isReadOnly) {
         throw new RuntimeException("Can't set the value of read only element!");
      }
      if (v == null) {
         SharkUtils.updateSingleExtendedAttribute(null, eas, toName(), getVValue(), true);
      } else {
         this.value = v;
      }
   }

   /** Returns the Value attribute value of this object. */
   public String getVValue() {
      return get(toName()).toValue();
   }

   /** Returns the Value attribute value of this object. */
   public void setVValue(String value) {
      set(toName(), value);
   }

   protected void fillStructure() {
      XMLAttribute attrValue = new XMLAttribute(this, toName(), false);
      add(attrValue);
   }

   protected void handleStructure() {
      ExtendedAttribute ea = eas.getFirstExtendedAttributeForName(toName());
      if (ea != null) {
         setVValue(ea.getVValue());
         isPersisted = true;
      }
   }

   public boolean isPersisted() {
      return isPersisted;
   }
   
}
