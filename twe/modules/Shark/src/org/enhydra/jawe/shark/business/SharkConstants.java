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
import java.util.Collections;
import java.util.List;

/**
 * @author Sasa Bojanic
 */
public class SharkConstants {

   public static final String SCRIPT_VALUE_JAVASCRIPT = "text/javascript";

   public static final String SCRIPT_VALUE_JAVA = "text/java";

   public static final String SCRIPT_VALUE_PYTHONSCRIPT = "text/pythonscript";

   public static final String EA_TOOL_AGENT_CLASS = "ToolAgentClass";

   public static final String EA_TOOL_AGENT_CLASS_PROXY = "ToolAgentClassProxy";

   public static final String EA_APP_NAME = "AppName";

   public static final String EA_APP_MODE = "AppMode";

   public static final String EA_SCRIPT = "Script";

   public static final String EA_MAIL_TOOL_AGENT_SEND_EXECUTION_MODE = "MAIL_TOOL_AGENT_SEND_EXECUTION_MODE";

   public static final String EA_ALLOW_UNDEFINED_VARIABLES = "ALLOW_UNDEFINED_VARIABLES";

   public static final String EA_USE_PROCESS_CONTEXT_ONLY = "USE_PROCESS_CONTEXT_ONLY";

   public static final String EA_CREATE_ASSIGNMENTS = "CREATE_ASSIGNMENTS";

   public static final String EA_TRANSIENT = "TRANSIENT";

   public static final String EA_DELETE_FINISHED = "DELETE_FINISHED";

   public static final String EA_DYNAMIC_VARIABLE_HANDLING = "DYNAMIC_VARIABLE_HANDLING";

   public static final String EA_CHOOSE_NEXT_PERFORMER = "CHOOSE_NEXT_PERFORMER";

   public static final String EA_MAX_ASSIGNMENTS = "MAX_ASSIGNMENTS";

   public static final String EA_WORKLOAD_FACTOR = "WORKLOAD_FACTOR";

   public static final String EA_XFORMS_FILE = "XFORMS_FILE";

   public static final String EA_CHECK_FOR_FIRST_ACTIVITY = "CHECK_FOR_FIRST_ACTIVITY";

   public static final String EA_CHECK_FOR_CONTINUATION = "CHECK_FOR_CONTINUATION";

   public static final String EA_REDIRECT_AFTER_PROCESS_END = "REDIRECT_AFTER_PROCESS_END";

   public static final String EA_CHECK_FOR_COMPLETION = "CHECK_FOR_COMPLETION";

   public static final String CONFIGURE_SMTP_EVENT_AUDIT_MANAGER = "ConfigureEmail";

   public static final String SMTP_MODE = "SMTP_MODE";

   public static final String SMTP_EXECUTION_MODE = "SMTP_EXECUTION_MODE";

   public static final String SMTP_GROUP_EMAIL_ONLY = "SMTP_GROUP_EMAIL_ONLY";

   public static final String SMTP_SUBJECT = "SMTP_SUBJECT";

   public static final String SMTP_CONTENT = "SMTP_CONTENT";

   public static final String SMTP_ATTACHMENTS = "SMTP_ATTACHMENTS";

   public static final String SMTP_ATTACHMENT_NAMES = "SMTP_ATTACHMENT_NAMES";

   public static final String SMTP_DM_ATTACHMENTS = "SMTP_DM_ATTACHMENTS";

   public static final String SMTP_RECIPIENT_PARTICIPANT = "SMTP_RECIPIENT_PARTICIPANT";

   public static final String SMTP_EVENT_AUDIT_MANAGER_MODE = "SMTP_EVENT_AUDIT_MANAGER_MODE";

   public static final String SMTP_EVENT_AUDIT_MANAGER_EXECUTION_MODE = "SMTP_EVENT_AUDIT_MANAGER_EXECUTION_MODE";

   public static final String SMTP_EVENT_AUDIT_MANAGER_GROUP_EMAIL_ONLY = "SMTP_EVENT_AUDIT_MANAGER_GROUP_EMAIL_ONLY";

   public static final String SMTP_EVENT_AUDIT_MANAGER_SUBJECT = "SMTP_EVENT_AUDIT_MANAGER_SUBJECT";

   public static final String SMTP_EVENT_AUDIT_MANAGER_CONTENT = "SMTP_EVENT_AUDIT_MANAGER_CONTENT";

