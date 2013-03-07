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
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;
import org.enhydra.jxpdl.elements.SchemaType;

public class EmailConfigurationElement extends XMLComplexElement {

   protected ExtendedAttributes eas;

   protected boolean isPersisted = false;

   protected boolean isForErrorHandling = false;

   protected boolean isForLimitHandling = false;

   protected boolean isForAct = false;

   public EmailConfigurationElement(ExtendedAttributes eas,
                                    boolean isForAct,
                                    boolean isForErrorHandling,
                                    boolean isForLimitHandling) {
      super(eas.getParent(),
            ((XMLUtil.getActivity(eas) == null && isForAct) ? "Default"
                                                              + ((isForErrorHandling) ? ""
                                                                                     : "Activity")
                                                           : (XMLUtil.getWorkflowProcess(eas) == null) ? "Default"
                                                                                                         + ((isForErrorHandling) ? ""
                                                                                                                                : "Process")
                                                                                                      : "")
                  + (isForErrorHandling ? "Error" : (isForLimitHandling ? "Limit" : ""))
                  + "EmailConfiguration",
            true);
      this.eas = eas;
      this.isForAct = isForAct;
      this.isForErrorHandling = isForErrorHandling;
      this.isForLimitHandling = isForLimitHandling;
      notifyMainListeners = false;
      notifyListeners = false;
      handleStructure();
      setReadOnly(eas.isReadOnly() || !isConfigurable());
   }

