package jiro.java.lang;

import static jiro.java.lang.UtilsChar.*;
import static jiro.java.lang.UtilsString.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * テキストフォーマットメソッドを持つ不変ラッパーStringクラス。
 */
public class FormattableString {

  // 単語単位で区切られた整形対象の文字列リスト
  private final List<String> wordList;

  // 改行
  private final boolean returnOption;
  private final int returnSize;

  // インデント
  private final boolean indentOption;
  private final int indentSize;
  private final String indent;

  // 括弧で囲む
  private final boolean bracketsOption;
  private final Brackets bracketsType;

  // アクター名を表示
  private final boolean actorNameOption;
  private final String actorName;
  private final ActorNameType actorNameType;

  private static final String SEP = System.lineSeparator();

  // ************************************************************
  // Builderクラス
  // ************************************************************

  /**
   * FormattableStringインスタンスを生成するためのビルダークラス。
   */
  public static class Builder {//{{{

    private final List<String> wordList;

    private boolean returnOption = false;
    private int returnSize = 27 * 2;

    private boolean indentOption = false;
    private int indentSize = 0;

    private boolean bracketsOption = false;
    private Brackets bracketsType;

    private boolean actorNameOption = false;
    private String actorName = null;
    private ActorNameType actorNameType;

    /**
     * 整形対象の文字列を登録するコンストラクタ。
     * @param str 登録文字列
     **/
    public Builder(String str) { wordList = splitToWord(str); }

    /**
     * 整形対象の文字列を登録するコンストラクタ。
     * @param sb 登録文字列ビルダー
     **/
    public Builder(StringBuilder sb)  { this(sb.toString()); }

    /**
     * 整形対象の文字列のリストを登録するコンストラクタ。
     * @param list 登録文字列リスト
     **/
    public Builder(List<String> list) { wordList = list; }

    /**
     * 文字列のみ更新したインスタンスを生成するコンストラクタ。
     * @param fs 成形可能文字列のインスタンス
     * @param sb 更新する文字列
     **/
    public Builder(FormattableString fs, StringBuilder sb) {//{{{
      this(fs, sb.toString());
    }//}}}

    /**
     * 文字列のみ更新したインスタンスを生成するコンストラクタ。
     * @param fs 成形可能文字列のインスタンス
     * @param str 更新する文字列
     **/
    public Builder(FormattableString fs, String str) {//{{{

      wordList        = splitToWord(str);
      returnOption    = fs.returnOption;
      returnSize      = fs.returnSize;
      indentOption    = fs.indentOption;
      indentSize      = fs.indentSize;
      bracketsOption  = fs.bracketsOption;
      bracketsType    = fs.bracketsType;
      actorNameOption = fs.actorNameOption;
      actorName       = fs.actorName;
      actorNameType   = fs.actorNameType;

    }//}}}

    /**
     * 文字列を改行するオプションをセットする。
     * @param returnOption 改行するか否か (default: {@code false})
     * @return Builderインスタンス
     */
    public Builder returnOption(boolean returnOption) {//{{{
      this.returnOption = returnOption;
      return this;
    }//}}}

    /**
     * 文字列を改行する上限をセットする。<br>
     * このオプションは{@link Builder#returnOption(boolean) returnOption}がセッ
     * トされていないと機能しない。
     * @param returnSize 改行する文字上限 (default: 54)
     * @return Builderインスタンス
     */
    public Builder returnSize(int returnSize) {//{{{
      this.returnSize = returnSize;
      return this;
    }//}}}

    /**
     * 文字列をインデントするオプションをセットする。
     * @param indentOption インデントするか否か (default: {@code false})
     * @return Builderインスタンス
     */
    public Builder indentOption(boolean indentOption) {//{{{
      this.indentOption = indentOption;
      return this;
    }//}}}

    /**
     * 文字列をインデントする幅をセットする。<br>
     * このオプションは{@link Builder#indentOption(boolean) indentOption}がセッ
     * トされていないと機能しない。
     * @param indentSize インデントする文字幅 (default: 0)
     * @return Builderインスタンス
     */
    public Builder indentSize(int indentSize) {//{{{
      this.indentSize = indentSize;
      return this;
    }//}}}

