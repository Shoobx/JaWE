package org.enhydra.jawe.base.panel;


import org.enhydra.jawe.Settings;
import org.enhydra.jawe.base.panel.panels.XMLPanel;
import org.enhydra.shark.xpdl.XMLElement;


/**
 *  Class used to validate panels for XML elements.
 * 
 *  @author Sasa Bojanic
 */
public interface PanelValidator {

   boolean validatePanel (XMLElement el,XMLPanel panel);
   
   Settings getSettings ();
   
}
