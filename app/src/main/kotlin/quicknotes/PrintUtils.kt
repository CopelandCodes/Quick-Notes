package quicknotes

/**
 * Prints a formatted note to the console. Includes index number if provided.
 */
fun printNote(note: Note, index: Int? = null) {
    if (index != null) println("\n${index + 1}. ${note.title}")
    else println("\n${note.title}")

    println("Created At: ${note.createdAt}")
    if (note.updatedAt != note.createdAt) {
        println("Last Updated: ${note.updatedAt}")
    }
    println("Content: ${note.content}")
    println("Category: ${note.category}")
    println("Tags: ${note.tags.joinToString()}\n")
}