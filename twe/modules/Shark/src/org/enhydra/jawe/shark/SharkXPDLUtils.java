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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.enhydra.jawe.JaWEEAHandler;
import org.enhydra.jawe.XPDLUtils;
import org.enhydra.jawe.components.graph.GraphEAConstants;
import org.enhydra.jawe.shark.business.SharkConstants;
import org.enhydra.jawe.shark.business.WfVariables;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Activities;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ActivitySet;
import org.enhydra.jxpdl.elements.DataField;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;
import org.enhydra.jxpdl.elements.FormalParameter;
import org.enhydra.jxpdl.elements.Package;
import org.enhydra.jxpdl.elements.Participant;
import org.enhydra.jxpdl.elements.Transition;
import org.enhydra.jxpdl.elements.WorkflowProcess;

/**
 * Various XPDL utilities specific for shark/jawe configuration.
 * 
 * @author Sasa Bojanic
 */
public class SharkXPDLUtils extends XPDLUtils {

   public List getReferences(XMLComplexElement eaparent, ExtendedAttribute referenced) {
      return getExtendedAttributeReferences(eaparent, referenced, referenced.getName());
   }

   public List getExtendedAttributeReferences(XMLComplexElement eaparent,
                                              ExtendedAttribute ea,
                                              String referencedName) {
      if (ea instanceof XPDLStringVariable
          || referencedName.startsWith(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX)) {
         if (referencedName.startsWith(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX)) {
            referencedName = referencedName.substring(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX.length());
         }
         List references = new ArrayList();
         getXPDLStringEAReferences(eaparent, ea, referencedName, references);
         return references;
      }
      return super.getExtendedAttributeReferences(eaparent, ea, referencedName);
   }

   protected List getXPDLStringEAReferences(XMLComplexElement el,
                                            ExtendedAttribute ea,
                                            String referencedName,
                                            List references) {
      String fullReferencedName = SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX
                                  + referencedName;
      ExtendedAttribute eal = ((ExtendedAttributes) (el.get("ExtendedAttributes"))).getFirstExtendedAttributeForName(fullReferencedName);
      if (el instanceof WorkflowProcess
          && eal != null && XMLUtil.getWorkflowProcess(ea) == null) {
         return references;
      }

      references.addAll(getXPDLStringEAReferences(el, ea, referencedName));
      if (el instanceof Package) {
         Iterator it = ((Package) el).getWorkflowProcesses().toElements().iterator();
         while (it.hasNext()) {
            references.addAll(getXPDLStringEAReferences((WorkflowProcess) it.next(),
                                                        ea,
                                                        referencedName,
                                                        references));
         }
      } else if (el instanceof WorkflowProcess) {
         Iterator it = ((WorkflowProcess) el).getActivities().toElements().iterator();
         while (it.hasNext()) {
            Activity act = (Activity) it.next();
            references.addAll(getXPDLStringEAReferences(act, ea, referencedName));
         }
         it = ((WorkflowProcess) el).getActivitySets().toElements().iterator();
         while (it.hasNext()) {
            ActivitySet actSet = (ActivitySet) it.next();
            Iterator it2 = actSet.getActivities().toElements().iterator();
            while (it2.hasNext()) {
               Activity act = (Activity) it2.next();
               references.addAll(getXPDLStringEAReferences(act, ea, referencedName));
            }
         }
      }

      return references;
   }

