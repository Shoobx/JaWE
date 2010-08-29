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

package org.enhydra.jawe;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.enhydra.jawe.base.controller.JaWEController;

/**
 * Class which purpose is to provide static methods which are
 * used by classes that represents program apstraction of
 * XML elements. These methods offers support for reading or
 * writting an XML document and for generating the tooltips for
 * for the classes that needs it.
 */
public class JaWEXMLUtil {

   // filter for XML files
   private static JaWEXMLUtil.XMLFilter allFilter = new JaWEXMLUtil.XMLFilter(null);
   private static JaWEXMLUtil.XMLFilter xmlFilter = new JaWEXMLUtil.XMLFilter("xml");
   private static JaWEXMLUtil.XMLFilter xpdlFilter = new JaWEXMLUtil.XMLFilter("xpdl");
   private static JaWEXMLUtil.XMLFilter jpgFilter = new JaWEXMLUtil.XMLFilter("jpg");
   private static JaWEXMLUtil.XMLFilter svgFilter = new JaWEXMLUtil.XMLFilter("svg");
   private static JaWEXMLUtil.XMLFilter pdfFilter = new JaWEXMLUtil.XMLFilter("pdf");
   private static JaWEXMLUtil.XMLFilter txtFilter = new JaWEXMLUtil.XMLFilter("txt");

   private static javax.swing.filechooser.FileFilter[] lastChoosenFilter=
      new javax.swing.filechooser.FileFilter[6];
   static {
      lastChoosenFilter[0]=xpdlFilter;
      lastChoosenFilter[1]=jpgFilter;
      lastChoosenFilter[2]=svgFilter;
      lastChoosenFilter[3]=pdfFilter;
      lastChoosenFilter[4]=txtFilter;
      lastChoosenFilter[5]=allFilter;
   }

   private static JFileChooser chooser;

   static {
      String userDir=System.getProperty("user.dir");
      chooser=new JFileChooser(userDir);
   }

   public static final int INFORMATION_MESSAGE = JOptionPane.INFORMATION_MESSAGE;
   public static final int ERROR_MESSAGE = JOptionPane.ERROR_MESSAGE;

   //************************* XMLFILTER CLASS ***********************************
   /**
    * Used to filter XML files during opening or saving.
    */
   private static final class XMLFilter extends javax.swing.filechooser.FileFilter {
      private String myExtension=null;

      XMLFilter (String extension) {
         this.myExtension=extension;
      }

      public String getExtension () {
         return myExtension;
      }

      public boolean accept (File f) {
         if(f != null) {
            if(f.isDirectory()) {
               return true;
            }
            if (myExtension!=null) {
               String extension = null;
               String fName = f.getName();
               int i = fName.lastIndexOf('.');
               if(i>0 && i<fName.length()-1) {
                  extension=fName.substring(i+1).toLowerCase();
               }

               if(extension != null && extension.equalsIgnoreCase(myExtension)) {
                  return true;
               }
            } else {
               return true;
            }
         }
         return false;
      }

      public String getDescription () {
         JaWEController jc=JaWEManager.getInstance().getJaWEController();
         if (myExtension!=null) {
            return jc.getSettings().getLanguageDependentString(myExtension.toUpperCase()+"Description");
         } 
         return jc.getSettings().getLanguageDependentString("ALLDescription");         
      }
   }
   //********************** END OF XMLFILTER CLASS ******************************

   /* Show a dialog with the given error message. */
   public static void message(String message,int type) {
      JaWEController jc=JaWEManager.getInstance().getJaWEController();
      JOptionPane.showMessageDialog(jc.getJaWEFrame(),message,
            jc.getSettings().getLanguageDependentString("DialogTitle"),type);
   }

