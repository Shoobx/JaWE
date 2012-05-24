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

/**
 * Miroslav Popov, Dec 2, 2005 miroslav.popov@gmail.com
 */
package org.enhydra.jawe.components.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.net.URL;
import java.util.Properties;

import javax.swing.Action;
import javax.swing.ImageIcon;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.AdditionalResourceManager;
import org.enhydra.jawe.JaWEAction;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.components.graph.actions.ActivityReferredDocument;
import org.enhydra.jawe.components.graph.actions.ActualSize;
import org.enhydra.jawe.components.graph.actions.AddPoint;
import org.enhydra.jawe.components.graph.actions.DescendInto;
import org.enhydra.jawe.components.graph.actions.GraphPaste;
import org.enhydra.jawe.components.graph.actions.InsertActivitySet;
import org.enhydra.jawe.components.graph.actions.InsertMissingStartAndEndEvents;
import org.enhydra.jawe.components.graph.actions.MoveDownLane;
import org.enhydra.jawe.components.graph.actions.MoveUpLane;
import org.enhydra.jawe.components.graph.actions.NextGraph;
import org.enhydra.jawe.components.graph.actions.PreviousGraph;
import org.enhydra.jawe.components.graph.actions.RemoveLane;
import org.enhydra.jawe.components.graph.actions.RemovePoint;
import org.enhydra.jawe.components.graph.actions.RemoveStartAndEndEvents;
import org.enhydra.jawe.components.graph.actions.SaveAsJPG;
import org.enhydra.jawe.components.graph.actions.SetTransitionStyleNoRoutingBezier;
import org.enhydra.jawe.components.graph.actions.SetTransitionStyleNoRoutingOrthogonal;
import org.enhydra.jawe.components.graph.actions.SetTransitionStyleNoRoutingSpline;
import org.enhydra.jawe.components.graph.actions.SetTransitionStyleSimpleRoutingOrthogonal;
import org.enhydra.jawe.components.graph.actions.SetTransitionStyleSimpleRoutingSpline;
import org.enhydra.jawe.components.graph.actions.SimpleGraphLayout;
import org.enhydra.jawe.components.graph.actions.ZoomIn;
import org.enhydra.jawe.components.graph.actions.ZoomOut;

/**
 * @author Miroslav Popov
 */
public class GraphSettings extends JaWEComponentSettings {

   public static final Stroke DEPARTMENT_STROKE = new BasicStroke(2);

   public void init(JaWEComponent comp) {
      PROPERTYFILE_PATH = "org/enhydra/jawe/components/graph/properties/";
      PROPERTYFILE_NAME = "togwegraphcontroller.properties";
      super.init(comp);
   }

