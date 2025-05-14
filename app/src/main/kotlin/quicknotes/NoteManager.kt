package quicknotes

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

class NoteManager(dbPath: String = "notes.db") {
    private val connection: Connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")

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

    fun deleteNote(id: Int) {
        val sql = "DELETE FROM notes WHERE id = ?"
        connection.prepareStatement(sql).use { stmt ->
            stmt.setInt(1, id)
            stmt.executeUpdate()
        }
    }

    fun close() {
        connection.close()
    }
}
