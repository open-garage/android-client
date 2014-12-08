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
    public final int id;
    public final String url;
    public final String token;

    public SettingsInput(int id, String url, String token) {
        this.id = id;
        this.url = url;
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SettingsInput that = (SettingsInput) o;

        return id == that.id
                && url.equals(that.url)
                && token.equals(that.token);
    }

    private static final int HASH_PRIME = 31;

    @Override
    public int hashCode() {
        int result = id;
        result = HASH_PRIME * result + url.hashCode();
        result = HASH_PRIME * result + token.hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(url);
        parcel.writeString(token);
    }

    @SuppressWarnings("UnusedDeclaration")
    public static final Creator<SettingsInput> CREATOR = new Creator<SettingsInput>() {
        @Override
        public SettingsInput createFromParcel(Parcel parcel) {
            return new SettingsInput(parcel.readInt(), parcel.readString(), parcel.readString());
        }

        @Override
        public SettingsInput[] newArray(int size) {
            return new SettingsInput[size];
        }
    };
}
