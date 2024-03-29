package edu.northeastern.group26.littlemood;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtil {
    private DatabaseReference databaseReference;

    public FirebaseUtil() {
        // Get a reference to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("JournalEntries");
    }

    public void saveJournalEntry(JournalEntry journalEntry) {
        // Push the new entry, which auto-generates a unique ID
        DatabaseReference newEntryRef = databaseReference.push();

        // If you want to store the generated ID inside the object as well
        journalEntry.journalId = newEntryRef.getKey();

        newEntryRef.setValue(journalEntry);
    }
}
