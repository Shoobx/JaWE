package org.enhydra.jawe.wfmopen;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.panel.InlinePanel;
import org.enhydra.jawe.base.panel.TogWEPanelGenerator;
import org.enhydra.jawe.base.panel.panels.XMLBasicPanel;
import org.enhydra.jawe.base.panel.panels.XMLCheckboxPanel;
import org.enhydra.jawe.base.panel.panels.XMLComboPanel;
import org.enhydra.jawe.base.panel.panels.XMLDataTypesPanel;
import org.enhydra.jawe.base.panel.panels.XMLGroupPanel;
import org.enhydra.jawe.base.panel.panels.XMLMultiLineTextPanel;
import org.enhydra.jawe.base.panel.panels.XMLMultiLineTextPanelWithChoiceButton;
import org.enhydra.jawe.base.panel.panels.XMLPanel;
import org.enhydra.jawe.base.panel.panels.XMLTablePanel;
import org.enhydra.shark.xpdl.XMLAttribute;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.elements.Application;
import org.enhydra.shark.xpdl.elements.DataTypes;
import org.enhydra.shark.xpdl.elements.ExtendedAttribute;
import org.enhydra.shark.xpdl.elements.ExtendedAttributes;
import org.enhydra.shark.xpdl.elements.FormalParameters;
import org.enhydra.shark.xpdl.elements.Package;
import org.enhydra.shark.xpdl.elements.Script;
import org.enhydra.shark.xpdl.elements.Split;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;

/**
 * @author Sasa Bojanic
 */
public class WfMOpenPanelGenerator extends TogWEPanelGenerator {

   public WfMOpenPanelGenerator() throws Exception {
      super();
   }

   public XMLPanel getPanel(Application el) {
      Set hidden = getHiddenElements("XMLGroupPanel", el);
      List subpanels = new ArrayList();
      List groupsToShow = new ArrayList();
      if (!hidden.contains(el.get("Id"))) {
         subpanels.add(generateStandardTextPanel(el.get("Id"), true));
      }
      if (!hidden.contains(el.get("Name"))) {
         if (subpanels.size() > 0) {
            subpanels.add(new XMLBasicPanel());
         }
         subpanels.add(generateStandardTextPanel(el.get("Name"), true));
      }
      if (subpanels.size() > 0) {
         groupsToShow.add(new XMLGroupPanel(getPanelContainer(),
                                            el,
                                            subpanels,
                                            "",
                                            false,
                                            false,
                                            true));
      }

      if (!hidden.contains(el.get("Description"))) {
         groupsToShow.add(getPanel(el.get("Description")));
      }
      if (!hidden.contains(el.getApplicationTypes())) {
         groupsToShow.add(getPanel(el.getApplicationTypes().getFormalParameters()));
      }

      ExtendedAttributes eas = el.getExtendedAttributes();
      ExtendedAttribute ea = eas.getFirstExtendedAttributeForName(WfMOpenConstants.EA_IMPLEMENTATION);
      if (ea != null) {
         ImplementationExtendedAttribute iea = new ImplementationExtendedAttribute(ea);
         groupsToShow.add(getPanel(iea));
      }
      if (!hidden.contains(el.getExtendedAttributes())) {
         groupsToShow.add(getPanel(el.getExtendedAttributes()));
      }
      return new XMLGroupPanel(getPanelContainer(),
                               el,
                               groupsToShow,
                               JaWEManager.getInstance().getLabelGenerator().getLabel(el),
                               true,
                               false,
                               true);
   }

   public XMLPanel getPanel(DataTypes el) {
      List choices = new ArrayList(el.getChoices());
      choices.remove(el.getDeclaredType());
      Object chosen = el.getChoosen();
      if (!choices.contains(chosen)) {
         choices.add(0, chosen);
      }
      return new XMLDataTypesPanel(getPanelContainer(),
                                   el,
                                   choices,
                                   JaWEManager.getInstance()
                                      .getLabelGenerator()
                                      .getLabel(el),
                                   JaWEManager.getInstance()
                                      .getJaWEController()
                                      .canModifyElement(el));

   }

