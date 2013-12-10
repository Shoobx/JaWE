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

package org.enhydra.jawe.components.extpkgrelations;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.XPDLElementChangeInfo;
import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLElementChangeInfo;
import org.enhydra.shark.xpdl.elements.Package;

/**
 * Used to display the relations amongst Packages.
 * 
 * @author Sasa Bojanic
 */
public class ExtPkgRelations implements Observer, JaWEComponent {

   protected String type = JaWEComponent.SPECIAL_COMPONENT;

   protected boolean updateInProgress = false;

   protected ExtPkgRelationsPanel panel;

   protected ExtPkgRelationsSettings settings;

   public ExtPkgRelations(JaWEComponentSettings settings) throws Exception {
      this.settings = (ExtPkgRelationsSettings) settings;
      this.settings.init(this);
      init();
      JaWEManager.getInstance().getJaWEController().addObserver(this);
   }

   public void update(Observable o, Object arg) {
      if (!(arg instanceof XPDLElementChangeInfo))
         return;
      XPDLElementChangeInfo info = (XPDLElementChangeInfo) arg;
      int action = info.getAction();
      if (!(action == XMLElementChangeInfo.INSERTED
            || action == XMLElementChangeInfo.REMOVED
            || action == XPDLElementChangeInfo.SELECTED || action == XPDLElementChangeInfo.VALIDATION_ERRORS))
         return;

      long start = System.currentTimeMillis();
      JaWEManager.getInstance()
         .getLoggingManager()
         .info("ExtPkgOverview -> update for event " + info + " started ...");
      update(info);
      JaWEManager.getInstance()
         .getLoggingManager()
         .info("ExtPkgOverview -> update ended...");
      long end = System.currentTimeMillis();
      double diffs = (end - start) / 1000.0;
      JaWEManager.getInstance()
         .getLoggingManager()
         .debug("THE UPDATE OF EXT PKG OVERVIEW COMPONENT LASTED FOR "
                + diffs + " SECONDS!");
   }

   public void update(XPDLElementChangeInfo info) {
      if (updateInProgress)
         return;
      if (info.getSource() == this) {
         return;
      }
      updateInProgress = true;
      try {
         panel.refreshExtPkgPanel(info);
         settings.adjustActions();
      } finally {
         updateInProgress = false;
      }
   }

   protected void init() {
      panel = new ExtPkgRelationsPanel(this);
      panel.configure();
   }

   public JaWEComponentView getView() {
      return panel;
   }

   public JComponent getDisplay() {
      return panel.getDisplay();
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getName() {
      return "ExtPkgRelationsComponent";
   }

   public JaWEComponentSettings getSettings() {
      return settings;
   }

   public ExtPkgRelationsSettings getExtSettings() {
      return settings;
   }

   public boolean hasRelations() {
      return panel.hasRelations();
   }

   public boolean adjustXPDL(Package pkg) {
      return false;
   }

   public List checkValidity(XMLElement el, boolean fullCheck) {
      return null;
   }

   public boolean canCreateElement(XMLCollection col) {
      return true;
   }

   public boolean canInsertElement(XMLCollection col, XMLElement el) {
      return true;
   }

   public boolean canModifyElement(XMLElement el) {
      return true;
   }

   public boolean canRemoveElement(XMLCollection col, XMLElement el) {
      return true;
   }

   public boolean canDuplicateElement(XMLCollection col, XMLElement el) {
      return true;
   }

   public boolean canRepositionElement(XMLCollection col, XMLElement el) {
      return true;
   }

   public void setUpdateInProgress(boolean inProgress) {
      updateInProgress = inProgress;
   }

   public boolean isUpdateInProgress() {
      return updateInProgress;
   }

}
