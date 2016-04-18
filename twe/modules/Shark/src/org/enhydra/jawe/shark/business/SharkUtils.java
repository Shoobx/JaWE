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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
      if (ea == null && !removeUnconditionally && !(removeIfEmpty && (val == null || val.trim().equals("")))) {
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

   // vartype: 0 - xpdlstring, 1 - i18nvar, 2 - DataField or FormalParameter
   public static boolean doesVariableExist(XMLElement el, String id, int vartype) {
      boolean ret = SharkConstants.possibleSystemVariables.contains(id) && !id.startsWith("shark_process_error_");

      List xvs = new ArrayList();
      XMLElement el2check = el;
      if (vartype == 0) {
         if (el instanceof XPDLStringVariable) {
            XPDLStringVariables xpdlVars = (XPDLStringVariables) el.getParent();
            xvs = xpdlVars.getElementsForName(id);
         } else {
            el2check = el.getParent();
            ExtendedAttributes eas = (ExtendedAttributes) el2check.getParent();
            xvs = eas.getElementsForName(SharkConstants.EA_XPDL_STRING_VARIABLE_PREFIX + id);
         }
      } else if (vartype == 1) {
         if (el instanceof I18nVariable) {
            I18nVariables i18nVars = (I18nVariables) el.getParent();
            xvs = i18nVars.getElementsForName(id);
         } else {
            el2check = el.getParent();
            ExtendedAttributes eas = (ExtendedAttributes) el2check.getParent();
            xvs = eas.getElementsForName(SharkConstants.EA_I18N_VARIABLE_PREFIX + id);
         }
      }
      if (!(xvs.size() == 0 || (xvs.size() == 1 && xvs.contains(el2check)))) {
         ret = true;
      }
      ret = ret || vartype != 0 && getPossibleXPDLStringOrI18nVariables(el, true, true).containsKey(id);
      ret = ret || vartype != 1 && getPossibleXPDLStringOrI18nVariables(el, true, false).containsKey(id);

      if (vartype != 2) {
         WorkflowProcess wp = XMLUtil.getWorkflowProcess(el);
         if (wp != null) {
            ret = ret || wp.getAllVariables().containsKey(id);
         } else {
            ret = ret || XMLUtil.getPossibleDataFields(XMLUtil.getPackage(el)).containsKey(id);
         }
      }

      return ret;
   }

   public static Map<String, String> extractProcVarIdsAndXPDLStringIdsFromXPDLStringExpressions(Map<String, String> xpdlsvar, Collection<String> procvarids) {
      Map<String, String> ret = new HashMap<String, String>();

      Iterator<Map.Entry<String, String>> it = xpdlsvar.entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry<String, String> me = it.next();
         ret.put(me.getKey(), extractProcVarIdsAndXPDLStringIdsFromXPDLStringExpression(me.getValue(), procvarids, xpdlsvar.keySet()));
      }
      return ret;
   }

   public static String extractProcVarIdsAndXPDLStringIdsFromXPDLStringExpression(String expr, Collection<String> procvarids, Collection<String> xpdlstrids) {
      String ret = "";

      Set<String> keepVarIds = new HashSet<String>();
      Iterator<String> it = procvarids.iterator();
      while (it.hasNext()) {
         String key = it.next();
         if (-1 != expr.indexOf("{" + SharkConstants.PROCESS_VARIABLE_PLACEHOLDER_PREFIX + key + "}")) {
            keepVarIds.add(key);
         }
      }
      it = xpdlstrids.iterator();
      while (it.hasNext()) {
         String key = it.next();
         if (-1 != expr.indexOf("{" + SharkConstants.XPDL_STRING_PLACEHOLDER_PREFIX + key + "}")) {
            keepVarIds.add(key);
         }
      }
      Iterator<String> is = keepVarIds.iterator();
      while (is.hasNext()) {
         String string = (String) is.next();
         ret += string + " + ";
      }
      if (!ret.equals("")) {
         ret = ret.substring(0, ret.length() - 3);
      }
      return ret;
   }

   public static boolean isValidSharkDeadlineExpression(String expr) {
      String[] tks = XMLUtil.tokenize(expr, " ");
      int cntv = 0;
      for (int i = 0; i < tks.length; i++) {
         if (tks[i].equals(SharkConstants.DEADLINE_EXPRESSION_WORKDAYS_ONLY)) {
            cntv++;
         } else if (tks[i].endsWith(SharkConstants.DEADLINE_EXPRESSION_YEAR)) {
            String tv = getTimeValue(tks[i], SharkConstants.DEADLINE_EXPRESSION_YEAR);
            if (!tv.equals("")) {
               cntv++;
            }
         } else if (tks[i].endsWith(SharkConstants.DEADLINE_EXPRESSION_MONTH)) {
            String tv = getTimeValue(tks[i], SharkConstants.DEADLINE_EXPRESSION_MONTH);
            if (!tv.equals("")) {
               cntv++;
            }
         } else if (tks[i].endsWith(SharkConstants.DEADLINE_EXPRESSION_DAY)) {
            String tv = getTimeValue(tks[i], SharkConstants.DEADLINE_EXPRESSION_DAY);
            if (!tv.equals("")) {
               cntv++;
            }
         } else if (tks[i].endsWith(SharkConstants.DEADLINE_EXPRESSION_HOUR)) {
            String tv = getTimeValue(tks[i], SharkConstants.DEADLINE_EXPRESSION_HOUR);
            if (!tv.equals("")) {
               cntv++;
            }
         } else if (tks[i].endsWith(SharkConstants.DEADLINE_EXPRESSION_MINUTE)) {
            String tv = getTimeValue(tks[i], SharkConstants.DEADLINE_EXPRESSION_MINUTE);
            if (!tv.equals("")) {
               cntv++;
            }
         } else if (tks[i].endsWith(SharkConstants.DEADLINE_EXPRESSION_SECOND)) {
            String tv = getTimeValue(tks[i], SharkConstants.DEADLINE_EXPRESSION_SECOND);
            if (!tv.equals("")) {
               cntv++;
            }
         }
      }

      return tks != null && cntv == tks.length;
   }

   public static String getTimeValue(String timewithsuffix, String tsuffix) {
      String ret = "";

      int indofts = timewithsuffix.indexOf(tsuffix);
      if (indofts > 0) {
         String t = timewithsuffix.substring(0, indofts);
         try {
            ret = String.valueOf(new Integer(t));
         } catch (Exception ex) {
         }
      }

      return ret;
   }

}
