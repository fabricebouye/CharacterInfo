package test.query;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.function.Function;
import javafx.util.Duration;
import javax.json.JsonArray;
import javax.json.JsonObject;
import test.data.character.Character;
import test.data.character.CharacterFactory;

/**
 * Permet de faire des requêtes sur l'endpoint Characters.
 * @author Fabrice Bouyé
 */
public enum CharactersQuery {

    INSTANCE;

    /**
     * La durée de mise en cache sur le serveur est de 5 minutes.
     * <br/>voir <a href="https://forum-en.guildwars2.com/forum/community/api/Launching-v2-characters">Launching /v2/characters</a> par Lawton Campbell.
     */
    public static Duration SERVER_RETENTION_DURATION = Duration.minutes(5);

    /**
     * L'URL de base de cet endpoint.
     */
    private static final String BASECODE = "https://api.guildwars2.com/v2/characters"; // NOI18N.

    public static List<String> listCharacters(final String applicationKey) throws IOException {
        final String url = String.format("%s?access_token=%s", BASECODE, applicationKey); // NOI18N.
        final JsonArray jsonArray = QueryUtils.queryArray(url);
        return QueryUtils.jsonStringArrayToList(jsonArray, Function.identity());
    }

    public static Character characterInfo(final String applicationKey, final String name) throws IOException, URISyntaxException {
        final String url = String.format("%s/%s?access_token=%s", BASECODE, QueryUtils.encodeURLParameter(name), applicationKey); // NOI18N.
        final JsonObject jsonObject = QueryUtils.queryObject(url);
        return CharacterFactory.createCharacter(jsonObject);
    }

    public static List<Character> characterInfos(final String applicationKey, final String... names) throws IOException {
        final String url = String.format("%s?ids=%s&access_token=%s", BASECODE, QueryUtils.encodeURLParameter(QueryUtils.idsToString(names)), applicationKey); // NOI18N.
        System.out.println(url);
        final JsonArray jsonArray = QueryUtils.queryArray(url);
        return QueryUtils.jsonObjectArrayToList(jsonArray, CharacterFactory::createCharacter);
    }
}
