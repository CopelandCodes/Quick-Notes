package quicknote

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Returns the current local date and time as a formatted string.
 */
fun currentTimestamp(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    return LocalDateTime.now().format(formatter)
}