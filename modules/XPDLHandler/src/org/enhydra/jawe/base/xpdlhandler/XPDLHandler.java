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

package org.enhydra.jawe.base.xpdlhandler;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jxpdl.XMLInterface;
import org.enhydra.jxpdl.XMLInterfaceImpl;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.ExternalPackage;
import org.enhydra.jxpdl.elements.Package;

/**
 * Class which purpose is to provide methods which are
 * used by classes that represents program apstraction of
 * XML elements. These methods offers support for reading or
 * writting an XML document and for generating the tooltips for
 * for the classes that needs it.
 */
public class XPDLHandler extends XMLInterfaceImpl {

   protected XPDLHandlerSettings settings;
   
   private Map fileLocks=new HashMap();
   private Map rndAccessFiles=new HashMap();
   
   public XPDLHandler() {
      settings = new XPDLHandlerSettings();
      settings.init((JaWEComponent) null);
   }
   
   public XPDLHandler(XPDLHandlerSettings settings) throws Exception {
      this.settings = settings;
      this.settings.init((JaWEComponent) null);            
   }
   
   public Package getMainPackage () {
      return (Package)xmlFileToPackage.get(mainPackageReference);
   }

   public String getMainPackageId () {
      Package mainPackage=getMainPackage();
      if (mainPackage!=null) {
         return mainPackage.getId();
      }
      
      return null;      
   }

   public String getMainPackageFilename () {
      return mainPackageReference;
   }
   
   /**
    * This method has to be called when new package is created.
    * @param pkg
    */
   public void registerPackage (Package pkg) {
      ArrayList l=(ArrayList)idToPackages.get(pkg.getId());
      if (l==null) {
         l=new ArrayList();
      }
      l.add(pkg);
      idToPackages.put(pkg.getId(),l);
   }

   /**
    * This method has to be called from the newly created package after its
    * Id is entered.
    * @param pkg
    */
   public void changePackageId (Package pkg,String oldId,String newId) {
      ArrayList l=(ArrayList)idToPackages.remove(oldId);
//      if (l!=null) {
//
//      }
      l=(ArrayList)idToPackages.get(newId);
      if (l==null) {
         l=new ArrayList();
      }
      l.add(pkg);
      idToPackages.put(newId,l);

   }


   /**
    * This method is called when first saving new package, or when saving it with
    * a different name.
    * @param filename
    * @param pkg
    */
   public void registerPackageFilename (String filename,Package pkg) {      
      String uToRem=getAbsoluteFilePath(pkg);
      boolean isMainPackage=false;
      if (uToRem!=null) {
         if (uToRem.equals(mainPackageReference)) {
            isMainPackage=true;
         }
         xmlFileToPackage.remove(uToRem);
      }
      // release lock
      try {
         FileLock fl=(FileLock)fileLocks.remove(pkg);
         fl.release();
      } catch (Exception ex) {}
      // close the file stream
      try {
         RandomAccessFile raf=(RandomAccessFile)rndAccessFiles.remove(pkg);
         raf.close();
      } catch (Exception ex) {}

      String cp=XMLUtil.getCanonicalPath(filename, false);
      xmlFileToPackage.put(cp,pkg);
      if (isMainPackage || mainPackageReference==null) {
         mainPackageReference=cp;
      }

      File f=new File(cp);

      try {
         RandomAccessFile raf=new RandomAccessFile(f,"rw");
         rndAccessFiles.put(pkg,raf);
         if (settings.isFileLockingEnabled()) {
            FileLock fl=raf.getChannel().tryLock();
            if (fl!=null) {
               fileLocks.put(pkg,fl);
               // this should never happend
            } else {
               System.out.println("Can't lock");
            }
         }
         // this happens when using jdk1.4.0 under Linux, where
         // the locking is not supported
      } catch (IOException ex) {
      } catch (Exception ex) {}

      // register parent directory with the package
      try {
         packageToParentDirectory.put(pkg,f.getParentFile().getCanonicalPath());
      } catch (Exception ex) {
         packageToParentDirectory.put(pkg,f.getParentFile().getAbsolutePath());
      }
   }

