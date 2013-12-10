/**
 * Miroslav Popov, Oct 4, 2005 miroslav.popov@gmail.com
 */
package org.enhydra.jawe.base.controller;

import org.enhydra.jawe.JaWEConstants;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XPDLConstants;
import org.enhydra.shark.xpdl.elements.Activities;
import org.enhydra.shark.xpdl.elements.Activity;
import org.enhydra.shark.xpdl.elements.ActivitySet;
import org.enhydra.shark.xpdl.elements.ActivitySets;
import org.enhydra.shark.xpdl.elements.ActualParameter;
import org.enhydra.shark.xpdl.elements.Application;
import org.enhydra.shark.xpdl.elements.Applications;
import org.enhydra.shark.xpdl.elements.DataField;
import org.enhydra.shark.xpdl.elements.DataFields;
import org.enhydra.shark.xpdl.elements.Deadline;
import org.enhydra.shark.xpdl.elements.EnumerationValue;
import org.enhydra.shark.xpdl.elements.ExtendedAttribute;
import org.enhydra.shark.xpdl.elements.ExternalPackage;
import org.enhydra.shark.xpdl.elements.ExternalPackages;
import org.enhydra.shark.xpdl.elements.FormalParameter;
import org.enhydra.shark.xpdl.elements.FormalParameters;
import org.enhydra.shark.xpdl.elements.Member;
import org.enhydra.shark.xpdl.elements.Namespace;
import org.enhydra.shark.xpdl.elements.Package;
import org.enhydra.shark.xpdl.elements.Participant;
import org.enhydra.shark.xpdl.elements.Participants;
import org.enhydra.shark.xpdl.elements.Responsible;
import org.enhydra.shark.xpdl.elements.Tool;
import org.enhydra.shark.xpdl.elements.Transition;
import org.enhydra.shark.xpdl.elements.Transitions;
import org.enhydra.shark.xpdl.elements.TypeDeclaration;
import org.enhydra.shark.xpdl.elements.TypeDeclarations;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;
import org.enhydra.shark.xpdl.elements.WorkflowProcesses;

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
         } else if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_TOOL) {
            jt = jts.getType(JaWEConstants.ACTIVITY_TYPE_TOOL);
         } else if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_BLOCK) {
            jt = jts.getType(JaWEConstants.ACTIVITY_TYPE_BLOCK);
         } else if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_ROUTE) {
            jt = jts.getType(JaWEConstants.ACTIVITY_TYPE_ROUTE);
         } else if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_SUBFLOW) {
            jt = jts.getType(JaWEConstants.ACTIVITY_TYPE_SUBFLOW);
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
         if (jt==null) {
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
      } else if (el instanceof Tool) {
         jt = jts.getType(JaWEConstants.TOOL_DEFAULT);
      } else if (el instanceof Transition) {
         Transition tr = (Transition) el;
         if (tr.getCondition().getType().equals("")) {
            jt = jts.getType(JaWEConstants.TRANSITION_TYPE_UNCONDITIONAL);
         } else if (tr.getCondition().getType().equals(XPDLConstants.CONDITION_TYPE_CONDITION)) {
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
         jt = jts.getType(JaWEConstants.PROCESSES);
      }
      if (jt == null) {
         jt = jts.getType(jts.getDefaultType(el));
      }

      return jt;
   }

}
