package test.query;

import java.io.IOException;
import javax.json.JsonObject;
import test.data.guild.Guild;
import test.data.guild.GuildFactory;

/**
 * Permet de faire des requêtes sur l'endpoint Guild_Details.
 * @author Fabrice Bouyé
 */
public enum GuildDetailsQuery {

    INSTANCE;

    /**
     * L'URL de base de cet endpoint.
     */
    private static final String BASECODE = "https://api.guildwars2.com/v1/guild_details.json"; // NOI18N.

    /**
     * Récupère les infos d'une guilde.
     * @param id L'identifiant de la guilde.
     * @return Une instance de {@code Guild}, jamais {@code null}.
     * @throws IOException En cas d'erreur.
     */
    public static Guild guildInfo(final String id) throws IOException {
        final String url = String.format("%s?guild_id=%s", BASECODE, id); // NOI18N.
        final JsonObject jsonObject = QueryUtils.queryObject(url);
        return GuildFactory.createGuild(jsonObject);
    }
}
