

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


    @Test
    public void test1() {                           // валидный файл, тест на логику

        File s = new File(getClass().getResource("first.txt").getFile());
        Main.main(new String[]{s.getAbsolutePath()});

        assertEquals("ZZ, Df, S, D, H, L, O", outContent.toString());
    }

    @Test
    public void test111() {                           // валидный файл, тест на логику

        File s = new File(getClass().getResource("simple_logic_set.txt").getFile());
        Main.main(new String[]{s.getAbsolutePath()});

        assertEquals("GG, S, D, GGG, G, H", outContent.toString());
    }

    @Test
    public void test21() {                           // валидный файл посложнее, тест на логику

        File s = new File(getClass().getResource("valid.txt").getFile());
        Main.main(new String[]{s.getAbsolutePath()});

        assertEquals("ZZ, _O3, S, ZZ2, D, _yu6, H, Df7g_u8", outContent.toString());
    }

    @Test
    public void test000() {                           // пустой путь

        Main.main(new String[]{});

        assertEquals("Missing argument", outContent.toString());
    }

    @Test
    public void test00() {                           // неправильный путь

        File s = new File("про");
        Main.main(new String[]{s.getAbsolutePath()});

        assertEquals("Error when reading file: C:\\Users\\Evgeniy.Bezlepkin\\Desktop\\EvgenyDeduce2\\про"
                , outContent.toString());
    }

    @Test
    public void test2() {                           // пустой файл

        File s = new File(getClass().getResource("empty_file.txt").getFile());
        Main.main(new String[]{s.getAbsolutePath()});

        assertEquals("Invalid file: missing or wrong separator", outContent.toString());
    }

    @Test
    public void test3() {                           // ошибка в правилах: правила отсутствуют

        File s = new File(getClass().getResource("rule_error_emptyRule.txt").getFile());
        Main.main(new String[]{s.getAbsolutePath()});

        assertEquals("Invalid file: missing rules", outContent.toString());
    }

    @Test
    public void test4() {                           // ошибка в фактах: факты отсутствуют

        File s = new File(getClass().getResource("facts_error_emptyFacts.txt").getFile());
        Main.main(new String[]{s.getAbsolutePath()});

        assertEquals("Invalid file: missing known facts", outContent.toString());
    }

    @Test
    public void test5() {                           // невалидный файл: нет разделителя

        File s = new File(getClass().getResource("absent_separator.txt").getFile());
        Main.main(new String[]{s.getAbsolutePath()});

        assertEquals("Invalid file: invalid rule syntax", outContent.toString());
    }

    @Test
    public void test7() {                           // ошибка в данных - неверный логический символ

        File s = new File(getClass().getResource("rules_error.txt").getFile());
        Main.main(new String[]{s.getAbsolutePath()});

        assertEquals("Invalid file: invalid rule syntax", outContent.toString());
    }

    @Test
    public void test72() {                           // ошибка в данных - неверный логический символ

        File s = new File(getClass().getResource("rules_error_2.txt").getFile());
        Main.main(new String[]{s.getAbsolutePath()});

        assertEquals("Invalid file: invalid rule syntax", outContent.toString());
    }

    @Test
    public void test73() {                           // ошибка в данных - неверный логический символ

        File s = new File(getClass().getResource("rules_error_3.txt").getFile());
        Main.main(new String[]{s.getAbsolutePath()});

        assertEquals("Invalid file: invalid rule syntax", outContent.toString());
    }

    @Test
    public void test8() {                           // ошибка в данных - отсутствует ->

        File s = new File(getClass().getResource("missing_pointer.txt").getFile());
        Main.main(new String[]{s.getAbsolutePath()});

        assertEquals("Invalid file: invalid rule syntax", outContent.toString());
    }

    @Test
    public void test9() {                           // ошибка в данных - неправильный разделитель

        File s = new File(getClass().getResource("wrong_separator.txt").getFile());
        Main.main(new String[]{s.getAbsolutePath()});

        assertEquals("Invalid file: invalid rule syntax", outContent.toString());
    }

    @Test
    public void test10() {                           // ошибка в данных - перепутаны правила и факты

        File s = new File(getClass().getResource("wrong_rules_facts.txt").getFile());
        Main.main(new String[]{s.getAbsolutePath()});

        assertEquals("Invalid file: error with known facts", outContent.toString());
    }

    @Test
    public void test11() {                           // ошибка в правилах - пустая линия

        File s = new File(getClass().getResource("missing_Line_rules.txt").getFile());
        Main.main(new String[]{s.getAbsolutePath()});

        assertEquals("Invalid file: invalid rule syntax", outContent.toString());
    }

    @Test
    public void test12() {                           // ошибка в правилах - висячая строка

        File s = new File(getClass().getResource("hanging_rule.txt").getFile());
        Main.main(new String[]{s.getAbsolutePath()});

        assertEquals("Invalid file: invalid rule syntax", outContent.toString());
    }

    @Test
    public void test221() {                           // ошибка в правилах - пустая линия

        File s = new File(getClass().getResource("wrong_fact_space.txt").getFile());
        Main.main(new String[]{s.getAbsolutePath()});

        assertEquals("Invalid file: invalid rule syntax", outContent.toString());
    }

    @Test
    public void test222() {                           // ошибка в правилах - неверный выводимый факт

        File s = new File(getClass().getResource("rules_error_4.txt").getFile());
        Main.main(new String[]{s.getAbsolutePath()});

        assertEquals("Invalid file: invalid rule syntax", outContent.toString());
    }

    @Test
    public void test223() {                           // ошибка в правилах - ошибка в факте

        File s = new File(getClass().getResource("rules_error_5.txt").getFile());
        Main.main(new String[]{s.getAbsolutePath()});

        assertEquals("Invalid file: invalid rule syntax", outContent.toString());
    }

    @Test
    public void test224() {                           // ошибка в фактах

        File s = new File(getClass().getResource("rules_error_6.txt").getFile());
        Main.main(new String[]{s.getAbsolutePath()});

        assertEquals("Invalid file: error with known facts", outContent.toString());
    }

    @Test
    public void test225() {                           // сложные правила

        File s = new File(getClass().getResource("validS.txt").getFile());
        Main.main(new String[]{s.getAbsolutePath()});

        assertEquals("ZZ, Df, A3, ZZ2, e, K, L, M", outContent.toString());
    }

    @Test
    public void test226() {                           // сложные правила

        File s = new File(getClass().getResource("simple_brackets.txt").getFile());
        Main.main(new String[]{s.getAbsolutePath()});

        assertEquals("A, H, H1, H3, H5, H6, H7", outContent.toString());
    }
}




