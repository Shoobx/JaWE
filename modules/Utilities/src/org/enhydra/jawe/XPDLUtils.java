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

package org.enhydra.jawe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.enhydra.jawe.base.xpdlhandler.XPDLHandler;
import org.enhydra.jawe.components.graph.GraphEAConstants;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLInterface;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.Activities;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ActivitySet;
import org.enhydra.jxpdl.elements.Application;
import org.enhydra.jxpdl.elements.Artifact;
import org.enhydra.jxpdl.elements.Association;
import org.enhydra.jxpdl.elements.Associations;
import org.enhydra.jxpdl.elements.ConnectorGraphicsInfo;
import org.enhydra.jxpdl.elements.ConnectorGraphicsInfos;
import org.enhydra.jxpdl.elements.DataField;
import org.enhydra.jxpdl.elements.DataTypes;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;
import org.enhydra.jxpdl.elements.ExternalPackage;
import org.enhydra.jxpdl.elements.FormalParameter;
import org.enhydra.jxpdl.elements.Lane;
import org.enhydra.jxpdl.elements.NodeGraphicsInfo;
import org.enhydra.jxpdl.elements.NodeGraphicsInfos;
import org.enhydra.jxpdl.elements.Package;
import org.enhydra.jxpdl.elements.Participant;
import org.enhydra.jxpdl.elements.Pool;
import org.enhydra.jxpdl.elements.Responsible;
import org.enhydra.jxpdl.elements.Responsibles;
import org.enhydra.jxpdl.elements.Transition;
import org.enhydra.jxpdl.elements.Transitions;
import org.enhydra.jxpdl.elements.TypeDeclaration;
import org.enhydra.jxpdl.elements.WorkflowProcess;
import org.enhydra.jxpdl.utilities.SequencedHashMap;

/**
 * Various utilities.
 * 
 * @author Sasa Bojanic
 */
public class XPDLUtils {

   public boolean hasCircularTransitions(Set transitions) {
      return XMLUtil.hasCircularTransitions(transitions);
   }

   public boolean isStartingActivity(Activity act) {
      return XMLUtil.isStartingActivity(act);
   }

   public boolean isEndingActivity(Activity act) {
      return XMLUtil.isEndingActivity(act);
   }

   public List getReferences(XMLComplexElement pkgOrWp, XMLComplexElement referenced) {
      return getReferences(pkgOrWp, referenced,JaWEManager.getInstance().getXPDLHandler());
   }

   public List getReferences(XMLComplexElement pkgOrWp,
                                    XMLComplexElement referenced,
                                    XMLInterface xmli) {
      if (pkgOrWp instanceof Package) {
         return getReferences((Package) pkgOrWp, referenced, xmli);
      } else if (pkgOrWp instanceof WorkflowProcess) {
         return getReferences((WorkflowProcess) pkgOrWp, referenced);
      }
      return new ArrayList();
   }

   public List getReferences(Package pkg,
                                    XMLComplexElement referenced,
                                    XMLInterface xmli) {
      if (referenced instanceof Package) {
         return getReferences((Package) referenced, xmli);
      } else if (referenced instanceof TypeDeclaration) {
         return getReferences(pkg, (TypeDeclaration) referenced);
      } else if (referenced instanceof Participant) {
         return getReferences(pkg, (Participant) referenced);
      } else if (referenced instanceof Application) {
         return getReferences(pkg, (Application) referenced);
      } else if (referenced instanceof DataField) {
         return getReferences(pkg, (DataField) referenced);
      } else if (referenced instanceof WorkflowProcess) {
         return getReferences(pkg, (WorkflowProcess) referenced);
      } else if (referenced instanceof Lane) {
         return getReferences(pkg, (Lane) referenced);
      }
      return new ArrayList();
   }

