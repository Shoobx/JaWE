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

package org.enhydra.jawe;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import org.enhydra.jawe.base.componentmanager.ComponentManager;
import org.enhydra.jawe.base.controller.ControllerSettings;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.base.display.DisplayNameGenerator;
import org.enhydra.jawe.base.display.DisplayNameGeneratorSettings;
import org.enhydra.jawe.base.display.StandardDisplayNameGenerator;
import org.enhydra.jawe.base.editor.NewStandardXPDLEditorSettings;
import org.enhydra.jawe.base.editor.NewStandardXPDLElementEditor;
import org.enhydra.jawe.base.editor.TableEditor;
import org.enhydra.jawe.base.editor.TableEditorSettings;
import org.enhydra.jawe.base.editor.ValidationOrSearchResultEditor;
import org.enhydra.jawe.base.editor.XPDLElementEditor;
import org.enhydra.jawe.base.idfactory.IdFactory;
import org.enhydra.jawe.base.idfactory.IdFactorySettings;
import org.enhydra.jawe.base.label.LabelGenerator;
import org.enhydra.jawe.base.label.LabelGeneratorSettings;
import org.enhydra.jawe.base.label.StandardLabelGenerator;
import org.enhydra.jawe.base.logger.LoggingManager;
import org.enhydra.jawe.base.panel.InlinePanel;
import org.enhydra.jawe.base.panel.PanelSettings;
import org.enhydra.jawe.base.panel.PanelValidator;
import org.enhydra.jawe.base.panel.StandardPanelGenerator;
import org.enhydra.jawe.base.panel.StandardPanelValidator;
import org.enhydra.jawe.base.tooltip.StandardTooltipGenerator;
import org.enhydra.jawe.base.tooltip.TooltipGenerator;
import org.enhydra.jawe.base.tooltip.TooltipGeneratorSettings;
import org.enhydra.jawe.base.transitionhandler.TransitionHandler;
import org.enhydra.jawe.base.transitionhandler.TransitionHandlerSettings;
import org.enhydra.jawe.base.xpdlhandler.XPDLHandler;
import org.enhydra.jawe.base.xpdlhandler.XPDLHandlerSettings;
import org.enhydra.jawe.base.xpdlhandler.XPDLRepHandler;
import org.enhydra.jawe.base.xpdlobjectfactory.XPDLObjectFactory;
import org.enhydra.jawe.base.xpdlobjectfactory.XPDLObjectFactorySettings;
import org.enhydra.jawe.base.xpdlvalidator.XPDLValidatorSettings;
import org.enhydra.shark.xpdl.StandardPackageValidator;
import org.enhydra.shark.xpdl.XPDLRepositoryHandler;

/**
 * This class is used to get all jawe's managers.
 * 
 * @author Sasa Bojanic
 * @author Miroslav Popov
 */
public class JaWEManager {

   public static final String TOGWE_BASIC_PROPERTYFILE_PATH = "org/enhydra/jawe/properties/";

   public static final String TOGWE_BASIC_PROPERTYFILE_NAME = "togwebasic.properties";

   protected PropertyMgr propertyMgr;

   protected static boolean isConfigured = false;

   protected static Properties properties;

   protected static String version;

   protected static String release;

   protected static String buildNo;

   protected static String buildEd;

   protected static String buildEdSuff;

   protected ComponentManager componentManager;

   protected LabelGenerator labelGenerator;

   protected LoggingManager loggingManager;

   protected IdFactory idFactory;

   protected XPDLObjectFactory xpdlObjectFactory;

   protected TransitionHandler transitionHandler;

   protected PanelValidator panelValidator;

   protected StandardPackageValidator xpdlValidator;

   protected XPDLHandler xpdlHandler;

   protected JaWEController jaweController;

   protected DisplayNameGenerator displayNameGenerator;

   protected XPDLElementEditor xpdlElementEditor;

   protected TableEditor tableEditor;

   protected TooltipGenerator tooltipGenerator;

   protected String panelGeneratorClassName;

   protected String inlinePanelClassName;

   protected XPDLUtils xpdlUtils;

   protected ValidationOrSearchResultEditor validationOrSearchResultEditor;

   protected static String splash;

   protected boolean showSplash = false;

   protected static String aboutMsg;

   protected static boolean showLicenseInfo = true;

   protected static String additionalLicenseText;

   // the one and only instance of this class
   protected static JaWEManager jaweManager;

