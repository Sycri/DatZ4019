package lv.lu.students.lk17235.datz4019.ui.main.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import lv.lu.students.lk17235.datz4019.databinding.FragmentOrdersBinding
import lv.lu.students.lk17235.datz4019.ui.main.OrderAdapter
import lv.lu.students.lk17235.datz4019.ui.main.OrderPagingSource

class OrdersFragment : Fragment() {
    private lateinit var binding: FragmentOrdersBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: OrdersViewModel by viewModels()

        val adapter = OrderAdapter(viewModel.viewModelScope, viewModel.orderRepository)
        binding.recyclerViewOrders.adapter = adapter

        val pagingConfig = PagingConfig(10, 10, false)
        val pager = Pager(pagingConfig) {
            OrderPagingSource(viewModel.orderRepository)
        }

        lifecycleScope.launch {
            pager.flow.collect {
                adapter.submitData(it)
            }
        }
    }
}
