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

import java.net.URL;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class Hyperactive implements HyperlinkListener {
   private String hoveredURLStr;

   public void hyperlinkUpdate(HyperlinkEvent e) {
      if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
         URL url = e.getURL();
         if (url != null) {
            hoveredURLStr = url.toExternalForm();
         } else {
            // error case
            hoveredURLStr = null;
         }

      } else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
         hoveredURLStr = null;
      } else if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED && hoveredURLStr != null) {
         String startCommand = System.getProperty("path.to.start");
         String url = hoveredURLStr;
         if (null != startCommand) {
            if (System.getProperty("path.separator").equals(";")) {
               url = "\"" + url + "\"";
            }
         } else {
             if (System.getProperty("path.separator").equals(":")) {
                 startCommand = guessWhereToStart();
             } else {
                 startCommand = "cmd /c start";
                 url = "\"" + url + "\"" + " \"" + url + "\"";
             }
         }
         try {
            Runtime.getRuntime().exec(startCommand + " " + url);
         } catch (Throwable t) {
            t.printStackTrace();
         }
      }
   }
   
   public static String guessWhereToStart() {
       try {
           if (Runtime.getRuntime().exec( new String[] {"which", "gnome-open"}).waitFor() == 0) {
               return "gnome-open";
           }
           if (Runtime.getRuntime().exec( new String[] {"which", "kfmclient"}).waitFor() == 0) {
               return "kfmclient exec";
           }
       } catch (Throwable e) {
       }
//     javax.swing.JOptionPane.showMessageDialog(null,
//     "Coudn't find application to open document with.", "Error",
//     javax.swing.JOptionPane.ERROR_MESSAGE);
       return "";
   }
   
}
