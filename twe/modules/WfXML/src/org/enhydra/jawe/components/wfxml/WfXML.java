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

package org.enhydra.jawe.components.wfxml;

import java.net.Authenticator;
import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;

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
 *  Used to handle search events such as "References" event.
 *
 *  @author Sasa Bojanic
 */
public class WfXML implements JaWEComponent, Observer {

   protected String type = JaWEComponent.OTHER_COMPONENT;
   
   protected WfXMLPanel panel;

   protected boolean updateInProgress=false;

   protected DefInfos defInfos=new DefInfos();
   
   protected WfXMLSettings settings;   
   
   public WfXML(JaWEComponentSettings settings) throws Exception {
      Authenticator.setDefault(new WfXMLAuthenticator(JaWEManager.getInstance().getJaWEController().getJaWEFrame()));
      this.settings = (WfXMLSettings)settings;
      this.settings.init(this);
      init();
      JaWEManager.getInstance().getJaWEController().addObserver(this);
   }

   public void update(Observable o, Object arg) {
      if (!(arg instanceof XPDLElementChangeInfo)) return;
      XPDLElementChangeInfo info=(XPDLElementChangeInfo)arg;
      int action=info.getAction();
      if (!(action == XMLElementChangeInfo.UPDATED ||
            action == XMLElementChangeInfo.INSERTED ||
            action == XMLElementChangeInfo.REMOVED ||
            action == XPDLElementChangeInfo.SELECTED))
         return;      
      
      long start=System.currentTimeMillis();
      JaWEManager.getInstance().getLoggingManager().info("WfXML -> update for event "+info+" started ...");
      settings.adjustActions();
      JaWEManager.getInstance().getLoggingManager().info("WfXML -> update ended...");
      long end=System.currentTimeMillis();
      double diffs=(end-start)/1000.0;
      JaWEManager.getInstance().getLoggingManager().debug("THE UPDATE OF WfXML COMPONENT LASTED FOR "+diffs+" SECONDS!");
   }
   
   public JaWEComponentSettings getSettings() {
      return settings;
   }
   
   public WfXMLSettings getWfXMLSettings() {
      return settings;
   }
   
   public boolean hasEntries () {
      return defInfos.size()>0;
   }

   public boolean hasConnection () {
      return panel.hasConnection();
   }
   
   public boolean hasConnectionsInHistory () {
      String conn=panel.getSelectedConnection();
      if (conn!=null && conn.trim().length()>0) {
         return true;
      }
      
      return false;
   }
   
   public void clearConnectionHistory () {
      panel.getComboPanel().cleanup();   
   }

   public DefInfos getDefInfos() {
      return defInfos;
   }
   
   public void listDefinitions (String conn) throws Exception {
      getDefInfos().clear();
      List l=WfXMLConnector.wfxmlListDefinitions(new URL(conn),getDefInfos());
      getDefInfos().addAll(l);      
   }
   
   protected void init () {
      panel=new WfXMLPanel(this);
      panel.configure();
      settings.adjustActions();
   }

   public JaWEComponentView getView () {
       return panel;
    }

    public JComponent getDisplay () {
       return panel.getDisplay();
    }

    public String getType() {
       return type;
    }   

    public void setType(String type) {
       this.type = type; 
    }
    
    public String getName () {
       return "WfXML";
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
   
    public void setUpdateInProgress(boolean inProgress) {
       
    }
    
    public boolean isUpdateInProgress() {
       return false;
    }
    
}
