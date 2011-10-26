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

/**
 * Miroslav Popov, Dec 20, 2005 miroslav.popov@gmail.com
 */
package org.enhydra.jawe.base.logger;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Miroslav Popov
 */
public class LoggingManager {

   private static final String defaultLogChannel = "JaWE";

   private static Logger logger = Logger.getLogger(LoggingManager.class.getName());

   /**
    * Log a message object with the <i>ERROR</i> Level.
    * 
    * @param msg the message to log.
    */
   public void error(String msg) {
      error(defaultLogChannel, msg);
   }

   /**
    * Log a message object with the <i>ERROR</i> Level.
    * 
    * @param msg the message to log.
    * @param ex the exception to log, including its stack trace.
    */
   public void error(String msg, Throwable ex) {
      error(defaultLogChannel, msg, ex);
   }

   /**
    * Log a message object with the <i>ERROR</i> Level.
    * 
    * @param channel the log channel to be used for logging.
    * @param msg the message to log.
    */
   public void error(String channel, String msg) {
      // System.err.println(msg);
      logger.log(Level.SEVERE, msg);
   }

   /**
    * Log a message object with the <i>ERROR</i> Level.
    * 
    * @param channel the log channel to be used for logging.
    * @param msg the message to log.
    * @param ex the exception to log, including its stack trace.
    */
   public void error(String channel, String msg, Throwable ex) {
      // System.err.println(msg);
      // ex.printStackTrace();
      logger.log(Level.SEVERE, msg, ex);
   }

   /**
    * Log a message object with the <i>WARN</i> Level.
    * 
    * @param msg the message to log.
    */
   public void warn(String msg) {
      warn(defaultLogChannel, msg);
   }

   /**
    * Log a message object with the <i>WARN</i> Level.
    * 
    * @param msg the message to log.
    * @param ex the exception to log, including its stack trace.
    */
   public void warn(String msg, Throwable ex) {
      warn(defaultLogChannel, msg, ex);
   }

   /**
    * Log a message object with the <i>WARN</i> Level.
    * 
    * @param channel the log channel to be used for logging.
    * @param msg the message to log.
    */
   public void warn(String channel, String msg) {
      // System.err.println(msg);
      logger.log(Level.WARNING, msg);
   }

   /**
    * Log a message object with the <i>WARN</i> Level.
    * 
    * @param channel the log channel to be used for logging.
    * @param msg the message to log.
    * @param ex the exception to log, including its stack trace.
    */
   public void warn(String channel, String msg, Throwable ex) {
      // System.err.println(msg);
      // ex.printStackTrace();
      logger.log(Level.WARNING, msg, ex);
   }

   /**
    * Log a message object with the <i>INFO</i> Level.
    * 
    * @param msg the message to log.
    */
   public void info(String msg) {
      info(defaultLogChannel, msg);
   }

   /**
    * Log a message object with the <i>INFO</i> Level.
    * 
    * @param msg the message to log.
    * @param ex the exception to log, including its stack trace.
    */
   public void info(String msg, Throwable ex) {
      info(defaultLogChannel, msg, ex);
   }

   /**
    * Log a message object with the <i>INFO</i> Level.
    * 
    * @param channel the log channel to be used for logging.
    * @param msg the message to log.
    */
   public void info(String channel, String msg) {
      // System.out.println(msg);
      logger.log(Level.INFO, msg);
   }

   /**
    * Log a message object with the <i>INFO</i> Level.
    * 
    * @param channel the log channel to be used for logging.
    * @param msg the message to log.
    * @param ex the exception to log, including its stack trace.
    */
   public void info(String channel, String msg, Throwable ex) {
      // System.out.println(msg);
      // ex.printStackTrace();
      logger.log(Level.INFO, msg, ex);
   }

   /**
    * Log a message object with the <i>DEBUG</i> level.
    * 
    * @param msg the message to log.
    */
   public void debug(String msg) {
      debug(defaultLogChannel, msg);
   }

   /**
    * Log a message object with the <i>DEBUG</i> level.
    * 
    * @param msg the message to log.
    * @param ex the exception to log, including its stack trace.
    */
   public void debug(String msg, Throwable ex) {
      debug(defaultLogChannel, msg, ex);
   }

   /**
    * Log a message object with the <i>DEBUG</i> level.
    * 
    * @param channel the log channel to be used for logging.
    * @param msg the message to log.
    */
   public void debug(String channel, String msg) {
      // System.err.println(msg);
      logger.log(Level.FINEST, msg);
   }

   /**
    * Log a message object with the <i>DEBUG</i> level.
    * 
    * @param channel the log channel to be used for logging.
    * @param msg the message to log.
    * @param ex the exception to log, including its stack trace.
    */
   public void debug(String channel, String msg, Throwable ex) {
      // System.err.println(msg);
      // ex.printStackTrace();
      logger.log(Level.FINEST, msg, ex);
   }

}
