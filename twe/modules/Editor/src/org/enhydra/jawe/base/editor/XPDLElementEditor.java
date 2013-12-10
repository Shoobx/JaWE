package org.enhydra.jawe.base.editor;

import java.awt.Window;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.shark.xpdl.XMLElement;

public interface XPDLElementEditor extends JaWEComponent {

   void configure();
   
   void setTitle (String title);
   
   XPDLElementEditor getParentEditor ();
   
   XMLElement getEditingElement ();
   
   void editXPDLElement ();
   
   void editXPDLElement (XMLElement el);
   
   boolean canApplyChanges ();

//   void applyChanges ();

   int getStatus();
   
   Window getWindow ();

   Window getParentWindow ();   
   
   public boolean isVisible();

   public void setModified (boolean modif);
   
   public void close ();
}
