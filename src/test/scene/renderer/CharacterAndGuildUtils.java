package test.scene.renderer;

import java.util.List;
import java.util.Optional;
import test.data.character.Character;
import test.data.guild.Guild;

/**
 * Méthodes utilitaires pour l'affichage des personnages, guilde, etc.
 * @author Fabrice Bouyé
 */
public enum CharacterAndGuildUtils {

    INSTANCE;

    public static String professionAndRaceLabel(final Character character) {
        final Character.Profession profession = character.getProfession();
        final Character.Race race = character.getRace();
        final Character.Gender gender = character.getGender();
        return String.format("%s %s", race, profession);
    }
    
    /**
     * Renvoie la guilde d'un personnage.
     * @param character Le personnage.
     * @param guilds Les guildes du compte.
     * @return Une instance de {@code Guild}, peut être {@code null}.
     */
    public static Guild guildForCharacter(final Character character, final List<Guild> guilds) {
        final Optional<Guild> result = guilds.stream()
                .filter(guild -> guild.getId().equals(character.getGuild()))
                .findFirst();
        return result.isPresent() ? result.get() : null;
    }

    /**
     * Renvoie le label pour afficher une guilde.
     * @param guild La guilde.
     * @return Une {@code String}, jamais {@code null}.
     */
    public static String guildTagLabel(final Guild guild) {
        return String.format("[%s]", guild.getTag()); // NOI18N.
    }

    /**
     * Renvoie le label pour afficher une guilde.
     * @param guild La guilde.
     * @return Une {@code String}, jamais {@code null}.
     */
    public static String guildLabel(final Guild guild) {
        return String.format("%s [%s]", guild.getName(), guild.getTag()); // NOI18N.
    }
}
