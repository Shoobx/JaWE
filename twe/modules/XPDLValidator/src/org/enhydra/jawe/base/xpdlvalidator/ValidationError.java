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

package org.enhydra.jawe.base.xpdlvalidator;

import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Utils;
import org.enhydra.shark.xpdl.ParsingErrors;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLValidationError;


/**
 * @author Sasa Bojanic
 *
 */
public final class ValidationError  {

   private XMLValidationError verr;
   
   public ValidationError (XMLValidationError verror) {
      this.verr=verror;
   }
   
   public String getType () {
      return verr.getType();      
   }

   public String getSubType () {
      return verr.getSubType();      
   }

   public String getId () {
      return verr.getId();      
   }

   public String getDescription () {
      return verr.getDescription();      
   }

   public XMLElement getElement () {
      return verr.getElement();      
   }
   
   public String toString () {
      String retVal="";
      XMLElement el=verr.getElement();
      if (el != null) {
          retVal += Utils.getLocString(Utils.getLocation(el),el)+": ";        
           
          retVal+=ResourceManager.getLanguageDependentString("TypeKey")+"="+ResourceManager.getLanguageDependentString(verr.getType()+"TypeKey");
          retVal+=", "+ResourceManager.getLanguageDependentString("SubTypeKey")+"="+ResourceManager.getLanguageDependentString(verr.getSubType()+"TypeKey");
          String err=ResourceManager.getLanguageDependentString(verr.getId());
          if (err==null) {
             err=verr.getId();
          }
          retVal+=", "+err;
          if (verr.getDescription()!=null && verr.getDescription().length()>0) {
             String desc=verr.getDescription();
             if (verr.getSubType().equals(XMLValidationError.SUB_TYPE_SCHEMA)) {
                if (desc.indexOf(ParsingErrors.ERROR)==0) {
                   String ld=ResourceManager.getLanguageDependentString(ParsingErrors.ERROR+"Key")+" "+ResourceManager.getLanguageDependentString("AtLineNumberKey")+" ";
                   desc=ld+desc.substring(ParsingErrors.ERROR.length()+ParsingErrors.AT_LINE_NO_STRING.length());
                } else if (desc.indexOf(ParsingErrors.WARNING)==0) {
                   String ld=ResourceManager.getLanguageDependentString(ParsingErrors.WARNING+"Key")+" "+ResourceManager.getLanguageDependentString("AtLineNumberKey")+" ";
                   desc=ld+desc.substring(ParsingErrors.WARNING.length()+ParsingErrors.AT_LINE_NO_STRING.length());
                } else if (desc.indexOf(ParsingErrors.FATAL_ERROR)==0) {
                   String ld=ResourceManager.getLanguageDependentString(ParsingErrors.FATAL_ERROR+"Key")+" "+ResourceManager.getLanguageDependentString("AtLineNumberKey")+" ";
                   desc=ld+desc.substring(ParsingErrors.FATAL_ERROR.length()+ParsingErrors.AT_LINE_NO_STRING.length());
                }
             } else {
                retVal += ": ";
             }
             retVal += desc;
          }
       }
       return retVal;      
   }

}