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

import java.util.Iterator;

import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;

public class WebClientVariableConfigurationElement extends XMLComplexElement {

   protected ExtendedAttributes eas;

   public WebClientVariableConfigurationElement(ExtendedAttributes eas) {
      super(eas.getParent(), "WebClientConfiguration", true);
      this.eas = eas;
      notifyMainListeners = false;
      notifyListeners = false;
      handleStructure();
      setReadOnly(eas.isReadOnly());
   }

   public void setValue(String v) {
      if (v == null) {
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_URL_VARIABLE, null, null, true, false);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_RENDERING_HINT, null, null, true, false);
      } else {
         if (isReadOnly) {
            throw new RuntimeException("Can't set the value of read only element!");
         }
         this.value = v;
      }
   }

   public XMLAttribute getURLVariableAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_URL_VARIABLE);
   }

   public XMLAttribute getRenderingHintAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_RENDERING_HINT);
   }

   protected void fillStructure() {
      XMLAttribute attrURLVariable = new XMLAttribute(this, SharkConstants.EA_URL_VARIABLE, false, new String[] {
                                                                                                                  "",
                                                                                                                  SharkConstants.EA_URL_VARIABLE_VALUE_SAME_WINDOW,
                                                                                                                  SharkConstants.EA_URL_VARIABLE_VALUE_NEW_WINDOW
      }, 0);

      XMLAttribute attrRenderingHint = new XMLAttribute(this, SharkConstants.EA_RENDERING_HINT, false, new String[] {
                                                                                                                      "LARGE", ""
      }, 1);

      add(attrURLVariable);
      add(attrRenderingHint);
   }

   protected void handleStructure() {
      Iterator it = eas.toElements().iterator();
      while (it.hasNext()) {
         ExtendedAttribute ea = (ExtendedAttribute) it.next();
         String eaname = ea.getName();
         XMLElement attr = get(eaname);
         if (attr != null) {
            String eaval = ea.getVValue();
            if (attr instanceof XMLAttribute) {
               XMLAttribute xmlattr = (XMLAttribute) attr;
               if (xmlattr.getChoices() != null && !((XMLAttribute) attr).getChoices().contains(eaval)) {
                  eaval = xmlattr.getDefaultChoice();
               }
            }
            attr.setValue(eaval);
         }
      }
   }

   public boolean isForModalDialog() {
      return true;
   }

}
