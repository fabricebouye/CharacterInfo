package test.data.character;

import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import test.query.QueryUtils;

/**
 * La fabrique à personnage.
 * @author Fabrice Bouyé
 */
public enum CharacterFactory {

    INSTANCE;

    /**
     * Crée un nouveau personnage.
     * @param jsonObject L'objet JSON source.
     * @return Une instance de {@code Character}, jamais {@code null}.
     */
    public static Character createCharacter(final JsonObject jsonObject) {
        final Character result = new Character();
        result.name = jsonObject.getString("name"); // NOI18N.
        // FB-2015-05-30 : on supposera que les phases beta lors de l'ajout d'une race pouraient aussi provoquer une NullPointerException.
        result.race = Character.Race.find(QueryUtils.fromNullOrMissingString(jsonObject, "race")); // NOI18N.
        // FB-2015-05-30 : durant la phase de béta de Heart of Thorns les personnages Revenant n'ont pas de profession.
        // FB-2015-07-11 : la profession du revenant semble désormais correctement spécifiée.
        result.profession = Character.Profession.find(QueryUtils.fromNullOrMissingString(jsonObject, "profession")); // NOI18N.
        result.gender = Character.Gender.find(jsonObject.getString("gender")); // NOI18N.
        result.level = jsonObject.getInt("level"); // NOI18N.
        // FB-2015-07-11 : le champs contient désormais null quand un personnage n'a pas de guilde.
        result.guild = QueryUtils.fromNullOrMissingString(jsonObject, "guild"); // NOI18N.
        return result;
    }
}
