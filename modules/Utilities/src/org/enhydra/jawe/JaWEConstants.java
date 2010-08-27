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
* Used to hold various static variables to be easily accessed
* from anywhere.
*/
public class JaWEConstants {

   public static final String JAWE_CURRENT_CONFIG_HOME="JaWE_CURRENT_CONFIG_HOME";
   public static final String JAWE_CONFIG_NAME="JaWE_CONFIG_NAME";
   //--------------------------
   public static final String JAWE_HOME=System.getProperty("JaWE_HOME");
   public static final String JAWE_CONF_HOME=((JAWE_HOME!=null) ? (JAWE_HOME+"/"+"config") : (null));

   public static final String JAWE_USER_HOME=System.getProperty("user.home")+"/.JaWE";
   
   public static final String JAWE_BASIC_PROPERTYFILE_PATH = "org/enhydra/jawe/properties/";
   public static final String JAWE_BASIC_PROPERTYFILE_NAME = "jaweconfiguration.properties";

   public static final String JAWE_LANGUAGE_MISC_PROPERTYFILE_PATH = "org/enhydra/jawe/language/";
   public static final String JAWE_LANGUAGE_MISC_PROPERTYFILE_NAME = "jawelanguagemisc.properties";

   public static final String JAWE_ACTIVITY_ICONS="org/enhydra/jawe/activityicons";
   
   //--------------------------   
   // JaWE types
   // Activity
   public static final String ACTIVITY_TYPE = "ACTIVITY";
   public static final String ACTIVITY_TYPE_BLOCK = "ACTIVITY_BLOCK";
   public static final String ACTIVITY_TYPE_NO = "ACTIVITY_NO";
   public static final String ACTIVITY_TYPE_ROUTE = "ACTIVITY_ROUTE";
   public static final String ACTIVITY_TYPE_SUBFLOW = "ACTIVITY_SUBFLOW";
   public static final String ACTIVITY_TYPE_TOOL = "ACTIVITY_TOOL";

   // Activity set
   public static final String ACTIVITY_SET_TYPE_DEFAULT = "ACTIVITYSET_DEFAULT";
   
   // Actual parameter
   public static final String ACTUAL_PARAMETER_DEFAULT = "ACTUALPARAMETER_DEFAULT";
   
   // Application type
   public static final String APPLICATION_TYPE_DEFAULT = "APPLICATION_DEFAULT";
   
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
   public static final String PARTICIPANT_TYPE_HUMAN="PARTICIPANT_HUMAN";
   public static final String PARTICIPANT_TYPE_ORGANIZATIONAL_UNIT="PARTICIPANT_ORGANIZATIONAL_UNIT";   
   public static final String PARTICIPANT_TYPE_RESOURCE="PARTICIPANT_RESOURCE";
   public static final String PARTICIPANT_TYPE_RESOURCE_SET="PARTICIPANT_RESOURCE_SET";
   public static final String PARTICIPANT_TYPE_ROLE="PARTICIPANT_ROLE";
   public static final String PARTICIPANT_TYPE_SYSTEM="PARTICIPANT_SYSTEM";
   
   // Responsible
   public static final String RESPONSIBLE_DEFAULT = "RESPONSIBLE_DEFAULT";
   
   // Tool
   public static final String TOOL_DEFAULT = "TOOL_DEFAULT";
   
   // Transitions
   public static final String TRANSITION_TYPE = "TRANSITION";
   public static final String TRANSITION_TYPE_UNCONDITIONAL="TRANSITION_UNCONDITIONAL";
   public static final String TRANSITION_TYPE_CONDITIONAL="TRANSITION_CONDITIONAL";
   public static final String TRANSITION_TYPE_DEFAULTEXCEPTION="TRANSITION_DEFAULTEXCEPTION";
   public static final String TRANSITION_TYPE_EXCEPTION="TRANSITION_EXCEPTION";
   public static final String TRANSITION_TYPE_OTHERWISE="TRANSITION_OTHERWISE";   
   
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
   
   // DataFields
   public static final String DATAFIELDS = "DATAFIELDS";

   // External packages
   public static final String EXTERNALPACKAGES = "EXTERNALPACKAGES";
   
   // Formal parameters
   public static final String FORMALPARAMETERS = "FORMALPARAMETERS";

   // Participants
   public static final String PARTICIPANTS = "PARTICIPANTS";
   
   // Processes
   public static final String PROCESSES = "PROCESSES";
   
   // Transitions
   public static final String TRANSITIONS = "TRANSITIONS";  

   // Type declarations
   public static final String TYPEDECLARATIONS = "TYPEDECLARATIONS";  
}
