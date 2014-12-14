/*
 * OpenGarage
 *
 * Created by Dustin Steiner on 8.12.2014.
 * Copyright (c) 2014 Dustin Steiner. All rights reserved.
 */

package net.alopix.morannon.popup;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dustin on 08/12/14.
 */
public class SettingsInput implements Parcelable {
    public final String title;
    public final String value;
    public final boolean isNumber;

    public SettingsInput(String title, String value, boolean isNumber) {
        this.title = title;
        this.value = value;
        this.isNumber = isNumber;
    }

    public SettingsInput(String title, String value) {
        this(title, value, false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SettingsInput that = (SettingsInput) o;

        return title.equals(that.title)
                && value.equals(that.value);
    }

    private static final int HASH_PRIME = 31;

    @Override
    public int hashCode() {
        int result = 1;
        result = HASH_PRIME * result + title.hashCode();
        result = HASH_PRIME * result + value.hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(value);
        parcel.writeInt(isNumber ? 1 : 0);
    }

    @SuppressWarnings("UnusedDeclaration")
    public static final Creator<SettingsInput> CREATOR = new Creator<SettingsInput>() {
        @Override
        public SettingsInput createFromParcel(Parcel parcel) {
            return new SettingsInput(parcel.readString(), parcel.readString(), parcel.readInt() == 1);
        }

        @Override
        public SettingsInput[] newArray(int size) {
            return new SettingsInput[size];
        }
    };
}