   public static final String SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS = "SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS";

   public static final String SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES = "SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES";

   public static final String SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS = "SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS";

   public static final String CONFIGURE_ERROR_HANDLER = "ConfigureErrorHandler";

   public static final String EA_SMTP_ERROR_HANDLER_EXECUTION_MODE = "SMTP_ERROR_HANDLER_EXECUTION_MODE";

   public static final String EA_SMTP_ERROR_HANDLER_GROUP_EMAIL_ONLY = "SMTP_ERROR_HANDLER_GROUP_EMAIL_ONLY";

   public static final String EA_SMTP_ERROR_HANDLER_MODE = "SMTP_ERROR_HANDLER_MODE";

   public static final String EA_SMTP_ERROR_HANDLER_SUBJECT = "SMTP_ERROR_HANDLER_SUBJECT";

   public static final String EA_SMTP_ERROR_HANDLER_CONTENT = "SMTP_ERROR_HANDLER_CONTENT";

   public static final String EA_SMTP_ERROR_HANDLER_ATTACHMENTS = "SMTP_ERROR_HANDLER_ATTACHMENTS";

   public static final String EA_SMTP_ERROR_HANDLER_ATTACHMENT_NAMES = "SMTP_ERROR_HANDLER_ATTACHMENT_NAMES";

   public static final String EA_SMTP_ERROR_HANDLER_DM_ATTACHMENTS = "SMTP_ERROR_HANDLER_DM_ATTACHMENTS";

   public static final String EA_SMTP_ERROR_HANDLER_RECIPIENT_PARTICIPANT = "SMTP_ERROR_HANDLER_RECIPIENT_PARTICIPANT";

   public static final String EA_ERROR_HANDLER_RETURN_CODE = "SMTP_HANDLER_RETURN_CODE";

   public static final String EA_SMTP_DEADLINE_HANDLER_EXECUTION_MODE = "SMTP_DEADLINE_HANDLER_EXECUTION_MODE";

   public static final String EA_SMTP_DEADLINE_HANDLER_GROUP_EMAIL_ONLY = "SMTP_DEADLINE_HANDLER_GROUP_EMAIL_ONLY";

   public static final String EA_SMTP_DEADLINE_HANDLER_MODE = "SMTP_DEADLINE_HANDLER_MODE";

   public static final String EA_SMTP_DEADLINE_HANDLER_SUBJECT = "SMTP_DEADLINE_HANDLER_SUBJECT";

   public static final String EA_SMTP_DEADLINE_HANDLER_CONTENT = "SMTP_DEADLINE_HANDLER_CONTENT";

   public static final String EA_SMTP_DEADLINE_HANDLER_ATTACHMENTS = "SMTP_DEADLINE_HANDLER_ATTACHMENTS";

   public static final String EA_SMTP_DEADLINE_HANDLER_ATTACHMENT_NAMES = "SMTP_DEADLINE_HANDLER_ATTACHMENT_NAMES";

   public static final String EA_SMTP_DEADLINE_HANDLER_DM_ATTACHMENTS = "SMTP_DEADLINE_HANDLER_DM_ATTACHMENTS";

   public static final String EA_SMTP_DEADLINE_HANDLER_RECIPIENT_PARTICIPANT = "SMTP_DEADLINE_HANDLER_RECIPIENT_PARTICIPANT";

   public static final String[] SMTP_DEADLINE_HANDLER_POSSIBLE_EAS = new String[] {
         EA_SMTP_DEADLINE_HANDLER_ATTACHMENT_NAMES,
         EA_SMTP_DEADLINE_HANDLER_ATTACHMENTS,
         EA_SMTP_DEADLINE_HANDLER_CONTENT,
         EA_SMTP_DEADLINE_HANDLER_DM_ATTACHMENTS,
         EA_SMTP_DEADLINE_HANDLER_EXECUTION_MODE,
         EA_SMTP_DEADLINE_HANDLER_GROUP_EMAIL_ONLY,
         EA_SMTP_DEADLINE_HANDLER_MODE,
         EA_SMTP_DEADLINE_HANDLER_RECIPIENT_PARTICIPANT,
         EA_SMTP_DEADLINE_HANDLER_SUBJECT
   };

