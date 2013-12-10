package org.enhydra.jawe.base.panel.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.enhydra.jawe.JaWEXMLUtil;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Settings;
import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jawe.base.panel.PanelSettings;
import org.enhydra.shark.xpdl.XMLElement;

/**
* Creates panel with JLabel, JTextField and JButton, this panel is
* used to set the file name from some choosed location.
* @author Sasa Bojanic
*/
public class XMLLocationPanel extends XMLBasicPanel {

   private static Dimension fileButtonDimension=new Dimension(25,20);

   protected JTextField jtf;
   protected JLabel jl;

   public XMLLocationPanel (PanelContainer pc,XMLElement myOwnerL,boolean isEnabled) {
      this(pc,myOwnerL,isEnabled,-1);
   }

   public XMLLocationPanel (PanelContainer pc,XMLElement myOwner,boolean isEnabled, final int filteringMode) {

      super(pc,myOwner,"",false,false,true);

      boolean rightAllignment=false;
      Dimension textDim=new Dimension(250, 20);
      
      Color bkgCol=new Color(245,245,245);
      if (pc!=null) {
         Settings settings=pc.getSettings();
      
         rightAllignment = settings.getSettingBoolean("XMLBasicPanel.RightAllignment"); 

         textDim=new Dimension(
            settings.getSettingInt("SimplePanelTextWidth"), 
            settings.getSettingInt("SimplePanelTextHeight"));
         fileButtonDimension=new Dimension(fileButtonDimension.width,textDim.height);
         textDim.width=textDim.width-fileButtonDimension.width;

         if (settings instanceof PanelSettings) {
            bkgCol=((PanelSettings)settings).getBackgroundColor();
         }

         jl=new JLabel(pc.getLabelGenerator().getLabel(myOwner)+": ");
      } else {
         jl=new JLabel(ResourceManager.getLanguageDependentString(myOwner.toName()+"Key")+": ");
      }

      jl.setAlignmentX(Component.LEFT_ALIGNMENT);
      jl.setAlignmentY(Component.BOTTOM_ALIGNMENT);
      jl.setHorizontalAlignment(SwingConstants.RIGHT);
      //jl.setMaximumSize(new Dimension(Short.MAX_VALUE,10));

      jtf=new JTextField();
      jtf.setText(myOwner.toValue());
      jtf.setAlignmentX(Component.LEFT_ALIGNMENT);
      jtf.setAlignmentY(Component.BOTTOM_ALIGNMENT);
      jtf.setMinimumSize(new Dimension(textDim));
      jtf.setMaximumSize(new Dimension(textDim));
      jtf.setPreferredSize(new Dimension(textDim));
      jtf.setEnabled(isEnabled);
      jtf.setBackground(bkgCol);
      
      //add key listener
      final XMLPanel p=this;
      jtf.addKeyListener(new KeyAdapter() {
         public void keyPressed(KeyEvent e) {
            if (getPanelContainer()==null) return; 
            if (PanelUtilities.isModifyingEvent(e)) {
               getPanelContainer().panelChanged(p, e);
            }
         }          
      });

      JButton jb=new JButton("...");
      jb.setAlignmentX(Component.LEFT_ALIGNMENT);
      jb.setAlignmentY(Component.BOTTOM_ALIGNMENT);
      jb.setMinimumSize(new Dimension(fileButtonDimension));
      jb.setMaximumSize(new Dimension(fileButtonDimension));
      jb.setPreferredSize(new Dimension(fileButtonDimension));
      jb.setEnabled(isEnabled);

      if (rightAllignment) {
         add(Box.createHorizontalGlue());
      }
      add(jl);
      if (!rightAllignment) {
//         add(Box.createRigidArea(new Dimension(200-jl.getPreferredSize().width,1)));
         add(Box.createHorizontalGlue());

      }
      add(jtf);
      add(jb);

      jb.addActionListener(new ActionListener(){
         public void actionPerformed( ActionEvent ae ){
            String fileName="";
            String message=ResourceManager.getLanguageDependentString("DialogChooseFile");
            fileName=JaWEXMLUtil.dialog(getWindow(),message,0,filteringMode,null);
            if (fileName!=null && fileName.length()>0) {
               jtf.setText(fileName);
               if (getPanelContainer()==null) return;
               getPanelContainer().panelChanged(p, ae);
            }
         }
      });
   }

   public boolean validateEntry () {
      if (isEmpty() && getOwner().isRequired()
         && !getOwner().isReadOnly()) {

          //TODO CHECK THIS
         XMLBasicPanel.defaultErrorMessage(this.getWindow(),jl.getText());
         jtf.requestFocus();
         return false;
      }
      return true;
   }

   public boolean isEmpty () {
      return getText().trim().equals("");
   }

   public void setElements () {
      if (!getOwner().isReadOnly()) {
         myOwner.setValue(getText().trim());
      }
   }

   public String getText () {
      return jtf.getText();
   }

   public Object getValue () {
      return getText();
   }

   public void requestFocus () {
      jtf.requestFocus();
   }
   
}
