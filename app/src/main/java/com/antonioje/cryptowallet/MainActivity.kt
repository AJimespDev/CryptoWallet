package com.antonioje.cryptowallet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.antonioje.cryptowallet.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    val toolbar get() = binding.toolbar
    val bottomNavegationBar get() = binding.bottomNavegationBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("usu_preferencias", Context.MODE_PRIVATE)
        val language = sharedPreferences?.getString(LANGUAGE, null)

        if (language != null) {
            setLocale(language)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        NavigationUI.setupWithNavController(binding.bottomNavegationBar, navController)


        setAppBarGone()
        setBottomNavGone()
    }

    private fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    fun logOut() {
        //Obtener la instancia de SharedPreferences
        val sharedPreferences =
            this.getSharedPreferences("usu_preferencias", Context.MODE_PRIVATE)


        //Editar las preferencias
        val editor = sharedPreferences?.edit()
        editor?.clear()
        editor?.apply()

        FirebaseAuth.getInstance().signOut()

        finish()

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

        val locale = Locale.getDefault()
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        startActivity(intent)
    }

    fun restartActivity() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

        finish()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun setAppBarGone() {
        supportActionBar!!.hide()
    }

    fun setBottomNavGone(){
        binding.bottomNavegationBar.visibility = View.GONE
    }
    fun setBottomNavVisible(){
        binding.bottomNavegationBar.visibility = View.VISIBLE
    }

    fun setAppBarVisible() {
        supportActionBar!!.show()
        //OCULTO LA FLECHITA PARA RETROCEDER HACIA ATRAS
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

    }

    companion object{
        const val EMAIL = "email"
        const val PROVIDER = "provider"
        const val MANTENERSESION ="mantenersesion"
        const val LANGUAGE = "español"
        const val THEME = "theme"
        const val GOOGLE_SIGN_IN  = 100
    }

}