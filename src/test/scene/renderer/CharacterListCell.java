package test.scene.renderer;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import test.data.guild.Guild;
import test.data.character.Character;

/**
 * Cellule pour afficher un personnage.
 * @author Fabrice Bouyé
 */
public class CharacterListCell extends ListCell<Character> {

    private final List<Guild> guilds;

    private Node graphic;
    private CharacterRendererController controller;

    public CharacterListCell(final List<Guild> guilds) {
        this.guilds = guilds;
        final URL fxmlURL = getClass().getResource("CharacterRenderer.fxml");
        final FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);
        try {
            graphic = fxmlLoader.load();
            controller = fxmlLoader.getController();
        } catch (IOException ex) {
            Logger.getLogger(CharacterListCell.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Override
    protected void updateItem(final Character item, final boolean empty) {
        super.updateItem(item, empty);
        Node graphic = null;
        if (!empty && item != null) {
            graphic = this.graphic;
            final Guild guild = CharacterAndGuildUtils.guildForCharacter(item, guilds);
            controller.updateDisplay(item, guild);
        } else {
            controller.updateDisplay(null, null);
        }
        setGraphic(graphic);
        setText(null);
    }
}