   protected JaWEManager() {
      version = BuildInfo.getVersion();
      release = BuildInfo.getRelease();
      buildNo = BuildInfo.getBuildNo();
      buildEdSuff = BuildInfo.getJRESuffix();

      buildEd = "C";
      splash = "org/enhydra/jawe/images/JaWESplash.jpg";
      aboutMsg = " Visit <a href=\"http://www.together.at/prod/workflow/twe\">Together Workflow Editor Homepage</a>"
                 + " for more <br>information about the product."
                 + "<br><br>For support please contact: <a href=\"mailto:jawe@enhydra.org\">jawe@enhydra.org</a>"
                 + "<br><br>© <a href=\"http://www.together.at\">Together Teamsolutions Co., Ltd</a></p>";
      additionalLicenseText = "<html><p align center>"
                              + "<br>This product includes software developed by the "
                              + "<br>Apache Software Foundation (http://www.apache.org/).</html>";

      splash = properties.getProperty("JaWESplash.Image",
                                      "org/enhydra/jawe/images/JaWESplash.jpg");
      try {
         ResourceBundle rb = ResourceBundle.getBundle("org.enhydra.jawe.aboutbox");
         aboutMsg = rb.getString("aboutMsg");
         showLicenseInfo = new Boolean(rb.getString("showLicenseInfo")).booleanValue();
         additionalLicenseText = rb.getString("additionalLicenseText");
      } catch (Exception e) {
      }

      showSplash = true;
      propertyMgr = new JaWEPropertyMgr();
   }

   public static JaWEManager getInstance() {
      if (jaweManager == null) {
         jaweManager = new JaWEManager();
      }

      return jaweManager;
   }

   public static void configure(Properties props) {
      if (isConfigured) {
         JaWEManager.getInstance()
            .getLoggingManager()
            .info("Trying to configure "
                  + jaweManager.getName() + " instance that is already configured !!!");
         return;
      }

      if (props == null) {
         throw new Error(jaweManager.getName()
                         + " needs to be configured properly - given Properties have null value!!!");
      }

      configureFromJar();
      Utils.adjustProperties(properties, props);
      isConfigured = true;
   }

   public static void configure(String filePath) {
      if (isConfigured) {
         JaWEManager.getInstance()
            .getLoggingManager()
            .info("Trying to configure "
                  + jaweManager.getName() + " instance that is already configured !!!");
         return;
      }

      if (filePath == null) {
         throw new Error(jaweManager.getName()
                         + " need to be configured properly - given path to configuration file is null!!!");
      }

      File config = new File(filePath);
      JaWEManager.configure(config);
   }

   public static void configure(File configFile) {
      if (isConfigured) {
         JaWEManager.getInstance()
            .getLoggingManager()
            .info("Trying to configure "
                  + jaweManager.getName() + " instance that is already configured !!!");
         return;
      }

      if (configFile == null) {
         throw new Error(jaweManager.getName()
                         + " need to be configured properly - given configuration file is null!!!");
      }

      if (!configFile.isAbsolute()) {
         configFile = configFile.getAbsoluteFile();
      }

      if (configFile.exists()) {
         configureFromJar();
         FileInputStream fis = null;
         try {
            fis = new FileInputStream(configFile);
            Properties props = new Properties();
            props.load(fis);
            fis.close();
            Utils.adjustProperties(properties, props);
         } catch (Exception ex) {
            throw new Error("Something went wrong while reading of configuration from the file!!!",
                            ex);
         }
      } else {
         throw new Error(jaweManager.getName()
                         + " needs to be configured properly - configuration file "
                         + configFile + " does not exist!!!");
      }

      isConfigured = true;
   }

   public static void configure() {
      if (isConfigured) {
         JaWEManager.getInstance()
            .getLoggingManager()
            .info("Trying to configure "
                  + jaweManager.getName() + " instance that is already configured !!!");
         return;
      }

      configureFromJar();
      isConfigured = true;
   }

