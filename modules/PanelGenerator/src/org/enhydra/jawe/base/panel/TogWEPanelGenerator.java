package org.enhydra.jawe.base.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.panel.panels.XMLActualParametersPanel;
import org.enhydra.jawe.base.panel.panels.XMLBasicPanel;
import org.enhydra.jawe.base.panel.panels.XMLComboChoicePanel;
import org.enhydra.jawe.base.panel.panels.XMLComboPanel;
import org.enhydra.jawe.base.panel.panels.XMLComboPanelWithReferenceLink;
import org.enhydra.jawe.base.panel.panels.XMLGroupPanel;
import org.enhydra.jawe.base.panel.panels.XMLHighlightPanelWithReferenceLink;
import org.enhydra.jawe.base.panel.panels.XMLListPanel;
import org.enhydra.jawe.base.panel.panels.XMLLocationPanel;
import org.enhydra.jawe.base.panel.panels.XMLMultiLineHighlightPanelWithChoiceButton;
import org.enhydra.jawe.base.panel.panels.XMLMultiLineTextPanel;
import org.enhydra.jawe.base.panel.panels.XMLMultiLineTextPanelWithChoiceButton;
import org.enhydra.jawe.base.panel.panels.XMLPanel;
import org.enhydra.jawe.base.panel.panels.XMLTablePanel;
import org.enhydra.jawe.base.panel.panels.XMLTablePanelForVariables;
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
import org.enhydra.shark.xpdl.elements.Activities;
import org.enhydra.shark.xpdl.elements.Activity;
import org.enhydra.shark.xpdl.elements.ActivitySets;
import org.enhydra.shark.xpdl.elements.ActualParameter;
import org.enhydra.shark.xpdl.elements.ActualParameters;
import org.enhydra.shark.xpdl.elements.Application;
import org.enhydra.shark.xpdl.elements.BlockActivity;
import org.enhydra.shark.xpdl.elements.Condition;
import org.enhydra.shark.xpdl.elements.DataFields;
import org.enhydra.shark.xpdl.elements.DeadlineCondition;
import org.enhydra.shark.xpdl.elements.Deadlines;
import org.enhydra.shark.xpdl.elements.DeclaredType;
import org.enhydra.shark.xpdl.elements.EnumerationType;
import org.enhydra.shark.xpdl.elements.ExceptionName;
import org.enhydra.shark.xpdl.elements.ExtendedAttribute;
import org.enhydra.shark.xpdl.elements.ExtendedAttributes;
import org.enhydra.shark.xpdl.elements.ExternalPackage;
import org.enhydra.shark.xpdl.elements.ExternalPackages;
import org.enhydra.shark.xpdl.elements.FormalParameters;
import org.enhydra.shark.xpdl.elements.Namespaces;
import org.enhydra.shark.xpdl.elements.Participant;
import org.enhydra.shark.xpdl.elements.Participants;
import org.enhydra.shark.xpdl.elements.Performer;
import org.enhydra.shark.xpdl.elements.RecordType;
import org.enhydra.shark.xpdl.elements.Responsible;
import org.enhydra.shark.xpdl.elements.Responsibles;
import org.enhydra.shark.xpdl.elements.SchemaType;
import org.enhydra.shark.xpdl.elements.SubFlow;
import org.enhydra.shark.xpdl.elements.TimeEstimation;
import org.enhydra.shark.xpdl.elements.Tool;
import org.enhydra.shark.xpdl.elements.Tools;
import org.enhydra.shark.xpdl.elements.Transition;
import org.enhydra.shark.xpdl.elements.TransitionRefs;
import org.enhydra.shark.xpdl.elements.Transitions;
import org.enhydra.shark.xpdl.elements.TypeDeclaration;
import org.enhydra.shark.xpdl.elements.TypeDeclarations;
import org.enhydra.shark.xpdl.elements.UnionType;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;

/**
 * Used to generate panels for all XPDL entities.
 * 
 * @author Zoran Milakovic
 * @author Sasa Bojanic
 */
public class TogWEPanelGenerator extends StandardPanelGenerator {

   // public static final String
   // HIDE_COLLECTIONS_AND_COMPLEX_CHOICES="HideCollectionsAndComplexChoices";

   public TogWEPanelGenerator() {
   }

   public XMLPanel getPanel(Activities el) {
      return togwe_generateStandardTablePanel(el, true, false);
   }

   protected XMLPanel getPanel(Activity el, int no, Set hidden) {
      XMLPanel p = null;
      if (no != 2) {
         p = super.getPanel(el, no, hidden);
      } else {
         if (!hidden.contains(el.getActivityTypes())) {
            ActivityTypesChoiceElement atce = new ActivityTypesChoiceElement(el.getActivityTypes());
            atce.setReadOnly(el.getActivityTypes().isReadOnly());
            p = this.getPanel(atce);
         }
      }
      return p;
   }

