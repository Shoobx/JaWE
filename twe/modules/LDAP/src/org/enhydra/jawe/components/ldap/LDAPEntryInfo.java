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

import org.enhydra.shark.xpdl.XMLAttribute;
import org.enhydra.shark.xpdl.XMLComplexElement;

public class LDAPEntryInfo extends XMLComplexElement {
   
   public LDAPEntryInfo () {
      super(null,true);
   }

   protected void fillStructure () {
      XMLAttribute attrId = new XMLAttribute(this,"Id",true);
      XMLAttribute attrName=new XMLAttribute(this,"Name",true);
      XMLAttribute attrDescription=new XMLAttribute(this,"Description",false);
      XMLAttribute attrType=new XMLAttribute(this,"Type",false);
      XMLAttribute attrDN=new XMLAttribute(this,"DN",false);
      XMLAttribute attrDetails=new XMLAttribute(this,"Details",false);
      
      add(attrId);
      add(attrName);
      add(attrDescription);
      add(attrType);
      add(attrDN);
      add(attrDetails);
   }
   
   public String getId () {
      return get("Id").toValue();
   }

   public void setId (String id) {
      set("Id",id);
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
   
   public String getDN () {
      return get("DN").toValue();
   }

   public void setDN (String dn) {
      set("DN",dn);
   }
   
   public String getType () {
      return get("Type").toValue();
   }

   public void setType (String type) {
      set("Type",type);
   }
   
   public String getDetails () {
      return get("Details").toValue();
   }

   public void setDetails (String det) {
      set("Details",det);
   }
     
}
