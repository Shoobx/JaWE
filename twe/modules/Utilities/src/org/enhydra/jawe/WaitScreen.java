package org.enhydra.jawe;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

/**
 * Used to view some kind of splash screen.
 *
 * @author Sasa Bojanic
 * @version 1.0
 */
public class WaitScreen extends JWindow {

   JLabel splashI=new JLabel();
   JLabel splashM=new JLabel();
   JLabel splashT=new JLabel();
   JPanel p=new JPanel();
   public WaitScreen (Window parent) {
      super(parent);

      getContentPane().add(p,BorderLayout.CENTER);
      splashI.setLayout(new BorderLayout());
      splashI.add(splashT, BorderLayout.NORTH);
      splashI.add(splashM, BorderLayout.CENTER);
      splashI.setBorder(BorderFactory.createRaisedBevelBorder());
      p.add(splashI);
   }

   public void show (Icon splashIcon,String splashTitle,String splashMessage) {
      String pre="<html><font size=4 face=\"sans-serif\">";// color=\"blue\">";
      String post="</font></html>";
      if (splashTitle==null) splashTitle="";
      if (splashMessage==null) splashMessage="";
      int pw=getPreferredWidth(splashTitle,splashMessage);
      splashTitle=pre+splashTitle+post;
      pre="<html><font size=4 face=\"sans-serif\">";// color=\"red\">";
      splashMessage=pre+splashMessage+post;

      try {
         splashI.setIcon(splashIcon);
      } catch (Exception ex) {}
      splashT.setText(splashTitle);
      splashT.setHorizontalAlignment(SwingConstants.CENTER);
      splashM.setText(splashMessage);
      splashM.setHorizontalAlignment(SwingConstants.CENTER);
      try {
         if (pw>600) pw=600;
         splashI.setPreferredSize(new Dimension(pw,
               (int)((3+pw/600)*getFontMetrics(getFont()).getHeight()*1.5)));
         pack();
         Utils.center(this,10,10);
         setVisible(true);
         p.paintImmediately(p.getBounds());
      } catch (Exception ex) {
         setVisible(false);
      }

   }

   protected int getPreferredWidth (String s1,String s2) {
      int w1=10*s1.length();
      int w2=10*s2.length();
      Font f=getFont();
      if (f==null) {
         f=GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()[0];
      }
      try {
         w1=getFontMetrics(f).stringWidth(s1);
         w2=getFontMetrics(f).stringWidth(s2);
      } catch (Exception ex) {}
      
      if (w1>w2) {
         return w1+50;
      }
      
      return w2+50;
   }

}

