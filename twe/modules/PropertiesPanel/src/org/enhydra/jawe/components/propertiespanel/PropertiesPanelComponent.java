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

/*
 * Created on Sep 20, 2005
 */
package org.enhydra.jawe.components.propertiespanel;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.XPDLElementChangeInfo;
import org.enhydra.jawe.base.panel.InlinePanel;
import org.enhydra.jawe.base.panel.PanelSettings;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLElementChangeInfo;
import org.enhydra.jxpdl.elements.Package;

public class PropertiesPanelComponent implements JaWEComponent, Observer {

   protected String type = JaWEComponent.OTHER_COMPONENT;

   protected InlinePanel inlinePanel;

   protected String name = null;

   protected PanelSettings settings;

   protected boolean updateInProgress = false;

   public PropertiesPanelComponent(JaWEComponentSettings settings) throws Exception {

      try {
         ClassLoader cl = getClass().getClassLoader();
         inlinePanel = (InlinePanel) cl.loadClass(JaWEManager.getInstance()
            .getInlinePanelClassName()).newInstance();
      } catch (Exception ex) {
         String msg = "PropertiesPanelComponent --> Problems while instantiating InlinePanel class '"
                      + JaWEManager.getInstance().getInlinePanelClassName()
                      + "' - using default implementation!";
         JaWEManager.getInstance().getLoggingManager().error(msg, ex);
         inlinePanel = new InlinePanel();
      }
      try {
         this.settings = (PanelSettings) settings;
         inlinePanel.setJaWEComponent(this);
         // settings must be initialized after creation of InlinePanel
         this.settings.init(this);
         inlinePanel.init();

         JaWEManager.getInstance().getJaWEController().addObserver(this);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public JaWEComponentSettings getSettings() {
      return settings;
   }

   public PanelSettings getPanelSettings() {
      return (PanelSettings) getSettings();
   }

   public void setUpdateInProgress(boolean updateInProgress) {
      this.updateInProgress = updateInProgress;
   }

   public boolean isUpdateInProgress() {
      return updateInProgress;
   }

   public void update(Observable o, Object arg) {
      if (!(arg instanceof XPDLElementChangeInfo))
         return;
      if (updateInProgress)
         return;

      if (!(arg instanceof XPDLElementChangeInfo))
         return;

      XPDLElementChangeInfo info = (XPDLElementChangeInfo) arg;
      int action = info.getAction();
      if (!(action == XMLElementChangeInfo.UPDATED
            || action == XMLElementChangeInfo.INSERTED
            || action == XMLElementChangeInfo.REMOVED
            || action == XPDLElementChangeInfo.SELECTED
            || action == XPDLElementChangeInfo.UNDOABLE_ACTION_ENDED
            || action == XPDLElementChangeInfo.UNDO || action == XPDLElementChangeInfo.REDO))
         return;

      long start = System.currentTimeMillis();
      updateInProgress = true;
      try {
         if (action == XPDLElementChangeInfo.UNDOABLE_ACTION_ENDED
             || action == XPDLElementChangeInfo.UNDO
             || action == XPDLElementChangeInfo.REDO) {
            if (action == XPDLElementChangeInfo.UNDOABLE_ACTION_ENDED) {
               for (int i = 0; i < info.getChangedSubElements().size(); i++) {
                  XPDLElementChangeInfo subinfo = (XPDLElementChangeInfo) info.getChangedSubElements()
                     .get(i);
                  if (subinfo.getAction() == XMLElementChangeInfo.REMOVED) {
                     inlinePanel.update(subinfo);
                  }
               }
            }
            inlinePanel.setActiveElement(null);
         } else {
            inlinePanel.update(info);
         }
      } finally {
         updateInProgress = false;
      }
      long end = System.currentTimeMillis();
      double diffs = (end - start) / 1000.0;
      JaWEManager.getInstance()
         .getLoggingManager()
         .debug("THE UPDATE OF PROPERTY PANEL COMPONENT LASTED FOR "
                + diffs + " SECONDS!");
   }

   public JaWEComponentView getView() {
      return inlinePanel;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getName() {
      return "PropertiesPanelComponent";
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

   public boolean canModifyElement(XMLCollection col, XMLElement el) {
      return true;
   }

   public boolean canModifyElement(XMLElement col) {
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

}
