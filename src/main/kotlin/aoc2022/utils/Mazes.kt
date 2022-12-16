package aoc2022.utils

class  MazeToTarget<T>(
    val to: T,
    val neighbours: (T) -> Iterable<T>
) {
    val distances = HashMap<T, Int>()
    fun distanceFrom(from: T): Int {
        return routeFrom(from).count()
    }

    private fun routeFrom(from: T): Sequence<T> {
        return generateSequence(from) { pos ->
            if (to != pos) {
                neighbours(pos).minByOrNull { distances[it]!! }!!
            }
            else { null}
        }
    }

    init {
        val queue = ArrayDeque<T>()
        queue.add(to)
        distances[to] = 0
        while(!queue.isEmpty()) {
            val pos = queue.removeFirst()
            val distance = distances[pos]!!
            neighbours(pos)
                .filter { it !in distances }
                .forEach {
                    queue.addLast(it)
                    distances[it] = distance + 1
                }
        }
    }

}