   public static final List<String> SMTP_DEADLINE_HANDLER_POSSIBLE_EAS_LIST = Collections.unmodifiableList(Arrays.asList(SMTP_DEADLINE_HANDLER_POSSIBLE_EAS));

   public static final String SMTP_LIMIT_HANDLER_PREFIX = "SMTP_LIMIT_HANDLER_";

   public static final String SMTP_LIMIT_HANDLER_EXECUTION_MODE = "SMTP_LIMIT_HANDLER_EXECUTION_MODE";

   public static final String SMTP_LIMIT_HANDLER_GROUP_EMAIL_ONLY = "SMTP_LIMIT_HANDLER_GROUP_EMAIL_ONLY";

   public static final String SMTP_LIMIT_HANDLER_MODE = "SMTP_LIMIT_HANDLER_MODE";

   public static final String SMTP_LIMIT_HANDLER_SUBJECT = "SMTP_LIMIT_HANDLER_SUBJECT";

   public static final String SMTP_LIMIT_HANDLER_CONTENT = "SMTP_LIMIT_HANDLER_CONTENT";

   public static final String SMTP_LIMIT_HANDLER_ATTACHMENTS = "SMTP_LIMIT_HANDLER_ATTACHMENTS";

   public static final String SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES = "SMTP_LIMIT_HANDLER_ATTACHMENT_NAMES";

   public static final String SMTP_LIMIT_HANDLER_DM_ATTACHMENTS = "SMTP_LIMIT_HANDLER_DM_ATTACHMENTS";

   public static final String SMTP_LIMIT_HANDLER_RECIPIENT_PARTICIPANT = "SMTP_LIMIT_HANDLER_RECIPIENT_PARTICIPANT";

   public static final String EA_IS_MANDATORY = "IS_MANDATORY";

   public static final String EA_MIN_LENGTH = "MIN_LENGTH";

   public static final String EA_DYNAMICSCRIPT = "DYNAMICSCRIPT";

   public static final String VTP_UPDATE = "VariableToProcess_UPDATE";

   public static final String VTP_VIEW = "VariableToProcess_VIEW";

   public static final String EA_VALUE_TRUE = "true";

   public static final String EA_VALUE_FALSE = "false";

   public static final String TOOL_AGENT_BEAN_SHELL = "org.enhydra.shark.toolagent.BshToolAgent";

   public static final String TOOL_AGENT_CHECKDOCUMENTFORMATS = "org.enhydra.shark.toolagent.CheckDocumentFormatsToolAgent";

   public static final String TOOL_AGENT_JAVACLASS = "org.enhydra.shark.toolagent.JavaClassToolAgent";

   public static final String TOOL_AGENT_JAVASCRIPT = "org.enhydra.shark.toolagent.JavaScriptToolAgent";

   public static final String TOOL_AGENT_LDAP = "org.enhydra.shark.toolagent.LDAPToolAgent";

   public static final String TOOL_AGENT_MAIL = "org.enhydra.shark.toolagent.MailToolAgent";

   public static final String TOOL_AGENT_QUARTZ = "org.enhydra.shark.toolagent.QuartzToolAgent";

   public static final String TOOL_AGENT_RUNTIMEAPPLICATION = "org.enhydra.shark.toolagent.RuntimeApplicationToolAgent";

   public static final String TOOL_AGENT_SCHEDULER = "org.enhydra.shark.toolagent.SchedulerToolAgent";

   public static final String TOOL_AGENT_SOAP = "org.enhydra.shark.toolagent.SOAPToolAgent";

   public static final String TOOL_AGENT_USERGROUP = "org.enhydra.shark.toolagent.UserGroupToolAgent";

   public static final String TOOL_AGENT_XPATH = "org.enhydra.shark.toolagent.XPathToolAgent";

   public static final String TOOL_AGENT_XPIL = "org.enhydra.shark.toolagent.XPILToolAgent";

   public static final String TOOL_AGENT_XSLT = "org.enhydra.shark.toolagent.XSLTToolAgent";

   public static final String TOOL_AGENT_FORMAL_PARAMETER_RESULT_SL = "result";

   public static final String TOOL_AGENT_FORMAL_PARAMETER_SOURCE = "source";

   public static final String TOOL_AGENT_FORMAL_PARAMETER_RESULT_CL = "Result";

   public static final String TOOL_AGENT_FORMAL_PARAMETER_ALLOWED_DOCUMENT_FORMATS = "ALLOWED_DOCUMENT_FORMATS";

