package org.enhydra.jawe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.enhydra.jawe.base.xpdlhandler.XPDLHandler;
import org.enhydra.jawe.components.graph.GraphEAConstants;
import org.enhydra.shark.utilities.SequencedHashMap;
import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLCollectionElement;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.XPDLConstants;
import org.enhydra.shark.xpdl.elements.Activities;
import org.enhydra.shark.xpdl.elements.Activity;
import org.enhydra.shark.xpdl.elements.ActivitySet;
import org.enhydra.shark.xpdl.elements.ActualParameter;
import org.enhydra.shark.xpdl.elements.Application;
import org.enhydra.shark.xpdl.elements.Applications;
import org.enhydra.shark.xpdl.elements.ArrayType;
import org.enhydra.shark.xpdl.elements.BlockActivity;
import org.enhydra.shark.xpdl.elements.DataField;
import org.enhydra.shark.xpdl.elements.DataFields;
import org.enhydra.shark.xpdl.elements.DataTypes;
import org.enhydra.shark.xpdl.elements.Deadline;
import org.enhydra.shark.xpdl.elements.DeclaredType;
import org.enhydra.shark.xpdl.elements.ExtendedAttribute;
import org.enhydra.shark.xpdl.elements.ExtendedAttributes;
import org.enhydra.shark.xpdl.elements.ExternalPackage;
import org.enhydra.shark.xpdl.elements.FormalParameter;
import org.enhydra.shark.xpdl.elements.Join;
import org.enhydra.shark.xpdl.elements.ListType;
import org.enhydra.shark.xpdl.elements.Member;
import org.enhydra.shark.xpdl.elements.Package;
import org.enhydra.shark.xpdl.elements.Participant;
import org.enhydra.shark.xpdl.elements.RecordType;
import org.enhydra.shark.xpdl.elements.RedefinableHeader;
import org.enhydra.shark.xpdl.elements.Responsible;
import org.enhydra.shark.xpdl.elements.Responsibles;
import org.enhydra.shark.xpdl.elements.Split;
import org.enhydra.shark.xpdl.elements.SubFlow;
import org.enhydra.shark.xpdl.elements.Tool;
import org.enhydra.shark.xpdl.elements.Tools;
import org.enhydra.shark.xpdl.elements.Transition;
import org.enhydra.shark.xpdl.elements.TransitionRef;
import org.enhydra.shark.xpdl.elements.TransitionRefs;
import org.enhydra.shark.xpdl.elements.TransitionRestriction;
import org.enhydra.shark.xpdl.elements.TransitionRestrictions;
import org.enhydra.shark.xpdl.elements.Transitions;
import org.enhydra.shark.xpdl.elements.TypeDeclaration;
import org.enhydra.shark.xpdl.elements.UnionType;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;

/**
 * Various utilities.
 * 
 * @author Sasa Bojanic
 */
public class XPDLUtils {

   public boolean hasCircularTransitions(Set transitions) {
      Iterator it = transitions.iterator();
      while (it.hasNext()) {
         Transition t = (Transition) it.next();
         if (t.getFrom().equals(t.getTo()))
            return true;
      }
      return false;
   }

   // Useful only if JaWE is set to work without start/end bubbles
   public boolean isStartingActivity(Activity act) {
      Set trans = XMLUtil.getIncomingTransitions(act);
      if (trans.size() == 0 || (trans.size() == 1 && hasCircularTransitions(trans))) {
         return true;
      }
      return false;
   }

   // Useful only if JaWE is set to work without start/end bubbles
   public boolean isEndingActivity(Activity act) {
      Set trans = XMLUtil.getNonExceptionalOutgoingTransitions(act);
      if (trans.size() == 0 || (trans.size() == 1 && hasCircularTransitions(trans))) {
         return true;
      }
      return false;
   }

   public List getReferences(XMLComplexElement pkgOrWp, XMLComplexElement referenced) {
      if (pkgOrWp instanceof Package) {
         return getReferences((Package) pkgOrWp, referenced);
      } else if (pkgOrWp instanceof WorkflowProcess) {
         return getReferences((WorkflowProcess) pkgOrWp, referenced);
      }
      return new ArrayList();
   }

   public List getReferences(Package pkg, XMLComplexElement referenced) {
      if (referenced instanceof Package) {
         return getReferences((Package) referenced);
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
      }

      return new ArrayList();
   }

