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

import java.util.Arrays;
import java.util.Iterator;

import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;
import org.enhydra.jxpdl.elements.SchemaType;

public class EmailConfigurationElement extends XMLComplexElement {

   protected ExtendedAttributes eas;

   protected boolean isPersisted = false;

   public EmailConfigurationElement(ExtendedAttributes eas) {
      super(eas.getParent(), "EmailConfiguration", true);
      this.eas = eas;
      notifyMainListeners = false;
      notifyListeners = false;
      handleStructure();
      setReadOnly(eas.isReadOnly());
   }

   public void setValue(String v) {
      if (isReadOnly) {
         throw new RuntimeException("Can't set the value of read only element!");
      }
      if (v == null) {
         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_MODE,
                                                  null,
                                                  false);
         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_EXECUTION_MODE,
                                                  null,
                                                  false);
         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_GROUP_EMAIL_ONLY,
                                                  null,
                                                  false);
         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_SUBJECT,
                                                  null,
                                                  true);
         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_CONTENT,
                                                  null,
                                                  true);
         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS,
                                                  null,
                                                  true);
         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES,
                                                  null,
                                                  true);
         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS,
                                                  null,
                                                  true);
      } else {
         this.value = v;
      }
   }

   public XMLAttribute getModeAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_MODE);
   }

   public XMLAttribute getExecutionModeAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_EXECUTION_MODE);
   }

   public XMLAttribute getGroupEmailOnlyAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_GROUP_EMAIL_ONLY);
   }

   public XMLAttribute getSubjectAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_SUBJECT);
   }

   public XMLAttribute getContentAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_CONTENT);
   }

   public WfVariables getAttachmentsElement() {
      return (WfVariables) get(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS);
   }

   public WfVariables getAttachmentNamesElement() {
      return (WfVariables) get(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES);
   }

   public WfVariables getDMAttachmentsElement() {
      return (WfVariables) get(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS);
   }

   protected void fillStructure() {

      XMLAttribute attrMode = new XMLAttribute(this,
                                               SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_MODE,
                                               false,
                                               new String[] {
                                                     "true", "false"
                                               },
                                               0);
      XMLAttribute attrExecutionMode = new XMLAttribute(this,
                                                        SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_EXECUTION_MODE,
                                                        false,
                                                        new String[] {
                                                              "asynchronous",
                                                              "synchronous"
                                                        },
                                                        0);
      XMLAttribute attrGroupEmailOnly = new XMLAttribute(this,
                                                         SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_GROUP_EMAIL_ONLY,
                                                         false,
                                                         new String[] {
                                                               "true", "false"
                                                         },
                                                         1);
      XMLAttribute attrSubject = new XMLAttribute(this,
                                                  SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_SUBJECT,
                                                  false);

      XMLAttribute attrContent = new XMLAttribute(this,
                                                  SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_CONTENT,
                                                  false);
      WfVariables elAttachments = new WfVariables(this,
                                                  SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS,
                                                  Arrays.asList(new String[] {
                                                     XMLUtil.getShortClassName(SchemaType.class.getName())
                                                  }),
                                                  ",",
                                                  false);
      WfVariables elAttachmentNames = new WfVariables(this,
                                                      SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES,
                                                      Arrays.asList(new String[] {
                                                         XPDLConstants.BASIC_TYPE_STRING
                                                      }),
                                                      ",",
                                                      false);
      WfVariables elDMAttachments = new WfVariables(this,
                                                    SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS,
                                                    Arrays.asList(new String[] {
                                                       XPDLConstants.BASIC_TYPE_STRING
                                                    }),
                                                    ",",
                                                    false);
      add(attrMode);
      add(attrExecutionMode);
      add(attrGroupEmailOnly);
      add(attrSubject);
      add(attrContent);
      add(elAttachments);
      add(elAttachmentNames);
      add(elDMAttachments);

   }

   protected void handleStructure() {
      int pc = 0;
      Iterator it = eas.toElements().iterator();
      while (it.hasNext()) {
         ExtendedAttribute ea = (ExtendedAttribute) it.next();
         String eaname = ea.getName();
         XMLElement attr = get(eaname);
         if (attr != null) {
            String eaval = ea.getVValue();
            if (eaname.equals(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS)
                || eaname.equals(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS)
                || eaname.equals(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES)) {
               ((WfVariables) attr).createStructure(eaval);
            } else {
               if (eaname.equals(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_MODE)
                   || eaname.equals(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_EXECUTION_MODE)
                   || eaname.equals(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_GROUP_EMAIL_ONLY)) {
                  pc++;
               }
               attr.setValue(eaval);
            }
         }
      }
      isPersisted = pc >= 3;
   }

   public boolean isPersisted() {
      return isPersisted;
   }
}
