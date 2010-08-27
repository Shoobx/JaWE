package org.enhydra.jawe.components.graph;


/**
 * Info structure for start/end element description.
 *
 * @author Sasa Bojanic
 */
import java.awt.Point;

import org.enhydra.shark.xpdl.elements.ExtendedAttribute;

public final class StartEndDescription {
   
   private String actSetId=null;
   private String participantId=FreeTextExpressionParticipant.getInstance().getId();
   private String actId=null;
   private Point offset=new Point(0,0);
   private String transitionStyle=GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_ORTHOGONAL;
   private String type=GraphEAConstants.START_TYPE_DEFAULT;
   private String eaname;
   
   boolean isStart=true;
   
   public StartEndDescription () {
      
   }
   
   public StartEndDescription (ExtendedAttribute ea) {
         this(ea.getName(),ea.getVValue());
   }      
    
   public StartEndDescription (String eaname,String eaval) {
      try {
         setEAName(eaname);
         
         //      System.out.println("ean="+eaname+", eaval="+eaval);
//         String[] startOrEndD=Utils.tokenize(eaval, ",");
         int ind=0;
         int ind2=0;
         if (eaname.indexOf("BLOCK")>=0) {
            ind=eaval.indexOf(GraphEAConstants.EA_PART_ACTIVITY_SET_ID+"=");
            ind2=eaval.indexOf(","+GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID+"=", ind);
            String asId=eaval.substring(ind+(GraphEAConstants.EA_PART_ACTIVITY_SET_ID+"=").length(),ind2);
            setActSetId(asId);
            ind=ind2+1;
         }
         
         ind2=eaval.indexOf(","+GraphEAConstants.EA_PART_CONNECTING_ACTIVITY_ID+"=",ind);
         String parId=eaval.substring(ind+(GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID+"=").length(),ind2);
         setParticipantId(parId);
         ind=ind2+1;
         
         ind2=eaval.indexOf(","+GraphEAConstants.EA_PART_GRAPH_OFFSET_X+"=",ind);
         String aId=eaval.substring(ind+(GraphEAConstants.EA_PART_CONNECTING_ACTIVITY_ID+"=").length(),ind2);
         if (!(aId==null || aId.equals(""))) {
            setActId(aId);
         }
         ind=ind2+1;
         
         Point temp_offset=new Point(0,0);
         ind2=eaval.indexOf(","+GraphEAConstants.EA_PART_GRAPH_OFFSET_Y+"=",ind);
         String off=eaval.substring(ind+(GraphEAConstants.EA_PART_GRAPH_OFFSET_X+"=").length(),ind2);
         temp_offset.x=Integer.parseInt(off);
         ind=ind2+1;
         
         ind2=eaval.indexOf(","+GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE+"=",ind);
         off=eaval.substring(ind+(GraphEAConstants.EA_PART_GRAPH_OFFSET_Y+"=").length(),ind2);
         temp_offset.y=Integer.parseInt(off);
         setOffset(temp_offset);
         ind=ind2+1;
         
         ind2=eaval.indexOf(","+GraphEAConstants.EA_PART_TYPE+"=",ind);
         String ts=eaval.substring(ind+(GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE+"=").length(),ind2);
         setTransitonStyle(ts);
         ind=ind2+1;
         
         String temp_type=eaval.substring(ind+(GraphEAConstants.EA_PART_TYPE+"=").length());
         setType(temp_type);
      } catch (Exception ex) {
         System.err.println("Exception while parsing start/end ea");
         ex.printStackTrace();
      }
   }
   
   
   public String getActId() {
      return this.actId;
   }
   public void setActId(String actId) {
      this.actId = actId;
   }
   public String getActSetId() {
      return this.actSetId;
   }
   public void setActSetId(String actSetId) {
      this.actSetId = actSetId;
   }
   public String getParticipantId() {
      return this.participantId;
   }
   public void setParticipantId(String participantId) {
      this.participantId = participantId;
   }
   public String getTransitionStyle() {
      return this.transitionStyle;
   }
   public void setTransitonStyle(String transitionStyle) {
      this.transitionStyle=transitionStyle;
   }
   public Point getOffset() {
      return this.offset;
   }
   public void setOffset(Point offset) {
      this.offset = offset;
   }
   public void setType(String type) {
      this.type = type;
   }
   public String getType() {
      return this.type;
   }

   public String getEAName () {
      return eaname;
   }
   
   public void setEAName (String eaname) {
      this.eaname=eaname;
   }

   public boolean isStart () {
      return eaname.indexOf("START")>=0;
   }
   
   public String toString () {
      String ret="";
      if (actSetId!=null) ret+=GraphEAConstants.EA_PART_ACTIVITY_SET_ID+"="+actSetId+",";
      ret+=GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID+"="+participantId+",";
      ret+=GraphEAConstants.EA_PART_CONNECTING_ACTIVITY_ID+"=";
      if (actId!=null) {
         ret+=actId;
      }
      ret+=",";
      ret+=GraphEAConstants.EA_PART_GRAPH_OFFSET_X+"="+String.valueOf(offset.x)+",";
      ret+=GraphEAConstants.EA_PART_GRAPH_OFFSET_Y+"="+String.valueOf(offset.y)+",";
      ret+=GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE+"="+transitionStyle+",";
      ret+=GraphEAConstants.EA_PART_TYPE+"="+type;
      return ret;
   }
}
