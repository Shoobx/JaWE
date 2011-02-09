/**
* Together Workflow Editor
* Copyright (C) 2010 Together Teamsolutions Co., Ltd. 
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
