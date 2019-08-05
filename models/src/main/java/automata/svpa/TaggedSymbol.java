package automata.svpa;


public class TaggedSymbol<S> {

	public S input;
	public SymbolTag tag;
	
	public TaggedSymbol(S input, SymbolTag tag) {
		super();
		this.input = input;
		this.tag = tag;
	}
	
	@Override
	public String toString() {
	    switch(tag) {
	      case Internal: return input.toString();
	      case Call: return String.format("<%s", input);
	      case Return: return String.format("%s>", input);
//	      case Call: return String.format("\u3008%s", input);
//	      case Return: return String.format("%s\u3009", input);
	      default: throw new IllegalArgumentException();
	    }		
	}
	
	
	public enum SymbolTag{
		Internal, Call, Return;		
	}
	
}
