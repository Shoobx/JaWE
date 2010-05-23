package org.enhydra.jawe;

import org.enhydra.shark.xpdl.XMLElement;

public class SearchResult {

   protected XMLElement el;
   protected String disp;
   
   public SearchResult (XMLElement el) {
      this.el=el;
   }
   
   public XMLElement getElement () {
      return el;
   }
   
   public String toString () {
      if (disp==null) {
         disp=Utils.getLocString(Utils.getLocation(el), el);
      }
      return disp;
   }
   
}