   public RandomAccessFile getRaf (Package pkg){
      return (RandomAccessFile)rndAccessFiles.get(pkg);
   }

   // In this implementation, if handleExternalPackages is set to false, it is assumed
   // that JaWE is opening transient Package
   public Package openPackage (String pkgReference,
         boolean handleExternalPackages) {
      parsingErrorMessages.clear();
      if (mainPackageReference==null && handleExternalPackages) {
         mainPackageReference=pkgReference;
      }

      // this method opens the package. It also opens all of it's external packages
      // if handleExternalPackages is set to true
      Package pkg=openPackageRecursively(pkgReference,handleExternalPackages);
      if (pkg!=null) {
         pkg.setTransient(!handleExternalPackages);
         System.setProperty("user.dir",getParentDirectory(pkg));
      }

      return pkg;            
   }

   public void printDebug () {
      super.printDebug();
      System.out.println("fileLocks="+fileLocks);
      System.out.println("rndAccessFiles="+rndAccessFiles);
      System.out.println("main package reference="+mainPackageReference);
   }

   // Recursive implementation
   protected Package openPackageRecursively (String pkgReference,
         boolean handleExternalPackages) {

      Package pkg=null;
      File f=null;
      String oldP=pkgReference;

      String baseDirectory=null;
      pkgReference=XMLUtil.getCanonicalPath(pkgReference,"",false);
      if (pkgReference==null) {
         Set fem=new HashSet();
         fem.add("File does not exist");
         parsingErrorMessages.put(oldP,fem);
         return null;
      } 
      
      f=new File(pkgReference);
      try {
         baseDirectory=f.getParentFile().getCanonicalPath();
      } catch (Exception ex) {
         baseDirectory=f.getParentFile().getAbsolutePath();
      }      

      if (xmlFileToPackage.containsKey(pkgReference)) {
         return getPackageByFilename(pkgReference);
      }

      pkg=parseDocument(pkgReference,true);
      try {
         // trying to open main package file as 'rw'
         // and to lock it exclusivly
         if (oldP.equals(mainPackageReference)) {
            RandomAccessFile raf=null;
            try {
               raf=new RandomAccessFile(f,"rw");
            } catch (Exception ex) {
               raf=new RandomAccessFile(f,"r");
            }
            rndAccessFiles.put(pkg,raf);
            if (settings.isFileLockingEnabled()) {
               FileLock fl=raf.getChannel().tryLock();
               // this happens if the main package is not already locked
               if (fl!=null) {
                  fileLocks.put(pkg,fl);
                  // this happens if the file is already opened as 'rw' and locked
                  // exclusivly, or if it is opened as 'r' and locked as shared
               } else {
                  Set errorMessages = new HashSet();
                  errorMessages.add(ResourceManager.getLanguageDependentString("ErrorTheFileIsLocked"));
                  parsingErrorMessages.put(pkgReference,errorMessages);
                  return null;
               }
            }
            // trying to open external package file as 'rw'
            // and to lock it exclusivly
         } else {
            RandomAccessFile raf=null;
            try {
               raf=new RandomAccessFile(f,"rw");
            } catch (Exception ex) {
               raf=new RandomAccessFile(f,"r");
            }
            rndAccessFiles.put(pkg,raf);
            if (settings.isFileLockingEnabled()) {
               FileLock fl=raf.getChannel().tryLock();
               //FileLock fl=raf.getChannel().tryLock(0L,Long.MAX_VALUE,true);
               // this happens if the file isn't already opened as
               // 'rw' and locked exclusivly
               if (fl!=null) {
                  fileLocks.put(pkg,fl);
                  // this happens if the file is opened as 'rw' and locked exclusivly
               } else {
                  Set errorMessages = new HashSet();
                  errorMessages.add(ResourceManager.getLanguageDependentString("ErrorTheFileIsLocked"));
                  parsingErrorMessages.put(pkgReference,errorMessages);
                  return null;
               }
            }
         }
         // this exception happens if using jdk1.4.0 under Linux
      } catch (Exception ex) {
         //ex.printStackTrace();
      }

      if (pkg!=null) {
         String pkgId=pkg.getId();
         // check if package is already imported
         if (idToPackages.containsKey(pkgId)) {
            // check if this is the same package, or just the one with the same id
            if (xmlFileToPackage.containsKey(pkgReference)) {
               return getPackageById(pkgId);
            }
            
            throw new RuntimeException("Can't open two packages with the same Id");            
         }
         ArrayList l=(ArrayList)idToPackages.get(pkgId);
         if (l==null) {
            l=new ArrayList();
         }
         l.add(pkg);
         idToPackages.put(pkgId,l);
         xmlFileToPackage.put(pkgReference,pkg);
         try {
            packageToParentDirectory.put(pkg,f.getParentFile().getCanonicalPath());
         } catch (Exception ex) {
            packageToParentDirectory.put(pkg,f.getParentFile().getAbsolutePath());
         }
         // open all external packages if handleExternalPackages is set to true,
         // otherwise, it assumes that if there are external packages, the
         // href element is similar to their Ids
         Iterator eps=pkg.getExternalPackages().toElements().iterator();
         while (eps.hasNext()) {
            ExternalPackage ep = (ExternalPackage)eps.next();
            String pathToExtPackage=ep.getHref();
            String extPkgId=ep.getId();
            if (handleExternalPackages) {
               // setting working dir to be the one of the current package
               String ptep=XMLUtil.getCanonicalPath(pathToExtPackage,baseDirectory,false);
               if (ptep==null) {
                  Set fem=new HashSet();
                  fem.add("File does not exist");
                  parsingErrorMessages.put(pathToExtPackage,fem);                  
                  continue;
               }
               
               //System.setProperty("user.dir",packageToParentDirectory.get(pkg).toString());
               Package extPkg=openPackageRecursively(ptep,handleExternalPackages);
               extPkgId=extPkg.getId();               
            } else {
               if (extPkgId!=null) {
                  extPkgId=XMLUtil.getExternalPackageId(pathToExtPackage);
               }
            }
            pkg.addExternalPackageMapping(pathToExtPackage, extPkgId);
         }
      } else {
         System.err.println("Problems with opening file "+pkgReference);
      }
      return pkg;
   }

