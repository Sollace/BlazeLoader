package com.blazeloader.api.particles;

import net.minecraft.client.particle.IParticleFactory;

public class ParticleTypeClient extends ParticleType {
	
	private IParticleFactory particleFactory;
	
	public ParticleTypeClient(String name, boolean ignoreDistance, int argumentCount) {
		super(name, ignoreDistance, argumentCount);
	}
	
	protected final ParticleTypeClient setFactory(IParticleFactory factory) {
		if (particleFactory == null) {
			particleFactory = factory;
		}
		return this;
	}
	
	/**
	 * Gets the factory used to spawn this particle.
	 * 
	 * @return IParticleFactory
	 */
	public IParticleFactory getFactory() {
		return particleFactory;
	}
}
