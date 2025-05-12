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
    var category: String = readLine()?.trim().takeUnless { it.isNullOrBlank()} ?: "General", // Sets a default category.
    var tags: List<String>,
    val createdAt: String = currentTimestamp(),
    var updatedAt: String = currentTimestamp()
)

/**
 * A class responsible for loading, saving, and managing all the notes.
 */
class NoteManager(private val notesFile: File) {
    // Load notes from file into memory when the class is initialized
    private val notes: MutableList<Note> = loadNotes()

    // Return all notes for display
    fun getAllNotes(): List<Note> = notes

    // Return a specific note by index, or null if out of range
    fun getNote(index: Int): Note? =
        if (index in 0 until notes.size) notes[index] else null

    // Add a new note to the list and save the updated list to file
    fun addNote(title: String, content: String, category: String, tags: List<String>) {
        notes.add(Note(title, content, category, tags))
        saveNotes()
    }

    // Update an existing note by index with new values if provided
    fun updateNote(index: Int, title: String?, content: String?, category: String?, tags: List<String>?) {
        val note = getNote(index) ?: return
        if (!title.isNullOrBlank()) note.title = title
        if (!content.isNullOrBlank()) note.content = content
        if (!category.isNullOrBlank()) note.category = category
        if (!tags.isNullOrEmpty()) note.tags = tags
        note.updatedAt = currentTimestamp()
        saveNotes()
    }

    // Remove a note by index and save the updated list
    fun deleteNote(index: Int): Boolean {
        if (index in 0 until notes.size) {
            notes.removeAt(index)
            saveNotes()
            return true
        }
        return false
    }

    // Return all notes that contain a category matching the search string
    fun searchByCategory(category: String): List<Note> =
        notes.filter { note -> note.category.equals(category, ignoreCase = true) }

    // Return all notes that contain a tag matching the search string
    fun searchByTag(tag: String): List<Note> =
        notes.filter { it -> it.tags.any { it.equals(tag, ignoreCase = true) } }

    /**
     * Saves a list of notes to a local text file.
     * If text file does not exist, one is created.
     * Fields are separated by '|' to simplify serialization.
     */
    private fun saveNotes() {
        notesFile.printWriter().use { out ->
            notes.forEach { note ->
                val line = listOf(
                    note.title.replace("|", "[PIPE]"),
                    note.content.replace("|", ""),
                    note.category.replace("|", ""),
                    note.tags.joinToString(",").replace("|", ""),
                    note.createdAt,
                    note.updatedAt
                ).joinToString("|")
                out.println(line)
            }
        }
    }

    /**
     * Reads notes from a local text file and converts them to a list of Note objects.
     * Returns an empty list if the file doesn't exist.
     */
    private fun loadNotes(): MutableList<Note> {
        if (!notesFile.exists()) return mutableListOf()

        return notesFile.readLines().map { line ->
            val parts = line.split("|")
            val title = parts[0].replace("[PIPE]", "|")
            val content = parts[1]
            val category = parts[2]
            val tagString = parts[3]
            val created = parts[4]
            val updated = parts[5]
            Note(
                title = title,
                content = content,
                category = category,
                tags = tagString.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                createdAt = created,
                updatedAt = updated
            )
        }.toMutableList()
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
 * Prints a formatted note to the console.
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
    println("Tags: ${note.tags.joinToString()}")
}

/**
 * Main function: Displays a menu for users to manage their notes
 * (create, view, search, edit, delete, and exit).
 */
fun main() {
    val manager = NoteManager(notesFile)

    while (true) {
        println(
            """
            |======= Quick Notes =======
            |1. Add Note
            |2. View All Notes
            |3. View Notes by Category
            |4. View Notes by Tag
            |5. View Note by Index
            |6. Edit Note
            |7. Delete Note
            |0. Exit
            |===========================
            """.trimMargin()
        )

        when (readLine()?.trim()) {
            "1" -> {
                print("Enter title: ")
                val title = readLine() ?: ""
                print("Enter content: ")
                val content = readLine() ?: ""
                print("Enter a category: ")
                val category = readLine() ?: ""
                print("Enter tags (comma-separated): ")
                val tagInput = readLine() ?: ""
                val tags = tagInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                manager.addNote(title, content, category, tags)
                println("Note added!\n")
            }

            "2" -> {
                println("All Notes:")
                manager.getAllNotes().forEachIndexed { index, note ->
                    printNote(note, index)
                }
            }

            "3" -> {
                print("Enter category to search: ")
                val category = readLine()?.trim() ?: ""
                val found = manager.searchByCategory(category).sortedBy { it.title.lowercase() } // Sort results alphabetically
                if (found.isEmpty()) println("No notes found.")
                else found.forEachIndexed { index, note ->
                    printNote(note, index)
                }
            }

            "4" -> {
                print("Enter tag to search: ")
                val tag = readLine()?.trim() ?: ""
                val found = manager.searchByTag(tag).sortedBy { it.title.lowercase() } // Sort results alphabetically
                if (found.isEmpty()) println("No notes found.")
                else found.forEachIndexed { index, note ->
                    printNote(note, index)
                }
            }

            "5" -> {
                print("Enter note number: ")
                val index = readLine()?.toIntOrNull()?.minus(1) ?: -1
                val note = manager.getNote(index)
                if (note != null) printNote(note)
                else println("Invalid note number.")
            }

            "6" -> {
                print("Enter note number to edit: ")
                val index = readLine()?.toIntOrNull()?.minus(1) ?: -1
                val existing = manager.getNote(index)
                if (existing != null) {
                    print("New title [${existing.title}]: ")
                    val newTitle = readLine()?.takeIf { it.isNotBlank() }
                    print("New content [${existing.content}]: ")
                    val newContent = readLine()?.takeIf { it.isNotBlank() }
                    print("New category [${existing.category}]: ")
                    val newCategory = readLine()?.takeIf { it.isNotBlank() }
                    print("New tags (comma-separated) [${existing.tags.joinToString()}]: ")
                    val newTagsInput = readLine()
                    val newTags = if (!newTagsInput.isNullOrBlank())
                        newTagsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() } else null
                    manager.updateNote(index, newTitle, newContent, newCategory, newTags)
                    println("Note updated.")
                } else println("Invalid note number.")
            }

            "7" -> {
                print("Enter note number to delete: ")
                val index = readLine()?.toIntOrNull()?.minus(1) ?: -1
                val target = manager.getNote(index)
                if (target != null) {
                    print("Delete '${target.title}'? (y/n): ")
                    val confirm = readLine()?.lowercase()
                    if (confirm == "y" || confirm == "yes") {
                        manager.deleteNote(index)
                        println("Note deleted.")
                    } else println("Canceled.")
                } else println("Invalid note number.")
            }

            "0" -> {
                println("Goodbye!")
                break
            }

            else -> println("\nInvalid option.\n")
        }
    }
}
