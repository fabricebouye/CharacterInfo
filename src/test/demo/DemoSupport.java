package test.demo;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.function.Function;
import javax.json.JsonArray;
import javax.json.JsonObject;
import test.data.account.Account;
import test.data.account.AccountFactory;
import test.data.character.Character;
import test.data.character.CharacterFactory;
import test.data.guild.Guild;
import test.data.guild.GuildFactory;
import test.data.tokeninfo.TokenInfo;
import test.data.tokeninfo.TokenInfoFactory;
import test.query.QueryUtils;

/**
 *
 * @author Fabrice Bouyé
 */
public enum DemoSupport {

    INSTANCE;

    /**
     * Pattern simplifié des clés générées par le site officiel.
     */
    private static final String APPLICATION_KEY_PATTERN = "X{8}-X{4}-X{4}-X{4}-X{20}-X{4}-X{4}-X{4}-X{12}"; // NOI18N.

    /**
     * Test si une clé match le pattern de clé démo.
     * @param value La clé à tester.
     * @return {@code True} si le test réussit, {@code false} sinon.
     */
    public static boolean isDemoApplicationKey(final String value) {
        return (value == null) ? false : value.matches(APPLICATION_KEY_PATTERN); // NOI18N.
    }

    public static TokenInfo tokenInfo() throws IOException {
        final URL url = DemoSupport.class.getResource("keyinfo.json"); // NOI18N.
        final JsonObject jsonObject = QueryUtils.queryObject(url.toExternalForm());
        return TokenInfoFactory.createTokenInfo(jsonObject);
    }

    public static Account accountInfo() throws IOException {
        final URL url = DemoSupport.class.getResource("account.json"); // NOI18N.
        final JsonObject jsonObject = QueryUtils.queryObject(url.toExternalForm());
        return AccountFactory.createAccount(jsonObject);
    }

    public static Guild guildInfo(final String id) throws IOException {
        final String filename = String.format("guild_%s.json", id);
        final URL url = DemoSupport.class.getResource(filename); // NOI18N.
        final JsonObject jsonObject = QueryUtils.queryObject(url.toExternalForm());
        return GuildFactory.createGuild(jsonObject);
    }

    public static List<String> listCharacters() throws IOException {
        final URL url = DemoSupport.class.getResource("characterlist.json"); // NOI18N.
        final JsonArray jsonArray = QueryUtils.queryArray(url.toExternalForm());
        return QueryUtils.jsonStringArrayToList(jsonArray, Function.identity());
    }
    
    public static List<Character> characterInfos() throws IOException {
        final URL url = DemoSupport.class.getResource("characters.json"); // NOI18N.
        final JsonArray jsonArray = QueryUtils.queryArray(url.toExternalForm());
        return QueryUtils.jsonObjectArrayToList(jsonArray, CharacterFactory::createCharacter);
    }
}