   protected List getXPDLStringEAReferences(XMLComplexElement el,
                                            ExtendedAttribute ea2s,
                                            String referencedName) {
      List references = new ArrayList();

      String postfixProc = "_PROCESS";
      String postfixAct = "_ACTIVITY";

      Iterator it = ((ExtendedAttributes) el.get("ExtendedAttributes")).toElements()
         .iterator();
      while (it.hasNext()) {
         ExtendedAttribute ea = (ExtendedAttribute) it.next();
         if (ea == ea2s) {
            continue;
         }
         boolean canref = false;
         if (el instanceof Activity) {
            if (ea.getName().equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT
                                    + postfixAct)
                || ea.getName().equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT
                                       + postfixAct)
                || ea.getName().equals(SharkConstants.EA_SMTP_ERROR_HANDLER_SUBJECT)
                || ea.getName().equals(SharkConstants.EA_SMTP_ERROR_HANDLER_CONTENT)
                || ea.getName()
                   .startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_SUBJECT)
                || ea.getName()
                   .startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_CONTENT)
                || ea.getName().equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT
                                       + postfixAct)
                || ea.getName().equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT
                                       + postfixAct)) {
               canref = true;
            }
         } else if (el instanceof WorkflowProcess) {
            if (ea.getName().equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT
                                    + postfixProc)
                || ea.getName().equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT
                                       + postfixProc)
                || ea.getName().equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT
                                       + postfixAct)
                || ea.getName().equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT
                                       + postfixAct)
                || ea.getName().equals(SharkConstants.EA_SMTP_ERROR_HANDLER_SUBJECT)
                || ea.getName().equals(SharkConstants.EA_SMTP_ERROR_HANDLER_CONTENT)
                || ea.getName().equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_SUBJECT)
                || ea.getName().equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_CONTENT)
                || ea.getName().equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT
                                       + postfixProc)
                || ea.getName().equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT
                                       + postfixProc)
                || ea.getName().equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT
                                       + postfixAct)
                || ea.getName().equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT
                                       + postfixAct)
                || ea.getName().startsWith(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX)) {
               canref = true;
            }
         } else if (el instanceof Package) {
            if (ea.getName().equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT
                                    + postfixProc)
                || ea.getName().equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT
                                       + postfixProc)
                || ea.getName().equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT
                                       + postfixAct)
                || ea.getName().equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT
                                       + postfixAct)
                || ea.getName().equals(SharkConstants.EA_SMTP_ERROR_HANDLER_SUBJECT)
                || ea.getName().equals(SharkConstants.EA_SMTP_ERROR_HANDLER_CONTENT)
                || ea.getName().equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_SUBJECT)
                || ea.getName().equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_CONTENT)
                || ea.getName().equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT
                                       + postfixProc)
                || ea.getName().equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT
                                       + postfixProc)
                || ea.getName().equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT
                                       + postfixAct)
                || ea.getName().equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT
                                       + postfixAct)
                || ea.getName().startsWith(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX)) {
               canref = true;
            }
         }
         if (canref) {
            if (XMLUtil.getUsingPositions(ea.getVValue(),
                                          "{"
                                                + SharkConstants.XPDL_STRING_PLACEHOLDER_PREFIX
                                                + referencedName + "}",
                                          new HashMap(),
                                          false)
               .size() > 0) {
               references.add(ea.get("Value"));
            }
         }

      }
      return references;
   }

   public void updateExtendedAttributeReferences(List refsEAValues,
                                                 String oldEAName,
                                                 String newEAName) {
      Iterator it = refsEAValues.iterator();
      while (it.hasNext()) {
         XMLElement easmtpv = (XMLElement) it.next();
         if (easmtpv instanceof XMLAttribute) {
            XMLAttribute a = (XMLAttribute) easmtpv;
            String expr = easmtpv.toValue();
            String searchValue = "{"
                                 + SharkConstants.XPDL_STRING_PLACEHOLDER_PREFIX
                                 + oldEAName.substring(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX.length())
                                 + "}";
            String replaceValue = "{"
                                  + SharkConstants.XPDL_STRING_PLACEHOLDER_PREFIX
                                  + newEAName.substring(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX.length())
                                  + "}";
            int varLengthDiff = replaceValue.length() - searchValue.length();

            List positions = XMLUtil.getUsingPositions(expr,
                                                       searchValue,
                                                       new HashMap(),
                                                       false);
            for (int i = 0; i < positions.size(); i++) {
               int pos = ((Integer) positions.get(i)).intValue();
               int realPos = pos + varLengthDiff * i;
               String pref = expr.substring(0, realPos);
               String suff = expr.substring(realPos + searchValue.length());
               expr = pref + replaceValue + suff;
               // System.out.println("Pref="+pref+", suff="+suff+", expr="+expr);
            }
            easmtpv.setValue(expr);
            it.remove();
         }
      }
      super.updateExtendedAttributeReferences(refsEAValues, oldEAName, newEAName);
   }

   public List getParticipantReferences(XMLComplexElement pkgOrWp, String referencedId) {
      if (referencedId.equals("")) {
         return new ArrayList();
      }
      if (pkgOrWp instanceof Package) {
         return getParticipantReferences((Package) pkgOrWp, referencedId);
      }

      return getParticipantReferences((WorkflowProcess) pkgOrWp, referencedId);
   }

   public List getReferences(Package pkg, Participant referenced) {
      if (XMLUtil.getPackage(referenced) != pkg
          && pkg.getParticipant(referenced.getId()) != null) {
         return new ArrayList();
      }

      return getParticipantReferences(pkg, referenced.getId());
   }

   public List getParticipantReferences(Package pkg, String referencedId) {
      List references = new ArrayList();
      if (referencedId.equals("")) {
         return references;
      }

      references.addAll(tGetParticipantReferences(pkg, referencedId));

      Iterator it = pkg.getWorkflowProcesses().toElements().iterator();
      while (it.hasNext()) {
         WorkflowProcess wp = (WorkflowProcess) it.next();
         if (wp.getParticipant(referencedId) == null) {
            references.addAll(getParticipantReferences(wp, referencedId));
         }
      }

      return references;
   }

   public List getReferences(WorkflowProcess wp, Participant referenced) {
      if (XMLUtil.getWorkflowProcess(referenced) == null
          && wp.getParticipant(referenced.getId()) != null) {
         return new ArrayList();
      }
      Package pkg = XMLUtil.getPackage(wp);
      if (XMLUtil.getPackage(referenced) != pkg
          && pkg.getParticipant(referenced.getId()) != null) {
         return new ArrayList();
      }

      return getParticipantReferences(wp, referenced.getId());
   }

   public List getParticipantReferences(WorkflowProcess wp, String referencedId) {
      List references = new ArrayList();
      if (referencedId.equals("")) {
         return references;
      }
      references.addAll(tGetParticipantReferences(wp, referencedId));

      Iterator it = wp.getActivitySets().toElements().iterator();
      while (it.hasNext()) {
         ActivitySet as = (ActivitySet) it.next();
         references.addAll(tGetParticipantReferences(as, referencedId));
      }

      return references;
   }

   protected List tGetParticipantReferences(XMLComplexElement pkgOrWpOrAs,
                                            String referencedId) {
      List references = XMLUtil.tGetParticipantReferences(pkgOrWpOrAs, referencedId);

      String postfixProc = "_PROCESS";
      String postfixAct = "_ACTIVITY";

      if (!(pkgOrWpOrAs instanceof Package)) {
         Iterator it = ((Activities) pkgOrWpOrAs.get("Activities")).toElements()
            .iterator();
         while (it.hasNext()) {
            Activity act = (Activity) it.next();
            int type = act.getActivityType();
            // special extended attributes for shark client applications
            if (type == XPDLConstants.ACTIVITY_TYPE_TASK_APPLICATION
                || type == XPDLConstants.ACTIVITY_TYPE_NO) {
               List eas = act.getExtendedAttributes().toElements();
               for (int i = 0; i < eas.size(); i++) {
                  ExtendedAttribute ea = (ExtendedAttribute) eas.get(i);
                  if (ea.getName()
                     .equals(SharkConstants.EA_SMTP_ERROR_HANDLER_RECIPIENT_PARTICIPANT)
                      || ea.getName()
                         .startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_RECIPIENT_PARTICIPANT)
                      || ea.getName()
                         .equals(SharkConstants.SMTP_LIMIT_HANDLER_RECIPIENT_PARTICIPANT
                                 + postfixAct)) {
                     if (ea.getVValue().equals(referencedId)) {
                        references.add(ea.get("Value"));
                     }
                  }
               }
            }
         }
      }
      if (!(pkgOrWpOrAs instanceof ActivitySet)) {
         List eas = ((ExtendedAttributes) pkgOrWpOrAs.get("ExtendedAttributes")).toElements();
         for (int i = 0; i < eas.size(); i++) {
            ExtendedAttribute ea = (ExtendedAttribute) eas.get(i);
            if (ea.getName()
               .equals(SharkConstants.EA_SMTP_ERROR_HANDLER_RECIPIENT_PARTICIPANT)
                || ea.getName()
                   .equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_RECIPIENT_PARTICIPANT)
                || ea.getName()
                   .equals(SharkConstants.SMTP_LIMIT_HANDLER_RECIPIENT_PARTICIPANT
                           + postfixProc)
                || ea.getName()
                   .equals(SharkConstants.SMTP_LIMIT_HANDLER_RECIPIENT_PARTICIPANT
                           + postfixAct)) {
               if (ea.getVValue().equals(referencedId)) {
                  references.add(ea.get("Value"));
               }
            }
         }
      }
      return references;
   }

   public List getDataFieldReferences(XMLComplexElement pkgOrWp, String referencedId) {
      if (referencedId.equals("")) {
         return new ArrayList();
      }
      if (pkgOrWp instanceof Package) {
         return getDataFieldReferences((Package) pkgOrWp, referencedId);
      }

      return getDataFieldReferences((WorkflowProcess) pkgOrWp, referencedId);
   }

   public List getReferences(Package pkg, DataField referenced) {
      return getDataFieldReferences(pkg, referenced.getId());
   }

   public List getDataFieldReferences(Package pkg, String referencedId) {
      List references = new ArrayList();
      if (referencedId.equals("")) {
         return references;
      }

      references.addAll(XMLUtil.getInitialValueReferences(pkg, referencedId));

      Iterator it = pkg.getWorkflowProcesses().toElements().iterator();
      while (it.hasNext()) {
         WorkflowProcess wp = (WorkflowProcess) it.next();
         if (wp.getDataField(referencedId) == null) {
            references.addAll(getDataFieldReferences(wp, referencedId));
         }
      }

      return references;
   }

   public List getReferences(WorkflowProcess wp, DataField referenced) {
      if (XMLUtil.getWorkflowProcess(referenced) == null
          && (wp.getDataField(referenced.getId()) != null || wp.getFormalParameter(referenced.getId()) != null)) {
         return new ArrayList();
      }

      return getDataFieldReferences(wp, referenced.getId());
   }

   public List getDataFieldReferences(WorkflowProcess wp, String referencedId) {
      List references = new ArrayList();
      if (referencedId.equals("")) {
         return references;
      }

      references.addAll(getVariableReferences(wp, referencedId));

      Iterator it = wp.getActivitySets().toElements().iterator();
      while (it.hasNext()) {
         ActivitySet as = (ActivitySet) it.next();
         references.addAll(getVariableReferences(as, referencedId));
      }

      return references;
   }

   public List getReferences(WorkflowProcess wp, FormalParameter referenced) {
      List references = new ArrayList();
      if (!(referenced.getParent().getParent() instanceof WorkflowProcess)) {
         return references;
      }

      return getFormalParameterReferences(wp, referenced.getId());
   }

   public List getFormalParameterReferences(WorkflowProcess wp, String referencedId) {
      List references = new ArrayList();
      if (referencedId.equals("")) {
         return references;
      }
      if (wp.getDataField(referencedId) != null) {
         return references;
      }

      references.addAll(getVariableReferences(wp, referencedId));

      Iterator it = wp.getActivitySets().toElements().iterator();
      while (it.hasNext()) {
         ActivitySet as = (ActivitySet) it.next();
         references.addAll(getVariableReferences(as, referencedId));
      }

      return references;
   }

   protected List getVariableReferences(XMLCollectionElement wpOrAs, String dfOrFpId) {
      String postfixProc = "_PROCESS";
      String postfixAct = "_ACTIVITY";

      List references = XMLUtil.getVariableReferences(wpOrAs, dfOrFpId);

      Map allVars = XMLUtil.getWorkflowProcess(wpOrAs).getAllVariables();
      Iterator it = ((Activities) wpOrAs.get("Activities")).toElements().iterator();
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         int type = act.getActivityType();
         // special extended attributes for shark client applications
         if (type == XPDLConstants.ACTIVITY_TYPE_TASK_APPLICATION
             || type == XPDLConstants.ACTIVITY_TYPE_NO) {
            List eas = act.getExtendedAttributes().toElements();
            for (int i = 0; i < eas.size(); i++) {
               ExtendedAttribute ea = (ExtendedAttribute) eas.get(i);
               if (ea.getName().equals(SharkConstants.VTP_UPDATE)
                   || ea.getName().equals(SharkConstants.VTP_VIEW)) {
                  if (XMLUtil.getUsingPositions(ea.getVValue(), dfOrFpId, allVars).size() > 0) {
                     references.add(ea.get("Value"));
                  }
               } else if (ea.getName()
                  .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES
                          + postfixAct)
                          || ea.getName()
                             .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS
                                     + postfixAct)
                          || ea.getName()
                             .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS
                                     + postfixAct)
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
                             .equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES
                                     + postfixAct)
                          || ea.getName()
                             .equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENTS
                                     + postfixAct)
                          || ea.getName()
                             .equals(SharkConstants.SMTP_LIMIT_HANDLER_DM_ATTACHMENTS
                                     + postfixAct)) {
                  WfVariables vars = new WfVariables(act, ea.getName(), null, ",", false);
                  vars.createStructure(ea.getVValue());
                  if (vars.getCollectionElement(dfOrFpId) != null) {
                     references.add(ea.get("Value"));
                  }
               } else if (ea.getName()
                  .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT + postfixAct)
                          || ea.getName()
                             .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT
                                     + postfixAct)
                          || ea.getName()
                             .equals(SharkConstants.EA_SMTP_ERROR_HANDLER_SUBJECT)
                          || ea.getName()
                             .equals(SharkConstants.EA_SMTP_ERROR_HANDLER_CONTENT)
                          || ea.getName()
                             .startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_SUBJECT)
                          || ea.getName()
                             .startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_CONTENT)
                          || ea.getName()
                             .equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT
                                     + postfixAct)
                          || ea.getName()
                             .equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT
                                     + postfixAct)) {
                  if (XMLUtil.getUsingPositions(ea.getVValue(),
                                                "{"
                                                      + SharkConstants.PROCESS_VARIABLE_PLACEHOLDER_PREFIX
                                                      + dfOrFpId + "}",
                                                allVars,
                                                false)
                     .size() > 0) {
                     references.add(ea.get("Value"));
                  }
               }
            }
         }
      }
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(wpOrAs);
      List eas = wp.getExtendedAttributes().toElements();
      for (int i = 0; i < eas.size(); i++) {
         ExtendedAttribute ea = (ExtendedAttribute) eas.get(i);
         if (ea.getName()
            .equals(SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_FILENAMEVAR)) {
            if (XMLUtil.getUsingPositions(ea.getVValue(), dfOrFpId, allVars).size() > 0) {
               references.add(ea.get("Value"));
            }
         } else if (ea.getName()
            .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES
                    + postfixProc)
                    || ea.getName()
                       .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS
                               + postfixProc)
                    || ea.getName()
                       .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS
                               + postfixProc)
                    || ea.getName()
                       .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES
                               + postfixAct)
                    || ea.getName()
                       .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS
                               + postfixAct)
                    || ea.getName()
                       .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS
                               + postfixAct)
                    || ea.getName()
                       .equals(SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENT_NAMES)
                    || ea.getName()
                       .equals(SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENTS)
                    || ea.getName()
                       .equals(SharkConstants.EA_SMTP_ERROR_HANDLER_DM_ATTACHMENTS)
                    || ea.getName()
                       .equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENT_NAMES)
                    || ea.getName()
                       .equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENTS)
                    || ea.getName()
                       .equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_DM_ATTACHMENTS)
                    || ea.getName()
                       .equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES
                               + postfixProc)
                    || ea.getName().equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENTS
                                           + postfixProc)
                    || ea.getName()
                       .equals(SharkConstants.SMTP_LIMIT_HANDLER_DM_ATTACHMENTS
                               + postfixProc)
                    || ea.getName()
                       .equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES
                               + postfixAct)
                    || ea.getName().equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENTS
                                           + postfixAct)
                    || ea.getName()
                       .equals(SharkConstants.SMTP_LIMIT_HANDLER_DM_ATTACHMENTS
                               + postfixAct)) {
            WfVariables vars = new WfVariables(wp, ea.getName(), null, ",", false);
            vars.createStructure(ea.getVValue());
            if (vars.getCollectionElement(dfOrFpId) != null) {
               references.add(ea.get("Value"));
            }
         } else if (ea.getName().equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT
                                        + postfixProc)
                    || ea.getName()
                       .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT
                               + postfixProc)
                    || ea.getName()
                       .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT
                               + postfixAct)
                    || ea.getName()
                       .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT
                               + postfixAct)
                    || ea.getName().equals(SharkConstants.EA_SMTP_ERROR_HANDLER_SUBJECT)
                    || ea.getName().equals(SharkConstants.EA_SMTP_ERROR_HANDLER_CONTENT)
                    || ea.getName()
                       .equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_SUBJECT)
                    || ea.getName()
                       .equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_CONTENT)
                    || ea.getName().equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT
                                           + postfixProc)
                    || ea.getName().equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT
                                           + postfixProc)
                    || ea.getName().equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT
                                           + postfixAct)
                    || ea.getName().equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT
                                           + postfixAct)
                    || ea.getName()
                       .startsWith(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX)) {
            if (XMLUtil.getUsingPositions(ea.getVValue(),
                                          "{"
                                                + SharkConstants.PROCESS_VARIABLE_PLACEHOLDER_PREFIX
                                                + dfOrFpId + "}",
                                          allVars,
                                          false)
               .size() > 0) {
               references.add(ea.get("Value"));
            }
         }
      }
      Package pkg = XMLUtil.getPackage(wp);
      eas = pkg.getExtendedAttributes().toElements();
      for (int i = 0; i < eas.size(); i++) {
         ExtendedAttribute ea = (ExtendedAttribute) eas.get(i);
         if (ea.getName()
            .equals(SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_FILENAMEVAR)) {
            if (XMLUtil.getUsingPositions(ea.getVValue(), dfOrFpId, allVars).size() > 0) {
               references.add(ea.get("Value"));
            }
         } else if (ea.getName()
            .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES
                    + postfixProc)
                    || ea.getName()
                       .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS
                               + postfixProc)
                    || ea.getName()
                       .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS
                               + postfixProc)
                    || ea.getName()
                       .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES
                               + postfixAct)
                    || ea.getName()
                       .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS
                               + postfixAct)
                    || ea.getName()
                       .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS
                               + postfixAct)
                    || ea.getName()
                       .equals(SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENT_NAMES)
                    || ea.getName()
                       .equals(SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENTS)
                    || ea.getName()
                       .equals(SharkConstants.EA_SMTP_ERROR_HANDLER_DM_ATTACHMENTS)
                    || ea.getName()
                       .equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENT_NAMES)
                    || ea.getName()
                       .equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENTS)
                    || ea.getName()
                       .equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_DM_ATTACHMENTS)
                    || ea.getName()
                       .equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES
                               + postfixProc)
                    || ea.getName().equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENTS
                                           + postfixProc)
                    || ea.getName()
                       .equals(SharkConstants.SMTP_LIMIT_HANDLER_DM_ATTACHMENTS
                               + postfixProc)
                    || ea.getName()
                       .equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES
                               + postfixAct)
                    || ea.getName().equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENTS
                                           + postfixAct)
                    || ea.getName()
                       .equals(SharkConstants.SMTP_LIMIT_HANDLER_DM_ATTACHMENTS
                               + postfixAct)) {
            WfVariables vars = new WfVariables(wp, ea.getName(), null, ",", false);
            vars.createStructure(ea.getVValue());
            if (vars.getCollectionElement(dfOrFpId) != null) {
               references.add(ea.get("Value"));
            }
         } else if (ea.getName().equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT
                                        + postfixProc)
                    || ea.getName()
                       .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT
                               + postfixProc)
                    || ea.getName()
                       .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT
                               + postfixAct)
                    || ea.getName()
                       .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT
                               + postfixAct)
                    || ea.getName().equals(SharkConstants.EA_SMTP_ERROR_HANDLER_SUBJECT)
                    || ea.getName().equals(SharkConstants.EA_SMTP_ERROR_HANDLER_CONTENT)
                    || ea.getName()
                       .equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_SUBJECT)
                    || ea.getName()
                       .equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_CONTENT)
                    || ea.getName().equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT
                                           + postfixProc)
                    || ea.getName().equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT
                                           + postfixProc)
                    || ea.getName().equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT
                                           + postfixAct)
                    || ea.getName().equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT
                                           + postfixAct)
                    || ea.getName()
                       .startsWith(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX)) {
            if (XMLUtil.getUsingPositions(ea.getVValue(),
                                          "{"
                                                + SharkConstants.PROCESS_VARIABLE_PLACEHOLDER_PREFIX
                                                + dfOrFpId + "}",
                                          allVars,
                                          false)
               .size() > 0) {
               references.add(ea.get("Value"));
            }
         }
      }

      return references;
   }

   public Set getAllExtendedAttributeNames(Collection extAttribs) {
      Set eaNames = new HashSet();
      Iterator it = extAttribs.iterator();
      String postfixProc = "_PROCESS";
      String postfixAct = "_ACTIVITY";
      while (it.hasNext()) {
         ExtendedAttribute ea = (ExtendedAttribute) it.next();
         String eaName = ea.getName();
         XMLElement pp = ea.getParent().getParent();
         if (pp instanceof XMLComplexElement && eaName.equals(JaWEEAHandler.EA_JAWE_TYPE)) {
            continue;
         }
         if (pp instanceof Package
             && (eaName.equals(JaWEEAHandler.EA_EDITING_TOOL)
                 || eaName.equals(JaWEEAHandler.EA_EDITING_TOOL_VERSION)
                 || eaName.equals(JaWEEAHandler.EA_JAWE_CONFIGURATION)
                 || eaName.equals(JaWEEAHandler.EA_JAWE_EXTERNAL_PACKAGE_ID)
                 || eaName.equals(SharkConstants.EA_ALLOW_UNDEFINED_VARIABLES)
                 || eaName.equals(SharkConstants.EA_DYNAMIC_VARIABLE_HANDLING)
                 || eaName.equals(SharkConstants.EA_CHOOSE_NEXT_PERFORMER)
                 || eaName.equals(SharkConstants.EA_MAX_ASSIGNMENTS)
                 || eaName.equals(SharkConstants.EA_WORKLOAD_FACTOR)
                 || eaName.equals(SharkConstants.EA_USE_PROCESS_CONTEXT_ONLY)
                 || eaName.equals(SharkConstants.EA_CREATE_ASSIGNMENTS)
                 || eaName.equals(SharkConstants.EA_TRANSIENT)
                 || eaName.equals(SharkConstants.EA_DELETE_FINISHED)
                 || eaName.equals(SharkConstants.EA_CHECK_FOR_FIRST_ACTIVITY)
                 || eaName.equals(SharkConstants.EA_CHECK_FOR_CONTINUATION)
                 || eaName.equals(SharkConstants.EA_REDIRECT_AFTER_PROCESS_END)
                 || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENT_NAMES)
                 || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENTS)
                 || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_CONTENT)
                 || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_DM_ATTACHMENTS)
                 || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_EXECUTION_MODE)
                 || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_GROUP_EMAIL_ONLY)
                 || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_MODE)
                 || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_SUBJECT)
                 || eaName.equals(SharkConstants.EA_NEWPROC_ERROR_HANDLER_DO_CREATE)
                 || eaName.equals(SharkConstants.EA_FILESYSLOG_ERROR_HANDLER_DO_WRITE)
                 || eaName.equals(SharkConstants.EA_ERROR_HANDLER_RETURN_CODE)
                 || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_RECIPIENT_PARTICIPANT)
                 || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENT_NAMES)
                 || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENTS)
                 || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_CONTENT)
                 || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_DM_ATTACHMENTS)
                 || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_EXECUTION_MODE)
                 || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_GROUP_EMAIL_ONLY)
                 || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_MODE)
                 || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_SUBJECT)
                 || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_RECIPIENT_PARTICIPANT)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES
                                  + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENTS
                                  + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_DM_ATTACHMENTS
                                  + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_EXECUTION_MODE
                                  + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_GROUP_EMAIL_ONLY
                                  + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_MODE + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENTS
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_DM_ATTACHMENTS
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_EXECUTION_MODE
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_GROUP_EMAIL_ONLY
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_MODE + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES
                                  + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS
                                  + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT
                                  + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS
                                  + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_EXECUTION_MODE
                                  + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_GROUP_EMAIL_ONLY
                                  + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_MODE
                                  + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT
                                  + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_EXECUTION_MODE
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_GROUP_EMAIL_ONLY
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_MODE
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT
                                  + postfixAct) || eaName.startsWith(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX))
             || eaName.equals(SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_FILENAMEVAR)
             || eaName.equals(SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_LOG_XPIL)) {
            continue;
         }
         if (pp instanceof WorkflowProcess
             && (eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_WORKFLOW_PARTICIPANT_ORIENTATION)
                 || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_BLOCK_PARTICIPANT_ORIENTATION)
                 || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_WORKFLOW_PARTICIPANT_ORDER)
                 || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_BLOCK_PARTICIPANT_ORDER)
                 || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_START_OF_WORKFLOW)
                 || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_END_OF_WORKFLOW)
                 || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_START_OF_BLOCK)
                 || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_END_OF_BLOCK)
                 || eaName.equals(SharkConstants.EA_ALLOW_UNDEFINED_VARIABLES)
                 || eaName.equals(SharkConstants.EA_DYNAMIC_VARIABLE_HANDLING)
                 || eaName.equals(SharkConstants.EA_CHOOSE_NEXT_PERFORMER)
                 || eaName.equals(SharkConstants.EA_MAX_ASSIGNMENTS)
                 || eaName.equals(SharkConstants.EA_WORKLOAD_FACTOR)
                 || eaName.equals(SharkConstants.EA_USE_PROCESS_CONTEXT_ONLY)
                 || eaName.equals(SharkConstants.EA_CREATE_ASSIGNMENTS)
                 || eaName.equals(SharkConstants.EA_TRANSIENT)
                 || eaName.equals(SharkConstants.EA_DELETE_FINISHED)
                 || eaName.equals(SharkConstants.EA_CHECK_FOR_FIRST_ACTIVITY)
                 || eaName.equals(SharkConstants.EA_CHECK_FOR_CONTINUATION)
                 || eaName.equals(SharkConstants.EA_REDIRECT_AFTER_PROCESS_END)
                 || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENT_NAMES)
                 || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENTS)
                 || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_CONTENT)
                 || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_DM_ATTACHMENTS)
                 || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_EXECUTION_MODE)
                 || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_GROUP_EMAIL_ONLY)
                 || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_MODE)
                 || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_SUBJECT)
                 || eaName.equals(SharkConstants.EA_NEWPROC_ERROR_HANDLER_DO_CREATE)
                 || eaName.equals(SharkConstants.EA_FILESYSLOG_ERROR_HANDLER_DO_WRITE)
                 || eaName.equals(SharkConstants.EA_ERROR_HANDLER_RETURN_CODE)
                 || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_RECIPIENT_PARTICIPANT)
                 || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENT_NAMES)
                 || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENTS)
                 || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_CONTENT)
                 || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_DM_ATTACHMENTS)
                 || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_EXECUTION_MODE)
                 || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_GROUP_EMAIL_ONLY)
                 || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_MODE)
                 || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_SUBJECT)
                 || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_RECIPIENT_PARTICIPANT)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES
                                  + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENTS
                                  + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_DM_ATTACHMENTS
                                  + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_EXECUTION_MODE
                                  + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_GROUP_EMAIL_ONLY
                                  + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_MODE + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENTS
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_DM_ATTACHMENTS
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_EXECUTION_MODE
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_GROUP_EMAIL_ONLY
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_MODE + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES
                                  + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS
                                  + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT
                                  + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS
                                  + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_EXECUTION_MODE
                                  + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_GROUP_EMAIL_ONLY
                                  + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_MODE
                                  + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT
                                  + postfixProc)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_EXECUTION_MODE
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_GROUP_EMAIL_ONLY
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_MODE
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT
                                  + postfixAct) || eaName.startsWith(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX))
             || eaName.equals(SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_FILENAMEVAR)
             || eaName.equals(SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_LOG_XPIL)) {

            continue;
         }
         if (pp instanceof Activity
             && (eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_OFFSET)
                 || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID)
                 || eaName.equals(SharkConstants.EA_MAX_ASSIGNMENTS)
                 || eaName.equals(SharkConstants.EA_WORKLOAD_FACTOR)
                 || eaName.equals(SharkConstants.VTP_UPDATE)
                 || eaName.equals(SharkConstants.VTP_VIEW)
                 || eaName.equals(SharkConstants.EA_CHECK_FOR_COMPLETION)
                 || eaName.equals(SharkConstants.EA_XFORMS_FILE)
                 || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENT_NAMES)
                 || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENTS)
                 || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_CONTENT)
                 || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_DM_ATTACHMENTS)
                 || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_EXECUTION_MODE)
                 || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_GROUP_EMAIL_ONLY)
                 || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_MODE)
                 || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_SUBJECT)
                 || eaName.equals(SharkConstants.EA_NEWPROC_ERROR_HANDLER_DO_CREATE)
                 || eaName.equals(SharkConstants.EA_FILESYSLOG_ERROR_HANDLER_DO_WRITE)
                 || eaName.equals(SharkConstants.EA_ERROR_HANDLER_RETURN_CODE)
                 || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_RECIPIENT_PARTICIPANT)
                 || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENT_NAMES)
                 || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENTS)
                 || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_CONTENT)
                 || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_DM_ATTACHMENTS)
                 || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_EXECUTION_MODE)
                 || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_GROUP_EMAIL_ONLY)
                 || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_MODE)
                 || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_SUBJECT)
                 || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_RECIPIENT_PARTICIPANT)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENTS
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_DM_ATTACHMENTS
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_EXECUTION_MODE
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_GROUP_EMAIL_ONLY
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_MODE + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_EXECUTION_MODE
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_GROUP_EMAIL_ONLY
                                  + postfixAct)
                 || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_MODE
                                  + postfixAct) || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT
                                                                 + postfixAct))) {
            continue;
         }
         if (pp instanceof Transition
             && (eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_BREAK_POINTS) || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE))) {
            continue;
         }
         if (pp instanceof DataField
             && (eaName.equals(SharkConstants.EA_TRANSIENT)
                 || eaName.equals(SharkConstants.EA_DYNAMICSCRIPT)
                 || eaName.equals(SharkConstants.EA_MIN_LENGTH) || eaName.equals(SharkConstants.EA_IS_MANDATORY))) {
            continue;
         }
         eaNames.add(eaName);
      }
      return eaNames;

   }

   public void updateVariableReferences(List refAPsOrPerfsOrCondsOrDlConds,
                                        String oldDfOrFpId,
                                        String newDfOrFpId) {
      String postfixProc = "_PROCESS";
      String postfixAct = "_ACTIVITY";

      Iterator it = refAPsOrPerfsOrCondsOrDlConds.iterator();
      while (it.hasNext()) {
         XMLElement easmtpv = (XMLElement) it.next();
         if (easmtpv instanceof XMLAttribute) {
            XMLAttribute a = (XMLAttribute) easmtpv;
            boolean isAct = a.getParent().getParent().getParent() instanceof Activity;
            if (a.toName().equals("Value")) {
               if (a.getParent() instanceof ExtendedAttribute
                   && (isAct
                       || a.getParent().getParent().getParent() instanceof WorkflowProcess || a.getParent()
                      .getParent()
                      .getParent() instanceof Package)) {
                  String eaName = ((ExtendedAttribute) a.getParent()).getName();
                  if (eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT
                                    + postfixProc)
                      || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT
                                       + postfixProc)
                      || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT
                                       + postfixAct)
                      || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT
                                       + postfixAct)
                      || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_SUBJECT)
                      || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_CONTENT)
                      || (isAct && (eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_SUBJECT) || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_CONTENT)))
                      || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_SUBJECT)
                      || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_CONTENT)
                      || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT
                                       + postfixProc)
                      || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT
                                       + postfixProc)
                      || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT
                                       + postfixAct)
                      || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT
                                       + postfixAct)
                      || (!(a.getParent().getParent().getParent() instanceof Activity) && eaName.startsWith(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX))) {
                     String expr = easmtpv.toValue();
                     String searchValue = "{"
                                          + SharkConstants.PROCESS_VARIABLE_PLACEHOLDER_PREFIX
                                          + oldDfOrFpId + "}";
                     String replaceValue = "{"
                                           + SharkConstants.PROCESS_VARIABLE_PLACEHOLDER_PREFIX
                                           + newDfOrFpId + "}";
                     int varLengthDiff = replaceValue.length() - searchValue.length();

                     Map vars = null;
                     if (XMLUtil.getWorkflowProcess(easmtpv) != null) {
                        vars = XMLUtil.getWorkflowProcess(easmtpv).getAllVariables();
                     } else {
                        vars = XMLUtil.getPossibleVariables(easmtpv);
                     }
                     List positions = XMLUtil.getUsingPositions(expr,
                                                                searchValue,
                                                                vars,
                                                                false);
                     for (int i = 0; i < positions.size(); i++) {
                        int pos = ((Integer) positions.get(i)).intValue();
                        int realPos = pos + varLengthDiff * i;
                        String pref = expr.substring(0, realPos);
                        String suff = expr.substring(realPos + searchValue.length());
                        expr = pref + replaceValue + suff;
                        // System.out.println("Pref="+pref+", suff="+suff+", expr="+expr);
                     }
                     easmtpv.setValue(expr);
                     it.remove();
                  }
               }
            }
         }
      }
      super.updateVariableReferences(refAPsOrPerfsOrCondsOrDlConds,
                                     oldDfOrFpId,
                                     newDfOrFpId);
   }
}
