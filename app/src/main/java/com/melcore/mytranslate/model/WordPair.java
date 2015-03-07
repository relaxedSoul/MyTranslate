package com.melcore.mytranslate.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Origin and it's translation pair
 * Created by Melcore on 02.03.2015.
 */
@DatabaseTable
public class WordPair {

    @DatabaseField(generatedId=true)
    private int id;

    @DatabaseField
    private String origin;

    @DatabaseField
    private String translate;

    public WordPair(){
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }
}
