package regexconverter;


import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.sat4j.specs.TimeoutException;

import RegexParser.AnchorNode;
import RegexParser.CharNode;
import RegexParser.CharacterClassNode;
import RegexParser.ConcatenationNode;
import RegexParser.DotNode;
import RegexParser.EscapedCharNode;
import RegexParser.FormulaNode;
import RegexParser.IntervalNode;
import RegexParser.MetaCharNode;
import RegexParser.ModifierNode;
import RegexParser.NormalCharNode;
import RegexParser.NotCharacterClassNode;
import RegexParser.OptionalNode;
import RegexParser.PlusNode;
import RegexParser.RegexNode;
import RegexParser.RepetitionNode;
import RegexParser.StarNode;
import RegexParser.UnionNode;
import automata.sfa.SFA;
import automata.sfa.SFAInputMove;
import automata.sfa.SFAMove;
import theory.characters.CharPred;
import theory.characters.StdCharPred;
import theory.intervals.UnaryCharIntervalSolver;

public class RegexConverter {

	public static SFA<CharPred, Character> toSFA(FormulaNode phi, UnaryCharIntervalSolver unarySolver)
			throws TimeoutException {
		SFA<CharPred, Character> outputSFA = null;

		if (phi instanceof UnionNode) {
			// get left SFA and right SFA, union them

			UnionNode cphi = (UnionNode) phi;
			SFA<CharPred, Character> left = toSFA(cphi.getMyRegex1(), unarySolver);
			SFA<CharPred, Character> right = toSFA(cphi.getMyRegex2(), unarySolver);
			outputSFA = SFA.union(left, right, unarySolver);

			return outputSFA;

		} else if (phi instanceof ConcatenationNode) {
			// get the first SFA in concatenation list, then for every following
			// SFA, iteratively concatenate them

			ConcatenationNode cphi = (ConcatenationNode) phi;
			List<RegexNode> concateList = cphi.getList();
			Iterator<RegexNode> it = concateList.iterator();
			//initialize SFA to empty SFA
			Collection<SFAMove<CharPred, Character>> transitionsA = new LinkedList<SFAMove<CharPred, Character>>();
			outputSFA = SFA.MkSFA(transitionsA, 0, Arrays.asList(0), unarySolver);
			while (it.hasNext()) {
				SFA<CharPred, Character> followingSFA = toSFA(it.next(), unarySolver);
				outputSFA = SFA.concatenate(outputSFA, followingSFA, unarySolver);
			}
			return outputSFA;

		} else if (phi instanceof DotNode) {
			// make a SFA that has a transition which accepts TRUE
			Collection<SFAMove<CharPred, Character>> transitionsA = new LinkedList<SFAMove<CharPred, Character>>();
			transitionsA.add(new SFAInputMove<CharPred, Character>(0, 1, unarySolver.True()));
			return SFA.MkSFA(transitionsA, 0, Arrays.asList(1), unarySolver);

		} else if (phi instanceof AnchorNode) {
			AnchorNode cphi = (AnchorNode) phi;
			outputSFA = toSFA(cphi.getMyRegex1(), unarySolver);
			if(!cphi.hasStartAnchor()){
				// put startAnchor SFA to the front of the following SFA
				outputSFA = SFA.concatenate(SFA.getFullSFA(unarySolver), outputSFA, unarySolver) ;
			}
			if(!cphi.hasEndAnchor()){
				// for end anchor, create a SFA that has state 0 that goes to state 1 with every input and add self-loop for state 1
				outputSFA = SFA.concatenate(outputSFA, SFA.getFullSFA(unarySolver), unarySolver);
			}
			
		} else if (phi instanceof StarNode) {
			// use existing SFA.star() method
			StarNode cphi = (StarNode) phi;
			SFA<CharPred, Character> tempSFA = toSFA(cphi.getMyRegex1(), unarySolver);
			outputSFA = SFA.star(tempSFA, unarySolver);

		} else if (phi instanceof PlusNode) {
			// expr+ = expr concatenate with expr*
			PlusNode cphi = (PlusNode) phi;
			SFA<CharPred, Character> tempSFA = toSFA(cphi.getMyRegex1(), unarySolver);
			outputSFA = SFA.concatenate(tempSFA, SFA.star(tempSFA, unarySolver), unarySolver);

		} else if (phi instanceof OptionalNode) {
			OptionalNode cphi = (OptionalNode) phi;
			SFA<CharPred, Character> tempSFA = toSFA(cphi.getMyRegex1(), unarySolver);
			
			// build an SFA that only accepts the empty string
			Collection<SFAMove<CharPred, Character>> transitions = new LinkedList<SFAMove<CharPred, Character>>();
			outputSFA = SFA.union(tempSFA, SFA.MkSFA(transitions,0, Arrays.asList(0), unarySolver), unarySolver);

		} else if (phi instanceof NormalCharNode) {
			// make a SFA that has a transition which accepts this char
			NormalCharNode cphi = (NormalCharNode) phi;
			Collection<SFAMove<CharPred, Character>> transitions = new LinkedList<SFAMove<CharPred, Character>>();
			transitions.add(new SFAInputMove<CharPred, Character>(0, 1, new CharPred(cphi.getChar())));
			return SFA.MkSFA(transitions, 0, Arrays.asList(1), unarySolver);

		} else if (phi instanceof EscapedCharNode) {
			// make a SFA that has a transition which accepts the char after the
			// backslash
			EscapedCharNode cphi = (EscapedCharNode) phi;
			Collection<SFAMove<CharPred, Character>> transitions = new LinkedList<SFAMove<CharPred, Character>>();
			transitions.add(new SFAInputMove<CharPred, Character>(0, 1, new CharPred(cphi.getChar())));
			return SFA.MkSFA(transitions, 0, Arrays.asList(1), unarySolver);

		} else if (phi instanceof MetaCharNode) {
			MetaCharNode cphi = (MetaCharNode) phi;
			Collection<SFAMove<CharPred, Character>> transitions = new LinkedList<SFAMove<CharPred, Character>>();
			char meta = cphi.getChar();
			if (meta == 't') {
				// CharPred \t
				transitions.add(new SFAInputMove<CharPred, Character>(0, 1, new CharPred('\t', '\t')));
				return SFA.MkSFA(transitions, 0, Arrays.asList(1), unarySolver);
			} else if (meta == 'n') {
				// CharPred \n
				transitions.add(new SFAInputMove<CharPred, Character>(0, 1, new CharPred('\n', '\n')));
				return SFA.MkSFA(transitions, 0, Arrays.asList(1), unarySolver);
			} else if (meta == 'r') {
				// CharPred \r
				transitions.add(new SFAInputMove<CharPred, Character>(0, 1, new CharPred('\r', '\r')));
				return SFA.MkSFA(transitions, 0, Arrays.asList(1), unarySolver);
			} else if (meta == 'f') {
				// CharPred \f
				transitions.add(new SFAInputMove<CharPred, Character>(0, 1, new CharPred('\f', '\f')));
				return SFA.MkSFA(transitions, 0, Arrays.asList(1), unarySolver);
			} else if (meta == 'b') {
				// don't know how to do word boundary
				throw new UnsupportedOperationException();
			} else if (meta == 'B') {
				// don't know how to do word boundary
				throw new UnsupportedOperationException();
			} else if (meta == 'd') {
				// use existing NUM
				transitions.add(new SFAInputMove<CharPred, Character>(0, 1, StdCharPred.NUM));
				return SFA.MkSFA(transitions, 0, Arrays.asList(1), unarySolver);
			} else if (meta == 'D') {
				// MkNot(NUM)
				transitions.add(new SFAInputMove<CharPred, Character>(0, 1, unarySolver.MkNot(StdCharPred.NUM)));
				return SFA.MkSFA(transitions, 0, Arrays.asList(1), unarySolver);
			} else if (meta == 's') {
				// use existing SPACES
				transitions.add(new SFAInputMove<CharPred, Character>(0, 1, StdCharPred.SPACES));
				return SFA.MkSFA(transitions, 0, Arrays.asList(1), unarySolver);
			} else if (meta == 'S') {
				// MkNot(SPACES)
				transitions.add(new SFAInputMove<CharPred, Character>(0, 1, unarySolver.MkNot(StdCharPred.SPACES)));
				return SFA.MkSFA(transitions, 0, Arrays.asList(1), unarySolver);

			} else if (meta == 'v') {
				// predicate= new CharPred('\v', '\v');
				// this meta can be seen in the regexlib but it seems java
				// does not support this
				throw new UnsupportedOperationException();
			} else if (meta == 'w') {
				// use existing WORD
				transitions.add(new SFAInputMove<CharPred, Character>(0, 1, StdCharPred.WORD));
				return SFA.MkSFA(transitions, 0, Arrays.asList(1), unarySolver);
			} else if (meta == 'W') {
				// MkNot(WORD)
				transitions.add(new SFAInputMove<CharPred, Character>(0, 1, unarySolver.MkNot(StdCharPred.WORD)));
				return SFA.MkSFA(transitions, 0, Arrays.asList(1), unarySolver);
			}

		} else if (phi instanceof CharacterClassNode) {
			// MkOr each interval then MkSFA from the final CharPred
			CharacterClassNode cphi = (CharacterClassNode) phi;
			Collection<SFAMove<CharPred, Character>> transitions = new LinkedList<SFAMove<CharPred, Character>>();
			List<IntervalNode> intervalList = cphi.getIntervals();
			Iterator<IntervalNode> it = intervalList.iterator();
			CharPred predicate = unarySolver.False();
			if (it.hasNext()) {
				predicate = getCharPred(it.next(), unarySolver);
				while (it.hasNext()) {
					CharPred temp = getCharPred(it.next(), unarySolver);
					predicate = unarySolver.MkOr(predicate, temp);
				}
			}
			transitions.add(new SFAInputMove<CharPred, Character>(0, 1, predicate));
			return SFA.MkSFA(transitions, 0, Arrays.asList(1), unarySolver);

		} else if (phi instanceof NotCharacterClassNode) {
			// MkOr each interval then MkNot the final result of the CharPred,
			// then MkSFA from that final CharPred
			NotCharacterClassNode cphi = (NotCharacterClassNode) phi;
			Collection<SFAMove<CharPred, Character>> transitions = new LinkedList<SFAMove<CharPred, Character>>();
			List<IntervalNode> intervalList = cphi.getIntervals();
			Iterator<IntervalNode> it = intervalList.iterator();
			CharPred predicate = unarySolver.False();
			if (it.hasNext()) {
				predicate = getCharPred(it.next(), unarySolver);
				while (it.hasNext()) {
					CharPred temp = getCharPred(it.next(), unarySolver);
					predicate = unarySolver.MkOr(predicate, temp);
				}
			}
			predicate = unarySolver.MkNot(predicate);
			transitions.add(new SFAInputMove<CharPred, Character>(0, 1, predicate));
			return SFA.MkSFA(transitions, 0, Arrays.asList(1), unarySolver);

		} else if (phi instanceof RepetitionNode) {
			RepetitionNode cphi = (RepetitionNode) phi;
			Collection<SFAMove<CharPred, Character>> transitionsA = new LinkedList<SFAMove<CharPred, Character>>();
			outputSFA = SFA.MkSFA(transitionsA, 0, Arrays.asList(0), unarySolver);
			SFA<CharPred, Character> tempSFA = toSFA(cphi.getMyRegex1(), unarySolver);
			for (int i = 0; i < cphi.getMin(); i++) { //now we looped min times
				outputSFA = SFA.concatenate(outputSFA, tempSFA, unarySolver);
			}
			
			if (cphi.getMode().equals("min")) {
				//already looped min times
				return outputSFA;
				
			} else if (cphi.getMode().equals("minToInfinite")) {
				// concatenate with a star, e.g. R{3,} = RRR(R)*
				return SFA.concatenate(outputSFA, SFA.star(tempSFA, unarySolver), unarySolver);
				
			} else { // minToMax
				SFA<CharPred, Character> unions = outputSFA;
				for(int i = cphi.getMin(); i< cphi.getMax(); i++){
					unions = SFA.concatenate(unions, tempSFA, unarySolver);
					outputSFA = SFA.union(outputSFA, unions, unarySolver);
				}
				return outputSFA;
			}
			
		}else if (phi instanceof ModifierNode) {
			throw new UnsupportedOperationException();
		}else {
			System.err.println("Wrong instance of phi, program will quit");
			System.exit(-1);
		}

		return outputSFA;

	}