   public XMLPanel getPanel(ActivitySets el) {
      return togwe_generateStandardTablePanel(el, true, false);
   }

   public XMLPanel getPanel(ActualParameter el) {
      // List panelElements=new ArrayList();
      // SequencedHashMap choices =
      // XMLUtil.getPossibleVariables(XMLUtil.getWorkflowProcess(el));
      // Object choosen=choices.get(el.toValue());
      // if (choosen==null) {
      // if (!el.toValue().equals("")) {
      // choosen=el.toValue();
      // }
      // }
      // SpecialChoiceElement cc=new SpecialChoiceElement(
      // el,
      // "",
      // new ArrayList(choices.values()),
      // choosen,
      // true,
      // "Id",
      // el.toName(),
      // el.isRequired());
      // cc.setReadOnly(el.isReadOnly());
      return new XMLMultiLineHighlightPanelWithChoiceButton(getPanelContainer(),
                                                       el,
                                                       "Xpression",
                                                       false,
                                                       true,
                                                       XMLMultiLineTextPanel.SIZE_LARGE,
                                                       false,
                                                       getActualParameterOrConditionChoices(el),
                                                       JaWEManager.getInstance()
                                                          .getJaWEController()
                                                          .canModifyElement(el));
      // return new
      // XMLComboPanelWithReferenceLink(getPanelContainer(),cc,null,true,false,true);
   }

   public XMLPanel getPanel(ActualParameters el) {
      return togwe_generateStandardTablePanel(el, true, false);
   }

   public XMLPanel getPanel(DeadlineCondition el) {
      return new XMLMultiLineHighlightPanelWithChoiceButton(getPanelContainer(),
                                                       el,
                                                       el.toName(),
                                                       false,
                                                       true,
                                                       XMLMultiLineTextPanel.SIZE_LARGE,
                                                       false,
                                                       new ArrayList(XMLUtil.getPossibleVariables(XMLUtil.getWorkflowProcess(el))
                                                          .values()),
                                                       JaWEManager.getInstance()
                                                          .getJaWEController()
                                                          .canModifyElement(el));
   }

   public XMLPanel getPanel(Deadlines el) {
      return togwe_generateStandardListPanel(el, true, false);
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

      return new XMLComboPanelWithReferenceLink(getPanelContainer(),
                                                cc,
                                                null,
                                                true,
                                                false,
                                                false,
                                                JaWEManager.getInstance()
                                                   .getJaWEController()
                                                   .canModifyElement(el));

   }

   public XMLPanel getPanel(EnumerationType el) {
      return togwe_generateStandardListPanel(el, true, false);
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
      return new XMLMultiLineTextPanelWithChoiceButton(getPanelContainer(),
                                                       el,
                                                       el.toName(),
                                                       false,
                                                       true,
                                                       XMLMultiLineTextPanel.SIZE_SMALL,
                                                       false,
                                                       choices,
                                                       JaWEManager.getInstance()
                                                          .getJaWEController()
                                                          .canModifyElement(el));
   }

   public XMLPanel getPanel(ExtendedAttributes el) {
      return togwe_generateStandardTablePanel(el, true, false);
   }

   public XMLPanel getPanel(ExternalPackages el) {
      return togwe_generateStandardListPanel(el, true, false);
   }

   public XMLPanel getPanel(Namespaces el) {
      return togwe_generateStandardTablePanel(el, true, false);
   }

   public XMLPanel getPanel(Participants el) {
      return togwe_generateStandardTablePanel(el, true, false);
   }

