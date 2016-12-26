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

import org.enhydra.jawe.base.display.DisplayNameGeneratorSettings;
import org.enhydra.jawe.base.display.StandardDisplayNameGenerator;
import org.enhydra.jawe.shark.business.SharkConstants;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.elements.DataField;
import org.enhydra.jxpdl.elements.Package;
import org.enhydra.jxpdl.elements.WorkflowProcess;

public class SharkDisplayNameGenerator extends StandardDisplayNameGenerator {

   public SharkDisplayNameGenerator() {
      super();
   }

   public SharkDisplayNameGenerator(DisplayNameGeneratorSettings settings) {
      super(settings);
   }

   public String getDisplayName(WorkflowProcess el) {
      String dn = super.getDisplayName(el);
      return getDisplayNameForPackageOrWorkflowProcess(el, dn);
   }

   public String getDisplayName(Package el) {
      String dn = super.getDisplayName(el);
      return getDisplayNameForPackageOrWorkflowProcess(el, dn);
   }

   protected String getDisplayNameForPackageOrWorkflowProcess(XMLComplexElement pkgOrWp, String defDisplayName) {
      if (pkgOrWp instanceof Package || pkgOrWp instanceof WorkflowProcess) {
         String epn = "";
         DataField cat = null;
         if (pkgOrWp instanceof Package) {
            cat = ((Package) pkgOrWp).getDataField(SharkConstants.SHARK_VARIABLE_CATEGORY);
         } else {
            cat = ((WorkflowProcess) pkgOrWp).getDataField(SharkConstants.SHARK_VARIABLE_CATEGORY);
         }
         if (cat != null) {
            epn = cat.getInitialValue();
            if (epn.startsWith("\"")) {
               epn = epn.substring(1);
               if (epn.endsWith("\"")) {
                  epn = epn.substring(0, epn.length() - 1);
               }
            }
            epn = " (" + epn + ")";
         }
         return defDisplayName + epn;
      }
      return defDisplayName;
   }
}
