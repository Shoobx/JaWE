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

package org.enhydra.jawe.components.extpkgrelations;

import java.util.ArrayList;
import java.util.List;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.components.XPDLTreeModel;
import org.enhydra.jawe.components.XPDLTreeNode;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.Package;

/**
 *  Search Navigator hierarchy tree model.
 * 
 *  @author Sasa Bojanic
 */
public class ExtPkgRelationsTreeModel extends XPDLTreeModel {

   public ExtPkgRelationsTreeModel (JaWEComponent comp) {
      super(comp);
   }

   public XPDLTreeNode insertNode(XMLElement el) {
      
      XPDLTreeNode n=null;
      if (el instanceof Package) {
//         System.err.println("Inserting el "+elToIns);
         n=findNode(el);
//         System.err.println("... already exists - "+(n!=null));
         if (n==null) {
            if (getRootNode().isLeaf()) {
               n=insertNode(getRootNode(),el);
            }
         }
      }
      return n;
   }
   
   protected XPDLTreeNode findNodeForPath (XPDLTreeNode parent,XMLElement el) {
      if (parent==getRootNode()) {
         return null;
      }
      XPDLTreeNode n=(XPDLTreeNode)parent.getParent();
      if (n.getUserObject()==el) {
         return n;
      } 
      
      return findNodeForPath(n, el);      
   }
   
   protected XPDLTreeNode insertNode (XPDLTreeNode parent,XMLElement el) {
      List toInsert=new ArrayList();
      XPDLTreeNode nfp=findNodeForPath(parent, el);
      if (nfp==null) {
         Package toIns=(Package)el;
         toInsert=XMLUtil.getImmediateExternalPackages(JaWEManager.getInstance().getXPDLHandler(), toIns);
      }
      XPDLTreeNode n=createNode(el);
      insertNodeInto(n, parent, parent.getChildCount());
      for (int i=0; i<toInsert.size(); i++) {
         Package pkg=(Package)toInsert.get(i);
         insertNode(n,pkg);
      }
      return n;
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
}
