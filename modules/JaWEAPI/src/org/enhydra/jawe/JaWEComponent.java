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



import java.util.List;

import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.elements.Package;

/**
 * This interface should be implemented by any class that tends to be a JaWE's component.
 */
public interface JaWEComponent {

   /** Constant representing the name for the main area. */
   public static final String MAIN_COMPONENT = "MAIN";
   /** Constant representing the name for the special area. */
   public static final String SPECIAL_COMPONENT = "SPECIAL";
   /** Constant representing the name for the tree area. */
   public static final String TREE_COMPONENT = "TREE";
   /** Constant representing the name for the other area. */
   public static final String OTHER_COMPONENT = "OTHER";   
   /** Constant representing the name for the upper status area. */
   public static final String UPPER_STATUS_COMPONENT = "UPPER_STATUS";
   /** Constant representing the name for the lower status area. */
   public static final String LOWER_STATUS_COMPONENT = "LOWER_STATUS";   
   
   JaWEComponentSettings getSettings();   
   JaWEComponentView getView();

   
   String getName();
   String getType();
   public void setType(String type);

   boolean adjustXPDL(Package pkg);   
   
   // returns a list of ValidationError elements, or null if there are no errors
   List checkValidity (XMLElement el,boolean fullCheck);
   
   boolean canCreateElement(XMLCollection col);   
   boolean canInsertElement(XMLCollection col, XMLElement el);   
   public boolean canModifyElement(XMLElement el);   
   public boolean canRemoveElement(XMLCollection col, XMLElement el);   
   public boolean canDuplicateElement(XMLCollection col, XMLElement el);   
   public boolean canRepositionElement (XMLCollection col,XMLElement el); 


   public void setUpdateInProgress(boolean inProgress);
   public boolean isUpdateInProgress();
}