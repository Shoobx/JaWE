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

/**
 * Miroslav Popov, Oct 3, 2005 miroslav.popov@gmail.com
 */
package org.enhydra.jawe.base.controller;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.ImageIcon;

import org.apache.xerces.parsers.DOMParser;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.JaWEConstants;
import org.enhydra.jawe.JaWEEAHandler;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLComplexChoice;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLSimpleElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.XPDLConstants;
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
import org.enhydra.jxpdl.elements.Package;
import org.enhydra.jxpdl.elements.Participant;
import org.enhydra.jxpdl.elements.Participants;
import org.enhydra.jxpdl.elements.Pool;
import org.enhydra.jxpdl.elements.Pools;
import org.enhydra.jxpdl.elements.Responsible;
import org.enhydra.jxpdl.elements.Responsibles;
import org.enhydra.jxpdl.elements.TaskApplication;
import org.enhydra.jxpdl.elements.Transition;
import org.enhydra.jxpdl.elements.Transitions;
import org.enhydra.jxpdl.elements.TypeDeclaration;
import org.enhydra.jxpdl.elements.TypeDeclarations;
import org.enhydra.jxpdl.elements.WorkflowProcess;
import org.enhydra.jxpdl.elements.WorkflowProcesses;
import org.enhydra.jxpdl.utilities.SequencedHashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * @author Miroslav Popov
 * @author Sasa Bojanic
 */
public class JaWETypes extends JaWEComponentSettings {

   public static final String XPDL_TEMPLATE = "XPDLTemplate";

   protected Map allTypes = new HashMap();

   protected Map allTypesMapping = new HashMap();

   protected List activityTypes = new ArrayList();

   protected List activitySetTypes = new ArrayList();

   protected List actualParameterTypes = new ArrayList();

   protected List applicationTypes = new ArrayList();

   protected List artifactTypes = new ArrayList();

   protected List associationTypes = new ArrayList();

   protected List dataFieldTypes = new ArrayList();

   protected List deadlineTypes = new ArrayList();

   protected List enumerationValueTypes = new ArrayList();

   protected List extendedAttributeTypes = new ArrayList();

   protected Map extAttribTypes = new HashMap();

   protected List externalPackageTypes = new ArrayList();

   protected List formalParameterTypes = new ArrayList();

   protected List laneTypes = new ArrayList();

   protected List memberTypes = new ArrayList();

   protected List namespaceTypes = new ArrayList();

   protected List packageTypes = new ArrayList();

   protected List participantTypes = new ArrayList();

   protected List poolTypes = new ArrayList();

   protected List responsibleTypes = new ArrayList();

   protected List transitionTypes = new ArrayList();

   protected List typeDeclarationTypes = new ArrayList();

   protected List workflowProcessTypes = new ArrayList();

   protected Map templateMap = new SequencedHashMap();

   protected boolean fullTemplateCheckForNonStandardTypes = true;

   public void init(JaWEComponent comp) {
      PROPERTYFILE_PATH = "org/enhydra/jawe/base/controller/properties/";
      PROPERTYFILE_NAME = "jawetypes.properties";
      super.init(comp);
   }

   public JaWEType getType(String typeId) {
      return (JaWEType) allTypes.get(typeId);
   }

   public boolean hasTemplateId(String templateId) {
      if (templateId == null) {
         return false;
      }
      return templateMap.containsKey(templateId);
   }

   public XMLElement getTemplateElement(String templateId) {
      if (templateMap.get(templateId) == null)
         System.out.println("TM="+templateMap.keySet()+", TID="+templateId);
      return (XMLElement) ((XMLElement) templateMap.get(templateId)).clone();
   }

   public boolean fillFromTemplate(XMLElement el, String tmplId) {
      if (tmplId == null) {
         return false;
      }
      XMLElement tmplEl = (XMLElement) templateMap.get(tmplId);
      if (tmplEl != null) {
         el.makeAs(tmplEl);
         return true;
      }
      return false;
   }

   public List getTypes(XMLElement el) {
      Class parentForEA = null;
      if (el instanceof ExtendedAttribute) {
         parentForEA = el.getParent().getParent().getClass();
      } else if (el instanceof ExtendedAttributes) {
         parentForEA = el.getParent().getClass();
      }
      return getTypes(el.getClass(), parentForEA);
   }

   public void addJaWEType(JaWEComponent controller, JaWEType jt, XMLElement templateEl) {
      Class typeClass = jt.getClassType();
      String id = jt.getTypeId();
      String name = XMLUtil.getShortClassName(typeClass.getName()) + "Type";
      List typeList = getTypesList(typeClass, null);

      typeList.remove(jt);
      allTypes.remove(id);

      typeList.add(jt);
      allTypes.put(id, jt);
      allTypesMapping.put("JaWETypes." + name + ".Id." + id, id);
      if (templateEl != null) {
         templateMap.put(id, templateEl);
      }

   }

   public void removeJaWEType(JaWEComponent controller, JaWEType jt) {
      String id = jt.getTypeId();
      Class jtcls = jt.getClassType();
      String name = XMLUtil.getShortClassName(jtcls.getName());
      allTypes.remove(id);
      activityTypes.remove(jt);
      allTypesMapping.remove("JaWETypes." + name + ".Id." + id);
      templateMap.remove(id);
   }

