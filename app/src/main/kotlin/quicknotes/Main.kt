package quicknotes

fun main() {
    val manager = NoteManager()

    println("Welcome to QuickNotes with SQLite!")

    while (true) {
        println(
            """
            |======= Quick Notes =======
            |1. Add Note
            |2. View All Notes
            |3. View Notes by Category
            |4. View Notes by Tag
            |5. View Note by ID
            |6. Edit Note
            |7. Delete Note
            |0. Exit
            |===========================
        """.trimMargin()
        )

        when (readLine()?.trim()) {
            "1" -> {
                print("Title: ")
                val title = readLine() ?: ""
                print("Content: ")
                val content = readLine() ?: ""
                print("Category: ")
                val category = readLine() ?: ""
                print("Tags (comma-separated): ")
                val tags = readLine()?.split(",")?.map { it.trim() } ?: emptyList()

                val note = Note(
                    title = title,
                    content = content,
                    category = category,
                    tags = tags
                )
                manager.addNote(note)
                println("Note added!\n")
            }

            "2" -> {
                val notes = manager.getAllNotes()
                if (notes.isEmpty()) println("No notes found.")
                else notes.forEach { printNote(it) }
            }

            "3" -> {
                print("Enter category to search: ")
                val category = readLine()?.trim() ?: ""
                val notes = manager.getAllNotes().filter { it.category.equals(category, ignoreCase = true) }
                if (notes.isEmpty()) println("No notes found in that category.")
                else notes.forEach { printNote(it) }
            }

            "4" -> {
                print("Enter tag to search: ")
                val tag = readLine()?.trim() ?: ""
                val notes = manager.getAllNotes().filter { it.tags.any { t -> t.equals(tag, ignoreCase = true) } }
                if (notes.isEmpty()) println("No notes found with that tag.")
                else notes.forEach { printNote(it) }
            }

            "5" -> {
                print("Enter note ID: ")
                val id = readLine()?.toIntOrNull()
                val note = manager.getAllNotes().find { it.id == id }
                if (note != null) printNote(note)
                else println("Note not found.")
            }

            "6" -> {
                print("Enter note ID to edit: ")
                val id = readLine()?.toIntOrNull()
                val existing = manager.getAllNotes().find { it.id == id }

                if (existing != null) {
                    print("New title [${existing.title}]: ")
                    val newTitle = readLine()?.takeIf { it.isNotBlank() } ?: existing.title

                    print("New content [${existing.content}]: ")
                    val newContent = readLine()?.takeIf { it.isNotBlank() } ?: existing.content

                    print("New category [${existing.category}]: ")
                    val newCategory = readLine()?.takeIf { it.isNotBlank() } ?: existing.category

                    print("New tags (comma-separated) [${existing.tags.joinToString()}]: ")
                    val tagInput = readLine()
                    val newTags = if (!tagInput.isNullOrBlank())
                        tagInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    else existing.tags

                    val updatedNote = existing.copy(
                        title = newTitle,
                        content = newContent,
                        category = newCategory,
                        tags = newTags,
                        updatedAt = currentTimestamp()
                    )
                    manager.updateNote(id!!, updatedNote)
                    println("Note updated.")
                } else {
                    println("Note not found.")
                }
            }

            "7" -> {
                print("Enter note ID to delete: ")
                val id = readLine()?.toIntOrNull()
                val note = manager.getAllNotes().find { it.id == id }
                if (note != null) {
                    print("Are you sure you want to delete '${note.title}'? (y/n): ")
                    if (readLine()?.lowercase() in listOf("y", "yes")) {
                        manager.deleteNote(id!!)
                        println("Note deleted.")
                    } else {
                        println("Delete canceled.")
                    }
                } else println("Note not found.")
            }

            "0" -> {
                manager.close()
                println("Goodbye!")
                break
            }

            else -> println("Invalid option.\n")
        }
    }
}
