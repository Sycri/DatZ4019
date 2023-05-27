package lv.lu.students.lk17235.datz4019.ui.main.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import lv.lu.students.lk17235.datz4019.databinding.FragmentSettingsBinding
import lv.lu.students.lk17235.datz4019.ui.main.MainViewModel

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedViewModel: MainViewModel by activityViewModels()
        val viewModel: SettingsViewModel by viewModels()

        sharedViewModel.isUserCourier.observe(viewLifecycleOwner) {
            binding.switchIsCourier.isChecked = it
        }

        binding.switchIsCourier.setOnCheckedChangeListener { _, isChecked ->
            sharedViewModel.setIsUserCourier(isChecked)
        }
    }
}