   public static final String TOOL_AGENT_FORMAL_PARAMETER_UNSUPPORTED_DOCUMENT_IDS = "UNSUPPORTED_DOCUMENT_IDS";

   public static final String TOOL_AGENT_FORMAL_PARAMETER_TRANSFORMER_NAME = "transformer_name";

   public static final String TOOL_AGENT_FORMAL_PARAMETER_TRANSFORMER_PATH = "transformer_path";

   public static final String TOOL_AGENT_FORMAL_PARAMETER_TRANSFORMER_NODE = "transformer_node";

   public static final String TOOL_AGENT_FORMAL_PARAMETER_TRANSFORMER_SCRIPT = "transformer_script";

   public static final String TOOL_AGENT_FORMAL_PARAMETER_NODE = "Node";

   public static final String TOOL_AGENT_FORMAL_PARAMETER_EXPRESSIONS = "Expressions";

   public static final String TOOL_AGENT_FORMAL_PARAMETER_RESULT_VARIABLE_IDS = "ResultVariableIds";

   public static final String TOOL_AGENT_FORMAL_PARAMETER_PREFIX_XML_PREFIX_AND_NAMESPACE_URI = "XML_PREFIX_AND_NAMESPACE_URI";

   public static final String TOOL_AGENT_FORMAL_PARAMETER_XPIL = "XPIL";

   public static final String TOOL_AGENT_FORMAL_PARAMETER_METHOD = "Method";

   public static final String TOOL_AGENT_FORMAL_PARAMETER_ARG1 = "Arg1";

   public static final String TOOL_AGENT_FORMAL_PARAMETER_ARG2 = "Arg2";

   public static final String TOOL_AGENT_FORMAL_PARAMETER_RESULT_VARIABLE_ID = "ResultVariableId";

   public static final String TOOL_AGENT_FORMAL_PARAMETER_NAME = "Name";

   public static final String TOOL_AGENT_FORMAL_PARAMETER_TO_ADDRESSES = "to_addresses";

   public static final String TOOL_AGENT_FORMAL_PARAMETER_CC_ADDRESSES = "cc_addresses";

   public static final String TOOL_AGENT_FORMAL_PARAMETER_BCC_ADDRESSES = "bcc_addresses";

   public static final String TOOL_AGENT_LDAP_METHOD_getAllGroupEntries = "getAllGroupEntries";

   public static final String TOOL_AGENT_LDAP_METHOD_getAllOrganizationalUnitEntries = "getAllOrganizationalUnitEntries";

   public static final String TOOL_AGENT_LDAP_METHOD_getAllUserEntries = "getAllUserEntries";

   public static final String TOOL_AGENT_LDAP_METHOD_getAllUserEntriesForGroup = "getAllUserEntriesForGroup";

   public static final String TOOL_AGENT_LDAP_METHOD_getAllImmediateUserEntries = "getAllImmediateUserEntries";

   public static final String TOOL_AGENT_LDAP_METHOD_getAllSubOrganizationalUnitEntries = "getAllSubOrganizationalUnitEntries";

   public static final String TOOL_AGENT_LDAP_METHOD_getAllImmediateSubOrganizationalUnitEntries = "getAllImmediateSubOrganizationalUnitEntries";

   public static final String TOOL_AGENT_LDAP_METHOD_getUserAttribute = "getUserAttribute";

   public static final String TOOL_AGENT_LDAP_METHOD_getGroupAttribute = "getGroupAttribute";

   public static final String TOOL_AGENT_LDAP_METHOD_doesGroupExist = "doesGroupExist";

   public static final String TOOL_AGENT_LDAP_METHOD_doesGroupBelongToGroup = "doesGroupBelongToGroup";

   public static final String TOOL_AGENT_LDAP_METHOD_doesUserBelongToGroup = "doesUserBelongToGroup";

   public static final String TOOL_AGENT_LDAP_METHOD_doesUserExist = "doesUserExist";

   public static final String TOOL_AGENT_LDAP_METHOD_getUserByEmail = "getUserByEmail";

   public static final String TOOL_AGENT_LDAP_METHOD_checkPassword = "checkPassword";