   public synchronized List closePackages (String pkgId) {
      Package main=getMainPackage();
      List l=super.closePackages(pkgId);
      if (l!=null) {
         // removing file to package mapping
         Iterator itr=l.iterator();
         while (itr.hasNext()) {
            Package toRemove=(Package)itr.next();
            if (toRemove==main) {
               mainPackageReference=null;
            }
            // close file
            RandomAccessFile raf=(RandomAccessFile)rndAccessFiles.remove(toRemove);
            try {
               raf.close();
            } catch (Exception ex) {}
            // unlock file
            FileLock fl=(FileLock)fileLocks.remove(toRemove);
            try {
               fl.release();
            } catch (Exception ex) {}
         }
      }
      return l;
   }

   public synchronized Package closePackageVersion (String pkgId,String pkgVer) {
      Package main=getMainPackage();
      ArrayList l=(ArrayList)idToPackages.get(pkgId);
      if (l.size()==1) {
         return (Package)this.closePackages(pkgId).get(0);
      }
         
      Package toRemove=super.closePackageVersion(pkgId, pkgVer);
//      System.out.println("TRID="+toRemove.getId());
      if (toRemove!=null) {
         if (toRemove==main) {
            mainPackageReference=null;
         }
         // close file
         RandomAccessFile raf=(RandomAccessFile)rndAccessFiles.remove(toRemove);
         try {
            raf.close();
         } catch (Exception ex) {}
         // unlock file
         FileLock fl=(FileLock)fileLocks.remove(toRemove);
         try {
            fl.release();
         } catch (Exception ex) {}
      }
      return toRemove;
   }

