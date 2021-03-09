package es.edufdezsoy.manga2kindle.ui.onboarding.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.repository.SharedPreferencesHandler
import kotlinx.android.synthetic.main.fragment_screen03.view.*

class Screen03Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_screen03, container, false)

        view.nextBtn.setOnClickListener {
            findNavController().navigate(R.id.action_onBoardingFragment_to_mainFragment)
            onBoardingFinished()
        }

        return view
    }

    private fun onBoardingFinished() {
        SharedPreferencesHandler(requireContext()).onBoarding = true
    }
}