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

   protected boolean isForErrorHandling = false;

   protected boolean isForDeadlineHandling = false;

   protected boolean isForLimitHandling = false;

   protected String eaExtension = null;

   protected boolean isForAct = false;

   protected boolean canSetValue = true;

   public EmailConfigurationElement(ExtendedAttributes eas,
                                    boolean isForAct,
                                    boolean isForErrorHandling,
                                    boolean isForDeadlineHandling,
                                    boolean isForLimitHandling,
                                    String eaExtension) {
      super(eas.getParent(), getMyName(eas, isForAct, isForErrorHandling, isForDeadlineHandling, isForLimitHandling, eaExtension), true);
      this.eas = eas;
      this.isForAct = isForAct;
      this.isForErrorHandling = isForErrorHandling;
      this.isForDeadlineHandling = isForDeadlineHandling || eaExtension != null;
      this.isForLimitHandling = isForLimitHandling;
      this.eaExtension = eaExtension;
      notifyMainListeners = false;
      notifyListeners = false;
      if (eaExtension != null) {
         set("ExceptionName", eaExtension);
         setConfigurable(true);
      }
      if (!"".equals(eaExtension)) {
         handleStructure();
      }
      setReadOnly(eas.isReadOnly() || !isConfigurable());
   }

   public void setValue(String v) {
      if (!canSetValue()) {
         return;
      }
      if (v == null) {
         boolean removeUnconditionally = !isConfigurable();
         String postFix = getPostfix();
         if (eaExtension != null && !eaExtension.equals("") && !postFix.equals("_" + eaExtension)) {
            String oldPostFix = "_" + eaExtension;
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_SMTP_DEADLINE_HANDLER_MODE + oldPostFix, null, null, false, true);
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_SMTP_DEADLINE_HANDLER_EXECUTION_MODE + oldPostFix, null, null, false, true);
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_SMTP_DEADLINE_HANDLER_GROUP_EMAIL_ONLY + oldPostFix, null, null, false, true);
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_SMTP_DEADLINE_HANDLER_SIGNED_EMAIL + oldPostFix, null, null, false, true);
            SharkUtils.updateSingleExtendedAttribute(this,
                                                     eas,
                                                     SharkConstants.EA_SMTP_DEADLINE_HANDLER_RECIPIENT_PARTICIPANT + oldPostFix,
                                                     null,
                                                     null,
                                                     false,
                                                     true);
            SharkUtils.updateSingleExtendedAttribute(this,
                                                     eas,
                                                     SharkConstants.EA_SMTP_DEADLINE_HANDLER_RECIPIENT_VARIABLE + oldPostFix,
                                                     null,
                                                     null,
                                                     true,
                                                     true);
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_SMTP_DEADLINE_HANDLER_SUBJECT + oldPostFix, null, null, true, true);
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_SMTP_DEADLINE_HANDLER_CONTENT + oldPostFix, null, null, true, true);
            SharkUtils
               .updateSingleExtendedAttribute(this, eas, SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENTS + oldPostFix, "ContentVariable", null, true, true);
            SharkUtils.updateSingleExtendedAttribute(this,
                                                     eas,
                                                     SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENT_NAMES + oldPostFix,
                                                     "NameVariableOrExpression",
                                                     null,
                                                     true,
                                                     true);
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_SMTP_DEADLINE_HANDLER_DM_ATTACHMENTS + oldPostFix, null, null, true, true);

         }
         SharkUtils
            .updateSingleExtendedAttribute(this,
                                           eas,
                                           isForErrorHandling ? SharkConstants.EA_SMTP_ERROR_HANDLER_MODE
                                                              : (isForDeadlineHandling ? SharkConstants.EA_SMTP_DEADLINE_HANDLER_MODE + postFix
                                                                                       : (isForLimitHandling ? SharkConstants.SMTP_LIMIT_HANDLER_MODE + postFix
                                                                                                             : SharkConstants.SMTP_EVENT_AUDIT_MANAGER_MODE
                                                                                                               + postFix)),
                                           null,
                                           null,
                                           false,
                                           removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  isForErrorHandling ? SharkConstants.EA_SMTP_ERROR_HANDLER_EXECUTION_MODE
                                                                     : (isForDeadlineHandling ? SharkConstants.EA_SMTP_DEADLINE_HANDLER_EXECUTION_MODE + postFix
                                                                                              : (isForLimitHandling ? SharkConstants.SMTP_LIMIT_HANDLER_EXECUTION_MODE
                                                                                                                      + postFix
                                                                                                                    : SharkConstants.SMTP_EVENT_AUDIT_MANAGER_EXECUTION_MODE
                                                                                                                      + postFix)),
                                                  null,
                                                  null,
                                                  false,
                                                  removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  isForErrorHandling ? SharkConstants.EA_SMTP_ERROR_HANDLER_SIGNED_EMAIL
                                                                     : (isForDeadlineHandling ? SharkConstants.EA_SMTP_DEADLINE_HANDLER_SIGNED_EMAIL + postFix
                                                                                              : (isForLimitHandling ? SharkConstants.SMTP_LIMIT_HANDLER_SIGNED_EMAIL
                                                                                                                      + postFix
                                                                                                                    : SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SIGNED_EMAIL
                                                                                                                      + postFix)),
                                                  null,
                                                  null,
                                                  false,
                                                  removeUnconditionally);
         if (isForAct) {
            SharkUtils
               .updateSingleExtendedAttribute(this,
                                              eas,
                                              isForErrorHandling ? SharkConstants.EA_SMTP_ERROR_HANDLER_GROUP_EMAIL_ONLY
                                                                 : (isForDeadlineHandling ? SharkConstants.EA_SMTP_DEADLINE_HANDLER_GROUP_EMAIL_ONLY + postFix
                                                                                          : (isForLimitHandling ? SharkConstants.SMTP_LIMIT_HANDLER_GROUP_EMAIL_ONLY
                                                                                                                  + postFix
                                                                                                                : SharkConstants.SMTP_EVENT_AUDIT_MANAGER_GROUP_EMAIL_ONLY
                                                                                                                  + postFix)),
                                              null,
                                              null,
                                              false,
                                              removeUnconditionally);
         }
         if (isForErrorHandling || isForDeadlineHandling || isForLimitHandling) {
            SharkUtils
               .updateSingleExtendedAttribute(this,
                                              eas,
                                              isForErrorHandling ? SharkConstants.EA_SMTP_ERROR_HANDLER_RECIPIENT_PARTICIPANT
                                                                 : (isForDeadlineHandling ? SharkConstants.EA_SMTP_DEADLINE_HANDLER_RECIPIENT_PARTICIPANT
                                                                                            + postFix
                                                                                          : SharkConstants.SMTP_LIMIT_HANDLER_RECIPIENT_PARTICIPANT + postFix),
                                              null,
                                              null,
                                              false,
                                              removeUnconditionally);
            SharkUtils
               .updateSingleExtendedAttribute(this,
                                              eas,
                                              isForErrorHandling ? SharkConstants.EA_SMTP_ERROR_HANDLER_RECIPIENT_VARIABLE
                                                                 : (isForDeadlineHandling ? SharkConstants.EA_SMTP_DEADLINE_HANDLER_RECIPIENT_VARIABLE + postFix
                                                                                          : SharkConstants.SMTP_LIMIT_HANDLER_RECIPIENT_VARIABLE + postFix),
                                              null,
                                              null,
                                              true,
                                              removeUnconditionally);
         }

         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  isForErrorHandling ? SharkConstants.EA_SMTP_ERROR_HANDLER_SUBJECT
                                                                     : (isForDeadlineHandling ? SharkConstants.EA_SMTP_DEADLINE_HANDLER_SUBJECT + postFix
                                                                                              : (isForLimitHandling ? SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT
                                                                                                                      + postFix
                                                                                                                    : SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT
                                                                                                                      + postFix)),
                                                  null,
                                                  null,
                                                  true,
                                                  removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  isForErrorHandling ? SharkConstants.EA_SMTP_ERROR_HANDLER_CONTENT
                                                                     : (isForDeadlineHandling ? SharkConstants.EA_SMTP_DEADLINE_HANDLER_CONTENT + postFix
                                                                                              : (isForLimitHandling ? SharkConstants.SMTP_LIMIT_HANDLER_CONTENT
                                                                                                                      + postFix
                                                                                                                    : SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT
                                                                                                                      + postFix)),
                                                  null,
                                                  null,
                                                  true,
                                                  removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  isForErrorHandling ? SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENTS
                                                                     : (isForDeadlineHandling ? SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENTS + postFix
                                                                                              : (isForLimitHandling ? SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENTS
                                                                                                                      + postFix
                                                                                                                    : SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS
                                                                                                                      + postFix)),
                                                  "ContentVariable",
                                                  null,
                                                  true,
                                                  removeUnconditionally);
         SharkUtils
            .updateSingleExtendedAttribute(this,
                                           eas,
                                           isForErrorHandling ? SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENT_NAMES
                                                              : (isForDeadlineHandling ? SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENT_NAMES + postFix
                                                                                       : (isForLimitHandling ? SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES
                                                                                                               + postFix
                                                                                                             : SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES
                                                                                                               + postFix)),
                                           "NameVariableOrExpression",
                                           null,
                                           true,
                                           removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  isForErrorHandling ? SharkConstants.EA_SMTP_ERROR_HANDLER_DM_ATTACHMENTS
                                                                     : (isForDeadlineHandling ? SharkConstants.EA_SMTP_DEADLINE_HANDLER_DM_ATTACHMENTS + postFix
                                                                                              : (isForLimitHandling ? SharkConstants.SMTP_LIMIT_HANDLER_DM_ATTACHMENTS
                                                                                                                      + postFix
                                                                                                                    : SharkConstants.SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS
                                                                                                                      + postFix)),
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
      String postFix = getPostfix();
      if ((SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES + postFix).equals(name)
          || (SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENT_NAMES).equals(name)
          || (SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENT_NAMES + postFix).equals(name)
          || (SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES + postFix).equals(name)) {
         name = SharkConstants.SMTP_ATTACHMENTS;
      }
      if (isForErrorHandling) {
         name = name.replaceAll("ERROR_HANDLER_", "");
      } else if (isForDeadlineHandling) {
         name = name.replaceAll("DEADLINE_HANDLER_", "");
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

   public XMLAttribute getExtensionAttribute() {
      return (XMLAttribute) get("ExceptionName");
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

   public XMLAttribute getSignedEmailAttribute() {
      return (XMLAttribute) get(SharkConstants.SMTP_SIGNED_EMAIL);
   }

   public XMLAttribute getRecipientParticipantAttribute() {
      return (XMLAttribute) get(SharkConstants.SMTP_RECIPIENT_PARTICIPANT);
   }

   public XMLAttribute getRecipientUserAttribute() {
      return (XMLAttribute) get(SharkConstants.SMTP_RECIPIENT_VARIABLE);
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
      XMLAttribute attrConfigureEmail = new XMLAttribute(this, SharkConstants.CONFIGURE_SMTP_EVENT_AUDIT_MANAGER, false, new String[] {
                                                                                                                                        "true", "false"
      }, 0);

      XMLAttribute attrEANameSuffix = new XMLAttribute(this, "ExceptionName", true);
      XMLAttribute attrMode = new XMLAttribute(this, SharkConstants.SMTP_MODE, false, new String[] {
                                                                                                     "true", "false"
      }, 0);
      XMLAttribute attrExecutionMode = new XMLAttribute(this, SharkConstants.SMTP_EXECUTION_MODE, false, new String[] {
                                                                                                                        "asynchronous", "synchronous"
      }, 0);
      XMLAttribute attrGroupEmailOnly = new XMLAttribute(this, SharkConstants.SMTP_GROUP_EMAIL_ONLY, false, new String[] {
                                                                                                                           "true", "false"
      }, 1);
      XMLAttribute attrSignedEmail = new XMLAttribute(this, SharkConstants.SMTP_SIGNED_EMAIL, false, new String[] {
                                                                                                                    "true", "false"
      }, 1);

      XMLAttribute attrRecipientParticipant = new XMLAttribute(this, SharkConstants.SMTP_RECIPIENT_PARTICIPANT, false);
      XMLAttribute attrRecipientUser = new XMLAttribute(this, SharkConstants.SMTP_RECIPIENT_VARIABLE, false);
      XMLAttribute attrSubject = new XMLAttribute(this, SharkConstants.SMTP_SUBJECT, false);

      XMLAttribute attrContent = new XMLAttribute(this, SharkConstants.SMTP_CONTENT, false);
      WfAttachments elAttachments = new WfAttachments(this, SharkConstants.SMTP_ATTACHMENTS, Arrays.asList(new String[] {
                                                                                                                          XPDLConstants.BASIC_TYPE_STRING,
                                                                                                                          XMLUtil
                                                                                                                             .getShortClassName(SchemaType.class
                                                                                                                                .getName())
      }), Arrays.asList(new String[] {
                                       XPDLConstants.BASIC_TYPE_STRING
      }), ",", false);
      WfVariables elDMAttachments = new WfVariables(this, SharkConstants.SMTP_DM_ATTACHMENTS, Arrays.asList(new String[] {
                                                                                                                           XPDLConstants.BASIC_TYPE_STRING
      }), ",", false);
      add(attrConfigureEmail);
      add(attrEANameSuffix);
      add(attrMode);
      add(attrExecutionMode);
      add(attrGroupEmailOnly);
      add(attrSignedEmail);
      add(attrRecipientParticipant);
      add(attrRecipientUser);
      add(attrSubject);
      add(attrContent);
      add(elAttachments);
      add(elDMAttachments);

   }

   protected void handleStructure() {
      int pc = 0;
      Iterator it = eas.toElements().iterator();
      String postFix = getPostfix();
      boolean hasAny = false;
      while (it.hasNext()) {
         ExtendedAttribute ea = (ExtendedAttribute) it.next();
         String eaname = ea.getName();
         XMLElement attr = null;
         if (isForErrorHandling && eaname.startsWith("SMTP_ERROR_HANDLER_")) {
            eaname = eaname.replaceAll("ERROR_HANDLER_", "");
         } else if (isForDeadlineHandling && eaname.startsWith("SMTP_DEADLINE_HANDLER_")) {
            eaname = eaname.replaceAll("DEADLINE_HANDLER_", "");
            if (!postFix.equals("")) {
               int indof = eaname.lastIndexOf(postFix);
               if (indof == -1)
                  continue;
               eaname = eaname.substring(0, indof);
            }
         } else if (isForLimitHandling && eaname.startsWith("SMTP_LIMIT_HANDLER_")) {
            eaname = eaname.replaceAll("LIMIT_HANDLER_", "");
            int indof = eaname.lastIndexOf(postFix);
            if (indof == -1)
               continue;
            eaname = eaname.substring(0, indof);
         } else if (!(isForErrorHandling || isForDeadlineHandling || isForLimitHandling) && eaname.startsWith("SMTP_EVENT_AUDIT_MANAGER_")) {
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
                                                 : (isForDeadlineHandling ? SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENT_NAMES
                                                                          : (isForLimitHandling ? SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES + postFix
                                                                                                : SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES
                                                                                                  + postFix));
               ExtendedAttribute eans = eas.getFirstExtendedAttributeForName(eansn);
               String nms = "";
               if (eans != null) {
                  nms = eans.getVValue();
               }
               ((WfAttachments) attr).createStructure(eaval, nms);
            } else {
               if (eaname.equals(SharkConstants.SMTP_MODE) || eaname.equals(SharkConstants.SMTP_EXECUTION_MODE)
                   || eaname.equals(SharkConstants.SMTP_GROUP_EMAIL_ONLY) || eaname.equals(SharkConstants.SMTP_SIGNED_EMAIL)
                   || eaname.equals(SharkConstants.SMTP_RECIPIENT_PARTICIPANT) || eaname.equals(SharkConstants.SMTP_RECIPIENT_VARIABLE)) {
                  pc++;
               }
               attr.setValue(eaval);
            }
            hasAny = true;
         }
      }
      getConfigureEmailAttribute().setValue(String.valueOf(hasAny || eaExtension != null));
      int toCompNo = (isForAct ? 4 : 3) + (isForErrorHandling || isForDeadlineHandling || isForLimitHandling ? 1 : 0);
      isPersisted = pc >= toCompNo;
   }

   public boolean isPersisted() {
      return isPersisted;
   }

   public void setPersisted(boolean isPersisted) {
      this.isPersisted = isPersisted;
      if (isPersisted) {
         eaExtension = getExtensionAttribute().toValue();
      }
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

   public boolean isForDeadlineHandling() {
      return isForDeadlineHandling;
   }

   public boolean isForLimitHandling() {
      return isForLimitHandling;
   }

   public boolean isForNonDefaultDeadlineHandling() {
      return eaExtension != null;
   }

   private static String getMyName(ExtendedAttributes eas,
                                   boolean isForAct,
                                   boolean isForErrorHandling,
                                   boolean isForDeadlineHandling,
                                   boolean isForLimitHandling,
                                   String specialExtension) {
      String myName = "";
      if (XMLUtil.getActivity(eas) == null && isForAct) {
         myName = "Default";
         if (!isForErrorHandling && !isForDeadlineHandling) {
            myName += "Activity";
         }
      } else if (XMLUtil.getWorkflowProcess(eas) == null) {
         myName = "Default";
         if (!isForErrorHandling) {
            myName += "Process";
         }
      }
      if (isForErrorHandling) {
         myName += "Error";
      } else if (isForDeadlineHandling) {
         myName += "Deadline";
      } else if (isForLimitHandling) {
         myName += "Limit";
      }
      myName += "EmailConfiguration";
      // String mysName = ((XMLUtil.getActivity(eas) == null && isForAct) ? "Default"
      // + ((isForErrorHandling) ? ""
      // : "Activity")
      // : (XMLUtil.getWorkflowProcess(eas) == null) ? "Default"
      // + ((isForErrorHandling) ? ""
      // : "Process")
      // : "")
      // + (isForErrorHandling ? "Error" : (isForLimitHandling ? "Limit"
      // : ""))
      // + "EmailConfiguration";
      return myName;
   }

   protected String getPostfix() {
      String postFix = "";
      if (isForAct) {
         if (eaExtension != null) {
            XMLElement en = super.get("ExceptionName");
            if (en != null && !en.toValue().equals("")) {
               postFix = "_" + en.toValue();
            }
         } else if (!isForErrorHandling && !isForDeadlineHandling) {
            postFix = "_ACTIVITY";
         }
      } else {
         if (!isForErrorHandling && !isForDeadlineHandling) {
            postFix = "_PROCESS";
         }
      }
      return postFix;
   }

   public boolean isForModalDialog() {
      return true;
   }

   public boolean canSetValue() {
      return canSetValue;
   }

   public void setCanSetValue(boolean csv) {
      canSetValue = csv;
   }
}