   public List getReferences(Package pkg) {
      List references = new ArrayList();
      String pkgId = pkg.getId();
      Iterator it = JaWEManager.getInstance()
         .getXPDLHandler()
         .getAllPackages()
         .iterator();
      while (it.hasNext()) {
         Package p = (Package) it.next();
         if (p.getExternalPackageIds().contains(pkgId)) {
            references.add(p);
         }
      }

      return references;

   }

   public List getAllExternalPackageReferences(Package pkg, Package referenced) {
      List references = new ArrayList();

      if (referenced != null) {
         Iterator it = referenced.getApplications().toElements().iterator();
         while (it.hasNext()) {
            Application app = (Application) it.next();
            references.addAll(getReferences(pkg, app));
         }
         it = referenced.getParticipants().toElements().iterator();
         while (it.hasNext()) {
            Participant par = (Participant) it.next();
            references.addAll(getReferences(pkg, par));
         }
         it = referenced.getWorkflowProcesses().toElements().iterator();
         while (it.hasNext()) {
            WorkflowProcess wp = (WorkflowProcess) it.next();
            references.addAll(getReferences(pkg, wp));
         }
      }

      return references;
   }

   public List getReferences(Package pkg, TypeDeclaration referenced) {
      return getTypeDeclarationReferences(pkg, referenced.getId());
   }

   public List getReferences(WorkflowProcess wp, TypeDeclaration referenced) {
      return tGetTypeDeclarationReferences(wp, referenced.getId());
   }

   public List getReferences(TypeDeclaration td, TypeDeclaration referenced) {
      if (td.getId().equals(referenced.getId()))
         return new ArrayList();
      return getReferencingDeclaredTypes(td.getDataTypes(), referenced.getId());
   }

   public List getTypeDeclarationReferences(Package pkg, String referencedId) {
      List references = new ArrayList();
      if (referencedId.equals("")) {
         return references;
      }
      Iterator it = pkg.getTypeDeclarations().toElements().iterator();
      while (it.hasNext()) {
         TypeDeclaration td = (TypeDeclaration) it.next();
         if (td.getId().equals(referencedId))
            continue;
         references.addAll(getReferencingDeclaredTypes(td.getDataTypes(), referencedId));
      }

      references.addAll(tGetTypeDeclarationReferences(pkg, referencedId));

      it = pkg.getWorkflowProcesses().toElements().iterator();
      while (it.hasNext()) {
         WorkflowProcess wp = (WorkflowProcess) it.next();
         references.addAll(tGetTypeDeclarationReferences(wp, referencedId));
      }

      return references;
   }

   protected List tGetTypeDeclarationReferences(XMLComplexElement pkgOrWp,
                                                String referencedId) {
      List references = new ArrayList();
      if (referencedId.equals("")) {
         return references;
      }

      Iterator it = ((Applications) pkgOrWp.get("Applications")).toElements().iterator();
      while (it.hasNext()) {
         Application app = (Application) it.next();
         Iterator fps = app.getApplicationTypes()
            .getFormalParameters()
            .toElements()
            .iterator();
         while (fps.hasNext()) {
            Object obj = fps.next();
            // System.err.println(obj.getClass().getName());
            FormalParameter fp = (FormalParameter) obj;
            references.addAll(getReferencingDeclaredTypes(fp.getDataType().getDataTypes(),
                                                          referencedId));
         }
      }
      it = ((DataFields) pkgOrWp.get("DataFields")).toElements().iterator();
      while (it.hasNext()) {
         DataField df = (DataField) it.next();
         references.addAll(getReferencingDeclaredTypes(df.getDataType().getDataTypes(),
                                                       referencedId));
      }
      if (pkgOrWp instanceof WorkflowProcess) {
         it = ((WorkflowProcess) pkgOrWp).getFormalParameters().toElements().iterator();
         while (it.hasNext()) {
            FormalParameter fp = (FormalParameter) it.next();
            references.addAll(getReferencingDeclaredTypes(fp.getDataType().getDataTypes(),
                                                          referencedId));
         }
      }

      return references;
   }

