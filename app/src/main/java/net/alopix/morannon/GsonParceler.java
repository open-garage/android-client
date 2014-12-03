/*
 * OpenGarage
 *
 * Created by Dustin Steiner on 3.12.2014.
 * Copyright (c) 2014 Dustin Steiner. All rights reserved.
 */

package net.alopix.morannon;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import flow.Parceler;
import flow.Path;

/**
 * Created by dustin on 03.12.2014.
 */
public class GsonParceler implements Parceler {
    private final Gson mGson;

    public GsonParceler(Gson gson) {
        mGson = gson;
    }

    @Override
    public Parcelable wrap(Path instance) {
        try {
            String json = encode(instance);
            return new Wrapper(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path unwrap(Parcelable parcelable) {
        Wrapper wrapper = (Wrapper) parcelable;
        try {
            return decode(wrapper.json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String encode(Path instance) throws IOException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(stringWriter);

        try {
            Class<?> type = instance.getClass();

            writer.beginObject();
            writer.name(type.getName());
            mGson.toJson(instance, type, writer);
            writer.endObject();

            return stringWriter.toString();
        } finally {
            writer.close();
        }
    }

    private Path decode(String json) throws IOException {
        JsonReader reader = new JsonReader(new StringReader(json));

        try {
            reader.beginObject();

            Class<?> type = Class.forName(reader.nextName());
            return mGson.fromJson(reader, type);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            reader.close();
        }
    }

    private static class Wrapper implements Parcelable {
        final String json;

        Wrapper(String json) {
            this.json = json;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeString(json);
        }

        public static final Parcelable.Creator<Wrapper> CREATOR = new Parcelable.Creator<Wrapper>() {
            @Override
            public Wrapper createFromParcel(Parcel in) {
                String json = in.readString();
                return new Wrapper(json);
            }

            @Override
            public Wrapper[] newArray(int size) {
                return new Wrapper[size];
            }
        };
    }
}
