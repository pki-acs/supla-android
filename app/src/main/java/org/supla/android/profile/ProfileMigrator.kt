package org.supla.android.profile

/*
 Copyright (C) AC SOFTWARE SP. Z O.O.

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import android.content.Context

import org.supla.android.db.AuthProfileItem
import org.supla.android.Preferences

/**
ProfileMigrator is a utility class which only task is
to generate a profile (default) profile entry derived
from legacy settings (i.e. authentication settings 
stored in application preferences) or for usage in
new app installations.
*/
class ProfileMigrator(private val ctx: Context) {
    
    /**
     @returns a profile item object populated with
     values derived from application preferences.
     */
    fun makeProfileUsingPreferences(): AuthProfileItem {
        
        val ai = AuthInfo()
    }
}
