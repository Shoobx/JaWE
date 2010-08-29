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

package org.enhydra.jawe.components.ldap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.naming.NamingException;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.XPDLElementChangeInfo;
import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLElementChangeInfo;
import org.enhydra.shark.xpdl.elements.Package;

public class LDAPController implements JaWEComponent, Observer {

   protected String type = JaWEComponent.OTHER_COMPONENT;

   protected LDAPPanel panel;

   protected boolean updateInProgress = false;

   protected LDAPSettings settings;

   protected LDAPConfiguration ldapConfig;

   public LDAPController(JaWEComponentSettings settings) throws Exception {
      this.settings = (LDAPSettings) settings;
      this.settings.init(this);
      init();
      JaWEManager.getInstance().getJaWEController().addObserver(this);
      ldapConfig = new LDAPConfiguration(this.settings);
   }

   public LDAPConfiguration getLDAPConfig() {
      return ldapConfig;
   }

   public void update(Observable o, Object arg) {
      if (!(arg instanceof XPDLElementChangeInfo))
         return;
      XPDLElementChangeInfo info = (XPDLElementChangeInfo) arg;
      int action = info.getAction();
      if (!(action == XMLElementChangeInfo.REMOVED
            || action == XMLElementChangeInfo.INSERTED || action == XPDLElementChangeInfo.SELECTED))
         return;

      long start = System.currentTimeMillis();
      JaWEManager.getInstance().getLoggingManager().info("LDAP -> update for event "
                                                         + info + " started ...");
      update(info);
      JaWEManager.getInstance().getLoggingManager().info("LDAP -> update ended...");
      long end = System.currentTimeMillis();
      double diffs = (end - start) / 1000.0;
      JaWEManager.getInstance()
         .getLoggingManager()
         .debug("THE UPDATE OF LDAP COMPONENT LASTED FOR " + diffs + " SECONDS!");
   }

   public void update(XPDLElementChangeInfo info) {
      if (updateInProgress)
         return;
      if (info.getSource() == this) {
         return;
      }
      updateInProgress = true;
      try {
         if (info.getChangedElement() instanceof Package
             && !info.getChangedElement().isReadOnly()) {
            if (info.getAction() == XMLElementChangeInfo.REMOVED) {
               boolean remove = false;
               if (info.getChangedSubElements().size() == 0) {
                  remove = true;
               }
               for (int i = 0; i < info.getChangedSubElements().size(); i++) {
                  if (info.getChangedSubElements().get(i) instanceof Package && !((Package)info.getChangedSubElements().get(i)).isReadOnly()) {
                     remove = true;
                     break;
                  }
               }
               if (remove) {
                  panel.refreshPanel(new ArrayList());
               }
            }
         }
         settings.adjustActions();
      } finally {
         updateInProgress = false;
      }
   }

   public JaWEComponentSettings getSettings() {
      return settings;
   }

   public LDAPSettings getLDAPSettings() {
      return settings;
   }

   public void listEntries() {
      List infos=new ArrayList();
      try {
         LDAPUtils.getEntries(getLDAPConfig(),
                                               Arrays.asList(settings.getLDAPObjectClassFilterChoices()),
                                               panel.getSelectedObjectClass(),
                                               panel.getSearchCriteria(),
                                               infos);

      } catch (NamingException ex) {
         ex.printStackTrace();
         if (ex.getMessage().indexOf("Sizelimit Exceeded") != -1) {
            JaWEManager.getInstance()
               .getJaWEController()
               .message(ResourceManager.getLanguageDependentString("ErrorLDAPSearchPartiallyFailedSizelimitExceeded"),
                        JOptionPane.ERROR_MESSAGE);

         } else if (ex.getMessage().indexOf("Timelimit Exceeded") != -1) {
            JaWEManager.getInstance()
               .getJaWEController()
               .message(ResourceManager.getLanguageDependentString("ErrorLDAPSearchPartiallyFailedTimelimitExceeded"),
                        JOptionPane.ERROR_MESSAGE);

         } else {
            JaWEManager.getInstance()
               .getJaWEController()
               .message(ResourceManager.getLanguageDependentString("ErrorLDAPSearchFailed")
                              + "\n" + ex.getClass() + ":" + ex.getMessage(),
                        JOptionPane.ERROR_MESSAGE);
         }
         System.err.println("Search failed: " + ex.getMessage());
      } catch (Exception ex) {
         ex.printStackTrace();

         /* Handle any other types of exceptions. */
         JaWEManager.getInstance()
            .getJaWEController()
            .message(ResourceManager.getLanguageDependentString("ErrorLDAPSearchFailed")
                           + "\n" + ex.getClass() + ":" + ex.getMessage(),
                     JOptionPane.ERROR_MESSAGE);
         System.err.println("Non-naming error: " + ex.getMessage());
      } finally {
         panel.refreshPanel(infos);
      }

   }

   public boolean hasSelectedEntries () {
      return panel.hasSelectedEntries();
   }

   public int howManyEntries () {
      return panel.howManyEntries();
   }
   
   public void importSelectedEntries () {
      List entries=panel.getSelectedEntries();
      LDAPUtils.createParticipants(entries);
   }
   
   public void importAllEntries () {
      List entries=panel.getAllEntries();
      LDAPUtils.createParticipants(entries);
   }
   
   protected void init() {
      panel = new LDAPPanel(this);
      panel.configure();
      settings.adjustActions();
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
      return "LDAPComponent";
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

   }

   public boolean isUpdateInProgress() {
      return false;
   }

}
