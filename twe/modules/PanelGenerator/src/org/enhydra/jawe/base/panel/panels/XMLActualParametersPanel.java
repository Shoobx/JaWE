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

