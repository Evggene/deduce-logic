

import org.apache.commons.io.FileUtils;
import org.junit.*;

import java.io.*;


public class Tests extends Assert {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.err;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }


    @Test
    public void test1() throws Exception {                           // валидный файл, тест на логику

        File s = new File(getClass().getResource("first.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("A, H, H1, H3, H5, H6, H7", outContent.toString());
    }

    @Test
    public void test111() {                           // валидный файл, тест на логику

        File s = new File(getClass().getResource("simple_logic_set.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("GG, S, D, GGG, G, H", outContent.toString());
    }

    @Test
    public void test21() {                           // валидный файл посложнее, тест на логику

        File s = new File(getClass().getResource("valid.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("ZZ, _O3, S, ZZ2, D, _yu6, H, Df7g_u8", outContent.toString());
    }


    @Test
    public void test00() {                           // неправильный путь

        File s = new File("про");
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error when reading file: C:\\Users\\Evgeniy.Bezlepkin\\Desktop\\EvgenyDeduce2\\про"
                , outContent.toString());
    }

    @Test
    public void test2() {                           // пустой файл

        File s = new File(getClass().getResource("empty_file.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 0: missing or wrong separator", outContent.toString());
    }

    @Test
    public void test3() {                           // ошибка в правилах: правила отсутствуют

        File s = new File(getClass().getResource("rule_error_emptyRule.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 2: missing rules", outContent.toString());
    }

    @Test
    public void test4() {                           // ошибка в фактах: факты отсутствуют

        File s = new File(getClass().getResource("facts_error_emptyFacts.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 7: missing known facts", outContent.toString());
    }

    @Test
    public void test5() {                           // невалидный файл: нет разделителя

        File s = new File(getClass().getResource("absent_separator.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 7: invalid rule syntax)", outContent.toString());
    }

    @Test
    public void test7() {                           // ошибка в данных - неверный логический символ

        File s = new File(getClass().getResource("rules_error.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 4: invalid rule syntax (wrong operator)", outContent.toString());
    }

    @Test
    public void test72() {                           // ошибка в данных - неверный логический символ

        File s = new File(getClass().getResource("rules_error_2.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 1: invalid rule syntax (wrong operator)", outContent.toString());
    }

    @Test
    public void test73() {                           // ошибка в данных - неверный логический символ

        File s = new File(getClass().getResource("rules_error_3.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 1: invalid rule syntax (wrong operator)", outContent.toString());
    }

    @Test
    public void test8() {                           // ошибка в данных - отсутствует ->

        File s = new File(getClass().getResource("missing_pointer.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 4: invalid rule syntax", outContent.toString());
    }

    @Test
    public void test9() {                           // ошибка в данных - неправильный разделитель

        File s = new File(getClass().getResource("wrong_separator.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 7: invalid rule syntax", outContent.toString());
    }

    @Test
    public void test10() {                           // ошибка в данных - перепутаны правила и факты

        File s = new File(getClass().getResource("wrong_rules_facts.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 8: error with known facts", outContent.toString());
    }

    @Test
    public void test11() {                           // ошибка в правилах - пустая линия

        File s = new File(getClass().getResource("missing_Line_rules.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 7: invalid rule syntax)", outContent.toString());
    }

    @Test
    public void test12() {                           // ошибка в правилах - висячая строка

        File s = new File(getClass().getResource("hanging_rule.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 6: invalid rule syntax", outContent.toString());
    }

    @Test
    public void test221() {                           // ошибка в правилах - пустая линия

        File s = new File(getClass().getResource("wrong_fact_space.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 4: invalid rule syntax", outContent.toString());
    }

    @Test
    public void test222() {                           // ошибка в правилах - неверный выводимый факт

        File s = new File(getClass().getResource("rules_error_4.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 1: invalid rule syntax (wrong symbol in deducing fact)", outContent.toString());
    }

    @Test
    public void test223() {                           // ошибка в правилах - ошибка в факте

        File s = new File(getClass().getResource("rules_error_5.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 1: invalid rule syntax (wrong symbols)", outContent.toString());
    }

    @Test
    public void test224() {                           // ошибка в фактах

        File s = new File(getClass().getResource("rules_error_6.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 8: error with known facts", outContent.toString());
    }

    @Test
    public void test225() {                           // сложные правила

        File s = new File(getClass().getResource("validS.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("ZZ, Df, A3, ZZ2, e, K, L, M", outContent.toString());
    }

    @Test
    public void test226() {                           // сложные правила

        File s = new File(getClass().getResource("simple_brackets.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("A, H, H1, H3, H5, H6, H7", outContent.toString());
    }

    @Test
    public void test227() {                           // сложные правила

        File s = new File(getClass().getResource("first.xml").getFile());
        Main.main(new String[]{"deduce", "-xmlin", s.getAbsolutePath()});

        assertEquals("A, B, C", outContent.toString());
    }


    @Test
    public void test228() throws IOException {                           // проверка конвертации xml в txt

        File input = new File(getClass().getResource("logic.xml").getFile());
        File output = new File(("checkxmllogic.txt"));
        File check = new File(getClass().getResource("checkxmllogic2.txt").getFile());

        Main.main(new String[]{"convert", "-xmlin", input.getAbsolutePath(), "-txtout", output.getAbsolutePath()});

        assertEquals("Conversion is done", outContent.toString());
        assertEquals("The files differ!",
                FileUtils.readFileToString(output, "utf-8"),
                FileUtils.readFileToString(check, "utf-8"));
    }


    @Test
    public void test229() throws IOException {                           // проверка конвертации txt в xml

        File input = new File(getClass().getResource("first.txt").getFile());
        File output = new File("checkfirstxmlc.xml");
        File check = new File(getClass().getResource("logic.xml").getFile());
        Main.main(new String[]{"convert", "-txtin", input.getAbsolutePath(), "-xmlout", output.getAbsolutePath()});

        assertEquals("Conversion is done", outContent.toString());
        assertEquals("The files differ!",
                FileUtils.readFileToString(output, "utf-8"),
                FileUtils.readFileToString(check, "utf-8"));
    }


    @Test
    public void test2299() throws IOException {                           // проверка конвертации txt в db и обратно в txt

        File output = new File("dbcheck.txt");
        File check = new File(getClass().getResource("checkxmllogic2.txt").getFile());
        Main.main(new String[]{"convert", "-dbin", "checkLogic", "D:\\config.xml", "-txtout", output.getAbsolutePath()});

        //assertEquals("Conversion is done", outContent.toString());
        assertEquals("The files differ!",
                FileUtils.readFileToString(output, "utf-8"),
                FileUtils.readFileToString(check, "utf-8"));
    }



}