   public List getReferencingDeclaredTypes(DataTypes dts, String typeDeclarationId) {
      List toRet = new ArrayList();
      if (typeDeclarationId.equals("")) {
         return toRet;
      }

      XMLElement choosen = dts.getChoosen();
      if (choosen instanceof DeclaredType) {
         if (((DeclaredType) choosen).getId().equals(typeDeclarationId)) {
            toRet.add(choosen);
         }
      } else if (choosen instanceof ArrayType) {
         return getReferencingDeclaredTypes(((ArrayType) choosen).getDataTypes(),
                                            typeDeclarationId);
      } else if (choosen instanceof ListType) {
         return getReferencingDeclaredTypes(((ListType) choosen).getDataTypes(),
                                            typeDeclarationId);
      } else if (choosen instanceof RecordType || choosen instanceof UnionType) {
         Iterator it = ((XMLCollection) choosen).toElements().iterator();
         while (it.hasNext()) {
            Member m = (Member) it.next();
            toRet.addAll(getReferencingDeclaredTypes(m.getDataTypes(), typeDeclarationId));
         }
      }

      return toRet;
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
      List references = new ArrayList();
      if (referencedId.equals("")) {
         return references;
      }

      if (!(pkgOrWpOrAs instanceof ActivitySet)) {
         Iterator it = ((RedefinableHeader) pkgOrWpOrAs.get("RedefinableHeader")).getResponsibles()
            .toElements()
            .iterator();
         while (it.hasNext()) {
            Responsible rs = (Responsible) it.next();
            if (rs.toValue().equals(referencedId)) {
               references.add(rs);
            }
         }
      }
      if (!(pkgOrWpOrAs instanceof Package)) {
         Iterator it = ((Activities) pkgOrWpOrAs.get("Activities")).toElements()
            .iterator();
         while (it.hasNext()) {
            Activity act = (Activity) it.next();
            String perf = act.getPerformer();
            if (perf.equals(referencedId)) {
               references.add(act.get("Performer"));
            }
         }
      }

      return references;
   }

   public List getApplicationReferences(XMLComplexElement pkgOrWp, String referencedId) {
      if (referencedId.equals("")) {
         return new ArrayList();
      }
      if (pkgOrWp instanceof Package) {
         return getApplicationReferences((Package) pkgOrWp, referencedId);
      }

      return getApplicationReferences((WorkflowProcess) pkgOrWp, referencedId);
   }

   public List getReferences(Package pkg, Application referenced) {
      if (XMLUtil.getPackage(referenced) != pkg
          && pkg.getApplication(referenced.getId()) != null) {
         return new ArrayList();
      }

      return getApplicationReferences(pkg, referenced.getId());
   }

   public List getApplicationReferences(Package pkg, String referencedId) {
      List references = new ArrayList();
      if (referencedId.equals("")) {
         return references;
      }

      Iterator it = pkg.getWorkflowProcesses().toElements().iterator();
      while (it.hasNext()) {
         WorkflowProcess wp = (WorkflowProcess) it.next();
         if (wp.getApplication(referencedId) == null) {
            references.addAll(getApplicationReferences(wp, referencedId));
         }
      }

      return references;
   }

   public List getReferences(WorkflowProcess wp, Application referenced) {
      if (XMLUtil.getWorkflowProcess(referenced) == null
          && wp.getApplication(referenced.getId()) != null) {
         return new ArrayList();
      }
      Package pkg = XMLUtil.getPackage(wp);
      if (XMLUtil.getPackage(referenced) != pkg
          && pkg.getParticipant(referenced.getId()) != null) {
         return new ArrayList();
      }

      return getApplicationReferences(wp, referenced.getId());
   }

   public List getApplicationReferences(WorkflowProcess wp, String referencedId) {
      List references = new ArrayList();
      if (referencedId.equals("")) {
         return references;
      }

      references.addAll(tGetApplicationReferences(wp, referencedId));

      Iterator it = wp.getActivitySets().toElements().iterator();
      while (it.hasNext()) {
         ActivitySet as = (ActivitySet) it.next();
         references.addAll(tGetApplicationReferences(as, referencedId));
      }

      return references;
   }

