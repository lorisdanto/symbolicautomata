package benchmark.regexconverter;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.sat4j.specs.TimeoutException;

import com.google.common.collect.ImmutableList;

import RegexParser.RegexParserProvider;
import RegexParser.CharacterClassNode;
import RegexParser.CharNode;
import RegexParser.ConcatenationNode;
import RegexParser.DotNode;
import RegexParser.EndAnchorNode;
import RegexParser.EscapedCharNode;
import RegexParser.FormulaNode;
import RegexParser.IntervalNode;
import RegexParser.MetaCharNode;
import RegexParser.NormalCharNode;
import RegexParser.NotCharacterClassNode;
import RegexParser.OptionalNode;
import RegexParser.PlusNode;
import RegexParser.RegexListNode;
import RegexParser.RegexNode;
import RegexParser.RepetitionNode;
import RegexParser.StarNode;
import RegexParser.StartAnchorNode;
import RegexParser.UnionNode;

import automata.sfa.*;
import theory.BooleanAlgebra;
import theory.characters.*;
import theory.intervals.UnaryCharIntervalSolver;;

public class RegexConverter {
	
	public static <A, B> SFA<A, B> toSFA(FormulaNode phi, BooleanAlgebra<A, B> ba, UnaryCharIntervalSolver unarySolver) throws TimeoutException{
		SFA<A, B> outputSFA = null;
		
		if (phi instanceof UnionNode) {
			UnionNode cphi= (UnionNode)phi;
			SFA<A, B> left = toSFA(cphi.getMyRegex1(), ba, unarySolver);
			SFA<A, B> right = toSFA(cphi.getMyRegex2(), ba, unarySolver);
			outputSFA = SFA.union(left, right, ba);
			
		} else if (phi instanceof ConcatenationNode) {
			ConcatenationNode cphi= (ConcatenationNode)phi;
			List<RegexNode> concateList = cphi.getList();
			for(RegexNode node: concateList){
				SFA<A, B> tempSFA = toSFA(node, ba, unarySolver);
				outputSFA = SFA.concatenate(outputSFA, tempSFA, ba);
			}
			
		} else if (phi instanceof DotNode) {
			DotNode cphi= (DotNode)phi;
			//unfinished
			
		} else if (phi instanceof StartAnchorNode) {
			StartAnchorNode cphi= (StartAnchorNode)phi;
			//unfinished
			
		} else if (phi instanceof EndAnchorNode) {
			EndAnchorNode cphi= (EndAnchorNode)phi;
			//unfinished
			
		} else if (phi instanceof StarNode) {
			StarNode cphi= (StarNode)phi;
			SFA<A, B> tempSFA = toSFA(cphi.getMyRegex1(), ba, unarySolver);
			outputSFA = SFA.star(tempSFA, ba);
			
		} else if (phi instanceof PlusNode) {
			PlusNode cphi= (PlusNode)phi;
			SFA<A, B> tempSFA = toSFA(cphi.getMyRegex1(), ba, unarySolver);
			outputSFA = SFA.concatenate(tempSFA, SFA.star(tempSFA, ba), ba);
			
		} else if (phi instanceof OptionalNode) {
			OptionalNode cphi= (OptionalNode)phi;
			//unfinished
			
		} else if (phi instanceof NormalCharNode) {
			NormalCharNode cphi= (NormalCharNode)phi;
			
			
		} else if (phi instanceof EscapedCharNode) {
			EscapedCharNode cphi= (EscapedCharNode)phi;
			
			
		} else if (phi instanceof MetaCharNode) {
			MetaCharNode cphi= (MetaCharNode)phi;
			
			
		} else if (phi instanceof CharacterClassNode) {
			CharacterClassNode cphi= (CharacterClassNode)phi;
			
			List<IntervalNode> intervalList = cphi.getIntervals();
			Iterator<IntervalNode> it = intervalList.iterator();
			if (it.hasNext()) {
				CharPred predicate = getCharPred(it.next());
				while (it.hasNext()){
					CharPred temp = getCharPred(it.next());
					predicate =unarySolver.MkOr(predicate, temp);
				}
			}
			//TODO
			//outputSFA = 
			
		} else if (phi instanceof NotCharacterClassNode) {
			NotCharacterClassNode cphi= (NotCharacterClassNode)phi;

			List<IntervalNode> intervalList = cphi.getIntervals();
			Iterator<IntervalNode> it = intervalList.iterator();
			if (it.hasNext()) {
				CharPred predicate = getCharPred(it.next());
				while (it.hasNext()){
					CharPred temp = getCharPred(it.next());
					predicate =unarySolver.MkOr(predicate, temp);
				}
			}
			//TODO
			//outputSFA = 
			
			outputSFA = SFA.complementOf( outputSFA ,ba);
			
		} else if (phi instanceof RepetitionNode) {
			RepetitionNode cphi= (RepetitionNode)phi;
			SFA<A, B> tempSFA = toSFA(cphi.getMyRegex1(), ba, unarySolver);
			if (cphi.getMode().equals("min")){
				for (int i=0;i<cphi.getMin();i++){
					outputSFA = SFA.concatenate(outputSFA, tempSFA, ba);
				}
			}else if(cphi.getMode().equals("minToInfinite")){
				//TODO
				for (int i=0;i<cphi.getMin();i++){
					//tempSFA = SFA.concatenate(, tempSFA, ba);
				}
			}else{ //minToMax
				
			}
		} else {
			System.err.println("Wrong instance of phi, program will quit");
			System.exit(-1);
		}
		

		return outputSFA;
		
	}
	
	private static CharPred getCharPred(IntervalNode node){
		CharPred predicate = null;
		if(node.getMode().equals("single")){
			CharNode single = node.getChar1();
			if (single instanceof NormalCharNode){
				predicate = new CharPred(single.getChar());
			}else if (single instanceof MetaCharNode){
				char meta = single.getChar();
				if(meta == 't'){
					predicate= new CharPred('\t', '\t');
				}else if (meta == 'n'){
					predicate= new CharPred('\n', '\n');
				}else if (meta == 'r'){
					predicate= new CharPred('\r', '\r');
				}else if (meta == 'f'){
					predicate= new CharPred('\f', '\f');
				}else if (meta == 'b'){
					//predicate = 
				}else if (meta == 'B'){
					//predicate = 
				}else if (meta == 'd'){
					predicate = StdCharPred.NUM;
				}else if (meta == 'D'){
					//predicate = 
				}else if (meta == 's'){
					predicate = StdCharPred.SPACES;
				}else if (meta == 'S'){
					//predicate = 
				}else if (meta == 'v'){
					//predicate= new CharPred('\v', '\v');
					//this meta can be seen in the regexlib but it seems java does not support this
				}else if (meta == 'w'){
					predicate = StdCharPred.WORD;
				}else if(meta == 'W'){
					//not sure how to take complement, there should be more
					predicate = CharPred.of(ImmutableList.of('[', ']',
							'!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '.',
							'/', ':', ';', '<', '=', '>', '?', '@', '\\', '^', '`', '{',
							'|', '}', '~', '-'));
				}
				
				
			}else{ 
				//EscapedCharNode, getChar() method returns the char after the backslash
				//TODO: not sure if backslash is needed
				CharPred temp = new CharPred(single.getChar());
			}
		}else{
			//TODO: currently ignore the case of metaCharacters in range e.g. [\d-\s]
			CharNode start = node.getChar1();
			CharNode end = node.getChar2();
			predicate= new CharPred(start.getChar(), end.getChar());
		}
		
		return predicate;
	}
	

}
