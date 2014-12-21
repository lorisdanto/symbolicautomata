package test.SVPA;



public class SVPAUnitTest {

//	@Test
//	public void testCreateDot() {
//		try {
//			Context c = new Context();
//			Z3Provider<IntExpr> z3p = new Z3Provider<IntExpr>(c, c.IntSort());
//			BooleanAlgebra<Predicate<IntExpr>, IntExpr> ba = z3p;			
//
//			getSVPAa(c, ba).createDotFile("vpaA", "");
//			getSVPAb(c, ba).createDotFile("vpaB", "");		
//
//		} catch (Z3Exception e) {
//			System.out.print(e);
//		} catch (AutomataException e) {
//			System.out.print(e);
//		}
//	}
//
//	@Test
//	public void testMkSVPA() {
//		try {
//			Context c = new Context();
//			Z3Provider<IntExpr> z3p = new Z3Provider<IntExpr>(c, c.IntSort());
//			BooleanAlgebra<Predicate<IntExpr>, IntExpr> ba = z3p;			
//
//			SVPA<Predicate<IntExpr>, IntExpr> autA = getSVPAa(c, ba);
//
////			assertTrue(autA.isDeterministic);
//			assertTrue(autA.stateCount == 2);
////			assertTrue(autA.transitionCount == 1);
//
//		} catch (Z3Exception e) {
//			System.out.print(e);
//		} catch (AutomataException e) {
//			System.out.print(e);
//		}
//	}
//	
//	@Test
//	public void testEmptyFull() {
//		try {
//			Context c = new Context();
//			Z3Provider<IntExpr> z3p = new Z3Provider<IntExpr>(c, c.IntSort());
//			BooleanAlgebra<Predicate<IntExpr>, IntExpr> ba = z3p;
//
//			SVPA<Predicate<IntExpr>, IntExpr> empty = SVPA.getEmptySVPA(ba);	
//			SVPA<Predicate<IntExpr>, IntExpr> full = SVPA.getFullSVPA(ba);	
//
//
//			boolean check = empty.createDotFile("emptysvpa", "");
//			check = full.createDotFile("fullsvpa", "");
//			
//			assertTrue(check);
//
//		} catch (Z3Exception e) {
//			System.out.print(e);
//		}
//	}
//	
//	@Test
//	public void testAccept() {
//
//		try {
//			Context c = new Context();
//			Z3Provider<IntExpr> z3p = new Z3Provider<IntExpr>(c, c.IntSort());
//			BooleanAlgebra<Predicate<IntExpr>, IntExpr> ba = z3p;
//
//			SVPA<Predicate<IntExpr>, IntExpr> autA = getSVPAa(c, ba);
//			
//			TaggedSymbol<IntExpr> c1 = new TaggedSymbol<IntExpr>(c.MkInt(1), SymbolTag.Call);
////			TaggedSymbol<IntExpr> c2 = new TaggedSymbol<IntExpr>(c.MkInt(2), SymbolTag.Call);
//			TaggedSymbol<IntExpr> r1 = new TaggedSymbol<IntExpr>(c.MkInt(1), SymbolTag.Return);
////			TaggedSymbol<IntExpr> r2 = new TaggedSymbol<IntExpr>(c.MkInt(2), SymbolTag.Return);
////			TaggedSymbol<IntExpr> i1 = new TaggedSymbol<IntExpr>(c.MkInt(1), SymbolTag.Internal);
////			TaggedSymbol<IntExpr> i2 = new TaggedSymbol<IntExpr>(c.MkInt(2), SymbolTag.Internal);
////			TaggedSymbol<IntExpr> rb = new TaggedSymbol<IntExpr>(c.MkInt(4), SymbolTag.Return);
//			
//			TaggedSymbol<IntExpr> c5 = new TaggedSymbol<IntExpr>(c.MkInt(5), SymbolTag.Call);
//			TaggedSymbol<IntExpr> r5 = new TaggedSymbol<IntExpr>(c.MkInt(5), SymbolTag.Return);
//			
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> input1 = Arrays.asList(c1,r1); 						
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> input2 = Arrays.asList(r5,c5,r5);
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> input3 = Arrays.asList(r1,c1,c1,r1);
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> input4 = Arrays.asList(); 	
//
//			boolean acceptInput1 = autA.accepts(input1, ba);
//			boolean acceptInput2 = autA.accepts(input2, ba);
//			boolean acceptInput3 = autA.accepts(input3, ba);
//			boolean acceptInput4 = autA.accepts(input4, ba);
//			
//			assertTrue(acceptInput1);
//			assertFalse(acceptInput2);
//			assertTrue(acceptInput3);
//			assertTrue(acceptInput4);
//			
//
//		} catch (Z3Exception e) {
//			System.out.print(e);
//		} catch (AutomataException e) {
//			System.out.print(e);
//		}
//
//	}
//
//	@Test
//	public void testIntersectionWith() {
//		try {
//			Context c = new Context();
//			Z3Provider<IntExpr> z3p = new Z3Provider<IntExpr>(c, c.IntSort());
//			BooleanAlgebra<Predicate<IntExpr>, IntExpr> ba = z3p;
//			
//			//First Automaton
//			SVPA<Predicate<IntExpr>, IntExpr> autA = getSVPAa(c, ba);
//			
//			//Second Automaton
//			SVPA<Predicate<IntExpr>, IntExpr> autB = getSVPAb(c, ba);			
//			
//			//Compute intersection
//			SVPA<Predicate<IntExpr>, IntExpr> inters = autA.intersectionWith(autB, ba);
//			
//			TaggedSymbol<IntExpr> c1 = new TaggedSymbol<IntExpr>(c.MkInt(1), SymbolTag.Call);
//			TaggedSymbol<IntExpr> i1 = new TaggedSymbol<IntExpr>(c.MkInt(1), SymbolTag.Internal);
//			
//			TaggedSymbol<IntExpr> c5 = new TaggedSymbol<IntExpr>(c.MkInt(5), SymbolTag.Call);
//			TaggedSymbol<IntExpr> r5 = new TaggedSymbol<IntExpr>(c.MkInt(5), SymbolTag.Return);
//			TaggedSymbol<IntExpr> r6 = new TaggedSymbol<IntExpr>(c.MkInt(6), SymbolTag.Return);
//			
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> a = Arrays.asList(i1); 						
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> b = Arrays.asList(c5,r6);
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> ab = Arrays.asList(c5,r5);
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> notab = Arrays.asList(c1,r5); 	
//			
//			assertTrue(autA.accepts(a, ba));
//			assertFalse(autA.accepts(b, ba));
//			assertTrue(autA.accepts(ab, ba));
//			assertFalse(autA.accepts(notab, ba));
//			
//			assertTrue(autB.accepts(b, ba));
//			assertFalse(autB.accepts(a, ba));
//			assertTrue(autB.accepts(ab, ba));
//			assertFalse(autB.accepts(notab, ba));
//			
//			assertTrue(inters.accepts(ab, ba));
//			assertFalse(inters.accepts(a, ba));
//			assertFalse(inters.accepts(b, ba));
//			assertFalse(inters.accepts(notab, ba));					
//			
//		} catch (Z3Exception e) {
//			System.out.print(e);
//		} catch (AutomataException e) {
//			System.out.print(e);
//		}
//	}
//	
//	@Test
//	public void testUnion() {
//		try {
//			Context c = new Context();
//			Z3Provider<IntExpr> z3p = new Z3Provider<IntExpr>(c, c.IntSort());
//			BooleanAlgebra<Predicate<IntExpr>, IntExpr> ba = z3p;
//
//			SVPA<Predicate<IntExpr>, IntExpr> autA = getSVPAa(c, ba);
//
//			SVPA<Predicate<IntExpr>, IntExpr> autB = getSVPAb(c, ba);
//
//			SVPA<Predicate<IntExpr>, IntExpr> union = autA
//					.unionWith(autB, z3p);
//
//			TaggedSymbol<IntExpr> c1 = new TaggedSymbol<IntExpr>(c.MkInt(1), SymbolTag.Call);
//			TaggedSymbol<IntExpr> i1 = new TaggedSymbol<IntExpr>(c.MkInt(1), SymbolTag.Internal);
//			
//			TaggedSymbol<IntExpr> c5 = new TaggedSymbol<IntExpr>(c.MkInt(5), SymbolTag.Call);
//			TaggedSymbol<IntExpr> r5 = new TaggedSymbol<IntExpr>(c.MkInt(5), SymbolTag.Return);
//			TaggedSymbol<IntExpr> r6 = new TaggedSymbol<IntExpr>(c.MkInt(6), SymbolTag.Return);
//			
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> a = Arrays.asList(i1); 						
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> b = Arrays.asList(c5,r6);
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> ab = Arrays.asList(c5,r5);
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> notab = Arrays.asList(c1,r5); 	
//			
//			assertTrue(autA.accepts(a, ba));
//			assertFalse(autA.accepts(b, ba));
//			assertTrue(autA.accepts(ab, ba));
//			assertFalse(autA.accepts(notab, ba));
//			
//			assertTrue(autB.accepts(b, ba));
//			assertFalse(autB.accepts(a, ba));
//			assertTrue(autB.accepts(ab, ba));
//			assertFalse(autB.accepts(notab, ba));
//			
//			assertTrue(union.accepts(ab, ba));
//			assertTrue(union.accepts(a, ba));
//			assertTrue(union.accepts(b, ba));
//			assertFalse(union.accepts(notab, ba));
//
//		} catch (Z3Exception e) {
//			System.out.print(e);
//		} catch (AutomataException e) {
//			System.out.print(e);
//		}
//	}
//	
//	@Test
//	public void testMkTotal() {
//		try {
//			Context c = new Context();
//			Z3Provider<IntExpr> z3p = new Z3Provider<IntExpr>(c, c.IntSort());
//			BooleanAlgebra<Predicate<IntExpr>, IntExpr> ba = z3p;
//
//			SVPA<Predicate<IntExpr>, IntExpr> autA = getSVPAa(c, ba);						
//			
//			SVPA<Predicate<IntExpr>, IntExpr> totA = autA.mkTotal(ba);
//			
//			TaggedSymbol<IntExpr> c1 = new TaggedSymbol<IntExpr>(c.MkInt(1), SymbolTag.Call);
//			TaggedSymbol<IntExpr> i1 = new TaggedSymbol<IntExpr>(c.MkInt(1), SymbolTag.Internal);
//			
//			TaggedSymbol<IntExpr> c5 = new TaggedSymbol<IntExpr>(c.MkInt(5), SymbolTag.Call);
//			TaggedSymbol<IntExpr> r5 = new TaggedSymbol<IntExpr>(c.MkInt(5), SymbolTag.Return);
//			TaggedSymbol<IntExpr> r6 = new TaggedSymbol<IntExpr>(c.MkInt(6), SymbolTag.Return);
//			
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> la = Arrays.asList(i1); 						
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> lb = Arrays.asList(c5,r6);
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> lab = Arrays.asList(c5,r5);
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> lnot = Arrays.asList(c1,r5); 
//
//
//			assertTrue(autA.accepts(la, ba));
//			assertFalse(autA.accepts(lb, ba));
//			assertTrue(autA.accepts(lab, ba));
//			assertFalse(autA.accepts(lnot, ba));
//
//			assertTrue(totA.accepts(la, ba));
//			assertFalse(totA.accepts(lb, ba));
//			assertTrue(totA.accepts(lab, ba));
//			assertFalse(totA.accepts(lnot, ba));
//
//		} catch (Z3Exception e) {
//			System.out.print(e);
//		} catch (AutomataException e) {
//			System.out.print(e);
//		}
//	}
//	
//	@Test
//	public void testComplement() {
//		try {
//			Context c = new Context();
//			Z3Provider<IntExpr> z3p = new Z3Provider<IntExpr>(c, c.IntSort());
//			BooleanAlgebra<Predicate<IntExpr>, IntExpr> ba = z3p;
//
//			SVPA<Predicate<IntExpr>, IntExpr> autA = getSVPAa(c, ba);			
//			
//			SVPA<Predicate<IntExpr>, IntExpr> complementA = autA
//					.complement(ba);
//			
//			
//			
//			SVPA<Predicate<IntExpr>, IntExpr> autB = getSVPAb(c, ba);		
//			
//			SVPA<Predicate<IntExpr>, IntExpr> complementB= autB
//					.complement(ba);
//
//			TaggedSymbol<IntExpr> c1 = new TaggedSymbol<IntExpr>(c.MkInt(1), SymbolTag.Call);
//			TaggedSymbol<IntExpr> i1 = new TaggedSymbol<IntExpr>(c.MkInt(1), SymbolTag.Internal);
//			
//			TaggedSymbol<IntExpr> c5 = new TaggedSymbol<IntExpr>(c.MkInt(5), SymbolTag.Call);
//			TaggedSymbol<IntExpr> r5 = new TaggedSymbol<IntExpr>(c.MkInt(5), SymbolTag.Return);
//			TaggedSymbol<IntExpr> r6 = new TaggedSymbol<IntExpr>(c.MkInt(6), SymbolTag.Return);
//			
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> la = Arrays.asList(i1); 						
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> lb = Arrays.asList(c5,r6);
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> lab = Arrays.asList(c5,r5);
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> lnot = Arrays.asList(c1,r5); 
//
//
//			assertTrue(autA.accepts(la, ba));
//			assertFalse(autA.accepts(lb, ba));
//			assertTrue(autA.accepts(lab, ba));
//			assertFalse(autA.accepts(lnot, ba));
//
//			assertFalse(complementA.accepts(la, ba));
//			assertTrue(complementA.accepts(lb, ba));
//			assertFalse(complementA.accepts(lab, ba));
//			assertTrue(complementA.accepts(lnot, ba));
//			
//			assertFalse(autB.accepts(la, ba));
//			assertTrue(autB.accepts(lb, ba));
//			assertTrue(autB.accepts(lab, ba));
//			assertFalse(autB.accepts(lnot, ba));
//
//			assertTrue(complementB.accepts(la, ba));
//			assertFalse(complementB.accepts(lb, ba));
//			assertFalse(complementB.accepts(lab, ba));
//			assertTrue(complementB.accepts(lnot, ba));
//
//		} catch (Z3Exception e) {
//			System.out.print(e);
//		} catch (AutomataException e) {
//			System.out.print(e);
//		}
//	}
//	
//	@Test
//	public void testDeterminization() {
//		try {
//			Context c = new Context();
//			Z3Provider<IntExpr> z3p = new Z3Provider<IntExpr>(c, c.IntSort());
//			BooleanAlgebra<Predicate<IntExpr>, IntExpr> ba = z3p;
//
//			SVPA<Predicate<IntExpr>, IntExpr> autA = getSVPAa(c, ba);			
//			
//			SVPA<Predicate<IntExpr>, IntExpr> detA= autA
//					.determinize(ba);
//			
//			TaggedSymbol<IntExpr> c1 = new TaggedSymbol<IntExpr>(c.MkInt(1), SymbolTag.Call);
//			TaggedSymbol<IntExpr> i1 = new TaggedSymbol<IntExpr>(c.MkInt(1), SymbolTag.Internal);
//			
//			TaggedSymbol<IntExpr> c5 = new TaggedSymbol<IntExpr>(c.MkInt(5), SymbolTag.Call);
//			TaggedSymbol<IntExpr> r5 = new TaggedSymbol<IntExpr>(c.MkInt(5), SymbolTag.Return);
//			TaggedSymbol<IntExpr> r6 = new TaggedSymbol<IntExpr>(c.MkInt(6), SymbolTag.Return);
//			
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> la = Arrays.asList(c5,i1,r5); 						
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> lb = Arrays.asList(c5,r6);
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> lab = Arrays.asList(c5,r5);
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> lnot = Arrays.asList(c1,r5); 
//
//
//			assertTrue(autA.accepts(la, ba));
//			assertFalse(autA.accepts(lb, ba));
//			assertTrue(autA.accepts(lab, ba));
//			assertFalse(autA.accepts(lnot, ba));
//
//			assertTrue(detA.accepts(la, ba));
//			assertFalse(detA.accepts(lb, ba));
//			assertTrue(detA.accepts(lab, ba));
//			assertFalse(detA.accepts(lnot, ba));
//
//		} catch (Z3Exception e) {
//			System.out.print(e);
//		} catch (AutomataException e) {
//			System.out.print(e);
//		}
//	}
//	
//	@Test
//	public void testEpsilonRem() {
//		try {
//			Context c = new Context();
//			Z3Provider<IntExpr> z3p = new Z3Provider<IntExpr>(c, c.IntSort());
//			BooleanAlgebra<Predicate<IntExpr>, IntExpr> ba = z3p;
//
//			SVPA<Predicate<IntExpr>, IntExpr> autA = getSVPAa(c, ba);			
//			
//			SVPA<Predicate<IntExpr>, IntExpr> epsFree= autA
//					.removeEpsilonMoves(ba);
//			
//			TaggedSymbol<IntExpr> c1 = new TaggedSymbol<IntExpr>(c.MkInt(1), SymbolTag.Call);
//			TaggedSymbol<IntExpr> i1 = new TaggedSymbol<IntExpr>(c.MkInt(1), SymbolTag.Internal);
//			
//			TaggedSymbol<IntExpr> c5 = new TaggedSymbol<IntExpr>(c.MkInt(5), SymbolTag.Call);
//			TaggedSymbol<IntExpr> r5 = new TaggedSymbol<IntExpr>(c.MkInt(5), SymbolTag.Return);
//			TaggedSymbol<IntExpr> r6 = new TaggedSymbol<IntExpr>(c.MkInt(6), SymbolTag.Return);
//			
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> la = Arrays.asList(i1); 						
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> lb = Arrays.asList(c5,r6);
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> lab = Arrays.asList(c5,r5);
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> lnot = Arrays.asList(c1,r5); 
//
//
//			assertTrue(autA.accepts(la, ba));
//			assertFalse(autA.accepts(lb, ba));
//			assertTrue(autA.accepts(lab, ba));
//			assertFalse(autA.accepts(lnot, ba));
//
//			assertTrue(epsFree.accepts(la, ba));
//			assertFalse(epsFree.accepts(lb, ba));
//			assertTrue(epsFree.accepts(lab, ba));
//			assertFalse(epsFree.accepts(lnot, ba));
//
//		} catch (Z3Exception e) {
//			System.out.print(e);
//		} catch (AutomataException e) {
//			System.out.print(e);
//		}
//	}
//	
//	
//	@Test
//	public void testDiff() {
//		try {
//			Context c = new Context();
//			Z3Provider<IntExpr> z3p = new Z3Provider<IntExpr>(c, c.IntSort());
//			BooleanAlgebra<Predicate<IntExpr>, IntExpr> ba = z3p;
//
//			SVPA<Predicate<IntExpr>, IntExpr> autA = getSVPAa(c, ba);
//			
//			SVPA<Predicate<IntExpr>, IntExpr> autB = getSVPAb(c, ba);
//			
//			SVPA<Predicate<IntExpr>, IntExpr> diff = autA
//					.minus(autB, z3p);
//			
//
//			TaggedSymbol<IntExpr> c1 = new TaggedSymbol<IntExpr>(c.MkInt(1), SymbolTag.Call);
//			TaggedSymbol<IntExpr> i1 = new TaggedSymbol<IntExpr>(c.MkInt(1), SymbolTag.Internal);
//			
//			TaggedSymbol<IntExpr> c5 = new TaggedSymbol<IntExpr>(c.MkInt(5), SymbolTag.Call);
//			TaggedSymbol<IntExpr> r5 = new TaggedSymbol<IntExpr>(c.MkInt(5), SymbolTag.Return);
//			TaggedSymbol<IntExpr> r6 = new TaggedSymbol<IntExpr>(c.MkInt(6), SymbolTag.Return);
//			
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> a = Arrays.asList(i1); 						
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> b = Arrays.asList(c5,r6);
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> ab = Arrays.asList(c5,r5);
//			@SuppressWarnings("unchecked")
//			List<TaggedSymbol<IntExpr>> notab = Arrays.asList(c1,r5); 	
//			
//			assertTrue(autA.accepts(a, ba));
//			assertFalse(autA.accepts(b, ba));
//			assertTrue(autA.accepts(ab, ba));
//			assertFalse(autA.accepts(notab, ba));
//			
//			assertTrue(autB.accepts(b, ba));
//			assertFalse(autB.accepts(a, ba));
//			assertTrue(autB.accepts(ab, ba));
//			assertFalse(autB.accepts(notab, ba));
//			
//			assertFalse(diff.accepts(ab, ba));
//			assertTrue(diff.accepts(a, ba));
//			assertFalse(diff.accepts(b, ba));
//			assertFalse(diff.accepts(notab, ba));
//
//		} catch (Z3Exception e) {
//			System.out.print(e);
//		} catch (AutomataException e) {
//			System.out.print(e);
//		}
//	}
//	
//	@Test
//	public void testEquivalence() {
//		try {
//			Context c = new Context();
//			Z3Provider<IntExpr> z3p = new Z3Provider<IntExpr>(c, c.IntSort());
//			BooleanAlgebra<Predicate<IntExpr>, IntExpr> ba = z3p;
//
//			SVPA<Predicate<IntExpr>, IntExpr> autA = getSVPAa(c, ba);
//			
//			SVPA<Predicate<IntExpr>, IntExpr> cA = autA
//					.complement(ba);						
//			
//			SVPA<Predicate<IntExpr>, IntExpr> cUcA = autA
//					.unionWith(cA, ba);
//			
//			SVPA<Predicate<IntExpr>, IntExpr> ccA = cA
//					.complement(ba);
//			
//			SVPA<Predicate<IntExpr>, IntExpr> autB = getSVPAb(c, ba);
//			
//			SVPA<Predicate<IntExpr>, IntExpr> cB = autB
//					.complement(ba);	
//			
//			SVPA<Predicate<IntExpr>, IntExpr> cUcB = autB
//					.unionWith(cB, ba);
//						
//			
//			SVPA<Predicate<IntExpr>, IntExpr> ccB = cB
//					.complement(ba);
//			
//			assertFalse(autA.isEquivalentTo(autB, ba));	
//			
//			autA.createDotFile("a", "");
//			autA.removeEpsilonMoves(ba).createDotFile("ae", "");
//			autA.removeEpsilonMoves(ba).mkTotal(ba).createDotFile("at", "");
//			autA.createDotFile("a1", "");				
//			autA.createDotFile("a1", "");
//			autA.minus(ccA, ba).createDotFile("diff1", "");
//			ccA.minus(autA, ba).createDotFile("diff2", "");			
//			
//			cA.createDotFile("ca", "");
//			ccA.createDotFile("cca", "");
//			
//			assertTrue(autA.isEquivalentTo(ccA, ba));			
//			
//			assertTrue(autB.isEquivalentTo(ccB, ba));
//			
//			assertTrue(autA.isEquivalentTo(autA.intersectionWith(autA, ba), ba));
//			assertTrue(SVPA.getEmptySVPA(ba).isEquivalentTo(autA.minus(autA, ba), ba));
//
//			assertTrue(cUcA.isEquivalentTo(SVPA.getFullSVPA(ba), ba));
//			
//			assertTrue(cUcB.isEquivalentTo(SVPA.getFullSVPA(ba), ba));
//			assertTrue(cUcB.isEquivalentTo(cUcA, ba));
//			assertFalse(autB.isEquivalentTo(autA, ba));
//
//		} catch (Z3Exception e) {
//			System.out.print(e);
//		} catch (AutomataException e) {
//			System.out.print(e);
//		}
//	}
//	
//	@Test
//	public void testEpsRemove() {
//		try {
//			Context c = new Context();
//			Z3Provider<IntExpr> z3p = new Z3Provider<IntExpr>(c, c.IntSort());
//			BooleanAlgebra<Predicate<IntExpr>, IntExpr> ba = z3p;
//			
//			//First Automaton
//			SVPA<Predicate<IntExpr>, IntExpr> autA = getSVPAa(c, ba);
//			
//			//Second Automaton
//			SVPA<Predicate<IntExpr>, IntExpr> autAnoEps = autA.removeEpsilonMoves(ba);			
//			
//			assertFalse(autA.isEpsilonFree);
//			assertTrue(autAnoEps.isEpsilonFree);
//
//		} catch (Z3Exception e) {
//			System.out.print(e);
//		} catch (AutomataException e) {
//			System.out.print(e);
//		}
//	}
//	
//	@Test
//	public void testGetWitness() {
//		try {
//			Context c = new Context();
//			Z3Provider<IntExpr> z3p = new Z3Provider<IntExpr>(c, c.IntSort());
//			BooleanAlgebra<Predicate<IntExpr>, IntExpr> ba = z3p;
//
//			SVPA<Predicate<IntExpr>, IntExpr> autA = getSVPAa(c, ba);
//
//			autA.getWitness(ba);
//
//		} catch (Z3Exception e) {
//			System.out.print(e);
//		} catch (AutomataException e) {
//			System.out.print(e);
//		}
//	}	
//	
//	@Test
//	public void testReachRem() {
//		try {
//			Context c = new Context();
//			Z3Provider<IntExpr> z3p = new Z3Provider<IntExpr>(c, c.IntSort());
//			BooleanAlgebra<Predicate<IntExpr>, IntExpr> ba = z3p;
//						
//			Predicate<IntExpr> geq0 = new Predicate<IntExpr>("x", c.MkGe(
//					(IntExpr) c.MkConst(c.MkSymbol("x"), c.IntSort()),
//					c.MkInt(0)), c.IntSort());
//			
//			Collection<SVPAMove<Predicate<IntExpr>, IntExpr>> transA = new LinkedList<SVPAMove<Predicate<IntExpr>, IntExpr>>();
//			transA.add(new Call<Predicate<IntExpr>, IntExpr>(0,1,0,
//					geq0));
//			
//			transA.add(new Return<Predicate<IntExpr>, IntExpr>(1,2,0,
//					geq0));	
//			
//			//First Automaton
//			SVPA<Predicate<IntExpr>, IntExpr> autA = SVPA.MkSVPA(transA,
//					Arrays.asList(0), Arrays.asList(2), ba);		
//			
//			assertFalse(autA.isEmpty);
//
//		} catch (Z3Exception e) {
//			System.out.print(e);
//		} catch (AutomataException e) {
//			System.out.print(e);
//		}
//	}
//	
//	private SVPA<Predicate<IntExpr>, IntExpr> getSVPAa(Context c, BooleanAlgebra<Predicate<IntExpr>, IntExpr> ba) throws Z3Exception, AutomataException{
//		
//		Predicate<IntExpr> geq0 = new Predicate<IntExpr>("x", c.MkGe(
//				(IntExpr) c.MkConst(c.MkSymbol("x"), c.IntSort()),
//				c.MkInt(0)), c.IntSort());
//		Predicate<IntExpr> geq4 = new Predicate<IntExpr>("y", c.MkLe(
//				(IntExpr) c.MkConst(c.MkSymbol("y"), c.IntSort()),
//				c.MkInt(4)), c.IntSort());
//
//		BinaryPredicate<IntExpr> eqcalret = new BinaryPredicate<IntExpr>(
//				"x", "y", c.MkEq(c.MkConst(c.MkSymbol("x"), c.IntSort()),
//						c.MkConst(c.MkSymbol("y"), c.IntSort())),
//				c.IntSort());
//		
//		Collection<SVPAMove<Predicate<IntExpr>, IntExpr>> transA = new LinkedList<SVPAMove<Predicate<IntExpr>, IntExpr>>();
//		transA.add(new Internal<Predicate<IntExpr>, IntExpr>(1,1,
//				geq4));			
//		transA.add(new ReturnBS<Predicate<IntExpr>, IntExpr>(0, 0,
//				geq4));
//
//		transA.add(new Call<Predicate<IntExpr>, IntExpr>(0, 0, 0, geq0));			
//
//		transA.add(new Return<Predicate<IntExpr>, IntExpr>(0, 1, 0,
//				eqcalret));
//		transA.add(new Epsilon<Predicate<IntExpr>, IntExpr>(0, 1));
//		transA.add(new Return<Predicate<IntExpr>, IntExpr>(1, 1, 0,
//				eqcalret));
//
//		return SVPA.MkSVPA(transA,
//				Arrays.asList(0), Arrays.asList(1), ba);
//			
//	}
//
//	private SVPA<Predicate<IntExpr>, IntExpr> getSVPAb(Context c, BooleanAlgebra<Predicate<IntExpr>, IntExpr> ba) throws Z3Exception, AutomataException{
//		
//		Predicate<IntExpr> geq0 = new Predicate<IntExpr>("x", c.MkGe(
//				(IntExpr) c.MkConst(c.MkSymbol("x"), c.IntSort()),
//				c.MkInt(0)), c.IntSort());
//
//		BoolExpr[] be = {
//				c.MkGe((IntExpr) c.MkConst(c.MkSymbol("x"), c.IntSort()),
//						c.MkInt(5)),
//				c.MkGe((IntExpr) c.MkConst(c.MkSymbol("y"), c.IntSort()),
//						c.MkInt(5)) };
//		BinaryPredicate<IntExpr> geq5both = new BinaryPredicate<IntExpr>(
//				"x", "y", c.MkAnd(be), c.IntSort());
//		
//		Collection<SVPAMove<Predicate<IntExpr>, IntExpr>> transB = new LinkedList<SVPAMove<Predicate<IntExpr>, IntExpr>>();
//		
//		transB.add(new Call<Predicate<IntExpr>, IntExpr>(0, 0,0,
//				geq0));		
//
//		transB.add(new Return<Predicate<IntExpr>, IntExpr>(0, 0, 0,
//				geq5both));
//
//		return SVPA.MkSVPA(transB,
//				Arrays.asList(0), Arrays.asList(0), ba);
//	}

}