   /* Open a dialog and return the filename. Returns null if canceled.
    * @param parent    The parent component of dialog.
    * @param message   The message to write in the title of dialog
    * @param mode      0 - open dialog, 1 - save dialog
    * @param filteringMode if 0 - displays .xml and .xpdl files, if 1 - displays
    *   .jpg files, 2 - displays SVG files, 3 - displays .xml and .xpdl files,
    *  otherwise displays all files
    * @param initialName The initial name of the file to be saved or opened
    * @return The filename of opened/saved file.
    */
   public static String dialog(Component parent,String message,
                               int mode,int filteringMode,String initialName) {
      chooser.updateUI();
      chooser.setDialogTitle(message);
      int setFilterIndex = 0;
      switch (filteringMode) {
         case 0:
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setFileFilter(allFilter);
            chooser.setFileFilter(xmlFilter);
            chooser.setFileFilter(xpdlFilter);
            chooser.removeChoosableFileFilter(jpgFilter);
            chooser.removeChoosableFileFilter(svgFilter);
            chooser.removeChoosableFileFilter(pdfFilter);
            chooser.removeChoosableFileFilter(txtFilter);
            setFilterIndex = 0;
            break;
         case 1:
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setFileFilter(allFilter);
            chooser.setFileFilter(jpgFilter);
            chooser.removeChoosableFileFilter(xmlFilter);
            chooser.removeChoosableFileFilter(xpdlFilter);
            chooser.removeChoosableFileFilter(svgFilter);
            chooser.removeChoosableFileFilter(pdfFilter);
            chooser.removeChoosableFileFilter(txtFilter);
            setFilterIndex = 1;
            break;
         case 2:
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setFileFilter(allFilter);
            chooser.setFileFilter(svgFilter);
            chooser.removeChoosableFileFilter(xmlFilter);
            chooser.removeChoosableFileFilter(xpdlFilter);
            chooser.removeChoosableFileFilter(pdfFilter);
            chooser.removeChoosableFileFilter(jpgFilter);
            chooser.removeChoosableFileFilter(txtFilter);
            setFilterIndex = 2;
            break;
         case 3:
             chooser.setAcceptAllFileFilterUsed(false);
             chooser.setFileFilter(allFilter);
             chooser.setFileFilter(pdfFilter);
             chooser.removeChoosableFileFilter(xmlFilter);
             chooser.removeChoosableFileFilter(xpdlFilter);
             chooser.removeChoosableFileFilter(jpgFilter);
             chooser.removeChoosableFileFilter(svgFilter);
             chooser.removeChoosableFileFilter(txtFilter);
             setFilterIndex = 3;
             break;
         case 4:
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setFileFilter(allFilter);
            chooser.setFileFilter(txtFilter);
            chooser.removeChoosableFileFilter(xmlFilter);
            chooser.removeChoosableFileFilter(xpdlFilter);
            chooser.removeChoosableFileFilter(pdfFilter);
            chooser.removeChoosableFileFilter(jpgFilter);
            chooser.removeChoosableFileFilter(svgFilter);
            setFilterIndex = 4;
            break;
         default:
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setFileFilter(allFilter);
            chooser.removeChoosableFileFilter(xmlFilter);
            chooser.removeChoosableFileFilter(xpdlFilter);
            chooser.removeChoosableFileFilter(pdfFilter);
            chooser.removeChoosableFileFilter(jpgFilter);
            chooser.removeChoosableFileFilter(svgFilter);
            chooser.removeChoosableFileFilter(txtFilter);
            setFilterIndex = 5;
            break;
      }
      try {
         chooser.setFileFilter(lastChoosenFilter[setFilterIndex]);
      } catch (Exception ex) {}

      if (initialName!=null && initialName.length()>0) {
         File f=new File(initialName);
         chooser.setSelectedFile(f);
         chooser.ensureFileIsVisible(f);
      }

      int returnVal=-1;
      String fileName=null;

      while (true) {
         if (mode==0) {
            returnVal = chooser.showOpenDialog(parent);
         }
         else {
            returnVal = chooser.showSaveDialog(parent);
         }

         if(returnVal == JFileChooser.APPROVE_OPTION) {
            File f=chooser.getSelectedFile();
            fileName=f.getAbsolutePath();
            // SAVING:
            // - if extension isn't specified, tries to save the file with default extension
            // - it will not save file with extension if the file with extension already exists
            String extension=((JaWEXMLUtil.XMLFilter)chooser.getFileFilter()).getExtension();
            int dotIndex=f.getName().lastIndexOf(".");

            String oldFilename=fileName;
            JaWEController jc=JaWEManager.getInstance().getJaWEController();
            if (mode==1) {
               if ((filteringMode>=0 && filteringMode<=3) && extension!=null &&
                   dotIndex==-1) {
                  fileName+="."+extension;
               }
               // check if user have choosed an existing filename
               if (new File(fileName).exists()) {
                  int r=JOptionPane.showConfirmDialog(jc.getJaWEFrame(),
                        jc.getSettings().getLanguageDependentString("WarningFileAlreadyExistsDoYouWantToReplaceIt"),
                        jc.getSettings().getLanguageDependentString("DialogTitle"),JOptionPane.YES_NO_OPTION);
                  if (r==JOptionPane.NO_OPTION) {
                     fileName=oldFilename;
                     continue;
                  }
               }

               // OPENING:
               // - if extension isn't specified, and given file doesn't exist, tries
               //   to open the .xml file with the same name
            } else {
               if (mode==0 && !f.exists()) {
                  if ((filteringMode>=0 && filteringMode<=3) && dotIndex==-1) {
                     fileName+="."+extension;
                     if (!new File(fileName).exists()) {
                        fileName=null;
                     }
                  } else {
                     fileName=null;
                  }
               }
               if (fileName==null) {
                  JaWEXMLUtil.message(
                        jc.getSettings().getLanguageDependentString("WarningFileDoesNotExistPleaseSelectExistingFileToOpen"),
                     JOptionPane.WARNING_MESSAGE);
                  fileName=oldFilename;
                  continue;
               }
            }
         }
         break;
      }

      try {
         lastChoosenFilter[setFilterIndex]=chooser.getFileFilter();
      } catch (Exception ex) {}

      return fileName;
   }

}

