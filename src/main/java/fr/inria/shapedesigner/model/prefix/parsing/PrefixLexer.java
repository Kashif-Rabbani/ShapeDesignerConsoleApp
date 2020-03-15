// Generated from fr/inria/shapedesigner/model/prefix/parsing/Prefix.g4 by ANTLR 4.7.1
package fr.inria.shapedesigner.model.prefix.parsing;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PrefixLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, KW_PREFIX=3, PASS=4, IRIREF=5, PNAME_NS=6, PNAME_LN=7;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "KW_PREFIX", "PASS", "IRIREF", "PNAME_NS", "PNAME_LN", 
		"UCHAR", "PN_CHARS_BASE", "PN_CHARS_U", "PN_CHARS", "PN_PREFIX", "PN_LOCAL", 
		"PLX", "PERCENT", "HEX", "PN_LOCAL_ESC", "A", "B", "C", "D", "E", "F", 
		"G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", 
		"U", "V", "W", "X", "Y", "Z"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'@'", "'.'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, "KW_PREFIX", "PASS", "IRIREF", "PNAME_NS", "PNAME_LN"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public PrefixLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Prefix.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\t\u00ff\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\6\5f\n\5\r\5\16\5"+
		"g\3\5\3\5\3\6\3\6\3\6\7\6o\n\6\f\6\16\6r\13\6\3\6\3\6\3\7\5\7w\n\7\3\7"+
		"\3\7\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\3\t\3\t\3\t\3\t\5\t\u0092\n\t\3\n\5\n\u0095\n\n\3\13\3\13\5"+
		"\13\u0099\n\13\3\f\3\f\5\f\u009d\n\f\3\r\3\r\3\r\7\r\u00a2\n\r\f\r\16"+
		"\r\u00a5\13\r\3\r\5\r\u00a8\n\r\3\16\3\16\3\16\5\16\u00ad\n\16\3\16\3"+
		"\16\3\16\7\16\u00b2\n\16\f\16\16\16\u00b5\13\16\3\16\3\16\3\16\5\16\u00ba"+
		"\n\16\5\16\u00bc\n\16\3\17\3\17\5\17\u00c0\n\17\3\20\3\20\3\20\3\20\3"+
		"\21\5\21\u00c7\n\21\3\22\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26"+
		"\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35"+
		"\3\35\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&"+
		"\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\2\2-\3\3\5\4\7\5\t\6\13\7\r\b\17"+
		"\t\21\2\23\2\25\2\27\2\31\2\33\2\35\2\37\2!\2#\2%\2\'\2)\2+\2-\2/\2\61"+
		"\2\63\2\65\2\67\29\2;\2=\2?\2A\2C\2E\2G\2I\2K\2M\2O\2Q\2S\2U\2W\2\3\2"+
		"\"\5\2\13\f\17\17\"\"\t\2\2\"$$>@^^``bb}\177\7\2//\62;\u00b9\u00b9\u0302"+
		"\u0371\u2041\u2042\4\2\60\60<<\5\2\62;CHch\t\2##%\61==??ABaa\u0080\u0080"+
		"\4\2CCcc\4\2DDdd\4\2EEee\4\2FFff\4\2GGgg\4\2HHhh\4\2IIii\4\2JJjj\4\2K"+
		"Kkk\4\2LLll\4\2MMmm\4\2NNnn\4\2OOoo\4\2PPpp\4\2QQqq\4\2RRrr\4\2SSss\4"+
		"\2TTtt\4\2UUuu\4\2VVvv\4\2WWww\4\2XXxx\4\2YYyy\4\2ZZzz\4\2[[{{\4\2\\\\"+
		"||\3\20\2C\2\\\2c\2|\2\u00c2\2\u00d8\2\u00da\2\u00f8\2\u00fa\2\u0301\2"+
		"\u0372\2\u037f\2\u0381\2\u2001\2\u200e\2\u200f\2\u2072\2\u2191\2\u2c02"+
		"\2\u2ff1\2\u3003\2\ud801\2\uf902\2\ufdd1\2\ufdf2\2\uffff\2\2\3\uffff\20"+
		"\u00ed\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2"+
		"\r\3\2\2\2\2\17\3\2\2\2\3Y\3\2\2\2\5[\3\2\2\2\7]\3\2\2\2\te\3\2\2\2\13"+
		"k\3\2\2\2\rv\3\2\2\2\17z\3\2\2\2\21\u0091\3\2\2\2\23\u0094\3\2\2\2\25"+
		"\u0098\3\2\2\2\27\u009c\3\2\2\2\31\u009e\3\2\2\2\33\u00ac\3\2\2\2\35\u00bf"+
		"\3\2\2\2\37\u00c1\3\2\2\2!\u00c6\3\2\2\2#\u00c8\3\2\2\2%\u00cb\3\2\2\2"+
		"\'\u00cd\3\2\2\2)\u00cf\3\2\2\2+\u00d1\3\2\2\2-\u00d3\3\2\2\2/\u00d5\3"+
		"\2\2\2\61\u00d7\3\2\2\2\63\u00d9\3\2\2\2\65\u00db\3\2\2\2\67\u00dd\3\2"+
		"\2\29\u00df\3\2\2\2;\u00e1\3\2\2\2=\u00e3\3\2\2\2?\u00e5\3\2\2\2A\u00e7"+
		"\3\2\2\2C\u00e9\3\2\2\2E\u00eb\3\2\2\2G\u00ed\3\2\2\2I\u00ef\3\2\2\2K"+
		"\u00f1\3\2\2\2M\u00f3\3\2\2\2O\u00f5\3\2\2\2Q\u00f7\3\2\2\2S\u00f9\3\2"+
		"\2\2U\u00fb\3\2\2\2W\u00fd\3\2\2\2YZ\7B\2\2Z\4\3\2\2\2[\\\7\60\2\2\\\6"+
		"\3\2\2\2]^\5C\"\2^_\5G$\2_`\5-\27\2`a\5/\30\2ab\5\65\33\2bc\5S*\2c\b\3"+
		"\2\2\2df\t\2\2\2ed\3\2\2\2fg\3\2\2\2ge\3\2\2\2gh\3\2\2\2hi\3\2\2\2ij\b"+
		"\5\2\2j\n\3\2\2\2kp\7>\2\2lo\n\3\2\2mo\5\21\t\2nl\3\2\2\2nm\3\2\2\2or"+
		"\3\2\2\2pn\3\2\2\2pq\3\2\2\2qs\3\2\2\2rp\3\2\2\2st\7@\2\2t\f\3\2\2\2u"+
		"w\5\31\r\2vu\3\2\2\2vw\3\2\2\2wx\3\2\2\2xy\7<\2\2y\16\3\2\2\2z{\5\r\7"+
		"\2{|\5\33\16\2|\20\3\2\2\2}~\7^\2\2~\177\7w\2\2\177\u0080\3\2\2\2\u0080"+
		"\u0081\5!\21\2\u0081\u0082\5!\21\2\u0082\u0083\5!\21\2\u0083\u0084\5!"+
		"\21\2\u0084\u0092\3\2\2\2\u0085\u0086\7^\2\2\u0086\u0087\7W\2\2\u0087"+
		"\u0088\3\2\2\2\u0088\u0089\5!\21\2\u0089\u008a\5!\21\2\u008a\u008b\5!"+
		"\21\2\u008b\u008c\5!\21\2\u008c\u008d\5!\21\2\u008d\u008e\5!\21\2\u008e"+
		"\u008f\5!\21\2\u008f\u0090\5!\21\2\u0090\u0092\3\2\2\2\u0091}\3\2\2\2"+
		"\u0091\u0085\3\2\2\2\u0092\22\3\2\2\2\u0093\u0095\t\"\2\2\u0094\u0093"+
		"\3\2\2\2\u0095\24\3\2\2\2\u0096\u0099\5\23\n\2\u0097\u0099\7a\2\2\u0098"+
		"\u0096\3\2\2\2\u0098\u0097\3\2\2\2\u0099\26\3\2\2\2\u009a\u009d\5\25\13"+
		"\2\u009b\u009d\t\4\2\2\u009c\u009a\3\2\2\2\u009c\u009b\3\2\2\2\u009d\30"+
		"\3\2\2\2\u009e\u00a7\5\23\n\2\u009f\u00a2\5\27\f\2\u00a0\u00a2\7\60\2"+
		"\2\u00a1\u009f\3\2\2\2\u00a1\u00a0\3\2\2\2\u00a2\u00a5\3\2\2\2\u00a3\u00a1"+
		"\3\2\2\2\u00a3\u00a4\3\2\2\2\u00a4\u00a6\3\2\2\2\u00a5\u00a3\3\2\2\2\u00a6"+
		"\u00a8\5\27\f\2\u00a7\u00a3\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\32\3\2\2"+
		"\2\u00a9\u00ad\5\25\13\2\u00aa\u00ad\4\62<\2\u00ab\u00ad\5\35\17\2\u00ac"+
		"\u00a9\3\2\2\2\u00ac\u00aa\3\2\2\2\u00ac\u00ab\3\2\2\2\u00ad\u00bb\3\2"+
		"\2\2\u00ae\u00b2\5\27\f\2\u00af\u00b2\t\5\2\2\u00b0\u00b2\5\35\17\2\u00b1"+
		"\u00ae\3\2\2\2\u00b1\u00af\3\2\2\2\u00b1\u00b0\3\2\2\2\u00b2\u00b5\3\2"+
		"\2\2\u00b3\u00b1\3\2\2\2\u00b3\u00b4\3\2\2\2\u00b4\u00b9\3\2\2\2\u00b5"+
		"\u00b3\3\2\2\2\u00b6\u00ba\5\27\f\2\u00b7\u00ba\7<\2\2\u00b8\u00ba\5\35"+
		"\17\2\u00b9\u00b6\3\2\2\2\u00b9\u00b7\3\2\2\2\u00b9\u00b8\3\2\2\2\u00ba"+
		"\u00bc\3\2\2\2\u00bb\u00b3\3\2\2\2\u00bb\u00bc\3\2\2\2\u00bc\34\3\2\2"+
		"\2\u00bd\u00c0\5\37\20\2\u00be\u00c0\5#\22\2\u00bf\u00bd\3\2\2\2\u00bf"+
		"\u00be\3\2\2\2\u00c0\36\3\2\2\2\u00c1\u00c2\7\'\2\2\u00c2\u00c3\5!\21"+
		"\2\u00c3\u00c4\5!\21\2\u00c4 \3\2\2\2\u00c5\u00c7\t\6\2\2\u00c6\u00c5"+
		"\3\2\2\2\u00c7\"\3\2\2\2\u00c8\u00c9\7^\2\2\u00c9\u00ca\t\7\2\2\u00ca"+
		"$\3\2\2\2\u00cb\u00cc\t\b\2\2\u00cc&\3\2\2\2\u00cd\u00ce\t\t\2\2\u00ce"+
		"(\3\2\2\2\u00cf\u00d0\t\n\2\2\u00d0*\3\2\2\2\u00d1\u00d2\t\13\2\2\u00d2"+
		",\3\2\2\2\u00d3\u00d4\t\f\2\2\u00d4.\3\2\2\2\u00d5\u00d6\t\r\2\2\u00d6"+
		"\60\3\2\2\2\u00d7\u00d8\t\16\2\2\u00d8\62\3\2\2\2\u00d9\u00da\t\17\2\2"+
		"\u00da\64\3\2\2\2\u00db\u00dc\t\20\2\2\u00dc\66\3\2\2\2\u00dd\u00de\t"+
		"\21\2\2\u00de8\3\2\2\2\u00df\u00e0\t\22\2\2\u00e0:\3\2\2\2\u00e1\u00e2"+
		"\t\23\2\2\u00e2<\3\2\2\2\u00e3\u00e4\t\24\2\2\u00e4>\3\2\2\2\u00e5\u00e6"+
		"\t\25\2\2\u00e6@\3\2\2\2\u00e7\u00e8\t\26\2\2\u00e8B\3\2\2\2\u00e9\u00ea"+
		"\t\27\2\2\u00eaD\3\2\2\2\u00eb\u00ec\t\30\2\2\u00ecF\3\2\2\2\u00ed\u00ee"+
		"\t\31\2\2\u00eeH\3\2\2\2\u00ef\u00f0\t\32\2\2\u00f0J\3\2\2\2\u00f1\u00f2"+
		"\t\33\2\2\u00f2L\3\2\2\2\u00f3\u00f4\t\34\2\2\u00f4N\3\2\2\2\u00f5\u00f6"+
		"\t\35\2\2\u00f6P\3\2\2\2\u00f7\u00f8\t\36\2\2\u00f8R\3\2\2\2\u00f9\u00fa"+
		"\t\37\2\2\u00faT\3\2\2\2\u00fb\u00fc\t \2\2\u00fcV\3\2\2\2\u00fd\u00fe"+
		"\t!\2\2\u00feX\3\2\2\2\25\2gnpv\u0091\u0094\u0098\u009c\u00a1\u00a3\u00a7"+
		"\u00ac\u00b1\u00b3\u00b9\u00bb\u00bf\u00c6\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}