   protected List tGetApplicationReferences(XMLCollectionElement wpOrAs,
                                            String referencedId) {
      List references = new ArrayList();
      if (referencedId.equals("")) {
         return references;
      }

      List types = new ArrayList();
      types.add(new Integer(XPDLConstants.ACTIVITY_TYPE_TOOL));
      Iterator it = getActivities((Activities) wpOrAs.get("Activities"), types).iterator();
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         Iterator tools = act.getActivityTypes()
            .getImplementation()
            .getImplementationTypes()
            .getTools()
            .toElements()
            .iterator();
         while (tools.hasNext()) {
            Tool t = (Tool) tools.next();
            if (t.getId().equals(referencedId)) {
               references.add(t);
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

   public List getReferences(Package pkg, WorkflowProcess referenced) {
      List references = new ArrayList();
      if (XMLUtil.getPackage(referenced) != pkg
          && pkg.getWorkflowProcess(referenced.getId()) != null) {
         return references;
      }

      Iterator it = pkg.getWorkflowProcesses().toElements().iterator();
      while (it.hasNext()) {
         WorkflowProcess wp = (WorkflowProcess) it.next();
         references.addAll(getReferences(wp, referenced));
      }

      return references;
   }

   public List getWorkflowProcessReferences(Package pkg, String referencedId) {
      List references = new ArrayList();
      if (referencedId.equals("")) {
         return references;
      }

      Iterator it = pkg.getWorkflowProcesses().toElements().iterator();
      while (it.hasNext()) {
         WorkflowProcess wp = (WorkflowProcess) it.next();
         references.addAll(getWorkflowProcessReferences(wp, referencedId));
      }

      return references;
   }

   public List getReferences(WorkflowProcess wp, WorkflowProcess referenced) {
      return getWorkflowProcessReferences(wp, referenced.getId());
   }

   public List getWorkflowProcessReferences(WorkflowProcess wp, String referencedId) {
      List references = new ArrayList();
      if (referencedId.equals("")) {
         return references;
      }

      references.addAll(tGetWorkflowProcessReferences(wp, referencedId));

      Iterator it = wp.getActivitySets().toElements().iterator();
      while (it.hasNext()) {
         ActivitySet as = (ActivitySet) it.next();
         references.addAll(tGetWorkflowProcessReferences(as, referencedId));
      }

      return references;
   }

   protected List tGetWorkflowProcessReferences(XMLCollectionElement wpOrAs,
                                                String referencedId) {
      List references = new ArrayList();
      if (referencedId.equals("")) {
         return references;
      }

      List types = new ArrayList();
      types.add(new Integer(XPDLConstants.ACTIVITY_TYPE_SUBFLOW));
      Iterator it = getActivities((Activities) wpOrAs.get("Activities"), types).iterator();
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         SubFlow s = act.getActivityTypes()
            .getImplementation()
            .getImplementationTypes()
            .getSubFlow();
         if (s.getId().equals(referencedId)) {
            references.add(s);
         }
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
      List references = new ArrayList();
      if (dfOrFpId.equals("")) {
         return references;
      }

      Map allVars = XMLUtil.getWorkflowProcess(wpOrAs).getAllVariables();
      Iterator it = ((Activities) wpOrAs.get("Activities")).toElements().iterator();
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         int type = act.getActivityType();
         // actual parameter (can be expression containing variable, or direct variable
         // reference)
         List aps = new ArrayList();
         if (type == XPDLConstants.ACTIVITY_TYPE_SUBFLOW) {
            aps.addAll(act.getActivityTypes()
               .getImplementation()
               .getImplementationTypes()
               .getSubFlow()
               .getActualParameters()
               .toElements());
         } else if (type == XPDLConstants.ACTIVITY_TYPE_TOOL) {
            Tools tools = act.getActivityTypes()
               .getImplementation()
               .getImplementationTypes()
               .getTools();
            Iterator itt = tools.toElements().iterator();
            while (itt.hasNext()) {
               Tool t = (Tool) itt.next();
               aps.addAll(t.getActualParameters().toElements());
            }
         }
         Iterator itap = aps.iterator();
         while (itap.hasNext()) {
            ActualParameter ap = (ActualParameter) itap.next();
            if (XMLUtil.getUsingPositions(ap.toValue(), dfOrFpId, allVars).size() > 0) {
               references.add(ap);
            }
         }

         Iterator itdls = act.getDeadlines().toElements().iterator();
         while (itdls.hasNext()) {
            Deadline dl = (Deadline) itdls.next();
            String dcond = dl.getDeadlineCondition();
            if (XMLUtil.getUsingPositions(dcond, dfOrFpId, allVars).size() > 0) {
               references.add(dl.get("DeadlineCondition"));
            }
         }

         // performer (can be expression containing variable, or direct variable
         // reference)
         String perf = act.getPerformer();
         if (XMLUtil.getUsingPositions(perf, dfOrFpId, allVars).size() > 0) {
            references.add(act.get("Performer"));
         }
      }

      // transition condition (can be expression containing variable, or direct variable
      // reference)
      it = ((Transitions) wpOrAs.get("Transitions")).toElements().iterator();
      while (it.hasNext()) {
         Transition t = (Transition) it.next();
         if (XMLUtil.getUsingPositions(t.getCondition().toValue(), dfOrFpId, allVars)
            .size() > 0) {
            references.add(t.getCondition());
         }
      }

      return references;
   }

   public List getReferences(WorkflowProcess wp, ActivitySet referenced) {
      return getActivitySetReferences(wp, referenced.getId());
   }

   public List getActivitySetReferences(WorkflowProcess wp, String referencedId) {
      List references = new ArrayList();
      if (referencedId.equals("")) {
         return references;
      }

      references.addAll(tGetActivitySetReferences(wp, referencedId));

      Iterator it = wp.getActivitySets().toElements().iterator();
      while (it.hasNext()) {
         ActivitySet as = (ActivitySet) it.next();
         references.addAll(tGetActivitySetReferences(as, referencedId));
      }

      return references;
   }

   public List getReferences(ActivitySet as, ActivitySet referenced) {
      return tGetActivitySetReferences(as, referenced.getId());
   }

   public List getReferences(ActivitySet as, String referencedId) {
      return tGetActivitySetReferences(as, referencedId);
   }

   public List tGetActivitySetReferences(XMLCollectionElement wpOrAs, String referencedId) {
      List references = new ArrayList();
      if (referencedId.equals("")) {
         return references;
      }

      List types = new ArrayList();
      types.add(new Integer(XPDLConstants.ACTIVITY_TYPE_BLOCK));
      Iterator it = getActivities((Activities) wpOrAs.get("Activities"), types).iterator();
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         BlockActivity ba = act.getActivityTypes().getBlockActivity();
         if (ba.getBlockId().equals(referencedId)) {
            references.add(ba);
         }
      }

      return references;
   }

   public List getReferences(Activity act) {
      return getActivityReferences((XMLCollectionElement) act.getParent().getParent(),
                                   act.getId());
   }

   public List getActivityReferences(XMLCollectionElement wpOrAs, String referencedId) {
      List refs = new ArrayList();
      Transitions tras = ((Transitions) wpOrAs.get("Transitions"));
      Iterator it = getTransitions(tras, referencedId, true).iterator();
      while (it.hasNext()) {
         refs.add(((Transition) it.next()).get("To"));
      }
      it = getTransitions(tras, referencedId, false).iterator();
      while (it.hasNext()) {
         refs.add(((Transition) it.next()).get("From"));
      }
      return new ArrayList(refs);
   }

   public List getReferences(Transition tra) {
      Activities acts = (Activities) ((XMLCollectionElement) tra.getParent().getParent()).get("Activities");
      Activity from = acts.getActivity(tra.getFrom());
      Activity to = acts.getActivity(tra.getTo());
      Set refs = new HashSet();
      if (from != null) {
         refs.add(from);
      }
      if (to != null) {
         refs.add(to);
      }
      return new ArrayList(refs);
   }

   public boolean correctSplitsAndJoins(Package pkg) {
      boolean changed = false;

      Iterator it = pkg.getWorkflowProcesses().toElements().iterator();
      while (it.hasNext()) {
         WorkflowProcess wp = (WorkflowProcess) it.next();
         changed = correctSplitsAndJoins(wp) || changed;
      }
      return changed;
   }

   public boolean correctSplitsAndJoins(WorkflowProcess wp) {
      boolean changed = correctSplitsAndJoins(wp.getActivities().toElements());
      Iterator it = wp.getActivitySets().toElements().iterator();
      while (it.hasNext()) {
         ActivitySet as = (ActivitySet) it.next();
         changed = correctSplitsAndJoins(as.getActivities().toElements()) || changed;
      }
      return changed;
   }

   public boolean correctSplitsAndJoins(List acts) {
      boolean changed = false;
      Iterator it = acts.iterator();
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         changed = correctSplitAndJoin(act) || changed;
      }
      return changed;
   }

