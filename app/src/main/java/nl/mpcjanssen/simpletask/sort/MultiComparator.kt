package nl.mpcjanssen.simpletask.sort

import nl.mpcjanssen.simpletask.ActiveFilter
import nl.mpcjanssen.simpletask.Logger
import nl.mpcjanssen.simpletask.task.Task
import java.util.*
import kotlin.comparisons.then

class MultiComparator(sorts: ArrayList<String>, today: String, caseSensitve: Boolean, createAsBackup: Boolean) {
    var comparator : Comparator<Task>? = null

    var fileOrder = true

    init {
        val log = Logger

        label@ for (sort in sorts) {
            val parts = sort.split(ActiveFilter.SORT_SEPARATOR.toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            var reverse = false
            val sortType: String
            if (parts.size == 1) {
                // support older shortcuts and widgets
                reverse = false
                sortType = parts[0]
            } else {
                sortType = parts[1]
                if (parts[0] == ActiveFilter.REVERSED_SORT) {
                    reverse = true
                }
            }
            var comp : Comparator<Task>
            when (sortType) {
                "file_order" -> {
                    fileOrder = !reverse
                    break@label
                }
                "by_context" -> comp = ContextComparator(caseSensitve)
                "by_project" -> comp = ProjectComparator(caseSensitve)
                "alphabetical" -> comp = AlphabeticalComparator(caseSensitve)
                "by_prio" -> comp = PriorityComparator()
                "completed" -> comp = CompletedComparator()
                "by_creation_date" -> comp = CreationDateComparator()
                "in_future" -> comp = FutureComparator(today)
                "by_due_date" -> comp = DueDateComparator()
                "by_threshold_date" -> comp = ThresholdDateComparator(createAsBackup)
                "by_completion_date" -> comp = CompletionDateComparator()
                else -> {
                    log.warn("MultiComparator", "Unknown sort: " + sort)
                    continue@label
                }
            }
            if (reverse) {
                comp = CompReverser(comp)
            }
            comparator = comparator?.then(comp) ?: comp
        }
    }

}
