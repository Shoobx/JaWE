/**
 * Together Workflow Editor
 * Copyright (C) 2011 Together Teamsolutions Co., Ltd. 
 * 
 * This program is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 *
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * GNU General Public License for more details. 
 *
 * You should have received a copy of the GNU General Public License 
 * along with this program. If not, see http://www.gnu.org/licenses
 */

package org.enhydra.jawe;

import org.enhydra.jxpdl.XMLElement;

/**
 * Interface that provides a way to handle "browsing" history for {@link XMLElement}.
 * 
 * @author Sasa Bojanic
 */
public interface HistoryManager {

   /**
    * Initializes the HistoryManager.
    * 
    * @param maxHistory The maximal number of "history entries".
    */
   void init(int maxHistory);

   /**
    * Adds another entry into the history.
    * 
    * @param current The currently selected {@link XMLElement}.
    * @param newElement New {@link XMLElement} that will be selected.
    */
   void addToHistory(XMLElement current, XMLElement newElement);

   /**
    * Removes given entry from the history.
    * 
    * @param el The {@link XMLElement} instance to remove.
    */
   void removeFromHistory(XMLElement el);

   /**
    * Retrieves the next element from the "re-do" history (used for "re-do" action), and
    * puts currently selected into the history.
    * 
    * @param current The currently selected {@link XMLElement}.
    * @return The next {@link XMLElement} in the history.
    */
   XMLElement getNext(XMLElement current);

   /**
    * Retrieves the previous element from the "undo" history (used for "undo" action), and
    * puts currently selected into the history.
    * 
    * @param current The currently selected {@link XMLElement}.
    * @return The next {@link XMLElement} into the history.
    */
   XMLElement getPrevious(XMLElement current);

   /**
    * Checks if there are elements in "undo" history.
    * 
    * @return true if there is an element in "undo" history list.
    */
   boolean canGoBack();

   /**
    * Checks if there are elements in "re-do" history.
    * 
    * @return true if there is an element in "re-do" history list.
    */
   boolean canGoForward();

   /** Clears all the entries from both "undo" and "re-do" history. */
   void cleanHistory();

}
