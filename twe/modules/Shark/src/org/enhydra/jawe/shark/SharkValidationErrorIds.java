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

/**
 * @author Sasa Bojanic
 */
public final class SharkValidationErrorIds {

   public static final String ERROR_SCRIPT_NOT_DEFINED = "ERROR_SCRIPT_NOT_DEFINED";

   public static final String ERROR_UNSUPPORTED_SCRIPT = "ERROR_UNSUPPORTED_SCRIPT";

   public static final String ERROR_UNSUPPORTED_DATA_TYPE = "ERROR_UNSUPPORTED_DATA_TYPE";

   public static final String ERROR_MANUAL_START_MODE_FOR_TOOL_ACTIVITY_WITH_SYSTEM_PARTICIPANT_PERFORMER = "ERROR_MANUAL_START_MODE_FOR_TOOL_ACTIVITY_WITH_SYSTEM_PARTICIPANT_PERFORMER";

   public static final String ERROR_SYSTEM_PARTICIPANT_PERFORMER_FOR_NO_IMPLEMENTATION_ACTIVITY = "ERROR_SYSTEM_PARTICIPANT_PERFORMER_FOR_NO_IMPLEMENTATION_ACTIVITY";

   public static final String WARNING_TOOL_AGENT_CLASS_NOT_DEFINED = "WARNING_TOOL_AGENT_CLASS_NOT_DEFINED";

   public static final String WARNING_NON_EXISTING_VARIABLE_REFERENCE = "WARNING_NON_EXISTING_VARIABLE_REFERENCE";

   public static final String ERROR_TOOL_AGENT_INVALID_FORMAL_PARAMETER_MODE_IN_REQUIRED = "ERROR_TOOL_AGENT_INVALID_FORMAL_PARAMETER_MODE_IN_REQUIRED";

   public static final String ERROR_TOOL_AGENT_INVALID_FORMAL_PARAMETER_MODE_OUT_REQUIRED = "ERROR_TOOL_AGENT_INVALID_FORMAL_PARAMETER_MODE_OUT_REQUIRED";

   public static final String ERROR_TOOL_AGENT_INVALID_PARAMETER_TYPE_BASIC_TYPE_STRING_REQUIRED = "ERROR_TOOL_AGENT_INVALID_PARAMETER_TYPE_BASIC_TYPE_STRING_REQUIRED";

   public static final String ERROR_TOOL_AGENT_INVALID_PARAMETER_TYPE_SCHEMA_TYPE_REQUIRED = "ERROR_TOOL_AGENT_INVALID_PARAMETER_TYPE_SCHEMA_TYPE_REQUIRED";

   public static final String ERROR_TOOL_AGENT_APP_NAME_EXTENDED_ATTRIBUTE_REQUIRED = "ERROR_TOOL_AGENT_APP_NAME_EXTENDED_ATTRIBUTE_REQUIRED";

   public static final String ERROR_TOOL_AGENT_BSH_OR_JS_SCRIPT_PARAMETER_REQUIRED = "ERROR_TOOL_AGENT_BSH_OR_JS_SCRIPT_PARAMETER_REQUIRED";

   public static final String ERROR_TOOL_AGENT_CHECK_DOCUMENT_FORMATS_MISSING_REQUIRED_PARAMETERS = "ERROR_TOOL_AGENT_CHECK_DOCUMENT_FORMATS_MISSING_REQUIRED_PARAMETERS";

   public static final String ERROR_TOOL_AGENT_LDAP_OR_USERGROUP_MISSING_REQUIRED_PARAMETERS = "ERROR_TOOL_AGENT_LDAP_OR_USERGROUP_MISSING_REQUIRED_PARAMETERS";

   public static final String ERROR_TOOL_AGENT_LDAP_OR_USERGROUP_UNEXISTING_METHOD_NAME = "ERROR_TOOL_AGENT_LDAP_OR_USERGROUP_UNEXISTING_METHOD_NAME";

   public static final String ERROR_TOOL_AGENT_LDAP_OR_USERGROUP_INVALID_NUMBER_OF_ARGUMENTS_0_REQUIRED = "ERROR_TOOL_AGENT_LDAP_OR_USERGROUP_INVALID_NUMBER_OF_ARGUMENTS_0_REQUIRED";

   public static final String ERROR_TOOL_AGENT_LDAP_OR_USERGROUP_INVALID_NUMBER_OF_ARGUMENTS_1_REQUIRED = "ERROR_TOOL_AGENT_LDAP_OR_USERGROUP_INVALID_NUMBER_OF_ARGUMENTS_1_REQUIRED";

   public static final String ERROR_TOOL_AGENT_LDAP_OR_USERGROUP_INVALID_NUMBER_OF_ARGUMENTS_2_REQUIRED = "ERROR_TOOL_AGENT_LDAP_OR_USERGROUP_INVALID_NUMBER_OF_ARGUMENTS_2_REQUIRED";

   public static final String ERROR_TOOL_AGENT_MAIL_MISSING_REQUIRED_PARAMETERS = "ERROR_TOOL_AGENT_MAIL_MISSING_REQUIRED_PARAMETERS";

   public static final String ERROR_TOOL_AGENT_QUARTZ_OR_SCHEDULER_TOOL_AGENT_CLASS_PROXY_EXTENDED_ATTRIBUTE_REQUIRED = "ERROR_TOOL_AGENT_QUARTZ_OR_SCHEDULER_TOOL_AGENT_CLASS_PROXY_EXTENDED_ATTRIBUTE_REQUIRED";

