package test.demo;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import test.data.account.Account;
import test.data.character.Character;
import test.data.guild.Guild;
import test.data.tokeninfo.TokenInfo;

/**
 * Test de la classe {@code DemoSupport}.
 * @author Fabrice Bouyé
 */
public class DemoSupportTest {
    
    public DemoSupportTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test de la méthode {@code isDemoApplicationKey}.
     */
    @Test
    public void testIsDemoApplicationKey() {
        System.out.println("isDemoApplicationKey");
        final String[] values = {"XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXXXXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX",
            "FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFFFFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF",
            "",
            null};
        final boolean[] expResults = {true, false, false, false};
        IntStream.range(0, values.length)
                .forEach(index -> {
                    final String value = values[index];
                    final boolean expResult = expResults[index];
                    final boolean result = DemoSupport.isDemoApplicationKey(value);
                    assertEquals(expResult, result);
                });
    }

    /**
     * Test de la méthode {@code tokenInfo}.
     */
    @Test
    public void testTokenInfo() throws Exception {
        System.out.println("tokenInfo");
        final TokenInfo result = DemoSupport.tokenInfo();
        assertEquals("XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX", result.getId());
        assertEquals("Demo Key", result.getName());
        assertEquals(2, result.getPermissions().size());
        assertEquals(true, result.getPermissions().contains(TokenInfo.Permission.ACCOUNT));
        assertEquals(true, result.getPermissions().contains(TokenInfo.Permission.CHARACTERS));
    }

    /**
     * Test de la méthode {@code accountInfo}.
     */
    @Test
    public void testAccountInfo() throws Exception {
        System.out.println("tokenInfo");
        final Account result = DemoSupport.accountInfo();
        assertEquals("XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX", result.getId());
        assertEquals("ExampleAccount.1234", result.getName());
        assertEquals(1001, result.getWorld());
        assertEquals(3, result.getGuilds().size());
        assertEquals(true, result.getGuilds().contains("01"));
        assertEquals(true, result.getGuilds().contains("02"));
        assertEquals(true, result.getGuilds().contains("03"));
    }

    /**
     * Test de la méthode {@code guildInfo}.
     */
    @Test
    public void testGuildInfo() throws Exception {
        System.out.println("guildInfo");
        final String[] guildIds = {"01", "02", "03"};
        final String[] guildNames = {"Destiny's Edge", "Delaqua Investigations", "Rox's Warband"};
        final String[] guildTags = {"DE", "DIE", "RoX"};
        IntStream.range(0, guildIds.length)
                .forEach(index -> {
                    try {
                        final String guildId = guildIds[index];
                        final Guild result = DemoSupport.guildInfo(guildId);
                        assertEquals(guildId, result.getId());
                        assertEquals(guildNames[index], result.getName());
                        assertEquals(guildTags[index], result.getTag());
                    } catch (IOException ex) {
                        fail(ex.getMessage());
                    }
                });
    }

    /**
     * Test de la méthode {@code listCharacters}.
     */
    @Test
    public void testListCharacters() throws Exception {
        System.out.println("listCharacters");
        final List<String> result = DemoSupport.listCharacters();
        assertEquals(10, result.size());
        assertEquals(true, result.contains("Rytlock Brimstone"));
        assertEquals(true, result.contains("Logan Thackeray"));
        assertEquals(true, result.contains("Eir Stegalkin"));
        assertEquals(true, result.contains("Zojja"));
        assertEquals(true, result.contains("Caithe"));
        assertEquals(true, result.contains("Marjory Delaqua"));
        assertEquals(true, result.contains("Kasmeer Meade"));
        assertEquals(true, result.contains("Rox Pickheart"));
        assertEquals(true, result.contains("Braham Eirsson"));
        assertEquals(true, result.contains("Taimi"));
    }

    /**
     * Test de la méthode {@code listCharacters}.
     */
    @Test
    public void testCharacterInfos() throws Exception {
        System.out.println("charactersInfos");
        final List<Character> result = DemoSupport.characterInfos();
        assertEquals(10, result.size());
        //
        assertEquals("Rytlock Brimstone", result.get(0).getName());
        assertEquals(Character.Race.CHARR, result.get(0).getRace());
        assertEquals(Character.Profession.WARRIOR, result.get(0).getProfession());
        assertEquals(80, result.get(0).getLevel());
        assertEquals("01", result.get(0).getGuild());
        //
        assertEquals("Logan Thackeray", result.get(1).getName());
        assertEquals(Character.Race.HUMAN, result.get(1).getRace());
        assertEquals(Character.Profession.GUARDIAN, result.get(1).getProfession());
        assertEquals(80, result.get(1).getLevel());
        assertEquals("01", result.get(1).getGuild());
        //
        assertEquals("Eir Stegalkin", result.get(2).getName());
        assertEquals(Character.Race.NORN, result.get(2).getRace());
        assertEquals(Character.Profession.RANGER, result.get(2).getProfession());
        assertEquals(80, result.get(2).getLevel());
        assertEquals("01", result.get(2).getGuild());
        //
        assertEquals("Zojja", result.get(3).getName());
        assertEquals(Character.Race.ASURA, result.get(3).getRace());
        assertEquals(Character.Profession.ELEMENTALIST, result.get(3).getProfession());
        assertEquals(80, result.get(3).getLevel());
        assertEquals("01", result.get(3).getGuild());
        //
        assertEquals("Caithe", result.get(4).getName());
        assertEquals(Character.Race.SYLVARI, result.get(4).getRace());
        assertEquals(Character.Profession.THIEF, result.get(4).getProfession());
        assertEquals(80, result.get(4).getLevel());
        assertEquals("01", result.get(4).getGuild());
        //
        assertEquals("Marjory Delaqua", result.get(5).getName());
        assertEquals(Character.Race.HUMAN, result.get(5).getRace());
        assertEquals(Character.Profession.NECROMANCER, result.get(5).getProfession());
        assertEquals(80, result.get(5).getLevel());
        assertEquals("02", result.get(5).getGuild());
        //
        assertEquals("Kasmeer Meade", result.get(6).getName());
        assertEquals(Character.Race.HUMAN, result.get(6).getRace());
        assertEquals(Character.Profession.MESMER, result.get(6).getProfession());
        assertEquals(80, result.get(6).getLevel());
        assertEquals("02", result.get(6).getGuild());
        //
        assertEquals("Rox Pickheart", result.get(7).getName());
        assertEquals(Character.Race.CHARR, result.get(7).getRace());
        assertEquals(Character.Profession.RANGER, result.get(7).getProfession());
        assertEquals(80, result.get(7).getLevel());
        assertEquals("03", result.get(7).getGuild());
        //
        assertEquals("Braham Eirsson", result.get(8).getName());
        assertEquals(Character.Race.NORN, result.get(8).getRace());
        assertEquals(Character.Profession.GUARDIAN, result.get(8).getProfession());
        assertEquals(80, result.get(8).getLevel());
        assertEquals("03", result.get(8).getGuild());
        //
        assertEquals("Taimi", result.get(9).getName());
        assertEquals(Character.Race.ASURA, result.get(9).getRace());
        assertEquals(Character.Profession.ENGINEER, result.get(9).getProfession());
        assertEquals(80, result.get(9).getLevel());
        assertEquals("03", result.get(9).getGuild());
    }
}
