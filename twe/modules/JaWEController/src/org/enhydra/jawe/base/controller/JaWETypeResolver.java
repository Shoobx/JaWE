/**
 * Together Workflow Editor
 * Copyright (C) 2010 Together Teamsolutions Co., Ltd. 
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

/**
 * Miroslav Popov, Oct 4, 2005 miroslav.popov@gmail.com
 */
package org.enhydra.jawe.base.controller;

import org.enhydra.jawe.JaWEConstants;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Activities;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ActivitySet;
import org.enhydra.jxpdl.elements.ActivitySets;
import org.enhydra.jxpdl.elements.ActualParameter;
import org.enhydra.jxpdl.elements.Application;
import org.enhydra.jxpdl.elements.Applications;
import org.enhydra.jxpdl.elements.Artifact;
import org.enhydra.jxpdl.elements.Artifacts;
import org.enhydra.jxpdl.elements.Association;
import org.enhydra.jxpdl.elements.Associations;
import org.enhydra.jxpdl.elements.DataField;
import org.enhydra.jxpdl.elements.DataFields;
import org.enhydra.jxpdl.elements.Deadline;
import org.enhydra.jxpdl.elements.EnumerationValue;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExternalPackage;
import org.enhydra.jxpdl.elements.ExternalPackages;
import org.enhydra.jxpdl.elements.FormalParameter;
import org.enhydra.jxpdl.elements.FormalParameters;
import org.enhydra.jxpdl.elements.Lane;
import org.enhydra.jxpdl.elements.Lanes;
import org.enhydra.jxpdl.elements.Member;
import org.enhydra.jxpdl.elements.Namespace;
import org.enhydra.jxpdl.elements.Package;
import org.enhydra.jxpdl.elements.Participant;
import org.enhydra.jxpdl.elements.Participants;
import org.enhydra.jxpdl.elements.Pool;
import org.enhydra.jxpdl.elements.Pools;
import org.enhydra.jxpdl.elements.Responsible;
import org.enhydra.jxpdl.elements.Transition;
import org.enhydra.jxpdl.elements.Transitions;
import org.enhydra.jxpdl.elements.TypeDeclaration;
import org.enhydra.jxpdl.elements.TypeDeclarations;
import org.enhydra.jxpdl.elements.WorkflowProcess;
import org.enhydra.jxpdl.elements.WorkflowProcesses;

/**
 * @author Miroslav Popov
 */
public class JaWETypeResolver {

   protected JaWEController jc;

   protected JaWETypes jts;

   public JaWETypeResolver(JaWEController controller) {
      jc = controller;
      jts = controller.getJaWETypes();
   }

