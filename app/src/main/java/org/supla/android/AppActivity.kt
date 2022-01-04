package org.supla.android

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

import android.os.Bundle
import android.os.Build
import android.view.WindowManager
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.fragment.NavHostFragment
import org.supla.android.databinding.ActivityAppBinding
import org.supla.android.ui.AppBar

class AppActivity: BaseActivity(), NavController.OnDestinationChangedListener {

    private lateinit var binding: ActivityAppBinding
    private lateinit var ctrl: NavController

    override fun onCreate(sis: Bundle?) {
        super.onCreate(sis)

        binding = DataBindingUtil.setContentView(this,
                                                 R.layout.activity_app)
        binding.lifecycleOwner = this
        val navToolbar: AppBar = binding.navToolbar
        setSupportActionBar(navToolbar)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
           window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
           window.setStatusBarColor(ResourcesCompat.getColor(getResources(),
               R.color.splash_bg, null));
        }
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        ctrl = navHostFragment.navController
        val cfg = AppBarConfiguration(ctrl.graph)
        NavigationUI.setupWithNavController(navToolbar,
                                            ctrl, cfg)

        ctrl.addOnDestinationChangedListener(this)
    }


    override fun onResume() {
        super.onResume()

        if(!Preferences(this).configIsSet()) {
            ctrl.popBackStack(R.id.mainScreen, true)
//            ctrl.clearBackStack(R.id.mainScreen)
        android.util.Log.i("SuplaNav", " nav stack entry: " +  ctrl.currentBackStackEntry)
            ctrl.navigate(R.id.cfgAuth)
       //    ctrl.navigate(R.id.action_main_auth)
        }

        configureNavBar()
    }

    override public fun onDestinationChanged(ctrl: NavController,
                                             dest: NavDestination,
                                             args: Bundle?) {
        var navBarVisible = true
        if(dest.id == R.id.cfgAuth) {
            val pm = SuplaApp.getApp().getProfileManager(this)
            if(!pm.getCurrentAuthInfo().isAuthDataComplete) {
                navBarVisible = false
            }
        }
        android.util.Log.i("SuplaNav", " dest changed to: " + dest)
        android.util.Log.i("SuplaNav", " nav stack entry: " +  ctrl.currentBackStackEntry)
//        binding.navToolbar.setVisibility(if(navBarVisible) View.VISIBLE else View.GONE)

configureNavBar()
    }

    private fun configureNavBar() {
        val cd = ctrl.currentDestination
        val pe = ctrl.previousBackStackEntry
        if(cd == null) { return }
        if(cd.id == R.id.mainScreen) {
            binding.navToolbar.setNavigationIcon(R.drawable.hamburger)
        } else {
            if(pe == null) {
                binding.navToolbar.setNavigationIcon(null)
            } else {
                binding.navToolbar.setNavigationIcon(R.drawable.navbar_back)
            }
        }
    }

}
