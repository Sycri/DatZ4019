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
import lv.lu.students.lk17235.datz4019.data.model.OrderModel
import lv.lu.students.lk17235.datz4019.databinding.ListItemOrderBinding

class OrderAdapter(
    private val viewModelScope: CoroutineScope,
    private val repository: OrderRepository
) : PagingDataAdapter<OrderModel, OrderViewHolder>(OrderModelComparator) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemOrderBinding.inflate(inflater, parent, false)
        return OrderViewHolder(binding, viewModelScope, repository)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = getItem(position)
        holder.bind(order)
    }
}

class OrderViewHolder(
    private val binding: ListItemOrderBinding,
    private val viewModelScope: CoroutineScope,
    private val repository: OrderRepository
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(order: OrderModel?) {
        if (order != null) {
            binding.textViewOrderName.text = order.name

            // Call getOrderPhotoUri to get the photo URI for the order
            order.photoFileName?.let { photoFileName ->
                viewModelScope.launch {
                    repository.getOrderPhotoUri(photoFileName)?.let { photoUri ->
                        // Load and display the order photo using the provided URI
                        Glide.with(binding.root)
                            .load(photoUri)
                            .centerCrop()
                            .into(binding.imageViewOrderLogo)
                    }
                }
            }
        } else {
            // Order is null, show placeholder or clear views
            binding.textViewOrderName.text = ""
            binding.imageViewOrderLogo.setImageDrawable(null)
        }
    }
}

object OrderModelComparator : DiffUtil.ItemCallback<OrderModel>() {
    override fun areItemsTheSame(oldItem: OrderModel, newItem: OrderModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: OrderModel, newItem: OrderModel): Boolean {
        return oldItem.name == newItem.name
            && oldItem.description == newItem.description
            && oldItem.photoFileName == newItem.photoFileName
    }
}