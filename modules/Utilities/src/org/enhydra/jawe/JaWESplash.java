package org.enhydra.jawe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

public class JaWESplash extends JWindow {

   public JaWESplash() {
      URL imageURL = null;
      String image = JaWEManager.getSplashScreenImage();
      if (image != null && !"".equals(image)) {
         imageURL = JaWEManager.class.getClassLoader().getResource(image);
      }

      JLabel l = null;
      if(imageURL!=null) {
         l=new JLabel(new ImageIcon(imageURL));
      } else {
         l=new JLabel("");
      }
      getContentPane().add(l, BorderLayout.CENTER);

      Utils.center(this, 100, 200);

      addMouseListener(new MouseAdapter() {
         public void mousePressed(MouseEvent e) {
            hideSplash();
         }
      });
      setVisible(true);
   }

   public void hideSplash () {
      if (isVisible()) {
         setVisible(false);
         dispose();
      }
   }
   
   public static JPanel getSplashPanel() {
      Hyperactive ha = new Hyperactive();
      
      JPanel p = new JPanel();
      p.setLayout(new BorderLayout());      
      p.setBackground(Color.WHITE);
      
      URL imageURL = null;

      // logo
      String image = JaWEManager.getSplashScreenImage();
      if (image != null && !"".equals(image)) {
         imageURL = JaWEManager.class.getClassLoader().getResource(image);
      }
      JLabel logo = null;
      if (imageURL!=null) {
         logo=new JLabel(new ImageIcon(imageURL), SwingConstants.CENTER);
      } else {
         logo=new JLabel("", SwingConstants.CENTER);
      }
         
      JEditorPane text = new JEditorPane();
      text.setAlignmentX(CENTER_ALIGNMENT);
      text.setAlignmentY(TOP_ALIGNMENT);
      text.addHyperlinkListener(ha);
      text.setContentType("text/html");
      text.setOpaque(false);
      String t = "<html><p align=center><b>Version:  " + JaWEManager.getVersion() + "-"+JaWEManager.getRelease()+"</b>"
            + "<br>Build Id: " + JaWEManager.getBuildEdition()+ JaWEManager.getBuildEditionSuffix() + "-"+JaWEManager.getBuildNo() + "<br><br>" 
            + JaWEManager.getAboutMsg();   

      text.setText(t);
      text.setEditable(false);      
      
      p.add(logo, BorderLayout.NORTH);
      p.add(text, BorderLayout.CENTER);

      return p;
   }
}

