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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JCheckBox;

import org.enhydra.jawe.JaWEConstants;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.base.panel.InlinePanel;
import org.enhydra.jawe.base.panel.SpecialChoiceElement;
import org.enhydra.jawe.base.panel.StandardPanelGenerator;
import org.enhydra.jawe.base.panel.panels.PanelUtilities;
import org.enhydra.jawe.base.panel.panels.XMLCheckboxPanel;
import org.enhydra.jawe.base.panel.panels.XMLComboPanel;
import org.enhydra.jawe.base.panel.panels.XMLComboPanelWithReferenceLink;
import org.enhydra.jawe.base.panel.panels.XMLDataTypesPanel;
import org.enhydra.jawe.base.panel.panels.XMLGroupPanel;
import org.enhydra.jawe.base.panel.panels.XMLGroupPanelGL;
import org.enhydra.jawe.base.panel.panels.XMLListPanel;
import org.enhydra.jawe.base.panel.panels.XMLMultiLineTextPanelWithOptionalChoiceButtons;
import org.enhydra.jawe.base.panel.panels.XMLPanel;
import org.enhydra.jawe.base.panel.panels.XMLTablePanel;
import org.enhydra.jawe.base.panel.panels.XMLTextPanel;
import org.enhydra.jawe.shark.business.DeadlineHandlerConfigurationElement;
import org.enhydra.jawe.shark.business.EmailConfigurationElement;
import org.enhydra.jawe.shark.business.ErrorHandlerConfigurationElement;
import org.enhydra.jawe.shark.business.SharkConstants;
import org.enhydra.jawe.shark.business.SharkUtils;
import org.enhydra.jawe.shark.business.WebClientConfigurationElement;
import org.enhydra.jawe.shark.business.WfAttachment;
import org.enhydra.jawe.shark.business.WfAttachments;
import org.enhydra.jawe.shark.business.WfVariable;
import org.enhydra.jawe.shark.business.WfVariables;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.Application;
import org.enhydra.jxpdl.elements.BasicType;
import org.enhydra.jxpdl.elements.Condition;
import org.enhydra.jxpdl.elements.DataField;
import org.enhydra.jxpdl.elements.DataFields;
import org.enhydra.jxpdl.elements.DataTypes;
import org.enhydra.jxpdl.elements.Deadline;
import org.enhydra.jxpdl.elements.ExpressionType;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;
import org.enhydra.jxpdl.elements.InitialValue;
import org.enhydra.jxpdl.elements.Package;
import org.enhydra.jxpdl.elements.Participant;
import org.enhydra.jxpdl.elements.Script;
import org.enhydra.jxpdl.elements.WorkflowProcess;
import org.enhydra.jxpdl.utilities.SequencedHashMap;

/**
 * @author Sasa Bojanic
 */
public class SharkPanelGenerator extends StandardPanelGenerator {

   public static final String APP_DEF_CHOICES_FILE = "shkappdefchoices.properties";

   protected static final String CONFIG_STRING_CHOICES_FILE = "shkconfigstringchoices.properties";

   protected static List<String> appDefChoices = null;

   protected static List<String> configStringChoices = null;

   public static List<String> getAppDefChoices() {
      if (appDefChoices == null) {
         appDefChoices = new ArrayList();
         try {
            Properties props = new Properties();
            String cch = System.getProperty(JaWEConstants.JAWE_CURRENT_CONFIG_HOME);
            Utils.manageProperties(props, cch, APP_DEF_CHOICES_FILE);
            Iterator it = props.keySet().iterator();
            while (it.hasNext()) {
               String chk = (String) it.next();
               appDefChoices.add(chk.substring(0, chk.length() - 3));
            }
         } catch (Exception ex) {
         }
      }
      return new ArrayList<String>(appDefChoices);
   }

   public static List<String> getConfigStringChoices() {
      if (configStringChoices == null) {
         configStringChoices = new ArrayList();
         try {
            Properties csc = new Properties();
            String cch = System.getProperty(JaWEConstants.JAWE_CURRENT_CONFIG_HOME);
            Utils.manageProperties(csc, cch, CONFIG_STRING_CHOICES_FILE);
            configStringChoices.addAll(csc.stringPropertyNames());
         } catch (Exception ex) {
         }
      }
      return new ArrayList<String>(configStringChoices);
   }

   public SharkPanelGenerator() throws Exception {
      super();
   }

   public XMLPanel getPanel(final ErrorHandlerConfigurationElement el) {

      final XMLGroupPanel ltPanel = new SharkModeGroupPanel(getPanelContainer(),
                                                            el,
                                                            new ArrayList(),
                                                            getPanelContainer().getLanguageDependentString(el.toName() + "Key"),
                                                            true,
                                                            false,
                                                            false);
      populateErrorHandlerConfigPanel(el, ltPanel);
      if (!el.isPersisted() && el.isConfigurable()) {
         getPanelContainer().panelChanged(ltPanel, null);
      }

      return ltPanel;
   }

   public XMLPanel getPanel(final DeadlineHandlerConfigurationElement el) {
      XMLPanel ece = getPanel(el.getEmailConfigurationElement());
      XMLPanel eces = generateStandardTablePanel(el.getEmailConfigurationElements(), true, true, true, true);
      List tgp = new ArrayList();
      tgp.add(ece);
      tgp.add(eces);

      XMLPanel ret = new SharkModeGroupPanel(getPanelContainer(),
                                             el,
                                             tgp,
                                             getPanelContainer().getLanguageDependentString(el.toName() + "Key"),
                                             true,
                                             false,
                                             false);
      return ret;
   }

