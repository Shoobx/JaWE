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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.enhydra.jawe.JaWEEAHandler;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.XPDLUtils;
import org.enhydra.jawe.components.graph.GraphEAConstants;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLInterface;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Activities;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ActivitySet;
import org.enhydra.jxpdl.elements.ActualParameter;
import org.enhydra.jxpdl.elements.ActualParameters;
import org.enhydra.jxpdl.elements.Application;
import org.enhydra.jxpdl.elements.Applications;
import org.enhydra.jxpdl.elements.DataField;
import org.enhydra.jxpdl.elements.DataFields;
import org.enhydra.jxpdl.elements.Deadline;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;
import org.enhydra.jxpdl.elements.FormalParameter;
import org.enhydra.jxpdl.elements.FormalParameters;
import org.enhydra.jxpdl.elements.Package;
import org.enhydra.jxpdl.elements.Participant;
import org.enhydra.jxpdl.elements.TaskApplication;
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

   public List getExtendedAttributeReferences(XMLComplexElement eaparent, ExtendedAttribute ea, String referencedName) {
      boolean isxpdlstr = ea instanceof XPDLStringVariable || referencedName.startsWith(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX);
      if (isxpdlstr || ea instanceof I18nVariable || referencedName.startsWith(SharkConstants.EA_I18N_VARIABLE_PREFIX)) {
         if (referencedName.startsWith(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX)) {
            referencedName = referencedName.substring(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX.length());
         } else if (referencedName.startsWith(SharkConstants.EA_I18N_VARIABLE_PREFIX)) {
            referencedName = referencedName.substring(SharkConstants.EA_I18N_VARIABLE_PREFIX.length());
         }
         List references = new ArrayList();
         getXPDLStringOrI18nEAReferences(eaparent, ea, referencedName, references, isxpdlstr);
         return references;
      }
      return super.getExtendedAttributeReferences(eaparent, ea, referencedName);
   }

   protected List getXPDLStringOrI18nEAReferences(XMLComplexElement el, ExtendedAttribute ea, String referencedName, List references, boolean isxpdlstr) {
      String fullReferencedName = (isxpdlstr ? SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX : SharkConstants.EA_I18N_VARIABLE_PREFIX) + referencedName;
      ExtendedAttribute eal = ((ExtendedAttributes) (el.get("ExtendedAttributes"))).getFirstExtendedAttributeForName(fullReferencedName);
      if (el instanceof WorkflowProcess && eal != null && XMLUtil.getWorkflowProcess(ea) == null) {
         return references;
      }

      references.addAll(getXPDLStringOrI18nEAReferences(el, ea, referencedName, isxpdlstr));
      if (el instanceof Package) {
         Iterator it = ((Package) el).getWorkflowProcesses().toElements().iterator();
         while (it.hasNext()) {
            getXPDLStringOrI18nEAReferences((WorkflowProcess) it.next(), ea, referencedName, references, isxpdlstr);
         }
         references.addAll(getBshJSTAReferences((Package) el, referencedName));
         references.addAll(getInitialValueReferencesForI18nVariable((Package) el, referencedName));
      } else if (el instanceof WorkflowProcess) {
         Iterator it = ((WorkflowProcess) el).getActivities().toElements().iterator();
         while (it.hasNext()) {
            Activity act = (Activity) it.next();
            references.addAll(getXPDLStringOrI18nEAReferences(act, ea, referencedName, isxpdlstr));
         }
         Iterator itt = ((WorkflowProcess) el).getTransitions().toElements().iterator();
         while (itt.hasNext()) {
            Transition t = (Transition) itt.next();
            references.addAll(getXPDLStringOrI18nEAReferences(t, ea, referencedName, isxpdlstr));
         }
         it = ((WorkflowProcess) el).getActivitySets().toElements().iterator();
         while (it.hasNext()) {
            ActivitySet actSet = (ActivitySet) it.next();
            Iterator it2 = actSet.getActivities().toElements().iterator();
            while (it2.hasNext()) {
               Activity act = (Activity) it2.next();
               references.addAll(getXPDLStringOrI18nEAReferences(act, ea, referencedName, isxpdlstr));
            }
            Iterator it2t = actSet.getTransitions().toElements().iterator();
            while (it2t.hasNext()) {
               Transition t = (Transition) it2t.next();
               references.addAll(getXPDLStringOrI18nEAReferences(t, ea, referencedName, isxpdlstr));
            }
         }
         references.addAll(getBshJSTAReferences((WorkflowProcess) el, referencedName));
         references.addAll(getInitialValueReferencesForI18nVariable((WorkflowProcess) el, referencedName));
      }

      return references;
   }

   protected List getXPDLStringOrI18nEAReferences(XMLComplexElement el, ExtendedAttribute ea2s, String referencedName, boolean isxpdlstr) {
      List references = new ArrayList();

      if (!(el instanceof Transition)) {
         String postfixProc = "_PROCESS";
         String postfixAct = "_ACTIVITY";

         Iterator it = ((ExtendedAttributes) el.get("ExtendedAttributes")).toElements().iterator();
         while (it.hasNext()) {
            ExtendedAttribute ea = (ExtendedAttribute) it.next();
            if (ea == ea2s) {
               continue;
            }
            boolean canref = false;
            boolean useplaceholders = true;
            String eaName = ea.getName();
            if (el instanceof Activity) {
               int type = ((Activity) el).getActivityType();
               if (eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT + postfixAct)
                   || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT + postfixAct) || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_SUBJECT)
                   || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_CONTENT) || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_SUBJECT)
                   || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_CONTENT)
                   || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT + postfixAct)
                   || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT + postfixAct) || (isxpdlstr && eaName.equals(SharkConstants.EA_FORM_PAGE_URL))
                   || (eaName.equals(SharkConstants.EA_BEAN_SHELL_SCRIPT_TO_EXECUTE_WHEN_OPENING)
                       && (type == XPDLConstants.ACTIVITY_TYPE_TASK_APPLICATION || type == XPDLConstants.ACTIVITY_TYPE_TASK_SCRIPT
                           || type == XPDLConstants.ACTIVITY_TYPE_NO))) {
                  canref = true;
                  if (eaName.equals(SharkConstants.EA_FORM_PAGE_URL) || eaName.equals(SharkConstants.EA_BEAN_SHELL_SCRIPT_TO_EXECUTE_WHEN_OPENING)) {
                     useplaceholders = false;
                  }
               }
            } else if (el instanceof WorkflowProcess) {
               if (eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT + postfixProc)
                   || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT + postfixProc)
                   || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT + postfixAct)
                   || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT + postfixAct) || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_SUBJECT)
                   || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_CONTENT) || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_SUBJECT)
                   || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_CONTENT) || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT + postfixProc)
                   || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT + postfixProc)
                   || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT + postfixAct)
                   || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT + postfixAct)
                   || eaName.startsWith(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX)) {
                  canref = true;
               }
            } else if (el instanceof Package) {
               if (eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT + postfixProc)
                   || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT + postfixProc)
                   || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT + postfixAct)
                   || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT + postfixAct) || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_SUBJECT)
                   || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_CONTENT) || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_SUBJECT)
                   || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_CONTENT) || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT + postfixProc)
                   || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT + postfixProc)
                   || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT + postfixAct)
                   || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT + postfixAct)
                   || eaName.startsWith(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX)) {
                  canref = true;
               }
            }
            if (canref) {
               String searchFor = referencedName;
               if (useplaceholders) {
                  searchFor = "{" + (isxpdlstr ? SharkConstants.XPDL_STRING_PLACEHOLDER_PREFIX : SharkConstants.I18N_PLACEHOLDER_PREFIX) + searchFor + "}";
               }
               if (XMLUtil.getUsingPositions(ea.getVValue(), searchFor, new HashMap(), false).size() > 0) {
                  references.add(ea.get("Value"));
               }
            }

         }
      }
      if (el instanceof Activity) {
         Activity act = (Activity) el;
         Map allVars = XMLUtil.getWorkflowProcess(act).getAllVariables();
         int type = act.getActivityType();
         // actual parameter (can be expression containing variable, or direct variable
         // reference)
         List aps = new ArrayList();
         if (type == XPDLConstants.ACTIVITY_TYPE_SUBFLOW) {
            aps.addAll(act.getActivityTypes().getImplementation().getImplementationTypes().getSubFlow().getActualParameters().toElements());
         } else if (type == XPDLConstants.ACTIVITY_TYPE_TASK_APPLICATION) {
            TaskApplication ta = act.getActivityTypes().getImplementation().getImplementationTypes().getTask().getTaskTypes().getTaskApplication();
            aps.addAll(ta.getActualParameters().toElements());
         }
         Iterator itap = aps.iterator();
         while (itap.hasNext()) {
            ActualParameter ap = (ActualParameter) itap.next();
            if (XMLUtil.getUsingPositions(ap.toValue(), referencedName, allVars, true, true).size() > 0) {
               references.add(ap);
            }
         }

         Iterator itdls = act.getDeadlines().toElements().iterator();
         while (itdls.hasNext()) {
            Deadline dl = (Deadline) itdls.next();
            String dcond = dl.getDeadlineDuration();
            if (XMLUtil.getUsingPositions(dcond, referencedName, allVars, true, true).size() > 0) {
               references.add(dl.get("DeadlineDuration"));
            }
         }

         // performer (can be expression containing variable, or direct variable
         // reference)
         String perf = act.getFirstPerformer();
         if (XMLUtil.getUsingPositions(perf, referencedName, allVars, true, true).size() > 0) {
            references.add(act.getFirstPerformerObj());
         }

      }
      if (el instanceof Transition) {
         Transition t = (Transition) el;
         Map allVars = XMLUtil.getWorkflowProcess(t).getAllVariables();
         if (XMLUtil.getUsingPositions(t.getCondition().toValue(), referencedName, allVars, true, true).size() > 0) {
            references.add(t.getCondition());
         }
      }
      return references;
   }

   protected List getInitialValueReferencesForI18nVariable(XMLComplexElement pkgOrWp, String i18nVarId) {
      List references = new ArrayList();

      DataFields dfs = null;
      Map allVars = new HashMap();
      if (pkgOrWp instanceof WorkflowProcess) {
         dfs = ((WorkflowProcess) pkgOrWp).getDataFields();
         allVars = ((WorkflowProcess) pkgOrWp).getAllVariables();
      } else {
         dfs = ((Package) pkgOrWp).getDataFields();
         allVars = XMLUtil.getPossibleDataFields(pkgOrWp);
      }
      Iterator it = dfs.toElements().iterator();
      while (it.hasNext()) {
         DataField df = (DataField) it.next();
         if (XMLUtil.getUsingPositions(df.getInitialValue(), i18nVarId, allVars, true, true).size() > 0) {
            references.add(df.get("InitialValue"));
         }
      }
      if (pkgOrWp instanceof WorkflowProcess) {
         it = ((WorkflowProcess) pkgOrWp).getFormalParameters().toElements().iterator();
         while (it.hasNext()) {
            FormalParameter fp = (FormalParameter) it.next();
            if (XMLUtil.getUsingPositions(fp.getInitialValue(), i18nVarId, allVars, true, true).size() > 0) {
               references.add(fp.get("InitialValue"));
            }
         }

      }
      return references;
   }

   public void updateExtendedAttributeReferences(List refsEAValues, String oldEAName, String newEAName) {
      Iterator it = refsEAValues.iterator();
      while (it.hasNext()) {
         XMLElement easmtpvOrApOrPerfOrCondOrDlCondOrIV = (XMLElement) it.next();
         if (easmtpvOrApOrPerfOrCondOrDlCondOrIV instanceof XMLAttribute
             && !((ExtendedAttribute) easmtpvOrApOrPerfOrCondOrDlCondOrIV.getParent()).getName().equals(SharkConstants.EA_FORM_PAGE_URL)
             && !((ExtendedAttribute) easmtpvOrApOrPerfOrCondOrDlCondOrIV.getParent()).getName()
                .equals(SharkConstants.EA_BEAN_SHELL_SCRIPT_TO_EXECUTE_WHEN_OPENING)
             && !((ExtendedAttribute) easmtpvOrApOrPerfOrCondOrDlCondOrIV.getParent()).getName().equals(SharkConstants.EA_SCRIPT)) {
            String expr = easmtpvOrApOrPerfOrCondOrDlCondOrIV.toValue();
            if (oldEAName.indexOf(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX) != -1) {
               String searchValue = "{" + SharkConstants.XPDL_STRING_PLACEHOLDER_PREFIX
                                    + oldEAName.substring(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX.length()) + "}";
               String replaceValue = "{" + SharkConstants.XPDL_STRING_PLACEHOLDER_PREFIX
                                     + newEAName.substring(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX.length()) + "}";
               expr = handleReplace(expr, searchValue, replaceValue, new HashMap(), false);
            } else if (oldEAName.indexOf(SharkConstants.EA_I18N_VARIABLE_PREFIX) != -1) {
               String searchValue = "{" + SharkConstants.I18N_PLACEHOLDER_PREFIX + oldEAName.substring(SharkConstants.EA_I18N_VARIABLE_PREFIX.length()) + "}";
               String replaceValue = "{" + SharkConstants.I18N_PLACEHOLDER_PREFIX + newEAName.substring(SharkConstants.EA_I18N_VARIABLE_PREFIX.length()) + "}";
               expr = handleReplace(expr, searchValue, replaceValue, new HashMap(), false);
            }
            easmtpvOrApOrPerfOrCondOrDlCondOrIV.setValue(expr);
            it.remove();
         } else {
            Map allVars = XMLUtil.getWorkflowProcess(easmtpvOrApOrPerfOrCondOrDlCondOrIV).getAllVariables();
            String expr = easmtpvOrApOrPerfOrCondOrDlCondOrIV.toValue();

            if (oldEAName.indexOf(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX) != -1) {
               String searchValue = oldEAName.substring(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX.length());
               String replaceValue = newEAName.substring(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX.length());
               expr = handleReplace(expr, searchValue, replaceValue, allVars, false);
            } else if (oldEAName.indexOf(SharkConstants.EA_I18N_VARIABLE_PREFIX) != -1) {
               String searchValue = oldEAName.substring(SharkConstants.EA_I18N_VARIABLE_PREFIX.length());
               String replaceValue = newEAName.substring(SharkConstants.EA_I18N_VARIABLE_PREFIX.length());
               expr = handleReplace(expr, searchValue, replaceValue, allVars, false);
            }

            easmtpvOrApOrPerfOrCondOrDlCondOrIV.setValue(expr);
         }
      }
      super.updateExtendedAttributeReferences(refsEAValues, oldEAName, newEAName);
   }

   protected String handleReplace(String expr, String searchValue, String replaceValue, Map allVars, boolean additionalCheck) {
      int varLengthDiff = replaceValue.length() - searchValue.length();

      List positions = XMLUtil.getUsingPositions(expr, searchValue, allVars, additionalCheck, additionalCheck);
      for (int i = 0; i < positions.size(); i++) {
         int pos = ((Integer) positions.get(i)).intValue();
         int realPos = pos + varLengthDiff * i;
         String pref = expr.substring(0, realPos);
         String suff = expr.substring(realPos + searchValue.length());
         expr = pref + replaceValue + suff;
      }
      return expr;
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
      if (XMLUtil.getPackage(referenced) != pkg && pkg.getParticipant(referenced.getId()) != null) {
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
      if (XMLUtil.getWorkflowProcess(referenced) == null && wp.getParticipant(referenced.getId()) != null) {
         return new ArrayList();
      }
      Package pkg = XMLUtil.getPackage(wp);
      if (XMLUtil.getPackage(referenced) != pkg && pkg.getParticipant(referenced.getId()) != null) {
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

   protected List tGetParticipantReferences(XMLComplexElement pkgOrWpOrAs, String referencedId) {
      List references = XMLUtil.tGetParticipantReferences(pkgOrWpOrAs, referencedId);

      String postfixProc = "_PROCESS";
      String postfixAct = "_ACTIVITY";

      if (!(pkgOrWpOrAs instanceof Package)) {
         Iterator it = ((Activities) pkgOrWpOrAs.get("Activities")).toElements().iterator();
         while (it.hasNext()) {
            Activity act = (Activity) it.next();
            int type = act.getActivityType();
            // special extended attributes for shark client applications
            if (type == XPDLConstants.ACTIVITY_TYPE_TASK_APPLICATION || type == XPDLConstants.ACTIVITY_TYPE_NO) {
               List eas = act.getExtendedAttributes().toElements();
               for (int i = 0; i < eas.size(); i++) {
                  ExtendedAttribute ea = (ExtendedAttribute) eas.get(i);
                  String eaName = ea.getName();
                  if (eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_RECIPIENT_PARTICIPANT)
                      || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_RECIPIENT_PARTICIPANT)
                      || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_RECIPIENT_PARTICIPANT + postfixAct)) {
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
            String eaName = ea.getName();
            if (eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_RECIPIENT_PARTICIPANT)
                || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_RECIPIENT_PARTICIPANT)
                || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_RECIPIENT_PARTICIPANT + postfixProc)
                || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_RECIPIENT_PARTICIPANT + postfixAct)) {
               if (ea.getVValue().equals(referencedId)) {
                  references.add(ea.get("Value"));
               }
            }
         }
      }
      return references;
   }

   public List getDataFieldReferences(XMLComplexElement pkgOrWp, String referencedId, XMLInterface xmli) {
      if (referencedId.equals("")) {
         return new ArrayList();
      }
      if (pkgOrWp instanceof Package) {
         return getDataFieldReferences((Package) pkgOrWp, referencedId, xmli);
      }

      return getDataFieldReferences((WorkflowProcess) pkgOrWp, referencedId, xmli);
   }

   public List getReferences(Package pkg, DataField referenced, XMLInterface xmli) {
      return getDataFieldReferences(pkg, referenced.getId(), xmli);
   }

   public List getDataFieldReferences(Package pkg, String referencedId, XMLInterface xmli) {
      List references = new ArrayList();
      if (referencedId.equals("")) {
         return references;
      }

      references.addAll(XMLUtil.getInitialValueReferences(pkg, referencedId));
      references.addAll(getBshJSTAReferences(pkg, referencedId));
      Map allVars = new HashMap();
      Iterator itdf = pkg.getDataFields().toElements().iterator();
      while (itdf.hasNext()) {
         DataField df = (DataField) itdf.next();
         allVars.put(df.getId(), df);
      }
      references.addAll(XMLUtil.getLaneVariableReferences(pkg, referencedId, allVars, xmli));

      Iterator it = pkg.getWorkflowProcesses().toElements().iterator();
      while (it.hasNext()) {
         WorkflowProcess wp = (WorkflowProcess) it.next();
         if (wp.getDataField(referencedId) == null) {
            references.addAll(getDataFieldReferences(wp, referencedId, xmli));
         }
      }

      return references;
   }

   public List getReferences(WorkflowProcess wp, DataField referenced, XMLInterface xmli) {
      if (XMLUtil.getWorkflowProcess(referenced) == null
          && (wp.getDataField(referenced.getId()) != null || wp.getFormalParameter(referenced.getId()) != null)) {
         return new ArrayList();
      }

      return getDataFieldReferences(wp, referenced.getId(), xmli);
   }

   public List getDataFieldReferences(WorkflowProcess wp, String referencedId, XMLInterface xmli) {
      List references = new ArrayList();
      if (referencedId.equals("")) {
         return references;
      }

      references.addAll(getVariableReferences(wp, referencedId, xmli));

      Iterator it = wp.getActivitySets().toElements().iterator();
      while (it.hasNext()) {
         ActivitySet as = (ActivitySet) it.next();
         references.addAll(getVariableReferences(as, referencedId, xmli));
      }

      return references;
   }

   public List getReferences(WorkflowProcess wp, FormalParameter referenced, XMLInterface xmli) {
      List references = new ArrayList();
      if (!(referenced.getParent().getParent() instanceof WorkflowProcess)) {
         return references;
      }

      return getFormalParameterReferences(wp, referenced.getId(), xmli);
   }

   public List getFormalParameterReferences(WorkflowProcess wp, String referencedId, XMLInterface xmli) {
      List references = new ArrayList();
      if (referencedId.equals("")) {
         return references;
      }
      if (wp.getDataField(referencedId) != null) {
         return references;
      }

      references.addAll(getVariableReferences(wp, referencedId, xmli));

      Iterator it = wp.getActivitySets().toElements().iterator();
      while (it.hasNext()) {
         ActivitySet as = (ActivitySet) it.next();
         references.addAll(getVariableReferences(as, referencedId, xmli));
      }

      return references;
   }

   protected List getBshJSTAReferences(XMLComplexElement pkgOrWp, String dfOrFpId) {
      List references = new ArrayList();

      Applications apps = null;
      Map allVars = new HashMap();
      if (pkgOrWp instanceof WorkflowProcess) {
         apps = ((WorkflowProcess) pkgOrWp).getApplications();
         allVars = ((WorkflowProcess) pkgOrWp).getAllVariables();
      } else {
         apps = ((Package) pkgOrWp).getApplications();
         allVars = XMLUtil.getPossibleDataFields(pkgOrWp);
      }
      Iterator it = apps.toElements().iterator();
      while (it.hasNext()) {
         Application app = (Application) it.next();
         ExtendedAttribute tac = app.getExtendedAttributes().getFirstExtendedAttributeForName(SharkConstants.EA_TOOL_AGENT_CLASS);
         if (tac != null && (SharkConstants.TOOL_AGENT_BEAN_SHELL.equals(tac.getVValue()) || SharkConstants.TOOL_AGENT_JAVASCRIPT.equals(tac.getVValue()))) {
            ExtendedAttribute eascr = app.getExtendedAttributes().getFirstExtendedAttributeForName(SharkConstants.EA_SCRIPT);
            if (eascr != null) {
               if (XMLUtil.getUsingPositions(eascr.getVValue(), dfOrFpId, allVars, false).size() > 0) {
                  references.add(eascr.get("Value"));
               }
            }
         }
      }
      return references;

   }

   protected List getVariableReferences(XMLCollectionElement wpOrAs, String dfOrFpId, XMLInterface xmli) {
      String postfixProc = "_PROCESS";
      String postfixAct = "_ACTIVITY";

      List references = XMLUtil.getVariableReferences(wpOrAs, dfOrFpId, xmli);

      Map allVars = XMLUtil.getWorkflowProcess(wpOrAs).getAllVariables();
      Iterator it = ((Activities) wpOrAs.get("Activities")).toElements().iterator();
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         int type = act.getActivityType();
         // special extended attributes for shark client applications
         if (type == XPDLConstants.ACTIVITY_TYPE_TASK_APPLICATION || type == XPDLConstants.ACTIVITY_TYPE_TASK_SCRIPT
             || type == XPDLConstants.ACTIVITY_TYPE_NO) {
            if (SharkUtils.allowFlag(act, SharkConstants.EA_EVALUATE_DESCRIPTION_AS_EXPRESSION_ACTIVITY, false)) {
               if (XMLUtil.getUsingPositions(act.getDescription(), dfOrFpId, allVars).size() > 0) {
                  references.add(act.get("Description"));
               }
            }
            if (SharkUtils.allowFlag(act, SharkConstants.EA_EVALUATE_LIMIT_AS_EXPRESSION_ACTIVITY, false)) {
               if (XMLUtil.getUsingPositions(act.getLimit(), dfOrFpId, allVars).size() > 0) {
                  references.add(act.get("Limit"));
               }
            }
            if (SharkUtils.allowFlag(act, SharkConstants.EA_EVALUATE_NAME_AS_EXPRESSION_ACTIVITY, false)) {
               if (XMLUtil.getUsingPositions(act.getName(), dfOrFpId, allVars).size() > 0) {
                  references.add(act.get("Name"));
               }
            }
            if (SharkUtils.allowFlag(act, SharkConstants.EA_EVALUATE_PRIORITY_AS_EXPRESSION_ACTIVITY, false)) {
               if (XMLUtil.getUsingPositions(act.getPriority(), dfOrFpId, allVars).size() > 0) {
                  references.add(act.get("Priority"));
               }
            }
            String appId = act.getActivityTypes().getImplementation().getImplementationTypes().getTask().getTaskTypes().getTaskApplication().getId();
            WorkflowProcess wp = XMLUtil.getWorkflowProcess(act);
            Application app = XMLUtil.findApplication(JaWEManager.getInstance().getXPDLHandler(), wp, appId);
            if (app != null) {
               String taName = null;
               ExtendedAttribute eataname = app.getExtendedAttributes().getFirstExtendedAttributeForName(SharkConstants.EA_TOOL_AGENT_CLASS);
               if (eataname != null) {
                  taName = eataname.getVValue();
                  if (taName.equals(SharkConstants.TOOL_AGENT_XPATH)) {
                     FormalParameters fps = app.getApplicationTypes().getFormalParameters();
                     FormalParameter resultVariableIds = fps.getFormalParameter(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_RESULT_VARIABLE_IDS);
                     int indofrvids = fps.indexOf(resultVariableIds);
                     if (indofrvids != -1) {
                        ActualParameters aps = act.getActivityTypes()
                           .getImplementation()
                           .getImplementationTypes()
                           .getTask()
                           .getTaskTypes()
                           .getTaskApplication()
                           .getActualParameters();
                        if (aps.size() > indofrvids) {
                           ActualParameter ap = (ActualParameter) aps.get(indofrvids);
                           if (XMLUtil.getUsingPositions(ap.toValue(), dfOrFpId, allVars).size() > 0) {
                              references.add(ap);
                           }
                        }
                     }
                  }
               }

            }

            List eas = act.getExtendedAttributes().toElements();
            for (int i = 0; i < eas.size(); i++) {
               ExtendedAttribute ea = (ExtendedAttribute) eas.get(i);
               String eaName = ea.getName();
               if (eaName.equals(SharkConstants.EA_VTP_UPDATE) || eaName.equals(SharkConstants.EA_VTP_VIEW) || eaName.equals(SharkConstants.EA_VTP_FETCH)
                   || eaName.equals(SharkConstants.EA_TWF_XML_VARIABLE_TO_HANDLE)
                   || eaName.equals(SharkConstants.EA_BEAN_SHELL_SCRIPT_TO_EXECUTE_WHEN_OPENING)) {
                  if (XMLUtil.getUsingPositions(ea.getVValue(), dfOrFpId, allVars).size() > 0) {
                     references.add(ea.get("Value"));
                  }
               } else if (eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES + postfixAct)
                          || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS + postfixAct)
                          || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS + postfixAct)
                          || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENT_NAMES)
                          || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENTS)
                          || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_DM_ATTACHMENTS)
                          || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENT_NAMES)
                          || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENTS)
                          || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_DM_ATTACHMENTS)
                          || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES + postfixAct)
                          || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENTS + postfixAct)
                          || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_DM_ATTACHMENTS + postfixAct)) {
                  WfVariables vars = new WfVariables(act, eaName, null, ",", false);
                  vars.createStructure(ea.getVValue());
                  if (vars.getCollectionElement(dfOrFpId) != null) {
                     references.add(ea.get("Value"));
                  }
               } else if (eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT + postfixAct)
                          || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT + postfixAct)
                          || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_SUBJECT) || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_CONTENT)
                          || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_SUBJECT)
                          || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_CONTENT)
                          || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT + postfixAct)
                          || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT + postfixAct)) {
                  if (XMLUtil.getUsingPositions(ea.getVValue(), "{" + SharkConstants.PROCESS_VARIABLE_PLACEHOLDER_PREFIX + dfOrFpId + "}", allVars, false)
                     .size() > 0) {
                     references.add(ea.get("Value"));
                  }
               } else if (eaName.equals(SharkConstants.EA_OVERRIDE_PROCESS_CONTEXT)) {
                  List<Integer> ups = XMLUtil.getUsingPositions(ea.getVValue(), dfOrFpId + ":", allVars, false);
                  if (ups.size() > 0 && ups.get(0).intValue() == 0) {
                     references.add(ea.get("Value"));
                  }
                  ups = XMLUtil.getUsingPositions(ea.getVValue(), "," + dfOrFpId + ":", allVars, false);
                  if (ups.size() > 0) {
                     references.add(ea.get("Value"));
                  }
               }
            }
         }
      }

      WorkflowProcess wp = XMLUtil.getWorkflowProcess(wpOrAs);
      List eas = wp.getExtendedAttributes().toElements();
      for (int i = 0; i < eas.size(); i++) {
         if (SharkUtils.allowFlag(wp, SharkConstants.EA_EVALUATE_DESCRIPTION_AS_EXPRESSION_PROCESS, false)) {
            if (XMLUtil.getUsingPositions(wp.getProcessHeader().getDescription(), dfOrFpId, allVars).size() > 0) {
               references.add(wp.getProcessHeader().get("Description"));
            }
         }
         if (SharkUtils.allowFlag(wp, SharkConstants.EA_EVALUATE_LIMIT_AS_EXPRESSION_PROCESS, false)) {
            if (XMLUtil.getUsingPositions(wp.getProcessHeader().getLimit(), dfOrFpId, allVars).size() > 0) {
               references.add(wp.getProcessHeader().get("Limit"));
            }
         }
         if (SharkUtils.allowFlag(wp, SharkConstants.EA_EVALUATE_NAME_AS_EXPRESSION_PROCESS, false)) {
            if (XMLUtil.getUsingPositions(wp.getName(), dfOrFpId, allVars).size() > 0) {
               references.add(wp.get("Name"));
            }
         }
         if (SharkUtils.allowFlag(wp, SharkConstants.EA_EVALUATE_PRIORITY_AS_EXPRESSION_PROCESS, false)) {
            if (XMLUtil.getUsingPositions(wp.getProcessHeader().getPriority(), dfOrFpId, allVars).size() > 0) {
               references.add(wp.getProcessHeader().get("Priority"));
            }
         }

         ExtendedAttribute ea = (ExtendedAttribute) eas.get(i);
         String eaName = ea.getName();
         if (eaName.equals(SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_FILENAMEVAR)) {
            if (XMLUtil.getUsingPositions(ea.getVValue(), dfOrFpId, allVars).size() > 0) {
               references.add(ea.get("Value"));
            }
         } else if (eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES + postfixProc)
                    || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS + postfixProc)
                    || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS + postfixProc)
                    || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES + postfixAct)
                    || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS + postfixAct)
                    || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS + postfixAct)
                    || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENT_NAMES) || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENTS)
                    || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_DM_ATTACHMENTS)
                    || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENT_NAMES)
                    || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENTS)
                    || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_DM_ATTACHMENTS)
                    || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES + postfixProc)
                    || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENTS + postfixProc)
                    || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_DM_ATTACHMENTS + postfixProc)
                    || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES + postfixAct)
                    || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENTS + postfixAct)
                    || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_DM_ATTACHMENTS + postfixAct)) {
            WfVariables vars = new WfVariables(wp, eaName, null, ",", false);
            vars.createStructure(ea.getVValue());
            if (vars.getCollectionElement(dfOrFpId) != null) {
               references.add(ea.get("Value"));
            }
         } else if (eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT + postfixProc)
                    || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT + postfixProc)
                    || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT + postfixAct)
                    || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT + postfixAct)
                    || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_SUBJECT) || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_CONTENT)
                    || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_SUBJECT) || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_CONTENT)
                    || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT + postfixProc)
                    || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT + postfixProc)
                    || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT + postfixAct)
                    || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT + postfixAct)
                    || eaName.startsWith(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX)) {
            if (XMLUtil.getUsingPositions(ea.getVValue(), "{" + SharkConstants.PROCESS_VARIABLE_PLACEHOLDER_PREFIX + dfOrFpId + "}", allVars, false)
               .size() > 0) {
               references.add(ea.get("Value"));
            }
         }
      }

      Package pkg = XMLUtil.getPackage(wp);
      eas = pkg.getExtendedAttributes().toElements();
      for (int i = 0; i < eas.size(); i++) {
         ExtendedAttribute ea = (ExtendedAttribute) eas.get(i);
         String eaName = ea.getName();
         if (eaName.equals(SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_FILENAMEVAR)) {
            if (XMLUtil.getUsingPositions(ea.getVValue(), dfOrFpId, allVars).size() > 0) {
               references.add(ea.get("Value"));
            }
         } else if (eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES + postfixProc)
                    || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS + postfixProc)
                    || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS + postfixProc)
                    || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES + postfixAct)
                    || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS + postfixAct)
                    || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS + postfixAct)
                    || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENT_NAMES) || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENTS)
                    || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_DM_ATTACHMENTS)
                    || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENT_NAMES)
                    || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENTS)
                    || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_DM_ATTACHMENTS)
                    || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES + postfixProc)
                    || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENTS + postfixProc)
                    || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_DM_ATTACHMENTS + postfixProc)
                    || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES + postfixAct)
                    || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENTS + postfixAct)
                    || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_DM_ATTACHMENTS + postfixAct)) {
            WfVariables vars = new WfVariables(wp, eaName, null, ",", false);
            vars.createStructure(ea.getVValue());
            if (vars.getCollectionElement(dfOrFpId) != null) {
               references.add(ea.get("Value"));
            }
         } else if (eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT + postfixProc)
                    || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT + postfixProc)
                    || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT + postfixAct)
                    || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT + postfixAct)
                    || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_SUBJECT) || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_CONTENT)
                    || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_SUBJECT) || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_CONTENT)
                    || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT + postfixProc)
                    || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT + postfixProc)
                    || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT + postfixAct)
                    || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT + postfixAct)
                    || eaName.startsWith(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX)) {
            if (XMLUtil.getUsingPositions(ea.getVValue(), "{" + SharkConstants.PROCESS_VARIABLE_PLACEHOLDER_PREFIX + dfOrFpId + "}", allVars, false)
               .size() > 0) {
               references.add(ea.get("Value"));
            }
         }
      }

      if (wpOrAs instanceof WorkflowProcess) {
         references.addAll(getBshJSTAReferences((WorkflowProcess) wpOrAs, dfOrFpId));
      }

      return references;
   }

   public List getReferences(Activity act) {
      return getActivityReferences((XMLCollectionElement) act.getParent().getParent(), act.getId());
   }

   public List getActivityReferences(XMLCollectionElement wpOrAs, String referencedId) {
      List references = XMLUtil.getActivityReferences(wpOrAs, referencedId);

      Iterator it = ((Activities) wpOrAs.get("Activities")).toElements().iterator();
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         int type = act.getActivityType();
         // special extended attributes for shark client applications
         if (type == XPDLConstants.ACTIVITY_TYPE_TASK_APPLICATION || type == XPDLConstants.ACTIVITY_TYPE_TASK_SCRIPT
             || type == XPDLConstants.ACTIVITY_TYPE_NO) {
            List eas = act.getExtendedAttributes().toElements();
            for (int i = 0; i < eas.size(); i++) {
               ExtendedAttribute ea = (ExtendedAttribute) eas.get(i);
               String eaName = ea.getName();
               if (eaName.equals(SharkConstants.EA_BACK_ACTIVITY_DEFINITION) || eaName.equals(SharkConstants.EA_ASSIGN_TO_PERFORMER_OF_ACTIVITY)
                   || eaName.equals(SharkConstants.EA_DO_NOT_ASSIGN_TO_PERFORMER_OF_ACTIVITY)) {
                  if (ea.getVValue().equals(referencedId)) {
                     references.add(ea.get("Value"));
                  }
               }
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
         if (pp instanceof Package || pp instanceof WorkflowProcess || pp instanceof Activity) {
            if (eaName.equals(SharkConstants.EA_I18N_NAME_TRANSLATION_KEY) || eaName.equals(SharkConstants.EA_I18N_DESCRIPTION_TRANSLATION_KEY)
                || eaName.equals(SharkConstants.EA_USE_PROCESS_CONTEXT_ONLY) || eaName.equals(SharkConstants.EA_CREATE_ASSIGNMENTS)
                || eaName.equals(SharkConstants.EA_CREATE_ASSIGNMENTS) || eaName.equals(SharkConstants.EA_CREATE_DEFAULT_ASSIGNMENT)
                || eaName.equals(SharkConstants.EA_HANDLE_ALL_ASSIGNMENTS) || eaName.equals(SharkConstants.EA_ACCEPT_SINGLE_ASSIGNMENT)
                || eaName.equals(SharkConstants.EA_REASSIGN_WITH_UNACCEPTANCE_TO_SINGLE_USER) || eaName.equals(SharkConstants.EA_DELETE_OTHER_ASSIGNMENTS)
                || eaName.equals(SharkConstants.EA_ASSIGNMENT_MANAGER_PLUGIN) || eaName.equals(SharkConstants.EA_REEVALUATE_DEADLINES)
                || eaName.equals(SharkConstants.EA_EVALUATE_LIMIT_AS_EXPRESSION_ACTIVITY)
                || eaName.equals(SharkConstants.EA_EVALUATE_NAME_AS_EXPRESSION_ACTIVITY)
                || eaName.equals(SharkConstants.EA_EVALUATE_PRIORITY_AS_EXPRESSION_ACTIVITY)
                || eaName.equals(SharkConstants.EA_EVALUATE_DESCRIPTION_AS_EXPRESSION_ACTIVITY)
                || eaName.equals(SharkConstants.EA_USE_FIRST_DEADLINE_TIME_FOR_LIMIT_IF_UNDEFINED)
                || eaName.equals(SharkConstants.EA_ASSIGNMENT_MANAGER_APPEND_RESPONSIBLES)
                || eaName.equals(SharkConstants.EA_ASSIGNMENT_MANAGER_TRY_STRAIGHTFORWARD_MAPPING)
                || eaName.equals(SharkConstants.EA_ASSIGNMENT_MANAGER_DEFAULT_ASSIGNEES) || eaName.equals(SharkConstants.EA_MAX_ASSIGNMENTS)
                || eaName.equals(SharkConstants.EA_WORKLOAD_FACTOR) || eaName.equals(SharkConstants.EA_CHECK_FOR_CONTINUATION)
                || eaName.equals(SharkConstants.EA_CHOOSE_NEXT_PERFORMER) || eaName.equals(SharkConstants.EA_ENABLE_REASSIGNMENT)
                || eaName.equals(SharkConstants.EA_HIDE_DYNAMIC_PROPERTIES) || eaName.equals(SharkConstants.EA_READ_ONLY_DYNAMIC_PROPERTIES)
                || eaName.equals(SharkConstants.EA_HIDE_CONTROLS) || eaName.equals(SharkConstants.EA_TURN_OFF_FEATURES)
                || eaName.equals(SharkConstants.EA_ERROR_HANDLER_RETURN_CODE) || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENT_NAMES)
                || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_ATTACHMENTS) || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_CONTENT)
                || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_DM_ATTACHMENTS) || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_EXECUTION_MODE)
                || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_GROUP_EMAIL_ONLY) || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_MODE)
                || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_SUBJECT) || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_RECIPIENT_PARTICIPANT)
                || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_RECIPIENT_VARIABLE) || eaName.equals(SharkConstants.EA_NEWPROC_ERROR_HANDLER_DO_CREATE)
                || eaName.equals(SharkConstants.EA_NEWPROC_ERROR_HANDLER_NEW_PROCESS_PACKAGE_ID)
                || eaName.equals(SharkConstants.EA_NEWPROC_ERROR_HANDLER_NEW_PROCESS_WORKFLOWPROCESS_ID)
                || eaName.equals(SharkConstants.EA_FILESYSLOG_ERROR_HANDLER_DO_WRITE) || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENT_NAMES)
                || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENTS) || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_CONTENT)
                || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_DM_ATTACHMENTS)
                || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_EXECUTION_MODE)
                || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_GROUP_EMAIL_ONLY) || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_MODE)
                || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_SUBJECT)
                || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_RECIPIENT_PARTICIPANT)
                || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_RECIPIENT_VARIABLE)
                || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES + postfixAct)
                || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENTS + postfixAct)
                || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT + postfixAct)
                || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_DM_ATTACHMENTS + postfixAct)
                || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_EXECUTION_MODE + postfixAct)
                || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_GROUP_EMAIL_ONLY + postfixAct)
                || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_MODE + postfixAct) || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT + postfixAct)
                || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_RECIPIENT_PARTICIPANT + postfixAct)
                || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_RECIPIENT_VARIABLE + postfixAct)
                || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES + postfixAct)
                || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS + postfixAct)
                || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT + postfixAct)
                || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS + postfixAct)
                || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_EXECUTION_MODE + postfixAct)
                || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_GROUP_EMAIL_ONLY + postfixAct)
                || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_MODE + postfixAct)
                || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT + postfixAct)) {

               continue;
            }
         }

         if (pp instanceof Package || pp instanceof WorkflowProcess) {
            if (eaName.equals(SharkConstants.EA_UNSATISFIED_SPLIT_CONDITION_HANDLING_MODE) || eaName.equals(SharkConstants.EA_ALLOW_UNDEFINED_VARIABLES)
                || eaName.equals(SharkConstants.EA_TRANSIENT) || eaName.equals(SharkConstants.EA_DELETE_FINISHED)
                || eaName.equals(SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_LOG_XPIL)
                || eaName.equals(SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_FILENAMEVAR) || eaName.equals(SharkConstants.EA_CHECK_FOR_FIRST_ACTIVITY)
                || eaName.equals(SharkConstants.EA_DYNAMIC_VARIABLE_HANDLING) || eaName.equals(SharkConstants.EA_REDIRECT_AFTER_PROCESS_END)
                || eaName.equals(SharkConstants.EA_EVALUATE_LIMIT_AS_EXPRESSION_PROCESS) || eaName.equals(SharkConstants.EA_EVALUATE_NAME_AS_EXPRESSION_PROCESS)
                || eaName.equals(SharkConstants.EA_EVALUATE_PRIORITY_AS_EXPRESSION_PROCESS)
                || eaName.equals(SharkConstants.EA_EVALUATE_DESCRIPTION_AS_EXPRESSION_PROCESS)
                || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES + postfixProc)
                || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_ATTACHMENTS + postfixProc)
                || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT + postfixProc)
                || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_DM_ATTACHMENTS + postfixProc)
                || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_EXECUTION_MODE + postfixProc)
                || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_GROUP_EMAIL_ONLY + postfixProc)
                || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_MODE + postfixProc) || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT + postfixProc)
                || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_RECIPIENT_PARTICIPANT + postfixProc)
                || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_RECIPIENT_VARIABLE + postfixProc)
                || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES + postfixProc)
                || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS + postfixProc)
                || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT + postfixProc)
                || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS + postfixProc)
                || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_EXECUTION_MODE + postfixProc)
                || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_GROUP_EMAIL_ONLY + postfixProc)
                || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_MODE + postfixProc)
                || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT + postfixProc)
                || eaName.startsWith(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX) || eaName.startsWith(SharkConstants.EA_I18N_VARIABLE_PREFIX)) {
               continue;
            }
         }

         if (pp instanceof Package && (eaName.equals(SharkConstants.EA_I18N_XPDL_FOLDER_NAME) || eaName.equals(JaWEEAHandler.EA_EDITING_TOOL)
                                       || eaName.equals(JaWEEAHandler.EA_EDITING_TOOL_VERSION) || eaName.equals(JaWEEAHandler.EA_JAWE_CONFIGURATION)
                                       || eaName.equals(JaWEEAHandler.EA_JAWE_EXTERNAL_PACKAGE_ID))) {
            continue;
         }
         if (pp instanceof WorkflowProcess
             && (eaName.equals(SharkConstants.EA_I18N_PROCESS_DEFINITION_FILE_NAME)
                 || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_WORKFLOW_PARTICIPANT_ORIENTATION)
                 || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_BLOCK_PARTICIPANT_ORIENTATION)
                 || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_WORKFLOW_PARTICIPANT_ORDER)
                 || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_BLOCK_PARTICIPANT_ORDER) || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_START_OF_WORKFLOW)
                 || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_END_OF_WORKFLOW) || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_START_OF_BLOCK)
                 || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_END_OF_BLOCK))) {

            continue;
         }
         if (pp instanceof Activity
             && (eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_OFFSET) || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID)
                 || eaName.equals(SharkConstants.EA_BEAN_SHELL_SCRIPT_TO_EXECUTE_WHEN_OPENING) || eaName.equals(SharkConstants.EA_ASSIGN_TO_ORIGINAL_PERFORMER)
                 || eaName.equals(SharkConstants.EA_ASSIGN_TO_PERFORMER_OF_ACTIVITY) || eaName.equals(SharkConstants.EA_DO_NOT_ASSIGN_TO_PERFORMER_OF_ACTIVITY)
                 || eaName.equals(SharkConstants.EA_OVERRIDE_PROCESS_CONTEXT) || eaName.equals(SharkConstants.EA_AUTO_COMPLETION)
                 || eaName.equals(SharkConstants.EA_VTP_UPDATE) || eaName.equals(SharkConstants.EA_VTP_VIEW) || eaName.equals(SharkConstants.EA_VTP_FETCH)
                 || eaName.equals(SharkConstants.EA_CHECK_FOR_COMPLETION) || eaName.equals(SharkConstants.EA_HTML5FORM_FILE)
                 || eaName.equals(SharkConstants.EA_HTML5FORM_EMBEDDED) || eaName.equals(SharkConstants.EA_HTML5FORM_XSL)
                 || eaName.equals(SharkConstants.EA_HTML_VARIABLE) || eaName.equals(SharkConstants.EA_IS_WEBDAV_FOR_ACTIVITY_VISIBLE)
                 || eaName.equals(SharkConstants.EA_BACK_ACTIVITY_DEFINITION) || eaName.equals(SharkConstants.EA_TWF_XML_VARIABLE_TO_HANDLE)
                 || eaName.equals(SharkConstants.EA_TWF_XML_VARIABLE_ITEM_NAME_PREFIXES) || eaName.equals(SharkConstants.EA_FORM_PAGE_URL)
                 || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENT_NAMES + "_")
                 || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_ATTACHMENTS + "_")
                 || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_CONTENT + "_")
                 || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_DM_ATTACHMENTS + "_")
                 || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_EXECUTION_MODE + "_")
                 || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_GROUP_EMAIL_ONLY + "_")
                 || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_MODE + "_")
                 || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_SUBJECT + "_")
                 || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_RECIPIENT_PARTICIPANT + "_")
                 || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_RECIPIENT_VARIABLE + "_"))) {
            continue;
         }
         if (pp instanceof Transition
             && (eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_BREAK_POINTS) || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE))) {
            continue;
         }
         if (pp instanceof DataField
             && (eaName.equals(SharkConstants.EA_TRANSIENT) || eaName.equals(SharkConstants.EA_DYNAMICSCRIPT) || eaName.equals(SharkConstants.EA_URL_VARIABLE)
                 || eaName.equals(SharkConstants.EA_IS_ACTIVITY_SCOPE_ONLY)
                 || eaName.equals(SharkConstants.EA_EVALUATE_DYNAMICSCRIPT_VARIABLE_FOR_TOOL_AGENT_ACTIVITIES)
                 || eaName.equals(SharkConstants.EA_I18N_NAME_TRANSLATION_KEY) || eaName.equals(SharkConstants.EA_I18N_DESCRIPTION_TRANSLATION_KEY))) {
            continue;
         }
         eaNames.add(eaName);
      }
      return eaNames;

   }

   public void updateVariableReferences(List refAPsOrPerfsOrCondsOrDlCondsOrApps, String oldDfOrFpId, String newDfOrFpId) {
      String postfixProc = "_PROCESS";
      String postfixAct = "_ACTIVITY";

      Iterator it = refAPsOrPerfsOrCondsOrDlCondsOrApps.iterator();
      while (it.hasNext()) {
         XMLElement easmtpv = (XMLElement) it.next();
         if (easmtpv instanceof ActualParameter) {
            ActualParameter ap = (ActualParameter) easmtpv;
            String expr = ap.toValue();
            if (expr.startsWith("\"") && expr.endsWith("\"")) {
               Map vars = null;
               if (XMLUtil.getWorkflowProcess(easmtpv) != null) {
                  vars = XMLUtil.getWorkflowProcess(easmtpv).getAllVariables();
               } else {
                  vars = XMLUtil.getPossibleVariables(easmtpv);
               }

               List positions = XMLUtil.getUsingPositions(expr, oldDfOrFpId, vars);
               int varLengthDiff = newDfOrFpId.length() - oldDfOrFpId.length();
               for (int i = 0; i < positions.size(); i++) {
                  int pos = ((Integer) positions.get(i)).intValue();
                  int realPos = pos + varLengthDiff * i;
                  String pref = expr.substring(0, realPos);
                  String suff = expr.substring(realPos + oldDfOrFpId.length());
                  expr = pref + newDfOrFpId + suff;
               }
               ap.setValue(expr);
            }
         } else if (easmtpv instanceof XMLAttribute) {
            XMLAttribute a = (XMLAttribute) easmtpv;
            boolean isAct = a.getParent().getParent().getParent() instanceof Activity;
            if (a.toName().equals("Value")) {
               if (a.getParent() instanceof ExtendedAttribute
                   && (isAct || a.getParent().getParent().getParent() instanceof WorkflowProcess || a.getParent().getParent().getParent() instanceof Package
                       || a.getParent().getParent().getParent() instanceof Application)) {
                  String eaName = ((ExtendedAttribute) a.getParent()).getName();
                  if (isAct && eaName.equals(SharkConstants.EA_OVERRIDE_PROCESS_CONTEXT)) {
                     String expr = easmtpv.toValue();
                     String searchValue = oldDfOrFpId + ":";
                     String replaceValue = newDfOrFpId + ":";
                     int varLengthDiff = replaceValue.length() - searchValue.length();

                     Map vars = XMLUtil.getWorkflowProcess(easmtpv).getAllVariables();
                     List<Integer> positions = XMLUtil.getUsingPositions(expr, searchValue, vars, false);
                     if (positions.size() > 0 && positions.get(0).intValue() == 0) {
                        int pos = 0;
                        int realPos = pos + varLengthDiff;
                        String pref = expr.substring(0, realPos);
                        String suff = expr.substring(realPos + searchValue.length());
                        expr = pref + replaceValue + suff;
                     }
                     searchValue = "," + searchValue;
                     replaceValue = "," + replaceValue;
                     positions = XMLUtil.getUsingPositions(expr, searchValue, vars, false);
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
                  } else if (eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT + postfixProc)
                             || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT + postfixProc)
                             || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT + postfixAct)
                             || eaName.equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT + postfixAct)
                             || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_SUBJECT) || eaName.equals(SharkConstants.EA_SMTP_ERROR_HANDLER_CONTENT)
                             || (isAct && (eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_SUBJECT)
                                           || eaName.startsWith(SharkConstants.EA_SMTP_DEADLINE_HANDLER_CONTENT)))
                             || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_SUBJECT) || eaName.equals(SharkConstants.EA_SMTP_DEADLINE_HANDLER_CONTENT)
                             || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT + postfixProc)
                             || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT + postfixProc)
                             || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_SUBJECT + postfixAct)
                             || eaName.equals(SharkConstants.SMTP_LIMIT_HANDLER_CONTENT + postfixAct)
                             || (!(a.getParent().getParent().getParent() instanceof Activity)
                                 && eaName.startsWith(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX))
                             || (eaName.equals(SharkConstants.EA_SCRIPT) && a.getParent().getParent().getParent() instanceof Application)) {
                     String expr = easmtpv.toValue();
                     boolean usePrefixes = !eaName.equals(SharkConstants.EA_SCRIPT);
                     String searchValue = usePrefixes ? "{" + SharkConstants.PROCESS_VARIABLE_PLACEHOLDER_PREFIX + oldDfOrFpId + "}" : oldDfOrFpId;
                     String replaceValue = usePrefixes ? "{" + SharkConstants.PROCESS_VARIABLE_PLACEHOLDER_PREFIX + newDfOrFpId + "}" : newDfOrFpId;
                     int varLengthDiff = replaceValue.length() - searchValue.length();

                     Map vars = null;
                     if (XMLUtil.getWorkflowProcess(easmtpv) != null) {
                        vars = XMLUtil.getWorkflowProcess(easmtpv).getAllVariables();
                     } else {
                        vars = XMLUtil.getPossibleVariables(easmtpv);
                     }
                     List positions = XMLUtil.getUsingPositions(expr, searchValue, vars, false);
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
      super.updateVariableReferences(refAPsOrPerfsOrCondsOrDlCondsOrApps, oldDfOrFpId, newDfOrFpId);
   }

}
