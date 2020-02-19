/**
 * Implementation of the discrimination (or classification) tree
 * 
 * @author George Argyros
 */

package algebralearning.sfa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.sat4j.specs.TimeoutException;

import algebralearning.oracles.MembershipOracle;


class TreeNode <D> {

    TreeNode <D> trueChild;
    TreeNode <D> falseChild;
    TreeNode <D> parent;
    List <D> label;
    Integer height;

	public TreeNode(List <D> l, TreeNode <D> p, TreeNode <D> t,
					TreeNode <D> f) {

		label = new ArrayList <D>(l);
        trueChild = t;
        falseChild = f;
		parent = p;
		height = 1;
    }
    
    public void setTrueChild(TreeNode <D> c) {
        trueChild = c;
    }

    public void setFalseChild(TreeNode <D> c) {
        falseChild = c;
    }

    public void setParent(TreeNode <D> p) {
        parent = p;
    }

    public void setLabel(List <D> l) {
        label = new ArrayList <D>(l);
    }
    
    public boolean isLeaf() {
        return (trueChild == null && falseChild == null);
    }

    public TreeNode <D> getTrueChild() {
        return trueChild;
    }

    public TreeNode <D> getFalseChild() {
        return falseChild;
    }

    public TreeNode <D> getParent() {
        return parent;
    }

	public List <D> getLabel() {
		return label;
    }
}


public class DiscriminationTree <D> {

    
    // Root of the tree 
    private TreeNode <D> root;
    // Nodes of the tree 
    private HashSet <List <D>> innerNodes;
    // Leafs 
    private List <List <D>> leafs;
    // Membership oracle to perform queries 
    MembershipOracle <List<D>> membOracle;
    // Map to locate leafs for replacement/addition 
    private Map <List<D>, TreeNode<D>> leafMap;
    private Boolean isMissingLeafFixed;


    public DiscriminationTree(MembershipOracle <List<D>> m) throws TimeoutException {
        List <D>emptyList = new ArrayList <D>();
        TreeNode <D> firstLeaf;

        // Initialize basic data structures 
        innerNodes = new HashSet <List <D>>();
        leafs = new LinkedList <List <D>>();
        leafMap = new HashMap <List<D>, TreeNode<D>>();
		isMissingLeafFixed = false;

        // Initialize the tree with two nodes labelled with the empty sequence */
        root = new TreeNode <D>(emptyList, null, null, null);
        firstLeaf = new TreeNode <D>(emptyList, root, null, null);
        if (m.query(emptyList)) {
            // empty sequence is a trueChild
            root.setTrueChild(firstLeaf);
        } else {
            // empty sequence is a falseChild
            root.setFalseChild(firstLeaf);
        }
        innerNodes.add(emptyList);
        leafs.add(emptyList);
        leafMap.put(emptyList, firstLeaf);
        membOracle = m;
    }
    
    
    public List <List<D>> getLeafs() {
    		return leafs;
    }
    
    
    /*************** Public methods ***************/

    public List <D> sift(List <D> word) throws TimeoutException {
    	/*
    	 * This method implements the sift algorithm as described in the
    	 * paper. The method will return the leaf for the given input word
    	 * or, if the corresponding leaf is the missing leaf from the root
    	 * node of tree, null will be returned.
    	 * 
    	 */
        TreeNode <D> curNode = root;
        List <D> q;
        while (!curNode.isLeaf()) {
			q = new ArrayList <D>(word);
            q.addAll(curNode.getLabel());
            if (membOracle.query(q)) {
                curNode = curNode.getTrueChild();
            } else {
                curNode = curNode.getFalseChild();
            }
            if (curNode == null) {
                // Signals the caller to call fixMissingAccessSequence in order
                // to replace the missing leaf.
                return null;
            }
        }
        return curNode.getLabel();
    }

    public void fixMissingAccessSequence(List <D> word) throws TimeoutException {
    	/*
    	 * If the sift operation returns null, the caller function can call this method
    	 * in order to add the missing leaf in the tree. Since there can be only one missing
    	 * leaf there is no need to specify a location parameter.
    	 */

        TreeNode <D> newChild = new TreeNode <D>(word, root, null, null);
        leafs.add(word);
        if (membOracle.query(word)) {
            root.setTrueChild(newChild);
        } else {        	
            root.setFalseChild(newChild);
        }
        leafMap.put(word, newChild);
        isMissingLeafFixed = true;
        return;
    }

    public void splitLeaf(List <D> leafLabel, List <D> newDist,
                          List <D> newLeaf) throws TimeoutException {
        TreeNode <D> newLeafNode = new TreeNode <D>(newLeaf, null, null, null);
        TreeNode <D> newDistNode = new TreeNode <D>(newDist, null, null, null);
        TreeNode <D> oldLeafNode = leafMap.get(leafLabel);
        TreeNode <D> oldLeafParent = oldLeafNode.getParent();
        List <D> q = new ArrayList <D>(newLeaf);

        if (leafs.contains(newLeaf)) {
        		throw new AssertionError("Attempting to insert existing access string");
        }
        
        // Update internal bookkeeping 
        innerNodes.add(newDist);
        leafs.add(newLeaf);
        leafMap.put(newLeaf, newLeafNode);

        // Fix the properties of each node 
        oldLeafNode.setParent(newDistNode);
        newLeafNode.setParent(newDistNode);
        newDistNode.setParent(oldLeafParent);
        q.addAll(newDist);
        if (membOracle.query(q)) {
            newDistNode.setTrueChild(newLeafNode);
            newDistNode.setFalseChild(oldLeafNode);
        } else {
            newDistNode.setTrueChild(oldLeafNode);
            newDistNode.setFalseChild(newLeafNode);
        }
        // Finally attach to the tree 
        if (oldLeafParent.getTrueChild() == oldLeafNode) {
            // oldLeafNode was the trueChild of his parent
            oldLeafParent.setTrueChild(newDistNode);
        } else {
            // Otherwise it was the falseChild
            oldLeafParent.setFalseChild(newDistNode);
        }
        return;
    }
    
        
    
    public boolean isTreeComplete() {
    		return isMissingLeafFixed;
    }
    
    
    
    
}
