package ir.salman.scale_ops.playground

import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewGroup.MarginLayoutParams
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ir.salman.scale_ops.R
import ir.salman.scale_ops.databinding.FragmentPlaygroundBinding
import ir.salman.scale_ops.model.Toy
import ir.salman.scale_ops.utils.observeWithLifecycle
import ir.salman.scale_ops.widget.Widget
import ir.salman.ui.binding.BindingFragment

@AndroidEntryPoint
class PlaygroundFragment :
    BindingFragment<FragmentPlaygroundBinding>(R.layout.fragment_playground) {

    private val viewModel: PlaygroundViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
    }

    private fun initData() =
        viewModel.let { vm ->
            vm.initialToys.observeWithLifecycle(this) { initToys(it) }
            vm.toysMove.observeWithLifecycle(this) { moveToys(it) }
            vm.toDeletedToys.observeWithLifecycle(this) { deleteToys(it) }
        }

    private fun initToys(toys: List<Toy>) =
        toys.forEach { toy: Toy ->
            val prevView = binding.container.findViewById<Widget>(toy.id)
            prevView?.let { binding.container.removeView(it) }

            val toyWidget = Widget(context = requireContext())
            toyWidget.id = toy.id
            toyWidget.type = toy.type

            val lp = MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            lp.topMargin = toy.y
            lp.leftMargin = toy.x
            toyWidget.layoutParams = lp

            binding.container.addView(toyWidget)
        }

    private fun moveToys(toys: List<Toy>) =
        toys.forEach { toy: Toy ->
            val toyWidget = binding.container.findViewById<Widget>(toy.id)

            val lp = toyWidget.layoutParams as MarginLayoutParams
            lp.topMargin = toy.y
            lp.leftMargin = toy.x
            toyWidget.layoutParams = lp
        }

    private fun deleteToys(toys: List<Toy>) =
        toys.forEach { toy: Toy ->
            with(binding.container) {
                val toyWidget = findViewById<Widget>(toy.id)
                removeView(toyWidget)
            }
        }
}