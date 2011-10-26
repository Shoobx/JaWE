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

/**
 * Miroslav Popov, Aug 2, 2005
 */
package org.enhydra.jawe.base.logger.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.base.logger.LogPanel;
import org.enhydra.jawe.base.logger.LoggingManagerComponent;

/**
 * @author Miroslav Popov
 *
 */
public class CleanPage extends ActionBase {

   public CleanPage(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {  
      LoggingManagerComponent lg = (LoggingManagerComponent)jawecomponent;
      
      if ( ((LogPanel)lg.getView()).getTextSize() != 0)
         setEnabled(true); 
      else
         setEnabled(false);      
   }
   
   public void actionPerformed(ActionEvent e) {      
      LoggingManagerComponent con = (LoggingManagerComponent)jawecomponent;      
      
      ((LogPanel)(con.getView())).clearText();
      
      setEnabled(false);
   }
}
