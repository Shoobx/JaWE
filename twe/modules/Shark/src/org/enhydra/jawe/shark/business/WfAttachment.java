package org.enhydra.jawe.shark.business;

import java.util.List;

import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;

public class WfAttachment extends XMLComplexElement {
   
   private List filterContentVariable;
   private List filterNameVariableOrExpression;
   
   public WfAttachment (WfAttachments parent,List filterContentVariable, List filterNameVariableOrExpression) {
      super(parent, true);
      this.filterContentVariable=filterContentVariable;
      this.filterNameVariableOrExpression=filterNameVariableOrExpression;
      notifyMainListeners = false;
      notifyListeners = false;
  }

   protected void fillStructure () {
      XMLAttribute attrContentVariable=new XMLAttribute(this,"ContentVariable", true); //required      
      add(attrContentVariable);
      XMLAttribute attrNameVariableOrExpression=new XMLAttribute(this,"NameVariableOrExpression", false); //not required      
      add(attrNameVariableOrExpression);
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
         if (((WfAttachments)parent).contains(this)) {
            getParent().setValue(v);
         }
      } else {
         this.value = v;
      }
   }

   public String getContentVariable () {
      return get("ContentVariable").toValue();
   }
   
   public void setContentVariable (String cv) {
      get("ContentVariable").setValue(cv);
   }

   public String getNameVariableOrExpression () {
      return get("NameVariableOrExpression").toValue();
   }

   public void setNameVariableOrExpression (String noe) {
      get("NameVariableOrExpression").setValue(noe);
   }

   public List getFilterContentVariable () {
      return filterContentVariable;
   }
   
   public List getFilterNameVariableOrExpression () {
      return filterNameVariableOrExpression;
   }
}
