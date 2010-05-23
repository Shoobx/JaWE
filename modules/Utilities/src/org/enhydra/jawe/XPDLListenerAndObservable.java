package org.enhydra.jawe;

import java.util.Observable;

import org.enhydra.shark.xpdl.XMLElementChangeInfo;
import org.enhydra.shark.xpdl.XMLElementChangeListener;
import org.enhydra.shark.xpdl.elements.Package;

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

