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

package org.enhydra.jawe.base.controller.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractButton;
import javax.swing.Action;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;

/**
 * Action that toggles file watching functionality on/off.
 * When enabled, the application automatically watches for external changes
 * to the current file and prompts for reload when changes are detected.
 *
 * @author Claude Code
 */
public class ToggleFileWatching extends ActionBase {

   public ToggleFileWatching(JaWEComponent jawecomponent) {
      super(jawecomponent);
      // Set initial checkbox state - file watching is enabled by default
      putValue(Action.SELECTED_KEY, true);
   }

   public void enableDisableAction() {
      // Always enabled - users can always toggle file watching on/off
      setEnabled(true);

      // Update checkbox state to reflect current file watcher status
      JaWEController jc = JaWEManager.getInstance().getJaWEController();
      boolean isWatchingEnabled = true; // Default to enabled

      if (jc != null && jc.getFileWatcherService() != null) {
         isWatchingEnabled = jc.getFileWatcherService().isEnabled();
      }

      // Set the checkbox state - this will be automatically handled by the JCheckBoxMenuItem
      putValue(Action.SELECTED_KEY, isWatchingEnabled);
   }

   public void actionPerformed(ActionEvent e) {
      JaWEController jc = JaWEManager.getInstance().getJaWEController();

      if (jc.getFileWatcherService() != null) {
         // Get the new state from the checkbox menu item
         boolean newState = false;
         if (e.getSource() instanceof AbstractButton) {
            newState = ((AbstractButton) e.getSource()).isSelected();
         } else {
            // Fallback: toggle current state
            newState = !jc.getFileWatcherService().isEnabled();
         }

         // Set file watching state to match checkbox
         jc.getFileWatcherService().setEnabled(newState);

         // Update this action's checkbox state
         putValue(Action.SELECTED_KEY, newState);

         // Log the change
         String message = "File watching " + (newState ? "enabled" : "disabled") + " by user";
         JaWEManager.getInstance().getLoggingManager().info("ToggleFileWatching -> " + message);
      }
   }
}
