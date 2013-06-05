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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.enhydra.jxpdl.StandardPackageValidator;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.XMLValidationError;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.XPDLValidationErrorIds;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.Application;
import org.enhydra.jxpdl.elements.ArrayType;
import org.enhydra.jxpdl.elements.BasicType;
import org.enhydra.jxpdl.elements.DataField;
import org.enhydra.jxpdl.elements.EnumerationType;
import org.enhydra.jxpdl.elements.ExceptionName;
import org.enhydra.jxpdl.elements.ExpressionType;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;
import org.enhydra.jxpdl.elements.FormalParameter;
import org.enhydra.jxpdl.elements.FormalParameters;
import org.enhydra.jxpdl.elements.InitialValue;
import org.enhydra.jxpdl.elements.ListType;
import org.enhydra.jxpdl.elements.Package;
import org.enhydra.jxpdl.elements.Participant;
import org.enhydra.jxpdl.elements.Performer;
import org.enhydra.jxpdl.elements.RecordType;
import org.enhydra.jxpdl.elements.SchemaType;
import org.enhydra.jxpdl.elements.UnionType;
import org.enhydra.jxpdl.elements.WorkflowProcess;
import org.enhydra.jxpdl.utilities.SequencedHashMap;

/**
 * Special shark validation - to determine if the package is 'shark' valid. It extends the
 * standard package validator to add some additional restrictions.
 * 
 * @author Sasa Bojanic
 */
public abstract class SharkPackageValidator extends StandardPackageValidator {

   public SharkPackageValidator() {
   }

   public SharkPackageValidator(Properties settings) throws Exception {
      super(settings);
   }

