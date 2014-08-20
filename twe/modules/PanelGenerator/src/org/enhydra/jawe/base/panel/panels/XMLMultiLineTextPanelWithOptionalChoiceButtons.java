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

package org.enhydra.jawe.base.panel.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.enhydra.jawe.JaWEConstants;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Settings;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jawe.base.panel.PanelSettings;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;

/**
 * Creates panel with JLabel and JTextArea.
 * 
 * @author Sasa Bojanic
 */
public class XMLMultiLineTextPanelWithOptionalChoiceButtons extends XMLBasicPanel implements XMLAppendChoiceInterface {

   protected JTextArea jta;

   protected JLabel jl;

   protected JPanel jspAndOpt;
   
   protected boolean falseRequiredForCC = false;

   public XMLMultiLineTextPanelWithOptionalChoiceButtons(PanelContainer pc,
                                                         XMLElement myOwner,
                                                         boolean isVertical,
                                                         int noOfLines,
                                                         boolean wrapLines,
                                                         boolean isEnabled,
                                                         List<List> choices,
                                                         List<String> chTooltips) {
      this(pc, myOwner, myOwner.toName(), myOwner.isRequired(), isVertical, noOfLines, wrapLines, choices, chTooltips, isEnabled, null);
   }

   public XMLMultiLineTextPanelWithOptionalChoiceButtons(PanelContainer pc,
                                                         XMLElement myOwner,
                                                         String labelKey,
                                                         boolean isFalseRequired,
                                                         boolean isVertical,
                                                         int type,
                                                         boolean wrapLines,
                                                         List<List> choices,
                                                         List<String> chTooltips,
                                                         boolean isEnabled,
                                                         String tooltip) {
      this(pc, myOwner, labelKey, isFalseRequired, isVertical, type, wrapLines, choices, chTooltips, isEnabled, null, tooltip, null);
   }

