/**
 * TestSVPA
 * test.SST
 * Aug 3, 2015
 * @author Loris D'Antoni
 */
package test.SST;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import theory.CharFunc;
import theory.CharOffset;
import theory.CharPred;
import theory.CharSolver;
import transducers.sst.CharFunction;
import transducers.sst.ConstantToken;
import transducers.sst.FunctionalVariableUpdate;
import transducers.sst.OutputUpdate;
import transducers.sst.SST;
import transducers.sst.SSTInputMove;
import transducers.sst.SSTMove;
import transducers.sst.SSTVariable;
import transducers.sst.SimpleVariableUpdate;
import transducers.sst.Token;
import automata.sfa.SFA;
import automata.sfa.SFAInputMove;
import automata.sfa.SFAMove;

/**
 * DReXUnitTest
 */
public class DReXUnitTest {

	@Test
	public void testPrePost() {
		CharSolver ba = new CharSolver();
		SST<CharPred, CharFunc, Character> sstID = getID(ba).star(ba);

		
		SFA<CharPred, Character> alphanum = alphaNumSFA(ba);


		SFA<CharPred, Character> comp = alphanum.complement(ba);
		SFA<CharPred, Character> pre = sstID.getPreImage(comp, ba);
		SFA<CharPred, Character> pre2 = sstID.getPreImage(alphanum, ba);

		// SFA<CharPred, Character> tcdom = sstBase.getPreImage(atLeast2As, ba);
		assertTrue(sstID.typeCheck(alphanum, alphanum, ba));
	}

	// S: F(0) = a
	private SST<CharPred, CharFunc, Character> getID(CharSolver ba) {

		List<Token<CharPred, CharFunc, Character>> output = new ArrayList<Token<CharPred, CharFunc, Character>>();
		output.add(new CharFunction<CharPred, CharFunc, Character>(CharOffset.IDENTITY));
		
		return SST.getBaseSST(ba.True(), output, ba);
	}

	// string str1 = "Hello".
	// string str2 = "Hello".
	// string str3 = str1.
	//
	// assert("Hello" == str1).
	// assert(str1 == str2 and str2 == str3).
	//
	// strfn id = [x : U* / x].
	// assert(id(str1) == str2).
	//
	// language pre = ("a".."z" or "A".."Z" or "0".."9")*.
	// assert(triple(pre, id, pre)).
	//
	// language post = ("a".."z" or "A".."Z")*.
	// assert(not(triple(pre, id, post))).

	// Identity transducer
	private SST<CharPred, CharFunc, Character> getIDSTT(CharSolver ba) {

		Collection<SSTMove<CharPred, CharFunc, Character>> transitionsA = new ArrayList<SSTMove<CharPred, CharFunc, Character>>();

		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				ba.True(), xEQxid()));

		// Output function just outputs x
		Map<Integer, OutputUpdate<CharPred, CharFunc, Character>> outputFunction = new HashMap<Integer, OutputUpdate<CharPred, CharFunc, Character>>();
		outputFunction.put(0, justXout());

		return SST.MkSST(transitionsA, 0, onlyX, outputFunction, ba);
	}

	// SFA that accepts strings contatining at least two as
	private SFA<CharPred, Character> alphaNumSFA(CharSolver ba) {

		Collection<SFAMove<CharPred, Character>> transitionsA = new ArrayList<SFAMove<CharPred, Character>>();
		CharPred alphaNum = ba.MkOr(alpha, num);

		transitionsA.add(new SFAInputMove<CharPred, Character>(0, 0, alphaNum));

		// Output function just outputs x

		return SFA.MkSFA(transitionsA, 0, Arrays.asList(0), ba);
	}

	CharPred alpha = new CharPred('a', 'z');
	CharPred a = new CharPred('a');
	CharPred num = new CharPred('1', '9');
	CharPred comma = new CharPred(',');
	int onlyX = 1;

	private OutputUpdate<CharPred, CharFunc, Character> justXout() {
		SSTVariable<CharPred, CharFunc, Character> xv = new SSTVariable<>(0);
		LinkedList<ConstantToken<CharPred, CharFunc, Character>> justX = new LinkedList<>();
		justX.add(xv);
		return new OutputUpdate<CharPred, CharFunc, Character>(justX);
	}

	private SimpleVariableUpdate<CharPred, CharFunc, Character> justXsimple() {
		SSTVariable<CharPred, CharFunc, Character> xv = new SSTVariable<>(0);
		LinkedList<ConstantToken<CharPred, CharFunc, Character>> justX = new LinkedList<>();
		justX.add(xv);
		return new SimpleVariableUpdate<CharPred, CharFunc, Character>(justX);
	}

	private FunctionalVariableUpdate<CharPred, CharFunc, Character> xEQxid() {
		SSTVariable<CharPred, CharFunc, Character> xv = new SSTVariable<>(0);
		LinkedList<Token<CharPred, CharFunc, Character>> xa = new LinkedList<>();
		xa.add(xv);
		xa.add(new CharFunction<CharPred, CharFunc, Character>(
				CharOffset.IDENTITY));
		return new FunctionalVariableUpdate<>(xa);
	}

}
