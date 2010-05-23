package org.enhydra.jawe.components.wfxml;

import org.enhydra.shark.xpdl.XMLAttribute;
import org.enhydra.shark.xpdl.XMLComplexElement;

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
