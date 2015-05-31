package test.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.text.Text;
import javafx.util.Duration;
import test.data.account.Account;
import test.data.character.Character;
import test.data.guild.Guild;
import test.query.AccountQuery;
import test.query.CharactersQuery;
import test.query.GuildDetailsQuery;
import test.scene.renderer.CharacterListCell;
import test.text.ApplicationKeyTextFormatter;
import test.text.ApplicationKeyUtils;

/**
 * Contrôleur du FXML.
 * @author Fabrice Bouyé
 */
public final class CharacterInfoController implements Initializable {

    @FXML
    private TextField applicationKeyField;
    @FXML
    private Text accountCharacterLabel;
    @FXML
    private ListView<Character> characterList;

    private final Properties settings = new Properties();

    public CharacterInfoController() {
        // Chargement du fichier de config si présent.
        final File file = new File("settings.properties"); // NOI18N.
        if (file.exists() && file.canRead()) {
            try (final InputStream input = new FileInputStream(file)) {
                settings.load(input);
            } catch (IOException ex) {
                Logger.getLogger(CharacterInfoController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    private ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        final TextFormatter<String> applicationKeyTextFormatter = new ApplicationKeyTextFormatter();
        applicationKeyField.setTextFormatter(applicationKeyTextFormatter);
        applicationKeyField.textProperty().addListener(applicationKeyChangeListener);
        final Optional<String> applicationKeyOptional = Optional.ofNullable(settings.getProperty("app.key")); // NOI18N.
        applicationKeyOptional.ifPresent(applicationKey -> {
            applicationKeyField.setText(applicationKey);
            applicationKeyField.positionCaret(0);
            applicationKeyField.selectRange(0, 0);
        });
        //
    }

    /**
     * La pseudo-classe servant de décorateur en cas d'erreur.
     */
    private final PseudoClass errorPseudoClass = PseudoClass.getPseudoClass("error"); // NOI18N.

    /**
     * Invoqué si la valeur de la clé d'application change.
     */
    private final ChangeListener<String> applicationKeyChangeListener = (observable, oldValue, newValue) -> {
        final boolean applicationKeyValid = ApplicationKeyUtils.validateApplicationKey(newValue);
        applicationKeyField.pseudoClassStateChanged(errorPseudoClass, !applicationKeyValid);
        if (applicationKeyValid) {
            settings.setProperty("app.key", newValue); // NOI18N.
            characterList.getItems().clear();
            start();
        } else {
            stop();
            settings.setProperty("app.key", null); // NOI18N.
            accountCharacterLabel.setText(resources.getString("no.accout.label")); // NOI18N.
        }
    };

    /**
     * Le résultat de la requête.
     * @author Fabrice Bouyé
     */
    private static class QueryResult {

        Account account;
        List<Guild> guilds;
        List<Character> characters;
    }

    /**
     * Le service de mise à jour automatique.
     */
    private ScheduledService<QueryResult> updateService;
    /**
     * Le temps d'attente entre chaque mise à jour automatique.
     */
    private Duration updateWaitTime = CharactersQuery.SERVER_RETENTION_DURATION;

    /**
     * Démarre le service de mise à jour automatique.
     */
    public void start() {
        if (updateService == null) {
            updateService = new ScheduledService<QueryResult>() {

                @Override
                protected Task<QueryResult> createTask() {
                    return new Task<QueryResult>() {

                        @Override
                        protected QueryResult call() throws Exception {
                            final QueryResult result = new QueryResult();
                            final String applicationKey = settings.getProperty("app.key"); // NOI18N.
                            if (isCancelled()) {
                                return null;
                            }
                            result.account = AccountQuery.accountInfo(applicationKey);
                            if (isCancelled()) {
                                return null;
                            }
                            final List<Guild> guilds = new ArrayList(result.account.getGuilds().size());
                            for (final String guildId : result.account.getGuilds()) {
                                final Guild guild = GuildDetailsQuery.guildInfo(guildId);
                                guilds.add(guild);
                                if (isCancelled()) {
                                    return null;
                                }
                            }
                            result.guilds = Collections.unmodifiableList(guilds);
                            final List<String> names = CharactersQuery.listCharacters(applicationKey);
                            result.characters = CharactersQuery.characterInfos(applicationKey, names.toArray(new String[0]));
                            if (isCancelled()) {
                                return null;
                            }
                            return result;
                        }
                    };
                }
            };
            updateService.setRestartOnFailure(true);
            updateService.setPeriod(updateWaitTime);
            updateService.setOnSucceeded(workerStateEvent -> {
                final QueryResult result = updateService.getValue();
                String label = resources.getString("account.characters.pattern"); // NOI18N.
                accountCharacterLabel.setText(String.format(label, result.account.getName()));
                characterList.getItems().setAll(result.characters);
                characterList.setCellFactory(listView -> new CharacterListCell(result.guilds));
            });
            updateService.setOnFailed(workerStateEvent -> {
                System.err.println(updateService.getException());
                updateService.getException().printStackTrace();
            });
            updateService.setOnCancelled(workerStateEvent -> {
            });
        }
        updateService.restart();
    }

    /**
     * Stoppe le service de mise a jour automatique.
     */
    public void stop() {
        if (updateService == null) {
            return;
        }
        updateService.cancel();
    }
}