   protected void populateErrorHandlerConfigPanel(final ErrorHandlerConfigurationElement el, final XMLGroupPanel ltPanel) {
      boolean enableEditing = JaWEManager.getInstance().getJaWEController().canModifyElement(el);

      final XMLCheckboxPanel configErrorHandler = new XMLCheckboxPanel(getPanelContainer(),
                                                                       el.getConfigureErrorHandlerAttribute(),
                                                                       null,
                                                                       false,
                                                                       !el.getConfigureErrorHandlerAttribute().isReadOnly(),
                                                                       false,
                                                                       null);
      XMLPanel returnCode = new XMLComboPanel(getPanelContainer(), el.getReturnCodeAttribute(), null, true, true, false, false, enableEditing);

      XMLPanel newproc = new XMLCheckboxPanel(getPanelContainer(), el.getDoCreateNewProcAttribute(), null, false, enableEditing, false, null);

      XMLPanel filesyslog = new XMLCheckboxPanel(getPanelContainer(), el.getDoWriteFilesysLogAttribute(), null, false, enableEditing, false, null);

      List tgp = new ArrayList();

      tgp.add(configErrorHandler);
      tgp.add(returnCode);

      List cbp = new ArrayList();
      cbp.add(newproc);
      cbp.add(filesyslog);
      XMLGroupPanel cbPanel = new XMLGroupPanel(getPanelContainer(), el, cbp, "", false, false, false, null);

      tgp.add(cbPanel);
      tgp.add(getPanel(el.getEmailConfigurationElement()));
      for (int i = 0; i < tgp.size(); i++) {
         ltPanel.addToGroup(tgp.get(i));
      }
      final JCheckBox jcb = configErrorHandler.getCheckBox();
      jcb.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            configErrorHandler.setElements();
            el.setReadOnly(!el.isReadOnly());
            while (true) {
               try {
                  ltPanel.removeFromGroup(0);
               } catch (Exception ex) {
                  break;
               }
            }
            populateErrorHandlerConfigPanel(el, ltPanel);
            ltPanel.repaint();
            if (ltPanel.getParent() != null) {
               ltPanel.getParent().repaint();
            }
         }
      });
   }

   public XMLPanel getPanel(final EmailConfigurationElement el) {
      final XMLGroupPanel ltPanel = new SharkModeGroupPanel(getPanelContainer(),
                                                            el,
                                                            new ArrayList(),
                                                            getPanelContainer().getLanguageDependentString(el.toName() + "Key"),
                                                            true,
                                                            el.isForErrorHandling() || (el.isForDeadlineHandling() && !el.isForNonDefaultDeadlineHandling()),
                                                            false);
      populateEmailConfigPanel(el, ltPanel);
      if (!el.isPersisted() && el.isConfigurable()) {
         getPanelContainer().panelChanged(ltPanel, null);
      }

      return ltPanel;
   }

   protected void populateEmailConfigPanel(final EmailConfigurationElement el, final XMLGroupPanel ltPanel) {
      boolean enableEditing = JaWEManager.getInstance().getJaWEController().canModifyElement(el);

      List choices = new ArrayList(XMLUtil.getPossibleVariables(el).values());
      // DataField df = new DataField(wp.getDataFields());
      // df.setId(" ");
      // choices.add(0, df);
      final XMLCheckboxPanel configEmail = new XMLCheckboxPanel(getPanelContainer(),
                                                                el.getConfigureEmailAttribute(),
                                                                null,
                                                                false,
                                                                !el.getConfigureEmailAttribute().isReadOnly(),
                                                                false,
                                                                null);

      XMLElement ext = el.getExtensionAttribute();
      List extChoices = new ArrayList();
      Activity act = XMLUtil.getActivity(el);
      if (act != null) {
         List<Deadline> ddls = act.getDeadlines().toElements();
         for (Deadline dd : ddls) {
            if (!dd.getExceptionName().equals("")) {
               extChoices.add(dd.getExceptionName());
            }
         }
      }
      if (!extChoices.contains(ext.toValue()) && !ext.toValue().equals("")) {
         extChoices.add(0, ext.toValue());
      }
      XMLPanel extension = new XMLComboPanel(getPanelContainer(), ext, ext.toName(), extChoices, false, true, false, true, true, true, enableEditing, null);

      XMLPanel mode = new XMLCheckboxPanel(getPanelContainer(), el.getModeAttribute(), null, false, enableEditing, false, null);
      XMLPanel executionMode = new XMLCheckboxPanel(getPanelContainer(), el.getExecutionModeAttribute(), null, false, enableEditing, false, null);
      List cbp = new ArrayList();
      cbp.add(mode);
      cbp.add(executionMode);
      if (el.isForActivity()) {
         XMLPanel groupEmailOnly = new XMLCheckboxPanel(getPanelContainer(), el.getGroupEmailOnlyAttribute(), null, false, enableEditing, false, null);
         cbp.add(groupEmailOnly);
      }
      XMLGroupPanel cbPanel = new XMLGroupPanel(getPanelContainer(), el, cbp, "", false, false, false, null);

      XMLPanel recipientParticipant = null;
      if (el.isForDeadlineHandling() || el.isForLimitHandling() || el.isForErrorHandling()) {
         SequencedHashMap choicesForPar = null;
         XMLComplexElement pkgOrWp = XMLUtil.getWorkflowProcess(el);
         if (pkgOrWp == null) {
            pkgOrWp = XMLUtil.getPackage(el);
            choicesForPar = XMLUtil.getPossibleParticipants((Package) pkgOrWp, JaWEManager.getInstance().getXPDLHandler());
         } else {
            choicesForPar = XMLUtil.getPossibleParticipants((WorkflowProcess) pkgOrWp, JaWEManager.getInstance().getXPDLHandler());
         }
         Participant choosen = null;
         String pId = el.getRecipientParticipantAttribute().toValue();
         if (!pId.equals("")) {
            Iterator it = choicesForPar.values().iterator();
            while (it.hasNext()) {
               Participant p = (Participant) it.next();
               if (pId.equals(p.getId())) {
                  choosen = p;
                  break;
               }
            }
         }
         if (choosen != null) {
            choicesForPar.put(choosen.getId(), choosen);
         }

         SpecialChoiceElement cc = new SpecialChoiceElement(el.getRecipientParticipantAttribute(),
                                                            "",
                                                            new ArrayList(choicesForPar.values()),
                                                            choosen,
                                                            false,
                                                            "Id",
                                                            el.getRecipientParticipantAttribute().toName(),
                                                            el.getRecipientParticipantAttribute().isRequired());
         cc.setReadOnly(el.isReadOnly());

         recipientParticipant = new XMLComboPanelWithReferenceLink(getPanelContainer(), cc, null, true, false, false, enableEditing, null);
      }

      List<List> mc = prepareExpressionChoices(el);
      List<String> mct = prepareExpressionChoicesTooltips(el);

      XMLPanel subject = new XMLMultiLineTextPanelForSMTPEAs(getPanelContainer(), el.getSubjectAttribute(), true, 1, false, enableEditing, mc, mct);
      XMLPanel content = new XMLMultiLineTextPanelForSMTPEAs(getPanelContainer(), el.getContentAttribute(), true, 7, false, enableEditing, mc, mct);
      XMLPanel attachments = generateStandardTablePanel(el.getAttachmentsElement(), true, true, true, true);
      XMLPanel dmAttachments = new XMLListPanel((InlinePanel) getPanelContainer(),
                                                el.getDMAttachmentsElement(),
                                                el.getDMAttachmentsElement().toElements(),
                                                getPanelContainer().getLanguageDependentString(el.getDMAttachmentsElement().toName() + "Key"),
                                                true,
                                                false,
                                                enableEditing,
                                                true,
                                                true,
                                                true,
                                                null);

      List tgp = new ArrayList();

      if (!el.isForNonDefaultDeadlineHandling()) {
         tgp.add(configEmail);
      } else {
         tgp.add(extension);
      }
      tgp.add(cbPanel);
      if (recipientParticipant != null) {
         tgp.add(recipientParticipant);
      }
      tgp.add(subject);
      tgp.add(content);
      tgp.add(attachments);
      tgp.add(dmAttachments);
      for (int i = 0; i < tgp.size(); i++) {
         ltPanel.addToGroup(tgp.get(i));
      }
      final JCheckBox jcb = configEmail.getCheckBox();
      jcb.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            configEmail.setElements();
            el.setReadOnly(!el.isReadOnly());
            while (true) {
               try {
                  ltPanel.removeFromGroup(0);
               } catch (Exception ex) {
                  break;
               }
            }
            populateEmailConfigPanel(el, ltPanel);
            ltPanel.repaint();
            if (ltPanel.getParent() != null) {
               ltPanel.getParent().repaint();
               if (ltPanel.getParent().getParent() != null) {
                  ltPanel.getParent().getParent().repaint();
               }
            }
         }
      });
   }

   public XMLPanel getPanel(WebClientConfigurationElement el) {
      boolean enableEditing = JaWEManager.getInstance().getJaWEController().canModifyElement(el);
      List tgp = new ArrayList();

      // DataField df = new DataField(wp.getDataFields());
      // df.setId(" ");
      // choices.add(0, df);
      List ealist = new ArrayList();
      if (el.isForActivity()) {
         ExtendedAttributesWrapper eaw = el.getVariablesElement();
         XMLPanel vp = new ExtAttrWrapperTablePanel((InlinePanel) getPanelContainer(),
                                                    eaw,
                                                    JaWEManager.getInstance().getLabelGenerator().getLabel(eaw)
                                                          + ", " + eaw.size() + " " + getPanelContainer().getLanguageDependentString("ElementsKey"),
                                                    true,
                                                    false,
                                                    false,
                                                    false,
                                                    true,
                                                    true,
                                                    true);
         tgp.add(vp);
         XMLPanel xfpnl = getPanel(el.getXFormsFileAttribute());
         tgp.add(xfpnl);
         XMLPanel cfcmppnl = new XMLCheckboxPanel(getPanelContainer(), el.getCheckForCompletionAttribute(), null, false, enableEditing, false, null);
         ealist.add(cfcmppnl);
      } else {
         XMLPanel rapepnl = getPanel(el.getRedirectAfterProcessEndAttribute());
         tgp.add(rapepnl);
         XMLPanel cffapnl = new XMLCheckboxPanel(getPanelContainer(), el.getCheckForFirstActivityAttribute(), null, false, enableEditing, false, null);
         ealist.add(cffapnl);         
         XMLPanel dvhpnl = new XMLCheckboxPanel(getPanelContainer(), el.getDynamicVariableHandlingAttribute(), null, false, enableEditing, false, null);
         ealist.add(dvhpnl);         
      }
      XMLPanel cfcntpnl = new XMLCheckboxPanel(getPanelContainer(), el.getCheckForContinuationAttribute(), null, false, enableEditing, false, null);
      ealist.add(cfcntpnl);
      XMLPanel cnppnl = new XMLCheckboxPanel(getPanelContainer(), el.getChooseNextPerformerAttribute(), null, false, enableEditing, false, null);
      ealist.add(cnppnl);
      XMLPanel erpnl = new XMLCheckboxPanel(getPanelContainer(), el.getEnableReassignmentAttribute(), null, false, enableEditing, false, null);
      ealist.add(erpnl);
      
      if (ealist.size() > 2) {
         for (int i = 0; i < ealist.size(); i += 2) {
            List subpanels = new ArrayList();
            subpanels.add(ealist.get(i));
            subpanels.add(Box.createHorizontalGlue());
            if ((i + 1) < ealist.size()) {
               subpanels.add(ealist.get(i + 1));
            }
            subpanels.add(Box.createHorizontalGlue());
            if ((i + 1) < ealist.size()) {
               subpanels.add(ealist.get(i + 1));
            }
            tgp.add(new XMLGroupPanelGL(getPanelContainer(), el, subpanels, "", false, false, true, null));
         }
      } else {
         tgp.addAll(ealist);
      }

      
      XMLPanel hdppnl = new XMLListPanel((InlinePanel) getPanelContainer(),
                                         el.getHideDynamicPropertiesElement(),
                                         el.getHideDynamicPropertiesElement().toElements(),
                                         getPanelContainer().getLanguageDependentString(el.getHideDynamicPropertiesElement().toName() + "Key"),
                                         true,
                                         false,
                                         enableEditing,
                                         true,
                                         true,
                                         true,
                                         null);
      tgp.add(hdppnl);
      XMLPanel roppnl = new XMLListPanel((InlinePanel) getPanelContainer(),
                                         el.getReadOnlyDynamicPropertiesElement(),
                                         el.getReadOnlyDynamicPropertiesElement().toElements(),
                                         getPanelContainer().getLanguageDependentString(el.getReadOnlyDynamicPropertiesElement().toName() + "Key"),
                                         true,
                                         false,
                                         enableEditing,
                                         true,
                                         true,
                                         true,
                                         null);
      tgp.add(roppnl);
      XMLPanel hcpnl = new XMLListPanel((InlinePanel) getPanelContainer(),
                                        el.getHideControlsElement(),
                                        el.getHideControlsElement().toElements(),
                                        getPanelContainer().getLanguageDependentString(el.getHideControlsElement().toName() + "Key"),
                                        true,
                                        false,
                                        enableEditing,
                                        true,
                                        true,
                                        true,
                                        null);
      tgp.add(hcpnl);
      XMLPanel tofpnl = new XMLListPanel((InlinePanel) getPanelContainer(),
                                         el.getTurnOffFeaturesElement(),
                                         el.getTurnOffFeaturesElement().toElements(),
                                         getPanelContainer().getLanguageDependentString(el.getTurnOffFeaturesElement().toName() + "Key"),
                                         true,
                                         false,
                                         enableEditing,
                                         true,
                                         true,
                                         true,
                                         null);
      tgp.add(tofpnl);

      XMLPanel wcpanel = new SharkModeGroupPanel(getPanelContainer(),
                                                 el,
                                                 tgp,
                                                 getPanelContainer().getLanguageDependentString(el.toName() + "Key"),
                                                 true,
                                                 true,
                                                 false);
      if (!el.isPersisted()) {
         getPanelContainer().panelChanged(wcpanel, null);
      }

      return wcpanel;
   }

   public XMLPanel getPanel(WfVariable el) {
      String title = getPanelContainer().getLanguageDependentString("DMAttachmentKey");
      if (el.getParent().toName().equals(SharkConstants.EA_HIDE_DYNAMIC_PROPERTIES)) {
         title = getPanelContainer().getLanguageDependentString("HideDynamicPropertyKey");
      } else if (el.getParent().toName().equals(SharkConstants.EA_READ_ONLY_DYNAMIC_PROPERTIES)) {
         title = getPanelContainer().getLanguageDependentString("ReadOnlyDynamicPropertyKey");
      } else if (el.getParent().toName().equals(SharkConstants.EA_HIDE_CONTROLS)) {
         title = getPanelContainer().getLanguageDependentString("HideControlPropertyKey");
      } else if (el.getParent().toName().equals(SharkConstants.EA_TURN_OFF_FEATURES)) {
         title = getPanelContainer().getLanguageDependentString("TurnOffFeatureKey");
      }
      SharkModeGroupPanel gp = new SharkModeGroupPanel(getPanelContainer(), el, el.toElements(), title, true, false, true);
      return gp;
   }

   public XMLPanel getPanel(WfAttachment el) {
      SharkModeGroupPanel gp = new SharkModeGroupPanel(getPanelContainer(),
                                                       el,
                                                       el.toElements(),
                                                       getPanelContainer().getLanguageDependentString("AttachmentKey"),
                                                       true,
                                                       false,
                                                       true);
      return gp;
   }

   public XMLPanel getPanel(ExtendedAttributeWrapper el) {
      SharkModeGroupPanel gp = new SharkModeGroupPanel(getPanelContainer(),
                                                       el,
                                                       el.toElements(),
                                                       getPanelContainer().getLanguageDependentString(el.toName() + "Key"),
                                                       true,
                                                       false,
                                                       true);
      return gp;
   }

   public XMLPanel getPanel(XPDLStringVariable el) {
      SharkModeGroupPanel gp = new SharkModeGroupPanel(getPanelContainer(),
                                                       el,
                                                       el.toElements(),
                                                       getPanelContainer().getLanguageDependentString("XPDLStringVariableKey"),
                                                       true,
                                                       false,
                                                       true);
      return gp;
   }

   public XMLPanel getPanel(XPDLStringVariables el) {
      return generateStandardTablePanel(el, true, false, false, false);
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
          || el.getActivityType() == XPDLConstants.ACTIVITY_TYPE_EVENT_END || el.getActivityType() == XPDLConstants.ACTIVITY_TYPE_EVENT_START) {
         hidden.add(el.get("Deadlines"));
      }
      boolean isManual = el.getActivityType() == XPDLConstants.ACTIVITY_TYPE_NO
                         || ((el.getActivityType() == XPDLConstants.ACTIVITY_TYPE_TASK_APPLICATION || el.getActivityType() == XPDLConstants.ACTIVITY_TYPE_TASK_SCRIPT) && (el.getStartMode()
                            .equals(XPDLConstants.ACTIVITY_MODE_MANUAL) || el.getFinishMode().equals(XPDLConstants.ACTIVITY_MODE_MANUAL)));
      boolean isAutomatic = el.getActivityType() == XPDLConstants.ACTIVITY_TYPE_TASK_APPLICATION
                            || el.getActivityType() == XPDLConstants.ACTIVITY_TYPE_TASK_SCRIPT;
      if (!isManual && !isAutomatic) {
         if (no >= 5 && no < 8) {
            return null;
         }
         if (!((no == 8 || no == 9) && (el.getActivityType() == XPDLConstants.ACTIVITY_TYPE_BLOCK || el.getActivityType() == XPDLConstants.ACTIVITY_TYPE_SUBFLOW))) {
            return super.getPanel(el, no, hidden);
         }
      }
      if (!isManual && no < 5)
         return super.getPanel(el, no, hidden);
      if (!isManual && no < 7)
         return null;
      if (!isAutomatic && no == 7)
         return null;

      XMLPanel p = null;
      ExtendedAttributes eas = el.getExtendedAttributes();
      switch (no) {
         case 1:
            XMLGroupPanel gp = (XMLGroupPanel) super.getPanel(el, no, hidden);

            ExtendedAttribute ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_MAX_ASSIGNMENTS);
            if (ea != null) {
               XMLPanel pnl = new XMLTextPanel(getPanelContainer(),
                                               ea.get("Value"),
                                               getPanelContainer().getLanguageDependentString(ea.getName() + "Key"),
                                               false,
                                               false,
                                               JaWEManager.getInstance().getJaWEController().canModifyElement(ea),
                                               null);
               gp.addToGroup(pnl);
            }
            ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_WORKLOAD_FACTOR);
            if (ea != null) {
               XMLPanel pnl = new XMLTextPanel(getPanelContainer(),
                                               ea.get("Value"),
                                               getPanelContainer().getLanguageDependentString(ea.getName() + "Key"),
                                               false,
                                               false,
                                               JaWEManager.getInstance().getJaWEController().canModifyElement(ea),
                                               null);
               gp.addToGroup(pnl);
            }
            p = gp;
            break;
         case 5:
            p = getPanel(new WebClientConfigurationElement(eas, true));
            break;
         case 6:
            p = getPanel(new EmailConfigurationElement(eas, true, false, false, false, null));
            break;
         case 7:
            p = getPanel(new ErrorHandlerConfigurationElement(eas));
            break;
         case 8:
            p = getPanel(new DeadlineHandlerConfigurationElement(eas));
            // p = getPanel(new EmailConfigurationElement(eas, true, false, true, false,
            // false));
            break;
         case 9:
            p = getPanel(new EmailConfigurationElement(eas, true, false, false, true, null));
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
      Set hidden = PanelUtilities.getHiddenElements(getPanelContainer(), "XMLGroupPanel", el);
      List subpanels = new ArrayList();
      List groupsToShow = new ArrayList();
      if (!hidden.contains(el.get("Id"))) {
         subpanels.add(generateStandardTextPanel(el.get("Id"), true));
      }
      if (!hidden.contains(el.get("Name"))) {
         subpanels.add(generateStandardTextPanel(el.get("Name"), true));
      }
      if (subpanels.size() > 0) {
         groupsToShow.add(new XMLGroupPanel(getPanelContainer(), el, subpanels, "", false, false, true, true, null));
      }

      if (!hidden.contains(el.get("Description"))) {
         groupsToShow.add(getPanel(el.get("Description")));
      }
      XMLPanel canBeRemoved = null;
      if (!hidden.contains(el.getApplicationTypes())) {
         canBeRemoved = getPanel(el.getApplicationTypes().getFormalParameters());
         groupsToShow.add(canBeRemoved);
      }

      ExtendedAttributes eas = el.getExtendedAttributes();
      ExtendedAttribute ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_TOOL_AGENT_CLASS);
      boolean specTA = false;
      if (ea != null) {
         String taName = ea.getVValue();
         XMLPanel taPanel = getToolAgentPanel(el, taName);
         if (taPanel != null) {
            groupsToShow.add(taPanel);
            if (SharkConstants.TOOL_AGENT_LDAP.equals(taName)) {
               groupsToShow.remove(canBeRemoved);
            } else if (SharkConstants.TOOL_AGENT_QUARTZ.equals(taName)) {
               groupsToShow.add(getPanel(el.getExtendedAttributes()));
            } else if (SharkConstants.TOOL_AGENT_SCHEDULER.equals(taName)) {
               groupsToShow.add(getPanel(el.getExtendedAttributes()));
            } else if (SharkConstants.TOOL_AGENT_USERGROUP.equals(taName)) {
               groupsToShow.remove(canBeRemoved);
            }
         }
         specTA = taPanel != null;
      }
      if (!specTA) {
         if (ea != null) {
            XMLElement eav = ea.get("Value");

            XMLPanel pnl = getPanel(eav);

            groupsToShow.add(pnl);
         }
         if (!hidden.contains(el.getExtendedAttributes())) {
            groupsToShow.add(getPanel(el.getExtendedAttributes()));
         }
      }
      return new XMLGroupPanel(getPanelContainer(), el, groupsToShow, JaWEManager.getInstance().getLabelGenerator().getLabel(el), true, false, true, null);
   }

   public XMLElement getToolAgentElement(Application el, String taName) {
      XMLElement ret = null;
      if (SharkConstants.TOOL_AGENT_BEAN_SHELL.equals(taName)) {
         ret = new ScriptBasedToolAgentElement(el, SharkConstants.TOOL_AGENT_BEAN_SHELL);
      } else if (SharkConstants.TOOL_AGENT_CHECKDOCUMENTFORMATS.equals(taName)) {
         ret = new StandardToolAgentElement(el, SharkConstants.TOOL_AGENT_CHECKDOCUMENTFORMATS);
      } else if (SharkConstants.TOOL_AGENT_JAVACLASS.equals(taName)) {
         ret = new JavaClassToolAgentElement(el, SharkConstants.TOOL_AGENT_JAVACLASS);
      } else if (SharkConstants.TOOL_AGENT_JAVASCRIPT.equals(taName)) {
         ret = new ScriptBasedToolAgentElement(el, SharkConstants.TOOL_AGENT_JAVASCRIPT);
      } else if (SharkConstants.TOOL_AGENT_LDAP.equals(taName)) {
         ret = new LDAPOrUserGroupToolAgentElement(el, SharkConstants.TOOL_AGENT_LDAP);
      } else if (SharkConstants.TOOL_AGENT_MAIL.equals(taName)) {
         ret = new MailToolAgentElement(el, SharkConstants.TOOL_AGENT_MAIL);
      } else if (SharkConstants.TOOL_AGENT_QUARTZ.equals(taName)) {
         ret = new ProxyBasedToolAgentElement(el, SharkConstants.TOOL_AGENT_QUARTZ);
      } else if (SharkConstants.TOOL_AGENT_RUNTIMEAPPLICATION.equals(taName)) {
         ret = new RuntimeApplicationToolAgentElement(el, SharkConstants.TOOL_AGENT_RUNTIMEAPPLICATION);
      } else if (SharkConstants.TOOL_AGENT_SCHEDULER.equals(taName)) {
         ret = new ProxyBasedToolAgentElement(el, SharkConstants.TOOL_AGENT_SCHEDULER);
      } else if (SharkConstants.TOOL_AGENT_SOAP.equals(taName)) {
         ret = new SOAPToolAgentElement(el, SharkConstants.TOOL_AGENT_SOAP);
      } else if (SharkConstants.TOOL_AGENT_USERGROUP.equals(taName)) {
         ret = new LDAPOrUserGroupToolAgentElement(el, SharkConstants.TOOL_AGENT_USERGROUP);
      } else if (SharkConstants.TOOL_AGENT_XPATH.equals(taName)) {
         ret = new StandardToolAgentElement(el, SharkConstants.TOOL_AGENT_XPATH);
      } else if (SharkConstants.TOOL_AGENT_XPIL.equals(taName)) {
         ret = new StandardToolAgentElement(el, SharkConstants.TOOL_AGENT_XPIL);
      } else if (SharkConstants.TOOL_AGENT_XSLT.equals(taName)) {
         ret = new ScriptBasedToolAgentElement(el, SharkConstants.TOOL_AGENT_XSLT);
      }
      return ret;
   }

   public XMLPanel getToolAgentPanel(Application el, String taName) {
      XMLElement tael = getToolAgentElement(el, taName);
      if (tael != null) {
         return getPanel(tael);
      }
      return null;
   }

   public XMLPanel generateStandardPanel(XMLElement el) {
      if (el instanceof ToolAgentElementBase) {
         if (el instanceof LDAPOrUserGroupToolAgentElement) {
            return new SharkLDAPAndUserGroupToolAgentDynamicPanel(getPanelContainer(), (LDAPOrUserGroupToolAgentElement) el, null);
         }
         return generateSharkModeGroupPanel((XMLComplexElement) el, false, false);
      }
      return super.generateStandardPanel(el);
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
      return new XMLComboPanel(getPanelContainer(), el.get("Type"), choices, true, true, false, false, JaWEManager.getInstance()
         .getJaWEController()
         .canModifyElement(el));
   }

   public XMLPanel getPanel(DataField el) {
      if (el.getId().equals(SharkConstants.SHARK_VARIABLE_CATEGORY)) {
         return new XMLTextPanel(getPanelContainer(),
                                 el.get("InitialValue"),
                                 getPanelContainer().getLanguageDependentString("CategoryKey"),
                                 false,
                                 false,
                                 JaWEManager.getInstance().getJaWEController().canModifyElement(el),
                                 null);
      }
      XMLGroupPanel gp = (XMLGroupPanel) super.getPanel(el);
      ExtendedAttributes eas = el.getExtendedAttributes();
      ExtendedAttribute ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_MIN_LENGTH);
      if (ea != null) {
         XMLPanel pnl = new XMLTextPanel(getPanelContainer(),
                                         ea.get("Value"),
                                         getPanelContainer().getLanguageDependentString(ea.getName() + "Key"),
                                         false,
                                         false,
                                         JaWEManager.getInstance().getJaWEController().canModifyElement(ea),
                                         null);
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
         XMLCheckboxPanel cbp = new XMLCheckboxPanel(getPanelContainer(), ea.get("Value"), n, false, JaWEManager.getInstance()
            .getJaWEController()
            .canModifyElement(ea.get("Value")), false, null);
         int insertAt = gp.getPanelPositionForElement(el.get("Length"));
         if (insertAt >= 0 && gp.getComponentCount() >= ++insertAt) {
            gp.addToGroup(cbp, insertAt);
         } else {
            gp.addToGroup(cbp);
         }
      }
      // TODO: implement logic for URLVariable ext. attrib
      ExtendedAttributeWrapper eaw = new ExtendedAttributeWrapper(eas, SharkConstants.EA_URL_VARIABLE, true);
      // if (!eaw.isPersisted()) {
      // eaw.setVValue("false");
      // getPanelContainer().panelChanged(null, null);
      // }
      XMLPanel cbp = getPanel(eaw);
      int insertAt = gp.getPanelPositionForElement(el.get("Description"));
      // if (insertAt > 0) {
      // gp.addToGroup(cbp, insertAt - 1);
      // } else {
      // gp.addToGroup(cbp);
      // }
      eaw = new ExtendedAttributeWrapper(eas, SharkConstants.EA_TRANSIENT, true);
      if (!eaw.isPersisted()) {
         eaw.setVValue("false");
         getPanelContainer().panelChanged(null, null);
      }
      cbp = getPanel(eaw);
      insertAt = gp.getPanelPositionForElement(el.get("Description"));
      if (insertAt > 0) {
         gp.addToGroup(cbp, insertAt - 1);
      } else {
         gp.addToGroup(cbp);
      }

      eaw = new ExtendedAttributeWrapper(eas, SharkConstants.EA_DYNAMICSCRIPT, true);
      if (!eaw.isPersisted()) {
         eaw.setVValue("false");
         getPanelContainer().panelChanged(null, null);
      }
      cbp = getPanel(eaw);
      eaw = new ExtendedAttributeWrapper(eas, SharkConstants.EA_IS_ACTIVITY_SCOPE_ONLY, true);
      if (!eaw.isPersisted()) {
         eaw.setVValue("false");
         getPanelContainer().panelChanged(null, null);
      }
      XMLPanel cbp2 = getPanel(eaw);
      List pnls = new ArrayList();
      pnls.add(cbp);
      pnls.add(cbp2);
      XMLGroupPanel dsp = new XMLGroupPanel(getPanelContainer(), eas, pnls, "", true, true, false, null);
      insertAt = gp.getPanelPositionForElement(el.get("Description"));
      if (insertAt > 0) {
         gp.addToGroup(dsp, insertAt - 1);
      } else {
         gp.addToGroup(dsp);
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
      return new XMLDataTypesPanel(getPanelContainer(), el, choices, JaWEManager.getInstance().getLabelGenerator().getLabel(el), JaWEManager.getInstance()
         .getJaWEController()
         .canModifyElement(el), null);
   }

   public XMLPanel getPanel(ExtendedAttribute el) {
      if (el.getParent().getParent() instanceof Activity && (el.getName().equals(SharkConstants.VTP_UPDATE) || el.getName().equals(SharkConstants.VTP_VIEW))) {
         // boolean roVar = false;
         // String vVal = el.getVValue();
         Map vars = XMLUtil.getPossibleVariables(XMLUtil.getWorkflowProcess(el));
         List chs = null;
         // if (vars.containsKey(vVal)) {
         // roVar = true;
         // } else {
         chs = getPossibleVariableChoices(vars, el);
         // }
         if (!vars.containsKey(el.toValue()) && !el.getVValue().equals("")) {
            chs.add(0, el.getVValue());
         }

         return new ExtAttribPanel(getPanelContainer(), el, chs, false, false, JaWEManager.getInstance().getJaWEController().canModifyElement(el), true, null);
      }
      return super.getPanel(el);
   }

   protected XMLPanel getPanel(Package el, int no, Set hidden) {
      if ((no != 1 && no < 14) || no > 21) {
         return super.getPanel(el, no, hidden);
      }
      XMLPanel p = null;

      if (no == 1) {
         List panelElements = new ArrayList();

         if (!hidden.contains(el.get("Id"))) {
            panelElements.add(el.get("Id"));
         }
         if (!hidden.contains(el.get("Name"))) {
            panelElements.add(el.get("Name"));
         }

         DataField cat = el.getDataField(SharkConstants.SHARK_VARIABLE_CATEGORY);
         if (cat != null) {
            panelElements.add(cat);
         }

         if (!hidden.contains(el.getConformanceClass())) {
            panelElements.add(el.getConformanceClass());
         }
         if (!hidden.contains(el.getScript())) {
            panelElements.add(getPanel(el.getScript(), true));
         }

         List ealist = new ArrayList();
         List pplist = new ArrayList();
         ExtendedAttributes eas = el.getExtendedAttributes();
         ExtendedAttributeWrapper eaw = new ExtendedAttributeWrapper(eas, SharkConstants.EA_ALLOW_UNDEFINED_VARIABLES, true);
         if (!eaw.isPersisted()) {
            eaw.setVValue("true");
            getPanelContainer().panelChanged(null, null);
         }
         XMLPanel pnl = getPanel(eaw);
         ealist.add(pnl);
         eaw = new ExtendedAttributeWrapper(eas, SharkConstants.EA_USE_PROCESS_CONTEXT_ONLY, true);
         if (!eaw.isPersisted()) {
            getPanelContainer().panelChanged(null, null);
         }
         pnl = getPanel(eaw);
         ealist.add(pnl);
         eaw = new ExtendedAttributeWrapper(eas, SharkConstants.EA_CREATE_ASSIGNMENTS, true);
         if (!eaw.isPersisted()) {
            eaw.setVValue("true");
            getPanelContainer().panelChanged(null, null);
         }
         pnl = getPanel(eaw);
         ealist.add(pnl);
         eaw = new ExtendedAttributeWrapper(eas, SharkConstants.EA_TRANSIENT, true);
         if (!eaw.isPersisted()) {
            getPanelContainer().panelChanged(null, null);
         }
         pnl = getPanel(eaw);
         ealist.add(pnl);
         eaw = new ExtendedAttributeWrapper(eas, SharkConstants.EA_DELETE_FINISHED, true);
         if (!eaw.isPersisted()) {
            getPanelContainer().panelChanged(null, null);
         }
         pnl = getPanel(eaw);
         ealist.add(pnl);
         eaw = new ExtendedAttributeWrapper(eas, SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_LOG_XPIL, true);
         if (!eaw.isPersisted()) {
            eaw.setVValue("false");
            getPanelContainer().panelChanged(null, null);
         }
         pnl = getPanel(eaw);
         ealist.add(pnl);

         if (ealist.size() > 2) {
            for (int i = 0; i < ealist.size(); i += 2) {
               List subpanels = new ArrayList();
               subpanels.add(ealist.get(i));
               subpanels.add(Box.createHorizontalGlue());
               if ((i + 1) < ealist.size()) {
                  subpanels.add(ealist.get(i + 1));
               }
               subpanels.add(Box.createHorizontalGlue());
               if ((i + 1) < ealist.size()) {
                  subpanels.add(ealist.get(i + 1));
               }
               pplist.add(new XMLGroupPanelGL(getPanelContainer(), el, subpanels, "", false, false, true, null));
            }
         } else {
            pplist.addAll(ealist);
         }

         XMLPanel xpilfnPnl = getPanel(new ExtendedAttributeWrapper(eas, SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_FILENAMEVAR, false));
         pplist.add(xpilfnPnl);
         XMLPanel rapePnl = getPanel(new ExtendedAttributeWrapper(eas, SharkConstants.EA_REDIRECT_AFTER_PROCESS_END, false));
         pplist.add(rapePnl);
         XMLGroupPanel gpp = new XMLGroupPanel(getPanelContainer(),
                                               el,
                                               pplist,
                                               getPanelContainer().getLanguageDependentString("DefaultAttributesForProcessesKey"),
                                               true,
                                               true,
                                               true,
                                               null);
         panelElements.add(gpp);

         ExtendedAttribute ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_MAX_ASSIGNMENTS);
         if (ea != null) {
            pnl = new XMLTextPanel(getPanelContainer(),
                                   ea.get("Value"),
                                   getPanelContainer().getLanguageDependentString(ea.getName() + "Key"),
                                   false,
                                   false,
                                   JaWEManager.getInstance().getJaWEController().canModifyElement(ea),
                                   null);
            panelElements.add(pnl);
         }
         ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_WORKLOAD_FACTOR);
         if (ea != null) {
            pnl = new XMLTextPanel(getPanelContainer(),
                                   ea.get("Value"),
                                   getPanelContainer().getLanguageDependentString(ea.getName() + "Key"),
                                   false,
                                   false,
                                   JaWEManager.getInstance().getJaWEController().canModifyElement(ea),
                                   null);
            panelElements.add(pnl);
         }

         if (!hidden.contains(el.getExtendedAttributes())) {
            panelElements.add(el.getExtendedAttributes());
         }
         if (panelElements.size() > 0) {
            p = new XMLGroupPanel(getPanelContainer(), el, panelElements, getPanelContainer().getLanguageDependentString("GeneralKey"), true, false, true, null);
         }
      } else if (no == 14) {
         XPDLStringVariables eaw = new XPDLStringVariables(el.getExtendedAttributes());
         p = getPanel(eaw);
      } else if (no == 15 || no == 16) {
         p = getPanel(new EmailConfigurationElement(el.getExtendedAttributes(), (no == 16), false, false, false, null));
      } else if (no == 17) {
         p = getPanel(new ErrorHandlerConfigurationElement(el.getExtendedAttributes()));
      } else if (no == 18) {
         p = getPanel(new EmailConfigurationElement(el.getExtendedAttributes(), true, false, true, false, null));
      } else if (no == 19 || no == 20) {
         p = getPanel(new EmailConfigurationElement(el.getExtendedAttributes(), (no == 20), false, false, true, null));
      } else if (no == 21) {
         p = getPanel(new WebClientConfigurationElement(el.getExtendedAttributes(), false));
      }
      return p;
   }

   public XMLPanel getPanel(WorkflowProcess el, int no, Set hidden) {
      if ((no != 1 && no < 11) || no > 18) {
         return super.getPanel(el, no, hidden);
      }
      XMLPanel p = null;

      if (no == 1) {
         List panelElements = new ArrayList();
         if (!hidden.contains(el.get("Id"))) {
            panelElements.add(el.get("Id"));
         }
         if (!hidden.contains(el.get("Name"))) {
            panelElements.add(el.get("Name"));
         }

         DataField cat = el.getDataField(SharkConstants.SHARK_VARIABLE_CATEGORY);
         if (cat != null) {
            panelElements.add(cat);
         }

         if (!hidden.contains(el.getAccessLevelAttribute())) {
            panelElements.add(el.getAccessLevelAttribute());
         }

         List ealist = new ArrayList();
         ExtendedAttributes eas = el.getExtendedAttributes();
         ExtendedAttributeWrapper eaw = new ExtendedAttributeWrapper(eas, SharkConstants.EA_ALLOW_UNDEFINED_VARIABLES, true);
         if (!eaw.isPersisted()) {
            eaw.setVValue("true");
            getPanelContainer().panelChanged(null, null);
         }
         XMLPanel pnl = getPanel(eaw);
         ealist.add(pnl);
         eaw = new ExtendedAttributeWrapper(eas, SharkConstants.EA_USE_PROCESS_CONTEXT_ONLY, true);
         if (!eaw.isPersisted()) {
            getPanelContainer().panelChanged(null, null);
         }
         pnl = getPanel(eaw);
         ealist.add(pnl);
         eaw = new ExtendedAttributeWrapper(eas, SharkConstants.EA_CREATE_ASSIGNMENTS, true);
         if (!eaw.isPersisted()) {
            eaw.setVValue("true");
            getPanelContainer().panelChanged(null, null);
         }
         pnl = getPanel(eaw);
         ealist.add(pnl);
         eaw = new ExtendedAttributeWrapper(eas, SharkConstants.EA_TRANSIENT, true);
         if (!eaw.isPersisted()) {
            getPanelContainer().panelChanged(null, null);
         }
         pnl = getPanel(eaw);
         ealist.add(pnl);
         eaw = new ExtendedAttributeWrapper(eas, SharkConstants.EA_DELETE_FINISHED, true);
         if (!eaw.isPersisted()) {
            getPanelContainer().panelChanged(null, null);
         }
         pnl = getPanel(eaw);
         ealist.add(pnl);
         eaw = new ExtendedAttributeWrapper(eas, SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_LOG_XPIL, true);
         if (!eaw.isPersisted()) {
            eaw.setVValue("false");
            getPanelContainer().panelChanged(null, null);
         }
         pnl = getPanel(eaw);
         ealist.add(pnl);

         if (ealist.size() > 2) {
            for (int i = 0; i < ealist.size(); i += 2) {
               List subpanels = new ArrayList();
               subpanels.add(ealist.get(i));
               subpanels.add(Box.createHorizontalGlue());
               if ((i + 1) < ealist.size()) {
                  subpanels.add(ealist.get(i + 1));
               }
               subpanels.add(Box.createHorizontalGlue());
               if ((i + 1) < ealist.size()) {
                  subpanels.add(ealist.get(i + 1));
               }
               panelElements.add(new XMLGroupPanelGL(getPanelContainer(), el, subpanels, "", false, false, true, null));
            }
         } else {
            panelElements.addAll(ealist);
         }

         XMLPanel xpilfnPnl = getPanel(new ExtendedAttributeWrapper(eas, SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_FILENAMEVAR, false));
         panelElements.add(xpilfnPnl);
         XMLPanel rapePnl = getPanel(new ExtendedAttributeWrapper(eas, SharkConstants.EA_REDIRECT_AFTER_PROCESS_END, false));
         panelElements.add(rapePnl);

         ExtendedAttribute ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_MAX_ASSIGNMENTS);
         if (ea != null) {
            pnl = new XMLTextPanel(getPanelContainer(),
                                   ea.get("Value"),
                                   getPanelContainer().getLanguageDependentString(ea.getName() + "Key"),
                                   false,
                                   false,
                                   JaWEManager.getInstance().getJaWEController().canModifyElement(ea),
                                   null);
            panelElements.add(pnl);
         }
         ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_WORKLOAD_FACTOR);
         if (ea != null) {
            pnl = new XMLTextPanel(getPanelContainer(),
                                   ea.get("Value"),
                                   getPanelContainer().getLanguageDependentString(ea.getName() + "Key"),
                                   false,
                                   false,
                                   JaWEManager.getInstance().getJaWEController().canModifyElement(ea),
                                   null);
            panelElements.add(pnl);
         }

         if (!hidden.contains(el.getExtendedAttributes())) {
            panelElements.add(el.getExtendedAttributes());
         }
         if (panelElements.size() > 0) {
            p = new XMLGroupPanel(getPanelContainer(), el, panelElements, getPanelContainer().getLanguageDependentString("GeneralKey"), true, false, true, null);
         }
      } else if (no == 11) {
         XPDLStringVariables eaw = new XPDLStringVariables(el.getExtendedAttributes());
         p = getPanel(eaw);
      } else if (no == 12 || no == 13) {
         p = getPanel(new EmailConfigurationElement(el.getExtendedAttributes(), (no == 13), false, false, false, null));
      } else if (no == 14) {
         p = getPanel(new ErrorHandlerConfigurationElement(el.getExtendedAttributes()));
      } else if (no == 15) {
         p = getPanel(new EmailConfigurationElement(el.getExtendedAttributes(), true, false, true, false, null));
      } else if (no == 16 || no == 17) {
         p = getPanel(new EmailConfigurationElement(el.getExtendedAttributes(), (no == 17), false, false, true, null));
      } else if (no == 18) {
         p = getPanel(new WebClientConfigurationElement(el.getExtendedAttributes(), false));
      }
      return p;
   }

   public XMLPanel getPanel(XMLAttribute el) {
      if ((el.getParent() instanceof Script && el.toName().equals("Type")) || (el.getParent() instanceof ExpressionType && el.toName().equals("ScriptType"))) {
         List choices = new ArrayList();
         choices.add(SharkConstants.SCRIPT_VALUE_JAVASCRIPT);
         choices.add(SharkConstants.SCRIPT_VALUE_JAVA);
         choices.add(SharkConstants.SCRIPT_VALUE_PYTHONSCRIPT);
         String choosen = el.toValue();
         if (!choices.contains(choosen)) {
            choices.add(0, choosen);
         }

         return new XMLComboPanel(getPanelContainer(), el, choices, false, true, false, false, JaWEManager.getInstance()
            .getJaWEController()
            .canModifyElement(el));

      } else if (el.getParent() instanceof WfVariable || el.getParent() instanceof WfAttachment) {
         Map chm = new SequencedHashMap();
         String title = null;
         if (el.getParent() instanceof WfVariable) {
            if (el.getParent().getParent().toName().equals(SharkConstants.EA_HIDE_DYNAMIC_PROPERTIES)) {
               createDummyDataFields(chm, new String[] {
                     "Id", "Name", "Priority", "Description"
               }, true);
               title = getPanelContainer().getLanguageDependentString("PropertyKey");
            } else if (el.getParent().getParent().toName().equals(SharkConstants.EA_READ_ONLY_DYNAMIC_PROPERTIES)) {
               createDummyDataFields(chm, new String[] {
                     "Name", "Priority", "Description"
               }, true);
               title = getPanelContainer().getLanguageDependentString("PropertyKey");
            } else if (el.getParent().getParent().toName().equals(SharkConstants.EA_HIDE_CONTROLS)) {
               createDummyDataFields(chm, new String[] {
                     "NewTask",
                     "NewSubtask",
                     "DeleteAllTasks",
                     "NewTaskList",
                     "RestoreDeleted",
                     "ImportTaskLists",
                     "EditTask",
                     "ChangeOwner",
                     "EditTaskList",
                     "ShowFields",
                     "PreviewTask",
                     "Delete",
                     "DeleteTaskList",
                     "RemoveImportedTaskList",
                     "RenameTask",
                     "RenameTaskList",
                     "ConnectToOutlook",
                     "ProperSyncConnection",
                     "Refresh",
                     "Copy",
                     "Child",
                     "Sibling",
                     "TableStyle",
                     "MemoStyle",
                     "Set10",
                     "Set25",
                     "Set50",
                     "Set100",
                     "BlueCategory",
                     "ClearCategories",
                     "GreenCategory",
                     "PurpleCategory",
                     "OrangeCategory",
                     "RedCategory",
                     "YellowCategory",
                     "RefreshTaskList",
                     "Today",
                     "Tomorrow",
                     "This",
                     "Next",
                     "In",
                     "NoDate",
                     "Custom",
                     "MarkComplete",
                     "MarkInProgress",
                     "MarkNotStarted",
                     "FollowUpMarkComplete",
                     "FollowUpDelete",
                     "GroupBy",
                     "ViewPlain",
                     "ViewSubtasks",
                     "SortAsc",
                     "SortDesc",
                     "toolboxRow",
                     "titleRow",
                     "quickCreateRow"
               }, false);
               title = getPanelContainer().getLanguageDependentString("ControlKey");
            } else if (el.getParent().getParent().toName().equals(SharkConstants.EA_TURN_OFF_FEATURES)) {
               createDummyDataFields(chm, new String[] {
                     "New", "Delete", "Rename", "SetDueDate", "MarkComplete", "MarkInProgress", "MarkNotStarted", "MarkWaiting", "MarkDeferred"
               }, false);
               title = getPanelContainer().getLanguageDependentString("FeatureKey");
            } else {
               chm = XMLUtil.getPossibleVariables(el);
               title = getPanelContainer().getLanguageDependentString("IdListVariableNameKey");
            }
         } else {
            chm = XMLUtil.getPossibleVariables(el);
         }
         List varIds = new ArrayList();
         List filter = null;
         boolean editable = false;
         if (el.getParent() instanceof WfVariable) {
            WfVariable var = (WfVariable) el.getParent();
            List l = ((WfVariables) var.getParent()).toElements();
            for (int i = 0; i < l.size(); i++) {
               varIds.add(((WfVariable) l.get(i)).getId());
            }
            filter = var.getFilter();
         } else {
            WfAttachment var = (WfAttachment) el.getParent();
            List l = ((WfAttachments) var.getParent()).toElements();
            for (int i = 0; i < l.size(); i++) {
               if (el.toName().equals("ContentVariable")) {
                  varIds.add(((WfAttachment) l.get(i)).getContentVariable());
               }
            }
            if (el.toName().equals("ContentVariable")) {
               filter = var.getFilterContentVariable();
            } else {
               filter = var.getFilterNameVariableOrExpression();
               editable = true;
            }
         }
         List choices = PanelUtilities.getPossibleVariableChoices(SharkUtils.getPossibleVariableChoices(chm, varIds, el.toValue()), filter, 2, false);

         if (editable) {
            if (!chm.containsKey(el.toValue()) && !el.toValue().equals("")) {
               choices.add(0, el.toValue());
            }
         }
         return new XMLComboPanel(getPanelContainer(), el, title, choices, false, true, false, editable, true, true, JaWEManager.getInstance()
            .getJaWEController()
            .canModifyElement(el), null);
      } else if (el.toName().equals("Value")
                 && el.getParent() instanceof ExtendedAttribute
                 && ((ExtendedAttribute) el.getParent()).getName().equals(SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_FILENAMEVAR)
                 || el.toName().equals(SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_FILENAMEVAR) && el.getParent() instanceof ExtendedAttributeWrapper) {
         Map chm = XMLUtil.getPossibleVariables(el);
         List choices = PanelUtilities.getPossibleVariableChoices(new ArrayList(chm.values()), Arrays.asList(new String[] {
            XPDLConstants.BASIC_TYPE_STRING
         }), 1, false);
         DataField df = createDummyDataField(" ", false);
         choices.add(0, df);
         return new XMLComboPanel(getPanelContainer(),
                                  el,
                                  getPanelContainer().getLanguageDependentString(SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_FILENAMEVAR + "Key"),
                                  choices,
                                  false,
                                  true,
                                  false,
                                  false,
                                  true,
                                  true,
                                  JaWEManager.getInstance().getJaWEController().canModifyElement(el),
                                  null);
      } else if (el.getParent() instanceof ScriptBasedToolAgentElement && el.toName().equals("Script")) {
         int noOfLines = 15;
         // try {
         // noOfLines = getPanelContainer().getSettings()
         // .getSettingInt("PreferredNumberOfLinesForExpression");
         // } catch (Exception ex) {
         // System.err.println("Wrong value for parameter XMLActualParametersPanel.preferredNumberOfLinesForExpression! Using default: "+noOfLines);
         // }
         String ext = "txt";
         String taName = ((ScriptBasedToolAgentElement) el.getParent()).toName();
         if (taName.equals(SharkConstants.TOOL_AGENT_XSLT)) {
            ext = "xsl";
         } else if (taName.equals(SharkConstants.TOOL_AGENT_BEAN_SHELL)) {
            ext = "java";
         } else if (taName.equals(SharkConstants.TOOL_AGENT_JAVASCRIPT)) {
            ext = "js";
         }
         return new XMLMultiLineTextPanelWithOptionalChoiceButtons(getPanelContainer(),
                                                                   el,
                                                                   "Script",
                                                                   false,
                                                                   true,
                                                                   noOfLines,
                                                                   false,
                                                                   prepareExpressionChoices(el.getParent().getParent()),
                                                                   prepareExpressionChoicesTooltips(el.getParent().getParent()),
                                                                   JaWEManager.getInstance().getJaWEController().canModifyElement(el),
                                                                   null,
                                                                   null,
                                                                   ext);
      } else if ((el.getParent() instanceof ExtendedAttribute
                  && ((ExtendedAttribute) el.getParent()).getName().equals(SharkConstants.EA_TOOL_AGENT_CLASS)
                  && el.getParent().getParent().getParent() instanceof Application && el.toName().equals("Value"))
                 || (el.getParent() instanceof ProxyBasedToolAgentElement && el.toName().equals(SharkConstants.EA_TOOL_AGENT_CLASS_PROXY))) {
         String name = el.getParent() instanceof ProxyBasedToolAgentElement ? SharkConstants.EA_TOOL_AGENT_CLASS_PROXY : SharkConstants.EA_TOOL_AGENT_CLASS;
         if (name.equals(SharkConstants.EA_TOOL_AGENT_CLASS)) {
            return new XMLTextPanel(getPanelContainer(),
                                    el,
                                    getPanelContainer().getLanguageDependentString(name + "Key"),
                                    false,
                                    false,
                                    JaWEManager.getInstance().getJaWEController().canModifyElement(el),
                                    null);
         } else {
            String choosen = el.toValue();
            List choices = SharkPanelGenerator.getAppDefChoices();
            choices.remove(SharkConstants.TOOL_AGENT_QUARTZ);
            choices.remove(SharkConstants.TOOL_AGENT_SCHEDULER);
            if (!choices.contains(choosen)) {
               choices.add(0, choosen);
            }
            Application app = XMLUtil.getApplication(el);
            List specChoices = new ArrayList();
            Object specChoosen = null;
            for (int i = 0; i < choices.size(); i++) {
               String choice = (String) choices.get(i);
               XMLElement tael = getToolAgentElement(app, choice);
               if (tael != null) {
                  specChoices.add(tael);
               }
               if (choice.equals(choosen)) {
                  specChoosen = tael;
               }
            }
            if (specChoosen == null) {
               specChoosen = choosen;
            }
            SpecialChoiceElement cc = new SpecialChoiceElement(el, "", specChoices, specChoosen, true, "", "", el.isRequired());
            cc.setReadOnly(el.isReadOnly());

            return new XMLComboPanelWithReferenceLinkForTAs(getPanelContainer(), cc, null, true, false, true, JaWEManager.getInstance()
               .getJaWEController()
               .canModifyElement(el), null);
         }

      } else if (el.getParent() instanceof ExtendedAttribute
                 && el.toName().equals("Name") && el.toValue().startsWith(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX)
                 && (el.getParent().getParent().getParent() instanceof WorkflowProcess || el.getParent().getParent().getParent() instanceof Package)) {
         return new XMLTextPanel(getPanelContainer(), el, false, false, false);
      } else if (el.getParent() instanceof XPDLStringVariable) {
         if (el.toName().equals("Name")) {
            return super.generateStandardTextPanel(el, false);
         }
         boolean enableEditing = JaWEManager.getInstance().getJaWEController().canModifyElement(el);

         XMLPanel value = new XMLMultiLineTextPanelForSMTPEAs(getPanelContainer(),
                                                              el,
                                                              true,
                                                              10,
                                                              false,
                                                              enableEditing,
                                                              prepareExpressionChoices(el.getParent()),
                                                              prepareExpressionChoicesTooltips(el.getParent()));

         return value;
      } else if (el.getParent() instanceof ExtendedAttributeWrapper && ((ExtendedAttributeWrapper) el.getParent()).isDecisionAttribute()) {

         return new XMLCheckboxPanel(getPanelContainer(),
                                     el,
                                     getPanelContainer().getLanguageDependentString(el.getParent().toName() + "Key"),
                                     false,
                                     JaWEManager.getInstance().getJaWEController().canModifyElement(el),
                                     false,
                                     null);
      } else if (el.getParent() instanceof ExtendedAttribute && el.toName().equals("Value")) {
         ExtendedAttribute ea = (ExtendedAttribute) el.getParent();
         XMLElement parentObj = ea.getParent().getParent();
         boolean isAct = parentObj instanceof Activity;
         boolean isWp = parentObj instanceof WorkflowProcess;
         boolean isPkg = parentObj instanceof Package;

         if (isAct || isWp || isPkg) {
            String postFixAct = "_ACTIVITY";
            String postFixProc = "_PROCESS";
            if ((!isAct && (ea.getName().equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT + postFixProc) || ea.getName()
               .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT + postFixProc)))
                || (ea.getName().equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_CONTENT + postFixAct) || ea.getName()
                   .equals(SharkConstants.SMTP_EVENT_AUDIT_MANAGER_SUBJECT + postFixAct))
                || (!isAct && (ea.getName().startsWith(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX)))) {
               boolean enableEditing = JaWEManager.getInstance().getJaWEController().canModifyElement(el);
               XMLPanel value = new XMLMultiLineTextPanelForSMTPEAs(getPanelContainer(),
                                                                    el,
                                                                    true,
                                                                    10,
                                                                    false,
                                                                    enableEditing,
                                                                    prepareExpressionChoices(ea),
                                                                    prepareExpressionChoicesTooltips(ea));

               return value;
            }
         }
      }
      return super.getPanel(el);
   }

   protected DataField createDummyDataField(String id, boolean useTranslationForName) {
      DataField df = new DataField(null);
      df.setId(id);
      if (useTranslationForName) {
         df.setName(getPanelContainer().getLanguageDependentString(id + "Key"));
      }
      df.getDataType().getDataTypes().setBasicType();
      df.getDataType().getDataTypes().getBasicType().setTypeSTRING();
      return df;
   }

   protected void createDummyDataFields(Map putIn, String[] ids, boolean useTranslationForName) {
      for (int i = 0; i < ids.length; i++) {
         putIn.put(ids[i], createDummyDataField(ids[i], useTranslationForName));
      }
   }

   protected XMLGroupPanel generateSharkModeGroupPanel(XMLComplexElement cel, boolean hasTitle, boolean hasEmptyBorder) {
      List toShow = getStandardGroupPanelComponents(cel);
      if ((cel instanceof ExpressionType && !(cel.getParent() instanceof Condition)) || cel instanceof Condition) {
         hasTitle = true;
      }
      return new SharkModeGroupPanel(getPanelContainer(),
                                     cel,
                                     toShow,
                                     JaWEManager.getInstance().getLabelGenerator().getLabel(cel),
                                     true,
                                     hasTitle,
                                     hasEmptyBorder);
   }

   protected XMLTablePanel generateStandardTablePanel(XMLCollection cl, boolean hasTitle, boolean hasEmptyBorder, boolean miniDim, boolean useBasicToolbar) {
      if (cl instanceof DataFields) {
         List elementsToShow = cl.toElements();
         Set hidden = PanelUtilities.getHiddenElements(getPanelContainer(), "XMLTablePanel", cl);
         elementsToShow.removeAll(hidden);
         List columnsToShow = PanelUtilities.getColumnsToShow(getPanelContainer(), "XMLTablePanel", cl);
         return new SharkTablePanelForVariables((InlinePanel) getPanelContainer(),
                                                cl,
                                                columnsToShow,
                                                elementsToShow,
                                                JaWEManager.getInstance().getLabelGenerator().getLabel(cl)
                                                      + ", " + (cl.size() - hidden.size()) + " "
                                                      + getPanelContainer().getLanguageDependentString("ElementsKey"),
                                                hasTitle,
                                                hasEmptyBorder,
                                                false,
                                                miniDim,
                                                true,
                                                true);

      }
      return super.generateStandardTablePanel(cl, hasTitle, hasEmptyBorder, miniDim, useBasicToolbar);
   }

   protected List getPossibleVariableChoices(Map vars, ExtendedAttribute eac) {
      ExtendedAttributes eas = (ExtendedAttributes) eac.getParent();
      List l = new ArrayList(vars.values());
      for (int i = 0; i < eas.size(); i++) {
         ExtendedAttribute ea = (ExtendedAttribute) eas.get(i);
         if (ea != eac && (ea.getName().equals(SharkConstants.VTP_VIEW) || ea.getName().equals(SharkConstants.VTP_UPDATE))) {
            XMLCollectionElement var = (XMLCollectionElement) vars.get(ea.getVValue());
            if (var != null) {
               l.remove(var);
            }
         }
      }
      return l;
   }

   public List getExpressionChoices(XMLElement el) {
      List l = super.getExpressionChoices(el);

      List<String> csc = SharkPanelGenerator.getConfigStringChoices();
      for (int i = 0; i < csc.size(); i++) {
         String id = csc.get(i);
         DataField df = createDummyDataField(id, false);
         l.add(df);
      }
      boolean isForActivity = XMLUtil.getActivity(el) != null || XMLUtil.getApplication(el) != null;
      boolean isXPDLString = (el instanceof XPDLStringVariable)
                             || ((el instanceof ExtendedAttribute) && ((ExtendedAttribute) el).getName()
                                .startsWith(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX));
      for (int i = 0; i < SharkConstants.possibleSystemVariables.size(); i++) {
         String id = SharkConstants.possibleSystemVariables.get(i);
         if (id.startsWith("shark_activity_") && !isForActivity && !isXPDLString) {
            continue;
         }
         DataField df = createDummyDataField(id, false);
         if (!SharkConstants.SHARK_SESSION_HANDLE.equals(id)) {
            if (id.endsWith("_time")) {
               df.getDataType().getDataTypes().getBasicType().setTypeDATETIME();
            }
         } else {
            df.getDataType().getDataTypes().setExternalReference();
            df.getDataType().getDataTypes().getExternalReference().setLocation("org.enhydra.shark.api.client.wfmc.wapi.WMSessionHandle");
         }
         l.add(df);
      }
      return l;
   }

   public List<List> prepareExpressionChoices(XMLElement el) {
      List<List> mc = new ArrayList<List>();

      List l = new ArrayList();
      boolean isForActivity = XMLUtil.getActivity(el) != null
                              || XMLUtil.getApplication(el) != null
                              || ((el instanceof EmailConfigurationElement) && ((EmailConfigurationElement) el).isForActivity());
      boolean isXPDLString = (el instanceof XPDLStringVariable)
                             || ((el instanceof ExtendedAttribute) && ((ExtendedAttribute) el).getName()
                                .startsWith(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX));
      boolean canBeDynamicScript = el instanceof InitialValue;

      for (int i = 0; i < SharkConstants.possibleSystemVariables.size(); i++) {
         String id = SharkConstants.possibleSystemVariables.get(i);
         if (id.startsWith("shark_activity_") && !isForActivity && !isXPDLString && !canBeDynamicScript) {
            continue;
         }
         DataField df = createDummyDataField(id, false);
         if (!SharkConstants.SHARK_SESSION_HANDLE.equals(id)) {
            if (id.endsWith("_time")) {
               df.getDataType().getDataTypes().getBasicType().setTypeDATETIME();
            }
         } else {
            df.getDataType().getDataTypes().setExternalReference();
            df.getDataType().getDataTypes().getExternalReference().setLocation("org.enhydra.shark.api.client.wfmc.wapi.WMSessionHandle");
         }
         l.add(df);
      }
      mc.add(l);

      mc.add(getBasicExpressionChoices(el));

      if (!(el instanceof InitialValue)) {
         l = new ArrayList();
         List<String> csc = SharkPanelGenerator.getConfigStringChoices();
         for (int i = 0; i < csc.size(); i++) {
            String id = csc.get(i);
            DataField df = createDummyDataField(id, false);
            l.add(df);
         }
         mc.add(l);

         l = new ArrayList();
         List<String> xpdlsc = new ArrayList<String>(SharkUtils.getPossibleXPDLStringVariables(el, true).stringPropertyNames());
         for (int i = 0; i < xpdlsc.size(); i++) {
            String id = xpdlsc.get(i);
            DataField df = createDummyDataField(id, false);
            l.add(df);
         }
         mc.add(l);

      }
      return mc;
   }

   public List<String> prepareExpressionChoicesTooltips(XMLElement el) {
      List<String> mct = new ArrayList<String>();

      mct.add(getSettings().getLanguageDependentString("InsertSystemVariableKey"));
      mct.add(getSettings().getLanguageDependentString("InsertVariableKey"));

      if (!(el instanceof InitialValue)) {
         mct.add(getSettings().getLanguageDependentString("InsertConfigStringVariableKey"));
         mct.add(getSettings().getLanguageDependentString("InsertXPDLStringVariableKey"));
      }
      return mct;
   }

}
