import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * A menu-driven notes app built on the Notebook class.
 * Notes have a category and a date, can be pinned to the top,
 * and are saved to notes.txt so they are still there next time.
 *
 * Each note is stored as one line of text, like:
 *   [PINNED] [School] Finish OOPDA homework (Oct 12, 2026)
 *
 * @author Thomas Sanchez
 */
public class NotesApp {

    private static final String FILE_NAME = "notes.txt";
    private static final String PIN_MARK = "[PINNED] ";
    private static final String[] CATEGORIES = {"School", "Work", "Personal", "Other"};
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy");

    private static Scanner input = new Scanner(System.in);
    private static Notebook notebook = new Notebook();

    public static void main(String[] args) {
        try {
            notebook.loadFromFile(FILE_NAME);
        } catch (IOException e) {
            System.out.println("Could not load saved notes. Starting fresh.");
        }

        System.out.println("=== Notes App ===");
        System.out.println("Loaded " + notebook.numberOfNotes() + " saved note(s).");

        boolean running = true;

        while (running) {
            System.out.println("\n1. View all notes (" + notebook.numberOfNotes() + ")");
            System.out.println("2. Add a note");
            System.out.println("3. Edit a note");
            System.out.println("4. Delete a note");
            System.out.println("5. Search notes");
            System.out.println("6. View by category");
            System.out.println("7. Pin / unpin a note");
            System.out.println("8. Move a note to the top");
            System.out.println("9. Move a note up one");
            System.out.println("10. Move a note down one");
            System.out.println("11. Delete all notes");
            System.out.println("0. Save and quit");

            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1:
                    viewNotes();
                    break;
                case 2:
                    addNote();
                    break;
                case 3:
                    editNote();
                    break;
                case 4:
                    deleteNote();
                    break;
                case 5:
                    searchNotes();
                    break;
                case 6:
                    viewByCategory();
                    break;
                case 7:
                    togglePin();
                    break;
                case 8:
                case 9:
                case 10:
                    moveNote(choice);
                    break;
                case 11:
                    deleteAll();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("That's not an option. Try again.");
            }

            keepPinnedOnTop();
            save();
        }

        System.out.println("Notes saved. Goodbye!");
    }

    private static void viewNotes() {
        if (notebook.numberOfNotes() == 0) {
            System.out.println("You don't have any notes yet.");
            return;
        }
        for (int i = 0; i < notebook.numberOfNotes(); i++) {
            System.out.println((i + 1) + ". " + notebook.getNote(i));
        }
    }

    private static void addNote() {
        String text = readLine("Type your note: ");
        if (text.isEmpty()) {
            System.out.println("Empty notes aren't saved.");
            return;
        }
        String category = chooseCategory();
        String note = buildNote(text, category);
        if (notebook.addNote(note)) {
            System.out.println("Added: " + note);
        } else {
            System.out.println("That note already exists.");
        }
    }

    private static void editNote() {
        viewNotes();
        int position = readInt("Number of the note to edit: ") - 1;
        String oldNote = notebook.getNote(position);
        if (oldNote == null) {
            System.out.println("No note with that number.");
            return;
        }
        String newText = readLine("New text: ");
        if (newText.isEmpty()) {
            System.out.println("Edit cancelled. Notes can't be empty.");
            return;
        }
        String category = chooseCategory();
        String newNote = buildNote(newText, category);
        if (oldNote.startsWith(PIN_MARK)) {
            newNote = PIN_MARK + newNote;
        }
        notebook.setNote(position, newNote);
        System.out.println("Updated: " + newNote);
    }

    private static void deleteNote() {
        viewNotes();
        int position = readInt("Number of the note to delete: ") - 1;
        if (notebook.deleteNote(position)) {
            System.out.println("Note deleted.");
        } else {
            System.out.println("No note with that number.");
        }
    }

    private static void searchNotes() {
        String keyword = readLine("Search for: ");
        printList(notebook.searchNotes(keyword), "No notes contain \"" + keyword + "\".");
    }

    private static void viewByCategory() {
        String category = chooseCategory();
        printList(notebook.searchNotes("[" + category + "]"), "No " + category + " notes yet.");
    }

    private static void togglePin() {
        viewNotes();
        int position = readInt("Number of the note to pin or unpin: ") - 1;
        String note = notebook.getNote(position);
        if (note == null) {
            System.out.println("No note with that number.");
            return;
        }
        if (note.startsWith(PIN_MARK)) {
            notebook.setNote(position, note.substring(PIN_MARK.length()));
            System.out.println("Unpinned.");
        } else {
            notebook.setNote(position, PIN_MARK + note);
            System.out.println("Pinned to the top.");
        }
    }

    private static void moveNote(int choice) {
        viewNotes();
        int position = readInt("Number of the note to move: ") - 1;
        String note = notebook.getNote(position);
        if (note == null) {
            System.out.println("No note with that number.");
            return;
        }

        boolean moved;
        if (choice == 8) {
            moved = notebook.moveNoteToTop(note);
        } else if (choice == 9) {
            moved = notebook.moveNoteUp(note);
        } else {
            moved = notebook.moveNoteDown(note);
        }

        if (moved) {
            System.out.println("Note moved. (Pinned notes always stay above the rest.)");
        } else {
            System.out.println("That note can't move that way.");
        }
    }

    private static void deleteAll() {
        if (notebook.numberOfNotes() == 0) {
            System.out.println("There's nothing to delete.");
            return;
        }
        String answer = readLine("Delete all " + notebook.numberOfNotes()
                + " notes? This can't be undone. (y/n): ");
        if (answer.equalsIgnoreCase("y")) {
            notebook.setNotes(new ArrayList<String>());
            System.out.println("All notes deleted.");
        } else {
            System.out.println("Nothing was deleted.");
        }
    }

    private static String chooseCategory() {
        for (int i = 0; i < CATEGORIES.length; i++) {
            System.out.println("  " + (i + 1) + ". " + CATEGORIES[i]);
        }
        while (true) {
            int pick = readInt("Category number: ");
            if (pick >= 1 && pick <= CATEGORIES.length) {
                return CATEGORIES[pick - 1];
            }
            System.out.println("Pick a number from 1 to " + CATEGORIES.length + ".");
        }
    }

    private static String buildNote(String text, String category) {
        String today = LocalDate.now().format(DATE_FORMAT);
        return "[" + category + "] " + text + " (" + today + ")";
    }

    private static void keepPinnedOnTop() {
        ArrayList<String> pinned = new ArrayList<String>();
        ArrayList<String> others = new ArrayList<String>();
        for (String note : notebook.getNotes()) {
            if (note.startsWith(PIN_MARK)) {
                pinned.add(note);
            } else {
                others.add(note);
            }
        }
        pinned.addAll(others);
        notebook.setNotes(pinned);
    }

    private static void printList(ArrayList<String> notes, String emptyMessage) {
        if (notes.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        System.out.println("Found " + notes.size() + " note(s):");
        for (String note : notes) {
            System.out.println("  - " + note);
        }
    }

    private static void save() {
        try {
            notebook.saveToFile(FILE_NAME);
        } catch (IOException e) {
            System.out.println("Warning: your notes could not be saved.");
        }
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return input.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(input.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }
}
