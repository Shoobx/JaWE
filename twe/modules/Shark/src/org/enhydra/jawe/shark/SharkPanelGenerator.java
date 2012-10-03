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

package org.enhydra.jawe.shark;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.Box;

import org.enhydra.jawe.JaWEConstants;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.base.panel.InlinePanel;
import org.enhydra.jawe.base.panel.StandardPanelGenerator;
import org.enhydra.jawe.base.panel.panels.XMLCheckboxPanel;
import org.enhydra.jawe.base.panel.panels.XMLComboPanel;
import org.enhydra.jawe.base.panel.panels.XMLDataTypesPanel;
import org.enhydra.jawe.base.panel.panels.XMLGroupPanel;
import org.enhydra.jawe.base.panel.panels.XMLGroupPanelGL;
import org.enhydra.jawe.base.panel.panels.XMLListPanel;
import org.enhydra.jawe.base.panel.panels.XMLMultiLineTextPanelWithOptionalChoiceButtons;
import org.enhydra.jawe.base.panel.panels.XMLPanel;
import org.enhydra.jawe.base.panel.panels.XMLTextPanel;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.Application;
import org.enhydra.jxpdl.elements.BasicType;
import org.enhydra.jxpdl.elements.DataField;
import org.enhydra.jxpdl.elements.DataFields;
import org.enhydra.jxpdl.elements.DataTypes;
import org.enhydra.jxpdl.elements.DeadlineDuration;
import org.enhydra.jxpdl.elements.ExpressionType;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;
import org.enhydra.jxpdl.elements.Package;
import org.enhydra.jxpdl.elements.Script;
import org.enhydra.jxpdl.elements.WorkflowProcess;

/**
 * @author Sasa Bojanic
 */
public class SharkPanelGenerator extends StandardPanelGenerator {

   public static final String APP_DEF_CHOICES_FILE = "shkappdefchoices.properties";

   public static final String CONFIG_STRING_CHOICES_FILE = "shkconfigstringchoices.properties";

   protected Properties appDefChoices = new Properties();

   protected List commonInfoChoices = new ArrayList();

   protected List configStringChoices = new ArrayList();

   public SharkPanelGenerator() throws Exception {
      super();
      try {
         String cch = System.getProperty(JaWEConstants.JAWE_CURRENT_CONFIG_HOME);
         Utils.manageProperties(appDefChoices, cch, APP_DEF_CHOICES_FILE);
      } catch (Exception ex) {
      }
      try {
         Properties csc = new Properties();
         String cch = System.getProperty(JaWEConstants.JAWE_CURRENT_CONFIG_HOME);
         Utils.manageProperties(csc, cch, CONFIG_STRING_CHOICES_FILE);
         configStringChoices.addAll(csc.keySet());
      } catch (Exception ex) {
      }

      commonInfoChoices.add("shark_version");
      commonInfoChoices.add("shark_release");
      commonInfoChoices.add("shark_buildid");
      commonInfoChoices.add("user");
      commonInfoChoices.add("time");
      commonInfoChoices.add("xpdl_id");
      commonInfoChoices.add("xpdl_version");
      commonInfoChoices.add("xpdl_timestamp");
      commonInfoChoices.add("process_definition_id");
      commonInfoChoices.add("process_definition_name");
      commonInfoChoices.add("process_id");
      commonInfoChoices.add("process_name");
      commonInfoChoices.add("process_description");
      commonInfoChoices.add("process_priority");
      commonInfoChoices.add("process_limit");
      commonInfoChoices.add("activity_definition_id");
      commonInfoChoices.add("activity_definition_name");
      commonInfoChoices.add("activity_id");
      commonInfoChoices.add("activity_name");
      commonInfoChoices.add("activity_description");
      commonInfoChoices.add("activity_priority");
      commonInfoChoices.add("activity_limit");
   }

