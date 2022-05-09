package org.supla.android.widget.onoff
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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.supla.android.Preferences
import org.supla.android.db.Channel
import org.supla.android.db.DbHelper
import org.supla.android.lib.SuplaConst
import org.supla.android.profile.ProfileManager
import org.supla.android.widget.WidgetConfiguration
import org.supla.android.widget.WidgetPreferences
import java.security.InvalidParameterException

class OnOffWidgetConfigurationViewModel(private val preferences: Preferences,
                                        private val dbHelper: DbHelper,
                                        private val widgetPreferences: WidgetPreferences,
                                        private val profileManager: ProfileManager) : ViewModel() {

    private val _userLoggedIn = MutableLiveData<Boolean>()
    val userLoggedIn: LiveData<Boolean> = _userLoggedIn

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _confirmationResult = MutableLiveData<Result<Channel>>()
    val confirmationResult: LiveData<Result<Channel>> = _confirmationResult

    private val _channelsList = MutableLiveData<List<Channel>>()
    val channelsList: LiveData<List<Channel>> = _channelsList

    var selectedChannel: Channel? = null
    var widgetId: Int? = null
    var displayName: String? = null

    init {
        _dataLoading.value = true
        triggerDataLoad()
    }

    fun confirmSelection() {
        when {
            widgetId == null -> {
                _confirmationResult.value = Result.failure(InvalidParameterException())
            }
            selectedChannel == null -> {
                _confirmationResult.value = Result.failure(NoItemSelectedException())
            }
            displayName == null || displayName?.isBlank() == true -> {
                _confirmationResult.value = Result.failure(EmptyDisplayNameException())
            }
            else -> {
                setWidgetConfiguration(widgetId!!, selectedChannel!!.channelId, displayName!!, selectedChannel!!.func, selectedChannel!!.color)
                _confirmationResult.value = Result.success(selectedChannel!!)
            }
        }
    }

    fun onDisplayNameChanged(s: CharSequence, `_`: Int, `__`: Int, `___`: Int) {
        displayName = s.toString()
    }

    private fun triggerDataLoad() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val configSet = preferences.configIsSet()
                if (configSet) {
                    val switches = getAllChannels().filter {
                        it.func == SuplaConst.SUPLA_CHANNELFNC_LIGHTSWITCH
                                || it.func == SuplaConst.SUPLA_CHANNELFNC_DIMMER
                                || it.func == SuplaConst.SUPLA_CHANNELFNC_DIMMERANDRGBLIGHTING
                                || it.func == SuplaConst.SUPLA_CHANNELFNC_RGBLIGHTING
                                || it.func == SuplaConst.SUPLA_CHANNELFNC_POWERSWITCH
                    }
                    _channelsList.postValue(switches)
                    if (switches.isNotEmpty()) {
                        selectedChannel = switches[0]
                    }
                }

                _dataLoading.postValue(false)
                _userLoggedIn.postValue(configSet)
            }
        }
    }

    private fun getAllChannels(): List<Channel> {
        dbHelper.channelListCursor.use { cursor ->
            val channels = mutableListOf<Channel>()
            if (!cursor.moveToFirst()) {
                return channels
            }

            do {
                val channel = Channel()
                channel.AssignCursorData(cursor)
                channels.add(channel)
            } while (cursor.moveToNext())

            // As the widgets are stateless it is possible that user creates many widgets for the same channel id
            return channels
        }
    }

    private fun setWidgetConfiguration(widgetId: Int, channelId: Int, channelName: String,
                                       channelFunction: Int, channelColor: Int) {
        widgetPreferences.setWidgetConfiguration(widgetId, WidgetConfiguration(channelId, channelName, channelFunction, channelColor, profileManager.getCurrentProfile().id))
    }
}

class NoItemSelectedException : RuntimeException() {}

class EmptyDisplayNameException : RuntimeException() {}