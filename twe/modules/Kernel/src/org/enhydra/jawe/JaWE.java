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

package org.enhydra.jawe;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphControllerPanel;
import org.enhydra.jawe.components.graph.GraphUtilities;
import org.enhydra.jawe.components.graph.actions.SaveAsJPG;
import org.enhydra.jawe.components.graph.actions.SaveAsSVG;
import org.enhydra.jxpdl.elements.WorkflowProcess;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.WString;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;

/**
 * The main editor class.
 * 
 * @author Sasa Bojanic
 */
public class JaWE {

   private static final String XPDL_FILEPATH = "xpdl_file_path";

   private static final String WRITE_GRAPH_PROCDEFID = "write_graph_2_file_procdefid";

   private static final String WRITE_GRAPH_FILEPATH = "write_graph_2_file_filepath";

   private static final String WRITE_GRAPH_FORMAT = "write_graph_2_file_format";

   private static final String JaWE_WORKING_DIR = "JaWE_WORKING_DIR";

   private static final String VALIDATE = "validate";

   private static final String VALIDATION_OUTPUT_FORMAT = "validation_output_format";

   public static void main(String[] args) throws Throwable {
      // Check if we're in validation mode and set headless mode early
      Map<String, String> argsMap = getArgumentsMap(args);
      boolean isValidationMode = isValidationMode(argsMap);

      if (isValidationMode) {
         System.setProperty("java.awt.headless", "true");
      } else {
         // Only show startup messages when not in validation mode
         System.out.println("Starting JAWE ....");
         System.out.println("JaWE -> JaWE is being initialized ...");
      }

      try {
         setAppUserModelID();
      } catch (Exception e) {
         e.printStackTrace();
      }

      File cfgf = null;
      if (!isValidationMode) {
         System.out.println("JaWE_CONF_HOME=" + JaWEConstants.JAWE_CONF_HOME);
      }
      if (JaWEConstants.JAWE_CONF_HOME != null) {
         File mainConfig = new File(JaWEConstants.JAWE_CONF_HOME + "/" + "defaultconfig");
         Properties props = new Properties();
         if (mainConfig.exists()) {
            FileInputStream fis = null;
            try {
               fis = new FileInputStream(mainConfig);
               props.load(fis);
               fis.close();
            } catch (Exception ex) {
               System.err.println("Something went wrong while reading configuration from the file " + mainConfig.getAbsolutePath());
            }
         }
         String conf_home = JaWEConstants.JAWE_CONF_HOME + "/" + props.getProperty(JaWEConstants.JAWE_CURRENT_CONFIG_HOME);
         File cfh = new File(conf_home);
         if (cfh.exists()) {
            System.setProperty(JaWEConstants.JAWE_CURRENT_CONFIG_HOME, conf_home);
            if (Utils.checkFileExistence(JaWEManager.TOGWE_BASIC_PROPERTYFILE_NAME)
                || Utils.checkResourceExistence(JaWEManager.TOGWE_BASIC_PROPERTYFILE_PATH, JaWEManager.TOGWE_BASIC_PROPERTYFILE_NAME)) {
               cfgf = new File(conf_home + "/" + JaWEManager.TOGWE_BASIC_PROPERTYFILE_NAME);
            } else {
               cfgf = new File(conf_home + "/" + JaWEConstants.JAWE_BASIC_PROPERTYFILE_NAME);
            }
         }
      }
      if (cfgf != null && cfgf.exists()) {
         JaWEManager.configure(cfgf);
      } else {
         JaWEManager.configure();
      }

      // Suppress logging output during validation mode
      if (isValidationMode) {
         java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.OFF);
      }

