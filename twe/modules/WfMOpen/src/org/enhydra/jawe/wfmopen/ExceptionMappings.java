package org.enhydra.jawe.wfmopen;

import java.util.List;

import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLElement;

public class ExceptionMappings extends XMLCollection {

   public ExceptionMappings(ImplementationExtendedAttribute iea) {
      super(iea, false);
      notifyMainListeners = false;
      notifyListeners = false;
   }

   public void createStructure (List excMappings) {
      for (int i=0; i<excMappings.size(); i++) {
         elements.add(excMappings.get(i));
      }
   }
   
   public void setValue(String v) {
      if (isReadOnly) {
         throw new RuntimeException("Can't set the value of read only element!");
      }
      if (v == null) {
         getParent().setValue(v);
      } else {
         this.value = v;
      }
   }

   public void add (XMLElement el) {
      super.add(el);
      setValue(null);
   }   
   
   public boolean add (int no,XMLElement el) {
      boolean ret=super.add(no,el);
      setValue(null);
      return ret;
   }   

   protected XMLElement removeElement (int no) {
      XMLElement el=super.removeElement(no);
      setValue(null);
      return el;
   }   

   public boolean reposition (XMLElement el,int newPos) {
      boolean ret=super.reposition(el, newPos);
      setValue(null);
      return ret;
   }
   
   public XMLElement generateNewElement() {
      return new ExceptionMapping(this);
   }

}
