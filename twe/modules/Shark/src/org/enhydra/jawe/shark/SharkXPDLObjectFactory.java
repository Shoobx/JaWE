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

import org.enhydra.jawe.base.xpdlobjectfactory.XPDLObjectFactory;
import org.enhydra.jawe.base.xpdlobjectfactory.XPDLObjectFactorySettings;
import org.enhydra.jawe.shark.business.DeadlineEmailConfigurationElements;
import org.enhydra.jawe.shark.business.EmailConfigurationElement;
import org.enhydra.jawe.shark.business.SharkConstants;
import org.enhydra.jawe.shark.business.WfAttachment;
import org.enhydra.jawe.shark.business.WfAttachments;
import org.enhydra.jawe.shark.business.WfNameValue;
import org.enhydra.jawe.shark.business.WfNameValues;
import org.enhydra.jawe.shark.business.WfVariable;
import org.enhydra.jawe.shark.business.WfVariables;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;
import org.enhydra.jxpdl.elements.WorkflowProcess;

/**
 * @author Sasa Bojanic
 */
public class SharkXPDLObjectFactory extends XPDLObjectFactory {

   public SharkXPDLObjectFactory(XPDLObjectFactorySettings settings) throws Exception {
      super(settings);
   }

   public ExtendedAttribute createXPDLObject(ExtendedAttributesWrapper eas, String type, boolean addToCollection) {
      ExtendedAttribute ea = (ExtendedAttribute) eas.generateNewElement();
      if (addToCollection) {
         eas.add(ea);
      }
      return ea;
   }

   public XPDLStringVariable createXPDLObject(XPDLStringVariables eas, String type, boolean addToCollection) {
      XPDLStringVariable ea = (XPDLStringVariable) eas.generateNewElement();
      if (addToCollection) {
         eas.add(ea);
      }
      return ea;
   }

   public WfVariable createXPDLObject(WfVariables sps, String type, boolean addToCollection) {
      WfVariable sp = (WfVariable) sps.generateNewElement();

      adjustXPDLObject(sp, type);

      if (addToCollection) {
         sps.add(sp);
      }
      return sp;
   }

   public WfAttachment createXPDLObject(WfAttachments sps, String type, boolean addToCollection) {
      WfAttachment sp = (WfAttachment) sps.generateNewElement();

      adjustXPDLObject(sp, type);

      if (addToCollection) {
         sps.add(sp);
      }
      return sp;
   }

   public WfNameValue createXPDLObject(WfNameValues nvs, String type, boolean addToCollection) {
      WfNameValue nv = (WfNameValue) nvs.generateNewElement();

      adjustXPDLObject(nv, type);

      if (addToCollection) {
         nvs.add(nv);
      }
      return nv;
   }

   public EmailConfigurationElement createXPDLObject(DeadlineEmailConfigurationElements eces, String type, boolean addToCollection) {
      EmailConfigurationElement ece = (EmailConfigurationElement) eces.generateNewElement();

      adjustXPDLObject(ece, type);

      if (addToCollection) {
         eces.add(ece);
      }
      return ece;
   }

