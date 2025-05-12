data class Note(
    var title: String,
    var content: String,
    var tags: List<String>
)

fun main() {
    val notes = mutableListOf<Note>()

    while (true) {
        println(
            """
            |===== Note-Taking App =====
            |1. Add Note
            |2. View All Notes
            |3. View Notes by Tag
            |4. View Note by Index
            |5. Edit Note
            |6. Exit
            |===========================
            """.trimMargin()
        )

        when (readLine()?.trim()) {
            "1" -> {
                print("Enter title: ")
                val title = readLine() ?: ""
                print("Enter content: ")
                val content = readLine() ?: ""
                print("Enter tags (comma-separated): ")
                val tagInput = readLine() ?: ""
                val tags = tagInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }

                notes.add(Note(title, content, tags))
                println(" Note added!\n")
            }

            "2" -> {
                println("All Notes:")
                notes.forEachIndexed { index, note ->
                    println("${index + 1}. ${note.title} [${note.tags.joinToString()}]")
                }
                println()
            }

            "3" -> {
                print("Enter tag to search: ")
                val tagSearch = readLine()?.trim()?.lowercase() ?: ""
                val filtered = notes.filter { note ->
                    note.tags.any { it.equals(tagSearch, ignoreCase = true) }
                }

                if (filtered.isEmpty()) {
                    println(" No notes found with tag \"$tagSearch\"\n")
                } else {
                    println("Notes with tag \"$tagSearch\":")
                    filtered.forEach { note ->
                        println("- ${note.title}: ${note.content} [${note.tags.joinToString()}]")
                    }
                    println()
                }
            }

            "4" -> {
                print("Enter note number to view: ")
                val index = readLine()?.toIntOrNull()
                if (index != null && index in 1..notes.size) {
                    val note = notes[index - 1]
                    println("Title: ${note.title}")
                    println("Content: ${note.content}")
                    println("Tags: ${note.tags.joinToString()}\n")
                } else {
                    println(" Invalid index.\n")
                }
            }

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
                    val newTags = newTagsInput?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }

                    if (newTitle != null) note.title = newTitle
                    if (newContent != null) note.content = newContent
                    if (newTags != null) note.tags = newTags

                    println(" Note updated!\n")
                } else {
                    println(" Invalid index.\n")
                }
            }

            "6" -> {
                println(" Goodbye!")
                break
            }

            else -> println("Invalid option.\n")
        }
    }
}
