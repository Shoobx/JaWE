package org.enhydra.jawe.base.tooltip;

import org.enhydra.jawe.Settings;
import org.enhydra.shark.xpdl.XMLElement;

/**
 *  Used to generate tooltips for XML elements.
 *
 *  @author Sasa Bojanic
 */
public interface TooltipGenerator {

   String getTooltip (XMLElement el);
   
   Settings getSettings ();
   
}
