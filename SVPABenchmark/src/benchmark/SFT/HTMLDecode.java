package benchmark.SFT;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import benchmark.SFT.codecs.HTMLEntityCodec;

import org.sat4j.specs.TimeoutException;
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
import java.util.Set;
import java.util.HashSet;

/**
 * The program focuses on decoding 2-digit html codes.
 * It is an implementation of the example on page 9 of a paper called Symbolic Finite State Transducers: Algorithms And
 * Applications
 * You could use JUnit to test method GetTagsBySFT or just use normal way to run the main method in order to decode
 * 2-digit html codes
 */
public class HTMLDecode {
    private static HTMLEntityCodec htmlCodec = new HTMLEntityCodec();
    private static SFT<CharPred, CharFunc, Character> sft = null;
    private static UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();

    private static SFT<CharPred, CharFunc, Character> MkDecodeSFT() throws TimeoutException {
        List<SFTMove<CharPred, CharFunc, Character>> transitions = new LinkedList<SFTMove<CharPred, CharFunc, Character>>();

        List<CharFunc> output00 = new ArrayList<CharFunc>();
        output00.add(CharOffset.IDENTITY);
        transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(0, 0, ba.MkNot(new CharPred('&')),
                output00));

        List<CharFunc> output01 = new ArrayList<CharFunc>();
        transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(0, 1, new CharPred('&'), output01));

        List<CharFunc> output10 = new ArrayList<CharFunc>();
        output10.add(new CharConstant('&'));
        output10.add(CharOffset.IDENTITY);
        transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 0, ba.MkAnd(ba.MkNot(new CharPred(
                '&')), ba.MkNot(new CharPred('#'))), output10));

        List<CharFunc> output11 = new ArrayList<CharFunc>();
        output11.add(new CharConstant('&'));
        transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 1, new CharPred('&'), output11));

        List<CharFunc> output12 = new ArrayList<CharFunc>();
        transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(1, 2, new CharPred('#'), output12));

        List<CharFunc> output21 = new ArrayList<CharFunc>();
        output21.add(new CharConstant('&'));
        output21.add(new CharConstant('#'));
        transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(2, 1, new CharPred('&'), output21));

        List<CharFunc> output20 = new ArrayList<CharFunc>();
        output20.add(new CharConstant('&'));
        output20.add(new CharConstant('#'));
        output20.add(CharOffset.IDENTITY);
        transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(2, 0, ba.MkAnd(ba.MkNot(new
                CharPred('&')), ba.MkNot(new CharPred('0', '9'))), output20));

        for (Integer i = 0; i < 10; i++) {
            List<CharFunc> output = new ArrayList<CharFunc>();
            transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(2, i + 3,
                    new CharPred(i.toString().charAt(0)), output));
        }

        for (Integer i = 0; i < 10; i++) {
            List<CharFunc> output = new ArrayList<CharFunc>();
            output.add(new CharConstant('&'));
            output.add(new CharConstant('#'));
            output.add(new CharConstant(i.toString().charAt(0)));
            output.add(CharOffset.IDENTITY);
            transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(i + 3, 0, ba.MkAnd(ba.MkNot(
                    new CharPred('&')), ba.MkNot(new CharPred('0', '9'))), output));
        }

        for (Integer i = 0; i < 10; i++) {
            List<CharFunc> output = new ArrayList<CharFunc>();
            output.add(new CharConstant('&'));
            output.add(new CharConstant('#'));
            output.add(new CharConstant(i.toString().charAt(0)));
            transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(i + 3, 1,
                    new CharPred('&'), output));
        }

        for (Integer i = 0; i < 10; i++) {
            for (Integer j = 0; j < 10; j++) {
                List<CharFunc> output = new ArrayList<CharFunc>();
                transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(i + 3, 13 + 10 * i + j,
                        new CharPred(j.toString().charAt(0)), output));
            }
        }

        for (Integer i = 0; i < 10; i++) {
            for (Integer j = 0; j < 10; j++) {
                List<CharFunc> output = new ArrayList<CharFunc>();
                output.add(new CharConstant('&'));
                output.add(new CharConstant('#'));
                output.add(new CharConstant(i.toString().charAt(0)));
                output.add(new CharConstant(j.toString().charAt(0)));
                output.add(CharOffset.IDENTITY);
                transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(13 + 10 * i + j, 0,
                        ba.MkAnd(ba.MkNot(new CharPred(';')), ba.MkNot(new CharPred('&'))), output));
            }
        }

        for (Integer i = 0; i < 10; i++) {
            for (Integer j = 0; j < 10; j++) {
                List<CharFunc> output = new ArrayList<CharFunc>();
                output.add(new CharConstant((char) (10 * i + j)));
                transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(13 + 10 * i + j, 0,
                        new CharPred(';'), output));
            }
        }

        for (Integer i = 0; i < 10; i++) {
            for (Integer j = 0; j < 10; j++) {
                List<CharFunc> output = new ArrayList<CharFunc>();
                output.add(new CharConstant('&'));
                output.add(new CharConstant('#'));
                output.add(new CharConstant(i.toString().charAt(0)));
                output.add(new CharConstant(j.toString().charAt(0)));
                transitions.add(new SFTInputMove<CharPred, CharFunc, Character>(13 + 10 * i + j, 1,
                        new CharPred('&'), output));
            }
        }


        Map<Integer, Set<List<Character>>> finStatesAndTails = new HashMap<Integer, Set<List<Character>>>();

        finStatesAndTails.put(0, new HashSet<List<Character>>());

        Set<List<Character>> tails1 = new HashSet<List<Character>>();
        List<Character> tail11 = new ArrayList<Character>();
        tail11.add('&');
        tails1.add(tail11);
        finStatesAndTails.put(1, tails1);

        Set<List<Character>> tails2 = new HashSet<List<Character>>();
        List<Character> tail21 = new ArrayList<Character>();
        tail21.add('&');
        tail21.add('#');
        tails2.add(tail21);
        finStatesAndTails.put(2, tails2);

        for (Integer i = 0; i < 10; i++) {
            Set<List<Character>> tails = new HashSet<List<Character>>();
            List<Character> tail = new ArrayList<Character>();
            tail.add('&');
            tail.add('#');
            tail.add(i.toString().charAt(0));
            tails.add(tail);
            finStatesAndTails.put(i + 3, tails);
        }

        for (Integer i = 0; i < 10; i++) {
            for (Integer j = 0; j < 10; j++) {
                Set<List<Character>> tails = new HashSet<List<Character>>();
                List<Character> tail = new ArrayList<Character>();
                tail.add('&');
                tail.add('#');
                tail.add(i.toString().charAt(0));
                tail.add(j.toString().charAt(0));
                tails.add(tail);
                finStatesAndTails.put(13 + 10 * i + j, tails);
            }
        }

        return SFT.MkSFT(transitions, 0, finStatesAndTails, ba);
    }

    /**
     * the modification of ST on page 9 on the paper named after Symbolic Finite State Transducers: Algorithms And
     * Applications
     */
    public static String decodeSFT(String input) throws TimeoutException {
        // if the required SFT has not been created yet, then generate it. Otherwise we could just use it directly
        // instead of generating it every time decodeSFT is called, which is quite time consuming.
        if (sft == null)
            sft = MkDecodeSFT();

        List<Character> output = sft.outputOn(stringToListOfCharacter(input), ba);

        return ba.stringOfList(output);
    }

    /**
     * convert a string into a list of characters
     * @param input a string
     * @return a list of class Character
     */
    private static List<Character> stringToListOfCharacter(String input) {
        List<Character> output = new ArrayList<Character>();
        for (char character: input.toCharArray())
            output.add(character);
        return output;
    }

    public static void main(String args[]) throws org.sat4j.specs.TimeoutException {
        String helloWorld = "&#72;&#69;&#76;&#79;&#44;&#32;&#87;&#79;&#82;&#76;&#68;&#33;";

        // both of htmlCodec.decode and decodeSFT should get "HELLO, WORLD!"
        System.out.println(htmlCodec.decode(helloWorld));
        System.out.print('\n');
        System.out.println(decodeSFT(helloWorld));
    }

    @Test
    public void test() throws org.sat4j.specs.TimeoutException {
        // test all 2-digit html codes
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                String input = "&#" + i + j + ";";
                assertEquals(htmlCodec.decode(input), decodeSFT(input));
            }
        }

        // other inputs
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                String input = "&" + i + j + "#;";
                assertEquals(htmlCodec.decode(input), decodeSFT(input));
            }
        }
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                String input = "#&" + i + j + ";";
                assertEquals(htmlCodec.decode(input), decodeSFT(input));
            }
        }

    }
}