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