   protected XMLPanel getPanel(EmailConfigurationElement el) {
      boolean enableEditing = JaWEManager.getInstance()
         .getJaWEController()
         .canModifyElement(el);

      WorkflowProcess wp = XMLUtil.getWorkflowProcess(el);
      List choices = new ArrayList(XMLUtil.getPossibleVariables(wp).values());
      // DataField df = new DataField(wp.getDataFields());
      // df.setId(" ");
      // choices.add(0, df);
      XMLPanel mode = new XMLCheckboxPanel(getPanelContainer(),
                                           el.getModeAttribute(),
                                           null,
                                           false,
                                           enableEditing,
                                           false);
      XMLPanel executionMode = new XMLCheckboxPanel(getPanelContainer(),
                                                    el.getExecutionModeAttribute(),
                                                    null,
                                                    false,
                                                    enableEditing,
                                                    false);
      XMLPanel groupEmailOnly = new XMLCheckboxPanel(getPanelContainer(),
                                                     el.getGroupEmailOnlyAttribute(),
                                                     null,
                                                     false,
                                                     enableEditing,
                                                     false);
      List cbp = new ArrayList();
      cbp.add(mode);
      cbp.add(executionMode);
      cbp.add(groupEmailOnly);
      XMLGroupPanel cbPanel = new XMLGroupPanel(getPanelContainer(),
                                                el,
                                                cbp,
                                                getPanelContainer().getLanguageDependentString("SMTPEntries"),
                                                false,
                                                false,
                                                false);

      List variableChoices = getSMTPExpressionChoices(XMLUtil.getWorkflowProcess(el));
      List againVc = getSMTPExpressionChoices(XMLUtil.getWorkflowProcess(el));
      List moreVc = new ArrayList(commonInfoChoices);
      List<List> mc = new ArrayList<List>();
      mc.add(configStringChoices);
      mc.add(commonInfoChoices);
      mc.add(variableChoices);
      XMLPanel subject = new XMLMultiLineTextPanelForSMTPEAs(getPanelContainer(),
                                                             el.getSubjectAttribute(),
                                                             true,
                                                             XMLMultiLineTextPanelWithOptionalChoiceButtons.SIZE_SMALL,
                                                             false,
                                                             enableEditing,
                                                             mc);
      XMLPanel content = new XMLMultiLineTextPanelForSMTPEAs(getPanelContainer(),
                                                             el.getContentAttribute(),
                                                             true,
                                                             XMLMultiLineTextPanelWithOptionalChoiceButtons.SIZE_EXTRA_LARGE,
                                                             false,
                                                             enableEditing,
                                                             mc);
      XMLPanel attachments = new XMLListPanel((InlinePanel) getPanelContainer(),
                                              el.getAttachmentsElement(),
                                              el.getAttachmentsElement().toElements(),
                                              getPanelContainer().getLanguageDependentString(el.getAttachmentsElement()
                                                 .toName()
                                                                                             + "Key"),
                                              true,
                                              false,
                                              enableEditing,
                                              true,
                                              true,
                                              true);

      XMLPanel attachmentNames = new XMLListPanel((InlinePanel) getPanelContainer(),
                                                  el.getAttachmentNamesElement(),
                                                  el.getAttachmentNamesElement()
                                                     .toElements(),
                                                  getPanelContainer().getLanguageDependentString(el.getAttachmentNamesElement()
                                                     .toName()
                                                                                                 + "Key"),
                                                  true,
                                                  false,
                                                  enableEditing,
                                                  true,
                                                  true,
                                                  true);
      List alp = new ArrayList();
      alp.add(attachments);
      alp.add(attachmentNames);
      XMLGroupPanel attachPanel = new XMLGroupPanel(getPanelContainer(),
                                                    el,
                                                    alp,
                                                    getPanelContainer().getLanguageDependentString("SMTPEntries"),
                                                    false,
                                                    false,
                                                    false);

      XMLPanel dmAttachments = new XMLListPanel((InlinePanel) getPanelContainer(),
                                                el.getDMAttachmentsElement(),
                                                el.getDMAttachmentsElement().toElements(),
                                                getPanelContainer().getLanguageDependentString(el.getDMAttachmentsElement()
                                                   .toName()
                                                                                               + "Key"),
                                                true,
                                                false,
                                                enableEditing,
                                                true,
                                                true,
                                                true);

      List tgp = new ArrayList();

      // tgp.add(mode);
      // tgp.add(executionMode);
      tgp.add(cbPanel);
      // tgp.add(groupEmailOnly);
      tgp.add(subject);
      tgp.add(content);
      // tgp.add(attachments);
      // tgp.add(attachmentNames);
      tgp.add(attachPanel);
      tgp.add(dmAttachments);

      XMLGroupPanel ltPanel = new SharkModeGroupPanel(getPanelContainer(),
                                                      el,
                                                      tgp,
                                                      getPanelContainer().getLanguageDependentString("SMTPEntries"),
                                                      true,
                                                      false,
                                                      false);
      return ltPanel;
   }

   public XMLPanel getPanel(WfVariable el) {
      SharkModeGroupPanel gp = new SharkModeGroupPanel(getPanelContainer(),
                                                       el,
                                                       el.toElements(),
                                                       getPanelContainer().getLanguageDependentString(el.toName()
                                                                                                      + "Key"),
                                                       true,
                                                       false,
                                                       true);
      return gp;
   }