   public List getReferences(WorkflowProcess wp, XMLComplexElement referenced) {
      if (referenced instanceof Participant) {
         return getReferences(wp, (Participant) referenced);
      } else if (referenced instanceof Application) {
         return getReferences(wp, (Application) referenced);
      } else if (referenced instanceof DataField) {
         return getReferences(wp, (DataField) referenced);
      } else if (referenced instanceof WorkflowProcess) {
         return getReferences(wp, (WorkflowProcess) referenced);
      } else if (referenced instanceof FormalParameter) {
         return getReferences(wp, (FormalParameter) referenced);
      } else if (referenced instanceof ActivitySet) {
         return getReferences(wp, (ActivitySet) referenced);
      } else if (referenced instanceof Lane) {
         return getReferences(wp, (Lane) referenced);
      }

      return new ArrayList();
   }

   
   public List getReferences(Package pkg) {
      return getReferences(pkg,JaWEManager.getInstance().getXPDLHandler());
   }

   public List getReferences(Package pkg, XMLInterface xmli) {
      return XMLUtil.getReferences(pkg,xmli);
   }   
   
   public List getAllExternalPackageReferences(Package pkg, Package referenced) {
      return XMLUtil.getAllExternalPackageReferences(pkg, referenced);
   }

   public List getReferences(Package pkg, TypeDeclaration referenced) {
      return XMLUtil.getReferences(pkg,referenced);
   }

   public List getReferences(WorkflowProcess wp, TypeDeclaration referenced) {
      return XMLUtil.getReferences(wp,referenced);
   }

   public List getReferences(TypeDeclaration td, TypeDeclaration referenced) {
      return XMLUtil.getReferences(td,referenced);
   }

   public List getTypeDeclarationReferences(Package pkg, String referencedId) {
      return XMLUtil.getTypeDeclarationReferences(pkg,referencedId);
   }

   public List getReferencingDeclaredTypes(DataTypes dts, String typeDeclarationId) {
      return XMLUtil.getReferencingDeclaredTypes(dts,typeDeclarationId);
   }

   public List getReferences(Artifact referenced) {
      return XMLUtil.getReferences(referenced);
   }

   public List getArtifactReferences(Package pkg, String referencedId) {
      return XMLUtil.getArtifactReferences(pkg,referencedId);
   }

   public List getReferences(Package pkg, Association referenced) {
      return XMLUtil.getReferences(pkg, referenced);
   }

   public List getReferences(WorkflowProcess wp, Association referenced) {
      return XMLUtil.getReferences(wp, referenced);
   }

   public List getReferences(ActivitySet as, Association referenced) {
      return XMLUtil.getReferences(as, referenced);
   }

   public List getParticipantReferences(XMLComplexElement pkgOrWp, String referencedId) {
      return XMLUtil.getParticipantReferences(pkgOrWp,referencedId);   
   }

   public List getReferences(Package pkg, Participant referenced) {
      return XMLUtil.getReferences(pkg,referenced);
   }

   public List getParticipantReferences(Package pkg, String referencedId) {
      return XMLUtil.getParticipantReferences(pkg,referencedId);
   }

   public List getReferences(WorkflowProcess wp, Participant referenced) {
      return XMLUtil.getReferences(wp,referenced);
   }

   public List getParticipantReferences(WorkflowProcess wp, String referencedId) {
      return XMLUtil.getParticipantReferences(wp,referencedId);
   }

   public List getApplicationReferences(XMLComplexElement pkgOrWp, String referencedId) {
      return XMLUtil.getApplicationReferences(pkgOrWp,referencedId);
   }

   public List getReferences(Package pkg, Application referenced) {
      return XMLUtil.getReferences(pkg,referenced);
   }

   public List getApplicationReferences(Package pkg, String referencedId) {
      return XMLUtil.getApplicationReferences(pkg,referencedId);
   }

   public List getReferences(WorkflowProcess wp, Application referenced) {
      return XMLUtil.getReferences(wp,referenced);
   }

   public List getApplicationReferences(WorkflowProcess wp, String referencedId) {
      return XMLUtil.getApplicationReferences(wp,referencedId);
   }

   public List getLaneReferences(XMLComplexElement pkgOrWp, String referencedId) {
      return XMLUtil.getLaneReferences(pkgOrWp,referencedId);
   }

   public List getReferences(Package pkg, Lane referenced) {
      return XMLUtil.getReferences(pkg,referenced);
   }

   public List getLaneReferences(Package pkg, String referencedId) {
      return XMLUtil.getLaneReferences(pkg,referencedId);
   }

   public List getReferences(WorkflowProcess wp, Lane referenced) {
      return XMLUtil.getReferences(wp, referenced);
   }