   public JaWEType getJaWEType(XMLElement el) {
      JaWEType jt = jts.compareToTemplate(el);
      if (jt != null) {
         return jt;
      }
      if (el instanceof Activity) {
         Activity act = (Activity) el;
         if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_NO) {
            jt = jts.getType(JaWEConstants.ACTIVITY_TYPE_NO);
         } else if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_TASK_APPLICATION) {
            jt = jts.getType(JaWEConstants.ACTIVITY_TYPE_TASK_APPLICATION);
         } else if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_BLOCK) {
            jt = jts.getType(JaWEConstants.ACTIVITY_TYPE_BLOCK);
         } else if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_ROUTE) {
            if (act.getActivityTypes()
               .getRoute()
               .getGatewayType()
               .equals(XPDLConstants.JOIN_SPLIT_TYPE_PARALLEL)) {
               jt = jts.getType(JaWEConstants.ACTIVITY_TYPE_ROUTE_PARALLEL);
            } else if (act.getActivityTypes()
               .getRoute()
               .getGatewayType()
               .equals(XPDLConstants.JOIN_SPLIT_TYPE_INCLUSIVE)) {
               jt = jts.getType(JaWEConstants.ACTIVITY_TYPE_ROUTE_INCLUSIVE);
            } else {
               jt = jts.getType(JaWEConstants.ACTIVITY_TYPE_ROUTE_EXCLUSIVE);
            }
         } else if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_SUBFLOW) {
            jt = jts.getType(JaWEConstants.ACTIVITY_TYPE_SUBFLOW);
         } else if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_EVENT_START) {
            jt = jts.getType(JaWEConstants.ACTIVITY_TYPE_START);
         } else if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_EVENT_END) {
            jt = jts.getType(JaWEConstants.ACTIVITY_TYPE_END);
         }
      } else if (el instanceof ActivitySet) {
         jt = jts.getType(JaWEConstants.ACTIVITY_SET_TYPE_DEFAULT);
      } else if (el instanceof Activities) {
         jt = jts.getType(JaWEConstants.ACTIVITIES);
      } else if (el instanceof ActivitySets) {
         jt = jts.getType(JaWEConstants.ACTIVITYSETS);
      } else if (el instanceof ActualParameter) {
         jt = jts.getType(JaWEConstants.ACTUAL_PARAMETER_DEFAULT);
      } else if (el instanceof Application) {
         jt = jts.getType(JaWEConstants.APPLICATION_TYPE_DEFAULT);
      } else if (el instanceof Applications) {
         jt = jts.getType(JaWEConstants.APPLICATIONS);
      } else if (el instanceof Artifact) {
         Artifact art = (Artifact) el;
         if (art.getArtifactType().equals(XPDLConstants.ARTIFACT_TYPE_DATAOBJECT)) {
            jt = jts.getType(JaWEConstants.ARTIFACT_TYPE_DATA_OBJECT);
         } else {
            jt = jts.getType(JaWEConstants.ARTIFACT_TYPE_ANNOTATION);
         }
      } else if (el instanceof Artifacts) {
         jt = jts.getType(JaWEConstants.ARTIFACTS);
      } else if (el instanceof Association) {
         jt = jts.getType(JaWEConstants.ASSOCIATION_TYPE_DEFAULT);
      } else if (el instanceof Associations) {
         jt = jts.getType(JaWEConstants.ASSOCIATIONS);
      } else if (el instanceof DataFields) {
         jt = jts.getType(JaWEConstants.DATAFIELDS);
      } else if (el instanceof DataField) {
         jt = jts.getType(JaWEConstants.DATA_FIELD_DEFAULT);
      } else if (el instanceof Deadline) {
         jt = jts.getType(JaWEConstants.DEADLINE_DEFAULT);
      } else if (el instanceof EnumerationValue) {
         jt = jts.getType(JaWEConstants.ENUMERATION_VALUE_DEFAULT);
      } else if (el instanceof ExtendedAttribute) {
         jt = jts.getType(JaWEConstants.EXTENDED_ATTRIBUTE_DEFAULT);
      } else if (el instanceof ExternalPackage) {
         jt = jts.getType(JaWEConstants.EXTERNAL_PACKAGE_DEFAULT);
      } else if (el instanceof ExternalPackages) {
         jt = jts.getType(JaWEConstants.EXTERNALPACKAGES);
      } else if (el instanceof FormalParameter) {
         jt = jts.getType(JaWEConstants.FORMAL_PARAMETER_DEFAULT);
      } else if (el instanceof FormalParameters) {
         jt = jts.getType(JaWEConstants.FORMALPARAMETERS);
      } else if (el instanceof Lane) {
         jt = jts.getType(JaWEConstants.LANE_TYPE_DEFAULT);
      } else if (el instanceof Lanes) {
         jt = jts.getType(JaWEConstants.LANES);
      } else if (el instanceof Member) {
         jt = jts.getType(JaWEConstants.MEMBER_DEFAULT);
      } else if (el instanceof Namespace) {
         jt = jts.getType(JaWEConstants.NAMESPACE_DEFAULT);
      } else if (el instanceof Package) {
         Package pkg = (Package) el;
         if (pkg == jc.getMainPackage()) {
            jt = jts.getType(JaWEConstants.PACKAGE_DEFAULT);
         }
         if (jt == null) {
            if (!pkg.isTransient()) {
               jt = jts.getType(JaWEConstants.PACKAGE_EXTERNAL);
            }
         }
         if (jt == null) {
            jt = jts.getType(JaWEConstants.PACKAGE_TRANSIENT);
         }
      } else if (el instanceof Participant) {
         Participant par = (Participant) el;
         if (par.getParticipantType()
            .getType()
            .equals(XPDLConstants.PARTICIPANT_TYPE_HUMAN)) {
            jt = jts.getType(JaWEConstants.PARTICIPANT_TYPE_HUMAN);
         } else if (par.getParticipantType()
            .getType()
            .equals(XPDLConstants.PARTICIPANT_TYPE_ROLE)) {
            jt = jts.getType(JaWEConstants.PARTICIPANT_TYPE_ROLE);
         } else if (par.getParticipantType()
            .getType()
            .equals(XPDLConstants.PARTICIPANT_TYPE_ORGANIZATIONAL_UNIT)) {
            jt = jts.getType(JaWEConstants.PARTICIPANT_TYPE_ORGANIZATIONAL_UNIT);
         } else if (par.getParticipantType()
            .getType()
            .equals(XPDLConstants.PARTICIPANT_TYPE_RESOURCE)) {
            jt = jts.getType(JaWEConstants.PARTICIPANT_TYPE_RESOURCE);
         } else if (par.getParticipantType()
            .getType()
            .equals(XPDLConstants.PARTICIPANT_TYPE_RESOURCE_SET)) {
            jt = jts.getType(JaWEConstants.PARTICIPANT_TYPE_RESOURCE_SET);
         } else if (par.getParticipantType()
            .getType()
            .equals(XPDLConstants.PARTICIPANT_TYPE_SYSTEM)) {
            jt = jts.getType(JaWEConstants.PARTICIPANT_TYPE_SYSTEM);
         }
      } else if (el instanceof Participants) {
         jt = jts.getType(JaWEConstants.PARTICIPANTS);
      } else if (el instanceof Responsible) {
         jt = jts.getType(JaWEConstants.RESPONSIBLE_DEFAULT);
      } else if (el instanceof Pool) {
         jt = jts.getType(JaWEConstants.POOL_TYPE_DEFAULT);
      } else if (el instanceof Pools) {
         jt = jts.getType(JaWEConstants.POOLS);
      } else if (el instanceof Transition) {
         Transition tr = (Transition) el;
         if (tr.getCondition().getType().equals("")) {
            jt = jts.getType(JaWEConstants.TRANSITION_TYPE_UNCONDITIONAL);
         } else if (tr.getCondition()
            .getType()
            .equals(XPDLConstants.CONDITION_TYPE_CONDITION)) {
            jt = jts.getType(JaWEConstants.TRANSITION_TYPE_CONDITIONAL);
         } else if (tr.getCondition()
            .getType()
            .equals(XPDLConstants.CONDITION_TYPE_OTHERWISE)) {
            jt = jts.getType(JaWEConstants.TRANSITION_TYPE_OTHERWISE);
         } else if (tr.getCondition()
            .getType()
            .equals(XPDLConstants.CONDITION_TYPE_EXCEPTION)) {
            jt = jts.getType(JaWEConstants.TRANSITION_TYPE_EXCEPTION);
         } else if (tr.getCondition()
            .getType()
            .equals(XPDLConstants.CONDITION_TYPE_DEFAULTEXCEPTION)) {
            jt = jts.getType(JaWEConstants.TRANSITION_TYPE_DEFAULTEXCEPTION);
         }
      } else if (el instanceof Transitions) {
         jt = jts.getType(JaWEConstants.TRANSITIONS);
      } else if (el instanceof TypeDeclaration) {
         jt = jts.getType(JaWEConstants.TYPE_DECLARATION_DEFAULT);
      } else if (el instanceof TypeDeclarations) {
         jt = jts.getType(JaWEConstants.TYPEDECLARATIONS);
      } else if (el instanceof WorkflowProcess) {
         jt = jts.getType(JaWEConstants.WORKFLOW_PROCESS_TYPE_DEFAULT);
      } else if (el instanceof WorkflowProcesses) {
         jt = jts.getType(JaWEConstants.WORKFLOWPROCESSES);
      }
      if (jt == null) {
         jt = jts.getType(jts.getDefaultType(el));
      }

      return jt;
   }

}
