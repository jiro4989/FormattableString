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

    //show(new File("./input/test1.txt"));
    //show(new File("./input/test2.txt"));
    //show(new File("./input/test3.csv"));

    try {
      FormattableText ft = new FormattableText.Builder(new File("./input/test3.csv"))
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

  // test methods

  private static void show(File file) {//{{{

    UtilsString.readTextFrom(file).ifPresent(text -> {

      FormattableString fs = new FormattableString.Builder(text)
        .returnOption(true)
        .returnSize(RETURN_SIZE)
        .indentOption(true)
        .indentSize(INDENT_SIZE)
        .bracketsOption(true)
        .bracketsType(BRACKETS)
        .actorNameOption(true)
        .actorName(ACTOR)
        .actorNameType(ActorNameType.ALL_WINDOW)
        .build();
      showText(fs);

    });

  }//}}}

  private static void showText(FormattableString fs) {//{{{

    showLine();
    System.out.println("変換前のテキスト");
    showLine();

    String newString = fs
      .toString();

    System.out.println(newString);
    System.out.println("");

    showLine();
    System.out.println("変換後のテキスト");
    showLine();

    newString = fs
      .format()
      .toString();

    System.out.println(newString);
    System.out.println("");

  }//}}}

  private static void showLine() {//{{{

    StringBuilder sb = new StringBuilder();
    for (int i=0; i<RETURN_SIZE; i++) {
      sb.append('*');
    }
    System.out.println(sb.toString());

  }//}}}

  private static void showList(List<List<String>> list) {
    AtomicInteger atom = new AtomicInteger(0);
    list.stream().forEach(l -> {
      atom.getAndIncrement();
      l.stream().forEach(s -> {
        System.out.println("line" + atom.get() + " : " + s);
      });
    });
  }

}
