package com.somesh.todoapp

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.somesh.todoapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var toolBar: Toolbar
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        drawerLayout = binding.drawerLayout
        navigationView = binding.navigationView
        toolBar = findViewById(R.id.toolbar)
        setSupportActionBar(toolBar)

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_home -> {
                    supportFragmentManager.beginTransaction().replace(R.id.content, HomeFragment())
                        .commit()
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.action_finished_task -> {
                    supportFragmentManager.beginTransaction().replace(R.id.content, FinishedTaskFragment())
                        .commit()
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.action_logout -> {
                    val sharedPreferences = getSharedPreferences(getString(R.string.USER_PREF), MODE_PRIVATE)
                    sharedPreferences.edit().clear().apply()
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                    finish()
                    true
                } else -> {
                    false
                }
            }
        }
        initDrawer()
    }

    private fun initDrawer() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.content, HomeFragment()).commit()
        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolBar, R.string.drawer_open, R.string.drawer_close)
        drawerToggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.white)
        drawerLayout.addDrawerListener(drawerToggle)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}