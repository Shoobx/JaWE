package org.enhydra.jawe.base.panel;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
import org.enhydra.jawe.base.panel.panels.XMLActualParametersPanel;
import org.enhydra.jawe.base.panel.panels.XMLBasicListPanel;
import org.enhydra.jawe.base.panel.panels.XMLBasicPanel;
import org.enhydra.jawe.base.panel.panels.XMLBasicTablePanel;
import org.enhydra.jawe.base.panel.panels.XMLCheckboxPanel;
import org.enhydra.jawe.base.panel.panels.XMLComboChoicePanel;
import org.enhydra.jawe.base.panel.panels.XMLComboPanel;
import org.enhydra.jawe.base.panel.panels.XMLDataTypesPanel;
import org.enhydra.jawe.base.panel.panels.XMLGroupPanel;
import org.enhydra.jawe.base.panel.panels.XMLLocationPanel;
import org.enhydra.jawe.base.panel.panels.XMLMultiLineTextPanel;
import org.enhydra.jawe.base.panel.panels.XMLPanel;
import org.enhydra.jawe.base.panel.panels.XMLRadioPanel;
import org.enhydra.jawe.base.panel.panels.XMLTabbedPanel;
import org.enhydra.jawe.base.panel.panels.XMLTextPanel;
import org.enhydra.shark.utilities.SequencedHashMap;
import org.enhydra.shark.xpdl.XMLAttribute;
import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLCollectionElement;
import org.enhydra.shark.xpdl.XMLComplexChoice;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLEmptyChoiceElement;
import org.enhydra.shark.xpdl.XMLSimpleElement;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.XPDLConstants;
import org.enhydra.shark.xpdl.elements.*;
import org.enhydra.shark.xpdl.elements.Package;

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

   public XMLPanel getPanel(Activities el) {
      return generateStandardTablePanel(el, true, false);
   }

   public XMLPanel getPanel(Activity el) {

      List panels = new ArrayList();
      Set hidden = getHiddenElements("XMLGroupPanel", el);
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
                                   false);
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
            if (!hidden.contains(el.get("Performer"))) {
               int type = el.getActivityType();
               if (type == XPDLConstants.ACTIVITY_TYPE_NO
                   || type == XPDLConstants.ACTIVITY_TYPE_TOOL) {
                  panelElements.add(el.get("Performer"));
               }
            }
            if (!hidden.contains(el.getStartMode())) {
               panelElements.add(el.getStartMode());
            }
            if (!hidden.contains(el.getFinishMode())) {
               panelElements.add(el.getFinishMode());
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
                                     true);
            }
            break;

         case 2:
            if (!hidden.contains(el.getActivityTypes())) {
               int type = el.getActivityType();
               if (type == XPDLConstants.ACTIVITY_TYPE_TOOL) {
                  Tools tools = el.getActivityTypes()
                     .getImplementation()
                     .getImplementationTypes()
                     .getTools();
                  p = this.getPanel(tools);
               } else if (type == XPDLConstants.ACTIVITY_TYPE_SUBFLOW) {
                  SubFlow sbflw = el.getActivityTypes()
                     .getImplementation()
                     .getImplementationTypes()
                     .getSubFlow();
                  p = this.getPanel(sbflw);
               } else if (type == XPDLConstants.ACTIVITY_TYPE_BLOCK) {
                  BlockActivity ba = el.getActivityTypes().getBlockActivity();
                  p = this.getPanel(ba);
               }
            }
            break;

         case 3:
            TransitionRestrictions trs = el.getTransitionRestrictions();
            if (!hidden.contains(trs) && trs.size() > 0) {
               TransitionRestriction tr = (TransitionRestriction) trs.get(0);
               p = getPanel(tr);
            }
            break;

         case 4:
            if (!hidden.contains(el.getSimulationInformation())) {
               p = this.getPanel(el.getSimulationInformation());
            }
            break;

         case 5:
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

   public XMLPanel getPanel(ActivitySets el) {
      return generateStandardTablePanel(el, true, false);
   }

   public XMLPanel getPanel(ActivityTypes el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(ActualParameter el) {
      SequencedHashMap choices = XMLUtil.getPossibleVariables(XMLUtil.getWorkflowProcess(el));
      Object choosen = choices.get(el.toValue());
      if (choosen == null) {
         if (!el.toValue().equals("")) {
            choosen = el.toValue();
         }
      }
      SpecialChoiceElement cc = new SpecialChoiceElement(el,
                                                         "",
                                                         new ArrayList(choices.values()),
                                                         choosen,
                                                         true,
                                                         "Id",
                                                         el.toName(),
                                                         el.isRequired());
      cc.setReadOnly(el.isReadOnly());

      return new XMLComboPanel(getPanelContainer(),
                               cc,
                               null,
                               false,
                               true,
                               false,
                               true,
                               JaWEManager.getInstance()
                                  .getJaWEController()
                                  .canModifyElement(el));
   }

   public XMLPanel getPanel(ActualParameters el) {
      return generateStandardTablePanel(el, true, false);
   }

   public XMLPanel getPanel(Application el) {
      return generateStandardGroupPanel(el, true, false);
   }

   public XMLPanel getPanel(Applications el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(ApplicationTypes el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(ArrayType el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(Author el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(Automatic el) {
      return new XMLBasicPanel();
   }

   public XMLPanel getPanel(BasicType el) {
      return this.getPanel((XMLAttribute) el.get("Type"));
      // return generateStandardPanel(el);
   }

   public XMLPanel getPanel(BlockActivity el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(Codepage el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(Condition el) {
      return generateStandardGroupPanel(el, true, false);
   }

   protected XMLPanel getPanel(Condition el, boolean hasTitle) {
      return generateStandardGroupPanel(el, hasTitle, false);
   }

   public XMLPanel getPanel(ConformanceClass el) {
      return generateStandardPanel(el.getGraphConformanceAttribute());
   }

   public XMLPanel getPanel(Cost el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(CostUnit el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(Countrykey el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(Created el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(DataField el) {
      Set hidden = getHiddenElements("XMLGroupPanel", el);
      List subpanels = new ArrayList();
      List groupsToShow = new ArrayList();
      if (!hidden.contains(el.get("Id"))) {
         subpanels.add(generateStandardTextPanel(el.get("Id"), true));
      }
      if (!hidden.contains(el.get("Length"))) {
         subpanels.add(generateStandardTextPanel(el.get("Length"), true));
      }
      if (subpanels.size() > 0) {
         groupsToShow.add(new XMLGroupPanel(getPanelContainer(),
                                            el,
                                            subpanels,
                                            "",
                                            true,
                                            false,
                                            true));
      }
      subpanels.clear();
      if (!hidden.contains(el.get("Name"))) {
         subpanels.add(generateStandardTextPanel(el.get("Name"), true));
      }
      if (!hidden.contains(el.get("IsArray"))) {
         XMLAttribute isa = (XMLAttribute) el.get("IsArray");
         // List choices=isa.getChoices();
         subpanels.add(new XMLCheckboxPanel(getPanelContainer(),
                                            isa,
                                            "",
                                            true,
                                            JaWEManager.getInstance()
                                               .getJaWEController()
                                               .canModifyElement(isa), false));
      }

      if (subpanels.size() > 0) {
         groupsToShow.add(new XMLGroupPanel(getPanelContainer(),
                                            el,
                                            subpanels,
                                            "",
                                            true,
                                            false,
                                            true));
      }

      if (!hidden.contains(el.get("InitialValue"))) {
         groupsToShow.add(getPanel(el.get("InitialValue")));
      }

      if (!hidden.contains(el.getDataType())) {
         groupsToShow.add(getPanel(el.getDataType()));
      }
      if (!hidden.contains(el.get("Description"))) {
         groupsToShow.add(getPanel(el.get("Description")));
      }
      if (!hidden.contains(el.getExtendedAttributes())) {
         groupsToShow.add(getPanel(el.getExtendedAttributes()));
      }
      if (groupsToShow.size() == 6) {
         XMLGroupPanel g1 = new XMLGroupPanel(getPanelContainer(), el, new Object[] {
               groupsToShow.get(0), groupsToShow.get(1)
         }, "", false, false, true);
         return new XMLGroupPanel(getPanelContainer(), el, new Object[] {
               g1,
               groupsToShow.get(2),
               groupsToShow.get(3),
               groupsToShow.get(4),
               groupsToShow.get(5)
         }, "", true, false, true);

      }
      return new XMLGroupPanel(getPanelContainer(),
                               el,
                               groupsToShow,
                               "",
                               true,
                               false,
                               true);
   }

   public XMLPanel getPanel(DataFields el) {
      return generateStandardPanel(el);
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
                                      .canModifyElement(el));
   }

   public XMLPanel getPanel(Deadline el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(DeadlineCondition el) {
      return new XMLMultiLineTextPanel(getPanelContainer(),
                                       el,
                                       el.toName(),
                                       false,
                                       true,
                                       XMLMultiLineTextPanel.SIZE_LARGE,
                                       false,
                                       JaWEManager.getInstance()
                                          .getJaWEController()
                                          .canModifyElement(el));
   }

   public XMLPanel getPanel(Deadlines el) {
      return generateStandardListPanel(el, true, false);
   }

   public XMLPanel getPanel(DeclaredType el) {
      XMLElement tdsel = el;
      while (!(tdsel instanceof TypeDeclarations)) {
         tdsel = tdsel.getParent();
         if (tdsel == null) {
            tdsel = XMLUtil.getPackage(el).getTypeDeclarations();
            break;
         }
      }

      TypeDeclarations tds = (TypeDeclarations) tdsel;
      List choices = tds.toElements();
      XMLElement choosen = tds.getTypeDeclaration(el.getId());
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

      return new XMLComboPanel(getPanelContainer(),
                               cc,
                               null,
                               false,
                               true,
                               false,
                               false,
                               JaWEManager.getInstance()
                                  .getJaWEController()
                                  .canModifyElement(el));

   }

   public XMLPanel getPanel(Description el) {
      return generateStandardMultiLineTextPanel(el,
                                                true,
                                                XMLMultiLineTextPanel.SIZE_SMALL,
                                                true);
   }

   public XMLPanel getPanel(Documentation el) {
      return new XMLLocationPanel(getPanelContainer(), el, JaWEManager.getInstance()
         .getJaWEController()
         .canModifyElement(el));
   }

   public XMLPanel getPanel(Duration el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(EnumerationType el) {
      return generateStandardListPanel(el, true, false);
   }

   public XMLPanel getPanel(EnumerationValue el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(ExceptionName el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(ExtendedAttributes el) {
      return generateStandardTablePanel(el, true, false);
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

   public XMLPanel getPanel(FinishMode el) {
      return getPanel(el.getStartFinishModes());
   }

   public XMLPanel getPanel(FormalParameter el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(FormalParameters el) {
      return generateStandardPanel(el);
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
                                          false);

      p.getComboBox().setRenderer(cbr);
      return p;
   }

   public XMLPanel getPanel(Implementation el) {
      // XMLComplexChoice refMode=new XMLComplexChoice(null, "Type" , true ) {
      // protected void fillChoices() {
      // this.choices.add(new No(null));
      // this.choices.add(new Tools(null));
      // this.choices.add(new SubFlow(null));
      // }
      // };
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(ImplementationTypes el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(InitialValue el) {
      return generateStandardMultiLineTextPanel(el,
                                                true,
                                                XMLMultiLineTextPanel.SIZE_MEDIUM,
                                                false);
   }

   public XMLPanel getPanel(Join el) {
      return generateStandardGroupPanel(el, true, false);
   }

   public XMLPanel getPanel(Length el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(Limit el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(ListType el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(Manual el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(Member el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(Namespace el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(Namespaces el) {
      return generateStandardTablePanel(el, true, false);
   }

   public XMLPanel getPanel(No el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(Package el) {
      List panels = new ArrayList();
      Set hidden = getHiddenElements("XMLGroupPanel", el);
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
                                   false);
      } else if (panels.size() == 1) {
         return (XMLPanel) panels.get(0);
      } else {
         return new XMLBasicPanel();
      }

   }

   public XMLPanel getBasicPanel(Package el) {
      List panels = new ArrayList();
      Set hidden = getHiddenElements("XMLGroupPanel", el);
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
                                   false);
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
                                     true);
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
            if (!hidden.contains(el.getWorkflowProcesses())) {
               p = this.getPanel(el.getWorkflowProcesses());
            }
            break;

         case 10:
            if (!hidden.contains(el.getNamespaces())) {
               p = this.getPanel(el.getNamespaces());
            }
            break;

         default:
            throw new RuntimeException();

      }
      return p;
   }

   public XMLPanel getPanel(PackageHeader el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(Participant el) {
      return generateStandardGroupPanel(el, true, false);
   }

   public XMLPanel getPanel(Participants el) {
      return generateStandardTablePanel(el, true, false);
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
                                  .canModifyElement(el));
   }

   public XMLPanel getPanel(Performer el) {
      Activity act = XMLUtil.getActivity(el);
      int type = act.getActivityType();
      if (type == XPDLConstants.ACTIVITY_TYPE_NO
          || type == XPDLConstants.ACTIVITY_TYPE_TOOL) {
         SequencedHashMap choices = XMLUtil.getPossibleParticipants(XMLUtil.getWorkflowProcess(el),
                                                                    JaWEManager.getInstance()
                                                                       .getXPDLHandler());
         Object choosen = choices.get(el.toValue());
         if (choosen == null) {
            if (!el.toValue().equals("")) {
               choosen = el.toValue();
            }
         }
         SpecialChoiceElement cc = new SpecialChoiceElement(el,
                                                            "",
                                                            new ArrayList(choices.values()),
                                                            choosen,
                                                            true,
                                                            "Id",
                                                            el.toName(),
                                                            el.isRequired());
         cc.setReadOnly(el.isReadOnly());

         return new XMLComboPanel(getPanelContainer(),
                                  cc,
                                  null,
                                  false,
                                  true,
                                  false,
                                  true,
                                  JaWEManager.getInstance()
                                     .getJaWEController()
                                     .canModifyElement(el));

      }
      return new XMLTextPanel(getPanelContainer(),
                              el,
                              false,
                              false,
                              JaWEManager.getInstance()
                                 .getJaWEController()
                                 .canModifyElement(el));
   }

   public XMLPanel getPanel(Priority el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(PriorityUnit el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(ProcessHeader el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(RecordType el) {
      return generateStandardListPanel(el, true, false);
      /*
       * XMLListPanel controlledPanel=new XMLListPanel(el,"",false,true,false) { public
       * boolean checkRequired () { if (el.isReadOnly() || (el.size()>0)) { return true; }
       * else {
       * XMLPanel.errorMessage(this.getDialog(),JaWEManager.getInstance().getLabelGenerator().getLabel(el),"",
       * getLanguageDependentString("ErrorTheListMustContainAtLeastOneElement"));
       * controlPanel.getComponent(1).requestFocus(); return false; } } };
       * controlPanel=new XMLListControlPanel(el,"",true,false,true); return new
       * XMLGroupPanel(el,new XMLPanel[]{
       * controlledPanel,controlPanel},JaWEManager.getInstance().getLabelGenerator().getLabel(el),XMLPanel.BOX_LAYOUT,
       * false,true);
       */
   }

   public XMLPanel getPanel(RedefinableHeader el) {
      // TODO
      return generateStandardPanel(el);
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

      return new XMLComboPanel(getPanelContainer(),
                               cc,
                               null,
                               false,
                               true,
                               false,
                               false,
                               JaWEManager.getInstance()
                                  .getJaWEController()
                                  .canModifyElement(el));
   }

   public XMLPanel getPanel(Responsibles el) {
      return generateStandardListPanel(el, true, false);
   }

   public XMLPanel getPanel(Route el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(SchemaType el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(Script el) {
      return generateStandardGroupPanel(el, false, false);
   }

   protected XMLPanel getPanel(Script el, boolean hasTitle) {
      return generateStandardGroupPanel(el, hasTitle, false);
   }

   public XMLPanel getPanel(SimulationInformation el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(Split el) {
      return generateStandardGroupPanel(el, true, false);
   }

   public XMLPanel getPanel(StartFinishModes el) {
      return new XMLComboPanel(getPanelContainer(),
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

   public XMLPanel getPanel(StartMode el) {
      return getPanel(el.getStartFinishModes());
   }

   public XMLPanel getPanel(SubFlow el) {
      Set hidden = getHiddenElements("XMLGroupPanel", el);
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

      final XMLComboPanel cp = new XMLComboPanel(getPanelContainer(),
                                                 cc,
                                                 null,
                                                 false,
                                                 true,
                                                 false,
                                                 true,
                                                 JaWEManager.getInstance()
                                                    .getJaWEController()
                                                    .canModifyElement(el.get("Id")));

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
         final XMLActualParametersPanel app = new XMLActualParametersPanel(getPanelContainer(),
                                                                           el.getActualParameters(),
                                                                           fps);
         panelElements.add(app);
         cp.getComboBox().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               Object sel = cp.getSelectedItem();
               FormalParameters _fps = null;
               if (sel instanceof WorkflowProcess) {
                  _fps = ((WorkflowProcess) sel).getFormalParameters();
               }
               app.setFormalParameters(_fps);
               app.validate();
            }
         });
         cp.getComboBox()
            .getEditor()
            .getEditorComponent()
            .addKeyListener(new KeyAdapter() {
               public void keyPressed(KeyEvent e) {
                  Object sel = cp.getSelectedItem();
                  FormalParameters _fps = null;
                  if (sel instanceof WorkflowProcess) {
                     _fps = ((WorkflowProcess) sel).getFormalParameters();
                  }
                  app.setFormalParameters(_fps);
                  app.validate();
               }
            });
         cp.getComboBox().addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
               Object sel = cp.getSelectedItem();
               FormalParameters _fps = null;
               if (sel instanceof WorkflowProcess) {
                  _fps = ((WorkflowProcess) sel).getFormalParameters();
               }
               app.setFormalParameters(_fps);
               app.validate();
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
                                  true);
      }
      return null;
   }

   public XMLPanel getPanel(TimeEstimation el) {
      return generateStandardGroupPanel(el, false, false);
   }

   public XMLPanel getPanel(Tool el) {
      Set hidden = getHiddenElements("XMLGroupPanel", el);
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

      final XMLComboPanel cp = new XMLComboPanel(getPanelContainer(),
                                                 cc,
                                                 null,
                                                 false,
                                                 true,
                                                 false,
                                                 false,
                                                 JaWEManager.getInstance()
                                                    .getJaWEController()
                                                    .canModifyElement(el.get("Id")));

      if (!hidden.contains(el.get("Id"))) {
         panelElements.add(cp);
      }
      if (!hidden.contains(el.getTypeAttribute())) {
         panelElements.add(el.getTypeAttribute());
      }
      if (!hidden.contains(el.getActualParameters())) {
         FormalParameters fps = null;
         if (choosen instanceof Application) {
            fps = ((Application) choosen).getApplicationTypes().getFormalParameters();
         }
         final XMLActualParametersPanel app = new XMLActualParametersPanel(getPanelContainer(),
                                                                           el.getActualParameters(),
                                                                           fps);
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
      if (!hidden.contains(el.getExtendedAttributes())) {
         panelElements.add(el.getExtendedAttributes());
      }
      if (panelElements.size() > 0) {
         return new XMLGroupPanel(getPanelContainer(),
                                  el,
                                  panelElements,
                                  getPanelContainer().getLanguageDependentString(el.toName()
                                                                                 + "Key"),
                                  true,
                                  false,
                                  true);
      }
      return null;
   }

   public XMLPanel getPanel(Tools el) {
      return generateStandardTablePanel(el, true, false);
   }

   public XMLPanel getPanel(Transition el) {
      Set hidden = getHiddenElements("XMLGroupPanel", el);
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
                                  true);
      }
      return new XMLBasicPanel();
   }

   public XMLPanel getPanel(TransitionRef el) {
      // Activity act=XMLUtil.getActivity(el);
      // Set ogt=XMLUtil.getNonExceptionalOutgoingTransitions(act);
      // List ordrdl=XMLUtil.getOrderedOutgoingTransitions(act, ogt);
      // SequencedHashMap choices=new SequencedHashMap();
      // Iterator it=ordrdl.iterator();
      // while (it.hasNext()) {
      // Transition t=(Transition)it.next();
      // choices.put(t.getId(),t);
      // }
      // Transition choosen=null;
      // String tId=el.getId();
      // if (!tId.equals("")) {
      // it=choices.values().iterator();
      // while (it.hasNext()) {
      // Transition t=(Transition)it.next();
      // if (tId.equals(t.getId())) {
      // choosen=t;
      // break;
      // }
      // }
      // }
      // // filter choices: exclude already existing
      // it=((TransitionRefs)el.getParent()).toElements().iterator();
      // System.out.println("Choices="+choices.keySet());
      // while (it.hasNext()) {
      // TransitionRef tr=(TransitionRef)it.next();
      // choices.remove(tr.getId());
      // }
      // if (choosen!=null) {
      // choices.put(choosen.getId(),choosen);
      // }
      // // else if (choices.size()>0) {
      // // choosen=(Transition)choices.getValue(0);
      // // getPanelContainer().setModified(true);
      // // }
      //      
      //      
      // SpecialChoiceElement cc=new SpecialChoiceElement(
      // el,
      // "Id",
      // new ArrayList(choices.values()),
      // choosen,
      // false,
      // "Id",
      // el.toName(),
      // el.isRequired());
      // cc.setReadOnly(true);
      //         
      // return new XMLComboPanel(getPanelContainer(),cc,null,true,false,false);
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(TransitionRefs el) {
      return generateStandardListPanel(el, true, false);
   }

   public XMLPanel getPanel(TransitionRestriction el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(TransitionRestrictions el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(Transitions el) {
      return generateStandardTablePanel(el, true, false);
   }

   public XMLPanel getPanel(TypeDeclaration el) {

      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(TypeDeclarations el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(UnionType el) {
      return generateStandardListPanel(el, true, false);
   }

   public XMLPanel getPanel(ValidFrom el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(ValidTo el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(Vendor el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(Version el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(WaitingTime el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(WorkflowProcess el) {
      List panels = new ArrayList();
      Set hidden = getHiddenElements("XMLGroupPanel", el);
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
                                   false);
      } else if (panels.size() == 1) {
         return (XMLPanel) panels.get(0);
      } else {
         return new XMLBasicPanel();
      }

   }

   public XMLPanel getBasicPanel(WorkflowProcess el) {
      List panels = new ArrayList();
      Set hidden = getHiddenElements("XMLGroupPanel", el);
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
                                   false);
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
                                     true);
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

   public XMLPanel getPanel(WorkflowProcesses el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(WorkingTime el) {
      return generateStandardPanel(el);
   }

   public XMLPanel getPanel(XPDLVersion el) {
      return generateStandardPanel(el);
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
         if (XMLUtil.getWorkflowProcess(holder) != null
             && (holder instanceof Activity
                 || holder instanceof Transition || holder instanceof Tool || holder instanceof WorkflowProcess)) {
            return new XMLMultiLineTextPanel(getPanelContainer(),
                                             el,
                                             "Value",
                                             false,
                                             true,
                                             XMLMultiLineTextPanel.SIZE_LARGE,
                                             false,
                                             JaWEManager.getInstance()
                                                .getJaWEController()
                                                .canModifyElement(el));
         } else if (holder instanceof Application
                    && ((Application) holder).getApplicationTypes().getChoosen() instanceof FormalParameters) {
            return new XMLMultiLineTextPanel(getPanelContainer(),
                                             el,
                                             "Value",
                                             false,
                                             true,
                                             XMLMultiLineTextPanel.SIZE_LARGE,
                                             false,
                                             JaWEManager.getInstance()
                                                .getJaWEController()
                                                .canModifyElement(el));
         } else {
            return new XMLMultiLineTextPanel(getPanelContainer(),
                                             el,
                                             "Value",
                                             false,
                                             true,
                                             XMLMultiLineTextPanel.SIZE_LARGE,
                                             false,
                                             JaWEManager.getInstance()
                                                .getJaWEController()
                                                .canModifyElement(el));
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

         return new XMLComboPanel(getPanelContainer(),
                                  cc,
                                  null,
                                  false,
                                  true,
                                  false,
                                  false,
                                  JaWEManager.getInstance()
                                     .getJaWEController()
                                     .canModifyElement(el));

      }
      if (el.toName().equalsIgnoreCase("From")) {
         return getPanelForFROMAttribute((Transition) el.getParent());
      }
      if (el.toName().equalsIgnoreCase("To")) {
         return getPanelForTOAttribute((Transition) el.getParent());
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

   protected XMLPanel getPanelForFROMAttribute(Transition transition) {
      SequencedHashMap choices = JaWEManager.getInstance()
         .getTransitionHandler()
         .getPossibleSourceActivities(transition);

      // System.out.println("Choices="+choices);
      String tFrom = transition.getFrom();

      Activity current = ((Activities) ((XMLCollectionElement) transition.getParent()
         .getParent()).get("Activities")).getActivity(tFrom);
      if (current != null)
         choices.put(current.getId(), current);

      Activity choosen = null;
      if (!tFrom.equals("")) {
         choosen = (Activity) choices.get(tFrom);
      }
      // if (choosen==null && choices.size()>0) {
      // choosen=(Activity)choices.getValue(0);
      // getPanelContainer().setModified(true);
      // }

      XMLAttribute from = (XMLAttribute) transition.get("From");
      SpecialChoiceElement cc = new SpecialChoiceElement(from,
                                                         "",
                                                         new ArrayList(choices.values()),
                                                         choosen,
                                                         true,
                                                         "Id",
                                                         "From",
                                                         from.isRequired());
      cc.setReadOnly(from.isReadOnly());
      return new XMLComboPanel(getPanelContainer(),
                               cc,
                               null,
                               false,
                               true,
                               false,
                               false,
                               JaWEManager.getInstance()
                                  .getJaWEController()
                                  .canModifyElement(from));
   }

   protected XMLPanel getPanelForTOAttribute(Transition transition) {
      SequencedHashMap choices = JaWEManager.getInstance()
         .getTransitionHandler()
         .getPossibleTargetActivities(transition);
      String tTo = transition.getTo();

      Activity current = ((Activities) ((XMLCollectionElement) transition.getParent()
         .getParent()).get("Activities")).getActivity(tTo);
      if (current != null)
         choices.put(current.getId(), current);

      Activity choosen = null;
      if (!tTo.equals("")) {
         choosen = (Activity) choices.get(tTo);
      }
      // if (choosen==null && choices.size()>0) {
      // choosen=(Activity)choices.getValue(0);
      // getPanelContainer().setModified(true);
      // }

      XMLAttribute to = (XMLAttribute) transition.get("To");
      SpecialChoiceElement cc = new SpecialChoiceElement(to,
                                                         "",
                                                         new ArrayList(choices.values()),
                                                         choosen,
                                                         true,
                                                         "Id",
                                                         "To",
                                                         to.isRequired());
      cc.setReadOnly(to.isReadOnly());
      return new XMLComboPanel(getPanelContainer(),
                               cc,
                               null,
                               false,
                               true,
                               false,
                               false,
                               JaWEManager.getInstance()
                                  .getJaWEController()
                                  .canModifyElement(to));
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
         panel = generateStandardTablePanel((XMLCollection) el, true, false);
      } else {
         panel = new XMLBasicPanel();
      }
      return panel;
   }

   protected XMLMultiLineTextPanel generateStandardMultiLineTextPanel(

   XMLElement el, boolean isVertical, int size, boolean wrapLines) {
      return new XMLMultiLineTextPanel(getPanelContainer(),
                                       el,
                                       isVertical,
                                       size,
                                       wrapLines,
                                       JaWEManager.getInstance()
                                          .getJaWEController()
                                          .canModifyElement(el));
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

   protected XMLBasicTablePanel generateStandardTablePanel(XMLCollection cl,
                                                           boolean hasTitle,
                                                           boolean hasEmptyBorder) {
      List elementsToShow = cl.toElements();
      Set hidden = getHiddenElements("XMLTablePanel", cl);
      elementsToShow.removeAll(hidden);
      List columnsToShow = getColumnsToShow("XMLTablePanel", cl);
      boolean miniDim = false;
      if (cl instanceof ExtendedAttributes) {
         miniDim = true;
      }
      return new XMLBasicTablePanel((InlinePanel) getPanelContainer(),
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
                                    true,
                                    false,
                                    false,
                                    miniDim,
                                    true,
                                    true);
   }

   protected XMLBasicListPanel generateStandardListPanel(XMLCollection cl,
                                                         boolean hasTitle,
                                                         boolean hasEmptyBorder) {
      List elementsToShow = cl.toElements();
      Set hidden = getHiddenElements("XMLListPanel", cl);
      elementsToShow.removeAll(hidden);
      return new XMLBasicListPanel((InlinePanel) getPanelContainer(),
                                   cl,
                                   elementsToShow,
                                   JaWEManager.getInstance()
                                      .getLabelGenerator()
                                      .getLabel(cl)
                                         + ", "
                                         + (cl.size() - hidden.size())
                                         + " "
                                         + getPanelContainer().getLanguageDependentString("ElementsKey"),
                                   true,
                                   false,
                                   true,
                                   false);
   }

   protected XMLGroupPanel generateStandardGroupPanel(XMLComplexElement cel,
                                                      boolean hasTitle,
                                                      boolean hasEmptyBorder) {
      Set hidden = getHiddenElements("XMLGroupPanel", cel);
      List toShow = new ArrayList(cel.toElements());
      toShow.removeAll(hidden);
      if (cel instanceof Condition) {
         toShow.add(new XMLMultiLineTextPanel(getPanelContainer(),
                                              cel,
                                              "Xpression",
                                              false,
                                              true,
                                              XMLMultiLineTextPanel.SIZE_MEDIUM,
                                              false,
                                              JaWEManager.getInstance()
                                                 .getJaWEController()
                                                 .canModifyElement(cel)));
      } else if (cel instanceof ExtendedAttribute) {
         XMLElement holder = cel.getParent().getParent();
         if (XMLUtil.getWorkflowProcess(holder) != null
             && (holder instanceof Activity
                 || holder instanceof Transition || holder instanceof Application
                 || holder instanceof Tool || holder instanceof WorkflowProcess)) {
            toShow.add(new XMLMultiLineTextPanel(getPanelContainer(),
                                                 cel,
                                                 "ComplexContent",
                                                 false,
                                                 true,
                                                 XMLMultiLineTextPanel.SIZE_LARGE,
                                                 false,
                                                 JaWEManager.getInstance()
                                                    .getJaWEController()
                                                    .canModifyElement(cel)));
         } else {
            toShow.add(new XMLMultiLineTextPanel(getPanelContainer(),
                                                 cel,
                                                 "ComplexContent",
                                                 false,
                                                 true,
                                                 XMLMultiLineTextPanel.SIZE_LARGE,
                                                 false,
                                                 JaWEManager.getInstance()
                                                    .getJaWEController()
                                                    .canModifyElement(cel)));
         }
      } else if (cel instanceof SchemaType) {
         toShow.add(new XMLMultiLineTextPanel(getPanelContainer(),
                                              cel,
                                              "ComplexContent",
                                              false,
                                              true,
                                              XMLMultiLineTextPanel.SIZE_LARGE,
                                              false,
                                              JaWEManager.getInstance()
                                                 .getJaWEController()
                                                 .canModifyElement(cel)));
      }
      return new XMLGroupPanel(getPanelContainer(),
                               cel,
                               toShow,
                               JaWEManager.getInstance()
                                  .getLabelGenerator()
                                  .getLabel(cel),
                               true,
                               hasTitle,
                               hasEmptyBorder);
   }

   protected Set getHiddenElements(String panelName, XMLComplexElement cel) {
      Set hidden = new HashSet();

      String hstr = getPanelContainer().getSettings().getSettingString("HideSubElements."
                                                                       + panelName + "."
                                                                       + cel.toName());

      String[] hstra = Utils.tokenize(hstr, " ");
      if (hstra != null) {
         for (int i = 0; i < hstra.length; i++) {
            XMLElement el = cel.get(hstra[i]);
            if (el != null) {
               hidden.add(el);
            } else if (cel instanceof Package) {
               Package pkg = (Package) cel;
               if (hstra[i].equals(pkg.getNamespaces().toName())) {
                  hidden.add(pkg.getNamespaces());
               }
            }
         }
      }
      // if
      // (getProperties().getProperty(HIDE_COLLECTIONS_AND_COMPLEX_CHOICES,"false").equals("true"))
      // {
      // List subEls=cel.toElements();
      // for (int i=0; i<subEls.size(); i++) {
      // XMLElement subEl=(XMLElement)subEls.get(i);
      // if (subEl instanceof XMLCollection || subEl instanceof XMLComplexChoice || subEl
      // instanceof DataType) {
      // hidden.add(subEl);
      // }
      // }
      // }
      return hidden;
   }

   protected Set getHiddenElements(String panelName, XMLCollection col) {
      Set hidden = new HashSet();
      String elAttr = getPanelContainer().getSettings().getSettingString("HideElements."
                                                                         + panelName
                                                                         + "."
                                                                         + col.toName());
      String[] els = Utils.tokenize(elAttr, " ");
      for (int k = 0; k < els.length; k++) {
         String key = els[k];
         String hstr = getPanelContainer().getSettings().getSettingString("HideElements."
                                                                          + panelName
                                                                          + "."
                                                                          + col.toName()
                                                                          + "." + key);
         String[] hstra = Utils.tokenize(hstr, " ");
         if (hstra != null) {
            for (int i = 0; i < hstra.length; i++) {
               if (hstra[i].equals("*")) {
                  hidden.addAll(col.toElements());
                  return hidden;
               }
               Iterator ci = col.toElements().iterator();
               while (ci.hasNext()) {
                  XMLElement el = (XMLElement) ci.next();
                  if (key.equals("")) {
                     if (el.toValue().equals(hstra[i])) {
                        hidden.add(el);
                     }
                  } else {
                     if (el instanceof XMLComplexElement) {
                        XMLElement sel = ((XMLComplexElement) el).get(key);
                        if (sel != null) {
                           if (sel.toValue().equals(hstra[i])) {
                              hidden.add(el);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
      // System.out.println("There are "+hidden.size()+" hidden elements for panel
      // "+panelName+",
      // col="+col+", ks="+keyStarts+", colsize="+col.size());
      return hidden;
   }

   protected List getColumnsToShow(String panelName, XMLCollection col) {
      XMLElement el = col.generateNewElement();
      List toShow = new ArrayList();
      if (el instanceof XMLComplexElement) {
         String hstr = getPanelContainer().getSettings().getSettingString("ShowColumns."
                                                                          + panelName
                                                                          + "."
                                                                          + col.toName());
         // System.err.println("CTS for col "+col+" is "+hstr);
         String[] hstra = Utils.tokenize(hstr, " ");
         if (hstra.length > 0) {
            toShow.addAll(Arrays.asList(hstra));
         } else {
            toShow.addAll(((XMLComplexElement) el).toElementMap().keySet());
         }
         // System.err.println("CTS list for col "+col+" is "+toShow);
      }
      return toShow;
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
