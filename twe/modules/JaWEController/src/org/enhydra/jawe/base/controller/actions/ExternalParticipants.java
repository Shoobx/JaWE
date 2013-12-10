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

public class ExternalParticipants extends ActionBase {
   public ExternalParticipants (JaWEComponent jawecomponent) {
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
            toShow.addAll(pkg.getParticipants().toElements());
         }
      }
   
      List columnsToShow=new ArrayList();
      columnsToShow.add("Id");
      columnsToShow.add("Name");
      columnsToShow.add("ParticipantType");
      columnsToShow.add("Description");
      
      String title=
         JaWEManager.getInstance().getLabelGenerator().getLabel(p.getExternalPackages())+
         " - " +         
         JaWEManager.getInstance().getLabelGenerator().getLabel(p.getParticipants());
      JaWEManager.getInstance().getTableEditor().showTable(title, p.getParticipants(), toShow, columnsToShow);
   }
}
