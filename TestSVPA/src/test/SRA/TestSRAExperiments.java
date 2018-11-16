package test.SRA;

import automata.sra.*;
import automata.sfa.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Test;
import org.sat4j.specs.TimeoutException;
import theory.characters.CharPred;
import theory.characters.StdCharPred;
import theory.intervals.UnaryCharIntervalSolver;

import java.sql.Time;
import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestSRAExperiments {

    @Test
    public void testSSNParserSRA() throws TimeoutException {
        assertTrue(SSNParser.accepts(validName1, ba));
        assertTrue(SSNParser.accepts(validName2, ba));
        assertFalse(SSNParser.accepts(invalidName1, ba));
        assertFalse(SSNParser.accepts(invalidName2, ba));
        assertFalse(SSNParser.accepts(invalidName3, ba));
    }

    @Test
    public void testSSNSimulation() throws TimeoutException {
        assertTrue(SRA.canSimulate(SSNParser, SSNParserFirst, ba, false, Long.MAX_VALUE));
        assertTrue(SRA.canSimulate(SSNParser, SSNParserLast, ba, false, Long.MAX_VALUE));
    }

    @Test
    public void testSSNSFA() throws TimeoutException {
        assertTrue(SSNParserSFA.accepts(validName1, ba));
        assertTrue(SSNParserSFA.accepts(validName2, ba));
        assertFalse(SSNParserSFA.accepts(invalidName1, ba));
        assertFalse(SSNParserSFA.accepts(invalidName2, ba));
        assertFalse(SSNParserSFA.accepts(invalidName3, ba));
    }

    @Test
    public void testSSNFirstSFA() throws TimeoutException {
        assertTrue(SSNParserFirstSFA.accepts(validName1, ba));
        assertTrue(SSNParserFirstSFA.accepts(validName2, ba));
    }

    @Test
    public void testSSNLastSFA() throws TimeoutException {
        assertTrue(SSNParserLastSFA.accepts(validName1, ba));
        assertTrue(SSNParserLastSFA.accepts(validName2, ba));
    }

    @Test
    public void testSSNInclusion() throws TimeoutException {
        assertTrue(SSNParserFirst.languageIncludes(SSNParser, ba, Long.MAX_VALUE));
        assertTrue(SSNParserLast.languageIncludes(SSNParser, ba, Long.MAX_VALUE));
    }

    @Test
    public void testSSNEquivalence() throws TimeoutException {
        assertTrue(SSNParserFirst.isLanguageEquivalent(SSNParserFirst, ba, Long.MAX_VALUE));
        assertTrue(SSNParserLast.isLanguageEquivalent(SSNParserLast, ba, Long.MAX_VALUE));
    }

    @Test
    public void testSSNParserMSRAtoSRA() throws TimeoutException {
        SRA<CharPred, Character> toSRA = SSNParser.toSingleValuedSRA(ba, Long.MAX_VALUE);
        assertTrue(toSRA.accepts(validName1, ba));
        assertTrue(toSRA.accepts(validName2, ba));
        assertFalse(toSRA.accepts(invalidName1, ba));
        assertFalse(toSRA.accepts(invalidName2, ba));
        assertFalse(toSRA.accepts(invalidName3, ba));
    }

    @Test
    public void testXMLParserSRA() throws TimeoutException {
        boolean check = XMLParserSRA.createDotFile("xml", "");
        assertTrue(check);
        assertTrue(XMLParserSRA.accepts(validXML1, ba));
        assertTrue(XMLParserSRA.accepts(validXML2, ba));
        assertTrue(XMLParserSRA.accepts(validXML3, ba));
        assertTrue(XMLParserSRA.accepts(validXML4, ba));
        assertTrue(XMLParserSRA.accepts(validXML5, ba));
        assertTrue(XMLParserSRA.accepts(validXML6, ba));
        assertTrue(XMLParserSRA.accepts(validXML7, ba));
        assertFalse(XMLParserSRA.accepts(invalidXML1, ba));
        assertFalse(XMLParserSRA.accepts(invalidXML2, ba));
        assertFalse(XMLParserSRA.accepts(invalidXML3, ba));
        assertFalse(XMLParserSRA.accepts(invalidXML4, ba));
        assertFalse(XMLParserSRA.accepts(invalidXML5, ba));
        assertFalse(XMLParserSRA.accepts(invalidXML6, ba));
        assertFalse(XMLParserSRA.accepts(invalidXML7, ba));
    }

    @Test
    public void testIPPacketParserSRASingleValued() throws TimeoutException {
        SRA<CharPred, Character> IP2PacketParserSRASS = IP2PacketParserSRA.toSingleValuedSRA(ba, Long.MAX_VALUE);
        SRA<CharPred, Character> IP3PacketParserSRASS = IP3PacketParserSRA.toSingleValuedSRA(ba, Long.MAX_VALUE);
        SRA<CharPred, Character> IP4PacketParserSRASS = IP4PacketParserSRA.toSingleValuedSRA(ba, Long.MAX_VALUE);


        assertTrue(IP3PacketParserSRASS.accepts(validIPPacket1, ba));
        assertTrue(IP4PacketParserSRASS.accepts(validIPPacket1, ba));
        assertTrue(IP2PacketParserSRASS.accepts(validIPPacket2, ba));
        assertTrue(IP3PacketParserSRASS.accepts(validIPPacket2, ba));
        assertTrue(IP4PacketParserSRASS.accepts(validIPPacket2, ba));
        assertTrue(IP2PacketParserSRASS.accepts(validIPPacket3, ba));
        assertTrue(IP3PacketParserSRASS.accepts(validIPPacket3, ba));
        assertTrue(IP4PacketParserSRASS.accepts(validIPPacket3, ba));
        assertFalse(IP2PacketParserSRASS.accepts(invalidIPPacket1, ba));
        assertFalse(IP3PacketParserSRASS.accepts(invalidIPPacket1, ba));
        assertFalse(IP4PacketParserSRASS.accepts(invalidIPPacket1, ba));
        assertFalse(IP2PacketParserSRASS.accepts(invalidIPPacket2, ba));
        assertFalse(IP3PacketParserSRASS.accepts(invalidIPPacket2, ba));
        assertFalse(IP4PacketParserSRASS.accepts(invalidIPPacket2, ba));
        assertFalse(IP2PacketParserSRASS.accepts(invalidIPPacket3, ba));
        assertFalse(IP3PacketParserSRASS.accepts(invalidIPPacket3, ba));
        assertFalse(IP4PacketParserSRASS.accepts(invalidIPPacket3, ba));
        assertTrue(IP2PacketParserSRASS.accepts(dependentIPPacket1, ba));
        assertTrue(IP3PacketParserSRASS.accepts(dependentIPPacket1, ba));
        assertTrue(IP4PacketParserSRASS.accepts(dependentIPPacket1, ba));

    }

    @Test
    public void testIPPacketParserSRASingleValuedComplete() throws TimeoutException {
        SRA<CharPred, Character> IP2PacketParserSRASS = IP2PacketParserSRA.toSingleValuedSRA(ba, Long.MAX_VALUE);
        SRA<CharPred, Character> IP3PacketParserSRASS = IP3PacketParserSRA.toSingleValuedSRA(ba, Long.MAX_VALUE);
        SRA<CharPred, Character> IP4PacketParserSRASS = IP4PacketParserSRA.toSingleValuedSRA(ba, Long.MAX_VALUE);

        IP2PacketParserSRASS.complete(ba);
        IP3PacketParserSRASS.complete(ba);
        IP4PacketParserSRASS.complete(ba);

        assertTrue(IP3PacketParserSRASS.accepts(validIPPacket1, ba));
        assertTrue(IP4PacketParserSRASS.accepts(validIPPacket1, ba));
        assertTrue(IP2PacketParserSRASS.accepts(validIPPacket2, ba));
        assertTrue(IP3PacketParserSRASS.accepts(validIPPacket2, ba));
        assertTrue(IP4PacketParserSRASS.accepts(validIPPacket2, ba));
        assertTrue(IP2PacketParserSRASS.accepts(validIPPacket3, ba));
        assertTrue(IP3PacketParserSRASS.accepts(validIPPacket3, ba));
        assertTrue(IP4PacketParserSRASS.accepts(validIPPacket3, ba));
        assertFalse(IP2PacketParserSRASS.accepts(invalidIPPacket1, ba));
        assertFalse(IP3PacketParserSRASS.accepts(invalidIPPacket1, ba));
        assertFalse(IP4PacketParserSRASS.accepts(invalidIPPacket1, ba));
        assertFalse(IP2PacketParserSRASS.accepts(invalidIPPacket2, ba));
        assertFalse(IP3PacketParserSRASS.accepts(invalidIPPacket2, ba));
        assertFalse(IP4PacketParserSRASS.accepts(invalidIPPacket2, ba));
        assertFalse(IP2PacketParserSRASS.accepts(invalidIPPacket3, ba));
        assertFalse(IP3PacketParserSRASS.accepts(invalidIPPacket3, ba));
        assertFalse(IP4PacketParserSRASS.accepts(invalidIPPacket3, ba));
        assertTrue(IP2PacketParserSRASS.accepts(dependentIPPacket1, ba));
        assertTrue(IP3PacketParserSRASS.accepts(dependentIPPacket1, ba));
        assertTrue(IP4PacketParserSRASS.accepts(dependentIPPacket1, ba));
    }

    @Test
    public void testIPPacketParserSRA() throws TimeoutException {
        assertTrue(IP2PacketParserSRA.accepts(validIPPacket1, ba));
        assertTrue(IP3PacketParserSRA.accepts(validIPPacket1, ba));
        assertTrue(IP4PacketParserSRA.accepts(validIPPacket1, ba));
        assertTrue(IP6PacketParserSRA.accepts(validIPPacket1, ba));
        assertTrue(IP9PacketParserSRA.accepts(validIPPacket1, ba));
        assertTrue(IP2PacketParserSRA.accepts(validIPPacket2, ba));
        assertTrue(IP3PacketParserSRA.accepts(validIPPacket2, ba));
        assertTrue(IP4PacketParserSRA.accepts(validIPPacket2, ba));
        assertTrue(IP6PacketParserSRA.accepts(validIPPacket2, ba));
        assertTrue(IP9PacketParserSRA.accepts(validIPPacket2, ba));
        assertTrue(IP2PacketParserSRA.accepts(validIPPacket3, ba));
        assertTrue(IP3PacketParserSRA.accepts(validIPPacket3, ba));
        assertTrue(IP4PacketParserSRA.accepts(validIPPacket3, ba));
        assertTrue(IP6PacketParserSRA.accepts(validIPPacket3, ba));
        assertTrue(IP9PacketParserSRA.accepts(validIPPacket3, ba));
        assertFalse(IP2PacketParserSRA.accepts(invalidIPPacket1, ba));
        assertFalse(IP3PacketParserSRA.accepts(invalidIPPacket1, ba));
        assertFalse(IP4PacketParserSRA.accepts(invalidIPPacket1, ba));
        assertFalse(IP6PacketParserSRA.accepts(invalidIPPacket1, ba));
        assertFalse(IP9PacketParserSRA.accepts(invalidIPPacket1, ba));
        assertFalse(IP2PacketParserSRA.accepts(invalidIPPacket2, ba));
        assertFalse(IP3PacketParserSRA.accepts(invalidIPPacket2, ba));
        assertFalse(IP4PacketParserSRA.accepts(invalidIPPacket2, ba));
        assertFalse(IP6PacketParserSRA.accepts(invalidIPPacket2, ba));
        assertFalse(IP9PacketParserSRA.accepts(invalidIPPacket2, ba));
        assertFalse(IP2PacketParserSRA.accepts(invalidIPPacket3, ba));
        assertFalse(IP3PacketParserSRA.accepts(invalidIPPacket3, ba));
        assertFalse(IP4PacketParserSRA.accepts(invalidIPPacket3, ba));
        assertFalse(IP6PacketParserSRA.accepts(invalidIPPacket3, ba));
        assertFalse(IP9PacketParserSRA.accepts(invalidIPPacket3, ba));
        assertTrue(IP2PacketParserSRA.accepts(dependentIPPacket1, ba));
        assertTrue(IP3PacketParserSRA.accepts(dependentIPPacket1, ba));
        assertTrue(IP4PacketParserSRA.accepts(dependentIPPacket1, ba));
        assertTrue(IP6PacketParserSRA.accepts(dependentIPPacket1, ba));
        assertFalse(IP9PacketParserSRA.accepts(dependentIPPacket1, ba));
    }

//    @Test
//    public void testPPEquivalence() throws TimeoutException {
//        assertFalse(productParserC4.isLanguageEquivalent(productParserCL4, ba, Long.MAX_VALUE));
//    }

//    @Test
//    public void testPPC2Emptiness() throws TimeoutException {
//        assertFalse(SRA.isLanguageEmpty(productParserC6, ba, Long.MAX_VALUE));
//    }

//    @Test
//    public void testPPInclusion() throws TimeoutException {
//        assertFalse(productParserCL4.languageIncludes(productParserC4, ba, Long.MAX_VALUE));
//    }
//
//    @Test
//    public void testPPCL4Inclusion() throws TimeoutException {
//        assertTrue(productParserC9.languageIncludes(productParserCL9, ba, Long.MAX_VALUE));
//    }
//
//    @Test
//    public void testPPCL6Inclusion() throws TimeoutException {
//        assertTrue(productParserC6.languageIncludes(productParserCL6, ba, Long.MAX_VALUE));
//    }
//
//    @Test
//    public void testPPCL9Inclusion() throws TimeoutException {
//        assertTrue(productParserC9.languageIncludes(productParserCL9, ba, Long.MAX_VALUE));
//    }
//
//    @Test
//    public void testPPCL2Equivalence() throws TimeoutException {
//        assertTrue(productParserCL2.isLanguageEquivalent(productParserCL2, ba, Long.MAX_VALUE));
//    }
//
//    @Test
//    public void testPPCL3Equivalence() throws TimeoutException {
//        assertTrue(productParserCL3.isLanguageEquivalent(productParserCL3, ba, Long.MAX_VALUE));
//    }
//    @Test
//    public void testPPCL4Equivalence() throws TimeoutException {
//        assertTrue(productParserCL4.isLanguageEquivalent(productParserCL4, ba, Long.MAX_VALUE));
//    }
//
//    @Test
//    public void testPPCL6Equivalence() throws TimeoutException {
//        assertTrue(productParserCL6.isLanguageEquivalent(productParserCL6, ba, Long.MAX_VALUE));
//    }
//    @Test
//    public void testPPCL9Equivalence() throws TimeoutException {
//        assertTrue(productParserCL9.isLanguageEquivalent(productParserCL9, ba, Long.MAX_VALUE));
//    }

//    @Test
//    public void testPPAcceptance() throws TimeoutException {
//        assertTrue(productParserC2.accepts(valid2PP, ba));
//        assertTrue(productParserC2.accepts(valid2PP10, ba));
//        assertTrue(productParserC2.accepts(valid2PP100000, ba));
//        assertTrue(productParserC2.accepts(valid2PP1000000, ba));
//        assertTrue(productParserC2.accepts(valid2PP10000000, ba));
//        assertTrue(productParserCL2.accepts(valid2PP, ba));
//        assertTrue(productParserCL2.accepts(valid2PP10, ba));
//        assertTrue(productParserCL2.accepts(valid2PP100000, ba));
//        assertTrue(productParserCL2.accepts(valid2PP1000000, ba));
//        assertTrue(productParserCL2.accepts(valid2PP10000000, ba));
//        assertTrue(productParserC3.accepts(valid3PP, ba));
//        assertTrue(productParserC3.accepts(valid3PP10, ba));
//        assertTrue(productParserC3.accepts(valid3PP100000, ba));
//        assertTrue(productParserC3.accepts(valid3PP1000000, ba));
//        assertTrue(productParserC3.accepts(valid3PP10000000, ba));
//        assertTrue(productParserCL3.accepts(valid3PP, ba));
//        assertTrue(productParserCL3.accepts(valid3PP10, ba));
//        assertTrue(productParserCL3.accepts(valid3PP100000, ba));
//        assertTrue(productParserCL3.accepts(valid3PP1000000, ba));
//        assertTrue(productParserCL3.accepts(valid3PP10000000, ba));
//        assertTrue(productParserC4.accepts(valid4PP, ba));
//        assertTrue(productParserC4.accepts(valid4PP10, ba));
//        assertTrue(productParserC4.accepts(valid4PP100000, ba));
//        assertTrue(productParserC4.accepts(valid4PP1000000, ba));
//        assertTrue(productParserC4.accepts(valid4PP10000000, ba));
//        assertTrue(productParserCL4.accepts(valid4PP, ba));
//        assertTrue(productParserCL4.accepts(valid4PP10, ba));
//        assertTrue(productParserCL4.accepts(valid4PP100000, ba));
//        assertTrue(productParserCL4.accepts(valid4PP1000000, ba));
//        assertTrue(productParserCL4.accepts(valid4PP10000000, ba));
//        assertTrue(productParserC6.accepts(valid6PP, ba));
//        assertTrue(productParserC6.accepts(valid6PP10, ba));
//        assertTrue(productParserC6.accepts(valid6PP100000, ba));
//        assertTrue(productParserC6.accepts(valid6PP1000000, ba));
//        assertTrue(productParserC6.accepts(valid6PP10000000, ba));
//        assertTrue(productParserCL6.accepts(valid6PP, ba));
//        assertTrue(productParserCL6.accepts(valid6PP10, ba));
//        assertTrue(productParserCL6.accepts(valid6PP100000, ba));
//        assertTrue(productParserCL6.accepts(valid6PP1000000, ba));
//        assertTrue(productParserCL6.accepts(valid6PP10000000, ba));
//        assertTrue(productParserC9.accepts(valid9PP, ba));
//        assertTrue(productParserC9.accepts(valid9PP10, ba));
//        assertTrue(productParserC9.accepts(valid9PP100000, ba));
//        assertTrue(productParserC9.accepts(valid9PP1000000, ba));
//        assertTrue(productParserC9.accepts(valid9PP10000000, ba));
//        assertTrue(productParserCL9.accepts(valid9PP, ba));
//        assertTrue(productParserCL9.accepts(valid9PP10, ba));
//        assertTrue(productParserCL9.accepts(valid9PP100000, ba));
//        assertTrue(productParserCL9.accepts(valid9PP1000000, ba));
//        assertTrue(productParserCL9.accepts(valid9PP10000000, ba));
//    }

//    @Test
//    public void testSFAInclusion3In2() throws TimeoutException {
//        assertTrue(IP3PacketParserSimplifiedSFA.intersectionWith(IP2PacketParserSimplifiedSFA.complement(ba), ba).isEmpty());
//    }
//
//    @Test
//    public void testSFAEquivalence3In2() throws TimeoutException {
//
//        assertFalse(IP2PacketParserSimplifiedSFA.isHopcroftKarpEquivalentTo(IP3PacketParserSimplifiedSFA, ba));
//    }
//
//    @Test
//    public void testSRAEmptiness() throws TimeoutException {
//        assertFalse(SRA.isLanguageEmpty(productParserCL4, ba, Long.MAX_VALUE));
//    }
//
//    @Test
//    public void testSFAEquivalence4In3() throws TimeoutException {
//        assertFalse(IP3PacketParserSimplifiedSFA.isHopcroftKarpEquivalentTo(IP4PacketParserSimplifiedSFA, ba));
//    }
//
//    @Test
//    public void testSFAInclusion4In2() throws TimeoutException {
//        assertTrue(IP4PacketParserSimplifiedSFA.intersectionWith(IP2PacketParserSimplifiedSFA.complement(ba), ba).isEmpty());
//    }
//
//    @Test
//    public void testSFAEquivalence4In2() throws TimeoutException {
//        assertFalse(IP2PacketParserSimplifiedSFA.isHopcroftKarpEquivalentTo(IP4PacketParserSimplifiedSFA, ba));
//    }
//
//    @Test
//    public void testSRAInclusion3In2() throws TimeoutException {
//        assertTrue(IP2PacketParserSimplifiedSRA.languageIncludes(IP3PacketParserSimplifiedSRA, ba, Long.MAX_VALUE));
//    }
//
//    @Test
//    public void testSRAEquivalence3In2() throws TimeoutException {
//        assertFalse(IP2PacketParserSimplifiedSRA.isLanguageEquivalent(IP3PacketParserSimplifiedSRA, ba, Long.MAX_VALUE));
//    }
//
//    @Test
//    public void testSRAInclusion4In3() throws TimeoutException {
//        assertTrue(IP3PacketParserSimplifiedSRA.languageIncludes(IP4PacketParserSimplifiedSRA, ba, Long.MAX_VALUE));
//    }
//
//    @Test
//    public void testSRAEquivalence4In3() throws TimeoutException {
//        assertFalse(IP3PacketParserSimplifiedSRA.isLanguageEquivalent(IP4PacketParserSimplifiedSRA, ba, Long.MAX_VALUE));
//    }
//
//    @Test
//    public void testSRAInclusion4In2() throws TimeoutException {
//        assertTrue(IP2PacketParserSimplifiedSRA.languageIncludes(IP4PacketParserSimplifiedSRA, ba, Long.MAX_VALUE));
//    }
//
//    @Test
//    public void testSRAEquivalence4In2() throws TimeoutException {
//        assertFalse(IP2PacketParserSimplifiedSRA.isLanguageEquivalent(IP4PacketParserSimplifiedSRA, ba, Long.MAX_VALUE));
//    }
//
//    @Test
//    public void testSRAEquivalence6In4() throws TimeoutException {
//        assertFalse(IP4PacketParserSRA.isLanguageEquivalent(IP6PacketParserSRA, ba, Long.MAX_VALUE));
//    }
//
//    @Test
//    public void testSRAInclusion6In4() throws TimeoutException {
//        assertFalse(IP4PacketParserSRA.languageIncludes(IP6PacketParserSRA, ba, Long.MAX_VALUE));
//    }
//
//    @Test
//    public void testSRAEquivalence9In6() throws TimeoutException {
//        assertFalse(IP6PacketParserSRA.isLanguageEquivalent(IP9PacketParserSRA, ba, Long.MAX_VALUE));
//    }
//
//    @Test
//    public void testSRAInclusion9In6() throws TimeoutException {
//        assertFalse(IP6PacketParserSRA.languageIncludes(IP9PacketParserSRA, ba, Long.MAX_VALUE));
//    }

    // ---------------------------------------
    // Predicates
    // ---------------------------------------
    private UnaryCharIntervalSolver ba = new UnaryCharIntervalSolver();
    private CharPred alpha = StdCharPred.ALPHA;
    private CharPred num = StdCharPred.NUM;
    private CharPred alphaNum = StdCharPred.ALPHA_NUM;
    private CharPred lowerAlpha = StdCharPred.LOWER_ALPHA;
    private CharPred upperAlpha = StdCharPred.UPPER_ALPHA;
    private CharPred dot = new CharPred('.');
    private CharPred comma = new CharPred(',');
    private CharPred space = new CharPred(' ');
    private CharPred open = new CharPred('<');
    private CharPred close = new CharPred('>');
    private CharPred slash = new CharPred('/');

    // SSN test strings
    private List<Character> validName1 = lOfS("Tiago, Ferreira, TF"); // accepted by SSNParser
    private List<Character> validName2 = lOfS("Thomas, Thomson, TT"); // accepted by SSNParser
    private List<Character> invalidName1 = lOfS("Tiago, Ferreira, TA"); // not accepted by SSNParser
    private List<Character> invalidName2 = lOfS("Tiago, Ferreira, AA"); // not accepted by SSNParser
    private List<Character> invalidName3 = lOfS("Tiago, Ferreira, A"); // not accepted by SSNParser

    // PP test strings
//    private List<Character> valid2PP = lOfS("C:X4 L:4 D:toothbrush C:X4 L:4 D:toothpaste"); // accepted by PPC2 and PPCL2
//    private List<Character> valid2PP10 = getPPTestStrings("C:X4 L:4 D:toothbrush C:X4 L:4 D:toothpaste", 10);
//    private List<Character> valid2PP100000 = getPPTestStrings("C:X4 L:4 D:toothbrush C:X4 L:4 D:toothpaste", 100000);
//    private List<Character> valid2PP1000000 = getPPTestStrings("C:X4 L:4 D:toothbrush C:X4 L:4 D:toothpaste", 1000000);
//    private List<Character> valid2PP10000000 = getPPTestStrings("C:X4 L:4 D:toothbrush C:X4 L:4 D:toothpaste", 10000000);
//    private List<Character> valid3PP = lOfS("C:X4a L:4 D:toothbrush C:X4a L:4 D:toothpaste"); // accepted by PPC3 and PPCL3
//    private List<Character> valid3PP10 = getPPTestStrings("C:X4a L:4 D:toothbrush C:X4a L:4 D:toothpaste", 10);
//    private List<Character> valid3PP100000 = getPPTestStrings("C:X4a L:4 D:toothbrush C:X4a L:4 D:toothpaste", 100000);
//    private List<Character> valid3PP1000000 = getPPTestStrings("C:X4a L:4 D:toothbrush C:X4a L:4 D:toothpaste", 1000000);
//    private List<Character> valid3PP10000000 = getPPTestStrings("C:X4a L:4 D:toothbrush C:X4a L:4 D:toothpaste", 10000000);
//    private List<Character> valid4PP = lOfS("C:X4aB L:4 D:toothbrush C:X4aB L:4 D:toothpaste"); // accepted by PPC4 and PPCL4
//    private List<Character> valid4PP10 = getPPTestStrings("C:X4aB L:4 D:toothbrush C:X4aB L:4 D:toothpaste", 10);
//    private List<Character> valid4PP100000 = getPPTestStrings("C:X4aB L:4 D:toothbrush C:X4aB L:4 D:toothpaste", 100000);
//    private List<Character> valid4PP1000000 = getPPTestStrings("C:X4aB L:4 D:toothbrush C:X4aB L:4 D:toothpaste", 1000000);
//    private List<Character> valid4PP10000000 = getPPTestStrings("C:X4aB L:4 D:toothbrush C:X4aB L:4 D:toothpaste", 10000000);
//    private List<Character> valid6PP = lOfS("C:X4aB@y L:4 D:toothbrush C:X4aB@y L:4 D:toothpaste"); // accepted by PPC6 and PPCL6
//    private List<Character> valid6PP10 = getPPTestStrings("C:X4aB@y L:4 D:toothbrush C:X4aB@y L:4 D:toothpaste", 10);
//    private List<Character> valid6PP100000 = getPPTestStrings("C:X4aB@y L:4 D:toothbrush C:X4aB@y L:4 D:toothpaste", 100000);
//    private List<Character> valid6PP1000000 = getPPTestStrings("C:X4aB@y L:4 D:toothbrush C:X4aB@y L:4 D:toothpaste", 1000000);
//    private List<Character> valid6PP10000000 = getPPTestStrings("C:X4aB@y L:4 D:toothbrush C:X4aB@y L:4 D:toothpaste", 10000000);
//    private List<Character> valid9PP = lOfS("C:X4aB@y%z[ L:4 D:toothbrush C:X4aB@y%z[ L:4 D:toothpaste"); // accepted by PPC9 and PPCL9
//    private List<Character> valid9PP10 = getPPTestStrings("C:X4aB@y%z[ L:4 D:toothbrush C:X4aB@y%z[ L:4 D:toothpaste", 10);
//    private List<Character> valid9PP100000 = getPPTestStrings("C:X4aB@y%z[ L:4 D:toothbrush C:X4aB@y%z[ L:4 D:toothpaste", 100000);
//    private List<Character> valid9PP1000000 = getPPTestStrings("C:X4aB@y%z[ L:4 D:toothbrush C:X4aB@y%z[ L:4 D:toothpaste", 1000000);
//    private List<Character> valid9PP10000000 = getPPTestStrings("C:X4aB@y%z[ L:4 D:toothbrush C:X4aB@y%z[ L:4 D:toothpaste", 10000000);

    // XML test strings
    private List<Character> validXML1 = lOfS("<A></A>"); // accepted by XMLParser
    private List<Character> validXML2 = lOfS("<AB></AB>"); // accepted by XMLParser
    private List<Character> validXML3 = lOfS("<BB></BB>"); // accepted by XMLParser
    private List<Character> validXML4 = lOfS("<ABB></ABB>"); // accepted by XMLParser
    private List<Character> validXML5 = lOfS("<ABA></ABA>"); // accepted by XMLParser
    private List<Character> validXML6 = lOfS("<AAB></AAB>"); // accepted by XMLParser
    private List<Character> validXML7 = lOfS("<ABC></ABC>"); // accepted by XMLParser
    private List<Character> invalidXML1 = lOfS("<A></>"); // not accepted by XMLParser
    private List<Character> invalidXML2 = lOfS("<A><AB/>"); // not accepted by XMLParser
    private List<Character> invalidXML3 = lOfS("<></A>"); // not accepted by XMLParser
    private List<Character> invalidXML4 = lOfS("<A><A>"); // not accepted by XMLParser
    private List<Character> invalidXML5 = lOfS("<AA></AB>"); // not accepted by XMLParser
    private List<Character> invalidXML6 = lOfS("<AAA></CCC>"); // not accepted by XMLParser
    private List<Character> invalidXML7 = lOfS("<ABA></ABC>"); // not accepted by XMLParser

    // IP Packet test strings
    private List<Character> validIPPacket1 = lOfS("srcip:192.168.123.192 prt:40 dstip:192.168.123.224 prt:50 pload:'hello'"); // accepted by IP9PacketParser
    private List<Character> validIPPacketSimplified1 = lOfS("s:192.168.123.192 p:40 d:192.168.123.224 p:50 p:'hello'"); // accepted by IP2PacketParserSimplified
    private List<Character> validIPPacket2 = lOfS("srcip:192.168.123.122 prt:40 dstip:192.168.123.124 prt:5 pload:'hello123'"); // accepted by IP9PacketParser
    private List<Character> validIPPacket3 = lOfS("srcip:192.148.123.122 prt:40 dstip:192.148.123.124 prt:5 pload:'hello123'"); // accepted by IP9PacketParser
    private List<Character> invalidIPPacket1 = lOfS("srcip:12.168.123.122 prt:40 dstip:192.168.123.124 prt:5 pload:'hello123'"); // not accepted by either
    private List<Character> invalidIPPacket2 = lOfS("srcip:192.148.123.122 prt:40 dstip:192.148.123.124 prt:5 plad:'hello123'"); // not accepted by either
    private List<Character> invalidIPPacket3 = lOfS("srcip:192.148.123.122 dstip:192.148.123.124 prt:5 pload:'hello123'"); // not accepted by either
    private List<Character> dependentIPPacket1 = lOfS("srcip:192.148.125.122 prt:40 dstip:192.148.123.124 prt:5 pload:'hello123'"); // accepted by IP6PacketParser but not IP9PacketParser

    // Automata
    private SRA<CharPred, Character> SSNParser = getSSNParser(ba);
    private SFA<CharPred, Character> SSNParserSFA = getSSNParserSFA(ba);
    private SRA<CharPred, Character> SSNParserFirst = getSSNParserFirst(ba);
    private SFA<CharPred, Character> SSNParserFirstSFA = getSSNParserFirstSFA(ba);
    private SRA<CharPred, Character> SSNParserLast = getSSNParserLast(ba);
    private SFA<CharPred, Character> SSNParserLastSFA = getSSNParserLastSFA(ba);
    private SRA<CharPred, Character> XMLParserSRA = getXMLParserSRA(ba);
    private SRA<CharPred, Character> productParserC2 = getProductParserC2(ba);
    private SRA<CharPred, Character> productParserCL2 = getProductParserCL2(ba);
    private SRA<CharPred, Character> productParserC3 = getProductParserC3(ba);
    private SRA<CharPred, Character> productParserCL3 = getProductParserCL3(ba);
    private SRA<CharPred, Character> productParserC4 = getProductParserC4(ba);
    private SRA<CharPred, Character> productParserCL4 = getProductParserCL4(ba);
    private SRA<CharPred, Character> productParserC6 = getProductParserC6(ba);
    private SRA<CharPred, Character> productParserCL6 = getProductParserCL6(ba);
    private SRA<CharPred, Character> productParserC9 = getProductParserC9(ba);
    private SRA<CharPred, Character> productParserCL9 = getProductParserCL9(ba);
//    private SFA<CharPred, Character> productParserSFA = getProductParserSFA(ba); // Intractable.
    private SRA<CharPred, Character> IP2PacketParserSRA = getIP2PacketParserSRA(ba);
    private SRA<CharPred, Character> IP2PacketParserSimplifiedSRA = getIP2PacketParserSimplifiedSRA(ba);
    private SFA<CharPred, Character> IP2PacketParserSimplifiedSFA = getIP2PacketParserSimplifiedSFA(ba);
    private SRA<CharPred, Character> IP3PacketParserSRA = getIP3PacketParserSRA(ba);
    private SRA<CharPred, Character> IP3PacketParserSimplifiedSRA = getIP3PacketParserSimplifiedSRA(ba);
    private SFA<CharPred, Character> IP3PacketParserSimplifiedSFA = getIP3PacketParserSimplifiedSFA(ba);
    private SRA<CharPred, Character> IP4PacketParserSRA = getIP4PacketParserSRA(ba);
    private SRA<CharPred, Character> IP4PacketParserSimplifiedSRA = getIP4PacketParserSimplifiedSRA(ba);
    private SFA<CharPred, Character> IP4PacketParserSimplifiedSFA = getIP4PacketParserSimplifiedSFA(ba);
    private SRA<CharPred, Character> IP6PacketParserSRA = getIP6PacketParserSRA(ba);
    private SRA<CharPred, Character> IP6PacketParserSimplifiedSRA = getIP6PacketParserSimplifiedSRA(ba);
    private SRA<CharPred, Character> IP9PacketParserSRA = getIP9PacketParserSRA(ba);
    private SRA<CharPred, Character> IP9PacketParserSimplifiedSRA = getIP9PacketParserSimplifiedSRA(ba);

	private SRA<CharPred, Character> getSSNParser(UnaryCharIntervalSolver ba) {
		LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null));

		Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();
		// Read first initial and store it in register 0
		transitions.add(new SRAStoreMove<CharPred, Character>(0, 1, upperAlpha, 0));

		// Read an unbound number of lowercase letters for the rest of the first name.
        // Dispose of them on the dummy register (2)
        transitions.add(new SRAStoreMove<CharPred, Character>(1, 1, lowerAlpha, 2));

        // Read a comma and dispose of it on register (2)
        transitions.add(new SRAStoreMove<CharPred, Character>(1, 2, comma, 2));

        // Read an unbound number of spaces.
        transitions.add(new SRAStoreMove<CharPred, Character>(2, 2, space, 2));

        // Read the second initial and store it in register 1
        transitions.add(new SRAStoreMove<CharPred, Character>(2, 3, upperAlpha, 1));

        // Read an unbound number of lowercase letters for the rest of the last name.
        // Dispose of them on the dummy register (2)
        transitions.add(new SRAStoreMove<CharPred, Character>(3, 3, lowerAlpha, 2));

        // Read a comma and dispose of it on register (2)
        transitions.add(new SRAStoreMove<CharPred, Character>(3, 4, comma, 2));

        // Read an unbound number of spaces.
        transitions.add(new SRAStoreMove<CharPred, Character>(4, 4, space, 2));

        // Read the first initial and compare it to register 0
        transitions.add(new SRACheckMove<CharPred, Character>(4, 5, upperAlpha, 0));

        // Read the second initial and compare it to register 1
        transitions.add(new SRACheckMove<CharPred, Character>(5, 6, upperAlpha, 1));

		try {
			return SRA.MkSRA(transitions, 0, Arrays.asList(6), registers, ba);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

    private SFA<CharPred, Character> getSSNParserSFA(UnaryCharIntervalSolver ba) {
        Collection<SFAMove<CharPred, Character>> transitions = new LinkedList<SFAMove<CharPred, Character>>();
        List<Integer> finalStates = new LinkedList<Integer>();

        Character firstInitial = upperAlpha.intervals.get(0).left;
        Character firstEnd = upperAlpha.intervals.get(0).right;
        for (int firstCounter = 0; firstInitial < firstEnd; firstCounter++, firstInitial++) {
            transitions.add(new SFAInputMove<CharPred, Character>(0, (firstCounter * 185) + 1, new CharPred(firstInitial)));
            transitions.add(new SFAInputMove<CharPred, Character>((firstCounter * 185) + 1, (firstCounter * 185) + 1, lowerAlpha));
            transitions.add(new SFAInputMove<CharPred, Character>((firstCounter * 185) + 1, (firstCounter * 185) + 2, comma));
            transitions.add(new SFAInputMove<CharPred, Character>((firstCounter * 185) + 2, (firstCounter * 185) + 3, space));
            transitions.add(new SFAInputMove<CharPred, Character>((firstCounter * 185) + 3, (firstCounter * 185) + 3, space));

            Character secondInitial = upperAlpha.intervals.get(0).left;
            Character secondEnd = upperAlpha.intervals.get(0).right;
            for (int secondCounter = 0; secondInitial < secondEnd; secondCounter++, secondInitial++) {
                transitions.add(new SFAInputMove<CharPred, Character>((firstCounter * 185) + 3, (firstCounter * 185) + (secondCounter * 7) + 4, new CharPred(secondInitial)));
                transitions.add(new SFAInputMove<CharPred, Character>((firstCounter * 185) + (secondCounter * 7) + 4, (firstCounter * 185) + (secondCounter * 7) + 4, lowerAlpha));
                transitions.add(new SFAInputMove<CharPred, Character>((firstCounter * 185) + (secondCounter * 7) + 4, (firstCounter * 185) + (secondCounter * 7) + 5, comma));
                transitions.add(new SFAInputMove<CharPred, Character>((firstCounter * 185) + (secondCounter * 7) + 5, (firstCounter * 185) + (secondCounter * 7) + 6, space));
                transitions.add(new SFAInputMove<CharPred, Character>((firstCounter * 185) + (secondCounter * 7) + 6, (firstCounter * 185) + (secondCounter * 7) + 6, space));
                transitions.add(new SFAInputMove<CharPred, Character>((firstCounter * 185) + (secondCounter * 7) + 6, (firstCounter * 185) + (secondCounter * 7) + 7, new CharPred(firstInitial)));
                transitions.add(new SFAInputMove<CharPred, Character>((firstCounter * 185) + (secondCounter * 7) + 7, (firstCounter * 185) + (secondCounter * 7) + 8, new CharPred(secondInitial)));
                finalStates.add((firstCounter * 185) + (secondCounter * 7) + 8);
            }
        }

        try {
            return SFA.MkSFA(transitions, 0, finalStates, ba, false, false);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SRA<CharPred, Character> getSSNParserFirst(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null));
        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();

        // Read the first initial and store it in register 0
        transitions.add(new SRAStoreMove<CharPred, Character>(0, 1, upperAlpha, 0));

        // Read an unbound number of lowercase characters
        transitions.add(new SRAStoreMove<CharPred, Character>(1, 1, lowerAlpha, 1));

        // Read a comma
        transitions.add(new SRAStoreMove<CharPred, Character>(1, 2, comma, 1));

        // Read an unbound number of spaces
        transitions.add(new SRAStoreMove<CharPred, Character>(2, 2, space, 1));

        // Read a different second initial or a repeated second initial and store it in 1
        transitions.add(new SRAStoreMove<CharPred, Character>(2, 3, upperAlpha, 1));

        // Read an unbound number of lowercase characters
        transitions.add(new SRAStoreMove<>(3, 3, lowerAlpha,1));

        // Read a comma
        transitions.add(new SRAStoreMove<CharPred, Character>(3, 4, comma, 1));

        // Read an unbound number of spaces
        transitions.add(new SRAStoreMove<CharPred, Character>(4, 4, space, 1));

        // Read a capital that matches both registers or a capital that matches the first register
        transitions.add(new SRACheckMove<CharPred, Character>(4, 5, upperAlpha, 0));

        // Read a second capital
        transitions.add(new SRAStoreMove<CharPred, Character>(5, 6, upperAlpha, 1));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(6), registers, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SFA<CharPred, Character> getSSNParserFirstSFA(UnaryCharIntervalSolver ba) {
        Collection<SFAMove<CharPred, Character>> transitions = new LinkedList<SFAMove<CharPred, Character>>();
        List<Integer> finalStates = new LinkedList<Integer>();

        Character firstInitial = upperAlpha.intervals.get(0).left;
        Character firstEnd = upperAlpha.intervals.get(0).right;
        for (int firstCounter = 0; firstInitial < firstEnd; firstCounter++, firstInitial++) {
            transitions.add(new SFAInputMove<CharPred, Character>(0, (firstCounter * 9) + 1, new CharPred(firstInitial)));
            transitions.add(new SFAInputMove<CharPred, Character>((firstCounter * 9) + 1, (firstCounter * 9) + 1, lowerAlpha));
            transitions.add(new SFAInputMove<CharPred, Character>((firstCounter * 9) + 1, (firstCounter * 9) + 2, comma));
            transitions.add(new SFAInputMove<CharPred, Character>((firstCounter * 9) + 2, (firstCounter * 9) + 3, space));
            transitions.add(new SFAInputMove<CharPred, Character>((firstCounter * 9) + 3, (firstCounter * 9) + 3, space));
            transitions.add(new SFAInputMove<CharPred, Character>((firstCounter * 9) + 3, (firstCounter * 9) + 4, upperAlpha));
            transitions.add(new SFAInputMove<CharPred, Character>((firstCounter * 9) + 4, (firstCounter * 9) + 4, lowerAlpha));
            transitions.add(new SFAInputMove<CharPred, Character>((firstCounter * 9) + 4, (firstCounter * 9) + 5, comma));
            transitions.add(new SFAInputMove<CharPred, Character>((firstCounter * 9) + 5, (firstCounter * 9) + 6, space));
            transitions.add(new SFAInputMove<CharPred, Character>((firstCounter * 9) + 6, (firstCounter * 9) + 6, space));
            transitions.add(new SFAInputMove<CharPred, Character>((firstCounter * 9) + 6, (firstCounter * 9) + 7, new CharPred(firstInitial)));
            transitions.add(new SFAInputMove<CharPred, Character>((firstCounter * 9) + 7, (firstCounter * 9) + 8, upperAlpha));
            finalStates.add((firstCounter * 9) + 8);
        }

        try {
            return SFA.MkSFA(transitions, 0, finalStates, ba, false, false);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SRA<CharPred, Character> getSSNParserLast(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null));
        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();

        // Read the first initial and store it in register 0
        transitions.add(new SRAStoreMove<CharPred, Character>(0, 1, upperAlpha, 1));

        // Read an unbound number of lowercase characters
        transitions.add(new SRAStoreMove<CharPred, Character>(1, 1, lowerAlpha, 1));

        // Read a comma
        transitions.add(new SRAStoreMove<CharPred, Character>(1, 2, comma, 1));

        // Read an unbound number of spaces
        transitions.add(new SRAStoreMove<CharPred, Character>(2, 2, space, 1));

        // Read a different second initial or a repeated second initial and store it in 1
        transitions.add(new SRAStoreMove<CharPred, Character>(2, 3, upperAlpha, 0));

        // Read an unbound number of lowercase characters
        transitions.add(new SRAStoreMove<>(3, 3, lowerAlpha,1));

        // Read a comma
        transitions.add(new SRAStoreMove<CharPred, Character>(3, 4, comma, 1));

        // Read an unbound number of spaces
        transitions.add(new SRAStoreMove<CharPred, Character>(4, 4, space, 1));

        // Read a capital
        transitions.add(new SRAStoreMove<CharPred, Character>(4, 5, upperAlpha, 1));

        // Read a capital that matches both registers or a capital that matches the first register
        transitions.add(new SRACheckMove<CharPred, Character>(5, 6, upperAlpha, 0));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(6), registers, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SFA<CharPred, Character> getSSNParserLastSFA(UnaryCharIntervalSolver ba) {
        Collection<SFAMove<CharPred, Character>> transitions = new LinkedList<SFAMove<CharPred, Character>>();
        List<Integer> finalStates = new LinkedList<Integer>();

        transitions.add(new SFAInputMove<CharPred, Character>(0, 1, upperAlpha));
        transitions.add(new SFAInputMove<CharPred, Character>(1, 1, lowerAlpha));
        transitions.add(new SFAInputMove<CharPred, Character>(1, 2, comma));
        transitions.add(new SFAInputMove<CharPred, Character>(2, 3, space));
        transitions.add(new SFAInputMove<CharPred, Character>(3, 3, space));

        Character secondInitial = upperAlpha.intervals.get(0).left;
        Character secondEnd = upperAlpha.intervals.get(0).right;
        for (int secondCounter = 0; secondInitial < secondEnd; secondCounter++, secondInitial++) {
            transitions.add(new SFAInputMove<CharPred, Character>(3, (secondCounter * 7) + 4, new CharPred(secondInitial)));
            transitions.add(new SFAInputMove<CharPred, Character>((secondCounter * 7) + 4, (secondCounter * 7) + 4, lowerAlpha));
            transitions.add(new SFAInputMove<CharPred, Character>((secondCounter * 7) + 4, (secondCounter * 7) + 5, comma));
            transitions.add(new SFAInputMove<CharPred, Character>((secondCounter * 7) + 5, (secondCounter * 7) + 6, space));
            transitions.add(new SFAInputMove<CharPred, Character>((secondCounter * 7) + 6, (secondCounter * 7) + 6, space));
            transitions.add(new SFAInputMove<CharPred, Character>((secondCounter * 7) + 6, (secondCounter * 7) + 7, upperAlpha));
            transitions.add(new SFAInputMove<CharPred, Character>((secondCounter * 7) + 7, (secondCounter * 7) + 8, new CharPred(secondInitial)));
            finalStates.add((secondCounter * 7) + 8);
        }


        try {
            return SFA.MkSFA(transitions, 0, finalStates, ba, false, false);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SRA<CharPred, Character> getProductParserC2(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null));
        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();
        transitions.add(new SRAStoreMove<CharPred, Character>(0, 1, new CharPred('C'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(1, 2, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(2, 3, ba.MkNot(space), 1));
        transitions.add(new SRAStoreMove<CharPred, Character>(3, 4, ba.MkNot(space), 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(4, 5, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(5, 6, new CharPred('L'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(6, 7, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(7, 8, alphaNum, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(8, 9, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(9, 10, new CharPred('D'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(10, 11, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(11, 12, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(12, 12, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(12, 13, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(13, 14, new CharPred('C'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(14, 15, new CharPred(':'), 0));
        transitions.add(new SRACheckMove<CharPred, Character>(15, 16, ba.MkNot(space), 1));
        transitions.add(new SRACheckMove<CharPred, Character>(16, 17, ba.MkNot(space), 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(17, 18, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(18, 19, new CharPred('L'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(19, 20, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(20, 21, alphaNum, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(21, 22, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(22, 23, new CharPred('D'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(23, 24, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(24, 25, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(25, 25, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(25, 13, space, 0));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(25), registers, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SRA<CharPred, Character> getProductParserCL2(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null, null));
        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();
        transitions.add(new SRAStoreMove<CharPred, Character>(0, 1, new CharPred('C'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(1, 2, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(2, 3, ba.MkNot(space), 1));
        transitions.add(new SRAStoreMove<CharPred, Character>(3, 4, ba.MkNot(space), 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(4, 5, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(5, 6, new CharPred('L'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(6, 7, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(7, 8, alphaNum, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(8, 9, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(9, 10, new CharPred('D'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(10, 11, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(11, 12, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(12, 12, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(12, 13, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(13, 14, new CharPred('C'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(14, 15, new CharPred(':'), 0));
        transitions.add(new SRACheckMove<CharPred, Character>(15, 16, ba.MkNot(space), 1));
        transitions.add(new SRACheckMove<CharPred, Character>(16, 17, ba.MkNot(space), 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(17, 18, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(18, 19, new CharPred('L'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(19, 20, new CharPred(':'), 0));
        transitions.add(new SRACheckMove<CharPred, Character>(20, 21, alphaNum, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(21, 22, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(22, 23, new CharPred('D'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(23, 24, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(24, 25, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(25, 25, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(25, 13, space, 0));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(25), registers, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SRA<CharPred, Character> getProductParserC3(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null, null));
        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();
        transitions.add(new SRAStoreMove<CharPred, Character>(0, 1, new CharPred('C'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(1, 2, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(2, 3, ba.MkNot(space), 1));
        transitions.add(new SRAStoreMove<CharPred, Character>(3, 4, ba.MkNot(space), 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(4, 5, ba.MkNot(space), 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(5, 6, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(6, 7, new CharPred('L'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(7, 8, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(8, 9, alphaNum, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(9, 10, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(10, 11, new CharPred('D'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(11, 12, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(12, 13, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(13, 13, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(13, 14, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(14, 15, new CharPred('C'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(15, 16, new CharPred(':'), 0));
        transitions.add(new SRACheckMove<CharPred, Character>(16, 17, ba.MkNot(space), 1));
        transitions.add(new SRACheckMove<CharPred, Character>(17, 18, ba.MkNot(space), 2));
        transitions.add(new SRACheckMove<CharPred, Character>(18, 19, ba.MkNot(space), 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(19, 20, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(20, 21, new CharPred('L'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(21, 22, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(22, 23, alphaNum, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(23, 24, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(24, 25, new CharPred('D'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(25, 26, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(26, 27, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(27, 27, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(27, 14, space, 0));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(27), registers, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SRA<CharPred, Character> getProductParserCL3(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null, null, null));
        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();
        transitions.add(new SRAStoreMove<CharPred, Character>(0, 1, new CharPred('C'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(1, 2, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(2, 3, ba.MkNot(space), 1));
        transitions.add(new SRAStoreMove<CharPred, Character>(3, 4, ba.MkNot(space), 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(4, 5, ba.MkNot(space), 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(5, 6, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(6, 7, new CharPred('L'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(7, 8, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(8, 9, alphaNum, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(9, 10, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(10, 11, new CharPred('D'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(11, 12, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(12, 13, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(13, 13, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(13, 14, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(14, 15, new CharPred('C'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(15, 16, new CharPred(':'), 0));
        transitions.add(new SRACheckMove<CharPred, Character>(16, 17, ba.MkNot(space), 1));
        transitions.add(new SRACheckMove<CharPred, Character>(17, 18, ba.MkNot(space), 2));
        transitions.add(new SRACheckMove<CharPred, Character>(18, 19, ba.MkNot(space), 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(19, 20, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(20, 21, new CharPred('L'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(21, 22, new CharPred(':'), 0));
        transitions.add(new SRACheckMove<CharPred, Character>(22, 23, alphaNum, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(23, 24, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(24, 25, new CharPred('D'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(25, 26, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(26, 27, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(27, 27, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(27, 14, space, 0));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(27), registers, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SRA<CharPred, Character> getProductParserC4(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null, null, null));
        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();
        transitions.add(new SRAStoreMove<CharPred, Character>(0, 1, new CharPred('C'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(1, 2, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(2, 3, ba.MkNot(space), 1));
        transitions.add(new SRAStoreMove<CharPred, Character>(3, 4, ba.MkNot(space), 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(4, 5, ba.MkNot(space), 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(5, 6, ba.MkNot(space), 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(6, 7, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(7, 8, new CharPred('L'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(8, 9, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(9, 10, alphaNum, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(10, 11, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(11, 12, new CharPred('D'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(12, 13, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(13, 14, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(14, 14, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(14, 15, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(15, 16, new CharPred('C'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(16, 17, new CharPred(':'), 0));
        transitions.add(new SRACheckMove<CharPred, Character>(17, 18, ba.MkNot(space), 1));
        transitions.add(new SRACheckMove<CharPred, Character>(18, 19, ba.MkNot(space), 2));
        transitions.add(new SRACheckMove<CharPred, Character>(19, 20, ba.MkNot(space), 3));
        transitions.add(new SRACheckMove<CharPred, Character>(20, 21, ba.MkNot(space), 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(21, 22, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(22, 23, new CharPred('L'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(23, 24, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(24, 25, alphaNum, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(25, 26, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(26, 27, new CharPred('D'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(27, 28, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(28, 29, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(29, 29, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(29, 15, space, 0));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(29), registers, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SRA<CharPred, Character> getProductParserCL4(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null, null, null, null));
        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();
        transitions.add(new SRAStoreMove<CharPred, Character>(0, 1, new CharPred('C'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(1, 2, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(2, 3, ba.MkNot(space), 1));
        transitions.add(new SRAStoreMove<CharPred, Character>(3, 4, ba.MkNot(space), 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(4, 5, ba.MkNot(space), 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(5, 6, ba.MkNot(space), 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(6, 7, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(7, 8, new CharPred('L'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(8, 9, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(9, 10, alphaNum, 5));
        transitions.add(new SRAStoreMove<CharPred, Character>(10, 11, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(11, 12, new CharPred('D'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(12, 13, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(13, 14, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(14, 14, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(14, 15, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(15, 16, new CharPred('C'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(16, 17, new CharPred(':'), 0));
        transitions.add(new SRACheckMove<CharPred, Character>(17, 18, ba.MkNot(space), 1));
        transitions.add(new SRACheckMove<CharPred, Character>(18, 19, ba.MkNot(space), 2));
        transitions.add(new SRACheckMove<CharPred, Character>(19, 20, ba.MkNot(space), 3));
        transitions.add(new SRACheckMove<CharPred, Character>(20, 21, ba.MkNot(space), 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(21, 22, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(22, 23, new CharPred('L'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(23, 24, new CharPred(':'), 0));
        transitions.add(new SRACheckMove<CharPred, Character>(24, 25, alphaNum, 5));
        transitions.add(new SRAStoreMove<CharPred, Character>(25, 26, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(26, 27, new CharPred('D'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(27, 28, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(28, 29, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(29, 29, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(29, 15, space, 0));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(29), registers, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SRA<CharPred, Character> getProductParserC6(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null, null, null, null, null));
        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();
        transitions.add(new SRAStoreMove<CharPred, Character>(0, 1, new CharPred('C'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(1, 2, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(2, 3, ba.MkNot(space), 1));
        transitions.add(new SRAStoreMove<CharPred, Character>(3, 4, ba.MkNot(space), 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(4, 5, ba.MkNot(space), 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(5, 6, ba.MkNot(space), 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(6, 7, ba.MkNot(space), 5));
        transitions.add(new SRAStoreMove<CharPred, Character>(7, 8, ba.MkNot(space), 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(8, 9, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(9, 10, new CharPred('L'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(10, 11, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(11, 12, alphaNum, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(12, 13, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(13, 14, new CharPred('D'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(14, 15, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(15, 16, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(16, 16, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(16, 17, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(17, 18, new CharPred('C'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(18, 19, new CharPred(':'), 0));
        transitions.add(new SRACheckMove<CharPred, Character>(19, 20, ba.MkNot(space), 1));
        transitions.add(new SRACheckMove<CharPred, Character>(20, 21, ba.MkNot(space), 2));
        transitions.add(new SRACheckMove<CharPred, Character>(21, 22, ba.MkNot(space), 3));
        transitions.add(new SRACheckMove<CharPred, Character>(22, 23, ba.MkNot(space), 4));
        transitions.add(new SRACheckMove<CharPred, Character>(23, 24, ba.MkNot(space), 5));
        transitions.add(new SRACheckMove<CharPred, Character>(24, 25, ba.MkNot(space), 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(25, 26, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(26, 27, new CharPred('L'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(27, 28, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(28, 29, alphaNum, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(29, 30, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(30, 31, new CharPred('D'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(31, 32, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(32, 33, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(33, 33, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(33, 17, space, 0));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(33), registers, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SRA<CharPred, Character> getProductParserCL6(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null, null, null, null, null, null));
        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();
        transitions.add(new SRAStoreMove<CharPred, Character>(0, 1, new CharPred('C'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(1, 2, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(2, 3, ba.MkNot(space), 1));
        transitions.add(new SRAStoreMove<CharPred, Character>(3, 4, ba.MkNot(space), 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(4, 5, ba.MkNot(space), 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(5, 6, ba.MkNot(space), 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(6, 7, ba.MkNot(space), 5));
        transitions.add(new SRAStoreMove<CharPred, Character>(7, 8, ba.MkNot(space), 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(8, 9, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(9, 10, new CharPred('L'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(10, 11, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(11, 12, alphaNum, 7));
        transitions.add(new SRAStoreMove<CharPred, Character>(12, 13, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(13, 14, new CharPred('D'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(14, 15, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(15, 16, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(16, 16, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(16, 17, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(17, 18, new CharPred('C'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(18, 19, new CharPred(':'), 0));
        transitions.add(new SRACheckMove<CharPred, Character>(19, 20, ba.MkNot(space), 1));
        transitions.add(new SRACheckMove<CharPred, Character>(20, 21, ba.MkNot(space), 2));
        transitions.add(new SRACheckMove<CharPred, Character>(21, 22, ba.MkNot(space), 3));
        transitions.add(new SRACheckMove<CharPred, Character>(22, 23, ba.MkNot(space), 4));
        transitions.add(new SRACheckMove<CharPred, Character>(23, 24, ba.MkNot(space), 5));
        transitions.add(new SRACheckMove<CharPred, Character>(24, 25, ba.MkNot(space), 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(25, 26, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(26, 27, new CharPred('L'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(27, 28, new CharPred(':'), 0));
        transitions.add(new SRACheckMove<CharPred, Character>(28, 29, alphaNum, 7));
        transitions.add(new SRAStoreMove<CharPred, Character>(29, 30, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(30, 31, new CharPred('D'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(31, 32, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(32, 33, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(33, 33, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(33, 17, space, 0));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(33), registers, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SRA<CharPred, Character> getProductParserC9(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null, null, null, null, null, null, null, null));
        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();
        transitions.add(new SRAStoreMove<CharPred, Character>(0, 1, new CharPred('C'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(1, 2, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(2, 3, ba.MkNot(space), 1));
        transitions.add(new SRAStoreMove<CharPred, Character>(3, 4, ba.MkNot(space), 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(4, 5, ba.MkNot(space), 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(5, 6, ba.MkNot(space), 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(6, 7, ba.MkNot(space), 5));
        transitions.add(new SRAStoreMove<CharPred, Character>(7, 8, ba.MkNot(space), 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(8, 9, ba.MkNot(space), 7));
        transitions.add(new SRAStoreMove<CharPred, Character>(9, 10, ba.MkNot(space), 8));
        transitions.add(new SRAStoreMove<CharPred, Character>(10, 11, ba.MkNot(space), 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(11, 12, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(12, 13, new CharPred('L'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(13, 14, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(14, 15, alphaNum, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(15, 16, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(16, 17, new CharPred('D'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(17, 18, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(18, 19, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(19, 19, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(19, 20, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(20, 21, new CharPred('C'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(21, 22, new CharPred(':'), 0));
        transitions.add(new SRACheckMove<CharPred, Character>(22, 23, ba.MkNot(space), 1));
        transitions.add(new SRACheckMove<CharPred, Character>(23, 24, ba.MkNot(space), 2));
        transitions.add(new SRACheckMove<CharPred, Character>(24, 25, ba.MkNot(space), 3));
        transitions.add(new SRACheckMove<CharPred, Character>(25, 26, ba.MkNot(space), 4));
        transitions.add(new SRACheckMove<CharPred, Character>(26, 27, ba.MkNot(space), 5));
        transitions.add(new SRACheckMove<CharPred, Character>(27, 28, ba.MkNot(space), 6));
        transitions.add(new SRACheckMove<CharPred, Character>(28, 29, ba.MkNot(space), 7));
        transitions.add(new SRACheckMove<CharPred, Character>(29, 30, ba.MkNot(space), 8));
        transitions.add(new SRACheckMove<CharPred, Character>(30, 31, ba.MkNot(space), 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(31, 32, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(32, 33, new CharPred('L'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(33, 34, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(34, 35, alphaNum, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(35, 36, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(36, 37, new CharPred('D'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(37, 38, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(38, 39, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(39, 39, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(39, 20, space, 0));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(39), registers, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SRA<CharPred, Character> getProductParserCL9(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null, null, null, null, null, null, null, null, null));
        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();
        transitions.add(new SRAStoreMove<CharPred, Character>(0, 1, new CharPred('C'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(1, 2, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(2, 3, ba.MkNot(space), 1));
        transitions.add(new SRAStoreMove<CharPred, Character>(3, 4, ba.MkNot(space), 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(4, 5, ba.MkNot(space), 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(5, 6, ba.MkNot(space), 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(6, 7, ba.MkNot(space), 5));
        transitions.add(new SRAStoreMove<CharPred, Character>(7, 8, ba.MkNot(space), 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(8, 9, ba.MkNot(space), 7));
        transitions.add(new SRAStoreMove<CharPred, Character>(9, 10, ba.MkNot(space), 8));
        transitions.add(new SRAStoreMove<CharPred, Character>(10, 11, ba.MkNot(space), 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(11, 12, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(12, 13, new CharPred('L'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(13, 14, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(14, 15, alphaNum, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(15, 16, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(16, 17, new CharPred('D'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(17, 18, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(18, 19, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(19, 19, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(19, 20, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(20, 21, new CharPred('C'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(21, 22, new CharPred(':'), 0));
        transitions.add(new SRACheckMove<CharPred, Character>(22, 23, ba.MkNot(space), 1));
        transitions.add(new SRACheckMove<CharPred, Character>(23, 24, ba.MkNot(space), 2));
        transitions.add(new SRACheckMove<CharPred, Character>(24, 25, ba.MkNot(space), 3));
        transitions.add(new SRACheckMove<CharPred, Character>(25, 26, ba.MkNot(space), 4));
        transitions.add(new SRACheckMove<CharPred, Character>(26, 27, ba.MkNot(space), 5));
        transitions.add(new SRACheckMove<CharPred, Character>(27, 28, ba.MkNot(space), 6));
        transitions.add(new SRACheckMove<CharPred, Character>(28, 29, ba.MkNot(space), 7));
        transitions.add(new SRACheckMove<CharPred, Character>(29, 30, ba.MkNot(space), 8));
        transitions.add(new SRACheckMove<CharPred, Character>(30, 31, ba.MkNot(space), 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(31, 32, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(32, 33, new CharPred('L'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(33, 34, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(34, 35, alphaNum, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(35, 36, space, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(36, 37, new CharPred('D'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(37, 38, new CharPred(':'), 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(38, 39, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(39, 39, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(39, 20, space, 0));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(39), registers, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private List<Character> getPPTestStrings(String original, int repetitions) {
        int startIndex = original.indexOf(' ', original.indexOf(' ', original.indexOf(' ') + 1) + 1);
        if (startIndex != -1) {
            String repetition = original.substring(startIndex);
            StringBuilder sb = new StringBuilder();
            sb.append(original);
            for (int index = 0; index < repetitions; index++)
                sb.append(repetition);
            return lOfS(sb.toString());
        }
        return null;
    }

    private SFA<CharPred, Character> getProductParserSFA(UnaryCharIntervalSolver ba) {
        Collection<SFAMove<CharPred, Character>> transitions = new LinkedList<SFAMove<CharPred, Character>>();
        transitions.add(new SFAInputMove<CharPred, Character>(0, 1, new CharPred('C')));
        transitions.add(new SFAInputMove<CharPred, Character>(1, 2, new CharPred(':')));
        transitions.add(new SFAInputMove<CharPred, Character>(2, 3, space));
        for (ImmutablePair<Character, Character> firstInterval : ba.MkNot(space).intervals) {
            Character firstStart = firstInterval.left;
            Character firstEnd = firstInterval.right;
            for (int firstCounter = 0; firstStart < firstEnd; firstCounter++, firstStart++) {
                transitions.add(new SFAInputMove<CharPred, Character>(3, 4 + (firstCounter * 4), new CharPred(firstStart)));
                for (ImmutablePair<Character, Character> secondInterval : ba.MkNot(space).intervals) {
                    Character secondStart = secondInterval.left;
                    Character secondEnd = secondInterval.right;
                    for (int secondCounter = 0; secondStart < secondEnd; secondCounter++, secondStart++) {
                        transitions.add(new SFAInputMove<CharPred, Character>(4 + (firstCounter * 4), 4 + (firstCounter * 4) + (secondCounter * 2) + 1, new CharPred(secondStart)));
                        for (ImmutablePair<Character, Character> thirdInterval : ba.MkNot(space).intervals) {
                            Character thirdStart = thirdInterval.left;
                            Character thirdEnd = thirdInterval.right;
                            for (int thirdCounter = 0; thirdStart < thirdEnd; thirdCounter++, thirdStart++) {
                                transitions.add(new SFAInputMove<CharPred, Character>(4 + (firstCounter * 4) + (secondCounter * 2) + 1, 4 + (firstCounter * 4) + (secondCounter * 2) + thirdCounter + 2, new CharPred(thirdStart)));
                            }
                        }
                    }
                }
            }
        }

//        transitions.add(new SRAStoreMove<CharPred, Character>(6, 7, space, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(7, 8, new CharPred('L'), 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(8, 9, new CharPred(':'), 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(9, 10, alphaNum, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(10, 11, space, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(11, 12, new CharPred('D'), 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(12, 13, new CharPred(':'), 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(13, 14, alpha, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(14, 14, alpha, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(14, 15, space, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(15, 16, new CharPred('C'), 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(16, 17, new CharPred(':'), 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(17, 18, space, 3));
//        transitions.add(new SRACheckMove<CharPred, Character>(18, 19, ba.MkNot(space), 0));
//        transitions.add(new SRACheckMove<CharPred, Character>(19, 20, ba.MkNot(space), 1));
//        transitions.add(new SRACheckMove<CharPred, Character>(20, 21, ba.MkNot(space), 2));
//        transitions.add(new SRAStoreMove<CharPred, Character>(21, 22, new CharPred('L'), 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(22, 23, new CharPred(':'), 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(23, 24, alphaNum, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(24, 25, space, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(25, 26, new CharPred('D'), 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(26, 27, new CharPred(':'), 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(27, 28, alpha, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(28, 29, alpha, 3));

        try {
            return SFA.MkSFA(transitions, 0, Collections.singleton(3), ba, false, false);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SRA<CharPred, Character> getSmallXMLParserSRA(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList('<', '>', '/', null, null, null));
        Integer garbageReg = registers.size() - 1;

        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();

        // Read opening character
        transitions.add(new SRACheckMove<CharPred, Character>(0, 1, open, 0));

        // Read tag
        transitions.add(new SRAStoreMove<CharPred, Character>(1, 2, alpha, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(2, 3, alpha, 4));

        // Read closing character
        transitions.add(new SRACheckMove<CharPred, Character>(2, 5, close, 1));
        transitions.add(new SRACheckMove<CharPred, Character>(3, 5, close, 1));
        transitions.add(new SRACheckMove<CharPred, Character>(4, 5, close, 1));

        // Read any content (or not)
        transitions.add(new SRAStoreMove<CharPred, Character>(5, 5, alphaNum, garbageReg));

        // Read opening character and slash
        transitions.add(new SRACheckMove<CharPred, Character>(5, 6, open, 0));
        transitions.add(new SRACheckMove<CharPred, Character>(6, 7, slash, 2));

        // Read repeated tag (AAA, BBB, ...)
        transitions.add(new SRACheckMove<CharPred, Character>(7, 8, alpha, 3));
        transitions.add(new SRACheckMove<CharPred, Character>(8, 9, alpha, 4));

        // Read closing character
        transitions.add(new SRACheckMove<CharPred, Character>(8, 11, close, 1));
        transitions.add(new SRACheckMove<CharPred, Character>(9, 11, close, 1));
        transitions.add(new SRACheckMove<CharPred, Character>(10, 11, close, 1));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(11), registers, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SRA<CharPred, Character> getXMLParserSRA(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null, null));

        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();

        // Read opening character
        transitions.add(new SRAStoreMove<CharPred, Character>(0, 1, open, 3));

        // Read tag
        transitions.add(new SRAStoreMove<CharPred, Character>(1, 2, alpha, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(2, 3, alpha, 1));
        transitions.add(new SRAStoreMove<CharPred, Character>(3, 4, alpha, 2));

        // Read closing character
        transitions.add(new SRAStoreMove<CharPred, Character>(2, 5, close, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(3, 5, close, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(4, 5, close, 3));

        // Read any content (or not)
        transitions.add(new SRAStoreMove<CharPred, Character>(5, 5, alphaNum, 3));

        // Read opening character and slash
        transitions.add(new SRAStoreMove<CharPred, Character>(5, 6, open, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(6, 7, slash, 3));

        // Read repeated tag (AAA, BBB, ...)
        transitions.add(new SRACheckMove<CharPred, Character>(7, 8, alpha, 0));
        transitions.add(new SRACheckMove<CharPred, Character>(8, 9, alpha, 1));
        transitions.add(new SRACheckMove<CharPred, Character>(9, 10, alpha, 2));

        // Read closing character
        transitions.add(new SRAStoreMove<CharPred, Character>(8, 11, close, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(9, 11, close, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(10, 11, close, 3));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(11), registers, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SRA<CharPred, Character> getIP2PacketParserSRA(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null));

        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();

        for (int index = 0; index < "srcip:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index, index + 1, new CharPred("srcip:".charAt(index)), 2));

        transitions.add(new SRAStoreMove<CharPred, Character>(6, 7, num, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(7, 8, num, 1));
        transitions.add(new SRAStoreMove<CharPred, Character>(8, 9, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(9, 10, dot, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(10, 11, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(11, 12, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(12, 13, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(13, 14, dot, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(14, 15, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(15, 16, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(16, 17, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(17, 18, dot, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(18, 19, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(19, 20, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(20, 21, num, 2));

        for (int index = 0; index < " prt:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 21, index + 22, new CharPred(" prt:".charAt(index)), 2));

        transitions.add(new SRAStoreMove<CharPred, Character>(26, 27, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(27, 27, num, 2));

        for (int index = 0; index < " dstip:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 27, index + 28, new CharPred(" dstip:".charAt(index)), 2));

        transitions.add(new SRACheckMove<CharPred, Character>(34, 35, num, 0));
        transitions.add(new SRACheckMove<CharPred, Character>(35, 36, num, 1));
        transitions.add(new SRAStoreMove<CharPred, Character>(36, 37, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(37, 38, dot, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(38, 39, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(39, 40, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(40, 41, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(41, 42, dot, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(42, 43, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(43, 44, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(44, 45, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(45, 46, dot, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(46, 47, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(47, 48, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(48, 49, num, 2));

        for (int index = 0; index < " prt:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 49, index + 50, new CharPred(" prt:".charAt(index)), 2));

        transitions.add(new SRAStoreMove<CharPred, Character>(54, 55, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(55, 55, num, 2));

        for (int index = 0; index < " pload:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 55, index + 56, new CharPred(" pload:".charAt(index)), 2));

        transitions.add(new SRAStoreMove<CharPred, Character>(62, 63, new CharPred('\''), 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(63, 64, alphaNum, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(64, 64, alphaNum, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(64, 65, new CharPred('\''), 2));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(65), registers, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SRA<CharPred, Character> getIP2PacketParserSimplifiedSRA(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null));

        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();

        for (int index = 0; index < "s:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index, index + 1, new CharPred("s:".charAt(index)), 2));

        transitions.add(new SRAStoreMove<CharPred, Character>(2, 3, num, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(3, 4, num, 1));
        transitions.add(new SRAStoreMove<CharPred, Character>(4, 5, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(5, 6, dot, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(6, 7, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(7, 8, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(8, 9, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(9, 10, dot, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(10, 11, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(11, 12, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(12, 13, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(13, 14, dot, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(14, 15, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(15, 16, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(16, 17, num, 2));

        transitions.add(new SRAStoreMove<CharPred, Character>(17, 18, space, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(18, 18, ba.MkOr(new CharPred(':'), alphaNum), 2));

        for (int index = 0; index < " d:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 18, index + 19, new CharPred(" d:".charAt(index)), 2));

        transitions.add(new SRACheckMove<CharPred, Character>(21, 22, num, 0));
        transitions.add(new SRACheckMove<CharPred, Character>(22, 23, num, 1));
        transitions.add(new SRAStoreMove<CharPred, Character>(23, 24, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(24, 25, dot, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(25, 26, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(26, 27, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(27, 28, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(28, 29, dot, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(29, 30, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(30, 31, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(31, 32, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(32, 33, dot, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(33, 34, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(34, 35, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(35, 36, num, 2));

        transitions.add(new SRAStoreMove<CharPred, Character>(36, 37, space, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(37, 37, ba.MkOr(new CharPred(':'), alphaNum), 2));

        for (int index = 0; index < " p:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 37, index + 38, new CharPred(" p:".charAt(index)), 2));

        transitions.add(new SRAStoreMove<CharPred, Character>(40, 41, new CharPred('\''), 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(41, 42, alphaNum, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(42, 42, alphaNum, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(42, 43, new CharPred('\''), 2));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(43), registers, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SFA<CharPred, Character> getIP2PacketParserSimplifiedSFA(UnaryCharIntervalSolver ba) {
        Collection<SFAMove<CharPred, Character>> transitions = new LinkedList<SFAMove<CharPred, Character>>();
        LinkedList<Integer> finalStates = new LinkedList<Integer>();

        transitions.add(new SFAInputMove<CharPred, Character>(0, 1, new CharPred('s')));
        transitions.add(new SFAInputMove<CharPred, Character>(1, 2, new CharPred(':')));

        for (Integer firstDigit = 0; firstDigit < 10; firstDigit++) {
            transitions.add(new SFAInputMove<CharPred, Character>(2, (firstDigit * 401) + 3, new CharPred(firstDigit.toString().charAt(0))));

            for (Integer secondDigit = 0; secondDigit < 10; secondDigit++) {
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401), 3 + (firstDigit * 401) + (secondDigit * 40) + 1, new CharPred(secondDigit.toString().charAt(0))));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 1, 3 + (firstDigit * 401) + (secondDigit * 40) + 2, num));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 2, 3 + (firstDigit * 401) + (secondDigit * 40) + 3, dot));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 3, 3 + (firstDigit * 401) + (secondDigit * 40) + 4, num));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 4, 3 + (firstDigit * 401) + (secondDigit * 40) + 5, num));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 5, 3 + (firstDigit * 401) + (secondDigit * 40) + 6, num));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 6, 3 + (firstDigit * 401) + (secondDigit * 40) + 7, dot));

                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 7, 3 + (firstDigit * 401) + (secondDigit * 40) + 8, num));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 8, 3 + (firstDigit * 401) + (secondDigit * 40) + 9, num));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 9, 3 + (firstDigit * 401) + (secondDigit * 40) + 10, num));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 10, 3 + (firstDigit * 401) + (secondDigit * 40) + 11, dot));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 11, 3 + (firstDigit * 401) + (secondDigit * 40) + 12, num));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 12, 3 + (firstDigit * 401) + (secondDigit * 40) + 13, num));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 13, 3 + (firstDigit * 401) + (secondDigit * 40) + 14, num));

                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 14, 3 + (firstDigit * 401) + (secondDigit * 40) + 15, space));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 15, 3 + (firstDigit * 401) + (secondDigit * 40) + 15, ba.MkOr(new CharPred(':'), alphaNum)));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 15, 3 + (firstDigit * 401) + (secondDigit * 40) + 16, space));

                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 16, 3 + (firstDigit * 401) + (secondDigit * 40) + 17, new CharPred('d')));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 17, 3 + (firstDigit * 401) + (secondDigit * 40) + 18, new CharPred(':')));

                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 18, 3 + (firstDigit * 401) + (secondDigit * 40) + 19, new CharPred(firstDigit.toString().charAt(0))));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 19, 3 + (firstDigit * 401) + (secondDigit * 40) + 20, new CharPred(secondDigit.toString().charAt(0))));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 20, 3 + (firstDigit * 401) + (secondDigit * 40) + 21, num));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 21, 3 + (firstDigit * 401) + (secondDigit * 40) + 22, dot));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 22, 3 + (firstDigit * 401) + (secondDigit * 40) + 23, num));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 23, 3 + (firstDigit * 401) + (secondDigit * 40) + 24, num));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 24, 3 + (firstDigit * 401) + (secondDigit * 40) + 25, num));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 25, 3 + (firstDigit * 401) + (secondDigit * 40) + 26, dot));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 26, 3 + (firstDigit * 401) + (secondDigit * 40) + 27, num));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 27, 3 + (firstDigit * 401) + (secondDigit * 40) + 28, num));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 28, 3 + (firstDigit * 401) + (secondDigit * 40) + 29, num));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 29, 3 + (firstDigit * 401) + (secondDigit * 40) + 30, dot));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 30, 3 + (firstDigit * 401) + (secondDigit * 40) + 31, num));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 31, 3 + (firstDigit * 401) + (secondDigit * 40) + 32, num));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 32, 3 + (firstDigit * 401) + (secondDigit * 40) + 33, num));

                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 33, 3 + (firstDigit * 401) + (secondDigit * 40) + 34, space));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 34, 3 + (firstDigit * 401) + (secondDigit * 40) + 34, ba.MkOr(new CharPred(':'), alphaNum)));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 34, 3 + (firstDigit * 401) + (secondDigit * 40) + 35, space));

                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 35, 3 + (firstDigit * 401) + (secondDigit * 40) + 36, new CharPred('p')));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 36, 3 + (firstDigit * 401) + (secondDigit * 40) + 37, new CharPred(':')));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 37, 3 + (firstDigit * 401) + (secondDigit * 40) + 38, new CharPred('\'')));

                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 38, 3 + (firstDigit * 401) + (secondDigit * 40) + 39, alphaNum));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 39, 3 + (firstDigit * 401) + (secondDigit * 40) + 39, alphaNum));
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 401) + (secondDigit * 40) + 39, 3 + (firstDigit * 401) + (secondDigit * 40) + 40, new CharPred('\'')));
                finalStates.add(3 + (firstDigit * 401) + (secondDigit * 40) + 40);
            }
        }

        try {
            return SFA.MkSFA(transitions, 0,finalStates, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    private SRA<CharPred, Character> getIP3PacketParserSRA(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null, null));

        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();

        for (int index = 0; index < "srcip:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index, index + 1, new CharPred("srcip:".charAt(index)), 3));

        transitions.add(new SRAStoreMove<CharPred, Character>(6, 7, num, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(7, 8, num, 1));
        transitions.add(new SRAStoreMove<CharPred, Character>(8, 9, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(9, 10, dot, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(10, 11, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(11, 12, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(12, 13, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(13, 14, dot, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(14, 15, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(15, 16, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(16, 17, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(17, 18, dot, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(18, 19, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(19, 20, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(20, 21, num, 3));

        for (int index = 0; index < " prt:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 21, index + 22, new CharPred(" prt:".charAt(index)), 3));

        transitions.add(new SRAStoreMove<CharPred, Character>(26, 27, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(27, 27, num, 3));

        for (int index = 0; index < " dstip:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 27, index + 28, new CharPred(" dstip:".charAt(index)), 3));

        transitions.add(new SRACheckMove<CharPred, Character>(34, 35, num, 0));
        transitions.add(new SRACheckMove<CharPred, Character>(35, 36, num, 1));
        transitions.add(new SRACheckMove<CharPred, Character>(36, 37, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(37, 38, dot, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(38, 39, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(39, 40, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(40, 41, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(41, 42, dot, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(42, 43, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(43, 44, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(44, 45, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(45, 46, dot, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(46, 47, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(47, 48, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(48, 49, num, 3));

        for (int index = 0; index < " prt:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 49, index + 50, new CharPred(" prt:".charAt(index)), 3));

        transitions.add(new SRAStoreMove<CharPred, Character>(54, 55, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(55, 55, num, 3));

        for (int index = 0; index < " pload:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 55, index + 56, new CharPred(" pload:".charAt(index)), 3));

        transitions.add(new SRAStoreMove<CharPred, Character>(62, 63, new CharPred('\''), 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(63, 64, alphaNum, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(64, 64, alphaNum, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(64, 65, new CharPred('\''), 3));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(65), registers, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SRA<CharPred, Character> getIP3PacketParserSimplifiedSRA(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null, null));

        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();

        for (int index = 0; index < "s:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index, index + 1, new CharPred("s:".charAt(index)), 3));

        transitions.add(new SRAStoreMove<CharPred, Character>(2, 3, num, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(3, 4, num, 1));
        transitions.add(new SRAStoreMove<CharPred, Character>(4, 5, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(5, 6, dot, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(6, 7, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(7, 8, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(8, 9, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(9, 10, dot, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(10, 11, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(11, 12, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(12, 13, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(13, 14, dot, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(14, 15, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(15, 16, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(16, 17, num, 3));

        transitions.add(new SRAStoreMove<CharPred, Character>(17, 18, space, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(18, 18, ba.MkOr(new CharPred(':'), alphaNum), 3));

        for (int index = 0; index < " d:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 18, index + 19, new CharPred(" d:".charAt(index)), 3));

        transitions.add(new SRACheckMove<CharPred, Character>(21, 22, num, 0));
        transitions.add(new SRACheckMove<CharPred, Character>(22, 23, num, 1));
        transitions.add(new SRACheckMove<CharPred, Character>(23, 24, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(24, 25, dot, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(25, 26, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(26, 27, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(27, 28, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(28, 29, dot, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(29, 30, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(30, 31, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(31, 32, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(32, 33, dot, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(33, 34, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(34, 35, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(35, 36, num, 3));

        transitions.add(new SRAStoreMove<CharPred, Character>(36, 37, space, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(37, 37, ba.MkOr(new CharPred(':'), alphaNum), 3));

        for (int index = 0; index < " p:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 37, index + 38, new CharPred(" p:".charAt(index)), 3));

        transitions.add(new SRAStoreMove<CharPred, Character>(40, 41, new CharPred('\''), 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(41, 42, alphaNum, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(42, 42, alphaNum, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(42, 43, new CharPred('\''), 3));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(43), registers, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SFA<CharPred, Character> getIP3PacketParserSimplifiedSFA(UnaryCharIntervalSolver ba) {
        Collection<SFAMove<CharPred, Character>> transitions = new LinkedList<SFAMove<CharPred, Character>>();
        LinkedList<Integer> finalStates = new LinkedList<Integer>();

        transitions.add(new SFAInputMove<CharPred, Character>(0, 1, new CharPred('s')));
        transitions.add(new SFAInputMove<CharPred, Character>(1, 2, new CharPred(':')));

        for (Integer firstDigit = 0; firstDigit < 10; firstDigit++) {
            transitions.add(new SFAInputMove<CharPred, Character>(2, (firstDigit * 4329) + 3, new CharPred(firstDigit.toString().charAt(0))));

            for (Integer secondDigit = 0; secondDigit < 10; secondDigit++) {
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329), 3 + (firstDigit * 4329) + (secondDigit * 429) + 1, new CharPred(secondDigit.toString().charAt(0))));

                for (Integer thirdDigit = 0; thirdDigit < 10; thirdDigit++) {
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + 1, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 2, new CharPred(thirdDigit.toString().charAt(0))));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 2, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 3, dot));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 3, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 4, num));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 4, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 5, num));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 5, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 6, num));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 6, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 7, dot));

                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 7, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 8, num));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 8, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 9, num));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 9, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 10, num));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 10, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 11, dot));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 11, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 12, num));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 12, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 13, num));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 13, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 14, num));

                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 14, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 15, space));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 15, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 15, ba.MkOr(new CharPred(':'), alphaNum)));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 15, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 16, space));

                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 16, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 17, new CharPred('d')));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 17, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 18, new CharPred(':')));

                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 18, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 19, new CharPred(firstDigit.toString().charAt(0))));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 19, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 20, new CharPred(secondDigit.toString().charAt(0))));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 20, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 21,  new CharPred(thirdDigit.toString().charAt(0))));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 21, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 22, dot));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 22, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 23, num));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 23, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 24, num));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 24, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 25, num));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 25, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 26, dot));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 26, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 27, num));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 27, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 28, num));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 28, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 29, num));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 29, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 30, dot));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 30, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 31, num));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 31, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 32, num));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 32, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 33, num));

                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 33, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 34, space));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 34, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 34, ba.MkOr(new CharPred(':'), alphaNum)));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 34, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 35, space));

                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 35, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 36, new CharPred('p')));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 36, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 37, new CharPred(':')));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 37, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 38, new CharPred('\'')));

                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 38, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 39, alphaNum));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 39, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 39, alphaNum));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 39, 3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 40, new CharPred('\'')));
                    finalStates.add(3 + (firstDigit * 4329) + (secondDigit * 429) + (thirdDigit * 39) + 40);
                }
            }
        }

        try {
            return SFA.MkSFA(transitions, 0, finalStates, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    private SRA<CharPred, Character> getSmallIP3PacketParserSRA(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null, null));

        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();

        for (int index = 0; index < "s:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index, index + 1, new CharPred("s:".charAt(index)), 3));

        transitions.add(new SRAStoreMove<CharPred, Character>(2, 3, num, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(3, 4, num, 1));
        transitions.add(new SRAStoreMove<CharPred, Character>(4, 5, num, 2));
//        transitions.add(new SRAStoreMove<CharPred, Character>(9, 10, dot, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(10, 11, num, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(11, 12, num, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(12, 13, num, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(13, 14, dot, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(14, 15, num, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(15, 16, num, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(16, 17, num, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(17, 18, dot, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(18, 19, num, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(19, 20, num, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(20, 21, num, 3));

        for (int index = 0; index < " p:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 5, index + 6, new CharPred(" p:".charAt(index)), 3));

        transitions.add(new SRAStoreMove<CharPred, Character>(8, 9, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(9, 9, num, 3));

        for (int index = 0; index < " d:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 9, index + 10, new CharPred(" d:".charAt(index)), 3));

        transitions.add(new SRACheckMove<CharPred, Character>(12, 13, num, 0));
        transitions.add(new SRACheckMove<CharPred, Character>(13, 14, num, 1));
        transitions.add(new SRACheckMove<CharPred, Character>(14, 15, num, 2));
//        transitions.add(new SRAStoreMove<CharPred, Character>(37, 38, dot, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(38, 39, num, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(39, 40, num, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(40, 41, num, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(41, 42, dot, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(42, 43, num, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(43, 44, num, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(44, 45, num, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(45, 46, dot, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(46, 47, num, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(47, 48, num, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(48, 49, num, 3));

//        for (int index = 0; index < " prt:".length(); index++)
//            transitions.add(new SRAStoreMove<CharPred, Character>(index + 49, index + 50, new CharPred(" prt:".charAt(index)), 3));
//
//        transitions.add(new SRAStoreMove<CharPred, Character>(54, 55, num, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(55, 55, num, 3));

//        for (int index = 0; index < " pload:".length(); index++)
//            transitions.add(new SRAStoreMove<CharPred, Character>(index + 55, index + 56, new CharPred(" pload:".charAt(index)), 3));
//
//        transitions.add(new SRAStoreMove<CharPred, Character>(62, 63, new CharPred('\''), 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(63, 64, alphaNum, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(64, 64, alphaNum, 3));
//        transitions.add(new SRAStoreMove<CharPred, Character>(64, 65, new CharPred('\''), 3));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(15), registers, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SRA<CharPred, Character> getIP4PacketParserSRA(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null, null, null));

        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();

        for (int index = 0; index < "srcip:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index, index + 1, new CharPred("srcip:".charAt(index)), 4));

        transitions.add(new SRAStoreMove<CharPred, Character>(6, 7, num, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(7, 8, num, 1));
        transitions.add(new SRAStoreMove<CharPred, Character>(8, 9, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(9, 10, dot, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(10, 11, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(11, 12, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(12, 13, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(13, 14, dot, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(14, 15, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(15, 16, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(16, 17, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(17, 18, dot, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(18, 19, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(19, 20, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(20, 21, num, 4));

        for (int index = 0; index < " prt:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 21, index + 22, new CharPred(" prt:".charAt(index)), 4));

        transitions.add(new SRAStoreMove<CharPred, Character>(26, 27, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(27, 27, num, 4));

        for (int index = 0; index < " dstip:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 27, index + 28, new CharPred(" dstip:".charAt(index)), 4));

        transitions.add(new SRACheckMove<CharPred, Character>(34, 35, num, 0));
        transitions.add(new SRACheckMove<CharPred, Character>(35, 36, num, 1));
        transitions.add(new SRACheckMove<CharPred, Character>(36, 37, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(37, 38, dot, 4));
        transitions.add(new SRACheckMove<CharPred, Character>(38, 39, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(39, 40, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(40, 41, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(41, 42, dot, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(42, 43, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(43, 44, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(44, 45, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(45, 46, dot, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(46, 47, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(47, 48, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(48, 49, num, 4));

        for (int index = 0; index < " prt:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 49, index + 50, new CharPred(" prt:".charAt(index)), 4));

        transitions.add(new SRAStoreMove<CharPred, Character>(54, 55, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(55, 55, num, 4));

        for (int index = 0; index < " pload:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 55, index + 56, new CharPred(" pload:".charAt(index)), 4));

        transitions.add(new SRAStoreMove<CharPred, Character>(62, 63, new CharPred('\''), 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(63, 64, alphaNum, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(64, 64, alphaNum, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(64, 65, new CharPred('\''), 4));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(65), registers, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SRA<CharPred, Character> getIP4PacketParserSimplifiedSRA(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null, null, null));

        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();

        for (int index = 0; index < "s:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index, index + 1, new CharPred("s:".charAt(index)), 4));

        transitions.add(new SRAStoreMove<CharPred, Character>(2, 3, num, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(3, 4, num, 1));
        transitions.add(new SRAStoreMove<CharPred, Character>(4, 5, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(5, 6, dot, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(6, 7, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(7, 8, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(8, 9, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(9, 10, dot, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(10, 11, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(11, 12, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(12, 13, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(13, 14, dot, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(14, 15, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(15, 16, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(16, 17, num, 4));

        transitions.add(new SRAStoreMove<CharPred, Character>(17, 18, space, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(18, 18, ba.MkOr(new CharPred(':'), alphaNum), 4));

        for (int index = 0; index < " d:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 18, index + 19, new CharPred(" d:".charAt(index)), 4));

        transitions.add(new SRACheckMove<CharPred, Character>(21, 22, num, 0));
        transitions.add(new SRACheckMove<CharPred, Character>(22, 23, num, 1));
        transitions.add(new SRACheckMove<CharPred, Character>(23, 24, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(24, 25, dot, 4));
        transitions.add(new SRACheckMove<CharPred, Character>(25, 26, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(26, 27, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(27, 28, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(28, 29, dot, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(29, 30, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(30, 31, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(31, 32, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(32, 33, dot, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(33, 34, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(34, 35, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(35, 36, num, 4));

        transitions.add(new SRAStoreMove<CharPred, Character>(36, 37, space, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(37, 37, ba.MkOr(new CharPred(':'), alphaNum), 4));

        for (int index = 0; index < " p:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 37, index + 38, new CharPred(" p:".charAt(index)), 4));

        transitions.add(new SRAStoreMove<CharPred, Character>(40, 41, new CharPred('\''), 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(41, 42, alphaNum, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(42, 42, alphaNum, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(42, 43, new CharPred('\''), 4));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(43), registers, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SFA<CharPred, Character> getIP4PacketParserSimplifiedSFA(UnaryCharIntervalSolver ba) {
        Collection<SFAMove<CharPred, Character>> transitions = new LinkedList<SFAMove<CharPred, Character>>();
        LinkedList<Integer> finalStates = new LinkedList<Integer>();

        transitions.add(new SFAInputMove<CharPred, Character>(0, 1, new CharPred('s')));
        transitions.add(new SFAInputMove<CharPred, Character>(1, 2, new CharPred(':')));

        for (Integer firstDigit = 0; firstDigit < 10; firstDigit++) {
            transitions.add(new SFAInputMove<CharPred, Character>(2, (firstDigit * 46287) + 3, new CharPred(firstDigit.toString().charAt(0))));

            for (Integer secondDigit = 0; secondDigit < 10; secondDigit++) {
                transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287), 3 + (firstDigit * 46287) + (secondDigit * 4477) + 1, new CharPred(secondDigit.toString().charAt(0))));

                for (Integer thirdDigit = 0; thirdDigit < 10; thirdDigit++) {
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + 1, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + 2, new CharPred(thirdDigit.toString().charAt(0))));
                    transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + 2, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + 3, dot));

                    for (Integer fourthDigit = 0; fourthDigit < 10; fourthDigit++) {
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + 3, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 4, new CharPred(fourthDigit.toString().charAt(0))));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 4, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 5, num));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 5, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 6, num));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 6, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 7, dot));

                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 7, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 8, num));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 8, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 9, num));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 9, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 10, num));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 10, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 11, dot));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 11, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 12, num));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 12, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 13, num));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 13, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 14, num));

                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 14, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 15, space));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 15, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 15, ba.MkOr(new CharPred(':'), alphaNum)));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 15, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 16, space));

                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 16, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 17, new CharPred('d')));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 17, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 18, new CharPred(':')));

                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 18, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 19, new CharPred(firstDigit.toString().charAt(0))));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 19, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 20, new CharPred(secondDigit.toString().charAt(0))));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 20, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 21,  new CharPred(thirdDigit.toString().charAt(0))));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 21, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 22, dot));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 22, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 23, new CharPred(fourthDigit.toString().charAt(0))));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 23, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 24, num));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 24, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 25, num));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 25, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 26, dot));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 26, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 27, num));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 27, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 28, num));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 28, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 29, num));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 29, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 30, dot));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 30, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 31, num));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 31, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 32, num));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 32, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 33, num));

                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 33, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 34, space));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 34, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 34, ba.MkOr(new CharPred(':'), alphaNum)));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 34, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 35, space));

                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 35, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 36, new CharPred('p')));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 36, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 37, new CharPred(':')));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 37, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 38, new CharPred('\'')));

                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 38, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 39, alphaNum));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 39, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 39, alphaNum));
                        transitions.add(new SFAInputMove<CharPred, Character>(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 39, 3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 40, new CharPred('\'')));
                        finalStates.add(3 + (firstDigit * 46287) + (secondDigit * 4477) + (thirdDigit * 444) + (fourthDigit * 37) + 40);
                    }


                }
            }
        }

        try {
            return SFA.MkSFA(transitions, 0,finalStates, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    private SRA<CharPred, Character> getIP6PacketParserSRA(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null, null, null, null, null));

        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();

        for (int index = 0; index < "srcip:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index, index + 1, new CharPred("srcip:".charAt(index)), 6));

        transitions.add(new SRAStoreMove<CharPred, Character>(6, 7, num, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(7, 8, num, 1));
        transitions.add(new SRAStoreMove<CharPred, Character>(8, 9, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(9, 10, dot, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(10, 11, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(11, 12, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(12, 13, num, 5));
        transitions.add(new SRAStoreMove<CharPred, Character>(13, 14, dot, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(14, 15, num, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(15, 16, num, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(16, 17, num, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(17, 18, dot, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(18, 19, num, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(19, 20, num, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(20, 21, num, 6));

        for (int index = 0; index < " prt:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 21, index + 22, new CharPred(" prt:".charAt(index)), 6));

        transitions.add(new SRAStoreMove<CharPred, Character>(26, 27, num, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(27, 27, num, 6));

        for (int index = 0; index < " dstip:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 27, index + 28, new CharPred(" dstip:".charAt(index)), 6));

        transitions.add(new SRACheckMove<CharPred, Character>(34, 35, num, 0));
        transitions.add(new SRACheckMove<CharPred, Character>(35, 36, num, 1));
        transitions.add(new SRACheckMove<CharPred, Character>(36, 37, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(37, 38, dot, 6));
        transitions.add(new SRACheckMove<CharPred, Character>(38, 39, num, 3));
        transitions.add(new SRACheckMove<CharPred, Character>(39, 40, num, 4));
        transitions.add(new SRACheckMove<CharPred, Character>(40, 41, num, 5));
        transitions.add(new SRAStoreMove<CharPred, Character>(41, 42, dot, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(42, 43, num, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(43, 44, num, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(44, 45, num, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(45, 46, dot, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(46, 47, num, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(47, 48, num, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(48, 49, num, 6));

        for (int index = 0; index < " prt:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 49, index + 50, new CharPred(" prt:".charAt(index)), 6));

        transitions.add(new SRAStoreMove<CharPred, Character>(54, 55, num, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(55, 55, num, 6));

        for (int index = 0; index < " pload:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 55, index + 56, new CharPred(" pload:".charAt(index)), 6));

        transitions.add(new SRAStoreMove<CharPred, Character>(62, 63, new CharPred('\''), 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(63, 64, alphaNum, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(64, 64, alphaNum, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(64, 65, new CharPred('\''), 6));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(65), registers, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SRA<CharPred, Character> getIP6PacketParserSimplifiedSRA(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null, null, null, null, null));

        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();

        for (int index = 0; index < "s:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index, index + 1, new CharPred("s:".charAt(index)), 6));

        transitions.add(new SRAStoreMove<CharPred, Character>(2, 3, num, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(3, 4, num, 1));
        transitions.add(new SRAStoreMove<CharPred, Character>(4, 5, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(5, 6, dot, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(6, 7, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(7, 8, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(8, 9, num, 5));
        transitions.add(new SRAStoreMove<CharPred, Character>(9, 10, dot, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(10, 11, num, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(11, 12, num, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(12, 13, num, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(13, 14, dot, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(14, 15, num, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(15, 16, num, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(16, 17, num, 6));

        transitions.add(new SRAStoreMove<CharPred, Character>(17, 18, space, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(18, 18, ba.MkOr(new CharPred(':'), alphaNum), 6));

        for (int index = 0; index < " d:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 18, index + 19, new CharPred(" d:".charAt(index)), 6));

        transitions.add(new SRACheckMove<CharPred, Character>(21, 22, num, 0));
        transitions.add(new SRACheckMove<CharPred, Character>(22, 23, num, 1));
        transitions.add(new SRACheckMove<CharPred, Character>(23, 24, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(24, 25, dot, 6));
        transitions.add(new SRACheckMove<CharPred, Character>(25, 26, num, 3));
        transitions.add(new SRACheckMove<CharPred, Character>(26, 27, num, 4));
        transitions.add(new SRACheckMove<CharPred, Character>(27, 28, num, 5));
        transitions.add(new SRAStoreMove<CharPred, Character>(28, 29, dot, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(29, 30, num, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(30, 31, num, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(31, 32, num, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(32, 33, dot, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(33, 34, num, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(34, 35, num, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(35, 36, num, 6));

        transitions.add(new SRAStoreMove<CharPred, Character>(36, 37, space, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(37, 37, ba.MkOr(new CharPred(':'), alphaNum), 6));

        for (int index = 0; index < " p:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 37, index + 38, new CharPred(" p:".charAt(index)), 6));

        transitions.add(new SRAStoreMove<CharPred, Character>(40, 41, new CharPred('\''), 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(41, 42, alphaNum, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(42, 42, alphaNum, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(42, 43, new CharPred('\''), 6));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(43), registers, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SRA<CharPred, Character> getIP9PacketParserSRA(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null, null, null, null, null, null, null, null));

        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();

        for (int index = 0; index < "srcip:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index, index + 1, new CharPred("srcip:".charAt(index)), 9));

        transitions.add(new SRAStoreMove<CharPred, Character>(6, 7, num, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(7, 8, num, 1));
        transitions.add(new SRAStoreMove<CharPred, Character>(8, 9, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(9, 10, dot, 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(10, 11, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(11, 12, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(12, 13, num, 5));
        transitions.add(new SRAStoreMove<CharPred, Character>(13, 14, dot, 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(14, 15, num, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(15, 16, num, 7));
        transitions.add(new SRAStoreMove<CharPred, Character>(16, 17, num, 8));
        transitions.add(new SRAStoreMove<CharPred, Character>(17, 18, dot, 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(18, 19, num, 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(19, 20, num, 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(20, 21, num, 9));

        for (int index = 0; index < " prt:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 21, index + 22, new CharPred(" prt:".charAt(index)), 9));

        transitions.add(new SRAStoreMove<CharPred, Character>(26, 27, num, 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(27, 27, num, 9));

        for (int index = 0; index < " dstip:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 27, index + 28, new CharPred(" dstip:".charAt(index)), 9));

        transitions.add(new SRACheckMove<CharPred, Character>(34, 35, num, 0));
        transitions.add(new SRACheckMove<CharPred, Character>(35, 36, num, 1));
        transitions.add(new SRACheckMove<CharPred, Character>(36, 37, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(37, 38, dot, 9));
        transitions.add(new SRACheckMove<CharPred, Character>(38, 39, num, 3));
        transitions.add(new SRACheckMove<CharPred, Character>(39, 40, num, 4));
        transitions.add(new SRACheckMove<CharPred, Character>(40, 41, num, 5));
        transitions.add(new SRACheckMove<CharPred, Character>(41, 42, dot, 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(41, 42, dot, 9));
        transitions.add(new SRACheckMove<CharPred, Character>(42, 43, num, 6));
        transitions.add(new SRACheckMove<CharPred, Character>(43, 44, num, 7));
        transitions.add(new SRACheckMove<CharPred, Character>(44, 45, num, 8));
        transitions.add(new SRAStoreMove<CharPred, Character>(45, 46, dot, 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(46, 47, num, 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(47, 48, num, 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(48, 49, num, 9));

        for (int index = 0; index < " prt:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 49, index + 50, new CharPred(" prt:".charAt(index)), 9));

        transitions.add(new SRAStoreMove<CharPred, Character>(54, 55, num, 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(55, 55, num, 9));

        for (int index = 0; index < " pload:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 55, index + 56, new CharPred(" pload:".charAt(index)), 9));

        transitions.add(new SRAStoreMove<CharPred, Character>(62, 63, new CharPred('\''), 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(63, 64, alphaNum, 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(64, 64, alphaNum, 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(64, 65, new CharPred('\''), 9));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(65), registers, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private SRA<CharPred, Character> getIP9PacketParserSimplifiedSRA(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null, null, null, null, null, null, null, null));

        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();

        for (int index = 0; index < "s:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index, index + 1, new CharPred("s:".charAt(index)), 9));

        transitions.add(new SRAStoreMove<CharPred, Character>(2, 3, num, 0));
        transitions.add(new SRAStoreMove<CharPred, Character>(3, 4, num, 1));
        transitions.add(new SRAStoreMove<CharPred, Character>(4, 5, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(5, 6, dot, 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(6, 7, num, 3));
        transitions.add(new SRAStoreMove<CharPred, Character>(7, 8, num, 4));
        transitions.add(new SRAStoreMove<CharPred, Character>(8, 9, num, 5));
        transitions.add(new SRAStoreMove<CharPred, Character>(9, 10, dot, 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(10, 11, num, 6));
        transitions.add(new SRAStoreMove<CharPred, Character>(11, 12, num, 7));
        transitions.add(new SRAStoreMove<CharPred, Character>(12, 13, num, 8));
        transitions.add(new SRAStoreMove<CharPred, Character>(13, 14, dot, 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(14, 15, num, 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(15, 16, num, 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(16, 17, num, 9));

        transitions.add(new SRAStoreMove<CharPred, Character>(17, 18, space, 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(18, 18, ba.MkOr(new CharPred(':'), alphaNum), 9));

        for (int index = 0; index < " d:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 18, index + 19, new CharPred(" d:".charAt(index)), 9));

        transitions.add(new SRACheckMove<CharPred, Character>(21, 22, num, 0));
        transitions.add(new SRACheckMove<CharPred, Character>(22, 23, num, 1));
        transitions.add(new SRACheckMove<CharPred, Character>(23, 24, num, 2));
        transitions.add(new SRAStoreMove<CharPred, Character>(24, 25, dot, 9));
        transitions.add(new SRACheckMove<CharPred, Character>(25, 26, num, 3));
        transitions.add(new SRACheckMove<CharPred, Character>(26, 27, num, 4));
        transitions.add(new SRACheckMove<CharPred, Character>(27, 28, num, 5));
        transitions.add(new SRAStoreMove<CharPred, Character>(28, 29, dot, 9));
        transitions.add(new SRACheckMove<CharPred, Character>(29, 30, num, 6));
        transitions.add(new SRACheckMove<CharPred, Character>(30, 31, num, 7));
        transitions.add(new SRACheckMove<CharPred, Character>(31, 32, num, 8));
        transitions.add(new SRAStoreMove<CharPred, Character>(32, 33, dot, 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(33, 34, num, 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(34, 35, num, 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(35, 36, num, 9));

        transitions.add(new SRAStoreMove<CharPred, Character>(36, 37, space, 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(37, 37, ba.MkOr(new CharPred(':'), alphaNum), 9));

        for (int index = 0; index < " p:".length(); index++)
            transitions.add(new SRAStoreMove<CharPred, Character>(index + 37, index + 38, new CharPred(" p:".charAt(index)), 9));

        transitions.add(new SRAStoreMove<CharPred, Character>(40, 41, new CharPred('\''), 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(41, 42, alphaNum, 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(42, 42, alphaNum, 9));
        transitions.add(new SRAStoreMove<CharPred, Character>(42, 43, new CharPred('\''), 9));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(43), registers, ba);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

	// -------------------------
	// Auxiliary methods
	// -------------------------
	private List<Character> lOfS(String s) {
		List<Character> l = new ArrayList<Character>();
		char[] ca = s.toCharArray();
		for (int i = 0; i < s.length(); i++)
			l.add(ca[i]);
		return l;
	}

}