      if (shouldSaveGraph(argsMap)) {
         try {
            writeGraph(argsMap);
            System.exit(0);
         } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
         }
      }

      if (shouldValidate(argsMap)) {
         try {
            boolean hasErrors = performValidation(argsMap);
            System.exit(hasErrors ? 1 : 0 );
         } catch (Exception ex) {
            System.err.println("Error during validation: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(2);
         }
      }

      // Starting file name
      String fn = argsMap.get(XPDL_FILEPATH);

      // Don't start GUI if we're in validation mode
      if (isValidationMode(argsMap)) {
         System.err.println("Error: Validation mode was requested but validation did not complete properly.");
         System.exit(1);
      }

      JaWEManager.getInstance().start(fn);

      if (fn == null) {
         String wd = getWorkingDir();
         System.setProperty("user.dir", wd);
      }
   }

   /**
    * Set AppUserModelID for application
    * 
    * @throws Exception
    */
   private static void setAppUserModelID() throws Exception {
      // Setting AppUserModelID
      String OS = System.getProperty("os.name");
      boolean isWindows = OS.startsWith("Windows");
      if (isWindows && Integer.parseInt(System.getProperty("os.version").replace(".", "")) > 60) {
         // AppUsermodelID_Start
         final Map<String, Object> WIN32API_OPTIONS = new HashMap<String, Object>() {
            {
               put(Library.OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
               put(Library.OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
            }
         };
         Shell32 shell32 = (Shell32) Native.loadLibrary("shell32", Shell32.class, WIN32API_OPTIONS);
         WString wAppId = new WString("Together.Workflow.Editor");
         shell32.SetCurrentProcessExplicitAppUserModelID(wAppId);
         // AppUsermodelID_End
      }
   }

   private static boolean isValidationMode(Map<String, String> argsMap) {
      String validate = argsMap.get(VALIDATE);
      return "true".equalsIgnoreCase(validate);
   }

   private static Map<String, String> getArgumentsMap(String[] args) throws Exception {
      Map<String, String> argsMap = new HashMap<String, String>();
      if (args != null && args.length > 0) {
         File xpdlFile = new File(args[0]);
         if (xpdlFile.exists()) {
            argsMap.put(XPDL_FILEPATH, args[0]);
         }
         for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            int indofequal = arg.indexOf("=");
            if (indofequal != -1) {
               String key = arg.substring(0, indofequal);
               String val = arg.substring(indofequal + 1);
               if (!key.trim().equals("") && !val.trim().equals("")) {
                  argsMap.put(key, val);
               }
            }
         }
      }
      return argsMap;
   }

   private static String getWorkingDir() throws Exception {
      String wd = System.getProperty("JaWE_WORKING_DIR");
      if (wd != null && new File(wd).exists()) {
         return wd;
      }
      if (new File(JaWEConstants.JAWE_HOME + "/examples/xpdl2.1/RealLife").exists()) {
         return JaWEConstants.JAWE_HOME + "/examples/xpdl2.1/RealLife";
      } else {
         return JaWEConstants.JAWE_HOME + "/examples";
      }
   }

   private static boolean shouldSaveGraph(Map<String, String> argsMap) throws Exception {
      String fn = argsMap.get(XPDL_FILEPATH);
      String pdefid = argsMap.get(WRITE_GRAPH_PROCDEFID);
      String filepath = argsMap.get(WRITE_GRAPH_FILEPATH);
      String format = argsMap.get(WRITE_GRAPH_FORMAT);
      if (fn != null && pdefid != null && filepath != null && format != null) {
         return true;
      }
      return false;
   }

   private static void writeGraph(Map<String, String> argsMap) throws Exception {
      String fn = argsMap.get(XPDL_FILEPATH);
      String pdefid = argsMap.get(WRITE_GRAPH_PROCDEFID);
      String filepath = argsMap.get(WRITE_GRAPH_FILEPATH);
      String format = argsMap.get(WRITE_GRAPH_FORMAT);
      filepath = filepath + "." + format;
      String successMsg = "Graph from XPDL " + fn + " for process definition '" + pdefid + "' saved to " + filepath;
      JaWEManager.getInstance().init();
      org.enhydra.jxpdl.elements.Package pkg = JaWEManager.getInstance().getJaWEController().openPackageFromFile(fn);
      GraphController graphController = GraphUtilities.getGraphController();
      WorkflowProcess wp = pkg.getWorkflowProcess(pdefid);
      Graph graph = graphController.getGraph(wp);
      ((GraphControllerPanel) graphController.getView()).graphSelected(graph);
      graph.setSize(graph.getPreferredSize());
      graph.refresh();
      if (format.equalsIgnoreCase("jpg")) {
         SaveAsJPG.saveGraphAsJPG(filepath, graph);
         JaWEManager.getInstance().getLoggingManager().info(successMsg);
      } else if (format.equalsIgnoreCase("svg")) {
         SaveAsSVG.saveGraphAsSVG(filepath, graph);
         JaWEManager.getInstance().getLoggingManager().info(successMsg);
      } else if (format.equalsIgnoreCase("pdf")) {
         Class sapdfcls = Class.forName("org.enhydra.jawe.components.graph.actions.jped.SaveAsPDF");
         Method mth = sapdfcls.getMethod("saveGraphAsPDF", String.class, Graph.class, WorkflowProcess.class);
         mth.invoke(null, filepath, graph, wp);
         JaWEManager.getInstance().getLoggingManager().info(successMsg);
      } else {
         JaWEManager.getInstance().getLoggingManager().error("Unknown graph format: " + format);
         throw new RuntimeException("Unknown graph format " + format);
      }

   }

   private static boolean shouldValidate(Map<String, String> argsMap) throws Exception {
      String validate = argsMap.get(VALIDATE);
      return "true".equalsIgnoreCase(validate);
   }

   private static boolean performValidation(Map<String, String> argsMap) throws Exception {
      String fn = argsMap.get(XPDL_FILEPATH);
      String format = argsMap.get(VALIDATION_OUTPUT_FORMAT);
      if (format == null) {
         format = "text";
      }

      // Check if file is provided
      if (fn == null) {
         System.err.println("Error: No XPDL file specified for validation.");
         System.err.println("Usage: java -jar sbpecore.jar <xpdl_file> validate=true [validation_output_format=text|json|csv] [validation_exit_on_error=true|false]");
         return true; // Return true to indicate error
      }

      // Initialize JaWE without starting the GUI
      JaWEManager.getInstance().init();

      // Open the XPDL package
      org.enhydra.jxpdl.elements.Package pkg = JaWEManager.getInstance().getJaWEController().openPackageFromFile(fn);
      if (pkg == null) {
         throw new Exception("Could not open XPDL file: " + fn);
      }

      // Perform validation
      java.util.List validationErrors = JaWEManager.getInstance().getJaWEController().checkValidity(pkg, true);

      // Output results
      return outputValidationResults(fn, validationErrors, format);
   }

   private static boolean outputValidationResults(String filename, java.util.List validationErrors, String format) {
      int errorCount = 0;
      int warningCount = 0;

      // Count errors and warnings
      if (validationErrors != null) {
         for (int i = 0; i < validationErrors.size(); i++) {
            org.enhydra.jawe.base.xpdlvalidator.ValidationError error =
               (org.enhydra.jawe.base.xpdlvalidator.ValidationError) validationErrors.get(i);
            if ("ERROR".equals(error.getType())) {
               errorCount++;
            } else if ("WARNING".equals(error.getType())) {
               warningCount++;
            }
         }
      }

      if ("json".equalsIgnoreCase(format)) {
         outputJsonFormat(filename, validationErrors, errorCount, warningCount);
      } else {
         outputTextFormat(filename, validationErrors, errorCount, warningCount);
      }

      return errorCount > 0;
   }

   private static void outputTextFormat(String filename, java.util.List validationErrors, int errorCount, int warningCount) {
      System.out.println("XPDL Validation Results for: " + filename);
      System.out.println();

      if (validationErrors != null && validationErrors.size() > 0) {
         if (errorCount > 0) {
            System.out.println("Errors (" + errorCount + "):");
            for (int i = 0; i < validationErrors.size(); i++) {
               org.enhydra.jawe.base.xpdlvalidator.ValidationError error =
                  (org.enhydra.jawe.base.xpdlvalidator.ValidationError) validationErrors.get(i);
               if ("ERROR".equals(error.getType())) {
                  System.out.println("  " + formatErrorForText(error));
               }
            }
            System.out.println();
         }

         if (warningCount > 0) {
            System.out.println("Warnings (" + warningCount + "):");
            for (int i = 0; i < validationErrors.size(); i++) {
               org.enhydra.jawe.base.xpdlvalidator.ValidationError error =
                  (org.enhydra.jawe.base.xpdlvalidator.ValidationError) validationErrors.get(i);
               if ("WARNING".equals(error.getType())) {
                  System.out.println("  " + formatErrorForText(error));
               }
            }
            System.out.println();
         }
      } else {
         System.out.println("No validation errors or warnings found.");
         System.out.println();
      }

      System.out.println("Summary: " + errorCount + " errors, " + warningCount + " warnings");
   }

   private static String formatErrorForText(org.enhydra.jawe.base.xpdlvalidator.ValidationError error) {
      StringBuilder sb = new StringBuilder();
      sb.append(error.getType()).append(" [").append(error.getSubType()).append("] ");

      // Get element location
      if (error.getElement() != null) {
         String location = getElementLocation(error.getElement());
         sb.append(location).append(": ");
      }

      // Get meaningful error description
      String description = getErrorDescription(error);
      sb.append(description);
      return sb.toString();
   }

   private static String getErrorDescription(org.enhydra.jawe.base.xpdlvalidator.ValidationError error) {
      String errorId = error.getId();
      String originalDesc = error.getDescription();

      // Handle common error types with better descriptions
      if (errorId != null) {
         switch (errorId) {
            case "WARNING_UNUSED_VARIABLE":
               return "Unused variable '" + originalDesc + "'";
            case "WARNING_NON_EXISTING_VARIABLE_REFERENCE":
               return "Reference to non-existing variable '" + originalDesc + "'";
            case "WARNING_ACTUAL_PARAMETER_EXPRESSION_POSSIBLY_INVALID":
               return "Actual parameter expression may be invalid: '" + originalDesc + "'";
            case "ERROR_NON_EXISTING_VARIABLE_REFERENCE":
               return "Reference to non-existing variable '" + originalDesc + "'";
            case "WARNING_EXPRESSION_POSSIBLY_INVALID":
               return "Expression possibly invalid: '" + originalDesc + "'";
            case "ERROR_PERFORMER_NOT_DEFINED":
               return "No performer defined for activity";
            case "ERROR_UNSUPPORTED_SCRIPT":
               return "Unsupported script type: '" + originalDesc + "'";
            case "ERROR_SCRIPT_NOT_DEFINED":
               return "Script not defined";
            default:
               // For unknown error IDs, try to make a readable description
               if (errorId.startsWith("WARNING_") || errorId.startsWith("ERROR_")) {
                  String readableId = errorId.toLowerCase()
                     .replace("warning_", "")
                     .replace("error_", "")
                     .replace("_", " ");

                  if (originalDesc != null && !originalDesc.trim().isEmpty() && !isDescriptionJustElementName(originalDesc, error.getElement())) {
                     return readableId + ": '" + originalDesc + "'";
                  } else {
                     return readableId;
                  }
               }
         }
      }

      // Fallback to original description if available and meaningful
      if (originalDesc != null && !originalDesc.trim().isEmpty()) {
         // Check if description is just repeating the element name/ID
         if (!isDescriptionJustElementName(originalDesc, error.getElement())) {
            return originalDesc;
         }
         return "Issue with element '" + originalDesc + "'";
      }

      return "No description available";
   }

   private static boolean isDescriptionJustElementName(String description, org.enhydra.jxpdl.XMLElement element) {
      if (description == null || element == null) return false;

      try {
         if (element instanceof org.enhydra.jxpdl.XMLComplexElement) {
            org.enhydra.jxpdl.XMLComplexElement ce = (org.enhydra.jxpdl.XMLComplexElement) element;
            org.enhydra.jxpdl.XMLElement idElement = ce.get("Id");
            if (idElement != null && description.equals(idElement.toValue())) {
               return true;
            }
            org.enhydra.jxpdl.XMLElement nameElement = ce.get("Name");
            if (nameElement != null && description.equals(nameElement.toValue())) {
               return true;
            }
         }
      } catch (Exception e) {
         // Ignore
      }
      return false;
   }

   private static String getElementLocation(org.enhydra.jxpdl.XMLElement element) {
      if (element == null) return "Unknown";

      try {
         java.util.List<String> path = new java.util.ArrayList<String>();
         org.enhydra.jxpdl.XMLElement current = element;

         // Build path from element up to root
         while (current != null) {
            String elementName = getElementName(current);
            if (elementName != null && !elementName.isEmpty()) {
               path.add(0, elementName); // Add to beginning
            }
            current = current.getParent();
         }

         // Return formatted path
         if (path.size() > 0) {
            return String.join("/", path);
         } else {
            return element.getClass().getSimpleName();
         }
      } catch (Exception e) {
         return element.getClass().getSimpleName();
      }
   }

   private static String getElementName(org.enhydra.jxpdl.XMLElement element) {
      if (element == null) return null;

      try {
         String className = element.getClass().getSimpleName();

         // For complex elements, try to get ID or Name
         if (element instanceof org.enhydra.jxpdl.XMLComplexElement) {
            org.enhydra.jxpdl.XMLComplexElement ce = (org.enhydra.jxpdl.XMLComplexElement) element;

            // Try Id first
            org.enhydra.jxpdl.XMLElement idElement = ce.get("Id");
            if (idElement != null && !idElement.toValue().isEmpty()) {
               return className + "(" + idElement.toValue() + ")";
            }

            // Try Name if no Id
            org.enhydra.jxpdl.XMLElement nameElement = ce.get("Name");
            if (nameElement != null && !nameElement.toValue().isEmpty()) {
               return className + "(" + nameElement.toValue() + ")";
            }
         }

         // For collections, show type and count
         if (element instanceof org.enhydra.jxpdl.XMLCollection) {
            org.enhydra.jxpdl.XMLCollection coll = (org.enhydra.jxpdl.XMLCollection) element;
            return className + "[" + coll.size() + "]";
         }

         return className;
      } catch (Exception e) {
         return element.getClass().getSimpleName();
      }
   }

   private static void outputJsonFormat(String filename, java.util.List validationErrors, int errorCount, int warningCount) {
      System.out.println("{");
      System.out.println("  \"file\": \"" + escapeJson(filename) + "\",");
      System.out.println("  \"summary\": {");
      System.out.println("    \"errors\": " + errorCount + ",");
      System.out.println("    \"warnings\": " + warningCount);
      System.out.println("  },");
      System.out.println("  \"problems\": [");

      if (validationErrors != null && validationErrors.size() > 0) {
         for (int i = 0; i < validationErrors.size(); i++) {
            org.enhydra.jawe.base.xpdlvalidator.ValidationError error =
               (org.enhydra.jawe.base.xpdlvalidator.ValidationError) validationErrors.get(i);

            System.out.println("    {");
            System.out.println("      \"type\": \"" + escapeJson(error.getType()) + "\",");
            System.out.println("      \"subtype\": \"" + escapeJson(error.getSubType()) + "\",");
            System.out.println("      \"id\": \"" + escapeJson(error.getId()) + "\",");
            System.out.println("      \"description\": \"" + escapeJson(getErrorDescription(error)) + "\",");
            System.out.println("      \"element\": \"" + escapeJson(getElementLocation(error.getElement())) + "\"");

            if (i < validationErrors.size() - 1) {
               System.out.println("    },");
            } else {
               System.out.println("    }");
            }
         }
      }

      System.out.println("  ]");
      System.out.println("}");
   }

   private static String escapeJson(String input) {
      if (input == null) return "";
      return input.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
   }

}
