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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphControllerPanel;
import org.enhydra.jawe.components.graph.GraphUtilities;
import org.enhydra.jawe.components.graph.actions.SaveAsJPG;
import org.enhydra.jawe.components.graph.actions.SaveAsSVG;
import org.enhydra.jawe.components.graph.actions.jped.SaveAsPDF;
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

   public static void main(String[] args) throws Throwable {
      System.out.println("Starting JAWE ....");
      System.out.println("JaWE -> JaWE is being initialized ...");

      try {
         setAppUserModelID();
      } catch (Exception e) {
         e.printStackTrace();
      }

      File cfgf = null;
      System.out.println("JaWE_CONF_HOME=" + JaWEConstants.JAWE_CONF_HOME);
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
               System.err.println("Something went wrong while reading configuration from the file "
                                  + mainConfig.getAbsolutePath());
            }
         }
         String conf_home = JaWEConstants.JAWE_CONF_HOME
                            + "/"
                            + props.getProperty(JaWEConstants.JAWE_CURRENT_CONFIG_HOME);
         File cfh = new File(conf_home);
         if (cfh.exists()) {
            System.setProperty(JaWEConstants.JAWE_CURRENT_CONFIG_HOME, conf_home);
            if (Utils.checkFileExistence(JaWEManager.TOGWE_BASIC_PROPERTYFILE_NAME)
                || Utils.checkResourceExistence(JaWEManager.TOGWE_BASIC_PROPERTYFILE_PATH,
                                                JaWEManager.TOGWE_BASIC_PROPERTYFILE_NAME)) {
               cfgf = new File(conf_home
                               + "/" + JaWEManager.TOGWE_BASIC_PROPERTYFILE_NAME);
            } else {
               cfgf = new File(conf_home
                               + "/" + JaWEConstants.JAWE_BASIC_PROPERTYFILE_NAME);
            }
         }
      }
      if (cfgf != null && cfgf.exists()) {
         JaWEManager.configure(cfgf);
      } else {
         JaWEManager.configure();
      }

      // Starting file name
      String fn = null;

      // check if there is a file that should be open at the startup
      if (args != null && args.length > 0) {
         fn = args[0];
         for (int i = 0; i < args.length; i++) {
            System.out.println("ARG " + i + " is " + args[i]);
         }
      }

      Map<String, String> argsMap = getArgumentsMap(args);
      if (shouldSaveGraph(argsMap)) {
         try {
            writeGraph(argsMap);
            System.exit(0);
         } catch (Exception ex) {
            System.exit(1);
         }
      }

      JaWEManager.getInstance().start(fn);

      if (fn == null) {
         String jh = JaWEConstants.JAWE_HOME;
         System.setProperty("user.dir", jh + "/examples/xpdl2.1/RealLife");
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
      if (isWindows) {
         // AppUsermodelID_Start
         final Map<String, Object> WIN32API_OPTIONS = new HashMap<String, Object>() {
            {
               put(Library.OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
               put(Library.OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
            }
         };
         Shell32 shell32 = (Shell32) Native.loadLibrary("shell32",
                                                        Shell32.class,
                                                        WIN32API_OPTIONS);
         WString wAppId = new WString("Together.Workflow.Editor");
         shell32.SetCurrentProcessExplicitAppUserModelID(wAppId);
         // AppUsermodelID_End
      }
   }

   private static Map<String, String> getArgumentsMap(String[] args) throws Exception {
      Map<String, String> argsMap = new HashMap<String, String>();
      if (args != null && args.length > 1) {
         File xpdlFile = new File(args[0]);
         if (xpdlFile.exists()) {
            argsMap.put(XPDL_FILEPATH, args[0]);
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
      }
      return argsMap;
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
      String successMsg = "Graph from XPDL "+fn+" for process definition '"+pdefid+"' saved to "+filepath;
      JaWEManager.getInstance().init();
      org.enhydra.jxpdl.elements.Package pkg = JaWEManager.getInstance()
         .getJaWEController()
         .openPackageFromFile(fn);
      GraphController graphController = GraphUtilities.getGraphController();
      WorkflowProcess wp = pkg.getWorkflowProcess(pdefid);
      Graph graph = graphController.getGraph(wp);
      ((GraphControllerPanel) graphController.getView()).graphSelected(graph);
      graph.setSize(graph.getPreferredSize());
      graph.refresh();
      if (format.equalsIgnoreCase("jpg")) {
         SaveAsJPG.saveGraphAsJPG(filepath, graph);
         System.out.println(successMsg);
      } else if (format.equalsIgnoreCase("svg")) {
         SaveAsSVG.saveGraphAsSVG(filepath, graph);
         System.out.println(successMsg);
      } else if (format.equalsIgnoreCase("pdf")) {
         SaveAsPDF.saveGraphAsPDF(filepath, graph, wp);
         System.out.println(successMsg);
      } else {
         System.out.println("Unknown graph format: " + format);
         throw new RuntimeException("Unknown graph format " + format);
      }

   }

}
