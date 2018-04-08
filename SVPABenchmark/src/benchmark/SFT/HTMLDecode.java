package benchmark.SFT;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import benchmark.SFT.codecs.HTMLEntityCodec;

import theory.characters.CharConstant;
import theory.characters.CharFunc;
import theory.characters.CharOffset;
import theory.characters.CharPred;
import theory.intervals.UnaryCharIntervalSolver;
import transducers.sft.SFT;
import transducers.sft.SFTInputMove;
import transducers.sft.SFTMove;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class HTMLDecode {
    private static HTMLEntityCodec htmlCodec = new HTMLEntityCodec();
    private static SFT<CharPred, CharFunc, Character> sft = null;
    private static UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();

    private static SFT<CharPred, CharFunc, Character> MkDecodeSFT(){
        List<SFTMove<CharPred, CharFunc, Character>> transitions = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();
        CharPred notLessThan = ba.MkOr(new CharPred(CharPred.MIN_CHAR, (char)('<' - 1)), new CharPred((char)('<' + 1), CharPred.MAX_CHAR));
        CharPred notGreaterThan = ba.MkOr(new CharPred(CharPred.MIN_CHAR, (char)('>' - 1)), new CharPred((char)('>' + 1), CharPred.MAX_CHAR));

        List<CharFunc> output00 = new ArrayList<CharFunc>();
        transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(0, 0, notLessThan, output00));

        List<CharFunc> output01 = new ArrayList<CharFunc>();
        transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(0, 1, new CharPred('<'), output01));

        List<CharFunc> output11 = new ArrayList<CharFunc>();
        transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 1, new CharPred('<'), output11));

        List<CharFunc> output12 = new ArrayList<CharFunc>();
        transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 2, notLessThan, output12));

        List<CharFunc> output13 = new ArrayList<CharFunc>();
        output13.add(new CharConstant('<'));
        output13.add(CharOffset.IDENTITY);
        transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 3, notLessThan, output13));

        List<CharFunc> output20 = new ArrayList<CharFunc>();
        transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(2, 0, ba.MkAnd(notLessThan, notGreaterThan), output20));

        List<CharFunc> output21 = new ArrayList<CharFunc>();
        transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(2, 1, new CharPred('<'), output21));

        List<CharFunc> output30 = new ArrayList<CharFunc>();
        output30.add(CharOffset.IDENTITY);
        transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(3, 0, new CharPred('>'), output30));

        Map<Integer, List<Character>> finStatesAndTails = new HashMap<Integer, List<Character>>();
        finStatesAndTails.put(0, new ArrayList<Character>());
        finStatesAndTails.put(1, new ArrayList<Character>());
        finStatesAndTails.put(2, new ArrayList<Character>());

        return SFT.MkSFT(transitions, 0, finStatesAndTails, ba);
    }

    /**
     * the implementation of the SFT in left column on page 4 on the paper named after Symbolic Finite State Transducers:
     * Algorithms And Applications
     */
    public static String GetTagsBySFT(String input) {
        // if the required SFT has not been created yet, then generate it. Otherwise we could just use it directly
        // instead of generating it every time GetTagsBySFT is called, which is quite time consuming.
        if (sft == null)
            sft = MkDecodeSFT();
        return input;
    }

    public static void main(String args[]) {
        System.out.println(htmlCodec.decode("123&#49"));
    }

    @Test
    public void test()
    {
        assertEquals( "test!", htmlCodec.decode("&#116;&#101;&#115;&#116;!") );
    }
}

