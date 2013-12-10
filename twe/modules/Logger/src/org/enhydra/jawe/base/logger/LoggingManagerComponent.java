package org.enhydra.jawe.base.logger;

import java.text.SimpleDateFormat;
import java.util.List;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.elements.Package;

/**
 *  Used to log information.
 *
 *  @author Sasa Bojanic
 */
public class LoggingManagerComponent extends LoggingManager implements JaWEComponent {

   // Copy these two lines in Component manager at the end of init()    
   // ((LoggingManagerComponent)JaWEManager.getInstance().getLoggingManager()).init();
   // registerComponents(((LoggingManagerComponent)JaWEManager.getInstance().getLoggingManager()));
   //
   // and change togwebasic.property
   // from :
   // LoggingManager.Class=org.enhydra.jawe.base.logger.LoggingManager
   // to :
   // LoggingManager.Class=org.enhydra.jawe.base.logger.LoggingManagerComponent
   
   
   protected String type = JaWEComponent.OTHER_COMPONENT;
   
   public final static String DATE_TIME_PATTERN="yyyy.MM.dd HH:mm:ss:SSS";
   public static SimpleDateFormat sdf=new SimpleDateFormat(DATE_TIME_PATTERN);

   protected LogPanel panel;

   protected LoggerSettings settings;

   public LoggingManagerComponent() {
      settings = new LoggerSettings();
      settings.init(this);
   }
   
   public JaWEComponentSettings getSettings() {
      return settings;
   }
   
   public void init () {      
      panel=new LogPanel(this);
      panel.configure();
      panel.init();
   }

   /**
    * Log a message object with the <i>ERROR</i> Level.
    *
    * @param channel the log channel to be used for logging.
    * @param msg the message to log.
    */
   public void error (String channel,String msg){
      if (panel!=null) panel.appendText(updateMessage(msg));
      System.err.println(msg);
   }

   /**
    * Log a message object with the <i>ERROR</i> Level.
    *
    * @param channel the log channel to be used for logging.
    * @param msg the message to log.
    * @param ex the exception to log, including its stack trace.
    */
   public void error (String channel,String msg,Throwable ex) {
      if (panel!=null) panel.appendText(updateMessage(msg));
      System.err.println(msg);
      ex.printStackTrace();
   }

   /**
    * Log a message object with the <i>WARN</i> Level.
    *
    * @param channel the log channel to be used for logging.
    * @param msg the message to log.
    */
   public void warn (String channel,String msg) {
      if (panel!=null) panel.appendText(updateMessage(msg));
      System.err.println(msg);
   }

   /**
    * Log a message object with the <i>WARN</i> Level.
    *
    * @param channel the log channel to be used for logging.
    * @param msg the message to log.
    * @param ex the exception to log, including its stack trace.
    */
   public void warn (String channel,String msg,Throwable ex) {
      if (panel!=null) panel.appendText(updateMessage(msg));
      System.err.println(msg);
      ex.printStackTrace();
   }

   /**
    * Log a message object with the <i>INFO</i> Level.
    *
    * @param channel the log channel to be used for logging.
    * @param msg the message to log.
    */
   public void info (String channel,String msg) {
      if (panel!=null) panel.appendText(updateMessage(msg));
      System.out.println(msg);
   }

   /**
    * Log a message object with the <i>INFO</i> Level.
    *
    * @param channel the log channel to be used for logging.
    * @param msg the message to log.
    * @param ex the exception to log, including its stack trace.
    */
   public void info (String channel,String msg,Throwable ex) {
      if (panel!=null) panel.appendText(updateMessage(msg));
      System.out.println(msg);
      ex.printStackTrace();
   }

   /**
    * Log a message object with the <i>DEBUG</i> level.
    *
    * @param channel the log channel to be used for logging.
    * @param msg the message to log.
    */
   public void debug (String channel,String msg) {
      if (panel!=null) panel.appendText(updateMessage(msg));
      System.err.println(msg);
   }

   /**
    * Log a message object with the <i>DEBUG</i> level.
    *
    * @param channel the log channel to be used for logging.
    * @param msg the message to log.
    * @param ex the exception to log, including its stack trace.
    */
   public void debug (String channel,String msg,Throwable ex) {
      if (panel!=null) panel.appendText(updateMessage(msg));
      System.err.println(msg);
      ex.printStackTrace();
   }

   public JaWEComponentView getView () {
      return panel;
   }

   public String getType() {
      return type;
   }   

   public void setType(String type) {
      this.type = type; 
   }
   
   public String getName () {
      return "LoggingManagerComponent";
   }
   
   public boolean adjustXPDL (Package pkg) {
      return false;
   }

   public List checkValidity (XMLElement el,boolean fullCheck) {
      return null;
   }

   public boolean canCreateElement(XMLCollection col) {
      return true;
   }

   public boolean canInsertElement(XMLCollection col, XMLElement el) {
      return true;
   }

   public boolean canModifyElement(XMLElement el) {
      return true;
   }

   public boolean canRemoveElement(XMLCollection col, XMLElement el) {
      return true;
   }

   public boolean canDuplicateElement(XMLCollection col, XMLElement el) {
      return true;
   }

   public boolean canRepositionElement(XMLCollection col, XMLElement el) {
      return true;
   }
   
   protected String updateMessage (String msg) {
      settings.adjustActions();
      
      return "\n"+sdf.format(new java.util.Date())+":"+msg;
   }

   public void setUpdateInProgress(boolean inProgress) {
   }
   
   public boolean isUpdateInProgress() {
      return false;
   }
   
}
