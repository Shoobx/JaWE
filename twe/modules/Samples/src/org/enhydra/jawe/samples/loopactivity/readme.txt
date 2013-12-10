# Together Workflow Editor 
# Copyright (C) 2011 Together Teamsolutions Co., Ltd.
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or 
# (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful, 
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program. If not, see http://www.gnu.org/licenses
#
#######################################################################

About Loop activity example
---------------------------
This is a simple example of possible TWE customization by introducing your own
special activity types.
Loop activity is a specific type of BlockActivity. It is specific because it always must have four
special extended attributes called:

   - BackToPool
   - SetTemporary
   - LoopType
   - LoopCondition

and because it allows only one incoming and only one outgoing transition to/from the activity.


How to deploy
-------------
1. You must have TWE 2.0-4 (demo or professional) installed on your machine.
2. copy twe-loopactivity.jar into %TWE_HOME%/lib
   (%TWE_HOME% is folder where TWE 2.0-4 is installed)
3. unpack twe-loopactivity-properties.zip to %USER_HOME%/.JaWE

Run TWE normally.


How to test
-----------
When you start TWE, you'll see that Graph component has additional activity type in a toolbox.
1. Create new Package
2. Create new Process
3. Insert new Participant into the Graph
4. Select loop activity from the toolbox, and insert activity into the graph

   You will see that newly inserted activity has the special icon, and if you open the property panel,
   you'll see that it differs from normal BlockActivity (or any other activity). The differences are:
      - 'General' tab displays only few activity properties plus two special extended attributes, but
        presented by check-boxes
      - There is no 'Simulation information' and 'Extended attributes' tabs
      - There is additional tab called 'Loop characteristic' which contains two other special extended
        attributes, but presented by combo-box and text-area

5. Select XPDL View component to be visible, while having opened Dialog for editing properties of
   inserted loop activity
6. Click the check-boxes with 'Back to pool' and 'Set temporary' information on the general tab, and
   press apply button, and you will see how the change in XPDL View component - the two corresponding
   extended attributes are changing their values
7. Change the values in 'Loop characteristic' tab, and after pressing apply button, you'll see the other
   two corresponding extended attributes changing their values
8. Now insert some other activities into Graph, and try to connect them to our 'Loop' activity.
   You will not be able to have more than one input and more than one output connection.
