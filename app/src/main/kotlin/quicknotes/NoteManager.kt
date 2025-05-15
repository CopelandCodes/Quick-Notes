package quicknotes

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

/**
 * NoteManager is responsible for all interactions with the SQLite database.
 * It provides methods to create, read, update, delete, and search notes.
 *
 * The database is initialized automatically on creation of the class instance,
 * and a connection is maintained until the `close()` method is called.
 */
class NoteManager(dbPath: String = "notes.db") {

    // Establishes a connection to the SQLite database
    private val connection: Connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")

    // Initializes the notes table if it doesn't already exist
    init {
        connection.createStatement().use { stmt ->
            stmt.executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS notes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    content TEXT NOT NULL,
                    category TEXT NOT NULL,
                    tags TEXT,
                    createdAt TEXT NOT NULL,
                    updatedAt TEXT NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    /**
     * Inserts a new note into the database.
     * Its ID is ignored as it's auto-generated.
     */
    fun addNote(note: Note) {
        val sql = """
            INSERT INTO notes (title, content, category, tags, createdAt, updatedAt)
            VALUES (?, ?, ?, ?, ?, ?)
        """.trimIndent()

        connection.prepareStatement(sql).use { stmt ->
            stmt.setString(1, note.title)
            stmt.setString(2, note.content)
            stmt.setString(3, note.category)
            stmt.setString(4, note.tags.joinToString(","))
            stmt.setString(5, note.createdAt)
            stmt.setString(6, note.updatedAt)
            stmt.executeUpdate()
        }
    }

    /**
     * Retrieves all notes from the database.
     * returns a list of all notes stored in the database.
     */
    fun getAllNotes(): List<Note> {
        val notes = mutableListOf<Note>()
        val sql = "SELECT id, title, content, category, tags, createdAt, updatedAt FROM notes"

        connection.createStatement().use { stmt ->
            val rs: ResultSet = stmt.executeQuery(sql)
            while (rs.next()) {
                notes.add(
                    Note(
                        id = rs.getInt("id"),
                        title = rs.getString("title"),
                        content = rs.getString("content"),
                        category = rs.getString("category"),
                        tags = rs.getString("tags")?.split(",")?.map { it.trim() } ?: emptyList(),
                        createdAt = rs.getString("createdAt"),
                        updatedAt = rs.getString("updatedAt")
                    )
                )
            }
        }

        return notes
    }

    /**
     * Updates an existing note in the database by ID.
     */
    fun updateNote(id: Int, updated: Note) {
        val sql = """
            UPDATE notes SET 
                title = ?, 
                content = ?, 
                category = ?, 
                tags = ?, 
                updatedAt = ?
            WHERE id = ?
        """.trimIndent()

        connection.prepareStatement(sql).use { stmt ->
            stmt.setString(1, updated.title)
            stmt.setString(2, updated.content)
            stmt.setString(3, updated.category)
            stmt.setString(4, updated.tags.joinToString(","))
            stmt.setString(5, updated.updatedAt)
            stmt.setInt(6, id)
            stmt.executeUpdate()
        }
    }

    /**
     * Retrieves a single note by its unique ID.
     * returns the note if found, or null if not found.
     */
    fun getNoteById(id: Int): Note? {
        val sql = "SELECT id, title, content, category, tags, createdAt, updatedAt FROM notes WHERE id = ?"
        connection.prepareStatement(sql).use { stmt ->
            stmt.setInt(1, id)
            val rs = stmt.executeQuery()
            return if (rs.next()) {
                Note(
                    id = rs.getInt("id"),
                    title = rs.getString("title"),
                    content = rs.getString("content"),
                    category = rs.getString("category"),
                    tags = rs.getString("tags")?.split(",")?.map { it.trim() } ?: emptyList(),
                    createdAt = rs.getString("createdAt"),
                    updatedAt = rs.getString("updatedAt")
                )
            } else null
        }
    }

    /**
     * Searches notes by category (case-insensitive).
     * returns a list of notes in the given category.
     */
    fun searchByCategory(category: String): List<Note> {
        return getAllNotes().filter {
            it.category.equals(category, ignoreCase = true)
        }
    }

    /**
     * Searches notes that contain a given tag (case-insensitive).
     * returns a list of notes that contain the tag.
     */
    fun searchByTag(tag: String): List<Note> {
        return getAllNotes().filter {
            it.tags.any { t -> t.equals(tag, ignoreCase = true) }
        }
    }

    /**
     * Searches notes for a keyword in the title or content (case-insensitive).
     * returns a list of matching notes.
     */
    fun searchByKeyword(keyword: String): List<Note> {
        return getAllNotes().filter {
            it.title.contains(keyword, ignoreCase = true) ||
                    it.content.contains(keyword, ignoreCase = true)
        }
    }

    /**
     * Deletes a note by its ID.
     */
    fun deleteNote(id: Int) {
        val sql = "DELETE FROM notes WHERE id = ?"
        connection.prepareStatement(sql).use { stmt ->
            stmt.setInt(1, id)
            stmt.executeUpdate()
        }
    }

    /**
     * Closes the database connection.
     * Needs to be called when the application is shutting down.
     */
    fun close() {
        connection.close()
    }
}
