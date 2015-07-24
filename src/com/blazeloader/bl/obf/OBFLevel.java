package com.blazeloader.bl.obf;

/**
 * Type of obfuscation state for the game.
 */
public enum OBFLevel {
	/**
	 * Production build, all references are obfuscated.
	 */
	OBF,
	/**
	 * Forge build, references all use automatically generated names.
	 */
	SRG,
	/**
	 * Development build, all references have human-readable names.
	 */
	MCP;
}