   public XMLPanel getPanel(Performer el) {
      Activity act = XMLUtil.getActivity(el);
      int type = act.getActivityType();
      if (type == XPDLConstants.ACTIVITY_TYPE_NO
          || type == XPDLConstants.ACTIVITY_TYPE_TOOL) {

         return new XMLHighlightPanelWithReferenceLink(getPanelContainer(),
								                 el,
								                 null,
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

   public XMLPanel getPanel(RecordType el) {
      return togwe_generateStandardListPanel(el, true, false);
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
                                                   .canModifyElement(el));
   }

   public XMLPanel getPanel(Responsibles el) {
      return togwe_generateStandardListPanel(el, true, false);
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

      final XMLComboPanelWithReferenceLink cp = new XMLComboPanelWithReferenceLink(getPanelContainer(),
                                                                                   cc,
                                                                                   null,
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

      final XMLComboPanelWithReferenceLink cp = new XMLComboPanelWithReferenceLink(getPanelContainer(),
                                                                                   cc,
                                                                                   null,
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
      return togwe_generateStandardTablePanel(el, true, false);
   }

   public XMLPanel getPanel(TransitionRefs el) {
      return togwe_generateStandardListPanel(el, true, false);
   }

   public XMLPanel getPanel(Transitions el) {
      return togwe_generateStandardTablePanel(el, true, false);
   }

   public XMLPanel getPanel(UnionType el) {
      return togwe_generateStandardListPanel(el, true, false);
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
            return new XMLMultiLineTextPanelWithChoiceButton(getPanelContainer(),
                                                             el,
                                                             "Value",
                                                             false,
                                                             true,
                                                             XMLMultiLineTextPanel.SIZE_LARGE,
                                                             false,
                                                             new ArrayList(XMLUtil.getPossibleVariables(XMLUtil.getWorkflowProcess(holder))
                                                                .values()),
                                                             JaWEManager.getInstance()
                                                                .getJaWEController()
                                                                .canModifyElement(el));
         } else if (holder instanceof Application
                    && ((Application) holder).getApplicationTypes().getChoosen() instanceof FormalParameters) {
            FormalParameters fps = ((Application) holder).getApplicationTypes()
               .getFormalParameters();
            return new XMLMultiLineTextPanelWithChoiceButton(getPanelContainer(),
                                                             el,
                                                             "Value",
                                                             false,
                                                             true,
                                                             XMLMultiLineTextPanel.SIZE_LARGE,
                                                             false,
                                                             fps.toElements(),
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

         return new XMLComboPanelWithReferenceLink(getPanelContainer(),
                                                   cc,
                                                   null,
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
      cc.setChoosen(choosen);
      return new XMLComboPanelWithReferenceLink(getPanelContainer(),
                                                cc,
                                                null,
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
      return new XMLComboPanelWithReferenceLink(getPanelContainer(),
                                                cc,
                                                null,
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
         panel = togwe_generateStandardTablePanel((XMLCollection) el, true, false);
      } else {
         panel = new XMLBasicPanel();
      }
      return panel;
   }

   protected XMLTablePanel togwe_generateStandardTablePanel(XMLCollection cl,
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
                                              true,
                                              false,
                                              false,
                                              miniDim,
                                              true,
                                              true);

      } else
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
                                  true,
                                  false,
                                  false,
                                  miniDim,
                                  true,
                                  true);
   }

   protected XMLListPanel togwe_generateStandardListPanel(XMLCollection cl,
                                                          boolean hasTitle,
                                                          boolean hasEmptyBorder) {
      List elementsToShow = cl.toElements();
      Set hidden = getHiddenElements("XMLListPanel", cl);
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
                              false, false, false);
   }

   protected XMLGroupPanel generateStandardGroupPanel(XMLComplexElement cel,
                                                      boolean hasTitle,
                                                      boolean hasEmptyBorder) {
      Set hidden = getHiddenElements("XMLGroupPanel", cel);
      List toShow = new ArrayList(cel.toElements());
      toShow.removeAll(hidden);
      if (cel instanceof Condition) {    	
         toShow.add(new XMLMultiLineHighlightPanelWithChoiceButton(getPanelContainer(),
                 cel,
                 "Xpression",
                 false,
                 true,
                 XMLMultiLineTextPanel.SIZE_MEDIUM,
                 false,
                 getActualParameterOrConditionChoices(cel),
                 JaWEManager.getInstance()
                    .getJaWEController()
                    .canModifyElement(cel)));
         
      } else if (cel instanceof ExtendedAttribute) {
         XMLElement holder = cel.getParent().getParent();
         if (XMLUtil.getWorkflowProcess(holder) != null
             && (holder instanceof Activity
                 || holder instanceof Transition || holder instanceof Tool || holder instanceof WorkflowProcess)) {
            toShow.add(new XMLMultiLineTextPanelWithChoiceButton(getPanelContainer(),
                                                                 cel,
                                                                 "ComplexContent",
                                                                 false,
                                                                 true,
                                                                 XMLMultiLineTextPanel.SIZE_LARGE,
                                                                 false,
                                                                 new ArrayList(XMLUtil.getPossibleVariables(XMLUtil.getWorkflowProcess(holder))
                                                                    .values()),
                                                                 JaWEManager.getInstance()
                                                                    .getJaWEController()
                                                                    .canModifyElement(cel)));

         } else if (holder instanceof Application
                    && ((Application) holder).getApplicationTypes().getChoosen() instanceof FormalParameters) {
            FormalParameters fps = ((Application) holder).getApplicationTypes()
               .getFormalParameters();
            toShow.add(new XMLMultiLineTextPanelWithChoiceButton(getPanelContainer(),
                                                                 cel,
                                                                 "ComplexContent",
                                                                 false,
                                                                 true,
                                                                 XMLMultiLineTextPanel.SIZE_LARGE,
                                                                 false,
                                                                 fps.toElements(),
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

   protected List getActualParameterOrConditionChoices (XMLElement el) {
      return new ArrayList(XMLUtil.getPossibleVariables(XMLUtil.getWorkflowProcess(el)).values());
   }
   
   
}
