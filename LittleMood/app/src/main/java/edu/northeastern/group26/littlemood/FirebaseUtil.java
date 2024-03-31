package edu.northeastern.group26.littlemood;
import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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

    public void overwriteJournalEntry(String email, String date, JournalEntry journalEntry) {
        // Query to find the existing email
        Query query = databaseReference.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean entryFoundAndOverwritten = false;
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    String entryDate = childSnapshot.child("date").getValue(String.class);
                    if (date.equals(entryDate)) {
                        // Overwrite the old entry with the new data
                        String oldId = childSnapshot.getKey();
                        journalEntry.journalId = oldId;
                        databaseReference.child(childSnapshot.getKey()).setValue(journalEntry);
                        entryFoundAndOverwritten = true;
                        break;
                    }
                }
                if (!entryFoundAndOverwritten) {
                    saveJournalEntry(journalEntry);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
