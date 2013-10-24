/* The following code was generated by JFlex 1.4.3 on 10/24/13 6:18 PM */


package parser;

/**
   This is a lexical analyser generated by JFlex 1.4.3.
   We use it to tokenize input strings.

   The specification for the lexer can be found in the
   root folder of the project.
*/

@SuppressWarnings("unused")


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.3
 * on 10/24/13 6:18 PM from the specification file
 * <tt>lexer.flex</tt>
 */
class Lexer {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0, 0
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\12\0\1\0\25\0\1\7\1\42\1\44\1\43\3\0\1\42\5\0"+
    "\1\16\1\40\1\16\1\11\1\15\1\13\1\14\2\41\3\12\1\17"+
    "\1\40\4\0\1\42\1\0\1\5\1\33\1\34\1\30\1\23\1\1"+
    "\1\36\1\20\1\21\1\32\1\42\1\25\1\4\1\10\1\3\1\35"+
    "\1\42\1\2\1\22\1\6\1\26\1\37\1\27\1\24\1\31\1\42"+
    "\6\0\1\5\1\33\1\34\1\30\1\23\1\1\1\36\1\20\1\21"+
    "\1\32\1\42\1\25\1\4\1\10\1\3\1\35\1\42\1\2\1\22"+
    "\1\6\1\26\1\37\1\27\1\24\1\31\1\42\uff85\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\1\0\1\1\20\2\2\1\21\2\1\0\17\2\1\3"+
    "\1\0\1\2\1\4\2\0\2\2\1\4\1\2\2\4"+
    "\1\2\2\5\1\2\1\0\5\2\11\0\2\2\2\4"+
    "\6\2\2\4\1\6\1\2\20\0\2\2\1\4\2\2"+
    "\1\0\6\4\3\0\1\5\7\0\1\4\1\2\4\4"+
    "\1\5\3\2\24\0\1\2\1\0\1\2\1\4\7\2"+
    "\12\4\6\2\5\0\1\4\2\0\1\4\1\0\2\4"+
    "\2\0\2\4\1\0\2\4\1\5\1\0\1\2\4\0"+
    "\1\4\2\2\10\0\2\2\6\0\1\4\6\0\1\5"+
    "\1\2\1\0\1\4\1\2\3\0\2\2\6\0\1\2"+
    "\5\0";

