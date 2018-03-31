/**
 * SVPAlib
 * transducers.sft
 * Mar 5, 2018
 * @author Loris D'Antoni
 */
package transducers.sft;

import java.util.*;

import org.sat4j.specs.TimeoutException;

import automata.Automaton;
import automata.Move;
import automata.sfa.SFA;
import automata.sfa.SFAMove;
import automata.sfa.SFAEpsilon;
import automata.sfa.SFAInputMove;
import theory.BooleanAlgebraSubst;
import utilities.Pair;



/**
 * A symbolic finite state transducer
 * modified from the model given by the paper named after Symbolic Finite State Transducers: Algorithms And Applications
 *
 * @param <P>
 *            The type of predicates forming the Boolean algebra
 * @param <F>
 *            The type of functions S->S in the Boolean Algebra
 * @param <S>
 *            The domain of the Boolean algebra
 */
public class SFT<P, F, S> extends Automaton<P, S> {

    // SFT properties
    protected Collection<Integer> states;
    protected Integer initialState;
    protected Collection<Integer> finalStates;
    protected Integer maxStateId;

    // Moves are inputs or epsilon
    protected Map<Integer, Collection<SFTInputMove<P, F, S>>> transitionsFrom;
    protected Map<Integer, Collection<SFTInputMove<P, F, S>>> transitionsTo;

    protected Map<Integer, Collection<SFTEpsilon<P, F, S>>> epsTransitionsFrom;
    protected Map<Integer, Collection<SFTEpsilon<P, F, S>>> epsTransitionsTo;

    public Integer stateCount() {
        return states.size();
    }

    public Integer transitionCount() {
        return getTransitions().size();
    }

    protected SFT() {
        super();
        states = new HashSet<Integer>();
        transitionsFrom = new HashMap<Integer, Collection<SFTInputMove<P, F, S>>>();
        transitionsTo = new HashMap<Integer, Collection<SFTInputMove<P, F, S>>>();
        epsTransitionsFrom = new HashMap<Integer, Collection<SFTEpsilon<P, F, S>>>();
        epsTransitionsTo = new HashMap<Integer, Collection<SFTEpsilon<P, F, S>>>();
        finalStates = new HashSet<Integer>();
        maxStateId = 0;
        initialState = 0;
    }

    /*
    * Create a SFT (removes unreachable states)
    * Page 3, left column, the last 4 lines, definition 2
    */
    public static <P, F, S> SFT<P, F, S> MkSFT(Collection<SFTMove<P, F, S>> transitions, Integer initialState,
                                                     Collection<Integer> finalStates,
                                                     BooleanAlgebraSubst<P, F, S> ba) {
        SFT<P, F, S> aut = new SFT<P, F, S>();

        // Initialize state set
        aut.initialState = initialState;
        aut.finalStates = finalStates;
        aut.states = new HashSet<Integer>();
        aut.states.add(initialState);
        aut.states.addAll(finalStates);
        
        try {
            for (SFTMove<P, F, S> t : transitions)
                aut.addTransition(t, ba, false);
            return aut;
        } catch (TimeoutException toe) {
            return null;
        }
    }

    /**
     * Returns the empty SFT
     */
    public static <P, F, S> SFT<P, F, S> getEmptySFT(BooleanAlgebraSubst<P, F, S> ba) {
        SFT<P, F, S> aut = new SFT<P, F, S>();
        aut.states = new HashSet<Integer>();
        aut.states.add(0);
        aut.initialState = 0;
        aut.isDeterministic = true;
        aut.isEmpty = true;
        aut.isEpsilonFree = true;
        aut.maxStateId = 1;
        return aut;
    }

    public List<S> outpzutOn(List<S> input, BooleanAlgebraSubst<P, F, S> ba) throws TimeoutException {
        return outputOn(this, input, ba);
    }

    /**
     * Computes one of the ouptuts produced when reading input. Null if no such output exists
     *
     * @param input
     * @param ba
     * @return one output sequence, null if undefined
     * @throws TimeoutException
     */
    public static <P, F, S> List<S> outputOn(SFT<P, F, S> sftWithEps, List<S> input,
                                                 BooleanAlgebraSubst<P, F, S> ba) throws TimeoutException {
        List<S> output = new ArrayList<S>();

        // composition
        SFT<P, F, S> sft = sftWithEps.removeEpsilonMoves(ba);
        // Assume that there are no epsilon transitions for now
        int currentState = sft.getInitialState();

        for (S element : input) {
            Collection<SFTInputMove<P, F, S>> transitions = sft.getInputMovesFrom(currentState);
            boolean canMove = false;
            for (SFTInputMove<P, F, S> transition: transitions) {
                if (ba.HasModel(transition.guard, element)) {
                    for (F outputFunc: transition.outputFunctions)
                        output.add(ba.MkSubstFuncConst(outputFunc, element));
                    currentState = transition.to;
                    canMove = true;
                    break;
                }
            }
            if (!canMove)
                return null;
        }

        if (sft.getFinalStates().contains(currentState))
            return output;
        else
            return null;
    }

