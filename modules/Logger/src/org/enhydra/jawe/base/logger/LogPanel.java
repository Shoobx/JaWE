package org.enhydra.jawe.base.logger;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import org.enhydra.jawe.BarFactory;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentView;

/**
 *  Used to display debug information.
 *
 *  @author Sasa Bojanic
 */
public class LogPanel extends JPanel implements JaWEComponentView, ActionListener {

   public void configure () {      
   }

   protected LoggingManagerComponent controller;
   public LogPanel (LoggingManagerComponent controller) {
      this.controller=controller;
   }

   protected JTextArea jta;
   protected JButton clear;

   public int getTextSize() {
      if (jta == null) return 0;
      return jta.getText().length();
   }
   
   public void appendText (String txt) {
      if (jta==null) return;
      jta.append(txt);
   }

   public void clearText () {
      jta.setText("");
   }

   public void init () {
      setBorder(BorderFactory.createEtchedBorder());
      setLayout(new BorderLayout());
      
      
      JToolBar toolbar = BarFactory.createToolbar("defaultToolbar", controller);         
      toolbar.setFloatable(false);
      if (toolbar.getComponentCount() > 0)
         add(toolbar, BorderLayout.NORTH);

      JScrollPane jsp=new JScrollPane();
      jsp.setAlignmentX(Component.LEFT_ALIGNMENT);
      jsp.setAlignmentY(Component.TOP_ALIGNMENT);

      jta=new JTextArea();

      jta.setTabSize(4);
      jta.getCaret().setDot(0);
      jta.setLineWrap(false);

      jta.setEnabled(true);
      jta.setEditable(false);

      jsp.setViewportView(jta);
      add(jsp,BorderLayout.CENTER);
   }

   public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == clear) {
         clearText();
      }
   }

   public JaWEComponent getJaWEComponent () {
      return controller;
   }

   public JComponent getDisplay () {
      return this;
   }

}
