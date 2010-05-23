package org.enhydra.jawe.base.panel;

import java.util.EventObject;

import org.enhydra.jawe.Settings;
import org.enhydra.jawe.base.display.DisplayNameGenerator;
import org.enhydra.jawe.base.label.LabelGenerator;
import org.enhydra.jawe.base.panel.panels.XMLPanel;
import org.enhydra.jawe.base.tooltip.TooltipGenerator;


/**
 * Listener of the panel events.
 *
 *  @author Sasa Bojanic
 */
public interface PanelContainer {

   void panelChanged (XMLPanel panel,EventObject ev);
   
   String getLanguageDependentString (String nm);
   
   Settings getSettings ();
   
   PanelGenerator getPanelGenerator ();
   
   LabelGenerator getLabelGenerator();
   
   DisplayNameGenerator getDisplayNameGenerator();
   
   PanelValidator getPanelValidator ();
   
   TooltipGenerator getTooltipGenerator ();
   
}
   