   protected static void configureFromJar() {
      try {
         // creating USER_HOME/.JaWE directory if it doesn't exist
         File ujdir = new File(JaWEConstants.JAWE_USER_HOME);
         if (!ujdir.exists()) {
            try {
               ujdir.mkdir();
            } catch (Exception exc) {
            }
         }

         URL u = JaWEManager.class.getClassLoader()
            .getResource(JaWEConstants.JAWE_BASIC_PROPERTYFILE_PATH
                         + JaWEConstants.JAWE_BASIC_PROPERTYFILE_NAME);
         URLConnection urlConnection = u.openConnection();
         InputStream is = urlConnection.getInputStream();

         properties = new Properties();
         properties.load(is);
         Utils.copyPropertyFile(JaWEConstants.JAWE_BASIC_PROPERTYFILE_PATH,
                                JaWEConstants.JAWE_BASIC_PROPERTYFILE_NAME,
                                false);

         u = JaWEManager.class.getClassLoader()
            .getResource(JaWEConstants.JAWE_LANGUAGE_MISC_PROPERTYFILE_PATH
                         + JaWEConstants.JAWE_LANGUAGE_MISC_PROPERTYFILE_NAME);
         urlConnection = u.openConnection();
         is = urlConnection.getInputStream();

         Utils.copyPropertyFile(JaWEConstants.JAWE_LANGUAGE_MISC_PROPERTYFILE_PATH,
                                JaWEConstants.JAWE_LANGUAGE_MISC_PROPERTYFILE_NAME,
                                false);

      } catch (Exception ex) {
         ex.printStackTrace();
         throw new Error(jaweManager.getName()
                               + " need to be configured properly - Can't read "
                               + jaweManager.getName()
                               + "'s default configuration from JAR!!!",
                         ex);
      }
   }