   public boolean correctSplitAndJoin(Activity act) {
      Set ogt = XMLUtil.getOutgoingTransitions(act);
      Set inct = XMLUtil.getIncomingTransitions(act);
      TransitionRestrictions tres = act.getTransitionRestrictions();
      TransitionRestriction tr = null;
      boolean newTres = false;
      boolean changed = false;
      // LoggingManager lm=JaWEManager.getInstance().getLoggingManager();
      // lm.debug("Correcting split and join for activity "+act.getId()+",
      // ogts="+ogt.size()+", incts="+inct.size());
      if (tres.size() == 0) {
         if (ogt.size() > 1 || inct.size() > 1) {
            tr = JaWEManager.getInstance().getXPDLObjectFactory().createXPDLObject(tres,
                                                                                   "",
                                                                                   false);
            newTres = true;
         } else {
            return false;
         }
      } else {
         tr = (TransitionRestriction) tres.get(0);
      }

      Split s = tr.getSplit();
      Join j = tr.getJoin();

      TransitionRefs trefs = s.getTransitionRefs();
      if (ogt.size() <= 1) {
         if (trefs.size() > 0) {
            trefs.clear();
            changed = true;
         }
         if (!s.getType().equals(XPDLConstants.JOIN_SPLIT_TYPE_NONE)) {
            s.setTypeNONE();
            changed = true;
         }
      }
      if (ogt.size() > 1) {

         if (s.getType().equals(XPDLConstants.JOIN_SPLIT_TYPE_NONE)) {
            s.setTypeXOR();
            // lm.debug("--------------------- st set to xor");
            changed = true;
         }

         List trefIds = new ArrayList();
         for (int i = 0; i < trefs.size(); i++) {
            TransitionRef tref = (TransitionRef) trefs.get(i);
            trefIds.add(tref.getId());
         }

         List transitionIds = new ArrayList();
         Iterator it = ogt.iterator();
         while (it.hasNext()) {
            Transition t = (Transition) it.next();
            transitionIds.add(t.getId());
         }

         List toRem = new ArrayList(trefIds);
         toRem.removeAll(transitionIds);
         List toAdd = new ArrayList(transitionIds);
         toAdd.removeAll(trefIds);

         for (int i = 0; i < toRem.size(); i++) {
            TransitionRef tref = trefs.getTransitionRef((String) toRem.get(i));
            trefs.remove(tref);
            changed = true;
         }
         for (int i = 0; i < toAdd.size(); i++) {
            TransitionRef tref = JaWEManager.getInstance()
               .getXPDLObjectFactory()
               .createXPDLObject(trefs, "", false);
            tref.setId((String) toAdd.get(i));
            trefs.add(tref);
            changed = true;
         }
      }

      if (inct.size() <= 1) {
         if (!j.getType().equals(XPDLConstants.JOIN_SPLIT_TYPE_NONE)) {
            j.setTypeNONE();
            changed = true;
         }
      } else {
         if (j.getType().equals(XPDLConstants.JOIN_SPLIT_TYPE_NONE)) {
            j.setTypeXOR();
            changed = true;
         }
      }
      // lm.debug("--------------------- st="+s.getType()+", jt="+j.getType()+",
      // trefss="+trefs.size());

      if (s.getType().equals(XPDLConstants.JOIN_SPLIT_TYPE_NONE)
          && j.getType().equals(XPDLConstants.JOIN_SPLIT_TYPE_NONE)) {
         if (!newTres) {
            tres.remove(tr);
            changed = true;
         }
      } else if (newTres) {
         tres.add(tr);
      }

      return changed;
   }

