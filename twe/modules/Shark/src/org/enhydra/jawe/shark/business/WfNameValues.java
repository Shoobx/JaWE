package org.enhydra.jawe.shark.business;

import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;

public class WfNameValues extends XMLCollection {

   private String listtokenizer;

   private String namevaluetokenizer;
   
   private String namepartlabelkey;
   
   private String valuepartlabelkey;

   public WfNameValues(XMLComplexElement iea, String name, String listtokenizer, String namevaluetokenizer, String namePartLabelKey, String valuePartLabelKey, boolean isRequired) {
      super(iea, name, isRequired);
      this.listtokenizer = listtokenizer;
      this.namevaluetokenizer = namevaluetokenizer;
      this.namepartlabelkey=namePartLabelKey;
      this.valuepartlabelkey=valuePartLabelKey;
      notifyMainListeners = false;
      notifyListeners = false;
   }

   public void createStructure(String cvs) {
      String[] tokcvs = XMLUtil.tokenize(cvs, listtokenizer);
      for (int i = 0; i < tokcvs.length; i++) {
         String[] toknve = XMLUtil.tokenize(tokcvs[i], namevaluetokenizer);
         WfNameValue sp = new WfNameValue(this, namepartlabelkey, valuepartlabelkey);
         sp.setNamePart(toknve[0].trim());
         sp.setValuePart(toknve[1].trim());
         elements.add(sp);
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

   public void add(XMLElement el) {
      super.add(el);
      setValue(null);
   }

   public boolean add(int no, XMLElement el) {
      boolean ret = super.add(no, el);
      setValue(null);
      return ret;
   }

   protected XMLElement removeElement(int no) {
      XMLElement el = super.removeElement(no);
      setValue(null);
      return el;
   }

   public boolean reposition(XMLElement el, int newPos) {
      boolean ret = super.reposition(el, newPos);
      setValue(null);
      return ret;
   }

   public XMLElement generateNewElement() {
      return new WfNameValue(this, namepartlabelkey, valuepartlabelkey);
   }

   public String getListTokenizer() {
      return listtokenizer;
   }

   public String getNameValueTokenizer() {
      return namevaluetokenizer;
   }

   public String getNamePartLabelKey () {
      return namepartlabelkey;
   }

   public String getValuePartLabelKey () {
      return valuepartlabelkey;
   }
   
}