   public void init() {
      if (!isConfigured)
         return;

      String cmClass = properties.getProperty("ComponentManager.Class",
                                              "org.enhydra.jawe.base.componentmanager.ComponentManager");
      String dngClass = properties.getProperty("DisplayNameGenerator.Class",
                                               "org.enhydra.jawe.base.display.StandardDisplayNameGenerator");
      String dnSettings = properties.getProperty("DisplayNameGenerator.Settings",
                                                 "org.enhydra.jawe.base.display.DisplayNameGeneratorSettings");
      String jcClass = properties.getProperty("JaWEController.Class",
                                              "org.enhydra.jawe.base.controller.JaWEController");
      String jcSettings = properties.getProperty("JaWEController.Settings",
                                                 "org.enhydra.jawe.base.controller.ControllerSettings");
      String lgClass = properties.getProperty("LabelGenerator.Class",
                                              "org.enhydra.jawe.base.label.StandardLabelGenerator");
      String lgSettings = properties.getProperty("LabelGenerator.Settings",
                                                 "org.enhydra.jawe.base.label.LabelGeneratorSettings");
      String lmClass = properties.getProperty("LoggingManager.Class",
                                              "org.enhydra.jawe.base.logger.LoggingManager");
      String thClass = properties.getProperty("TransitionHandler.Class",
                                              "org.enhydra.jawe.base.transitionhandler.TransitionHandler");
      String thSettings = properties.getProperty("TransitionHandler.Settings",
                                                 "org.enhydra.jawe.base.transitionhandler.TransitionHandlerSettings");
      String idfClass = properties.getProperty("IdFactory.Class",
                                               "org.enhydra.jawe.base.idfactory.IdFactory");
      String idfSettings = properties.getProperty("IdFactory.Settings",
                                                  "org.enhydra.jawe.base.idfactory.IdFactorySettings");
      String xpdlofClass = properties.getProperty("XPDLObjectFactory.Class",
                                                  "org.enhydra.jawe.base.xpdlobjectfactory.XPDLObjectFactory");
      String xpdlofSettings = properties.getProperty("XPDLObjectFactory.Settings",
                                                     "org.enhydra.jawe.base.xpdlobjectfactory.XPDLObjectFactorySettings");
      String pnlvClass = properties.getProperty("PanelValidator.Class",
                                                "org.enhydra.jawe.base.panel.StandardPanelValidator");
      String xpdlvClass = properties.getProperty("XPDLValidator.Class",
                                                 "org.enhydra.jawe.base.xpdlvalidator.TogWEXPDLValidator");
      String xpdlvSettings = properties.getProperty("XPDLValidator.Settings",
                                                    "org.enhydra.jawe.base.xpdlvalidator.XPDLValidatorSettings");
      String xpdlrhClass = properties.getProperty("XPDLRepositoryHandler.Class",
                                                  "org.enhydra.shark.xpdl.XPDLRepositoryHandler");
      String xpdleeClass = properties.getProperty("XPDLElementEditor.Class",
                                                  "org.enhydra.jawe.base.editor.TogWEStandardXPDLElementEditor");
      String xpdlEditorSettings = properties.getProperty("XPDLElementEditor.Settings",
                                                         "org.enhydra.jawe.base.editor.TogWEStandardXPDLEditorSettings");

      String teClass = properties.getProperty("TableEditor.Class",
                                              "org.enhydra.jawe.base.editor.TogWETableEditor");
      String teSettings = properties.getProperty("TableEditor.Settings",
                                                 "org.enhydra.jawe.base.editor.TableEditorSettings");
      String ttgClass = properties.getProperty("TooltipGenerator.Class",
                                               "org.enhydra.jawe.base.tooltip.StandardTooltipGenerator");
      String ttgSettings = properties.getProperty("TooltipGenerator.Settings",
                                                  "org.enhydra.jawe.base.tooltip.TooltipGeneratorSettings");
      panelGeneratorClassName = properties.getProperty("PanelGenerator.Class",
                                                       "org.enhydra.jawe.base.panel.TogWEPanelGenerator");
      inlinePanelClassName = properties.getProperty("InlinePanel.Class",
                                                    "org.enhydra.jawe.base.panel.InlinePanel");

      String xpdlutClass = properties.getProperty("XPDLUtils.Class",
                                                  "org.enhydra.jawe.XPDLUtils");

      ClassLoader cl = getClass().getClassLoader();

      try {
         Constructor c = Class.forName(lmClass).getConstructor(new Class[] {});
         loggingManager = (LoggingManager) c.newInstance(new Object[] {});
         loggingManager.info("JaWEManager -> Working with '"
                             + lmClass + "' implementation of Logging Manager");
      } catch (Throwable ex) {
         String msg = "JaweManager -> Problems while instantiating Logging Manager '"
                      + lmClass + "' !";
         System.err.println(msg);
         throw new Error(msg, ex);
      }

      try {
         Constructor c = Class.forName(xpdlutClass).getConstructor(new Class[] {});
         xpdlUtils = (XPDLUtils) c.newInstance(new Object[] {});
         loggingManager.info("JaWEManager -> Working with '"
                             + xpdlutClass + "' implementation of XPDLUtils");
      } catch (Throwable ex) {
         xpdlUtils = new XPDLUtils();
         String msg = "JaweManager -> Problems while instantiating XPDL Utils '"
                      + xpdlutClass + "' - using default implementation!";
         loggingManager.error(msg, ex);
      }

      XPDLRepositoryHandler xpdlRHandler = null;
      try {
         Constructor c = Class.forName(xpdlrhClass).getConstructor(new Class[] {});
         xpdlRHandler = (XPDLRepositoryHandler) c.newInstance(new Object[] {});
         loggingManager.info("JaWEManager -> Working with '"
                             + xpdlrhClass
                             + "' implementation of XPDL Repository Handler");
      } catch (Throwable ex) {
         xpdlRHandler = new XPDLRepHandler();
         String msg = "JaweManager -> Problems while instantiating XPDL Repository Handler class '"
                      + xpdlrhClass + "' - using default implementation!";
         loggingManager.error(msg, ex);
      }

      xpdlHandler = createXPDLHandler(xpdlRHandler);
      loggingManager.info("JaWEManager -> Working with '"
                          + xpdlHandler.getClass().getName()
                          + "' implementation of XPDL Handler");

      try {
         ControllerSettings cs = (ControllerSettings) cl.loadClass(jcSettings)
            .newInstance();
         cs.setPropertyMgr(propertyMgr);

         Constructor c = Class.forName(jcClass).getConstructor(new Class[] {
            ControllerSettings.class
         });
         jaweController = (JaWEController) c.newInstance(new Object[] {
            cs
         });
         jaweController.init();
         loggingManager.info("JaWEManager -> Working with '"
                             + jcClass + "' implementation of JaWE Controller");
      } catch (Throwable ex) {
         jaweController = new JaWEController(new ControllerSettings());
         jaweController.init();
         String msg = "JaweManager -> Problems while instantiating JaWE Controller '"
                      + jcClass + "' - using default implementation!";
         loggingManager.error(msg, ex);
      }

      try {
         LabelGeneratorSettings ls = (LabelGeneratorSettings) cl.loadClass(lgSettings)
            .newInstance();
         ls.setPropertyMgr(propertyMgr);

         Constructor c = Class.forName(lgClass).getConstructor(new Class[] {
            LabelGeneratorSettings.class
         });
         labelGenerator = (StandardLabelGenerator) c.newInstance(new Object[] {
            ls
         });
         loggingManager.info("JaWEManager -> Working with '"
                             + lgClass + "' implementation of Label Generator");
      } catch (Exception ex) {
         labelGenerator = new StandardLabelGenerator();
         String msg = "JaweManager -> Problems while instantiating Label Generator '"
                      + lgClass + "' - using default implementation!";
         loggingManager.error(msg, ex);
      }

      try {
         TransitionHandlerSettings ts = (TransitionHandlerSettings) cl.loadClass(thSettings)
            .newInstance();
         ts.setPropertyMgr(propertyMgr);

         Constructor c = Class.forName(thClass).getConstructor(new Class[] {
            TransitionHandlerSettings.class
         });
         transitionHandler = (TransitionHandler) c.newInstance(new Object[] {
            ts
         });
         loggingManager.info("JaWEManager -> Working with '"
                             + thClass + "' implementation of Transition Handler");
      } catch (Throwable ex) {
         transitionHandler = new TransitionHandler();
         String msg = "JaweManager -> Problems while instantiating Transition Handler class '"
                      + thClass + "' - using default implementation!";
         loggingManager.error(msg, ex);
      }

      try {
         IdFactorySettings is = (IdFactorySettings) cl.loadClass(idfSettings)
            .newInstance();
         is.setPropertyMgr(propertyMgr);

         Constructor c = Class.forName(idfClass).getConstructor(new Class[] {
            IdFactorySettings.class
         });
         idFactory = (IdFactory) c.newInstance(new Object[] {
            is
         });
         loggingManager.info("JaWEManager -> Working with '"
                             + idfClass + "' implementation of Id Factory");
      } catch (Throwable ex) {
         idFactory = new IdFactory();
         String msg = "JaweManager -> Problems while instantiating Id Factory class '"
                      + idfClass + "' - using default implementation!";
         loggingManager.error(msg, ex);
      }

      try {
         XPDLObjectFactorySettings os = (XPDLObjectFactorySettings) cl.loadClass(xpdlofSettings)
            .newInstance();
         os.setPropertyMgr(propertyMgr);

         Constructor c = Class.forName(xpdlofClass).getConstructor(new Class[] {
            XPDLObjectFactorySettings.class
         });
         xpdlObjectFactory = (XPDLObjectFactory) c.newInstance(new Object[] {
            os
         });
         loggingManager.info("JaWEManager -> Working with '"
                             + xpdlofClass + "' implementation of XPDL Object Factory");
      } catch (Throwable ex) {
         xpdlObjectFactory = new XPDLObjectFactory();
         String msg = "JaweManager -> Problems while instantiating XPDL Object Factory class '"
                      + xpdlofClass + "' - using default implementation!";
         loggingManager.error(msg, ex);
      }

      try {
         panelValidator = (StandardPanelValidator) cl.loadClass(pnlvClass).newInstance();
         loggingManager.info("JaWEManager -> Working with '"
                             + pnlvClass + "' implementation of Panel Validator");
      } catch (Throwable ex) {
         panelValidator = new StandardPanelValidator();
         String msg = "JaweManager -> Problems while instantiating Panel Validator class '"
                      + pnlvClass + "' - using default implementation!";
         loggingManager.error(msg, ex);
      }

      try {
         XPDLValidatorSettings xvs = (XPDLValidatorSettings) cl.loadClass(xpdlvSettings)
            .newInstance();
         xvs.setPropertyMgr(propertyMgr);
         xvs.init(null);

         Constructor c = Class.forName(xpdlvClass).getConstructor(new Class[] {
            Properties.class
         });
         xpdlValidator = (StandardPackageValidator) c.newInstance(new Object[] {
            xvs.getProperties()
         });
         loggingManager.info("JaWEManager -> Working with '"
                             + xpdlvClass + "' implementation of XPDL Validator");
      } catch (Throwable ex) {
         XPDLValidatorSettings vs = new XPDLValidatorSettings();
         vs.init(null);
         xpdlValidator = new StandardPackageValidator(vs.getProperties());
         String msg = "JaweManager -> Problems while instantiating XPDL Validator class '"
                      + xpdlvClass + "' - using default implementation!";
         loggingManager.error(msg, ex);
      }
      validationOrSearchResultEditor = new ValidationOrSearchResultEditor();

      try {
         DisplayNameGeneratorSettings ds = (DisplayNameGeneratorSettings) cl.loadClass(dnSettings)
            .newInstance();
         ds.setPropertyMgr(propertyMgr);

         Constructor c = Class.forName(dngClass).getConstructor(new Class[] {
            DisplayNameGeneratorSettings.class
         });
         displayNameGenerator = (StandardDisplayNameGenerator) c.newInstance(new Object[] {
            ds
         });
         loggingManager.info("JaWEManager -> Working with '"
                             + dngClass + "' implementation of Display Name Generator");
      } catch (Exception ex) {
         displayNameGenerator = new StandardDisplayNameGenerator();
         String msg = "JaweManager -> Problems while instantiating Display Name Generator class '"
                      + dngClass + "' - using default implementation!";
         loggingManager.error(msg, ex);
      }

      try {
         cl.loadClass(panelGeneratorClassName).newInstance();
         loggingManager.info("JaWEManager -> Using '"
                             + panelGeneratorClassName
                             + "' implementation of Panel Generator");
      } catch (Exception ex) {
         String msg = "JaWEManager -> Problems while instantiating Panel Generator class '"
                      + JaWEManager.getInstance().getPanelGeneratorClassName()
                      + "' - using default implementation!";

         JaWEManager.getInstance().getLoggingManager().error(msg, ex);
         panelGeneratorClassName = StandardPanelGenerator.class.getName();
      }
      try {
         cl.loadClass(inlinePanelClassName).newInstance();
         loggingManager.info("JaWEManager -> Using '"
                             + inlinePanelClassName + "' implementation of Inline Panel");
      } catch (Exception ex) {
         String msg = "JaWEManager --> Problems while instantiating InlinePanel class '"
                      + JaWEManager.getInstance().getInlinePanelClassName()
                      + "' - using default implementation!";
         JaWEManager.getInstance().getLoggingManager().error(msg, ex);
         inlinePanelClassName = InlinePanel.class.getName();
      }

      try {
         PanelSettings ps = (PanelSettings) cl.loadClass(xpdlEditorSettings)
            .newInstance();
         ps.setPropertyMgr(propertyMgr);

         Constructor c = Class.forName(xpdleeClass).getConstructor(new Class[] {
            PanelSettings.class
         });
         xpdlElementEditor = (XPDLElementEditor) c.newInstance(new Object[] {
            ps
         });
         loggingManager.info("JaWEManager -> Working with '"
                             + xpdleeClass + "' implementation of XPDL Element Editor ");
      } catch (Throwable ex) {
         xpdlElementEditor = new NewStandardXPDLElementEditor(new NewStandardXPDLEditorSettings());
         String msg = "JaweManager -> Problems while instantiating XPDL Element Editor class '"
                      + xpdleeClass + "' - using default implementation!";
         loggingManager.error(msg, ex);
      }

      try {
         TableEditorSettings ts = (TableEditorSettings) cl.loadClass(teSettings)
            .newInstance();
         ts.setPropertyMgr(propertyMgr);

         Constructor c = Class.forName(teClass).getConstructor(new Class[] {
            TableEditorSettings.class
         });
         tableEditor = (TableEditor) c.newInstance(new Object[] {
            ts
         });
         loggingManager.info("JaWEManager -> Working with '"
                             + teClass + "' implementation of Table Editor ");
      } catch (Throwable ex) {
         tableEditor = new TableEditor(new TableEditorSettings());
         String msg = "JaweManager -> Problems while instantiating table editor class '"
                      + teClass + "' - using default implementation!";
         loggingManager.error(msg, ex);
      }

      try {
         TooltipGeneratorSettings ts = (TooltipGeneratorSettings) cl.loadClass(ttgSettings)
            .newInstance();
         ts.setPropertyMgr(propertyMgr);

         Constructor c = Class.forName(ttgClass).getConstructor(new Class[] {
            TooltipGeneratorSettings.class
         });
         tooltipGenerator = (StandardTooltipGenerator) c.newInstance(new Object[] {
            ts
         });
         loggingManager.info("JaWEManager -> Working with '"
                             + ttgClass + "' implementation of Tooltip Generator");
      } catch (Exception ex) {
         tooltipGenerator = new StandardTooltipGenerator();
         String msg = "JaweManager -> Problems while instantiating Tooltip Generator class '"
                      + ttgClass + "' - using default implementation!";
         loggingManager.error(msg, ex);
      }

      try {
         componentManager = (ComponentManager) cl.loadClass(cmClass).newInstance();
         componentManager.setPropertyMgr(propertyMgr);
         componentManager.init();
         loggingManager.info("JaWEManager -> Working with '"
                             + cmClass + "' implementation of Component Manager");
      } catch (Throwable ex) {
         componentManager = new ComponentManager();
         componentManager.init();
         String msg = "JaWEManger -> Problems while instantiating Component Manager class '"
                      + cmClass + "' - using default implementation!";
         loggingManager.error(msg, ex);
      }

      getXPDLHandler().getXPDLRepositoryHandler()
         .setXPDLPrefixEnabled(Boolean.valueOf(properties.getProperty("UseXPDLPrefix",
                                                                      "true"))
            .booleanValue());
      // caching resources
      Utils.getActivityIconsMap();

      // loading transient packages
      loadTransientPackages();
   }

