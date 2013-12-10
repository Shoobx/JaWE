package org.enhydra.jawe.base.label;

import org.enhydra.jawe.Settings;
import org.enhydra.shark.xpdl.XMLElement;

/**
 *  Used to generate labels for XML elements.
 *
 *  @author Sasa Bojanic
 */
public interface LabelGenerator {

   String getLabel(XMLElement el);

   Settings getSettings ();
}