   public XMLPanel getPanel(ImplementationExtendedAttribute el) {
      List subpanels = new ArrayList();
      XMLPanel txtPCls = getPanel(el.getClassAttribute());
      XMLPanel txtPExec = getPanel(el.getExecutionAttribute());

      List columnsToShow = getColumnsToShow("XMLTablePanel", el.getExceptionMappings());
      XMLPanel excPnl = new XMLTablePanel((InlinePanel) getPanelContainer(),
                                          el.getExceptionMappings(),
                                          columnsToShow,
                                          el.getExceptionMappings().toElements(),
                                          JaWEManager.getInstance()
                                             .getLabelGenerator()
                                             .getLabel(el.getExceptionMappings()),
                                          true,
                                          false,
                                          false,
                                          false,
                                          new Dimension(300, 75),
                                          true,
                                          true, false, false);

      columnsToShow = getColumnsToShow("XMLTablePanel", el.getPropertyList());

      XMLPanel propPnl = new XMLTablePanel((InlinePanel) getPanelContainer(),
                                           el.getPropertyList(),
                                           columnsToShow,
                                           el.getPropertyList().toElements(),
                                           JaWEManager.getInstance()
                                              .getLabelGenerator()
                                              .getLabel(el.getPropertyList()),
                                           true,
                                           false,
                                           false,
                                           false,
                                           new Dimension(300, 75),
                                           true,
                                           true, false, false);

      // XMLPanel propPnl= new XMLListPanel(
      // (InlinePanel)getPanelContainer(),
      // el.getPropertyList(),
      // el.getPropertyList().toElements(),
      // JaWEManager.getInstance().getLabelGenerator().getLabel(el.getPropertyList()),
      // true,
      // false,
      // true,
      // true);

      List tgp = new ArrayList();
      tgp.add(excPnl);
      tgp.add(propPnl);
      XMLGroupPanel ltPanel = new XMLGroupPanel(getPanelContainer(),
                                                el,
                                                tgp,
                                                "",
                                                false,
                                                false,
                                                false);
      subpanels.add(txtPCls);
      subpanels.add(txtPExec);

      // subpanels.add(excPnl);
      // subpanels.add(propPnl);
      subpanels.add(ltPanel);
      return new WfMOpenGroupPanel(getPanelContainer(),
                                   el,
                                   subpanels,
                                   getPanelContainer().getLanguageDependentString("ToolAgentKey"),
                                   true,
                                   true,
                                   true);

   }