    /**
     * Computes the composition with <code>sft</code> as a new SFT
     * Page 4, right column, start from the first line, part 3.1
     *
     * @throws TimeoutException
     */
    public SFT<P, F, S> composeWith(SFT<P, F, S> sft, BooleanAlgebraSubst<P, F, S> ba) throws TimeoutException {
        return compose(this, sft, ba);
    }

    /**
     * Computes the composition of <code>sft1</code> and <code>sft2</code>
     * Page 4, right column, start from the first line, part 3.1
     *
     * @throws TimeoutException
     */
    public static <P, F, S> SFT<P, F, S> compose(SFT<P, F, S> sft1withEps, SFT<P, F, S> sft2withEps,
                                                       BooleanAlgebraSubst<P, F, S> ba) throws TimeoutException {
        // Remove epsilons
        SFT<P, F, S> sft1 = sft1withEps.removeEpsilonMoves(ba);
        SFT<P, F, S> sft2 = sft2withEps.removeEpsilonMoves(ba);

        Collection<SFTMove<P, F, S>> transitions = new ArrayList<SFTMove<P, F, S>>();
        Integer initialState;
        HashSet<Integer> sft1FinalStates = new HashSet<Integer>(sft1.getFinalStates());
        HashSet<Integer> sft2FinalStates = new HashSet<Integer>(sft2.getFinalStates());
        HashSet<Integer> finalStates = new HashSet<Integer>();

        HashMap<Pair<Integer, Integer>, Integer> reached = new HashMap<Pair<Integer, Integer>, Integer>();
        LinkedList<Pair<Integer, Integer>> toVisit = new LinkedList<Pair<Integer, Integer>>();

        // Add initial state
        Pair<Integer, Integer> p = new Pair<Integer, Integer>(sft1.initialState, sft2.initialState);
        initialState = 0;
        reached.put(p, initialState);
        toVisit.push(p);

        // depth first search
        while (!toVisit.isEmpty()) {
            Pair<Integer, Integer> currState = toVisit.pop();
            int currStateId = reached.get(currState);
            if (sft1FinalStates.contains(currState.first) && sft2FinalStates.contains(currState.second))
                // the composed sft accepts a string iff sft1 and sft2 both accept the string
                finalStates.add(currStateId);
            for (SFTInputMove<P, F, S> t1 : sft1.getInputMovesFrom(currState.first)) {
                List<List<SFTMove<P, F, S>>> chains = sft2.getPossibleTransitionChains(currState.second, t1.outputFunctions.size());
                for (List<SFTMove<P, F, S>> chain: chains) {
                    // according to the algorithm of method getPossibleTransitionChains, there is at least one SFTMove
                    // in a chain.
                    SFTInputMove<P, F, S> t2 = (SFTInputMove<P, F, S>)chain.get(0);
                    P internalGuard = ba.MkAnd(ba.MkSubstFuncPred(t1.outputFunctions.get(0), t1.guard) , t2.guard);
                    P intersGuard = ba.MkAnd(t1.guard, ba.GetAllPossibleInputs(t1.outputFunctions.get(0), internalGuard));
                    List<F> outputFunctions = new LinkedList<F>();
                    for (F t2OutputFunction: t2.outputFunctions)
                        outputFunctions.add(ba.MkSubstFuncFunc(t2OutputFunction, t1.outputFunctions.get(0)));
                    // initialize the composed guard.
                    // consider the transition p --phi/[F, f2]--> p' in sft1 and
                    // q --psi/[g]--> q' --gamma/[h] --> q'' in sft2, the composed transition should be
                    // (p, q) --phi and psi(F) and gamma(f2)/[g(F), h(f2)]--> (p', q'')
                    for (int i = 1; i < chain.size(); i++) {
                        t2 = (SFTInputMove<P, F, S>)chain.get(i);
                        internalGuard = ba.MkAnd(ba.MkSubstFuncPred(t1.outputFunctions.get(i), t1.guard) , t2.guard);
                        intersGuard = ba.MkAnd(intersGuard, ba.GetAllPossibleInputs(t1.outputFunctions.get(i), internalGuard));
                        for (F t2OutputFunction: t2.outputFunctions)
                            outputFunctions.add(ba.MkSubstFuncFunc(t2OutputFunction, t1.outputFunctions.get(i)));
                    }
                    if (ba.IsSatisfiable(intersGuard)) {
                        Pair<Integer, Integer> nextState = new Pair<Integer, Integer>(t1.to, chain.get(chain.size() - 1).to);
                        int nextStateId = getStateId(nextState, reached, toVisit);

                        SFTInputMove<P, F, S> newTrans = new SFTInputMove<P, F, S>(currStateId, nextStateId,
                                intersGuard, outputFunctions);

                        transitions.add(newTrans);
                    }
                }
            }
        }

        return MkSFT(transitions, initialState, finalStates, ba);
    }

