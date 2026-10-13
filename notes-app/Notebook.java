import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * A simple notebook that stores text notes in an ArrayList.
 * You can add notes, delete notes, look notes up, and reorder them.
 *
 * @author  Thomas Sanchez
 * @version 1.0
 */
public class Notebook {

    private ArrayList<String> notes;

    /**
     * Creates a new, empty Notebook.
     */
    public Notebook() {
        notes = new ArrayList<String>();
    }

    /**
     * Gets the ArrayList of notes.
     *
     * @return the ArrayList of notes
     */
    public ArrayList<String> getNotes() {
        return notes;
    }

    /**
     * Sets the ArrayList of notes to a new ArrayList.
     *
     * @param newNotes the new ArrayList of notes
     */
    public void setNotes(ArrayList<String> newNotes) {
        notes = newNotes;
    }

    /**
     * Returns how many notes are in the notebook.
     *
     * @return the number of notes
     */
    public int numberOfNotes() {
        return notes.size();
    }

    /**
     * Adds a note to the end of the list. If the note is already in the
     * list, it will not be added again (no duplicates allowed).
     *
     * @param note the note text to add
     * @return true if the note was added, false if it was already there
     */
    public boolean addNote(String note) {
        boolean added = false;

        if (!notes.contains(note)) {
            notes.add(note);
            added = true;
        }

        return added;
    }

    /**
     * Deletes the note at the given position.
     *
     * @param position the index of the note to delete
     * @return true if a note was deleted, false if the position was bad
     */
    public boolean deleteNote(int position) {
        boolean deleted = false;

        if (position >= 0 && position < notes.size()) {
            notes.remove(position);
            deleted = true;
        }

        return deleted;
    }

    /**
     * Finds the position of a note using its text.
     *
     * @param note the note text to look for
     * @return the position of the note, or -1 if it is not in the list
     */
    public int getNoteNumber(String note) {
        return notes.indexOf(note);
    }

    /**
     * Gets the note text at a given position.
     *
     * @param position the index of the note
     * @return the note text, or null if the position is bad
     */
    public String getNote(int position) {
        String result = null;

        if (position >= 0 && position < notes.size()) {
            result = notes.get(position);
        }

        return result;
    }

    /**
     * Changes the note text at a given position.
     *
     * @param position the index of the note to change
     * @param newText  the new text to put there
     * @return true if the note was changed, false if the position was bad
     */
    public boolean setNote(int position, String newText) {
        boolean updated = false;

        if (position >= 0 && position < notes.size()) {
            notes.set(position, newText);
            updated = true;
        }

        return updated;
    }

    /**
     * Moves a note up one spot in the list, so it is closer to the top.
     * Nothing happens if the note is already at the top or is not found.
     *
     * @param note the text of the note to move
     * @return true if the note moved, false if it did not
     */
    public boolean moveNoteUp(String note) {
        boolean moved = false;
        int position = notes.indexOf(note);

        if (position > 0) {
            String temp = notes.get(position - 1);
            notes.set(position - 1, notes.get(position));
            notes.set(position, temp);
            moved = true;
        }

        return moved;
    }

    /**
     * Moves a note down one spot in the list, so it is closer to the end.
     * Nothing happens if the note is already at the end or is not found.
     *
     * @param note the text of the note to move
     * @return true if the note moved, false if it did not
     */
    public boolean moveNoteDown(String note) {
        boolean moved = false;
        int position = notes.indexOf(note);
        int lastPosition = notes.size() - 1;

        if (position != -1 && position < lastPosition) {
            String temp = notes.get(position + 1);
            notes.set(position + 1, notes.get(position));
            notes.set(position, temp);
            moved = true;
        }

        return moved;
    }

    /**
     * Moves a note all the way to the top of the list.
     *
     * @param note the text of the note to move
     * @return true if the note was found and moved, false if it was not found
     */
    public boolean moveNoteToTop(String note) {
        boolean moved = false;
        int position = notes.indexOf(note);

        if (position != -1) {
            notes.remove(position);
            notes.add(0, note);
            moved = true;
        }

        return moved;
    }

    /**
     * Moves a note all the way to the bottom of the list.
     *
     * @param note the text of the note to move
     * @return true if the note was found and moved, false if it was not found
     */
    public boolean moveNoteToBottom(String note) {
        boolean moved = false;
        int position = notes.indexOf(note);

        if (position != -1) {
            notes.remove(position);
            notes.add(note);
            moved = true;
        }

        return moved;
    }

    /**
     * Finds every note that contains a keyword, ignoring capital letters.
     *
     * @param keyword the word to search for
     * @return a list of the matching notes (empty if none match)
     */
    public ArrayList<String> searchNotes(String keyword) {
        ArrayList<String> matches = new ArrayList<String>();

        for (String note : notes) {
            if (note.toLowerCase().contains(keyword.toLowerCase())) {
                matches.add(note);
            }
        }

        return matches;
    }

    /**
     * Saves every note to a text file, one note per line.
     *
     * @param fileName the name of the file to save to
     * @throws IOException if the file can't be written
     */
    public void saveToFile(String fileName) throws IOException {
        Files.write(Paths.get(fileName), notes);
    }

    /**
     * Loads notes from a text file, if the file exists.
     *
     * @param fileName the name of the file to load from
     * @throws IOException if the file exists but can't be read
     */
    public void loadFromFile(String fileName) throws IOException {
        Path path = Paths.get(fileName);

        if (Files.exists(path)) {
            notes = new ArrayList<String>(Files.readAllLines(path));
        }
    }
}