   public void updateActivityReferences(List refsTrasToFrom,
                                        String oldActId,
                                        String newActId) {
      Iterator it = refsTrasToFrom.iterator();
      while (it.hasNext()) {
         ((XMLElement) it.next()).setValue(newActId);
      }
   }

   public void updateActivityOnTransitionIdChange(Activities acts,
                                                  String actFromId,
                                                  String oldTraId,
                                                  String newTraId) {
      Activity act = acts.getActivity(actFromId);
      updateActivityOnTransitionIdChange(act, oldTraId, newTraId);
   }

   public void updateActivityOnTransitionIdChange(Activity act,
                                                  String oldTraId,
                                                  String newTraId) {
      if (act != null) {
         Split s = XMLUtil.getSplit(act);
         if (s != null) {
            TransitionRef tref = s.getTransitionRefs().getTransitionRef(oldTraId);
            if (tref != null) {
               tref.setId(newTraId);
            }
         }
      }
   }

   public void updateActivitiesOnTransitionFromChange(Activities acts,
                                                      String traId,
                                                      String traOldFromId,
                                                      String traNewFromId) {
      if (traOldFromId != null) {
         Activity act = acts.getActivity(traOldFromId);
         if (act != null) {
            correctSplitAndJoin(act);
         }
      }
      if (traNewFromId != null) {
         Activity act = acts.getActivity(traNewFromId);
         if (act != null) {
            correctSplitAndJoin(act);
         }
      }
   }

   public void updateActivitiesOnTransitionToChange(Activities acts,
                                                    String traId,
                                                    String traOldToId,
                                                    String traNewToId) {
      if (traOldToId != null) {
         Activity act = acts.getActivity(traOldToId);
         if (act != null) {
            correctSplitAndJoin(act);
         }
      }
      if (traNewToId != null) {
         Activity act = acts.getActivity(traNewToId);
         if (act != null) {
            correctSplitAndJoin(act);
         }
      }
   }