   protected List getTypesList(Class xpdlClass, Class xpdlParentForEA) {
      if (xpdlClass == Activities.class || xpdlClass == Activity.class) {
         return activityTypes;
      } else if (xpdlClass == ActivitySet.class || xpdlClass == ActivitySets.class) {
         return activitySetTypes;
      } else if (xpdlClass == ActualParameter.class
                 || xpdlClass == ActualParameters.class) {
         return actualParameterTypes;
      } else if (xpdlClass == Application.class || xpdlClass == Applications.class) {
         return applicationTypes;
      } else if (xpdlClass == Artifact.class || xpdlClass == Artifacts.class) {
         return artifactTypes;
      } else if (xpdlClass == Association.class || xpdlClass == Associations.class) {
         return associationTypes;
      } else if (xpdlClass == DataField.class || xpdlClass == DataFields.class) {
         return dataFieldTypes;
      } else if (xpdlClass == Deadline.class || xpdlClass == Deadlines.class) {
         return deadlineTypes;
      } else if (xpdlClass == EnumerationType.class) {
         return enumerationValueTypes;
      } else if (xpdlClass == ExtendedAttribute.class
                 || xpdlClass == ExtendedAttributes.class) {
         if (xpdlParentForEA == null
             || ((List) extAttribTypes.get(XMLUtil.getShortClassName(xpdlParentForEA.getName()))).size() == 0) {
            return extendedAttributeTypes;
         }
         return (List) extAttribTypes.get(XMLUtil.getShortClassName(xpdlParentForEA.getName()));
      } else if (xpdlClass == ExternalPackage.class
                 || xpdlClass == ExternalPackages.class) {
         return externalPackageTypes;
      } else if (xpdlClass == FormalParameter.class
                 || xpdlClass == FormalParameters.class) {
         return formalParameterTypes;
      } else if (xpdlClass == Lane.class || xpdlClass == Lanes.class) {
         return laneTypes;
      } else if (xpdlClass == Member.class) {
         return memberTypes;
      } else if (xpdlClass == Namespace.class || xpdlClass == Namespaces.class) {
         return namespaceTypes;
      } else if (xpdlClass == Package.class) {
         return packageTypes;
      } else if (xpdlClass == Participant.class || xpdlClass == Participants.class) {
         return participantTypes;
      } else if (xpdlClass == Pool.class || xpdlClass == Pools.class) {
         return poolTypes;
      } else if (xpdlClass == Responsible.class || xpdlClass == Responsibles.class) {
         return responsibleTypes;
      } else if (xpdlClass == Transition.class || xpdlClass == Transitions.class) {
         return transitionTypes;
      } else if (xpdlClass == TypeDeclaration.class
                 || xpdlClass == TypeDeclarations.class) {
         return typeDeclarationTypes;
      } else if (xpdlClass == WorkflowProcess.class
                 || xpdlClass == WorkflowProcesses.class) {
         return workflowProcessTypes;
      }
      return new ArrayList();
   }

   public List getTypes(Class xpdlClass, Class xpdlParentForEA) {
      return new ArrayList(getTypesList(xpdlClass, xpdlParentForEA));
   }

   public String getDefaultType(XMLElement el) {
      Class parentForEA = null;
      if (el instanceof ExtendedAttribute) {
         parentForEA = el.getParent().getParent().getClass();
      } else if (el instanceof ExtendedAttributes) {
         parentForEA = el.getParent().getClass();
      }
      return getDefaultType(el.getClass(), parentForEA);
   }

   public String getDefaultType(Class el, Class xpdlParentForEA) {
      if (el == Activity.class) {
         if (activityTypes.size() > 0) {
            return ((JaWEType) activityTypes.get(0)).getTypeId();
         }
      } else if (el == ActivitySet.class) {
         if (activitySetTypes.size() > 0) {
            return ((JaWEType) activitySetTypes.get(0)).getTypeId();
         }
      } else if (el == ActualParameter.class) {
         if (actualParameterTypes.size() > 0) {
            return ((JaWEType) actualParameterTypes.get(0)).getTypeId();
         }
      } else if (el == Application.class) {
         if (applicationTypes.size() > 0) {
            return ((JaWEType) applicationTypes.get(0)).getTypeId();
         }
      } else if (el == Artifact.class) {
         if (artifactTypes.size() > 0) {
            return ((JaWEType) artifactTypes.get(0)).getTypeId();
         }
      } else if (el == Association.class) {
         if (artifactTypes.size() > 0) {
            return ((JaWEType) associationTypes.get(0)).getTypeId();
         }
      } else if (el == DataField.class) {
         if (dataFieldTypes.size() > 0) {
            return ((JaWEType) dataFieldTypes.get(0)).getTypeId();
         }
      } else if (el == Deadline.class) {
         if (deadlineTypes.size() > 0) {
            return ((JaWEType) deadlineTypes.get(0)).getTypeId();
         }
      } else if (el == EnumerationType.class) {
         if (enumerationValueTypes.size() > 0) {
            return ((JaWEType) enumerationValueTypes.get(0)).getTypeId();
         }
      } else if (el == ExtendedAttribute.class) {
         if (xpdlParentForEA == null) {
            if (extendedAttributeTypes.size() > 0) {
               return ((JaWEType) extendedAttributeTypes.get(0)).getTypeId();
            }
         } else {
            List tps = (List) extAttribTypes.get(XMLUtil.getShortClassName(xpdlParentForEA.getName()));
            if (tps.size() > 0) {
               return ((JaWEType) tps.get(0)).getTypeId();
            }
         }
      } else if (el == ExternalPackage.class) {
         if (externalPackageTypes.size() > 0) {
            return ((JaWEType) externalPackageTypes.get(0)).getTypeId();
         }
      } else if (el == FormalParameter.class) {
         if (formalParameterTypes.size() > 0) {
            return ((JaWEType) formalParameterTypes.get(0)).getTypeId();
         }
      } else if (el == Lane.class) {
         if (laneTypes.size() > 0) {
            return ((JaWEType) laneTypes.get(0)).getTypeId();
         }
      } else if (el == Member.class) {
         if (memberTypes.size() > 0) {
            return ((JaWEType) memberTypes.get(0)).getTypeId();
         }
      } else if (el == Namespace.class) {
         if (namespaceTypes.size() > 0) {
            return ((JaWEType) namespaceTypes.get(0)).getTypeId();
         }
      } else if (el == Package.class) {
         if (packageTypes.size() > 0) {
            return ((JaWEType) packageTypes.get(0)).getTypeId();
         }
      } else if (el == Participant.class) {
         if (participantTypes.size() > 0) {
            return ((JaWEType) participantTypes.get(0)).getTypeId();
         }
      } else if (el == Pool.class) {
         if (poolTypes.size() > 0) {
            return ((JaWEType) poolTypes.get(0)).getTypeId();
         }
      } else if (el == Responsible.class) {
         if (responsibleTypes.size() > 0) {
            return ((JaWEType) responsibleTypes.get(0)).getTypeId();
         }
      } else if (el == Transition.class) {
         if (transitionTypes.size() > 0) {
            return ((JaWEType) transitionTypes.get(0)).getTypeId();
         }
      } else if (el == TypeDeclaration.class) {
         if (typeDeclarationTypes.size() > 0) {
            return ((JaWEType) typeDeclarationTypes.get(0)).getTypeId();
         }
      } else if (el == WorkflowProcess.class) {
         if (workflowProcessTypes.size() > 0) {
            return ((JaWEType) workflowProcessTypes.get(0)).getTypeId();
         }
      }

      return "";
   }

