//package com.krashkrosh748199.GmVibe
//
//import android.content.Intent
//import android.os.Bundle
//import com.google.android.material.bottomnavigation.BottomNavigationView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.fragment.app.Fragment
//import com.krashkrosh748199.GmVibe.Fragments.HomeFragment
//import com.krashkrosh748199.GmVibe.Fragments.NotificationFragment
//import com.krashkrosh748199.GmVibe.Fragments.ProfileFragment
//import com.krashkrosh748199.GmVibe.Fragments.SearchFragment
//
//class MainActivity : AppCompatActivity() {
//
//
//    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
//        when (item.itemId) {
//            R.id.nav_home -> {
//                moveToFragment(HomeFragment())
//                return@OnNavigationItemSelectedListener true
//            }
//            R.id.nav_search -> {
//                moveToFragment(SearchFragment())
//                return@OnNavigationItemSelectedListener true
//            }
//            R.id.nav_add_post -> {
//                item.isChecked = false
//                startActivity(Intent(this@MainActivity,AddPostActivity::class.java))
//                return@OnNavigationItemSelectedListener true
//            }
//            R.id.nav_notifications -> {
//                moveToFragment(NotificationFragment())
//                return@OnNavigationItemSelectedListener true
//            }
//            R.id.nav_profile -> {
//                moveToFragment(ProfileFragment())
//                return@OnNavigationItemSelectedListener true
//            }
//        }
//
//        false
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        val navView: BottomNavigationView = findViewById(R.id.nav_view)
//
//        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
//        moveToFragment(HomeFragment())
//
//    }
//
//    private fun moveToFragment(fragment: Fragment){
//        val fragmentTrans = supportFragmentManager.beginTransaction()
//        fragmentTrans.replace(R.id.fragment_container,fragment)
//        fragmentTrans.commit()
//    }
//
//}
package com.krashkrosh748199.GmVibe

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.krashkrosh748199.GmVibe.Fragments.HomeFragment
import com.krashkrosh748199.GmVibe.Fragments.NotificationFragment
import com.krashkrosh748199.GmVibe.Fragments.ProfileFragment
import com.krashkrosh748199.GmVibe.Fragments.SearchFragment

class MainActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                moveToFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_search -> {
                moveToFragment(SearchFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_add_post -> {
                item.isChecked = false
                startActivity(Intent(this@MainActivity, AddPostActivity::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_notifications -> {
                moveToFragment(NotificationFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profile -> {
                moveToFragment(ProfileFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Hide the status bar and navigation bar for a full-screen experience
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                )

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        moveToFragment(HomeFragment())
    }

    private fun moveToFragment(fragment: Fragment) {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragment_container, fragment)
        fragmentTrans.commit()
    }
}
