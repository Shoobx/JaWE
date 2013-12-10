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

/* JaWEConstants.java
 *
 * Authors:
 * Stefanovic Nenad  chupo@iis.ns.ac.yu
 * Bojanic Sasa      sasaboy@neobee.net
 * Puskas Vladimir   vpuskas@eunet.yu
 * Pilipovic Goran   zboniek@uns.ac.yu
 *
 */

package org.enhydra.jawe;

/**
 * Used to hold various static variables to be easily accessed from anywhere.
 */
public class JaWEConstants {

   public static final String JAWE_CURRENT_CONFIG_HOME = "JaWE_CURRENT_CONFIG_HOME";

   public static final String JAWE_CONFIG_NAME = "JaWE_CONFIG_NAME";

   // --------------------------
   public static final String JAWE_HOME = System.getProperty("JaWE_HOME");

   public static final String JAWE_CONF_HOME = ((JAWE_HOME != null) ? (JAWE_HOME + "/" + "config")
                                                                   : (null));

   public static final String JAWE_USER_HOME = System.getProperty("user.home") + "/.JaWE";

   public static final String JAWE_BASIC_PROPERTYFILE_PATH = "org/enhydra/jawe/properties/";

   public static final String JAWE_BASIC_PROPERTYFILE_NAME = "jaweconfiguration.properties";

   public static final String JAWE_LANGUAGE_MISC_PROPERTYFILE_PATH = "org/enhydra/jawe/language/";

   public static final String JAWE_LANGUAGE_MISC_PROPERTYFILE_NAME = "jawelanguagemisc.properties";

   public static final String JAWE_ACTIVITY_ICONS = "org/enhydra/jawe/activityicons";

   // --------------------------
   // JaWE types
   // Activity
   public static final String ACTIVITY_TYPE = "ACTIVITY";

   public static final String ACTIVITY_TYPE_BLOCK = "ACTIVITY_BLOCK";

   public static final String ACTIVITY_TYPE_NO = "ACTIVITY_NO";

   public static final String ACTIVITY_TYPE_ROUTE = "ACTIVITY_ROUTE";

   public static final String ACTIVITY_TYPE_ROUTE_PARALLEL = "ACTIVITY_ROUTE_PARALLEL";

   public static final String ACTIVITY_TYPE_ROUTE_EXCLUSIVE = "ACTIVITY_ROUTE_EXCLUSIVE";

   public static final String ACTIVITY_TYPE_ROUTE_INCLUSIVE = "ACTIVITY_ROUTE_INCLUSIVE";

   public static final String ACTIVITY_TYPE_SUBFLOW = "ACTIVITY_SUBFLOW";

   public static final String ACTIVITY_TYPE_TOOL = "ACTIVITY_TOOL";

   public static final String ACTIVITY_TYPE_START = "ACTIVITY_START";

   public static final String ACTIVITY_TYPE_END = "ACTIVITY_END";

   public static final String ACTIVITY_TYPE_TASK_APPLICATION = "ACTIVITY_TASK_APPLICATION";

   // Activity set
   public static final String ACTIVITY_SET_TYPE_DEFAULT = "ACTIVITYSET_DEFAULT";

   // Actual parameter
   public static final String ACTUAL_PARAMETER_DEFAULT = "ACTUALPARAMETER_DEFAULT";

   // Application type
   public static final String APPLICATION_TYPE_DEFAULT = "APPLICATION_DEFAULT";

   // Artifact type
   public static final String ARTIFACT_TYPE = "ARTIFACT";
   public static final String ARTIFACT_TYPE_DATA_OBJECT = "ARTIFACT_DATA_OBJECT";
   public static final String ARTIFACT_TYPE_ANNOTATION = "ARTIFACT_ANNOTATION";

   // Association type
   public static final String ASSOCIATION_TYPE = "ASSOCIATION";
   public static final String ASSOCIATION_TYPE_DEFAULT = "ASSOCIATION_DEFAULT";
   public static final String ASSOCIATION_TYPE_NONE = "ASSOCIATION_NONE";
   public static final String ASSOCIATION_TYPE_BIDIRECTIONAL = "ASSOCIATION_BIDIRECTIONAL";

   // Data field
   public static final String DATA_FIELD_DEFAULT = "DATAFIELD_DEFAULT";

   // Deadline
   public static final String DEADLINE_DEFAULT = "DEADLINE_DEFAULT";

   // Enumeration
   public static final String ENUMERATION_VALUE_DEFAULT = "ENUMERATIONVALUE_DEFAULT";

