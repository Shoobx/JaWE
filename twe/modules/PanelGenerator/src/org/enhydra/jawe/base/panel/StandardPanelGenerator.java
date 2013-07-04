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

package org.enhydra.jawe.base.panel;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.Settings;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.base.panel.panels.PanelUtilities;
import org.enhydra.jawe.base.panel.panels.XMLActualParametersPanel;
import org.enhydra.jawe.base.panel.panels.XMLAdvancedActualParametersPanel;
import org.enhydra.jawe.base.panel.panels.XMLBasicPanel;
import org.enhydra.jawe.base.panel.panels.XMLCheckboxPanel;
import org.enhydra.jawe.base.panel.panels.XMLColorPanel;
import org.enhydra.jawe.base.panel.panels.XMLComboChoicePanel;
import org.enhydra.jawe.base.panel.panels.XMLComboPanel;
import org.enhydra.jawe.base.panel.panels.XMLComboPanelWithReferenceLink;
import org.enhydra.jawe.base.panel.panels.XMLDataTypesPanel;
import org.enhydra.jawe.base.panel.panels.XMLGroupPanel;
import org.enhydra.jawe.base.panel.panels.XMLHighlightPanelWithReferenceLink;
import org.enhydra.jawe.base.panel.panels.XMLListPanel;
import org.enhydra.jawe.base.panel.panels.XMLLocationPanel;
import org.enhydra.jawe.base.panel.panels.XMLMultiLineHighlightPanelWithChoiceButton;
import org.enhydra.jawe.base.panel.panels.XMLMultiLineTextPanelWithOptionalChoiceButtons;
import org.enhydra.jawe.base.panel.panels.XMLPanel;
import org.enhydra.jawe.base.panel.panels.XMLRadioPanel;
import org.enhydra.jawe.base.panel.panels.XMLTabbedPanel;
import org.enhydra.jawe.base.panel.panels.XMLTablePanel;
import org.enhydra.jawe.base.panel.panels.XMLTablePanelForVariables;
import org.enhydra.jawe.base.panel.panels.XMLTextPanel;
import org.enhydra.jawe.base.panel.panels.XMLTextPanelWithReferenceLink;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLComplexChoice;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLEmptyChoiceElement;
import org.enhydra.jxpdl.XMLSimpleElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ActivitySet;
import org.enhydra.jxpdl.elements.Application;
import org.enhydra.jxpdl.elements.Artifact;
import org.enhydra.jxpdl.elements.Association;
import org.enhydra.jxpdl.elements.BasicType;
import org.enhydra.jxpdl.elements.BlockActivity;
import org.enhydra.jxpdl.elements.Condition;
import org.enhydra.jxpdl.elements.ConformanceClass;
import org.enhydra.jxpdl.elements.DataField;
import org.enhydra.jxpdl.elements.DataFields;
import org.enhydra.jxpdl.elements.DataObject;
import org.enhydra.jxpdl.elements.DataType;
import org.enhydra.jxpdl.elements.DataTypes;
import org.enhydra.jxpdl.elements.Deadlines;
import org.enhydra.jxpdl.elements.DeclaredType;
import org.enhydra.jxpdl.elements.Description;
import org.enhydra.jxpdl.elements.Documentation;
import org.enhydra.jxpdl.elements.EnumerationType;
import org.enhydra.jxpdl.elements.ExceptionName;
import org.enhydra.jxpdl.elements.ExpressionType;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;
import org.enhydra.jxpdl.elements.ExternalPackage;
import org.enhydra.jxpdl.elements.ExternalPackages;
import org.enhydra.jxpdl.elements.ExternalReference;
import org.enhydra.jxpdl.elements.FormalParameter;
import org.enhydra.jxpdl.elements.FormalParameters;
import org.enhydra.jxpdl.elements.Icon;
import org.enhydra.jxpdl.elements.InitialValue;
import org.enhydra.jxpdl.elements.Join;
import org.enhydra.jxpdl.elements.Package;
import org.enhydra.jxpdl.elements.Participant;
import org.enhydra.jxpdl.elements.ParticipantType;
import org.enhydra.jxpdl.elements.Performer;
import org.enhydra.jxpdl.elements.Pool;
import org.enhydra.jxpdl.elements.RecordType;
import org.enhydra.jxpdl.elements.Responsible;
import org.enhydra.jxpdl.elements.Responsibles;
import org.enhydra.jxpdl.elements.SchemaType;
import org.enhydra.jxpdl.elements.Script;
import org.enhydra.jxpdl.elements.Split;
import org.enhydra.jxpdl.elements.SubFlow;
import org.enhydra.jxpdl.elements.TaskApplication;
import org.enhydra.jxpdl.elements.TimeEstimation;
import org.enhydra.jxpdl.elements.Transition;
import org.enhydra.jxpdl.elements.TransitionRefs;
import org.enhydra.jxpdl.elements.TypeDeclaration;
import org.enhydra.jxpdl.elements.UnionType;
import org.enhydra.jxpdl.elements.WorkflowProcess;
import org.enhydra.jxpdl.utilities.SequencedHashMap;

/**
 * Used to generate panels for all XPDL entities.
 * 
 * @author Zoran Milakovic
 * @author Sasa Bojanic
 */
public class StandardPanelGenerator implements PanelGenerator {

   // public static final String
   // HIDE_COLLECTIONS_AND_COMPLEX_CHOICES="HideCollectionsAndComplexChoices";

   protected PanelContainer pc;

   // public String getLanguageDependentString (String nm) {
   // return pc.getPanelSettings().getLanguageDependentString(nm);
   // }
   //
   public StandardPanelGenerator() {
   }

   public void setPanelContainer(PanelContainer pc) {
      this.pc = pc;
   }

   public PanelContainer getPanelContainer() {
      return pc;
   }

   public XMLPanel getPanel(Activity el) {

      List panels = new ArrayList();
      Set hidden = PanelUtilities.getHiddenElements(getPanelContainer(),
                                                    "XMLGroupPanel",
                                                    el);
      for (int i = 1;; i++) {
         try {
            XMLPanel p = getPanel(el, i, hidden);
            if (p != null) {
               panels.add(p);
            }
         } catch (Exception ex) {
//            ex.printStackTrace();
            break;
         }
      }

      if (panels.size() > 1) {
         return new XMLTabbedPanel(getPanelContainer(),
                                   el,
                                   panels,
                                   JaWEManager.getInstance()
                                      .getLabelGenerator()
                                      .getLabel(el),
                                   false,
                                   null);
      } else if (panels.size() == 1) {
         return (XMLPanel) panels.get(0);
      } else {
         return new XMLBasicPanel();
      }

   }

