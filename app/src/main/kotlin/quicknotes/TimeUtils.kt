package quicknotes

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun currentTimestamp(): String {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}
