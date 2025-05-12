import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Note(
    var title: String,
    var content: String,
    var tags: List<String>,
    val createdAt: String = currentTimestamp(),
    var updatedAt: String = currentTimestamp()
)

fun currentTimestamp(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    return LocalDateTime.now().format(formatter)
}


val notesFile = File("notes.txt")

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

            "7" -> {
                if (notes.isEmpty()) {
                    println("No notes to delete.\n")
                } else {
                    println("Select a note to delete:")
                    notes.forEachIndexed { index, note ->
                        println("${index + 1}. ${note.title} [${note.tags.joinToString()}]")
                    }
                    print("Enter note number: ")
                    val index = readLine()?.toIntOrNull()

                    if (index != null && index in 1..notes.size) {
                        val removed = notes.removeAt(index - 1)
                        saveNotes(notes)
                        println(" Deleted note: '${removed.title}'\n")
                    } else {
                        println(" Invalid note number.\n")
                    }
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
