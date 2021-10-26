package org.supla.android.cfg

import android.content.Context
import android.app.Activity
import android.os.Bundle
import android.os.Build
import android.graphics.Typeface
import android.view.inputmethod.InputMethodManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import org.supla.android.R
import org.supla.android.SuplaApp
import org.supla.android.databinding.FragmentAuthBinding

class AuthFragment: Fragment() {
        private val viewModel: CfgViewModel by activityViewModels()
    private lateinit var binding: FragmentAuthBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth,
					  container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.cfgAdvanced.viewModel = viewModel
        binding.cfgBasic.viewModel = viewModel

	      var type = SuplaApp.getApp().typefaceOpenSansRegular

        arrayOf(binding.cfgAdvanced.edServerAddr,
                binding.cfgAdvanced.edServerAddrEmail,
                binding.cfgAdvanced.edAccessID,
		            binding.cfgAdvanced.edAccessIDpwd, 
                binding.cfgAdvanced.cfgEmail,
		            /*binding.cfgAdvanced.cfgProfileName!!,*/ 
                binding.cfgBasic.cfgEmail)
            .forEach {
                it.setOnFocusChangeListener { v, hasFocus ->
                   val createAccountVisibility: Int
                   if(hasFocus) { 
                      createAccountVisibility = View.GONE
                   } else {
                      hideKeyboard(v)
                      createAccountVisibility = View.VISIBLE
                   }
                   arrayOf(binding.dontHaveAccountText,
                           binding.cfgCreateAccount).forEach {
                       it.visibility = createAccountVisibility
                   }
                }
                it.setTypeface(type)
            }
        arrayOf(binding.cfgBasic.cfgLabelEmail,
		            binding.cfgAdvanced.cfgLabelEmail,
                binding.cfgAdvanced.cbAutoLabel,
		            binding.cfgAdvanced.cfgLabelSvrAddress,
                binding.cfgAdvanced.addDeviceWarning,
                binding.cfgCreateAccount,
                binding.dontHaveAccountText,
                binding.cfgCbAdvanced).forEach {
            it.setTypeface(type)
        }
        binding.cfgCreateAccount.setTypeface(type, Typeface.BOLD)

        type = SuplaApp.getApp().typefaceQuicksandRegular
        arrayOf(binding.cfgBasic.cfgLabelTitleBasic).forEach {
            it.setTypeface(type)
        }


        if(viewModel.authByEmail.value ?: false) {
            binding.cfgAdvanced.authType.position = 0
        } else {
            binding.cfgAdvanced.authType.position = 1
        }

        binding.cfgAdvanced.authType.setOnPositionChangedListener() { 
            pos -> viewModel.selectEmailAuth(pos == 0)
        }

        return binding.root
    }

    fun hideKeyboard(v: View) {
        val service = SuplaApp.getApp().getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
        service?.let { it.hideSoftInputFromWindow(v.windowToken, 0) }
    }
}
