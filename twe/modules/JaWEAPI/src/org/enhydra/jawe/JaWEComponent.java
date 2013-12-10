package org.enhydra.jawe;



import java.util.List;

import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.elements.Package;

/**
 * JaWE component interface.
 * 
 * @author Sasa Bojanic
 * @author Miroslav Popov
 */
public interface JaWEComponent {

   public static final String MAIN_COMPONENT = "MAIN";
   public static final String SPECIAL_COMPONENT = "SPECIAL";
   public static final String TREE_COMPONENT = "TREE";
   public static final String OTHER_COMPONENT = "OTHER";   
   public static final String UPPER_STATUS_COMPONENT = "UPPER_STATUS";
   public static final String LOWER_STATUS_COMPONENT = "LOWER_STATUS";   
   
   JaWEComponentSettings getSettings();   
   JaWEComponentView getView();

   
   String getName();
   String getType();
   public void setType(String type);

   boolean adjustXPDL(Package pkg);   
   
   // returns a list of ValidationError elements, or null if there are no errors
   List checkValidity (XMLElement el,boolean fullCheck);
   
   boolean canCreateElement(XMLCollection col);   
   boolean canInsertElement(XMLCollection col, XMLElement el);   
   public boolean canModifyElement(XMLElement el);   
   public boolean canRemoveElement(XMLCollection col, XMLElement el);   
   public boolean canDuplicateElement(XMLCollection col, XMLElement el);   
   public boolean canRepositionElement (XMLCollection col,XMLElement el); 


   public void setUpdateInProgress(boolean inProgress);
   public boolean isUpdateInProgress();
}