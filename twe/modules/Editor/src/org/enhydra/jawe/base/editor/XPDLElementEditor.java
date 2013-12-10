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

package org.enhydra.jawe.base.editor;

import java.awt.Window;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jxpdl.XMLElement;

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
