package test.SRA;

import automata.sra.*;
import automata.sfa.*;
import logic.ltl.Predicate;
import org.junit.Test;
import org.omg.CORBA.TIMEOUT;
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
        assertTrue(SSNParserSRA.accepts(validName1, ba));
        assertTrue(SSNParserSRA.accepts(validName2, ba));
        assertFalse(SSNParserSRA.accepts(invalidName1, ba));
        assertFalse(SSNParserSRA.accepts(invalidName2, ba));
        assertFalse(SSNParserSRA.accepts(invalidName3, ba));
    }

    @Test
    public void testSSNParserMSRA() throws TimeoutException {
        assertTrue(SSNParserMSRA.accepts(validName1, ba));
        assertTrue(SSNParserMSRA.accepts(validName2, ba));
        assertTrue(SSNParserFirst.accepts(validName1, ba));
        assertTrue(SSNParserFirst.accepts(validName2, ba));
        assertTrue(SSNParserLast.accepts(validName1, ba));
        assertTrue(SSNParserLast.accepts(validName2, ba));
        assertFalse(SSNParserMSRA.accepts(invalidName1, ba));
        assertFalse(SSNParserMSRA.accepts(invalidName2, ba));
        assertFalse(SSNParserMSRA.accepts(invalidName3, ba));
    }

    @Test
    public void testSSNSimulation() throws TimeoutException {
        assertTrue(SRA.canSimulate(SSNParserMSRA, SSNParserFirst, ba, false, Long.MAX_VALUE));
        assertTrue(SSNParserFirst.languageIncludes(SSNParserMSRA, ba, Long.MAX_VALUE));
    }

    @Test
    public void testSSNParserMSRAtoSRA() throws TimeoutException {
        SRA<CharPred, Character> toSRA = SSNParserMSRA.toSingleValuedSRA(ba, Long.MAX_VALUE);
        toSRA.createDotFile("ssnSra", "");
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
        assertTrue(IP2PacketParserSimplifiedSFA.createDotFile("IPSFA", ""));
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

    @Test
    public void IP2SRAInfo() throws TimeoutException {
        System.out.println("IP2 Simplified SRA Info:");
        System.out.println();
        System.out.println("Number of states: " + IP2PacketParserSimplifiedSRA.getStates().size());
        System.out.println("Number of transitions: " + IP2PacketParserSimplifiedSRA.getMoves().size());
    }

    @Test
    public void IP2SFAInfo() throws TimeoutException {
        System.out.println("IP2 Simplified SFA Info:");
        System.out.println();
        System.out.println("Number of states: " + IP2PacketParserSimplifiedSFA.getStates().size());
        System.out.println("Number of transitions: " + IP2PacketParserSimplifiedSFA.getMoves().size());
    }

    @Test public void IP2SRATiming() throws TimeoutException {
        assertTrue(IP2PacketParserSimplifiedSRA.accepts(validIPPacketSimplified1, ba));
        assertFalse(IP2PacketParserSimplifiedSRA.accepts(invalidIPPacketSimplified1, ba));
    }

    @Test
    public void IP2SFATiming() throws TimeoutException {
        assertTrue(IP2PacketParserSimplifiedSFA.accepts(validIPPacketSimplified1, ba));
        assertFalse(IP2PacketParserSimplifiedSFA.accepts(invalidIPPacketSimplified1, ba));
    }

    @Test
    public void testSimulationIP() throws TimeoutException {
        // assertTrue(IP2PacketParserSRA.languageIncludes(IP3PacketParserSRA, ba, Long.MAX_VALUE));
        assertTrue(IP3PacketParserSRA.languageIncludes(IP4PacketParserSRA, ba, Long.MAX_VALUE));
        // assertTrue(IP2PacketParserSRA.languageIncludes(IP4PacketParserSRA, ba, Long.MAX_VALUE));
//        assertTrue(IP6PacketParserSRA.languageIncludes(IP9PacketParserSRA, ba, Long.MAX_VALUE));
//
//        assertFalse(IP2PacketParserSRA.isLanguageEquivalent(IP3PacketParserSRA, ba, Long.MAX_VALUE));
//        assertFalse(IP3PacketParserSRA.isLanguageEquivalent(IP4PacketParserSRA, ba, Long.MAX_VALUE));
//        assertFalse(IP2PacketParserSRA.isLanguageEquivalent(IP3PacketParserSRA, ba, Long.MAX_VALUE));
//        assertFalse(IP3PacketParserSRA.isLanguageEquivalent(IP4PacketParserSRA, ba, Long.MAX_VALUE));

    }
//
//
//    @Test
//    public void testLanguageInclusion() throws TimeoutException {
//        assertTrue(IP2PacketParserSRA.languageIncludes(IP3PacketParserSRA, ba, Long.MAX_VALUE));
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
    private List<Character> invalidIPPacketSimplified1 = lOfS("s:132.168.123.192 p:40 d:192.168.123.224 p:50 p:'hello'"); // not accepted by IP2PacketParserSimplified
    private List<Character> validIPPacket2 = lOfS("srcip:192.168.123.122 prt:40 dstip:192.168.123.124 prt:5 pload:'hello123'"); // accepted by IP9PacketParser
    private List<Character> validIPPacket3 = lOfS("srcip:192.148.123.122 prt:40 dstip:192.148.123.124 prt:5 pload:'hello123'"); // accepted by IP9PacketParser
    private List<Character> invalidIPPacket1 = lOfS("srcip:12.168.123.122 prt:40 dstip:192.168.123.124 prt:5 pload:'hello123'"); // not accepted by either
    private List<Character> invalidIPPacket2 = lOfS("srcip:192.148.123.122 prt:40 dstip:192.148.123.124 prt:5 plad:'hello123'"); // not accepted by either
    private List<Character> invalidIPPacket3 = lOfS("srcip:192.148.123.122 dstip:192.148.123.124 prt:5 pload:'hello123'"); // not accepted by either
    private List<Character> dependentIPPacket1 = lOfS("srcip:192.148.125.122 prt:40 dstip:192.148.123.124 prt:5 pload:'hello123'"); // accepted by IP6PacketParser but not IP9PacketParser

    // Automata
    private SRA<CharPred, Character> SSNParserSRA = getSSNParserSRA(ba);
    private SRA<CharPred, Character> SSNParserMSRA = getSSNParserMSRA(ba);
    private SRA<CharPred, Character> SSNParserFirst = getSSNParserFirst(ba);
    private SRA<CharPred, Character> SSNParserLast = getSSNParserLast(ba);
    private SRA<CharPred, Character> XMLParserSRA = getXMLParserSRA(ba);
    private SRA<CharPred, Character> IP2PacketParserSRA = getIP2PacketParserSRA(ba);
    private SRA<CharPred, Character> IP2PacketParserSimplifiedSRA = getIP2PacketParserSimplifiedSRA(ba);
    private SFA<CharPred, Character> IP2PacketParserSimplifiedSFA = getIP2PacketParserSimplifiedSFA(ba);
    private SRA<CharPred, Character> IP3PacketParserSRA = getIP3PacketParserSRA(ba);
    private SRA<CharPred, Character> IP4PacketParserSRA = getIP4PacketParserSRA(ba);
    private SRA<CharPred, Character> IP6PacketParserSRA = getIP6PacketParserSRA(ba);
    private SRA<CharPred, Character> IP9PacketParserSRA = getIP9PacketParserSRA(ba);

    // TODO: Run over CSV, generate CSV data.
    // TODO: IP packets in the same subnet.
    // TODO: ISBN-10
    // https://regexr.com

	private SRA<CharPred, Character> getSSNParserSRA(UnaryCharIntervalSolver ba) {
		LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null));

		Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();
		// Read first initial and store it in register 0
		transitions.add(new SRAFreshMove<CharPred, Character>(0, 1, upperAlpha, 0, registers.size()));

		// Read an unbound number of lowercase letters for the rest of the first name.
        // Dispose of them on the dummy register (2)
        transitions.add(new SRACheckMove<CharPred, Character>(1, 1, lowerAlpha, 2));
        transitions.add(new SRAFreshMove<CharPred, Character>(1, 1, lowerAlpha, 2, registers.size()));

        // Read a comma and dispose of it on register (2)
        transitions.add(new SRAFreshMove<CharPred, Character>(1, 2, comma, 2, registers.size()));

        // Read an unbound number of spaces.
        transitions.add(new SRACheckMove<CharPred, Character>(2, 2, space, 2));
        transitions.add(new SRAFreshMove<CharPred, Character>(2, 2, space, 2, registers.size()));

        // Read the second initial and store it in register 1
        transitions.add(new SRAFreshMove<CharPred, Character>(2, 3, upperAlpha, 1, registers.size()));

        // Read an unbound number of lowercase letters for the rest of the last name.
        // Dispose of them on the dummy register (2)
        transitions.add(new SRAFreshMove<CharPred, Character>(3, 3, lowerAlpha, 2, registers.size()));
        transitions.add(new SRACheckMove<CharPred, Character>(3, 3, lowerAlpha, 2));

        // Read a comma and dispose of it on register (2)
        transitions.add(new SRAFreshMove<CharPred, Character>(3, 4, comma, 2, registers.size()));

        // Read an unbound number of spaces.
        transitions.add(new SRAFreshMove<CharPred, Character>(4, 4, space, 2, registers.size()));
        transitions.add(new SRACheckMove<CharPred, Character>(4, 4, space, 2));

        // Read the first initial and compare it to register 0
        transitions.add(new SRACheckMove<CharPred, Character>(4, 5, upperAlpha, 0));

        // Read the second initial and compare it to register 1
        transitions.add(new SRACheckMove<CharPred, Character>(5, 6, upperAlpha, 1));

        // Read the second initial and check if it is a repeated initial
        transitions.add(new SRACheckMove<CharPred, Character>(2, 7, upperAlpha, 0));

        // Read an unbound number of lowercase letters for the rest of the last name.
        // Dispose of them on the dummy register (2)
        transitions.add(new SRAFreshMove<CharPred, Character>(7, 7, lowerAlpha, 2, registers.size()));
        transitions.add(new SRACheckMove<CharPred, Character>(7, 7, lowerAlpha, 2));

        // Read a comma and dispose of it on register (2)
        transitions.add(new SRAFreshMove<CharPred, Character>(7, 8, comma, 2, registers.size()));

        // Read an unbound number of spaces.
        transitions.add(new SRAFreshMove<CharPred, Character>(8, 8, space, 2, registers.size()));
        transitions.add(new SRACheckMove<CharPred, Character>(8, 8, space, 2));

        // Read the first initial and compare it to register 0
        transitions.add(new SRACheckMove<CharPred, Character>(8, 9, upperAlpha, 0));

        // Read the second initial and compare it to register 0
        transitions.add(new SRACheckMove<CharPred, Character>(9, 10, upperAlpha, 0));

		try {
			return SRA.MkSRA(transitions, 0, Arrays.asList(6, 10), registers, ba);
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

    private SRA<CharPred, Character> getSSNParserMSRA(UnaryCharIntervalSolver ba) {
        LinkedList<Character> registers = new LinkedList<Character>(Arrays.asList(null, null, null));
        Collection<SRAMove<CharPred, Character>> transitions = new LinkedList<SRAMove<CharPred, Character>>();

        // Read the first initial and store it in register 0
        transitions.add(new SRAStoreMove<CharPred, Character>(0, 1, upperAlpha, 0));

        // Read an unbound number of lowercase characters
        transitions.add(new SRAStoreMove<CharPred, Character>(1, 1, lowerAlpha, 2));

        // Read a comma
        transitions.add(new SRAStoreMove<CharPred, Character>(1, 2, comma, 2));

        // Read an unbound number of spaces
        transitions.add(new SRAStoreMove<CharPred, Character>(2, 2, space, 2));

        // Read a different second initial or a repeated second initial and store it in 1
        transitions.add(new SRAStoreMove<CharPred, Character>(2, 3, upperAlpha, 1));

        // Read an unbound number of lowercase characters
        transitions.add(new SRAStoreMove<>(3, 3, lowerAlpha,2));

        // Read a comma
        transitions.add(new SRAStoreMove<CharPred, Character>(3, 4, comma, 2));

        // Read an unbound number of spaces
        transitions.add(new SRAStoreMove<CharPred, Character>(4, 4, space, 2));

        // Read a capital that matches both registers or a capital that matches the first register
        transitions.add(new SRACheckMove<CharPred, Character>(4, 5, upperAlpha, 0));

        // Read a capital that matches both registers or a capital that matches the second register
        transitions.add(new SRACheckMove<CharPred, Character>(5, 6, upperAlpha, 1));

        try {
            return SRA.MkSRA(transitions, 0, Collections.singleton(6), registers, ba);
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
            return SFA.MkSFA(transitions, 0,finalStates, ba, false, false);
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