   public void validateElement(XMLAttribute el, List existingErrors, boolean fullCheck) {
      super.validateElement(el, existingErrors, fullCheck);

      XMLElement parent = el.getParent();
      if (el.toName().equals("Type") && parent instanceof BasicType) {
         String choosenSubType = el.toValue();
         if (choosenSubType.equals(XPDLConstants.BASIC_TYPE_PERFORMER)
             || choosenSubType.equals(XPDLConstants.BASIC_TYPE_REFERENCE)) {
            XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                             XMLValidationError.SUB_TYPE_LOGIC,
                                                             SharkValidationErrorIds.ERROR_UNSUPPORTED_DATA_TYPE,
                                                             el.toName(),
                                                             el);
            existingErrors.add(verr);
         }
      }
      if (parent instanceof ExtendedAttribute
          && (parent.getParent().getParent() instanceof Activity
              || parent.getParent().getParent() instanceof WorkflowProcess || parent.getParent()
             .getParent() instanceof Package)) {
         boolean isWarning = false;
         boolean isAct = parent.getParent().getParent() instanceof Activity;
         String postfixProc = "_PROCESS";
         String postfixAct = "_ACTIVITY";
         if (el.toName().equals("Name")) {
            if (((el.toValue().equals(SharkConstants.VTP_UPDATE) || el.toValue()
               .equals(SharkConstants.VTP_VIEW)) && isAct)
                || (!isAct && (el.toValue()
                   .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES
                           + postfixProc)
                               || el.toValue()
                                  .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS
                                          + postfixProc)
                               || el.toValue()
                                  .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS
                                          + postfixProc)
                               || el.toValue()
                                  .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT
                                          + postfixProc)
                               || el.toValue()
                                  .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT
                                          + postfixProc)
                               || el.toValue()
                                  .equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES
                                          + postfixProc)
                               || el.toValue()
                                  .equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENTS
                                          + postfixProc)
                               || el.toValue()
                                  .equals(SharkConstants.SMTP_LIMIT_HANDLER_DM_ATTACHMENTS
                                          + postfixProc)
                               || el.toValue()
                                  .equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT
                                          + postfixProc) || el.toValue()
                   .equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT + postfixProc)))
                || el.toValue()
                   .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES
                           + postfixAct)
                || el.toValue()
                   .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS
                           + postfixAct)
                || el.toValue()
                   .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS
                           + postfixAct)
                || el.toValue().equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT
                                       + postfixAct)
                || el.toValue().equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT
                                       + postfixAct)
                || el.toValue()
                   .equals(SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENT_NAMES)
                || el.toValue().equals(SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENTS)
                || el.toValue()
                   .equals(SharkConstants.EA_SMTP_ERROR_HANDLER_DM_ATTACHMENTS)
                || el.toValue().equals(SharkConstants.EA_SMTP_ERROR_HANDLER_SUBJECT)
                || el.toValue().equals(SharkConstants.EA_SMTP_ERROR_HANDLER_CONTENT)
                || el.toValue()
                   .equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENT_NAMES)
                || el.toValue()
                   .equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENTS)
                || el.toValue()
                   .equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_DM_ATTACHMENTS)
                || el.toValue().equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_SUBJECT)
                || el.toValue().equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_CONTENT)
                || (isAct && (el.toValue()
                   .startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENT_NAMES)
                              || el.toValue()
                                 .startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENTS)
                              || el.toValue()
                                 .startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_DM_ATTACHMENTS)
                              || el.toValue()
                                 .startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_SUBJECT) || el.toValue()
                   .startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_CONTENT)))
                || el.toValue().equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES
                                       + postfixAct)
                || el.toValue().equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENTS
                                       + postfixAct)
                || el.toValue().equals(SharkConstants.SMTP_LIMIT_HANDLER_DM_ATTACHMENTS
                                       + postfixAct)
                || el.toValue().equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT
                                       + postfixAct)
                || el.toValue().equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT
                                       + postfixAct)
                || (!isAct && el.toValue()
                   .startsWith(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX))) {

               isWarning = el.toValue().equals(SharkConstants.VTP_UPDATE)
                           || el.toValue().equals(SharkConstants.VTP_VIEW)
                           || el.toValue()
                              .startsWith(SharkConstants.SMTP_LIMIT_HANDLER_PREFIX);

               ExtendedAttribute ea = (ExtendedAttribute) parent;
               WorkflowProcess wp = XMLUtil.getWorkflowProcess(ea);
               Map m = null;
               if (wp != null) {
                  m = wp.getAllVariables();
               } else {
                  m = XMLUtil.getPossibleVariables(ea);
               }
               List<String> vals = new ArrayList<String>();
               List<String> sysvals = new ArrayList<String>();
               List<String> csvals = new ArrayList<String>();
               List<String> xpdlsvals = new ArrayList<String>();
               if (ea.getName()
                  .startsWith(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES)
                   || ea.getName()
                      .startsWith(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS)
                   || ea.getName()
                      .startsWith(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS)
                   || ea.getName()
                      .equals(SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENT_NAMES)
                   || ea.getName()
                      .equals(SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENTS)
                   || ea.getName()
                      .equals(SharkConstants.EA_SMTP_ERROR_HANDLER_DM_ATTACHMENTS)
                   || ea.getName()
                      .startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENT_NAMES)
                   || ea.getName()
                      .startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENTS)
                   || ea.getName()
                      .startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_DM_ATTACHMENTS)
                   || ea.getName()
                      .startsWith(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES)
                   || ea.getName()
                      .startsWith(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENTS)
                   || ea.getName()
                      .startsWith(SharkConstants.SMTP_LIMIT_HANDLER_DM_ATTACHMENTS)) {
                  WfVariables vars = new WfVariables((XMLComplexElement) parent.getParent()
                                                        .getParent(),
                                                     ea.getName(),
                                                     null,
                                                     ",",
                                                     false);
                  vars.createStructure(ea.getVValue());
                  List els = vars.toElements();
                  for (int i = 0; i < els.size(); i++) {
                     WfVariable v = (WfVariable) els.get(i);
                     vals.add(v.getId());
                  }
               } else if (ea.getName()
                  .startsWith(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT)
                          || ea.getName()
                             .startsWith(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT)
                          || ea.getName()
                             .equals(SharkConstants.EA_SMTP_ERROR_HANDLER_SUBJECT)
                          || ea.getName()
                             .equals(SharkConstants.EA_SMTP_ERROR_HANDLER_CONTENT)
                          || ea.getName()
                             .startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_SUBJECT)
                          || ea.getName()
                             .startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_CONTENT)
                          || ea.getName()
                             .startsWith(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT)
                          || ea.getName()
                             .startsWith(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT)
                          || (!isAct && ea.getName()
                             .startsWith(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX))) {
                  vals = getPossiblePlaceholderVariables(ea.getVValue(),
                                                         SharkConstants.PROCESS_VARIABLE_PLACEHOLDER_PREFIX);
                  sysvals = getPossiblePlaceholderVariables(ea.getVValue(), "");
                  csvals = getPossiblePlaceholderVariables(ea.getVValue(),
                                                           SharkConstants.CONFIG_STRING_PLACEHOLDER_PREFIX);
                  xpdlsvals = getPossiblePlaceholderVariables(ea.getVValue(),
                                                              SharkConstants.XPDL_STRING_PLACEHOLDER_PREFIX);
               } else {
                  vals.add(ea.getVValue());
               }
               for (int i = 0; i < vals.size(); i++) {
                  String v = vals.get(i);
                  if (m.get(v) == null
                      && !((ea.getName()
                         .startsWith(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES)
                            || ea.getName()
                               .equals(SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENT_NAMES)
                            || ea.getName()
                               .startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENT_NAMES) || ea.getName()
                         .startsWith(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES)) && ((v.startsWith("\"") && v.endsWith("\"")) || v.equals("")))) {
                     boolean allowUndefinedVariables = allowsUndefinedVariables(wp, false);
                     boolean isWPLevel = XMLUtil.getWorkflowProcess(el) != null;

                     XMLValidationError verr = new XMLValidationError((isWarning
                                                                       || allowUndefinedVariables || !isWPLevel) ? XMLValidationError.TYPE_WARNING
                                                                                                                : XMLValidationError.TYPE_ERROR,
                                                                      XMLValidationError.SUB_TYPE_LOGIC,
                                                                      (isWarning
                                                                       || allowUndefinedVariables || !isWPLevel) ? SharkValidationErrorIds.WARNING_NON_EXISTING_VARIABLE_REFERENCE
                                                                                                                : XPDLValidationErrorIds.ERROR_NON_EXISTING_VARIABLE_REFERENCE,
                                                                      v,
                                                                      ea.get("Value"));
                     existingErrors.add(verr);
                     if (!fullCheck) {
                        return;
                     }
                  }
               }
               for (int i = 0; i < sysvals.size(); i++) {
                  String v = sysvals.get(i);
                  if (!SharkConstants.possibleSystemVariables.contains(v)) {
                     XMLValidationError verr = new XMLValidationError(isWarning ? XMLValidationError.TYPE_WARNING
                                                                               : XMLValidationError.TYPE_ERROR,
                                                                      XMLValidationError.SUB_TYPE_LOGIC,
                                                                      SharkValidationErrorIds.ERROR_NON_EXISTING_SYSTEM_VARIABLE_REFERENCE,
                                                                      v,
                                                                      ea.get("Value"));
                     existingErrors.add(verr);
                     if (!fullCheck) {
                        return;
                     }
                  }
               }
               for (int i = 0; i < csvals.size(); i++) {
                  String v = csvals.get(i);
                  if (!getConfigStringChoices().contains(v)) {
                     XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_WARNING,
                                                                      XMLValidationError.SUB_TYPE_LOGIC,
                                                                      SharkValidationErrorIds.WARNING_NON_EXISTING_CONFIG_STRING_VARIABLE_REFERENCE,
                                                                      v,
                                                                      ea.get("Value"));
                     existingErrors.add(verr);
                     if (!fullCheck) {
                        return;
                     }
                  }
               }
               Properties psxpdl = getPossibleXPDLStringVariables(el, true);
               for (int i = 0; i < xpdlsvals.size(); i++) {
                  String v = xpdlsvals.get(i);
                  if (!psxpdl.containsKey(v)) {
                     boolean isWPLevel = XMLUtil.getWorkflowProcess(el) != null;
                     XMLValidationError verr = new XMLValidationError(isWPLevel
                                                                            && !isWarning ? XMLValidationError.TYPE_ERROR
                                                                                         : XMLValidationError.TYPE_WARNING,
                                                                      XMLValidationError.SUB_TYPE_LOGIC,
                                                                      isWPLevel
                                                                            && !isWarning ? SharkValidationErrorIds.ERROR_NON_EXISTING_SHARK_STRING_VARIABLE_REFERENCE
                                                                                         : SharkValidationErrorIds.WARNING_NON_EXISTING_SHARK_STRING_VARIABLE_REFERENCE,
                                                                      v,
                                                                      ea.get("Value"));
                     existingErrors.add(verr);
                     if (!fullCheck) {
                        return;
                     }
                  }
               }
            } else if (el.toValue()
               .equals(SharkConstants.EA_SMTP_ERROR_HANDLER_RECIPIENT_PARTICIPANT)
                       || el.toValue()
                          .equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_RECIPIENT_PARTICIPANT)
                       || (isAct && el.toValue()
                          .startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_RECIPIENT_PARTICIPANT))
                       || el.toValue()
                          .equals(SharkConstants.SMTP_LIMIT_HANDLER_RECIPIENT_PARTICIPANT
                                  + postfixProc)
                       || el.toValue()
                          .equals(SharkConstants.SMTP_LIMIT_HANDLER_RECIPIENT_PARTICIPANT
                                  + postfixAct)) {
               ExtendedAttribute ea = (ExtendedAttribute) parent;
               XMLComplexElement pkgOrWp = XMLUtil.getWorkflowProcess(el);
               Map choices = null;
               if (pkgOrWp == null) {
                  pkgOrWp = XMLUtil.getPackage(el);
                  choices = XMLUtil.getPossibleParticipants((Package) pkgOrWp,
                                                            xmlInterface);
               } else {
                  choices = XMLUtil.getPossibleParticipants((WorkflowProcess) pkgOrWp,
                                                            xmlInterface);
               }

               if (!choices.containsKey(ea.getVValue())) {
                  isWarning = el.toValue()
                     .startsWith(SharkConstants.SMTP_LIMIT_HANDLER_PREFIX);
                  XMLValidationError verr = new XMLValidationError(isWarning ? XMLValidationError.TYPE_WARNING
                                                                            : XMLValidationError.TYPE_ERROR,
                                                                   XMLValidationError.SUB_TYPE_LOGIC,
                                                                   XPDLValidationErrorIds.ERROR_NON_EXISTING_PARTICIPANT_REFERENCE,
                                                                   ea.getVValue(),
                                                                   ea.get("Value"));
                  existingErrors.add(verr);
                  if (!fullCheck) {
                     return;
                  }
               }
            }
         }
      }
      if ((el.toName().equals("StartMode") || el.toName().equals("FinishMode"))
          && el.toValue().equals(XPDLConstants.ACTIVITY_MODE_MANUAL)
          && parent instanceof Activity) {
         Activity act = (Activity) parent;
         if (act.getActivityType() != XPDLConstants.ACTIVITY_TYPE_TASK_APPLICATION
             || el.toName().equals("FinishMode"))
            return;

         String performer = act.getFirstPerformer();
         boolean isSystemParticipantPerformer = false;
         Participant p = null;
         p = XMLUtil.findParticipant(xmlInterface,
                                     XMLUtil.getWorkflowProcess(act),
                                     performer);
         if (p != null) {
            String participantType = p.getParticipantType().getType();
            if (participantType.equals(XPDLConstants.PARTICIPANT_TYPE_SYSTEM)) {
               isSystemParticipantPerformer = true;
            }
         }
         if (isSystemParticipantPerformer) {
            XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                             XMLValidationError.SUB_TYPE_LOGIC,
                                                             SharkValidationErrorIds.ERROR_MANUAL_START_MODE_FOR_TOOL_ACTIVITY_WITH_SYSTEM_PARTICIPANT_PERFORMER,
                                                             act.getId(),
                                                             el);
            existingErrors.add(verr);
         }
      }
   }

   protected boolean isHandlingDeadlines(Activity el) {
      boolean isHandling = XMLUtil.getExceptionalOutgoingTransitions(el).size() > 0;
      if (!isHandling) {
         if (isDeadlineHandlerUsed()) {
            if (new EmailConfigurationElement(el.getExtendedAttributes(),
                                              true,
                                              false,
                                              true,
                                              false,
                                              null).isPersisted()) {
               isHandling = true;
            } else if (new DeadlineEmailConfigurationElements(el.getExtendedAttributes()).size() > 0) {
               isHandling = true;
            } else if (new EmailConfigurationElement(XMLUtil.getWorkflowProcess(el)
               .getExtendedAttributes(), true, false, true, false, null).isPersisted()) {
               isHandling = true;
            } else if (new EmailConfigurationElement(XMLUtil.getPackage(el)
               .getExtendedAttributes(), true, false, true, false, null).isPersisted()) {
               isHandling = true;
            }
         }
      }
      return isHandling;
   }

   public void validateElement(Application el, List existingErrors, boolean fullCheck) {
      super.validateElement(el, existingErrors, fullCheck);

      String taName = null;
      ExtendedAttribute eataname = el.getExtendedAttributes()
         .getFirstExtendedAttributeForName(SharkConstants.EA_TOOL_AGENT_CLASS);
      if (eataname != null) {
         taName = eataname.getVValue();
      }
      if (taName == null || taName.trim().equals("")) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_WARNING,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          SharkValidationErrorIds.WARNING_TOOL_AGENT_CLASS_NOT_DEFINED,
                                                          "",
                                                          el);
         existingErrors.add(verr);
      } else {
         validateToolAgent(taName, el, existingErrors, fullCheck);
      }
   }

   public void validateElement(ArrayType el, List existingErrors, boolean fullCheck) {
      XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                       XMLValidationError.SUB_TYPE_LOGIC,
                                                       SharkValidationErrorIds.ERROR_UNSUPPORTED_DATA_TYPE,
                                                       el.toName(),
                                                       el);
      existingErrors.add(verr);
   }

   public void validateElement(EnumerationType el, List existingErrors, boolean fullCheck) {
      XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                       XMLValidationError.SUB_TYPE_LOGIC,
                                                       SharkValidationErrorIds.ERROR_UNSUPPORTED_DATA_TYPE,
                                                       el.toName(),
                                                       el);
      existingErrors.add(verr);
   }

   public void validateElement(ExceptionName el, List existingErrors, boolean fullCheck) {
      super.validateElement(el, existingErrors, fullCheck);
      if (!isElementLengthOK(el)) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          XPDLValidationErrorIds.ERROR_UNALLOWED_LENGTH,
                                                          el.toName(),
                                                          el);
         existingErrors.add(verr);
      }
   }

   protected boolean isDeadlineExceptionValid(ExceptionName el) {
      boolean isValid = super.isDeadlineExceptionValid(el);
      if (!isValid) {
         if (isDeadlineHandlerUsed()) {
            Activity act = XMLUtil.getActivity(el);
            if (new EmailConfigurationElement(act.getExtendedAttributes(),
                                              true,
                                              false,
                                              true,
                                              false,
                                              null).isPersisted()) {
               isValid = true;
            } else if (new EmailConfigurationElement(XMLUtil.getWorkflowProcess(el)
               .getExtendedAttributes(), true, false, true, false, null).isPersisted()) {
               isValid = true;
            } else if (new EmailConfigurationElement(XMLUtil.getPackage(el)
               .getExtendedAttributes(), true, false, true, false, null).isPersisted()) {
               isValid = true;
            } else {
               DeadlineEmailConfigurationElements deces = new DeadlineEmailConfigurationElements(act.getExtendedAttributes());
               if (deces.size() > 0) {
                  Iterator it = deces.toElements().iterator();
                  while (it.hasNext()) {
                     EmailConfigurationElement ece = (EmailConfigurationElement) it.next();
                     if (ece.getExtensionAttribute().toValue().equals(el.toValue())) {
                        isValid = true;
                        break;
                     }
                  }
               }
            }
         }
      }
      return isValid;
   }

   public void validateElement(ExpressionType el, List existingErrors, boolean fullCheck) {
      if (!el.getScriptType().equals("")) {
         validateScript(el, existingErrors, fullCheck);
      }
   }

   public void validateElement(ListType el, List existingErrors, boolean fullCheck) {
      XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                       XMLValidationError.SUB_TYPE_LOGIC,
                                                       SharkValidationErrorIds.ERROR_UNSUPPORTED_DATA_TYPE,
                                                       el.toName(),
                                                       el);
      existingErrors.add(verr);
   }

   public void validateElement(org.enhydra.jxpdl.elements.Package el,
                               List existingErrors,
                               boolean fullCheck) {
      super.validateElement(el, existingErrors, fullCheck);
      if (existingErrors.size() == 0 || fullCheck) {
         validateScript(el.getScript(), existingErrors, fullCheck);
      }
      if (!fullCheck && existingErrors.size() > 0) {
         return;
      }
      checkVariableCircularReferences(el, existingErrors, fullCheck);
   }

   public void validateElement(Performer el, List existingErrors, boolean fullCheck) {
      super.validateElement(el, existingErrors, fullCheck);
      if (fullCheck || existingErrors.size() == 0) {
         // check performer
         Activity act = XMLUtil.getActivity(el);
         if (act != null && act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_NO) {
            String performer = el.toValue();
            Participant p = null;
            WorkflowProcess wp = XMLUtil.getWorkflowProcess(act);
            p = XMLUtil.findParticipant(xmlInterface, wp, performer);
            if (p != null) {
               String participantType = p.getParticipantType().getType();
               if (participantType.equals(XPDLConstants.PARTICIPANT_TYPE_SYSTEM)) {
                  XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                                   XMLValidationError.SUB_TYPE_LOGIC,
                                                                   SharkValidationErrorIds.ERROR_SYSTEM_PARTICIPANT_PERFORMER_FOR_NO_IMPLEMENTATION_ACTIVITY,
                                                                   act.getId(),
                                                                   el);
                  existingErrors.add(verr);

               }
            }
         }
      }

   }

   public void validateElement(RecordType el, List existingErrors, boolean fullCheck) {
      XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                       XMLValidationError.SUB_TYPE_LOGIC,
                                                       SharkValidationErrorIds.ERROR_UNSUPPORTED_DATA_TYPE,
                                                       el.toName(),
                                                       el);
      existingErrors.add(verr);
   }

   public void validateElement(UnionType el, List existingErrors, boolean fullCheck) {
      XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                       XMLValidationError.SUB_TYPE_LOGIC,
                                                       SharkValidationErrorIds.ERROR_UNSUPPORTED_DATA_TYPE,
                                                       el.toName(),
                                                       el);
      existingErrors.add(verr);
   }

   public void validateElement(WorkflowProcess el, List existingErrors, boolean fullCheck) {
      super.validateElement(el, existingErrors, fullCheck);
      if (!fullCheck && existingErrors.size() > 0) {
         return;
      }
      checkVariableCircularReferences(el, existingErrors, fullCheck);
   }

   protected void checkVariableCircularReferences(XMLComplexElement pkgOrWp,
                                                  List existingErrors,
                                                  boolean fullCheck) {
      Map vars = new HashMap();
      if (pkgOrWp instanceof WorkflowProcess) {
         Iterator it = ((WorkflowProcess) pkgOrWp).getDataFields()
            .toElements()
            .iterator();
         while (it.hasNext()) {
            DataField df = (DataField) it.next();
            if (!vars.containsKey(df.getId())) {
               vars.put(df.getId(), df);
            }
         }
         it = ((WorkflowProcess) pkgOrWp).getFormalParameters().toElements().iterator();
         while (it.hasNext()) {
            FormalParameter fp = (FormalParameter) it.next();
            if (!vars.containsKey(fp.getId())) {
               vars.put(fp.getId(), fp);
            }
         }
      } else {
         Iterator it = ((Package) pkgOrWp).getDataFields().toElements().iterator();
         while (it.hasNext()) {
            DataField df = (DataField) it.next();
            if (!vars.containsKey(df.getId())) {
               vars.put(df.getId(), df);
            }
         }
      }
      Iterator<Map.Entry<String, String>> it = vars.entrySet().iterator();
      Map<String, String> dynamicScriptVariablesContext = new HashMap();
      Map<String, String> otherVariablesContext = new HashMap();
      while (it.hasNext()) {
         Map.Entry me = (Map.Entry) it.next();
         String id = (String) me.getKey();
         XMLCollectionElement dfOrFp = (XMLCollectionElement) me.getValue();
         String iv = dfOrFp.get("InitialValue").toValue();
         if (isDynamicScriptVariable(dfOrFp, id)) {
            dynamicScriptVariablesContext.put(id, iv);
         } else {
            otherVariablesContext.put(id, iv);
         }
      }
      try {
         XMLUtil.determineVariableEvaluationOrder(dynamicScriptVariablesContext);
      } catch (Exception ex) {
         String excMsg = ex.getMessage();
         handleCircularReferenceExceptionMessage(vars, excMsg, existingErrors, true);
      }
      if (!fullCheck && existingErrors.size() > 0) {
         return;
      }
      try {
         XMLUtil.determineVariableEvaluationOrder(otherVariablesContext);
      } catch (Exception ex) {
         String excMsg = ex.getMessage();
         handleCircularReferenceExceptionMessage(vars, excMsg, existingErrors, false);
      }
      if (!fullCheck && existingErrors.size() > 0) {
         return;
      }

      // check if "normal" variable is referencing dynamic script variable - this is
      // forbidden
      it = otherVariablesContext.entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry<String, String> me = it.next();
         String id = me.getKey();
         String iv = me.getValue();

         Iterator<String> it2 = dynamicScriptVariablesContext.keySet().iterator();
         while (it2.hasNext()) {
            String dsvid = it2.next();
            if (XMLUtil.getUsingPositions(iv,
                                          dsvid,
                                          dynamicScriptVariablesContext,
                                          true,
                                          true).size() > 0) {
               XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                                XMLValidationError.SUB_TYPE_LOGIC,
                                                                SharkValidationErrorIds.ERROR_VARIABLE_INITIAL_VALUE_DYNAMICSCRIPT_VARIABLE_REFERENCES_NOT_ALLOWED,
                                                                id,
                                                                ((XMLCollectionElement) vars.get(id)).get("InitialValue"));
               existingErrors.add(verr);
               if (!fullCheck) {
                  return;
               }
            }
         }
      }

      if (!fullCheck && existingErrors.size() > 0) {
         return;
      }
      // check circular-references inside Shark XPDL String variables
      Map<String, String> props = new HashMap(getPossibleXPDLStringVariables(pkgOrWp,
                                                                             false));
      Map<String, String> placeholderProps = new HashMap<String, String>();
      Iterator<Map.Entry<String, String>> ite = props.entrySet().iterator();
      while (ite.hasNext()) {
         Map.Entry<String, String> me = ite.next();
         placeholderProps.put("{"
                                    + SharkConstants.XPDL_STRING_PLACEHOLDER_PREFIX
                                    + me.getKey() + "}",
                              me.getValue());
      }
      try {
         XMLUtil.determineVariableEvaluationOrder(placeholderProps);
      } catch (Exception ex) {
         String excMsg = ex.getMessage();
         Map m1 = getPossibleXPDLStringVariablesEAValues(pkgOrWp, false);
         Map m2 = new HashMap();
         Iterator<Map.Entry<String, XMLElement>> ite2 = m1.entrySet().iterator();
         while (ite2.hasNext()) {
            Map.Entry me = ite2.next();
            m2.put("{"
                         + SharkConstants.XPDL_STRING_PLACEHOLDER_PREFIX + me.getKey()
                         + "}",
                   me.getValue());
         }
         handleCircularReferenceExceptionMessage(m2, excMsg, existingErrors, false);
      }

   }

   protected void handleCircularReferenceExceptionMessage(Map vars,
                                                          String exceptionMessage,
                                                          List existingErrors,
                                                          boolean dynamicScriptVars) {
      List varIds = new ArrayList();
      if (exceptionMessage.startsWith(XMLUtil.EXCEPTION_PREFIX_SELF_REFERENCES_NOT_ALLOWED)) {
         String varId = exceptionMessage.substring(XMLUtil.EXCEPTION_PREFIX_SELF_REFERENCES_NOT_ALLOWED.length())
            .trim();
         XMLElement eel = (XMLElement) vars.get(varId);
         if (eel instanceof XMLCollectionElement) {
            eel = ((XMLCollectionElement) eel).get("InitialValue");
         } else {
            varId = varId.substring(SharkConstants.XPDL_STRING_PLACEHOLDER_PREFIX.length() + 1,
                                    varId.length() - 1);
         }
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          dynamicScriptVars ? SharkValidationErrorIds.ERROR_DYNAMICSCRIPT_VARIABLE_SELF_REFERENCES_NOT_ALLOWED
                                                                           : ((eel instanceof InitialValue) ? SharkValidationErrorIds.ERROR_VARIABLE_INITIAL_VALUE_SELF_REFERENCES_NOT_ALLOWED
                                                                                                           : SharkValidationErrorIds.ERROR_SHARK_STRING_VARIABLE_SELF_REFERENCES_NOT_ALLOWED),
                                                          varId,
                                                          eel);
         existingErrors.add(verr);
      } else if (exceptionMessage.startsWith(XMLUtil.EXCEPTION_PREFIX_CIRCULAR_REFERENCES_NOT_ALLOWED)) {
         String[] vids = exceptionMessage.substring(XMLUtil.EXCEPTION_PREFIX_CIRCULAR_REFERENCES_NOT_ALLOWED.length())
            .trim()
            .split(",");
         for (int i = 0; i < vids.length; i++) {
            String varId = vids[i].trim();
            XMLElement eel = (XMLElement) vars.get(varId);
            if (eel instanceof XMLCollectionElement) {
               eel = ((XMLCollectionElement) eel).get("InitialValue");
            }
            XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                             XMLValidationError.SUB_TYPE_LOGIC,
                                                             dynamicScriptVars ? SharkValidationErrorIds.ERROR_DYNAMICSCRIPT_VARIABLE_CIRCULAR_REFERENCES_NOT_ALLOWED
                                                                              : (eel instanceof InitialValue) ? SharkValidationErrorIds.ERROR_VARIABLE_INITIAL_VALUE_CIRCULAR_REFERENCES_NOT_ALLOWED
                                                                                                             : SharkValidationErrorIds.ERROR_SHARK_STRING_VARIABLE_CIRCULAR_REFERENCES_NOT_ALLOWED,
                                                             varId,
                                                             eel);
            existingErrors.add(verr);
         }
      } else if (exceptionMessage.startsWith(XMLUtil.EXCEPTION_PREFIX_IMPLICIT_CIRCULAR_REFERENCES_NOT_ALLOWED)) {
         String varId = exceptionMessage.substring(XMLUtil.EXCEPTION_PREFIX_IMPLICIT_CIRCULAR_REFERENCES_NOT_ALLOWED.length())
            .trim();
         XMLElement eel = (XMLElement) vars.get(varId);
         if (eel instanceof XMLCollectionElement) {
            eel = ((XMLCollectionElement) eel).get("InitialValue");
         }
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          dynamicScriptVars ? SharkValidationErrorIds.ERROR_DYNAMICSCRIPT_VARIABLE_IMPLICIT_CIRCULAR_REFERENCES_NOT_ALLOWED
                                                                           : (eel instanceof InitialValue) ? SharkValidationErrorIds.ERROR_VARIABLE_INITIAL_VALUE_IMPLICIT_CIRCULAR_REFERENCES_NOT_ALLOWED
                                                                                                          : SharkValidationErrorIds.ERROR_SHARK_STRING_VARIABLE_IMPLICIT_CIRCULAR_REFERENCES_NOT_ALLOWED,
                                                          varId,
                                                          eel);
         existingErrors.add(verr);
      }
   }

   protected boolean isDynamicScriptVariable(XMLCollectionElement dfOrFp, String varId) {
      if (dfOrFp instanceof FormalParameter) {
         return false;
      }
      if (dfOrFp == null) {
         return false;
      }
      XMLComplexElement parent = XMLUtil.getWorkflowProcess(dfOrFp);
      if (parent == null) {
         parent = XMLUtil.getPackage(dfOrFp);
      }
      ExtendedAttribute ea = ((ExtendedAttributes) dfOrFp.get("ExtendedAttributes")).getFirstExtendedAttributeForName(SharkConstants.EA_DYNAMICSCRIPT);
      boolean isDynamicScriptVariable = ea != null
                                        && ea.getVValue().equalsIgnoreCase("true");
      return isDynamicScriptVariable;
   }

   public boolean isElementLengthOK(XMLElement el) {
      if (el instanceof XMLAttribute
          && el.toName().equals("Name")
          && ((el.getParent() instanceof WorkflowProcess && el.toValue().length() > 90) || (el.getParent() instanceof Activity && el.toValue()
             .length() > 254))) {
         return false;
      }
      if (el instanceof XMLAttribute
          && el.toName().equals("Id")
          && el.toValue().length() > 90
          && (el.getParent() instanceof org.enhydra.jxpdl.elements.Package
              || el.getParent() instanceof WorkflowProcess || el.getParent() instanceof Activity)) {
         return false;
      }
      if (el instanceof XMLAttribute
          && el.toName().equals("Id")
          && el.toValue().length() > 100
          && (el.getParent() instanceof DataField || el.getParent() instanceof FormalParameter)) {
         return false;
      }

      if (el instanceof ExceptionName && el.toValue().length() > 100) {
         return false;
      }
      return super.isElementLengthOK(el);
   }

   protected boolean isRemoteSubflowIdOK(String subflowID) {
      try {
         new URL(subflowID);
      } catch (Throwable thr) {
         return false;
      }
      return true;
   }

   protected boolean isIdUnique(XMLCollectionElement newEl) {
      boolean ret = super.isIdUnique(newEl);
      if (newEl instanceof DataField || newEl instanceof FormalParameter) {
         String id = newEl.getId();
         if (getConfigStringChoices().contains(id)
             || SharkConstants.possibleSystemVariables.contains(id)) {
            ret = false;
         }
      }
      return ret;
   }

   protected Map getExtendedChoices(XMLElement el) {
      SequencedHashMap map = XMLUtil.getPossibleVariables(el);

      if (!(el instanceof InitialValue)) {
         List<String> csc = getConfigStringChoices();
         for (int i = 0; i < csc.size(); i++) {
            String id = csc.get(i);
            DataField df = new DataField(null);
            df.setId(id);
            df.getDataType().getDataTypes().setBasicType();
            df.getDataType().getDataTypes().getBasicType().setTypeSTRING();
            map.put(id, df);
         }

         List<String> xpdlsc = getPossibleXPDLStringVariableNames(el, true);
         for (int i = 0; i < xpdlsc.size(); i++) {
            String id = xpdlsc.get(i);
            DataField df = new DataField(null);
            df.setId(id);
            df.getDataType().getDataTypes().setBasicType();
            df.getDataType().getDataTypes().getBasicType().setTypeSTRING();
            map.put(id, df);
         }
      }

      boolean isForActivity = XMLUtil.getActivity(el) != null;
      for (int i = 0; i < SharkConstants.possibleSystemVariables.size(); i++) {
         String id = SharkConstants.possibleSystemVariables.get(i);
         if (id.startsWith("shark_activity_") && !isForActivity) {
            continue;
         }
         DataField df = new DataField(null);
         df.setId(id);
         if (!SharkConstants.SHARK_SESSION_HANDLE.equals(id)) {
            df.getDataType().getDataTypes().setBasicType();
            if (!id.endsWith("_time")) {
               df.getDataType().getDataTypes().getBasicType().setTypeSTRING();
            } else {
               df.getDataType().getDataTypes().getBasicType().setTypeDATETIME();
            }
         } else {
            df.getDataType().getDataTypes().setExternalReference();
            df.getDataType()
               .getDataTypes()
               .getExternalReference()
               .setLocation("org.enhydra.shark.api.client.wfmc.wapi.WMSessionHandle");
         }
         map.put(id, df);
      }

      return map;
   }

   protected Map getActualParameterOrConditionChoices(XMLElement el) {
      return getExtendedChoices(el);
   }

   protected Map getDeadlineConditionChoices(XMLElement el) {
      return getExtendedChoices(el);
   }

   protected Map getPerformerChoices(XMLElement el) {
      Map map = getExtendedChoices(el);

      Map parts = XMLUtil.getPossibleParticipants(XMLUtil.getWorkflowProcess(el),
                                                  xmlInterface);
      Iterator it = parts.entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry me = (Map.Entry) it.next();
         if (map.containsKey(me.getKey()))
            continue;
         DataField df = new DataField(null);
         df.setId(me.getKey().toString());
         df.getDataType().getDataTypes().setBasicType();
         df.getDataType().getDataTypes().getBasicType().setTypeSTRING();
         map.put(me.getKey().toString(), df);
      }

      return map;
   }

   protected Map getInitialValueChoices(XMLElement el) {
      return getExtendedChoices(el);
   }

   protected boolean compareDataTypes(FormalParameter fp,
                                      Class dataTypeClass,
                                      String subType) {
      if (fp != null) {
         XMLElement dt = fp.getDataType().getDataTypes().getChoosen();
         boolean firstMatch = dt.getClass() == dataTypeClass;
         boolean secondMatch = dataTypeClass != BasicType.class
                               || (dt instanceof BasicType && ((BasicType) dt).getType()
                                  .equals(subType));
         return firstMatch && secondMatch;
      }
      return false;
   }

   protected void validateToolAgent(String taName,
                                    Application el,
                                    List existingErrors,
                                    boolean fullCheck) {
      if (taName.equals(SharkConstants.TOOL_AGENT_BEAN_SHELL)
          || taName.equals(SharkConstants.TOOL_AGENT_JAVASCRIPT)) {
         validateToolAgentBeanShellOrJavaScript(el, existingErrors, fullCheck);
      } else if (taName.equals(SharkConstants.TOOL_AGENT_CHECKDOCUMENTFORMATS)) {
         validateToolAgentCheckDocumentFormats(el, existingErrors, fullCheck);
      } else if (taName.equals(SharkConstants.TOOL_AGENT_JAVACLASS)) {
         validateToolAgentJavaClass(el, existingErrors, fullCheck);
      } else if (taName.equals(SharkConstants.TOOL_AGENT_LDAP)) {
         validateToolAgentLDAP(el, existingErrors, fullCheck);
      } else if (taName.equals(SharkConstants.TOOL_AGENT_MAIL)) {
         validateToolAgentMail(el, existingErrors, fullCheck);
      } else if (taName.equals(SharkConstants.TOOL_AGENT_QUARTZ)
                 || taName.equals(SharkConstants.TOOL_AGENT_SCHEDULER)) {
         validateToolAgentQuartzOrScheduler(el, existingErrors, fullCheck);
      } else if (taName.equals(SharkConstants.TOOL_AGENT_RUNTIMEAPPLICATION)) {
         validateToolAgentRuntimeApplication(el, existingErrors, fullCheck);
      } else if (taName.equals(SharkConstants.TOOL_AGENT_SOAP)) {
         validateToolAgentSOAP(el, existingErrors, fullCheck);
      } else if (taName.equals(SharkConstants.TOOL_AGENT_USERGROUP)) {
         validateToolAgentUserGroup(el, existingErrors, fullCheck);
      } else if (taName.equals(SharkConstants.TOOL_AGENT_XPATH)) {
         validateToolAgentXPath(el, existingErrors, fullCheck);
      } else if (taName.equals(SharkConstants.TOOL_AGENT_XPIL)) {
         validateToolAgentXPIL(el, existingErrors, fullCheck);
      } else if (taName.equals(SharkConstants.TOOL_AGENT_XSLT)) {
         validateToolAgentXSLT(el, existingErrors, fullCheck);
      }
   }

   protected void validateToolAgentBeanShellOrJavaScript(Application el,
                                                         List existingErrors,
                                                         boolean fullCheck) {
      FormalParameters fps = el.getApplicationTypes().getFormalParameters();
      ExtendedAttribute ea = el.getExtendedAttributes()
         .getFirstExtendedAttributeForName(SharkConstants.EA_APP_NAME);
      if (ea == null || ea.getVValue().trim().equals("")) {
         ea = el.getExtendedAttributes()
            .getFirstExtendedAttributeForName(SharkConstants.EA_SCRIPT);
      }
      if (ea == null || ea.getVValue().trim().equals("")) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          SharkValidationErrorIds.ERROR_TOOL_AGENT_BSH_OR_JS_SCRIPT_PARAMETER_REQUIRED,
                                                          "",
                                                          el);
         existingErrors.add(verr);
      }

   }

   protected void validateToolAgentCheckDocumentFormats(Application el,
                                                        List existingErrors,
                                                        boolean fullCheck) {
      FormalParameters fps = el.getApplicationTypes().getFormalParameters();
      FormalParameter alloweddfs = fps.getFormalParameter(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_ALLOWED_DOCUMENT_FORMATS);
      FormalParameter unsupporteddids = fps.getFormalParameter(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_UNSUPPORTED_DOCUMENT_IDS);
      if (alloweddfs == null || unsupporteddids == null) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          SharkValidationErrorIds.ERROR_TOOL_AGENT_CHECK_DOCUMENT_FORMATS_MISSING_REQUIRED_PARAMETERS,
                                                          "",
                                                          el);
         existingErrors.add(verr);
      } else if (alloweddfs.getMode().equals(XPDLConstants.FORMAL_PARAMETER_MODE_OUT)) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          SharkValidationErrorIds.ERROR_TOOL_AGENT_INVALID_FORMAL_PARAMETER_MODE_IN_REQUIRED,
                                                          "",
                                                          alloweddfs);
         existingErrors.add(verr);
      } else if (unsupporteddids.getMode().equals(XPDLConstants.FORMAL_PARAMETER_MODE_IN)) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          SharkValidationErrorIds.ERROR_TOOL_AGENT_INVALID_FORMAL_PARAMETER_MODE_OUT_REQUIRED,
                                                          "",
                                                          unsupporteddids);
         existingErrors.add(verr);
      }
   }

   protected void validateToolAgentJavaClass(Application el,
                                             List existingErrors,
                                             boolean fullCheck) {
      FormalParameters fps = el.getApplicationTypes().getFormalParameters();
      ExtendedAttribute ea = el.getExtendedAttributes()
         .getFirstExtendedAttributeForName(SharkConstants.EA_APP_NAME);
      if (ea == null || ea.getVValue().trim().equals("")) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          SharkValidationErrorIds.ERROR_TOOL_AGENT_APP_NAME_EXTENDED_ATTRIBUTE_REQUIRED,
                                                          "",
                                                          el);
         existingErrors.add(verr);
      }
   }

   protected void validateToolAgentLDAP(Application el,
                                        List existingErrors,
                                        boolean fullCheck) {
      FormalParameters fps = el.getApplicationTypes().getFormalParameters();
      FormalParameter result = fps.getFormalParameter(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_RESULT_CL);
      FormalParameter resultVarId = fps.getFormalParameter(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_RESULT_VARIABLE_ID);
      FormalParameter method = fps.getFormalParameter(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_METHOD);
      FormalParameter arg1 = fps.getFormalParameter(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_ARG1);
      FormalParameter arg2 = fps.getFormalParameter(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_ARG2);
      String methodId = null;
      if (method == null) {
         ExtendedAttribute ea = el.getExtendedAttributes()
            .getFirstExtendedAttributeForName(SharkConstants.EA_APP_NAME);
         if (ea != null) {
            methodId = ea.getVValue().trim();
         }
      }
      if ((method == null && methodId == null) || (result == null && resultVarId == null)) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          SharkValidationErrorIds.ERROR_TOOL_AGENT_LDAP_OR_USERGROUP_MISSING_REQUIRED_PARAMETERS,
                                                          "",
                                                          el);
         existingErrors.add(verr);
      }
      if (methodId != null) {
         if (!SharkConstants.TOOL_AGENT_LDAP_POSSIBLE_METHODS_LIST.contains(methodId)) {
            XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                             XMLValidationError.SUB_TYPE_LOGIC,
                                                             SharkValidationErrorIds.ERROR_TOOL_AGENT_LDAP_OR_USERGROUP_UNEXISTING_METHOD_NAME,
                                                             "",
                                                             el);
            existingErrors.add(verr);
         } else if (methodId.equals(SharkConstants.TOOL_AGENT_LDAP_METHOD_checkPassword)
                    || methodId.equals(SharkConstants.TOOL_AGENT_LDAP_METHOD_doesGroupBelongToGroup)
                    || methodId.equals(SharkConstants.TOOL_AGENT_LDAP_METHOD_doesUserBelongToGroup)
                    || methodId.equals(SharkConstants.TOOL_AGENT_LDAP_METHOD_getGroupAttribute)
                    || methodId.equals(SharkConstants.TOOL_AGENT_LDAP_METHOD_getUserAttribute)) {
            if (arg1 == null || arg2 == null) {
               XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                                XMLValidationError.SUB_TYPE_LOGIC,
                                                                SharkValidationErrorIds.ERROR_TOOL_AGENT_LDAP_OR_USERGROUP_INVALID_NUMBER_OF_ARGUMENTS_2_REQUIRED,
                                                                "",
                                                                el);
               existingErrors.add(verr);
            }
         } else if (methodId.equals(SharkConstants.TOOL_AGENT_LDAP_METHOD_getAllOrganizationalUnitEntries)
                    || methodId.equals(SharkConstants.TOOL_AGENT_LDAP_METHOD_getAllUserEntries)) {
            if (arg1 != null || arg2 != null) {
               XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                                XMLValidationError.SUB_TYPE_LOGIC,
                                                                SharkValidationErrorIds.ERROR_TOOL_AGENT_LDAP_OR_USERGROUP_INVALID_NUMBER_OF_ARGUMENTS_0_REQUIRED,
                                                                "",
                                                                el);
               existingErrors.add(verr);
            }
         } else if (arg1 == null || arg2 != null) {
            XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                             XMLValidationError.SUB_TYPE_LOGIC,
                                                             SharkValidationErrorIds.ERROR_TOOL_AGENT_LDAP_OR_USERGROUP_INVALID_NUMBER_OF_ARGUMENTS_1_REQUIRED,
                                                             "",
                                                             el);
            existingErrors.add(verr);
         }
      }
      if (arg1 != null
          && !compareDataTypes(arg1, BasicType.class, XPDLConstants.BASIC_TYPE_STRING)) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          SharkValidationErrorIds.ERROR_TOOL_AGENT_INVALID_PARAMETER_TYPE_BASIC_TYPE_STRING_REQUIRED,
                                                          "",
                                                          arg1);
         existingErrors.add(verr);
      }
      if (arg2 != null
          && !compareDataTypes(arg2, BasicType.class, XPDLConstants.BASIC_TYPE_STRING)) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          SharkValidationErrorIds.ERROR_TOOL_AGENT_INVALID_PARAMETER_TYPE_BASIC_TYPE_STRING_REQUIRED,
                                                          "",
                                                          arg2);
         existingErrors.add(verr);
      }
      if (method != null
          && !compareDataTypes(method, BasicType.class, XPDLConstants.BASIC_TYPE_STRING)) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          SharkValidationErrorIds.ERROR_TOOL_AGENT_INVALID_PARAMETER_TYPE_BASIC_TYPE_STRING_REQUIRED,
                                                          "",
                                                          method);
         existingErrors.add(verr);
      }
      if (resultVarId != null
          && !compareDataTypes(resultVarId,
                               BasicType.class,
                               XPDLConstants.BASIC_TYPE_STRING)) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          SharkValidationErrorIds.ERROR_TOOL_AGENT_INVALID_PARAMETER_TYPE_BASIC_TYPE_STRING_REQUIRED,
                                                          "",
                                                          resultVarId);
         existingErrors.add(verr);
      }
   }

   protected void validateToolAgentMail(Application el,
                                        List existingErrors,
                                        boolean fullCheck) {
      FormalParameters fps = el.getApplicationTypes().getFormalParameters();
      boolean sendMail = true;
      ExtendedAttribute ea = el.getExtendedAttributes()
         .getFirstExtendedAttributeForName(SharkConstants.EA_APP_MODE);
      if (ea != null && ea.getVValue().trim().equals("1")) {
         sendMail = false;
      }
      if (sendMail) {
         FormalParameter tos = fps.getFormalParameter(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_TO_ADDRESSES);
         FormalParameter ccs = fps.getFormalParameter(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_CC_ADDRESSES);
         FormalParameter bccs = fps.getFormalParameter(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_BCC_ADDRESSES);
         if (tos == null && ccs == null && bccs == null) {
            XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                             XMLValidationError.SUB_TYPE_LOGIC,
                                                             SharkValidationErrorIds.ERROR_TOOL_AGENT_MAIL_MISSING_REQUIRED_PARAMETERS,
                                                             "",
                                                             el);
            existingErrors.add(verr);
         }
      }
   }

   protected void validateToolAgentQuartzOrScheduler(Application el,
                                                     List existingErrors,
                                                     boolean fullCheck) {
      String taProxyName = null;
      ExtendedAttribute ea = el.getExtendedAttributes()
         .getFirstExtendedAttributeForName(SharkConstants.EA_TOOL_AGENT_CLASS_PROXY);
      if (ea != null) {
         taProxyName = ea.getVValue();
      }
      if (taProxyName == null || taProxyName.trim().equals("")) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          SharkValidationErrorIds.ERROR_TOOL_AGENT_QUARTZ_OR_SCHEDULER_TOOL_AGENT_CLASS_PROXY_EXTENDED_ATTRIBUTE_REQUIRED,
                                                          "",
                                                          el);
         existingErrors.add(verr);
      } else if (taProxyName.equals(SharkConstants.TOOL_AGENT_QUARTZ)
                 || taProxyName.equals(SharkConstants.TOOL_AGENT_SCHEDULER)) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          SharkValidationErrorIds.ERROR_TOOL_AGENT_QUARTZ_OR_SCHEDULER_TOOL_AGENT_CLASS_PROXY_REFERENCE_INVALID,
                                                          "",
                                                          el);
         existingErrors.add(verr);
      } else {
         validateToolAgent(taProxyName, el, existingErrors, fullCheck);
      }
   }

   protected void validateToolAgentRuntimeApplication(Application el,
                                                      List existingErrors,
                                                      boolean fullCheck) {
      FormalParameters fps = el.getApplicationTypes().getFormalParameters();
      ExtendedAttribute ea = el.getExtendedAttributes()
         .getFirstExtendedAttributeForName(SharkConstants.EA_APP_NAME);
      if (ea == null || ea.getVValue().trim().equals("")) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          SharkValidationErrorIds.ERROR_TOOL_AGENT_APP_NAME_EXTENDED_ATTRIBUTE_REQUIRED,
                                                          "",
                                                          el);
         existingErrors.add(verr);
      }
   }

   protected void validateToolAgentSOAP(Application el,
                                        List existingErrors,
                                        boolean fullCheck) {
      FormalParameters fps = el.getApplicationTypes().getFormalParameters();
      ExtendedAttribute ea = el.getExtendedAttributes()
         .getFirstExtendedAttributeForName(SharkConstants.EA_APP_NAME);
      if (ea == null || ea.getVValue().trim().equals("")) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          SharkValidationErrorIds.ERROR_TOOL_AGENT_APP_NAME_EXTENDED_ATTRIBUTE_REQUIRED,
                                                          "",
                                                          el);
         existingErrors.add(verr);
      } else if (fps.size() == 0) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          SharkValidationErrorIds.ERROR_TOOL_AGENT_SOAP_MISSING_REQUIRED_PARAMETERS,
                                                          "",
                                                          el);
         existingErrors.add(verr);
      } else {
         FormalParameter soapmethod = (FormalParameter) fps.get(0);
         if (!compareDataTypes(soapmethod,
                               BasicType.class,
                               XPDLConstants.BASIC_TYPE_STRING)) {
            XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                             XMLValidationError.SUB_TYPE_LOGIC,
                                                             SharkValidationErrorIds.ERROR_TOOL_AGENT_SOAP_INVALID_SOAP_OPERATION_PARAMETER_TYPE,
                                                             "",
                                                             el);
            existingErrors.add(verr);
         }

      }
   }

   protected void validateToolAgentUserGroup(Application el,
                                             List existingErrors,
                                             boolean fullCheck) {
      FormalParameters fps = el.getApplicationTypes().getFormalParameters();
      FormalParameter name = fps.getFormalParameter(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_NAME);
      FormalParameter result = fps.getFormalParameter(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_RESULT_CL);
      FormalParameter resultVarId = fps.getFormalParameter(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_RESULT_VARIABLE_ID);
      FormalParameter method = fps.getFormalParameter(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_METHOD);
      FormalParameter arg1 = fps.getFormalParameter(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_ARG1);
      FormalParameter arg2 = fps.getFormalParameter(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_ARG2);
      String methodId = null;
      if (method == null) {
         ExtendedAttribute ea = el.getExtendedAttributes()
            .getFirstExtendedAttributeForName(SharkConstants.EA_APP_NAME);
         if (ea != null) {
            methodId = ea.getVValue().trim();
         }
      }
      if ((method == null && methodId == null) || (result == null && resultVarId == null)) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          SharkValidationErrorIds.ERROR_TOOL_AGENT_LDAP_OR_USERGROUP_MISSING_REQUIRED_PARAMETERS,
                                                          "",
                                                          el);
         existingErrors.add(verr);
      }
      if (methodId != null) {
         if (!SharkConstants.TOOL_AGENT_USERGROUP_POSSIBLE_METHODS_LIST.contains(methodId)) {
            XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                             XMLValidationError.SUB_TYPE_LOGIC,
                                                             SharkValidationErrorIds.ERROR_TOOL_AGENT_LDAP_OR_USERGROUP_UNEXISTING_METHOD_NAME,
                                                             "",
                                                             el);
            existingErrors.add(verr);
         } else if (methodId.equals(SharkConstants.TOOL_AGENT_USERGROUP_METHOD_doesGroupBelongToGroup)
                    || methodId.equals(SharkConstants.TOOL_AGENT_USERGROUP_METHOD_doesUserBelongToGroup)
                    || methodId.equals(SharkConstants.TOOL_AGENT_USERGROUP_METHOD_getUserAttribute)
                    || methodId.equals(SharkConstants.TOOL_AGENT_USERGROUP_METHOD_getGroupAttribute)
                    || methodId.equals(SharkConstants.TOOL_AGENT_USERGROUP_METHOD_validateUser)) {
            if (arg1 == null || arg2 == null) {
               XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                                XMLValidationError.SUB_TYPE_LOGIC,
                                                                SharkValidationErrorIds.ERROR_TOOL_AGENT_LDAP_OR_USERGROUP_INVALID_NUMBER_OF_ARGUMENTS_2_REQUIRED,
                                                                "",
                                                                el);
               existingErrors.add(verr);
            }
         } else if (methodId.equals(SharkConstants.TOOL_AGENT_USERGROUP_METHOD_getAllGroups)
                    || methodId.equals(SharkConstants.TOOL_AGENT_USERGROUP_METHOD_getAllUsers)) {
            if (arg1 != null || arg2 != null) {
               XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                                XMLValidationError.SUB_TYPE_LOGIC,
                                                                SharkValidationErrorIds.ERROR_TOOL_AGENT_LDAP_OR_USERGROUP_INVALID_NUMBER_OF_ARGUMENTS_0_REQUIRED,
                                                                "",
                                                                el);
               existingErrors.add(verr);
            }
         } else if (arg1 == null || arg2 != null) {
            XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                             XMLValidationError.SUB_TYPE_LOGIC,
                                                             SharkValidationErrorIds.ERROR_TOOL_AGENT_LDAP_OR_USERGROUP_INVALID_NUMBER_OF_ARGUMENTS_1_REQUIRED,
                                                             "",
                                                             el);
            existingErrors.add(verr);
         }
      }
      if (arg1 != null
          && !compareDataTypes(arg1, BasicType.class, XPDLConstants.BASIC_TYPE_STRING)) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          SharkValidationErrorIds.ERROR_TOOL_AGENT_INVALID_PARAMETER_TYPE_BASIC_TYPE_STRING_REQUIRED,
                                                          "",
                                                          arg1);
         existingErrors.add(verr);
      }
      if (arg2 != null
          && !compareDataTypes(arg2, BasicType.class, XPDLConstants.BASIC_TYPE_STRING)) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          SharkValidationErrorIds.ERROR_TOOL_AGENT_INVALID_PARAMETER_TYPE_BASIC_TYPE_STRING_REQUIRED,
                                                          "",
                                                          arg2);
         existingErrors.add(verr);
      }
      if (method != null
          && !compareDataTypes(method, BasicType.class, XPDLConstants.BASIC_TYPE_STRING)) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          SharkValidationErrorIds.ERROR_TOOL_AGENT_INVALID_PARAMETER_TYPE_BASIC_TYPE_STRING_REQUIRED,
                                                          "",
                                                          method);
         existingErrors.add(verr);
      }
      if (resultVarId != null
          && !compareDataTypes(resultVarId,
                               BasicType.class,
                               XPDLConstants.BASIC_TYPE_STRING)) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          SharkValidationErrorIds.ERROR_TOOL_AGENT_INVALID_PARAMETER_TYPE_BASIC_TYPE_STRING_REQUIRED,
                                                          "",
                                                          resultVarId);
         existingErrors.add(verr);
      }
      if (name != null
          && !compareDataTypes(name, BasicType.class, XPDLConstants.BASIC_TYPE_STRING)) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          SharkValidationErrorIds.ERROR_TOOL_AGENT_INVALID_PARAMETER_TYPE_BASIC_TYPE_STRING_REQUIRED,
                                                          "",
                                                          name);
         existingErrors.add(verr);
      }
   }

   protected void validateToolAgentXPath(Application el,
                                         List existingErrors,
                                         boolean fullCheck) {
      FormalParameters fps = el.getApplicationTypes().getFormalParameters();
      FormalParameter node = fps.getFormalParameter(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_NODE);
      FormalParameter expressions = fps.getFormalParameter(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_EXPRESSIONS);
      FormalParameter resultVariableIds = fps.getFormalParameter(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_RESULT_VARIABLE_IDS);
      if (node == null || expressions == null || resultVariableIds == null) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          SharkValidationErrorIds.ERROR_TOOL_AGENT_XPATH_MISSING_REQUIRED_PARAMETERS,
                                                          "",
                                                          el);
         existingErrors.add(verr);
      } else {
         if (!compareDataTypes(node, SchemaType.class, null)) {
            XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                             XMLValidationError.SUB_TYPE_LOGIC,
                                                             SharkValidationErrorIds.ERROR_TOOL_AGENT_INVALID_PARAMETER_TYPE_SCHEMA_TYPE_REQUIRED,
                                                             "",
                                                             node);
            existingErrors.add(verr);
         }
         if (!compareDataTypes(expressions,
                               BasicType.class,
                               XPDLConstants.BASIC_TYPE_STRING)) {
            XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                             XMLValidationError.SUB_TYPE_LOGIC,
                                                             SharkValidationErrorIds.ERROR_TOOL_AGENT_INVALID_PARAMETER_TYPE_BASIC_TYPE_STRING_REQUIRED,
                                                             "",
                                                             expressions);
            existingErrors.add(verr);
         }
         if (!compareDataTypes(resultVariableIds,
                               BasicType.class,
                               XPDLConstants.BASIC_TYPE_STRING)) {
            XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                             XMLValidationError.SUB_TYPE_LOGIC,
                                                             SharkValidationErrorIds.ERROR_TOOL_AGENT_INVALID_PARAMETER_TYPE_BASIC_TYPE_STRING_REQUIRED,
                                                             "",
                                                             resultVariableIds);
            existingErrors.add(verr);
         }
      }
      for (int i = 0; i < fps.size(); i++) {
         FormalParameter fp = (FormalParameter) fps.get(i);
         if (fp.getId()
            .startsWith(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_PREFIX_XML_PREFIX_AND_NAMESPACE_URI)) {
            if (!compareDataTypes(fp, BasicType.class, XPDLConstants.BASIC_TYPE_STRING)) {
               XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                                XMLValidationError.SUB_TYPE_LOGIC,
                                                                SharkValidationErrorIds.ERROR_TOOL_AGENT_INVALID_PARAMETER_TYPE_BASIC_TYPE_STRING_REQUIRED,
                                                                "",
                                                                fp);
               existingErrors.add(verr);
            }
         }
      }
   }

   protected void validateToolAgentXPIL(Application el,
                                        List existingErrors,
                                        boolean fullCheck) {
      FormalParameters fps = el.getApplicationTypes().getFormalParameters();
      FormalParameter xpil = fps.getFormalParameter(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_XPIL);
      if (xpil == null) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          SharkValidationErrorIds.ERROR_TOOL_AGENT_XPIL_XPIL_PARAMETER_REQUIRED,
                                                          "",
                                                          el);
         existingErrors.add(verr);
      } else if (!compareDataTypes(xpil, SchemaType.class, null)) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          SharkValidationErrorIds.ERROR_TOOL_AGENT_INVALID_PARAMETER_TYPE_SCHEMA_TYPE_REQUIRED,
                                                          "",
                                                          xpil);
         existingErrors.add(verr);
      }
   }

   protected void validateToolAgentXSLT(Application el,
                                        List existingErrors,
                                        boolean fullCheck) {
      FormalParameters fps = el.getApplicationTypes().getFormalParameters();
      FormalParameter result = fps.getFormalParameter(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_RESULT_SL);
      FormalParameter source = fps.getFormalParameter(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_SOURCE);
      if (result == null) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          SharkValidationErrorIds.ERROR_TOOL_AGENT_XSLT_RESULT_PARAMETER_REQUIRED,
                                                          "",
                                                          el);
         existingErrors.add(verr);
      } else {
         if (result.getMode().equals(XPDLConstants.FORMAL_PARAMETER_MODE_IN)) {
            XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                             XMLValidationError.SUB_TYPE_LOGIC,
                                                             SharkValidationErrorIds.ERROR_TOOL_AGENT_INVALID_FORMAL_PARAMETER_MODE_OUT_REQUIRED,
                                                             "",
                                                             result);
            existingErrors.add(verr);
         }
         if (!compareDataTypes(result, SchemaType.class, null)
             && !compareDataTypes(result,
                                  BasicType.class,
                                  XPDLConstants.BASIC_TYPE_STRING)) {
            XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                             XMLValidationError.SUB_TYPE_LOGIC,
                                                             SharkValidationErrorIds.ERROR_TOOL_AGENT_INVALID_PARAMETER_TYPE_SCHEMA_TYPE_REQUIRED,
                                                             "",
                                                             result);
         }
      }

      if (source != null) {
         if (source.getMode().equals(XPDLConstants.FORMAL_PARAMETER_MODE_OUT)) {
            XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                             XMLValidationError.SUB_TYPE_LOGIC,
                                                             SharkValidationErrorIds.ERROR_TOOL_AGENT_INVALID_FORMAL_PARAMETER_MODE_IN_REQUIRED,
                                                             "",
                                                             result);
            existingErrors.add(verr);
         }
         if (!compareDataTypes(source, SchemaType.class, null)
             && !compareDataTypes(source,
                                  BasicType.class,
                                  XPDLConstants.BASIC_TYPE_STRING)) {
            XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                             XMLValidationError.SUB_TYPE_LOGIC,
                                                             SharkValidationErrorIds.ERROR_TOOL_AGENT_INVALID_PARAMETER_TYPE_SCHEMA_TYPE_REQUIRED,
                                                             "",
                                                             source);
         }
      }
      if (result != null) {
         if (fps.size() == 1) {

            ExtendedAttribute ea = el.getExtendedAttributes()
               .getFirstExtendedAttributeForName(SharkConstants.EA_SCRIPT);
            if (ea == null || ea.getVValue().trim().equals("")) {
               XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                                XMLValidationError.SUB_TYPE_LOGIC,
                                                                SharkValidationErrorIds.ERROR_TOOL_AGENT_XSLT_TRANSFORMER_PARAMETER_REQUIRED,
                                                                "",
                                                                el);
               existingErrors.add(verr);
            }
         } else {
            boolean hasTransformation = false;
            List l = fps.toElements();
            for (int i = 0; i < l.size(); i++) {
               FormalParameter fp = (FormalParameter) l.get(i);
               if (fp.getId()
                  .equals(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_TRANSFORMER_NAME)
                   || fp.getId()
                      .equals(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_TRANSFORMER_PATH)
                   || fp.getId()
                      .equals(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_TRANSFORMER_NODE)
                   || fp.getId()
                      .equals(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_TRANSFORMER_SCRIPT)) {
                  hasTransformation = true;
                  if (fp.getMode().equals(XPDLConstants.FORMAL_PARAMETER_MODE_OUT)) {
                     XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                                      XMLValidationError.SUB_TYPE_LOGIC,
                                                                      SharkValidationErrorIds.ERROR_TOOL_AGENT_INVALID_FORMAL_PARAMETER_MODE_IN_REQUIRED,
                                                                      "",
                                                                      fp);
                     existingErrors.add(verr);
                     if (!fullCheck)
                        break;
                  }
                  if (fp.getId()
                     .equals(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_TRANSFORMER_NODE)
                      && !compareDataTypes(fp, SchemaType.class, null)) {
                     XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                                      XMLValidationError.SUB_TYPE_LOGIC,
                                                                      SharkValidationErrorIds.ERROR_TOOL_AGENT_INVALID_PARAMETER_TYPE_SCHEMA_TYPE_REQUIRED,
                                                                      "",
                                                                      fp);
                     existingErrors.add(verr);
                  } else if (!fp.getId()
                     .equals(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_TRANSFORMER_NODE)
                             && !compareDataTypes(fp,
                                                  BasicType.class,
                                                  XPDLConstants.BASIC_TYPE_STRING)) {
                     XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                                      XMLValidationError.SUB_TYPE_LOGIC,
                                                                      SharkValidationErrorIds.ERROR_TOOL_AGENT_INVALID_PARAMETER_TYPE_BASIC_TYPE_STRING_REQUIRED,
                                                                      "",
                                                                      fp);
                     existingErrors.add(verr);
                  }
                  break;
               }
            }
            if (!hasTransformation) {
               ExtendedAttribute ea = el.getExtendedAttributes()
                  .getFirstExtendedAttributeForName(SharkConstants.EA_SCRIPT);
               if (ea == null || ea.getVValue().trim().equals("")) {
                  XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                                   XMLValidationError.SUB_TYPE_LOGIC,
                                                                   SharkValidationErrorIds.ERROR_TOOL_AGENT_XSLT_TRANSFORMER_PARAMETER_REQUIRED,
                                                                   "",
                                                                   el);
                  existingErrors.add(verr);
               }
            }
         }
      }
   }

   protected boolean allowsUndefinedVariables(XMLElement el, boolean defaultValue) {
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(el);
      Package pkg = XMLUtil.getPackage(el);

      ExtendedAttribute ea = null;
      Boolean allow = new Boolean(defaultValue);
      if (wp != null) {
         ea = wp.getExtendedAttributes()
            .getFirstExtendedAttributeForName(SharkConstants.EA_ALLOW_UNDEFINED_VARIABLES);
         if (ea != null) {
            allow = new Boolean(ea.getVValue().equalsIgnoreCase("true"));
         }
      }
      if (ea == null && pkg != null) {
         ea = XMLUtil.getPackage(wp)
            .getExtendedAttributes()
            .getFirstExtendedAttributeForName(SharkConstants.EA_ALLOW_UNDEFINED_VARIABLES);
         if (ea != null) {
            allow = new Boolean(ea.getVValue().equalsIgnoreCase("true"));
         }
      }
      return allow.booleanValue();
   }

   protected List<String> getPossiblePlaceholderVariables(String eav, String typePrefix) {
      List ret = new ArrayList();
      String prefix = "{" + typePrefix;
      String postfix = "}";
      List ups = XMLUtil.getUsingPositions(eav, prefix, new HashMap(), false);
      // System.out.println("UPS for "+subjOrCont+" is "+ups);
      for (int i = 0; i < ups.size(); i++) {
         int posprefix = ((Integer) ups.get(i)).intValue();
         int pospostfix = eav.indexOf(postfix, posprefix);
         if (pospostfix > posprefix) {
            String placeholdercontent = eav.substring(posprefix + 1, pospostfix);
            String varId = eav.substring(posprefix + prefix.length(), pospostfix);
            if (!typePrefix.equals("")
                || !(placeholdercontent.startsWith(SharkConstants.PROCESS_VARIABLE_PLACEHOLDER_PREFIX)
                     || placeholdercontent.startsWith(SharkConstants.CONFIG_STRING_PLACEHOLDER_PREFIX) || placeholdercontent.startsWith(SharkConstants.XPDL_STRING_PLACEHOLDER_PREFIX))) {
               ret.add(varId);
            }
         }
      }
      return ret;
   }

   public abstract void validateScript(XMLComplexElement el,
                                       List existingErrors,
                                       boolean fullCheck);

   public abstract boolean isDeadlineHandlerUsed();

   protected abstract Map getPossibleXPDLStringVariablesEAValues(XMLElement el,
                                                                 boolean allLevels);

   protected abstract Properties getPossibleXPDLStringVariables(XMLElement el,
                                                                boolean allLevels);

   protected abstract List<String> getPossibleXPDLStringVariableNames(XMLElement el,
                                                                      boolean allLevels);

   protected abstract List<String> getConfigStringChoices();

}