package test.data.tokeninfo;

import java.util.List;

/**
 * Implémente les infos d'un jeton de sécurité.
 * Voir <a href="https://forum-en.guildwars2.com/forum/community/api/Launching-v2-tokeninfo">Launching /v2/tokeninfo</a> par Lawton Campbell.
 * @author Fabrice Bouyé
 */
public final class TokenInfo {

    /**
     * Implémente les permissions d'un jeton de sécurité.
     * @author Fabrice Bouyé
     */
    public enum Permission {

        ACCOUNT("account"), // NOI18N.
        CHARACTERS("characters"), // NOI18N.
        INVENTORIES("inventories"), // NOI18N.
        TRADINGPOST("tradingpost"), // NOI18N.
        WALLET("wallet"), // NOI18N.
        UNLOCKS("unlocks"), // NOI18N.
        PVP("pvp"), // NOI18N.
        BUILDS("builds"), // NOI18N.
        PROGRESSION("progression"), // NOI18N.
        GUILDS("guilds"), // NOI18N.
        UNKNOWN(null);

        private final String value;

        private Permission(String value) {
            this.value = value;
        }

        public static Permission find(final String value) {
            Permission result = Permission.UNKNOWN;
            if (value != null) {
                for (final Permission toTest : values()) {
                    if (value.equals(toTest.value)) {
                        result = toTest;
                        break;
                    }
                }
            }
            return result;
        }
    }

    String id;
    String name;
    List<Permission> permissions;

    /**
     * Crée une instance vide.
     */
    TokenInfo() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }
}
