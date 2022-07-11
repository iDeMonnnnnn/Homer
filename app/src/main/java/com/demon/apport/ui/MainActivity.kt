package com.demon.apport.ui

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.demon.apport.R
import com.demon.apport.data.Constants
import com.demon.apport.data.InfoModel
import com.demon.apport.databinding.ActivityMainBinding
import com.demon.apport.receiver.WifiReceiver
import com.demon.apport.service.WebHelper
import com.demon.apport.service.WebService
import com.demon.apport.util.FileUtils
import com.demon.apport.util.LogUtils
import com.demon.apport.util.Tag
import com.demon.qfsolution.utils.getExternalOrFilesDir
import com.jeremyliao.liveeventbus.LiveEventBus

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var bind: ActivityMainBinding

    private val mApps: MutableList<InfoModel> = mutableListOf()

    private val adapter by lazy {
        FilesAdapter(mApps)
    }

    private val receiver by lazy {
        WifiReceiver()
    }

    private val filter by lazy {
        IntentFilter().apply {
            addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
            //addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        setSupportActionBar(bind.toolbar)

        registerReceiver(receiver, filter)

        initEventBus()
        initRecyclerView()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_setting -> {
                val intent = Intent(this@MainActivity, LogActivity::class.java)
                intent.putExtra("path", LogUtils.getTodayLog())
                startActivity(intent)
            }
            R.id.menu_wifi -> {
                LogUtils.wtf(Tag, "Server.isRunning=${WebHelper.instance.isConnected()}")
                WifiStateDialog().showAllowingState(supportFragmentManager)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.item_menu, menu) //加载menu布局
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        WebService.stop(this)
    }


    private fun initEventBus() {
        LiveEventBus.get<Int>(Constants.LOAD_BOOK_LIST).observe(this) {
            val listArr: MutableList<InfoModel> = FileUtils.getAllFiles(this@MainActivity)
            runOnUiThread {
                bind.refreshLayout.isRefreshing = false
                mApps.clear()
                mApps.addAll(listArr)
                adapter.notifyDataSetChanged()
            }
        }

        LiveEventBus.get<Boolean>(Constants.WIFI_CONNECT_CHANGE_EVENT).observe(this) {
            if (it) {
                WebService.start(this)
            } else {
                WebService.stop(this)
            }
        }
    }


    private fun initRecyclerView() {
        bind.run {
            list.setHasFixedSize(true)
            list.layoutManager = LinearLayoutManager(this@MainActivity)
            list.adapter = adapter
            LiveEventBus.get<Int>(Constants.LOAD_BOOK_LIST).post(0)
            bind.refreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
            )
            bind.refreshLayout.setOnRefreshListener { LiveEventBus.get<Int>(Constants.LOAD_BOOK_LIST).post(0) }
        }
    }


}