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