   public List getLaneReferences(WorkflowProcess wp, String referencedId) {
      return XMLUtil.getLaneReferences(wp,referencedId);
   }

   public List getDataFieldReferences(XMLComplexElement pkgOrWp, String referencedId) {
      return XMLUtil.getDataFieldReferences(pkgOrWp, referencedId);
   }

   public List getReferences(Package pkg, DataField referenced) {
      return XMLUtil.getReferences(pkg, referenced);
   }

   public List getDataFieldReferences(Package pkg, String referencedId) {
      return XMLUtil.getDataFieldReferences(pkg,referencedId);
   }

   public List getReferences(WorkflowProcess wp, DataField referenced) {
      return XMLUtil.getReferences(wp,referenced);
   }

   public List getDataFieldReferences(WorkflowProcess wp, String referencedId) {
      return XMLUtil.getDataFieldReferences(wp,referencedId);
   }

   public List getReferences(Package pkg, WorkflowProcess referenced) {
      return XMLUtil.getReferences(pkg,referenced);
   }

   public List getWorkflowProcessReferences(Package pkg, String referencedId) {
      return XMLUtil.getWorkflowProcessReferences(pkg,referencedId);
   }

   public List getReferences(WorkflowProcess wp, WorkflowProcess referenced) {
      return XMLUtil.getReferences(wp,referenced);
   }

   public List getWorkflowProcessReferences(WorkflowProcess wp, String referencedId) {
      return XMLUtil.getWorkflowProcessReferences(wp,referencedId);
   }

   public List getReferences(WorkflowProcess wp, FormalParameter referenced) {
      return XMLUtil.getReferences(wp,referenced);
   }

   public List getFormalParameterReferences(WorkflowProcess wp, String referencedId) {
      return XMLUtil.getFormalParameterReferences(wp,referencedId);
   }

   public List getReferences(WorkflowProcess wp, ActivitySet referenced) {
      return XMLUtil.getReferences(wp,referenced);
   }

   public List getActivitySetReferences(WorkflowProcess wp, String referencedId) {
      return XMLUtil.getActivitySetReferences(wp,referencedId);
   }

   public List getReferences(ActivitySet as, ActivitySet referenced) {
      return XMLUtil.getReferences(as,referenced);
   }

   public List getReferences(ActivitySet as, String referencedId) {
      return XMLUtil.getReferences(as,referencedId);
   }

   public List getReferences(Activity act) {
      return XMLUtil.getReferences(act);
   }

   public List getActivityReferences(XMLCollectionElement wpOrAs, String referencedId) {
      return XMLUtil.getActivityReferences(wpOrAs,referencedId);
   }

   public List getReferences(Transition tra) {
      return XMLUtil.getReferences(tra);
   }

   public boolean correctSplitsAndJoins(Package pkg) {
      return XMLUtil.correctSplitsAndJoins(pkg);
   }

   public boolean correctSplitsAndJoins(WorkflowProcess wp) {
      return XMLUtil.correctSplitsAndJoins(wp);
   }

   public boolean correctSplitsAndJoins(List acts) {
      return XMLUtil.correctSplitsAndJoins(acts);
   }

   public boolean correctSplitAndJoin(Activity act) {
      return XMLUtil.correctSplitAndJoin(act);
   }

   public void updateActivityReferences(List refsTrasToFrom,
                                        String oldActId,
                                        String newActId) {
      XMLUtil.updateActivityReferences(refsTrasToFrom, oldActId, newActId);
   }

   public void updateArtifactReferences(List refsAsocsTargetSource,
                                        String oldArtId,
                                        String newArtId) {
      XMLUtil.updateArtifactReferences(refsAsocsTargetSource, oldArtId, newArtId);
   }

   public void updateActivityOnTransitionIdChange(Activities acts,
                                                  String actFromId,
                                                  String oldTraId,
                                                  String newTraId) {
      XMLUtil.updateActivityOnTransitionIdChange(acts, actFromId, oldTraId, newTraId);
   }

   public void updateActivityOnTransitionIdChange(Activity act,
                                                  String oldTraId,
                                                  String newTraId) {
      XMLUtil.updateActivityOnTransitionIdChange(act, oldTraId, newTraId);
   }

