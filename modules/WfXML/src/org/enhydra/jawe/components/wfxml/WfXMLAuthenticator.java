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

/* WfXMLAuthenticator.java */
package org.enhydra.jawe.components.wfxml;

import java.awt.GridLayout;
import java.awt.Window;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

import javax.swing.*;

/**
 * WfXMLAuthenticator
 * 
 * @author V.Puskas
 * @version 0.1
 */
public class WfXMLAuthenticator extends Authenticator {

   private Window parent;

   private JTextField jtf;

   private JTextField jpf;

   private JPanel jp;

   private JLabel jl;

   /**
    * @param w
    */
   public WfXMLAuthenticator(Window w) {
      super();
      parent = w;
      jtf = new JTextField();
      jpf = new JPasswordField();
      jl = new JLabel();
      jp = new JPanel(new GridLayout(3, 2, 5, 3));
      jp.add(new JLabel("Requesting Prompt:"));
      jp.add(jl);
      jp.add(new JLabel("Username:"));
      jp.add(jtf);
      jp.add(new JLabel("Password:"));
      jp.add(jpf);
   }

   protected PasswordAuthentication getPasswordAuthentication() {
      jl.setText(this.getRequestingPrompt());
      int a = JOptionPane.showConfirmDialog(parent,
                                            jp,
                                            "Enter credentials, please!",
                                            JOptionPane.OK_CANCEL_OPTION,
                                            JOptionPane.QUESTION_MESSAGE);
      //System.err.println("answer:\t"+a+"\njtfu:\t"+jtf.getText()+"\njtfp:\t"+jpf.getText());
      if (JOptionPane.OK_OPTION == a) {
         return new PasswordAuthentication(jtf.getText(), jpf.getText()
            .toCharArray());
      }
      
      return null;
   }
}