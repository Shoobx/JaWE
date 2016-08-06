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

package org.enhydra.jawe.shark.business;

import java.util.Arrays;
import java.util.Iterator;

import org.enhydra.jawe.shark.ExtendedAttributesWrapper;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;

public class WebClientConfigurationElement extends XMLComplexElement {

   protected ExtendedAttributes eas;

   protected boolean isPersisted = false;

   protected boolean isForAct = false;

   protected ExtendedAttributesWrapper eaw = null;

   public WebClientConfigurationElement(ExtendedAttributes eas, boolean isForAct) {
      super(eas.getParent(), "WebClientConfiguration", true);
      this.eas = eas;
      this.isForAct = isForAct;
      notifyMainListeners = false;
      notifyListeners = false;
      handleStructure();
      eaw = new ExtendedAttributesWrapper((ExtendedAttributes) ((XMLComplexElement) this.parent).get("ExtendedAttributes"));
      setReadOnly(eas.isReadOnly());
   }

   public void setValue(String v) {
      if (v == null) {
         boolean removeUnconditionally = false;
         if (isForAct) {
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_CHECK_FOR_COMPLETION, null, null, false, removeUnconditionally);
         } else {
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_CHECK_FOR_FIRST_ACTIVITY, null, null, false, removeUnconditionally);
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_REDIRECT_AFTER_PROCESS_END, null, null, true, removeUnconditionally);
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_DYNAMIC_VARIABLE_HANDLING, null, null, false, removeUnconditionally);
         }
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_CHECK_FOR_CONTINUATION, null, null, false, removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_CHOOSE_NEXT_PERFORMER, null, null, false, removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_ENABLE_REASSIGNMENT, null, null, false, removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_HIDE_DYNAMIC_PROPERTIES, null, null, true, removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_READ_ONLY_DYNAMIC_PROPERTIES, null, null, true, removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_HIDE_CONTROLS, null, null, true, removeUnconditionally);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_TURN_OFF_FEATURES, null, null, true, removeUnconditionally);
         if (isForAct) {
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_BACK_ACTIVITY_DEFINITION, null, null, true, removeUnconditionally);
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_IS_WEBDAV_FOR_ACTIVITY_VISIBLE, null, null, false, removeUnconditionally);
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_HTML5FORM_FILE, null, null, true, removeUnconditionally);
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_HTML5FORM_EMBEDDED, null, null, true, removeUnconditionally);
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_HTML_VARIABLE, null, null, true, removeUnconditionally);
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_HTML5FORM_XSL, null, null, true, removeUnconditionally);
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_FORM_PAGE_URL, null, null, true, removeUnconditionally);
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_TWF_XML_VARIABLE_TO_HANDLE, null, null, true, removeUnconditionally);
            SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_TWF_XML_VARIABLE_ITEM_NAME_PREFIXES, null, null, true, removeUnconditionally);
         }
      } else {
         if (isReadOnly) {
            throw new RuntimeException("Can't set the value of read only element!");
         }
         this.value = v;
      }
   }

   public void setReadOnly(boolean ro) {
      super.setReadOnly(ro);
      if (eaw != null) {
         eaw.setReadOnly(eas.isReadOnly());
      }
   }

   public ExtendedAttributesWrapper getVariablesElement() {
      return eaw;
   }

   public XMLAttribute getHTML5FormFileAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_HTML5FORM_FILE);
   }

   public XMLAttribute getHTML5FormEmbeddedAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_HTML5FORM_EMBEDDED);
   }

   public XMLAttribute getHTMLVariableAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_HTML_VARIABLE);
   }

   public XMLAttribute getHTML5FormXSLAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_HTML5FORM_XSL);
   }

   public XMLAttribute getFormPageURLAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_FORM_PAGE_URL);
   }

   public XMLAttribute getTWFXMLVariableToHandleAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_TWF_XML_VARIABLE_TO_HANDLE);
   }

   public XMLAttribute getTWFXMLItemNamePrefixesElement() {
      return (XMLAttribute) get(SharkConstants.EA_TWF_XML_VARIABLE_ITEM_NAME_PREFIXES);
   }
   
   public XMLAttribute getIsWebDAVForActivityVisibleAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_IS_WEBDAV_FOR_ACTIVITY_VISIBLE);
   }

   public XMLAttribute getBackActivityDefinitionAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_BACK_ACTIVITY_DEFINITION);
   }

   public XMLAttribute getCheckForCompletionAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_CHECK_FOR_COMPLETION);
   }

   public XMLAttribute getCheckForContinuationAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_CHECK_FOR_CONTINUATION);
   }

   public XMLAttribute getChooseNextPerformerAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_CHOOSE_NEXT_PERFORMER);
   }

   public XMLAttribute getEnableReassignmentAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_ENABLE_REASSIGNMENT);
   }

   public WfVariables getHideDynamicPropertiesElement() {
      return (WfVariables) get(SharkConstants.EA_HIDE_DYNAMIC_PROPERTIES);
   }

   public WfVariables getReadOnlyDynamicPropertiesElement() {
      return (WfVariables) get(SharkConstants.EA_READ_ONLY_DYNAMIC_PROPERTIES);
   }

   public WfVariables getHideControlsElement() {
      return (WfVariables) get(SharkConstants.EA_HIDE_CONTROLS);
   }

   public WfVariables getTurnOffFeaturesElement() {
      return (WfVariables) get(SharkConstants.EA_TURN_OFF_FEATURES);
   }

   public XMLAttribute getCheckForFirstActivityAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_CHECK_FOR_FIRST_ACTIVITY);
   }

   public XMLAttribute getRedirectAfterProcessEndAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_REDIRECT_AFTER_PROCESS_END);
   }

   public XMLAttribute getDynamicVariableHandlingAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_DYNAMIC_VARIABLE_HANDLING);
   }

   protected void fillStructure() {
      XMLAttribute attrHTML5FormFile = new XMLAttribute(this, SharkConstants.EA_HTML5FORM_FILE, false);
      XMLAttribute attrHTML5FormEmbedded = new XMLAttribute(this, SharkConstants.EA_HTML5FORM_EMBEDDED, false);
      XMLAttribute attrHTMLVariable = new XMLAttribute(this, SharkConstants.EA_HTML_VARIABLE, false);
      XMLAttribute attrHTML5FormXSL = new XMLAttribute(this, SharkConstants.EA_HTML5FORM_XSL, false);
      XMLAttribute attrFormPageURL = new XMLAttribute(this, SharkConstants.EA_FORM_PAGE_URL, false);
      XMLAttribute attrTWFXML = new XMLAttribute(this, SharkConstants.EA_TWF_XML_VARIABLE_TO_HANDLE, false);
      XMLAttribute attrTWFXMLItemNamePrefixes = new XMLAttribute(this, SharkConstants.EA_TWF_XML_VARIABLE_ITEM_NAME_PREFIXES, false);
      XMLAttribute attrIsWebDAVForActivityVisible = new XMLAttribute(this, SharkConstants.EA_IS_WEBDAV_FOR_ACTIVITY_VISIBLE, false, new String[] {
            "true", "false"
      }, 0);
      XMLAttribute attrBackActivityDefinition = new XMLAttribute(this, SharkConstants.EA_BACK_ACTIVITY_DEFINITION, false);
      XMLAttribute attrCheckForCompletion = new XMLAttribute(this, SharkConstants.EA_CHECK_FOR_COMPLETION, false, new String[] {
            "true", "false"
      }, 1);
      XMLAttribute attrCheckForContinuation = new XMLAttribute(this, SharkConstants.EA_CHECK_FOR_CONTINUATION, false, new String[] {
            "true", "false"
      }, 1);

      XMLAttribute attrChooseNextPerformer = new XMLAttribute(this, SharkConstants.EA_CHOOSE_NEXT_PERFORMER, false, new String[] {
            "true", "false"
      }, 1);
      XMLAttribute attrEnableReassignment = new XMLAttribute(this, SharkConstants.EA_ENABLE_REASSIGNMENT, false, new String[] {
            "true", "false"
      }, 0);
      WfVariables elHideDynamicProperties = new WfVariables(this, SharkConstants.EA_HIDE_DYNAMIC_PROPERTIES, Arrays.asList(new String[] {
         XPDLConstants.BASIC_TYPE_STRING
      }), "|", false);
      WfVariables elReadOnlyDynamicProperties = new WfVariables(this, SharkConstants.EA_READ_ONLY_DYNAMIC_PROPERTIES, Arrays.asList(new String[] {
         XPDLConstants.BASIC_TYPE_STRING
      }), "|", false);
      WfVariables elHideControls = new WfVariables(this, SharkConstants.EA_HIDE_CONTROLS, Arrays.asList(new String[] {
         XPDLConstants.BASIC_TYPE_STRING
      }), "|", false);
      WfVariables elTurnOffFeatures = new WfVariables(this, SharkConstants.EA_TURN_OFF_FEATURES, Arrays.asList(new String[] {
         XPDLConstants.BASIC_TYPE_STRING
      }), "|", false);

      XMLAttribute attrCheckForFirstActivity = new XMLAttribute(this, SharkConstants.EA_CHECK_FOR_FIRST_ACTIVITY, false, new String[] {
            "true", "false"
      }, 1);
      XMLAttribute attrRedirectAfterProcessEnd = new XMLAttribute(this, SharkConstants.EA_REDIRECT_AFTER_PROCESS_END, false);
      XMLAttribute attrDynamicVariableHandling = new XMLAttribute(this, SharkConstants.EA_DYNAMIC_VARIABLE_HANDLING, false, new String[] {
            "true", "false"
      }, 1);

      add(attrHTML5FormFile);
      add(attrHTML5FormEmbedded);
      add(attrHTMLVariable);
      add(attrHTML5FormXSL);
      add(attrFormPageURL);
      add(attrTWFXML);
      add(attrTWFXMLItemNamePrefixes);
      add(attrIsWebDAVForActivityVisible);
      add(attrBackActivityDefinition);
      add(attrCheckForCompletion);
      add(attrCheckForContinuation);
      add(attrChooseNextPerformer);
      add(attrEnableReassignment);
      add(elHideDynamicProperties);
      add(elReadOnlyDynamicProperties);
      add(elHideControls);
      add(elTurnOffFeatures);
      add(attrCheckForFirstActivity);
      add(attrRedirectAfterProcessEnd);
      add(attrDynamicVariableHandling);
   }

   protected void handleStructure() {
      int pc = 0;
      Iterator it = eas.toElements().iterator();
      boolean hasAny = false;
      while (it.hasNext()) {
         ExtendedAttribute ea = (ExtendedAttribute) it.next();
         String eaname = ea.getName();
         XMLElement attr = get(eaname);
         if (attr != null) {
            String eaval = ea.getVValue();
            if (eaname.equals(SharkConstants.EA_HIDE_DYNAMIC_PROPERTIES)
                || eaname.equals(SharkConstants.EA_READ_ONLY_DYNAMIC_PROPERTIES) || eaname.equals(SharkConstants.EA_HIDE_CONTROLS)
                || eaname.equals(SharkConstants.EA_TURN_OFF_FEATURES)) {
               ((WfVariables) attr).createStructure(eaval);
            } else {
               if ((eaname.equals(SharkConstants.EA_CHECK_FOR_COMPLETION) || eaname.equals(SharkConstants.EA_IS_WEBDAV_FOR_ACTIVITY_VISIBLE))
                   && parent instanceof Activity
                   || eaname.equals(SharkConstants.EA_CHECK_FOR_CONTINUATION)
                   || eaname.equals(SharkConstants.EA_CHOOSE_NEXT_PERFORMER)
                   || eaname.equals(SharkConstants.EA_ENABLE_REASSIGNMENT)
                   || (eaname.equals(SharkConstants.EA_CHECK_FOR_FIRST_ACTIVITY) || eaname.equals(SharkConstants.EA_DYNAMIC_VARIABLE_HANDLING)
                                                                                    && !(parent instanceof Activity))) {
                  pc++;
               }
               attr.setValue(eaval);
            }
            hasAny = true;
         }
      }

      int toCompNo = (isForAct ? 5 : 5);
      isPersisted = pc >= toCompNo;
   }

   public boolean isPersisted() {
      return isPersisted;
   }

   public void setPersisted(boolean isPersisted) {
      this.isPersisted = isPersisted;
   }

   public boolean isForActivity() {
      return isForAct;
   }

   public boolean isForModalDialog() {
      return true;
   }

}
