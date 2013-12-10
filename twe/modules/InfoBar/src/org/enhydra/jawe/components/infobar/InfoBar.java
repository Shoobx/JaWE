/**
 * Miroslav Popov, Nov 29, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.components.infobar;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.XMLElementChoiceButton;
import org.enhydra.jawe.base.display.DisplayNameGenerator;
import org.enhydra.jawe.base.recentfiles.RecentFilesManager;
import org.enhydra.jawe.base.recentfiles.RecentFilesSettings;
import org.enhydra.shark.xpdl.elements.Package;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;

/**
 * @author Miroslav Popov
 *
 */
public class InfoBar extends JPanel implements JaWEComponentView {

   protected InfoBarController controller;
   
   protected JLabel fileNameLabel = new JLabel(" - ");
   protected JLabel packageNameLabel = new JLabel(" - ");
   protected JLabel processNameLabel = new JLabel(" - ");
   
   protected XMLElementChoiceButton pkgChoiceButton;
   protected XMLElementChoiceButton wpChoiceButton;
   protected JButton fileButton;

   protected RecentFilesManager recentF;
   
   public InfoBar(InfoBarController controller) {
      this.controller = controller;
   }
   
   public void configure() {
   }
   
   public void init() {
      try {
         recentF = new RecentFilesManager(new RecentFilesSettings());
      } catch (Exception e) {
      }
      
      ImageIcon curIc = controller.getInfoBarSettings().getPackageSelectionIcon();      
      pkgChoiceButton = new XMLElementChoiceButton(Package.class,controller, curIc, false, new String[] {"Id","Name"});
      pkgChoiceButton.setContentAreaFilled(false);
      pkgChoiceButton.setBorder(BorderFactory.createEmptyBorder());

      curIc = controller.getInfoBarSettings().getProcessSelectionIcon();
      wpChoiceButton = new XMLElementChoiceButton(WorkflowProcess.class,controller,curIc, false, new String[] {"Id","Name"});
      wpChoiceButton.setContentAreaFilled(false);
      wpChoiceButton.setBorder(BorderFactory.createEmptyBorder());
      
      curIc = controller.getInfoBarSettings().getFileIcon();
      fileButton = new JButton(curIc);
      fileButton.setContentAreaFilled(false);
      fileButton.setBorder(BorderFactory.createEmptyBorder());
      fileButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            JMenu menu = (JMenu)recentF.getView().getDisplay();
            JPopupMenu popup = menu.getPopupMenu();
            
//            if (JaWEManager.getInstance().getJaWEController().getMainPackage() != null) {
//               ExternalPackages eps = JaWEManager.getInstance().getJaWEController().getMainPackage()
//                     .getExternalPackages();
//               if (eps.size() != 0)
//                  popup.addSeparator();
//               for (int i = 0; i < eps.size(); i++) {
//                  JMenuItem mi = new JMenuItem("e " + ((ExternalPackage) eps.get(i)).getHref());
//                  mi.addActionListener(new ActionListener() {
//                     public void actionPerformed(ActionEvent e) {
//                        String filename = e.getActionCommand().substring(2);
//                        if (JaWEManager.getInstance().getJaWEController().tryToClosePackage(null)) {
//                           JaWEManager.getInstance().getJaWEController().openPackageFromFile(filename);
//                        }
//                     }
//                  });
//                  popup.add(mi);
//               }
//            }                        
//        
            popup.show(fileButton.getParent(), fileButton.getX(), fileButton.getY()+fileButton.getHeight());            
         }
      });
//      fileNameLabel.setIcon(curIc);
      
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      Color bkgCol;

      bkgCol = controller.getInfoBarSettings().getBackgroundColor();
      
      setBackground(bkgCol);      
      setBorder(BorderFactory.createEtchedBorder());
      add(pkgChoiceButton);
      add(Box.createHorizontalStrut(3));
      add(packageNameLabel);
      add(Box.createHorizontalStrut(10));
      add(wpChoiceButton);
      add(Box.createHorizontalStrut(3));
      add(processNameLabel);
      add(Box.createHorizontalStrut(10));      
      add(Box.createHorizontalGlue());
      add(fileButton);
      add(Box.createHorizontalStrut(3));
      add(fileNameLabel);
      add(Box.createHorizontalStrut(10));      
   }

   public JaWEComponent getJaWEComponent() {
      return controller;
   }

   public JComponent getDisplay() {
      return this;
   }
   
   public void setFileName (String fileName) {
      if (fileName == null || fileName.trim().equals(""))
         fileName = " - ";
      fileNameLabel.setText(fileName);
   }
   
   public void setPackageName (Package pkg) {
      String pkgName=" - ";
      if (pkg != null) {
         DisplayNameGenerator dng = JaWEManager.getInstance().getDisplayNameGenerator();
         pkgName=dng.getDisplayName(pkg);
      }
      packageNameLabel.setText(pkgName);
      pkgChoiceButton.setObjectIcon(pkg);
   }
   
   public void setProcessName (WorkflowProcess wp) {
      String procName=" - ";
      
      if (wp != null) {
         DisplayNameGenerator dng = JaWEManager.getInstance().getDisplayNameGenerator();
         procName = dng.getDisplayName(wp);
      }
      processNameLabel.setText(procName);
      wpChoiceButton.setObjectIcon(wp);
   }   
}