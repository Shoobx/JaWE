package org.enhydra.jawe.wfmopen;

import org.enhydra.shark.xpdl.XMLAttribute;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.XMLElement;

/**
 *  Represents special element for WfMOpen ext. attrib of Application definition.
 *
 *  @author Sasa Bojanic
 */
public class ExceptionMapping extends XMLComplexElement {
   
   public ExceptionMapping (ExceptionMappings parent) {
      super(parent, false);
      notifyMainListeners = false;
      notifyListeners = false;
  }

   protected void fillStructure () {
      XMLAttribute attrJavaExc=new XMLAttribute(this,"JavaException", true);
      XMLAttribute attrProcessException=new XMLAttribute(this,"ProcessException", false);
      add(attrJavaExc);
      add(attrProcessException);
   }

   public void makeAs (XMLElement el) {
      super.makeAs(el);
      setValue(el.toValue());
   }

   public void setValue(String v) {
      if (isReadOnly) {
         throw new RuntimeException("Can't set the value of read only element!");
      }
      if (v == null) {
         if (((ExceptionMappings)parent).contains(this)) {
            getParent().setValue(v);
         }
      } else {
         this.value = v;
      }
   }
   
   public String getJavaException() {
      return get("JavaException").toValue();
   }
   public void setJavaException(String javaExc) {
      set("JavaException",javaExc);
   }
   public String getProcessException() {
      return get("ProcessException").toValue();
   }
   public void setProcessException(String procExc) {
      set("ProcessException",procExc);
   }

}
