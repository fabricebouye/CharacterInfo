package test.data.character;

/**
 * L'implémentation d'un personnage.
 * @author Fabrice Bouyé
 */
public final class Character {

    /**
     * L'implémentation d'une race.
     * @author Fabrice Bouyé
     */
    public enum Race {

        ASURA("Asura"), // NOI18N.
        CHARR("Charr"), // NOI18N.
        HUMAN("Human"), // NOI18N.
        NORN("Norn"), // NOI18N.
        SYLVARI("Sylvari"), // NOI18N.
        UNKNOWN(null);

        private final String value;

        Race(final String value) {
            this.value = value;
        }

        public static Race find(final String value) {
            Race result = Race.UNKNOWN;
            if (value != null) {
                for (final Race toTest : values()) {
                    if (value.equals(toTest.value)) {
                        result = toTest;
                        break;
                    }
                }
            }
            return result;
        }
    }

    /**
     * L'implémentation d'une profession.
     * @author Fabrice Bouyé
     */
    public enum Profession {

        ELEMENTALIST("Elementalist"), // NOI18N.
        ENGINEER("Engineer"), // NOI18N.
        GUARDIAN("Guardian"), // NOI18N.
        MESMER("Mesmer"), // NOI18N.
        NECROMANCER("Necromancer"), // NOI18N.
        RANGER("Ranger"), // NOI18N.
        REVENANT("Revenant"), // NOI18N.
        THIEF("Thief"), // NOI18N.
        WARRIOR("Warrior"), // NOI18N.
        UNKNOWN(null);

        private final String value;

        Profession(final String value) {
            this.value = value;
        }

        public static Profession find(final String value) {
            Profession result = Profession.UNKNOWN;
            if (value != null) {
                for (final Profession toTest : values()) {
                    if (value.equals(toTest.value)) {
                        result = toTest;
                        break;
                    }
                }
            }
            return result;
        }
    }

    /**
     * L'implémentation du genre.
     * @author Fabrice Bouyé
     */
    public enum Gender {

        MALE("Male"), // NOI18N.
        FEMALE("Female"), // NOI18N.
        UNKNOWN(null);

        private final String value;

        Gender(final String value) {
            this.value = value;
        }

        public static Gender find(final String value) {
            Gender result = Gender.UNKNOWN;
            if (value != null) {
                for (final Gender toTest : values()) {
                    if (value.equals(toTest.value)) {
                        result = toTest;
                        break;
                    }
                }
            }
            return result;
        }
    }

    String name;
    Race race;
    Profession profession;
    Gender gender;
    int level;
    String guild;

    /**
     * Crée une instance vide.
     */
    Character() {
    }
}
