package test.scene;

import java.util.ResourceBundle;
import test.data.character.Character;

/**
 * @author Fabrice Bouyé
 */
public enum LabelUtils {

    INSTANCE;

    public static String labelKeyForGender(final String prefix, final Character.Gender gender, final Enum value) {
        final Character.Gender g = (gender == Character.Gender.UNKNOWN) ? Character.Gender.FEMALE : gender;
        return String.format("%s.%s.%s.label", prefix, g.name().toLowerCase(), value.name().toLowerCase());
    }

    public static String professionLabel(final ResourceBundle resources, final Character.Gender gender, final Character.Profession profession) {
        final String key = labelKeyForGender("ui.profession", gender, profession);
        return resources.getString(key);
    }

    public static String raceLabel(final ResourceBundle resources, final Character.Gender gender, final Character.Race race) {
        final String key = labelKeyForGender("ui.race", gender, race);
        return resources.getString(key);
    }

    public static String raceAndProfessionLabel(final ResourceBundle resources, final Character.Gender gender, final Character.Race race, final Character.Profession profession) {
        final String raceStr = raceLabel(resources, gender, race);
        final String professionStr = professionLabel(resources, gender, profession);
        final String pattern = resources.getString("ui.race_and_profession.pattern");
        return String.format(pattern, raceStr, professionStr);
    }
}