   // Extended attribute
   public static final String EXTENDED_ATTRIBUTE_DEFAULT = "EXTENDEDATTRIBUTE_DEFAULT";

   // External Package
   public static final String EXTERNAL_PACKAGE_DEFAULT = "EXTERNALPACKAGE_DEFAULT";

   // Formal parameter
   public static final String FORMAL_PARAMETER_DEFAULT = "FORMALPARAMETER_DEFAULT";

   // LANE
   public static final String LANE_TYPE = "LANE";

   public static final String LANE_TYPE_DEFAULT = "LANE_DEFAULT";

   // Member
   public static final String MEMBER_DEFAULT = "MEMBER_DEFAULT";

   // Namespace
   public static final String NAMESPACE_DEFAULT = "NAMESPACE_DEFAULT";

   // Package
   public static final String PACKAGE_DEFAULT = "PACKAGE_DEFAULT";

   public static final String PACKAGE_EXTERNAL = "PACKAGE_EXTERNAL";

   public static final String PACKAGE_TRANSIENT = "PACKAGE_TRANSIENT";

   // Participant
   public static final String PARTICIPANT_TYPE = "PARTICIPANT";

   public static final String PARTICIPANT_TYPE_HUMAN = "PARTICIPANT_HUMAN";

   public static final String PARTICIPANT_TYPE_ORGANIZATIONAL_UNIT = "PARTICIPANT_ORGANIZATIONAL_UNIT";

   public static final String PARTICIPANT_TYPE_RESOURCE = "PARTICIPANT_RESOURCE";

   public static final String PARTICIPANT_TYPE_RESOURCE_SET = "PARTICIPANT_RESOURCE_SET";

   public static final String PARTICIPANT_TYPE_ROLE = "PARTICIPANT_ROLE";

   public static final String PARTICIPANT_TYPE_SYSTEM = "PARTICIPANT_SYSTEM";

   // POOL
   public static final String POOL_TYPE = "POOL";

   public static final String POOL_TYPE_DEFAULT = "POOL_DEFAULT";

   // Responsible
   public static final String RESPONSIBLE_DEFAULT = "RESPONSIBLE_DEFAULT";

   // Tool
   public static final String TOOL_DEFAULT = "TOOL_DEFAULT";

   // Transitions
   public static final String TRANSITION_TYPE = "TRANSITION";

   public static final String TRANSITION_TYPE_UNCONDITIONAL = "TRANSITION_UNCONDITIONAL";

   public static final String TRANSITION_TYPE_CONDITIONAL = "TRANSITION_CONDITIONAL";

   public static final String TRANSITION_TYPE_DEFAULTEXCEPTION = "TRANSITION_DEFAULTEXCEPTION";

   public static final String TRANSITION_TYPE_EXCEPTION = "TRANSITION_EXCEPTION";

   public static final String TRANSITION_TYPE_OTHERWISE = "TRANSITION_OTHERWISE";

   // Type declaration
   public static final String TYPE_DECLARATION_DEFAULT = "TYPEDECLARATION_DEFAULT";

   // Workflow process
   public static final String WORKFLOW_PROCESS_TYPE_DEFAULT = "WORKFLOWPROCESS_DEFAULT";

   // Activities
   public static final String ACTIVITIES = "ACTIVITIES";

   // ActivitySets
   public static final String ACTIVITYSETS = "ACTIVITYSETS";

   // Applications
   public static final String APPLICATIONS = "APPLICATIONS";

   // Artifacts
   public static final String ARTIFACTS = "ARTIFACTS";

   // Associations
   public static final String ASSOCIATIONS = "ASSOCIATIONS";

   // DataFields
   public static final String DATAFIELDS = "DATAFIELDS";

   // External packages
   public static final String EXTERNALPACKAGES = "EXTERNALPACKAGES";

   // Formal parameters
   public static final String FORMALPARAMETERS = "FORMALPARAMETERS";

   // Lanes
   public static final String LANES = "LANES";

   // Participants
   public static final String PARTICIPANTS = "PARTICIPANTS";

   // Pools
   public static final String POOLS = "POOLS";

   // WorkflowProcesses
   public static final String WORKFLOWPROCESSES = "WORKFLOWPROCESSES";

   // Transitions
   public static final String TRANSITIONS = "TRANSITIONS";

   // Type declarations
   public static final String TYPEDECLARATIONS = "TYPEDECLARATIONS";
}
