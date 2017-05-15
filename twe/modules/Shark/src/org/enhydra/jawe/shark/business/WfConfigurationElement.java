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
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;

public class WfConfigurationElement extends XMLComplexElement {

   protected ExtendedAttributes eas;

   protected boolean isForAct = false;

   protected boolean isFullyManualAct = false;

   public WfConfigurationElement(ExtendedAttributes eas, boolean isForAct, boolean isFullyManualAct) {
      super(eas.getParent(), "WfConfiguration", true);
      this.eas = eas;
      this.isForAct = isForAct;
      this.isFullyManualAct = isFullyManualAct;
      notifyMainListeners = false;
      notifyListeners = false;
      try {
         handleStructure();
      } catch (Exception ex) {
         ex.printStackTrace();
      }
      setReadOnly(eas.isReadOnly());
   }

   public void setValue(String v) {
      if (v == null) {
         boolean removeUnconditionally = false;
         if (!isForAct) {
            SharkUtils.updateSingleExtendedAttribute(this,
                                                     eas,
                                                     SharkConstants.EA_UNSATISFIED_SPLIT_CONDITION_HANDLING_MODE,
                                                     null,
                                                     null,
                                                     true,
                                                     removeUnconditionally);
         }
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_USE_PROCESS_CONTEXT_ONLY, null, null, true, removeUnconditionally);
         if (!isForAct) {
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_ALLOW_UNDEFINED_VARIABLES, null, null, true, removeUnconditionally);
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_TRANSIENT, null, null, true, removeUnconditionally);
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_DELETE_FINISHED, null, null, true, removeUnconditionally);
         }
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_REEVALUATE_DEADLINES, null, null, true, removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_CREATE_ASSIGNMENTS, null, null, true, removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_CREATE_DEFAULT_ASSIGNMENT, null, null, true, removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_HANDLE_ALL_ASSIGNMENTS, null, null, true, removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_ACCEPT_SINGLE_ASSIGNMENT, null, null, true, removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  SharkConstants.EA_REASSIGN_WITH_UNACCEPTANCE_TO_SINGLE_USER,
                                                  null,
                                                  null,
                                                  true,
                                                  removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_DELETE_OTHER_ASSIGNMENTS, null, null, true, removeUnconditionally);
         if (!isForAct) {
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_EVALUATE_NAME_AS_EXPRESSION_PROCESS, null, null, true, removeUnconditionally);
            SharkUtils
               .updateSingleExtendedAttribute(this, eas, SharkConstants.EA_EVALUATE_DESCRIPTION_AS_EXPRESSION_PROCESS, null, null, true, removeUnconditionally);
            SharkUtils.updateSingleExtendedAttribute(this,
                                                     eas,
                                                     SharkConstants.EA_EVALUATE_LIMIT_AS_EXPRESSION_PROCESS,
                                                     null,
                                                     null,
                                                     true,
                                                     removeUnconditionally);
            SharkUtils.updateSingleExtendedAttribute(this,
                                                     eas,
                                                     SharkConstants.EA_EVALUATE_PRIORITY_AS_EXPRESSION_PROCESS,
                                                     null,
                                                     null,
                                                     true,
                                                     removeUnconditionally);
         }
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_EVALUATE_NAME_AS_EXPRESSION_ACTIVITY, null, null, true, removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  SharkConstants.EA_EVALUATE_DESCRIPTION_AS_EXPRESSION_ACTIVITY,
                                                  null,
                                                  null,
                                                  true,
                                                  removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_EVALUATE_LIMIT_AS_EXPRESSION_ACTIVITY, null, null, true, removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  SharkConstants.EA_EVALUATE_PRIORITY_AS_EXPRESSION_ACTIVITY,
                                                  null,
                                                  null,
                                                  true,
                                                  removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  SharkConstants.EA_USE_FIRST_DEADLINE_TIME_FOR_LIMIT_IF_UNDEFINED,
                                                  null,
                                                  null,
                                                  true,
                                                  removeUnconditionally);
         if (isForAct) {
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_OVERRIDE_PROCESS_CONTEXT, null, null, true, removeUnconditionally);
            if (isFullyManualAct) {
               SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_AUTO_COMPLETION, null, null, true, removeUnconditionally);
            }
         }

         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_ASSIGNMENT_MANAGER_PLUGIN, null, null, true, removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_ASSIGNMENT_MANAGER_APPEND_RESPONSIBLES, null, null, true, removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this,
                                                  eas,
                                                  SharkConstants.EA_ASSIGNMENT_MANAGER_TRY_STRAIGHTFORWARD_MAPPING,
                                                  null,
                                                  null,
                                                  true,
                                                  removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_ASSIGNMENT_MANAGER_DEFAULT_ASSIGNEES, null, null, true, removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_MAX_ASSIGNMENTS, null, null, true, removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_WORKLOAD_FACTOR, null, null, true, removeUnconditionally);
         if (!isForAct) {
            SharkUtils.updateSingleExtendedAttribute(this,
                                                     eas,
                                                     SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_LOG_XPIL,
                                                     null,
                                                     null,
                                                     true,
                                                     removeUnconditionally);
            SharkUtils.updateSingleExtendedAttribute(this,
                                                     eas,
                                                     SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_FILENAMEVAR,
                                                     null,
                                                     null,
                                                     true,
                                                     removeUnconditionally);
         } else {
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_ASSIGN_TO_ORIGINAL_PERFORMER, null, null, true, removeUnconditionally);
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_ASSIGN_TO_PERFORMER_OF_ACTIVITY, null, null, true, removeUnconditionally);
            SharkUtils.updateSingleExtendedAttribute(this,
                                                     eas,
                                                     SharkConstants.EA_DO_NOT_ASSIGN_TO_PERFORMER_OF_ACTIVITY,
                                                     null,
                                                     null,
                                                     true,
                                                     removeUnconditionally);
         }
      } else {
         if (isReadOnly) {
            throw new RuntimeException("Can't set the value of read only element!");
         }
         this.value = v;
      }
   }

   public void setReadOnly(boolean ro) {
      super.setReadOnly(ro);
   }

   public XMLAttribute getUnsatisfiedSplitConditionHandlingModeAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_UNSATISFIED_SPLIT_CONDITION_HANDLING_MODE);
   }

   public XMLAttribute getAllowUndefinedVariablesAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_ALLOW_UNDEFINED_VARIABLES);
   }

   public XMLAttribute getReevaluateDeadlinesAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_REEVALUATE_DEADLINES);
   }

   public XMLAttribute getCreateAssignmentsAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_CREATE_ASSIGNMENTS);
   }

   public XMLAttribute getCreateDefaultAssignmentAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_CREATE_DEFAULT_ASSIGNMENT);
   }

   public XMLAttribute getHandleAllAssignmentsAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_HANDLE_ALL_ASSIGNMENTS);
   }

   public XMLAttribute getAcceptSingleAssignmentAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_ACCEPT_SINGLE_ASSIGNMENT);
   }

   public XMLAttribute getReassignWithUnacceptanceToSingleUserAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_REASSIGN_WITH_UNACCEPTANCE_TO_SINGLE_USER);
   }

   public XMLAttribute getDeleteOtherAssignmentsAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_DELETE_OTHER_ASSIGNMENTS);
   }

   public XMLAttribute getAssignmentManagerPlugInAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_ASSIGNMENT_MANAGER_PLUGIN);
   }

   public XMLAttribute getMaxAssignmentsAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_MAX_ASSIGNMENTS);
   }

   public XMLAttribute getWorkloadFactorAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_WORKLOAD_FACTOR);
   }

   public XMLAttribute getTransientAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_TRANSIENT);
   }

   public XMLAttribute getDeleteFinishedAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_DELETE_FINISHED);
   }

   public XMLAttribute getUseProcessContextOnlyAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_USE_PROCESS_CONTEXT_ONLY);
   }

   public XMLAttribute getLogXPILWhenFinishedAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_LOG_XPIL);
   }

   public XMLAttribute getXPILLogFilenameVarAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_FILENAMEVAR);
   }

   public XMLAttribute getAppendResponsiblesAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_ASSIGNMENT_MANAGER_APPEND_RESPONSIBLES);
   }

   public XMLAttribute getTryStraightForwardMappingAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_ASSIGNMENT_MANAGER_TRY_STRAIGHTFORWARD_MAPPING);
   }

   public WfVariables getDefaultAssigneesElement() {
      return (WfVariables) get(SharkConstants.EA_ASSIGNMENT_MANAGER_DEFAULT_ASSIGNEES);
   }

   public XMLAttribute getAssignToOriginalPerformerAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_ASSIGN_TO_ORIGINAL_PERFORMER);
   }

   public XMLAttribute getAssignToPerformerOfActivityAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_ASSIGN_TO_PERFORMER_OF_ACTIVITY);
   }

   public XMLAttribute getDoNotAssignToPerformerOfActivityAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_DO_NOT_ASSIGN_TO_PERFORMER_OF_ACTIVITY);
   }

   public WfNameValues getOverrideProcessContextElement() {
      return (WfNameValues) get(SharkConstants.EA_OVERRIDE_PROCESS_CONTEXT);
   }

   public XMLAttribute getAutoCompletionAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_AUTO_COMPLETION);
   }

   public XMLAttribute getEvaluateProcessNameAsExpression() {
      return (XMLAttribute) get(SharkConstants.EA_EVALUATE_NAME_AS_EXPRESSION_PROCESS);
   }

   public XMLAttribute getEvaluateProcessDescriptionAsExpression() {
      return (XMLAttribute) get(SharkConstants.EA_EVALUATE_DESCRIPTION_AS_EXPRESSION_PROCESS);
   }

   public XMLAttribute getEvaluateProcessLimitAsExpression() {
      return (XMLAttribute) get(SharkConstants.EA_EVALUATE_LIMIT_AS_EXPRESSION_PROCESS);
   }

   public XMLAttribute getEvaluateProcessPriorityAsExpression() {
      return (XMLAttribute) get(SharkConstants.EA_EVALUATE_PRIORITY_AS_EXPRESSION_PROCESS);
   }

   public XMLAttribute getEvaluateActivityNameAsExpression() {
      return (XMLAttribute) get(SharkConstants.EA_EVALUATE_NAME_AS_EXPRESSION_ACTIVITY);
   }

   public XMLAttribute getEvaluateActivityDescriptionAsExpression() {
      return (XMLAttribute) get(SharkConstants.EA_EVALUATE_DESCRIPTION_AS_EXPRESSION_ACTIVITY);
   }

   public XMLAttribute getEvaluateActivityLimitAsExpression() {
      return (XMLAttribute) get(SharkConstants.EA_EVALUATE_LIMIT_AS_EXPRESSION_ACTIVITY);
   }

   public XMLAttribute getEvaluateActivityPriorityAsExpression() {
      return (XMLAttribute) get(SharkConstants.EA_EVALUATE_PRIORITY_AS_EXPRESSION_ACTIVITY);
   }

   public XMLAttribute getUseFirstDeadlineTimeForLimitIfUndefined() {
      return (XMLAttribute) get(SharkConstants.EA_USE_FIRST_DEADLINE_TIME_FOR_LIMIT_IF_UNDEFINED);
   }

   protected void fillStructure() {
      XMLAttribute attrUnsatisfiedSplitConditionHandlingMode = new XMLAttribute(this,
                                                                                SharkConstants.EA_UNSATISFIED_SPLIT_CONDITION_HANDLING_MODE,
                                                                                false,
                                                                                new String[] {
                                                                                               "", "FINISH_IF_POSSIBLE", "IGNORE", "ROLLBACK"
                                                                                }, 0);
      XMLAttribute attrAllowUndefinedVariables = new XMLAttribute(this, SharkConstants.EA_ALLOW_UNDEFINED_VARIABLES, false, new String[] {
                                                                                                                                           "true", "false", ""
      }, 2);
      XMLAttribute attrReevaluateDeadlines = new XMLAttribute(this, SharkConstants.EA_REEVALUATE_DEADLINES, false, new String[] {
                                                                                                                                  "true", "false", ""
      }, 2);
      XMLAttribute attrCreateAssignments = new XMLAttribute(this, SharkConstants.EA_CREATE_ASSIGNMENTS, false, new String[] {
                                                                                                                              "true", "false", ""
      }, 2);

      XMLAttribute attrCreateDefaultAssignment = new XMLAttribute(this, SharkConstants.EA_CREATE_DEFAULT_ASSIGNMENT, false, new String[] {
                                                                                                                                           "true", "false", ""
      }, 2);

      XMLAttribute attrHandleAllAssignments = new XMLAttribute(this, SharkConstants.EA_HANDLE_ALL_ASSIGNMENTS, false, new String[] {
                                                                                                                                     "true", "false", ""
      }, 2);

      XMLAttribute attrAcceptSingleAssignment = new XMLAttribute(this, SharkConstants.EA_ACCEPT_SINGLE_ASSIGNMENT, false, new String[] {
                                                                                                                                         "true", "false", ""
      }, 2);

      XMLAttribute attrReassignWithUnacceptanceToSingleUser = new XMLAttribute(this,
                                                                               SharkConstants.EA_REASSIGN_WITH_UNACCEPTANCE_TO_SINGLE_USER,
                                                                               false,
                                                                               new String[] {
                                                                                              "true", "false", ""
                                                                               }, 2);

      XMLAttribute attrDeleteOtherAssignments = new XMLAttribute(this, SharkConstants.EA_DELETE_OTHER_ASSIGNMENTS, false, new String[] {
                                                                                                                                         "true", "false", ""
      }, 2);

      XMLAttribute attrAssignmentManagerPlugin = new XMLAttribute(this, SharkConstants.EA_ASSIGNMENT_MANAGER_PLUGIN, false);

      XMLAttribute attrMaxAssignments = new XMLAttribute(this, SharkConstants.EA_MAX_ASSIGNMENTS, false);
      XMLAttribute attrWorkloadFactor = new XMLAttribute(this, SharkConstants.EA_WORKLOAD_FACTOR, false);

      XMLAttribute attrTransient = new XMLAttribute(this, SharkConstants.EA_TRANSIENT, false, new String[] {
                                                                                                             "true", "false", ""
      }, 2);
      XMLAttribute attrDeleteFinished = new XMLAttribute(this, SharkConstants.EA_DELETE_FINISHED, false, new String[] {
                                                                                                                        "true", "false", ""
      }, 2);
      XMLAttribute attrUseProcessContext = new XMLAttribute(this, SharkConstants.EA_USE_PROCESS_CONTEXT_ONLY, false, new String[] {
                                                                                                                                    "true", "false", ""
      }, 2);

      XMLAttribute attrLogXPILWhenFinished = new XMLAttribute(this, SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_LOG_XPIL, false, new String[] {
                                                                                                                                                  "true",
                                                                                                                                                  "false", ""
      }, 2);
      XMLAttribute attrXPILLogFilenameVar = new XMLAttribute(this, SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_FILENAMEVAR, false);

      XMLAttribute attrAppendResponsibles = new XMLAttribute(this, SharkConstants.EA_ASSIGNMENT_MANAGER_APPEND_RESPONSIBLES, false, new String[] {
                                                                                                                                                   "true", ""
      }, 1);

      XMLAttribute attrTryStraightForwardMapping = new XMLAttribute(this,
                                                                    SharkConstants.EA_ASSIGNMENT_MANAGER_TRY_STRAIGHTFORWARD_MAPPING,
                                                                    false,
                                                                    new String[] {
                                                                                   "true", "false", ""
                                                                    }, 2);
      WfVariables elDefaultAssignees = new WfVariables(this, SharkConstants.EA_ASSIGNMENT_MANAGER_DEFAULT_ASSIGNEES, Arrays.asList(new String[] {
                                                                                                                                                  XPDLConstants.BASIC_TYPE_STRING
      }), ",", false);

      XMLAttribute attrAssignToOriginalPerformer = new XMLAttribute(this, SharkConstants.EA_ASSIGN_TO_ORIGINAL_PERFORMER, false, new String[] {
                                                                                                                                                "true", ""
      }, 1);
      XMLAttribute attrAssignToPerformerOfActivity = new XMLAttribute(this, SharkConstants.EA_ASSIGN_TO_PERFORMER_OF_ACTIVITY, false);
      XMLAttribute attrDoNotAssignToPerformerOfActivity = new XMLAttribute(this, SharkConstants.EA_DO_NOT_ASSIGN_TO_PERFORMER_OF_ACTIVITY, false);

      WfNameValues elOverrideProcessContext = new WfNameValues(this,
                                                               SharkConstants.EA_OVERRIDE_PROCESS_CONTEXT,
                                                               ",",
                                                               ":",
                                                               "Variable",
                                                               "OverrideExpression",
                                                               false);

      XMLAttribute attrAutoCompletion = new XMLAttribute(this, SharkConstants.EA_AUTO_COMPLETION, false, new String[] {
                                                                                                                        "true", ""
      }, 1);

      XMLAttribute attrEvaluateProcessName = new XMLAttribute(this, SharkConstants.EA_EVALUATE_NAME_AS_EXPRESSION_PROCESS, false, new String[] {
                                                                                                                                                 "true",
                                                                                                                                                 "false", ""
      }, 2);
      XMLAttribute attrEvaluateProcessDescription = new XMLAttribute(this, SharkConstants.EA_EVALUATE_DESCRIPTION_AS_EXPRESSION_PROCESS, false, new String[] {
                                                                                                                                                               "true",
                                                                                                                                                               "false",
                                                                                                                                                               ""
      }, 2);
      XMLAttribute attrEvaluateProcessLimit = new XMLAttribute(this, SharkConstants.EA_EVALUATE_LIMIT_AS_EXPRESSION_PROCESS, false, new String[] {
                                                                                                                                                   "true",
                                                                                                                                                   "false", ""
      }, 2);
      XMLAttribute attrEvaluateProcessPriority = new XMLAttribute(this, SharkConstants.EA_EVALUATE_PRIORITY_AS_EXPRESSION_PROCESS, false, new String[] {
                                                                                                                                                         "true",
                                                                                                                                                         "false",
                                                                                                                                                         ""
      }, 2);
      XMLAttribute attrEvaluateActivityName = new XMLAttribute(this, SharkConstants.EA_EVALUATE_NAME_AS_EXPRESSION_ACTIVITY, false, new String[] {
                                                                                                                                                   "true",
                                                                                                                                                   "false", ""
      }, 2);
      XMLAttribute attrEvaluateActivityDescription = new XMLAttribute(this, SharkConstants.EA_EVALUATE_DESCRIPTION_AS_EXPRESSION_ACTIVITY, false, new String[] {
                                                                                                                                                                 "true",
                                                                                                                                                                 "false",
                                                                                                                                                                 ""
      }, 2);
      XMLAttribute attrEvaluateActivityLimit = new XMLAttribute(this, SharkConstants.EA_EVALUATE_LIMIT_AS_EXPRESSION_ACTIVITY, false, new String[] {
                                                                                                                                                     "true",
                                                                                                                                                     "false", ""
      }, 2);
      XMLAttribute attrEvaluateActivityPriority = new XMLAttribute(this, SharkConstants.EA_EVALUATE_PRIORITY_AS_EXPRESSION_ACTIVITY, false, new String[] {
                                                                                                                                                           "true",
                                                                                                                                                           "false",
                                                                                                                                                           ""
      }, 2);
      XMLAttribute attrUseFirstDeadlineTimeForLimitIfUndefined = new XMLAttribute(this,
                                                                                  SharkConstants.EA_USE_FIRST_DEADLINE_TIME_FOR_LIMIT_IF_UNDEFINED,
                                                                                  false,
                                                                                  new String[] {
                                                                                                 "true", "false", ""
                                                                                  }, 2);

      add(attrUnsatisfiedSplitConditionHandlingMode);
      add(attrReevaluateDeadlines);
      add(attrAllowUndefinedVariables);
      add(attrCreateAssignments);
      add(attrCreateDefaultAssignment);
      add(attrHandleAllAssignments);
      add(attrAcceptSingleAssignment);
      add(attrReassignWithUnacceptanceToSingleUser);
      add(attrDeleteOtherAssignments);
      add(attrAssignmentManagerPlugin);
      add(attrMaxAssignments);
      add(attrWorkloadFactor);
      add(attrTransient);
      add(attrDeleteFinished);
      add(attrUseProcessContext);
      add(attrLogXPILWhenFinished);
      add(attrXPILLogFilenameVar);
      add(attrAppendResponsibles);
      add(attrTryStraightForwardMapping);
      add(elDefaultAssignees);
      add(attrAssignToOriginalPerformer);
      add(attrAssignToPerformerOfActivity);
      add(attrDoNotAssignToPerformerOfActivity);
      add(elOverrideProcessContext);
      add(attrAutoCompletion);
      add(attrEvaluateProcessName);
      add(attrEvaluateProcessDescription);
      add(attrEvaluateProcessLimit);
      add(attrEvaluateProcessPriority);
      add(attrEvaluateActivityName);
      add(attrEvaluateActivityDescription);
      add(attrEvaluateActivityLimit);
      add(attrEvaluateActivityPriority);
      add(attrUseFirstDeadlineTimeForLimitIfUndefined);
   }

   protected void handleStructure() {
      Iterator it = eas.toElements().iterator();
      while (it.hasNext()) {
         ExtendedAttribute ea = (ExtendedAttribute) it.next();
         String eaname = ea.getName();
         XMLElement attr = get(eaname);
         if (attr != null) {
            String eaval = ea.getVValue();
            if (eaname.equals(SharkConstants.EA_ASSIGNMENT_MANAGER_DEFAULT_ASSIGNEES)) {
               ((WfVariables) attr).createStructure(eaval);
            } else if (eaname.equals(SharkConstants.EA_OVERRIDE_PROCESS_CONTEXT)) {
               ((WfNameValues) attr).createStructure(eaval);
            } else {
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
   }

   public boolean isForActivity() {
      return isForAct;
   }

   public boolean isForFullyManualActivity() {
      return isForAct && isFullyManualAct;
   }

   public boolean isForModalDialog() {
      return true;
   }

}
