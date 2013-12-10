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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Window;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.swing.ImageIcon;

import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.base.xpdlvalidator.ValidationError;
import org.enhydra.jawe.misc.PFLocale;
import org.enhydra.shark.utilities.SequencedHashMap;
import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.XMLValidationError;
import org.enhydra.shark.xpdl.XPDLConstants;
import org.enhydra.shark.xpdl.elements.Activity;
import org.enhydra.shark.xpdl.elements.ActivitySet;
import org.enhydra.shark.xpdl.elements.Application;
import org.enhydra.shark.xpdl.elements.DataField;
import org.enhydra.shark.xpdl.elements.ExtendedAttribute;
import org.enhydra.shark.xpdl.elements.ExtendedAttributes;
import org.enhydra.shark.xpdl.elements.FormalParameter;
import org.enhydra.shark.xpdl.elements.Package;
import org.enhydra.shark.xpdl.elements.Participant;
import org.enhydra.shark.xpdl.elements.Transition;
import org.enhydra.shark.xpdl.elements.TypeDeclaration;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;

/**
 * Various utilities.
 * 
 * @author Sasa Bojanic
 */
public class Utils {

   public static final String LANG_PROP_PREFIX = "JaWE";

   public static Properties getProperties(String path, Properties defaultProperties)
      throws Exception {
      Properties props = defaultProperties;
      if (path == null || path.equals(""))
         return props;
      File configFile = new File(path);
      if (configFile != null) {
         if (!configFile.isAbsolute()) {
            configFile = configFile.getAbsoluteFile();
         }
         if (!configFile.exists())
            configFile = null;
      }
      if (configFile == null) {
         path = System.getProperty(JaWEConstants.JAWE_CURRENT_CONFIG_HOME) + "/" + path;
         configFile = new File(path);
         if (configFile == null) {
            return props;
         }
         if (!configFile.isAbsolute()) {
            configFile = configFile.getAbsoluteFile();
         }
      }
      if (configFile.exists()) {
         FileInputStream fis = null;
         try {
            fis = new FileInputStream(configFile);
            props.load(fis);
            fis.close();
         } catch (Exception ex) {
            throw new Exception("Something went wrong while reading configuration from file "
                                      + configFile + "!!!",
                                ex);
         }
      } else {
         throw new Exception("Component needs to be configured properly - configuration file "
                             + configFile + " does not exist!!!");
      }
      return props;
   }

   public static boolean checkFileExistence(String path) {
      if (path == null || path.equals(""))
         return false;
      File configFile = new File(path);
      if (configFile != null) {
         if (!configFile.isAbsolute()) {
            configFile = configFile.getAbsoluteFile();
         }
         if (!configFile.exists())
            configFile = null;
      }
      if (configFile == null) {
         path = System.getProperty(JaWEConstants.JAWE_CURRENT_CONFIG_HOME) + "/" + path;
         configFile = new File(path);
         if (configFile == null) {
            return false;
         }
         if (!configFile.isAbsolute()) {
            configFile = configFile.getAbsoluteFile();
         }
      }
      if (!configFile.exists()) {
         return false;
      }
      return true;
   }

   public static boolean checkResourceExistence(String path, String name) {
      return Utils.class.getClassLoader().getResource(path + name) != null;
   }

