package test.data.guild;

import javax.json.JsonArray;
import javax.json.JsonObject;
import test.query.QueryUtils;

/**
 * Fabrique à guilde.
 * @author Fabrice Bouyé
 */
public enum GuildFactory {

    INSTANCE;

    /**
     * Crée une guilde.
     * @param jsonObject L'objet JSON source.
     * @return Une instance de {@code Guild}, never {@code null}.
     */
    public static Guild createGuild(final JsonObject jsonObject) {
        final Guild result = new Guild();
        result.id = jsonObject.getString("guild_id"); // NOI18N.
        result.name = jsonObject.getString("guild_name"); // NOI18N.
        result.tag = jsonObject.getString("tag"); // NOI18N.
        result.emblem = createEmblem(jsonObject.getJsonObject("emblem")); // NOI18N.
        return result;
    }

    /**
     * Crée un emblème de guilde.
     * @param jsonObject L'objet JSON source.
     * @return Une instance de {@code Emblem}, never {@code null}.
     */
    private static Emblem createEmblem(final JsonObject jsonObject) {
        final Emblem result = new Emblem();
        result.backgroundId = jsonObject.getInt("background_id"); // NOI18N.
        result.foregroundId = jsonObject.getInt("foreground_id"); // NOI18N.
        final JsonArray jsonFlags = jsonObject.getJsonArray("flags"); // NOI18N.
        result.flags = QueryUtils.jsonStringArrayToList(jsonFlags, Emblem.Flag::find);
        result.backgroundColorId = jsonObject.getInt("background_color_id"); // NOI18N.        
        result.foregroundPrimaryColorId = jsonObject.getInt("foreground_primary_color_id"); // NOI18N.
        result.foregroundSecondaryColorId = jsonObject.getInt("foreground_secondary_color_id"); // NOI18N.
        return result;
    }
}
