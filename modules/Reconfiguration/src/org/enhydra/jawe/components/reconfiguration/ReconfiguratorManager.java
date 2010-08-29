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

package org.enhydra.jawe.components.reconfiguration;

import java.util.List;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.elements.Package;

public class ReconfiguratorManager implements JaWEComponent {

   protected String type = JaWEComponent.OTHER_COMPONENT;
   
   protected ReconfiguratorMenu menu;
   
   protected ReconfiguratorSettings settings;

   public ReconfiguratorManager (JaWEComponentSettings settings) throws Exception {
      this.settings = (ReconfiguratorSettings) settings;
      this.settings.init(this);      
      init();
   }

   public JaWEComponentSettings getSettings() {
      return settings;
   }
   
   protected void init () { 
      menu = new ReconfiguratorMenu(this);
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
      return "ReconfiguratorComponent";
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
