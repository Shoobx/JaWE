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

package org.enhydra.jawe.components.packagenavigator;

import java.util.Iterator;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.components.XPDLTreeModel;
import org.enhydra.jawe.components.XPDLTreeNode;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.elements.Activity;
import org.enhydra.shark.xpdl.elements.ActivitySet;
import org.enhydra.shark.xpdl.elements.Package;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;

/**
 *  TreeView hierarchy tree model.
 * 
 *  @author Zoran Milakovic
 *  @author Sasa Bojanic
 */
public class PackageNavigatorTreeModel extends XPDLTreeModel {

   public PackageNavigatorTreeModel (JaWEComponent comp) {
      super(comp);
   }

   public XPDLTreeNode insertNode(XMLElement el) {
      if (el instanceof Package || el instanceof WorkflowProcess || el instanceof ActivitySet || el instanceof Activity) {

         if (el instanceof Package) {
            return super.insertNode(el);
         }

         XPDLTreeNode parent = null;
         if (el instanceof WorkflowProcess) {
            parent = findNode(XMLUtil.getPackage(el));
         } else if (el instanceof ActivitySet) {
            parent = findNode(XMLUtil.getWorkflowProcess(el));
         } else if (el instanceof Activity) {
            XMLElement wpOrAs = XMLUtil.getActivitySet(el);
            if (wpOrAs == null) {
               wpOrAs = XMLUtil.getWorkflowProcess(el);
            }
            parent = findNode(wpOrAs);
         }
         if (parent != null) {
            XPDLTreeNode n=findNode(parent, el);
            if (n==null) {
               n=insertNode(parent,el);
            } else {
               n=null;
            }
            return n;
         }
      }

      return null;
   }
   
   protected XPDLTreeNode insertNode (XPDLTreeNode parent,XMLElement el) {
      if (el instanceof Package || 
            el instanceof WorkflowProcess ||
            el instanceof ActivitySet ||
            el instanceof Activity) {
         XPDLTreeNode n=createNode(el);
         insertNodeInto(n, parent, parent.getChildCount());

         if (el instanceof Package || 
               el instanceof WorkflowProcess ||
               el instanceof ActivitySet) {
         
            if (el instanceof Package) {
               Iterator it=((Package)el).getWorkflowProcesses().toElements().iterator();
               while (it.hasNext()) {
                  insertNode(n,(WorkflowProcess)it.next());
               }               
            } else if (el instanceof WorkflowProcess) {
               Iterator it=((WorkflowProcess)el).getActivitySets().toElements().iterator();
               while (it.hasNext()) {
                  insertNode(n,(ActivitySet)it.next());
               }        
               it=((WorkflowProcess)el).getActivities().toElements().iterator();
               while (it.hasNext()) {
                  insertNode(n,(Activity)it.next());
               }                       
            } else if (el instanceof ActivitySet) {
               Iterator it=((ActivitySet)el).getActivities().toElements().iterator();
               while (it.hasNext()) {
                  insertNode(n,(Activity)it.next());
               }                       
            }
         }        
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
      if (uo instanceof XMLElement) {
         if (uo instanceof Package) {
            return ((Package)uo).getWorkflowProcesses().size()==0;
         } else if (uo instanceof WorkflowProcess) {
            WorkflowProcess wp=(WorkflowProcess)uo;
            return wp.getActivitySets().size()==0 && wp.getActivities().size()==0;
         } else if (uo instanceof ActivitySet) {
            return ((ActivitySet)uo).getActivities().size()==0;
         } else if (uo instanceof Activity)
            return true;
      }
      return false;
   }   
   
}
