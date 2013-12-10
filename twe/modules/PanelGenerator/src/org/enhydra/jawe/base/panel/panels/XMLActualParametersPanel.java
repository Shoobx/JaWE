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

package org.enhydra.jawe.base.panel.panels;

import java.awt.Dimension;

import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jawe.base.panel.PanelGenerator;
import org.enhydra.shark.xpdl.elements.ActualParameters;
import org.enhydra.shark.xpdl.elements.FormalParameters;


/**
 * Creates a table panel.
 * @author Sasa Bojanic
 */
public class XMLActualParametersPanel extends XMLBasicPanel {

   protected XMLPanel fpPanel;
   protected XMLPanel apPanel;
   public XMLActualParametersPanel (
         PanelContainer pc,
         ActualParameters myOwner,
         FormalParameters fps) {

      super(pc,myOwner,"",false,false,false);

      setFormalParameters(fps);
      PanelGenerator pg = pc.getPanelGenerator();
      apPanel=pg.getPanel(myOwner);


      add(fpPanel);
      add(apPanel);

      setPreferredSize(new Dimension(700,200));

   }

   public void setFormalParameters (FormalParameters fps) {
      PanelGenerator pg=pc.getPanelGenerator();
      if (fpPanel!=null) {
         remove(0);
      }
      if (fps!=null) {         
         fpPanel=pg.getPanel(fps);
      } else {
         fpPanel=new XMLBasicPanel();
      }
      add(fpPanel,0);
      validate();
   }

   public void cleanup () {
      apPanel.cleanup();
      if (fpPanel!=null) {
         fpPanel.cleanup();         
      }
   }
   
}

