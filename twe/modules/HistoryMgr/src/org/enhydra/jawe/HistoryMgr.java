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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;

/**
 * Implementation that provides a way to handle "browsing" history for {@link XMLElement}
 * that can be used e.g. when browsing through the graph and property panels.
 * 
 * @author Sasa Bojanic
 */
public class HistoryMgr implements HistoryManager {

   /** Stack instance used to handle "undo" history. */
   protected Stack backHistory = new Stack();

   /** Stack instance used to handle "re-do" history. */
   protected Stack forwardHistory = new Stack();

   /** The maximum number of undo/redo history entries. -1 means unlimited. */
   protected int maxHistory = -1;

   /** 
    * Default constructor.
    */
   public HistoryMgr() {
   }

   public void init(int pMaxHistory) {
      if (pMaxHistory >= 0) {
         this.maxHistory = pMaxHistory;
      } else {
         this.maxHistory = Integer.MAX_VALUE;
      }
   }

   public void addToHistory(XMLElement current, XMLElement el) {
      if (maxHistory <= 0)
         return;
      if (el == null && current == null)
         return;
      if (el != current && current != null) {
         if (!backHistory.isEmpty() && backHistory.lastElement() == current) {
            return;
         }
         if (backHistory.size() >= maxHistory) {
            backHistory.remove(0);
         }

         backHistory.push(current);
         forwardHistory.clear();
      }
   }

   public void removeFromHistory(XMLElement el) {
      if (el == null) {
         return;
      }
      List childrenToRemove = new ArrayList();
      for (int j = 0; j < backHistory.size(); j++) {
         XMLElement elInHistory = (XMLElement) backHistory.get(j);
         if (elInHistory == el || XMLUtil.isParentsChild(el, elInHistory)) {
            childrenToRemove.add(elInHistory);
         }
      }
      if (childrenToRemove.size() > 0) {
         forwardHistory.clear();
         backHistory.removeAll(childrenToRemove);
         // remove same elements next to each other
         Iterator it = backHistory.iterator();
         XMLElement cur = null;
         while (it.hasNext()) {
            XMLElement ne = (XMLElement) it.next();
            if (cur == null) {
               cur = ne;
            } else {
               if (ne == cur) {
                  it.remove();
               } else {
                  cur = ne;
               }
            }
         }
      } else {
         boolean clear = false;
         for (int j = 0; j < forwardHistory.size(); j++) {
            XMLElement elInHistory = (XMLElement) forwardHistory.get(j);
            if (elInHistory == el || XMLUtil.isParentsChild(el, elInHistory)) {
               clear = true;
               break;
            }
         }
         if (clear) {
            forwardHistory.clear();
         }
      }
   }

   public XMLElement getNext(XMLElement current) {
      if (maxHistory <= 0)
         return null;

      XMLElement el = null;
      if (!forwardHistory.empty()) {
         el = (XMLElement) forwardHistory.pop();
         if (current != null) {
            if (backHistory.size() >= maxHistory) {
               backHistory.remove(0);
            }
            backHistory.push(current);
         }
      }
      return el;
   }

   public XMLElement getPrevious(XMLElement current) {
      if (maxHistory <= 0)
         return null;

      XMLElement el = null;
      if (!backHistory.empty()) {
         el = (XMLElement) backHistory.pop();
         if (current != null) {
            if (forwardHistory.size() >= maxHistory) {
               forwardHistory.remove(0);
            }
            forwardHistory.push(current);
         }
      }
      return el;
   }

   public boolean canGoBack() {
      return !backHistory.isEmpty();
   }

   public boolean canGoForward() {
      return !forwardHistory.isEmpty();
   }

   public void cleanHistory() {
      backHistory.clear();
      forwardHistory.clear();
   }

}
