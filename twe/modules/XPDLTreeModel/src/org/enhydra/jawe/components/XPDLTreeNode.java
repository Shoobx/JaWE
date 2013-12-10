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

package org.enhydra.jawe.components;

import javax.swing.tree.DefaultMutableTreeNode;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.shark.xpdl.XMLAttribute;
import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLSimpleElement;

/**
 *  Used to handle events in TreeView tree.
 * 
 *  @author Zoran Milakovic
 */
public class XPDLTreeNode extends DefaultMutableTreeNode  {

   public XPDLTreeNode () {
      super("XPDL");
   }

   public XPDLTreeNode (XMLElement el) {
      super(el);
   }

   public XMLElement getXPDLElement () {
      if (userObject instanceof XMLElement) {
         return (XMLElement)userObject;
      }
       
      return null;      
   }

   public String toString () {
      if (userObject instanceof XMLElement) { 
         XMLElement el=(XMLElement)userObject;
         String label=el.toName();
         String detail=JaWEManager.getInstance().getDisplayNameGenerator().getDisplayName(el);
         String disp=label;
         if (detail!=null && detail.equals("")) {
            if (el instanceof XMLAttribute || el instanceof XMLSimpleElement) {
               disp+=":";
            } else {
               
            }
         } else {
            if (el instanceof XMLAttribute || el instanceof XMLSimpleElement) {
               disp+=": "+detail;
            } else if (el instanceof XMLCollection) {
                  if (getChildCount() > 0)                  
                     disp += " - " + getChildCount();
            } else {
               disp+=" - "+detail;
            }
         }
         return disp;
      } 
      
      return userObject.toString();      
   }      
}
