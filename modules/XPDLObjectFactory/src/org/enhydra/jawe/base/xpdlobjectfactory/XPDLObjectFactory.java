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

package org.enhydra.jawe.base.xpdlobjectfactory;

import java.lang.reflect.Method;
import java.util.HashSet;

import javax.tools.Tool;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEConstants;
import org.enhydra.jawe.JaWEEAHandler;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.base.controller.JaWETypes;
import org.enhydra.jawe.base.xpdlhandler.XPDLHandler;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.Activities;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ActivitySet;
import org.enhydra.jxpdl.elements.ActivitySets;
import org.enhydra.jxpdl.elements.ActualParameter;
import org.enhydra.jxpdl.elements.ActualParameters;
import org.enhydra.jxpdl.elements.Application;
import org.enhydra.jxpdl.elements.Applications;
import org.enhydra.jxpdl.elements.Artifact;
import org.enhydra.jxpdl.elements.Artifacts;
import org.enhydra.jxpdl.elements.Association;
import org.enhydra.jxpdl.elements.Associations;
import org.enhydra.jxpdl.elements.ConnectorGraphicsInfo;
import org.enhydra.jxpdl.elements.ConnectorGraphicsInfos;
import org.enhydra.jxpdl.elements.DODataField;
import org.enhydra.jxpdl.elements.DODataFields;
import org.enhydra.jxpdl.elements.DataField;
import org.enhydra.jxpdl.elements.DataFields;
import org.enhydra.jxpdl.elements.Deadline;
import org.enhydra.jxpdl.elements.Deadlines;
import org.enhydra.jxpdl.elements.EnumerationType;
import org.enhydra.jxpdl.elements.EnumerationValue;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;
import org.enhydra.jxpdl.elements.ExternalPackage;
import org.enhydra.jxpdl.elements.ExternalPackages;
import org.enhydra.jxpdl.elements.FormalParameter;
import org.enhydra.jxpdl.elements.FormalParameters;
import org.enhydra.jxpdl.elements.Lane;
import org.enhydra.jxpdl.elements.Lanes;
import org.enhydra.jxpdl.elements.Member;
import org.enhydra.jxpdl.elements.Namespace;
import org.enhydra.jxpdl.elements.Namespaces;
import org.enhydra.jxpdl.elements.NodeGraphicsInfo;
import org.enhydra.jxpdl.elements.NodeGraphicsInfos;
import org.enhydra.jxpdl.elements.Package;
import org.enhydra.jxpdl.elements.PackageHeader;
import org.enhydra.jxpdl.elements.Participant;
import org.enhydra.jxpdl.elements.Participants;
import org.enhydra.jxpdl.elements.Performer;
import org.enhydra.jxpdl.elements.Performers;
import org.enhydra.jxpdl.elements.Pool;
import org.enhydra.jxpdl.elements.Pools;
import org.enhydra.jxpdl.elements.RecordType;
import org.enhydra.jxpdl.elements.Responsible;
import org.enhydra.jxpdl.elements.Responsibles;
import org.enhydra.jxpdl.elements.Transition;
import org.enhydra.jxpdl.elements.TransitionRef;
import org.enhydra.jxpdl.elements.TransitionRefs;
import org.enhydra.jxpdl.elements.TransitionRestriction;
import org.enhydra.jxpdl.elements.TransitionRestrictions;
import org.enhydra.jxpdl.elements.Transitions;
import org.enhydra.jxpdl.elements.TypeDeclaration;
import org.enhydra.jxpdl.elements.TypeDeclarations;
import org.enhydra.jxpdl.elements.UnionType;
import org.enhydra.jxpdl.elements.WorkflowProcess;
import org.enhydra.jxpdl.elements.WorkflowProcesses;

/**
 * Factory for generating XPDL objects.
 * 
 * @author Sasa Bojanic
 */
public class XPDLObjectFactory {

   protected XPDLObjectFactorySettings settings;

   public XPDLObjectFactory() {
      settings = new XPDLObjectFactorySettings();
      settings.init((JaWEComponent) null);
   }

   public XPDLObjectFactory(XPDLObjectFactorySettings settings) {
      this.settings = settings;
      this.settings.init((JaWEComponent) null);
   }

