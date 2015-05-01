/**
 * BooleanAlgebras
 * theory
 * Apr 21, 2015
 * @author Loris D'Antoni
 */

package theory;

/**
 * CharFunc: a character function of the form x0+off where off is an offset
 */
public class CharConstant implements CharFunc {

	Character c;

	/**
	 * The constant c
	 */
	public CharConstant(Character c) {
		this.c = c;
	}

	@Override
	public String toString() {
		if(Character.isSpaceChar(c) || c<33 || c>126)
    		return "\\u" + Integer.toHexString(c | 0x10000).substring(1);
    	return c.toString();
	}

	public CharFunc SubstIn(CharFunc f1) {
		if(f1 instanceof CharConstant)
			return f1;	
		else{
			CharOffset co = (CharOffset)f1;
			return new CharConstant((char)(c+co.increment));
		}
	}

	public CharPred SubstIn(CharPred p, CharSolver cs) {
		if(cs.HasModel(p,c))
			return cs.True();
		else
			return cs.False();
	}

	public Character InstantiateWith(Character c) {
		return c;
	}
}
