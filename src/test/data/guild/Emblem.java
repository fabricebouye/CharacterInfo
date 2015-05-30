package test.data.guild;

import java.util.List;

/**
 * Implémente un emblème de guilde.
 * @author Fabrice Bouyé
 */
public final class Emblem {

    /**
     * Implémente un flag.
     * @author Fabrice Bouyé
     */
    public enum Flag {

        FLIP_BACKGROUND_HORIZONTAL("FlipBackgroundHorizontal"),
        FLIP_BACKGROUND_VERTICAL("FlipBackgroundVertical"),
        FLIP_FOREGROUND_HORIZONTAL("FlipForegroundHorizontal"),
        FLIP_FOREGROUND_VERTICAL("FlipForegroundVertical"),
        UNKNOWN(null);

        private final String value;

        Flag(final String value) {
            this.value = value;
        }

        public static Flag find(final String value) {
            Flag result = Flag.UNKNOWN;
            if (value != null) {
                for (final Flag toTest : values()) {
                    if (value.equals(toTest.value)) {
                        result = toTest;
                        break;
                    }
                }
            }
            return result;
        }
    }

    int backgroundId;
    int foregroundId;
    List<Flag> flags;
    int backgroundColorId;
    int foregroundPrimaryColorId;
    int foregroundSecondaryColorId;

    /**
     * Crée une instance vide.
     */
    Emblem() {

    }

    public int getBackgroundId() {
        return backgroundId;
    }

    public int getForegroundId() {
        return foregroundId;
    }

    public List<Flag> getFlags() {
        return flags;
    }

    public int getBackgroundColorId() {
        return backgroundColorId;
    }

    public int getForegroundPrimaryColorId() {
        return foregroundPrimaryColorId;
    }

    public int getForegroundSecondaryColorId() {
        return foregroundSecondaryColorId;
    }
}
