package test.demo;

import java.util.List;
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
        assertEquals(1, result.getGuilds().size());
        assertEquals(true, result.getGuilds().contains("XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX"));
    }

    /**
     * Test de la méthode {@code guildInfo}.
     */
    @Test
    public void testGuildInfo() throws Exception {
        System.out.println("guildInfo");
        final Guild result = DemoSupport.guildInfo();
        assertEquals("XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX", result.getId());
        assertEquals("Destiny's Edge", result.getName());
        assertEquals("DE", result.getTag());
    }

    /**
     * Test de la méthode {@code listCharacters}.
     */
    @Test
    public void testListCharacters() throws Exception {
        System.out.println("listCharacters");
        final List<String> result = DemoSupport.listCharacters();
        assertEquals(5, result.size());
        assertEquals(true, result.contains("Rytlock Brimstone"));
        assertEquals(true, result.contains("Logan Thackeray"));
        assertEquals(true, result.contains("Eir Stegalkin"));
        assertEquals(true, result.contains("Zojja"));
        assertEquals(true, result.contains("Caithe"));
    }

    /**
     * Test de la méthode {@code listCharacters}.
     */
    @Test
    public void testCharacterInfos() throws Exception {
        System.out.println("charactersInfos");
        final List<Character> result = DemoSupport.characterInfos();
        assertEquals(5, result.size());
        //
        assertEquals("Rytlock Brimstone", result.get(0).getName());
        assertEquals(Character.Race.CHARR, result.get(0).getRace());
        assertEquals(Character.Profession.WARRIOR, result.get(0).getProfession());
        assertEquals(80, result.get(0).getLevel());
        assertEquals("XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX", result.get(0).getGuild());
        //
        assertEquals("Logan Thackeray", result.get(1).getName());
        assertEquals(Character.Race.HUMAN, result.get(1).getRace());
        assertEquals(Character.Profession.GUARDIAN, result.get(1).getProfession());
        assertEquals(80, result.get(1).getLevel());
        assertEquals("XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX", result.get(1).getGuild());
        //
        assertEquals("Eir Stegalkin", result.get(2).getName());
        assertEquals(Character.Race.NORN, result.get(2).getRace());
        assertEquals(Character.Profession.RANGER, result.get(2).getProfession());
        assertEquals(80, result.get(2).getLevel());
        assertEquals("XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX", result.get(2).getGuild());
        //
        assertEquals("Zojja", result.get(3).getName());
        assertEquals(Character.Race.ASURA, result.get(3).getRace());
        assertEquals(Character.Profession.ELEMENTALIST, result.get(3).getProfession());
        assertEquals(80, result.get(3).getLevel());
        assertEquals("XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX", result.get(3).getGuild());
        //
        assertEquals("Caithe", result.get(4).getName());
        assertEquals(Character.Race.SYLVARI, result.get(4).getRace());
        assertEquals(Character.Profession.THIEF, result.get(4).getProfession());
        assertEquals(80, result.get(4).getLevel());
        assertEquals("XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX", result.get(4).getGuild());
    }
}
