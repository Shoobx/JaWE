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

package org.enhydra.jawe.base.controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;

import org.enhydra.jawe.BarFactory;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.JaWETabbedPane;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jxpdl.XMLUtil;

/**
 * Used to handle JaWE.
 * 
 * @author Sasa Bojanic
 */
public class JaWEFrame extends JFrame implements JaWEComponentView {

   protected JaWEController controller;

   protected JMenuBar menubar = new JMenuBar();

   protected JToolBar toolbar;

   protected JPanel menubarAndToolbar = new JPanel();

   protected JPanel workingArea = new JPanel();

   protected JSplitPane bigSplit;

   protected JSplitPane smallSplit1;

   protected JSplitPane smallSplit2;

   protected JaWETabbedPane treeComponents = new JaWETabbedPane();

   protected JaWETabbedPane specialComponents = new JaWETabbedPane();

   protected JaWETabbedPane mainComponents = new JaWETabbedPane();

   protected JaWETabbedPane otherComponents = new JaWETabbedPane();

   protected JaWETabbedPane buttonComponents = new JaWETabbedPane();

   // protected XPDLPreview xpdlPreview;
   // protected ImageIcon appIcon, logoIcon;

   // attributes for maximizing
   private Rectangle oldBounds;

   private JComponent maxComponent;

   private Container maxCompParent;

   public JaWEFrame(JaWEController controller) {
      this.controller = controller;
      setLocale(ResourceManager.getChoosenLocale());
      JOptionPane.setDefaultLocale(ResourceManager.getChoosenLocale());
      JFileChooser.setDefaultLocale(ResourceManager.getChoosenLocale());
   }

   public void configure() {
      init();
   }

   public void maximizeComponent(JComponent com) {
      workingArea.removeAll();

      oldBounds = new Rectangle(com.getBounds());
      com.setBounds(0, 0, workingArea.getWidth(), workingArea.getHeight());

      maxComponent = com;
      maxCompParent = maxComponent.getParent();

      workingArea.add(com, BorderLayout.CENTER);

      com.setVisible(true);
   }

   public void restoreWorkingArea() {
      maxComponent.setBounds(oldBounds);
      workingArea.remove(maxComponent);

      workingArea.removeAll();
      maxCompParent.add(maxComponent);

      workingArea.add(bigSplit, BorderLayout.CENTER);
   }

