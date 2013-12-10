package org.enhydra.jawe.components.wfxml.actions;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.JOptionPane;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.WaitScreen;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.components.wfxml.WfXML;
import org.enhydra.jawe.components.wfxml.WfXMLConnector;
import org.enhydra.jawe.components.wfxml.WfXMLPanel;

import org.enhydra.shark.xpdl.elements.Package;

/**
 * Uploads XPDL to the engine.
 * 
 * @author Sasa Bojanic
 */
public class Upload extends ActionBase {

   public Upload(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      WfXML wfxml = (WfXML)jawecomponent;
      
      Package mainPkg=JaWEManager.getInstance().getJaWEController().getMainPackage();
      Package workingPkg=JaWEManager.getInstance().getJaWEController().getSelectionManager().getWorkingPKG();
      if (wfxml.hasConnection() && mainPkg!=null && mainPkg==workingPkg) {
         setEnabled(true); 
      } else {
         setEnabled(false);
      }
   }
   
   public void actionPerformed(ActionEvent e) {      
      WfXML wfxml = (WfXML)jawecomponent;      
      WfXMLPanel panel=(WfXMLPanel)wfxml.getView();
      JaWEController jc=JaWEManager.getInstance().getJaWEController();
      String pkgId=jc.getMainPackageId();
      String conn=panel.getSelectedConnection();
      if (conn!=null && pkgId!=null && !pkgId.trim().equals("")) {
         WaitScreen ws=new WaitScreen(jc.getJaWEFrame());
         boolean err=false;
         String msg=null;
         try {
            ws.show(null, "", wfxml.getSettings().getLanguageDependentString("UploadingKey"));
            String pkgContent=WfXMLConnector.xpdlToString(jc.getMainPackage());
            WfXMLConnector.wfxmlNewDefinition2(new URL(conn),pkgContent);
            try {
               wfxml.listDefinitions(conn);
               msg=pkgId+" - "+wfxml.getSettings().getLanguageDependentString("InformationWfXMLXPDLIsSucessfullyUploaded");
            } catch (Exception ex) {
               err=true;
               msg=wfxml.getSettings().getLanguageDependentString("ErrorWfXMLProblemsWhileGettingDefinitionListForRegistry")+" "+conn+" !";
            }
         } catch (Exception ex) {
            ex.printStackTrace();
            err=true;
            msg=wfxml.getSettings().getLanguageDependentString("ErrorWfXMLProblemsWhileUploadingXPDL")+" "+conn+" !";
         } finally {
            ws.setVisible(false);
         }
         jc.message(msg,err ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
         
      } else {
//         cont.complainLoudly("WarningEmptySelectionToEditOrDelete");
      }
      
   }   
}
