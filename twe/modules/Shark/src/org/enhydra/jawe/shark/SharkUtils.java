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
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.BasicType;
import org.enhydra.jxpdl.elements.DataField;
import org.enhydra.jxpdl.elements.DeclaredType;
import org.enhydra.jxpdl.elements.ExternalReference;
import org.enhydra.jxpdl.elements.FormalParameter;
import org.enhydra.jxpdl.elements.SchemaType;
import org.enhydra.jxpdl.elements.TypeDeclaration;

/**
 * Various XPDL utilities specific for shark/jawe configuration.
 * 
 * @author Sasa Bojanic
 */
public class SharkUtils {

   public static final String APP_DEF_CHOICES_FILE = "shkappdefchoices.properties";

   public static List appDefChoices = null;

   public static List getAppDefChoices() {
      if (appDefChoices == null) {
         appDefChoices = new ArrayList();
         try {
            Properties props = new Properties();
            String cch = System.getProperty(JaWEConstants.JAWE_CURRENT_CONFIG_HOME);
            Utils.manageProperties(props, cch, APP_DEF_CHOICES_FILE);
            Iterator it = props.keySet().iterator();
            while (it.hasNext()) {
               String chk = (String)it.next();
               appDefChoices.add(chk.substring(0, chk.length()-3));
            }
         } catch (Exception ex) {
         }
      }
      return appDefChoices;
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

   // filterType: 0-only declared types, 1-only basic and schema types, 2-both declared
   // and basic and schema,
   // 3-any declared and specified basic and schema types
   // if getIdList is set to true, the list of Strings representing variable Ids will be
   // returned,
   // otherwise it will return a list of DataField or FormalParameter objects
   public static List getPossibleVariableChoices(List choices,
                                                 List tds,
                                                 int filterType,
                                                 boolean getIdList) {
      if (tds == null || tds.size() == 0) {
         return new ArrayList(choices);
      }
      ArrayList filteredChoices = new ArrayList();
      for (int i = 0; i < choices.size(); i++) {
         XMLCollectionElement dfOrFP = (XMLCollectionElement) choices.get(i);
         XMLElement chn = null;
         if (dfOrFP.getId().trim().equals("")) {
            if (getIdList) {
               filteredChoices.add(dfOrFP.getId());
            } else {
               filteredChoices.add(dfOrFP);
            }
            continue;
         }
         if (dfOrFP instanceof DataField) {
            chn = ((DataField) dfOrFP).getDataType().getDataTypes().getChoosen();
         } else {
            chn = ((FormalParameter) dfOrFP).getDataType().getDataTypes().getChoosen();
         }
         if ((filterType == 0 || filterType >= 2) && chn instanceof DeclaredType) {
            if (filterType == 3) {
               if (getIdList) {
                  filteredChoices.add(dfOrFP.getId());
               } else {
                  filteredChoices.add(dfOrFP);
               }
            } else {
               String dtId = ((DeclaredType) chn).getId();
               TypeDeclaration td = XMLUtil.getPackage(dfOrFP).getTypeDeclaration(dtId);
               if (td != null) {
                  XMLElement chndt = td.getDataTypes().getChoosen();
                  if (chndt instanceof ExternalReference) {
                     String loc = ((ExternalReference) chndt).getLocation();
                     for (int j = 0; j < tds.size(); j++) {
                        if ((((String) tds.get(j)).equals("<>") && loc.endsWith("<>"))
                            || loc.endsWith("." + tds.get(j).toString())) {
                           if (getIdList) {
                              filteredChoices.add(dfOrFP.getId());
                           } else {
                              filteredChoices.add(dfOrFP);
                           }
                           break;
                        }
                     }
                  }
               }
            }
         }
         if ((filterType > 0) && (chn instanceof BasicType || chn instanceof SchemaType)) {
            String t = (chn instanceof BasicType) ? ((BasicType) chn).getType()
                                                 : XMLUtil.getShortClassName(SchemaType.class.getName());
            for (int j = 0; j < tds.size(); j++) {
               if (t.endsWith(tds.get(j).toString())) {
                  if (getIdList) {
                     filteredChoices.add(dfOrFP.getId());
                  } else {
                     filteredChoices.add(dfOrFP);
                  }
                  break;
               }
            }
         }
      }
      return filteredChoices;
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
}
