/**
* Together Workflow Editor
* Copyright (C) 2010 Together Teamsolutions Co., Ltd. 
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

/**
 * Miroslav Popov, Dec 13, 2005 miroslav.popov@gmail.com
 */
package org.enhydra.jawe.base.xpdlvalidator;

import java.util.Properties;

import org.enhydra.jawe.AdditionalResourceManager;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.shark.xpdl.StandardPackageValidator;

/**
 * @author Miroslav Popov
 */
public class XPDLValidatorSettings extends JaWEComponentSettings {

   public void init(JaWEComponent comp) {
      PROPERTYFILE_PATH = "org/enhydra/jawe/base/xpdlvalidator/properties/";
      PROPERTYFILE_NAME = "xpdlvalidator.properties";
      super.init(comp);
   }

   public void loadDefault(JaWEComponent comp, Properties properties) {
      // defaults
      arm = new AdditionalResourceManager(properties);

      componentSettings.putAll(properties);
      componentSettings.put(StandardPackageValidator.VALIDATE_SUBFLOW_REFERENCES,
                            new Boolean(properties.getProperty(StandardPackageValidator.VALIDATE_SUBFLOW_REFERENCES,
                                                               "true")
                               .equals("true")));
      componentSettings.put(StandardPackageValidator.VALIDATE_PERFORMER_EXPRESSIONS,
                            new Boolean(properties.getProperty(StandardPackageValidator.VALIDATE_PERFORMER_EXPRESSIONS,
                                                               "true")
                               .equals("true")));
      componentSettings.put(StandardPackageValidator.VALIDATE_ACTUAL_PARAMETER_EXPRESSIONS,
                            new Boolean(properties.getProperty(StandardPackageValidator.VALIDATE_ACTUAL_PARAMETER_EXPRESSIONS,
                                                               "true")
                               .equals("true")));
      componentSettings.put(StandardPackageValidator.VALIDATE_CONDITION_EXPRESSIONS,
                            new Boolean(properties.getProperty(StandardPackageValidator.VALIDATE_CONDITION_EXPRESSIONS,
                                                               "true")
                               .equals("true")));
      componentSettings.put(StandardPackageValidator.VALIDATE_CONDITION_BY_TYPE,
                            new Boolean(properties.getProperty(StandardPackageValidator.VALIDATE_CONDITION_BY_TYPE,
                                                               "true")
                               .equals("true")));
      componentSettings.put(StandardPackageValidator.VALIDATE_DEADLINE_EXPRESSIONS,
                            new Boolean(properties.getProperty(StandardPackageValidator.VALIDATE_DEADLINE_EXPRESSIONS,
                                                               "true")
                               .equals("true")));
      componentSettings.put(StandardPackageValidator.VALIDATE_UNUSED_VARIABLES,
                            new Boolean(properties.getProperty(StandardPackageValidator.VALIDATE_UNUSED_VARIABLES,
                                                               "true")
                               .equals("true")));
      componentSettings.put(StandardPackageValidator.ALLOW_UNDEFINED_START,
                            new Boolean(properties.getProperty(StandardPackageValidator.ALLOW_UNDEFINED_START,
                                                               "true")
                               .equals("true")));
      componentSettings.put(StandardPackageValidator.ALLOW_UNDEFINED_END,
                            new Boolean(properties.getProperty(StandardPackageValidator.ALLOW_UNDEFINED_END,
                                                               "true")
                               .equals("true")));

   }

   public boolean shouldValidateSubflowReferences() {
      return ((Boolean) componentSettings.get(StandardPackageValidator.VALIDATE_SUBFLOW_REFERENCES)).booleanValue();
   }

   public boolean shouldValidatePeformerExpressions() {
      return ((Boolean) componentSettings.get(StandardPackageValidator.VALIDATE_PERFORMER_EXPRESSIONS)).booleanValue();
   }

   public boolean shouldValidateActualParameterExpressions() {
      return ((Boolean) componentSettings.get(StandardPackageValidator.VALIDATE_ACTUAL_PARAMETER_EXPRESSIONS)).booleanValue();
   }

   public boolean shouldValidateDeadlineExpressions() {
      return ((Boolean) componentSettings.get(StandardPackageValidator.VALIDATE_DEADLINE_EXPRESSIONS)).booleanValue();
   }

   public boolean shouldValidateConditionExpressions() {
      return ((Boolean) componentSettings.get(StandardPackageValidator.VALIDATE_CONDITION_EXPRESSIONS)).booleanValue();
   }

   public boolean shouldValidateUnusedVariables() {
      return ((Boolean) componentSettings.get(StandardPackageValidator.VALIDATE_UNUSED_VARIABLES)).booleanValue();
   }

   public boolean shouldValidateConditionByType() {
      return ((Boolean) componentSettings.get(StandardPackageValidator.VALIDATE_CONDITION_BY_TYPE)).booleanValue();
   }

   public boolean shouldAllowUndefinedStart() {
      return ((Boolean) componentSettings.get(StandardPackageValidator.ALLOW_UNDEFINED_START)).booleanValue();
   }

   public boolean shouldAllowUndefinedEnd() {
      return ((Boolean) componentSettings.get(StandardPackageValidator.ALLOW_UNDEFINED_END)).booleanValue();
   }

   public Properties getProperties() {
      Properties props = new Properties();
      props.putAll(componentSettings);
      props.put(StandardPackageValidator.VALIDATE_SUBFLOW_REFERENCES,
                componentSettings.get(StandardPackageValidator.VALIDATE_SUBFLOW_REFERENCES)
                   .toString());
      props.put(StandardPackageValidator.VALIDATE_PERFORMER_EXPRESSIONS,
                componentSettings.get(StandardPackageValidator.VALIDATE_PERFORMER_EXPRESSIONS)
                   .toString());
      props.put(StandardPackageValidator.VALIDATE_ACTUAL_PARAMETER_EXPRESSIONS,
                componentSettings.get(StandardPackageValidator.VALIDATE_ACTUAL_PARAMETER_EXPRESSIONS)
                   .toString());
      props.put(StandardPackageValidator.VALIDATE_DEADLINE_EXPRESSIONS,
                componentSettings.get(StandardPackageValidator.VALIDATE_DEADLINE_EXPRESSIONS)
                   .toString());
      props.put(StandardPackageValidator.VALIDATE_UNUSED_VARIABLES,
                componentSettings.get(StandardPackageValidator.VALIDATE_UNUSED_VARIABLES)
                   .toString());
      props.put(StandardPackageValidator.VALIDATE_CONDITION_EXPRESSIONS,
                componentSettings.get(StandardPackageValidator.VALIDATE_CONDITION_EXPRESSIONS)
                   .toString());
      props.put(StandardPackageValidator.VALIDATE_CONDITION_BY_TYPE,
                componentSettings.get(StandardPackageValidator.VALIDATE_CONDITION_BY_TYPE)
                   .toString());
      props.put(StandardPackageValidator.ALLOW_UNDEFINED_START,
                componentSettings.get(StandardPackageValidator.ALLOW_UNDEFINED_START)
                   .toString());
      props.put(StandardPackageValidator.ALLOW_UNDEFINED_END,
                componentSettings.get(StandardPackageValidator.ALLOW_UNDEFINED_END)
                   .toString());
      return props;
   }

}
