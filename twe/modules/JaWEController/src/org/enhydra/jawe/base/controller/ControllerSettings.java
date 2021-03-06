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

/**
 * Miroslav Popov, Dec 1, 2005 miroslav.popov@gmail.com
 */
package org.enhydra.jawe.base.controller;

import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.swing.Action;
import javax.swing.ImageIcon;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.AdditionalResourceManager;
import org.enhydra.jawe.JaWEAction;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.base.controller.actions.Close;
import org.enhydra.jawe.base.controller.actions.Exit;
import org.enhydra.jawe.base.controller.actions.ExternalApplications;
import org.enhydra.jawe.base.controller.actions.ExternalParticipants;
import org.enhydra.jawe.base.controller.actions.ExternalProcesses;
import org.enhydra.jawe.base.controller.actions.ExternalTypeDeclarations;
import org.enhydra.jawe.base.controller.actions.HelpAbout;
import org.enhydra.jawe.base.controller.actions.NewPackage;
import org.enhydra.jawe.base.controller.actions.Open;
import org.enhydra.jawe.base.controller.actions.PackageAddExternalPackage;
import org.enhydra.jawe.base.controller.actions.PackageApplications;
import org.enhydra.jawe.base.controller.actions.PackageCheckValidity;
import org.enhydra.jawe.base.controller.actions.PackageExternalPackages;
import org.enhydra.jawe.base.controller.actions.PackageNamespaces;
import org.enhydra.jawe.base.controller.actions.PackageNewProcess;
import org.enhydra.jawe.base.controller.actions.PackageParticipants;
import org.enhydra.jawe.base.controller.actions.PackageProcesses;
import org.enhydra.jawe.base.controller.actions.PackageProperties;
import org.enhydra.jawe.base.controller.actions.PackageReferredDocument;
import org.enhydra.jawe.base.controller.actions.PackageRemoveExternalPackage;
import org.enhydra.jawe.base.controller.actions.PackageTypeDeclarations;
import org.enhydra.jawe.base.controller.actions.PackageWorkflowRelevantData;
import org.enhydra.jawe.base.controller.actions.ProcessActivitiesOverview;
import org.enhydra.jawe.base.controller.actions.ProcessActivitySetsOverview;
import org.enhydra.jawe.base.controller.actions.ProcessApplications;
import org.enhydra.jawe.base.controller.actions.ProcessFormalParameters;
import org.enhydra.jawe.base.controller.actions.ProcessParticipants;
import org.enhydra.jawe.base.controller.actions.ProcessProperties;
import org.enhydra.jawe.base.controller.actions.ProcessTransitionsOverview;
import org.enhydra.jawe.base.controller.actions.ProcessWorkflowRelevantData;
import org.enhydra.jawe.base.controller.actions.Reopen;
import org.enhydra.jawe.base.controller.actions.Save;
import org.enhydra.jawe.base.controller.actions.SaveAs;
import org.enhydra.jawe.base.controller.actions.StartDesignTimeValidation;
import org.enhydra.jawe.base.controller.actions.StopDesignTimeValidation;

/**
 * @author Miroslav Popov
 */
public class ControllerSettings extends JaWEComponentSettings {

   public void init(JaWEComponent comp) {
      PROPERTYFILE_PATH = "org/enhydra/jawe/base/controller/properties/";
      PROPERTYFILE_NAME = "togwecontroller.properties";
      super.init(comp);
   }

