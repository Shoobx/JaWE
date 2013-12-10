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

package org.enhydra.jawe.base.controller.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.xpdlhandler.XPDLHandler;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.elements.Package;

public class ExternalApplications extends ActionBase {
   public ExternalApplications (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {  
      if (getPackage() != null && getPackage().getExternalPackageIds().size() != 0)
         setEnabled(true);
      else 
         setEnabled(false);  
   }
   
   public void actionPerformed(ActionEvent e) {
      Package p=getPackage();
      
      List toShow=new ArrayList();      
      XPDLHandler xpdlh=JaWEManager.getInstance().getXPDLHandler();
      Iterator it=XMLUtil.getAllExternalPackageIds(xpdlh, p, new HashSet()).iterator();
      while (it.hasNext()) {
         Package pkg=xpdlh.getPackageById((String)it.next());
         if (pkg!=p) {
            toShow.addAll(pkg.getApplications().toElements());
         }
      }
   
      List columnsToShow=new ArrayList();
      columnsToShow.add("Id");
      columnsToShow.add("Name");
      columnsToShow.add("Description");
      
      String title=
         JaWEManager.getInstance().getLabelGenerator().getLabel(p.getExternalPackages())+
         " - " +
         JaWEManager.getInstance().getLabelGenerator().getLabel(p.getApplications());
      JaWEManager.getInstance().getTableEditor().showTable(title, p.getApplications(), toShow, columnsToShow);
   }
}
