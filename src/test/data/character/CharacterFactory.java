package test.data.character;

import javax.json.JsonObject;

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
        result.race = jsonObject.containsKey("race") ? Character.Race.find(jsonObject.getString("race")) : Character.Race.UNKNOWN; // NOI18N.
        // FB-2015-05-30 : durant la phase de béta de Heart of Thorns les personnages Revenant n'ont pas de profession.
        result.profession = jsonObject.containsKey("profession") ? Character.Profession.find(jsonObject.getString("profession")) : Character.Profession.UNKNOWN; // NOI18N.
        result.gender = Character.Gender.find(jsonObject.getString("gender")); // NOI18N.
        result.level = jsonObject.getInt("level"); // NOI18N.
        // FB-2015-05-30 : un personnage peut ne pas avoir de guilde active.
        result.guild = jsonObject.containsKey("guild") ? jsonObject.getString("guild") : null; // NOI18N.
        return result;
    }
}
