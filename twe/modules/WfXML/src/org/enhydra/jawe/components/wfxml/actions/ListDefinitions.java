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
