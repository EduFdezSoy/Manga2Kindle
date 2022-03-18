package es.edufdezsoy.manga2kindle.ui.onboarding.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.repository.SharedPreferencesHandler
import es.edufdezsoy.manga2kindle.databinding.FragmentScreen03Binding

class Screen03Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding = FragmentScreen03Binding.inflate(inflater, container, false)

        binding.nextBtn.setOnClickListener {
            findNavController().navigate(R.id.action_onBoardingFragment_to_mainFragment)
            onBoardingFinished(binding)
        }

        return binding.root
    }

    private fun onBoardingFinished(binding: FragmentScreen03Binding) {
        val pref = SharedPreferencesHandler(requireContext())
        pref.onBoarding = true
        pref.kindleEmail =
            binding.autoCompleteTextView.text.toString() // TODO: may check if this is an email
        view?.clearFocus()
    }
}