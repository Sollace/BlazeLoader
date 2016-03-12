package com.blazeloader.api.particles;

public class ParticleType implements IParticle {
	/**
	 * A default value representing nothing, or an unknown type.
	 */
	public static final ParticleType NONE = new NullParticleType();
	
	private final String particleName;
	private final int argCount;
	
	private int particleId;
	
	private boolean ignoreDist;
	private int maxParticleDistance = 255;
	
	public ParticleType(String name, boolean ignoreDistance, int argumentCount) {
		particleName = name;
		argCount = argumentCount;
		setIgnoreDistance(ignoreDistance);
	}
	
	public void setIgnoreDistance(boolean val) {
		ignoreDist = val;
	}
	
	public boolean getIgnoreDistance() {
		return ignoreDist;
	}
	
	public void setMaxDistance(int dist) {
		maxParticleDistance = dist;
	}
	
	public int getMaxDistance() {
		return maxParticleDistance;
	}
	
	public final int getId() {
		return particleId;
	}
	
	protected IParticle setId(int id) {
		particleId = id;
		return this;
	}
	
	public final String getName() {
		return particleName;
	}
	
	public boolean hasArguments() {
		return argCount > 0;
	}
	
	public int getArgumentCount() {
		return argCount;
	}
	
	public ParticleData getData(int... arguments) {
		return ParticleData.get(this, getIgnoreDistance(), arguments);
	}
	
	private static final class NullParticleType extends ParticleType {
		private NullParticleType() {
			super("UNKNOWN", false, 0);
		}
		
		protected IParticle setId(int id) {
			return this;
		}
		
		public int getMaxDistance() {
			return -1;
		}
		
		public boolean getIgnoreDistance() {
			return false;
		}
	}
}
