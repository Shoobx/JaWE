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

import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.ExtendedAttributes;

public class DeadlineHandlerConfigurationElement extends XMLComplexElement {

   protected ExtendedAttributes eas;

   protected boolean isPersisted = false;

   protected EmailConfigurationElement elECE;

   protected DeadlineEmailConfigurationElements elECEs;

   public DeadlineHandlerConfigurationElement(ExtendedAttributes eas) {
      super(eas.getParent(), (XMLUtil.getActivity(eas) == null ? "Default" : "")
                             + "DeadlineHandlerConfiguration", true);
      this.eas = eas;
      notifyMainListeners = false;
      notifyListeners = false;
      this.elECEs.setReadOnly(eas.isReadOnly());
   }

   public void setValue(String v) {
      if (v == null) {
//         elECE.setValue(null);
//         Thread.dumpStack();
      } else {
         if (isReadOnly) {
            throw new RuntimeException("Can't set the value of read only element!");
         }
         this.value = v;
      }
   }

   public EmailConfigurationElement getEmailConfigurationElement() {
      return elECE;
   }

   public DeadlineEmailConfigurationElements getEmailConfigurationElements() {
      return elECEs;
   }

   protected void fillStructure() {
      ExtendedAttributes eas = (ExtendedAttributes) ((XMLComplexElement) parent).get("ExtendedAttributes");
      this.elECE = new EmailConfigurationElement(eas,
                                                 true,
                                                 false,
                                                 true,
                                                 false,
                                                 null);
      this.elECEs = new DeadlineEmailConfigurationElements(eas);
      add(this.elECE);
      add(this.elECEs);
   }

   public boolean isPersisted() {
      return elECE.isPersisted();
   }

}
