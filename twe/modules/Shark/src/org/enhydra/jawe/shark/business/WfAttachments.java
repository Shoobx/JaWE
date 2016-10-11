package org.enhydra.jawe.shark.business;

import java.util.List;

import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;

public class WfAttachments extends XMLCollection {

   private List filterContentVariable;
   private List filterNameVariableOrExpression;
   private String tokenizer;
   
   public WfAttachments(XMLComplexElement iea, String name, List filterContentVariable, List filterNameVariableOrExpression, String tokenizer, boolean isRequired) {
      super(iea, name, isRequired);
      this.filterContentVariable = filterContentVariable;
      this.filterNameVariableOrExpression = filterNameVariableOrExpression;
      this.tokenizer = tokenizer;
      notifyMainListeners = false;
      notifyListeners = false;
   }

   public void createStructure(String cvs, String nve) {
      String[] tokcvs = XMLUtil.tokenize(cvs, tokenizer);
      String[] toknve = XMLUtil.tokenize(nve, tokenizer);
      for (int i = 0; i < tokcvs.length; i++) {
         WfAttachment sp = new WfAttachment(this, filterContentVariable, filterNameVariableOrExpression);
         sp.setContentVariable(tokcvs[i].trim());
         String name = "";
         if (toknve.length>i) {
            name = toknve[i].trim();
         }
         sp.setNameVariableOrExpression(name);
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
      return new WfAttachment(this, filterContentVariable, filterNameVariableOrExpression);
   }

   public String getTokenizer () {
      return tokenizer;
   }
   
}
