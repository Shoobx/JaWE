package org.enhydra.jawe.base.panel;

import org.enhydra.jawe.Settings;
import org.enhydra.jawe.base.panel.panels.XMLPanel;
import org.enhydra.shark.xpdl.XMLElement;


/**
 * Used to generate panels for XML elements.
 *
 *  @author Sasa Bojanic
 */
public interface PanelGenerator {

   XMLPanel getPanel (XMLElement el);

   Settings getSettings ();
   
   void setPanelContainer (PanelContainer pc);
}
