package com.jerrellmardis.amphitheatre.model.tvdb;

public enum Language {
    ALL(0, "all"),
    ENGLISH(7, "en");

    private int mId;

    private String mCode;

    private Language(int id, String code) {
        mId = id;
        mCode = code;
    }

    public static Language parse(String code) {
        for (Language language : values()) {
            if (language.getCode().equalsIgnoreCase(code)) {
                return language;
            }
        }
        return null;
    }

    public int getId() {
        return mId;
    }

    public String getCode() {
        return mCode;
    }

    @Override
    public String toString() {
        return mCode;
    }

}
