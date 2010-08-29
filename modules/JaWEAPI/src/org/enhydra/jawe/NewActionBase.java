/**
* Together Workflow Editor
* Copyright (C) 2010 Together Teamsolutions Co., Ltd. 
* 
* This program is free software: you can redistribute it and/or modify 
* it under the terms of the GNU General Public License as published by 
* the Free Software Foundation, either version 3 of the License, or 
* (at your option) any later version. 
*
* This program is distributed in the hope that it will be useful, 
* but WITHOUT ANY WARRANTY; without even the implied warranty of 
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
* GNU General Public License for more details. 
*
* You should have received a copy of the GNU General Public License 
* along with this program. If not, see http://www.gnu.org/licenses
*/

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
