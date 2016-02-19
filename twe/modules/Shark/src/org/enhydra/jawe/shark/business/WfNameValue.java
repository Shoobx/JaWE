package org.enhydra.jawe.shark.business;

import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;

public class WfNameValue extends XMLComplexElement {

   private String namepartlabelkey;
   
   private String valuepartlabelkey;

   public WfNameValue(WfNameValues parent, String namePartLabelKey, String valuePartLabelKey) {
      super(parent, true);
      this.namepartlabelkey = namePartLabelKey;
      this.valuepartlabelkey = valuePartLabelKey;
      _fillStructure(namePartLabelKey, valuePartLabelKey);
      notifyMainListeners = false;
      notifyListeners = false;
   }

   protected void fillStructure() {
   }

   protected void _fillStructure(String namePartLabelKey, String valuePartLabelKey) {
      XMLAttribute attrNamePart = new XMLAttribute(this, namePartLabelKey, true);
      add(attrNamePart);
      XMLAttribute attrValuePart = new XMLAttribute(this, valuePartLabelKey, true);
      add(attrValuePart);
   }

   public void makeAs(XMLElement el) {
      super.makeAs(el);
      setValue(el.toValue());
   }

   public void setValue(String v) {
      if (isReadOnly) {
         throw new RuntimeException("Can't set the value of read only element!");
      }
      if (v == null) {
         if (((WfNameValues) parent).contains(this)) {
            getParent().setValue(v);
         }
      } else {
         this.value = v;
      }
   }

   public String getNamePart() {
      return get(namepartlabelkey).toValue();
   }

   public void setNamePart(String np) {
      get(namepartlabelkey).setValue(np);
   }

   public String getValuePart() {
      return get(valuepartlabelkey).toValue();
   }

   public void setValuePart(String vp) {
      get(valuepartlabelkey).setValue(vp);
   }


   public String getNamePartLabelKey () {
      return namepartlabelkey;
   }

   public String getValuePartLabelKey () {
      return valuepartlabelkey;
   }
   
}
