package com.blazeloader.api.particles;

public interface IParticle {
	
	/**
	 * Set whether this particle will always spawn regardless of how far it is from the camera.
	 * 
	 * @param val	distance
	 */
	public void setIgnoreDistance(boolean val);
	
	/**
	 * Whether this particle will always spawn regardless of how far it is from the camera.
	 *
	 * @return val    distance
	 */
	public boolean getIgnoreDistance();
	
	/**
	 * Set maximum distance away from the camera this particle can be spawned.
	 * 
	 * Ignored if getIgnoreDistance() is true.
	 * 
	 * @param dist	distance in blocks
	 */
	public void setMaxDistance(int dist);
	
	/**
	 * Maximum distance away from the camera this particle can be spawned.
	 * 
	 * Ignored if getIgnoreDistance() is true.
	 *
	 * @return distance in blocks
	 * @default 255
	 */
	public int getMaxDistance();
	
	/**
	 * The id for this particle.
	 * 
	 * This particle will be allocated an Id at runtime.
	 * 
	 * @return int id
	 */
	public int getId();
	
	/**
	 * This particle's name.
	 * 
	 * @return String name
	 */
	public String getName();
	
	/**
	 * Returns true if this particle takes any extra arguments.
	 * 
	 * @return true of argsCound > 0
	 */
	public boolean hasArguments();
	
	/**
	 * Returns the total extra arguments this particle accepts.
	 * 
	 * @return int args
	 */
	public int getArgumentCount();
	
	/**
	 * Gets a new ParticleData object to be used when spawning this type of particle.
	 * 
	 * @param arguments	Any additional arguments to be sent with this particle.
	 */
	public ParticleData getData(int... arguments);
}