    /**
     * return an equivalent copy without epsilon moves
     */
    protected SFT<P, F, S> removeEpsilonMoves(BooleanAlgebraSubst<P, F, S> ba) {
        return removeEpsilonMovesFrom(this, ba);
    }

    /**
     * return an equivalent copy without epsilon moves
     */
    protected static <P, F, S> SFT<P, F, S> removeEpsilonMovesFrom(SFT<P, F, S> sft,
                                                                         BooleanAlgebraSubst<P, F, S> ba) {

        if (sft.isEpsilonFree)
            return sft;

        Collection<SFTMove<P, F, S>> transitions = new ArrayList<SFTMove<P, F, S>>();
        Collection<Integer> finalStates = new HashSet<Integer>(sft.getFinalStates());

        for (Integer state: sft.getStates())
            transitions.addAll(sft.getInputMovesFrom(state));

        for (Integer state: sft.getStates()) {
            Map<Integer, List<Integer>> epsilonClosureAndPath = sft.getSFTEpsClosureAndPath(state);
            if (!finalStates.contains(state)) // update the final state
                for (Integer nextState: epsilonClosureAndPath.keySet())
                    if (sft.getFinalStates().contains(nextState)) {
                        finalStates.add(state);
                        break;
                    }
            for (Integer nextState: epsilonClosureAndPath.keySet())
                if (!nextState.equals(state)) {
                    List<Integer> path = epsilonClosureAndPath.get(nextState);
                    List<F> outputFuncAlongPath = new ArrayList<F>();
                    for (int i = 0; i < path.size() - 1; i++) {
                        for (SFTEpsilon<P, F, S> t: (sft.getEpsilonMovesFrom(path.get(i))))
                            if (path.get(i + 1).equals(t.to)) {
                                for (S output: t.outputs)
                                    outputFuncAlongPath.add(ba.MkFuncConst(output));
                                break;
                            }
                    }
                    for (SFTInputMove<P, F, S> nextInputMove: sft.getInputMovesFrom(nextState)) {
                        List<F> combinedOutputFunctions = new ArrayList<F>(outputFuncAlongPath);
                        combinedOutputFunctions.addAll(nextInputMove.outputFunctions);
                        transitions.add(new SFTInputMove<P, F, S>(state, nextInputMove.to, nextInputMove.guard, combinedOutputFunctions));
                    }
                }
        }

        return MkSFT(transitions, sft.initialState, finalStates, ba);
    }

    private <P, F, S> Map<Integer, List<Integer>> getSFTEpsClosureAndPath(Integer currentState) {
        return getSFTEpsClosureAndPath(this, currentState);
    }

    /**
     * get the set of all states reachable from <code>currentState</code> using zero or more epsilon transitions
     * and all corresponding transition paths.
     * @param sft symbolic finite transducer
     * @param currentState the start point
     */
    private static <P, F, S> Map<Integer, List<Integer>> getSFTEpsClosureAndPath(SFT<P, F, S> sft, Integer currentState) {
        Map<Integer, List<Integer>> epsilonClosureAndPath = new HashMap<Integer, List<Integer>>();

        Collection<Integer> reached = new HashSet<Integer>(currentState);
        LinkedList<Integer> toVisit = new LinkedList<Integer>();
        toVisit.add(currentState);
        List<Integer> path = new ArrayList<Integer>();
        path.add(currentState);
        epsilonClosureAndPath.put(currentState, path);

        while (toVisit.size() > 0) {
            int fromState = toVisit.pop();
            for (SFTEpsilon<P, F, S> t : sft.getEpsilonMovesFrom(fromState)) {
                if (!reached.contains(t.to)) {
                    reached.add(t.to);
                    toVisit.add(t.to);
                    List<Integer> newPath = epsilonClosureAndPath.get(fromState);
                    newPath.add(t.to);
                    epsilonClosureAndPath.put(t.to, newPath);
                } else {
                    throw new IllegalArgumentException(
                            "the epsilon transitions cause ambiguity (" + "their relation not a tree)");
                }
            }
        }

        return epsilonClosureAndPath;
    }