   public void removeTransitionsForActivity(Activity act) {
      Set trasToRemove = getTransitionsForActivity(act);
      Transitions trs = (Transitions) ((XMLCollectionElement) act.getParent().getParent()).get("Transitions");
      Activities acs = (Activities) act.getParent();

      if (trasToRemove.size() > 0) {
         Iterator itt = trasToRemove.iterator();
         while (itt.hasNext()) {
            Transition t = (Transition) itt.next();
            Activity from = acs.getActivity(t.getFrom());
            Activity to = acs.getActivity(t.getTo());
            if (from != act && from != null) {
               correctSplitAndJoin(from);
            }
            if (to != act && to != null) {
               correctSplitAndJoin(to);
            }
         }
         trs.removeAll(new ArrayList(trasToRemove));
      }

   }

   public void removeTransitionsForActivities(List acts) {
      if (acts.size() == 0)
         return;
      Activities acs = (Activities) ((Activity) acts.get(0)).getParent();
      Set trasToRemove = new HashSet();
      Iterator it = acts.iterator();
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         trasToRemove.addAll(getTransitionsForActivity(act));
      }
      if (trasToRemove.size() > 0) {
         Transitions trs = (Transitions) ((XMLCollectionElement) trasToRemove.toArray()[0]).getParent();
         trs.removeAll(new ArrayList(trasToRemove));
         Iterator itt = trasToRemove.iterator();
         while (itt.hasNext()) {
            Transition t = (Transition) itt.next();
            Activity from = acs.getActivity(t.getFrom());
            Activity to = acs.getActivity(t.getTo());
            if (from != null && !acts.contains(from)) {
               correctSplitAndJoin(from);
            }
            if (to != null && !acts.contains(to)) {
               correctSplitAndJoin(to);
            }
         }
      }
   }

   protected Set getTransitionsForActivity(Activity act) {
      Set trasToRemove = XMLUtil.getIncomingTransitions(act);
      trasToRemove.addAll(XMLUtil.getOutgoingTransitions(act));
      return trasToRemove;
   }

   public List getTransitions(Transitions tras, String actId, boolean isToAct) {
      List l = new ArrayList();
      Iterator it = tras.toElements().iterator();
      while (it.hasNext()) {
         Transition t = (Transition) it.next();
         if (isToAct) {
            if (t.getTo().equals(actId)) {
               l.add(t);
            }
         } else {
            if (t.getFrom().equals(actId)) {
               l.add(t);
            }
         }
      }
      return l;
   }

   public void updateTypeDeclarationReferences(List refDeclaredTypes, String newTdId) {
      Iterator it = refDeclaredTypes.iterator();
      while (it.hasNext()) {
         DeclaredType dt = (DeclaredType) it.next();
         dt.setId(newTdId);
      }
   }

   public void updateApplicationReferences(List refTools, String newAppId) {
      Iterator it = refTools.iterator();
      while (it.hasNext()) {
         Tool t = (Tool) it.next();
         t.setId(newAppId);
      }
   }

   public void updateParticipantReferences(List refPerfsAndResps, String newParId) {
      Iterator it = refPerfsAndResps.iterator();
      while (it.hasNext()) {
         XMLElement pOrR = (XMLElement) it.next();
         pOrR.setValue(newParId);
      }
   }

   public void updateWorkflowProcessReferences(List refSbflws, String newWpId) {
      Iterator it = refSbflws.iterator();
      while (it.hasNext()) {
         SubFlow s = (SubFlow) it.next();
         s.setId(newWpId);
      }
   }

   public void updateActivitySetReferences(List refBlocks, String newAsId) {
      Iterator it = refBlocks.iterator();
      while (it.hasNext()) {
         BlockActivity ba = (BlockActivity) it.next();
         ba.setBlockId(newAsId);
      }
   }

   public void updateVariableReferences(List refAPsOrPerfsOrCondsOrDlConds,
                                        String oldDfOrFpId,
                                        String newDfOrFpId) {
      Iterator it = refAPsOrPerfsOrCondsOrDlConds.iterator();
      int varLengthDiff = newDfOrFpId.length() - oldDfOrFpId.length();
      while (it.hasNext()) {
         XMLElement apOrPerfOrCondOrDlCond = (XMLElement) it.next();
         String expr = apOrPerfOrCondOrDlCond.toValue();
         List positions = XMLUtil.getUsingPositions(expr,
                                                    oldDfOrFpId,
                                                    XMLUtil.getWorkflowProcess(apOrPerfOrCondOrDlCond)
                                                       .getAllVariables());
         for (int i = 0; i < positions.size(); i++) {
            int pos = ((Integer) positions.get(i)).intValue();
            int realPos = pos + varLengthDiff * i;
            String pref = expr.substring(0, realPos);
            String suff = expr.substring(realPos + oldDfOrFpId.length());
            expr = pref + newDfOrFpId + suff;
            // System.out.println("Pref="+pref+", suff="+suff+", expr="+expr);
         }
         apOrPerfOrCondOrDlCond.setValue(expr);
      }
   }

   public List getActivities(Package pkg, List types) {
      List l = new ArrayList();
      Iterator it = pkg.getWorkflowProcesses().toElements().iterator();
      while (it.hasNext()) {
         WorkflowProcess wp = (WorkflowProcess) it.next();
         l.addAll(getActivities(wp, types));
      }
      return l;
   }

   public List getActivities(WorkflowProcess wp, List types) {
      List l = new ArrayList();
      l.addAll(getActivities(wp.getActivities(), types));
      Iterator it = wp.getActivitySets().toElements().iterator();
      while (it.hasNext()) {
         ActivitySet as = (ActivitySet) it.next();
         l.addAll(getActivities(as.getActivities(), types));
      }
      return l;
   }

   public List getActivities(Activities acts, List types) {
      List l = new ArrayList();

      Iterator it = acts.toElements().iterator();
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         int type = act.getActivityType();
         if (types.contains(new Integer(type))) {
            l.add(act);
         }
      }

      return l;
   }

   public SequencedHashMap getPossibleResponsibles(Responsibles resp, Responsible rsp) {
      SequencedHashMap choices = null;
      if (XMLUtil.getWorkflowProcess(resp) != null) {
         choices = XMLUtil.getPossibleParticipants(XMLUtil.getWorkflowProcess(resp),
                                                   JaWEManager.getInstance()
                                                      .getXPDLHandler());
      } else {
         choices = XMLUtil.getPossibleParticipants(XMLUtil.getPackage(resp),
                                                   JaWEManager.getInstance()
                                                      .getXPDLHandler());
      }
      // filter choices: exclude already existing performers
      Iterator it = resp.toElements().iterator();
      while (it.hasNext()) {
         Responsible r = (Responsible) it.next();
         if (r != rsp) {
            choices.remove(r.toValue());
         }
      }
      return choices;
   }

   public boolean doesCrossreferenceExist(Package pkg) {
      XPDLHandler xpdlhandler = JaWEManager.getInstance().getXPDLHandler();
      boolean crossRefs = false;

      Iterator epids = pkg.getExternalPackageIds().iterator();
      while (epids.hasNext()) {
         try {
            Package extP = xpdlhandler.getPackageById((String) epids.next());
            if (XMLUtil.getAllExternalPackageIds(xpdlhandler, extP, new HashSet())
               .contains(pkg.getId())) {
               crossRefs = true;
               break;
            }
         } catch (Exception ex) {
         }
      }
      return crossRefs;
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
      } else if (cel instanceof Tool) {
         Iterator it = pkg.getWorkflowProcesses().toElements().iterator();
         while (it.hasNext()) {
            WorkflowProcess wp = (WorkflowProcess) it.next();
            Iterator acti = wp.getActivities().toElements().iterator();
            while (acti.hasNext()) {
               Activity act = (Activity) acti.next();
               if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_TOOL) {
                  Tools ts = act.getActivityTypes()
                     .getImplementation()
                     .getImplementationTypes()
                     .getTools();
                  extAttribNames.addAll(getAllExtendedAttributeNamesForElements(ts.toElements()));
               }
            }
            Iterator asets = wp.getActivitySets().toElements().iterator();
            while (asets.hasNext()) {
               ActivitySet as = (ActivitySet) asets.next();
               acti = as.getActivities().toElements().iterator();
               while (acti.hasNext()) {
                  Activity act = (Activity) acti.next();
                  if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_TOOL) {
                     Tools ts = act.getActivityTypes()
                        .getImplementation()
                        .getImplementationTypes()
                        .getTools();
                     extAttribNames.addAll(getAllExtendedAttributeNamesForElements(ts.toElements()));
                  }
               }
            }
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

}