   public static final String[] TOOL_AGENT_LDAP_POSSIBLE_METHODS = new String[] {
         TOOL_AGENT_LDAP_METHOD_checkPassword,
         TOOL_AGENT_LDAP_METHOD_doesGroupBelongToGroup,
         TOOL_AGENT_LDAP_METHOD_doesGroupExist,
         TOOL_AGENT_LDAP_METHOD_doesUserBelongToGroup,
         TOOL_AGENT_LDAP_METHOD_doesUserExist,
         TOOL_AGENT_LDAP_METHOD_getAllGroupEntries,
         TOOL_AGENT_LDAP_METHOD_getAllImmediateSubOrganizationalUnitEntries,
         TOOL_AGENT_LDAP_METHOD_getAllImmediateUserEntries,
         TOOL_AGENT_LDAP_METHOD_getAllOrganizationalUnitEntries,
         TOOL_AGENT_LDAP_METHOD_getAllSubOrganizationalUnitEntries,
         TOOL_AGENT_LDAP_METHOD_getAllUserEntries,
         TOOL_AGENT_LDAP_METHOD_getAllUserEntriesForGroup,
         TOOL_AGENT_LDAP_METHOD_getGroupAttribute,
         TOOL_AGENT_LDAP_METHOD_getUserAttribute,
         TOOL_AGENT_LDAP_METHOD_getUserByEmail
   };

   public static final List<String> TOOL_AGENT_LDAP_POSSIBLE_METHODS_LIST = Collections.unmodifiableList(Arrays.asList(SharkConstants.TOOL_AGENT_LDAP_POSSIBLE_METHODS));

   public static final String TOOL_AGENT_USERGROUP_METHOD_getAllGroups = "getAllGroups";

   public static final String TOOL_AGENT_USERGROUP_METHOD_getAllGroupsForUser = "getAllGroupsForUser";

   public static final String TOOL_AGENT_USERGROUP_METHOD_getAllUsers = "getAllUsers";

   public static final String TOOL_AGENT_USERGROUP_METHOD_getAllUsersForGroups = "getAllUsersForGroups";

   public static final String TOOL_AGENT_USERGROUP_METHOD_getAllImmediateUsersForGroup = "getAllImmediateUsersForGroup";

   public static final String TOOL_AGENT_USERGROUP_METHOD_getAllSubgroupsForGroups = "getAllSubgroupsForGroups";

   public static final String TOOL_AGENT_USERGROUP_METHOD_getAllImmediateSubgroupsForGroup = "getAllImmediateSubgroupsForGroup";

   public static final String TOOL_AGENT_USERGROUP_METHOD_doesGroupExist = "doesGroupExist";

   public static final String TOOL_AGENT_USERGROUP_METHOD_doesGroupBelongToGroup = "doesGroupBelongToGroup";

   public static final String TOOL_AGENT_USERGROUP_METHOD_getGroupName = "getGroupName";

   public static final String TOOL_AGENT_USERGROUP_METHOD_getGroupDescription = "getGroupDescription";

   public static final String TOOL_AGENT_USERGROUP_METHOD_getGroupEMailAddress = "getGroupEMailAddress";

   public static final String TOOL_AGENT_USERGROUP_METHOD_doesUserBelongToGroup = "doesUserBelongToGroup";

   public static final String TOOL_AGENT_USERGROUP_METHOD_doesUserExist = "doesUserExist";

   public static final String TOOL_AGENT_USERGROUP_METHOD_getUserPassword = "getUserPassword";

   public static final String TOOL_AGENT_USERGROUP_METHOD_getUserRealName = "getUserRealName";

   public static final String TOOL_AGENT_USERGROUP_METHOD_getUserFirstName = "getUserFirstName";

   public static final String TOOL_AGENT_USERGROUP_METHOD_getUserLastName = "getUserLastName";

   public static final String TOOL_AGENT_USERGROUP_METHOD_getUserEMailAddress = "getUserEMailAddress";

   public static final String TOOL_AGENT_USERGROUP_METHOD_getUserAttribute = "getUserAttribute";

   public static final String TOOL_AGENT_USERGROUP_METHOD_getGroupAttribute = "getGroupAttribute";

   public static final String TOOL_AGENT_USERGROUP_METHOD_getObjects = "getObjects";

   public static final String TOOL_AGENT_USERGROUP_METHOD_getGroups = "getGroups";

   public static final String TOOL_AGENT_USERGROUP_METHOD_validateUser = "validateUser";

