// Generated from fr/inria/shapedesigner/model/prefix/parsing/Prefix.g4 by ANTLR 4.7.1
package fr.inria.shapedesigner.model.prefix.parsing;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PrefixParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface PrefixVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link PrefixParser#prefix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrefix(PrefixParser.PrefixContext ctx);
	/**
	 * Visit a parse tree produced by {@link PrefixParser#prefixDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrefixDecl(PrefixParser.PrefixDeclContext ctx);
}