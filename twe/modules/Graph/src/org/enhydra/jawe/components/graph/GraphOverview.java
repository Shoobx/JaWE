/**
 * Miroslav Popov, Nov 30, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.components.graph;

import org.enhydra.jawe.JaWEComponent;

/**
 * @author Miroslav Popov
 *
 */
public interface GraphOverview extends JaWEComponent {
   
   void init(GraphController controller);
   
   void displayGraph();
    
   Graph getGraph ();
   
}