   public static final String[] TOOL_AGENT_USERGROUP_POSSIBLE_METHODS = new String[] {
         TOOL_AGENT_USERGROUP_METHOD_doesGroupBelongToGroup,
         TOOL_AGENT_USERGROUP_METHOD_doesGroupExist,
         TOOL_AGENT_USERGROUP_METHOD_doesUserBelongToGroup,
         TOOL_AGENT_USERGROUP_METHOD_doesUserExist,
         TOOL_AGENT_USERGROUP_METHOD_getAllGroups,
         TOOL_AGENT_USERGROUP_METHOD_getAllGroupsForUser,
         TOOL_AGENT_USERGROUP_METHOD_getAllImmediateSubgroupsForGroup,
         TOOL_AGENT_USERGROUP_METHOD_getAllImmediateUsersForGroup,
         TOOL_AGENT_USERGROUP_METHOD_getAllSubgroupsForGroups,
         TOOL_AGENT_USERGROUP_METHOD_getAllUsers,
         TOOL_AGENT_USERGROUP_METHOD_getAllUsersForGroups,
         TOOL_AGENT_USERGROUP_METHOD_getGroupAttribute,
         TOOL_AGENT_USERGROUP_METHOD_getGroupDescription,
         TOOL_AGENT_USERGROUP_METHOD_getGroupEMailAddress,
         TOOL_AGENT_USERGROUP_METHOD_getGroupName,
         TOOL_AGENT_USERGROUP_METHOD_getGroups,
         TOOL_AGENT_USERGROUP_METHOD_getObjects,
         TOOL_AGENT_USERGROUP_METHOD_getUserAttribute,
         TOOL_AGENT_USERGROUP_METHOD_getUserEMailAddress,
         TOOL_AGENT_USERGROUP_METHOD_getUserFirstName,
         TOOL_AGENT_USERGROUP_METHOD_getUserLastName,
         TOOL_AGENT_USERGROUP_METHOD_getUserPassword,
         TOOL_AGENT_USERGROUP_METHOD_getUserRealName,
         TOOL_AGENT_USERGROUP_METHOD_validateUser
   };

   public static final List<String> TOOL_AGENT_USERGROUP_POSSIBLE_METHODS_LIST = Collections.unmodifiableList(Arrays.asList(SharkConstants.TOOL_AGENT_USERGROUP_POSSIBLE_METHODS));

   public static final String SHARK_PROCESS_DEFINITION_ID = "shark_process_definition_id";

   public static final String SHARK_PROCESS_DEFINITION_NAME = "shark_process_definition_name";

   public static final String SHARK_PROCESS_ID = "shark_process_id";

   public static final String SHARK_PROCESS_NAME = "shark_process_name";

   public static final String SHARK_PROCESS_DESCRIPTION = "shark_process_description";

   public static final String SHARK_PROCESS_PRIORITY = "shark_process_priority";

   public static final String SHARK_PROCESS_LIMIT_TIME = "shark_process_limit_time";

   public static final String SHARK_PROCESS_CREATED_TIME = "shark_process_created_time";

   public static final String SHARK_PROCESS_CREATED_BY = "shark_process_created_by";

   public static final String SHARK_PROCESS_STARTED_TIME = "shark_process_started_time";

   public static final String SHARK_PROCESS_STARTED_BY = "shark_process_started_by";

   public static final String SHARK_PROCESS_FINISHED_TIME = "shark_process_finished_time";

   public static final String SHARK_PROCESS_FINISHED_BY = "shark_process_finished_by";

   public static final String SHARK_PROCESS_STATE = "shark_process_state";

   public static final String SHARK_ACTIVITY_DEFINITION_ID = "shark_activity_definition_id";

   public static final String SHARK_ACTIVITY_DEFINITION_NAME = "shark_activity_definition_name";

   public static final String SHARK_ACTIVITY_ID = "shark_activity_id";

   public static final String SHARK_ACTIVITY_NAME = "shark_activity_name";

   public static final String SHARK_ACTIVITY_DESCRIPTION = "shark_activity_description";

   public static final String SHARK_ACTIVITY_PRIORITY = "shark_activity_priority";

   public static final String SHARK_ACTIVITY_LIMIT_TIME = "shark_activity_limit_time";

   public static final String SHARK_ACTIVITY_CREATED_TIME = "shark_activity_created_time";

