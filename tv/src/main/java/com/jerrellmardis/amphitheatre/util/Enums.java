package com.jerrellmardis.amphitheatre.util;

/**
 * Created by Jeremy Shore on 8/18/14.
 */
public class Enums {

    public enum PalettePresenterType {
        ALLCARDS(2),
        FOCUSEDCARD(1),
        NOTHING(0);

        private int mOrdinal;

        PalettePresenterType(int ordinal) {
            this.mOrdinal = ordinal;
        }

        public int getOrdinal() {
            return this.mOrdinal;
        }

        public static int getOrdinal(String value) {
            return PalettePresenterType.valueOf(value.replace(" ", "").toUpperCase()).getOrdinal();
        }

        public static PalettePresenterType valueOf(int ordinal) {
            switch(ordinal) {
                case 0:
                    return NOTHING;
                case 2:
                    return ALLCARDS;
                default:
                    return FOCUSEDCARD;
            }
        }
    }

    public enum PaletteColor {
        VIBRANT(5),
        LIGHTVIBRANT(3),
        DARKVIBRANT(4),
        MUTED(2),
        LIGHTMUTED(0),
        DARKMUTED(1);

        private int mOrdinal;

        PaletteColor(int ordinal) {
            this.mOrdinal = ordinal;
        }

        public int getOrdinal() {
            return this.mOrdinal;
        }

        public static int getOrdinal(String value) {
            return PaletteColor.valueOf(value.replace(" ", "").toUpperCase()).getOrdinal();
        }

        public static PaletteColor valueOf(int ordinal) {
            switch (ordinal) {
                case 0:
                    return LIGHTMUTED;
                case 2:
                    return MUTED;
                case 3:
                    return LIGHTVIBRANT;
                case 4:
                    return DARKVIBRANT;
                case 5:
                    return VIBRANT;
                default:
                    return DARKMUTED;
            }
        }
    }

    public enum BlurState {
        ON(0),
        OFF(1);

        private int mOrdinal;

        BlurState(int ordinal) {
            this.mOrdinal = ordinal;
        }

        public int getOrdinal() {
            return this.mOrdinal;
        }

        public static int getOrdinal(String value) {
            return BlurState.valueOf(value.toUpperCase()).getOrdinal();
        }

        public static BlurState valueOf(int ordinal) {
            switch(ordinal) {
                case 1:
                    return BlurState.OFF;
                default:
                    return BlurState.ON;
            }
        }
    }
 }
