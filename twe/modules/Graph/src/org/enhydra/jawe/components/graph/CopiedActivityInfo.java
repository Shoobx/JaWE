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
   
