package ir.salman.scale_ops.playground

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.salman.scale_ops.model.DIRECTION_STEP
import ir.salman.scale_ops.model.Move
import ir.salman.scale_ops.model.Toy
import ir.salman.scale_ops.model.randomMove
import ir.salman.scale_ops.utils.screenHeight
import ir.salman.scale_ops.utils.screenWidth
import ir.salman.scale_ops.widget.Widget
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.absoluteValue

@HiltViewModel
class PlaygroundViewModel @Inject constructor() : ViewModel() {

    private var toys: MutableList<Toy> = initToys()

    private val _initialToys = MutableStateFlow(toys)
    val initialToys = _initialToys.asStateFlow()

    private val _toysMove = MutableSharedFlow<List<Toy>>()
    val toysMove = _toysMove.asSharedFlow()

    private val _toDeleteToys = MutableSharedFlow<List<Toy>>()
    val toDeletedToys = _toDeleteToys.asSharedFlow()

    init {
        moveToysLoop()
    }

    private fun moveToysLoop() {
        viewModelScope.launch(IO) {
            delay(MOVE_THRESHOLD)

            viewModelScope.launch(Main) {
                moveToys()
                moveToysLoop()
            }
        }
    }

    private suspend fun moveToys() {
        val toDeleteToys = mutableListOf<Toy>()
        toys = toys
            .map { it.move() }
            .filter { toy ->
                // remove if widget is going to out of screen
                var condition = true

                // remove on crashes tow toy and
                for (possibleCrashToy in toys) {
                    if (crashHappen(toy, possibleCrashToy)) {
                        condition = toy.type <= possibleCrashToy.type

                        // check for bounce move direction
                        if (toy.type <= possibleCrashToy.type) {
                            toy.nextMove =
                                when (toy.prevMove) {
                                    Move.UP -> Move.DOWN
                                    Move.DOWN -> Move.UP
                                    Move.LEFT -> Move.RIGHT
                                    Move.RIGHT -> Move.LEFT
                                    // toys should not crash each other on initial state
                                    null -> throw RuntimeException("invalid prevMove value of toy")
                                }
                        }
                    }
                }

                // append to list for further remove from screen
                if (!condition) {
                    toDeleteToys.add(toy)
                }
                condition
            }.toMutableList()

        _toDeleteToys.emit(toDeleteToys)
        _toysMove.emit(toys)
    }
}

private const val MOVE_THRESHOLD = 500L

private fun initToys() =
    mutableListOf<Toy>().apply {
        addAll(
            (1..5).map { id ->
                createToyWith(
                    id = id,
                    type = Widget.Type.ROCK,
                    this
                )
            }
        )
        addAll(
            (6..10).map { id ->
                createToyWith(
                    id = id,
                    type = Widget.Type.SCISSOR,
                    this
                )
            }
        )
        addAll(
            (11..15).map { id ->
                createToyWith(
                    id = id,
                    type = Widget.Type.PAPER,
                    this
                )
            }
        )
    }

private fun createToyWith(id: Int, type: Widget.Type, toys: List<Toy>): Toy {
    val createdToy = Toy(
        id = id,
        type = type,
        x = randomWidth(),
        y = randomHeight()
    )

    // prevent that initial toys crash together
    var crashes = false
    for (toy in toys) {
        crashes = crashHappen(createdToy, toy)
        if (crashes) {
            break
        }
    }

    return if (crashes) {
        createToyWith(id, type, toys)
    } else {
        createdToy
    }
}


private fun Toy.move(): Toy {
    val move = when {
        nextMove != null -> nextMove
        x - DIRECTION_STEP < 0 -> Move.RIGHT
        x + Widget.SIZE + DIRECTION_STEP > screenWidth() -> Move.LEFT
        y - DIRECTION_STEP < 0 -> Move.DOWN
        y + Widget.SIZE + DIRECTION_STEP > screenHeight() -> Move.UP
        else -> randomMove()
    }

    val movedX = when (move) {
        Move.RIGHT -> x + DIRECTION_STEP
        Move.LEFT -> x - DIRECTION_STEP
        else -> x
    }

    val movedY = when (move) {
        Move.DOWN -> y + DIRECTION_STEP
        Move.UP -> y - DIRECTION_STEP
        else -> y
    }

    return copy(
        x = movedX,
        y = movedY,
        prevMove = move,
        nextMove = null
    )
}

private fun crashHappen(t1: Toy, t2: Toy) =
    t1.id != t2.id &&
            (t1.x - t2.x).absoluteValue <= Widget.SIZE &&
            (t1.y - t2.y).absoluteValue <= Widget.SIZE


private fun randomWidth() = widthRange().random()
private fun randomHeight() = heightRange().random()

private fun widthRange() = (0 until screenWidth() - Widget.SIZE)
private fun heightRange() = (0 until screenHeight() - Widget.SIZE)
