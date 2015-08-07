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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;
import org.enhydra.jxpdl.elements.Package;
import org.enhydra.jxpdl.elements.WorkflowProcess;

/**
 * Various XPDL utilities specific for shark/jawe configuration.
 * 
 * @author Sasa Bojanic
 */
public class SharkUtils {

   public static List getPossibleVariableChoices(Map vars, List varIds, String curId) {
      List l = new ArrayList(vars.values());
      for (int i = 0; i < varIds.size(); i++) {
         XMLCollectionElement var = (XMLCollectionElement) vars.get(varIds.get(i));
         if (var != null && !var.getId().equals(curId)) {
            l.remove(var);
         }
      }
      return l;
   }

   public static Map<String, String> getPossibleXPDLStringOrI18nVariables(XMLElement el, boolean allLevels, boolean isxpdlstr) {
      Map<String, String> ret = new HashMap<String, String>();
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(el);
      ExtendedAttributes eas = null;
      if (wp != null) {
         eas = wp.getExtendedAttributes();
         for (int i = 0; i < eas.size(); i++) {
            ExtendedAttribute ea = (ExtendedAttribute) eas.get(i);
            if (ea.getName().startsWith(isxpdlstr ? SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX : SharkConstants.EA_I18N_VARIABLE_PREFIX)) {
               ret.put(ea.getName().substring(isxpdlstr ? SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX.length()
                                                       : SharkConstants.EA_I18N_VARIABLE_PREFIX.length()),
                       ea.getVValue());
            }
         }
      }
      if (allLevels || wp == null) {
         eas = XMLUtil.getPackage(el).getExtendedAttributes();
         for (int i = 0; i < eas.size(); i++) {
            ExtendedAttribute ea = (ExtendedAttribute) eas.get(i);
            if (ea.getName().startsWith(isxpdlstr ? SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX : SharkConstants.EA_I18N_VARIABLE_PREFIX)) {
               String realName = ea.getName().substring(isxpdlstr ? SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX.length()
                                                                 : SharkConstants.EA_I18N_VARIABLE_PREFIX.length());
               if (!ret.containsKey(realName)) {
                  ret.put(ea.getName().substring(isxpdlstr ? SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX.length()
                                                          : SharkConstants.EA_I18N_VARIABLE_PREFIX.length()),
                          ea.getVValue());
               }
            }
         }
      }

      return ret;
   }

   public static Map<String, XMLElement> getPossibleXPDLStringOrI18nVariablesEAValues(XMLElement el, boolean allLevels, boolean isxpdlstr) {
      Map<String, XMLElement> ret = new HashMap<String, XMLElement>();
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(el);
      ExtendedAttributes eas = null;
      if (wp != null) {
         eas = wp.getExtendedAttributes();
         for (int i = 0; i < eas.size(); i++) {
            ExtendedAttribute ea = (ExtendedAttribute) eas.get(i);
            if (ea.getName().startsWith(isxpdlstr ? SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX : SharkConstants.EA_I18N_VARIABLE_PREFIX)) {
               ret.put(ea.getName().substring(isxpdlstr ? SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX.length()
                                                       : SharkConstants.EA_I18N_VARIABLE_PREFIX.length()),
                       ea.get("Value"));
            }
         }
      }
      if (allLevels || wp == null) {
         eas = XMLUtil.getPackage(el).getExtendedAttributes();
         for (int i = 0; i < eas.size(); i++) {
            ExtendedAttribute ea = (ExtendedAttribute) eas.get(i);
            if (ea.getName().startsWith(isxpdlstr ? SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX : SharkConstants.EA_I18N_VARIABLE_PREFIX)) {
               String realName = ea.getName().substring(isxpdlstr ? SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX.length()
                                                                 : SharkConstants.EA_I18N_VARIABLE_PREFIX.length());
               if (!ret.containsKey(realName)) {
                  ret.put(ea.getName().substring(isxpdlstr ? SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX.length()
                                                          : SharkConstants.EA_I18N_VARIABLE_PREFIX.length()),
                          ea.get("Value"));
               }
            }
         }
      }
      return ret;
   }

   public static void updateSingleExtendedAttribute(XMLComplexElement parent,
                                                    ExtendedAttributes eas,
                                                    String name,
                                                    String subName,
                                                    String val,
                                                    boolean removeIfEmpty,
                                                    boolean removeUnconditionally) {
      ExtendedAttribute ea = eas.getFirstExtendedAttributeForName(name);
      if (ea == null && !removeUnconditionally) {
         ea = (ExtendedAttribute) eas.generateNewElement();
         ea.setName(name);
         eas.add(ea);
      }
      if (removeUnconditionally) {
         if (ea != null) {
            eas.remove(ea);
         }
      } else {
         if (val == null) {
            XMLElement el = parent.get(name);
            val = "";
            if (el instanceof WfVariables) {
               WfVariables vare = (WfVariables) el;
               val = "";
               if (vare.size() > 0) {
                  List varl = vare.toElements();
                  for (int i = 0; i < varl.size(); i++) {
                     val += ((WfVariable) varl.get(i)).getId();
                     if (i < varl.size() - 1) {
                        val += vare.getTokenizer();
                     }
                  }
               }
            } else if (el instanceof WfAttachments) {
               WfAttachments vare = (WfAttachments) el;
               val = "";
               if (vare.size() > 0) {
                  List varl = vare.toElements();
                  for (int i = 0; i < varl.size(); i++) {
                     val += ((WfAttachment) varl.get(i)).get(subName).toValue();
                     if (i < varl.size() - 1) {
                        val += vare.getTokenizer();
                     }
                  }
               }
            } else if (el instanceof WfNameValues) {
               WfNameValues vare = (WfNameValues) el;
               val = "";
               if (vare.size() > 0) {
                  List varl = vare.toElements();
                  for (int i = 0; i < varl.size(); i++) {
                     WfNameValue nv = (WfNameValue) varl.get(i);
                     val += nv.getNamePart() + vare.getNameValueTokenizer() + nv.getValuePart();
                     if (i < varl.size() - 1) {
                        val += vare.getListTokenizer();
                     }
                  }
               }
            } else {
               val = el.toValue().trim();
            }
         }
         if (removeIfEmpty && val.trim().equals("")) {
            eas.remove(ea);
         } else {
            ea.setVValue(val);
         }
      }
   }

   public static boolean allowFlag(XMLElement el, String eaname, boolean defaultValue) {
      Activity act = XMLUtil.getActivity(el);
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(el);
      Package pkg = XMLUtil.getPackage(el);

      ExtendedAttribute ea = null;
      Boolean allow = new Boolean(defaultValue);

      if (act != null) {
         ea = act.getExtendedAttributes().getFirstExtendedAttributeForName(eaname);
      }
      if (ea == null && wp != null) {
         ea = wp.getExtendedAttributes().getFirstExtendedAttributeForName(eaname);
      }
      if (ea == null && pkg != null) {
         ea = XMLUtil.getPackage(el).getExtendedAttributes().getFirstExtendedAttributeForName(eaname);
      }
      if (ea != null) {
         allow = new Boolean(ea.getVValue().equalsIgnoreCase("true"));
      }
      return allow.booleanValue();
   }

}
