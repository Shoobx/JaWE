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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.enhydra.jawe.JaWEEAHandler;
import org.enhydra.jawe.XPDLUtils;
import org.enhydra.jawe.components.graph.GraphEAConstants;
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
import org.enhydra.jxpdl.elements.FormalParameter;
import org.enhydra.jxpdl.elements.Package;
import org.enhydra.jxpdl.elements.Transition;
import org.enhydra.jxpdl.elements.WorkflowProcess;

/**
 * Various XPDL utilities specific for shark/jawe configuration.
 * 
 * @author Sasa Bojanic
 */
public class SharkXPDLUtils extends XPDLUtils {

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
               }
            }
         }
      }

      return references;
   }

   public Set getAllExtendedAttributeNames(Collection extAttribs) {
      Set eaNames = new HashSet();
      Iterator it = extAttribs.iterator();
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
                 || eaName.equals(SharkConstants.EA_CHECK_FOR_CONTINUATION) || eaName.equals(SharkConstants.EA_REDIRECT_AFTER_PROCESS_END))) {
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
                 || eaName.equals(SharkConstants.EA_CHECK_FOR_CONTINUATION) || eaName.equals(SharkConstants.EA_REDIRECT_AFTER_PROCESS_END))) {
            continue;
         }
         if (pp instanceof Activity
             && (eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_OFFSET)
                 || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID)
                 || eaName.equals(SharkConstants.EA_MAX_ASSIGNMENTS)
                 || eaName.equals(SharkConstants.EA_WORKLOAD_FACTOR)
                 || eaName.equals(SharkConstants.VTP_UPDATE)
                 || eaName.equals(SharkConstants.VTP_VIEW) || eaName.equals(SharkConstants.EA_XFORMS_FILE))) {
            continue;
         }
         if (pp instanceof Transition
             && (eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_BREAK_POINTS) || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE))) {
            continue;
         }
         if (pp instanceof DataField
             && (eaName.equals(SharkConstants.EA_TRANSIENT)
                 || eaName.equals(SharkConstants.EA_MIN_LENGTH) || eaName.equals(SharkConstants.EA_IS_MANDATORY))) {
            continue;
         }
         eaNames.add(eaName);
      }
      return eaNames;

   }

}
