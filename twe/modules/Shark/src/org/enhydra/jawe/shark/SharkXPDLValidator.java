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

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.shark.business.SharkConstants;
import org.enhydra.jawe.shark.business.SharkPackageValidator;
import org.enhydra.jawe.shark.business.SharkValidationErrorIds;
import org.enhydra.jxpdl.StandardPackageValidator;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLValidationError;
import org.enhydra.jxpdl.elements.ExpressionType;
import org.enhydra.jxpdl.elements.Script;
import org.enhydra.jxpdl.elements.TSScript;

/**
 * Special shark validation for JaWE environment - to determine if the package is 'shark'
 * valid. It extends the shark package validator to add environment specific
 * functionality.
 * 
 * @author Sasa Bojanic
 */
public class SharkXPDLValidator extends SharkPackageValidator {

   public SharkXPDLValidator() {
   }

   public SharkXPDLValidator(Properties settings) throws Exception {
      super(settings);
   }

   protected int getNoOfReferences(XMLComplexElement parent, XMLComplexElement el) {
      return JaWEManager.getInstance().getXPDLUtils().getReferences(parent, el).size();
   }

   protected StandardPackageValidator createValidatorInstance() {
      return new SharkXPDLValidator();
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

   public boolean isDeadlineHandlerUsed() {
      return true;
   }

   protected Map getPossibleSharkStringVariablesEAValues(XMLElement el, boolean allLevels) {
      return SharkUtils.getPossibleSharkStringVariablesEAValues(el, allLevels);
   }

   protected Properties getPossibleSharkStringVariables(XMLElement el, boolean allLevels) {
      return SharkUtils.getPossibleSharkStringVariables(el, allLevels);
   }

   protected List<String> getPossibleSharkStringVariableNames(XMLElement el,
                                                              boolean allLevels) {
      return new ArrayList<String>(SharkUtils.getPossibleSharkStringVariables(el,
                                                                              allLevels)
         .stringPropertyNames());
   }

   protected List<String> getConfigStringChoices() {
      return SharkUtils.getConfigStringChoices();
   }

}
