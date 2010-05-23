package org.enhydra.jawe;


/**
* The base class for JaWE actions for generating new XPDL elements.
*/
public abstract class NewActionBase extends ActionBase {

   protected Class xpdlTypeClass;
   protected Class xpdlTypeClassParentForEA;
   
   public NewActionBase(JaWEComponent jawecomponent,Class xpdlTypeClass,Class xpdlTypeClassParentForEA) {
      super(jawecomponent);
      this.xpdlTypeClass=xpdlTypeClass;
      this.xpdlTypeClassParentForEA=xpdlTypeClassParentForEA;
   }

   public NewActionBase(JaWEComponent jawecomponent, String name,Class xpdlTypeClass,Class xpdlTypeClassParentForEA) {
      super(jawecomponent,name);
      this.xpdlTypeClass=xpdlTypeClass;
      this.xpdlTypeClassParentForEA=xpdlTypeClassParentForEA;
   }

   public Class getXPDLTypeClass() {
      return xpdlTypeClass;
   }
   
   public Class getXPDLTypeClassParentForEA() {
      return xpdlTypeClassParentForEA;
   }   

}
