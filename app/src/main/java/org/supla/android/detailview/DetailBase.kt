package org.supla.android.detailview
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
import android.widget.FrameLayout
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.View
import androidx.lifecycle.Observer
import androidx.databinding.DataBindingUtil
import org.supla.android.db.ChannelBase
import org.supla.android.NavigationActivity
import org.supla.android.R

import org.supla.android.databinding.CountdownTimerViewBinding
import kotlin.properties.Delegates

open class DetailBase @JvmOverloads constructor(
    private val ctx: Context, 
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0): FrameLayout(ctx) {

    private var countdownTimerViewBinding: CountdownTimerViewBinding? = null

    public var channelBase: ChannelBase? = null
    
    public  var baseViewModel: DetailViewModel? by Delegates.observable(null) 
    { p, o, n -> n?.countDownTimerViewActive?.observe(ctx as NavigationActivity,
       Observer { act -> countdownTimerViewBinding?.root?.setVisibility(if(act) View.VISIBLE else View.GONE)
       })
    }
                                                                                     

    open fun setData(cbase: ChannelBase) {
        channelBase = cbase
    }

    open fun onDetailShow() {
        if(ctx is NavigationActivity &&
           hasCountdownTimerView) {
//            val parent = getParent() as ViewGroup
            if(countdownTimerViewBinding == null) {
                val binding: CountdownTimerViewBinding = 
                    DataBindingUtil.inflate(
                        ctx.getLayoutInflater(),
                        R.layout.countdown_timer_view,
                        this, false)
                binding.lifecycleOwner = ctx
                binding.root.setVisibility(View.GONE)
                binding.root.setLayoutParams(
                    LayoutParams(LayoutParams.MATCH_PARENT,
                                 LayoutParams.MATCH_PARENT))
                addView(binding.root)
                countdownTimerViewBinding = binding
            }
            ctx.setCounterButtonVisible(true)
        }
    }

    open fun onDetailHide() {
        if(ctx is NavigationActivity) {
            ctx.setCounterButtonVisible(false)
            val binding = countdownTimerViewBinding
            if(binding != null) {
                removeView(binding.root)
                countdownTimerViewBinding = null
            }
        }
    }

    val hasCountdownTimerView: Boolean
        get() = channelBase!!.supportsCountdownTimer()
}
