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
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *  Used to hold information to properly "graph paste" copied/cut selection.
 *
 *  @author Sasa Bojanic
 */
public class CopyOrCutInfo {

   private Point referencePoint;
   private Map actInfoToActRect;
   private Point pasteTo;
   private Map graphToOffsetPoint=new HashMap();
   
   public CopyOrCutInfo (Point refPoint,Map ainfo) {
      this.referencePoint=refPoint;
      this.actInfoToActRect=ainfo;
   }

   public Point getReferencePoint () {
      return referencePoint;
   }
   
   public void setPastePoint (Point pastePoint) {
      this.pasteTo=pastePoint;
   }
   
   public Point getPastePoint () {
      return pasteTo;
   }
   
   public Point getOffsetPoint (Graph g) {
//System.err.println("Asking for offset point for graph "+g);
//System.err.println("            ... offset map = "+graphToOffsetPoint);
      Point offset=(Point)graphToOffsetPoint.get(g);
      if (offset==null) {
//System.err.println("                      ......new offset point created");      
         offset=new Point(10,10);
         graphToOffsetPoint.put(g,offset);
      }
      return offset;
   }
   
   public void incrementOffsetPoint (Graph g) {
//System.err.println("Incrementing offset point for graph "+g);
      Point offset=getOffsetPoint(g);
      offset=new Point(offset.x+10, offset.y+10);                  
      if (offset.x > 150 || offset.y > 150) {
//System.err.println("            .........Reseting offset point");
         offset = new Point(10, 10);         
      }      
      graphToOffsetPoint.put(g,offset);
   }
   
   public Rectangle getActivityBounds (CopiedActivityInfo ai) {
      Iterator it=actInfoToActRect.entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry me=(Map.Entry)it.next();
         CopiedActivityInfo cai=(CopiedActivityInfo)me.getKey();
//         System.err.println("  ..... . Comparing ai "+ai+" to cai "+cai);
         Rectangle r=(Rectangle)me.getValue();
//         System.err.println("    ... gacomp");
         if (ai.getLaneId().equals(cai.getLaneId())) {
//            System.err.println("    ... gabsuc1");
            if (ai.getOffsetPoint().equals(cai.getOffsetPoint())) {
//               System.err.println("    ... gabsuc2");
               return r;
            }
         }
      }
      return null;
   }
   
   public void removeGraphInfo (Graph g) {
      graphToOffsetPoint.remove(g);
   }
   
   public String toString () {
      String ret="---------CopyOrCutInfo----------------";
      ret+="\nReferencePoint="+referencePoint;
      ret+="\nPasteTo="+pasteTo;
      ret+="\nActInfoToRectg="+actInfoToActRect;
      ret+="--------- End of CopyOrCutInfo----------------";
      return ret;
   }
}
