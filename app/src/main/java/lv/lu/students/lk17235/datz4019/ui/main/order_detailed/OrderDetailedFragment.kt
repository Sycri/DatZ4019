package lv.lu.students.lk17235.datz4019.ui.main.order_detailed

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import lv.lu.students.lk17235.datz4019.R
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

        val photoPickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            viewModel.afterPhotoClick(it)
        }

        with(binding) {
            editTextAddress.addTextChangedListener {
                viewModel.setOrderAddress(it.toString())
            }

            editTextComment.addTextChangedListener {
                viewModel.setOrderComment(it.toString())
            }

            imageButtonPhoto.setOnClickListener {
                viewModel.onPhotoClick()
            }

            buttonSave.setOnClickListener {
                viewModel.onOrderSaveClick()
            }
        }

        with(viewModel) {
            orderAddress.observe(viewLifecycleOwner) {
                if (binding.editTextAddress.text.toString() != it) {
                    binding.editTextAddress.setText(it)
                }

                binding.textInputLayoutAddress.error = when {
                    isAddressBlank -> getString(R.string.invalid_address)
                    else -> null
                }
            }

            orderComment.observe(viewLifecycleOwner) {
                if (binding.editTextComment.text.toString() != it) {
                    binding.editTextComment.setText(it)
                }
            }

            orderPhotoUri.observe(viewLifecycleOwner) {
                if (it == null) {
                    binding.imageButtonPhoto.setImageDrawable(null)
                } else {
                    Glide.with(binding.root)
                        .load(it)
                        .centerCrop()
                        .into(binding.imageButtonPhoto)
                }
            }

            orderUserCreated.observe(viewLifecycleOwner) {
                binding.editTextAddress.isEnabled = it
                binding.editTextComment.isEnabled = it
                binding.imageButtonPhoto.isEnabled = it
                binding.buttonSave.visibility = if (it) View.VISIBLE else View.GONE
            }

            loadingBarVisibility.observe(viewLifecycleOwner) {
                binding.loadingBar.visibility = it
            }

            isDataValid.observe(viewLifecycleOwner) {
                binding.buttonSave.isEnabled = it
            }

            navigateBack.observe(viewLifecycleOwner) {
                if (it) {
                    findNavController().navigateUp()
                    afterNavigateBack()
                }
            }

            choosingPhoto.observe(viewLifecycleOwner) {
                if (it) {
                    photoPickerLauncher.launch("image/*")
                }
            }
        }
    }
}
