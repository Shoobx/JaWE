package org.enhydra.jawe.components.wfxml.actions;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.JOptionPane;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.WaitScreen;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.components.wfxml.DefInfo;
import org.enhydra.jawe.components.wfxml.WfXML;
import org.enhydra.jawe.components.wfxml.WfXMLConnector;
import org.enhydra.jawe.components.wfxml.WfXMLPanel;
import org.enhydra.shark.xpdl.elements.Package;
import org.w3c.dom.Node;

/**
 * Downloads XPDL from the engine.
 * 
 * @author Sasa Bojanic
 */
public class Download extends ActionBase {

   public Download(JaWEComponent jawecomponent) {
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
      
      DefInfo el = panel.getSelectedDefInfo();
      if (el!=null && el.getDefinitionKey()!=null) {
         JaWEController jc=JaWEManager.getInstance().getJaWEController();
         if (jc.tryToClosePackage(jc.getMainPackageId(), false)) {
         
            WaitScreen ws=new WaitScreen(jc.getJaWEFrame());
            boolean err=false;
            String msg=null;
            try {
               ws.show(null, "", wfxml.getSettings().getLanguageDependentString("DownloadingKey"));
               String defKey=el.getDefinitionKey();
               defKey=defKey.replaceAll("&", "&amp;");
               URL u=new URL(defKey);
               Node n=WfXMLConnector.wfxmlGetDefinition(u);
               byte[] pkgCnt=WfXMLConnector.node2Bytes(n);

               Package pkg=jc.openPackageFromStream(pkgCnt);
               msg=pkg.getId()+" - "+wfxml.getSettings().getLanguageDependentString("InformationWfXMLXPDLIsSucessfullyDownloaded");
            } catch (Exception ex) {
               ex.printStackTrace();
               err=true;
               msg=wfxml.getSettings().getLanguageDependentString("ErrorWfXMLProblemsWhileDownloadingXPDL")+" "+el.getDefinitionKey()+" !";
            } finally {
               ws.setVisible(false);
            }
            jc.message(msg,err ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
         }
      } else {
//         cont.complainLoudly("WarningEmptySelectionToEditOrDelete");
      }
      
   }   
}
