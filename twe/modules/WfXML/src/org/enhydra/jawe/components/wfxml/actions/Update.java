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

package org.enhydra.jawe.components.wfxml.actions;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.JOptionPane;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.WaitScreen;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.components.wfxml.DefInfo;
import org.enhydra.jawe.components.wfxml.WfXML;
import org.enhydra.jawe.components.wfxml.WfXMLConnector;
import org.enhydra.jawe.components.wfxml.WfXMLPanel;
import org.enhydra.jxpdl.elements.Package;

/**
 * Update XPDL to the engine.
 * 
 * @author Sasa Bojanic
 */
public class Update extends ActionBase {

   public Update(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      WfXML wfxml = (WfXML) jawecomponent;

      Package mainPkg = JaWEManager.getInstance().getJaWEController().getMainPackage();
      Package workingPkg = JaWEManager.getInstance()
         .getJaWEController()
         .getSelectionManager()
         .getWorkingPKG();
      if (wfxml.hasConnection()
          && mainPkg != null && mainPkg == workingPkg
          && guessIfUpdateCanWork(((WfXMLPanel) wfxml.getView()).getSelectedDefInfo())) {
         setEnabled(true);
      } else {
         setEnabled(false);
      }
   }

   public void actionPerformed(ActionEvent e) {
      WfXML wfxml = (WfXML) jawecomponent;
      WfXMLPanel panel = (WfXMLPanel) wfxml.getView();
      JaWEController jc = JaWEManager.getInstance().getJaWEController();

      DefInfo defInfo = panel.getSelectedDefInfo();
      String pkgId = jc.getMainPackageId();
      String conn = panel.getSelectedConnection();
      if (conn != null && pkgId != null && !pkgId.trim().equals("")) {
         WaitScreen ws = new WaitScreen(jc.getJaWEFrame());
         boolean err = false;
         String msg = null;
         try {
            ws.show(null,
                    "",
                    wfxml.getSettings().getLanguageDependentString("UpdatingKey"));
            String pkgContent = WfXMLConnector.xpdlToString(JaWEManager.getInstance()
               .getJaWEController()
               .getMainPackage());
            WfXMLConnector.wfxmlSetDefinition2(new URL(defInfo.getDefinitionKey()),
                                               pkgContent);
            try {
               wfxml.listDefinitions(conn);
               msg = pkgId
                     + " - "
                     + wfxml.getSettings()
                        .getLanguageDependentString("InformationWfXMLXPDLIsSucessfullyUpdated");
            } catch (Exception ex) {
               err = true;
               msg = wfxml.getSettings()
                  .getLanguageDependentString("ErrorWfXMLProblemsWhileGettingDefinitionListForRegistry")
                     + " " + conn + " !";
            }
         } catch (Exception ex) {
            ex.printStackTrace();
            err = true;
            msg = wfxml.getSettings()
               .getLanguageDependentString("ErrorWfXMLProblemsWhileUpdatingXPDL")
                  + " "
                  + defInfo.getDefinitionKey() + " !";
         } finally {
            ws.setVisible(false);
         }
         jc.message(msg, err ? JOptionPane.ERROR_MESSAGE
                            : JOptionPane.INFORMATION_MESSAGE);
      } else {
         // cont.complainLoudly("WarningEmptySelectionToEditOrDelete");
      }

   }

   protected boolean guessIfUpdateCanWork(DefInfo di) {
      boolean canWork = false;

      if (di != null) {
         canWork = true;
         String wpkgId = JaWEManager.getInstance()
            .getJaWEController()
            .getSelectionManager()
            .getWorkingPKG()
            .getId();
         String key = di.getDefinitionKey();

         if (key.indexOf("procMgr=" + wpkgId) == -1) {
            canWork = false;
         }
      }

      return canWork;
   }

}
