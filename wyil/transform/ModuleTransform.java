package wyil.transform;

import wyil.lang.Module;

public interface ModuleTransform {
	public Module apply(Module module);
}