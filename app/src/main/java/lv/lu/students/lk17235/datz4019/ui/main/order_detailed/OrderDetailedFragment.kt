package lv.lu.students.lk17235.datz4019.ui.main.order_detailed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import lv.lu.students.lk17235.datz4019.databinding.FragmentOrderDetailedBinding

class OrderDetailedFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: OrderDetailedFragmentArgs by navArgs()

        val viewModel: OrderDetailedViewModel by viewModels() {
            OrderDetailedViewModelFactory(args.documentId)
        }

        with(viewModel) {
            orderTitle.observe(viewLifecycleOwner) {
                binding.textViewOrderTitle.text = it
            }

            orderDescription.observe(viewLifecycleOwner) {
                binding.textViewOrderDescription.text = it
            }

            orderPhotoUri.observe(viewLifecycleOwner) {
                Glide.with(binding.root)
                    .load(it)
                    .centerCrop()
                    .into(binding.imageViewOrderLogo)
            }
        }
    }
}