   public void loadDefault(JaWEComponent comp, Properties properties) {
      // defaults
      arm = new AdditionalResourceManager(properties);

      componentSettings.put("AllowInvalidPackageSaving", new Boolean(properties.getProperty("AllowInvalidPackageSaving", "true").equals("true")));
      componentSettings.put("AskOnDeletion", new Boolean(properties.getProperty("AskOnDeletion", "false").equals("true")));
      componentSettings.put("AskOnDeletionOfReferencedElements",
                            new Boolean(properties.getProperty("AskOnDeletionOfReferencedElements", "true").equals("true")));
      componentSettings.put("DoNotAskOnDeletionOfReferencedElementTypes",
                            properties.getProperty("DoNotAskOnDeletionOfReferencedElementTypes", "Activity Transition"));
      componentSettings.put("DesignTimeValidation", new Boolean(properties.getProperty("DesignTimeValidation", "true").equals("true")));
      componentSettings.put("InitialXPDLValidation", new Boolean(properties.getProperty("InitialXPDLValidation", "true").equals("true")));
      componentSettings.put("StartMaximized", new Boolean(properties.getProperty("StartMaximized", "true").equals("true")));
      componentSettings.put("ShowTooltip", new Boolean(properties.getProperty("ShowTooltip", "true").equals("true")));
      componentSettings.put("TitleString", properties.getProperty("TitleString", "{pkgName} {pkgVer} - {appName} {appVer} ({appConfig})"));
      componentSettings.put("UndoHistoryManager.Class", properties.getProperty("UndoHistoryManager.Class", "org.enhydra.jawe.undohistorymgr.UndoHistoryMgr"));
      componentSettings.put("UndoHistorySize", new Integer(properties.getProperty("UndoHistorySize", "-1")));

      componentSettings.put("Encoding", properties.getProperty("Encoding", "UTF-8"));
      componentSettings.put("FrameSettings", properties.getProperty("FrameSettings", "V; special H tree; main H other"));
      componentSettings.put("DefaultActionsEditOrder",
                            properties.getProperty("DefaultActions.Edit.ActionOrder", "Undo Redo - Cut Copy Paste Delete - EditProperties"));
      componentSettings.put("TypeResolverClass", properties.getProperty("TypeResolverClass", "org.enhydra.jawe.base.controller.JaWETypeResolver"));

      componentSettings.put("MainDividerLocation", new Double(properties.getProperty("MainDividerLocation", "0.2")));
      componentSettings.put("FirstSmallDividerLocation", new Double(properties.getProperty("FirstSmallDividerLocation", "0.3")));
      componentSettings.put("SecondSmallDividerLocation", new Double(properties.getProperty("SecondSmallDividerLocation", "0.75")));

      ImageIcon appIcon;
      URL iconURL = ResourceManager.getResource(properties, "ApplicationIcon");
      if (iconURL != null)
         appIcon = new ImageIcon(iconURL);
      else
         appIcon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/twe16.png"));
      componentSettings.put("ApplicationIcon", appIcon);

      ImageIcon hicon;
      try {
         hicon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/together.gif"));
         componentSettings.put("Sponsore1Logo", hicon);
      } catch (Exception ex) {
      }

      try {
         hicon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/Abacus.jpg"));
         componentSettings.put("Sponsore2Logo", hicon);
      } catch (Exception ex) {
      }

      ImageIcon actionIcon;
      iconURL = ResourceManager.getResource(properties, "DefaultAction.Icon." + JaWEActions.NEW_ACTION);
      if (iconURL != null)
         actionIcon = new ImageIcon(iconURL);
      else
         actionIcon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/new.gif"));
      componentSettings.put("DefaultImage" + JaWEActions.NEW_ACTION, actionIcon);

      iconURL = ResourceManager.getResource(properties, "DefaultAction.Icon." + JaWEActions.DUPLICATE_ACTION);
      if (iconURL != null)
         actionIcon = new ImageIcon(iconURL);
      else
         actionIcon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/duplicate.png"));
      componentSettings.put("DefaultImage" + JaWEActions.DUPLICATE_ACTION, actionIcon);

      iconURL = ResourceManager.getResource(properties, "DefaultAction.Icon." + JaWEActions.REFERENCES);
      if (iconURL != null)
         actionIcon = new ImageIcon(iconURL);
      else
         actionIcon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/references.gif"));
      componentSettings.put("DefaultImage" + JaWEActions.REFERENCES, actionIcon);

      iconURL = ResourceManager.getResource(properties, "DefaultAction.Icon." + JaWEActions.UNDO_ACTION);
      if (iconURL != null)
         actionIcon = new ImageIcon(iconURL);
      else
         actionIcon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/nav_left_blue.png"));
      componentSettings.put("DefaultImage" + JaWEActions.UNDO_ACTION, actionIcon);

      iconURL = ResourceManager.getResource(properties, "DefaultAction.Icon." + JaWEActions.REDO_ACTION);
      if (iconURL != null)
         actionIcon = new ImageIcon(iconURL);
      else
         actionIcon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/nav_right_blue.png"));
      componentSettings.put("DefaultImage" + JaWEActions.REDO_ACTION, actionIcon);

      iconURL = ResourceManager.getResource(properties, "DefaultAction.Icon." + JaWEActions.CUT_ACTION);
      if (iconURL != null)
         actionIcon = new ImageIcon(iconURL);
      else
         actionIcon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/cut.gif"));
      componentSettings.put("DefaultImage" + JaWEActions.CUT_ACTION, actionIcon);

      iconURL = ResourceManager.getResource(properties, "DefaultAction.Icon." + JaWEActions.COPY_ACTION);
      if (iconURL != null)
         actionIcon = new ImageIcon(iconURL);
      else
         actionIcon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/copy.gif"));
      componentSettings.put("DefaultImage" + JaWEActions.COPY_ACTION, actionIcon);

      iconURL = ResourceManager.getResource(properties, "DefaultAction.Icon." + JaWEActions.PASTE_ACTION);
      if (iconURL != null)
         actionIcon = new ImageIcon(iconURL);
      else
         actionIcon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/paste.gif"));
      componentSettings.put("DefaultImage" + JaWEActions.PASTE_ACTION, actionIcon);

      iconURL = ResourceManager.getResource(properties, "DefaultAction.Icon." + JaWEActions.DELETE_ACTION);
      if (iconURL != null)
         actionIcon = new ImageIcon(iconURL);
      else
         actionIcon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/delete.gif"));
      componentSettings.put("DefaultImage" + JaWEActions.DELETE_ACTION, actionIcon);

      iconURL = ResourceManager.getResource(properties, "DefaultAction.Icon." + JaWEActions.EDIT_PROPERTIES_ACTION);
      if (iconURL != null)
         actionIcon = new ImageIcon(iconURL);
      else
         actionIcon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/properties.gif"));
      componentSettings.put("DefaultImage" + JaWEActions.EDIT_PROPERTIES_ACTION, actionIcon);

      // menus, toolbars and actions
      componentSettings.put("MainMenu",
                            properties.getProperty("MainMenu.ActionOrder", "*File jawe_editmenu *Search *Package *Process *ExternalPackages *Settings *Help"));

      loadDefaultMenusToolbarsAndActions(comp);
      componentSettings.putAll(Utils.loadAllMenusAndToolbars(properties));
      componentAction.putAll(Utils.loadActions(properties, comp, componentAction));

      List compAct = ResourceManager.getResourceStrings(properties, "Action.JaWEComponentClass.", false);
      for (int i = 0; i < compAct.size(); i++) {
         String className = ResourceManager.getResourceString(properties, "Action.JaWEComponentClass." + compAct.get(i));
         componentSettings.put(compAct.get(i) + "ClassName", className);
      }
      // ********
   }

