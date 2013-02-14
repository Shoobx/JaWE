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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.enhydra.jawe.JaWEConstants;
import org.enhydra.jawe.Utils;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;

/**
 * Various XPDL utilities specific for shark/jawe configuration.
 * 
 * @author Sasa Bojanic
 */
public class SharkUtils {

   public static final String APP_DEF_CHOICES_FILE = "shkappdefchoices.properties";

   public static final String CONFIG_STRING_CHOICES_FILE = "shkconfigstringchoices.properties";

   public static List<String> appDefChoices = null;

   protected static List<String> configStringChoices = null;
   
   public static List getAppDefChoices() {
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
      return new ArrayList(appDefChoices);
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

   public static List getPossibleVariablesWithinMailSubjectOrContent(String subjOrCont) {
      List ret = new ArrayList();
      String prefix = "{process_variable:";
      String postfix = "}";
      List ups = XMLUtil.getUsingPositions(subjOrCont, prefix, new HashMap(), false);
      // System.out.println("UPS for "+subjOrCont+" is "+ups);
      for (int i = 0; i < ups.size(); i++) {
         int posprefix = ((Integer) ups.get(i)).intValue();
         int pospostfix = subjOrCont.indexOf(postfix, posprefix);
         if (pospostfix > posprefix) {
            String varId = subjOrCont.substring(posprefix + prefix.length(), pospostfix);
            ret.add(varId);
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
                        val += ",";
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
                        val += ",";
                     }
                  }
               }
            } else {
               val = el.toValue().trim();
            }
         }
         if (removeIfEmpty && val.equals("")) {
            eas.remove(ea);
         } else {
            ea.setVValue(val);
         }
      }
   }

}
