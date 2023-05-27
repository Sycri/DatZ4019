package lv.lu.students.lk17235.datz4019.ui.main.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import lv.lu.students.lk17235.datz4019.databinding.FragmentOrdersBinding
import lv.lu.students.lk17235.datz4019.ui.main.OrderAdapter
import lv.lu.students.lk17235.datz4019.ui.main.OrderPagingSource

class OrdersFragment : Fragment() {
    private lateinit var binding: FragmentOrdersBinding
    private lateinit var viewModel: OrdersViewModel
    private lateinit var adapter: OrderAdapter

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

        viewModel = ViewModelProvider(this)[OrdersViewModel::class.java]

        adapter = OrderAdapter(viewModel.viewModelScope, viewModel.orderRepository)
        binding.recyclerViewOrders.adapter = adapter
        binding.swipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
        }

        val pagingConfig = PagingConfig(10, 10, false)
        val pager = Pager(pagingConfig) {
            OrderPagingSource(viewModel.orderRepository)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    pager.flow.collect {
                        adapter.submitData(it)
                    }
                }

                adapter.loadStateFlow
                    .distinctUntilChangedBy { it.refresh }
                    .collect { loadState ->
                        binding.swipeRefreshLayout.isRefreshing = loadState.refresh is LoadState.Loading
                    }
            }
        }
    }
}
