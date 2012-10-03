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
import java.util.List;

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

   public EmailConfigurationElement(ExtendedAttributes eas) {
      super(eas.getParent(), "EmailConfiguration", true);
      this.eas = eas;
      notifyMainListeners = false;
      notifyListeners = false;
      handleStructure(this, eas);
      setReadOnly(eas.isReadOnly());
   }

   public void setValue(String v) {
      if (isReadOnly) {
         throw new RuntimeException("Can't set the value of read only element!");
      }
      if (v == null) {
         updateSingleElement(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_MODE, false);
         updateSingleElement(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_EXECUTION_MODE,
                             false);
         updateSingleElement(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_GROUP_EMAIL_ONLY,
                             false);
         updateSingleElement(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_SUBJECT, true);
         updateSingleElement(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_CONTENT, true);
         updateSingleElement(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS, true);
         updateSingleElement(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES,
                             true);
         updateSingleElement(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS,
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

   protected static void handleStructure(XMLComplexElement el, ExtendedAttributes eas) {
      Iterator it = eas.toElements().iterator();
      while (it.hasNext()) {
         ExtendedAttribute ea = (ExtendedAttribute) it.next();
         XMLElement attr = el.get(ea.getName());
         if (attr != null) {
            if (ea.getName()
               .equals(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS)
                || ea.getName()
                   .equals(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS)
                || ea.getName()
                   .equals(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES)) {
               ((WfVariables) attr).createStructure(ea.getVValue());
            } else {
               attr.setValue(ea.getVValue());
            }
         }
      }

   }

   protected void updateSingleElement(String name, boolean removeIfEmpty) {
      ExtendedAttribute ea = eas.getFirstExtendedAttributeForName(name);
      XMLElement el = get(name);
      if (ea == null) {
         ea = (ExtendedAttribute) eas.generateNewElement();
         ea.setName(name);
         eas.add(ea);
      }
      String val = "";
      if (el instanceof WfVariables) {
         WfVariables vare = (WfVariables) el;
         val = "";
         if (vare.size() > 0) {
            List varl = vare.toElements();
            for (int i = 0; i < varl.size(); i++) {
               val += ((WfVariable) varl.get(i)).get("Id").toValue();
               if (i < varl.size() - 1) {
                  val += ",";
               }
            }
         }
      } else {
         val = el.toValue().trim();
      }
      if (removeIfEmpty && val.equals("")) {
         eas.remove(ea);
      } else {
         ea.setVValue(val);
      }
   }

}
