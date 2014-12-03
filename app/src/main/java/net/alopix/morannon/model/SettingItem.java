package net.alopix.morannon.model;

/**
 * Created by dustin on 03.12.2014.
 */
public class SettingItem {
    private String mTitle;
    private String mDescription;
    private boolean mClickable;

    public SettingItem() {
    }

    public SettingItem(String title, String description) {
        this(title, description, false);
    }

    public SettingItem(String title, String description, boolean clickable) {
        mTitle = title;
        mDescription = description;
        mClickable = clickable;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public boolean isClickable() {
        return mClickable;
    }

    public void setClickable(boolean clickable) {
        mClickable = clickable;
    }
}
