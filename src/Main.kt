import java.io.File // Imported to read and write text files
import java.time.LocalDateTime // For timestamps
import java.time.format.DateTimeFormatter // For formatting timestamps

// Immutable variable that defines the local filename for saving notes
val notesFile = File("notes.txt")

/**
 * A data class representing a Note with a title, content, tags,
 * and timestamps for creation and last update.
 */
data class Note(
    var title: String,
    var content: String,
    var tags: List<String>,
    val createdAt: String = currentTimestamp(),
    var updatedAt: String = currentTimestamp()
)

/**
 * Reads notes from a local text file and converts them to a list of Note objects.
 * Returns an empty list if the file doesn't exist.
 */
fun loadNotes(): MutableList<Note> {
    if (!notesFile.exists()) return mutableListOf()

    return notesFile.readLines().mapNotNull { line ->
        val parts = line.split("|")
        if (parts.size >= 5) {
            val (title, content, tagString, created, updated) = parts
            Note(
                title = title,
                content = content,
                tags = tagString.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                createdAt = created,
                updatedAt = updated
            )
        } else null
    }.toMutableList()
}

/**
 * Saves a list of notes to a local text file.
 * Fields are separated by '|' to simplify serialization.
 */
fun saveNotes(notes: List<Note>) {
    notesFile.printWriter().use { out ->
        notes.forEach { note ->
            val line = listOf(
                note.title.replace("|", ""),
                note.content.replace("|", ""),
                note.tags.joinToString(",").replace("|", ""),
                note.createdAt,
                note.updatedAt
            ).joinToString("|")
            out.println(line)
        }
    }
}

/**
 * Returns the current local date and time as a formatted string.
 */
fun currentTimestamp(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    return LocalDateTime.now().format(formatter)
}

/**
 * Main function: Displays a menu for users to manage their notes
 * (create, view, search, edit, delete, and exit).
 */
fun main() {
    val notes = loadNotes() // Load existing notes from file

    while (true) {
        // Display the main menu
        println(
            """
            |===== Note-Taking App =====
            |1. Add Note
            |2. View All Notes
            |3. View Notes by Tag
            |4. View Note by Index
            |5. Edit Note
            |6. Delete Note
            |0. Exit
            |===========================
            """.trimMargin()
        )

        when (readLine()?.trim()) {
            // Add a new note
            "1" -> {
                print("Enter title: ")
                val title = readLine() ?: ""
                print("Enter content: ")
                val content = readLine() ?: ""
                print("Enter tags (comma-separated): ")
                val tagInput = readLine() ?: ""
                val tags = tagInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }

                notes.add(Note(title, content, tags))
                saveNotes(notes)
                println("Note added!\n")
            }

            // View all notes
            "2" -> {
                println("All Notes:")
                notes.forEachIndexed { index, note ->
                    println("\n${index + 1}. ${note.title}")
                    println("Created At: ${note.createdAt}")
                    if (note.updatedAt != note.createdAt) {
                        println("Last Updated: ${note.updatedAt}")
                    }
                    println("Content: ${note.content}")
                    println("Tags: ${note.tags.joinToString()}\n")
                }
                println()
            }

            // Search and view notes by tag
            "3" -> {
                print("Enter tag to search: ")
                val tagSearch = readLine()?.trim()?.lowercase() ?: ""
                val filtered = notes.filter { note ->
                    note.tags.any { it.equals(tagSearch, ignoreCase = true) }
                }

                if (filtered.isEmpty()) {
                    println("No notes found with tag \"$tagSearch\"")
                    println("Returning to main menu\n")
                } else {
                    println("Notes with tag \"$tagSearch\":")
                    filtered.forEachIndexed { index, note ->
                        println("\n${index + 1}. ${note.title}")
                        println("Created At: ${note.createdAt}")
                        if (note.updatedAt != note.createdAt) {
                            println("Last Updated: ${note.updatedAt}")
                        }
                        println("Content: ${note.content}")
                        println("Tags: ${note.tags.joinToString()}\n")
                    }
                    println()
                }
            }

            // View a single note by index
            "4" -> {
                print("Enter note number to view: ")
                val index = readLine()?.toIntOrNull()
                if (index != null && index in 1..notes.size) {
                    val note = notes[index - 1]
                    println("\n${index + 1}. ${note.title}")
                    println("Created At: ${note.createdAt}")
                    if (note.updatedAt != note.createdAt) {
                        println("Last Updated: ${note.updatedAt}")
                    }
                    println("Content: ${note.content}")
                    println("Tags: ${note.tags.joinToString()}\n")

                } else {
                    println("\nInvalid number, returning to main menu.\n")
                }
            }

            // Edit a note by index
            "5" -> {
                print("Enter note number to edit: ")
                val index = readLine()?.toIntOrNull()
                if (index != null && index in 1..notes.size) {
                    val note = notes[index - 1]

                    print("New title [${note.title}]: ")
                    val newTitle = readLine()?.takeIf { it.isNotBlank() }

                    print("New content [${note.content}]: ")
                    val newContent = readLine()?.takeIf { it.isNotBlank() }

                    print("New tags (comma-separated) [${note.tags.joinToString()}]: ")
                    val newTagsInput = readLine()
                    if (!newTagsInput.isNullOrBlank()) {
                        val newTags = newTagsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        note.tags = newTags
                    }

                    if (newTitle != null) note.title = newTitle
                    if (newContent != null) note.content = newContent

                    note.updatedAt = currentTimestamp()
                    saveNotes(notes)

                    println("Note updated!\n")
                } else {
                    println("\nInvalid number, returning to main menu.\n")
                }
            }

            // Delete a note by index with confirmation
            "6" -> {
                if (notes.isEmpty()) {
                    println("No notes to delete.\n")
                } else {
                    println("Select a note to delete:")
                    notes.forEachIndexed { index, note ->
                        println("${index + 1}. ${note.title}")
                    }
                    print("Enter note number: ")
                    val index = readLine()?.toIntOrNull()

                    if (index != null && index in 1..notes.size) {
                        val target = notes[index - 1]
                        println("\nYou are about to delete: '${index + 1}. ${target.title}'")
                        print("Are you sure? (y/n): ")
                        val confirmation = readLine()?.trim()?.lowercase()

                        if (confirmation == "y" || confirmation == "yes") {
                            notes.removeAt(index - 1)
                            saveNotes(notes)
                            println("Note deleted.\n")
                        } else {
                            println("Deletion canceled.\n")
                        }
                    } else {
                        println("Invalid note number.\n")
                    }
                }
            }

            // Exit the program
            "0" -> {
                println("Goodbye!")
                break
            }

            // Invalid menu option handler
            else -> println("\nInvalid option.\n")
        }
    }
}
