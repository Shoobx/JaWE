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

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.xpdlvalidator.TogWEXPDLValidator;
import org.enhydra.jxpdl.StandardPackageValidator;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.XMLValidationError;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.XPDLValidationErrorIds;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.Application;
import org.enhydra.jxpdl.elements.ArrayType;
import org.enhydra.jxpdl.elements.BasicType;
import org.enhydra.jxpdl.elements.DataField;
import org.enhydra.jxpdl.elements.EnumerationType;
import org.enhydra.jxpdl.elements.ExceptionName;
import org.enhydra.jxpdl.elements.ExpressionType;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.FormalParameter;
import org.enhydra.jxpdl.elements.ListType;
import org.enhydra.jxpdl.elements.Participant;
import org.enhydra.jxpdl.elements.Performer;
import org.enhydra.jxpdl.elements.RecordType;
import org.enhydra.jxpdl.elements.Script;
import org.enhydra.jxpdl.elements.TSScript;
import org.enhydra.jxpdl.elements.UnionType;
import org.enhydra.jxpdl.elements.WorkflowProcess;
import org.enhydra.jxpdl.utilities.SequencedHashMap;

/**
 * Special shark validation - to determine if the package is 'shark' valid. It extends the
 * JaWE's package validator to add some additional restrictions.
 * 
 * @author Sasa Bojanic
 */
public class SharkXPDLValidator extends TogWEXPDLValidator {

   public SharkXPDLValidator() {
   }

   public SharkXPDLValidator(Properties settings) throws Exception {
      super(settings);
   }

