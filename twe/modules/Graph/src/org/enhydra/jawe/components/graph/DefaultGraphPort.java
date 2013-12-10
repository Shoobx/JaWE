package org.enhydra.jawe.components.graph;


/**
 * Used to define Port object in graph.
 *
 * @author Sasa Bojanic   
 */
public class DefaultGraphPort extends GraphPortInterface {


   protected String type;
   /**
    * Creates activity with given userObject. Also creates default port
    * for holding activity transitions.
    */
   public DefaultGraphPort(String name,String type) {
      super(name);
      this.type=type;
   }

   public GraphActivityInterface getActivity () {
      return (GraphActivityInterface)getParent();
   }
   
   public String getType () {
      return type;
   }
   
}
