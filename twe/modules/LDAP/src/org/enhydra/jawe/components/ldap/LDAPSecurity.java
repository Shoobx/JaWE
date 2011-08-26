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


import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;

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
