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

package org.enhydra.jawe.components.xpdlview;

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
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.Package;

/**
 *  Used to handle process graphs.
 *
 *  @author Sasa Bojanic
 */
public class XPDLViewController implements Observer, JaWEComponent {
   
   protected String type = JaWEComponent.MAIN_COMPONENT;
   protected boolean updateInProgress=false;
   
   protected XPDLViewControllerPanel panel;   
   protected XPDLViewSettings settings;

   public XPDLViewController(JaWEComponentSettings settings)  throws Exception {
      this.settings = (XPDLViewSettings) settings;
      this.settings.init(this);
      init();
      JaWEManager.getInstance().getJaWEController().addObserver(this);
   }   
   
   // ********************** Observer   
   public void update(Observable o, Object arg) {
      if (!(arg instanceof XPDLElementChangeInfo)) return;
      XPDLElementChangeInfo info=(XPDLElementChangeInfo)arg;
      int action=info.getAction();
      if (!(action == XMLElementChangeInfo.UPDATED ||
            action == XMLElementChangeInfo.INSERTED ||
            action == XMLElementChangeInfo.REMOVED ||
            action == XMLElementChangeInfo.REPOSITIONED ||
            action == XPDLElementChangeInfo.SELECTED ||
            action == XPDLElementChangeInfo.UNDOABLE_ACTION_ENDED ||
            action == XPDLElementChangeInfo.UNDO ||
            action == XPDLElementChangeInfo.REDO)) 
         return;      
      
      long start=System.currentTimeMillis();
      JaWEManager.getInstance().getLoggingManager().info("XPDLViewController -> update for event "+info+" started ...");
      if (action==XPDLElementChangeInfo.UNDOABLE_ACTION_ENDED ||
            action==XPDLElementChangeInfo.UNDO ||
            action==XPDLElementChangeInfo.REDO) {

         panel.setSelectedElement(null);
      } else {
         update(info);
      }
      JaWEManager.getInstance().getLoggingManager().info("XPDLViewController -> update ended");
      long end=System.currentTimeMillis();
      double diffs=(end-start)/1000.0;
      JaWEManager.getInstance().getLoggingManager().debug("THE UPDATE OF XPDL COMPONENT LASTED FOR "+diffs+" SECONDS!");
   }   
   // **********************      

   // ********************** JaWEComponent   
   public JaWEComponentSettings getSettings() {
      return settings;
   }
   
   public JaWEComponentView getView () {
      return panel;
   }

   public String getName () {
      return "XPDLComponent";
   }
   
   public String getComponentType() {
      return type;
   }   

   public void setComponentType(String type) {
      this.type = type; 
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
   // **********************      
   
   protected void init () {
      // creating special items and buttons
      panel=createPanel();
   }
   
   protected XPDLViewControllerPanel createPanel () {
      XPDLViewControllerPanel p=new XPDLViewControllerPanel (this);
      p.configure();
      p.init();
      return p;
   }
   
   public void update (XPDLElementChangeInfo info) {
      if (updateInProgress) return;
      if (info.getSource()==this) return;
      updateInProgress=true;
      try {
         int action=info.getAction();
         if (action==XMLElementChangeInfo.REMOVED) {
            panel.setSelectedElement(null);
            return;
         }
         XMLElement curSelEl=panel.getSelectedElement();
         XMLElement toSelect;
         if (settings.showXPDLDetails())
            toSelect=info.getChangedElement();
         else
            toSelect = XMLUtil.getPackage(info.getChangedElement());
         if (!(action==XPDLElementChangeInfo.SELECTED || (curSelEl==toSelect && action==XMLElementChangeInfo.UPDATED))) {
            toSelect=null;
         }
         if (curSelEl!=toSelect || action==XMLElementChangeInfo.UPDATED) {
            panel.setSelectedElement(toSelect);
         }
      } finally {
         updateInProgress=false;
      }
   }   

   public XPDLViewSettings getXPDLViewSettings() {
      return settings;
   }

   public void setUpdateInProgress(boolean inProgress) {
      updateInProgress=true;
   }
   
   public boolean isUpdateInProgress() {
      return updateInProgress;
   }

}
