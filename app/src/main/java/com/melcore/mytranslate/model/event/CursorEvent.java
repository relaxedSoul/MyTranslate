package com.melcore.mytranslate.model.event;

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
