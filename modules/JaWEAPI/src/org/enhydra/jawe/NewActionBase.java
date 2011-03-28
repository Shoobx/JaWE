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

import org.enhydra.jxpdl.elements.ExtendedAttribute;

/**
 * The base class for JaWE actions for generating new XPDL elements.
 */
public abstract class NewActionBase extends ActionBase {

   /**
    * The class for the XPDL element.
    */
   protected Class xpdlTypeClass;

   /**
    * If XPDL element class is {@link ExtendedAttribute}, represents the class of its
    * parent XPDL element.
    */
   protected Class xpdlTypeClassParentForEA;

   /**
    * Constructor.
    * 
    * @param jawecomponent {@link JaWEComponent} for this action.
    * @param xpdlTypeClass The class for XPDL element.
    * @param xpdlTypeClassParentForEA The class for the parent element of the XPDL
    *           element.
    */
   public NewActionBase(JaWEComponent jawecomponent,
                        Class xpdlTypeClass,
                        Class xpdlTypeClassParentForEA) {
      super(jawecomponent);
      this.xpdlTypeClass = xpdlTypeClass;
      this.xpdlTypeClassParentForEA = xpdlTypeClassParentForEA;
   }

   /**
    * 
    * @param jawecomponent {@link JaWEComponent} for this action.
    * @param name The action name.
    * @param xpdlTypeClass The class for XPDL element.
    * @param xpdlTypeClassParentForEA The class for the parent element of the XPDL
    *           element.
    */
   public NewActionBase(JaWEComponent jawecomponent,
                        String name,
                        Class xpdlTypeClass,
                        Class xpdlTypeClassParentForEA) {
      super(jawecomponent, name);
      this.xpdlTypeClass = xpdlTypeClass;
      this.xpdlTypeClassParentForEA = xpdlTypeClassParentForEA;
   }

   /**
    * 
    * @return The class for the XPDL element.
    */
   public Class getXPDLTypeClass() {
      return xpdlTypeClass;
   }

   /**
    * 
    * @return The class of XPDL element's parent element.
    */
   public Class getXPDLTypeClassParentForEA() {
      return xpdlTypeClassParentForEA;
   }

}