   public void validateElement(XMLAttribute el, List existingErrors, boolean fullCheck) {
      super.validateElement(el, existingErrors, fullCheck);

      XMLElement parent = el.getParent();
      if (el.toName().equals("Type") && parent instanceof BasicType) {
         String choosenSubType = el.toValue();
         if (choosenSubType.equals(XPDLConstants.BASIC_TYPE_PERFORMER)
             || choosenSubType.equals(XPDLConstants.BASIC_TYPE_REFERENCE)) {
            XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                             XMLValidationError.SUB_TYPE_LOGIC,
                                                             SharkValidationErrorIds.ERROR_UNSUPPORTED_DATA_TYPE,
                                                             el.toName(),
                                                             el);
            existingErrors.add(verr);
         }
      }
      if (el.toName().equals("Name")
          && parent instanceof ExtendedAttribute
          && parent.getParent().getParent() instanceof Activity) {
         if (el.toValue().equals(SharkConstants.VTP_UPDATE)
             || el.toValue().equals(SharkConstants.VTP_VIEW)) {
            ExtendedAttribute ea = (ExtendedAttribute) parent;
            WorkflowProcess wp = XMLUtil.getWorkflowProcess(ea);
            Map m = wp.getAllVariables();
            if (m.get(ea.getVValue()) == null) {
               XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                                XMLValidationError.SUB_TYPE_LOGIC,
                                                                XPDLValidationErrorIds.ERROR_NON_EXISTING_VARIABLE_REFERENCE,
                                                                ea.getVValue(),
                                                                ea.get("Value"));
               existingErrors.add(verr);
            }
         }
      }
      if (el.toName().equals("Name")
          && parent instanceof ExtendedAttribute
          && parent.getParent().getParent() instanceof Application) {
         if (el.toValue().equals(SharkConstants.EA_TOOL_AGENT_CLASS)) {
            ExtendedAttribute ea = (ExtendedAttribute) parent;
            if (ea.getVValue().equals("")) {
               XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_WARNING,
                                                                XMLValidationError.SUB_TYPE_LOGIC,
                                                                SharkValidationErrorIds.WARNING_TOOL_AGENT_CLASS_NOT_DEFINED,
                                                                ea.getVValue(),
                                                                ea.get("Value"));
               existingErrors.add(verr);
            }
         }
      }
      if ((el.toName().equals("StartMode") || el.toName().equals("FinishMode"))
          && el.toValue().equals(XPDLConstants.ACTIVITY_MODE_MANUAL)
          && parent instanceof Activity) {
         Activity act = (Activity) parent;
         if (act.getActivityType() != XPDLConstants.ACTIVITY_TYPE_TASK_APPLICATION
             || el.toName().equals("FinishMode"))
            return;

         String performer = act.getFirstPerformer();
         boolean isSystemParticipantPerformer = false;
         Participant p = null;
         p = XMLUtil.findParticipant(xmlInterface,
                                     XMLUtil.getWorkflowProcess(act),
                                     performer);
         if (p != null) {
            String participantType = p.getParticipantType().getType();
            if (participantType.equals(XPDLConstants.PARTICIPANT_TYPE_SYSTEM)) {
               isSystemParticipantPerformer = true;
            }
         }
         if (isSystemParticipantPerformer) {
            XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                             XMLValidationError.SUB_TYPE_LOGIC,
                                                             SharkValidationErrorIds.ERROR_MANUAL_START_MODE_FOR_TOOL_ACTIVITY_WITH_SYSTEM_PARTICIPANT_PERFORMER,
                                                             act.getId(),
                                                             el);
            existingErrors.add(verr);
         }
      }
   }

   public void validateElement(Application el, List existingErrors, boolean fullCheck) {
      super.validateElement(el, existingErrors, fullCheck);

      if (el.getExtendedAttributes()
         .getFirstExtendedAttributeForName(SharkConstants.EA_TOOL_AGENT_CLASS) == null) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_WARNING,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          SharkValidationErrorIds.WARNING_TOOL_AGENT_CLASS_NOT_DEFINED,
                                                          "",
                                                          el);
         existingErrors.add(verr);
      }
   }

   public void validateElement(ArrayType el, List existingErrors, boolean fullCheck) {
      XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                       XMLValidationError.SUB_TYPE_LOGIC,
                                                       SharkValidationErrorIds.ERROR_UNSUPPORTED_DATA_TYPE,
                                                       el.toName(),
                                                       el);
      existingErrors.add(verr);
   }

   public void validateElement(EnumerationType el, List existingErrors, boolean fullCheck) {
      XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                       XMLValidationError.SUB_TYPE_LOGIC,
                                                       SharkValidationErrorIds.ERROR_UNSUPPORTED_DATA_TYPE,
                                                       el.toName(),
                                                       el);
      existingErrors.add(verr);
   }

   public void validateElement(ExceptionName el, List existingErrors, boolean fullCheck) {
      super.validateElement(el, existingErrors, fullCheck);
      if (!isElementLengthOK(el)) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          XPDLValidationErrorIds.ERROR_UNALLOWED_LENGTH,
                                                          el.toName(),
                                                          el);
         existingErrors.add(verr);         
      }      
   }
   
   public void validateElement(ExpressionType el, List existingErrors, boolean fullCheck) {
      if (!el.getScriptType().equals("")) {
         validateScript(el, existingErrors, fullCheck);
      }
   }

   public void validateElement(ListType el, List existingErrors, boolean fullCheck) {
      XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                       XMLValidationError.SUB_TYPE_LOGIC,
                                                       SharkValidationErrorIds.ERROR_UNSUPPORTED_DATA_TYPE,
                                                       el.toName(),
                                                       el);
      existingErrors.add(verr);
   }

   public void validateElement(org.enhydra.jxpdl.elements.Package el,
                               List existingErrors,
                               boolean fullCheck) {
      super.validateElement(el, existingErrors, fullCheck);
      if (existingErrors.size() == 0 || fullCheck) {
         validateScript(el.getScript(), existingErrors, fullCheck);
      }
   }

   public void validateElement(Performer el, List existingErrors, boolean fullCheck) {
      super.validateElement(el, existingErrors, fullCheck);
      if (fullCheck || existingErrors.size() == 0) {
         // check performer
         Activity act = XMLUtil.getActivity(el);
         if (act!=null && act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_NO) {
            String performer = el.toValue();
            Participant p = null;
            WorkflowProcess wp = XMLUtil.getWorkflowProcess(act);
            p = XMLUtil.findParticipant(xmlInterface, wp, performer);
            if (p != null) {
               String participantType = p.getParticipantType().getType();
               if (participantType.equals(XPDLConstants.PARTICIPANT_TYPE_SYSTEM)) {
                  XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                                   XMLValidationError.SUB_TYPE_LOGIC,
                                                                   SharkValidationErrorIds.ERROR_SYSTEM_PARTICIPANT_PERFORMER_FOR_NO_IMPLEMENTATION_ACTIVITY,
                                                                   act.getId(),
                                                                   el);
                  existingErrors.add(verr);

               }
            }
         }
      }

   }

   public void validateElement(RecordType el, List existingErrors, boolean fullCheck) {
      XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                       XMLValidationError.SUB_TYPE_LOGIC,
                                                       SharkValidationErrorIds.ERROR_UNSUPPORTED_DATA_TYPE,
                                                       el.toName(),
                                                       el);
      existingErrors.add(verr);
   }

   public void validateElement(UnionType el, List existingErrors, boolean fullCheck) {
      XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                       XMLValidationError.SUB_TYPE_LOGIC,
                                                       SharkValidationErrorIds.ERROR_UNSUPPORTED_DATA_TYPE,
                                                       el.toName(),
                                                       el);
      existingErrors.add(verr);
   }

   /** Introduces restrictions on script type. */
   public void validateScript(XMLComplexElement el, List existingErrors, boolean fullCheck) {
      String sType = "";
      if (el instanceof Script) {
         sType = ((Script) el).getType();
      } else {
         sType = ((ExpressionType) el).getScriptType();
      }
      if (!(sType.equals(SharkConstants.SCRIPT_VALUE_JAVA)
            || sType.equals(SharkConstants.SCRIPT_VALUE_JAVASCRIPT) || (sType.equals(SharkConstants.SCRIPT_VALUE_PYTHONSCRIPT) && !(el instanceof TSScript)))) {
         XMLValidationError verr = null;
         if (!sType.equals("")) {
            verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                          XMLValidationError.SUB_TYPE_LOGIC,
                                          SharkValidationErrorIds.ERROR_UNSUPPORTED_SCRIPT,
                                          sType,
                                          el);
         } else {
            verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                          XMLValidationError.SUB_TYPE_LOGIC,
                                          SharkValidationErrorIds.ERROR_SCRIPT_NOT_DEFINED,
                                          sType,
                                          el);
         }
         existingErrors.add(verr);
      }

   }

   public boolean isElementLengthOK(XMLElement el) {
      if (el instanceof XMLAttribute
          && el.toName().equals("Name")
          && ((el.getParent() instanceof WorkflowProcess && el.toValue().length() > 90) || (el.getParent() instanceof Activity && el.toValue()
             .length() > 254))) {
         return false;
      }
      if (el instanceof XMLAttribute
          && el.toName().equals("Id")
          && el.toValue().length() > 90
          && (el.getParent() instanceof org.enhydra.jxpdl.elements.Package
              || el.getParent() instanceof WorkflowProcess || el.getParent() instanceof Activity)) {
         return false;
      }
      if (el instanceof XMLAttribute
            && el.toName().equals("Id")
            && el.toValue().length() > 100
            && (el.getParent() instanceof DataField
                || el.getParent() instanceof FormalParameter)) {
           return false;
        }

      if (el instanceof ExceptionName && el.toValue().length()>100) {
         return false;
      }
      return super.isElementLengthOK(el);
   }
   
   protected boolean isRemoteSubflowIdOK(String subflowID) {
      try {
         new URL(subflowID);
      } catch (Throwable thr) {
         return false;
      }
      return true;
   }

   protected StandardPackageValidator createValidatorInstance() {
      return new SharkXPDLValidator();
   }

   protected Map getActualParameterOrConditionChoices(XMLElement el) {
      SequencedHashMap map = XMLUtil.getPossibleVariables(XMLUtil.getWorkflowProcess(el));

      DataField df = new DataField(null);
      df.setId(SharkConstants.PROCESS_ID);
      df.getDataType().getDataTypes().setBasicType();
      df.getDataType().getDataTypes().getBasicType().setTypeSTRING();
      map.put(SharkConstants.PROCESS_ID, df);

      df = new DataField(null);
      df.setId(SharkConstants.ACTIVITY_ID);
      df.getDataType().getDataTypes().setBasicType();
      df.getDataType().getDataTypes().getBasicType().setTypeSTRING();
      map.put(SharkConstants.ACTIVITY_ID, df);

      df = new DataField(null);
      df.setId(SharkConstants.SESSION_HANDLE);
      df.getDataType().getDataTypes().setBasicType();
      df.getDataType().getDataTypes().getBasicType().setTypeSTRING();
      map.put(SharkConstants.SESSION_HANDLE, df);

      df = new DataField(null);
      df.setId(SharkConstants.SHARK_RELEASE);
      df.getDataType().getDataTypes().setBasicType();
      df.getDataType().getDataTypes().getBasicType().setTypeSTRING();
      map.put(SharkConstants.SHARK_RELEASE, df);

      df = new DataField(null);
      df.setId(SharkConstants.SHARK_VERSION);
      df.getDataType().getDataTypes().setBasicType();
      df.getDataType().getDataTypes().getBasicType().setTypeSTRING();
      map.put(SharkConstants.SHARK_VERSION, df);

      df = new DataField(null);
      df.setId(SharkConstants.SHARK_BUILDID);
      df.getDataType().getDataTypes().setBasicType();
      df.getDataType().getDataTypes().getBasicType().setTypeSTRING();
      map.put(SharkConstants.SHARK_BUILDID, df);

      return map;
   }

   protected Map getDeadlineConditionChoices(XMLElement el) {
      Map map = getActualParameterOrConditionChoices(el);

      DataField df = new DataField(null);
      df.setId(SharkConstants.PROCESS_STARTED_TIME);
      df.getDataType().getDataTypes().setBasicType();
      df.getDataType().getDataTypes().getBasicType().setTypeDATETIME();
      map.put(SharkConstants.PROCESS_STARTED_TIME, df);

      df = new DataField(null);
      df.setId(SharkConstants.ACTIVITY_ACTIVATED_TIME);
      df.getDataType().getDataTypes().setBasicType();
      df.getDataType().getDataTypes().getBasicType().setTypeDATETIME();
      map.put(SharkConstants.ACTIVITY_ACTIVATED_TIME, df);

      df = new DataField(null);
      df.setId(SharkConstants.ACTIVITY_ACCEPTED_TIME);
      df.getDataType().getDataTypes().setBasicType();
      df.getDataType().getDataTypes().getBasicType().setTypeDATETIME();
      map.put(SharkConstants.ACTIVITY_ACCEPTED_TIME, df);

      return map;
   }

   protected Map getPerformerChoices(XMLElement el) {
      Map map = getActualParameterOrConditionChoices(el);

      Map parts = XMLUtil.getPossibleParticipants(XMLUtil.getWorkflowProcess(el),
                                                  JaWEManager.getInstance()
                                                     .getXPDLHandler());
      Iterator it = parts.entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry me = (Map.Entry) it.next();
         if (map.containsKey(me.getKey()))
            continue;
         DataField df = new DataField(null);
         df.setId(me.getKey().toString());
         df.getDataType().getDataTypes().setBasicType();
         df.getDataType().getDataTypes().getBasicType().setTypeSTRING();
         map.put(me.getKey().toString(), df);
      }

      return map;
   }

}
