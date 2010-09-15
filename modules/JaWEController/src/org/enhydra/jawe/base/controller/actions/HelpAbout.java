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

package org.enhydra.jawe.base.controller.actions;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.Hyperactive;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.JaWESplash;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.base.controller.JaWEController;

public class HelpAbout extends ActionBase {

   private JDialog aboutDlg;

   public HelpAbout (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }
   
   public void enableDisableAction() {      
   }
   
   public void actionPerformed(ActionEvent e) {
      JaWEController jcon = (JaWEController) jawecomponent;

      if (aboutDlg == null) {
         String title = jcon.getSettings().getLanguageDependentString("AboutFrameTitle");
         aboutDlg = new JaWEAboutDialog(jcon.getJaWEFrame(), title);
         aboutDlg.setVisible(true);
      } else {
         aboutDlg.setVisible(true);
      }
   }
   
   public class JaWEAboutDialog extends JDialog {
      protected JButton okButton;
      private JPanel licencePnl;
      private JTabbedPane tabContainer;
      private JToolBar buttonToolbar;

      private ActionListener actionHandler = new ActionHandler();

      public void showAbout(Frame owner, String title){
         JaWEAboutDialog about =new JaWEAboutDialog(owner, title);
         about.setVisible(true);
         about=null;
      }

      protected JaWEAboutDialog(Frame owner, String title) {
         super(owner, title, true);
         createUI();
         setResizable(false);
         pack();
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         Dimension winsize = this.getSize();
         setLocation(screenSize.width/2 - (winsize.width/2),
                     screenSize.height/2 - (winsize.height/2));
         setLocationRelativeTo(owner);
      }

      private void createUI(){
         createButtonPane();
         if (JaWEManager.showLicenseInfo()) {
            createTabbedPane();         
            getContentPane().add(tabContainer,BorderLayout.CENTER);
         } else {
            getContentPane().add(JaWESplash.getSplashPanel(),BorderLayout.CENTER);
         }
         getContentPane().add(buttonToolbar,BorderLayout.SOUTH);
         getRootPane().setDefaultButton(okButton);
      }

      private void createButtonPane(){
         okButton = new JButton("   OK   ");
         okButton.addActionListener(actionHandler);
         buttonToolbar = new JToolBar(SwingConstants.HORIZONTAL);
         buttonToolbar.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
         buttonToolbar.setFloatable(false);
         buttonToolbar.add(Box.createHorizontalGlue());
         buttonToolbar.add(okButton);
         buttonToolbar.add(Box.createHorizontalGlue());
      }

      private void createTabbedPane(){
         createLicencePane();
         tabContainer = new JTabbedPane();
         tabContainer.addTab(ResourceManager.getLanguageDependentString("GeneralKey"), JaWESplash.getSplashPanel());
         tabContainer.addTab(ResourceManager.getLanguageDependentString("LicenseKey"), licencePnl);
      }

      private void createLicencePane() {
         licencePnl = new JPanel(new BorderLayout());
         
         JScrollPane licenceS = new JScrollPane();
         licenceS.setPreferredSize(new Dimension(400, 300));
         
         licenceS.setAlignmentX(Component.LEFT_ALIGNMENT);
         licenceS.setAlignmentY(Component.TOP_ALIGNMENT);

         JTextArea licenceText = new JTextArea();         

         licenceText.setTabSize(4);         
         licenceText.getCaret().setDot(0);
//         Font f = licenceText.getFont();
//         f.
//         licenceText.setfont
         licenceText.setLineWrap(true);
         licenceText.setWrapStyleWord(true);

         licenceText.setEnabled(true);
         licenceText.setEditable(false);

         licenceS.setViewportView(licenceText);
         
         String textL = "";
         try {
            URL u = ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/License.txt");
            URLConnection ucon = u.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(ucon.getInputStream()));

            String temp;
            while ((temp = br.readLine()) != null) {
               textL += temp + "\n";
            }
         } catch (Exception e) {
         }
         
         licenceText.append(textL);    
         licenceText.getCaret().setDot(0);
         
         Hyperactive ha = new Hyperactive();
         JEditorPane text = new JEditorPane();
         text.setAlignmentX(LEFT_ALIGNMENT);
         text.setAlignmentY(TOP_ALIGNMENT);
         text.addHyperlinkListener(ha);
         text.setContentType("text/html");
         text.setOpaque(false);
         String t=JaWEManager.getAdditionalLicenseText();               
         text.setText(t);
         text.setEditable(false);
         
         
         licencePnl.add(licenceS, BorderLayout.CENTER);
         licencePnl.add(text, BorderLayout.SOUTH);
      }

      // Close on escape
      protected JRootPane createRootPane() {
         KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
         JRootPane _rootPane = new JRootPane();
         _rootPane.registerKeyboardAction(new ActionListener() {
                  public void actionPerformed(ActionEvent actionEvent) {
                     setVisible(false);
                  }
               }, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
         return _rootPane;
      }

      private class ActionHandler implements ActionListener {
         public void actionPerformed(ActionEvent e) {
            if (e.getSource() == okButton) {
               dispose();
            }
         }
      }
   }
}