   public void loadDefault(JaWEComponent comp, Properties properties) {
      arm = new AdditionalResourceManager(properties);

      componentSettings.put("UseLaneChoiceButton",
                            new Boolean(properties.getProperty("GraphPanel.UseLaneChoiceButton",
                                                               "true")
                               .equals("true")));
      componentSettings.put("UseActivitySetChoiceButton",
                            new Boolean(properties.getProperty("GraphPanel.UseActivitySetChoiceButton",
                                                               "true")
                               .equals("true")));
      componentSettings.put("GraphOverview.Class",
                            properties.getProperty("GraphOverview.Class",
                                                   "org.enhydra.jawe.components.graph.overviewpanel.GraphOverviewPanel"));
      componentSettings.put("ShowGraphOverview",
                            new Boolean(properties.getProperty("GraphOverview.Show",
                                                               "true").equals("true")));
      componentSettings.put("NameWrapping",
                            new Boolean(properties.getProperty("Graph.NameWrapping",
                                                               "true").equals("true")));
      componentSettings.put("WordWrapping",
                            new Boolean(properties.getProperty("Graph.WrappingStyleWordStatus",
                                                               "true")
                               .equals("true")));
      componentSettings.put("ShowGrid",
                            new Boolean(properties.getProperty("Graph.ShowGrid", "false")
                               .equals("true")));
      componentSettings.put("ShowIcons",
                            new Boolean(properties.getProperty("Graph.ShowIcon", "true")
                               .equals("true")));
      componentSettings.put("ShowShadow",
                            new Boolean(properties.getProperty("Graph.ShowShadow", "true")
                               .equals("true")));
      componentSettings.put("ShowTransitionCondition",
                            new Boolean(properties.getProperty("Graph.ShowTransitionCondition",
                                                               "false")
                               .equals("true")));
      componentSettings.put("ShowTransitionNameForCondition",
                            new Boolean(properties.getProperty("Graph.ShowTransitionNameForCondition",
                                                               "false")
                               .equals("true")));
      componentSettings.put("ShowArtifacts",
                            new Boolean(properties.getProperty("Graph.ShowArtifacts",
                                                               "true")));

      componentSettings.put("GraphClass",
                            properties.getProperty("Graph.Class",
                                                   "org.enhydra.jawe.components.graph.Graph"));

      componentSettings.put("GraphControllerPanel",
                            properties.getProperty("Graph.ControllerPanel",
                                                   "org.enhydra.jawe.components.graph.GraphControllerPanel"));

      componentSettings.put("GraphManagerClass",
                            properties.getProperty("GraphManager.Class",
                                                   "org.enhydra.jawe.components.graph.GraphManager"));
      componentSettings.put("GraphMarqueeHandlerClass",
                            properties.getProperty("GraphMarqueeHandler.Class",
                                                   "org.enhydra.jawe.components.graph.GraphMarqueeHandler"));
      componentSettings.put("GraphModelClass",
                            properties.getProperty("GraphModel.Class",
                                                   "org.enhydra.jawe.components.graph.JaWEGraphModel"));
      componentSettings.put("GraphObjectFactoryClass",
                            properties.getProperty("GraphObjectFactory.Class",
                                                   "org.enhydra.jawe.components.graph.GraphObjectFactory"));
      componentSettings.put("GraphObjectRendererFactoryClass",
                            properties.getProperty("GraphObjectRendererFactoryClass",
                                                   "org.enhydra.jawe.components.graph.GraphObjectRendererFactory"));
      componentSettings.put("DefaultTransitionStyle",
                            properties.getProperty("Graph.DefaultTransitionStyle",
                                                   "NO_ROUTING_SPLINE"));

      componentSettings.put("GridSize",
                            new Integer(properties.getProperty("Graph.GridSize", "10")));
      componentSettings.put("ShadowWidth",
                            new Integer(properties.getProperty("Graph.ShadowWidth", "3")));
      componentSettings.put("GraphFontSize",
                            new Integer(properties.getProperty("Graph.FontSize", "11")));
      componentSettings.put("ActivityHeight",
                            new Integer(properties.getProperty("Graph.ActivityHeight",
                                                               "60")));
      componentSettings.put("ActivityWidth",
                            new Integer(properties.getProperty("Graph.ActivityWidth",
                                                               "90")));
      componentSettings.put("DataObjectHeight",
                            new Integer(properties.getProperty("Graph.DataObjectHeight",
                                                               "40")));
      componentSettings.put("DataObjectWidth",
                            new Integer(properties.getProperty("Graph.DataObjectWidth",
                                                               "30")));
      componentSettings.put("AnnotationHeight",
                            new Integer(properties.getProperty("Graph.AnnotationHeight",
                                                               "35")));
      componentSettings.put("AnnotationWidth",
                            new Integer(properties.getProperty("Graph.AnnotationWidth",
                                                               "120")));
      componentSettings.put("GatewayHeight",
                            new Integer(properties.getProperty("Graph.GatewayHeight",
                                                               "43")));
      componentSettings.put("GatewayWidth",
                            new Integer(properties.getProperty("Graph.GatewayWidth", "43")));
      componentSettings.put("EventRadius",
                            new Integer(properties.getProperty("Graph.EventRadius", "15")));
      componentSettings.put("LaneNameWidth",
                            new Integer(properties.getProperty("Graph.LaneNameWidth",
                                                               "35")));
      componentSettings.put("MinLaneWidth",
                            new Integer(properties.getProperty("Graph.LaneMinWidth",
                                                               "800")));
      componentSettings.put("MinLaneHeight",
                            new Integer(properties.getProperty("Graph.LaneMinHeight",
                                                               "150")));
      componentSettings.put("AdditionalToolbar",
                            new Boolean(properties.getProperty("Graph.AdditionalToolbar",
                                                               "false")));

      componentSettings.put("Graph.HistoryManager.Class",
                            properties.getProperty("Graph.HistoryManager.Class",
                                                   "org.enhydra.jawe.historymgr.HistoryMgr"));
      componentSettings.put("Graph.HistorySize",
                            new Integer(properties.getProperty("Graph.HistorySize", "15")));

      Color color;
      try {
         color = Utils.getColor(ResourceManager.getResourceString(properties,
                                                                  "Graph.ActivitySelectedColor"));
      } catch (Exception e) {
         color = Utils.getColor("R=248,G=242,B=14");
      }
      componentSettings.put("ActivitySelectedColor", color);

      try {
         color = Utils.getColor(ResourceManager.getResourceString(properties,
                                                                  "Graph.StartEventColor"));
      } catch (Exception e) {
         color = Utils.getColor("R=102, G=204, B=51");
      }
      componentSettings.put("StartEventColor", color);

      try {
         color = Utils.getColor(ResourceManager.getResourceString(properties,
                                                                  "Graph.EndEventColor"));
      } catch (Exception e) {
         color = Utils.getColor("R=236, G=120, B=98");
      }
      componentSettings.put("EndEventColor", color);

      try {
         color = Utils.getColor(ResourceManager.getResourceString(properties,
                                                                  "Graph.BackgroundColor"));
      } catch (Exception e) {
//         color = Utils.getColor("R=255,G=255,B=215");
         color = Color.WHITE;
      }
      componentSettings.put("BackgroundColor", color);
      try {
         color = Utils.getColor(ResourceManager.getResourceString(properties,
                                                                  "Graph.GridColor"));
      } catch (Exception e) {
         color = Utils.getColor("R=187,G=247,B=190");
      }
      componentSettings.put("GridColor", color);
      try {
         color = Utils.getColor(ResourceManager.getResourceString(properties,
                                                                  "Graph.TextColor"));
      } catch (Exception e) {
         color = Utils.getColor("SystemColor.textText");
      }
      componentSettings.put("TextColor", color);
      try {
         color = Utils.getColor(ResourceManager.getResourceString(properties,
                                                                  "Graph.LaneBorderColor"));
      } catch (Exception e) {
         color = Utils.getColor("SystemColor.textText");
      }
      componentSettings.put("LaneBorderColor", color);
      try {
         color = Utils.getColor(ResourceManager.getResourceString(properties,
                                                                  "Graph.LaneFreeTextExpressionColor"));
      } catch (Exception e) {
         color = Utils.getColor("R=255,G=255,B=215");
      }
      componentSettings.put("LaneFreeTextExpressionColor", color);
      try {
         color = Utils.getColor(ResourceManager.getResourceString(properties,
                                                                  "Graph.LaneCommonExpressionColor"));
      } catch (Exception e) {
         color = Utils.getColor("R=255,G=255,B=215");
      }
      componentSettings.put("LaneCommonExpressionColor", color);

      try {
         color = Utils.getColor(ResourceManager.getResourceString(properties,
                                                                  "Graph.HandleColor"));
      } catch (Exception e) {
         color = Utils.getColor("Color.pink");
      }
      componentSettings.put("HandleColor", color);
      try {
         color = Utils.getColor(ResourceManager.getResourceString(properties,
                                                                  "Graph.MarqueeColor"));
      } catch (Exception e) {
         color = Utils.getColor("SystemColor.textHighlight");
      }
      componentSettings.put("MarqueeColor", color);

      ImageIcon cicon;
      URL iconURL = ResourceManager.getResource(properties,
                                                "Graph.XPDLElement.Image.Default");
      if (iconURL != null)
         cicon = new ImageIcon(iconURL);
      else
         cicon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/genericactivity.gif"));
      componentSettings.put("DefaultActivityIcon", cicon);

      iconURL = ResourceManager.getResource(properties, "Graph.XPDLElement.Image.Start");
      if (iconURL != null)
         cicon = new ImageIcon(iconURL);
      else
         cicon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/start.gif"));
      componentSettings.put("Start", cicon);

      iconURL = ResourceManager.getResource(properties, "Graph.XPDLElement.Image.End");
      if (iconURL != null)
         cicon = new ImageIcon(iconURL);
      else
         cicon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/end.gif"));
      componentSettings.put("End", cicon);

      iconURL = ResourceManager.getResource(properties,
                                            "Graph.XPDLElement.Image.FreeTextLane");
      if (iconURL != null)
         cicon = new ImageIcon(iconURL);
      else
         cicon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/freetextlane.png"));
      componentSettings.put("FreeTextLane", cicon);

      iconURL = ResourceManager.getResource(properties,
                                            "Graph.XPDLElement.Image.CommonExpresionLane");
      if (iconURL != null)
         cicon = new ImageIcon(iconURL);
      else
         cicon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/commonexplane.png"));
      componentSettings.put("CommonExpresionLane", cicon);

      iconURL = ResourceManager.getResource(properties, "GraphPanel.Image.Lanes");
      if (iconURL != null)
         cicon = new ImageIcon(iconURL);
      else
         cicon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/lanesselect.gif"));
      componentSettings.put("Lanes", cicon);

      iconURL = ResourceManager.getResource(properties,
                                            "GraphPanel.Image.ActivitySetSelect");
      if (iconURL != null)
         cicon = new ImageIcon(iconURL);
      else
         cicon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/activitysetselect.gif"));
      componentSettings.put("ActivitySetSelect", cicon);

      iconURL = ResourceManager.getResource(properties, "GraphPanel.Image.Selection");
      if (iconURL != null)
         cicon = new ImageIcon(iconURL);
      else
         cicon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/select.gif"));
      componentSettings.put("Selection", cicon);

      // menus, toolbars and actions
      loadDefaultMenusToolbarsAndActions(comp);
      componentSettings.putAll(Utils.loadAllMenusAndToolbars(properties));
      componentAction.putAll(Utils.loadActions(properties, comp, componentAction));
   }

