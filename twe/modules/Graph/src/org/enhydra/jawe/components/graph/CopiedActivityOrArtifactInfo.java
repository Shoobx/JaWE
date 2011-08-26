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
 * Holds information to properly "graph paste" copied/cut selection.
 * 
 */
public class CopiedActivityOrArtifactInfo {

   /** The id of the lane activity/artifact belongs to. */
   private String laneId;

   /** An offset point of activity/artifact that is being copied. */
   private Point offsetPoint;

   /**
    * Creates new info object.
    * 
    * @param laneId The id of lane.
    * @param off The offset.
    */
   public CopiedActivityOrArtifactInfo(String laneId, Point off) {
      this.laneId = laneId;
      this.offsetPoint = off;
   }

   /**
    * @return The Id of lane copied activity/artifact belongs to.
    */
   public String getLaneId() {
      return laneId;
   }

   /**
    * @return The offset point of activity/artifact that is being copied.
    */
   public Point getOffsetPoint() {
      return offsetPoint;
   }

   public boolean equals(Object ai) {
      // System.err.println("... Comparing ainfo "+ai+", to ainfo "+this);
      if (ai instanceof CopiedActivityOrArtifactInfo) {
         CopiedActivityOrArtifactInfo ainfo = (CopiedActivityOrArtifactInfo) ai;
         return this.laneId.equals(ainfo.laneId)
                && this.offsetPoint.equals(ainfo.offsetPoint);
      }
      return false;
   }

   public String toString() {
      return "<-CopiedActivityOrArtifactInfo: laneId="
             + laneId + ", offsetPoint=" + offsetPoint + "->";
   }

}
