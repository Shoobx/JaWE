package org.enhydra.jawe.wfmopen;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.enhydra.jawe.JaWEEAHandler;
import org.enhydra.jawe.XPDLUtils;
import org.enhydra.jawe.components.graph.GraphEAConstants;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.elements.Activity;
import org.enhydra.shark.xpdl.elements.Application;
import org.enhydra.shark.xpdl.elements.ExtendedAttribute;
import org.enhydra.shark.xpdl.elements.Transition;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;

/**
 * Various XPDL utilities specific for shark/jawe configuration.
 * 
 * @author Sasa Bojanic
 */
public class WfMOpenXPDLUtils extends XPDLUtils {

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
         if (pp instanceof org.enhydra.shark.xpdl.elements.Package
             && (eaName.equals(JaWEEAHandler.EA_EDITING_TOOL)
                 || eaName.equals(JaWEEAHandler.EA_EDITING_TOOL_VERSION)
                 || eaName.equals(JaWEEAHandler.EA_JAWE_CONFIGURATION)
                 || eaName.equals(JaWEEAHandler.EA_JAWE_EXTERNAL_PACKAGE_ID)
                 || eaName.equals(WfMOpenConstants.EA_REMOVE_CLOSED_PROCESS)
                 || eaName.equals(WfMOpenConstants.EA_STORE_AUDIT_EVENTS) || eaName.equals(WfMOpenConstants.EA_AUDIT_EVENT_SELECTION))) {
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
                 || eaName.equals(WfMOpenConstants.EA_REMOVE_CLOSED_PROCESS)
                 || eaName.equals(WfMOpenConstants.EA_STORE_AUDIT_EVENTS)
                 || eaName.equals(WfMOpenConstants.EA_AUDIT_EVENT_SELECTION) || eaName.equals(WfMOpenConstants.EA_DEBUG))) {
            continue;
         }
         if (pp instanceof Application
               && eaName.equals(WfMOpenConstants.EA_IMPLEMENTATION)) {
              continue;
           }
         if (pp instanceof Activity
             && (eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_OFFSET)
                 || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID) || eaName.equals(WfMOpenConstants.EA_DEFERRED_CHOICE))) {
            continue;
         }
         if (pp instanceof Transition
             && (eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_BREAK_POINTS) || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE))) {
            continue;
         }
         eaNames.add(eaName);
      }
      return eaNames;

   }

}
