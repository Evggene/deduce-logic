

import org.junit.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;


public class Tests extends Assert {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

//
//    @Test(expected = Exception.class)       // на все эти идентификаторы вылетает Exception
//    public void testIdentificators1() throws Exception {
//        Parser p = new Parser(new String("1"));
//        ArrayList<String> list = new ArrayList();
//
//        list.add("1D && F -> K");
//        p.validateLine(list);
//        list.clear();
//
//        list.add("^gh && F -> K");
//        p.validateLine(list);
//        list.clear();
//
//        list.add("gh& && F -> K");
//        p.validateLine(list);
//        list.clear();

//        p.checkIdentificator("_");
//        p.checkIdentificator("3");
//        p.checkIdentificator("_|||");
//        p.checkIdentificator("__");
//        p.checkIdentificator("___");
//        p.checkIdentificator("_12H||");
//        p.checkIdentificator("_333");
//        p.checkIdentificator(",");
//    }
//
//
//    @Test       // валидные идентификаторы
//    public void testIdentificators2() throws Exception {
//        Parser p = new Parser(listOfLogics, listOfResults);
//        p.checkIdentificator("d");   // valid
//        p.checkIdentificator("g2h");   // valid
//        p.checkIdentificator("g_h");   // valid
//        p.checkIdentificator("_gh");  // valid
//        p.checkIdentificator("_12G");  // valid
//    }


    @Test
    public void test1() {                           // валидный файл, тест на логику
        File file = new File("functionalTests/valid.txt");
        Main.main(new String[]{file.getAbsolutePath()});

        assertEquals("Df, S, L, H, ZZ, O, D", outContent.toString());
    }

    @Test
    public void test000() {                           // пустой путь

        Main.main(new String[]{});

        assertEquals("Missing argument", outContent.toString());
    }

    @Test
    public void test00() {                           // неправильный путь

        Main.main(new String[]{"про"});

        assertEquals("Error when reading file: про", outContent.toString());
    }

    @Test
    public void test2() {                           // пустой файл

        File file = new File("functionalTests/empty_file.txt");
        Main.main(new String[]{file.getAbsolutePath()});

        assertEquals("File does not valid: missing or wrong separator", outContent.toString());
    }

    @Test
    public void test3() {                           // ошибка в правилах: правила отсутствуют

        File file = new File("functionalTests/rule_error_emptyRule.txt");
        Main.main(new String[]{file.getAbsolutePath()});

        assertEquals("File does not valid: missing rules", outContent.toString());
    }

    @Test
    public void test4() {                           // ошибка в фактах: факты отсутствуют

        File file = new File("functionalTests/facts_error_emptyFacts.txt");
        Main.main(new String[]{file.getAbsolutePath()});

        assertEquals("File does not valid: missing facts or empty line", outContent.toString());
    }

    @Test
    public void test5() {                           // невалидный файл: нет разделителя

        File file = new File("functionalTests/absent_separator.txt");
        Main.main(new String[]{file.getAbsolutePath()});

        assertEquals("File does not valid: missing rules", outContent.toString());
    }

    @Test
    public void test7() {                           // ошибка в данных - неверный логический символ

        File file = new File("functionalTests/rules_error.txt");
        Main.main(new String[]{file.getAbsolutePath()});

        assertEquals("File does not valid: Wrong value S|  K", outContent.toString());
    }

    @Test
    public void test8() {                           // ошибка в данных - отсутствует ->

        File file = new File("functionalTests/missing_pointer.txt");
        Main.main(new String[]{file.getAbsolutePath()});

        assertEquals("File does not valid: missing ->", outContent.toString());
    }

    @Test
    public void test9() {                           // ошибка в данных - неправильный разделитель

        File file = new File("functionalTests/wrong_separator.txt");
        Main.main(new String[]{file.getAbsolutePath()});

        assertEquals("File does not valid: missing ->", outContent.toString());
    }

    @Test
    public void test10() {                           // ошибка в данных - перепутаны правила и факты

        File file = new File("functionalTests/wrong_rules_facts.txt");
        Main.main(new String[]{file.getAbsolutePath()});

        assertEquals("File does not valid: Wrong value Df || Yg && jhkl->ZZ", outContent.toString());
    }

    @Test
    public void test11() {                           // ошибка в правилах - пустая линия

        File file = new File("functionalTests/missing_Line_rules.txt");
        Main.main(new String[]{file.getAbsolutePath()});

        assertEquals("File does not valid: missing rules", outContent.toString());
    }

}