   public void updateActivitiesOnTransitionFromChange(Activities acts,
                                                      String traId,
                                                      String traOldFromId,
                                                      String traNewFromId) {
      XMLUtil.updateActivitiesOnTransitionFromChange(acts, traId, traOldFromId, traNewFromId);
   }

   public void updateActivitiesOnTransitionToChange(Activities acts,
                                                    String traId,
                                                    String traOldToId,
                                                    String traNewToId) {
      XMLUtil.updateActivitiesOnTransitionFromChange(acts, traId, traOldToId, traNewToId);
   }

   public void removeTransitionsAndAssociationsForActivity(Activity act) {
      XMLUtil.removeTransitionsForActivity(act);
      XMLUtil.removeAssociationsForActivityOrArtifact(act);
   }

   public void removeAssociationsForArtifact(Artifact art) {
      XMLUtil.removeAssociationsForActivityOrArtifact(art);
   }

   public void removeTransitionsAndAssociationsForActivities(List acts) {
      XMLUtil.removeTransitionsForActivities(acts);
      XMLUtil.removeAssociationsForActivitiesOrArtifacts(acts);
   }

   public void removeAssociationsForArtifacts(List arts) {
      XMLUtil.removeAssociationsForActivitiesOrArtifacts(arts);
   }

   public List getTransitions(Transitions tras, String actId, boolean isToAct) {
      return XMLUtil.getTransitions(tras, actId, isToAct);
   }

   public List getAssociations(Associations asocs, String aId, boolean isToA) {
      return XMLUtil.getAssociations(asocs, aId, isToA);
   }

   public void removeNestedLanesForLanes(List lanes) {
      XMLUtil.removeNestedLanesForLanes(lanes);
   }

   public void removeNestedLanesForLane(Lane lane) {
      XMLUtil.removeNestedLanesForLane(lane);
   }

   public void createPoolsForProcesses(List wps) {
      XMLUtil.createPoolsForProcesses(wps);
   }

   public Pool createPoolForProcess(WorkflowProcess wp) {
      return XMLUtil.createPoolForProcess(wp);
   }

   public void removePoolsForProcesses(List wps) {
      XMLUtil.removePoolsForProcesses(wps);
   }

   public Pool removePoolForProcess(WorkflowProcess wp) {
      return XMLUtil.removePoolForProcess(wp);
   }

   public Pool getPoolForProcess(WorkflowProcess wp) {
      return XMLUtil.getPoolForProcessOrActivitySet(wp);
   }

   public WorkflowProcess getProcessForPool(Pool pool) {
      return XMLUtil.getProcessForPool(pool);
   }

   public void createPoolsForActivitySets(List ass) {
      XMLUtil.createPoolsForActivitySets(ass);
   }

   public Pool createPoolForActivitySet(ActivitySet as) {
      return XMLUtil.createPoolForActivitySet(as);
   }

   public void removePoolsForActivitySets(List ass) {
      XMLUtil.removePoolsForActivitySets(ass);
   }

   public Pool removePoolForActivitySet(ActivitySet as) {
      return XMLUtil.removePoolForActivitySet(as);
   }

   public Pool getPoolForActivitySet(ActivitySet as) {
      return XMLUtil.getPoolForProcessOrActivitySet(as);
   }

   public ActivitySet getActivitySetForPool(Pool pool) {
      return XMLUtil.getActivitySetForPool(pool);
   }

   public Pool getPoolForProcessOrActivitySet(XMLCollectionElement wpOrAs) {
      return XMLUtil.getPoolForProcessOrActivitySet(wpOrAs);
   }

   public void removeArtifactAndAssociationsForProcessesOrActivitySets(List wpsOrAss) {
      XMLUtil.removeArtifactAndAssociationsForProcessesOrActivitySets(wpsOrAss);
   }

   public List removeArtifactAndAssociationsForProcessOrActivitySet(XMLCollectionElement wpOrAs) {
      return XMLUtil.removeArtifactAndAssociationsForProcessOrActivitySet(wpOrAs);
   }

   
   public void updateTypeDeclarationReferences(List refDeclaredTypes, String newTdId) {
      XMLUtil.updateTypeDeclarationReferences(refDeclaredTypes, newTdId);
   }

