package benchmark.SFT;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import theory.characters.CharPred;
import theory.characters.StdCharPred;
import theory.characters.CharFunc;
import theory.characters.CharOffset;
import theory.intervals.UnaryCharIntervalSolver;
import transducers.sft.SFT;
import transducers.sft.SFTMove;
import transducers.sft.SFTInputMove;

/**
 * According to the left column on page 4 of the paper named after Symbolic Finite State Transducers: Algorithms And
 * Applications, properties such as commutativity and idempotence of SFTs depend on the theory of labels. Here I give out
 * two brief examples
 */
public class CommutativityAndIdempotence {
    private static UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();
    private static SFT<CharPred, CharFunc, Character> caseConversion;
    private static SFT<CharPred, CharFunc, Character> deleteZeros;

    /**
     * convert all lower cases to upper cases and all upper cases to lower cases
     */
    private static SFT<CharPred, CharFunc, Character> MkCaseConversionSft() throws Exception {
        List<SFTMove<CharPred, CharFunc, Character>> transitions = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();

        List<CharFunc> output001 = new ArrayList<CharFunc>();
        output001.add(CharOffset.TO_LOWER_CASE);
        transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(0, 0, StdCharPred.UPPER_ALPHA, output001));

        List<CharFunc> output002 = new ArrayList<CharFunc>();
        output002.add(CharOffset.TO_UPPER_CASE);
        transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(0, 0, StdCharPred.LOWER_ALPHA, output002));

        Map<Integer, Set<List<Character>>> finStatesAndTails = new HashMap<Integer, Set<List<Character>>>();
        finStatesAndTails.put(0, new HashSet<List<Character>>());

        return SFT.MkSFT(transitions, 0, finStatesAndTails, ba);
    }

    /**
     * delete all character '0' in the string
     */
    private static SFT<CharPred, CharFunc, Character> MkDeleteZeros() throws Exception {
        List<SFTMove<CharPred, CharFunc, Character>> transitions = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();

        List<CharFunc> output001 = new ArrayList<CharFunc>();
        transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(0, 0, new CharPred('0'), output001));

        List<CharFunc> output002 = new ArrayList<CharFunc>();
        output002.add(CharOffset.IDENTITY);
        transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(0, 0, ba.MkNot(new CharPred('0')), output002));

        Map<Integer, Set<List<Character>>> finStatesAndTails = new HashMap<Integer, Set<List<Character>>>();
        finStatesAndTails.put(0, new HashSet<List<Character>>());

        return SFT.MkSFT(transitions, 0, finStatesAndTails, ba);
    }

    @Test
    public void test() throws Exception {
        caseConversion = MkCaseConversionSft();
        deleteZeros = MkDeleteZeros();
        // commutativity: delete zeros first or convert cases first does not matter, so they should commute
        SFT<CharPred, CharFunc, Character> composed1 = deleteZeros.composeWith(caseConversion, ba);
        SFT<CharPred, CharFunc, Character> composed2 = caseConversion.composeWith(deleteZeros, ba);
        assertTrue(SFT.decide1equality(composed1, composed2, ba));

        // Idempotence: delete zeros is idempotence because whatever times you use the SFT, all zeros are removed and
        // all other characters are unchanged.
        SFT<CharPred, CharFunc, Character> composed = MkDeleteZeros();
        for (int i = 1; i < 10; i++) {
            composed = composed.composeWith(MkDeleteZeros(), ba);
            assertTrue(SFT.decide1equality(deleteZeros, composed, ba));
        }
    }
}
