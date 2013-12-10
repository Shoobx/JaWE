package org.enhydra.jawe;

import javax.swing.JComponent;

/**
 *  Interface for displaying JaWE components.
 *
 *  @author Sasa Bojanic
 */
public interface JaWEComponentView {

   void configure ();   
   void init ();
   
   JaWEComponent getJaWEComponent ();
   
   JComponent getDisplay ();      
}
