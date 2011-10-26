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

package org.enhydra.jawe;

import java.util.Observable;

import org.enhydra.jxpdl.XMLElementChangeInfo;
import org.enhydra.jxpdl.XMLElementChangeListener;
import org.enhydra.jxpdl.elements.Package;

/**
 * Observable class that represents XPDL model.
 * It listents to the changes of XPDL model and notifies its observers.
 *
 *  @author Sasa Bojanic
 */
public class XPDLListenerAndObservable extends Observable implements XMLElementChangeListener {
   
   protected boolean isModified;
   protected Package pkg;
   
   public XPDLListenerAndObservable (Package pkg, boolean receiveEvents) {
      if (pkg!=null && receiveEvents) {
         pkg.setNotifyMainListeners(true);
         pkg.addListener(this);
      }
      this.pkg=pkg;
   }
   
   public void xmlElementChanged(XMLElementChangeInfo info) {
//      Thread.dumpStack();
      isModified=true;
      setChanged();
      notifyObservers(info);
   }
   
   public boolean isModified () {
      return isModified;
   }
   
   public void setModified (boolean modified) {
      isModified=modified;
   }

   public Package getPackage () {
      return pkg;
   }

   public void unregisterFromXPDL () {
      pkg.removeListener(this);
   }
}