   public void restart(String filename) throws Throwable {
      clearComponents();
      String confHome = System.getProperty(JaWEConstants.JAWE_CURRENT_CONFIG_HOME);
      String cfn = confHome + "/" + JaWEManager.TOGWE_BASIC_PROPERTYFILE_NAME;
      File cf = new File(cfn);

      if (!cf.exists()) {
         cfn = confHome + "/" + JaWEConstants.JAWE_BASIC_PROPERTYFILE_NAME;
         cf = new File(cfn);
      }
      if (cf.exists()) {
         JaWEManager.configure(cf);
      } else {
         JaWEManager.configure();
      }
      ResourceManager.reconfigure();
      JaWEManager.getInstance().start(filename);
   }

   public void loadTransientPackages() {
      String ltpstr = properties.getProperty("DefaultTransientPackages", "");
      String[] tps = Utils.tokenize(ltpstr, ",");
      if (tps != null) {
         for (int i = 0; i < tps.length; i++) {
            String tp = tps[i].trim();
            getJaWEController().addTransientPackage(tp);
         }
      }
   }

   public String getName() {
      return ResourceManager.getLanguageDependentString("Title");
   }

   public XPDLHandler createXPDLHandler(XPDLRepositoryHandler xpdlRHandler) {
      ClassLoader cl = getClass().getClassLoader();

      String xpdlhClass = properties.getProperty("XPDLHandler.Class",
                                                 "org.enhydra.jawe.base.xpdlhandler.XPDLHandler");
      String xpdlhSettings = properties.getProperty("XPDLHandler.Settings",
                                                    "org.enhydra.jawe.base.xpdlhandler.XPDLHandlerSettings");

      XPDLHandler xpdlh = null;
      try {
         XPDLHandlerSettings hs = (XPDLHandlerSettings) cl.loadClass(xpdlhSettings)
            .newInstance();
         hs.setPropertyMgr(propertyMgr);

         Constructor c = Class.forName(xpdlhClass).getConstructor(new Class[] {
            XPDLHandlerSettings.class
         });
         xpdlh = (XPDLHandler) c.newInstance(new Object[] {
            hs
         });
      } catch (Throwable ex) {
         String msg = "JaweManager -> Problems while instantiating XPDL Handler class '"
                      + xpdlhClass + "' - using default!";
         xpdlh = new XPDLHandler();
         xpdlh.setXPDLRepositoryHandler(xpdlRHandler);
         loggingManager.error(msg, ex);
      }
      xpdlh.setXPDLRepositoryHandler(xpdlRHandler);
      xpdlh.setLocale(ResourceManager.getChoosenLocale());
      return xpdlh;
   }

