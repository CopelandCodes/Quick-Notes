package quicknotes

/**
 * Data class representing a Note with a title, content, category, tags,
 * and timestamps for creation and last update.
 */
data class Note(
    val id: Int? = null, // DB ID, used as the note ID number
    var title: String,
    var content: String,
    var category: String = "General",
    var tags: List<String>,
    val createdAt: String = currentTimestamp(),
    var updatedAt: String = currentTimestamp()
)
