package theory.characters;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.google.common.collect.ImmutableList;

public class StdCharPred {

	public final static CharPred TRUE = new CharPred(CharPred.MIN_CHAR, CharPred.MAX_CHAR);
	public final static CharPred FALSE = new CharPred(ImmutableList.<ImmutablePair<Character, Character>>of());

	public final static CharPred ALPHA = new CharPred(ImmutableList.of(
			ImmutablePair.of('A', 'Z'),
			ImmutablePair.of('a', 'z')));
	public final static CharPred UPPER_ALPHA = new CharPred('A', 'Z');
	public final static CharPred LOWER_ALPHA = new CharPred('a', 'z');

	public final static CharPred NUM = new CharPred('0', '9');

	public final static CharPred ALPHA_NUM = new CharPred(ImmutableList.of(
			ImmutablePair.of('0', '9'),
			ImmutablePair.of('A', 'Z'),
			ImmutablePair.of('a', 'z')));
	public final static CharPred WORD = new CharPred(ImmutableList.of(
			ImmutablePair.of('0', '9'),
			ImmutablePair.of('A', 'Z'),
			ImmutablePair.of('a', 'z'),
			ImmutablePair.of('_', '_')));

	public final static CharPred BLANK = new CharPred(ImmutableList.of(
			ImmutablePair.of(' ', ' '),
			ImmutablePair.of('\t', '\t')));
	public final static CharPred SPACES = new CharPred(ImmutableList.of(
			ImmutablePair.of('\t', '\r'),
			ImmutablePair.of(' ', ' ')));

	public final static CharPred CNTRL = new CharPred(ImmutableList.of(
			ImmutablePair.of((char)0, (char)31),
			ImmutablePair.of((char)127, (char)127)));
	public final static CharPred GRAPH = new CharPred(ImmutableList.of(
			ImmutablePair.of((char)0x21, (char)0x7e)));
	public final static CharPred PRINT = new CharPred(ImmutableList.of(
			ImmutablePair.of((char)0x20, (char)0x7e)));
	public final static CharPred PUNCT = CharPred.of(ImmutableList.of('[', ']',
			'!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '.',
			'/', ':', ';', '<', '=', '>', '?', '@', '\\', '^', '_', '`', '{',
			'|', '}', '~', '-'));

	public final static CharPred XDIGIT = new CharPred(ImmutableList.of(
			ImmutablePair.of('0', '9'),
			ImmutablePair.of('A', 'F'),
			ImmutablePair.of('a', 'f')));

}
