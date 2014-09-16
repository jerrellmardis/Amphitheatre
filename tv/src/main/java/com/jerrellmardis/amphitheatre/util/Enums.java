package com.jerrellmardis.amphitheatre.util;

/**
 * Created by Jeremy Shore on 8/18/14.
 */
public class Enums {

    public enum PalettePresenterType {
        ALLCARDS(2),
        FOCUSEDCARD(1),
        NOTHING(0);

        private int mPosition;

        PalettePresenterType(int position) {
            this.mPosition = position;
        }

        public int getPosition() {
            return this.mPosition;
        }

        public static int getPosition(String value) {
            return PalettePresenterType.valueOf(value.replace(" ", "").toUpperCase()).getPosition();
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

        private int mPosition;

        PaletteColor(int position) {
            this.mPosition = position;
        }

        public int getPosition() {
            return this.mPosition;
        }

        public static int getPosition(String value) {
            return PaletteColor.valueOf(value.replace(" ", "").toUpperCase()).getPosition();
        }

        public static PaletteColor valueOf(int position) {
            switch (position) {
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

        private int mPosition;

        BlurState(int position) {
            this.mPosition = position;
        }

        public int getPosition() {
            return this.mPosition;
        }

        public static int getPosition(String value) {
            return BlurState.valueOf(value.toUpperCase()).getPosition();
        }

        public static BlurState valueOf(int position) {
            switch(position) {
                case 1:
                    return BlurState.OFF;
                default:
                    return BlurState.ON;
            }
        }
    }
 }
