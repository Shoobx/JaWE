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

package org.enhydra.jawe.components.graph;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Holds information to properly "graph paste" copied/cut selection.
 */
public class CopyOrCutInfo {

   /** The reference point for pasting. */
   private Point referencePoint;

   /**
    * Information about graphical objects being copied. Keys are
    * {@link CopiedActivityOrArtifactInfo} instances and values are Rectangle instances
    * describing the positions of activity/artifact.
    */
   private Map actInfoToActRect;

   /** The point where the graph objects should be paste. */
   private Point pasteTo;

   /**
    * Information about the default offset point for the graphs. Keys are the
    * {@link Graph} objects and values are offset Point objects. This information is used
    * in the case non-graph paste is performed (no paste TO point information).
    */
   private Map graphToOffsetPoint = new HashMap();

   /**
    * Creates new info object.
    * 
    * @param refPoint
    * @param ainfo
    */
   public CopyOrCutInfo(Point refPoint, Map ainfo) {
      this.referencePoint = refPoint;
      this.actInfoToActRect = ainfo;
   }

   /**
    * @return The reference point for performing paste operation.
    */
   public Point getReferencePoint() {
      return referencePoint;
   }

   /**
    * Sets the paste point information.
    * @param pastePoint The point where the selection should be paste.
    */
   public void setPastePoint(Point pastePoint) {
      this.pasteTo = pastePoint;
   }

   /**
    * @return The paste point.
    */
   public Point getPastePoint() {
      return pasteTo;
   }

   /**
    * Returns the offset point information for the given graph.
    * @param g {@link Graph} instance.
    * @return The offset point.
    */
   public Point getOffsetPoint(Graph g) {
      // System.err.println("Asking for offset point for graph "+g);
      // System.err.println("            ... offset map = "+graphToOffsetPoint);
      Point offset = (Point) graphToOffsetPoint.get(g);
      if (offset == null) {
         // System.err.println("                      ......new offset point created");
         offset = new Point(10, 10);
         graphToOffsetPoint.put(g, offset);
      }
      return offset;
   }

   /**
    * Increments offset point information for the given graph.
    * @param g The {@link Graph} instance.
    */
   public void incrementOffsetPoint(Graph g) {
      // System.err.println("Incrementing offset point for graph "+g);
      Point offset = getOffsetPoint(g);
      offset = new Point(offset.x + 10, offset.y + 10);
      if (offset.x > 150 || offset.y > 150) {
         // System.err.println("            .........Reseting offset point");
         offset = new Point(10, 10);
      }
      graphToOffsetPoint.put(g, offset);
   }

   /**
    * Returns the bounds of copied activity or artifact.
    * @param ai {@link CopiedActivityOrArtifactInfo} instance holding information about activity/artifact.
    * @return The bounds of copied activity/artifact.
    */
   public Rectangle getActivityBounds(CopiedActivityOrArtifactInfo ai) {
      Iterator it = actInfoToActRect.entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry me = (Map.Entry) it.next();
         CopiedActivityOrArtifactInfo cai = (CopiedActivityOrArtifactInfo) me.getKey();
         // System.err.println("  ..... . Comparing ai "+ai+" to cai "+cai);
         Rectangle r = (Rectangle) me.getValue();
         // System.err.println("    ... gacomp");
         if (ai.getLaneId().equals(cai.getLaneId())) {
            // System.err.println("    ... gabsuc1");
            if (ai.getOffsetPoint().equals(cai.getOffsetPoint())) {
               // System.err.println("    ... gabsuc2");
               return r;
            }
         }
      }
      return null;
   }

   /**
    * Removes the offset point information for the given graph.
    * @param g {@link Graph} instance.
    */
   public void removeGraphInfo(Graph g) {
      graphToOffsetPoint.remove(g);
   }

   public String toString() {
      String ret = "---------CopyOrCutInfo----------------";
      ret += "\nReferencePoint=" + referencePoint;
      ret += "\nPasteTo=" + pasteTo;
      ret += "\nActInfoToRectg=" + actInfoToActRect;
      ret += "--------- End of CopyOrCutInfo----------------";
      return ret;
   }
}