   public XMLMultiLineTextPanelWithOptionalChoiceButtons(PanelContainer pc,
                                                         XMLElement myOwner,
                                                         String labelKey,
                                                         boolean isFalseRequired,
                                                         boolean isVertical,
                                                         int type,
                                                         boolean wrapLines,
                                                         List<List> choices,
                                                         List<String> chTooltips,
                                                         boolean isEnabled,
                                                         String initText,
                                                         String tooltip,
                                                         final String extension) {

      super(pc, myOwner, "", false, false, true, tooltip);

      this.falseRequiredForCC = isFalseRequired;

      boolean rightAllignment = false;

      Color bkgCol = new Color(245, 245, 245);
      if (pc != null) {
         Settings settings = pc.getSettings();

         rightAllignment = settings.getSettingBoolean("XMLBasicPanel.RightAllignment");

         if (settings instanceof PanelSettings) {
            bkgCol = ((PanelSettings) settings).getBackgroundColor();
         }

      }

      JScrollPane jsp = new JScrollPane();
      jsp.setAlignmentX(Component.LEFT_ALIGNMENT);
      jsp.setAlignmentY(Component.TOP_ALIGNMENT);

      String lbl = "";
      if (pc != null) {
         lbl = pc.getSettings().getLanguageDependentString(labelKey + "Key") + ": ";
      } else {
         lbl = ResourceManager.getLanguageDependentString(labelKey + "Key") + ": ";
      }
      jl = new JLabel(lbl);
      jl.setAlignmentX(Component.LEFT_ALIGNMENT);
      jl.setAlignmentY(Component.TOP_ALIGNMENT);

      if (rightAllignment) {
         jl.setHorizontalAlignment(SwingConstants.RIGHT);
      } else {
         jl.setHorizontalAlignment(SwingConstants.LEFT);
      }

      jspAndOpt = new JPanel();
      jspAndOpt.setLayout(new BoxLayout(jspAndOpt, BoxLayout.X_AXIS));
      jspAndOpt.setAlignmentX(Component.LEFT_ALIGNMENT);
      jspAndOpt.setAlignmentY(Component.TOP_ALIGNMENT);
      jta = new JTextArea();

      jta.setTabSize(4);
      if (initText!=null) {
         jta.setText(initText);
      } else {
         jta.setText(myOwner.toValue());
      }
      jta.getCaret().setDot(0);
      jta.setLineWrap(wrapLines);
      jta.setWrapStyleWord(wrapLines);

      jta.setAlignmentX(Component.LEFT_ALIGNMENT);
      jta.setAlignmentY(Component.TOP_ALIGNMENT);

      jta.setEnabled(isEnabled);
      jta.setBackground(bkgCol);

      final XMLPanel p = this;
      jta.addKeyListener(new KeyAdapter() {
         public void keyPressed(KeyEvent e) {
            if (getPanelContainer() == null)
               return;
            if (PanelUtilities.isModifyingEvent(e)) {
               getPanelContainer().panelChanged(p, e);
            }
         }
      });

      jspAndOpt.add(jsp);
      if (choices != null) {
         for (int i = 0; i < choices.size(); i++) {
            List list = choices.get(i);
            String chTooltip = null;
            if (chTooltips != null && chTooltips.size() >= i) {
               chTooltip = chTooltips.get(i);
            }
            XMLChoiceButtonWithPopup optBtn = new XMLChoiceButtonWithPopup(this,
                                                                           list,
                                                                           ((PanelSettings) pc.getSettings()).getInsertVariableDefaultIcon(),
                                                                           ((PanelSettings) pc.getSettings()).getInsertVariablePressedIcon(),
                                                                           chTooltip);
            // Dimension di=new Dimension(18,18);
            // optBtn.setMinimumSize(new Dimension(di));
            // optBtn.setMaximumSize(new Dimension(di));
            // optBtn.setPreferredSize(new Dimension(di));
            // optBtn.setBorderPainted(false);
            optBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            optBtn.setAlignmentY(Component.TOP_ALIGNMENT);
            optBtn.setEnabled(isEnabled && list.size() > 0);
            optBtn.setContentAreaFilled(false);

            jspAndOpt.add(optBtn);

         }
      }

      if (extension != null) {
         final String appName = Utils.getAppForExtension(extension);
         Dimension fileButtonDimension = new Dimension(20, 20);
         ImageIcon ii = new ImageIcon(this.getClass().getClassLoader().getResource("org/enhydra/jawe/images/edit.png"));
         JButton jb = new JButton(ii);
         jb.setBorderPainted(false);
         jb.setAlignmentX(Component.LEFT_ALIGNMENT);
         jb.setAlignmentY(Component.TOP_ALIGNMENT);
         jb.setMinimumSize(new Dimension(fileButtonDimension));
         jb.setMaximumSize(new Dimension(fileButtonDimension));
         jb.setPreferredSize(new Dimension(fileButtonDimension));
         jb.setContentAreaFilled(false);
         jb.setEnabled(appName != null);
         jb.setToolTipText(getPanelContainer().getLanguageDependentString("EditExpressionInAssociatedApplicationTooltip"));
         jb.setEnabled(isEnabled);
         
         jb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               PanelContainer pc = getPanelContainer();
               String dirName = JaWEConstants.JAWE_USER_HOME + "/tmp";
               new File(dirName).mkdirs();
               String filename = dirName + "/tmp-" + System.currentTimeMillis() + (extension != null ? ("." + extension) : "");
               OutputStream os = null;
               try {
                  os = new FileOutputStream(filename);
                  os.write(jta.getText().getBytes());
                  os.flush();
               } catch (Exception ex) {
                  XMLBasicPanel.errorMessage(getWindow(), pc.getSettings().getLanguageDependentString("ErrorMessageKey"), "", pc.getSettings()
                     .getLanguageDependentString("ErrorWhileOpeningExternalEditorApplicationKey"));
                  ex.printStackTrace();
                  return;
               } finally {
                  if (os != null) {
                     try {
                        os.close();
                     } catch (Exception ex) {
                     }
                  }
               }
               File f = new File(filename);
               try {
                  String execName = appName + " " + filename;
                  Runtime rt = Runtime.getRuntime();
                  Process prc = rt.exec(execName);
                  long t1 = System.currentTimeMillis();
                  prc.waitFor();
                  long t2 = System.currentTimeMillis();
                  String fc = XMLUtil.fileToString(filename);
                  boolean nochange = jta.getText().equals(fc);
                  if ((t2 - t1) < 2500 && nochange) {
                     f.delete();
                     XMLBasicPanel.errorMessage(getWindow(), pc.getSettings().getLanguageDependentString("ErrorMessageKey"), "", pc.getSettings()
                        .getLanguageDependentString("ApplicationDidntOpenCorrectlyKey"));
                  } else {
                     if (!nochange) {
                        jta.setText(fc);
                        pc.panelChanged(p, null);
                     }
                     jta.requestFocus();
                     f.delete();
                  }
               } catch (Exception ex) {
                  XMLBasicPanel.errorMessage(getWindow(), pc.getSettings().getLanguageDependentString("ErrorMessageKey"), "", pc.getSettings()
                     .getLanguageDependentString("ErrorWhileOpeningExternalEditorApplicationKey"));
                  ex.printStackTrace();
               }
            }
         });

         jspAndOpt.add(jb);
      }
      jsp.setViewportView(jta);
      jsp.setAlignmentX(Component.LEFT_ALIGNMENT);
      jsp.setAlignmentY(Component.TOP_ALIGNMENT);

      double scale = 1.3;
      if (type <= 0) {
         type = 1;
      }
      if (type == 1) {
         scale = 2.5;
      } else if (type <= 3) {
         scale = 1.6;
      }

      int fm = (int) (getFontMetrics(getFont()).getHeight() * scale);
      Dimension dim = new Dimension(600, fm * type);
      // jta.setMinimumSize(new Dimension(dim.width - 5, dim.height - 5));
      // jta.setPreferredSize(new Dimension(dim.width - 5, dim.height - 5));

      jsp.setMinimumSize(dim);
      jsp.setPreferredSize(dim);

      JPanel mainPanel = this;
      if (isVertical) {
         mainPanel = new JPanel();
         mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
      }
      if (rightAllignment && !isVertical) {
         mainPanel.add(Box.createHorizontalGlue());
      }
      mainPanel.add(jl);
      if (!rightAllignment && !isVertical) {
         mainPanel.add(Box.createHorizontalGlue());

      }
      mainPanel.add(jspAndOpt);
      if (!rightAllignment && !isVertical) {
         mainPanel.add(Box.createHorizontalGlue());
      }
      if (isVertical) {
         add(mainPanel);
      }

   }

   public boolean validateEntry() {
      if (isEmpty() && getOwner().isRequired() && falseRequiredForCC && !getOwner().isReadOnly()) {
         // TODO CHECK THIS
         XMLBasicPanel.defaultErrorMessage(this.getWindow(), jl.getText());
         jta.requestFocus();
         return false;
      }
      return true;
   }

   public boolean isEmpty() {
      return getText().trim().equals("");
   }

   public void setElements() {
      if (!getOwner().isReadOnly()) {
         myOwner.setValue(getText().trim());
      }
   }

   public String getText() {
      return jta.getText();
   }

   public void appendText(String txt) {
      int cp = jta.getCaretPosition();
      String ct = jta.getText();
      String text = ct.substring(0, cp) + txt + ct.substring(cp);
      jta.setText(text);
      jta.setCaretPosition(cp + txt.length());
      jta.requestFocus();
      if (getPanelContainer() == null)
         return;
      getPanelContainer().panelChanged(this, null);
   }

   public Object getValue() {
      return getText();
   }

   public void requestFocus() {
      jta.requestFocus();
   }

}