   public void adjustXPDLObject(XMLElement el, String type) {
      super.adjustType(el, type);
      
      // do not use this feature
      if (true) {
         return;
      }
      
      // inherit Package ext. attrib. values for new WorkflowProcess
      if (el instanceof WorkflowProcess) {
         ExtendedAttributes peas = XMLUtil.getPackage(el).getExtendedAttributes();
         ExtendedAttributes eas = ((WorkflowProcess) el).getExtendedAttributes();

         // inherit kernel's ext. attribs
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_UNSATISFIED_SPLIT_CONDITION_HANDLING_MODE);
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_USE_PROCESS_CONTEXT_ONLY);
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_ALLOW_UNDEFINED_VARIABLES);
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_TRANSIENT);
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_DELETE_FINISHED);
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_CREATE_ASSIGNMENTS);
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_CREATE_DEFAULT_ASSIGNMENT);
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_HANDLE_ALL_ASSIGNMENTS);
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_ACCEPT_SINGLE_ASSIGNMENT);
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_REASSIGN_WITH_UNACCEPTANCE_TO_SINGLE_USER);
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_DELETE_OTHER_ASSIGNMENTS);
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_ASSIGNMENT_MANAGER_PLUGIN);
         
         // inherit plugin's ext. attribs
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_ASSIGNMENT_MANAGER_APPEND_RESPONSIBLES);
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_ASSIGNMENT_MANAGER_TRY_STRAIGHTFORWARD_MAPPING);
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_ASSIGNMENT_MANAGER_DEFAULT_ASSIGNEES);
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_ASSIGNMENT_MANAGER_DEFAULT_ASSIGNEES);
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_MAX_ASSIGNMENTS);
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_WORKLOAD_FACTOR);
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_LOG_XPIL);
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_XPILLOG_EVENT_AUDIT_MANAGER_FILENAMEVAR);
         
         // web client's ext. attribs
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_CHECK_FOR_FIRST_ACTIVITY);
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_DYNAMIC_VARIABLE_HANDLING);
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_CHECK_FOR_CONTINUATION);
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_CHOOSE_NEXT_PERFORMER);
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_ENABLE_REASSIGNMENT);
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_HIDE_DYNAMIC_PROPERTIES);
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_READ_ONLY_DYNAMIC_PROPERTIES);
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_HIDE_CONTROLS);
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_TURN_OFF_FEATURES);
         handleExtendedAttrib(peas, null, eas, SharkConstants.EA_REDIRECT_AFTER_PROCESS_END);         
      }
      if (el instanceof Activity) {
         ExtendedAttributes peas1 = XMLUtil.getWorkflowProcess(el).getExtendedAttributes();
         ExtendedAttributes peas2 = XMLUtil.getPackage(el).getExtendedAttributes();
         ExtendedAttributes eas = ((Activity) el).getExtendedAttributes();         

         // inherit kernel's ext. attribs
         handleExtendedAttrib(peas1, peas2, eas, SharkConstants.EA_USE_PROCESS_CONTEXT_ONLY);
         handleExtendedAttrib(peas1, peas2, eas, SharkConstants.EA_CREATE_ASSIGNMENTS);
         handleExtendedAttrib(peas1, peas2, eas, SharkConstants.EA_CREATE_DEFAULT_ASSIGNMENT);
         handleExtendedAttrib(peas1, peas2, eas, SharkConstants.EA_HANDLE_ALL_ASSIGNMENTS);
         handleExtendedAttrib(peas1, peas2, eas, SharkConstants.EA_ACCEPT_SINGLE_ASSIGNMENT);
         handleExtendedAttrib(peas1, peas2, eas, SharkConstants.EA_REASSIGN_WITH_UNACCEPTANCE_TO_SINGLE_USER);
         handleExtendedAttrib(peas1, peas2, eas, SharkConstants.EA_DELETE_OTHER_ASSIGNMENTS);
         handleExtendedAttrib(peas1, peas2, eas, SharkConstants.EA_OVERRIDE_PROCESS_CONTEXT);
         handleExtendedAttrib(peas1, peas2, eas, SharkConstants.EA_ASSIGNMENT_MANAGER_PLUGIN);
         
         // inherit plugin's ext. attribs
         handleExtendedAttrib(peas1, peas2, eas, SharkConstants.EA_ASSIGNMENT_MANAGER_APPEND_RESPONSIBLES);
         handleExtendedAttrib(peas1, peas2, eas, SharkConstants.EA_ASSIGNMENT_MANAGER_TRY_STRAIGHTFORWARD_MAPPING);
         handleExtendedAttrib(peas1, peas2, eas, SharkConstants.EA_ASSIGNMENT_MANAGER_DEFAULT_ASSIGNEES);
         handleExtendedAttrib(peas1, peas2, eas, SharkConstants.EA_MAX_ASSIGNMENTS);
         handleExtendedAttrib(peas1, peas2, eas, SharkConstants.EA_WORKLOAD_FACTOR);
         
         // web client's ext. attribs
         handleExtendedAttrib(peas1, peas2, eas, SharkConstants.EA_CHECK_FOR_COMPLETION);
         handleExtendedAttrib(peas1, peas2, eas, SharkConstants.EA_CHECK_FOR_CONTINUATION);
         handleExtendedAttrib(peas1, peas2, eas, SharkConstants.EA_CHOOSE_NEXT_PERFORMER);
         handleExtendedAttrib(peas1, peas2, eas, SharkConstants.EA_ENABLE_REASSIGNMENT);
         handleExtendedAttrib(peas1, peas2, eas, SharkConstants.EA_HIDE_DYNAMIC_PROPERTIES);
         handleExtendedAttrib(peas1, peas2, eas, SharkConstants.EA_READ_ONLY_DYNAMIC_PROPERTIES);
         handleExtendedAttrib(peas1, peas2, eas, SharkConstants.EA_HIDE_CONTROLS);
         handleExtendedAttrib(peas1, peas2, eas, SharkConstants.EA_TURN_OFF_FEATURES);         
      }
   }

   protected void handleExtendedAttrib(ExtendedAttributes eassrc1, ExtendedAttributes eassrc2, ExtendedAttributes eastrgt, String eaname) {
      ExtendedAttribute pea = eassrc1.getFirstExtendedAttributeForName(eaname);
      if (pea == null) {
         pea = eassrc2.getFirstExtendedAttributeForName(eaname);
      }
      if (pea != null) {
         ExtendedAttribute ea = eastrgt.getFirstExtendedAttributeForName(eaname);
         if (ea == null) {
            ea = (ExtendedAttribute) eastrgt.generateNewElement();
            ea.setName(eaname);
            eastrgt.add(ea);
         }
         if (ea != null) {
            ea.setVValue(pea.getVValue());
         }
      }
   }
   
}