    private <P, F, S> Collection<Integer> getSFTEpsClosure(Integer currentState) {
        return getSFTEpsClosure(this, currentState);
    }

    /**
     * get the set of all states reachable from <code>currentState</code> using zero or more epsilon transitions
     * @param sft symbolic finite transducer
     * @param currentState the start point
     */
    private static <P, F, S> Collection<Integer> getSFTEpsClosure(SFT<P, F, S> sft, Integer currentState) {
        Collection<Integer> epsilonClosure = new HashSet<Integer>();

        Collection<Integer> reached = new HashSet<Integer>(currentState);
        LinkedList<Integer> toVisit = new LinkedList<Integer>();
        toVisit.add(currentState);
        epsilonClosure.add(currentState);

        while (toVisit.size() > 0) {
            int fromState = toVisit.pop();
            for (SFTEpsilon<P, F, S> t : sft.getEpsilonMovesFrom(fromState)) {
                if (!reached.contains(t.to)) {
                    reached.add(t.to);
                    toVisit.add(t.to);
                    epsilonClosure.add(t.to);
                } else {
                    throw new IllegalArgumentException(
                            "the epsilon transitions cause ambiguity (" + "their relation not a tree)");
                }
            }
        }

        return epsilonClosure;
    }

    private List<List<SFTMove<P, F, S>>> getPossibleTransitionChains(Integer startState, int steps) {
        return possibleTransitionChains(this, startState, steps);
    }

    /**
     * Given a symbolic finite transducer and the start state, return all possible transition chains in
     * <code>steps</code> steps
     * @param sft symbolic finite transducer
     * @param startState the start state which could be any state, including but not limited to the initial state
     * @param steps the number of steps, which should be a natural number
     * @return
     */
    private static <P, F, S> List<List<SFTMove<P, F, S>>> possibleTransitionChains(SFT<P, F, S> sft, Integer startState, int steps) {
        List<List<SFTMove<P, F, S>>> chains = new ArrayList<List<SFTMove<P, F, S>>>();
        for (SFTMove<P, F, S> initialTransition: sft.getInputMovesFrom(startState)) {
            List<SFTMove<P, F, S>> tempList = new LinkedList<SFTMove<P, F, S>>();
            tempList.add(initialTransition);
            backtrack(chains, tempList, sft, steps - 1);
        }
        return chains;
    }

    // use backtrack method to get all possible transition chains
    private static <P, F, S> void backtrack(List<List<SFTMove<P, F, S>>> chains, List<SFTMove<P, F, S>> tempList, SFT<P, F, S> sft, int remainSteps) {
        if (remainSteps < 0)
            return; // no solution
        else if (remainSteps == 0)
            chains.add(new ArrayList<SFTMove<P, F, S>>(tempList));
        else {
            Integer currentState = tempList.get(tempList.size() - 1).to;
            for (SFTMove<P, F, S> transition: sft.getTransitionsFrom(currentState)) {
                tempList.add(transition);
                backtrack(chains, tempList, sft, remainSteps - 1);
                tempList.remove(tempList.size() - 1);
            }
        }
    }

    public boolean decide1equality(SFT<P, F, S> otherSft, BooleanAlgebraSubst<P, F, S> ba) {
        return decide1equality(this, otherSft, ba);
    }

