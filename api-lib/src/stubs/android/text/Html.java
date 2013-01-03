/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */
package android.text;

public class Html {

    public static Spanned fromHtml(String html) {
        return new SpannedString(html);
    }

    private static class SpannedString implements Spanned {
        private final String str;
        public SpannedString(String str) {
            this.str = str;
        }

        @Override
        public int length() {
            return str.length();
        }

        @Override
        public char charAt(int index) {
            return str.charAt(index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return str.subSequence(start, end);
        }

        @Override
        public String toString() {
            return str;
        }
    }
}
