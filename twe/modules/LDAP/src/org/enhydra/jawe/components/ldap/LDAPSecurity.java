/* Security.java
 *
 * Authors:
 * Stefanovic Nenad  chupo@iis.ns.ac.yu
 * Bojanic Sasa      sasaboy@neobee.net
 * Puskas Vladimir   vpuskas@eunet.yu
 * Pilipovic Goran   zboniek@uns.ac.yu
 *
 */

package org.enhydra.jawe.components.ldap;


import org.enhydra.shark.xpdl.XMLAttribute;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.XMLElement;

 // This is not a real WfMC DTD element, but it is introduced to help
public class LDAPSecurity extends XMLComplexElement {

   public LDAPSecurity (XMLElement parent) {
      super(parent, true);      
   }

   protected void fillStructure() {
      XMLAttribute level = new XMLAttribute(this, "Level", true, new String[] { "Anonymous", "UserAndPassword"}, 0);
      
      XMLAttribute userDN = new XMLAttribute(this, "UserDN", false);      
      XMLAttribute password = new XMLAttribute(this, "Password", false);
      
      super.add(level);
      super.add(userDN);
      super.add(password);
   }
}