    /**
     * judge whether <code>sft1withEps</code> and <code>sft2withEps</code> are 1-equality
     * Page 6, in the middle of left column, figure 3
     *
     * @param sft1withEps symbolic finite transducer 1 who may has epsilon transitions
     * @param sft2withEps symbolic finite transducer 2 who may has epsilon transitions
     */
    public static <P, F, S> boolean decide1equality(SFT<P, F, S> sft1withEps, SFT<P, F, S> sft2withEps, BooleanAlgebraSubst<P, F, S> ba) {
        SFTProduct<P, F, S> product = SFTProduct.MkSFTProduct(sft1withEps, sft2withEps, ba);
        // a product without epsilon transitions

        HashMap<Integer, Pair<List<S>, List<S>>> reached = new HashMap<Integer, Pair<List<S>, List<S>>>();
        LinkedList<Integer> toVisit = new LinkedList<Integer>();

        reached.put(product.getInitialState(), new Pair(new ArrayList<S>(), new ArrayList<S>()));
        toVisit.add(product.getInitialState());
        while (!toVisit.isEmpty()) {
            Integer currState = toVisit.pop();
            Pair<List<S>, List<S>> promise = reached.get(currState);
            for (SFTProductInputMove transition: product.getInputMovesFrom(currState)) {
                List<F> u = new ArrayList<F>();
                List<F> v = new ArrayList<F>();
                for (S a: promise.first)
                    u.add(ba.MkFuncConst(a));
                for (S b: promise.second)
                    v.add(ba.MkFuncConst(b));
                u.addAll(transition.outputFunctions1);
                v.addAll(transition.outputFunctions2);
                if (product.getFinalStates().contains(currState) && u.size() != v.size())
                    return false;
                if (u.size() >= v.size()) {
                    for (int i = 0; i < v.size(); i++)
                        if (!u.get(i).equals(v.get(i)))
                            return false;
                    List<F> w = new ArrayList<F>();
                    for (int i = v.size(); i < u.size(); i++)
                        w.add(u.get(i));
                    S witness = null;
                    try {
                        witness = (S)transition.getWitness(ba);
                    } catch (TimeoutException te) {
                        te.printStackTrace();
                    }
                    List<S> c = new ArrayList<S>();
                    List<F> cF = new ArrayList<F>(); // a sequence of lamda x.alpha, where alpha is a constant S
                    // stored in List<S> c
                    for (int i = 0; i < u.size() - v.size(); i++) {
                        c.add(ba.MkSubstFuncConst(w.get(i), witness));
                        cF.add(ba.MkSubstFuncFunc(w.get(i), ba.MkFuncConst(witness)));
                    }
                    for (int i = 0; i < u.size() - v.size(); i++)
                        try {
                            if (!ba.AreEquivalent(ba.MkSubstFuncPred(w.get(i), (P)transition.guard),
                                    ba.MkSubstFuncPred(cF.get(i), (P)transition.guard)))
                                return false;
                        } catch (TimeoutException te) {
                            te.printStackTrace();
                        }
                    if (reached.containsKey(currState) && !reached.get(currState).equals(new Pair(c, new ArrayList<S>())))
                        return false;
                    if (!reached.containsKey(currState)) {
                        toVisit.push(currState);
                        reached.put(currState, new Pair(c, new ArrayList<S>()));
                    }
                } else { // u.size() < v.size()
                    for (int i = 0; i < u.size(); i++)
                        if (!u.get(i).equals(v.get(i)))
                            return false;
                    List<F> w = new ArrayList<F>();
                    for (int i = u.size(); i < v.size(); i++)
                        w.add(u.get(i));
                    S witness = null;
                    try {
                        witness = (S)transition.getWitness(ba);
                    } catch (TimeoutException te) {
                        te.printStackTrace();
                    }
                    List<S> c = new ArrayList<S>();
                    List<F> cF = new ArrayList<F>(); // a sequence of lamda x.alpha, where alpha is a constant S
                    // stored in List<S> c
                    for (int i = 0; i < v.size() - u.size(); i++) {
                        c.add(ba.MkSubstFuncConst(w.get(i), witness));
                        cF.add(ba.MkSubstFuncFunc(w.get(i), ba.MkFuncConst(witness)));
                    }
                    for (int i = 0; i < v.size() - u.size(); i++)
                        try {
                            if (!ba.AreEquivalent(ba.MkSubstFuncPred(w.get(i), (P)transition.guard),
                                    ba.MkSubstFuncPred(cF.get(i), (P)transition.guard)))
                                return false;
                        } catch (TimeoutException te) {
                            te.printStackTrace();
                        }
                    if (reached.containsKey(currState) && !reached.get(currState).equals(new Pair(new ArrayList<S>(), c)))
                        return false;
                    if (!reached.containsKey(currState)) {
                        toVisit.push(currState);
                        reached.put(currState, new Pair(new ArrayList<S>(), c));
                    }
                }
            }
        }
        return true;
    }
    
    /**
     * Computes the domain automaton of the sft
     * Page 4, right column, the 17th line from the bottom
     *
     * @throws TimeoutException
     */
    public SFA<P, S> getDomain(BooleanAlgebraSubst<P, F, S> ba) throws TimeoutException {
        Collection<SFAMove<P, S>> transitions = new ArrayList<SFAMove<P, S>>();

        for (SFTInputMove<P, F, S> t : getInputMovesFrom(states))
            transitions.add(new SFAInputMove<P, S>(t.from, t.to, t.guard));

        for (SFTEpsilon<P, F, S> t : getEpsilonMovesFrom(states))
            transitions.add(new SFAEpsilon<P, S>(t.from, t.to));

        Collection<Integer> finalStates = getFinalStates();

        return SFA.MkSFA(transitions, initialState, finalStates, ba);
    }

