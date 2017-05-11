/**
 * Together Workflow Editor
 * Copyright (C) 2011 Together Teamsolutions Co., Ltd. 
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

package org.enhydra.jawe.base.controller.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.base.controller.JaWEController;

/**
 * Removes design time validation.
 */
public class StopDesignTimeValidation extends ActionBase {

   public StopDesignTimeValidation(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      JaWEController jc = (JaWEController) jawecomponent;
      if (jc.getMainPackage() != null && jc.isDesignTimeValidation())
         setEnabled(true);
      else
         setEnabled(false);
   }

   public void actionPerformed(ActionEvent e) {
      JaWEController jc = (JaWEController) jawecomponent;
      jc.setDesignTimeValidation(false);
      jc.adjustActions();
   }
}
