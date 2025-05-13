package quicknote

/**
 * Data class representing a Note with a title, content, category, tags,
 * and timestamps for creation and last update.
 */
data class Note(
    var title: String, // Title of the note
    var content: String, // Content or body of the note
    var category: String = "General", // Optional category for organizing notes
    var tags: List<String>, // List of tags associated with the note
    val createdAt: String = currentTimestamp(), // Timestamp when the note was created
    var updatedAt: String = currentTimestamp() // Timestamp of the last update to the note
)