    /**
     * compute the inverse image under <code>sfa</code>, which means compose(sft, sfa)
     * Page 7, left column, the first line
     * 
     * @throws TimeoutException
     */
    public SFA<P, S> inverseImage(SFA<P, S> sfaWithEps, BooleanAlgebraSubst<P, F, S> ba) throws TimeoutException {
        // Remove epsilons
        SFA<P, S> sfa = sfaWithEps.removeEpsilonMoves(ba);
        SFT<P, F, S> composition = this.composeWith(toSFT(sfa, ba), ba);
        return composition.getDomain(ba);
    }

    /**
     * convert a sfa to a sft by adding empty set of output functions in every transition
     * 
     * @return corresponding sft
     */
    private static <P, F, S> SFT<P, F, S> toSFT(SFA<P, S> sfa, BooleanAlgebraSubst<P, F, S> ba) {
        Collection<SFTMove<P, F, S>> transitions = new ArrayList<SFTMove<P, F, S>>();
        for (Integer state: sfa.getStates()) {
            for (SFAInputMove<P, S> transition: sfa.getInputMovesFrom(state)) {
                List<F> outputFunctions = new LinkedList<F>();
                SFTInputMove<P, F, S> newTrans = new SFTInputMove<P, F, S>(transition.from, transition.to,
                        transition.guard, outputFunctions);
                transitions.add(newTrans);
            }
            for (SFAEpsilon<P, S> transition: sfa.getEpsilonFrom(state)) {
                List<S> outputs = new LinkedList<S>();
                SFTEpsilon<P, F, S> newTrans = new SFTEpsilon<P, F, S>(transition.from, transition.to, outputs);
            }
        }
        return MkSFT(transitions, sfa.getInitialState(), sfa.getFinalStates(), ba);
    }

    /**
     * compute the domain restriction of <code>this</code> for <code>sfaWithEps</code>
     * Page 7, left column, the 8th line
     *
     * @return the domain restriction of the current sft
     */
    public SFT<P, F, S> domainRestriction(SFA<P, S> sfaWithEps, BooleanAlgebraSubst<P, F, S> ba) {
        // Remove epsilons
        SFT<P, F, S> sft = this.removeEpsilonMoves(ba);
        SFA<P, S> sfa = null;
        try {
            sfa = sfaWithEps.removeEpsilonMoves(ba);
        } catch (TimeoutException te) {
            te.printStackTrace();
        }

        Collection<SFTMove<P, F, S>> transitions = new ArrayList<SFTMove<P, F, S>>();
        Integer initialState = 0;

        HashMap<Pair<Integer, Integer>, Integer> reached = new HashMap<Pair<Integer, Integer>, Integer>();
        LinkedList<Pair<Integer, Integer>> toVisit = new LinkedList<Pair<Integer, Integer>>();

        // Add initial state
        Pair<Integer, Integer> p = new Pair<Integer, Integer>(sft.getInitialState(), sfa.getInitialState());

        initialState = 0;
        HashSet<Integer> finalStates = new HashSet<Integer>();

        reached.put(p, initialState);
        toVisit.add(p);

        // Combined has set of variables the disjoint union of the two sets
        while (!toVisit.isEmpty()) {
            Pair<Integer, Integer> currState = toVisit.removeFirst();
            int currStateId = reached.get(currState);

            // If both states are final, combine is final
            if (sft.isFinalState(currState.first) && sfa.isFinalState(currState.second)) {
                finalStates.add(currStateId);
            }

            for (SFTInputMove<P, F, S> t1 : sft.getInputMovesFrom(currState.first))
                for (SFAInputMove<P, S> t2 : sfa.getInputMovesFrom(currState.second)) {
                    P intersGuard = null;
                    try {
                        intersGuard = ba.MkAnd(t1.guard, t2.guard);
                    } catch (TimeoutException te) {
                        te.printStackTrace();
                    }
                    boolean isSatisfiable = false;
                    try {
                        isSatisfiable = ba.IsSatisfiable(intersGuard);
                    } catch (TimeoutException te) {
                        te.printStackTrace();
                    }
                    if (isSatisfiable) {
                        Pair<Integer, Integer> nextState = new Pair<Integer, Integer>(t1.to, t2.to);
                        int nextStateId = getStateId(nextState, reached, toVisit); // update reached and toVisit
                        SFTInputMove<P, F, S> newTrans = new SFTInputMove<P, F, S>(currStateId, nextStateId,
                                intersGuard, t1.outputFunctions);
                        transitions.add(newTrans);
                    }
                }
        }
        return MkSFT(transitions, initialState, finalStates, ba);
    }

