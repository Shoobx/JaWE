/**
 * Miroslav Popov, Dec 1, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe;


/**
 * @author Miroslav Popov
 *
 */
public interface Settings {
   
   Object getSetting(String key);

   String getSettingString (String key);
   
   int getSettingInt (String key);
   
   boolean getSettingBoolean (String key);
   
   double getSettingDouble (String key);
   
   String getLanguageDependentString (String nm);
   
}