   protected void loadTypes(Class typeClass,
                            String name,
                            List list,
                            JaWEComponent controller,
                            Properties properties) {
      List types = ResourceManager.getResourceStrings(properties, "JaWETypes."
                                                                  + name + ".Id.", false);

      String orderStr = properties.getProperty("JaWETypes." + name + ".Order", "");
      String[] order = XMLUtil.tokenize(orderStr, ",");
      for (int i = 0; i < order.length; i++) {
         order[i] = order[i].trim();
      }

      if (order.length > 0) {
         List types2 = new ArrayList();
         for (int i = 0; i < order.length; i++) {
            if (types.contains(order[i])) {
               types2.add(order[i]);
            }
         }
         for (int i = 0; i < types.size(); i++) {
            if (!types2.contains(types.get(i))) {
               types2.add(types.get(i));
            }
         }
         types = types2;
      }
      for (int i = 0; i < types.size(); i++) {
         String id = ResourceManager.getResourceString(properties, "JaWETypes."
                                                                   + name + ".Id."
                                                                   + types.get(i));
         if (id.trim().equals("")) {
            if (allTypesMapping.containsKey("JaWETypes." + name + ".Id." + types.get(i))) {
               String defId = (String) allTypesMapping.get("JaWETypes."
                                                           + name + ".Id." + types.get(i));
               JaWEType jtype = (JaWEType) allTypes.get(defId);
               if (list.contains(jtype)) {
                  list.remove(jtype);
               }
               allTypes.remove(defId);
            }
            continue;
         }
         String langDepName = null;
         String dispName = null;
         ImageIcon icon = null;
         Color color = null;
         try {
            langDepName = ResourceManager.getResourceString(properties,
                                                                   "JaWETypes."
                                                                         + name
                                                                         + ".LangDepName."
                                                                         + types.get(i));
            dispName = controller.getSettings().getLanguageDependentString(langDepName);
         } catch (Exception e) {
            System.err.println("JaWETypes->locaTypes: Failed to load type "
                               + typeClass + " for name " + name);
         }
         try {
            icon = new ImageIcon(ResourceManager.getResource(properties, "JaWETypes."
                                                                         + name
                                                                         + ".Icon."
                                                                         + types.get(i)));
         } catch (Exception e) {
         }
         try {
            color = Utils.getColor(ResourceManager.getResourceString(properties,
                                                                     "JaWETypes."
                                                                           + name
                                                                           + ".Color."
                                                                           + types.get(i)));
         } catch (Exception e) {
         }

         JaWEType jtype;
         if (allTypes.containsKey(id)) {
            jtype = (JaWEType) allTypes.get(id);
            if (list.contains(jtype))
               list.remove(jtype);
         } else {
            jtype = new JaWEType(typeClass, id);
            if (color == null)
               color = Color.DARK_GRAY;
         }

         if (dispName != null)
            jtype.setDisplayName(dispName);
         else if (jtype.getDisplayName()==null)
            jtype.setDisplayName(langDepName);
         
         if (icon != null)
            jtype.setIcon(icon);

         if (color != null)
            jtype.setColor(color);

         list.add(jtype);
         allTypes.put(id, jtype);
         allTypesMapping.put("JaWETypes." + name + ".Id." + types.get(i), id);

         try {
            String templateXML = ResourceManager.getResourceString(properties,
                                                                   "JaWETypes."
                                                                         + name + "."
                                                                         + XPDL_TEMPLATE
                                                                         + "."
                                                                         + types.get(i));
            if (templateXML != null && !templateXML.equals("")) {
               Document doc = parseDocument(templateXML, true);
               XMLElement tmpl = createTemplateElement(doc);
               templateMap.put(id, tmpl);
            }

         } catch (Exception e) {
         }

      }
   }

   protected void loadCollections(Properties properties, JaWEComponent controller) {
      loadCollection(Activities.class, "Activities", controller, properties);
      loadCollection(ActivitySets.class, "ActivitySets", controller, properties);
      loadCollection(Applications.class, "Applications", controller, properties);
      loadCollection(Artifacts.class, "Artifacts", controller, properties);
      loadCollection(Associations.class, "Associations", controller, properties);
      loadCollection(DataFields.class, "DataFields", controller, properties);
      loadCollection(ExternalPackages.class, "ExternalPackages", controller, properties);
      loadCollection(FormalParameters.class, "FormalParameters", controller, properties);
      loadCollection(Lanes.class, "Lanes", controller, properties);
      loadCollection(Participants.class, "Participants", controller, properties);
      loadCollection(Pools.class, "Pools", controller, properties);
      loadCollection(WorkflowProcesses.class, "Processes", controller, properties);
      loadCollection(Transitions.class, "Transitions", controller, properties);
      loadCollection(TypeDeclarations.class, "TypeDeclarations", controller, properties);
   }

   protected void loadCollection(Class typeClass,
                                 String name,
                                 JaWEComponent controller,
                                 Properties properties) {
      String id = ResourceManager.getResourceString(properties, "JaWETypes."
                                                                + name + ".Id");
      if (id == null)
         return;
      String dispName = "";
      ImageIcon icon = null;
      Color color = Color.DARK_GRAY;
      try {
         String langDepName = ResourceManager.getResourceString(properties,
                                                                "JaWETypes."
                                                                      + name
                                                                      + ".LangDepName");
         dispName = controller.getSettings().getLanguageDependentString(langDepName);
         icon = new ImageIcon(ResourceManager.getResource(properties, "JaWETypes."
                                                                      + name + ".Icon"));
         color = Utils.getColor(ResourceManager.getResourceString(properties,
                                                                  "JaWETypes."
                                                                        + name + ".Color"));
      } catch (Exception e) {
         System.err.println("JaWETypes->loadCollection: Failed to load collection "
                            + typeClass + " with name " + name);
      }

      JaWEType jtype = new JaWEType(typeClass, id, dispName, icon, color);

      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes." + name + ".Id", id);
   }

   public void loadDefault(JaWEComponent controller, Properties properties) {

      fullTemplateCheckForNonStandardTypes = new Boolean(properties.getProperty("JaWETypes.FullTemplateCheckForNonStandardTypes",
                                                                                "true")).booleanValue();

      // check Activity
      String id;
      String dispName = "";
      // defaults
      ImageIcon defIcon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/default.gif"));
      Color defColor = Color.LIGHT_GRAY;

      id = JaWEConstants.ACTIVITY_TYPE_START;
      dispName = controller.getSettings().getLanguageDependentString("StartEventKey");
      ImageIcon icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/start.gif"));
      Color color = new Color(102, 204, 51);
      JaWEType jtype = new JaWEType(Activity.class, id, dispName, icon, color);
      activityTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.ActivityType.Id.start", id);

      id = JaWEConstants.ACTIVITY_TYPE_END;
      dispName = controller.getSettings().getLanguageDependentString("EndEventKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/end.gif"));
      color = new Color(236, 120, 98);
      jtype = new JaWEType(Activity.class, id, dispName, icon, color);
      activityTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.ActivityType.Id.end", id);

