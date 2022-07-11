package com.demon.apport.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.NetworkInfo
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.demon.apport.R
import com.demon.apport.data.Constants
import com.demon.apport.databinding.FragmentWifiStateBinding
import com.demon.apport.receiver.WifiReceiver
import com.demon.apport.util.WifiUtils
import com.demon.apport.util.dp2px
import com.jeremyliao.liveeventbus.LiveEventBus

/**
 * @author DeMonnnnnn
 * @date 2022/6/20
 * @email liu_demon@qq.com
 * @desc
 */
class WifiStateDialog : DialogFragment() {

    private var _binding: FragmentWifiStateBinding? = null
    protected val binding: FragmentWifiStateBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.run {
            isCancelable = false
            setCanceledOnTouchOutside(false)
            setCancelable(false)
        }
        _binding = FragmentWifiStateBinding.inflate(layoutInflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.run {
            setGravity(Gravity.BOTTOM)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            decorView.setPadding(10.dp2px, 10.dp2px, 10.dp2px, 10.dp2px)
        }

        binding.mBtnWifiCancel.setOnClickListener {
            dismissAllowingStateLoss()
        }
        binding.mBtnWifiSettings.setOnClickListener {
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
        }

        if (WifiUtils.getNetState(requireContext())) {
            onWifiConnected()
        } else {
            onWifiDisconnected()
        }

        LiveEventBus.get<Boolean>(Constants.WIFI_CONNECT_CHANGE_EVENT).observe(this) {
            changeState(it)
        }
    }

    private fun changeState(state: Boolean) {
        if (state) onWifiConnected()
        else onWifiDisconnected()
    }

    private fun onWifiDisconnected() {
        binding.run {
            mTxtTitle.setText(R.string.wlan_disabled)
            mTxtTitle.setTextColor(requireContext().resources.getColor(android.R.color.black))
            mTxtSubTitle.visibility = View.VISIBLE
            mImgLanState.setImageResource(R.drawable.shared_wifi_shut_down)
            mTxtStateHint.setText(R.string.fail_to_start_http_service)
            mTxtAddress.visibility = View.GONE
            mButtonSplitLine.visibility = View.VISIBLE
            mBtnWifiSettings.visibility = View.VISIBLE
        }
    }

    private fun onWifiConnecting() {
        binding.run {
            mTxtTitle.setText(R.string.wlan_enabled)
            mTxtTitle.setTextColor(requireContext().resources.getColor(R.color.colorWifiConnected))
            mTxtSubTitle.visibility = View.GONE
            mImgLanState.setImageResource(R.drawable.shared_wifi_enable)
            mTxtStateHint.setText(R.string.retrofit_wlan_address)
            mTxtAddress.visibility = View.GONE
            mButtonSplitLine.visibility = View.GONE
            mBtnWifiSettings.visibility = View.GONE
        }
    }

    private fun onWifiConnected() {
        binding.run {
            mTxtTitle.setText(R.string.wlan_enabled)
            mTxtTitle.setTextColor(requireContext().resources.getColor(R.color.colorWifiConnected))
            mTxtSubTitle.visibility = View.GONE
            mImgLanState.setImageResource(R.drawable.shared_wifi_enable)
            mTxtStateHint.setText(R.string.pls_input_the_following_address_in_pc_browser)
            mTxtAddress.visibility = View.VISIBLE
            val ipAddr = WifiUtils.getWifiIp(requireContext())
            mTxtAddress.text = String.format(requireContext().getString(R.string.http_address), ipAddr, Constants.HTTP_PORT)
            mButtonSplitLine.visibility = View.GONE
            mBtnWifiSettings.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    /**
     * 关闭弹窗的时候调用dismissAllowingStateLoss
     */
    open fun showAllowingState(manager: FragmentManager) {
        showAllowingState(manager, tag)
    }

    /**
     * 关闭弹窗的时候调用dismissAllowingStateLoss
     */
    open fun showAllowingState(manager: FragmentManager, tagStr: String? = null) {
        manager.beginTransaction().add(this, tagStr ?: tag).commitAllowingStateLoss()
    }
}