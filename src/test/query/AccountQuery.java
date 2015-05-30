package test.query;

import java.io.IOException;
import javax.json.JsonObject;
import test.data.account.Account;
import test.data.account.AccountFactory;

/**
 * Permet de faire des requêtes sur l'endpoint Account.
 * @author Fabrice Bouyé
 */
public enum AccountQuery {

    INSTANCE;

    /**
     * L'URL de base de cet endpoint.
     */
    private static final String basecode = "https://api.guildwars2.com/v2/account"; // NOI18N.

    public static Account accountInfo(final String applicationKey) throws IOException {
        final String url = String.format("%s?access_token=%s", basecode, applicationKey);
        final JsonObject jsonObject = QueryUtils.queryObject(url);
        return AccountFactory.createAccount(jsonObject);
    }
}
