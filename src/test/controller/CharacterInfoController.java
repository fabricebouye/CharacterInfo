package test.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import test.data.account.Account;
import test.data.character.Character;
import test.data.guild.Guild;
import test.query.AccountQuery;
import test.query.CharactersQuery;
import test.query.GuildDetailsQuery;
import test.scene.renderer.CharacterListCell;
import test.scene.renderer.CharacterAndGuildUtils;
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
    private VBox listingVBox;
    @FXML
    private TextField searchField;
    @FXML
    private ListView<Character> characterListView;
    @FXML
    private ProgressIndicator progressIndicator;

    /**
    * Stocke les réglages utilisateur.
    */
    private final Properties settings = new Properties();

    /**
     * liste des personnages.
     */
    private ObservableList<Character> characterList = FXCollections.observableList(new LinkedList());

    /**
     * liste triée des personnages.
     */
    private SortedList<Character> sortedCharacterList = new SortedList<>(characterList);

    /**
     * liste filtrée des personnages.
     */
    private FilteredList<Character> filteredCharacterList = new FilteredList<>(sortedCharacterList);

    /**
     * Tri des personnages sur le nom.
     */
    private Comparator<Character> characterNameComparator = (c1, c2) -> c1.getName().compareTo(c2.getName());

    /**
     * Affiche tous les personnages.
     */
    private final Predicate<Character> allCharactersFilter = character -> true;

    /**
     * Crée une nouvelle instance.
     */
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
        // 
        sortedCharacterList.setComparator(characterNameComparator);
        filteredCharacterList.setPredicate(allCharactersFilter);
    }

    private ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        //
        characterListView.setItems(filteredCharacterList);
        listingVBox.setVisible(false);
        progressIndicator.setVisible(false);
        //
        final TextFormatter<String> applicationKeyTextFormatter = new ApplicationKeyTextFormatter();
        applicationKeyField.setTextFormatter(applicationKeyTextFormatter);
        final Optional<String> applicationKeyOptional = Optional.ofNullable(settings.getProperty("app.key")); // NOI18N.
        applicationKeyOptional.ifPresent(applicationKey -> {
            applicationKeyField.setText(applicationKey);
            applicationKeyField.positionCaret(0);
            applicationKeyField.selectRange(0, 0);
            Platform.runLater(() -> applicationKeyChanged(applicationKey));
        });
        applicationKeyField.textProperty().addListener(applicationKeyChangeListener);
        //
        searchField.textProperty().addListener(searchInvalidationListener);
    }

    /**
     * La pseudo-classe servant de décorateur en cas d'erreur.
     */
    private final PseudoClass errorPseudoClass = PseudoClass.getPseudoClass("error"); // NOI18N.

    /**
     * Invoqué si la valeur de la clé d'application change.
     */
    private final ChangeListener<String> applicationKeyChangeListener = (observable, oldValue, newValue) -> {
        applicationKeyChanged(newValue);
    };

    /**
     * Invoqué si la valeur de la valeur de recherche change.
     */
    private final InvalidationListener searchInvalidationListener = observable -> {
        final String searchText = searchField.getText();
        final String[] criteria = (searchText == null || searchText.trim().isEmpty()) ? null : searchText.trim().split("[\\s,;]+"); // NOI18N.
        final Predicate<Character> filter = (criteria == null) ? allCharactersFilter : character -> filterCharacter(character, criteria);
        filteredCharacterList.setPredicate(filter);
    };

    /**
     * Filtre la liste des personnage.
     * @param character Le personnage à tester.
     * @param criteria Les critères de recherche.
     * @return {@code True} si le test réussit, {@code false} sinon.
     */
    private boolean filterCharacter(final Character character, final String... criteria) {
        boolean result = true;
        for (final String criterion : criteria) {
            final String toMatch = normalizeForSearch(criterion);
            boolean criterionTest = false;
            // Teste le nom du personnage.       
            final boolean characterFound = normalizeForSearch(character.getName()).contains(toMatch);
            criterionTest |= characterFound;
            // Teste la race du personnage.       
            final boolean raceFound = normalizeForSearch(character.getRace().name()).contains(toMatch);
            criterionTest |= raceFound;
            // Teste la profession du personnage.       
            final boolean professionFound = normalizeForSearch(character.getProfession().name()).contains(toMatch);
            criterionTest |= professionFound;
            // Teste le nom de la guilde.
            final Guild guild = CharacterAndGuildUtils.guildForCharacter(character, currentQueryResult.guilds);
            final boolean guildNameFound = (guild == null) ? false : normalizeForSearch(guild.getName()).contains(toMatch);
            criterionTest |= guildNameFound;
            // Test le tag de la guilde.
            final boolean guildTagFound = (guild == null) ? false : normalizeForSearch(guild.getTag()).contains(toMatch);
            criterionTest |= guildTagFound;
            //
            result &= criterionTest;
        }
        return result;
    }

    /**
     * Normalize la chaine de charactère en retirant les accents et diacritiques.
     * @param source La chaîne source.
     * @return Une {@code String}, jamais {@code null}.
     */
    private String normalizeForSearch(final String source) {
        final String nfdNormalizedString = Normalizer.normalize(source.toLowerCase(), Normalizer.Form.NFD);
        final Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+"); // NOI18N.
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    /**
     * Invoqué quand la clé d'application change.
     * @param applicationKey La nouvelle clé d'application.
     */
    private void applicationKeyChanged(final String applicationKey) {
        final boolean applicationKeyValid = ApplicationKeyUtils.validateApplicationKey(applicationKey);
        applicationKeyField.pseudoClassStateChanged(errorPseudoClass, !applicationKeyValid);
        if (applicationKeyValid) {
            settings.setProperty("app.key", applicationKey); // NOI18N.
            characterList.clear();
            start();
        } else {
            stop();
            settings.setProperty("app.key", null); // NOI18N.
            accountCharacterLabel.setText(resources.getString("no.accout.label")); // NOI18N.
        }
    }

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
     * Dernier resultat.
     */
    private QueryResult currentQueryResult;

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
                            // Information sur le compte.
                            result.account = AccountQuery.accountInfo(applicationKey);
                            if (isCancelled()) {
                                return null;
                            }
                            // Information sur chaque guilde.
                            final List<Guild> guilds = new ArrayList(result.account.getGuilds().size());
                            for (final String guildId : result.account.getGuilds()) {
                                final Guild guild = GuildDetailsQuery.guildInfo(guildId);
                                guilds.add(guild);
                                if (isCancelled()) {
                                    return null;
                                }
                            }
                            result.guilds = Collections.unmodifiableList(guilds);
                            // Information sur chaque personnage.
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
                currentQueryResult = (QueryResult) workerStateEvent.getSource().getValue();
                String label = resources.getString("account.characters.pattern"); // NOI18N.
                accountCharacterLabel.setText(String.format(label, currentQueryResult.account.getName()));
                final Optional<Character> oldSelectionOptional = Optional.ofNullable(characterListView.getSelectionModel().getSelectedItem());
                characterList.setAll(currentQueryResult.characters);
                characterListView.setCellFactory(listView -> new CharacterListCell(currentQueryResult.guilds));
                listingVBox.setVisible(true);
                progressIndicator.setVisible(false);
                // Restore la sélection s'il y en a une.
                oldSelectionOptional.ifPresent(oldSelection -> {
                    final Optional<Character> newSelectionOptional = characterListView.getItems()
                            .stream()
                            .filter(character -> character.getName().equals(oldSelection.getName()))
                            .findFirst();
                    newSelectionOptional.ifPresent(newSelection -> characterListView.getSelectionModel().select(newSelection));
                });
            });
            updateService.setOnCancelled(workerStateEvent -> {
            });
            updateService.setOnFailed(workerStateEvent -> {
                System.err.println(workerStateEvent.getSource().getException());
                updateService.getException().printStackTrace();
            });
        }
        listingVBox.setVisible(false);
        progressIndicator.setVisible(true);
        updateService.restart();
    }

    /**
     * Stoppe le service de mise à jour automatique.
     */
    public void stop() {
        if (updateService == null) {
            return;
        }
        updateService.cancel();
    }
}
