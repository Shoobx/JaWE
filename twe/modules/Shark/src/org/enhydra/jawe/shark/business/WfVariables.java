package org.enhydra.jawe.shark.business;

import java.util.List;

import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;

public class WfVariables extends XMLCollection {

   private List filter;
   private String tokenizer;
   
   public WfVariables(XMLComplexElement iea, String name, List filter, String tokenizer, boolean isRequired) {
      super(iea, name, isRequired);
      this.filter = filter;
      this.tokenizer = tokenizer;
      notifyMainListeners = false;
      notifyListeners = false;
   }

   public void createStructure(String startupParams) {
      String[] tok = XMLUtil.tokenize(startupParams, tokenizer);
      for (int i = 0; i < tok.length; i++) {
         WfVariable sp = new WfVariable(this, filter);
         sp.set("Id", tok[i].trim());
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
      return new WfVariable(this, filter);
   }

}