   protected XMLPanel getPanel(Activity el, int no, Set hidden) {
      XMLPanel p = null;
      switch (no) {
         case 1:
            List panelElements = new ArrayList();
            if (!hidden.contains(el.get("Id"))) {
               panelElements.add(el.get("Id"));
            }
            if (!hidden.contains(el.get("Name"))) {
               panelElements.add(el.get("Name"));
            }
            if (!hidden.contains(el.getFirstPerformerObj())) {
               int type = el.getActivityType();
               if (type == XPDLConstants.ACTIVITY_TYPE_NO
                   || type == XPDLConstants.ACTIVITY_TYPE_TASK_APPLICATION) {
                  panelElements.add(el.getFirstPerformerObj());
               }
            }
            if (!hidden.contains(el.get("StartMode"))) {
               panelElements.add(el.get("StartMode"));
            }
            if (!hidden.contains(el.get("FinishMode"))) {
               panelElements.add(el.get("FinishMode"));
            }
            if (!hidden.contains(el.getDeadlines())) {
               panelElements.add(el.getDeadlines());
            }
            if (!hidden.contains(el.get("Priority"))) {
               panelElements.add(el.get("Priority"));
            }
            if (!hidden.contains(el.get("Limit"))) {
               panelElements.add(el.get("Limit"));
            }
            if (!hidden.contains(el.get("Icon"))) {
               panelElements.add(el.get("Icon"));
            }
            if (!hidden.contains(el.get("Documentation"))) {
               panelElements.add(el.get("Documentation"));
            }
            if (!hidden.contains(el.get("Description"))) {
               panelElements.add(el.get("Description"));
            }
            if (panelElements.size() > 0) {
               p = new XMLGroupPanel(getPanelContainer(),
                                     el,
                                     panelElements,
                                     getPanelContainer().getLanguageDependentString("GeneralKey"),
                                     true,
                                     false,
                                     true,
                                     null);
            }
            break;

         case 2:
            if (!hidden.contains(el.getActivityTypes())) {
               ActivityTypesChoiceElement atce = new ActivityTypesChoiceElement(el.getActivityTypes());
               atce.setReadOnly(el.getActivityTypes().isReadOnly());
               p = this.getPanel(atce);
               if (el.getActivityType() == XPDLConstants.ACTIVITY_TYPE_ROUTE
                   && el.getActivityTypes()
                      .getRoute()
                      .getGatewayType()
                      .equals(XPDLConstants.JOIN_SPLIT_TYPE_EXCLUSIVE)
                   && XMLUtil.getNonExceptionalOutgoingTransitions(el).size() > 1) {

                  if (p instanceof XMLGroupPanel) {
                     ((XMLGroupPanel) p).addToGroup(XMLUtil.getSplit(el)
                        .getTransitionRefs());
                  } else {
                     p.add(getPanel(XMLUtil.getSplit(el).getTransitionRefs()));
                  }
               }
            }
            break;

         case 3:
            if (!hidden.contains(el.getSimulationInformation())) {
               p = this.getPanel(el.getSimulationInformation());
            }
            break;

         case 4:
            if (!hidden.contains(el.getExtendedAttributes())) {
               p = this.getPanel(el.getExtendedAttributes());
            }
            break;
         default:
            throw new RuntimeException();

      }
      return p;
   }

   public XMLPanel getPanel(ActivitySet el) {
      return generateStandardGroupPanel(el, true, false);
   }

   public XMLPanel getPanel(Application el) {
      return generateStandardGroupPanel(el, true, false);
   }

   public XMLPanel getPanel(Association el) {
      return generateStandardGroupPanel(el, true, false);
   }

   public XMLPanel getPanel(Artifact el) {
      return generateStandardGroupPanel(el, true, false);
   }

   public XMLPanel getPanel(BasicType el) {
      return this.getPanel((XMLAttribute) el.get("Type"));
      // return generateStandardPanel(el);
   }

   public XMLPanel getPanel(ConformanceClass el) {
      return generateStandardPanel(el.getGraphConformanceAttribute());
   }

   public XMLPanel getPanel(DataField el) {
      return getDataFieldOrFormalParameterPanel(el);
   }

   public XMLPanel getPanel(DataObject el) {
      return generateStandardGroupPanel(el, true, false);
   }

   public XMLPanel getPanel(DataType el) {
      return this.getPanel(el.getDataTypes());
   }

   public XMLPanel getPanel(DataTypes el) {
      return new XMLDataTypesPanel(getPanelContainer(),
                                   el,
                                   null,
                                   JaWEManager.getInstance()
                                      .getLabelGenerator()
                                      .getLabel(el),
                                   JaWEManager.getInstance()
                                      .getJaWEController()
                                      .canModifyElement(el),
                                   null);
   }

   public XMLPanel getPanel(Deadlines el) {
      return generateStandardListPanel(el, true, false);
   }

   public XMLPanel getPanel(DeclaredType el) {
      SequencedHashMap chMap = XMLUtil.getPossibleTypeDeclarations(XMLUtil.getPackage(el),
                                                                   JaWEManager.getInstance()
                                                                      .getXPDLHandler());
      Object choosen = chMap.get(el.getId());
      if (choosen == null) {
         if (!el.toValue().equals("")) {
            choosen = el.toValue();
         }
      }
      List choices = new ArrayList(chMap.values());
      // if (choosen==null) {
      // if (choices.size()>0) {
      // choosen=(XMLElement)choices.get(0);
      // getPanelContainer().setModified(true);
      // }
      // }
      // if we are editing existing TypeDeclaration, remove it from the choices
      if (el.getParent().getParent() instanceof TypeDeclaration) {
         choices.remove(el.getParent().getParent());
      }
      SpecialChoiceElement cc = new SpecialChoiceElement(el,
                                                         "Id",
                                                         choices,
                                                         choosen,
                                                         false,
                                                         "Id",
                                                         "SubType",
                                                         el.isRequired());
      cc.setReadOnly(el.isReadOnly());

      return new XMLComboPanelWithReferenceLink(getPanelContainer(),
                                                cc,
                                                null,
                                                true,
                                                false,
                                                false,
                                                JaWEManager.getInstance()
                                                   .getJaWEController()
                                                   .canModifyElement(el),
                                                null);

   }

   public XMLPanel getPanel(Description el) {
      return generateStandardMultiLineTextPanel(el,
                                                true,
                                                XMLMultiLineTextPanelWithOptionalChoiceButtons.SIZE_SMALL,
                                                true);
   }

   public XMLPanel getPanel(Documentation el) {
      return new XMLLocationPanel(getPanelContainer(), el, JaWEManager.getInstance()
         .getJaWEController()
         .canModifyElement(el));
   }

   public XMLPanel getPanel(EnumerationType el) {
      return generateStandardListPanel(el, true, false);
   }

   public XMLPanel getPanel(ExceptionName el) {
      Activity act = XMLUtil.getActivity(el);
      Set ets = XMLUtil.getExceptionalOutgoingTransitions(act);
      List choices = new ArrayList();
      Iterator it = ets.iterator();
      while (it.hasNext()) {
         Transition t = (Transition) it.next();
         String ctype = t.getCondition().getType();
         if (ctype.equals(XPDLConstants.CONDITION_TYPE_EXCEPTION)) {
            if (!t.getCondition().toValue().equals("")) {
               choices.add(t.getCondition());
            }
         }
      }
      List<List> mc = new ArrayList<List>();
      mc.add(choices);
      return new XMLMultiLineTextPanelWithOptionalChoiceButtons(getPanelContainer(),
                                                                el,
                                                                el.toName(),
                                                                false,
                                                                true,
                                                                XMLMultiLineTextPanelWithOptionalChoiceButtons.SIZE_SMALL,
                                                                false,
                                                                mc,
                                                                null,
                                                                JaWEManager.getInstance()
                                                                   .getJaWEController()
                                                                   .canModifyElement(el),
                                                                null);
   }

   public XMLPanel getPanel(ExtendedAttribute el) {
      return generateStandardGroupPanel(el, true, false);
   }

   public XMLPanel getPanel(ExternalPackage el) {
      return generateStandardGroupPanel(el, true, false);
   }

   public XMLPanel getPanel(ExternalPackages el) {
      return generateStandardListPanel(el, true, false);
   }

   public XMLPanel getPanel(ExternalReference el) {
      return generateStandardGroupPanel(el, true, false);
   }

   public XMLPanel getPanel(FormalParameter el) {
      return getDataFieldOrFormalParameterPanel(el);
   }

   public XMLPanel getPanel(Icon el) {
      List choices = Utils.getActivityIconNamesList();
      String choosen = el.toValue();
      if (choices.size() == 0) {
         return new XMLLocationPanel(getPanelContainer(), el, JaWEManager.getInstance()
            .getJaWEController()
            .canModifyElement(el));
      }
      if (!choices.contains(choosen)) {
         choices.add(choosen);
      }
      XMLComboPanel p = new XMLComboPanel(getPanelContainer(),
                                          el,
                                          JaWEManager.getInstance()
                                             .getLabelGenerator()
                                             .getLabel(el),
                                          choices,
                                          false,
                                          true,
                                          false,
                                          true,
                                          JaWEManager.getInstance()
                                             .getJaWEController()
                                             .canModifyElement(el),
                                          false,
                                          false,
                                          null);

      p.getComboBox().setRenderer(cbr);
      return p;
   }

   public XMLPanel getPanel(Join el) {
      return generateStandardGroupPanel(el, true, false);
   }