   public XMLPanel getPanel(Property el) {
      List toShow = new ArrayList(el.toElements());
      String t = getPanelContainer().getLanguageDependentString("PropertyKey");
      WfMOpenGroupPanel gp = new WfMOpenGroupPanel(getPanelContainer(),
                                                   el,
                                                   toShow,
                                                   t,
                                                   true,
                                                   false,
                                                   true);

      FormalParameters fps = ((Application) XMLUtil.getParentElement(Application.class,
                                                                     el)).getApplicationTypes()
         .getFormalParameters();
      XMLPanel prop = new XMLMultiLineTextPanelWithChoiceButton(getPanelContainer(),
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
      gp.addToGroup(prop);
      return gp;
   }

   public XMLPanel getPanel(ExceptionMapping el) {
      List toShow = el.toElements();
      String t = getPanelContainer().getLanguageDependentString("ExceptionMappingKey");
      WfMOpenGroupPanel gp = new WfMOpenGroupPanel(getPanelContainer(),
                                                   el,
                                                   toShow,
                                                   t,
                                                   true,
                                                   false,
                                                   true);
      return gp;
   }

   protected XMLPanel getPanel(Package el, int no, Set hidden) {
      if (no == 5) {
         return null;
      }
      if (no != 1) {
         return super.getPanel(el, no, hidden);
      }
      XMLPanel p = null;

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

      ExtendedAttributes eas = el.getExtendedAttributes();
      ExtendedAttribute ea = eas.getFirstExtendedAttributeForName(WfMOpenConstants.EA_REMOVE_CLOSED_PROCESS);
      if (ea != null) {
         String n = getPanelContainer().getLanguageDependentString(ea.getName() + "Key");
         XMLElement eav = ea.get("Value");

         List choices = new ArrayList();
         choices.add(WfMOpenConstants.EA_REMOVE_CLOSED_PROCESS_VALUE_AUTOMATIC);
         choices.add(WfMOpenConstants.EA_REMOVE_CLOSED_PROCESS_VALUE_MANUAL);
         choices.add(WfMOpenConstants.EA_REMOVE_CLOSED_PROCESS_VALUE_COMPLETED);
         String choosen = eav.toValue();
         if (!choices.contains(choosen)) {
            choices.add(0, choosen);
         }

         XMLPanel pnl = new XMLComboPanel(getPanelContainer(),
                                          eav,
                                          n,
                                          choices,
                                          true,
                                          true,
                                          false,
                                          false,
                                          JaWEManager.getInstance()
                                             .getJaWEController()
                                             .canModifyElement(eav),
                                          true,
                                          false);

         panelElements.add(pnl);
      }

      ea = eas.getFirstExtendedAttributeForName(WfMOpenConstants.EA_STORE_AUDIT_EVENTS);
      if (ea != null) {
         String n = getPanelContainer().getLanguageDependentString(ea.getName() + "Key");
         XMLCheckboxPanel cbp = new XMLCheckboxPanel(getPanelContainer(),
                                                     ea.get("Value"),
                                                     n,
                                                     false,
                                                     JaWEManager.getInstance()
                                                        .getJaWEController()
                                                        .canModifyElement(ea.get("Value")),false);
         panelElements.add(cbp);
      }
      ea = eas.getFirstExtendedAttributeForName(WfMOpenConstants.EA_AUDIT_EVENT_SELECTION);
      if (ea != null) {
         String n = getPanelContainer().getLanguageDependentString(ea.getName() + "Key");
         XMLElement eav = ea.get("Value");

         List choices = new ArrayList();
         choices.add(WfMOpenConstants.EA_AUDIT_EVENT_SELECTION_VALUE_ALL_EVENTS);
         choices.add(WfMOpenConstants.EA_AUDIT_EVENT_SELECTION_VALUE_PROCESS_CLOSED_EVENTS_ONLY);
         choices.add(WfMOpenConstants.EA_AUDIT_EVENT_SELECTION_VALUE_STATE_EVENTS_ONLY);
         choices.add(WfMOpenConstants.EA_AUDIT_EVENT_SELECTION_VALUE_NO_EVENTS);
         String choosen = eav.toValue();
         if (!choices.contains(choosen)) {
            choices.add(0, choosen);
         }

         XMLPanel pnl = new XMLComboPanel(getPanelContainer(),
                                          eav,
                                          n,
                                          choices,
                                          true,
                                          true,
                                          false,
                                          false,
                                          JaWEManager.getInstance()
                                             .getJaWEController()
                                             .canModifyElement(eav),
                                          true,
                                          false);

         panelElements.add(pnl);
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

      return p;
   }

   public XMLPanel getPanel(Split el) {
      XMLGroupPanel gp = (XMLGroupPanel) super.getPanel(el);

      ExtendedAttribute ea = XMLUtil.getActivity(el)
         .getExtendedAttributes()
         .getFirstExtendedAttributeForName(WfMOpenConstants.EA_DEFERRED_CHOICE);
      if (ea != null) {
         XMLCheckboxPanel cbp = new XMLCheckboxPanel(getPanelContainer(),
                                                     ea.get("Value"),
                                                     getPanelContainer().getLanguageDependentString(ea.getName()
                                                                                                    + "Key"),
                                                     false,
                                                     JaWEManager.getInstance()
                                                        .getJaWEController()
                                                        .canModifyElement(ea.get("Value")),false);
         gp.addToGroup(cbp, 0);
      }

      return gp;
   }

   public XMLPanel getPanel(WorkflowProcess el, int no, Set hidden) {
      if (no != 1) {
         return super.getPanel(el, no, hidden);
      }
      XMLPanel p = null;

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

      ExtendedAttributes eas = el.getExtendedAttributes();
      ExtendedAttribute ea = eas.getFirstExtendedAttributeForName(WfMOpenConstants.EA_REMOVE_CLOSED_PROCESS);
      if (ea != null) {
         String n = getPanelContainer().getLanguageDependentString(ea.getName() + "Key");
         XMLElement eav = ea.get("Value");

         List choices = new ArrayList();
         choices.add(WfMOpenConstants.EA_REMOVE_CLOSED_PROCESS_VALUE_AUTOMATIC);
         choices.add(WfMOpenConstants.EA_REMOVE_CLOSED_PROCESS_VALUE_MANUAL);
         choices.add(WfMOpenConstants.EA_REMOVE_CLOSED_PROCESS_VALUE_COMPLETED);
         String choosen = eav.toValue();
         if (!choices.contains(choosen)) {
            choices.add(0, choosen);
         }

         XMLPanel pnl = new XMLComboPanel(getPanelContainer(),
                                          eav,
                                          n,
                                          choices,
                                          true,
                                          true,
                                          false,
                                          false,
                                          JaWEManager.getInstance()
                                             .getJaWEController()
                                             .canModifyElement(eav),
                                          true,
                                          false);

         panelElements.add(pnl);
      }

      ea = eas.getFirstExtendedAttributeForName(WfMOpenConstants.EA_STORE_AUDIT_EVENTS);
      if (ea != null) {
         XMLCheckboxPanel cbp = new XMLCheckboxPanel(getPanelContainer(),
                                                     ea.get("Value"),
                                                     getPanelContainer().getLanguageDependentString(ea.getName()
                                                                                                    + "Key"),
                                                     false,
                                                     JaWEManager.getInstance()
                                                        .getJaWEController()
                                                        .canModifyElement(ea.get("Value")),false);
         panelElements.add(cbp);
      }
      ea = eas.getFirstExtendedAttributeForName(WfMOpenConstants.EA_AUDIT_EVENT_SELECTION);
      if (ea != null) {
         String n = getPanelContainer().getLanguageDependentString(ea.getName() + "Key");
         XMLElement eav = ea.get("Value");

         List choices = new ArrayList();
         choices.add(WfMOpenConstants.EA_AUDIT_EVENT_SELECTION_VALUE_ALL_EVENTS);
         choices.add(WfMOpenConstants.EA_AUDIT_EVENT_SELECTION_VALUE_PROCESS_CLOSED_EVENTS_ONLY);
         choices.add(WfMOpenConstants.EA_AUDIT_EVENT_SELECTION_VALUE_STATE_EVENTS_ONLY);
         choices.add(WfMOpenConstants.EA_AUDIT_EVENT_SELECTION_VALUE_NO_EVENTS);
         String choosen = eav.toValue();
         if (!choices.contains(choosen)) {
            choices.add(0, choosen);
         }

         XMLPanel pnl = new XMLComboPanel(getPanelContainer(),
                                          eav,
                                          n,
                                          choices,
                                          true,
                                          true,
                                          false,
                                          true,
                                          JaWEManager.getInstance()
                                             .getJaWEController()
                                             .canModifyElement(eav),
                                          true,
                                          false);

         panelElements.add(pnl);
      }

      ea = eas.getFirstExtendedAttributeForName(WfMOpenConstants.EA_DEBUG);
      if (ea != null) {
         XMLCheckboxPanel cbp = new XMLCheckboxPanel(getPanelContainer(),
                                                     ea.get("Value"),
                                                     getPanelContainer().getLanguageDependentString(ea.getName()
                                                                                                    + "Key"),
                                                     false,
                                                     JaWEManager.getInstance()
                                                        .getJaWEController()
                                                        .canModifyElement(ea.get("Value")),false);
         panelElements.add(cbp);
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

      return p;
   }

   public XMLPanel getPanel(XMLAttribute el) {
      if (el.getParent() instanceof Script) {
         if (el.toName().equals("Type")) {
            List choices = new ArrayList();
            choices.add(WfMOpenConstants.SCRIPT_VALUE_JAVASCRIPT);
            choices.add(WfMOpenConstants.SCRIPT_VALUE_ECMASCRIPT);
            String choosen = el.toValue();
            if (!choices.contains(choosen)) {
               choices.add(0, choosen);
            }

            return new XMLComboPanel(getPanelContainer(),
                                     el,
                                     choices,
                                     false,
                                     true,
                                     false,
                                     false,
                                     JaWEManager.getInstance()
                                        .getJaWEController()
                                        .canModifyElement(el));
         } else if (el.toName().equals("Version")) {
            List choices = new ArrayList();
            choices.add(WfMOpenConstants.JAVASCRIPT_VERSION_1_5_VALUE);
            choices.add(WfMOpenConstants.ECMASCRIPT_VERSION_3rd_Edition_VALUE);
            String choosen = el.toValue();
            if (!choices.contains(choosen)) {
               choices.add(0, choosen);
            }

            return new XMLComboPanel(getPanelContainer(),
                                     el,
                                     choices,
                                     false,
                                     true,
                                     false,
                                     false,
                                     JaWEManager.getInstance()
                                        .getJaWEController()
                                        .canModifyElement(el));
         }

      } else if (el.getParent() instanceof ImplementationExtendedAttribute
                 && el.toName().equals("Class")) {
         List choices = new ArrayList();
         choices.add("de.danet.an.workflow.tools.JellyTool");
         choices.add("de.danet.an.workflow.tools.MailTool");
         choices.add("de.danet.an.workflow.tools.MBeanInvoker");
         choices.add("de.danet.an.workflow.tools.XSLTTool");
         choices.add("de.danet.an.workflow.tools.chabacc.Receiver");
         choices.add("de.danet.an.workflow.tools.chabacc.Sender");
         choices.add("de.danet.an.workflow.tools.rhino.JSExecutor");
         choices.add("de.danet.an.workflow.tools.rhino.JSExecutor2");
         choices.add("de.danet.an.workflow.tools.scarab.AddComment");
         choices.add("de.danet.an.workflow.tools.scarab.AssignIssue");
         choices.add("de.danet.an.workflow.tools.scarab.CopyIssue");
         choices.add("de.danet.an.workflow.tools.scarab.GetArchiveEmail");
         choices.add("de.danet.an.workflow.tools.scarab.GetAssignedUser");
         choices.add("de.danet.an.workflow.tools.scarab.GetAttributeValues");
         choices.add("de.danet.an.workflow.tools.scarab.GetDependencyCopiedFrom");
         choices.add("de.danet.an.workflow.tools.scarab.GetEmail");
         choices.add("de.danet.an.workflow.tools.scarab.GetIssueType");
         choices.add("de.danet.an.workflow.tools.scarab.GetLastModification");
         choices.add("de.danet.an.workflow.tools.scarab.GetModule");
         choices.add("de.danet.an.workflow.tools.scarab.SetAttribute");
         choices.add("de.danet.an.workflow.tools.scarab.SetDependency");
         choices.add("de.danet.an.workflow.tools.scarab.SetRole");
         choices.add("de.danet.an.workflow.tools.soap.GenericSOAPClient");
         choices.add("de.danet.an.workflow.tools.soapclient.SOAPClient");
         choices.add("de.danet.an.workflow.tools.timing.TimerCanceler");
         choices.add("de.danet.an.workflow.tools.timing.TimerCreator");
         choices.add("de.danet.an.workflow.tools.timing.WaitTool");
         choices.add("de.danet.an.workflow.tools.webclient.BrowserTool");

         String choosen = el.toValue();
         if (!choices.contains(choosen)) {
            choices.add(0, choosen);
         }

         return new XMLComboPanel(getPanelContainer(),
                                  el,
                                  choices,
                                  false,
                                  true,
                                  false,
                                  true,
                                  JaWEManager.getInstance()
                                     .getJaWEController()
                                     .canModifyElement(el));
      } else if (el.getParent() instanceof ExceptionMapping
                 && el.toName().equals("ProcessException")) {
         List choices = new ArrayList();
         choices.add("ConnectException");
         choices.add("Error");
         choices.add("MappedException");

         String choosen = el.toValue();
         if (!choices.contains(choosen)) {
            choices.add(0, choosen);
         }

         return new XMLComboPanel(getPanelContainer(),
                                  el,
                                  choices,
                                  false,
                                  true,
                                  false,
                                  true,
                                  JaWEManager.getInstance()
                                     .getJaWEController()
                                     .canModifyElement(el));
      }

      return super.getPanel(el);
   }

}
