package org.enhydra.jawe.shark;

import java.util.List;

import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLElement;

public class WfVariable extends XMLCollectionElement {
   
   private List filter;
   
   public WfVariable (WfVariables parent,List filter) {
      super(parent, true);
      this.filter=filter;
      notifyMainListeners = false;
      notifyListeners = false;
  }

//   protected void fillStructure () {
//      XMLAttribute attrId=new XMLAttribute(this,"Id", true); //required      
//      super.add(attrId);
//   }

   public void makeAs (XMLElement el) {
      super.makeAs(el);
      setValue(el.toValue());
   }

   public void setValue(String v) {
      if (isReadOnly) {
         throw new RuntimeException("Can't set the value of read only element!");
      }
      if (v == null) {
         if (((WfVariables)parent).contains(this)) {
            getParent().setValue(v);
         }
      } else {
         this.value = v;
      }
   }

   public List getFilter () {
      return filter;
   }
   
}