   public XMLPanel getPanel(Package el) {
      List panels = new ArrayList();
      Set hidden = PanelUtilities.getHiddenElements(getPanelContainer(),
                                                    "XMLGroupPanel",
                                                    el);
      for (int i = 1;; i++) {
         try {
            XMLPanel p = getPanel(el, i, hidden);
            if (p != null) {
               panels.add(p);
            }
         } catch (Exception ex) {
            break;
         }
      }

      if (panels.size() > 1) {
         return new XMLTabbedPanel(getPanelContainer(),
                                   el,
                                   panels,
                                   JaWEManager.getInstance()
                                      .getLabelGenerator()
                                      .getLabel(el),
                                   false,
                                   null);
      } else if (panels.size() == 1) {
         return (XMLPanel) panels.get(0);
      } else {
         return new XMLBasicPanel();
      }

   }

   public XMLPanel getBasicPanel(Package el) {
      List panels = new ArrayList();
      Set hidden = PanelUtilities.getHiddenElements(getPanelContainer(),
                                                    "XMLGroupPanel",
                                                    el);
      for (int i = 1; i <= 3; i++) {
         try {
            XMLPanel p = getPanel(el, i, hidden);
            if (p != null) {
               panels.add(p);
            }
         } catch (Exception ex) {
            break;
         }
      }

      if (panels.size() > 1) {
         return new XMLTabbedPanel(getPanelContainer(),
                                   el,
                                   panels,
                                   JaWEManager.getInstance()
                                      .getLabelGenerator()
                                      .getLabel(el),
                                   false,
                                   null);
      } else if (panels.size() == 1) {
         return (XMLPanel) panels.get(0);
      } else {
         return new XMLBasicPanel();
      }

   }

   protected XMLPanel getPanel(Package el, int no, Set hidden) {
      XMLPanel p = null;
      switch (no) {

         case 1:
            List panelElements = new ArrayList();
            if (!hidden.contains(el.get("Id"))) {
               panelElements.add(el.get("Id"));
            }
            if (!hidden.contains(el.get("Name"))) {
               panelElements.add(el.get("Name"));
            }
            if (!hidden.contains(el.getConformanceClass())) {
               panelElements.add(el.getConformanceClass());
            }
            if (!hidden.contains(el.getScript())) {
               panelElements.add(getPanel(el.getScript(), true));
            }
            if (!hidden.contains(el.getExtendedAttributes())) {
               panelElements.add(el.getExtendedAttributes());
            }
            if (panelElements.size() > 0) {
               p = new XMLGroupPanel(getPanelContainer(),
                                     el,
                                     panelElements,
                                     getPanelContainer().getLanguageDependentString("GeneralKey"),
                                     true,
                                     false,
                                     true,
                                     null);
            }
            break;

         case 2:
            if (!hidden.contains(el.getPackageHeader())) {
               p = this.getPanel(el.getPackageHeader());
            }
            break;

         case 3:
            if (!hidden.contains(el.getRedefinableHeader())) {
               p = this.getPanel(el.getRedefinableHeader());
            }
            break;

         case 4:
            if (!hidden.contains(el.getExternalPackages())) {
               p = this.getPanel(el.getExternalPackages());
            }
            break;

         case 5:
            if (!hidden.contains(el.getTypeDeclarations())) {
               p = this.getPanel(el.getTypeDeclarations());
            }
            break;

         case 6:
            if (!hidden.contains(el.getParticipants())) {
               p = this.getPanel(el.getParticipants());
            }
            break;

         case 7:
            if (!hidden.contains(el.getApplications())) {
               p = this.getPanel(el.getApplications());
            }
            break;

         case 8:
            if (!hidden.contains(el.getDataFields())) {
               p = this.getPanel(el.getDataFields());
            }
            break;

         case 9:
            if (!hidden.contains(el.getAssociations())) {
               p = this.getPanel(el.getAssociations());
            }
            break;

         case 10:
            if (!hidden.contains(el.getArtifacts())) {
               p = this.getPanel(el.getArtifacts());
            }
            break;

         case 11:
            if (!hidden.contains(el.getPools())) {
               p = this.getPanel(el.getPools());
            }
            break;

         case 12:
            if (!hidden.contains(el.getWorkflowProcesses())) {
               p = this.getPanel(el.getWorkflowProcesses());
            }
            break;

         case 13:
            if (!hidden.contains(el.getNamespaces())) {
               p = this.getPanel(el.getNamespaces());
            }
            break;

         default:
            throw new RuntimeException();

      }
      return p;
   }

   public XMLPanel getPanel(Participant el) {
      return generateStandardGroupPanel(el, true, false);
   }

   public XMLPanel getPanel(ParticipantType el) {
      return new XMLRadioPanel(getPanelContainer(),
                               el.getTypeAttribute(),
                               getPanelContainer().getLanguageDependentString(el.getTypeAttribute()
                                  .toName()
                                                                              + "Key"),
                               true,
                               true,
                               false,
                               JaWEManager.getInstance()
                                  .getJaWEController()
                                  .canModifyElement(el),
                               null);
   }

   public XMLPanel getPanel(Performer el) {
      Activity act = XMLUtil.getActivity(el);
      if (act == null
          || (act != null && (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_NO || act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_TASK_APPLICATION))) {
         return new XMLHighlightPanelWithReferenceLink(getPanelContainer(),
                                                       el,
                                                       prepareExpressionChoices(el),
                                                       prepareExpressionChoicesTooltips(el),
                                                       true,
                                                       false,
                                                       true,
                                                       JaWEManager.getInstance()
                                                          .getJaWEController()
                                                          .canModifyElement(el),
                                                       null);

      }
      return new XMLTextPanel(getPanelContainer(),
                              el,
                              false,
                              false,
                              JaWEManager.getInstance()
                                 .getJaWEController()
                                 .canModifyElement(el));
   }

   public XMLPanel getPanel(Pool el) {
      return generateStandardGroupPanel(el, true, false);
   }

   public XMLPanel getPanel(RecordType el) {
      return generateStandardListPanel(el, true, false);
      /*
       * XMLListPanel controlledPanel=new XMLListPanel(el,"",false,true,false) { public
       * boolean checkRequired () { if (el.isReadOnly() || (el.size()>0)) { return true; }
       * else {
       * XMLPanel.errorMessage(this.getDialog(),JaWEManager.getInstance().getLabelGenerator
       * ().getLabel(el),"",
       * getLanguageDependentString("ErrorTheListMustContainAtLeastOneElement"));
       * controlPanel.getComponent(1).requestFocus(); return false; } } };
       * controlPanel=new XMLListControlPanel(el,"",true,false,true); return new
       * XMLGroupPanel(el,new XMLPanel[]{
       * controlledPanel,controlPanel},JaWEManager.getInstance
       * ().getLabelGenerator().getLabel(el),XMLPanel.BOX_LAYOUT, false,true);
       */
   }

   public XMLPanel getPanel(Responsible el) {
      SequencedHashMap choices = JaWEManager.getInstance()
         .getXPDLUtils()
         .getPossibleResponsibles((Responsibles) el.getParent(), el);
      Participant choosen = null;
      String pId = el.toValue();
      if (!pId.equals("")) {
         Iterator it = choices.values().iterator();
         while (it.hasNext()) {
            Participant p = (Participant) it.next();
            if (pId.equals(p.getId())) {
               choosen = p;
               break;
            }
         }
      }
      if (choosen != null) {
         choices.put(choosen.getId(), choosen);
      }
      // else if (choices.size()>0) {
      // choosen=(Participant)choices.getValue(0);
      // getPanelContainer().setModified(true);
      // } else {
      // getPanelContainer().setModified(true);
      // }

      SpecialChoiceElement cc = new SpecialChoiceElement(el,
                                                         "",
                                                         new ArrayList(choices.values()),
                                                         choosen,
                                                         false,
                                                         "Id",
                                                         el.toName(),
                                                         el.isRequired());
      cc.setReadOnly(el.isReadOnly());

      return new XMLComboPanelWithReferenceLink(getPanelContainer(),
                                                cc,
                                                null,
                                                true,
                                                false,
                                                false,
                                                JaWEManager.getInstance()
                                                   .getJaWEController()
                                                   .canModifyElement(el),
                                                null);
   }