   public void updateApplicationReferences(List refTools, String newAppId) {
      XMLUtil.updateApplicationReferences(refTools, newAppId);
   }

   public void updateLaneReferences(List refNGIsAndNestedLanes, String newLaneId) {
      XMLUtil.updateLaneReferences(refNGIsAndNestedLanes, newLaneId);
   }

   public void updateParticipantReferences(List refPerfsAndResps, String newParId) {
      XMLUtil.updateParticipantReferences(refPerfsAndResps, newParId);
   }

   public void updateWorkflowProcessReferences(List refSbflwsOrPools, String newWpId) {
      XMLUtil.updateWorkflowProcessReferences(refSbflwsOrPools, newWpId);
   }

   public void updateActivitySetReferences(List refBlocks, String newAsId) {
      XMLUtil.updateActivitySetReferences(refBlocks, newAsId);
   }

   public void updateVariableReferences(List refAPsOrPerfsOrCondsOrDlConds,
                                        String oldDfOrFpId,
                                        String newDfOrFpId) {
      XMLUtil.updateVariableReferences(refAPsOrPerfsOrCondsOrDlConds, oldDfOrFpId, newDfOrFpId);
   }

   public List getActivities(Package pkg, List types) {
      return XMLUtil.getActivities(pkg, types);
   }

   public List getActivities(WorkflowProcess wp, List types) {
      return XMLUtil.getActivities(wp, types);
   }

   public List getActivities(Activities acts, List types) {
      return XMLUtil.getActivities(acts, types);
   }

   public SequencedHashMap getPossibleResponsibles(Responsibles resp, Responsible rsp) {
      return XMLUtil.getPossibleResponsibles(resp, rsp, JaWEManager.getInstance().getXPDLHandler());
   }

   public boolean doesCrossreferenceExist(Package pkg) {
      return XMLUtil.doesCrossreferenceExist(pkg, JaWEManager.getInstance().getXPDLHandler());
   }

   public Set getAllExtendedAttributeNames(XMLComplexElement cel, XPDLHandler xpdlh) {
      Set extAttribNames = new HashSet();

      Iterator it = xpdlh.getAllPackages().iterator();
      while (it.hasNext()) {
         extAttribNames.addAll(getAllExtendedAttributeNames((Package) it.next(), cel));
      }
      return extAttribNames;
   }

   public Set getAllExtendedAttributeNames(Package pkg, XMLComplexElement cel) {
      Set extAttribNames = new HashSet();
      if (cel instanceof Activity) {
         Iterator it = pkg.getWorkflowProcesses().toElements().iterator();
         while (it.hasNext()) {
            WorkflowProcess wp = (WorkflowProcess) it.next();
            extAttribNames.addAll(getAllExtendedAttributeNamesForElements(wp.getActivities()
               .toElements()));

            Iterator asets = wp.getActivitySets().toElements().iterator();
            while (asets.hasNext()) {
               ActivitySet as = (ActivitySet) asets.next();
               extAttribNames.addAll(getAllExtendedAttributeNamesForElements(as.getActivities()
                  .toElements()));
            }
         }
      } else if (cel instanceof Application) {
         extAttribNames.addAll(getAllExtendedAttributeNamesForElements(pkg.getApplications()
            .toElements()));
         Iterator it = pkg.getWorkflowProcesses().toElements().iterator();
         while (it.hasNext()) {
            WorkflowProcess wp = (WorkflowProcess) it.next();
            extAttribNames.addAll(getAllExtendedAttributeNamesForElements(wp.getApplications()
               .toElements()));
         }
      } else if (cel instanceof DataField) {
         extAttribNames.addAll(getAllExtendedAttributeNamesForElements(pkg.getDataFields()
            .toElements()));
         Iterator it = pkg.getWorkflowProcesses().toElements().iterator();
         while (it.hasNext()) {
            WorkflowProcess wp = (WorkflowProcess) it.next();
            extAttribNames.addAll(getAllExtendedAttributeNamesForElements(wp.getDataFields()
               .toElements()));
         }
      } else if (cel instanceof ExternalPackage) {
         extAttribNames.addAll(getAllExtendedAttributeNamesForElements(pkg.getExternalPackages()
            .toElements()));
      } else if (cel instanceof Package) {
         extAttribNames.addAll(getAllExtendedAttributeNames(pkg.getExtendedAttributes()
            .toElements()));
      } else if (cel instanceof Participant) {
         extAttribNames.addAll(getAllExtendedAttributeNamesForElements(pkg.getParticipants()
            .toElements()));
         Iterator it = pkg.getWorkflowProcesses().toElements().iterator();
         while (it.hasNext()) {
            WorkflowProcess wp = (WorkflowProcess) it.next();
            extAttribNames.addAll(getAllExtendedAttributeNamesForElements(wp.getParticipants()
               .toElements()));
         }
      } else if (cel instanceof Transition) {
         Iterator it = pkg.getWorkflowProcesses().toElements().iterator();
         while (it.hasNext()) {
            WorkflowProcess wp = (WorkflowProcess) it.next();
            extAttribNames.addAll(getAllExtendedAttributeNamesForElements(wp.getTransitions()
               .toElements()));
            Iterator asets = wp.getActivitySets().toElements().iterator();
            while (asets.hasNext()) {
               ActivitySet as = (ActivitySet) asets.next();
               extAttribNames.addAll(getAllExtendedAttributeNamesForElements(as.getTransitions()
                  .toElements()));
            }
         }
      } else if (cel instanceof TypeDeclaration) {
         extAttribNames.addAll(getAllExtendedAttributeNamesForElements(pkg.getTypeDeclarations()
            .toElements()));
      } else if (cel instanceof WorkflowProcess) {
         extAttribNames.addAll(getAllExtendedAttributeNamesForElements(pkg.getWorkflowProcesses()
            .toElements()));
      }
      return extAttribNames;
   }