   public void init() {
      mainComponents.setName(JaWEComponent.MAIN_COMPONENT);
      specialComponents.setName(JaWEComponent.SPECIAL_COMPONENT);
      treeComponents.setName(JaWEComponent.TREE_COMPONENT);
      otherComponents.setName(JaWEComponent.OTHER_COMPONENT);

      menubar = BarFactory.createMainMenu(controller);
      toolbar = BarFactory.createToolbar("defaultToolbar", controller);
      toolbar.setFloatable(false);

      // Logo
      // setIconImage(controller.getControllerSettings().getApplicationIcon().getImage());
      List<Image> icons = new ArrayList<Image>();
      icons.add(new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/twe16.png")).getImage());
      icons.add(new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/twe20.png")).getImage());
      icons.add(new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/twe24.png")).getImage());
      icons.add(new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/twe32.png")).getImage());
      icons.add(new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/twe40.png")).getImage());
      icons.add(new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/twe48.png")).getImage());
      icons.add(new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/twe60.png")).getImage());
      icons.add(new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/twe64.png")).getImage());
      icons.add(new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/twe72.png")).getImage());
      icons.add(new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/twe80.png")).getImage());
      icons.add(new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/twe96.png")).getImage());
      icons.add(new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/twe256.png")).getImage());
      setIconImages(icons);

      setBackground(Color.lightGray);
      getContentPane().setLayout(new BorderLayout());
      setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

      workingArea.setLayout(new BorderLayout());

      // menubar and toolbar
      menubarAndToolbar.setLayout(new BorderLayout());
      menubarAndToolbar.add(menubar, BorderLayout.NORTH);
      menubarAndToolbar.add(toolbar, BorderLayout.CENTER);

      arrangeFrame();

      getContentPane().add(menubarAndToolbar, BorderLayout.NORTH);
      workingArea.add(bigSplit, BorderLayout.CENTER);
      getContentPane().add(workingArea, BorderLayout.CENTER);
      getContentPane().add(buttonComponents, BorderLayout.SOUTH);

      addWindowListener(createAppCloser());
      pack();
      // set default location to be centered and size to be almost maximized
      Dimension screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment()
         .getDefaultScreenDevice()
         .getDefaultConfiguration()
         .getBounds()
         .getSize();
      int xMinus = 24, yMinus = 50;
      setBounds(xMinus / 2, yMinus / 2, screenSize.width - xMinus, screenSize.height
                                                                   - yMinus);

      if (controller.getControllerSettings().shoudStartMaximized()) {
         setExtendedState(MAXIMIZED_BOTH);
      }

      ToolTipManager.sharedInstance().setEnabled(controller.getControllerSettings()
         .showTooltip());
   }

   public JaWEComponent getJaWEComponent() {
      return controller;
   }

   public JComponent getDisplay() {
      return null;
   }

   public void addMainComponent(String name, JComponent c) {
      String lanName = controller.getSettings()
         .getLanguageDependentString(name + "Label");
      JaWEManager.getInstance()
         .getLoggingManager()
         .debug("Added Main component " + lanName);
      String tip = controller.getSettings()
         .getLanguageDependentString("DoubleClickToMaximize")
                   + " "
                   + lanName
                   + ". "
                   + controller.getSettings()
                      .getLanguageDependentString("RightClickToAdd");
      mainComponents.insertTab(lanName, null, c, tip, mainComponents.getComponentCount());
   }

   public void removeMainComponent(JComponent c) {
      mainComponents.remove(c);
   }

   public void addToTreeComponents(String name, JComponent c) {
      String lanName = controller.getSettings()
         .getLanguageDependentString(name + "Label");
      JaWEManager.getInstance()
         .getLoggingManager()
         .debug("Added tree component " + lanName);
      String tip = controller.getSettings()
         .getLanguageDependentString("DoubleClickToMaximize")
                   + " "
                   + lanName
                   + ". "
                   + controller.getSettings()
                      .getLanguageDependentString("RightClickToAdd");
      treeComponents.insertTab(lanName, null, c, tip, treeComponents.getComponentCount());
   }

   public void removeTreeComponent(JComponent c) {
      treeComponents.remove(c);
   }

   public void addToSpecialComponents(String name, JComponent c) {
      String lanName = controller.getSettings()
         .getLanguageDependentString(name + "Label");
      JaWEManager.getInstance()
         .getLoggingManager()
         .debug("Added special component " + lanName);
      String tip = controller.getSettings()
         .getLanguageDependentString("DoubleClickToMaximize")
                   + " "
                   + lanName
                   + ". "
                   + controller.getSettings()
                      .getLanguageDependentString("RightClickToAdd");
      specialComponents.insertTab(lanName,
                                  null,
                                  c,
                                  tip,
                                  specialComponents.getComponentCount());
   }

   public void removeSpecialComponent(JComponent c) {
      specialComponents.remove(c);
   }

   public void addToOtherComponents(String name, JComponent c) {
      String lanName = controller.getSettings()
         .getLanguageDependentString(name + "Label");
      JaWEManager.getInstance()
         .getLoggingManager()
         .debug("Added other component " + lanName);
      String tip = controller.getSettings()
         .getLanguageDependentString("DoubleClickToMaximize")
                   + " "
                   + lanName
                   + ". "
                   + controller.getSettings()
                      .getLanguageDependentString("RightClickToAdd");
      otherComponents.insertTab(lanName,
                                null,
                                c,
                                tip,
                                otherComponents.getComponentCount());
   }

   public void removeOtherComponent(JComponent c) {
      otherComponents.remove(c);
   }

   public void addUpperStatusComponent(String name, JComponent c) {
      String lanName = controller.getSettings()
         .getLanguageDependentString(name + "Label");
      JaWEManager.getInstance()
         .getLoggingManager()
         .debug("Added upper status  component " + lanName);
      menubarAndToolbar.add(c, BorderLayout.SOUTH);
   }

   public void removeUpperStatusComponent(JComponent c) {
      menubarAndToolbar.remove(c);
   }

   public void addLowerStatusComponent(String name, JComponent c) {
      String lanName = controller.getSettings()
         .getLanguageDependentString(name + "Label");
      JaWEManager.getInstance()
         .getLoggingManager()
         .debug("Added lower status  component " + lanName);
      workingArea.add(c, BorderLayout.SOUTH);
   }

   public void removeLowerStatusComponent(JComponent c) {
      workingArea.remove(c);
   }

   /**
    * Creates AppCloser object.
    */
   protected WindowAdapter createAppCloser() {
      return new JaWEFrame.AppCloser();
   }

   // ************** APPCLOSER CLASS FOR CLOSING APPLICATION WINDOW ***************
   /**
    * To shutdown when run as an application.
    */
   public final class AppCloser extends WindowAdapter {
      public void windowClosing(WindowEvent e) {
         JaWEManager.getInstance()
            .getJaWEController()
            .getSettings()
            .getAction("Exit")
            .getAction()
            .actionPerformed(null);
      }
   }

   public void arrangeFrame() {
      String splitString = controller.getControllerSettings().getFrameSettings();
      int divLoc1 = controller.getControllerSettings().getFirstSmallDividerLocation();
      int divLoc2 = controller.getControllerSettings().getSecondSmallDividerLocation();
      int divLoc3 = controller.getControllerSettings().getMainDividerLocation();

      try {
         String[] temp = XMLUtil.tokenize(splitString, ";");
         String bigSplitString = temp[0];

         if (temp.length == 1) {
            String[] smallSplitData = XMLUtil.tokenize(bigSplitString, " ");

            // without split
            if (smallSplitData.length == 1) {
               bigSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
               bigSplit.add(getFrameComponentByName(smallSplitData[0]));
               // one split
            } else {
               int splitType = JSplitPane.HORIZONTAL_SPLIT;
               JComponent firstC = getFrameComponentByName(smallSplitData[0]);
               JComponent secondC = getFrameComponentByName(smallSplitData[2]);
               if (smallSplitData[1].equals("H"))
                  splitType = JSplitPane.VERTICAL_SPLIT;

               bigSplit = new JSplitPane(splitType, firstC, secondC);
               bigSplit.setDividerLocation(divLoc1);
            }
            // many splits
         } else {
            String smallSplit1String = temp[1];
            String smallSplit2String = temp[2];

            String[] smallSplit1Data = XMLUtil.tokenize(smallSplit1String, " ");
            int splitType = JSplitPane.HORIZONTAL_SPLIT;
            JComponent firstC = getFrameComponentByName(smallSplit1Data[0]);
            JComponent secondC = null;
            if (smallSplit1Data.length != 1) {
               if (smallSplit1Data[1].equals("H"))
                  splitType = JSplitPane.VERTICAL_SPLIT;
               secondC = getFrameComponentByName(smallSplit1Data[2]);
            }

            smallSplit1 = new JSplitPane(splitType);
            smallSplit1.add(firstC);
            if (smallSplit1Data.length != 1) {
               smallSplit1.add(secondC);
               smallSplit1.setDividerLocation(divLoc1);
            }

            String[] smallSplit2Data = XMLUtil.tokenize(smallSplit2String, " ");
            splitType = JSplitPane.HORIZONTAL_SPLIT;
            firstC = getFrameComponentByName(smallSplit2Data[0]);
            secondC = null;
            if (smallSplit2Data.length != 1) {
               if (smallSplit2Data[1].equals("H"))
                  splitType = JSplitPane.VERTICAL_SPLIT;
               secondC = getFrameComponentByName(smallSplit2Data[2]);
            }

            smallSplit2 = new JSplitPane(splitType);
            smallSplit2.add(firstC);
            if (smallSplit2Data.length != 1) {
               smallSplit2.add(secondC);
               smallSplit2.setDividerLocation(divLoc2);
            }

            splitType = JSplitPane.HORIZONTAL_SPLIT;
            if (bigSplitString.equals("H"))
               splitType = JSplitPane.VERTICAL_SPLIT;

            bigSplit = new JSplitPane(splitType, smallSplit1, smallSplit2);
            bigSplit.setDividerLocation(divLoc3);
         }
      } catch (Exception e) {
         JaWEManager.getInstance()
            .getLoggingManager()
            .error("JaWEFrame -> Can't customize frame! Using default!");

         smallSplit1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                      specialComponents,
                                      treeComponents);
         smallSplit1.setDividerLocation(180);

         smallSplit2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                      mainComponents,
                                      otherComponents);
         smallSplit2.setDividerLocation(400);

         bigSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, smallSplit1, smallSplit2);
         bigSplit.setDividerLocation(180);
      }
   }

   public void rearrangeFrame() {
      int mcc = mainComponents.getComponentCount();
      if (mcc == 0) {
         mainComponents.getParent().remove(mainComponents);
      }
      int tcc = treeComponents.getComponentCount();
      if (tcc == 0) {
         treeComponents.getParent().remove(treeComponents);
      }
      int scc = specialComponents.getComponentCount();
      if (scc == 0) {
         specialComponents.getParent().remove(specialComponents);
      }
      int occ = otherComponents.getComponentCount();
      if (occ == 0) {
         otherComponents.getParent().remove(otherComponents);
      }
      if (tcc==0 && scc==0) {
         bigSplit.setDividerLocation(0);
      } else if (mcc==0 && occ==0){
         int sw = (int) GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getDefaultScreenDevice()
            .getDefaultConfiguration()
            .getBounds()
            .getSize()
            .getWidth();
         bigSplit.setResizeWeight(1.0);
         bigSplit.setDividerLocation(sw);
      }
      bigSplit.setEnabled(false);
   }

   private JComponent getFrameComponentByName(String name) {
      if (name.equals("main"))
         return mainComponents;
      else if (name.equals("tree"))
         return treeComponents;
      else if (name.equals("special"))
         return specialComponents;
      else if (name.equals("other"))
         return otherComponents;

      return null;
   }

}
