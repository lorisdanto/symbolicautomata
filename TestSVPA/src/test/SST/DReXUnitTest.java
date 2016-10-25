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
import org.sat4j.specs.TimeoutException;

import automata.sfa.SFA;
import automata.sfa.SFAInputMove;
import automata.sfa.SFAMove;
import theory.characters.CharFunc;
import theory.characters.CharOffset;
import theory.characters.CharPred;
import theory.characters.CharPred;
import theory.intervals.UnaryCharIntervalSolver;
import theory.intervals.UnaryCharIntervalSolver;
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

/**
 * DReXUnitTest
 */
public class DReXUnitTest {

	@Test
	public void testPrePost() throws TimeoutException {
		UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();
		SST<CharPred, CharFunc, Character> sstID = getID(ba).star(ba);

		
		SFA<CharPred, Character> alphanum = alphaNumSFA(ba);


		SFA<CharPred, Character> comp = alphanum.complement(ba);
		SFA<CharPred, Character> pre = sstID.getPreImage(comp, ba);
		SFA<CharPred, Character> pre2 = sstID.getPreImage(alphanum, ba);

		// SFA<CharPred, Character> tcdom = sstBase.getPreImage(atLeast2As, ba);
		assertTrue(sstID.typeCheck(alphanum, alphanum, ba));
	}
	
	
//	Hi Loris,
//
//	The sst preimage computation throws an exception for the following test
//	case. I don't want to mess around too much in that bit of code, I don't
//	understand it too well. I will add a proper test case and commit it in
//	about an hour. Could you please take a look at it?
//
//	Arjun
//
//	POST: (concat (concat (concat epsilon (star [0])) [1-9]) (star [0-9]))
//	NOT POST AUT:
//
//	Transitions
//	S: 0 -[1-9]-> 1
//	S: 0 -[\u0000-/:-\uffff]-> 4
//	S: 0 -[0]-> 2
//	S: 1 -[0-9]-> 3
//	S: 1 -[\u0000-/:-\uffff]-> 4
//	S: 2 -[0]-> 2
//	S: 2 -[1-9]-> 1
//	S: 2 -[\u0000-/:-\uffff]-> 4
//	S: 3 -[\u0000-/:-\uffff]-> 4
//	S: 3 -[0-9]-> 3
//	S: 4 -[\u0000-\uffff]-> 4
//	Initial State
//	0
//	Final States
//	0
//	2
//	4
//
//	SST: (ifelse 
//			(iter bot) 
		//	(split 
		//		(ifelse 
		//				(split (epsilon "1") (symbol [9] [x + -9])) 
		//				(split (iter (symbol [0-9] [x + 0])) (symbol [0-8] [x + 1]))
		//		)
		//		(iter (symbol [9] [x -> 0]))))
//
//	(Generate SST by e.getSST(solver))
	
	@Test
	public void testPrePostArjunEmail() throws TimeoutException {
		UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();
		SST<CharPred, CharFunc, Character> sst = getSTTArjun(ba);

		
		
		SFA<CharPred, Character> outputSFA = arjunOutputSFA(ba);

		SFA<CharPred, Character> pre = sst.getPreImage(outputSFA, ba);

		// SFA<CharPred, Character> tcdom = sstBase.getPreImage(atLeast2As, ba);
		assertTrue(!pre.isEmpty());
	}

