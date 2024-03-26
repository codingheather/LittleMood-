package edu.northeastern.group26.littlemood;

public class Emoji {
    String slug;
    String character;
    String group;
    String subGroup;

    public Emoji(String slug, String character, String group, String subGroup) {
        this.slug = slug;
        this.character = character;
        this.group = group;
        this.subGroup = subGroup;
    }

    @Override
    public String toString() {
        return "Emoji{" +
                "slug='" + slug + '\'' +
                ", character='" + character + '\'' +
                ", group='" + group + '\'' +
                ", subGroup='" + subGroup + '\'' +
                '}';
    }

    public String getSubGroup() {
        return subGroup;
    }

    public void setSubGroup(String subGroup) {
        this.subGroup = subGroup;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
