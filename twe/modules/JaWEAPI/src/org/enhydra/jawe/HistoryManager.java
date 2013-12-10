package org.enhydra.jawe;

import org.enhydra.shark.xpdl.XMLElement;

/**
 * Used to handle history.
 * 
 * @author Sasa Bojanic
 */
public interface HistoryManager {

   void init (int maxHistory);
   
   void addToHistory(XMLElement current,XMLElement el);
   void removeFromHistory(XMLElement el);

   XMLElement getNext(XMLElement current);
   XMLElement getPrevious (XMLElement current);

   boolean canGoBack();
   boolean canGoForward();
   
   void cleanHistory ();
   
}