    /**
     * 文字列の先頭と末尾に括弧を追加するオプションをセットする。
     * @param bracketsOption 括弧を追加するか否か (default: {@code false})
     * @return Builderインスタンス
     */
    public Builder bracketsOption(boolean bracketsOption) {//{{{
      this.bracketsOption = bracketsOption;
      return this;
    }//}}}

    /**
     * 文字列のインデント書式をセットする。{@link Brackets}<br>
     * このオプションは{@link Builder#bracketsOption(boolean) bracketsOption}が
     * セットされていないと機能しない。
     * @param bracketsType インデント書式 (default: {@code null})
     * @return Builderインスタンス
     */
    public Builder bracketsType(Brackets bracketsType) {//{{{
      this.bracketsType = bracketsType;
      return this;
    }//}}}

    /**
     * 文字列の特定の場所にアクター名を追加するオプションをセットする。
     * @param actorNameOption アクター名を追加するか否か
     * (default: {@code false})
     * @return Builderインスタンス
     */
    public Builder actorNameOption(boolean actorNameOption) {//{{{
      this.actorNameOption = actorNameOption;
      return this;
    }//}}}

    /**
     * アクター名をセットする。<br>
     * このオプションは{@link Builder#actorNameOption(boolean) actorNameOption}
     * がセットされていないと機能しない。<br>
     * もしactorNameOptionをtrueにした状態で、actorNameがnullだった場合(セットし
     * なかった場合も含む)、例外を返す。
     * @param actorName 追加するアクター名 (default: {@code null})
     * @return Builderインスタンス
     */
    public Builder actorName(String actorName) {//{{{
      this.actorName = actorName;
      return this;
    }//}}}

    /**
     * アクター名追加書式をセットする。{@link ActorNameType}<br>
     * このオプションは{@link Builder#actorNameOption(boolean) actorNameOption}
     * がセットされていないと機能しない。
     * @param type アクター名書式 (default: {@code null})
     * @return Builderインスタンス
     */
    public Builder actorNameType(ActorNameType type) {//{{{
      this.actorNameType = type;
      return this;
    }//}}}

    /**
     * FormattableStringインスタンスを生成する。
     * @return FormattableStringインスタンス
     */
    public FormattableString build() {//{{{

      if (actorNameOption && actorName == null)
        throw new NullPointerException(
            String.format(
              "actorNameOptionを有効にしている時はactorNameを設定しなければなりません。"
              + "- actorNameOption = %s, actorName = %s, ActorNameType = %s"
              , actorNameOption, actorName, actorNameType)
            );

      if (actorNameOption && actorNameType == null)
        throw new NullPointerException(
            String.format(
              "actorNameOptionを有効にしている時はactorNameTypeを設定しなければなりません。"
              + "- actorNameOption = %s, actorName = %s, ActorNameType = %s"
              , actorNameOption, actorName, actorNameType)
            );

      return new FormattableString(this);
    }//}}}

  }//}}}

  // ************************************************************
  // コンストラクタ
  // ************************************************************

  private FormattableString(Builder builder) {//{{{

    this.wordList        = builder.wordList;
    this.returnOption    = builder.returnOption;
    this.returnSize      = builder.returnSize;
    this.indentOption    = builder.indentOption;
    this.indentSize      = builder.indentSize;
    this.bracketsOption  = builder.bracketsOption;
    this.bracketsType    = builder.bracketsType;
    this.actorNameOption = builder.actorNameOption;
    this.actorName       = builder.actorName;
    this.actorNameType   = builder.actorNameType;

    StringBuilder sb = new StringBuilder(indentSize);
    for (int i=0; i<indentSize; i++) {
      sb.append(" ");
    }
    this.indent = sb.toString();

  }//}}}

  // ************************************************************
  // publicメソッド
  // ************************************************************

  /**
   * <p>
   * 内部に保持するテキストを整形した新しい自身のインスタンスを生成する。
   * </p><p>
   * 他のformatメソッドの一連の処理をまとめたメソッドであり、メソッドの呼び出し
   * の順序が重要であるため、基本的にはこのメソッドを利用するのが好ましい。
   * </p><p>
   * テキストの整形書式はこのクラスのインスタンス生成時のオプションで指定する。
   * </p>
   * {@link Builder}
   * @return 整形した文字列を保持する新しいインスタンス
   */
  public FormattableString format() {//{{{
    return formatCarriageReturn().formatActorName();
  }//}}}