    /**
     * Add Transition
     *
     * @throws TimeoutException
     */
    private void addTransition(SFTMove<P, F, S> transition, BooleanAlgebraSubst<P, F, S> ba, boolean skipSatCheck)
            throws TimeoutException {

        if (transition.isEpsilonTransition()) {
            if (transition.to == transition.from)
                return;
            isEpsilonFree = false;
        }

        if (skipSatCheck || transition.isSatisfiable(ba)) {

            if (transition.from > maxStateId)
                maxStateId = transition.from;
            if (transition.to > maxStateId)
                maxStateId = transition.to;

            states.add(transition.from);
            states.add(transition.to);

            if (transition.isEpsilonTransition()) {
                getEpsilonMovesFrom(transition.from).add((SFTEpsilon<P, F, S>) transition);
                getEpsilonMovesTo(transition.to).add((SFTEpsilon<P, F, S>) transition);
            } else {
                getInputMovesFrom(transition.from).add((SFTInputMove<P, F, S>) transition);
                getInputMovesTo(transition.to).add((SFTInputMove<P, F, S>) transition);
            }
        }
    }

    /**
     * judge whether <code>otherSft</code> is just <code>this</code> itself
     * It is just used for debugging
     * @param otherSft a SFT
     * @return the result of whether <code>otherSft</code> is just <code>this</code> itself
     */
    public boolean isItself(SFT<P, F, S> otherSft) {
        if (this.isDeterministic != otherSft.isDeterministic)
            return false;
        if (this.isEmpty != otherSft.isEmpty)
            return false;
        if (this.isEpsilonFree != otherSft.isEpsilonFree)
            return false;
        if (this.maxStateId != otherSft.maxStateId)
            return false;
        if (this.states != otherSft.states)
            return false;
        if (this.initialState != otherSft.initialState)
            return false;
        if (this.transitionsFrom != otherSft.transitionsFrom)
            return false;
        if (this.transitionsTo != otherSft.transitionsTo)
            return false;
        if (this.epsTransitionsFrom != otherSft.epsTransitionsFrom)
            return false;
        if (this.epsTransitionsTo != otherSft.epsTransitionsTo)
            return false;
        if (this.finalStates != otherSft.finalStates)
            return false;
        return true;
    }

    // ACCESORIES METHODS

    // GET INPUT MOVES

    /**
     * Returns the set of transitions from state <code>s</code>
     */
    public Collection<SFTInputMove<P, F, S>> getInputMovesFrom(Integer state) {
        Collection<SFTInputMove<P, F, S>> trset = transitionsFrom.get(state);
        if (trset == null) {
            trset = new HashSet<SFTInputMove<P, F, S>>();
            transitionsFrom.put(state, trset);
        }
        return trset;
    }

    /**
     * Returns the set of transitions starting set of states
     */
    public Collection<SFTInputMove<P, F, S>> getInputMovesFrom(Collection<Integer> stateSet) {
        Collection<SFTInputMove<P, F, S>> transitions = new LinkedList<SFTInputMove<P, F, S>>();
        for (Integer state : stateSet)
            transitions.addAll(getInputMovesFrom(state));
        return transitions;
    }

    /**
     * Returns the set of input transitions to state <code>s</code>
     */
    public Collection<SFTInputMove<P, F, S>> getInputMovesTo(Integer state) {
        Collection<SFTInputMove<P, F, S>> trset = transitionsTo.get(state);
        if (trset == null) {
            trset = new HashSet<SFTInputMove<P, F, S>>();
            transitionsTo.put(state, trset);
        }
        return trset;
    }

    /**
     * Returns the set of transitions to set of states
     */
    public Collection<SFTInputMove<P, F, S>> getInputMovesTo(Collection<Integer> stateSet) {
        Collection<SFTInputMove<P, F, S>> transitions = new LinkedList<SFTInputMove<P, F, S>>();
        for (Integer state : stateSet)
            transitions.addAll(getInputMovesTo(state));
        return transitions;
    }

    // GET Epsilon MOVES

    /**
     * Returns the set of transitions to state <code>s</code>
     */
    public Collection<SFTEpsilon<P, F, S>> getEpsilonMovesFrom(Integer state) {
        Collection<SFTEpsilon<P, F, S>> trset = epsTransitionsFrom.get(state);
        if (trset == null) {
            trset = new HashSet<SFTEpsilon<P, F, S>>();
            epsTransitionsFrom.put(state, trset);
        }
        return trset;
    }

    /**
     * Returns the set of transitions starting set of states
     */
    public Collection<SFTEpsilon<P, F, S>> getEpsilonMovesFrom(Collection<Integer> stateSet) {
        Collection<SFTEpsilon<P, F, S>> transitions = new LinkedList<SFTEpsilon<P, F, S>>();
        for (Integer state : stateSet)
            transitions.addAll(getEpsilonMovesFrom(state));
        return transitions;
    }