      id = JaWEConstants.ACTIVITY_TYPE_NO;
      dispName = controller.getSettings().getLanguageDependentString("NoKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/genericactivity.gif"));
      color = new Color(187, 247, 190);
      jtype = new JaWEType(Activity.class, id, dispName, icon, color);
      activityTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.ActivityType.Id.no_impl", id);

      id = JaWEConstants.ACTIVITY_TYPE_TASK_APPLICATION;
      dispName = controller.getSettings()
         .getLanguageDependentString("TaskApplicationKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/activitytool.gif"));
      color = new Color(255, 204, 204);
      jtype = new JaWEType(Activity.class, id, dispName, icon, color);
      activityTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.ActivityType.Id.task_application", id);

      id = JaWEConstants.ACTIVITY_TYPE_TASK_SCRIPT;
      dispName = controller.getSettings()
         .getLanguageDependentString("TaskScriptKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/activityscript.gif"));
      color = new Color(255, 255, 204);
      jtype = new JaWEType(Activity.class, id, dispName, icon, color);
      activityTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.ActivityType.Id.task_script", id);

      id = JaWEConstants.ACTIVITY_TYPE_SUBFLOW;
      dispName = controller.getSettings().getLanguageDependentString("SubFlowKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/subflowactivity.gif"));
      color = new Color(255, 106, 106);
      jtype = new JaWEType(Activity.class, id, dispName, icon, color);
      activityTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.ActivityType.Id.subflow", id);

      id = JaWEConstants.ACTIVITY_TYPE_BLOCK;
      dispName = controller.getSettings().getLanguageDependentString("BlockActivityKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/blockactivity.gif"));
      color = new Color(255, 165, 121);
      jtype = new JaWEType(Activity.class, id, dispName, icon, color);
      activityTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.ActivityType.Id.block", id);

      id = JaWEConstants.ACTIVITY_TYPE_ROUTE_EXCLUSIVE;
      dispName = controller.getSettings()
         .getLanguageDependentString("GatewayExclusiveKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/gatewayexclusive.gif"));
      color = new Color(255, 229, 124);
      jtype = new JaWEType(Activity.class, id, dispName, icon, color);
      activityTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.ActivityType.Id.gateway_exclusive", id);

      id = JaWEConstants.ACTIVITY_TYPE_ROUTE_PARALLEL;
      dispName = controller.getSettings()
         .getLanguageDependentString("GatewayParallelKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/gatewayparallel.gif"));
      color = new Color(255, 229, 124);
      jtype = new JaWEType(Activity.class, id, dispName, icon, color);
      activityTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.ActivityType.Id.gateway_parallel", id);

      // id = JaWEConstants.ACTIVITY_TYPE_ROUTE_INCLUSIVE;
      // dispName = controller.getSettings()
      // .getLanguageDependentString("GatewayInclusiveKey");
      // icon = new ImageIcon(JaWETypes.class.getClassLoader()
      // .getResource("org/enhydra/jawe/images/gatewayinclusive.gif"));
      // color = new Color(255, 229, 124);
      // jtype = new JaWEType(Activity.class, id, dispName, icon, color);
      // activityTypes.add(jtype);
      // allTypes.put(id, jtype);
      // allTypesMapping.put("JaWETypes.ActivityType.Id.gateway_inclusive", id);

      id = JaWEConstants.ACTIVITY_SET_TYPE_DEFAULT;
      dispName = controller.getSettings().getLanguageDependentString("ActivitySetKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/activityset.gif"));
      jtype = new JaWEType(ActivitySet.class, id, dispName, icon, defColor);
      activitySetTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.ActivitySetType.Id.default", id);

      id = JaWEConstants.ACTUAL_PARAMETER_DEFAULT;
      dispName = controller.getSettings()
         .getLanguageDependentString("ActualParameterKey");
      jtype = new JaWEType(ActualParameter.class, id, dispName, defIcon, defColor);
      actualParameterTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.ActualParameterType.Id.default", id);

      id = JaWEConstants.APPLICATION_TYPE_DEFAULT;
      dispName = controller.getSettings().getLanguageDependentString("ApplicationKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/applications.gif"));
      jtype = new JaWEType(Application.class, id, dispName, icon, defColor);
      applicationTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.ApplicationType.Id.default", id);

      id = JaWEConstants.ARTIFACT_TYPE_DATA_OBJECT;
      dispName = controller.getSettings().getLanguageDependentString("DataObjectKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/dataobject.gif"));
      color = new Color(255, 200, 0);
      jtype = new JaWEType(Artifact.class, id, dispName, icon, color);
      artifactTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.ArtifactType.Id.dataobject", id);

      id = JaWEConstants.ARTIFACT_TYPE_ANNOTATION;
      dispName = controller.getSettings().getLanguageDependentString("AnnotationKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/annotation.gif"));
      jtype = new JaWEType(Artifact.class, id, dispName, icon, defColor);
      artifactTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.ArtifactType.Id.annotation", id);

      id = JaWEConstants.ASSOCIATION_TYPE_DEFAULT;
      dispName = controller.getSettings().getLanguageDependentString("AssociationDirectionalKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/association.gif"));
      color = Color.BLACK;
      jtype = new JaWEType(Association.class, id, dispName, icon, color);
      associationTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.AssociationType.Id.default", id);

      id = JaWEConstants.ASSOCIATION_TYPE_NONE;
      dispName = controller.getSettings().getLanguageDependentString("AssociationNoneKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/association_nodirection.gif"));
      color = Color.BLACK;
      jtype = new JaWEType(Association.class, id, dispName, icon, color);
      associationTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.AssociationType.Id.no_direction", id);

      id = JaWEConstants.ASSOCIATION_TYPE_BIDIRECTIONAL;
      dispName = controller.getSettings().getLanguageDependentString("AssociationBidirectionalKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/association_bidirection.gif"));
      color = Color.BLACK;
      jtype = new JaWEType(Association.class, id, dispName, icon, color);
      associationTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.AssociationType.Id.bidirection", id);

      id = JaWEConstants.DATA_FIELD_DEFAULT;
      dispName = controller.getSettings().getLanguageDependentString("DataFieldKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/workflowrelevantdata.gif"));
      jtype = new JaWEType(DataField.class, id, dispName, icon, defColor);
      dataFieldTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.DataFieldType.Id.default", id);

      id = JaWEConstants.DEADLINE_DEFAULT;
      dispName = controller.getSettings().getLanguageDependentString("DeadlineKey");
      jtype = new JaWEType(Deadline.class, id, dispName, defIcon, defColor);
      deadlineTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.DeadlineType.Id.default", id);

      id = JaWEConstants.ENUMERATION_VALUE_DEFAULT;
      dispName = controller.getSettings()
         .getLanguageDependentString("EnumerationValueKey");
      jtype = new JaWEType(EnumerationValue.class, id, dispName, defIcon, defColor);
      enumerationValueTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.EnumerationValueType.Id.default", id);

      id = JaWEConstants.EXTENDED_ATTRIBUTE_DEFAULT;
      dispName = controller.getSettings()
         .getLanguageDependentString("ExtendedAttributeKey");
      jtype = new JaWEType(ExtendedAttribute.class, id, dispName, defIcon, defColor);
      extendedAttributeTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.ExtendedAttributeType.Id.default", id);

      id = JaWEConstants.EXTERNAL_PACKAGE_DEFAULT;
      dispName = controller.getSettings()
         .getLanguageDependentString("ExternalPackageKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/externalpackages.gif"));
      jtype = new JaWEType(ExternalPackage.class, id, dispName, icon, defColor);
      externalPackageTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.ExternalPackageType.Id.default", id);

      id = JaWEConstants.FORMAL_PARAMETER_DEFAULT;
      dispName = controller.getSettings()
         .getLanguageDependentString("FormalParameterKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/formalparameters.gif"));
      jtype = new JaWEType(FormalParameter.class, id, dispName, icon, defColor);
      formalParameterTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.FormalParameterType.Id.default", id);

      id = JaWEConstants.LANE_TYPE_DEFAULT;
      dispName = controller.getSettings().getLanguageDependentString("LaneKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/lane.gif"));
      color = new Color(255, 255, 215);
      jtype = new JaWEType(Lane.class, id, dispName, icon, color);
      laneTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.LaneType.Id.default", id);

      id = JaWEConstants.MEMBER_DEFAULT;
      dispName = controller.getSettings().getLanguageDependentString("MemberKey");
      jtype = new JaWEType(Member.class, id, dispName, defIcon, defColor);
      memberTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.MemeberType.Id.default", id);

      id = JaWEConstants.NAMESPACE_DEFAULT;
      dispName = controller.getSettings().getLanguageDependentString("NamespaceKey");
      jtype = new JaWEType(Namespace.class, id, dispName, defIcon, defColor);
      namespaceTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.NamespaceType.Id.default", id);

      id = JaWEConstants.PACKAGE_DEFAULT;
      dispName = controller.getSettings().getLanguageDependentString("PackageKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/package.gif"));
      jtype = new JaWEType(Package.class, id, dispName, icon, defColor);
      packageTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.PackageType.Id.default", id);

      id = JaWEConstants.PACKAGE_EXTERNAL;
      dispName = controller.getSettings()
         .getLanguageDependentString("ExternalPackageKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/externalpackages.gif"));
      jtype = new JaWEType(Package.class, id, dispName, icon, defColor);
      externalPackageTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.PackageType.Id.external", id);

      id = JaWEConstants.PACKAGE_TRANSIENT;
      dispName = controller.getSettings()
         .getLanguageDependentString("TransientPackageKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/transientpackage.gif"));
      jtype = new JaWEType(ExternalPackage.class, id, dispName, icon, defColor);
      externalPackageTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.PackageType.Id.transient", id);

      id = JaWEConstants.PARTICIPANT_TYPE_HUMAN;
      dispName = controller.getSettings().getLanguageDependentString("HUMANKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/participant.gif"));
      color = new Color(240, 240, 240);
      jtype = new JaWEType(Participant.class, id, dispName, icon, color);
      participantTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.ParticipantType.Id.human", id);

      id = JaWEConstants.PARTICIPANT_TYPE_ROLE;
      dispName = controller.getSettings().getLanguageDependentString("ROLEKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/participantrole.gif"));
      color = new Color(220, 220, 220);
      jtype = new JaWEType(Participant.class, id, dispName, icon, color);
      participantTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.ParticipantType.Id.role", id);

      id = JaWEConstants.PARTICIPANT_TYPE_ORGANIZATIONAL_UNIT;
      dispName = controller.getSettings()
         .getLanguageDependentString("ORGANIZATIONAL_UNITKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/participantorgunit.png"));
      color = new Color(200, 200, 200);
      jtype = new JaWEType(Participant.class, id, dispName, icon, color);
      participantTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.ParticipantType.Id.org_unit", id);

      id = JaWEConstants.PARTICIPANT_TYPE_RESOURCE;
      dispName = controller.getSettings().getLanguageDependentString("RESOURCEKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/participantresource.png"));
      color = new Color(146, 146, 180);
      jtype = new JaWEType(Participant.class, id, dispName, icon, color);
      participantTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.ParticipantType.Id.resource", id);

      id = JaWEConstants.PARTICIPANT_TYPE_RESOURCE_SET;
      dispName = controller.getSettings().getLanguageDependentString("RESOURCE_SETKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/participantresourceset.png"));
      color = new Color(146, 146, 160);
      jtype = new JaWEType(Participant.class, id, dispName, icon, color);
      participantTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.ParticipantType.Id.resource_set", id);

      id = JaWEConstants.PARTICIPANT_TYPE_SYSTEM;
      dispName = controller.getSettings().getLanguageDependentString("SYSTEMKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/participantsystem.png"));
      color = new Color(40, 145, 195);
      jtype = new JaWEType(Participant.class, id, dispName, icon, color);
      participantTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.ParticipantType.Id.system", id);

      id = JaWEConstants.POOL_TYPE_DEFAULT;
      dispName = controller.getSettings().getLanguageDependentString("PoolKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/pool.gif"));
      color = new Color(255, 255, 215);
      jtype = new JaWEType(Pool.class, id, dispName, icon, color);
      poolTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.PoolType.Id.default", id);

      id = JaWEConstants.RESPONSIBLE_DEFAULT;
      dispName = controller.getSettings().getLanguageDependentString("ResponsibleKey");
      jtype = new JaWEType(Responsible.class, id, dispName, defIcon, defColor);
      responsibleTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.ResponsibleType.Id.default", id);

      id = JaWEConstants.TRANSITION_TYPE_UNCONDITIONAL;
      dispName = controller.getSettings()
         .getLanguageDependentString("UNCONTROLLED_FLOWKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/unconditional.gif"));
      color = Color.BLACK;
      jtype = new JaWEType(Transition.class, id, dispName, icon, color);
      transitionTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.TransitionType.Id.default", id);

      id = JaWEConstants.TRANSITION_TYPE_CONDITIONAL;
      dispName = controller.getSettings()
         .getLanguageDependentString("CONDITIONAL_FLOWKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/transition.gif"));
      color = Color.BLACK;
      jtype = new JaWEType(Transition.class, id, dispName, icon, color);
      transitionTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.TransitionType.Id.condition", id);

      id = JaWEConstants.TRANSITION_TYPE_OTHERWISE;
      dispName = controller.getSettings().getLanguageDependentString("DEFAULT_FLOWKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/transitionotherwise.gif"));
      color = new Color(255, 128, 0);
      jtype = new JaWEType(Transition.class, id, dispName, icon, color);
      transitionTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.TransitionType.Id.otherwise", id);

      id = JaWEConstants.TRANSITION_TYPE_EXCEPTION;
      dispName = controller.getSettings().getLanguageDependentString("EXCEPTION_FLOWKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/transitionexception.gif"));
      color = new Color(255, 128, 255);
      jtype = new JaWEType(Transition.class, id, dispName, icon, color);
      transitionTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.TransitionType.Id.exception", id);

      id = JaWEConstants.TRANSITION_TYPE_DEFAULTEXCEPTION;
      dispName = controller.getSettings()
         .getLanguageDependentString("DEFAULT_EXCEPTION_FLOWKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/transitiondefaultexception.gif"));
      color = new Color(204, 0, 0);
      jtype = new JaWEType(Transition.class, id, dispName, icon, color);
      transitionTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.TransitionType.Id.defaultexception", id);

      id = JaWEConstants.TYPE_DECLARATION_DEFAULT;
      dispName = controller.getSettings()
         .getLanguageDependentString("TypeDeclarationKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/typedeclarations.gif"));
      jtype = new JaWEType(TypeDeclaration.class, id, dispName, icon, defColor);
      typeDeclarationTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.TypeDeclarationType.Id.default", id);

      id = JaWEConstants.WORKFLOW_PROCESS_TYPE_DEFAULT;
      dispName = controller.getSettings()
         .getLanguageDependentString("WorkflowProcessKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/process.gif"));
      jtype = new JaWEType(WorkflowProcess.class, id, dispName, icon, defColor);
      workflowProcessTypes.add(jtype);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.WorkflowProcessType.Id.default", id);

      id = JaWEConstants.ACTIVITIES;
      dispName = controller.getSettings().getLanguageDependentString("ActivitiesKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/activities.gif"));
      jtype = new JaWEType(Activities.class, id, dispName, icon, defColor);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.Activities.Id", id);

      id = JaWEConstants.ACTIVITYSETS;
      dispName = controller.getSettings().getLanguageDependentString("ActivitySetsKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/activitysets.gif"));
      jtype = new JaWEType(ActivitySets.class, id, dispName, icon, defColor);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.ActivitySets.Id", id);

      id = JaWEConstants.APPLICATIONS;
      dispName = controller.getSettings().getLanguageDependentString("ApplicationsKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/applications.gif"));
      jtype = new JaWEType(Applications.class, id, dispName, icon, defColor);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.Applications.Id", id);

      id = JaWEConstants.DATAFIELDS;
      dispName = controller.getSettings().getLanguageDependentString("DataFieldsKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/workflowrelevantdata.gif"));
      jtype = new JaWEType(DataFields.class, id, dispName, icon, defColor);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.DataFields.Id", id);

      id = JaWEConstants.EXTERNALPACKAGES;
      dispName = controller.getSettings()
         .getLanguageDependentString("ExternalPackagesKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/packages.gif"));
      jtype = new JaWEType(ExternalPackages.class, id, dispName, icon, defColor);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.ExternalPackages.Id", id);

      id = JaWEConstants.FORMALPARAMETERS;
      dispName = controller.getSettings()
         .getLanguageDependentString("FormalParametersKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/formalparameters.gif"));
      jtype = new JaWEType(FormalParameters.class, id, dispName, icon, defColor);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.FormalParameters.Id", id);

      id = JaWEConstants.LANES;
      dispName = controller.getSettings().getLanguageDependentString("LanesKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/lanes.gif"));
      jtype = new JaWEType(Lanes.class, id, dispName, icon, defColor);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.Lanes.Id", id);

      id = JaWEConstants.PARTICIPANTS;
      dispName = controller.getSettings().getLanguageDependentString("ParticipantsKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/participants.gif"));
      jtype = new JaWEType(Participants.class, id, dispName, icon, defColor);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.Participants.Id", id);

      id = JaWEConstants.POOLS;
      dispName = controller.getSettings().getLanguageDependentString("PoolsKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/pools.gif"));
      jtype = new JaWEType(Pools.class, id, dispName, icon, defColor);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.Pools.Id", id);

      id = JaWEConstants.WORKFLOWPROCESSES;
      dispName = controller.getSettings()
         .getLanguageDependentString("WorkflowProcessesKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/processes.gif"));
      jtype = new JaWEType(WorkflowProcesses.class, id, dispName, icon, defColor);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.WorkflowProcesses.Id", id);

      id = JaWEConstants.TRANSITIONS;
      dispName = controller.getSettings().getLanguageDependentString("TransitionsKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/transitions.gif"));
      jtype = new JaWEType(Transitions.class, id, dispName, icon, defColor);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.Transitions.Id", id);

      id = JaWEConstants.TYPEDECLARATIONS;
      dispName = controller.getSettings()
         .getLanguageDependentString("TypeDeclarationsKey");
      icon = new ImageIcon(JaWETypes.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/typedeclarations.gif"));
      jtype = new JaWEType(TypeDeclarations.class, id, dispName, icon, defColor);
      allTypes.put(id, jtype);
      allTypesMapping.put("JaWETypes.TypeDeclarations.Id", id);

      loadTypes(Activity.class, "ActivityType", activityTypes, controller, properties);
      loadTypes(ActivitySet.class,
                "ActivitySetType",
                activitySetTypes,
                controller,
                properties);
      loadTypes(ActualParameter.class,
                "ActualParameterType",
                actualParameterTypes,
                controller,
                properties);
      loadTypes(Application.class,
                "ApplicationType",
                applicationTypes,
                controller,
                properties);
      loadTypes(Artifact.class, "ArtifactType", artifactTypes, controller, properties);
      loadTypes(DataField.class, "DataFieldType", dataFieldTypes, controller, properties);
      loadTypes(Deadline.class, "DeadlineType", deadlineTypes, controller, properties);
      loadTypes(EnumerationType.class,
                "EnumerationValueType",
                enumerationValueTypes,
                controller,
                properties);

      loadTypes(ExtendedAttribute.class,
                "ExtendedAttributeType",
                extendedAttributeTypes,
                controller,
                properties);

      // Activity EAs
      List l = new ArrayList();
      loadTypes(ExtendedAttribute.class,
                "ExtendedAttributeType.Activity",
                l,
                controller,
                properties);
      extAttribTypes.put("Activity", l);
      // Application EAs
      l = new ArrayList();
      loadTypes(ExtendedAttribute.class,
                "ExtendedAttributeType.Application",
                l,
                controller,
                properties);
      extAttribTypes.put("Application", l);
      // DataField EAs
      l = new ArrayList();
      loadTypes(ExtendedAttribute.class,
                "ExtendedAttributeType.DataField",
                l,
                controller,
                properties);
      extAttribTypes.put("DataField", l);
      // Package EAs
      l = new ArrayList();
      loadTypes(ExtendedAttribute.class,
                "ExtendedAttributeType.Package",
                l,
                controller,
                properties);
      extAttribTypes.put("Package", l);
      // Participant EAs
      l = new ArrayList();
      loadTypes(ExtendedAttribute.class,
                "ExtendedAttributeType.Participant",
                l,
                controller,
                properties);
      extAttribTypes.put("Participant", l);
      // Tool EAs
      l = new ArrayList();
      loadTypes(ExtendedAttribute.class,
                "ExtendedAttributeType.Tool",
                l,
                controller,
                properties);
      extAttribTypes.put("Tool", l);
      // Transition EAs
      l = new ArrayList();
      loadTypes(ExtendedAttribute.class,
                "ExtendedAttributeType.Transition",
                l,
                controller,
                properties);
      extAttribTypes.put("Transition", l);
      // TypeDeclaration EAs
      l = new ArrayList();
      loadTypes(ExtendedAttribute.class,
                "ExtendedAttributeType.TypeDeclaration",
                l,
                controller,
                properties);
      extAttribTypes.put("TypeDeclaration", l);
      // WorkflowProcess EAs
      l = new ArrayList();
      loadTypes(ExtendedAttribute.class,
                "ExtendedAttributeType.WorkflowProcess",
                l,
                controller,
                properties);
      extAttribTypes.put("WorkflowProcess", l);

      loadTypes(ExternalPackage.class,
                "ExternalPackageType",
                externalPackageTypes,
                controller,
                properties);
      loadTypes(FormalParameter.class,
                "FormalParameterType",
                formalParameterTypes,
                controller,
                properties);
      loadTypes(Lane.class, "LaneType", laneTypes, controller, properties);
      loadTypes(Member.class, "MemberType", memberTypes, controller, properties);
      loadTypes(Namespace.class, "NamespaceType", namespaceTypes, controller, properties);
      loadTypes(Package.class, "PackageType", packageTypes, controller, properties);
      loadTypes(Participant.class,
                "ParticipantType",
                participantTypes,
                controller,
                properties);
      loadTypes(Pool.class, "PoolType", poolTypes, controller, properties);
      loadTypes(Responsible.class,
                "ResponsibleType",
                responsibleTypes,
                controller,
                properties);
      loadTypes(Transitions.class,
                "TransitionType",
                transitionTypes,
                controller,
                properties);
      loadTypes(TypeDeclaration.class,
                "TypeDeclarationType",
                typeDeclarationTypes,
                controller,
                properties);
      loadTypes(WorkflowProcess.class,
                "WorkflowProcessType",
                workflowProcessTypes,
                controller,
                properties);
      loadCollections(properties, controller);
   }

   protected Document parseDocument(String toParse, boolean isFile) {
      Document doc = null;

      // Create a Xerces DOM Parser
      DOMParser parser = new DOMParser();

      // Parse the Document and traverse the DOM
      try {
         parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error",
                           true);
         if (isFile) {
            // System.err.println("Parsing from file");
            File f = new File(toParse);
            if (!f.isAbsolute()) {
               toParse = getCurrentConfigFolder() + "/" + toParse;
               f = new File(toParse);
            }
            if (!f.exists()) {
               return null;
            }
            parser.parse(new InputSource(new FileInputStream(f)));
         } else {
            // System.err.println("Parsing from stream");
            parser.parse(new InputSource(new StringReader(toParse)));
         }
         doc = parser.getDocument();
      } catch (Exception ex) {
         ex.printStackTrace();
         System.err.println("Fatal error while parsing document");
         doc = null;
      }
      return doc;
   }

   protected XMLElement createTemplateElement(Document doc) throws Exception {
      Element elem = doc.getDocumentElement();
      String elName = elem.getNodeName();
      elName = "org.enhydra.jxpdl.elements." + elName;
      XMLElement el = null;
      if (!elName.endsWith("Package")) {
         try {
            el = (XMLElement) Class.forName(elName).getConstructors()[0].newInstance(new Object[] {
               null
            });
         } catch (Exception ex) {
            el = (XMLElement) Class.forName(elName).getConstructors()[0].newInstance(new Object[] {
                                                                                                   null, false
                                                                                                });
         }
      } else {
         el = new Package();
      }
      if (el instanceof XMLComplexElement) {
         JaWEManager.getInstance()
            .getXPDLHandler()
            .getXPDLRepositoryHandler()
            .fromXML(doc.getDocumentElement(), (XMLComplexElement) el);
      } else if (el instanceof XMLSimpleElement) {
         JaWEManager.getInstance()
            .getXPDLHandler()
            .getXPDLRepositoryHandler()
            .fromXML(doc.getDocumentElement(), (XMLSimpleElement) el);
      }
      return el;
   }

   protected void toString(XMLElement el) {
      if (el instanceof Activity) {
         Activity act = (Activity) el;
         String str = "\n\tId=" + act.getId();
         str += "\n\tName=" + act.getName();
         int acttype = act.getActivityType();
         str += "\n\tType=" + acttype;
         if (acttype == XPDLConstants.ACTIVITY_TYPE_TASK_APPLICATION) {
            TaskApplication ts = act.getActivityTypes()
               .getImplementation()
               .getImplementationTypes()
               .getTask()
               .getTaskTypes()
               .getTaskApplication();
            str += "\n\tApplicationId=" + ts.getId();
            Iterator it = ts.getActualParameters().toElements().iterator();
            int i = 1;
            while (it.hasNext()) {
               ActualParameter ap = (ActualParameter) it.next();
               str += "\n\t   Ap" + (i++) + "=" + ap.toValue();
            }
         }
         System.err.println("Activity data for " + act + " is:" + str);
      } else {
         if (el instanceof XMLSimpleElement) {
            System.err.println("\n\tElement type=" + el.toName());
         } else if (el instanceof XMLComplexElement) {
            List attributes = ((XMLComplexElement) el).getXMLAttributes();
            String str = "\n\tElement type=" + el.toName();
            for (int i = 0; i < attributes.size(); i++) {
               XMLAttribute attr = (XMLAttribute) attributes.get(i);
               str += "\n\t    "
                      + i + ". attribute [" + attr.toName() + "," + attr.toValue() + "]";
            }
            List elems = ((XMLComplexElement) el).getXMLElements();
            for (int i = 0; i < elems.size(); i++) {
               XMLElement attr = (XMLElement) elems.get(i);
               if (attr instanceof XMLSimpleElement) {
                  str += "\n\t    "
                         + i + ". simple el [" + attr.toName() + "," + attr.toValue()
                         + "]";
               } else {
                  toString(attr);
               }
            }
            System.err.println(str);
         } else if (el instanceof XMLCollection) {
            String str = "\n\tElement type=" + el.toName();
            List elems = ((XMLCollection) el).toElements();
            for (int i = 0; i < elems.size(); i++) {
               XMLElement attr = (XMLElement) elems.get(i);
               if (attr instanceof XMLAttribute) {
                  str += "\n\t    "
                         + i + ". attribute [" + attr.toName() + "," + attr.toValue()
                         + "]";
               }
               if (attr instanceof XMLSimpleElement) {
                  str += "\n\t    "
                         + i + ". simple el [" + attr.toName() + "," + attr.toValue()
                         + "]";
               } else {
                  toString(attr);
               }
            }
            System.err.println(str);
         }
      }
   }

   protected String getCurrentConfigFolder() {
      String currentConfig = JaWEConstants.JAWE_USER_HOME + "/templates";
      String cch = System.getProperty(JaWEConstants.JAWE_CURRENT_CONFIG_HOME);
      if (cch != null) {
         currentConfig = cch + "/templates";
      }
      return currentConfig;
   }

   public JaWEType compareToTemplate(XMLElement el) {
      String type = JaWEEAHandler.getJaWEType(el);
      if (type != null) {
         JaWEType jt = getType(type);
         if (jt != null) {
            if (!fullTemplateCheckForNonStandardTypes
                || (hasTemplateId(type) && compareToTemplate(el,
                                                             el,
                                                             getTemplateElement(type)))) {
               return jt;
            }
         }
      }
      return null;
   }

   protected boolean compareToTemplate(XMLElement topEl, XMLElement el, XMLElement tmplEl) {
      // System.err.println("BTR - Checking el "+el+" with tmpl el "+tmplEl);
      boolean ret = true;
      System.out.println("Checking against template");
      if (tmplEl.getClass() == el.getClass()) {
         if (el instanceof XMLSimpleElement && !tmplEl.isEmpty()) {
            ret = el.toValue().equals(tmplEl.toValue());
         } else if (el instanceof XMLAttribute) {
            boolean checkIt = true;
            if ((el.toName().equals("Id") || el.toName().equals("Name"))
                && el.getParent() == topEl && !(el instanceof ExtendedAttribute)) {
               checkIt = false;
            } else if (el.toName().equals("Value")) {
               checkIt = false;
            }
            if (checkIt) {
               XMLAttribute attrEl = (XMLAttribute) el;
               XMLAttribute attrTmplEl = (XMLAttribute) tmplEl;
               ret = (attrEl.getChoices() == null ? attrTmplEl.getChoices() == null
                                                 : attrEl.getChoices()
                                                    .equals(attrTmplEl.getChoices()));
               if (ret && !tmplEl.isEmpty()) {
                  ret = el.toValue().equals(tmplEl.toValue());
               }
            }
            // System.err.println("SE OR XMLA: ret="+ret);
         } else if (el instanceof XMLComplexElement) {
            if (!tmplEl.toValue().equals("") && !tmplEl.toValue().equals(el.toValue())) {
               ret = false;
               // System.err.println("CMPLXEL: different values");
            } else {
               XMLComplexElement cmplxEl = (XMLComplexElement) el;
               Iterator it = ((XMLComplexElement) tmplEl).toElementMap()
                  .entrySet()
                  .iterator();
               while (it.hasNext()) {
                  Map.Entry me = (Map.Entry) it.next();
                  String subElName = (String) me.getKey();
                  XMLElement subEl = (XMLElement) me.getValue();
                  if (!subEl.isEmpty()) {
                     if (!compareToTemplate(topEl, cmplxEl.get(subElName), subEl)) {
                        ret = false;
                        // System.err.println("CMPLXEL: different values for subEl
                        // "+subEl);
                        break;
                     }
                  }
               }
            }
         } else if (el instanceof XMLCollection) {
            if (!tmplEl.toValue().equals("") && !tmplEl.toValue().equals(el.toValue())) {
               ret = false;
               // System.err.println("COLL: different values");
            } else {
               XMLCollection col = (XMLCollection) el;
               List cels = ((XMLCollection) tmplEl).toElements();
               for (int i = 0; i < cels.size(); i++) {
                  XMLElement tcel = (XMLElement) cels.get(i);
                  if (tcel instanceof ActualParameter || tcel instanceof FormalParameter) {
                     if (!compareToTemplate(topEl, col.get(i), tcel)) {
                        ret = false;
                        // System.err.println("COLL: can't find col el "+cel+",
                        // val="+cel.toValue()+" in col at place "+i);
                        break;
                     }
                  } else {
                     boolean btt = false;
                     for (int j = 0; j < col.size(); j++) {
                        XMLElement e = (XMLElement) col.get(j);
                        if (compareToTemplate(topEl, e, tcel)) {
                           btt = true;
                           break;
                        }
                     }
                     if (!btt) {
                        ret = btt;
                     }
                  }
               }
            }
         } else if (el instanceof XMLComplexChoice) {
            XMLElement tmplChsn = ((XMLComplexChoice) tmplEl).getChoosen();
            XMLElement elChsn = ((XMLComplexChoice) el).getChoosen();
            ret = compareToTemplate(topEl, elChsn, tmplChsn);
            // System.err.println("CMPLCHC: ret="+ret);
         }
      } else {
         ret = false;
         // System.err.println("Different Java classes");
      }
      // System.err.println("Final ret="+ret);

      return ret;
   }

}
