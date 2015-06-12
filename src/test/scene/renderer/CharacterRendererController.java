package test.scene.renderer;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import test.data.character.Character;
import test.data.guild.Guild;

/**
 * Contrôleur du FXML.
 * @author Fabrice Bouyé
 */
public class CharacterRendererController implements Initializable {

    @FXML
    private Label nameLabel;
    @FXML
    private Text professionAndRaceLabel;
    @FXML
    private Text guildTagLabel;

    @Override
    public void initialize(final URL url, final ResourceBundle resouces) {
        guildTagLabel.managedProperty().bind(guildTagLabel.visibleProperty());
    }

    /**
     * Met à jour l'affichage.
     * @param character Le personnage, peut être {@code null}.
     * @param guild La guilde, peut être {@code null}.
     */
    public void updateDisplay(final Character character, final Guild guild) {
        final String nameText = (character == null) ? null : character.getName();        
        nameLabel.setText(nameText);
        final String professionAndRaceText = (character == null) ? null : CharacterAndGuildUtils.professionAndRaceLabel(character);
        professionAndRaceLabel.setText(professionAndRaceText);
        final String guildText = (guild == null) ? null : CharacterAndGuildUtils.guildTagLabel(guild);
        guildTagLabel.setText(guildText);
        guildTagLabel.setVisible(guild != null);
    }
}
