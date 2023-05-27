package lv.lu.students.lk17235.datz4019.ui.main.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import lv.lu.students.lk17235.datz4019.databinding.FragmentOrdersBinding
import lv.lu.students.lk17235.datz4019.ui.main.OrderAdapter

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

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.flow.collectLatest {
                        adapter.submitData(it)
                    }
                }

                launch {
                    adapter.loadStateFlow.collectLatest {
                        binding.swipeRefreshLayout.isRefreshing = it.refresh is LoadState.Loading
                    }
                }
            }
        }
    }
}