   public void closeAllPackages () {
      // close all files
      super.closeAllPackages();
      Iterator it=rndAccessFiles.values().iterator();
      while (it.hasNext()) {
         RandomAccessFile raf=(RandomAccessFile)it.next();
         try {
            raf.close();
         } catch (Exception ex) {}
      }
      rndAccessFiles.clear();
      unlockAllFiles();
      mainPackageReference=null;
   }

   public void unlockAllFiles () {
      // unlock all files
      Iterator it=fileLocks.values().iterator();
      while (it.hasNext()) {
         FileLock fl=(FileLock)it.next();
         try {
            fl.release();
         } catch (Exception ex) {}
      }
      fileLocks.clear();
   }

   public void lockAllFiles () throws Exception {
      if (!settings.isFileLockingEnabled()) return;
      Iterator it=rndAccessFiles.entrySet().iterator();
      Package main=(Package)xmlFileToPackage.get(mainPackageReference);
      while (it.hasNext()) {
         Map.Entry me=(Map.Entry)it.next();
         Package pkg=(Package)me.getKey();
         RandomAccessFile raf=(RandomAccessFile)me.getValue();

//         try {
            // lock main package exclusivly
            if (pkg.equals(main)) {
               FileLock fl=raf.getChannel().tryLock();
               // this happens if the main package is not already locked
               if (fl!=null) {
                  fileLocks.put(pkg,fl);
                  // this happens if the file is already opened as 'rw' and locked
                  // exclusivly, or if it is opened as 'r' and locked as shared
               } else {
                  continue;
               }
               // lock external package exclusivly
            } else {
               FileLock fl=raf.getChannel().tryLock(0L,Long.MAX_VALUE,true);
               // this happens if the file isn't already opened as
               // 'rw' and locked exclusivly
               if (fl!=null) {
                  fileLocks.put(pkg,fl);
                  // this happens if the file is opened as 'rw' and locked exclusivly
               } else {
                  continue;
               }
            }
            // this exception happens if using jdk1.4.0 under Linux
//         } catch (Exception ex) {
//            //ex.printStackTrace();
//         }
      }
   }


   public synchronized void synchronizePackages (XMLInterface xmlInterface) {
      Iterator it=xmlInterface.getAllPackages().iterator();
      while (it.hasNext()) {
         Package pkg=(Package)it.next();
         String pkgId=pkg.getId();
         ArrayList l=(ArrayList)idToPackages.get(pkgId);
         if (l==null) {
            l=new ArrayList();
         } else {
            continue;
         }
         l.add(pkg);
         idToPackages.put(pkgId,l);
         String fp=xmlInterface.getAbsoluteFilePath(pkg);
         if (fp!=null) {
            xmlFileToPackage.put(fp,pkg);
         }
         String pd=xmlInterface.getParentDirectory(pkg);
         if (pd!=null) {
            packageToParentDirectory.put(pkg,pd);
         }
         
         XPDLHandler xpdlh=(XPDLHandler)xmlInterface;
         RandomAccessFile raf = xpdlh.getRaf(pkg);
         if (raf!=null) {
            this.rndAccessFiles.put(pkg,raf);
         }
         FileLock fl=(FileLock)xpdlh.fileLocks.get(pkg);
         if (fl!=null) {
            if (xpdlh.getMainPackage()==pkg) {
               try {
                  fl.release();
               } catch (Exception ex) {System.err.println("Failed to release file lock for package "+pkg.getId());}
               fl=null;
               try {
                  fl=raf.getChannel().tryLock();
               } catch (Exception ex) {}
            }
            if (fl!=null) {
               this.fileLocks.put(pkg,fl);
            }
         }
      }
   }
   
}