   public XMLPanel getPanel(Responsibles el) {
      return generateStandardListPanel(el, true, false);
   }

   public XMLPanel getPanel(Script el) {
      return generateStandardGroupPanel(el, false, false);
   }

   protected XMLPanel getPanel(Script el, boolean hasTitle) {
      return generateStandardGroupPanel(el, hasTitle, false);
   }

   public XMLPanel getPanel(Split el) {
      return generateStandardGroupPanel(el, true, false);
   }

   public XMLPanel getPanel(SubFlow el) {
      Set hidden = PanelUtilities.getHiddenElements(getPanelContainer(),
                                                    "XMLGroupPanel",
                                                    el);
      List panelElements = new ArrayList();
      SequencedHashMap choices = XMLUtil.getPossibleSubflowProcesses(el,
                                                                     JaWEManager.getInstance()
                                                                        .getXPDLHandler());
      Object choosen = choices.get(el.getId());
      if (choosen == null) {
         if (!el.getId().equals("")) {
            choosen = el.getId();
         }
         // else {
         // if (choices.size()>0) {
         // choosen=choices.getValue(0);
         // getPanelContainer().setModified(true);
         // }
         // }
      }
      SpecialChoiceElement cc = new SpecialChoiceElement(el.get("Id"),
                                                         "",
                                                         new ArrayList(choices.values()),
                                                         choosen,
                                                         true,
                                                         "Id",
                                                         "WorkflowProcess",
                                                         true);
      cc.setReadOnly(el.get("Id").isReadOnly());

      final XMLComboPanelWithReferenceLink cp = new XMLComboPanelWithReferenceLink(getPanelContainer(),
                                                                                   cc,
                                                                                   null,
                                                                                   true,
                                                                                   false,
                                                                                   true,
                                                                                   JaWEManager.getInstance()
                                                                                      .getJaWEController()
                                                                                      .canModifyElement(el.get("Id")),
                                                                                   null);

      if (!hidden.contains(el.get("Id"))) {
         panelElements.add(cp);
      }
      if (!hidden.contains(el.getExecutionAttribute())) {
         panelElements.add(el.getExecutionAttribute());
      }

      if (!hidden.contains(el.getActualParameters())) {
         FormalParameters fps = null;
         if (choosen instanceof WorkflowProcess) {
            fps = ((WorkflowProcess) choosen).getFormalParameters();
         }
         final XMLActualParametersPanel app = generateActualParametersPanel(el, fps);
         // new XMLActualParametersPanel(getPanelContainer(),
         // el.getActualParameters(),
         // fps,
         // null);
         panelElements.add(app);
         ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               Object sel = cp.getSelectedItem();
               FormalParameters _fps = null;
               if (sel instanceof WorkflowProcess) {
                  _fps = ((WorkflowProcess) sel).getFormalParameters();
               }
               app.setFormalParameters(_fps);
            }
         };
         cp.getComboBox().addActionListener(al);
         cp.getComboBox()
            .getEditor()
            .getEditorComponent()
            .addKeyListener(new KeyAdapter() {
               public void keyPressed(KeyEvent e) {
                  if (PanelUtilities.isModifyingEvent(e)) {
                     Object sel = cp.getSelectedItem();
                     FormalParameters _fps = null;
                     if (sel instanceof WorkflowProcess) {
                        _fps = ((WorkflowProcess) sel).getFormalParameters();
                     }
                     app.setFormalParameters(_fps);
                  }
               }
            });

      }
      if (panelElements.size() > 0) {
         return new XMLGroupPanel(getPanelContainer(),
                                  el,
                                  panelElements,
                                  getPanelContainer().getLanguageDependentString(el.toName()
                                                                                 + "Key"),
                                  true,
                                  false,
                                  true,
                                  null);
      }
      return null;
   }

   public XMLPanel getPanel(TimeEstimation el) {
      return generateStandardGroupPanel(el, false, false);
   }

   public XMLPanel getPanel(TaskApplication el) {
      Set hidden = PanelUtilities.getHiddenElements(getPanelContainer(),
                                                    "XMLGroupPanel",
                                                    el);
      List panelElements = new ArrayList();
      SequencedHashMap choices = XMLUtil.getPossibleApplications(XMLUtil.getWorkflowProcess(el),
                                                                 JaWEManager.getInstance()
                                                                    .getXPDLHandler());
      Object choosen = choices.get(el.getId());
      if (choosen == null) {
         if (!el.getId().equals("")) {
            choosen = el.getId();
         }
         // else {
         // if (choices.size()>0) {
         // choosen=choices.getValue(0);
         // getPanelContainer().setModified(true);
         // }
         // }
      }
      SpecialChoiceElement cc = new SpecialChoiceElement(el.get("Id"),
                                                         "",
                                                         new ArrayList(choices.values()),
                                                         choosen,
                                                         true,
                                                         "Id",
                                                         "Application",
                                                         el.isRequired());
      cc.setReadOnly(el.get("Id").isReadOnly());

      final XMLComboPanelWithReferenceLink cp = new XMLComboPanelWithReferenceLink(getPanelContainer(),
                                                                                   cc,
                                                                                   null,
                                                                                   true,
                                                                                   false,
                                                                                   false,
                                                                                   JaWEManager.getInstance()
                                                                                      .getJaWEController()
                                                                                      .canModifyElement(el.get("Id")),
                                                                                   null);

      if (!hidden.contains(el.get("Id"))) {
         panelElements.add(cp);
      }
      if (!hidden.contains(el.get("Id"))) {
         panelElements.add(cp);
      }
      if (!hidden.contains(el.get("Name"))) {
         panelElements.add(el.get("Name"));
      }
      // if (!hidden.contains(el.get("PackageRef"))) {
      // panelElements.add(el.get("PackageRef"));
      // }
      if (!hidden.contains(el.getActualParameters())) {
         FormalParameters fps = null;
         if (choosen instanceof Application) {
            fps = ((Application) choosen).getApplicationTypes().getFormalParameters();
         }
         final XMLActualParametersPanel app = generateActualParametersPanel(el, fps);
         panelElements.add(app);
         ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               Object sel = cp.getSelectedItem();
               FormalParameters _fps = null;
               if (sel instanceof Application) {
                  _fps = ((Application) sel).getApplicationTypes().getFormalParameters();
               }
               app.setFormalParameters(_fps);
            }
         };
         cp.getComboBox().addActionListener(al);
      }
      if (!hidden.contains(el.get("Description"))) {
         panelElements.add(el.get("Description"));
      }
      if (panelElements.size() > 0) {
         return new XMLGroupPanel(getPanelContainer(),
                                  el,
                                  panelElements,
                                  getPanelContainer().getLanguageDependentString(el.toName()
                                                                                 + "Key"),
                                  true,
                                  false,
                                  true,
                                  null);
      }
      return null;
   }

   protected XMLActualParametersPanel generateActualParametersPanel(TaskApplication el,
                                                                    FormalParameters fps) {
      if (!getPanelContainer().getSettings()
         .getSettingBoolean("XMLActualParametersPanel.useAdvanced")) {
         return new XMLActualParametersPanel(getPanelContainer(),
                                             el.getActualParameters(),
                                             fps,
                                             null);
      } else {
         return new XMLAdvancedActualParametersPanel(getPanelContainer(),
                                                     el.getActualParameters(),
                                                     fps,
                                                     null);
      }
   }

   protected XMLActualParametersPanel generateActualParametersPanel(SubFlow el,
                                                                    FormalParameters fps) {
      if (!getPanelContainer().getSettings()
         .getSettingBoolean("XMLActualParametersPanel.useAdvanced")) {
         return new XMLActualParametersPanel(getPanelContainer(),
                                             el.getActualParameters(),
                                             fps,
                                             null);
      } else {
         return new XMLAdvancedActualParametersPanel(getPanelContainer(),
                                                     el.getActualParameters(),
                                                     fps,
                                                     null);
      }
   }

   public XMLPanel getPanel(Transition el) {
      Set hidden = PanelUtilities.getHiddenElements(getPanelContainer(),
                                                    "XMLGroupPanel",
                                                    el);
      List panelElements = new ArrayList();
      if (!hidden.contains(el.get("Id"))) {
         panelElements.add(el.get("Id"));
      }
      if (!hidden.contains(el.get("Name"))) {
         panelElements.add(el.get("Name"));
      }
      if (!hidden.contains(el.get("From"))) {
         panelElements.add(getPanelForFROMAttribute(el));
      }
      if (!hidden.contains(el.get("To"))) {
         panelElements.add(getPanelForTOAttribute(el));
      }
      if (!hidden.contains(el.getCondition())) {
         panelElements.add(el.getCondition());
      }
      if (!hidden.contains(el.get("Description"))) {
         panelElements.add(el.get("Description"));
      }
      if (!hidden.contains(el.getExtendedAttributes())) {
         panelElements.add(el.getExtendedAttributes());
      }

      if (panelElements.size() > 0) {
         return new XMLGroupPanel(getPanelContainer(),
                                  el,
                                  panelElements,
                                  JaWEManager.getInstance()
                                     .getLabelGenerator()
                                     .getLabel(el),
                                  true,
                                  false,
                                  true,
                                  null);
      }
      return new XMLBasicPanel();
   }

   public XMLPanel getPanel(TransitionRefs el) {
      return generateStandardListPanel(el, true, false);
   }

   public XMLPanel getPanel(UnionType el) {
      return generateStandardListPanel(el, true, false);
   }

   public XMLPanel getPanel(WorkflowProcess el) {
      List panels = new ArrayList();
      Set hidden = PanelUtilities.getHiddenElements(getPanelContainer(),
                                                    "XMLGroupPanel",
                                                    el);
      for (int i = 1;; i++) {
         try {
            XMLPanel p = getPanel(el, i, hidden);
            if (p != null) {
               panels.add(p);
            }
         } catch (Exception ex) {
            break;
         }
      }

      if (panels.size() > 1) {
         return new XMLTabbedPanel(getPanelContainer(),
                                   el,
                                   panels,
                                   JaWEManager.getInstance()
                                      .getLabelGenerator()
                                      .getLabel(el),
                                   false,
                                   null);
      } else if (panels.size() == 1) {
         return (XMLPanel) panels.get(0);
      } else {
         return new XMLBasicPanel();
      }

   }

   public XMLPanel getBasicPanel(WorkflowProcess el) {
      List panels = new ArrayList();
      Set hidden = PanelUtilities.getHiddenElements(getPanelContainer(),
                                                    "XMLGroupPanel",
                                                    el);
      for (int i = 1; i <= 3; i++) {
         try {
            XMLPanel p = getPanel(el, i, hidden);
            if (p != null) {
               panels.add(p);
            }
         } catch (Exception ex) {
            break;
         }
      }

      if (panels.size() > 1) {
         return new XMLTabbedPanel(getPanelContainer(),
                                   el,
                                   panels,
                                   JaWEManager.getInstance()
                                      .getLabelGenerator()
                                      .getLabel(el),
                                   false,
                                   null);
      } else if (panels.size() == 1) {
         return (XMLPanel) panels.get(0);
      } else {
         return new XMLBasicPanel();
      }

   }

   public XMLPanel getPanel(WorkflowProcess el, int no, Set hidden) {
      XMLPanel p = null;

      switch (no) {

         case 1:
            List panelElements = new ArrayList();
            if (!hidden.contains(el.get("Id"))) {
               panelElements.add(el.get("Id"));
            }
            if (!hidden.contains(el.get("Name"))) {
               panelElements.add(el.get("Name"));
            }
            if (!hidden.contains(el.getAccessLevelAttribute())) {
               panelElements.add(el.getAccessLevelAttribute());
            }
            if (!hidden.contains(el.getExtendedAttributes())) {
               panelElements.add(el.getExtendedAttributes());
            }
            if (panelElements.size() > 0) {
               p = new XMLGroupPanel(getPanelContainer(),
                                     el,
                                     panelElements,
                                     getPanelContainer().getLanguageDependentString("GeneralKey"),
                                     true,
                                     false,
                                     true,
                                     null);
            }
            break;

         case 2:
            if (!hidden.contains(el.getProcessHeader())) {
               p = this.getPanel(el.getProcessHeader());
            }
            break;

         case 3:
            if (!hidden.contains(el.getRedefinableHeader())) {
               p = this.getPanel(el.getRedefinableHeader());
            }
            break;

         case 4:
            if (!hidden.contains(el.getParticipants())) {
               p = this.getPanel(el.getParticipants());
            }
            break;

         case 5:
            if (!hidden.contains(el.getApplications())) {
               p = this.getPanel(el.getApplications());
            }
            break;

         case 6:
            if (!hidden.contains(el.getDataFields())) {
               p = this.getPanel(el.getDataFields());
            }
            break;

         case 7:
            if (!hidden.contains(el.getFormalParameters())) {
               p = this.getPanel(el.getFormalParameters());
            }
            break;

         case 8:
            if (!hidden.contains(el.getActivities())) {
               p = this.getPanel(el.getActivities());
            }
            break;

         case 9:
            if (!hidden.contains(el.getTransitions())) {
               p = this.getPanel(el.getTransitions());
            }
            break;

         case 10:
            if (!hidden.contains(el.getActivitySets())) {
               p = this.getPanel(el.getActivitySets());
            }
            break;

         default:
            throw new RuntimeException();

      }
      return p;
   }

   public XMLPanel getPanel(XMLAttribute el) {
      if (el.getParent() instanceof ExternalPackage && el.toName().equals("href")) {
         return new XMLLocationPanel(getPanelContainer(), el, JaWEManager.getInstance()
            .getJaWEController()
            .canModifyElement(el));
      }
      if (el.getParent() instanceof ExtendedAttribute) {
         if (el.toName().equals("Name")) {
            Set choices = JaWEManager.getInstance()
               .getXPDLUtils()
               .getAllExtendedAttributeNames((XMLComplexElement) el.getParent()
                                                .getParent()
                                                .getParent(),
                                             JaWEManager.getInstance().getXPDLHandler());
            String choosen = el.toValue();
            choices.add(choosen);
            return new XMLComboPanel(getPanelContainer(),
                                     el,
                                     new ArrayList(choices),
                                     false,
                                     true,
                                     false,
                                     true,
                                     JaWEManager.getInstance()
                                        .getJaWEController()
                                        .canModifyElement(el));
         }
         XMLElement holder = el.getParent().getParent().getParent();
         if ((holder instanceof Application && ((Application) holder).getApplicationTypes()
            .getChoosen() instanceof FormalParameters)
             || (XMLUtil.getWorkflowProcess(holder) != null && (holder instanceof Activity
                                                                || holder instanceof Transition || holder instanceof WorkflowProcess))) {
            List<List> mc = new ArrayList<List>();
            mc.add(getBasicExpressionChoices(el));
            return new XMLMultiLineTextPanelWithOptionalChoiceButtons(getPanelContainer(),
                                                                      el,
                                                                      "Value",
                                                                      false,
                                                                      true,
                                                                      XMLMultiLineTextPanelWithOptionalChoiceButtons.SIZE_LARGE,
                                                                      false,
                                                                      mc,
                                                                      getBasicExpressionChoicesTooltips(el),
                                                                      JaWEManager.getInstance()
                                                                         .getJaWEController()
                                                                         .canModifyElement(el),
                                                                      null);
         } else {
            return new XMLMultiLineTextPanelWithOptionalChoiceButtons(getPanelContainer(),
                                                                      el,
                                                                      "Value",
                                                                      false,
                                                                      true,
                                                                      XMLMultiLineTextPanelWithOptionalChoiceButtons.SIZE_LARGE,
                                                                      false,
                                                                      null,
                                                                      null,
                                                                      JaWEManager.getInstance()
                                                                         .getJaWEController()
                                                                         .canModifyElement(el),
                                                                      null);
         }
      }
      if (el.getParent() instanceof BlockActivity) {
         WorkflowProcess wp = XMLUtil.getWorkflowProcess(el);
         List choices = wp.getActivitySets().toElements();
         XMLElement choosen = wp.getActivitySet(el.toValue());
         // if (choosen==null && choices.size()>0) {
         // choosen=(XMLElement)choices.get(0);
         // getPanelContainer().setModified(true);
         // }
         SpecialChoiceElement cc = new SpecialChoiceElement(el,
                                                            "",
                                                            choices,
                                                            choosen,
                                                            true,
                                                            "Id",
                                                            "ActivitySet",
                                                            el.isRequired());
         cc.setReadOnly(el.isReadOnly());

         return new XMLComboPanelWithReferenceLink(getPanelContainer(),
                                                   cc,
                                                   null,
                                                   true,
                                                   false,
                                                   false,
                                                   JaWEManager.getInstance()
                                                      .getJaWEController()
                                                      .canModifyElement(el),
                                                   null);

      }
      if (el.toName().equalsIgnoreCase("From")) {
         return getPanelForFROMAttribute((Transition) el.getParent());
      }
      if (el.toName().equalsIgnoreCase("Source")) {
         return getPanelForFROMAttribute((Association) el.getParent());
      }
      if (el.toName().equalsIgnoreCase("To")) {
         return getPanelForTOAttribute((Transition) el.getParent());
      }
      if (el.toName().equalsIgnoreCase("Target")) {
         return getPanelForTOAttribute((Association) el.getParent());
      }
      if (el.toName().equalsIgnoreCase("Process")) {
         Package pkg = XMLUtil.getPackage(el);
         XMLCollectionElement wpOrAs = pkg.getWorkflowProcess(el.toValue());
         if (wpOrAs == null) {
            wpOrAs = pkg.getActivitySet(el.toValue());
         }
         return new XMLTextPanelWithReferenceLink(getPanelContainer(),
                                                  el,
                                                  wpOrAs,
                                                  null,
                                                  false,
                                                  JaWEManager.getInstance()
                                                     .getJaWEController()
                                                     .canModifyElement(el),
                                                  null);
      }
      if (el.toName().equalsIgnoreCase("FillColor")) {
         return new XMLColorPanel(pc, el, "FillColor", false, true, null);
      }
      if (el.toName().equalsIgnoreCase("ObjectClassFilter")) {
         return new XMLComboPanel(getPanelContainer(),
                                  el,
                                  null,
                                  true,
                                  true,
                                  false,
                                  true,
                                  true);
      }
      if (el.toName().equalsIgnoreCase("Password")) {
         return new XMLTextPanel(getPanelContainer(), el, false, true, true);
      }

      // search dialog
      if (el.toName().equalsIgnoreCase("SearchForElement")) {
         return new XMLComboPanel(getPanelContainer(),
                                  el,
                                  null,
                                  true,
                                  true,
                                  false,
                                  true,
                                  true);
      }
      if (el.toName().equalsIgnoreCase("SearchByProperty")) {
         return new XMLComboPanel(getPanelContainer(),
                                  el,
                                  null,
                                  true,
                                  true,
                                  false,
                                  true,
                                  true);
      }
      if (el.toName().equalsIgnoreCase("SearchByAttribute")) {
         return new XMLComboPanel(getPanelContainer(),
                                  el,
                                  null,
                                  true,
                                  true,
                                  false,
                                  true,
                                  true);
      }
      if (el.toName().equalsIgnoreCase("ArtifactType")) {
         XMLPanel panel = new XMLComboPanel(getPanelContainer(),
                                            el,
                                            null,
                                            false,
                                            true,
                                            false,
                                            false,
                                            false);
         return panel;
      }

      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(XMLEmptyChoiceElement el) {
      return new XMLBasicPanel();
   }

   public XMLPanel getPanel(XMLComplexChoice el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(XMLCollection el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(XMLComplexElement el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(XMLSimpleElement el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(XMLElement el) {

      try {
         Class cl = el.getClass();
         Method m = null;
         try {
            m = this.getClass().getMethod("getPanel", new Class[] {
               cl
            });
         } catch (Exception ex) {
            if (!(cl == XMLSimpleElement.class
                  || cl == XMLAttribute.class || cl == XMLComplexChoice.class
                  || cl == XMLComplexElement.class || cl == XMLCollectionElement.class || cl == XMLCollection.class)) {
               if (XMLComplexChoice.class.isAssignableFrom(cl)) {
                  cl = XMLComplexChoice.class;
               } else if (XMLAttribute.class.isAssignableFrom(cl)) {
                  cl = XMLAttribute.class;
               } else if (XMLSimpleElement.class.isAssignableFrom(cl)) {
                  cl = XMLSimpleElement.class;
               } else if (XMLComplexElement.class.isAssignableFrom(cl)) {
                  cl = XMLComplexElement.class;
               } else if (XMLCollection.class.isAssignableFrom(cl)) {
                  cl = XMLCollection.class;
               }
            }
         }

         m = this.getClass().getMethod("getPanel", new Class[] {
            cl
         });
         // System.err.println("calling "+m.toString());
         return (XMLPanel) m.invoke(this, new Object[] {
            el
         });
      } catch (Throwable e) {
         e.printStackTrace();
      }

      return generateStandardPanel(el);
   }

   protected XMLPanel getPanelForFROMAttribute(XMLCollectionElement tOrAss) {
      SequencedHashMap choices = (tOrAss instanceof Transition) ? JaWEManager.getInstance()
                                                                  .getTransitionHandler()
                                                                  .getPossibleSourceActivities((Transition) tOrAss)
                                                               : JaWEManager.getInstance()
                                                                  .getTransitionHandler()
                                                                  .getPossibleSourceOrTargetActivitiesOrArtifacts((Association) tOrAss);

      // System.out.println("Choices="+choices);
      String tFrom = (tOrAss instanceof Transition) ? ((Transition) tOrAss).getFrom()
                                                   : ((Association) tOrAss).getSource();

      XMLCollectionElement current = XMLUtil.getPackage(tOrAss).getActivity(tFrom);
      if (current == null)
         current = XMLUtil.getPackage(tOrAss).getArtifact(tFrom);
      if (current != null)
         choices.put(current.getId(), current);

      XMLCollectionElement choosen = null;
      if (!tFrom.equals("")) {
         choosen = (XMLCollectionElement) choices.get(tFrom);
      }
      // if (choosen==null && choices.size()>0) {
      // choosen=(Activity)choices.getValue(0);
      // getPanelContainer().setModified(true);
      // }

      XMLElement from = (tOrAss instanceof Transition) ? ((Transition) tOrAss).get("From")
                                                      : ((Association) tOrAss).get("Source");
      SpecialChoiceElement cc = new SpecialChoiceElement(from,
                                                         "",
                                                         new ArrayList(choices.values()),
                                                         choosen,
                                                         true,
                                                         "Id",
                                                         "From",
                                                         from.isRequired());
      cc.setReadOnly(from.isReadOnly());
      return new XMLComboPanelWithReferenceLink(getPanelContainer(),
                                                cc,
                                                null,
                                                true,
                                                false,
                                                false,
                                                JaWEManager.getInstance()
                                                   .getJaWEController()
                                                   .canModifyElement(from),
                                                null);
   }

   protected XMLPanel getPanelForTOAttribute(XMLCollectionElement tOrAss) {
      SequencedHashMap choices = (tOrAss instanceof Transition) ? JaWEManager.getInstance()
                                                                  .getTransitionHandler()
                                                                  .getPossibleTargetActivities((Transition) tOrAss)
                                                               : JaWEManager.getInstance()
                                                                  .getTransitionHandler()
                                                                  .getPossibleSourceOrTargetActivitiesOrArtifacts((Association) tOrAss);

      // System.out.println("Choices="+choices);
      String tTo = (tOrAss instanceof Transition) ? ((Transition) tOrAss).getTo()
                                                 : ((Association) tOrAss).getTarget();

      XMLCollectionElement current = XMLUtil.getPackage(tOrAss).getActivity(tTo);
      if (current == null)
         current = XMLUtil.getPackage(tOrAss).getArtifact(tTo);
      if (current != null)
         choices.put(current.getId(), current);

      XMLCollectionElement choosen = null;
      if (!tTo.equals("")) {
         choosen = (XMLCollectionElement) choices.get(tTo);
      }
      // if (choosen==null && choices.size()>0) {
      // choosen=(Activity)choices.getValue(0);
      // getPanelContainer().setModified(true);
      // }

      XMLElement to = (tOrAss instanceof Transition) ? ((Transition) tOrAss).get("To")
                                                    : ((Association) tOrAss).get("Target");
      SpecialChoiceElement cc = new SpecialChoiceElement(to,
                                                         "",
                                                         new ArrayList(choices.values()),
                                                         choosen,
                                                         true,
                                                         "Id",
                                                         "To",
                                                         to.isRequired());
      cc.setReadOnly(to.isReadOnly());
      return new XMLComboPanelWithReferenceLink(getPanelContainer(),
                                                cc,
                                                null,
                                                true,
                                                false,
                                                false,
                                                JaWEManager.getInstance()
                                                   .getJaWEController()
                                                   .canModifyElement(to),
                                                null);
   }

   public XMLPanel generateStandardPanel(XMLElement el) {
      // new Throwable().printStackTrace();
      // System.err.println("generateStandardPanel, el = "+el);
      XMLPanel panel = null;
      if (el instanceof XMLSimpleElement) {
         panel = generateStandardTextPanel(el, false);
      } else if (el instanceof XMLAttribute) {
         List choices = ((XMLAttribute) el).getChoices();
         if (choices == null) {
            panel = generateStandardTextPanel(el, false);
         } else {
            panel = new XMLComboPanel(getPanelContainer(),
                                      el,
                                      null,
                                      false,
                                      true,
                                      false,
                                      false,
                                      JaWEManager.getInstance()
                                         .getJaWEController()
                                         .canModifyElement(el));
         }

      } else if (el instanceof XMLComplexChoice) {
         panel = new XMLComboChoicePanel(getPanelContainer(),
                                         el,
                                         JaWEManager.getInstance()
                                            .getJaWEController()
                                            .canModifyElement(el));
      } else if (el instanceof XMLComplexElement) {
         panel = generateStandardGroupPanel((XMLComplexElement) el, false, true);
      } else if (el instanceof XMLCollection) {
         panel = generateStandardTablePanel((XMLCollection) el, true, false, false, false);
      } else {
         panel = new XMLBasicPanel();
      }
      return panel;
   }

   protected XMLMultiLineTextPanelWithOptionalChoiceButtons generateStandardMultiLineTextPanel(XMLElement el,
                                                                                               boolean isVertical,
                                                                                               int size,
                                                                                               boolean wrapLines) {
      return new XMLMultiLineTextPanelWithOptionalChoiceButtons(getPanelContainer(),
                                                                el,
                                                                isVertical,
                                                                size,
                                                                wrapLines,
                                                                JaWEManager.getInstance()
                                                                   .getJaWEController()
                                                                   .canModifyElement(el),
                                                                null,
                                                                null);
   }

   protected XMLTextPanel generateStandardTextPanel(XMLElement el, boolean isVertical) {
      return new XMLTextPanel(getPanelContainer(),
                              el,
                              isVertical,
                              false,
                              JaWEManager.getInstance()
                                 .getJaWEController()
                                 .canModifyElement(el));

   }

   protected XMLTablePanel generateStandardTablePanel(XMLCollection cl,
                                                      boolean hasTitle,
                                                      boolean hasEmptyBorder,
                                                      boolean miniDim,
                                                      boolean useBasicToolbar) {
      List elementsToShow = cl.toElements();
      Set hidden = PanelUtilities.getHiddenElements(getPanelContainer(),
                                                    "XMLTablePanel",
                                                    cl);
      elementsToShow.removeAll(hidden);
      List columnsToShow = PanelUtilities.getColumnsToShow(getPanelContainer(),
                                                           "XMLTablePanel",
                                                           cl);
      if (cl instanceof ExtendedAttributes) {
         miniDim = true;
      }
      if ((cl instanceof DataFields && cl.getParent() instanceof WorkflowProcess)
          || (cl instanceof FormalParameters)
          && cl.getParent() instanceof WorkflowProcess) {
         return new XMLTablePanelForVariables((InlinePanel) getPanelContainer(),
                                              cl,
                                              columnsToShow,
                                              elementsToShow,
                                              JaWEManager.getInstance()
                                                 .getLabelGenerator()
                                                 .getLabel(cl)
                                                    + ", "
                                                    + (cl.size() - hidden.size())
                                                    + " "
                                                    + getPanelContainer().getLanguageDependentString("ElementsKey"),
                                              hasTitle,
                                              hasEmptyBorder,
                                              false,
                                              miniDim,
                                              true,
                                              true);

      } else {
         return new XMLTablePanel((InlinePanel) getPanelContainer(),
                                  cl,
                                  columnsToShow,
                                  elementsToShow,
                                  JaWEManager.getInstance()
                                     .getLabelGenerator()
                                     .getLabel(cl)
                                        + ", "
                                        + (cl.size() - hidden.size())
                                        + " "
                                        + getPanelContainer().getLanguageDependentString("ElementsKey"),
                                  hasTitle,
                                  hasEmptyBorder,
                                  false,
                                  miniDim,
                                  null,
                                  true,
                                  true,
                                  useBasicToolbar,
                                  false,
                                  null);
      }
   }

   protected XMLListPanel generateStandardListPanel(XMLCollection cl,
                                                    boolean hasTitle,
                                                    boolean hasEmptyBorder) {
      List elementsToShow = cl.toElements();
      Set hidden = PanelUtilities.getHiddenElements(getPanelContainer(),
                                                    "XMLListPanel",
                                                    cl);
      elementsToShow.removeAll(hidden);
      return new XMLListPanel((InlinePanel) getPanelContainer(),
                              cl,
                              elementsToShow,
                              JaWEManager.getInstance().getLabelGenerator().getLabel(cl)
                                    + ", "
                                    + (cl.size() - hidden.size())
                                    + " "
                                    + getPanelContainer().getLanguageDependentString("ElementsKey"),
                              true,
                              false,
                              true,
                              false,
                              false,
                              false,
                              null);
   }

   protected XMLGroupPanel generateStandardGroupPanel(XMLComplexElement cel,
                                                      boolean hasTitle,
                                                      boolean hasEmptyBorder) {
      List toShow = getStandardGroupPanelComponents(cel);
      if ((cel instanceof ExpressionType && !(cel.getParent() instanceof Condition))
          || cel instanceof Condition) {
         hasTitle = true;
      }
      return new XMLGroupPanel(getPanelContainer(),
                               cel,
                               toShow,
                               JaWEManager.getInstance()
                                  .getLabelGenerator()
                                  .getLabel(cel),
                               true,
                               hasTitle,
                               hasEmptyBorder,
                               null);
   }

   protected List getStandardGroupPanelComponents(XMLComplexElement cel) {
      Set hidden = PanelUtilities.getHiddenElements(getPanelContainer(),
                                                    "XMLGroupPanel",
                                                    cel);
      List toShow = new ArrayList(cel.toElements());
      toShow.removeAll(hidden);
      if ((cel instanceof ExpressionType && !(cel.getParent() instanceof Condition))
          || cel instanceof Condition) {
         toShow.add(new XMLMultiLineHighlightPanelWithChoiceButton(getPanelContainer(),
                                                                   cel,
                                                                   "Expression",
                                                                   false,
                                                                   true,
                                                                   XMLMultiLineTextPanelWithOptionalChoiceButtons.SIZE_EXTRA_LARGE,
                                                                   false,
                                                                   prepareExpressionChoices(cel),
                                                                   prepareExpressionChoicesTooltips(cel),
                                                                   JaWEManager.getInstance()
                                                                      .getJaWEController()
                                                                      .canModifyElement(cel)));
      } else if (cel instanceof ExtendedAttribute) {
         XMLElement holder = cel.getParent().getParent();
         if ((holder instanceof Application && ((Application) holder).getApplicationTypes()
            .getChoosen() instanceof FormalParameters)
             || (XMLUtil.getWorkflowProcess(holder) != null && (holder instanceof Activity
                                                                || holder instanceof Transition || holder instanceof WorkflowProcess))) {
            List<List> mc = new ArrayList<List>();
            mc.add(getBasicExpressionChoices(holder));
            toShow.add(new XMLMultiLineTextPanelWithOptionalChoiceButtons(getPanelContainer(),
                                                                          cel,
                                                                          "ComplexContent",
                                                                          false,
                                                                          true,
                                                                          XMLMultiLineTextPanelWithOptionalChoiceButtons.SIZE_LARGE,
                                                                          false,
                                                                          mc,
                                                                          getBasicExpressionChoicesTooltips(holder),
                                                                          JaWEManager.getInstance()
                                                                             .getJaWEController()
                                                                             .canModifyElement(cel),
                                                                          null));

         } else {
            toShow.add(new XMLMultiLineTextPanelWithOptionalChoiceButtons(getPanelContainer(),
                                                                          cel,
                                                                          "ComplexContent",
                                                                          false,
                                                                          true,
                                                                          XMLMultiLineTextPanelWithOptionalChoiceButtons.SIZE_LARGE,
                                                                          false,
                                                                          null,
                                                                          null,
                                                                          JaWEManager.getInstance()
                                                                             .getJaWEController()
                                                                             .canModifyElement(cel),
                                                                          null));
         }
      } else if (cel instanceof SchemaType) {
         toShow.add(new XMLMultiLineTextPanelWithOptionalChoiceButtons(getPanelContainer(),
                                                                       cel,
                                                                       "ComplexContent",
                                                                       false,
                                                                       true,
                                                                       XMLMultiLineTextPanelWithOptionalChoiceButtons.SIZE_LARGE,
                                                                       false,
                                                                       null,
                                                                       null,
                                                                       JaWEManager.getInstance()
                                                                          .getJaWEController()
                                                                          .canModifyElement(cel),
                                                                       null));
      } else if (cel instanceof Artifact) {
         Artifact art = (Artifact) cel;
         XMLAttribute artType = art.getArtifactTypeAttribute();
         if (artType.toValue().equals(XPDLConstants.ARTIFACT_TYPE_ANNOTATION)) {
            toShow.remove(art.getDataObject());
         } else if (artType.toValue().equals(XPDLConstants.ARTIFACT_TYPE_DATAOBJECT)) {
            toShow.remove(art.getTextAnnotationAttribute());
         }
      }
      return toShow;
   }

   protected XMLPanel getDataFieldOrFormalParameterPanel(XMLCollectionElement el) {
      Set hidden = PanelUtilities.getHiddenElements(getPanelContainer(),
                                                    "XMLGroupPanel",
                                                    el);
      List subpanels = new ArrayList();
      List groupsToShow = new ArrayList();
      XMLGroupPanel g1 = null;
      if (!hidden.contains(el.get("Id"))) {
         subpanels.add(generateStandardTextPanel(el.get("Id"), true));
      }
      if (!hidden.contains(el.get("Name"))) {
         subpanels.add(generateStandardTextPanel(el.get("Name"), true));
      }
      if (el instanceof FormalParameter && !hidden.contains(el.get("Mode"))) {
         subpanels.add(new XMLComboPanel(getPanelContainer(),
                                         el.get("Mode"),
                                         null,
                                         false,
                                         true,
                                         true,
                                         false,
                                         JaWEManager.getInstance()
                                            .getJaWEController()
                                            .canModifyElement(el)));
      }
      if (subpanels.size() > 0) {
         g1 = new XMLGroupPanel(getPanelContainer(),
                                el,
                                subpanels,
                                "",
                                true,
                                false,
                                true,
                                null);
         groupsToShow.add(g1);
      }
      XMLGroupPanel g2 = null;
      subpanels.clear();
      if (!hidden.contains(el.get("Length"))) {
         subpanels.add(generateStandardTextPanel(el.get("Length"), true));
      }
      if (!hidden.contains(el.get("IsArray"))) {
         XMLAttribute isa = (XMLAttribute) el.get("IsArray");
         subpanels.add(new XMLCheckboxPanel(getPanelContainer(),
                                            isa,
                                            "",
                                            true,
                                            JaWEManager.getInstance()
                                               .getJaWEController()
                                               .canModifyElement(isa),
                                            false,
                                            null));
      }

      if (subpanels.size() > 0) {
         g2 = new XMLGroupPanel(getPanelContainer(),
                                el,
                                subpanels,
                                "",
                                true,
                                false,
                                true,
                                null);
         groupsToShow.add(g2);
      }
      if (g1 != null && g2 != null) {
         XMLGroupPanel gp = new XMLGroupPanel(getPanelContainer(),
                                              el,
                                              groupsToShow,
                                              "",
                                              false,
                                              false,
                                              true,
                                              true,
                                              null);
         groupsToShow.clear();
         groupsToShow.add(gp);
      }
      if (!hidden.contains(el.get("DataType"))) {
         groupsToShow.add(generateStandardGroupPanel((XMLComplexElement) el.get("DataType"),
                                    true,
                                    false));
      }
      if (!hidden.contains(el.get("InitialValue"))) {
         groupsToShow.add(getPanel(el.get("InitialValue")));
      }

      if (!hidden.contains(el.get("Description"))) {
         groupsToShow.add(getPanel(el.get("Description")));
      }
      if (el instanceof DataField && !hidden.contains(el.get("ExtendedAttributes"))) {
         groupsToShow.add(getPanel(el.get("ExtendedAttributes")));
      }
      return new XMLGroupPanel(getPanelContainer(),
                               el,
                               groupsToShow,
                               "",
                               true,
                               false,
                               true,
                               null);
   }

   public List getBasicExpressionChoices(XMLElement el) {
      if (el instanceof Application) {
         FormalParameters fps = ((Application) el).getApplicationTypes()
            .getFormalParameters();
         return new ArrayList(fps.toElements());
      }
      return new ArrayList(XMLUtil.getPossibleVariables(el).values());
   }

   public List<String> getBasicExpressionChoicesTooltips(XMLElement el) {
      List<String> tc = new ArrayList<String>();
      tc.add(getSettings().getLanguageDependentString("InsertVariableKey"));
      return tc;
   }

   public List getExpressionChoices(XMLElement el) {
      return getBasicExpressionChoices(el);
   }

   public List<List> prepareExpressionChoices(XMLElement el) {
      if (el instanceof InitialValue) {
         return null;
      }
      List<List> mc = new ArrayList<List>();
      mc.add(getExpressionChoices(el));
      return mc;
   }

   public List<String> prepareExpressionChoicesTooltips(XMLElement el) {
      if (el instanceof InitialValue) {
         return null;
      }
      List<String> tc = new ArrayList<String>();
      tc.add(getSettings().getLanguageDependentString("InsertVariableKey"));
      return tc;
   }

   public Settings getSettings() {
      return getPanelContainer().getSettings();
   }

   protected IconCBoxRenderer cbr = new IconCBoxRenderer();

   class IconCBoxRenderer extends JLabel implements ListCellRenderer {

      public IconCBoxRenderer() {
         setOpaque(true);
      }

      /*
       * This method finds the image and text corresponding to the selected value and
       * returns the label, set up to display the text and image.
       */
      public Component getListCellRendererComponent(JList list,
                                                    Object value,
                                                    int index,
                                                    boolean isSelected,
                                                    boolean cellHasFocus) {
         // Get the selected index. (The index param isn't
         // always valid, so just use the value.)
         String iconLoc = value.toString();

         if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
         } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
         }

         // Set the icon and text. If icon was null, say so.
         ImageIcon icon = (ImageIcon) Utils.getOriginalActivityIconsMap().get(iconLoc);
         setIcon(icon);
         setText(iconLoc);

         return this;
      }

   }

}
