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

package org.enhydra.jawe.xyz;

import java.util.Set;

import org.enhydra.jawe.base.panel.StandardPanelGenerator;
import org.enhydra.jawe.base.panel.panels.XMLPanel;

import org.enhydra.jxpdl.elements.Activity;

/**
 * @author Sasa Bojanic
 */
public class XYZPanelGenerator extends StandardPanelGenerator {


   public XYZPanelGenerator() throws Exception {
      super();
   }

   protected XMLPanel getPanel(Activity el, int no, Set hidden) {
      if (no == 3) {
         return null;
      }
      return super.getPanel(el,no,hidden);
   }

}