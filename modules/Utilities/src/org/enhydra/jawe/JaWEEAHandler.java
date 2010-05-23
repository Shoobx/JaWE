package org.enhydra.jawe;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.elements.ExtendedAttribute;
import org.enhydra.shark.xpdl.elements.ExtendedAttributes;
import org.enhydra.shark.xpdl.elements.ExternalPackage;
import org.enhydra.shark.xpdl.elements.Package;

/**
 * Various utilities for handling extended attributes for old and new version of JaWE.
 * 
 * @author Sasa Bojanic
 */
public class JaWEEAHandler {

   // --------------------------
   // Extended attributes
   public static final String EA_EDITING_TOOL = "EDITING_TOOL";

   public static final String EA_EDITING_TOOL_VERSION = "EDITING_TOOL_VERSION";

   public static final String EA_JAWE_CONFIGURATION = "JaWE_CONFIGURATION";

   public static final String EA_JAWE_TYPE = "JaWE_TYPE";

   private static final String[] STANDARD_TYPES_ARRAY = new String[] {
         JaWEConstants.ACTIVITY_SET_TYPE_DEFAULT,
         JaWEConstants.ACTIVITY_TYPE_BLOCK,
         JaWEConstants.ACTIVITY_TYPE_NO,
         JaWEConstants.ACTIVITY_TYPE_ROUTE,
         JaWEConstants.ACTIVITY_TYPE_SUBFLOW,
         JaWEConstants.ACTIVITY_TYPE_TOOL,
         JaWEConstants.APPLICATION_TYPE_DEFAULT,
         JaWEConstants.DATA_FIELD_DEFAULT,
         JaWEConstants.DEADLINE_DEFAULT,
         JaWEConstants.ENUMERATION_VALUE_DEFAULT,
         JaWEConstants.EXTENDED_ATTRIBUTE_DEFAULT,
         JaWEConstants.EXTERNAL_PACKAGE_DEFAULT,
         JaWEConstants.FORMAL_PARAMETER_DEFAULT,
         JaWEConstants.MEMBER_DEFAULT,
         JaWEConstants.NAMESPACE_DEFAULT,
         JaWEConstants.PACKAGE_DEFAULT,
         JaWEConstants.PACKAGE_EXTERNAL,
         JaWEConstants.PACKAGE_TRANSIENT,
         JaWEConstants.PARTICIPANT_TYPE_HUMAN,
         JaWEConstants.PARTICIPANT_TYPE_ORGANIZATIONAL_UNIT,
         JaWEConstants.PARTICIPANT_TYPE_RESOURCE,
         JaWEConstants.PARTICIPANT_TYPE_RESOURCE_SET,
         JaWEConstants.PARTICIPANT_TYPE_ROLE,
         JaWEConstants.PARTICIPANT_TYPE_SYSTEM,
         JaWEConstants.RESPONSIBLE_DEFAULT,
         JaWEConstants.TOOL_DEFAULT,
         JaWEConstants.TRANSITION_TYPE_CONDITIONAL,
         JaWEConstants.TRANSITION_TYPE_DEFAULTEXCEPTION,
         JaWEConstants.TRANSITION_TYPE_EXCEPTION,
         JaWEConstants.TRANSITION_TYPE_OTHERWISE,
         JaWEConstants.TRANSITION_TYPE_UNCONDITIONAL,
         JaWEConstants.TYPE_DECLARATION_DEFAULT,
         JaWEConstants.WORKFLOW_PROCESS_TYPE_DEFAULT
   };

   private static final List STANDARD_TYPES_LIST = Arrays.asList(STANDARD_TYPES_ARRAY);

   // ****** EXTERNAL PACKAGE
   public static final String EA_JAWE_EXTERNAL_PACKAGE_ID = "EXTERNAL_PACKAGE_ID";

   public static String getEditingTool(Package pkg) {
      String editingTool = "";
      ExtendedAttribute ea = JaWEEAHandler.getEditingToolEA(pkg);
      if (ea != null) {
         editingTool = ea.getVValue();
      }
      return editingTool;
   }

   public static void setEditingTool(Package pkg, String tool) {
      ExtendedAttribute ea = JaWEEAHandler.getEditingToolEA(pkg);
      if (ea == null) {
         ExtendedAttributes eas = pkg.getExtendedAttributes();
         ea = (ExtendedAttribute) eas.generateNewElement();
         ea.setName(JaWEEAHandler.EA_EDITING_TOOL);
         ea.setVValue(tool);
         eas.add(0, ea);
      } else {
         ea.setVValue(tool);
      }
   }

   protected static ExtendedAttribute getEditingToolEA(Package pkg) {
      return pkg.getExtendedAttributes()
         .getFirstExtendedAttributeForName(JaWEEAHandler.EA_EDITING_TOOL);
   }

   public static String getEditingToolVersion(Package pkg) {
      String editingTool = "";
      ExtendedAttribute ea = JaWEEAHandler.getEditingToolVersionEA(pkg);
      if (ea != null) {
         editingTool = ea.getVValue();
      }
      return editingTool;
   }

