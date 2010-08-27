package org.enhydra.jawe.components.problemsnavigator;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.XPDLElementChangeInfo;
import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.elements.Package;

/**
 *  Used to handle search events such as "References" event.
 *
 *  @author Sasa Bojanic
 */
public class ProblemsNavigator implements Observer, JaWEComponent {

   protected String type = JaWEComponent.OTHER_COMPONENT;
   protected ProblemsNavigatorPanel panel;

   protected boolean updateInProgress=false;

   protected ProblemsNavigatorSettings settings;
   
   public ProblemsNavigator(JaWEComponentSettings settings) throws Exception {
      this.settings = (ProblemsNavigatorSettings)settings;
      this.settings.init(this);

      init();
      JaWEManager.getInstance().getJaWEController().addObserver(this);
      JaWEManager.getInstance().getValidationOrSearchResultEditor().setValidationDisplayEnabled(false);
   }

   public JaWEComponentSettings getSettings() {
      return settings;
   }
   
   public void update(Observable o, Object arg) {
      if (!(arg instanceof XPDLElementChangeInfo)) return;
      XPDLElementChangeInfo info=(XPDLElementChangeInfo)arg;
      int action=info.getAction();
      if (!(action == XPDLElementChangeInfo.VALIDATION_ERRORS || action==XPDLElementChangeInfo.SELECTED)) 
         return;      

      long start=System.currentTimeMillis();
      JaWEManager.getInstance().getLoggingManager().info("ProblemsNavigator -> update for event "+info+" started ...");
      update(info);
      JaWEManager.getInstance().getLoggingManager().info("ProblemsNavigator -> update ended...");
      long end=System.currentTimeMillis();
      double diffs=(end-start)/1000.0;
      JaWEManager.getInstance().getLoggingManager().debug("THE UPDATE OF SEARCH NAVIG COMPONENT LASTED FOR "+diffs+" SECONDS!");
   }

   public void update (XPDLElementChangeInfo info) {
      if (updateInProgress) return;
      if (info.getSource()==this) {
         return;
      }
      updateInProgress=true;
      try {
         if (info.getAction()==XPDLElementChangeInfo.VALIDATION_ERRORS) {
         boolean specNotif=false;
         if (info.getNewValue() instanceof Boolean) {
            specNotif=((Boolean)info.getNewValue()).booleanValue();
         }
         panel.refreshProblemsPanel(info.getChangedElement(),info.getChangedSubElements(),specNotif);
         } else {
            panel.refreshOnSelection(info.getChangedElement());
         }
         settings.adjustActions();
      } finally {
         updateInProgress=false;
      }
   }

   public void cleanMatches () {
      panel.cleanup();
      settings.adjustActions();
   }
   
   public boolean hasMatches () {
      return panel.hasProblems();
   }
   
   protected void init () {
      panel=new ProblemsNavigatorPanel(this);
      panel.configure();
   }

   public JaWEComponentView getView () {
       return panel;
    }

    public JComponent getDisplay () {
       return panel.getDisplay();
    }

    public String getType() {
       return type;
    }   

    public void setType(String type) {
       this.type = type; 
    }
    
    public String getName () {
       return "ProblemsNavigator";
    }

    public boolean adjustXPDL (Package pkg) {
       return false;
    }

    public List checkValidity (XMLElement el,boolean fullCheck) {
       return null;
    }

    public boolean canCreateElement(XMLCollection col) {
       return true;
    }

    public boolean canInsertElement(XMLCollection col, XMLElement el) {
       return true;
    }

    public boolean canModifyElement(XMLElement el) {
       return true;
    }

    public boolean canRemoveElement(XMLCollection col, XMLElement el) {
       return true;
    }

    public boolean canDuplicateElement(XMLCollection col, XMLElement el) {
       return true;
    }

    public boolean canRepositionElement(XMLCollection col, XMLElement el) {
       return true;
    }

    public void setUpdateInProgress(boolean inProgress) {
       updateInProgress=inProgress;
    }
    
    public boolean isUpdateInProgress() {
       return updateInProgress;
    }
    
}
