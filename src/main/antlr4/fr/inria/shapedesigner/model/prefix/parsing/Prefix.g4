grammar Prefix;

prefix		    : prefixDecl * ;

prefixDecl		: KW_PREFIX PNAME_NS IRIREF 
                | '@' KW_PREFIX PNAME_NS IRIREF '.';


// Keywords
KW_PREFIX       	: P R E F I X ;

// terminals
PASS				  : [ \t\r\n]+ -> skip;
IRIREF                : '<' (~[\u0000-\u0020=<>"{}|^`\\] | UCHAR)* '>' ; /* #x00=NULL #01-#x1F=control codes #x20=space */
PNAME_NS			  : PN_PREFIX? ':' ;
PNAME_LN			  : PNAME_NS PN_LOCAL ;


fragment UCHAR                 : '\\u' HEX HEX HEX HEX | '\\U' HEX HEX HEX HEX HEX HEX HEX HEX ;
fragment PN_CHARS_BASE 		   : [A-Z] | [a-z] | [\u00C0-\u00D6] | [\u00D8-\u00F6] | [\u00F8-\u02FF] | [\u0370-\u037D]
					   		   | [\u037F-\u1FFF] | [\u200C-\u200D] | [\u2070-\u218F] | [\u2C00-\u2FEF] | [\u3001-\uD7FF]
					           | [\uF900-\uFDCF] | [\uFDF0-\uFFFD] | [\u{10000}-\u{EFFFD}]
					   		   ;
fragment PN_CHARS_U            : PN_CHARS_BASE | '_' ;
fragment PN_CHARS              : PN_CHARS_U | '-' | [0-9] | [\u00B7] | [\u0300-\u036F] | [\u203F-\u2040] ;
fragment PN_PREFIX             : PN_CHARS_BASE ((PN_CHARS | '.')* PN_CHARS)? ;
fragment PN_LOCAL              : (PN_CHARS_U | ':' | [0-9] | PLX) ((PN_CHARS | '.' | ':' | PLX)* (PN_CHARS | ':' | PLX))? ;
fragment PLX                   : PERCENT | PN_LOCAL_ESC ;
fragment PERCENT               : '%' HEX HEX ;
fragment HEX                   : [0-9] | [A-F] | [a-f] ;
fragment PN_LOCAL_ESC          : '\\' ('_' | '~' | '.' | '-' | '!' | '$' | '&' | '\'' | '(' | ')' | '*' | '+' | ','
					  		   | ';' | '=' | '/' | '?' | '#' | '@' | '%') ;


fragment A:('a'|'A');
fragment B:('b'|'B');
fragment C:('c'|'C');
fragment D:('d'|'D');
fragment E:('e'|'E');
fragment F:('f'|'F');
fragment G:('g'|'G');
fragment H:('h'|'H');
fragment I:('i'|'I');
fragment J:('j'|'J');
fragment K:('k'|'K');
fragment L:('l'|'L');
fragment M:('m'|'M');
fragment N:('n'|'N');
fragment O:('o'|'O');
fragment P:('p'|'P');
fragment Q:('q'|'Q');
fragment R:('r'|'R');
fragment S:('s'|'S');
fragment T:('t'|'T');
fragment U:('u'|'U');
fragment V:('v'|'V');
fragment W:('w'|'W');
fragment X:('x'|'X');
fragment Y:('y'|'Y');
fragment Z:('z'|'Z');
