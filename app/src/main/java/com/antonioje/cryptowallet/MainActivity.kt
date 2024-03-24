package com.antonioje.cryptowallet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.antonioje.cryptowallet.databinding.ActivityMainBinding
import com.antonioje.cryptowallet.home.ui.BuscarFragment
import com.antonioje.cryptowallet.home.ui.FavoriteListFragment
import com.antonioje.cryptowallet.home.list.ui.HomeFragment
import com.antonioje.cryptowallet.home.ui.PortfolioFragment
import com.antonioje.cryptowallet.home.ui.SettingsFragment
import com.antonioje.cryptowallet.login.ui.SignInFragment
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    val toolbar get() = binding.toolbar
    val bottomNavegationBar get() = binding.bottomNavegationBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        setAppBarGone()
        setBottomNavGone()
        setUpNavegationBar()
    }

    private fun setUpNavegationBar() {
        binding.bottomNavegationBar.setItemSelected(R.id.nav_market,true)
        binding.bottomNavegationBar.setOnItemSelectedListener {
            when(it){
                R.id.nav_market ->{
                    //binding.toolbar.title = getString(R.string.nav_market)
                    binding.toolbar.title = ""
                    setAppBarGone()
                    replaceFragment(HomeFragment())
                }
                R.id.nav_favorite -> {
                    binding.toolbar.title = getString(R.string.nav_favorite)
                    setAppBarVisible()
                    replaceFragment(FavoriteListFragment())
                }
                R.id.nav_buscar -> {
                    binding.toolbar.title = getString(R.string.nav_buscar)
                    setAppBarVisible()
                    replaceFragment(BuscarFragment())
                }
                R.id.nav_wallet -> {
                    binding.toolbar.title = getString(R.string.nav_wallet)
                    setAppBarVisible()
                    replaceFragment(PortfolioFragment())
                }
                R.id.nav_config -> {
                    binding.toolbar.title = getString(R.string.nav_config)
                    setAppBarVisible()
                    replaceFragment(SettingsFragment())
                }
            }
        }
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
        startActivity(intent)
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.constraintmain,fragment)
        fragmentTransaction.commit()
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
        const val GOOGLE_SIGN_IN  = 100
    }

}