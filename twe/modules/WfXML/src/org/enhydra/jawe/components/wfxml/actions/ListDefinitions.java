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

package org.enhydra.jawe.components.wfxml.actions;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.WaitScreen;
import org.enhydra.jawe.components.wfxml.WfXML;
import org.enhydra.jawe.components.wfxml.WfXMLPanel;

/**
 * Uploads XPDL to the engine.
 * 
 * @author Sasa Bojanic
 */
public class ListDefinitions extends ActionBase {

   public ListDefinitions(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      WfXML wfxml = (WfXML)jawecomponent;
      
      if (wfxml.hasConnection()) {
         setEnabled(true); 
      } else {
         setEnabled(false);
      }
   }
   
   public void actionPerformed(ActionEvent e) {      
      WfXML wfxml = (WfXML)jawecomponent;      
      WfXMLPanel panel=(WfXMLPanel)wfxml.getView();
      
      String conn=panel.getSelectedConnection();
      WaitScreen ws=new WaitScreen(JaWEManager.getInstance().getJaWEController().getJaWEFrame());
      boolean err=false;
      try {
         ws.show(null, "", wfxml.getSettings().getLanguageDependentString("ConnectingKey"));
         wfxml.listDefinitions(conn);
      } catch (Exception ex) {
         ex.printStackTrace();
         err=true;
      } finally {
         ws.setVisible(false);
      }
      if (err) {
         JaWEManager.getInstance().getJaWEController().message(wfxml.getSettings().getLanguageDependentString("ErrorWfXMLProblemsWhileGettingDefinitionListForRegistry")+" "+conn+" !",JOptionPane.ERROR_MESSAGE);
      }
      
   }   
}
