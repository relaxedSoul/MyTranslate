package com.melcore.mytranslate.model;

import android.database.Cursor;

public class CursorEvent {

    private Cursor cursor;

    public CursorEvent(Cursor cursor) {
        this.cursor = cursor;
    }

    public Cursor getCursor() {
        return cursor;
    }
}
