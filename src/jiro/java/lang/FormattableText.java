package jiro.java.lang;

import static jiro.java.lang.UtilsString.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicInteger;

public class FormattableText {

  private final List<List<String>> textList;
  private static final String SEP = System.lineSeparator();

  // Constructor

  public FormattableText(List<List<String>> list) {//{{{
    textList = list;
  }//}}}

  public static FormattableText newInstanceFrom(File file) throws IOException {//{{{
    Path path = file.toPath();
    BufferedReader br = Files.newBufferedReader(path, Charset.forName("UTF-8"));

    List<String> list = br.lines()
      .filter(s -> !s.startsWith("##"))
      .collect(Collectors.toList());

    br.close();
    List<List<String>> listList = splitToParagraphFrom(list);
    return new FormattableText(listList);
  }//}}}

  public FormattableText addActorName() {//{{{
    List<List<String>> newListList = new ArrayList<>();

    String name = "";
    for (List<String> list : textList) {
      List<String> newList = new ArrayList<>();

      String top = list.get(0);
      if (top.startsWith("#")) {
        name = top.replaceAll("^# *", "");
      } else {
        newList.add("# " + name);
      }

      newList.addAll(list);
      newListList.add(newList);
    }

    return new FormattableText(newListList);
  }//}}}

  public FormattableText splitToParagraph() {//{{{
    List<List<String>> newListList = new ArrayList<>();

    textList.stream().forEach(l -> {
      if (l.size() <= 4) {
        newListList.add(l);
      } else {
        List<String> newList = new ArrayList<>();
        for (int i=1; i<=l.size(); i++) {
          newList.add(l.get(i-1));
          if (i % 4 == 0) {
            newListList.add(newList);
            newList = new ArrayList<>();
          }
        }
        newListList.add(newList);
      }
    });

    return new FormattableText(newListList);
  }//}}}

  public FormattableText replaceActorName() {//{{{
    List<List<String>> newListList = new ArrayList<>();

    for (List<String> l : textList) {
      List<String> newList = new ArrayList<>();
      String name = "";

      for (String line : l) {
        if (line.startsWith("#")) {
          name = line.replaceAll("^# *", "");
        }

        String newLine = line.replaceAll("@name", name);
        newList.add(newLine);
      }

      newListList.add(newList);
    }

    return new FormattableText(newListList);
  }//}}}

  public FormattableText joining() {//{{{
    List<List<String>> newList = textList.stream()
      .map(this::createJoinedListWith)
      .collect(Collectors.toList());
    return new FormattableText(newList);
  }//}}}

  // 一時変数
  private final int returnSize       = 27 * 2;
  private final boolean indentOption = true;
  private final Brackets brackets    = Brackets.TYPE1;
  private final String indent        = "  ";

  public FormattableText formatPutBrackets() {//{{{
    List<List<String>> formedList = textList.stream()
      .map(this::createWrappedListWith)
      .collect(Collectors.toList());

    return new FormattableText(formedList);
  }//}}}

  public FormattableText formatCarriageReturn() {//{{{
    List<List<String>> formedList = textList.stream()
      .map(list -> {
        List<String> newList = new ArrayList<>();

        list.stream()
          .filter(s -> s.startsWith("#"))
          .findFirst()
          .ifPresent(s -> {
            newList.add(s);
          });

        AtomicInteger atom = new AtomicInteger(0);
        list.stream()
          .filter(s -> !s.startsWith("#"))
          .forEach(s -> {
            if (atom.getAndIncrement() != 0 && indentOption) {
              s = indent + s;
            }
            List<String> crl = createCarriageReturnedListWith(s);
            newList.addAll(crl);
          });

        return newList;
      })
    .collect(Collectors.toList());

    return new FormattableText(formedList);
  }//}}}

  public void show() {//{{{
    AtomicInteger atom = new AtomicInteger(0);
    textList.stream().forEach(l -> {
      atom.getAndIncrement();

      l.stream().forEach(s -> {
        int paragraphNumber = atom.get();
        System.out.println(String.format("paragraph %03d : %s", paragraphNumber, s));
      });
    });
  }//}}}

  // private methods

  private static List<List<String>> splitToParagraphFrom(List<String> list) {//{{{
    List<List<String>> paragraphList = new ArrayList<>();
    List<String> paragraph = new ArrayList<>();

    for (String line : list) {
      if (line.length() <= 0) {
        if (0 < paragraph.size())
          paragraphList.add(paragraph);
        paragraph = new ArrayList<>();
        continue;
      }
      paragraph.add(line);
    }

    return paragraphList;
  }//}}}

  private List<String> createCarriageReturnedListWith(String text) {//{{{
    List<String> newList = new ArrayList<>();

    List<String> wordList = splitToWord(text);
    int count = 0;
    StringBuilder sb = new StringBuilder();

    for (String word : wordList) {
      int length = stringLength(word);
      count += length;

      if (returnSize < count) {
        newList.add(sb.toString());
        sb.setLength(0);
        count = 0;

        if (word.startsWith(" ") || word.startsWith("　")) {
          word = (new StringBuilder(word)).deleteCharAt(0).toString();
          length = stringLength(word);
        }

        if (indentOption) {
          sb.append(indent);
          count += stringLength(indent);
        }

        sb.append(word);
        count += length;
        continue;
      }

      sb.append(word);
    }

    newList.add(sb.toString());
    return newList;
  }//}}}

  private List<String> createJoinedListWith(List<String> list) {//{{{
    List<String> nl = new ArrayList<>();
    list.stream()
      .filter(s -> s.startsWith("#"))
      .findFirst()
      .ifPresent(actor -> {
        nl.add(actor);
      });

    String text = list.stream()
      .filter(s -> !s.startsWith("#"))
      .collect(Collectors.joining());
    nl.add(text);

    return nl;
  }//}}}

  private List<String> createWrappedListWith(List<String> list) {//{{{
    List<String> newList = new ArrayList<>();
    list.stream()
      .filter(s -> s.startsWith("#"))
      .findFirst()
      .ifPresent(actor -> {
        newList.add(actor);
      });

    List<String> filteredList = list.stream()
      .filter(s -> !s.startsWith("#"))
      .collect(Collectors.toList());

    int i = 0;
    int listSize = filteredList.size();

    for (String str : filteredList) {
      if (i == 0) {
        str = brackets.START + str;
      }

      if (listSize - 1 <= i) {
        //String end = brackets.END;
        //List<String> tmpList = createCarriageReturnedListWith(str + end);
        //newList.addAll(tmpList);
        //continue;
        str += brackets.END;
      }

      newList.add(str);
      i++;
    }

    return newList;
  }//}}}

  @Override
  public String toString() {//{{{
    AtomicInteger atom = new AtomicInteger(0);
    return textList.stream()
      .map(l -> {
        int paragraphNumber = atom.incrementAndGet();
        return l.stream()
          .map(s -> String.format("paragraph %03d : %s", paragraphNumber, s + SEP))
          .collect(Collectors.joining());
      })
    .collect(Collectors.joining());
  }//}}}

}
