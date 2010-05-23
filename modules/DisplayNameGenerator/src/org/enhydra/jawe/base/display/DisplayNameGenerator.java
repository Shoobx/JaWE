package org.enhydra.jawe.base.display;

import org.enhydra.jawe.Settings;
import org.enhydra.shark.xpdl.XMLElement;

/**
 *  Used to generate strings for graphical representation of XML elements.
 *
 *  @author Sasa Bojanic
 */
public interface DisplayNameGenerator {
  
   String getDisplayName (XMLElement el);
   
   Settings getSettings ();
}
