package quicknotes

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Provides utility functions for working with date and time values,
 * such as generating a timestamp string in a standard format for storage and display.
 *
 * Returns the current date and time as a formatted string.
 *
 * Format: yyyy-MM-dd HH:mm:ss (e.g., 2025-05-14 22:47:00)
 *
 * This format is compatible with SQLite's TEXT datetime columns
 * and is human-readable while maintaining natural sorting order.
 *
 * Returns a string representing the current local timestamp for the createdAt date
 * and the updatedAt date for each note.
 */
fun currentTimestamp(): String {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}
