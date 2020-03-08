package com.example.architectureinaction.tasks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.architectureinaction.R
import com.example.architectureinaction.data.source.Injection
import com.example.architectureinaction.utils.replaceFragmentInActivity
import com.example.architectureinaction.utils.setupActionBar
import com.google.android.material.navigation.NavigationView

class TasksActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    private var filterType = TasksFilterType.ALL_TASKS

    private lateinit var presenter: TasksPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActionBar(R.id.toolbar) {
            setDisplayHomeAsUpEnabled(true) //显示返回键
            setHomeAsUpIndicator(R.drawable.ic_menu) //设置图标
        }

        drawerLayout = findViewById(R.id.drawer_layout)

        findViewById<NavigationView>(R.id.nav_view).setNavigationItemSelectedListener { item: MenuItem ->
            drawerLayout.closeDrawers()
            if (R.id.statistics == item.itemId) {
                //startActivity
            }
            true
        }

        val taskFragment = (supportFragmentManager.findFragmentById(R.id.contentFrame)
            ?: TasksFragment.newIntance().apply {
                replaceFragmentInActivity(this, R.id.contentFrame)
            })
        presenter = TasksPresenter(taskFragment as TasksFragment, Injection.provideTaskRepository(this))
        if (savedInstanceState != null) {
            filterType = savedInstanceState.getSerializable(CURRENT_FILTERING_TYPE) as TasksFilterType
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState.apply {
            putSerializable(CURRENT_FILTERING_TYPE, presenter.filterType)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        val CURRENT_FILTERING_TYPE = "current_filtering_type"
    }
}