   public static final String ERROR_TOOL_AGENT_QUARTZ_OR_SCHEDULER_TOOL_AGENT_CLASS_PROXY_REFERENCE_INVALID = "ERROR_TOOL_AGENT_QUARTZ_OR_SCHEDULER_TOOL_AGENT_CLASS_PROXY_REFERENCE_INVALID";

   public static final String ERROR_TOOL_AGENT_SOAP_MISSING_REQUIRED_PARAMETERS = "ERROR_TOOL_AGENT_SOAP_MISSING_REQUIRED_PARAMETERS";

   public static final String ERROR_TOOL_AGENT_SOAP_INVALID_SOAP_OPERATION_PARAMETER_TYPE = "ERROR_TOOL_AGENT_SOAP_INVALID_SOAP_OPERATION_PARAMETER_TYPE";
   
   public static final String ERROR_TOOL_AGENT_XPATH_MISSING_REQUIRED_PARAMETERS = "ERROR_TOOL_AGENT_XPATH_MISSING_REQUIRED_PARAMETERS";

   public static final String ERROR_TOOL_AGENT_XPIL_XPIL_PARAMETER_REQUIRED = "ERROR_TOOL_AGENT_XPIL_XPIL_PARAMETER_REQUIRED";

   public static final String ERROR_TOOL_AGENT_XSLT_RESULT_PARAMETER_REQUIRED = "ERROR_TOOL_AGENT_XSLT_RESULT_PARAMETER_REQUIRED";

   public static final String ERROR_TOOL_AGENT_XSLT_TRANSFORMER_PARAMETER_REQUIRED = "ERROR_TOOL_AGENT_XSLT_TRANSFORMER_PARAMETER_REQUIRED";
   
   public static final String ERROR_DYNAMICSCRIPT_VARIABLE_SELF_REFERENCES_NOT_ALLOWED = "ERROR_DYNAMICSCRIPT_VARIABLE_SELF_REFERENCES_NOT_ALLOWED";
   
   public static final String ERROR_DYNAMICSCRIPT_VARIABLE_CIRCULAR_REFERENCES_NOT_ALLOWED = "ERROR_DYNAMICSCRIPT_VARIABLE_CIRCULAR_REFERENCES_NOT_ALLOWED";

   public static final String ERROR_DYNAMICSCRIPT_VARIABLE_IMPLICIT_CIRCULAR_REFERENCES_NOT_ALLOWED = "ERROR_DYNAMICSCRIPT_VARIABLE_IMPLICIT_CIRCULAR_REFERENCES_NOT_ALLOWED";

   public static final String ERROR_VARIABLE_INITIAL_VALUE_SELF_REFERENCES_NOT_ALLOWED = "ERROR_VARIABLE_INITIAL_VALUE_SELF_REFERENCES_NOT_ALLOWED";
   
   public static final String ERROR_VARIABLE_INITIAL_VALUE_CIRCULAR_REFERENCES_NOT_ALLOWED = "ERROR_VARIABLE_INITIAL_VALUE_CIRCULAR_REFERENCES_NOT_ALLOWED";

   public static final String ERROR_VARIABLE_INITIAL_VALUE_IMPLICIT_CIRCULAR_REFERENCES_NOT_ALLOWED = "ERROR_VARIABLE_INITIAL_VALUE_IMPLICIT_CIRCULAR_REFERENCES_NOT_ALLOWED";
   
   public static final String ERROR_VARIABLE_INITIAL_VALUE_DYNAMICSCRIPT_VARIABLE_REFERENCES_NOT_ALLOWED = "ERROR_VARIABLE_INITIAL_VALUE_DYNAMICSCRIPT_VARIABLE_REFERENCES_NOT_ALLOWED";

   public static final String ERROR_SHARK_STRING_VARIABLE_SELF_REFERENCES_NOT_ALLOWED = "ERROR_SHARK_STRING_VARIABLE_SELF_REFERENCES_NOT_ALLOWED";
   
   public static final String ERROR_SHARK_STRING_VARIABLE_CIRCULAR_REFERENCES_NOT_ALLOWED = "ERROR_SHARK_STRING_VARIABLE_CIRCULAR_REFERENCES_NOT_ALLOWED";

   public static final String ERROR_SHARK_STRING_VARIABLE_IMPLICIT_CIRCULAR_REFERENCES_NOT_ALLOWED = "ERROR_SHARK_STRING_VARIABLE_IMPLICIT_CIRCULAR_REFERENCES_NOT_ALLOWED";

   public static final String ERROR_NON_EXISTING_SYSTEM_VARIABLE_REFERENCE = "ERROR_NON_EXISTING_SYSTEM_VARIABLE_REFERENCE";

   public static final String WARNING_NON_EXISTING_CONFIG_STRING_VARIABLE_REFERENCE = "WARNING_NON_EXISTING_CONFIG_STRING_VARIABLE_REFERENCE";

   public static final String ERROR_NON_EXISTING_SHARK_STRING_VARIABLE_REFERENCE = "ERROR_NON_EXISTING_SHARK_STRING_VARIABLE_REFERENCE";

   public static final String WARNING_NON_EXISTING_SHARK_STRING_VARIABLE_REFERENCE = "WARNING_NON_EXISTING_SHARK_STRING_VARIABLE_REFERENCE";
   
}
