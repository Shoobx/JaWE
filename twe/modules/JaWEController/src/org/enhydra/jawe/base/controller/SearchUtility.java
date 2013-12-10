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

package org.enhydra.jawe.base.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLComplexChoice;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLSimpleElement;
import org.enhydra.jxpdl.elements.Package;

/**
 * Utilities for perfoming simple search through XPDL.
 * 
 * @author Sasa Bojanic
 */
public class SearchUtility {

   public static List searchElement(Package pkg, SearchEntity se) {
      List result=new ArrayList();
      searchElement(pkg, se, result);
      return result;
   }

   public static void searchElement(XMLCollection cel, SearchEntity se, List result) {
      for (Iterator it = cel.toElements().iterator(); it.hasNext();) {
         XMLElement el = (XMLElement) it.next();
         if (el instanceof XMLSimpleElement) {
            if (se.getSearchForElement().equals("*")) {
               searchElement((XMLSimpleElement) el, se, result);
            }
         } else {
            searchElement((XMLComplexElement) el, se, result);
         }
      }
   }

   public static void searchElement(XMLComplexElement cel, SearchEntity se, List result) {
      boolean stopSearch=se.getSearchForElement().equals(cel.toName());
      boolean searchMe=se.getSearchForElement().equals("*") || stopSearch;
      if (searchMe && (se.getSearchByProperty().equals("Value") || se.getSearchByProperty().equals("")) && cel.toValue() != null) {
         if (compareStrings(se.getContainingText(), cel.toValue(), se.isCaseSensitive())) {
            addToResult(result, cel);
         }
      }
      for (Iterator it = cel.toElements().iterator(); it.hasNext();) {
         XMLElement el = (XMLElement) it.next();
         if (el instanceof XMLComplexElement && !stopSearch) {
            searchElement((XMLComplexElement) el, se, result);
         } else if (el instanceof XMLCollection && !stopSearch) {
            searchElement((XMLCollection) el, se, result);
         } else if (el instanceof XMLComplexChoice && !stopSearch) {
            searchElement((XMLComplexChoice) el, se, result);
         } else if (el instanceof XMLSimpleElement && searchMe) {
            searchElement((XMLSimpleElement) el, se, result);
         } else if (el instanceof XMLAttribute && searchMe) { 
            searchElement((XMLAttribute) el, se, result);
         }
      }
   }

   public static void searchElement(XMLComplexChoice el, SearchEntity se, List result) {
      XMLElement choosen = el.getChoosen();
      if (choosen != null) {
         if (choosen instanceof XMLComplexElement) {
            searchElement((XMLComplexElement) choosen, se, result);
         } else if (choosen instanceof XMLCollection) {
            searchElement(((XMLCollection) choosen), se, result);
         }
      }
   }

   public static void searchElement(XMLSimpleElement el, SearchEntity se, List result) {      
      if (se.getSearchByProperty().equals("*") || el.toName().equals(se.getSearchByProperty())) {
         if (compareStrings(se.getContainingText(), el.toValue(), se.isCaseSensitive())) {
            addToResult(result, el);
         }
      }
   }

   public static void searchElement(XMLAttribute el, SearchEntity se, List result) {
      if (se.getSearchByAttribute().equals("*") || el.toName().equals(se.getSearchByAttribute())) {
         if (compareStrings(se.getContainingText(), el.toValue(), se.isCaseSensitive())) {
            addToResult(result, el);
         }
      }
   }

   public static boolean compareStrings(String filter, String str, boolean caseSensitive) {
      if (caseSensitive) {
         return str.indexOf(filter) != -1;
      }
      return str.toLowerCase().indexOf(filter.toLowerCase()) != -1;
   }

   public static void addToResult(List result, XMLElement el) {
      if (!result.contains(el)) {
         result.add(el);
      } else {
         boolean contains=false;
         Iterator it=result.iterator();
         while (it.hasNext()) {
            XMLElement e=(XMLElement)it.next();
            if (e.equals(el) && e.hashCode()==el.hashCode()) {
               contains=true;
               break;
            }
         }
         if (!contains) {
            result.add(el);
         }
      }
   }
}