    /**
     * Returns the set of input transitions to state <code>s</code>
     */
    public Collection<SFTEpsilon<P, F, S>> getEpsilonMovesTo(Integer state) {
        Collection<SFTEpsilon<P, F, S>> trset = epsTransitionsTo.get(state);
        if (trset == null) {
            trset = new HashSet<SFTEpsilon<P, F, S>>();
            epsTransitionsTo.put(state, trset);
        }
        return trset;
    }

    /**
     * Returns the set of transitions starting set of states
     */
    public Collection<SFTEpsilon<P, F, S>> getEpsilonMovesTo(Collection<Integer> stateSet) {
        Collection<SFTEpsilon<P, F, S>> transitions = new LinkedList<SFTEpsilon<P, F, S>>();
        for (Integer state : stateSet)
            transitions.addAll(getEpsilonMovesTo(state));
        return transitions;
    }

    // GET ALL MOVES
    /**
     * Returns the set of transitions starting at state <code>s</code>
     */
    public Collection<SFTMove<P, F, S>> getTransitionsFrom(Integer state) {
        Collection<SFTMove<P, F, S>> trset = new HashSet<SFTMove<P, F, S>>();
        trset.addAll(getInputMovesFrom(state));
        trset.addAll(getEpsilonMovesFrom(state));
        return trset;
    }

    /**
     * Returns the set of transitions starting at a set of states
     */
    public Collection<SFTMove<P, F, S>> getTransitionsFrom(Collection<Integer> stateSet) {
        Collection<SFTMove<P, F, S>> trset = new HashSet<SFTMove<P, F, S>>();
        trset.addAll(getInputMovesFrom(stateSet));
        trset.addAll(getEpsilonMovesFrom(stateSet));
        return trset;
    }

    /**
     * Returns the set of transitions to state <code>s</code>
     */
    public Collection<SFTMove<P, F, S>> getTransitionsTo(Integer state) {
        Collection<SFTMove<P, F, S>> trset = new HashSet<SFTMove<P, F, S>>();
        trset.addAll(getInputMovesTo(state));
        trset.addAll(getEpsilonMovesTo(state));
        return trset;
    }

    /**
     * Returns the set of transitions to a set of states
     */
    public Collection<SFTMove<P, F, S>> getTransitionsTo(Collection<Integer> stateSet) {
        Collection<SFTMove<P, F, S>> trset = new HashSet<SFTMove<P, F, S>>();
        trset.addAll(getInputMovesTo(stateSet));
        trset.addAll(getEpsilonMovesTo(stateSet));
        return trset;
    }

    /**
     * Returns the set of transitions starting set of states
     */
    public Collection<SFTMove<P, F, S>> getTransitions() {
        return getTransitionsFrom(states);
    }

    // Methods for superclass
    @Override
    public Collection<Move<P, S>> getMoves() {
        Collection<Move<P, S>> transitions = new LinkedList<Move<P, S>>();
        transitions.addAll(getTransitions());
        return transitions;
    }

    @Override
    public Collection<Move<P, S>> getMovesFrom(Integer state) {
        Collection<Move<P, S>> transitions = new LinkedList<Move<P, S>>();
        transitions.addAll(getTransitionsFrom(state));
        return transitions;
    }

    @Override
    public Collection<Move<P, S>> getMovesTo(Integer state) {
        Collection<Move<P, S>> transitions = new LinkedList<Move<P, S>>();
        transitions.addAll(getTransitionsTo(state));
        return transitions;
    }

    @Override
    public Collection<Integer> getFinalStates() {
        return finalStates;
    }

    @Override
    public Integer getInitialState() {
        return initialState;
    }

    @Override
    public Collection<Integer> getStates() {
        return states;
    }
    
    @Override
    public Object clone() {
        SFT<P, F, S> cl = new SFT<P, F, S>();

        cl.isDeterministic = isDeterministic;
        cl.isTotal = isTotal;
        cl.isEmpty = isEmpty;
        cl.isEpsilonFree = isEpsilonFree;

        cl.maxStateId = maxStateId;

        cl.states = new HashSet<Integer>(states);
        cl.initialState = initialState;

        cl.transitionsFrom = new HashMap<Integer, Collection<SFTInputMove<P, F, S>>>(transitionsFrom);
        cl.transitionsTo = new HashMap<Integer, Collection<SFTInputMove<P, F, S>>>(transitionsTo);

        cl.epsTransitionsFrom = new HashMap<Integer, Collection<SFTEpsilon<P, F, S>>>(epsTransitionsFrom);
        cl.epsTransitionsTo = new HashMap<Integer, Collection<SFTEpsilon<P, F, S>>>(epsTransitionsTo);

        cl.finalStates = new HashSet<Integer>(finalStates);

        return cl;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}