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

import java.awt.Point;

/**
 *  Used to hold information to properly "graph paste" copied/cut selection.
 *
 *  @author Sasa Bojanic
 */
public class CopiedActivityInfo {

   private String parIdEA;
   private Point offsetPoint;
   
   public CopiedActivityInfo (String pId,Point off) {
      this.parIdEA=pId;
      this.offsetPoint=off;
   }
   
   public String getParIdEA () {
      return parIdEA;
   }
   
   public Point getOffsetPoint () {
      return offsetPoint;
   }
   
   public boolean equals (Object ai) {
//      System.err.println("... Comparing ainfo "+ai+", to ainfo "+this);
      if (ai instanceof CopiedActivityInfo) {
         CopiedActivityInfo ainfo=(CopiedActivityInfo)ai;
         return this.parIdEA.equals(ainfo.parIdEA) && this.offsetPoint.equals(ainfo.offsetPoint);
      }
      return false;
   }
   
   public String toString () {
      return "<-CopiedActivityInfo: parIdEA="+parIdEA+", offsetPoint="+offsetPoint+"->";
   }
   
}
   
