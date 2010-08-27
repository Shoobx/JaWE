package org.enhydra.jawe.shark;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.enhydra.jawe.JaWEEAHandler;
import org.enhydra.jawe.XPDLUtils;
import org.enhydra.jawe.components.graph.GraphEAConstants;
import org.enhydra.shark.xpdl.XMLCollectionElement;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.XPDLConstants;
import org.enhydra.shark.xpdl.elements.Activities;
import org.enhydra.shark.xpdl.elements.Activity;
import org.enhydra.shark.xpdl.elements.DataField;
import org.enhydra.shark.xpdl.elements.ExtendedAttribute;
import org.enhydra.shark.xpdl.elements.Package;
import org.enhydra.shark.xpdl.elements.Transition;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;

/**
 * Various XPDL utilities specific for shark/jawe configuration.
 * 
 * @author Sasa Bojanic
 */
public class SharkXPDLUtils extends XPDLUtils {

   protected List getVariableReferences(XMLCollectionElement wpOrAs, String dfOrFpId) {
      List references = super.getVariableReferences(wpOrAs, dfOrFpId);

      Map allVars = XMLUtil.getWorkflowProcess(wpOrAs).getAllVariables();
      Iterator it = ((Activities) wpOrAs.get("Activities")).toElements().iterator();
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         int type = act.getActivityType();
         // special extended attributes for shark client applications
         if (type == XPDLConstants.ACTIVITY_TYPE_TOOL
             || type == XPDLConstants.ACTIVITY_MODE_MANUAL) {
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
