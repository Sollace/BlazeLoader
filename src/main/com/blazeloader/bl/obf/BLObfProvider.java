package com.blazeloader.bl.obf;

import com.mumfrey.liteloader.core.runtime.Obf;
import com.mumfrey.liteloader.transformers.ObfProvider;

import net.acomputerdog.OBFUtil.util.TargetType;

/**
 * Provider for obfuscation names for AccessorInjection
 */
public class BLObfProvider implements ObfProvider {
	
	private String target;
	
	/**
	 * Creates a BLOBF provider with the classname already initialised.
	 * <p>
	 * The class name may still be overriden by any other valid classes picked up from annotations on the interface.
	 */
	public static BLObfProvider create(String className) {
		return new BLObfProvider(className);
	}
	
	private BLObfProvider(String target) {
		this.target = target;
	}
	
	@Override
	public Obf getByName(String name) {
		Obf result = provide(name, TargetType.CLASS);
		if (result != null) {
			target = name;
			return result;
		}
		name = target + "." + name;
		result = provide(name, TargetType.FIELD);
		if (result != null) return result;
		return provide(name, TargetType.METHOD);
	}
	
	private Obf provide(String name, TargetType type) {
		Obf result = provide(name, type, OBFLevel.MCP);
		if (result != null) return result;
		result = provide(name, type, OBFLevel.SRG);
		if (result != null) return result;
		return provide(name, type, OBFLevel.OBF);
	}
	
	private Obf provide(String name, TargetType type, OBFLevel level) {
		if (BLOBF.OBF.hasType(name, type, level)) {
			return BLOBF.OBF.getBLOBF(name, type, level);
		}
		return null;
	}

}