  private static int [] zzUnpackAction() {
    int [] result = new int[268];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\45\0\112\0\157\0\224\0\271\0\336\0\u0103"+
    "\0\u0128\0\u014d\0\u0172\0\u0197\0\u01bc\0\u01e1\0\u0206\0\u022b"+
    "\0\u0250\0\u0275\0\u029a\0\u02bf\0\u02e4\0\u0309\0\u032e\0\u0353"+
    "\0\u0378\0\u039d\0\u03c2\0\u03e7\0\u040c\0\u0431\0\u0456\0\u047b"+
    "\0\u04a0\0\u04c5\0\u04ea\0\u050f\0\u0534\0\u0559\0\u057e\0\u05a3"+
    "\0\u05c8\0\u05ed\0\u0612\0\u0637\0\u065c\0\u0681\0\u06a6\0\u06cb"+
    "\0\u06f0\0\u0715\0\u073a\0\u075f\0\u0784\0\u029a\0\u07a9\0\u07ce"+
    "\0\u07f3\0\u0818\0\u083d\0\u0862\0\u0887\0\u08ac\0\u08d1\0\u08f6"+
    "\0\u091b\0\u0940\0\u05a3\0\157\0\u0965\0\u098a\0\u09af\0\u09d4"+
    "\0\u09f9\0\u0a1e\0\u0a43\0\u0a68\0\u0a8d\0\u0ab2\0\u0ad7\0\u0afc"+
    "\0\u0b21\0\u0b46\0\u0b6b\0\u0b90\0\u0bb5\0\u0bda\0\u0bff\0\u0c24"+
    "\0\u0c49\0\u0c6e\0\u0c93\0\u0cb8\0\u0cdd\0\u0d02\0\u0d27\0\u0d4c"+
    "\0\45\0\u0d71\0\u0d96\0\u0dbb\0\u0de0\0\u0e05\0\u0e2a\0\u0e4f"+
    "\0\u0e74\0\u0e99\0\u0ebe\0\u0ee3\0\u0f08\0\u0f2d\0\u0f52\0\u0f77"+
    "\0\u0f9c\0\u0fc1\0\u0fe6\0\u100b\0\157\0\u1030\0\u091b\0\u1055"+
    "\0\u107a\0\u109f\0\u10c4\0\u10e9\0\u110e\0\u1133\0\u1158\0\u117d"+
    "\0\u11a2\0\45\0\u11c7\0\u11ec\0\u1211\0\u1236\0\u125b\0\u1280"+
    "\0\u12a5\0\u12ca\0\u12ef\0\u1314\0\u1339\0\u135e\0\u1383\0\u13a8"+
    "\0\u13cd\0\u13f2\0\u1417\0\u143c\0\u1461\0\u1486\0\u14ab\0\u14d0"+
    "\0\u14f5\0\u151a\0\u153f\0\u1564\0\u1589\0\u15ae\0\u15d3\0\u15f8"+
    "\0\u161d\0\u1642\0\u1667\0\u168c\0\u16b1\0\u16d6\0\u16fb\0\u1720"+
    "\0\u1745\0\u176a\0\u178f\0\u17b4\0\u17d9\0\u17fe\0\u1823\0\u1848"+
    "\0\u186d\0\u1892\0\u18b7\0\u18dc\0\u1901\0\u1926\0\u194b\0\u1970"+
    "\0\u1995\0\u19ba\0\u19df\0\u1a04\0\u1a29\0\u1a4e\0\u1a73\0\u1a98"+
    "\0\u07f3\0\u1abd\0\u1ae2\0\u1b07\0\u1b2c\0\u1b51\0\u1b76\0\u1b9b"+
    "\0\u1bc0\0\u1be5\0\u1c0a\0\u1c2f\0\u1c54\0\u1c79\0\u1c9e\0\u1cc3"+
    "\0\u1ce8\0\u1d0d\0\u1d32\0\u1d57\0\u1d7c\0\u16b1\0\u1da1\0\u1dc6"+
    "\0\u1deb\0\u1e10\0\u1e35\0\u1e5a\0\u1e7f\0\u1ea4\0\u1ec9\0\u1eee"+
    "\0\u1f13\0\u1f38\0\u1f5d\0\u1f82\0\u1fa7\0\u1fcc\0\u1ff1\0\u2016"+
    "\0\u203b\0\u2060\0\u2085\0\u20aa\0\u20cf\0\u20f4\0\u2119\0\45"+
    "\0\u213e\0\u1c79\0\u2163\0\u2188\0\u21ad\0\u21d2\0\u168c\0\u08ac"+
    "\0\u21f7\0\u221c\0\u2241\0\u2266\0\u228b\0\u22b0\0\u22d5\0\u22fa"+
    "\0\u231f\0\u2344\0\u1b9b\0\u2369\0\u238e\0\u23b3\0\u23d8\0\u23fd"+
    "\0\u2422\0\u1c0a\0\u2447\0\u246c";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[268];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\2"+
    "\1\11\1\12\1\13\1\14\1\15\1\16\1\4\1\13"+
    "\1\17\1\4\1\20\2\4\1\21\1\4\1\22\11\4"+
    "\1\13\1\4\1\23\1\24\46\0\1\4\1\25\4\4"+
    "\1\0\33\4\3\0\6\4\1\0\33\4\3\0\6\4"+
    "\1\0\1\26\32\4\3\0\2\4\1\27\3\4\1\0"+
    "\33\4\3\0\5\4\1\30\1\0\33\4\3\0\2\4"+
    "\1\31\1\32\2\4\1\0\10\4\1\33\5\4\1\34"+
    "\14\4\3\0\6\4\1\0\13\4\1\35\17\4\3\0"+
    "\6\4\1\0\1\4\1\36\1\37\3\40\1\4\1\37"+
    "\20\4\1\41\1\40\1\4\3\0\1\42\1\4\1\43"+
    "\1\44\1\45\1\4\1\46\1\47\1\50\1\4\3\50"+
    "\1\51\3\4\1\52\5\4\1\53\1\4\1\54\2\4"+
    "\1\55\2\4\1\56\1\50\1\4\3\0\1\42\1\4"+
    "\1\43\1\44\1\45\1\4\1\46\1\47\1\40\1\57"+
    "\3\40\1\51\1\57\2\4\1\52\5\4\1\53\1\4"+
    "\1\54\2\4\1\55\2\4\1\56\1\60\1\4\3\0"+
    "\1\42\1\4\1\43\1\44\1\45\1\4\1\46\1\47"+
    "\1\60\1\4\2\50\1\60\1\51\3\4\1\52\5\4"+
    "\1\53\1\4\1\54\2\4\1\55\2\4\1\56\1\50"+
    "\1\4\3\0\1\42\1\4\1\43\1\44\1\45\1\4"+
    "\1\46\1\47\1\61\1\37\1\61\1\40\1\61\1\51"+
    "\1\37\2\4\1\52\5\4\1\53\1\4\1\54\2\4"+
    "\1\55\2\4\1\56\1\40\1\4\3\0\4\4\1\62"+
    "\1\4\1\0\33\4\3\0\4\4\1\63\1\4\1\0"+
    "\16\4\1\27\14\4\3\0\4\4\1\64\1\4\1\0"+
    "\33\4\3\0\6\4\1\0\13\4\1\65\17\4\3\0"+
    "\6\66\1\0\33\66\3\0\42\67\3\0\2\4\1\70"+
    "\3\4\1\0\11\4\1\71\21\4\3\0\6\4\1\72"+
    "\33\4\3\0\6\4\1\0\1\71\32\4\3\0\6\4"+
    "\1\73\33\4\3\0\3\4\1\74\2\4\1\0\20\4"+
    "\1\75\12\4\3\0\1\4\1\76\4\4\1\0\33\4"+
    "\3\0\6\4\1\0\11\4\1\77\4\4\1\100\14\4"+
    "\3\0\6\4\1\0\13\4\1\101\17\4\3\0\6\4"+
    "\1\0\14\4\1\102\16\4\3\0\6\4\1\0\1\4"+
    "\1\103\1\104\3\103\1\4\1\104\20\4\1\41\1\103"+
    "\1\4\3\0\1\42\1\4\1\43\1\44\1\105\1\4"+
    "\1\106\1\47\1\50\1\4\3\50\1\51\3\4\1\52"+
    "\5\4\1\53\1\4\1\54\5\4\1\41\1\50\1\4"+
    "\3\0\1\42\1\4\1\43\1\44\1\105\1\4\1\106"+
    "\1\47\1\103\1\104\3\103\1\51\1\104\2\4\1\52"+
    "\5\4\1\53\1\4\1\54\5\4\1\41\1\103\1\4"+
    "\3\0\6\4\1\0\1\4\1\50\1\4\3\50\23\4"+
    "\1\50\1\4\3\0\6\4\1\0\13\4\1\107\17\4"+
    "\3\0\6\4\1\0\24\4\1\110\6\4\3\0\4\4"+
    "\1\111\1\4\1\0\33\4\3\0\3\4\1\104\2\4"+
    "\1\0\16\4\1\112\6\4\1\113\5\4\3\0\1\114"+
    "\1\0\1\115\1\116\1\117\1\0\1\46\1\120\11\0"+
    "\1\121\5\0\1\122\1\0\1\123\2\0\1\124\10\0"+
    "\2\4\1\125\3\4\1\0\33\4\3\0\6\4\1\0"+
    "\1\4\5\104\1\4\1\104\21\4\1\104\1\4\3\0"+
    "\6\4\1\0\1\4\1\126\3\127\1\130\1\4\1\127"+
    "\21\4\1\127\1\4\3\0\6\4\1\0\13\4\1\131"+
    "\17\4\3\0\6\4\1\0\13\4\1\132\17\4\3\0"+
    "\4\4\1\133\1\4\1\0\16\4\1\134\14\4\3\0"+
    "\3\4\1\104\2\4\1\0\33\4\3\0\6\4\1\0"+
    "\1\4\1\135\1\4\3\135\23\4\1\135\1\4\3\0"+
    "\1\42\1\4\1\43\1\44\1\105\1\4\1\106\1\47"+
    "\5\4\1\51\3\4\1\52\5\4\1\53\1\4\1\54"+
    "\10\4\3\0\1\42\1\4\1\43\1\44\1\105\1\4"+
    "\1\106\1\47\5\104\1\51\1\104\2\4\1\52\5\4"+
    "\1\53\1\4\1\54\6\4\1\104\1\4\3\0\1\42"+
    "\1\4\1\43\1\44\1\45\1\4\1\46\1\47\1\103"+
    "\1\104\3\103\1\51\1\104\2\4\1\52\5\4\1\53"+
    "\1\4\1\54\2\4\1\55\2\4\1\56\1\103\1\4"+
    "\3\0\6\4\1\0\15\4\1\136\15\4\3\0\5\4"+
    "\1\137\1\0\33\4\3\0\6\4\1\0\12\4\1\102"+
    "\20\4\3\0\6\4\1\0\20\4\1\140\12\4\3\0"+
    "\42\67\1\0\1\141\1\0\3\4\1\142\2\4\1\0"+
    "\33\4\3\0\6\4\1\0\20\4\1\75\12\4\3\0"+
    "\1\143\2\0\1\144\1\0\1\145\1\72\1\146\1\147"+
    "\1\150\1\151\1\152\1\151\1\0\1\150\1\153\1\0"+
    "\1\154\2\0\1\155\1\0\1\156\11\0\1\150\12\0"+
    "\1\73\1\0\1\157\1\160\1\161\1\160\1\162\1\0"+
    "\1\160\21\0\1\160\4\0\2\4\1\163\3\4\1\0"+
    "\33\4\3\0\4\4\1\164\1\4\1\0\33\4\3\0"+
    "\6\4\1\0\17\4\1\165\13\4\3\0\6\4\1\0"+
    "\12\4\1\166\20\4\3\0\1\4\1\167\4\4\1\0"+
    "\33\4\3\0\6\4\1\0\12\4\1\71\20\4\3\0"+
    "\5\4\1\166\1\0\33\4\3\0\6\4\1\0\16\4"+
    "\1\112\6\4\1\113\5\4\3\0\1\114\1\0\1\115"+
    "\1\116\1\170\1\0\1\106\1\120\11\0\1\121\5\0"+
    "\1\122\1\0\1\123\13\0\6\4\1\0\23\4\1\171"+
    "\7\4\3\0\5\4\1\172\1\0\33\4\3\0\1\4"+
    "\1\173\4\4\1\0\21\4\1\174\11\4\3\0\6\4"+
    "\1\0\26\4\1\175\4\4\3\0\1\4\1\176\4\4"+
    "\1\0\33\4\25\0\1\177\55\0\1\200\15\0\1\201"+
    "\43\0\1\202\21\0\1\203\6\0\1\204\12\0\1\205"+
    "\64\0\1\206\44\0\1\207\26\0\1\210\20\0\1\211"+
    "\22\0\1\202\41\0\6\4\1\0\27\4\1\212\3\4"+
    "\3\0\6\4\1\0\2\4\4\127\1\4\1\127\21\4"+
    "\1\127\1\4\3\0\6\4\1\0\6\4\1\213\24\4"+
    "\3\0\6\4\1\0\1\4\1\127\1\4\1\127\1\4"+
    "\1\127\1\213\24\4\3\0\6\4\1\0\25\4\1\214"+
    "\5\4\3\0\6\4\1\0\24\4\1\212\6\4\3\0"+
    "\6\4\1\0\1\215\32\4\3\0\6\4\1\0\1\216"+
    "\14\4\1\217\15\4\3\0\6\4\1\0\1\4\5\220"+
    "\1\4\1\220\21\4\1\220\1\4\3\0\6\4\1\0"+
    "\15\4\1\221\15\4\3\0\6\4\1\0\16\4\1\222"+
    "\14\4\3\0\6\4\1\0\1\223\32\4\3\0\6\4"+
    "\1\224\33\4\4\0\1\225\45\0\1\226\44\0\1\227"+
    "\1\230\13\0\1\231\5\0\1\232\41\0\1\233\33\0"+
    "\4\150\1\0\1\150\21\0\1\150\4\0\1\114\1\0"+
    "\1\115\1\116\1\170\1\0\1\106\1\120\5\0\1\234"+
    "\3\0\1\121\5\0\1\122\1\0\1\123\13\0\1\114"+
    "\1\0\1\115\1\116\1\170\1\0\1\106\1\120\5\150"+
    "\1\234\1\150\2\0\1\121\5\0\1\122\1\0\1\123"+
    "\6\0\1\150\4\0\1\114\1\0\1\115\1\116\1\170"+
    "\1\0\1\106\1\120\1\150\3\0\1\150\1\234\3\0"+
    "\1\121\5\0\1\122\1\0\1\123\17\0\1\235\44\0"+
    "\1\236\20\0\1\226\23\0\1\237\62\0\1\240\32\0"+
    "\1\241\1\242\3\241\1\0\1\242\20\0\1\243\1\241"+
    "\10\0\1\124\1\0\1\244\1\0\1\245\1\0\3\245"+
    "\17\0\1\124\2\0\1\246\1\245\10\0\1\124\1\0"+
    "\1\244\1\0\1\241\1\0\3\241\17\0\1\124\2\0"+
    "\1\246\1\245\10\0\1\124\1\0\1\244\1\0\1\247"+
    "\1\242\1\247\1\241\1\247\1\0\1\242\15\0\1\124"+
    "\2\0\1\246\1\241\4\0\1\4\1\250\4\4\1\0"+
    "\33\4\3\0\6\4\1\0\21\4\1\165\11\4\3\0"+
    "\6\4\1\251\33\4\30\0\1\203\6\0\1\204\10\0"+
    "\1\4\1\252\4\4\1\253\1\4\2\254\1\255\1\254"+
    "\1\256\1\4\1\254\21\4\1\254\1\4\3\0\2\4"+
    "\1\257\3\4\1\253\1\4\2\254\1\255\1\254\1\256"+
    "\1\4\1\254\21\4\1\254\1\4\3\0\6\4\1\253"+
    "\1\4\2\254\1\255\1\254\1\256\1\4\1\254\14\4"+
    "\1\260\4\4\1\254\1\4\3\0\6\4\1\253\1\4"+
    "\2\254\1\255\1\254\1\256\1\4\1\254\21\4\1\254"+
    "\1\4\3\0\6\4\1\253\1\4\2\254\1\255\1\254"+
    "\1\256\1\4\1\254\6\4\1\261\12\4\1\254\1\4"+
    "\3\0\6\4\1\253\1\4\2\254\1\255\1\254\1\256"+
    "\1\4\1\254\1\4\1\262\17\4\1\254\1\4\35\0"+
    "\1\263\17\0\1\264\40\0\1\265\26\0\1\253\51\0"+
    "\1\266\10\0\1\267\101\0\1\270\42\0\1\271\43\0"+
    "\1\270\20\0\1\272\44\0\1\273\14\0\1\274\20\0"+
    "\6\4\1\253\1\4\2\254\1\255\1\254\1\256\1\4"+
    "\1\254\3\4\1\275\15\4\1\254\1\4\3\0\6\4"+
    "\1\0\1\4\2\254\1\255\1\254\1\256\1\4\1\254"+
    "\21\4\1\254\1\4\3\0\5\4\1\276\1\253\1\4"+
    "\2\254\1\255\1\254\1\256\1\4\1\254\21\4\1\254"+
    "\1\4\3\0\6\4\1\253\1\4\2\254\1\255\1\254"+
    "\1\256\1\4\1\254\6\4\1\277\12\4\1\254\1\4"+
    "\3\0\6\4\1\253\1\4\2\254\1\255\1\254\1\256"+
    "\1\4\1\254\3\4\1\174\15\4\1\254\1\4\3\0"+
    "\6\4\1\253\1\4\2\254\1\255\1\254\1\256\1\4"+
    "\1\254\11\4\1\174\7\4\1\254\1\4\3\0\4\4"+
    "\1\55\1\4\1\244\25\4\1\55\5\4\3\0\2\4"+
    "\1\300\3\4\1\0\33\4\3\0\1\4\1\301\4\4"+
    "\1\0\33\4\3\0\6\4\1\0\13\4\1\302\17\4"+
    "\3\0\1\143\2\0\1\144\1\0\1\145\1\224\1\146"+
    "\1\303\1\304\1\305\1\306\1\307\1\0\1\304\1\153"+
    "\1\0\1\154\2\0\1\155\1\0\1\156\11\0\1\304"+
    "\24\0\1\310\33\0\1\310\40\0\1\311\23\0\1\312"+
    "\16\0\1\313\63\0\1\314\4\0\1\315\41\0\1\316"+
    "\45\0\1\317\31\0\1\320\3\321\1\322\1\0\1\321"+
    "\21\0\1\321\30\0\1\323\25\0\1\324\60\0\1\317"+
    "\52\0\1\325\25\0\1\326\1\202\3\326\1\0\1\202"+
    "\20\0\1\243\1\326\14\0\1\245\1\0\3\245\22\0"+
    "\1\243\1\245\14\0\1\245\1\0\3\245\23\0\1\245"+
    "\10\0\1\124\1\0\1\244\25\0\1\124\20\0\5\202"+
    "\1\0\1\202\21\0\1\202\14\0\1\327\1\0\3\327"+
    "\23\0\1\327\10\0\1\124\1\0\1\244\1\0\1\326"+
    "\1\202\3\326\1\0\1\202\15\0\1\124\2\0\1\246"+
    "\1\326\4\0\1\4\1\330\4\4\1\0\33\4\3\0"+
    "\1\143\2\0\1\144\1\0\1\331\1\251\12\0\1\154"+
    "\4\0\1\156\16\0\6\4\1\0\16\4\1\277\14\4"+
    "\11\0\1\253\1\0\2\332\1\333\1\332\1\334\1\0"+
    "\1\332\21\0\1\332\4\0\6\4\1\0\1\4\5\165"+
    "\1\4\1\165\21\4\1\165\1\4\3\0\6\4\1\0"+
    "\1\4\1\335\4\165\1\4\1\165\21\4\1\165\1\4"+
    "\3\0\6\4\1\0\1\4\5\165\1\4\1\335\21\4"+
    "\1\165\1\4\3\0\6\4\1\0\23\4\1\336\7\4"+
    "\3\0\6\4\1\0\10\4\1\174\22\4\3\0\6\4"+
    "\1\0\12\4\1\337\20\4\3\0\6\4\1\0\15\4"+
    "\1\174\15\4\4\0\1\340\4\0\1\253\1\0\2\332"+
    "\1\333\1\332\1\334\1\0\1\332\21\0\1\332\6\0"+
    "\1\341\3\0\1\253\1\0\2\332\1\333\1\332\1\334"+
    "\1\0\1\332\21\0\1\332\12\0\1\253\1\0\2\332"+
    "\1\333\1\332\1\334\1\0\1\332\14\0\1\342\4\0"+
    "\1\332\12\0\1\253\1\0\2\332\1\333\1\332\1\334"+
    "\1\0\1\332\6\0\1\343\12\0\1\332\12\0\1\253"+
    "\1\0\2\332\1\333\1\332\1\334\1\0\1\332\1\0"+
    "\1\344\17\0\1\332\12\0\1\253\1\0\2\332\1\333"+
    "\1\332\1\334\1\0\1\332\3\0\1\345\15\0\1\332"+
    "\11\0\1\346\1\253\1\0\2\332\1\333\1\332\1\334"+
    "\1\0\1\332\21\0\1\332\12\0\1\253\1\0\2\332"+
    "\1\333\1\332\1\334\1\0\1\332\6\0\1\347\12\0"+
    "\1\332\12\0\1\253\1\0\2\332\1\333\1\332\1\334"+
    "\1\0\1\332\3\0\1\253\15\0\1\332\12\0\1\253"+
    "\1\0\2\332\1\333\1\332\1\334\1\0\1\332\11\0"+
    "\1\253\7\0\1\332\4\0\3\4\1\257\2\4\1\0"+
    "\33\4\3\0\6\4\1\0\13\4\1\275\17\4\3\0"+
    "\4\4\1\350\1\4\1\0\33\4\3\0\6\4\1\0"+
    "\17\4\1\351\13\4\3\0\6\4\1\0\12\4\1\301"+
    "\20\4\13\0\1\241\1\352\3\353\1\0\1\352\20\0"+
    "\1\243\1\353\4\0\1\114\1\0\1\115\1\116\1\117"+
    "\1\0\1\46\1\120\1\245\1\0\3\245\1\234\3\0"+
    "\1\121\5\0\1\122\1\0\1\123\2\0\1\124\2\0"+
    "\1\246\1\245\4\0\1\114\1\0\1\115\1\116\1\117"+
    "\1\0\1\46\1\120\1\353\1\150\3\353\1\234\1\150"+
    "\2\0\1\121\5\0\1\122\1\0\1\123\2\0\1\124"+
    "\2\0\1\246\1\354\4\0\1\114\1\0\1\115\1\116"+
    "\1\117\1\0\1\46\1\120\1\354\1\0\2\245\1\354"+
    "\1\234\3\0\1\121\5\0\1\122\1\0\1\123\2\0"+
    "\1\124\2\0\1\246\1\245\4\0\1\114\1\0\1\115"+
    "\1\116\1\117\1\0\1\46\1\120\1\355\1\352\1\355"+
    "\1\353\1\355\1\234\1\352\2\0\1\121\5\0\1\122"+
    "\1\0\1\123\2\0\1\124\2\0\1\246\1\353\33\0"+
    "\1\312\17\0\1\356\46\0\1\357\66\0\1\360\37\0"+
    "\1\361\24\0\1\362\64\0\1\310\30\0\1\361\50\0"+
    "\4\321\1\0\1\321\21\0\1\321\21\0\1\363\37\0"+
    "\1\321\1\0\1\321\1\0\1\321\1\363\53\0\1\364"+
    "\45\0\1\365\26\0\1\366\45\0\5\367\1\0\1\367"+
    "\21\0\1\367\4\0\2\4\1\370\3\4\1\0\33\4"+
    "\22\0\1\371\5\0\1\232\27\0\5\360\1\0\1\360"+
    "\21\0\1\360\14\0\1\372\4\360\1\0\1\360\21\0"+
    "\1\360\14\0\5\360\1\0\1\372\21\0\1\360\4\0"+
    "\6\4\1\0\1\4\5\254\1\4\1\254\21\4\1\254"+
    "\1\4\3\0\6\4\1\0\13\4\1\373\17\4\3\0"+
    "\5\4\1\174\1\0\33\4\30\0\1\347\51\0\1\374"+
    "\31\0\1\253\46\0\1\375\47\0\1\253\23\0\1\341"+
    "\63\0\1\345\26\0\1\376\40\0\1\4\1\377\4\4"+
    "\1\0\33\4\3\0\6\4\1\0\13\4\1\u0100\17\4"+
    "\3\0\1\114\1\0\1\115\1\116\1\170\1\0\1\106"+
    "\1\120\1\245\1\0\3\245\1\234\3\0\1\121\5\0"+
    "\1\122\1\0\1\123\5\0\1\243\1\245\4\0\1\114"+
    "\1\0\1\115\1\116\1\170\1\0\1\106\1\120\1\326"+
    "\1\202\3\326\1\234\1\202\2\0\1\121\5\0\1\122"+
    "\1\0\1\123\5\0\1\243\1\326\4\0\1\114\1\0"+
    "\1\115\1\116\1\170\1\0\1\106\1\120\5\202\1\234"+
    "\1\202\2\0\1\121\5\0\1\122\1\0\1\123\6\0"+
    "\1\202\4\0\1\114\1\0\1\115\1\116\1\117\1\0"+
    "\1\46\1\120\1\326\1\202\3\326\1\234\1\202\2\0"+
    "\1\121\5\0\1\122\1\0\1\123\2\0\1\124\2\0"+
    "\1\246\1\326\5\0\1\u0101\73\0\1\360\22\0\1\251"+
    "\46\0\2\332\1\333\1\332\1\334\1\0\1\332\21\0"+
    "\1\332\6\0\1\u0102\43\0\1\u0103\65\0\1\u0104\47\0"+
    "\1\315\27\0\5\332\1\0\1\332\21\0\1\332\4\0"+
    "\1\4\1\174\4\4\1\0\33\4\25\0\1\u0105\27\0"+
    "\1\253\40\0\1\u0106\43\0\6\4\1\0\21\4\1\174"+
    "\11\4\3\0\6\4\1\0\13\4\1\u0107\17\4\4\0"+
    "\1\u0108\71\0\1\u0109\37\0\1\u0103\24\0\1\253\73\0"+
    "\1\253\14\0\6\4\1\0\1\165\32\4\5\0\1\u010a"+
    "\64\0\1\u010b\44\0\1\u010c\31\0\1\360\34\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[9361];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\1\0\1\11\43\1\1\0\20\1\1\0\2\1\2\0"+
    "\12\1\1\0\5\1\11\0\14\1\1\11\1\1\20\0"+
    "\5\1\1\0\6\1\3\0\1\11\7\0\12\1\24\0"+
    "\1\1\1\0\31\1\5\0\1\1\2\0\1\1\1\0"+
    "\2\1\2\0\2\1\1\0\3\1\1\0\1\1\4\0"+
    "\3\1\10\0\2\1\6\0\1\11\6\0\2\1\1\0"+
    "\2\1\3\0\2\1\6\0\1\1\5\0";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[268];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;

  /* user code: */
  String name;


  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  Lexer(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  Lexer(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 156) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzCurrentPos*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = zzReader.read(zzBuffer, zzEndRead,
                                            zzBuffer.length-zzEndRead);

    if (numRead > 0) {
      zzEndRead+= numRead;
      return false;
    }
    // unlikely but not impossible: read 0 characters, but not at end of stream    
    if (numRead == 0) {
      int c = zzReader.read();
      if (c == -1) {
        return true;
      } else {
        zzBuffer[zzEndRead++] = (char) c;
        return false;
      }     
    }

	// numRead < 0
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEOFDone = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public Token nextToken() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL)
            zzInput = zzBufferL[zzCurrentPosL++];
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = zzBufferL[zzCurrentPosL++];
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 2: 
          { return new WordToken(yytext());
          }
        case 7: break;
        case 3: 
          { return new TagToken(yytext());
          }
        case 8: break;
        case 5: 
          { String contents = yytext();
	String[] split = contents.split("\\s+", 2);

	// Handles the optional qualifier
	if (split.length == 2 && (split[0].equalsIgnoreCase("at") || split[0].equalsIgnoreCase("from"))) {
		return new TimeToken(split[1].trim());
	}
	else {
		return new TimeToken(yytext());
	}
          }
        case 9: break;
        case 6: 
          { String contents = yytext();
    return new WordToken(contents.substring(1, contents.length()-1));
          }
        case 10: break;
        case 4: 
          { String contents = yytext();
	String[] split = contents.split("\\s+", 2);

	// Handles the optional qualifier
	if (split.length == 2 && (split[0].equalsIgnoreCase("on") || split[0].equalsIgnoreCase("from"))) {
		return new DateToken(split[1].trim());
	}
	else {
		return new DateToken(yytext());
	}
          }
        case 11: break;
        case 1: 
          { 
          }
        case 12: break;
        default: 
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            return null;
          } 
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