   public static final String SHARK_ACTIVITY_STARTED_TIME = "shark_activity_started_time";

   public static final String SHARK_ACTIVITY_FINISHED_TIME = "shark_activity_finished_time";

   public static final String SHARK_ACTIVITY_STATE = "shark_activity_state";

   public static final String SHARK_ACTIVITY_HANDLED_BY = "shark_activity_handled_by";

   public static final String SHARK_SESSION_HANDLE = "shark_session_handle";

   public static final String SHARK_VERSION = "shark_version";

   public static final String SHARK_RELEASE = "shark_release";

   public static final String SHARK_BUILDID = "shark_buildid";

   public static final String SHARK_XPDL_ID = "shark_xpdl_id";

   public static final String SHARK_XPDL_VERSION = "shark_xpdl_version";

   public static final String SHARK_XPDL_UPLOAD_TIME = "shark_xpdl_upload_time";

   public static final String SHARK_USER = "shark_user";

   public static final String SHARK_TIME = "shark_time";

   public static final String SHARK_ACTIVITY_ERROR_MESSAGE = "shark_activity_error_message";

   public static final String SHARK_ACTIVITY_ERROR_STACKTRACE = "shark_activity_error_stacktrace";

   public static final String[] POSSIBLE_SYSTEM_VARIABLES = new String[] {
         SharkConstants.SHARK_PROCESS_DEFINITION_ID,
         SharkConstants.SHARK_PROCESS_DEFINITION_NAME,
         SharkConstants.SHARK_PROCESS_ID,
         SharkConstants.SHARK_PROCESS_NAME,
         SharkConstants.SHARK_PROCESS_DESCRIPTION,
         SharkConstants.SHARK_PROCESS_PRIORITY,
         SharkConstants.SHARK_PROCESS_LIMIT_TIME,
         SharkConstants.SHARK_PROCESS_CREATED_TIME,
         SharkConstants.SHARK_PROCESS_STARTED_TIME,
         SharkConstants.SHARK_PROCESS_FINISHED_TIME,
         SharkConstants.SHARK_PROCESS_STATE,
         SharkConstants.SHARK_PROCESS_CREATED_BY,
         SharkConstants.SHARK_ACTIVITY_DEFINITION_ID,
         SharkConstants.SHARK_ACTIVITY_DEFINITION_NAME,
         SharkConstants.SHARK_ACTIVITY_ID,
         SharkConstants.SHARK_ACTIVITY_NAME,
         SharkConstants.SHARK_ACTIVITY_DESCRIPTION,
         SharkConstants.SHARK_ACTIVITY_PRIORITY,
         SharkConstants.SHARK_ACTIVITY_LIMIT_TIME,
         SharkConstants.SHARK_ACTIVITY_CREATED_TIME,
         SharkConstants.SHARK_ACTIVITY_STARTED_TIME,
         SharkConstants.SHARK_ACTIVITY_FINISHED_TIME,
         SharkConstants.SHARK_ACTIVITY_STATE,
         SharkConstants.SHARK_ACTIVITY_HANDLED_BY,
         SharkConstants.SHARK_ACTIVITY_ERROR_MESSAGE,
         SharkConstants.SHARK_ACTIVITY_ERROR_STACKTRACE,
         SharkConstants.SHARK_SESSION_HANDLE,
         SharkConstants.SHARK_VERSION,
         SharkConstants.SHARK_RELEASE,
         SharkConstants.SHARK_BUILDID,
         SharkConstants.SHARK_XPDL_ID,
         SharkConstants.SHARK_XPDL_VERSION,
         SharkConstants.SHARK_XPDL_UPLOAD_TIME,
         SharkConstants.SHARK_USER,
         SharkConstants.SHARK_TIME
   };

   public static final List<String> possibleSystemVariables = Collections.unmodifiableList(Arrays.asList(SharkConstants.POSSIBLE_SYSTEM_VARIABLES));

   public final static String EA_XPDL_STRING_VARIABLE_PREFIX = "XPDL_STRING_VARIABLE.";

   public static final String XPDL_STRING_PLACEHOLDER_PREFIX = "xpdl_string:";

   public static final String CONFIG_STRING_PLACEHOLDER_PREFIX = "config_string:";

   public static final String PROCESS_VARIABLE_PLACEHOLDER_PREFIX = "process_variable:";

}