   protected void loadDefaultMenusToolbarsAndActions(JaWEComponent comp) {
      // menu
      componentSettings.put("ACTIVITYMenu",
                            "jaweAction_Cut jaweAction_Copy jaweAction_Delete jaweAction_EditProperties - ActivityReferredDocument SelectConnectingTransitionsForSelectedActivities SelectConnectingAssociationsForSelectedActivities - SetToDefaultColor SetToDefaultSize SetElementsColor SetElementsSize");
      componentSettings.put("ACTIVITY_BLOCKMenu", "DescendInto");
      componentSettings.put("ACTIVITY_SUBFLOWMenu", "DescendInto");
      componentSettings.put("ARTIFACTMenu",
      "jaweAction_Cut jaweAction_Copy jaweAction_Delete jaweAction_EditProperties - SelectConnectingAssociationsForSelectedArtifacts - SetToDefaultColor SetToDefaultSize SetElementsColor SetElementsSize");
      componentSettings.put("LANEMenu",
                            "RemoveLane jaweAction_Delete jaweAction_EditProperties - MoveUpLane MoveDownLane - SetToDefaultColor SetElementsColor");
      componentSettings.put("SELECTMenu", "GraphPaste");
      componentSettings.put("TRANSITIONMenu",
                            "jaweAction_Cut jaweAction_Copy jaweAction_Delete jaweAction_EditProperties - AddPoint RemovePoint *SetTransitionStyle - SetToDefaultColor SetElementsColor");
      componentSettings.put("SetTransitionStyleMenu",
                            "SetTransitionStyleNoRoutingBezier SetTransitionStyleNoRoutingOrthogonal SetTransitionStyleNoRoutingSpline - SetTransitionStyleSimpleRoutingBezier SetTransitionStyleSimpleRoutingOrthogonal SetTransitionStyleSimpleRoutingSpline");
      componentSettings.put("SetTransitionStyleLangName", "SetTransitionStyle");

      // toolbar
      componentSettings.put("defaultToolbarToolbar", "*graphEditToolbar");
      componentSettings.put("toolboxToolbar",
                            "SetSelectMode - SetLaneMode* SetLaneModeFreeTextExpression SetLaneModeCommonExpression - SetArtifactMode* - SetActivityMode* - SetTransitionMode* - SetAssociationMode*");
      componentSettings.put("graphEditToolbarToolbar",
                            "SaveAsJPG SaveAsSVG SaveAsPDF - ZoomIn ActualSize ZoomOut - MoveUpLane MoveDownLane - PreviousGraph NextGraph - InsertMissingStartAndEndEvents RemoveStartAndEndEvents ShowTransitionCondition HideTransitionCondition ShowArtifacts HideArtifacts - RotateProcess SimpleGraphLayout - InsertActivitySet");

      // actions
      ActionBase action = null;
      ImageIcon icon;
      String langDepName;
      JaWEAction ja;

      // SetPerformerExpression
      try {
         String clsName = "org.enhydra.jawe.components.graph.actions.SetPerformerExpression";
         try {
            action = (ActionBase) Class.forName(clsName).getConstructor(new Class[] {
               JaWEComponent.class
            }).newInstance(new Object[] {
               comp
            });
         } catch (Exception e) {
         }
         icon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/commonexplanesetexp.png"));
         langDepName = "SetPerformerExpression";
         ja = new JaWEAction(action, icon, langDepName);
         componentAction.put(langDepName, ja);
      } catch (Exception ex) {
      }
      // SetLanesName
      try {
         String clsName = "org.enhydra.jawe.components.graph.actions.SetLanesName";
         try {
            action = (ActionBase) Class.forName(clsName).getConstructor(new Class[] {
               JaWEComponent.class
            }).newInstance(new Object[] {
               comp
            });
         } catch (Exception e) {
         }
         icon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/freetextlane.png"));                                                  
         langDepName = "SetLanesName";
         ja = new JaWEAction(action, icon, langDepName);
         componentAction.put(langDepName, ja);
      } catch (Exception ex) {
      }
      // ActualSize
      action = new ActualSize(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/actualsize.gif"));
      langDepName = "ActualSize";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // ActivityReferredDocument
      action = new ActivityReferredDocument(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/referred_document.png"));
      langDepName = "ActivityReferredDocument";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // SelectConnectingTransitionsForSelectedActivities
      try {
         String clsName = "org.enhydra.jawe.components.graph.actions.SelectConnectingTransitionsForSelectedActivities";
         try {
            action = (ActionBase) Class.forName(clsName).getConstructor(new Class[] {
               JaWEComponent.class
            }).newInstance(new Object[] {
               comp
            });
         } catch (Exception e) {
            action = null;
         }
         icon = null;
         langDepName = "SelectConnectingTransitionsForSelectedActivities";
         ja = new JaWEAction(action, icon, langDepName);
         componentAction.put(langDepName, ja);
      } catch (Exception ex) {
      }
      try {
         String clsName = "org.enhydra.jawe.components.graph.actions.SelectConnectingAssociationsForSelectedActivities";
         try {
            action = (ActionBase) Class.forName(clsName).getConstructor(new Class[] {
               JaWEComponent.class
            }).newInstance(new Object[] {
               comp
            });
         } catch (Exception e) {
            action = null;
         }
         icon = null;
         langDepName = "SelectConnectingAssociationsForSelectedActivities";
         ja = new JaWEAction(action, icon, langDepName);
         componentAction.put(langDepName, ja);
      } catch (Exception ex) {
      }
      try {
         String clsName = "org.enhydra.jawe.components.graph.actions.SelectConnectingAssociationsForSelectedArtifacts";
         try {
            action = (ActionBase) Class.forName(clsName).getConstructor(new Class[] {
               JaWEComponent.class
            }).newInstance(new Object[] {
               comp
            });
         } catch (Exception e) {
            action = null;
         }
         icon = null;
         langDepName = "SelectConnectingAssociationsForSelectedArtifacts";
         ja = new JaWEAction(action, icon, langDepName);
         componentAction.put(langDepName, ja);
      } catch (Exception ex) {
      }

      // AddPoint
      action = new AddPoint(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/addpoint.gif"));
      langDepName = "AddPoint";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // DescendInto
      action = new DescendInto(comp);
      langDepName = "DescendInto";
      ja = new JaWEAction(action, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // GraphPaste
      action = new GraphPaste(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/paste.gif"));
      langDepName = "Paste";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // InsertActivitySet
      action = new InsertActivitySet(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/activitysetnew.gif"));
      langDepName = "InsertActivitySet";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // MoveDownLane
      action = new MoveDownLane(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/lanedownright.gif"));
      langDepName = "MoveDownLane";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // MoveUpLane
      action = new MoveUpLane(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/laneupleft.gif"));
      langDepName = "MoveUpLane";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // NextGraph
      action = new NextGraph(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/nav_right_red.png"));
      langDepName = "NextGraph";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // PreviousGraph
      action = new PreviousGraph(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/nav_left_red.png"));
      langDepName = "PreviousGraph";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // InsertMissingStartAndEndEvents
      action = new InsertMissingStartAndEndEvents(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/startend.gif"));
      langDepName = "InsertMissingStartAndEndEvents";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // RemoveStartAndEndEvents
      action = new RemoveStartAndEndEvents(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/startend_remove.gif"));
      langDepName = "RemoveStartAndEndEvents";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // RemoveLane
      action = new RemoveLane(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/laneremove.png"));
      langDepName = "RemoveLane";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // RemovePoint
      action = new RemovePoint(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/removepoint.gif"));
      langDepName = "RemovePoint";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // ShowTransitionCondition
      try {
         String clsName = "org.enhydra.jawe.components.graph.actions.ShowTransitionCondition";
         try {
            action = (ActionBase) Class.forName(clsName).getConstructor(new Class[] {
               JaWEComponent.class
            }).newInstance(new Object[] {
               comp
            });
         } catch (Exception e) {
            action = null;
         }
         icon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/showtransitioncondition.gif"));
         langDepName = "ShowTransitionCondition";
         ja = new JaWEAction(action, icon, langDepName);
         componentAction.put(langDepName, ja);
      } catch (Exception ex) {
      }

      // HideTransitionCondition
      try {
         String clsName = "org.enhydra.jawe.components.graph.actions.HideTransitionCondition";
         try {
            action = (ActionBase) Class.forName(clsName).getConstructor(new Class[] {
               JaWEComponent.class
            }).newInstance(new Object[] {
               comp
            });
         } catch (Exception e) {
            action = null;
         }
         icon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/hidetransitioncondition.gif"));
         langDepName = "HideTransitionCondition";
         ja = new JaWEAction(action, icon, langDepName);
         componentAction.put(langDepName, ja);
      } catch (Exception ex) {
      }

      // HideArtifacts
      try {
         String clsName = "org.enhydra.jawe.components.graph.actions.HideArtifacts";
         try {
            action = (ActionBase) Class.forName(clsName).getConstructor(new Class[] {
               JaWEComponent.class
            }).newInstance(new Object[] {
               comp
            });
         } catch (Exception e) {
            action = null;
         }
         icon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/hideartifacts.gif"));
         langDepName = "HideArtifacts";
         ja = new JaWEAction(action, icon, langDepName);
         componentAction.put(langDepName, ja);
      } catch (Exception ex) {
      }

      // ShowArtifacts
      try {
         String clsName = "org.enhydra.jawe.components.graph.actions.ShowArtifacts";
         try {
            action = (ActionBase) Class.forName(clsName).getConstructor(new Class[] {
               JaWEComponent.class
            }).newInstance(new Object[] {
               comp
            });
         } catch (Exception e) {
            action = null;
         }
         icon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/showartifacts.gif"));
         langDepName = "ShowArtifacts";
         ja = new JaWEAction(action, icon, langDepName);
         componentAction.put(langDepName, ja);
      } catch (Exception ex) {
      }

      // SetToDefaultColor
      try {
         String clsName = "org.enhydra.jawe.components.graph.actions.SetToDefaultColor";
         try {
            action = (ActionBase) Class.forName(clsName).getConstructor(new Class[] {
               JaWEComponent.class
            }).newInstance(new Object[] {
               comp
            });
         } catch (Exception e) {
            action = null;
         }
         icon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/settodefaultcolor.png"));
         langDepName = "SetToDefaultColor";
         ja = new JaWEAction(action, icon, langDepName);
         componentAction.put(langDepName, ja);
      } catch (Exception ex) {
      }

      // SetToDefaultSize
      try {
         String clsName = "org.enhydra.jawe.components.graph.actions.SetToDefaultSize";
         try {
            action = (ActionBase) Class.forName(clsName).getConstructor(new Class[] {
               JaWEComponent.class
            }).newInstance(new Object[] {
               comp
            });
         } catch (Exception e) {
            action = null;
         }
         icon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/settodefaultsize.png"));
         langDepName = "SetToDefaultSize";
         ja = new JaWEAction(action, icon, langDepName);
         componentAction.put(langDepName, ja);
      } catch (Exception ex) {
      }

      // SetElementsColor
      try {
         String clsName = "org.enhydra.jawe.components.graph.actions.SetElementsColor";
         try {
            action = (ActionBase) Class.forName(clsName).getConstructor(new Class[] {
               JaWEComponent.class
            }).newInstance(new Object[] {
               comp
            });
         } catch (Exception e) {
            action = null;
         }
         icon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/setelementscolor.png"));
         langDepName = "SetElementsColor";
         ja = new JaWEAction(action, icon, langDepName);
         componentAction.put(langDepName, ja);
      } catch (Exception ex) {
      }
      
      // SetElementsColor
      try {
         String clsName = "org.enhydra.jawe.components.graph.actions.SetElementsSize";
         try {
            action = (ActionBase) Class.forName(clsName).getConstructor(new Class[] {
               JaWEComponent.class
            }).newInstance(new Object[] {
               comp
            });
         } catch (Exception e) {
            action = null;
         }
         icon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/setelementssize.png"));
         langDepName = "SetElementsSize";
         ja = new JaWEAction(action, icon, langDepName);
         componentAction.put(langDepName, ja);
      } catch (Exception ex) {
      }

      // RotateProcess
      try {
         String clsName = "org.enhydra.jawe.components.graph.actions.RotateProcess";
         try {
            action = (ActionBase) Class.forName(clsName).getConstructor(new Class[] {
               JaWEComponent.class
            }).newInstance(new Object[] {
               comp
            });
         } catch (Exception e) {
            action = null;
         }
         icon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/process_rotate.gif"));
         langDepName = "RotateProcess";
         ja = new JaWEAction(action, icon, langDepName);
         componentAction.put(langDepName, ja);
      } catch (Exception ex) {
      }

      // SimpleGraphLayout
      action = new SimpleGraphLayout(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/graph_layout.png"));
      langDepName = "SimpleGraphLayout";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // SaveAsJPG
      action = new SaveAsJPG(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/saveasjpg.gif"));
      langDepName = "SaveAsJPG";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // SaveAsSVG

      try {
         String clsName = "org.enhydra.jawe.components.graph.actions.jped.SaveAsSVG";
         action = (ActionBase) Class.forName(clsName).getConstructor(new Class[] {
            JaWEComponent.class
         }).newInstance(new Object[] {
            comp
         });
         icon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/saveassvg.gif"));
         langDepName = "SaveAsSVG";
         ja = new JaWEAction(action, icon, langDepName);
         componentAction.put(action.getValue(Action.NAME), ja);
      } catch (Exception e) {
      }

      // SaveAsPDF
      try {
         String clsName = "org.enhydra.jawe.components.graph.actions.jped.SaveAsPDF";
         action = (ActionBase) Class.forName(clsName).getConstructor(new Class[] {
            JaWEComponent.class
         }).newInstance(new Object[] {
            comp
         });
         icon = new ImageIcon(ResourceManager.class.getClassLoader()
            .getResource("org/enhydra/jawe/images/saveaspdf.gif"));
         langDepName = "SaveAsPDF";
         ja = new JaWEAction(action, icon, langDepName);
         componentAction.put(action.getValue(Action.NAME), ja);
      } catch (Exception e) {
      }

      // SetTransitionStyleNoRoutingBezier
      action = new SetTransitionStyleNoRoutingBezier(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/transitionbezier.gif"));
      langDepName = "SetTransitionStyleNoRoutingBezier";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // SetTransitionStyleNoRoutingOrthogonal
      action = new SetTransitionStyleNoRoutingOrthogonal(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/transitionortogonal.gif"));
      langDepName = "SetTransitionStyleNoRoutingOrthogonal";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // SetTransitionStyleNoRoutingSpline
      action = new SetTransitionStyleNoRoutingSpline(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/transitionspline.gif"));
      langDepName = "SetTransitionStyleNoRoutingSpline";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // SetTransitionStyleSimpleRoutingBezier
      action = new SetTransitionStyleNoRoutingBezier(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/transitionbeziersr.gif"));
      langDepName = "SetTransitionStyleSimpleRoutingBezier";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // SetTransitionStyleSimpleRoutingOrthogonal
      action = new SetTransitionStyleSimpleRoutingOrthogonal(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/transitionortogonalsr.gif"));
      langDepName = "SetTransitionStyleSimpleRoutingOrthogonal";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // SetTransitionStyleSimpleRoutingSpline
      action = new SetTransitionStyleSimpleRoutingSpline(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/transitionsplinesr.gif"));
      langDepName = "SetTransitionStyleSimpleRoutingSpline";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // SelectConnectingActivitiesForSelectedTransitions
      try {
         String clsName = "org.enhydra.jawe.components.graph.actions.SelectConnectingActivitiesForSelectedTransitions";
         try {
            action = (ActionBase) Class.forName(clsName).getConstructor(new Class[] {
               JaWEComponent.class
            }).newInstance(new Object[] {
               comp
            });
         } catch (Exception e) {
            action = null;
         }
         icon = null;
         langDepName = "SelectConnectingActivitiesForSelectedTransitions";
         ja = new JaWEAction(action, icon, langDepName);
         componentAction.put(langDepName, ja);
      } catch (Exception ex) {
      }
      try {
         String clsName = "org.enhydra.jawe.components.graph.actions.SelectConnectingActivitiesForSelectedAssociations";
         try {
            action = (ActionBase) Class.forName(clsName).getConstructor(new Class[] {
               JaWEComponent.class
            }).newInstance(new Object[] {
               comp
            });
         } catch (Exception e) {
            action = null;
         }
         icon = null;
         langDepName = "SelectConnectingActivitiesForSelectedAssociations";
         ja = new JaWEAction(action, icon, langDepName);
         componentAction.put(langDepName, ja);
      } catch (Exception ex) {
      }
      try {
         String clsName = "org.enhydra.jawe.components.graph.actions.SelectConnectingArtifactsForSelectedAssociations";
         try {
            action = (ActionBase) Class.forName(clsName).getConstructor(new Class[] {
               JaWEComponent.class
            }).newInstance(new Object[] {
               comp
            });
         } catch (Exception e) {
            action = null;
         }
         icon = null;
         langDepName = "SelectConnectingArtifactsForSelectedAssociations";
         ja = new JaWEAction(action, icon, langDepName);
         componentAction.put(langDepName, ja);
      } catch (Exception ex) {
      }

      // ZoomIn
      action = new ZoomIn(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/zoomin.gif"));
      langDepName = "ZoomIn";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);

      // ZoomOut
      action = new ZoomOut(comp);
      icon = new ImageIcon(ResourceManager.class.getClassLoader()
         .getResource("org/enhydra/jawe/images/zoomout.gif"));
      langDepName = "ZoomOut";
      ja = new JaWEAction(action, icon, langDepName);
      componentAction.put(action.getValue(Action.NAME), ja);
   }

   public String getMenuActionOrder(String menuName) {
      return (String) componentSettings.get(menuName + "Menu");
   }

   public String getToolbarActionOrder(String toolbarName) {
      return (String) componentSettings.get(toolbarName + "Toolbar");
   }

   public boolean isNameWrappingEnabled() {
      return ((Boolean) componentSettings.get("NameWrapping")).booleanValue();
   }

   public boolean isWordWrappingEnabled() {
      return ((Boolean) componentSettings.get("WordWrapping")).booleanValue();
   }

   public boolean shouldShowGrid() {
      return ((Boolean) componentSettings.get("ShowGrid")).booleanValue();
   }

   public boolean shouldShowIcons() {
      return ((Boolean) componentSettings.get("ShowIcons")).booleanValue();
   }

   public boolean isShadowEnabled() {
      return ((Boolean) componentSettings.get("ShowShadow")).booleanValue();
   }

   public boolean shouldShowTransitionCondition() {
      return ((Boolean) componentSettings.get("ShowTransitionCondition")).booleanValue();
   }

   public boolean shouldShowTransitionNameForCondition() {
      return ((Boolean) componentSettings.get("ShowTransitionNameForCondition")).booleanValue();
   }

   public boolean useLaneChoiceButton() {
      return ((Boolean) componentSettings.get("UseLaneChoiceButton")).booleanValue();
   }

   public boolean useActivitySetChoiceButton() {
      return ((Boolean) componentSettings.get("UseActivitySetChoiceButton")).booleanValue();
   }

   public boolean shouldShowGraphOverview() {
      return ((Boolean) componentSettings.get("ShowGraphOverview")).booleanValue();
   }

   public String getGraphObjectFactory() {
      return (String) componentSettings.get("GraphObjectFactoryClass");
   }

   public String getGraphObjectRendererFactory() {
      return (String) componentSettings.get("GraphObjectRendererFactoryClass");
   }

   public String getGraphMarqueeHandler() {
      return (String) componentSettings.get("GraphMarqueeHandlerClass");
   }

   public String getGraphClass() {
      return (String) componentSettings.get("GraphClass");
   }

   public String getGraphControllerPanel() {
      return (String) componentSettings.get("GraphControllerPanel");
   }

   public String getGraphModelClass() {
      return (String) componentSettings.get("GraphModelClass");
   }

   public String getGraphManager() {
      return (String) componentSettings.get("GraphManagerClass");
   }

   public String getDefaultTransitionStyle() {
      return (String) componentSettings.get("DefaultTransitionStyle");
   }

   public ImageIcon getDefaultActivityIcon() {
      return (ImageIcon) componentSettings.get("DefaultActivityIcon");
   }

   public ImageIcon getStartIcon() {
      return (ImageIcon) componentSettings.get("Start");
   }

   public ImageIcon getEndIcon() {
      return (ImageIcon) componentSettings.get("End");
   }

   public ImageIcon getFreeTextLaneIcon() {
      return (ImageIcon) componentSettings.get("FreeTextLane");
   }

   public ImageIcon getCommonExpresionLaneIcon() {
      return (ImageIcon) componentSettings.get("CommonExpresionLane");
   }

   public ImageIcon getLanesIcon() {
      return (ImageIcon) componentSettings.get("Lanes");
   }

   public ImageIcon getActivitySetSelectIcon() {
      return (ImageIcon) componentSettings.get("ActivitySetSelect");
   }

   public ImageIcon getSelectionIcon() {
      return (ImageIcon) componentSettings.get("Selection");
   }

   public int getGridSize() {
      return ((Integer) componentSettings.get("GridSize")).intValue();
   }

   public int getShadowWidth() {
      return ((Integer) componentSettings.get("ShadowWidth")).intValue();
   }

   public int getGraphFontSize() {
      return ((Integer) componentSettings.get("GraphFontSize")).intValue();
   }

   public int getActivityHeight() {
      return ((Integer) componentSettings.get("ActivityHeight")).intValue();
   }

   public int getActivityWidth() {
      return ((Integer) componentSettings.get("ActivityWidth")).intValue();
   }

   public int getDataObjectHeight() {
      return ((Integer) componentSettings.get("DataObjectHeight")).intValue();
   }

   public int getDataObjectWidth() {
      return ((Integer) componentSettings.get("DataObjectWidth")).intValue();
   }

   public int getAnnotationHeight() {
      return ((Integer) componentSettings.get("AnnotationHeight")).intValue();
   }

   public int getAnnotationWidth() {
      return ((Integer) componentSettings.get("AnnotationWidth")).intValue();
   }

   public int getGatewayHeight() {
      return ((Integer) componentSettings.get("GatewayHeight")).intValue();
   }

   public int getGatewayWidth() {
      return ((Integer) componentSettings.get("GatewayWidth")).intValue();
   }

   public int getEventRadius() {
      return ((Integer) componentSettings.get("EventRadius")).intValue();
   }

   public int getMinLaneWidth() {
      return ((Integer) componentSettings.get("MinLaneWidth")).intValue();
   }

   public int getMinLaneHeight() {
      return ((Integer) componentSettings.get("MinLaneHeight")).intValue();
   }

   public int getLaneNameWidth() {
      return ((Integer) componentSettings.get("LaneNameWidth")).intValue();
   }

   public Color getSelectedActivityColor() {
      return (Color) componentSettings.get("ActivitySelectedColor");
   }

   public Color getStartEventColor() {
      return (Color) componentSettings.get("StartEventColor");
   }

   public Color getEndEventColor() {
      return (Color) componentSettings.get("EndEventColor");
   }

   public Color getBackgroundColor() {
      return (Color) componentSettings.get("BackgroundColor");
   }

   public Color getGridColor() {
      return (Color) componentSettings.get("GridColor");
   }

   public Color getTextColor() {
      return (Color) componentSettings.get("TextColor");
   }

   public Color getLaneBorderColor() {
      return (Color) componentSettings.get("LaneBorderColor");
   }

   public Color getLaneFreeTextExpressionColor() {
      return (Color) componentSettings.get("LaneFreeTextExpressionColor");
   }

   public Color getLaneCommonExpressionColor() {
      return (Color) componentSettings.get("LaneCommonExpressionColor");
   }

   public Color getHandleColor() {
      return (Color) componentSettings.get("HandleColor");
   }

   public Color getMarqueeColor() {
      return (Color) componentSettings.get("MarqueeColor");
   }

   public String historyManagerClass() {
      return (String) componentSettings.get("Graph.HistoryManager.Class");
   }

   public int historySize() {
      return ((Integer) componentSettings.get("Graph.HistorySize")).intValue();
   }

   public String overviewClass() {
      return (String) componentSettings.get("GraphOverview.Class");
   }

   public boolean performAutomaticLayoutOnInsertion() {
      return false;
   }

   public boolean hasAdditionalToolbar() {
      return ((Boolean) componentSettings.get("AdditionalToolbar")).booleanValue();
   }
   
   public boolean shouldShowArtifacts () {      
      return ((Boolean) componentSettings.get("ShowArtifacts")).booleanValue();
   }
   
}
