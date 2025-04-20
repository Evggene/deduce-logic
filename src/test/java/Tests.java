

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import red.Main;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class Tests {

    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.err;

    @BeforeAll
    public static void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterAll
    public static void restoreStreams() {
        System.setOut(originalOut);
    }


    @Test
    public void test1() throws Exception {                           // валидный файл, тест на логику

        File s = new File(getClass().getResource("first.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("A, H, H1, H3, H5, H6, H7", outContent.toString());
    }

    @Test
    public void test111() throws Exception {                           // валидный файл, тест на логику

        File s = new File(getClass().getResource("simple_logic_set.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("GG, S, D, GGG, G, H", outContent.toString());
    }

    @Test
    public void test21() throws Exception {                           // валидный файл посложнее, тест на логику

        File s = new File(getClass().getResource("valid.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("ZZ, _O3, S, ZZ2, D, _yu6, H, Df7g_u8", outContent.toString());
    }


    @Test
    public void test00() throws Exception {                           // неправильный путь

        File s = new File("про");
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error when reading file: C:\\Users\\Evgeniy.Bezlepkin\\Desktop\\EvgenyDeduce2\\про"
                , outContent.toString());
    }

    @Test
    public void test2() throws Exception {                           // пустой файл

        File s = new File(getClass().getResource("empty_file.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 0: missing or wrong separator", outContent.toString());
    }

    @Test
    public void test3() throws Exception {                           // ошибка в правилах: правила отсутствуют

        File s = new File(getClass().getResource("rule_error_emptyRule.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 2: missing rules", outContent.toString());
    }

    @Test
    public void test4() throws Exception {                           // ошибка в фактах: факты отсутствуют

        File s = new File(getClass().getResource("facts_error_emptyFacts.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 7: missing known facts", outContent.toString());
    }

    @Test
    public void test5() throws Exception {                           // невалидный файл: нет разделителя

        File s = new File(getClass().getResource("absent_separator.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 7: invalid rule syntax)", outContent.toString());
    }

    @Test
    public void test7() throws Exception {                           // ошибка в данных - неверный логический символ

        File s = new File(getClass().getResource("rules_error.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 4: invalid rule syntax (wrong operator)", outContent.toString());
    }

    @Test
    public void test72() throws Exception {                           // ошибка в данных - неверный логический символ

        File s = new File(getClass().getResource("rules_error_2.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 1: invalid rule syntax (wrong operator)", outContent.toString());
    }

    @Test
    public void test73() throws Exception {                           // ошибка в данных - неверный логический символ

        File s = new File(getClass().getResource("rules_error_3.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 1: invalid rule syntax (wrong operator)", outContent.toString());
    }

    @Test
    public void test8() throws Exception {                           // ошибка в данных - отсутствует ->

        File s = new File(getClass().getResource("missing_pointer.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 4: invalid rule syntax", outContent.toString());
    }

    @Test
    public void test9() throws Exception {                           // ошибка в данных - неправильный разделитель

        File s = new File(getClass().getResource("wrong_separator.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 7: invalid rule syntax", outContent.toString());
    }

    @Test
    public void test10() throws Exception {                           // ошибка в данных - перепутаны правила и факты

        File s = new File(getClass().getResource("wrong_rules_facts.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 8: error with known facts", outContent.toString());
    }

    @Test
    public void test11() throws Exception {                           // ошибка в правилах - пустая линия

        File s = new File(getClass().getResource("missing_Line_rules.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 7: invalid rule syntax)", outContent.toString());
    }

    @Test
    public void test12() throws Exception {                           // ошибка в правилах - висячая строка

        File s = new File(getClass().getResource("hanging_rule.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 6: invalid rule syntax", outContent.toString());
    }

    @Test
    public void test221() throws Exception {                           // ошибка в правилах - пустая линия

        File s = new File(getClass().getResource("wrong_fact_space.txt").getFile());
        Main.main(new String[]{"-deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 4: invalid rule syntax", outContent.toString());
    }

    @Test
    public void test222() throws Exception {                           // ошибка в правилах - неверный выводимый факт

        File s = new File(getClass().getResource("rules_error_4.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 1: invalid rule syntax (wrong symbol in deducing fact)", outContent.toString());
    }

    @Test
    public void test223() throws Exception {                           // ошибка в правилах - ошибка в факте

        File s = new File(getClass().getResource("rules_error_5.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 1: invalid rule syntax (wrong symbols)", outContent.toString());
    }

    @Test
    public void test224() throws Exception {                           // ошибка в фактах

        File s = new File(getClass().getResource("rules_error_6.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("Error in line 8: error with known facts", outContent.toString());
    }

    @Test
    public void test225() throws Exception {                           // сложные правила

        File s = new File(getClass().getResource("validS.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("ZZ, Df, A3, ZZ2, e, K, L, M", outContent.toString());
    }

    @Test
    public void test226() throws Exception {                           // сложные правила

        File s = new File(getClass().getResource("simple_brackets.txt").getFile());
        Main.main(new String[]{"deduce", "-txtin", s.getAbsolutePath()});

        assertEquals("A, H, H1, H3, H5, H6, H7", outContent.toString());
    }

    @Test
    public void test227() throws Exception {                           // сложные правила

        File s = new File(getClass().getResource("first.xml").getFile());
        Main.main(new String[]{"deduce", "-xmlin", s.getAbsolutePath()});

        assertEquals("A, B, C", outContent.toString());
    }


    @Test
    public void test228() throws Exception {                           // проверка конвертации xml в txt

        File input = new File(getClass().getResource("logic.xml").getFile());
        File output = new File(("checkxmllogic.txt"));
        File check = new File(getClass().getResource("checkxmllogic2.txt").getFile());

        Main.main(new String[]{"write", "-xmlin", input.getAbsolutePath(), "-txtout", output.getAbsolutePath()});

        assertEquals("Conversion is done", outContent.toString());
        assertEquals("The files differ!",
                FileUtils.readFileToString(output, "utf-8"),
                FileUtils.readFileToString(check, "utf-8"));
    }


//    @Wrapper
//    public void test229() throws IOException {                           // проверка конвертации txt в xml
//
//        File input = new File(getClass().getResource("first.txt").getFile());
//        File output = new File("checkfirstxmlc.xml");
//        File check = new File(getClass().getResource("logic.xml").getFile());
//        red.Main.main(new String[]{"write", "-txtin", input.getAbsolutePath(), "-xmlout", output.getAbsolutePath()});
//
//        assertEquals("Conversion is done", outContent.toString());
//        assertEquals("The files differ!",
//                FileUtils.readFileToString(output, "utf-8"),
//                FileUtils.readFileToString(check, "utf-8"));
//    }


//    @Wrapper
//    public void test2299() throws IOException {                           // проверка конвертации txt в db и обратно в txt
//                                                                        // в бд необходима модель checkLogic
//
//        File output = new File("dbcheck.txt");
//        File check = new File(getClass().getResource("checkxmllogic2.txt").getFile());
//        red.Main.main(new String[]{"write", "-dbin", "checkLogic", "D:\\config.xml", "-txtout", output.getAbsolutePath()});
//
//        //assertEquals("Conversion is done", outContent.toString());
//        assertEquals("The files differ!",
//                FileUtils.readFileToString(output, "utf-8"),
//                FileUtils.readFileToString(check, "utf-8"));
//    }



}




