package test.scene.renderer;

import java.util.List;
import java.util.Optional;
import javafx.scene.control.ListCell;
import test.data.guild.Guild;
import test.data.character.Character;

/**
 * Cellule pour afficher un personnage.
 * @author Fabrice
 */
public class CharacterListCell extends ListCell<Character> {

    private final List<Guild> guilds;

    public CharacterListCell(final List<Guild> guilds) {
        this.guilds = guilds;
    }

    @Override
    protected void updateItem(final Character item, final boolean empty) {
        super.updateItem(item, empty);
        if (!empty && item != null) {
            final Optional<Guild> guildOptional = guilds.stream()
                    .filter(guild -> guild.getId().equals(item.getGuild()))
                    .findFirst();
            String text = item.getName();
            if (guildOptional.isPresent()) {
                text += " - " + guildOptional.get().getName();
            }
            setText(text);
        }
    }
}
