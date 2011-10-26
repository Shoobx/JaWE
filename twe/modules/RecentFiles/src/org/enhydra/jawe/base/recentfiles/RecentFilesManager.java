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
 * Miroslav Popov, Aug 5, 2005
 */
package org.enhydra.jawe.base.recentfiles;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.XPDLElementChangeInfo;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLElementChangeInfo;
import org.enhydra.jxpdl.elements.Package;

/**
 * @author Miroslav Popov
 *
 */
public class RecentFilesManager implements Observer, JaWEComponent {

   protected String type = JaWEComponent.OTHER_COMPONENT;
      
   protected RecentFilesMenu menu;
   
   protected RecentFilesSettings settings;

   public RecentFilesManager (JaWEComponentSettings settings) throws Exception {
      this.settings = (RecentFilesSettings) settings;
      this.settings.init(this);
      JaWEManager.getInstance().getJaWEController().addObserver(this);
      init();
   }

   public JaWEComponentSettings getSettings() {
      return settings;
   }
   
   protected void init () { 
      menu = new RecentFilesMenu(this);
      menu.configure();
      menu.init();
   }

   public JaWEComponentView getView () {
      return menu;
   }

   public String getType() {
      return type;
   }   

   public void setType(String type) {
      this.type = type; 
   }
   
   public String getName () {
      return "RecentFilesComponent";
   }
   
   public boolean adjustXPDL (Package pkg) {
      return false;
   }
   
   public List checkValidity (XMLElement el,boolean fullCheck) {
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

   
   public void update(Observable o, Object arg) {
      if (!(arg instanceof XPDLElementChangeInfo)) return;
      XPDLElementChangeInfo info=(XPDLElementChangeInfo)arg;
      XMLElement changedElement = info.getChangedElement();
      if (changedElement == null) return;

      int action = info.getAction();
      if (!(action == XMLElementChangeInfo.INSERTED ||
            action == XMLElementChangeInfo.REMOVED)) 
         return;     
      
      if (changedElement instanceof Package) {
         Package pkg = (Package) changedElement;
         if (action == XMLElementChangeInfo.INSERTED) {
            if (pkg == JaWEManager.getInstance().getJaWEController().getMainPackage()) {
               menu.addToRecentFiles(JaWEManager.getInstance().getXPDLHandler().getAbsoluteFilePath(pkg));
               menu.saveRecentFiles();
            }
         } else if (action == XMLElementChangeInfo.REMOVED) {
            if (JaWEManager.getInstance().getJaWEController().getMainPackage() == null) {
               String filePath = (String)info.getOldValue();
               if (filePath != null && !"".equals(filePath)) {
                  menu.addToRecentFiles(filePath);
                  menu.saveRecentFiles();
               }
            }
         }
      }
   }
   
   public void setUpdateInProgress(boolean inProgress) {
   }
   
   public boolean isUpdateInProgress() {
      return false;
   }
   
}
