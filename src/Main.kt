package quicknote

/**
 * Main function: Displays a menu for users to manage their notes.
 * Offers options to create, view, search, edit, and delete notes.
 * Notes can be searched by category or by tags.
 */
fun main() {
    // Creates the noteManager and populates it with the data in the notes.txt file if it exists.
    val manager = NoteManager(notesFile)

    while (true) {
        // Print the interactive menu
        println(
            /**
             * Triple quotes are used for raw strings which can span multiple lines, they do not
             * require escape characters, and all formating and indentations are passed on.
             */
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
            """.trimMargin() // Trims all whitespace to align text to the left. (remove indentations)
        )

        // Handle user selection from the menu
        when (readLine()?.trim()) {
            "1" -> {
                // Prompt user for new note details
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
                // Show all existing notes
                println("All Notes:")
                manager.getAllNotes().forEachIndexed { index, note ->
                    printNote(note, index)
                }
            }

            "3" -> {
                // Search and display notes by category
                print("Enter category to search: ")
                val category = readLine()?.trim() ?: ""
                val found = manager.searchByCategory(category)
                if (found.isEmpty()) println("No notes found.")
                else found.forEachIndexed { index, note ->
                    printNote(note, index)
                }
            }

            "4" -> {
                // Search and display notes by tag
                print("Enter tag to search: ")
                val tag = readLine()?.trim() ?: ""
                val found = manager.searchByTag(tag)
                if (found.isEmpty()) println("No notes found.")
                else found.forEachIndexed { index, note ->
                    printNote(note, index)
                }
            }

            "5" -> {
                // View a note by its index number
                print("Enter note number: ")
                val index = readLine()?.toIntOrNull()?.minus(1) ?: -1
                val note = manager.getNote(index)
                if (note != null) printNote(note)
                else println("Invalid note number.")
            }

            "6" -> {
                // Edit an existing note
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
                // Delete a note with confirmation
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
                // Exit the program
                println("Goodbye!")
                break
            }

            else -> println("\nInvalid option.\n")
        }
    }
}