   public void setValue(String v) {
      if (v == null) {
         boolean removeUnconditionally = !isConfigurable();
         String postFix = isForAct ? "_ACTIVITY" : "_PROCESS";

         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  isForErrorHandling ? SharkConstants.EA_SMTP_ERROR_HANDLER_MODE
                                                                    : (isForLimitHandling ? SharkConstants.SMTP_LIMIT_HANDLER_MODE
                                                                                            + postFix
                                                                                         : SharkConstants.SMTP_EVENT_AUDIT_MANAGER_MODE
                                                                                           + postFix),
                                                  null,
                                                  null,
                                                  false,
                                                  removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  isForErrorHandling ? SharkConstants.EA_SMTP_ERROR_HANDLER_EXECUTION_MODE
                                                                    : (isForLimitHandling ? SharkConstants.SMTP_LIMIT_HANDLER_EXECUTION_MODE
                                                                                            + postFix
                                                                                         : SharkConstants.SMTP_EVENT_AUDIT_MANAGER_EXECUTION_MODE
                                                                                           + postFix),
                                                  null,
                                                  null,
                                                  false,
                                                  removeUnconditionally);
         if (isForAct) {
            SharkUtils.updateSingleExtendedAttribute(this,
                                                     eas,
                                                     isForErrorHandling ? SharkConstants.EA_SMTP_ERROR_HANDLER_GROUP_EMAIL_ONLY
                                                                       : (isForLimitHandling ? SharkConstants.SMTP_LIMIT_HANDLER_GROUP_EMAIL_ONLY
                                                                                               + postFix
                                                                                            : SharkConstants.SMTP_EVENT_AUDIT_MANAGER_GROUP_EMAIL_ONLY
                                                                                              + postFix),
                                                     null,
                                                     null,
                                                     false,
                                                     removeUnconditionally);
         }
         if (isForErrorHandling || isForLimitHandling) {
            SharkUtils.updateSingleExtendedAttribute(this,
                                                     eas,
                                                     isForErrorHandling ? SharkConstants.EA_SMTP_ERROR_HANDLER_RECIPIENT_PARTICIPANT
                                                                       : SharkConstants.SMTP_LIMIT_HANDLER_RECIPIENT_PARTICIPANT
                                                                         + postFix,
                                                     null,
                                                     null,
                                                     false,
                                                     removeUnconditionally);
         }

         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  isForErrorHandling ? SharkConstants.EA_SMTP_ERROR_HANDLER_SUBJECT
                                                                    : (isForLimitHandling ? SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT
                                                                                            + postFix
                                                                                         : SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT
                                                                                           + postFix),
                                                  null,
                                                  null,
                                                  true,
                                                  removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  isForErrorHandling ? SharkConstants.EA_SMTP_ERROR_HANDLER_CONTENT
                                                                    : (isForLimitHandling ? SharkConstants.SMTP_LIMIT_HANDLER_CONTENT
                                                                                            + postFix
                                                                                         : SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT
                                                                                           + postFix),
                                                  null,
                                                  null,
                                                  true,
                                                  removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  isForErrorHandling ? SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENTS
                                                                    : (isForLimitHandling ? SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENTS
                                                                                            + postFix
                                                                                         : SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS
                                                                                           + postFix),
                                                  "ContentVariable",
                                                  null,
                                                  true,
                                                  removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  isForErrorHandling ? SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENT_NAMES
                                                                    : (isForLimitHandling ? SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES
                                                                                            + postFix
                                                                                         : SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES
                                                                                           + postFix),
                                                  "NameVariableOrExpression",
                                                  null,
                                                  true,
                                                  removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  isForErrorHandling ? SharkConstants.EA_SMTP_ERROR_HANDLER_DM_ATTACHMENTS
                                                                    : (isForLimitHandling ? SharkConstants.SMTP_LIMIT_HANDLER_DM_ATTACHMENTS
                                                                                            + postFix
                                                                                         : SharkConstants.SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS
                                                                                           + postFix),
                                                  null,
                                                  null,
                                                  true,
                                                  removeUnconditionally);
      } else {
         if (isReadOnly) {
            throw new RuntimeException("Can't set the value of read only element!");
         }
         this.value = v;
      }
   }

   public void setReadOnly(boolean ro) {
      super.setReadOnly(ro);
      if (!eas.isReadOnly()) {
         getConfigureEmailAttribute().setReadOnly(false);
      }
   }

   public XMLElement get(String name) {
      String postFix = isForAct ? "_ACTIVITY" : "_PROCESS";
      if ((SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES + postFix).equals(name)
          || (SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENT_NAMES).equals(name)
          || (SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES + postFix).equals(name)) {
         name = SharkConstants.SMTP_ATTACHMENTS;
      }
      if (isForErrorHandling) {
         name = name.replaceAll("ERROR_HANDLER_", "");
      } else if (isForLimitHandling) {
         name = name.replaceAll("LIMIT_HANDLER_", "");
      } else {
         name = name.replaceAll("EVENT_AUDIT_MANAGER_", "");
      }

      if (name.endsWith(postFix)) {
         name = name.substring(0, name.lastIndexOf(postFix));
      }
      return super.get(name);
   }

   public XMLAttribute getConfigureEmailAttribute() {
      return (XMLAttribute) get(SharkConstants.CONFIGURE_SMTP_EVENT_AUDIT_MANAGER);
   }

   public XMLAttribute getModeAttribute() {
      return (XMLAttribute) get(SharkConstants.SMTP_MODE);
   }

   public XMLAttribute getExecutionModeAttribute() {
      return (XMLAttribute) get(SharkConstants.SMTP_EXECUTION_MODE);
   }

   public XMLAttribute getGroupEmailOnlyAttribute() {
      return (XMLAttribute) get(SharkConstants.SMTP_GROUP_EMAIL_ONLY);
   }

   public XMLAttribute getRecipientParticipantAttribute() {
      return (XMLAttribute) get(SharkConstants.SMTP_RECIPIENT_PARTICIPANT);
   }

   public XMLAttribute getSubjectAttribute() {
      return (XMLAttribute) get(SharkConstants.SMTP_SUBJECT);
   }

   public XMLAttribute getContentAttribute() {
      return (XMLAttribute) get(SharkConstants.SMTP_CONTENT);
   }

   public WfAttachments getAttachmentsElement() {
      return (WfAttachments) get(SharkConstants.SMTP_ATTACHMENTS);
   }

   public WfVariables getDMAttachmentsElement() {
      return (WfVariables) get(SharkConstants.SMTP_DM_ATTACHMENTS);
   }

   protected void fillStructure() {
      XMLAttribute attrConfigureEmail = new XMLAttribute(this,
                                                         SharkConstants.CONFIGURE_SMTP_EVENT_AUDIT_MANAGER,
                                                         false,
                                                         new String[] {
                                                               "true", "false"
                                                         },
                                                         0);

      XMLAttribute attrMode = new XMLAttribute(this,
                                               SharkConstants.SMTP_MODE,
                                               false,
                                               new String[] {
                                                     "true", "false"
                                               },
                                               0);
      XMLAttribute attrExecutionMode = new XMLAttribute(this,
                                                        SharkConstants.SMTP_EXECUTION_MODE,
                                                        false,
                                                        new String[] {
                                                              "asynchronous",
                                                              "synchronous"
                                                        },
                                                        0);
      XMLAttribute attrGroupEmailOnly = new XMLAttribute(this,
                                                         SharkConstants.SMTP_GROUP_EMAIL_ONLY,
                                                         false,
                                                         new String[] {
                                                               "true", "false"
                                                         },
                                                         1);

      XMLAttribute attrRecipientParticipant = new XMLAttribute(this,
                                                               SharkConstants.SMTP_RECIPIENT_PARTICIPANT,
                                                               false);
      XMLAttribute attrSubject = new XMLAttribute(this,
                                                  SharkConstants.SMTP_SUBJECT,
                                                  false);

      XMLAttribute attrContent = new XMLAttribute(this,
                                                  SharkConstants.SMTP_CONTENT,
                                                  false);
      WfAttachments elAttachments = new WfAttachments(this,
                                                      SharkConstants.SMTP_ATTACHMENTS,
                                                      Arrays.asList(new String[] {
                                                            XPDLConstants.BASIC_TYPE_STRING,
                                                            XMLUtil.getShortClassName(SchemaType.class.getName())
                                                      }),
                                                      Arrays.asList(new String[] {
                                                         XPDLConstants.BASIC_TYPE_STRING
                                                      }),
                                                      ",",
                                                      false);
      WfVariables elDMAttachments = new WfVariables(this,
                                                    SharkConstants.SMTP_DM_ATTACHMENTS,
                                                    Arrays.asList(new String[] {
                                                       XPDLConstants.BASIC_TYPE_STRING
                                                    }),
                                                    ",",
                                                    false);
      add(attrConfigureEmail);
      add(attrMode);
      add(attrExecutionMode);
      add(attrGroupEmailOnly);
      add(attrRecipientParticipant);
      add(attrSubject);
      add(attrContent);
      add(elAttachments);
      add(elDMAttachments);

   }

   protected void handleStructure() {
      int pc = 0;
      Iterator it = eas.toElements().iterator();
      String postFix = isForAct ? "_ACTIVITY" : "_PROCESS";
      if (isForErrorHandling)
         postFix = "";
      boolean hasAny = false;
      while (it.hasNext()) {
         ExtendedAttribute ea = (ExtendedAttribute) it.next();
         String eaname = ea.getName();
         XMLElement attr = null;
         if (isForErrorHandling && eaname.startsWith("SMTP_ERROR_HANDLER_")) {
            eaname = eaname.replaceAll("ERROR_HANDLER_", "");
         } else if (isForLimitHandling && eaname.startsWith("SMTP_LIMIT_HANDLER_")) {
            eaname = eaname.replaceAll("LIMIT_HANDLER_", "");
            int indof = eaname.lastIndexOf(postFix);
            if (indof == -1)
               continue;
            eaname = eaname.substring(0, indof);
         } else if (!(isForErrorHandling || isForLimitHandling)
                    && eaname.startsWith("SMTP_EVENT_AUDIT_MANAGER_")) {
            eaname = eaname.replaceAll("EVENT_AUDIT_MANAGER_", "");
            int indof = eaname.lastIndexOf(postFix);
            if (indof == -1)
               continue;
            eaname = eaname.substring(0, indof);
         } else {
            continue;
         }
         attr = get(eaname);
         if (attr != null) {
            String eaval = ea.getVValue();
            if (eaname.equals(SharkConstants.SMTP_DM_ATTACHMENTS)) {
               ((WfVariables) attr).createStructure(eaval);
            } else if (eaname.equals(SharkConstants.SMTP_ATTACHMENTS)) {
               String eansn = isForErrorHandling ? SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENT_NAMES
                                                : isForLimitHandling ? SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES
                                                                       + postFix
                                                                    : SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES
                                                                      + postFix;
               ExtendedAttribute eans = eas.getFirstExtendedAttributeForName(eansn);
               String nms = "";
               if (eans != null) {
                  nms = eans.getVValue();
               }
               ((WfAttachments) attr).createStructure(eaval, nms);
            } else {
               if (eaname.equals(SharkConstants.SMTP_MODE)
                   || eaname.equals(SharkConstants.SMTP_EXECUTION_MODE)
                   || eaname.equals(SharkConstants.SMTP_GROUP_EMAIL_ONLY)
                   || eaname.equals(SharkConstants.SMTP_RECIPIENT_PARTICIPANT)) {
                  pc++;
               }
               attr.setValue(eaval);
            }
            hasAny = true;
         }
      }
      getConfigureEmailAttribute().setValue(String.valueOf(hasAny));
      int toCompNo = (isForAct ? 3 : 2) + (isForErrorHandling || isForLimitHandling ? 1 : 0);
      isPersisted = pc >= toCompNo;
   }

   public boolean isPersisted() {
      return isPersisted;
   }

   public boolean isConfigurable() {
      return getConfigureEmailAttribute().toValue().equalsIgnoreCase("true");
   }

   public void setConfigurable(boolean isConfigurable) {
      getConfigureEmailAttribute().setValue(String.valueOf(isConfigurable));
   }

   public boolean isForActivity() {
      return isForAct;
   }

   public boolean isForErrorHandling() {
      return isForErrorHandling;
   }

   public boolean isForLimitHandling() {
      return isForLimitHandling;
   }

}
