import java.io.File

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

    // Return all notes that contain a category matching the search string (case-insensitive)
    fun searchByCategory(category: String): List<Note> =
        notes.filter { note -> note.category.equals(category, ignoreCase = true) }
            .sortedBy { it.title.lowercase() } // Sort results alphabetically by title

    // Return all notes that contain a tag matching the search string (case-insensitive)
    fun searchByTag(tag: String): List<Note> =
        notes.filter { it -> it.tags.any { it.equals(tag, ignoreCase = true) } }
            .sortedBy { it.title.lowercase() } // Sort results alphabetically by title

    /**
     * Saves a list of notes to a local text file.
     * If the text file does not exist, one is created.
     * Fields are separated by '|' to simplify serialization.
     * Private, can only be accessed within the noteManager
     */
    private fun saveNotes() {
        notesFile.printWriter().use { out ->
            notes.forEach { note ->
                val line = listOf(
                    note.title.replace("|", ""),
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
     * Private, can only be accessed within the noteManager
     */
    private fun loadNotes(): MutableList<Note> {
        if (!notesFile.exists()) return mutableListOf()

        return notesFile.readLines().map { line ->
            val parts = line.split("|")
            val title = parts[0]
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
