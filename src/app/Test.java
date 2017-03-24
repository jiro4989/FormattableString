package app;

import jiro.java.lang.*;

import static jiro.java.lang.UtilsString.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Test {

  // 全角27文字で折り返し
  private static final int RETURN_SIZE   = 27 * 2;
  private static final int INDENT_SIZE   = 2;
  private static final Brackets BRACKETS = Brackets.TYPE1;
  private static final String ACTOR      = "【ハロルド】";

  // test code

  public static void main(String... args) {//{{{
    //show(new File("./input/test2.csv"));
    show(new File("./input/test3.txt"));
  }//}}}

  // test methods

  private static void show(File file) {//{{{
    try {
      FormattableText ft = new FormattableText.Builder(file)
        .actorNameOption(true)
        .returnOption(true)
        .returnSize(RETURN_SIZE)
        .indentOption(true)
        .indentSize(INDENT_SIZE)
        .bracketsOption(true)
        .brackets(BRACKETS)
        .joiningOption(false)
        .build();

      String text = ft.format().toString();
      System.out.println(text);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }//}}}

}