  /**
   * <p>
   * 内部に保持するテキストを整形した新しい自身のインスタンスを生成する。
   * </p><p>
   * 文字列の折り返し、インデントの追加、先頭と末尾への括弧の追加を行う。
   * </p><p>
   * テキストの整形書式はこのクラスのインスタンス生成時のオプションで指定する。
   * </p>
   * {@link Builder}
   * @return 整形した文字列を保持する新しいインスタンス
   */
  public FormattableString formatCarriageReturn() {//{{{

    StringBuilder sb = new StringBuilder();
    if (returnOption) {

      putStartBrackets();

      int count = 0;
      for (String word : wordList) {

        int length = stringLength(word);
        count += length;

        if (returnSize < count) {

          sb.append(SEP);
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

      putEndBrackets(sb);

      return new FormattableString.Builder(this, sb).build();

    } else {

      return new FormattableString.Builder(this, toString()).build();

    }

  }//}}}

  /**
   * <p>
   * 内部に保持するテキストを整形した新しい自身のインスタンスを生成する。
   * </p><p>
   * 先頭、あるいは４行区切りごとにアクター名を追加する。
   * </p><p>
   * テキストの整形書式はこのクラスのインスタンス生成時のオプションで指定する。
   * </p>
   * {@link Builder}
   * @return 整形した文字列を保持する新しいインスタンス
   */
  public FormattableString formatActorName() {//{{{

    if (actorNameOption) {

      String text = toString();
      String[] array = text.split(SEP);
      int lineCount = 0;
      int actorNameCount = 0;

      StringBuilder sb = new StringBuilder(text.toCharArray().length);
      for (int i=0; i<array.length; i++) {

        if (i % 3 == 0) {
          actorNameCount = insertActorName(sb, actorNameCount);
        }

        String line = array[i];
        sb.append(line);

        if (i != array.length - 1)
          sb.append(SEP);

      }

      return new FormattableString.Builder(this, sb).build();

    } else {

      return new FormattableString.Builder(this, toString()).build();

    }

  }//}}}

  /**
   * <p>
   * ４行単位で区切った文字列のリストを生成して返却する。
   * </p>
   * @return 4行で区切られた文字列のリスト
   */
  public List<String> toList() {//{{{

    String str = toString();
    int count = 0;

    String[] array = str.split(SEP);
    int arraySize = array.length;
    StringBuilder sb = new StringBuilder();
    List<String> list = new ArrayList<>(arraySize / 4);
    for (String line : array) {

      if (2 < count) {
        sb.append(line);
        list.add(sb.toString());
        sb.setLength(0);
        count = 0;
        continue;
      }

      sb.append(line);
      sb.append(SEP);
      count++;

    }

    if (0 < sb.length())
      list.add(sb.toString());

    return list;

  }//}}}

  // ************************************************************
  // privateメソッド
  // ************************************************************

  private int insertActorName(StringBuilder sb, int count) {//{{{

    if (actorNameType == ActorNameType.TOP_ONLY) {
      if (0 < count) return ++count;
    }

    sb.append(actorName);
    sb.append(SEP);

    return ++count;
  }//}}}

  private void putStartBrackets() {//{{{

    if (indentOption) {
      if (bracketsOption) {

        StringBuilder indentSb = new StringBuilder(indent);
        int len = stringLength(bracketsType.START);
        indentSb.delete(0, len);
        indentSb.insert(0, bracketsType.START);
        String newIndent = indentSb.toString();
        wordList.set(0, newIndent + wordList.get(0));

      } else {
        wordList.set(0, indent + wordList.get(0));
      }
    }

  }//}}}

  private void putEndBrackets(StringBuilder sb) {//{{{

    if (bracketsOption) {

      String str      = sb.toString();
      String[] array  = str.split(SEP);
      String lastLine = array[array.length-1];

      int len    =       stringLength(lastLine);
      int newLen = len + stringLength(bracketsType.END);

      if (returnSize < newLen) {
        sb.append(SEP);
      }

      sb.append(bracketsType.END);

    }

  }//}}}

  @Override
  public String toString() {//{{{
    return wordList.stream().collect(Collectors.joining());
  }//}}}

}