	// S: F(0) = a
	private SST<CharPred, CharFunc, Character> getID(UnaryCharIntervalSolver ba) {

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
	private SST<CharPred, CharFunc, Character> getSTTArjun(UnaryCharIntervalSolver ba) {

		SST<CharPred, CharFunc, Character> symbol9xm9 = SST.getBaseSST(new CharPred('9'), justXp(-9), ba);
		SST<CharPred, CharFunc, Character> epsilon1 = SST.getEpsilonSST(consttokclist('1'), ba);
		SST<CharPred, CharFunc, Character> splitep1sym9 =  SST.concatenate(epsilon1,symbol9xm9,ba);
		
		SST<CharPred, CharFunc, Character> symbol08xxp1 = SST.getBaseSST(new CharPred('0','8'), justXp(1), ba);
		SST<CharPred, CharFunc, Character> symbol09xx = SST.getBaseSST(new CharPred('0','9'), justXp(0), ba);		
		SST<CharPred, CharFunc, Character> splititersymbol = SST.concatenate(symbol09xx.star(ba), symbol08xxp1, ba);
		SST<CharPred, CharFunc, Character> ifelsesplitsplit = SST.union(splitep1sym9,splititersymbol, ba);
		
		
		SST<CharPred, CharFunc, Character> symbol9x0 = SST.getBaseSST(new CharPred('9'), consttoklist('0'), ba);
		SST<CharPred, CharFunc, Character> itersymbol9x0 = symbol9x0.star(ba);
		SST<CharPred, CharFunc, Character> bot = SST.getEmptySST(ba);
		SST<CharPred, CharFunc, Character> iterbot = bot.star(ba);
		SST<CharPred, CharFunc, Character> splitbig = SST.concatenate(ifelsesplitsplit, itersymbol9x0, ba);
							
		return SST.union(iterbot, splitbig, ba);
	}
	
	// Identity transducer
	private SST<CharPred, CharFunc, Character> getIDSTT(UnaryCharIntervalSolver ba) {

		Collection<SSTMove<CharPred, CharFunc, Character>> transitionsA = new ArrayList<SSTMove<CharPred, CharFunc, Character>>();

		transitionsA.add(new SSTInputMove<CharPred, CharFunc, Character>(0, 0,
				ba.True(), xEQxid()));

		// Output function just outputs x
		Map<Integer, OutputUpdate<CharPred, CharFunc, Character>> outputFunction = new HashMap<Integer, OutputUpdate<CharPred, CharFunc, Character>>();
		outputFunction.put(0, justXout());

		return SST.MkSST(transitionsA, 0, onlyX, outputFunction, ba);
	}

	// SFA that accepts strings contatining at least two as
	private SFA<CharPred, Character> alphaNumSFA(UnaryCharIntervalSolver ba) {

		Collection<SFAMove<CharPred, Character>> transitionsA = new ArrayList<SFAMove<CharPred, Character>>();
		CharPred alphaNum = ba.MkOr(alpha, num);

		transitionsA.add(new SFAInputMove<CharPred, Character>(0, 0, alphaNum));

		// Output function just outputs x

		try {
			return SFA.MkSFA(transitionsA, 0, Arrays.asList(0), ba);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// SFA that accepts strings contatining at least two as
	private SFA<CharPred, Character> arjunOutputSFA(UnaryCharIntervalSolver ba) {

		Collection<SFAMove<CharPred, Character>> transitionsA = new ArrayList<SFAMove<CharPred, Character>>();
		CharPred num09 = ba.MkOr(num0, num);
		CharPred notnum = ba.MkNot(num09);

		transitionsA.add(new SFAInputMove<CharPred, Character>(0, 1, num));
		transitionsA.add(new SFAInputMove<CharPred, Character>(0, 4, notnum));
		transitionsA.add(new SFAInputMove<CharPred, Character>(0, 2, num0));
		transitionsA.add(new SFAInputMove<CharPred, Character>(1, 3, num09));
		transitionsA.add(new SFAInputMove<CharPred, Character>(1, 4, notnum));
		transitionsA.add(new SFAInputMove<CharPred, Character>(2, 2, num0));
		transitionsA.add(new SFAInputMove<CharPred, Character>(2, 1, num));
		transitionsA.add(new SFAInputMove<CharPred, Character>(2, 4, notnum));
		transitionsA.add(new SFAInputMove<CharPred, Character>(3, 4, notnum));
		transitionsA.add(new SFAInputMove<CharPred, Character>(3, 3, num09));
		transitionsA.add(new SFAInputMove<CharPred, Character>(4, 4, ba.True()));
		

		try {
			return SFA.MkSFA(transitionsA, 0, Arrays.asList(0,2,4), ba);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
	CharPred alpha = new CharPred('a', 'z');
	CharPred a = new CharPred('a');
	CharPred num = new CharPred('1', '9');
	CharPred num0 = new CharPred('0');
	CharPred comma = new CharPred(',');
	int onlyX = 1;

	private OutputUpdate<CharPred, CharFunc, Character> justXout() {
		SSTVariable<CharPred, CharFunc, Character> xv = new SSTVariable<>(0);
		LinkedList<ConstantToken<CharPred, CharFunc, Character>> justX = new LinkedList<>();
		justX.add(xv);
		return new OutputUpdate<CharPred, CharFunc, Character>(justX);
	}

	private List<ConstantToken<CharPred, CharFunc, Character>> consttokclist(char c) {
		LinkedList<ConstantToken<CharPred, CharFunc, Character>> l = new LinkedList<>();
		l.add(new transducers.sst.CharConstant(c));
		return l;
	}
	
	private List<Token<CharPred, CharFunc, Character>> consttoklist(char c) {
		LinkedList<Token<CharPred, CharFunc, Character>> l = new LinkedList<>();
		l.add(new transducers.sst.CharConstant(c));
		return l;
	}

//	private List<Token<CharPred, CharFunc, Character>> justXlist() {
//		SSTVariable<CharPred, CharFunc, Character> xv = new SSTVariable<>(0);
//		LinkedList<Token<CharPred, CharFunc, Character>> justX = new LinkedList<>();
//		justX.add(xv);
//		return justX;
//	}
	
	private List<Token<CharPred, CharFunc, Character>> justXp(int i) {
		Token<CharPred, CharFunc, Character> xp1 = new CharFunction<CharPred, CharFunc, Character>(
					new CharOffset(i));
		LinkedList<Token<CharPred, CharFunc, Character>> justX = new LinkedList<>();
		justX.add(xp1);
		return justX;
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
