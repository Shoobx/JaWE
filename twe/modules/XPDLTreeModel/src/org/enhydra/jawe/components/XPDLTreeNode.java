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
