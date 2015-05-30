package test.data.guild;

/**
 * Implémente une guilde.
 * @author Fabrice Bouyé
 */
public final class Guild {

    String id;
    String name;
    String tag;
    Emblem emblem;

    /**
     * Crée une instance vide.
     */
    Guild() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public Emblem getEmblem() {
        return emblem;
    }
}
