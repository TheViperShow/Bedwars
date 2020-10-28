package me.thevipershow.bedwars;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class EncryptUtils {

    private EncryptUtils() {

    }

    public static final StringBuilder alphabet = new StringBuilder(126 - 32 + 4);
    private static Map<Character, Character> reverseAlphabet = null;

    static {
        for (int i = 32; i <= 126; i++) {
            if (i != 92) {
                alphabet.append((char) i);
            }
        }
        alphabet.append('§').append('♫').append('★').append('\n');
    }

    public enum Direction {
        FORWARD(-1), BACKWARDS(+1);

        private final int i;

        public Direction opposite() {
            return i == -1 ? BACKWARDS : FORWARD;
        }

        Direction(int i) {
            this.i = i;
        }
    }

    public static String rotateBy(final String starting, final int key, final Direction direction) {
        if (key < 0) {
            throw new IllegalArgumentException();
        }
        char[] chars = starting.toCharArray();
        final int length = chars.length;
        int rotations = 0;

        char[] copy = new char[chars.length];

        final boolean isForward = direction == Direction.FORWARD;

        while (rotations < key) {
            copy = Arrays.copyOf(chars, length);
            final char posToReplace = chars[isForward ? length - 1 : 0];

            int i = isForward ? 0 : length - 1;
            while (true) {
                if (isForward) {
                    if (i >= length - 1) {
                        break;
                    }
                    copy[i + 1] = chars[i];
                } else {
                    if (i <= 0) {
                        break;
                    }
                    copy[i - 1] = chars[i];
                }

                i += isForward ? +1 : -1;
            }

            copy[isForward ? 0 : length - 1] = posToReplace;
            rotations++;
            chars = copy;
        }

        return new String(copy);
    }

    public static Map<Character, Character> generateCipher(final int key, final Direction direction, final String knownAlphabet) {

        final String encryptResult = rotateBy(knownAlphabet, key, direction);
        final Map<Character, Character> cipher = new HashMap<>();

        final char[] originalCharsArray = knownAlphabet.toCharArray();
        final char[] rotatedCharsArray = encryptResult.toCharArray();
        for (int i = 0; i < rotatedCharsArray.length; i++) {
            final char originalChar = originalCharsArray[i];
            final char mappedChar = rotatedCharsArray[i];
            cipher.put(originalChar, mappedChar);
        }

        return cipher;
    }

    public static String decrypt(final String input, final String alphabet, final int alphabetKey, final int wordRotationKey, final Direction alphabetRotateDirection, final Direction wordRotationDirection) {

        final StringBuilder stringBuilder = new StringBuilder();
        final String reverseLastRotation = rotateBy(input, wordRotationKey, wordRotationDirection.opposite());
        if (reverseAlphabet == null) {
            final Map<Character, Character> map = generateCipher(alphabetKey, alphabetRotateDirection, alphabet);
            final Map<Character, Character> reverseMap = new HashMap<>();
            for (Map.Entry<Character, Character> entry : map.entrySet()) {
                reverseMap.put(entry.getValue(), entry.getKey());
            }
            reverseAlphabet = reverseMap;
        }

        for (final char c : reverseLastRotation.toCharArray()) {
            stringBuilder.append(reverseAlphabet.get(c));
        }

        return stringBuilder.toString();
    }
}
