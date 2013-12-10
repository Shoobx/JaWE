package org.enhydra.jawe.components.transpkgpool;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.components.XPDLTreeModel;
import org.enhydra.jawe.components.XPDLTreeNode;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.elements.Package;

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
