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
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;

public class WfConfigurationElement extends XMLComplexElement {

   protected ExtendedAttributes eas;

   protected boolean isPersisted = false;

   protected boolean isForAct = false;

   public WfConfigurationElement(ExtendedAttributes eas, boolean isForAct) {
      super(eas.getParent(), "WfConfiguration", true);
      this.eas = eas;
      this.isForAct = isForAct;
      notifyMainListeners = false;
      notifyListeners = false;
      handleStructure();
      setReadOnly(eas.isReadOnly());
   }

   public void setValue(String v) {
      if (v == null) {
         if (!isForAct) {
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_UNSATISFIED_SPLIT_CONDITION_HANDLING_MODE, null, null, true, false);
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_ALLOW_UNDEFINED_VARIABLES, null, null, false, false);
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_TRANSIENT, null, null, false, false);
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_DELETE_FINISHED, null, null, false, false);
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_LOG_XPIL, null, null, false, false);
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_FILENAMEVAR, null, null, true, false);
         }
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_CREATE_ASSIGNMENTS, null, null, false, false);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_CREATE_DEFAULT_ASSIGNMENT, null, null, false, false);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_HANDLE_ALL_ASSIGNMENTS, null, null, false, false);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_ACCEPT_SINGLE_ASSIGNMENT, null, null, false, false);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_REASSIGN_WITH_UNACCEPTANCE_TO_SINGLE_USER, null, null, false, false);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_DELETE_OTHER_ASSIGNMENTS, null, null, false, false);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_ASSIGNMENT_MANAGER_PLUGIN, null, null, true, false);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_MAX_ASSIGNMENTS, null, null, true, false);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_WORKLOAD_FACTOR, null, null, true, false);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_USE_PROCESS_CONTEXT_ONLY, null, null, false, false);
      } else {
         if (isReadOnly) {
            throw new RuntimeException("Can't set the value of read only element!");
         }
         this.value = v;
      }
   }

   public XMLAttribute getUnsatisfiedSplitConditionHandlingModeAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_UNSATISFIED_SPLIT_CONDITION_HANDLING_MODE);
   }

   public XMLAttribute getAllowUndefinedVariablesAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_ALLOW_UNDEFINED_VARIABLES);
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

   protected void fillStructure() {
      XMLAttribute attrUnsatisfiedSplitConditionHandlingMode = new XMLAttribute(this,
                                                                                SharkConstants.EA_UNSATISFIED_SPLIT_CONDITION_HANDLING_MODE,
                                                                                false,
                                                                                new String[] {
                                                                                      "", "FINISH_IF_POSSIBLE", "IGNORE", "ROLLBACK"
                                                                                },
                                                                                0);
      XMLAttribute attrAllowUndefinedVariables = new XMLAttribute(this, SharkConstants.EA_ALLOW_UNDEFINED_VARIABLES, false, new String[] {
            "true", "false"
      }, 1);
      XMLAttribute attrCreateAssignments = new XMLAttribute(this, SharkConstants.EA_CREATE_ASSIGNMENTS, false, new String[] {
            "true", "false"
      }, 0);

      XMLAttribute attrCreateDefaultAssignment = new XMLAttribute(this, SharkConstants.EA_CREATE_DEFAULT_ASSIGNMENT, false, new String[] {
            "true", "false"
      }, 0);

      XMLAttribute attrHandleAllAssignments = new XMLAttribute(this, SharkConstants.EA_HANDLE_ALL_ASSIGNMENTS, false, new String[] {
            "true", "false"
      }, 1);

      XMLAttribute attrAcceptSingleAssignment = new XMLAttribute(this, SharkConstants.EA_ACCEPT_SINGLE_ASSIGNMENT, false, new String[] {
            "true", "false"
      }, 1);

      XMLAttribute attrReassignWithUnacceptanceToSingleUser = new XMLAttribute(this,
                                                                               SharkConstants.EA_REASSIGN_WITH_UNACCEPTANCE_TO_SINGLE_USER,
                                                                               false,
                                                                               new String[] {
                                                                                     "true", "false"
                                                                               },
                                                                               1);

      XMLAttribute attrDeleteOtherAssignments = new XMLAttribute(this, SharkConstants.EA_DELETE_OTHER_ASSIGNMENTS, false, new String[] {
            "true", "false"
      }, 1);

      XMLAttribute attrAssignmentManagerPlugin = new XMLAttribute(this, SharkConstants.EA_ASSIGNMENT_MANAGER_PLUGIN, false);

      XMLAttribute attrMaxAssignments = new XMLAttribute(this, SharkConstants.EA_MAX_ASSIGNMENTS, false);
      XMLAttribute attrWorkloadFactor = new XMLAttribute(this, SharkConstants.EA_WORKLOAD_FACTOR, false);

      XMLAttribute attrTransient = new XMLAttribute(this, SharkConstants.EA_TRANSIENT, false, new String[] {
            "true", "false"
      }, 1);
      XMLAttribute attrDeleteFinished = new XMLAttribute(this, SharkConstants.EA_DELETE_FINISHED, false, new String[] {
            "true", "false"
      }, 1);
      XMLAttribute attrUseProcessContext = new XMLAttribute(this, SharkConstants.EA_USE_PROCESS_CONTEXT_ONLY, false, new String[] {
            "true", "false"
      }, 1);

      XMLAttribute attrLogXPILWhenFinished = new XMLAttribute(this, SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_LOG_XPIL, false, new String[] {
            "true", "false"
      }, 1);
      XMLAttribute attrXPILLogFilenameVar = new XMLAttribute(this, SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_FILENAMEVAR, false);

      add(attrUnsatisfiedSplitConditionHandlingMode);
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
            if (eaname.equals(SharkConstants.EA_USE_PROCESS_CONTEXT_ONLY)
                || eaname.equals(SharkConstants.EA_CREATE_ASSIGNMENTS)
                || eaname.equals(SharkConstants.EA_CREATE_DEFAULT_ASSIGNMENT)
                || eaname.equals(SharkConstants.EA_HANDLE_ALL_ASSIGNMENTS)
                || eaname.equals(SharkConstants.EA_ACCEPT_SINGLE_ASSIGNMENT)
                || eaname.equals(SharkConstants.EA_REASSIGN_WITH_UNACCEPTANCE_TO_SINGLE_USER)
                || eaname.equals(SharkConstants.EA_DELETE_OTHER_ASSIGNMENTS)
                || (eaname.equals(SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_LOG_XPIL)
                    || eaname.equals(SharkConstants.EA_ALLOW_UNDEFINED_VARIABLES) || eaname.equals(SharkConstants.EA_TRANSIENT) || eaname.equals(SharkConstants.EA_DELETE_FINISHED)
                                                                                                                                   && !(parent instanceof Activity))) {
               pc++;
            }
            attr.setValue(eaval);
         }
      }
      int toCompNo = (isForAct ? 7 : 11);
      isPersisted = pc >= toCompNo;
   }

   public boolean isPersisted() {
      return isPersisted;
   }

   public void setPersisted(boolean isPersisted) {
      this.isPersisted = isPersisted;
   }

   public boolean isForActivity() {
      return isForAct;
   }

   public boolean isForModalDialog() {
      return true;
   }

}
