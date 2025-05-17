package quicknotes

/**
 * Contains utility functions for displaying notes in a formatted and readable way.
 * Prints a single note to the console in a clean, readable format.
 * Omits the "Updated" field if the note has not been updated since creation.
 */
fun printNote(note: Note) {
    println("------------------------------------------------------")
    // Will show the creation date or a last modified date
    val dateLabel = if (note.updatedAt != note.createdAt) "Last Modified" else "Created"
    val dateValue = if (note.updatedAt != note.createdAt) note.updatedAt else note.createdAt
    println("ID: ${note.id}    $dateLabel: $dateValue")
    println("Title: ${note.title}")
    println("Category: ${note.category}")
    println("Content: ${note.content}\n")
    println("\nTags: ${note.tags.joinToString(", ")}")
    println("-----------------------------------------------------\n")
}