	private static CharPred getCharPred(IntervalNode node, UnaryCharIntervalSolver unarySolver) {
		CharPred predicate = null;
		if (node.getMode().equals("single")) {
			CharNode single = node.getChar1();
			if (single instanceof NormalCharNode) {
				predicate = new CharPred(single.getChar());
			} else if (single instanceof MetaCharNode) {
				char meta = single.getChar();
				if (meta == 't') {
					predicate = new CharPred('\t', '\t');
				} else if (meta == 'n') {
					predicate = new CharPred('\n', '\n');
				} else if (meta == 'r') {
					predicate = new CharPred('\r', '\r');
				} else if (meta == 'f') {
					predicate = new CharPred('\f', '\f');
				} else if (meta == 'b') {
					// don't know how to do word boundary
					throw new UnsupportedOperationException();
				} else if (meta == 'B') {
					// don't know how to do word boundary
					throw new UnsupportedOperationException();
				} else if (meta == 'd') {
					predicate = StdCharPred.NUM;
				} else if (meta == 'D') {
					predicate = unarySolver.MkNot(StdCharPred.NUM);
				} else if (meta == 's') {
					predicate = StdCharPred.SPACES;
				} else if (meta == 'S') {
					predicate = unarySolver.MkNot(StdCharPred.SPACES);
				} else if (meta == 'v') {
					// predicate= new CharPred('\v', '\v');
					// this meta can be seen in the regexlib but it seems java
					// does not support this
					throw new UnsupportedOperationException();
				} else if (meta == 'w') {
					predicate = StdCharPred.WORD;
				} else if (meta == 'W') {
					// not sure how to take complement, there should be more
					predicate = unarySolver.MkNot(StdCharPred.WORD);
				}

			} else { // EscapedCharNode
				// getChar() method returns the char after the backslash
				// TODO: not sure if backslash is needed
				predicate = new CharPred(single.getChar());
			}
		} else {
			predicate = new CharPred(node.getChar1().getChar(), node.getChar2().getChar());
		}

		return predicate;
	}

}