   protected XMLPanel getPanel(Activity el, int no, Set hidden) {
      if (no >= 3)
         no++;
      if (!(el.getActivityType() == XPDLConstants.ACTIVITY_TYPE_TASK_APPLICATION)) {
         hidden.add(el.get("StartMode"));
         hidden.add(el.get("FinishMode"));
         if (!(el.getActivityType() == XPDLConstants.ACTIVITY_TYPE_NO)) {
            hidden.add(el.get("Priority"));
            hidden.add(el.get("Limit"));
         }
      }
      if (el.getActivityType() == XPDLConstants.ACTIVITY_TYPE_ROUTE
          || el.getActivityType() == XPDLConstants.ACTIVITY_TYPE_EVENT_END
          || el.getActivityType() == XPDLConstants.ACTIVITY_TYPE_EVENT_START) {
         hidden.add(el.get("Deadlines"));
      }
      if (!(el.getActivityType() == XPDLConstants.ACTIVITY_TYPE_NO || (el.getActivityType() == XPDLConstants.ACTIVITY_TYPE_TASK_APPLICATION && (el.getStartMode()
         .equals(XPDLConstants.ACTIVITY_MODE_MANUAL) || el.getFinishMode()
         .equals(XPDLConstants.ACTIVITY_MODE_MANUAL))))) {
         return super.getPanel(el, no, hidden);
      }
      XMLPanel p = null;
      ExtendedAttributes eas = el.getExtendedAttributes();
      switch (no) {
         case 1:
            XMLGroupPanel gp = (XMLGroupPanel) super.getPanel(el, no, hidden);

            ExtendedAttribute ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_XFORMS_FILE);
            if (ea != null) {
               XMLPanel pnl = new XMLTextPanel(getPanelContainer(),
                                               ea.get("Value"),
                                               getPanelContainer().getLanguageDependentString(ea.getName()
                                                                                              + "Key"),
                                               false,
                                               false,
                                               JaWEManager.getInstance()
                                                  .getJaWEController()
                                                  .canModifyElement(ea));
               gp.addToGroup(pnl);
            }
            ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_MAX_ASSIGNMENTS);
            if (ea != null) {
               XMLPanel pnl = new XMLTextPanel(getPanelContainer(),
                                               ea.get("Value"),
                                               getPanelContainer().getLanguageDependentString(ea.getName()
                                                                                              + "Key"),
                                               false,
                                               false,
                                               JaWEManager.getInstance()
                                                  .getJaWEController()
                                                  .canModifyElement(ea));
               gp.addToGroup(pnl);
            }
            ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_WORKLOAD_FACTOR);
            if (ea != null) {
               XMLPanel pnl = new XMLTextPanel(getPanelContainer(),
                                               ea.get("Value"),
                                               getPanelContainer().getLanguageDependentString(ea.getName()
                                                                                              + "Key"),
                                               false,
                                               false,
                                               JaWEManager.getInstance()
                                                  .getJaWEController()
                                                  .canModifyElement(ea));
               gp.addToGroup(pnl);
            }
            p = gp;
            break;
         case 5:
            ExtendedAttributesWrapper eaw = new ExtendedAttributesWrapper(eas);
            p = generateStandardTablePanel(eaw, false, false);
            p = new ExtAttrWrapperTablePanel((InlinePanel) getPanelContainer(),
                                             eaw,
                                             JaWEManager.getInstance()
                                                .getLabelGenerator()
                                                .getLabel(eaw)
                                                   + ", "
                                                   + eaw.size()
                                                   + " "
                                                   + getPanelContainer().getLanguageDependentString("ElementsKey"),
                                             true,
                                             false,
                                             false,
                                             true,
                                             true,
                                             true,
                                             true);
            break;
         case 6:
            try {
               p = getPanel(new EmailConfigurationElement(eas));
            } catch (Exception ex) {
               ex.printStackTrace();
            }
            break;
         default:
            if (no < 5) {
               p = super.getPanel(el, no, hidden);
            } else {
               throw new RuntimeException();
            }
      }
      return p;
   }

   public XMLPanel getPanel(Application el) {
      Set hidden = getHiddenElements("XMLGroupPanel", el);
      List subpanels = new ArrayList();
      List groupsToShow = new ArrayList();
      if (!hidden.contains(el.get("Id"))) {
         subpanels.add(generateStandardTextPanel(el.get("Id"), true));
      }
      if (!hidden.contains(el.get("Name"))) {
         subpanels.add(generateStandardTextPanel(el.get("Name"), true));
      }
      if (subpanels.size() > 0) {
         groupsToShow.add(new XMLGroupPanel(getPanelContainer(),
                                            el,
                                            subpanels,
                                            "",
                                            false,
                                            false,
                                            true,
                                            true));
      }

      if (!hidden.contains(el.get("Description"))) {
         groupsToShow.add(getPanel(el.get("Description")));
      }
      if (!hidden.contains(el.getApplicationTypes())) {
         groupsToShow.add(getPanel(el.getApplicationTypes().getFormalParameters()));
      }

      ExtendedAttributes eas = el.getExtendedAttributes();
      ExtendedAttribute ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_TOOL_AGENT_CLASS);
      if (ea != null) {
         XMLElement eav = ea.get("Value");

         List choices = new ArrayList(appDefChoices.keySet());
         String choosen = eav.toValue();
         if (!choices.contains(choosen)) {
            choices.add(0, choosen);
         }
         XMLPanel pnl = new XMLComboPanel(getPanelContainer(),
                                          eav,
                                          getPanelContainer().getLanguageDependentString("ToolAgentClassKey"),
                                          choices,
                                          true,
                                          true,
                                          false,
                                          true,
                                          JaWEManager.getInstance()
                                             .getJaWEController()
                                             .canModifyElement(eav),
                                          true,
                                          true);

         groupsToShow.add(pnl);
      }
      ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_APP_NAME);
      if (ea != null) {
         XMLPanel pnl = new XMLTextPanel(getPanelContainer(),
                                         ea.get("Value"),
                                         getPanelContainer().getLanguageDependentString("ApplicationNameKey"),
                                         false,
                                         false,
                                         JaWEManager.getInstance()
                                            .getJaWEController()
                                            .canModifyElement(ea));
         groupsToShow.add(pnl);
      }
      ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_APP_MODE);
      if (ea != null) {
         XMLPanel pnl = new XMLTextPanel(getPanelContainer(),
                                         ea.get("Value"),
                                         getPanelContainer().getLanguageDependentString("ApplicationModeKey"),
                                         false,
                                         false,
                                         JaWEManager.getInstance()
                                            .getJaWEController()
                                            .canModifyElement(ea));
         groupsToShow.add(pnl);
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

   public XMLPanel getPanel(BasicType el) {
      XMLAttribute bttype = (XMLAttribute) el.get("Type");
      List origChoices = bttype.getChoices();
      List choices = new ArrayList(origChoices);
      String chosen = bttype.toValue();
      if (!chosen.equals(XPDLConstants.BASIC_TYPE_PERFORMER)) {
         choices.remove(XPDLConstants.BASIC_TYPE_PERFORMER);
      }
      if (!chosen.equals(XPDLConstants.BASIC_TYPE_REFERENCE)) {
         choices.remove(XPDLConstants.BASIC_TYPE_REFERENCE);
      }
      return new XMLComboPanel(getPanelContainer(),
                               el.get("Type"),
                               choices,
                               true,
                               true,
                               false,
                               false,
                               JaWEManager.getInstance()
                                  .getJaWEController()
                                  .canModifyElement(el));
   }

   public XMLPanel getPanel(DataField el) {
      XMLGroupPanel gp = (XMLGroupPanel) super.getPanel(el);
      ExtendedAttributes eas = el.getExtendedAttributes();
      ExtendedAttribute ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_MIN_LENGTH);
      if (ea != null) {
         XMLPanel pnl = new XMLTextPanel(getPanelContainer(),
                                         ea.get("Value"),
                                         getPanelContainer().getLanguageDependentString(ea.getName()
                                                                                        + "Key"),
                                         false,
                                         false,
                                         JaWEManager.getInstance()
                                            .getJaWEController()
                                            .canModifyElement(ea));
         int insertAt = gp.getPanelPositionForElement(el.get("Length"));
         if (insertAt >= 0) {
            gp.addToGroup(pnl, insertAt);
         } else {
            gp.addToGroup(pnl);
         }
      }
      ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_IS_MANDATORY);
      if (ea != null) {
         String n = getPanelContainer().getLanguageDependentString(ea.getName() + "Key");
         XMLCheckboxPanel cbp = new XMLCheckboxPanel(getPanelContainer(),
                                                     ea.get("Value"),
                                                     n,
                                                     false,
                                                     JaWEManager.getInstance()
                                                        .getJaWEController()
                                                        .canModifyElement(ea.get("Value")),
                                                     false);
         int insertAt = gp.getPanelPositionForElement(el.get("Length"));
         if (insertAt >= 0 && gp.getComponentCount() >= ++insertAt) {
            gp.addToGroup(cbp, insertAt);
         } else {
            gp.addToGroup(cbp);
         }
      }
      ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_TRANSIENT);
      if (ea != null) {
         String n = getPanelContainer().getLanguageDependentString(ea.getName() + "Key");
         XMLCheckboxPanel cbp = new XMLCheckboxPanel(getPanelContainer(),
                                                     ea.get("Value"),
                                                     n,
                                                     false,
                                                     JaWEManager.getInstance()
                                                        .getJaWEController()
                                                        .canModifyElement(ea.get("Value")),
                                                     false);
         int insertAt = gp.getPanelPositionForElement(el.get("Length"));
         if (insertAt >= 0 && gp.getComponentCount() >= (insertAt += 2)) {
            gp.addToGroup(cbp, insertAt);
         } else {
            gp.addToGroup(cbp);
         }
      }

      ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_DYNAMICSCRIPT);
      if (ea != null) {
         String n = getPanelContainer().getLanguageDependentString(ea.getName() + "Key");
         XMLCheckboxPanel cbp = new XMLCheckboxPanel(getPanelContainer(),
                                                     ea.get("Value"),
                                                     n,
                                                     false,
                                                     JaWEManager.getInstance()
                                                        .getJaWEController()
                                                        .canModifyElement(ea.get("Value")),
                                                     false);
         int insertAt = gp.getPanelPositionForElement(el.get("Length"));
         if (insertAt >= 0 && gp.getComponentCount() >= (insertAt += 2)) {
            gp.addToGroup(cbp, insertAt);
         } else {
            gp.addToGroup(cbp);
         }
      }

      return gp;
   }

   public XMLPanel getPanel(DataTypes el) {
      List choices = new ArrayList();
      choices.add(el.getBasicType());
      choices.add(el.getDeclaredType());
      choices.add(el.getSchemaType());
      choices.add(el.getExternalReference());
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

   public XMLPanel getPanel(DeadlineDuration el) {
      List cl = getExpressionChoices(el);
      DataFields dfs = XMLUtil.getWorkflowProcess(el).getDataFields();
      DataField df = new DataField(dfs);
      df.setId(SharkConstants.PROCESS_STARTED_TIME);
      cl.add(df);
      df = new DataField(dfs);
      df.setId(SharkConstants.ACTIVITY_ACTIVATED_TIME);
      cl.add(df);
      df = new DataField(dfs);
      df.setId(SharkConstants.ACTIVITY_ACCEPTED_TIME);
      cl.add(df);

      List<List> mc = new ArrayList<List>();
      mc.add(cl);

      return new XMLMultiLineTextPanelWithOptionalChoiceButtons(getPanelContainer(),
                                                                el,
                                                                el.toName(),
                                                                false,
                                                                true,
                                                                XMLMultiLineTextPanelWithOptionalChoiceButtons.SIZE_LARGE,
                                                                false,
                                                                mc,
                                                                JaWEManager.getInstance()
                                                                   .getJaWEController()
                                                                   .canModifyElement(el));
   }

   public XMLPanel getPanel(ExtendedAttribute el) {
      if (el.getParent().getParent() instanceof Activity
          && (el.getName().equals(SharkConstants.VTP_UPDATE) || el.getName()
             .equals(SharkConstants.VTP_VIEW))) {
//         boolean roVar = false;
//         String vVal = el.getVValue();
         Map vars = XMLUtil.getPossibleVariables(XMLUtil.getWorkflowProcess(el));
         List chs = null;
//         if (vars.containsKey(vVal)) {
//            roVar = true;
//         } else {
            chs = getPossibleVariableChoices(vars, (ExtendedAttributes) el.getParent());
//         }
         return new ExtAttribPanel(getPanelContainer(),
                                   el,
                                   chs,
                                   false,
                                   false,
                                   JaWEManager.getInstance()
                                      .getJaWEController()
                                      .canModifyElement(el),
                                   true);
      }
      return super.getPanel(el);
   }

   protected XMLPanel getPanel(Package el, int no, Set hidden) {
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

      List ealist = new ArrayList();
      ExtendedAttributes eas = el.getExtendedAttributes();
      ExtendedAttribute ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_ALLOW_UNDEFINED_VARIABLES);
      if (ea != null) {
         String n = getPanelContainer().getLanguageDependentString(ea.getName() + "Key");
         XMLCheckboxPanel cbp = new XMLCheckboxPanel(getPanelContainer(),
                                                     ea.get("Value"),
                                                     n,
                                                     false,
                                                     JaWEManager.getInstance()
                                                        .getJaWEController()
                                                        .canModifyElement(ea.get("Value")),
                                                     false);
         ealist.add(cbp);
      }
      ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_DYNAMIC_VARIABLE_HANDLING);
      if (ea != null) {
         String n = getPanelContainer().getLanguageDependentString(ea.getName() + "Key");
         XMLCheckboxPanel cbp = new XMLCheckboxPanel(getPanelContainer(),
                                                     ea.get("Value"),
                                                     n,
                                                     false,
                                                     JaWEManager.getInstance()
                                                        .getJaWEController()
                                                        .canModifyElement(ea.get("Value")),
                                                     false);
         ealist.add(cbp);
      }
      ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_CHOOSE_NEXT_PERFORMER);
      if (ea != null) {
         String n = getPanelContainer().getLanguageDependentString(ea.getName() + "Key");
         XMLCheckboxPanel cbp = new XMLCheckboxPanel(getPanelContainer(),
                                                     ea.get("Value"),
                                                     n,
                                                     false,
                                                     JaWEManager.getInstance()
                                                        .getJaWEController()
                                                        .canModifyElement(ea.get("Value")),
                                                     false);
         ealist.add(cbp);
      }
      ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_USE_PROCESS_CONTEXT_ONLY);
      if (ea != null) {
         String n = getPanelContainer().getLanguageDependentString(ea.getName() + "Key");
         XMLCheckboxPanel cbp = new XMLCheckboxPanel(getPanelContainer(),
                                                     ea.get("Value"),
                                                     n,
                                                     false,
                                                     JaWEManager.getInstance()
                                                        .getJaWEController()
                                                        .canModifyElement(ea.get("Value")),
                                                     false);
         ealist.add(cbp);
      }
      ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_CREATE_ASSIGNMENTS);
      if (ea != null) {
         String n = getPanelContainer().getLanguageDependentString(ea.getName() + "Key");
         XMLCheckboxPanel cbp = new XMLCheckboxPanel(getPanelContainer(),
                                                     ea.get("Value"),
                                                     n,
                                                     false,
                                                     JaWEManager.getInstance()
                                                        .getJaWEController()
                                                        .canModifyElement(ea.get("Value")),
                                                     false);
         ealist.add(cbp);
      }
      ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_TRANSIENT);
      if (ea != null) {
         String n = getPanelContainer().getLanguageDependentString(ea.getName() + "Key");
         XMLCheckboxPanel cbp = new XMLCheckboxPanel(getPanelContainer(),
                                                     ea.get("Value"),
                                                     n,
                                                     false,
                                                     JaWEManager.getInstance()
                                                        .getJaWEController()
                                                        .canModifyElement(ea.get("Value")),
                                                     false);
         ealist.add(cbp);
      }
      ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_DELETE_FINISHED);
      if (ea != null) {
         String n = getPanelContainer().getLanguageDependentString(ea.getName() + "Key");
         XMLCheckboxPanel cbp = new XMLCheckboxPanel(getPanelContainer(),
                                                     ea.get("Value"),
                                                     n,
                                                     false,
                                                     JaWEManager.getInstance()
                                                        .getJaWEController()
                                                        .canModifyElement(ea.get("Value")),
                                                     false);
         ealist.add(cbp);
      }

      ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_CHECK_FOR_FIRST_ACTIVITY);
      if (ea != null) {
         String n = getPanelContainer().getLanguageDependentString(ea.getName() + "Key");
         XMLCheckboxPanel cbp = new XMLCheckboxPanel(getPanelContainer(),
                                                     ea.get("Value"),
                                                     n,
                                                     false,
                                                     JaWEManager.getInstance()
                                                        .getJaWEController()
                                                        .canModifyElement(ea.get("Value")),
                                                     false);
         ealist.add(cbp);
      }

      ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_CHECK_FOR_CONTINUATION);
      if (ea != null) {
         String n = getPanelContainer().getLanguageDependentString(ea.getName() + "Key");
         XMLCheckboxPanel cbp = new XMLCheckboxPanel(getPanelContainer(),
                                                     ea.get("Value"),
                                                     n,
                                                     false,
                                                     JaWEManager.getInstance()
                                                        .getJaWEController()
                                                        .canModifyElement(ea.get("Value")),
                                                     false);
         ealist.add(cbp);
      }

      if (ealist.size() > 3) {
         for (int i = 0; i < ealist.size(); i += 2) {
            List subpanels = new ArrayList();
            subpanels.add(ealist.get(i));
            subpanels.add(Box.createHorizontalGlue());
            if ((i + 1) < ealist.size()) {
               subpanels.add(ealist.get(i + 1));
            }
            panelElements.add(new XMLGroupPanelGL(getPanelContainer(),
                                                  el,
                                                  subpanels,
                                                  "",
                                                  false,
                                                  false,
                                                  true));
         }
      } else {
         panelElements.addAll(ealist);
      }

      ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_REDIRECT_AFTER_PROCESS_END);
      if (ea != null) {
         XMLPanel pnl = new XMLTextPanel(getPanelContainer(),
                                         ea.get("Value"),
                                         getPanelContainer().getLanguageDependentString(ea.getName()
                                                                                        + "Key"),
                                         false,
                                         false,
                                         JaWEManager.getInstance()
                                            .getJaWEController()
                                            .canModifyElement(ea));
         panelElements.add(pnl);
      }
      ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_MAX_ASSIGNMENTS);
      if (ea != null) {
         XMLPanel pnl = new XMLTextPanel(getPanelContainer(),
                                         ea.get("Value"),
                                         getPanelContainer().getLanguageDependentString(ea.getName()
                                                                                        + "Key"),
                                         false,
                                         false,
                                         JaWEManager.getInstance()
                                            .getJaWEController()
                                            .canModifyElement(ea));
         panelElements.add(pnl);
      }
      ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_WORKLOAD_FACTOR);
      if (ea != null) {
         XMLPanel pnl = new XMLTextPanel(getPanelContainer(),
                                         ea.get("Value"),
                                         getPanelContainer().getLanguageDependentString(ea.getName()
                                                                                        + "Key"),
                                         false,
                                         false,
                                         JaWEManager.getInstance()
                                            .getJaWEController()
                                            .canModifyElement(ea));
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

      List ealist = new ArrayList();
      ExtendedAttributes eas = el.getExtendedAttributes();
      ExtendedAttribute ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_ALLOW_UNDEFINED_VARIABLES);
      if (ea != null) {
         String n = getPanelContainer().getLanguageDependentString(ea.getName() + "Key");
         XMLCheckboxPanel cbp = new XMLCheckboxPanel(getPanelContainer(),
                                                     ea.get("Value"),
                                                     n,
                                                     false,
                                                     JaWEManager.getInstance()
                                                        .getJaWEController()
                                                        .canModifyElement(ea.get("Value")),
                                                     false);
         ealist.add(cbp);
      }
      ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_DYNAMIC_VARIABLE_HANDLING);
      if (ea != null) {
         String n = getPanelContainer().getLanguageDependentString(ea.getName() + "Key");
         XMLCheckboxPanel cbp = new XMLCheckboxPanel(getPanelContainer(),
                                                     ea.get("Value"),
                                                     n,
                                                     false,
                                                     JaWEManager.getInstance()
                                                        .getJaWEController()
                                                        .canModifyElement(ea.get("Value")),
                                                     false);
         ealist.add(cbp);
      }
      ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_CHOOSE_NEXT_PERFORMER);
      if (ea != null) {
         String n = getPanelContainer().getLanguageDependentString(ea.getName() + "Key");
         XMLCheckboxPanel cbp = new XMLCheckboxPanel(getPanelContainer(),
                                                     ea.get("Value"),
                                                     n,
                                                     false,
                                                     JaWEManager.getInstance()
                                                        .getJaWEController()
                                                        .canModifyElement(ea.get("Value")),
                                                     false);
         ealist.add(cbp);
      }
      ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_USE_PROCESS_CONTEXT_ONLY);
      if (ea != null) {
         String n = getPanelContainer().getLanguageDependentString(ea.getName() + "Key");
         XMLCheckboxPanel cbp = new XMLCheckboxPanel(getPanelContainer(),
                                                     ea.get("Value"),
                                                     n,
                                                     false,
                                                     JaWEManager.getInstance()
                                                        .getJaWEController()
                                                        .canModifyElement(ea.get("Value")),
                                                     false);
         ealist.add(cbp);
      }
      ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_CREATE_ASSIGNMENTS);
      if (ea != null) {
         String n = getPanelContainer().getLanguageDependentString(ea.getName() + "Key");
         XMLCheckboxPanel cbp = new XMLCheckboxPanel(getPanelContainer(),
                                                     ea.get("Value"),
                                                     n,
                                                     false,
                                                     JaWEManager.getInstance()
                                                        .getJaWEController()
                                                        .canModifyElement(ea.get("Value")),
                                                     false);
         ealist.add(cbp);
      }
      ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_TRANSIENT);
      if (ea != null) {
         String n = getPanelContainer().getLanguageDependentString(ea.getName() + "Key");
         XMLCheckboxPanel cbp = new XMLCheckboxPanel(getPanelContainer(),
                                                     ea.get("Value"),
                                                     n,
                                                     false,
                                                     JaWEManager.getInstance()
                                                        .getJaWEController()
                                                        .canModifyElement(ea.get("Value")),
                                                     false);
         ealist.add(cbp);
      }
      ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_DELETE_FINISHED);
      if (ea != null) {
         String n = getPanelContainer().getLanguageDependentString(ea.getName() + "Key");
         XMLCheckboxPanel cbp = new XMLCheckboxPanel(getPanelContainer(),
                                                     ea.get("Value"),
                                                     n,
                                                     false,
                                                     JaWEManager.getInstance()
                                                        .getJaWEController()
                                                        .canModifyElement(ea.get("Value")),
                                                     false);
         ealist.add(cbp);
      }

      ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_CHECK_FOR_FIRST_ACTIVITY);
      if (ea != null) {
         String n = getPanelContainer().getLanguageDependentString(ea.getName() + "Key");
         XMLCheckboxPanel cbp = new XMLCheckboxPanel(getPanelContainer(),
                                                     ea.get("Value"),
                                                     n,
                                                     false,
                                                     JaWEManager.getInstance()
                                                        .getJaWEController()
                                                        .canModifyElement(ea.get("Value")),
                                                     false);
         ealist.add(cbp);
      }

      ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_CHECK_FOR_CONTINUATION);
      if (ea != null) {
         String n = getPanelContainer().getLanguageDependentString(ea.getName() + "Key");
         XMLCheckboxPanel cbp = new XMLCheckboxPanel(getPanelContainer(),
                                                     ea.get("Value"),
                                                     n,
                                                     false,
                                                     JaWEManager.getInstance()
                                                        .getJaWEController()
                                                        .canModifyElement(ea.get("Value")),
                                                     false);
         ealist.add(cbp);
      }

      if (ealist.size() > 3) {
         for (int i = 0; i < ealist.size(); i += 2) {
            List subpanels = new ArrayList();
            subpanels.add(ealist.get(i));
            subpanels.add(Box.createHorizontalGlue());
            if ((i + 1) < ealist.size()) {
               subpanels.add(ealist.get(i + 1));
            }
            panelElements.add(new XMLGroupPanelGL(getPanelContainer(),
                                                  el,
                                                  subpanels,
                                                  "",
                                                  false,
                                                  false,
                                                  true));
         }
      } else {
         panelElements.addAll(ealist);
      }

      ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_REDIRECT_AFTER_PROCESS_END);
      if (ea != null) {
         XMLPanel pnl = new XMLTextPanel(getPanelContainer(),
                                         ea.get("Value"),
                                         getPanelContainer().getLanguageDependentString(ea.getName()
                                                                                        + "Key"),
                                         false,
                                         false,
                                         JaWEManager.getInstance()
                                            .getJaWEController()
                                            .canModifyElement(ea));
         panelElements.add(pnl);
      }
      ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_MAX_ASSIGNMENTS);
      if (ea != null) {
         XMLPanel pnl = new XMLTextPanel(getPanelContainer(),
                                         ea.get("Value"),
                                         getPanelContainer().getLanguageDependentString(ea.getName()
                                                                                        + "Key"),
                                         false,
                                         false,
                                         JaWEManager.getInstance()
                                            .getJaWEController()
                                            .canModifyElement(ea));
         panelElements.add(pnl);
      }
      ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_WORKLOAD_FACTOR);
      if (ea != null) {
         XMLPanel pnl = new XMLTextPanel(getPanelContainer(),
                                         ea.get("Value"),
                                         getPanelContainer().getLanguageDependentString(ea.getName()
                                                                                        + "Key"),
                                         false,
                                         false,
                                         JaWEManager.getInstance()
                                            .getJaWEController()
                                            .canModifyElement(ea));
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

   public XMLPanel getPanel(XMLAttribute el) {
      if ((el.getParent() instanceof Script && el.toName().equals("Type"))
          || (el.getParent() instanceof ExpressionType && el.toName()
             .equals("ScriptType"))) {
         List choices = new ArrayList();
         choices.add(SharkConstants.SCRIPT_VALUE_JAVASCRIPT);
         choices.add(SharkConstants.SCRIPT_VALUE_JAVA);
         choices.add(SharkConstants.SCRIPT_VALUE_PYTHONSCRIPT);
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

      } else if (el.getParent() instanceof WfVariable) {
         WorkflowProcess wp = XMLUtil.getWorkflowProcess(el);
         Map chm = XMLUtil.getPossibleVariables(wp);
         List varIds = new ArrayList();
         WfVariable var = (WfVariable) el.getParent();
         List l = ((WfVariables) var.getParent()).toElements();
         for (int i = 0; i < l.size(); i++) {
            varIds.add(((WfVariable) l.get(i)).getId());
         }
         List choices = SharkUtils.getPossibleVariableChoices(SharkUtils.getPossibleVariableChoices(chm,
                                                                                                    varIds,
                                                                                                    el.toValue()),
                                                              var.getFilter(),
                                                              2,
                                                              false);

         boolean editable = var.getParent()
            .toName()
            .equals(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES);
         if (editable) {
            if (!chm.containsKey(el.toValue())) {
               choices.add(0, el.toValue());
            }
         }
         return new XMLComboPanel(getPanelContainer(),
                                  el,
                                  getPanelContainer().getLanguageDependentString("VariableKey"),
                                  choices,
                                  false,
                                  true,
                                  false,
                                  editable,
                                  true,
                                  true,
                                  JaWEManager.getInstance()
                                     .getJaWEController()
                                     .canModifyElement(el));
      }
      return super.getPanel(el);
   }

   protected List getPossibleVariableChoices(Map vars, ExtendedAttributes eas) {
      List l = new ArrayList(vars.values());
      for (int i = 0; i < eas.size(); i++) {
         ExtendedAttribute ea = (ExtendedAttribute) eas.get(i);
         if (ea.getName().equals(SharkConstants.VTP_VIEW)
             || ea.getName().equals(SharkConstants.VTP_UPDATE)) {
            XMLCollectionElement var = (XMLCollectionElement) vars.get(ea.getVValue());
            if (var != null) {
               l.remove(var);
            }
         }
      }
      return l;
   }

   protected List getExpressionChoices(XMLElement el) {
      List l = super.getExpressionChoices(el);

      DataField df = new DataField(null);
      df.setId(SharkConstants.PROCESS_ID);
      l.add(df);
      df = new DataField(null);
      df.setId(SharkConstants.ACTIVITY_ID);
      l.add(df);
      df = new DataField(null);
      df.setId(SharkConstants.SESSION_HANDLE);
      l.add(df);
      df = new DataField(null);
      df.setId(SharkConstants.SHARK_VERSION);
      l.add(df);
      df = new DataField(null);
      df.setId(SharkConstants.SHARK_RELEASE);
      l.add(df);
      df = new DataField(null);
      df.setId(SharkConstants.SHARK_BUILDID);
      l.add(df);

      return l;
   }

   protected List getSMTPExpressionChoices(XMLElement el) {
      List l = super.getExpressionChoices(el);
      return l;
   }
}
