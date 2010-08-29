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

package org.enhydra.jawe.components.graph;


/**
 * Used to define Port object in graph.
 *
 * @author Sasa Bojanic   
 */
public class DefaultGraphPort extends GraphPortInterface {


   protected String type;
   /**
    * Creates activity with given userObject. Also creates default port
    * for holding activity transitions.
    */
   public DefaultGraphPort(String name,String type) {
      super(name);
      this.type=type;
   }

   public GraphActivityInterface getActivity () {
      return (GraphActivityInterface)getParent();
   }
   
   public String getType () {
      return type;
   }
   
}
