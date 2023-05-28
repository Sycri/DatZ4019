package lv.lu.students.lk17235.datz4019.ui.main.order_detailed

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import lv.lu.students.lk17235.datz4019.R
import lv.lu.students.lk17235.datz4019.databinding.FragmentOrderDetailedBinding
import lv.lu.students.lk17235.datz4019.ui.main.MainViewModel
import java.text.DateFormat
import java.util.Calendar

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

        val sharedViewModel: MainViewModel by activityViewModels()
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

            buttonPickupTime.setOnClickListener {
                val calendar = Calendar.getInstance()

                // Set the initial time to the current time plus one hour
                calendar.add(Calendar.HOUR_OF_DAY, 1)

                val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

                TimePickerDialog(requireContext(), R.style.AppTheme_TimePicker, { _, hour, minute ->
                    viewModel.setOrderPickupTime(hour, minute)
                }, currentHour, 0, true).show()
            }

            imageButtonPhoto.setOnClickListener {
                viewModel.onPhotoClick()
            }

            buttonDelete.setOnClickListener {
                viewModel.onOrderDeleteClick()
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

            orderPickupTime.observe(viewLifecycleOwner) {
                if (it == null) {
                    binding.textViewPickupTime.text = getString(R.string.prompt_pickup_time)
                } else {
                    binding.textViewPickupTime.text = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(it)
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
                with(binding) {
                    editTextAddress.isEnabled = it
                    editTextComment.isEnabled = it
                    buttonPickupTime.isEnabled = it
                    imageButtonPhoto.isEnabled = it
                    buttonDelete.visibility = if (it && orderId.value != null) View.VISIBLE else View.GONE
                    buttonSave.visibility = if (it) View.VISIBLE else View.GONE
                }
            }

            loadingBarVisibility.observe(viewLifecycleOwner) {
                binding.loadingBar.visibility = it
            }

            orderId.observe(viewLifecycleOwner) {
                binding.buttonDelete.visibility = if (it == null || orderUserCreated.value != true) View.GONE else View.VISIBLE
            }

            isDataValid.observe(viewLifecycleOwner) {
                binding.buttonSave.isEnabled = it
            }

            navigateBack.observe(viewLifecycleOwner) {
                if (it) {
                    sharedViewModel.forceRefreshOrders()
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