   public static void setEditingToolVersion(Package pkg, String toolVersion) {
      ExtendedAttribute ea = JaWEEAHandler.getEditingToolVersionEA(pkg);
      if (ea == null) {
         ExtendedAttributes eas = pkg.getExtendedAttributes();
         ea = (ExtendedAttribute) eas.generateNewElement();
         ea.setName(JaWEEAHandler.EA_EDITING_TOOL_VERSION);
         ea.setVValue(toolVersion);
         eas.add(0, ea);
      } else {
         ea.setVValue(toolVersion);
      }
   }

   protected static ExtendedAttribute getEditingToolVersionEA(Package pkg) {
      return pkg.getExtendedAttributes()
         .getFirstExtendedAttributeForName(JaWEEAHandler.EA_EDITING_TOOL_VERSION);
   }

   public static String getJaWEConfig(Package pkg) {
      String jaweConfig = "";
      ExtendedAttribute ea = JaWEEAHandler.getJaWEConfigEA(pkg);
      if (ea != null) {
         jaweConfig = ea.getVValue();
      }
      return jaweConfig;
   }

   public static void setJaWEConfig(Package pkg, String config) {
      ExtendedAttribute ea = JaWEEAHandler.getJaWEConfigEA(pkg);
      if (ea == null) {
         ExtendedAttributes eas = pkg.getExtendedAttributes();
         ea = (ExtendedAttribute) eas.generateNewElement();
         ea.setName(JaWEEAHandler.EA_JAWE_CONFIGURATION);
         ea.setVValue(config);
         eas.add(0, ea);
      } else {
         ea.setVValue(config);
      }
   }

   protected static ExtendedAttribute getJaWEConfigEA(Package pkg) {
      return pkg.getExtendedAttributes()
         .getFirstExtendedAttributeForName(JaWEEAHandler.EA_JAWE_CONFIGURATION);
   }

   public static boolean removeOldPackageEAs(Package pkg) {
      boolean removed = false;
      ExtendedAttributes eas = pkg.getExtendedAttributes();
      ExtendedAttribute ea = eas.getFirstExtendedAttributeForName("MadeBy");
      if (ea != null) {
         eas.remove(ea);
         removed = true;
      }
      ea = eas.getFirstExtendedAttributeForName("Version");
      if (ea != null) {
         eas.remove(ea);
         removed = true;
      }
      return removed;
   }

   // ----------------------------------------------------------------------------------------------

   public static boolean adjustExternalPackageEAs(Package pkg) {
      boolean changed = false;

      Iterator it = pkg.getExternalPackages().toElements().iterator();
      while (it.hasNext()) {
         ExternalPackage ep = (ExternalPackage) it.next();
         ExtendedAttribute ea = ep.getExtendedAttributes()
            .getFirstExtendedAttributeForName(JaWEEAHandler.EA_JAWE_EXTERNAL_PACKAGE_ID);
         String extPkgId = pkg.getExternalPackageId(ep.getHref());
         if (extPkgId != null) {
            if (ea == null) {
               JaWEEAHandler.setExternalPackageEA(ep, extPkgId);
               changed = true;
            } else {
               String eav = ea.getVValue();
               if (!eav.equals(extPkgId)) {
                  ea.setVValue(extPkgId);
                  changed = true;
               }
            }
         }
      }

      return changed;
   }

   public static void setExternalPackageEA(ExternalPackage ep, String extPkgId) {
      ExtendedAttributes eas = ep.getExtendedAttributes();
      ExtendedAttribute ea = (ExtendedAttribute) eas.generateNewElement();
      ea.setName(JaWEEAHandler.EA_JAWE_EXTERNAL_PACKAGE_ID);
      ea.setVValue(extPkgId);
      eas.add(0, ea);
   }

   public static void setJaWEType(XMLElement el, String type) {
      if (el instanceof XMLComplexElement) {
         if (STANDARD_TYPES_LIST.contains(type))
            return;

         XMLComplexElement cel = (XMLComplexElement) el;
         ExtendedAttributes eas = (ExtendedAttributes) cel.get("ExtendedAttributes");
         if (eas != null) {
            ExtendedAttribute ea = eas.getFirstExtendedAttributeForName(JaWEEAHandler.EA_JAWE_TYPE);
            if (ea == null) {
               ea = (ExtendedAttribute) eas.generateNewElement();
            }
            ea.setName(JaWEEAHandler.EA_JAWE_TYPE);
            ea.setVValue(type);
            eas.add(0, ea);
         }
      }
   }

   public static String getJaWEType(XMLElement el) {
      String type = null;
      if (el instanceof XMLComplexElement) {
         XMLComplexElement cel = (XMLComplexElement) el;
         ExtendedAttributes eas = (ExtendedAttributes) cel.get("ExtendedAttributes");
         if (eas != null) {
            ExtendedAttribute ea = eas.getFirstExtendedAttributeForName(JaWEEAHandler.EA_JAWE_TYPE);
            if (ea != null) {
               type = ea.getVValue();
            }
         }
      }
      return type;
   }

}