   protected void clearComponents() {
      getJaWEController().getJaWEFrame().dispose();
      isConfigured = false;
      showSplash = false;
      componentManager = null;
      labelGenerator = null;
      loggingManager = null;
      idFactory = null;
      xpdlObjectFactory = null;
      transitionHandler = null;
      panelValidator = null;
      xpdlValidator = null;
      xpdlHandler = null;
      jaweController = null;
      displayNameGenerator = null;
      xpdlElementEditor = null;
      tableEditor = null;
      tooltipGenerator = null;
      panelGeneratorClassName = null;
      inlinePanelClassName = null;
      xpdlUtils = null;
   }

   public void start(String fileName) throws Throwable {
      if (!isConfigured)
         return;

      long tStart = System.currentTimeMillis();

      String lookAndFeelClassName = JaWEManager.getLookAndFeelClassName();
      if (lookAndFeelClassName == null || !lookAndFeelClassName.equals("")) {
         try {
            UIManager.setLookAndFeel(lookAndFeelClassName);
         } catch (Exception ex) {
            if (java.io.File.pathSeparator.equals(";")) {
               UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } else {
               UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            }
         }
      } else {
         if (java.io.File.pathSeparator.equals(";")) {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         } else {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
         }
      }
      FontUIResource f;
      try {
         try {
            f = new FontUIResource(JaWEManager.getFontName(),
                                   Font.PLAIN,
                                   JaWEManager.getFontSize());
         } catch (Exception ex) {
            ex.printStackTrace();
            f = (FontUIResource) UIManager.getDefaults().get("Label.font");
         }

         Enumeration keys = UIManager.getDefaults().keys();
         while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
               UIManager.put(key, f);
            }
         }
      } catch (Exception ex) {
      }

      JaWESplash splashScr = null;
      if (showSplash)
         splashScr = new JaWESplash();

      JaWEManager.getInstance().init();

      long tEnd = System.currentTimeMillis();
      if (loggingManager != null) {
         loggingManager.info("JaWEManager -> "
                             + getName()
                             + " editor initialization is finished, it lasted "
                             + (tEnd - tStart) + " ms");
         loggingManager.info(getName() + " ready and waiting ...");
      }

      if (fileName != null) {
         jaweController.openPackageFromFile(fileName);
      }

      if (splashScr != null) {
         splashScr.dispose();
         splashScr = null;
      }

      jaweController.showJaWEFrame();
   }

   public static String getBuildNo() {
      return buildNo;
   }

   public static String getBuildEdition() {
      return buildEd;
   }

   public static String getBuildEditionSuffix() {
      return buildEdSuff;
   }

   public static String getVersion() {
      return version;
   }

   public static String getRelease() {
      return release;
   }

   public static String getFontName() {

      if (isConfigured) {
         try {
            FontUIResource f = (FontUIResource) UIManager.getDefaults().get("Label.font");
            return properties.getProperty("Font.Name", f.getFontName());
         } catch (Exception ex) {
         }
      }
      return "System";
   }

   public static int getFontSize() {
      int fontSize = 11;

      if (isConfigured) {
         try {
            FontUIResource f = (FontUIResource) UIManager.getDefaults().get("Label.font");
            fontSize = f.getSize();
            String fnts = properties.getProperty("Font.Size", String.valueOf(fontSize));
            fontSize = Integer.parseInt(fnts);
         } catch (Exception ex) {
         }
      }

      return fontSize;
   }

   public String getStartingLocale() {
      if (isConfigured)
         return properties.getProperty("StartingLocale", "default");

      return "default";
   }

   public static String getLookAndFeelClassName() {
      if (isConfigured)
         return properties.getProperty("LookAndFeelClassName", "");

      return null;
   }

   public static String getSplashScreenImage() {
      return splash;
   }

   public static String getAboutMsg() {
      return aboutMsg;
   }

   public static boolean showLicenseInfo() {
      return showLicenseInfo;
   }

   public static String getAdditionalLicenseText() {
      return additionalLicenseText;
   }

   public ComponentManager getComponentManager() {
      return componentManager;
   }

   public LabelGenerator getLabelGenerator() {
      return labelGenerator;
   }

   public LoggingManager getLoggingManager() {
      return loggingManager;
   }

   public TransitionHandler getTransitionHandler() {
      return transitionHandler;
   }

   public IdFactory getIdFactory() {
      return idFactory;
   }

   public XPDLObjectFactory getXPDLObjectFactory() {
      return xpdlObjectFactory;
   }

   public PanelValidator getPanelValidator() {
      return panelValidator;
   }

   public StandardPackageValidator getXPDLValidator() {
      return xpdlValidator;
   }

   public XPDLHandler getXPDLHandler() {
      return xpdlHandler;
   }

   public JaWEController getJaWEController() {
      return jaweController;
   }

   public DisplayNameGenerator getDisplayNameGenerator() {
      return displayNameGenerator;
   }

   public XPDLElementEditor getXPDLElementEditor() {
      return xpdlElementEditor;
   }

   public TableEditor getTableEditor() {
      return tableEditor;
   }

   public TooltipGenerator getTooltipGenerator() {
      return tooltipGenerator;
   }

   public String getPanelGeneratorClassName() {
      return panelGeneratorClassName;
   }

   public String getInlinePanelClassName() {
      return inlinePanelClassName;
   }

   public XPDLUtils getXPDLUtils() {
      return xpdlUtils;
   }

   public ValidationOrSearchResultEditor getValidationOrSearchResultEditor() {
      return validationOrSearchResultEditor;
   }

   class JaWEPropertyMgr implements PropertyMgr {

      public Properties loadProperties(String path, String name) {
         Properties prop = new Properties();

         try {
            Utils.manageProperties(prop, path, name);
         } catch (Exception e) {
         }

         return prop;
      }

      public void manageProperties(JaWEComponent comp,
                                   JaWEComponentSettings settings,
                                   String path,
                                   String name) {
         try {
            settings.loadDefault(comp, loadProperties(path, name));
         } catch (Exception e) {
            System.err.println("Something's wrong with "
                               + name + ", it has been overwritten by the default one!");
            try {
               settings.clear();
               Utils.copyPropertyFile(path, name, true);
               settings.loadDefault(comp, new Properties());
            } catch (Exception ex) {
            }
         }
      }
   }

}
