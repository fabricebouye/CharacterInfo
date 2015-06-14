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
import java.util.stream.Collectors;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import test.data.account.Account;
import test.data.character.Character;
import test.data.guild.Guild;
import test.data.tokeninfo.TokenInfo;
import test.query.AccountQuery;
import test.query.CharactersQuery;
import test.query.GuildDetailsQuery;
import test.query.TokenInfoQuery;
import test.scene.LabelUtils;
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
    private FlowPane applicationKeyPermissionFlow;
    @FXML
    private Text messageLabel;
    @FXML
    private TextFlow guildsFlow;
    @FXML
    private VBox listingVBox;
    @FXML
    private TextField searchField;
    @FXML
    private ListView<Character> characterListView;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private CheckMenuItem nameCheckItem;
    @FXML
    private CheckMenuItem genderCheckItem;
    @FXML
    private CheckMenuItem raceCheckItem;
    @FXML
    private CheckMenuItem professionCheckItem;
    @FXML
    private CheckMenuItem guildCheckItem;
    @FXML
    private CheckMenuItem guildTagCheckItem;

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
        applicationKeyField.textProperty().addListener(applicationKeyChangeListener);
        //
        nameCheckItem.setSelected(Boolean.parseBoolean(settings.getProperty("search.filter.name", "true"))); // NOI18N.
        nameCheckItem.selectedProperty().addListener(searchInvalidationListener);
        genderCheckItem.setSelected(Boolean.parseBoolean(settings.getProperty("search.filter.gender", "true"))); // NOI18N.
        genderCheckItem.selectedProperty().addListener(searchInvalidationListener);
        raceCheckItem.setSelected(Boolean.parseBoolean(settings.getProperty("search.filter.race", "true"))); // NOI18N.
        raceCheckItem.selectedProperty().addListener(searchInvalidationListener);
        professionCheckItem.setSelected(Boolean.parseBoolean(settings.getProperty("search.filter.profession", "true"))); // NOI18N.
        professionCheckItem.selectedProperty().addListener(searchInvalidationListener);
        guildCheckItem.setSelected(Boolean.parseBoolean(settings.getProperty("search.filter.guild", "true"))); // NOI18N.
        guildCheckItem.selectedProperty().addListener(searchInvalidationListener);
        guildTagCheckItem.setSelected(Boolean.parseBoolean(settings.getProperty("search.filter.guild_tag", "true"))); // NOI18N.
        guildTagCheckItem.selectedProperty().addListener(searchInvalidationListener);
        //
        searchField.textProperty().addListener(searchInvalidationListener);
        //
        applicationKeyOptional.ifPresent(applicationKey -> {
            applicationKeyField.setText(applicationKey);
        });
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
        final String[] criteria = (searchText == null || searchText.trim().isEmpty()) ? null : Arrays.stream(searchText.trim().split("[\\s,;]+")) // NOI18N.
                .map(this::normalizeForSearch)
                .collect(Collectors.toList())
                .toArray(new String[0]);
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
        final boolean filterName = nameCheckItem.isSelected();
        final boolean filterGender = genderCheckItem.isSelected();
        final boolean filterRace = raceCheckItem.isSelected();
        final boolean filterProfession = professionCheckItem.isSelected();
        final boolean filterGuild = guildCheckItem.isSelected();
        final boolean filterGuildTag = guildTagCheckItem.isSelected();
        final String name = filterName ? normalizeForSearch(character.getName()) : null;
        // On a besoin du sexe pour le label de la profession et de la race.
        final Character.Gender gender = character.getGender();
        final String baseGender = filterGender ? normalizeForSearch(gender.name()) : null;
        final String i18nGender = filterGender ? normalizeForSearch(LabelUtils.genderLabel(resources, gender)) : null;
        final Character.Race race = filterRace ? character.getRace() : null;
        final String baseRace = (race == null) ? null : normalizeForSearch(race.name());
        final String i18nRace = (race == null) ? null : normalizeForSearch(LabelUtils.raceLabel(resources, gender, race));
        final Character.Profession profession = filterProfession ? character.getProfession() : null;
        final String baseProfession = (profession == null) ? null : normalizeForSearch(profession.name());
        final String i18nProfession = (profession == null) ? null : normalizeForSearch(LabelUtils.professionLabel(resources, gender, profession));
        final Guild guild = (filterGuild || filterGuildTag) ? CharacterAndGuildUtils.guildForCharacter(character, currentQueryResult.guilds) : null;
        final String guildName = (guild == null) ? null : normalizeForSearch(guild.getName());
        final String guildTag = (guild == null) ? null : normalizeForSearch(guild.getTag());
        //
        boolean result = true;
        for (final String criterion : criteria) {
            boolean criterionTest = false;
            // Teste le nom du personnage.  
            if (name != null) {
                final boolean characterFound = name.contains(criterion);
                criterionTest |= characterFound;
            }
            // Teste le sexe du personnage.       
            if (baseGender != null) {
                final boolean baseGenderFound = baseGender.contains(criterion);
                criterionTest |= baseGenderFound;
            }
            if (i18nGender != null) {
                final boolean i18nGenderFound = i18nGender.contains(criterion);
                criterionTest |= i18nGenderFound;
            }
            // Teste la race du personnage. 
            if (baseRace != null) {
                final boolean baseRaceFound = baseRace.contains(criterion);
                criterionTest |= baseRaceFound;
            }
            if (i18nRace != null) {
                final boolean i18nRaceFound = i18nRace.contains(criterion);
                criterionTest |= i18nRaceFound;
            }
            // Teste la profession du personnage.       
            if (baseProfession != null) {
                final boolean baseProfessionFound = baseProfession.contains(criterion);
                criterionTest |= baseProfessionFound;
            }
            if (i18nProfession != null) {
                final boolean i18nProfessionFound = i18nProfession.contains(criterion);
                criterionTest |= i18nProfessionFound;
            }
            // Teste le nom de la guilde.
            if (guildName != null) {
                final boolean guildNameFound = guildName.contains(criterion);
                criterionTest |= guildNameFound;
            }
            // Test le tag de la guilde.
            if (guildTag != null) {
                final boolean guildTagFound = guildTag.contains(criterion);
                criterionTest |= guildTagFound;
            }
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
        stopUpdateService();
        characterList.clear();
        listingVBox.setVisible(false);
        progressIndicator.setVisible(false);
        applicationKeyPermissionFlow.getChildren().clear();
        messageLabel.setText(null);
        messageLabel.setVisible(false);
        messageLabel.pseudoClassStateChanged(errorPseudoClass, false);
        if (applicationKeyValid) {
            settings.setProperty("app.key", applicationKey); // NOI18N.
            checkApplicationKeyAndStartUpdate();
        } else {
            settings.setProperty("app.key", ""); // NOI18N.
            messageLabel.setVisible(true);
            messageLabel.setText(resources.getString("no.account.label")); // NOI18N.
        }
    }

    /**
     * Le service qui va vérifier la validité de la clé.
     */
    private Service<TokenInfo> applicationKeyCheckService;

    /**
     * Vérifie les permission de la clé et si ok lance le service de mise à jour.
     */
    private void checkApplicationKeyAndStartUpdate() {
        if (applicationKeyCheckService == null) {
            applicationKeyCheckService = new Service<TokenInfo>() {

                @Override
                protected Task<TokenInfo> createTask() {
                    return new Task<TokenInfo>() {

                        @Override
                        protected TokenInfo call() throws Exception {
                            final String applicationKey = settings.getProperty("app.key"); // NOI18N.
                            final TokenInfo result = TokenInfoQuery.tokenInfo(applicationKey);
                            return result;
                        }
                    };
                }
            };
            applicationKeyCheckService.setOnSucceeded(workerStateEvent -> {
                final TokenInfo result = (TokenInfo) workerStateEvent.getSource().getValue();
                final List<TokenInfo.Permission> permissions = result.getPermissions();
                final List<Label> permissionsLabel = permissions.stream()
                        .map(permission -> {
                            final String text = LabelUtils.permissionLabel(resources, permission);
                            final Label label = new Label(text);
                            label.getStyleClass().add("permission-label");
                            return label;
                        })
                        .collect(Collectors.toList());
                applicationKeyPermissionFlow.getChildren().setAll(permissionsLabel);
                if (permissions.contains(TokenInfo.Permission.ACCOUNT) && permissions.contains(TokenInfo.Permission.CHARACTERS)) {
                    startUpdateService();
                } else {
                    messageLabel.setVisible(true);
                    messageLabel.pseudoClassStateChanged(errorPseudoClass, true);
                    messageLabel.setText(resources.getString("bad.permission.error"));
                }
            });
            applicationKeyCheckService.setOnCancelled(workerStateEvent -> {
            });
            applicationKeyCheckService.setOnFailed(workerStateEvent -> {
                messageLabel.setVisible(true);
                messageLabel.pseudoClassStateChanged(errorPseudoClass, true);
                messageLabel.setText(resources.getString("application_key.failed.error"));
                workerStateEvent.getSource().getException().printStackTrace();
            });
        }
        applicationKeyCheckService.restart();
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
    private void startUpdateService() {
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
                final Optional<Character> oldSelectionOptional = Optional.ofNullable(characterListView.getSelectionModel().getSelectedItem());
                characterList.setAll(currentQueryResult.characters);
                characterListView.setCellFactory(listView -> new CharacterListCell(currentQueryResult.guilds));
                listingVBox.setVisible(true);
                progressIndicator.setVisible(false);
                //
                guildsFlow.getChildren().clear();
                currentQueryResult.guilds
                        .stream()
                        .forEach(guild -> {
                            final long charactersInGuild = currentQueryResult.characters
                            .stream()
                            .filter(character -> guild.getId().equals(character.getGuild()))
                            .count();
                            final String guildText = CharacterAndGuildUtils.guildLabel(guild);
                            final String countText = String.format("(%d)", charactersInGuild);
                            final Text guildLabel = new Text(guildText);
                            guildLabel.getStyleClass().add("guild-label");
                            guildsFlow.getChildren().add(guildLabel);
                            if (charactersInGuild > 0) {
                                final Text spaceLabel = new Text(" ");
                                final Text countLabel = new Text(countText);
                                countLabel.getStyleClass().add("guild-count-label");
                                guildsFlow.getChildren().addAll(spaceLabel, countLabel);
                            }
                            final Text separatorLabel = new Text(" • ");
                            separatorLabel.getStyleClass().add("guild-separator-label");
                            guildsFlow.getChildren().add(separatorLabel);
                        });
                if (!guildsFlow.getChildren().isEmpty()) {
                    guildsFlow.getChildren().remove(guildsFlow.getChildren().size() - 1);
                }
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
                messageLabel.setVisible(true);
                messageLabel.pseudoClassStateChanged(errorPseudoClass, true);
                messageLabel.setText(resources.getString("application_key.failed.error"));
                workerStateEvent.getSource().getException().printStackTrace();
            });
        }
        listingVBox.setVisible(false);
        progressIndicator.setVisible(true);
        updateService.restart();
    }

    /**
     * Stoppe le service de mise à jour automatique.
     */
    private void stopUpdateService() {
        if (updateService == null) {
            return;
        }
        updateService.cancel();
    }
}