   protected void loadDefaultMenusToolbarsAndActions(JaWEComponent comp) {
      // menu
      componentSettings.put("FileMenu", "NewPackage Open Reopen Close - Save SaveAs - @RecentFiles - Exit");
      componentSettings.put("FileLangName", "file");
      componentSettings.put("SearchMenu", "Search jaweAction_References");
      componentSettings.put("SearchLangName", "search");
      componentSettings
         .put("PackageMenu",
              "PackageCheckValidity StartDesignTimeValidation StopDesignTimeValidation - PackageNewProcess - PackageNamespaces PackageProperties PackageProcesses PackageExternalPackages PackageAddExternalPackage PackageRemoveExternalPackage PackageTypeDeclarations PackageParticipants PackageApplications PackageWorkflowRelevantData PackageReferredDocument");
      componentSettings.put("PackageLangName", "package");
      componentSettings
         .put("ProcessMenu",
              "ProcessProperties ProcessParticipants ProcessApplications ProcessWorkflowRelevantData ProcessFormalParameters - ProcessActivitySetsOverview ProcessActivitiesOverview ProcessTransitionsOverview");
      componentSettings.put("ProcessLangName", "process");
      componentSettings.put("ExternalPackagesMenu", "ExternalParticipants ExternalProcesses ExternalApplications ExternalTypeDeclarations");
      componentSettings.put("ExternalPackagesLangName", "externalPackage");
      componentSettings.put("SettingsMenu", "@LanguageSwitcher @Reconfigurator");
      componentSettings.put("SettingsLangName", "settings");
      componentSettings.put("HelpMenu", "HelpManual HelpAbout");
      componentSettings.put("HelpLangName", "help");

      // toolbar
      componentSettings.put("defaultToolbarToolbar", "*filetoolbar jawe_edittoolbar *searchtoolbar *packagetoolbar *externaltoolbar *processtoolbar");
      componentSettings.put("filetoolbarToolbar", "NewPackage Open Reopen Close - Save SaveAs - Exit");
      componentSettings.put("searchtoolbarToolbar", "Search jaweAction_References");
      componentSettings
         .put("packagetoolbarToolbar",
              "PackageCheckValidity StartDesignTimeValidation StopDesignTimeValidation - PackageNewProcess - PackageNamespaces PackageProperties PackageProcesses PackageExternalPackages PackageAddExternalPackage PackageRemoveExternalPackage PackageTypeDeclarations PackageParticipants PackageApplications PackageWorkflowRelevantData");
      componentSettings.put("processtoolbarToolbar",
                            "ProcessProperties ProcessParticipants ProcessApplications ProcessWorkflowRelevantData ProcessFormalParameters");
      componentSettings.put("externaltoolbarToolbar", "ExternalParticipants ExternalProcesses ExternalApplications ExternalTypeDeclarations");

      // actions
      ActionBase action;
      ImageIcon icon;
      String langDepName;
      JaWEAction ja;

      // Close
      action = new Close(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/close.gif"));
      langDepName = "Close";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("Close", ja);

      // Exit
      action = new Exit(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/exit.gif"));
      langDepName = "Exit";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("Exit", ja);

      // HelpAbout
      action = new HelpAbout(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/twe16.png"));
      langDepName = "HelpAbout";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("HelpAbout", ja);

      // HelpManual
      try {
         String clsName = "org.enhydra.jawe.base.controller.actions.HelpManual";
         try {
            action = (ActionBase) Class.forName(clsName).getConstructor(new Class[] {
                                                                                      JaWEComponent.class
            }).newInstance(new Object[] {
                                          comp
            });
         } catch (Exception e) {
            action = null;
         }
         icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/manual.gif"));
         langDepName = "HelpManual";
         ja = new JaWEAction(action, icon, langDepName);
         componentAction.put("HelpManual", ja);
      } catch (Exception ex) {
      }

      // Language Switcher
      componentSettings.put("LanguageSwitcherClassName", "org.enhydra.jawe.components.languageswitcher.LanguageSwitcherManager");
      componentSettings.put("LanguageSwitcherSettingsName", "org.enhydra.jawe.components.languageswitcher.LanguageSwitcherSettings");

      // Reconfigurator
      componentSettings.put("ReconfiguratorClassName", "org.enhydra.jawe.components.reconfiguration.ReconfiguratorManager");
      componentSettings.put("ReconfiguratorSettingsName", "org.enhydra.jawe.components.reconfiguration.ReconfiguratorSettings");

      // NewPackage
      action = new NewPackage(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/new.gif"));
      langDepName = "NewPackage";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("NewPackage", ja);

      // Open
      action = new Open(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/open.gif"));
      langDepName = "Open";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("Open", ja);

      // RecentFiles
      componentSettings.put("RecentFilesClassName", "org.enhydra.jawe.base.recentfiles.RecentFilesManager");
      componentSettings.put("RecentFilesSettingsName", "org.enhydra.jawe.base.recentfiles.RecentFilesSettings");

      // Reopen
      action = new Reopen(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/reopen.gif"));
      langDepName = "Reopen";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("Reopen", ja);

      // Save
      action = new Save(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/save.gif"));
      langDepName = "Save";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("Save", ja);

      // SaveAs
      action = new SaveAs(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/saveas.gif"));
      langDepName = "SaveAs";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("SaveAs", ja);

      // Search
      try {
         String clsName = "org.enhydra.jawe.base.controller.actions.Search";
         try {
            action = (ActionBase) Class.forName(clsName).getConstructor(new Class[] {
                                                                                      JaWEComponent.class
            }).newInstance(new Object[] {
                                          comp
            });
         } catch (Exception e) {
            action = null;
         }
         icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/search.png"));
         langDepName = "Search";
         ja = new JaWEAction(action, icon, langDepName);
         componentAction.put("Search", ja);
      } catch (Exception ex) {
      }

      // Package
      // PackageApplications
      action = new PackageApplications(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/packageapplications.gif"));
      langDepName = "PackageApplications";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("PackageApplications", ja);

      // PackageCheckValidity
      action = new PackageCheckValidity(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/packagecheck.gif"));
      langDepName = "PackageCheckValidity";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("PackageCheckValidity", ja);

      // StartDesignTimeValidation
      action = new StartDesignTimeValidation(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/designtimevalidation-start.gif"));
      langDepName = "StartDesignTimeValidation";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("StartDesignTimeValidation", ja);

      // StopDesignTimeValidation
      action = new StopDesignTimeValidation(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/designtimevalidation-stop.gif"));
      langDepName = "StopDesignTimeValidation";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("StopDesignTimeValidation", ja);

      // PackageExternalPackages
      action = new PackageExternalPackages(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/externalpackages.gif"));
      langDepName = "PackageExternalPackages";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("PackageExternalPackages", ja);

      // PackageAddExternalPackage
      action = new PackageAddExternalPackage(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/externalpackagesadd.gif"));
      langDepName = "PackageAddExternalPackage";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("PackageAddExternalPackage", ja);

      // PackageRemoveExternalPackage
      action = new PackageRemoveExternalPackage(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/externalpackagesremove.gif"));
      langDepName = "PackageRemoveExternalPackage";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("PackageRemoveExternalPackage", ja);

      // PackageNamespaces
      action = new PackageNamespaces(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/namespaces.gif"));
      langDepName = "PackageNamespaces";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("PackageNamespaces", ja);

      // PackageNewProcess
      action = new PackageNewProcess(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/processnew.gif"));
      langDepName = "PackageNewProcess";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("PackageNewProcess", ja);

      // PackageParticipants
      action = new PackageParticipants(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/packageparticipants.gif"));
      langDepName = "PackageParticipants";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("PackageParticipants", ja);

      // PackageProcesses
      action = new PackageProcesses(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/processes.gif"));
      langDepName = "PackageProcesses";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("PackageProcesses", ja);

      // PackageProperties
      action = new PackageProperties(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/packageproperties.gif"));
      langDepName = "PackageProperties";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("PackageProperties", ja);

      // PackageTypeDeclarations
      action = new PackageTypeDeclarations(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/typedeclarations.gif"));
      langDepName = "PackageTypeDeclarations";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("PackageTypeDeclarations", ja);

      // PackageWorkflowRelevantData
      action = new PackageWorkflowRelevantData(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/packageworkflowrelevantdata.gif"));
      langDepName = "PackageWorkflowRelevantData";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("PackageWorkflowRelevantData", ja);

      // PackageReferredDocument
      action = new PackageReferredDocument(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/referred_document.png"));
      langDepName = "PackageReferredDocument";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // Process
      // ProcessActivitiesOverview
      action = new ProcessActivitiesOverview(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/activities.gif"));
      langDepName = "ProcessActivitiesOverview";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("ProcessActivitiesOverview", ja);

      // ProcessActivitySetsOverview
      action = new ProcessActivitySetsOverview(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/activitysets.gif"));
      langDepName = "ProcessActivitySetsOverview";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("ProcessActivitySetsOverview", ja);

      // ProcessApplications
      action = new ProcessApplications(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/processapplications.gif"));
      langDepName = "ProcessApplications";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("ProcessApplications", ja);

      // ProcessFormalParameters
      action = new ProcessFormalParameters(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/processformalparameters.gif"));
      langDepName = "ProcessFormalParameters";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("ProcessFormalParameters", ja);

      // ProcessParticipants
      action = new ProcessParticipants(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/processparticipants.gif"));
      langDepName = "ProcessParticipants";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("ProcessParticipants", ja);

      // ProcessProperties
      action = new ProcessProperties(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/processproperties.gif"));
      langDepName = "ProcessProperties";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("ProcessProperties", ja);

      // ProcessTransitionsOverview
      action = new ProcessTransitionsOverview(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/transitions.gif"));
      langDepName = "ProcessTransitionsOverview";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("ProcessTransitionsOverview", ja);

      // ProcessWorkflowRelevantData
      action = new ProcessWorkflowRelevantData(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/processworkflowrelevantdata.gif"));
      langDepName = "ProcessWorkflowRelevantData";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("ProcessWorkflowRelevantData", ja);

      // External Packages
      // External Applications
      action = new ExternalApplications(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/externalapplications.gif"));
      langDepName = "ExternalApplications";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("ExternalApplications", ja);

      // External TypeDeclarations
      action = new ExternalTypeDeclarations(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/externaltypedeclarations.gif"));
      langDepName = "ExternalTypeDeclarations";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("ExternalTypeDeclarations", ja);

      // ExternalParticipants
      action = new ExternalParticipants(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/externalparticipants.gif"));
      langDepName = "ExternalParticipants";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("ExternalParticipants", ja);

      // ExternalProcesses
      action = new ExternalProcesses(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/externalprocesses.gif"));
      langDepName = "ExternalProcesses";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put("ExternalProcesses", ja);
   }

   public String getMainMenuActionOrder() {
      return (String) componentSettings.get("MainMenu");
   }

   public String getMenuActionOrder(String menuName) {
      return (String) componentSettings.get(menuName + "Menu");
   }

   public String getToolbarActionOrder(String toolbarName) {
      return (String) componentSettings.get(toolbarName + "Toolbar");
   }

   public boolean allowInvalidPackageSaving() {
      return ((Boolean) componentSettings.get("AllowInvalidPackageSaving")).booleanValue();
   }

   public boolean isDesingTimeValidationEnabled() {
      return ((Boolean) componentSettings.get("DesignTimeValidation")).booleanValue();
   }

   public boolean isInitialXPDLValidationEnabled() {
      return ((Boolean) componentSettings.get("InitialXPDLValidation")).booleanValue();
   }

   public boolean shoudAskOnDeletion() {
      return ((Boolean) componentSettings.get("AskOnDeletion")).booleanValue();
   }

   public boolean shouldAskOnDeletionOfReferencedElements() {
      return ((Boolean) componentSettings.get("AskOnDeletionOfReferencedElements")).booleanValue();
   }

   public boolean shoudStartMaximized() {
      return ((Boolean) componentSettings.get("StartMaximized")).booleanValue();
   }

   public boolean showTooltip() {
      return ((Boolean) componentSettings.get("ShowTooltip")).booleanValue();
   }

   public String undoHistoryManagerClass() {
      return (String) componentSettings.get("UndoHistoryManager.Class");
   }

   public int undoHistorySize() {
      return ((Integer) componentSettings.get("UndoHistorySize")).intValue();
   }

   public String getEncoding() {
      return (String) componentSettings.get("Encoding");
   }

   public String getFrameSettings() {
      return (String) componentSettings.get("FrameSettings");
   }

   public String getDefaultActionsEditOrder() {
      return (String) componentSettings.get("DefaultActionsEditOrder");
   }

   public String getResolverTypeClassName() {
      return (String) componentSettings.get("TypeResolverClass");
   }

   public String doNotAskOnDeletionOfReferencedElementTypes() {
      return (String) componentSettings.get("DoNotAskOnDeletionOfReferencedElementTypes");
   }

   public double getMainDividerLocation() {
      return ((Double) componentSettings.get("MainDividerLocation")).doubleValue();
   }

   public double getFirstSmallDividerLocation() {
      return ((Double) componentSettings.get("FirstSmallDividerLocation")).doubleValue();
   }

   public double getSecondSmallDividerLocation() {
      return ((Double) componentSettings.get("SecondSmallDividerLocation")).doubleValue();
   }

   public ImageIcon getApplicationIcon() {
      return (ImageIcon) componentSettings.get("ApplicationIcon");
   }

   public ImageIcon getIconFor(String actionName) {
      return (ImageIcon) componentSettings.get("DefaultImage" + actionName);
   }

   public ImageIcon getSponsore1LogoIcon() {
      return (ImageIcon) componentSettings.get("Sponsore1Logo");
   }

   public ImageIcon getSponsore2LogoIcon() {
      return (ImageIcon) componentSettings.get("Sponsore2Logo");
   }

   public boolean useJaWEFrame() {
      return true;
   }
}