   public Set getAllExtendedAttributeNamesForElements(Collection elements) {
      Set s = new HashSet();
      Iterator it = elements.iterator();
      while (it.hasNext()) {
         XMLComplexElement cel = (XMLComplexElement) it.next();
         s.addAll(((ExtendedAttributes) cel.get("ExtendedAttributes")).toElements());
      }
      return getAllExtendedAttributeNames(s);
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
                 || eaName.equals(JaWEEAHandler.EA_JAWE_CONFIGURATION) || eaName.equals(JaWEEAHandler.EA_JAWE_EXTERNAL_PACKAGE_ID))) {
            continue;
         }
         if (pp instanceof WorkflowProcess
             && (eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_WORKFLOW_PARTICIPANT_ORIENTATION)
                 || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_BLOCK_PARTICIPANT_ORIENTATION)
                 || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_WORKFLOW_PARTICIPANT_ORDER)
                 || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_BLOCK_PARTICIPANT_ORDER)
                 || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_START_OF_WORKFLOW)
                 || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_END_OF_WORKFLOW)
                 || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_START_OF_BLOCK) || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_END_OF_BLOCK))) {
            continue;
         }
         if (pp instanceof Activity
             && (eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_OFFSET) || eaName.equals(GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID))) {
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

   public NodeGraphicsInfo getNodeGraphicsInfo(XMLCollectionElement actOrArtif) {
      Iterator it = ((NodeGraphicsInfos) actOrArtif.get("NodeGraphicsInfos")).toElements()
         .iterator();
      while (it.hasNext()) {
         NodeGraphicsInfo ngi = (NodeGraphicsInfo) it.next();
         if (ngi.getToolId().equals("JaWE")) {
            return ngi;
         }
      }
      return null;
   }

   public String getLaneId(XMLCollectionElement actOrArtif) {
      NodeGraphicsInfo ngi = getNodeGraphicsInfo(actOrArtif);
      if (ngi != null) {
         return ngi.getLaneId();
      }
      return null;
   }

   public ConnectorGraphicsInfo getConnectorGraphicsInfo(XMLCollectionElement tra) {
      Iterator it = ((ConnectorGraphicsInfos) tra.get("ConnectorGraphicsInfos")).toElements()
         .iterator();
      while (it.hasNext()) {
         ConnectorGraphicsInfo cgi = (ConnectorGraphicsInfo) it.next();
         if (cgi.getToolId().equals("JaWE")) {
            return cgi;
         }
      }
      return null;
   }

   
   
}