   public Package createPackage(String type) {
      Package pkg = new Package();
      adjustType(pkg, type);
      XPDLHandler xpdlhandler = JaWEManager.getInstance().getXPDLHandler();
      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(pkg, type);
      }
      String id = pkg.getId();
      if (!hasTemplate || id.equals("") || xpdlhandler.getPackageById(id) != null) {
         int i = 0;
         while (id.equals("") || xpdlhandler.getPackageById(id) != null) {
            id = "newpkg" + String.valueOf(++i);
         }
         pkg.setId(id);
      }
      if (!hasTemplate || pkg.getName().equals("")) {
         pkg.setName(id);
      }
      PackageHeader ph = pkg.getPackageHeader();
      if (!hasTemplate) {
         ph.setXPDLVersion("2.1");
         ph.setVendor("(c) Together Teamsolutions Co., Ltd.");
      }
      ph.setCreated(XMLUtil.getCurrentDateAndTime());

      Namespaces nss = pkg.getNamespaces();
      Namespace ns = createXPDLObject(nss, "", true);
      ns.setName("xpdl");
      ns.setLocation(XMLUtil.XMLNS_XPDL);
      adjustXPDLObject(pkg, type);

      return pkg;
   }

   public ActivitySet createXPDLObject(ActivitySets ass,
                                       String type,
                                       boolean addToCollection) {
      ActivitySet as = (ActivitySet) ass.generateNewElement();
      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      adjustType(as, type);
      if (hasTemplate) {
         jts.fillFromTemplate(as, type);
      }
      String id = as.getId();
      if (!hasTemplate || id.equals("") || ass.getActivitySet(id) != null) {
         if (id.equals("")) {
            id = JaWEManager.getInstance().getIdFactory().generateUniqueId(ass);
         } else {
            id = JaWEManager.getInstance()
               .getIdFactory()
               .generateSimilarOrIdenticalUniqueId(ass, new HashSet(), id);
         }
         int i = 0;
         while (ass.getActivitySet(id) != null) {
            id = id + String.valueOf(++i);
         }
         as.setId(id);
      }

      adjustXPDLObject(as, type);

      if (addToCollection) {
         ass.add(as);
      }
      return as;
   }

   public Activity createXPDLObject(Activities acts, String type, boolean addToCollection) {
      Activity act = null;
      if (type == null || type.equals("")) {
         type = JaWEConstants.ACTIVITY_TYPE_TOOL;
      }
      if (type.equals(JaWEConstants.ACTIVITY_TYPE_BLOCK)
          || type.equals(JaWEConstants.ACTIVITY_TYPE_NO)
          || type.equals(JaWEConstants.ACTIVITY_TYPE_ROUTE_PARALLEL)
          || type.equals(JaWEConstants.ACTIVITY_TYPE_ROUTE_EXCLUSIVE)
          || type.equals(JaWEConstants.ACTIVITY_TYPE_ROUTE_INCLUSIVE)
          || type.equals(JaWEConstants.ACTIVITY_TYPE_SUBFLOW)
          || type.equals(JaWEConstants.ACTIVITY_TYPE_TOOL)
          || type.equals(JaWEConstants.ACTIVITY_TYPE_START)
          || type.equals(JaWEConstants.ACTIVITY_TYPE_END)) {
         act = createStandardActivity(acts, type);
      } else {
         act = createSpecialActivity(acts, type);
      }

      adjustXPDLObject(act, type);

      if (addToCollection) {
         acts.add(act);
      }
      return act;
   }

   protected Activity createStandardActivity(Activities acts, String type) {
      Activity act = createXPDLActivity(acts, type);
      if (type.equals(JaWEConstants.ACTIVITY_TYPE_BLOCK)) {
         act.getActivityTypes().setBlockActivity();
      } else if (type.equals(JaWEConstants.ACTIVITY_TYPE_ROUTE_PARALLEL)) {
         act.getActivityTypes().setRoute();
         act.getActivityTypes().getRoute().setGatewayTypeParallel();
      } else if (type.equals(JaWEConstants.ACTIVITY_TYPE_ROUTE_EXCLUSIVE)) {
         act.getActivityTypes().setRoute();
         act.getActivityTypes().getRoute().setGatewayTypeExclusive();
//      } else if (type.equals(JaWEConstants.ACTIVITY_TYPE_ROUTE_INCLUSIVE)) {
//         act.getActivityTypes().setRoute();
//         act.getActivityTypes().getRoute().setGatewayTypeInclusive();
      } else if (type.equals(JaWEConstants.ACTIVITY_TYPE_SUBFLOW)) {
         act.getActivityTypes().setImplementation();
         act.getActivityTypes().getImplementation().getImplementationTypes().setSubFlow();
      } else if (type.equals(JaWEConstants.ACTIVITY_TYPE_TOOL)) {
         act.getActivityTypes().setImplementation();
         act.getActivityTypes().getImplementation().getImplementationTypes().setTask();
         act.getActivityTypes()
            .getImplementation()
            .getImplementationTypes()
            .getTask()
            .getTaskTypes()
            .setTaskApplication();
         act.setFirstPerformer("");
      } else if (type.equals(JaWEConstants.ACTIVITY_TYPE_NO)) {
         act.getActivityTypes().setImplementation();
         act.getActivityTypes().getImplementation().getImplementationTypes().setNo();
         act.setFirstPerformer("");
      } else if (type.equals(JaWEConstants.ACTIVITY_TYPE_START)) {
         act.getActivityTypes().setEvent();
         act.getActivityTypes().getEvent().getEventTypes().setStartEvent();
      } else if (type.equals(JaWEConstants.ACTIVITY_TYPE_END)) {
         act.getActivityTypes().setEvent();
         act.getActivityTypes().getEvent().getEventTypes().setEndEvent();
      }
      return act;
   }

   protected Activity createSpecialActivity(Activities acts, String type) {
      Activity act = createXPDLActivity(acts, type);
      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);

      if (!hasTemplate) {
         act.getActivityTypes()
            .getImplementation()
            .getImplementationTypes()
            .getTask()
            .getTaskTypes()
            .setTaskApplication();
         act.getActivityTypes().getImplementation().getImplementationTypes().setTask();
         act.getActivityTypes().setImplementation();
         // JaWEEAHandler.setActivityType(act, type);
      }
      return act;
   }

   protected Activity createXPDLActivity(Activities acts, String type) {
      Activity act = (Activity) acts.generateNewElement();
      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(act, type);
      }
      String id = act.getId();
      if (!hasTemplate || id.equals("") || acts.getActivity(id) != null) {
         if (id.equals("")) {
            id = JaWEManager.getInstance().getIdFactory().generateUniqueId(acts);
         } else {
            id = JaWEManager.getInstance()
               .getIdFactory()
               .generateSimilarOrIdenticalUniqueId(acts, new HashSet(), id);
         }
         int i = 0;
         while (acts.getActivity(id) != null) {
            id = id + String.valueOf(++i);
         }
         act.setId(id);
      }
      return act;
   }

   public ActualParameter createXPDLObject(ActualParameters aps,
                                           String type,
                                           boolean addToCollection) {
      ActualParameter ap = (ActualParameter) aps.generateNewElement();
      adjustType(ap, type);

      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(ap, type);
      }

      adjustXPDLObject(ap, type);

      if (addToCollection) {
         aps.add(ap);
      }
      return ap;
   }

   public Application createXPDLObject(Applications apps,
                                       String type,
                                       boolean addToCollection) {
      Application app = (Application) apps.generateNewElement();
      adjustType(app, type);

      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(app, type);
      }
      String id = app.getId();
      if (!hasTemplate || id.equals("") || apps.getApplication(id) != null) {
         if (id.equals("")) {
            id = JaWEManager.getInstance().getIdFactory().generateUniqueId(apps);
         } else {
            id = JaWEManager.getInstance()
               .getIdFactory()
               .generateSimilarOrIdenticalUniqueId(apps, new HashSet(), id);
         }
         int i = 0;
         while (apps.getApplication(id) != null) {
            id = id + String.valueOf(++i);
         }
         app.setId(id);
      }

      adjustXPDLObject(app, type);

      if (addToCollection) {
         apps.add(app);
      }
      return app;
   }

   public Artifact createXPDLObject(Artifacts arts, String type, boolean addToCollection) {
      Artifact art = (Artifact) arts.generateNewElement();
      adjustType(art, type);

      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(art, type);
      }
      String id = art.getId();
      if (!hasTemplate || id.equals("") || arts.getArtifact(id) != null) {
         if (id.equals("")) {
            id = JaWEManager.getInstance().getIdFactory().generateUniqueId(arts);
         } else {
            id = JaWEManager.getInstance()
               .getIdFactory()
               .generateSimilarOrIdenticalUniqueId(arts, new HashSet(), id);
         }
         int i = 0;
         while (arts.getArtifact(id) != null) {
            id = id + String.valueOf(++i);
         }
         art.setId(id);
      }

      if (type.equals(JaWEConstants.ARTIFACT_TYPE_ANNOTATION)) {
         art.setArtifactTypeAnnotation();
      } else if (type.equals(JaWEConstants.ARTIFACT_TYPE_DATA_OBJECT)) {
         art.setArtifactTypeDataObject();
      }
      adjustXPDLObject(art, type);

      if (addToCollection) {
         arts.add(art);
      }
      return art;
   }

   public Association createXPDLObject(Associations assocs,
                                       String type,
                                       boolean addToCollection) {
      Association assoc = (Association) assocs.generateNewElement();
      adjustType(assoc, type);

      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(assoc, type);
      }
      String id = assoc.getId();
      if (!hasTemplate || id.equals("") || assocs.getAssociation(id) != null) {
         if (id.equals("")) {
            id = JaWEManager.getInstance().getIdFactory().generateUniqueId(assocs);
         } else {
            id = JaWEManager.getInstance()
               .getIdFactory()
               .generateSimilarOrIdenticalUniqueId(assocs, new HashSet(), id);
         }
         int i = 0;
         while (assocs.getAssociation(id) != null) {
            id = id + String.valueOf(++i);
         }
         assoc.setId(id);
      }
      
      if (JaWEConstants.ASSOCIATION_TYPE_NONE.equals(type)) {
         assoc.setAssociationDirectionNONE();
      } else if (JaWEConstants.ASSOCIATION_TYPE_BIDIRECTIONAL.equals(type)) {
         assoc.setAssociationDirectionBOTH();
      } else {
         assoc.setAssociationDirectionFROM();
      }
      adjustXPDLObject(assoc, type);

      if (addToCollection) {
         assocs.add(assoc);
      }
      return assoc;
   }

   public ConnectorGraphicsInfo createXPDLObject(ConnectorGraphicsInfos cgis,
                                                 String type,
                                                 boolean addToCollection) {
      ConnectorGraphicsInfo cgi = (ConnectorGraphicsInfo) cgis.generateNewElement();
      adjustType(cgi, type);

      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(cgi, type);
      }

      adjustXPDLObject(cgi, type);

      if (addToCollection) {
         cgis.add(cgi);
      }
      return cgi;
   }

   public DataField createXPDLObject(DataFields dfs, String type, boolean addToCollection) {
      DataField df = (DataField) dfs.generateNewElement();
      adjustType(df, type);

      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(df, type);
      }
      String id = df.getId();
      if (!hasTemplate || id.equals("") || dfs.getDataField(id) != null) {
         if (id.equals("")) {
            id = JaWEManager.getInstance().getIdFactory().generateUniqueId(dfs);
         } else {
            id = JaWEManager.getInstance()
               .getIdFactory()
               .generateSimilarOrIdenticalUniqueId(dfs, new HashSet(), id);
         }
         int i = 0;
         while (dfs.getDataField(id) != null) {
            id = id + String.valueOf(++i);
         }
         df.setId(id);
      }

      adjustXPDLObject(df, type);

      if (addToCollection) {
         dfs.add(df);
      }
      return df;
   }

   public DODataField createXPDLObject(DODataFields dfs,
                                       String type,
                                       boolean addToCollection) {
      DODataField df = (DODataField) dfs.generateNewElement();
      adjustType(df, type);

      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(df, type);
      }
      String id = df.getId();
      if (!hasTemplate || id.equals("") || dfs.getDODataField(id) != null) {
         if (id.equals("")) {
            id = JaWEManager.getInstance().getIdFactory().generateUniqueId(dfs);
         } else {
            id = JaWEManager.getInstance()
               .getIdFactory()
               .generateSimilarOrIdenticalUniqueId(dfs, new HashSet(), id);
         }
         int i = 0;
         while (dfs.getDODataField(id) != null) {
            id = id + String.valueOf(++i);
         }
         df.setId(id);
      }

      adjustXPDLObject(df, type);

      if (addToCollection) {
         dfs.add(df);
      }
      return df;
   }

   public Deadline createXPDLObject(Deadlines dls, String type, boolean addToCollection) {
      Deadline dl = (Deadline) dls.generateNewElement();
      adjustType(dl, type);

      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(dl, type);
      }

      adjustXPDLObject(dl, type);

      if (addToCollection) {
         dls.add(dl);
      }
      return dl;
   }

   public EnumerationValue createXPDLObject(EnumerationType et,
                                            String type,
                                            boolean addToCollection) {
      EnumerationValue ev = (EnumerationValue) et.generateNewElement();
      adjustType(ev, type);

      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(ev, type);
      }

      adjustXPDLObject(ev, type);

      if (addToCollection) {
         et.add(ev);
      }
      return ev;
   }

   public ExtendedAttribute createXPDLObject(ExtendedAttributes eas,
                                             String type,
                                             boolean addToCollection) {
      ExtendedAttribute ea = (ExtendedAttribute) eas.generateNewElement();
      adjustType(ea, type);

      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(ea, type);
      }

      adjustXPDLObject(ea, type);

      if (addToCollection) {
         eas.add(ea);
      }
      return ea;
   }

   public ExternalPackage createXPDLObject(ExternalPackages eps,
                                           String type,
                                           boolean addToCollection) {
      ExternalPackage ep = (ExternalPackage) eps.generateNewElement();
      adjustType(ep, type);

      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(ep, type);
      }

      adjustXPDLObject(ep, type);

      if (addToCollection) {
         eps.add(ep);
      }
      return ep;
   }

   public FormalParameter createXPDLObject(FormalParameters fps,
                                           String type,
                                           boolean addToCollection) {
      FormalParameter fp = (FormalParameter) fps.generateNewElement();
      adjustType(fp, type);

      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(fp, type);
      }
      String id = fp.getId();
      if (!hasTemplate || id.equals("") || fps.getFormalParameter(id) != null) {
         if (id.equals("")) {
            id = JaWEManager.getInstance().getIdFactory().generateUniqueId(fps);
         } else {
            id = JaWEManager.getInstance()
               .getIdFactory()
               .generateSimilarOrIdenticalUniqueId(fps, new HashSet(), id);
         }
         int i = 0;
         while (fps.getFormalParameter(id) != null) {
            id = id + String.valueOf(++i);
         }
         fp.setId(id);
      }

      adjustXPDLObject(fp, type);

      if (addToCollection) {
         fps.add(fp);
      }
      return fp;
   }

   public Lane createXPDLObject(Lanes ls, String type, boolean addToCollection) {
      Lane l = (Lane) ls.generateNewElement();
      adjustType(l, type);

      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(l, type);
      }

      String id = l.getId();
      if (!hasTemplate || id.equals("") || ls.getLane(id) != null) {
         if (id.equals("")) {
            id = JaWEManager.getInstance().getIdFactory().generateUniqueId(ls);
         } else {
            id = JaWEManager.getInstance()
               .getIdFactory()
               .generateSimilarOrIdenticalUniqueId(ls, new HashSet(), id);
         }
         int i = 0;
         while (ls.getLane(id) != null) {
            id = id + String.valueOf(++i);
         }
         l.setId(id);
      }

      adjustXPDLObject(l, type);

      if (addToCollection) {
         ls.add(l);
      }
      return l;
   }

   public Namespace createXPDLObject(Namespaces nss, String type, boolean addToCollection) {
      Namespace ns = (Namespace) nss.generateNewElement();
      adjustType(ns, type);

      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(ns, type);
      }

      adjustXPDLObject(ns, type);

      if (addToCollection) {
         nss.add(ns);
      }
      return ns;
   }

   public NodeGraphicsInfo createXPDLObject(NodeGraphicsInfos ngis,
                                            String type,
                                            boolean addToCollection) {
      NodeGraphicsInfo ngi = (NodeGraphicsInfo) ngis.generateNewElement();
      adjustType(ngi, type);

      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(ngi, type);
      }

      adjustXPDLObject(ngi, type);

      if (addToCollection) {
         ngis.add(ngi);
      }
      return ngi;
   }

   public Participant createXPDLObject(Participants ps,
                                       String type,
                                       boolean addToCollection) {
      Participant par = (Participant) ps.generateNewElement();
      adjustType(par, type);

      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(par, type);
      }
      String id = par.getId();
      if (!hasTemplate || id.equals("") || ps.getParticipant(id) != null) {
         if (id.equals("")) {
            id = JaWEManager.getInstance().getIdFactory().generateUniqueId(ps);
         } else {
            id = JaWEManager.getInstance()
               .getIdFactory()
               .generateSimilarOrIdenticalUniqueId(ps, new HashSet(), id);
         }
         int i = 0;
         while (ps.getParticipant(id) != null) {
            id = id + String.valueOf(++i);
         }
         par.setId(id);
      }

      if (type == null || type.equals(""))
         type = JaWEConstants.PARTICIPANT_TYPE_ROLE;

      if (type.equals(JaWEConstants.PARTICIPANT_TYPE_HUMAN)) {
         par.getParticipantType().setTypeHUMAN();
      } else if (type.equals(JaWEConstants.PARTICIPANT_TYPE_ROLE)) {
         par.getParticipantType().setTypeROLE();
      } else if (type.equals(JaWEConstants.PARTICIPANT_TYPE_ORGANIZATIONAL_UNIT)) {
         par.getParticipantType().setTypeORGANIZATIONAL_UNIT();
      } else if (type.equals(JaWEConstants.PARTICIPANT_TYPE_RESOURCE)) {
         par.getParticipantType().setTypeRESOURCE();
      } else if (type.equals(JaWEConstants.PARTICIPANT_TYPE_RESOURCE_SET)) {
         par.getParticipantType().setTypeRESOURCE_SET();
      } else if (type.equals(JaWEConstants.PARTICIPANT_TYPE_SYSTEM)) {
         par.getParticipantType().setTypeSYSTEM();
      }

      adjustXPDLObject(par, type);

      if (addToCollection) {
         ps.add(par);
      }
      return par;
   }

   public Performer createXPDLObject(Performers perfs,
                                     String type,
                                     boolean addToCollection) {
      Performer perf = (Performer) perfs.generateNewElement();
      adjustType(perf, type);

      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(perf, type);
      }

      adjustXPDLObject(perf, type);

      if (addToCollection) {
         perfs.add(perf);
      }
      return perf;
   }

   public Pool createXPDLObject(Pools ps, String type, boolean addToCollection) {
      Pool p = (Pool) ps.generateNewElement();
      adjustType(p, type);

      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(p, type);
      }

      String id = p.getId();
      if (!hasTemplate || id.equals("") || ps.getPool(id) != null) {
         if (id.equals("")) {
            id = JaWEManager.getInstance().getIdFactory().generateUniqueId(ps);
         } else {
            id = JaWEManager.getInstance()
               .getIdFactory()
               .generateSimilarOrIdenticalUniqueId(ps, new HashSet(), id);
         }
         int i = 0;
         while (ps.getPool(id) != null) {
            id = id + String.valueOf(++i);
         }
         p.setId(id);
      }

      adjustXPDLObject(p, type);

      if (addToCollection) {
         ps.add(p);
      }
      return p;
   }

   public Member createXPDLObject(RecordType rt, String type, boolean addToCollection) {
      Member m = (Member) rt.generateNewElement();
      adjustType(m, type);

      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(m, type);
      }

      adjustXPDLObject(m, type);

      if (addToCollection) {
         rt.add(m);
      }
      return m;
   }

   public Responsible createXPDLObject(Responsibles rs,
                                       String type,
                                       boolean addToCollection) {
      Responsible r = (Responsible) rs.generateNewElement();
      adjustType(r, type);

      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(r, type);
      }
      // Map m=Utils.getPossibleResponsibles(rs,null);
      // String parId="";
      // if (m.size()>0) {
      // parId=(String)m.keySet().iterator().next();
      //
      // }
      // r.setValue(parId);

      adjustXPDLObject(r, type);

      if (addToCollection) {
         rs.add(r);
      }
      return r;
   }

   public Transition createXPDLObject(Transitions tras,
                                      String type,
                                      boolean addToCollection) {
      if (type == null || type.equals(""))
         type = JaWEConstants.TRANSITION_TYPE_UNCONDITIONAL;

      Transition tra = createXPDLTransition(tras, type);
      // if (type.equals(JaWEConstants.TRANSITION_TYPE_UNCONDITIONAL))
      // do nothing
      // else
      if (type.equals(JaWEConstants.TRANSITION_TYPE_CONDITIONAL))
         tra.getCondition().setTypeCONDITION();
      else if (type.equals(JaWEConstants.TRANSITION_TYPE_OTHERWISE))
         tra.getCondition().setTypeOTHERWISE();
      else if (type.equals(JaWEConstants.TRANSITION_TYPE_EXCEPTION))
         tra.getCondition().setTypeEXCEPTION();
      else if (type.equals(JaWEConstants.TRANSITION_TYPE_DEFAULTEXCEPTION))
         tra.getCondition().setTypeDEFAULTEXCEPTION();

      adjustXPDLObject(tra, type);

      if (addToCollection) {
         tras.add(tra);
      }
      return tra;
   }

   protected Transition createXPDLTransition(Transitions tras, String type) {
      Transition tra = (Transition) tras.generateNewElement();
      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(tra, type);
      }
      String id = tra.getId();
      if (!hasTemplate || id.equals("") || tras.getTransition(id) != null) {
         if (id.equals("")) {
            id = JaWEManager.getInstance().getIdFactory().generateUniqueId(tras);
         } else {
            id = JaWEManager.getInstance()
               .getIdFactory()
               .generateSimilarOrIdenticalUniqueId(tras, new HashSet(), id);
         }
         int i = 0;
         while (tras.getTransition(id) != null) {
            id = id + String.valueOf(++i);
         }
         tra.setId(id);
      }
      return tra;
   }

   public TransitionRef createXPDLObject(TransitionRefs trs,
                                         String type,
                                         boolean addToCollection) {
      TransitionRef tr = (TransitionRef) trs.generateNewElement();
      adjustType(tr, type);

      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(tr, type);
      }

      adjustXPDLObject(tr, type);

      if (addToCollection) {
         trs.add(tr);
      }
      return tr;
   }

   public TransitionRestriction createXPDLObject(TransitionRestrictions trests,
                                                 String type,
                                                 boolean addToCollection) {
      TransitionRestriction tres = (TransitionRestriction) trests.generateNewElement();
      adjustType(tres, type);

      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(tres, type);
      }

      adjustXPDLObject(tres, type);

      if (addToCollection) {
         trests.add(tres);
      }
      return tres;
   }

   public TypeDeclaration createXPDLObject(TypeDeclarations tds,
                                           String type,
                                           boolean addToCollection) {
      TypeDeclaration td = (TypeDeclaration) tds.generateNewElement();
      adjustType(td, type);

      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(td, type);
      }
      String id = td.getId();
      if (!hasTemplate || id.equals("") || tds.getTypeDeclaration(id) != null) {
         if (id.equals("")) {
            id = JaWEManager.getInstance().getIdFactory().generateUniqueId(tds);
         } else {
            id = JaWEManager.getInstance()
               .getIdFactory()
               .generateSimilarOrIdenticalUniqueId(tds, new HashSet(), id);
         }
         int i = 0;
         while (tds.getTypeDeclaration(id) != null) {
            id = id + String.valueOf(++i);
         }
         td.setId(id);
      }

      adjustXPDLObject(td, type);

      if (addToCollection) {
         tds.add(td);
      }
      return td;
   }

   public Member createXPDLObject(UnionType ut, String type, boolean addToCollection) {
      Member m = (Member) ut.generateNewElement();
      adjustType(m, type);

      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(m, type);
      }

      adjustXPDLObject(m, type);

      if (addToCollection) {
         ut.add(m);
      }
      return m;
   }

   public WorkflowProcess createXPDLObject(WorkflowProcesses wps,
                                           String type,
                                           boolean addToCollection) {
      WorkflowProcess wp = (WorkflowProcess) wps.generateNewElement();
      adjustType(wp, type);

      JaWETypes jts = JaWEManager.getInstance().getJaWEController().getJaWETypes();
      boolean hasTemplate = jts.hasTemplateId(type);
      if (hasTemplate) {
         jts.fillFromTemplate(wp, type);
      }
      String id = wp.getId();
      if (!hasTemplate || id.equals("") || wps.getWorkflowProcess(id) != null) {
         if (id.equals("")) {
            id = JaWEManager.getInstance().getIdFactory().generateUniqueId(wps);
         } else {
            id = JaWEManager.getInstance()
               .getIdFactory()
               .generateSimilarOrIdenticalUniqueId(wps, new HashSet(), id);
         }
         int i = 0;
         while (wps.getWorkflowProcess(id) != null) {
            id = id + String.valueOf(++i);
         }
         wp.setId(id);
      }

      if (!hasTemplate || wp.getName().equals("")) {
         wp.setName(id);
      }
      wp.getProcessHeader().setCreated(XMLUtil.getCurrentDateAndTime());
      wp.getRedefinableHeader().setAuthor(XMLUtil.getPackage(wps)
         .getRedefinableHeader()
         .getAuthor());

      adjustXPDLObject(wp, type);

      if (addToCollection) {
         wps.add(wp);
      }
      return wp;
   }

   public void adjustXPDLObject(XMLElement el, String type) {
      if (type != null && !type.equals("")) {
         JaWEEAHandler.setJaWEType(el, type);
      }
      if (el instanceof NodeGraphicsInfo) {
         ((NodeGraphicsInfo) el).setToolId("JaWE");
      }
      if (el instanceof ConnectorGraphicsInfo) {
         ((ConnectorGraphicsInfo) el).setToolId("JaWE");
      }
   }

   public XMLElement createXPDLObject(XMLCollection col,
                                      String type,
                                      boolean addToCollection) {
      try {
         Class cl = col.getClass();
         Method m = null;
         try {
            m = this.getClass().getMethod("createXPDLObject", new Class[] {
                  cl, String.class, boolean.class
            });
         } catch (Exception ex) {
            if (!(cl == XMLCollection.class)) {
               if (XMLCollection.class.isAssignableFrom(cl)) {
                  cl = XMLCollection.class;
               }
            }
         }
         m = this.getClass().getMethod("createXPDLObject", new Class[] {
               cl, String.class, boolean.class
         });
         // System.err.println("calling "+m.toString());
         return (XMLElement) m.invoke(this, new Object[] {
               col, type, new Boolean(addToCollection)
         });
      } catch (Throwable e) {
         e.printStackTrace();
         return null;
      }

   }

   public XMLElement duplicateXPDLObject(XMLCollection col, XMLElement el) {
      XMLElement dup = col.generateNewElement();
      dup.makeAs(el);
      if (dup instanceof XMLCollectionElement) {
         if (!(dup instanceof Tool)) {
            ((XMLCollectionElement) dup).setId(JaWEManager.getInstance()
               .getIdFactory()
               .generateUniqueId((XMLCollection) dup.getParent()));
         }
         XMLElement name = ((XMLCollectionElement) el).get("Name");
         if (name != null && name.toValue().length() > 0) {
            ((XMLCollectionElement) dup).set("Name",
                                             ResourceManager.getLanguageDependentString("CopyOfKey")
                                                   + " " + name.toValue());
         }
      }
      return dup;
   }

   public XMLElement makeIdenticalXPDLObject(XMLCollection col, XMLElement el) {
      XMLElement dup = col.generateNewElement();
      dup.makeAs(el);
      return dup;
   }

   protected String adjustType(XMLElement el, String type) {
      if (type == null || type.equals("")) {
         type = JaWEManager.getInstance()
            .getJaWEController()
            .getJaWETypes()
            .getDefaultType(el);
      }
      return type;
   }
}
