package edu.northeastern.group26.littlemood;

public class JournalEntry {
    public String journalId;
    public String date;
    public String emoji;
    public String text;
    public String photo; // This will be the id/index to the photo in Firebase Storage (see sticker project)
    public String email;
    public int  emailNum=1;

    // Default constructor is required for Firebase
    public JournalEntry() {
    }

    public JournalEntry(String date, String emoji, String text, String photo, String email) {
        this.date = date;
        this.emoji = emoji;
        this.text = text;
        this.photo = photo;
        this.email = email;
    }

    public String getJournalId() {
        return journalId;
    }

    public String getDate() {
        return date;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getText() {
        return text;
    }

    public String getPhoto() {
        return photo;
    }

    public String getEmail() {
        return email;
    }

    public void setJournalId(String journalId) {
        this.journalId = journalId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
