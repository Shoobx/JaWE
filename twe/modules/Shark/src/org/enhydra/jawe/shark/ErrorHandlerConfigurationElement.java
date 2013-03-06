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
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;

public class ErrorHandlerConfigurationElement extends XMLComplexElement {

   protected ExtendedAttributes eas;

   protected boolean isPersisted = false;

   protected EmailConfigurationElement elECE;

   public ErrorHandlerConfigurationElement(ExtendedAttributes eas) {
      super(eas.getParent(), (XMLUtil.getActivity(eas) == null ? "Default" : "")
                             + "ErrorHandlerConfiguration", true);
      this.eas = eas;
      notifyMainListeners = false;
      notifyListeners = false;
      handleStructure();
      setReadOnly(eas.isReadOnly() || !isConfigurable());
      if (!isConfigurable()) {
         if (elECE.isConfigurable()) {
            elECE.setConfigurable(false);
         }
         elECE.getConfigureEmailAttribute().setReadOnly(true);
      }
   }

   public void setValue(String v) {
      if (v == null) {
         boolean removeUnconditionally = !isConfigurable();
         if (removeUnconditionally) {
            if (elECE.isConfigurable()) {
               elECE.setConfigurable(false);
            }
            elECE.getConfigureEmailAttribute().setReadOnly(true);
         }

         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  SharkConstants.EA_SMTP_ERROR_HANDLER_RETURN_CODE,
                                                  null,
                                                  null,
                                                  false,
                                                  removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  SharkConstants.EA_SMTP_ERROR_HANDLER_RECIPIENT_PARTICIPANT,
                                                  null,
                                                  null,
                                                  false,
                                                  removeUnconditionally);
         elECE.setValue(null);
      } else {
         if (isReadOnly) {
            throw new RuntimeException("Can't set the value of read only element!");
         }
         this.value = v;
      }
   }

   public void setReadOnly(boolean ro) {
      boolean isECECFG = elECE.isConfigurable();
      super.setReadOnly(ro);
      if (!ro && !isECECFG) {
         elECE.setReadOnly(true);
      }
      if (ro) {
         if (elECE.isConfigurable()) {
            elECE.setConfigurable(false);
         }
         elECE.getConfigureEmailAttribute().setReadOnly(ro);
      }
      if (!eas.isReadOnly()) {
         getConfigureErrorHandlerAttribute().setReadOnly(false);
      }
   }

   public XMLAttribute getConfigureErrorHandlerAttribute() {
      return (XMLAttribute) get(SharkConstants.CONFIGURE_ERROR_HANDLER);
   }

   public XMLAttribute getReturnCodeAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_SMTP_ERROR_HANDLER_RETURN_CODE);
   }

   public XMLAttribute getRecipientParticipantAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_SMTP_ERROR_HANDLER_RECIPIENT_PARTICIPANT);
   }

   public EmailConfigurationElement getEmailConfigurationElement() {
      return elECE;
   }

   protected void fillStructure() {
      XMLAttribute attrConfigureEmail = new XMLAttribute(this,
                                                         SharkConstants.CONFIGURE_ERROR_HANDLER,
                                                         false,
                                                         new String[] {
                                                               "true", "false"
                                                         },
                                                         0);

      XMLAttribute attrReturnCode = new XMLAttribute(this,
                                                     SharkConstants.EA_SMTP_ERROR_HANDLER_RETURN_CODE,
                                                     false,
                                                     new String[] {
                                                           "PROPAGATE",
                                                           "IGNORE",
                                                           "TERMINATE_ACTIVITY",
                                                           "TERMINATE_PROCESS",
                                                     },
                                                     0);

      XMLAttribute attrRecipientParticipant = new XMLAttribute(this,
                                                               SharkConstants.EA_SMTP_ERROR_HANDLER_RECIPIENT_PARTICIPANT,
                                                               false);
      this.elECE = new EmailConfigurationElement((ExtendedAttributes) ((XMLComplexElement) parent).get("ExtendedAttributes"),
                                                 true,
                                                 true);
      add(attrConfigureEmail);
      add(attrReturnCode);
      add(attrRecipientParticipant);
      add(this.elECE);
   }

   protected void handleStructure() {
      elECE.handleStructure();
      int pc = 0;
      boolean hasAny = false;
      ExtendedAttribute earc = eas.getFirstExtendedAttributeForName(SharkConstants.EA_SMTP_ERROR_HANDLER_RETURN_CODE);
      ExtendedAttribute earp = eas.getFirstExtendedAttributeForName(SharkConstants.EA_SMTP_ERROR_HANDLER_RECIPIENT_PARTICIPANT);

      if (earc != null) {
         getReturnCodeAttribute().setValue(earc.getVValue());
         hasAny = true;
      }
      if (earp != null) {
         getRecipientParticipantAttribute().setValue(earp.getVValue());
         hasAny = true;
      }
      getConfigureErrorHandlerAttribute().setValue(String.valueOf(hasAny
                                                                  || elECE.isConfigurable()));

      isPersisted = hasAny && (elECE.isPersisted() || !elECE.isConfigurable());
   }

   public boolean isPersisted() {
      return isPersisted;
   }

   public boolean isConfigurable() {
      return getConfigureErrorHandlerAttribute().toValue().equalsIgnoreCase("true");
   }

}
