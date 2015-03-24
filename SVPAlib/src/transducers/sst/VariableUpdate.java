package transducers.sst;

import java.util.HashMap;

public abstract class VariableUpdate<P, F, S> {



	public abstract VariableUpdate<P, F, S> renameVars(
			HashMap<String, String> varRename);

}
