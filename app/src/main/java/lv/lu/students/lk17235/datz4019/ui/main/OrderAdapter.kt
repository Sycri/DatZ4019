package lv.lu.students.lk17235.datz4019.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import lv.lu.students.lk17235.datz4019.data.OrderRepository
import lv.lu.students.lk17235.datz4019.data.model.Order
import lv.lu.students.lk17235.datz4019.databinding.ListItemOrderBinding

class OrderAdapter(
    private val viewModelScope: CoroutineScope,
    private val repository: OrderRepository,
    private val clickListener: (String) -> Unit
) : PagingDataAdapter<Order, OrderAdapter.OrderViewHolder>(Companion) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemOrderBinding.inflate(inflater, parent, false)
        return OrderViewHolder(binding, viewModelScope, repository)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = getItem(position)
        holder.bind(order)
    }

    companion object : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }

    inner class OrderViewHolder(
        private val binding: ListItemOrderBinding,
        private val viewModelScope: CoroutineScope,
        private val repository: OrderRepository
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order?) {
            if (order != null) {
                binding.textViewAddress.text = order.address

                // Call getOrderPhotoUri to get the photo URI for the order
                order.photoFileName?.let { photoFileName ->
                    viewModelScope.launch {
                        repository.getOrderPhotoUri(photoFileName)?.let { photoUri ->
                            // Load and display the order photo using the provided URI
                            Glide.with(binding.root)
                                .load(photoUri)
                                .centerCrop()
                                .into(binding.imageViewPhoto)
                        }
                    }
                }

                binding.constraintLayoutItemOrder.setOnClickListener {
                    clickListener(order.id!!)
                }
            } else {
                // Order is null, show placeholder or clear views
                binding.textViewAddress.text = ""
                binding.imageViewPhoto.setImageDrawable(null)
            }
        }
    }
}
