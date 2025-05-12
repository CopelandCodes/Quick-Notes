fun main() {
    val filename = "notes.json"
    val notes = loadNotes(filename)

    while (true) {
        println(
            """
            |Choose an option:
            |1. Add note
            |2. View all notes
            |3. View notes by tag
            |4. Save and Exit
            """.trimMargin()
        )

        when (readLine()?.trim()) {
            "1" -> {
                print("Title: ")
                val title = readLine() ?: ""
                print("Content: ")
                val content = readLine() ?: ""
                print("Tag: ")
                val tag = readLine() ?: ""
                notes.add(Note(title, content, tag))
            }
            "2" -> viewNotes(notes)
            "3" -> {
                print("Enter tag: ")
                val tag = readLine() ?: ""
                viewNotesByTag(notes, tag)
            }
            "4" -> {
                saveNotes(notes, filename)
                println("Notes saved. Goodbye!")
                break
            }
            else -> println("Invalid option.")
        }
    }
}
