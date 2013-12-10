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

package org.enhydra.jawe.components.transpkgpool;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.components.XPDLTreeModel;
import org.enhydra.jawe.components.XPDLTreeNode;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.elements.Package;

/**
 *  Transient Package Pool hierarchy tree model.
 *
 *  @author Sasa Bojanic
 */
public class TransientPkgPoolTreeModel extends XPDLTreeModel {

   public TransientPkgPoolTreeModel (JaWEComponent comp) {
      super(comp);
   }

   public XPDLTreeNode insertNode(XMLElement el) {
      if (el instanceof Package) {
         return super.insertNode(el);
      }

      return null;
   }
   
   protected XPDLTreeNode insertNode (XPDLTreeNode parent,XMLElement el) {
      if (el instanceof Package) {
         XPDLTreeNode n=createNode(el);
         insertNodeInto(n, parent, parent.getChildCount());

         return n;
      }      
      return null;
   }
    
   protected XPDLTreeNode createNode (XMLElement el) {
      return new XPDLTreeNode(el) {
         public String toString () {
            if (userObject instanceof XMLElement) { 
               return  JaWEManager.getInstance().getDisplayNameGenerator().getDisplayName(((XMLElement)userObject));
            }
               
            return userObject.toString();                        
         }
      };
   }
   
   public boolean isLeaf(Object node) {
      Object uo=((XPDLTreeNode)node).getUserObject();
      if (uo instanceof Package) {
         return true;
      }
      return false;
   }   
   
}
