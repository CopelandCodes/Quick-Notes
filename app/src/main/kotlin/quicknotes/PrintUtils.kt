package quicknotes

/**
 * Contains utility functions for displaying notes in a formatted and readable way.
 * Prints a single note to the console in a clean, readable format.
 * Omits the "Updated" field if the note has not been updated since creation.
 */
fun printNote(note: Note) {
    println("-------------------------------")
    println("ID: ${note.id}")
    println("Title: ${note.title}")
    println("Category: ${note.category}")
    println("Tags: ${note.tags.joinToString(", ")}")
    println("Created: ${note.createdAt}")

    // Only print 'Updated' if it's different from 'Created'
    if (note.updatedAt != note.createdAt) {
        println("Updated: ${note.updatedAt}")
    }

    println("Content: ${note.content}")
    println("-------------------------------\n")
}