   public static Map getProperties(Properties properties, String startsWith) {
      Map toRet = new SequencedHashMap();
      Iterator it = properties.entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry me = (Map.Entry) it.next();
         if (((String) me.getKey()).startsWith(startsWith)) {
            toRet.put(me.getKey(), me.getValue());
         }
      }
      return toRet;
   }

   public static boolean copyPropertyFile(String path, String name, boolean overwrite)
      throws Exception {
      String confhome = System.getProperty(JaWEConstants.JAWE_CURRENT_CONFIG_HOME);
      if (confhome == null)
         return true;
      String filename = confhome + "/" + name;
      if (!overwrite) {
         boolean doesExist = Utils.checkFileExistence(filename);

         if (doesExist)
            return false;
      }

      System.out.println("Copying property file " + name + " to " + filename);
      InputStream is = null;
      URL u = Utils.class.getClassLoader().getResource(path + name);

      BufferedReader input = null;
      Writer output = null;
      try {
         URLConnection urlConnection = u.openConnection();
         is = urlConnection.getInputStream();
         input = new BufferedReader(new InputStreamReader(is));
         StringBuffer contents = new StringBuffer();
         String line = null; // not declared within while loop
         while ((line = input.readLine()) != null) {
            contents.append(line);
            contents.append(System.getProperty("line.separator"));
         }

         output = new BufferedWriter(new FileWriter(filename));
         output.write(contents.toString());
      } finally {
         if (input != null)
            input.close();
         if (output != null)
            output.close();
      }
      return true;
      // return false;
   }

   public static void manageProperties(Properties properties, String path, String name)
      throws Exception {
      try {
         // if (true) return;
         if (!Utils.copyPropertyFile(path, name, false)) {
            Properties external = new Properties();
            FileInputStream fis = null;
            try {
               fis = new FileInputStream(System.getProperty(JaWEConstants.JAWE_CURRENT_CONFIG_HOME)
                                         + "/" + name);
               external.load(fis);
               fis.close();
               Utils.adjustProperties(properties, external);
            } catch (Exception ex) {
               throw new Error("Something went wrong while reading external component properties !!!",
                               ex);
            }
         } else {
            URL u = JaWEManager.class.getClassLoader().getResource(path + name);
            if (u == null)
               return;
            URLConnection urlConnection = u.openConnection();
            InputStream is = urlConnection.getInputStream();

            properties.load(is);
         }
      } catch (Exception ex) {
         throw new Exception("Something went wrong while reading component's properties !!!",
                             ex);
      }
   }

   public static void adjustProperties(Properties original, Properties external) {
      Iterator it = external.entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry me = (Map.Entry) it.next();
         String key = (String) me.getKey();
         String val = (String) me.getValue();
         original.setProperty(key, val);
      }
   }

   /**
    * Take the given string and chop it up into a series of strings on given boundries.
    * This is useful for trying to get an array of strings out of the resource file.
    */
   public static String[] tokenize(String input, String boundary) {
      if (input == null)
         input = "";
      Vector v = new Vector();
      StringTokenizer t = new StringTokenizer(input, boundary);
      String cmd[];

      while (t.hasMoreTokens())
         v.addElement(t.nextToken());
      cmd = new String[v.size()];
      for (int i = 0; i < cmd.length; i++)
         cmd[i] = (String) v.elementAt(i);

      return cmd;
   }

   /** Returns the class name without package. */
   public static String getUnqualifiedClassName(Class cls) {
      String name = cls.getName();
      int lastDot = name.lastIndexOf(".");
      if (lastDot >= 0) {
         name = name.substring(lastDot + 1, name.length());
      }
      return name;
   }

   /**
    * Returns the color parsed from the given string. The color can be given in three
    * different string form:
    * <ul>
    * <li> using prefix Color, dot and wanted color, e.g. <b>Color.red</b>
    * <li> using prefix SystemColor, dot and wanted color, e.g. <b>SystemColor.desktop</b>
    * <li> using RGB like string, e.g. <b>R=124,G=213,B=12</b>
    * </ul>
    * 
    * @param col The string representation of wanted color.
    * @return The color based on given string or null if incorrect
    */
   public static Color getColor(String col) {
      Color c = null;
      int dotInd = col.indexOf(".");
      int r, g, b;
      if (col.indexOf("Color") != -1 && dotInd != -1) {
         try {
            ClassLoader cl = JaWEManager.class.getClassLoader();
            Class cls = cl.loadClass("java.awt." + col.substring(0, dotInd));
            Field f = cls.getField(col.substring(dotInd + 1));
            c = (Color) f.get(null);
            c = new Color(c.getRed(), c.getGreen(), c.getBlue());
         } catch (Exception ex) {
         }
      } else {
         try {
            int i1 = col.indexOf("R=");
            if (i1 == -1)
               i1 = col.indexOf("r=");
            int i1c = col.indexOf(",", i1 + 2);
            int i2 = col.indexOf("G=");
            if (i2 == -1)
               i2 = col.indexOf("g=");
            int i2c = col.indexOf(",", i2 + 2);
            int i3 = col.indexOf("B=");
            if (i3 == -1)
               i3 = col.indexOf("b=");
            if (i1 != -1
                && i1c != -1 && i2 != -1 && i2c != -1 && i3 != -1 && (i1c < i2)
                && (i2c < i3)) {
               r = Integer.valueOf(col.substring(i1 + 2, i1c).trim()).intValue();
               if (r < 0)
                  r = 0;
               if (r > 255)
                  r = 255;
               g = Integer.valueOf(col.substring(i2 + 2, i2c).trim()).intValue();
               if (g < 0)
                  g = 0;
               if (g > 255)
                  g = 255;
               b = Integer.valueOf(col.substring(i3 + 2).trim()).intValue();
               if (b < 0)
                  b = 0;
               if (b > 255)
                  b = 255;
               c = new Color(r, g, b);
            }
         } catch (Exception ex) {
         }
      }
      return c;
   }

   public static void flipCoordinates(Point p) {
      int x = p.x;
      p.x = p.y;
      p.y = x;
   }

   // ******************************** DEBUGGING STUFF ****************************
   /** Used for debug only */
   public static void printStrings(String[] s) {
      if (s != null) {
         for (int i = 0; i < s.length; i++) {
            System.out.println("String no " + i + " = " + s[i]);
         }
      } else {
         System.out.println("Passed string array is null !!!");
      }
   }

   // **************************** END OF DEBUGGING STUFF *************************

   public static void center(Window w, int minXDiffFromMax, int minYDiffFromMax) {
      w.pack();

      // make dialog smaller if needed, and center it
      // Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment()
         .getDefaultScreenDevice()
         .getDefaultConfiguration()
         .getBounds()
         .getSize();

      Dimension windowSize = w.getPreferredSize();
      if (windowSize.width > screenSize.width - minXDiffFromMax) {
         windowSize.width = screenSize.width - minXDiffFromMax;
      }
      if (windowSize.height > screenSize.height - minYDiffFromMax) {
         windowSize.height = screenSize.height - minYDiffFromMax;
      }
      w.setSize(windowSize);
      w.setLocation(screenSize.width / 2 - (windowSize.width / 2),
                    screenSize.height / 2 - (windowSize.height / 2));
   }

   public static java.util.List findPropertyFiles() {
      java.util.List pfs = new ArrayList();

      URL u = Utils.class.getClassLoader().getResource("org/enhydra/jawe/language");
      File path = null;
      if (u != null) {
         try {
            path = new File(u.toURI());
         } catch (Exception ex) {
         }
      }
      // if folder exists and realy is a folder but not file
      if (path != null && path.exists() && path.isDirectory()) {
         // getting all .property files within folder
         pfs.addAll(Arrays.asList(path.list(new PFFilter())));
      } else {
         JarFile jfile = null;
         try {
            String jp = "org/enhydra/jawe/language/JaWE.properties";
            u = Utils.class.getClassLoader().getResource(jp);
            String jarPath = u.getPath()
               .substring(0, u.getPath().length() - jp.length() - 2);
            jarPath = URLDecoder.decode(jarPath, "UTF-8");
            jarPath = jarPath.substring(5);
            jfile = new JarFile(jarPath, false);
            // get all entries
            Enumeration e = jfile.entries();
            // loop through entries and find appropriate ones
            while (e.hasMoreElements()) {
               try {
                  ZipEntry entry = (ZipEntry) e.nextElement();
                  String entryname = entry.getName();
                  // entry must end with '.properties'
                  if (entryname.indexOf(LANG_PROP_PREFIX) != -1
                      && entryname.endsWith(".properties")) {
                     pfs.add(entryname);
                  }
               } catch (Exception ex) {
               }
            }
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }

      // if there are no property files, try to find the default ones distributed
      // with JaWE
      if (pfs.size() == 0) {
         u = Utils.class.getClassLoader()
            .getResource("org/enhydra/jawe/language/JaWE.properties");
         if (u != null) {
            pfs.add("JaWE.properties");
         }
         u = Utils.class.getClassLoader()
            .getResource("org/enhydra/jawe/language/JaWE_de.properties");
         if (u != null) {
            pfs.add("JaWE_de.properties");
         }
         u = Utils.class.getClassLoader()
            .getResource("org/enhydra/jawe/language/JaWE_fr.properties");
         if (u != null) {
            pfs.add("JaWE_fr.properties");
         }
         u = Utils.class.getClassLoader()
            .getResource("org/enhydra/jawe/language/JaWE_sh.properties");
         if (u != null) {
            pfs.add("JaWE_sh.properties");
         }
         u = Utils.class.getClassLoader()
            .getResource("org/enhydra/jawe/language/JaWE_es.properties");
         if (u != null) {
            pfs.add("JaWE_es.properties");
         }
         u = Utils.class.getClassLoader()
            .getResource("org/enhydra/jawe/language/JaWE_pt.properties");
         if (u != null) {
            pfs.add("JaWE_pt.properties");
         }
      }
      java.util.List pfLocales = new ArrayList();
      if (pfs.size() > 0) {
         // adding default locale (for default property file)
         pfLocales.add(new PFLocale());
         for (int i = 0; i < pfs.size(); i++) {
            String propFile = (String) pfs.get(i);
            int p1 = propFile.indexOf(LANG_PROP_PREFIX) + LANG_PROP_PREFIX.length();
            boolean isDefault = !propFile.substring(p1, p1 + 1).equals("_");
            if (!isDefault) {
               PFLocale pfl = PFLocale.createPFLocale(propFile.substring(p1 + 1,
                                                                         propFile.length()
                                                                               - ".properties".length()));
               if (pfl != null) {
                  pfLocales.add(pfl);
               }
            }
         }
      }
      return pfLocales;
   }

   public static void copyFile(String src, String dest) {
      try {
         FileChannel sourceChannel = new FileInputStream(src).getChannel();
         FileChannel destinationChannel = new FileOutputStream(dest).getChannel();
         // Copy source file to destination file
         destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
         sourceChannel.close();
         destinationChannel.close();
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   public static void showEAS(ExtendedAttributes el) {
      System.err.println("ExtendedAttributes, el = "
                         + el + ", there are " + el.size() + " eas");
      Iterator it = el.toElements().iterator();
      while (it.hasNext()) {
         System.err.println();
         Utils.showEA((ExtendedAttribute) it.next());
      }
   }

   public static void showEA(ExtendedAttribute el) {
      System.err.println("ExtendedAttribute, el = " + el);
      System.err.println("ExtendedAttribute.name), el = " + el.get("Name"));
      System.err.println("ExtendedAttribute.value), el = " + el.get("Value"));
      System.err.println();
   }

   /** Gets the current date and time string in ISO-8601 format. */
   public static String getCurrentDateAndTime() {
      String dateSeparator = "-";
      String timeSeparator = ":";
      Calendar cal = new GregorianCalendar();
      String dateTime = "";
      dateTime = dateTime + String.valueOf(cal.get(Calendar.YEAR)) + dateSeparator;
      int mnth = cal.get(Calendar.MONTH) + 1;
      if (mnth < 10) {
         dateTime = dateTime + "0";
      }
      dateTime = dateTime + String.valueOf(mnth) + dateSeparator;
      int dayOfMnth = cal.get(Calendar.DAY_OF_MONTH);
      if (dayOfMnth < 10) {
         dateTime = dateTime + "0";
      }
      dateTime = dateTime + String.valueOf(dayOfMnth) + " ";
      int hr = cal.get(Calendar.HOUR_OF_DAY);
      int ampm = cal.get(Calendar.AM_PM);
      if (ampm == Calendar.PM && hr < 12) {
         hr += 12;
      }
      if (hr < 10) {
         dateTime = dateTime + "0";
      }
      dateTime = dateTime + String.valueOf(hr) + timeSeparator;
      int min = cal.get(Calendar.MINUTE);
      if (min < 10) {
         dateTime = dateTime + "0";
      }
      dateTime = dateTime + String.valueOf(min) + timeSeparator;
      int sec = cal.get(Calendar.SECOND);
      if (sec < 10) {
         dateTime = dateTime + "0";
      }
      dateTime = dateTime + String.valueOf(sec);

      return dateTime;
   }

   public static String getActivityStringType(int activityType) {
      String retVal = "";

      switch (activityType) {
         case XPDLConstants.ACTIVITY_TYPE_NO:
            retVal = JaWEConstants.ACTIVITY_TYPE_NO;
            break;
         case XPDLConstants.ACTIVITY_TYPE_TOOL:
            retVal = JaWEConstants.ACTIVITY_TYPE_TOOL;
            break;
         case XPDLConstants.ACTIVITY_TYPE_BLOCK:
            retVal = JaWEConstants.ACTIVITY_TYPE_BLOCK;
            break;
         case XPDLConstants.ACTIVITY_TYPE_ROUTE:
            retVal = JaWEConstants.ACTIVITY_TYPE_ROUTE;
            break;
         case XPDLConstants.ACTIVITY_TYPE_SUBFLOW:
            retVal = JaWEConstants.ACTIVITY_TYPE_SUBFLOW;
            break;
      }

      return retVal;
   }

   public static List sortValidationErrorList(List verrs) {
      List sorted = new ArrayList();

      Map epves = JaWEManager.getInstance()
         .getXPDLValidator()
         .getExtPkgValidationErrors();
      Iterator it = epves.values().iterator();
      while (it.hasNext()) {
         List l = (List) it.next();
         verrs.removeAll(l);
      }
      sorted.addAll(Utils.sortValidationErrorList(verrs, JaWEManager.getInstance()
         .getJaWEController()
         .getMainPackage()));
      it = epves.entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry me = (Map.Entry) it.next();
         Package p = (Package) me.getKey();
         List l = (List) me.getValue();
         sorted.addAll(Utils.sortValidationErrorList(l, p));
      }

      return sorted;

   }

   public static List sortValidationErrorList(List verrs, Package p) {
      List sorted = new ArrayList();

      sorted.addAll(findErrorList(verrs,
                                  Package.class,
                                  XMLValidationError.SUB_TYPE_SCHEMA));

      sorted.addAll(findErrorList(verrs, p, XMLValidationError.SUB_TYPE_LOGIC));
      sorted.addAll(findErrorListParent(verrs, p));
      sorted.addAll(findErrorListParent(verrs, p.getTypeDeclarations()));
      sorted.addAll(findErrorListParent(verrs, p.getApplications()));
      sorted.addAll(findErrorListParent(verrs, p.getParticipants()));
      sorted.addAll(findErrorListParent(verrs, p.getDataFields()));
      Iterator it = p.getApplications().toElements().iterator();
      while (it.hasNext()) {
         Application app = (Application) it.next();
         sorted.addAll(findErrorListParent(verrs, app.getApplicationTypes()
            .getFormalParameters()));
      }

      it = p.getWorkflowProcesses().toElements().iterator();
      while (it.hasNext()) {
         WorkflowProcess wp = (WorkflowProcess) it.next();
         sorted.addAll(findErrorList(verrs, wp));
      }
      it = p.getWorkflowProcesses().toElements().iterator();
      while (it.hasNext()) {
         WorkflowProcess wp = (WorkflowProcess) it.next();
         Iterator it2 = wp.getActivitySets().toElements().iterator();
         while (it2.hasNext()) {
            ActivitySet as = (ActivitySet) it2.next();
            sorted.addAll(findErrorList(verrs, as));
         }
      }
      it = p.getWorkflowProcesses().toElements().iterator();
      while (it.hasNext()) {
         WorkflowProcess wp = (WorkflowProcess) it.next();
         sorted.addAll(findErrorListParent(verrs, wp.getApplications()));
         sorted.addAll(findErrorListParent(verrs, wp.getParticipants()));
         sorted.addAll(findErrorListParent(verrs, wp.getDataFields()));
         sorted.addAll(findErrorListParent(verrs, wp.getFormalParameters()));
         Iterator it2 = wp.getApplications().toElements().iterator();
         while (it2.hasNext()) {
            Application app = (Application) it2.next();
            sorted.addAll(findErrorListParent(verrs, app.getApplicationTypes()
               .getFormalParameters()));
         }
         it2 = wp.getActivitySets().toElements().iterator();
         while (it2.hasNext()) {
            ActivitySet as = (ActivitySet) it2.next();
            Iterator it3 = as.getActivities().toElements().iterator();
            while (it3.hasNext()) {
               Activity act = (Activity) it3.next();
               sorted.addAll(findErrorList(verrs, act));
            }
            it3 = as.getTransitions().toElements().iterator();
            while (it3.hasNext()) {
               Transition tra = (Transition) it3.next();
               sorted.addAll(findErrorList(verrs, tra));
            }
         }
         it2 = wp.getActivities().toElements().iterator();
         while (it2.hasNext()) {
            Activity act = (Activity) it2.next();
            sorted.addAll(findErrorList(verrs, act));
         }
         it2 = wp.getTransitions().toElements().iterator();
         while (it2.hasNext()) {
            Transition tra = (Transition) it2.next();
            sorted.addAll(findErrorList(verrs, tra));
         }
      }
      verrs.removeAll(sorted);
      sorted.addAll(verrs);

      return sorted;
   }

   public static List findErrorList(List allErrs, Object obj, String validationType) {
      List toRet = new ArrayList();

      for (int i = 0; i < allErrs.size(); i++) {
         ValidationError verr = (ValidationError) allErrs.get(i);
         if (verr.getSubType().equals(validationType) && verr.getElement() == obj) {
            toRet.add(verr);
         }
      }
      return toRet;
   }

   public static List findErrorList(List allErrs, Object obj) {
      List toRet = new ArrayList();

      for (int i = 0; i < allErrs.size(); i++) {
         ValidationError verr = (ValidationError) allErrs.get(i);
         if (verr.getElement() == obj) {
            toRet.add(verr);
         }
      }
      return toRet;
   }

   public static List findErrorList(List allErrs, Class objClass) {
      List toRet = new ArrayList();

      for (int i = 0; i < allErrs.size(); i++) {
         ValidationError verr = (ValidationError) allErrs.get(i);
         if (verr.getElement().getClass() == objClass) {
            toRet.add(verr);
         }
      }
      return toRet;
   }

   public static List findErrorList(List allErrs, Class objClass, String validationType) {
      List toRet = new ArrayList();

      for (int i = 0; i < allErrs.size(); i++) {
         ValidationError verr = (ValidationError) allErrs.get(i);
         if (verr.getSubType().equals(validationType)
             && verr.getElement().getClass() == objClass) {
            toRet.add(verr);
         }
      }
      return toRet;
   }

   public static List findErrorListParent(List allErrs, Object parentObj) {
      List toRet = new ArrayList();

      for (int i = 0; i < allErrs.size(); i++) {
         ValidationError verr = (ValidationError) allErrs.get(i);
         if (verr.getElement().getParent() == parentObj) {
            toRet.add(verr);
         }
      }
      return toRet;
   }

   public static int countErrors(List verrs) {
      int errs = 0;
      for (int i = 0; i < verrs.size(); i++) {
         ValidationError verr = (ValidationError) verrs.get(i);
         if (verr.getType().equalsIgnoreCase(XMLValidationError.TYPE_ERROR)) {
            errs++;
         }
      }
      return errs;
   }

   /**
    * Java doesn't support direct opening of arbitrary documents, but this hack should do
    * it. For Windows executing "start", and for KDE "kfmclient exec" will open document
    * with associated application. Associations are of course system dependant, and we
    * cannot do anything about them.
    */
   public static boolean showExternalDocument(String document) {
      if (!(new File(document).canRead())) {
         return false;
      }
      String startCommand = System.getProperty("path.to.start");
      if (null != startCommand) {
         if (!new File(startCommand).canRead()) {
            return false;
         }
         if (System.getProperty("path.separator").equals(";")) {
            document = "\"" + document + "\"";
         }
      } else {
         if (System.getProperty("path.separator").equals(":")) {
            startCommand = "kfmclient exec";
         } else {
            startCommand = "cmd /c start";
            document = "\"" + document + "\"" + " \"" + document + "\"";
         }
      }
      try {
         Runtime.getRuntime().exec(startCommand + " " + document);
      } catch (Throwable t) {
         t.printStackTrace();
         return false;
      }
      return true;
   }

   public static XMLElement getLocation(XMLElement el) {
      XMLElement location = XMLUtil.getActivity(el);
      if (location == null) {
         location = XMLUtil.getTransition(el);
      }
      if (location == null) {
         location = XMLUtil.getTransition(el);
      }
      if (location == null) {
         location = XMLUtil.getParentElement(TypeDeclaration.class, el);
      }
      if (location == null) {
         location = XMLUtil.getParentElement(FormalParameter.class, el);
      }
      if (location == null) {
         location = XMLUtil.getParentElement(Application.class, el);
      }
      if (location == null) {
         location = XMLUtil.getParentElement(Participant.class, el);
      }
      if (location == null) {
         location = XMLUtil.getParentElement(DataField.class, el);
      }
      ActivitySet as = XMLUtil.getActivitySet(el);
      if (location == null) {
         location = as;
      }
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(el);
      if (location == null) {
         location = wp;
      }
      Package pkg = XMLUtil.getPackage(el);
      if (location == null) {
         location = pkg;
      }
      return location;
   }

   public static String getLocString(XMLElement location, XMLElement el) {
      ActivitySet as = XMLUtil.getActivitySet(el);
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(el);
      Package pkg = XMLUtil.getPackage(el);

      String loc = ResourceManager.getLanguageDependentString("PackageKey")
                   + " '" + pkg.getId() + "'";
      if (wp != null) {
         loc += ", "
                + ResourceManager.getLanguageDependentString("WorkflowProcessKey") + " '"
                + wp.getId() + "'";
      }
      if (as != null) {
         loc += ", "
                + ResourceManager.getLanguageDependentString("ActivitySetKey") + " '"
                + as.getId() + "'";
      }
      if (location != as && location != wp && location != pkg) {
         loc += ", "
                + ResourceManager.getLanguageDependentString(location.toName() + "Key")
                + " '" + ((XMLComplexElement) location).get("Id").toValue() + "'";
      }
      if (el != location && el != as && el != wp && el != pkg) {
         XMLElement parent=el.getParent();         
         if (parent!=location && !(parent instanceof XMLCollection)) {
            loc += ", "
                   + ResourceManager.getLanguageDependentString(parent.toName()
                                                                + "Key");
         }
         loc += " -> " + ResourceManager.getLanguageDependentString(el.toName() + "Key");
      }

      return loc;
   }

   public static List makeSearchResultList(List results) {
      List srl = new ArrayList();

      for (int i = 0; i < results.size(); i++) {
         srl.add(new SearchResult((XMLElement) (results.get(i))));
      }

      return srl;
   }

   public static Map loadActions(Properties properties, JaWEComponent comp) {
      return loadActions(properties, comp, new HashMap());
   }

   public static Map loadActions(Properties properties,
                                 JaWEComponent comp,
                                 Map defaultActions) {
      Map componentAction = new HashMap();

      // ******** actions
      Set actions = new HashSet();
      List actionsN = ResourceManager.getResourceStrings(properties,
                                                         "Action.Name.",
                                                         false);
      List actionsC = ResourceManager.getResourceStrings(properties,
                                                         "Action.Class.",
                                                         false);
      List actionsI = ResourceManager.getResourceStrings(properties,
                                                         "Action.Image.",
                                                         false);
      actions.addAll(actionsN);
      actions.addAll(actionsC);
      actions.addAll(actionsI);
      for (Iterator it = actions.iterator(); it.hasNext();) {
         String actionName = (String) it.next();

         try {
            JaWEAction ja;
            if (defaultActions.containsKey(actionName))
               ja = (JaWEAction) defaultActions.get(actionName);
            else
               ja = new JaWEAction();

            try {
               // Action Base
               String className = ResourceManager.getResourceString(properties,
                                                                    "Action.Class."
                                                                          + actionName);
               Constructor c = Class.forName(className).getConstructor(new Class[] {
                  JaWEComponent.class
               });
               ActionBase action = (ActionBase) c.newInstance(new Object[] {
                  comp
               });
               ja.setAction(action);
            } catch (Exception e) {
            }

            try {
               // Icon
               URL url = ResourceManager.getResource(properties, "Action.Image."
                                                                 + actionName);
               ImageIcon icon = null;
               if (url != null)
                  icon = new ImageIcon(url);

               ja.setIcon(icon);
            } catch (Exception e) {
            }

            try {
               // Language dependent name
               String langDepName = ResourceManager.getResourceString(properties,
                                                                      "Action.Name."
                                                                            + actionName);
               ja.setLangDepName(langDepName);
            } catch (Exception e) {
            }

            if (ja.getAction() != null) {
               componentAction.put(actionName, ja);
               JaWEManager.getInstance().getLoggingManager().info("Created "
                                                                  + comp.getName()
                                                                  + " action for class "
                                                                  + actionName);
            } else {
               JaWEManager.getInstance().getLoggingManager().info("Missing action for "
                                                                  + actionName);
            }
         } catch (Throwable thr) {
            JaWEManager.getInstance().getLoggingManager().error("Can't create "
                                                                + comp.getName()
                                                                + " action for class "
                                                                + actionName);
         }
      }

      return componentAction;
   }

   public static Map loadAllMenusAndToolbars(Properties properties) {
      Map mst = new HashMap();

      mst = loadSubMenus(properties);
      mst.putAll(loadPopups(properties));
      mst.putAll(loadToolbars(properties));

      return mst;
   }

   public static Map loadSubMenus(Properties properties) {
      Map componentSettings = new HashMap();

      List subMenus = ResourceManager.getResourceStrings(properties,
                                                         "SubMenu.Name.",
                                                         false);
      for (int i = 0; i < subMenus.size(); i++) {
         String aorder = ResourceManager.getResourceString(properties,
                                                           "SubMenu.ActionOrder."
                                                                 + subMenus.get(i));
         String langName = ResourceManager.getResourceString(properties,
                                                             "SubMenu.Name."
                                                                   + subMenus.get(i));
         componentSettings.put(subMenus.get(i) + "Menu", aorder);
         componentSettings.put(subMenus.get(i) + "LangName", langName);
      }

      return componentSettings;
   }

   public static Map loadToolbars(Properties properties) {
      Map componentSettings = new HashMap();

      List toolbars = ResourceManager.getResourceStrings(properties,
                                                         "Toolbar.ActionOrder.",
                                                         false);
      for (int i = 0; i < toolbars.size(); i++) {
         String aorder = ResourceManager.getResourceString(properties,
                                                           "Toolbar.ActionOrder."
                                                                 + toolbars.get(i));
         componentSettings.put(toolbars.get(i) + "Toolbar", aorder);
      }

      return componentSettings;
   }

   public static Map loadPopups(Properties properties) {
      Map componentSettings = new HashMap();
      List popupMenus = ResourceManager.getResourceStrings(properties,
                                                           "PopupMenu.ActionOrder.",
                                                           false);
      for (int i = 0; i < popupMenus.size(); i++) {
         String aorder = ResourceManager.getResourceString(properties,
                                                           "PopupMenu.ActionOrder."
                                                                 + popupMenus.get(i));
         componentSettings.put(popupMenus.get(i) + "Menu", aorder);
      }
      return componentSettings;
   }

   protected static SequencedHashMap actImgResources = null;

   protected static List actImgResourceNames = null;

   public static List getActivityIconNamesList() {
      if (actImgResources == null) {
         getActivityIconsMap();
      }
      return new ArrayList(actImgResourceNames);
   }

   public static SequencedHashMap getOriginalActivityIconsMap() {
      if (actImgResources == null) {
         getActivityIconsMap();
      }
      return actImgResources;
   }

   public static SequencedHashMap getActivityIconsMap() {
      if (actImgResources == null) {
         Map m = new HashMap();
         String classPath = System.getProperty("java.class.path");
         String[] cps = Utils.tokenize(classPath, File.pathSeparator);
         String jed = System.getProperty("java.ext.dirs");
         String[] jeds = Utils.tokenize(jed, File.pathSeparator);
         for (int i = 0; i < cps.length; i++) {
            String cp = cps[i];
            if (cp.indexOf("activityicons") >= 0) {
               m.putAll(Utils.getResourcesForPath(cp, JaWEConstants.JAWE_ACTIVITY_ICONS));
            }
         }
         for (int i = 0; i < jeds.length; i++) {
            String jd = jeds[i];
            File f = new File(jd);
            if (f.exists() && f.isDirectory()) {
               File[] files = f.listFiles();
               if (files != null) {
                  for (int j = 0; j < files.length; j++) {
                     if (files[j].isFile()
                         && files[j].getName().indexOf("activityicons") >= 0) {
                        m.putAll(Utils.getResourcesForPath(files[j].getAbsolutePath(),
                                                           JaWEConstants.JAWE_ACTIVITY_ICONS));
                     }
                  }
               }
            }
         }
         actImgResources = new SequencedHashMap();
         actImgResourceNames = new ArrayList(m.keySet());
         Collections.sort(actImgResourceNames);
         for (int i = 0; i < actImgResourceNames.size(); i++) {
            Object key = actImgResourceNames.get(i);
            actImgResources.put(key, m.get(key));
         }
      }
      return new SequencedHashMap(actImgResources);
   }

   /**
    * Returns a Map of images from located on given path.
    */
   public static Map getResourcesForPath(String classPath, String prefix) {
      Map resources = new HashMap();
      // case of resources contained in a jar file.
      if (classPath.endsWith(".jar")) {
         JarFile jfile = null;
         try {
            jfile = new JarFile(classPath, false);
         } catch (Throwable ex) {
            ex.printStackTrace();
            return resources;
         }
         // get all entries
         Enumeration e = jfile.entries();
         // loop through entries and find appropriate ones
         while (e.hasMoreElements()) {
            try {
               ZipEntry entry = (ZipEntry) e.nextElement();
               String entryname = entry.getName();
               String res = entryname;
               // removes starting '/'
               if (res.startsWith("/")) {
                  res = res.substring(1);
               }
               // entry must start with prefix
               if (entryname.startsWith(prefix)) {
                  // checking if resource is image
                  ImageIcon ii = null;
                  try {
                     ii = new ImageIcon(Utils.class.getClassLoader()
                        .getResource(entryname));
                  } catch (Exception ex) {
                  }

                  if (ii != null) {
                     // removes the prefix
                     res = res.substring(prefix.length());
                     if (res.startsWith("/")) {
                        res = res.substring(1);
                     }
                     if (res.length() > 0) {
                        resources.put(res, ii);
                     }
                  }
               }
            } catch (Throwable thr) {
            }
         }
      } else { // if it is not .jar file, but directory
         // getting the folder
         File startingFolder = new File(classPath + "/" + prefix);
         // if folder exists and realy is a folder but not file
         if (startingFolder.exists() && startingFolder.isDirectory()) {
            File[] children = startingFolder.listFiles();
            for (int i = 0; i < children.length; i++) {
               if (children[i].isFile()) {
                  String fileName = children[i].getName();
                  // checking if resource is image
                  ImageIcon ii = null;
                  try {
                     ii = new ImageIcon(Utils.class.getClassLoader()
                        .getResource(prefix + "/" + fileName));
                  } catch (Exception ex) {
                  }
                  if (ii != null) {
                     resources.put(fileName, ii);
                  }
               }
            }

         }
      }
      return resources;
   }

   public static boolean reconfigure(String newConfig) {
      JaWEController jc = JaWEManager.getInstance().getJaWEController();

      String fn = JaWEConstants.JAWE_CONF_HOME + "/defaultconfig";
      File file = new File(fn);

      if (!file.exists()) {
         return false;
      }

      File newFile = new File(JaWEConstants.JAWE_CONF_HOME + "/temp");

      WaitScreen ws = new WaitScreen(null);
      try {
         newFile.createNewFile();

         BufferedReader reader = new BufferedReader(new FileReader(file));
         BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));

         String line = null;
         boolean done = false;
         while ((line = reader.readLine()) != null) {
            if (!done
                && (line.startsWith("#" + JaWEConstants.JAWE_CURRENT_CONFIG_HOME) || line.startsWith(JaWEConstants.JAWE_CURRENT_CONFIG_HOME))) {
               line = JaWEConstants.JAWE_CURRENT_CONFIG_HOME + " = " + newConfig;

               done = true;
            }

            writer.write(line + "\n");
         }

         reader.close();
         writer.close();

         file.delete();
         newFile.renameTo(file);

         System.setProperty(JaWEConstants.JAWE_CURRENT_CONFIG_HOME,
                            JaWEConstants.JAWE_CONF_HOME + "/" + newConfig);

         String filename = jc.getPackageFilename(jc.getMainPackageId());
         jc.tryToClosePackage(jc.getMainPackageId(), true);

         ws.show(null, "", jc.getSettings()
            .getLanguageDependentString("ReconfiguringKey"));

         JaWEManager.getInstance().restart(filename);
         ws.setVisible(false);
         return true;
      } catch (Throwable e) {
         e.printStackTrace();
         return false;
      } finally {
         ws.setVisible(false);
      }
   }

}

class PFFilter implements FilenameFilter {
   String pfStr = ".properties";

   public boolean accept(File dir, String name) {
      String f = new File(name).getName();
      int fi = f.indexOf(Utils.LANG_PROP_PREFIX);
      int li = f.lastIndexOf(Utils.LANG_PROP_PREFIX);
      return (fi != -1 && fi == 0 && fi == li && f.endsWith(pfStr));
   }
}
