package com.antonioje.cryptowallet.home.settings.ui


import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavOptions
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.antonioje.cryptowallet.MainActivity
import com.antonioje.cryptowallet.R
import com.antonioje.cryptowallet.data.enum.DIVISAS
import com.antonioje.cryptowallet.data.enum.PROVIDERTYPE
import com.antonioje.cryptowallet.databinding.FragmentSettingsBinding
import com.antonioje.cryptowallet.login.ui.SignInFragment
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale


class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        initVariables()
        return binding.root
    }

    private fun initVariables() {
        val idiomas = arrayOf("Español", "English")
        var adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, idiomas)
        binding.spIdiomaCF.adapter = adapter

        val sharedPreferences =
            context?.getSharedPreferences("usu_preferencias", Context.MODE_PRIVATE)
        val email = sharedPreferences?.getString(MainActivity.EMAIL, null)
        val provider = sharedPreferences?.getString(MainActivity.PROVIDER, null)
        val language = sharedPreferences?.getString(MainActivity.LANGUAGE, null)

        if(email == null) {
            binding.tvTitlePreferences.visibility = View.GONE
            binding.cardViewIdioma.visibility = View.GONE
        }

        email?.let {
            binding.tvTitlePreferences.visibility = View.VISIBLE
            binding.cardViewIdioma.visibility = View.VISIBLE
            binding.tvEmailConfig.text = it.substring(0, it.indexOf("@"))
            binding.tvConfigEmail.text = it
        }

        provider?.let {
            binding.tvConfigProvider.text =
                if (it == "BASIC") getString(R.string.tvConfigProvider) else it
        }

        if (language != null) {
            val languageIndex = if (language == "en") 1 else 0
            binding.spIdiomaCF.setSelection(languageIndex)
        }

        binding.cardViewLogOut.setOnClickListener {
            (requireActivity() as MainActivity).logOut()
        }

        binding.spIdiomaCF.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val newLanguageCode = if (position == 0) "es" else "en"
                setLocale(newLanguageCode)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada
            }
        }

        binding.cardViewApi.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/es/api"))
            startActivity(intent)
        }

        binding.cardViewContacto.setOnClickListener {
            sendEmail()
        }

    }

    private fun sendEmail() {
        val recipient = "antonioje.dev@gmail.com"
        val subject = ""
        val message = ""

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // Solo aplicaciones de correo deberían manejar esto
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }

        startActivity(intent)

    }

    private fun setLocale(languageCode: String) {
        val sharedPreferences =
            context?.getSharedPreferences("usu_preferencias", Context.MODE_PRIVATE)
        var language = sharedPreferences?.getString(MainActivity.LANGUAGE, null)
        val editor = sharedPreferences?.edit()

        if (languageCode != language) {
            val locale = Locale(languageCode)
            val config = resources.configuration
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)

            editor?.putString(MainActivity.LANGUAGE, languageCode)
            editor?.apply()

            (requireActivity() as MainActivity).restartActivity()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}