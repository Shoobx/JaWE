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

package org.enhydra.jawe.components.wfxml;

import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLComplexElement;

public class DefInfo extends XMLComplexElement {
   
   public DefInfo (DefInfos di) {
      super(di,true);
   }

   protected void fillStructure () {
      XMLAttribute attrDefinitionKey = new XMLAttribute(this,"DefinitionKey",true);
      XMLAttribute attrName=new XMLAttribute(this,"Name",true);
      XMLAttribute attrDescription=new XMLAttribute(this,"Description",false);
      XMLAttribute attrVersion=new XMLAttribute(this,"Version",false);
      XMLAttribute attrStatus=new XMLAttribute(this,"Status",false);
      
      add(attrDefinitionKey);
      add(attrName);
      add(attrDescription);
      add(attrVersion);
      add(attrStatus);
   }
   
   public String getDefinitionKey () {
      return get("DefinitionKey").toValue();
   }

   public void setDefinitionKey (String defKey) {
      set("DefinitionKey",defKey);
   }
   
   public String getName () {
      return get("Name").toValue();
   }

   public void setName (String nm) {
      set("Name",nm);
   }
   
   public String getDescription () {
      return get("Description").toValue();
   }

   public void setDescription (String dsc) {
      set("Description",dsc);
   }
   
   public String getVersion () {
      return get("Version").toValue();
   }

   public void setVersion (String ver) {
      set("Version",ver);
   }
   
   public String getStatus () {
      return get("Status").toValue();
   }

   public void setStatus (String stat) {
      set("Status",stat);
   }
     
}
