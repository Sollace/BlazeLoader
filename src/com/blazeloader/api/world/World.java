package com.blazeloader.api.world;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.effect.EntityWeatherEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;

import com.blazeloader.api.block.UpdateType;
import com.blazeloader.api.particles.ApiParticles;
import com.blazeloader.api.particles.ParticleData;
import com.blazeloader.util.version.Versions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * An abstract wrapper for any given Minecraft world.
 * <p>
 * Provides a neatly organized and documented view of any Minecraft world,
 * incorporating the functionality of WorldServer, WorldClient, ApiWorld, and a Forge Modified World into one place.
 * <p>
 * Among the default methods you will also find some extra helper methods to make working with the world easier.
 * 
 * @param <T> The type of world object this world wraps
 */
public abstract class World<T extends net.minecraft.world.World> implements IBlockAccess {
	protected double max_entity_size = 2;
	
	/**
	 * All methods relating to packet sending and receiving
	 */
	public final Network network = new Network();
	/**
	 * Players in this world
	 */
	public final Players players = new Players();
	/**
	 * World entities
	 */
	public final EntitySelection<T> entities = new EntitySelection(this);
	
	protected final ClassBoundedEntitySelection classBoundedEntitySelection = new ClassBoundedEntitySelection(this);
	protected final ClassEntitySelection classEntitySelection = new ClassEntitySelection(this);
	protected final BoundedEntitySelection boundedEntitySelection = new BoundedEntitySelection(this);
	
	/**
	 * Weather control and behaviour
	 */
	public final Weather weather = new Weather();
	/**
	 * Particle/Sound effects
	 */
	public final SoundEffects soundEffects = new SoundEffects();
	/**
	 * Redstone power
	 */
	public final Power power = new Power();
	/**
	 * Date and time control and data for this world
	 */
	public final Time time = new Time();
	/**
	 * Methods relating to bounded regions in the world
	 */
	public final BoundingBoxes boxes = new BoundingBoxes();
	/**
	 * World chunk saving/loading
	 */
	public final Storage storage = new Storage();
	/**
	 * Gamerules governing the behaviour of this world
	 */
	public final GameRuleSet gamerules = new GameRuleSet();
	/**
	 * WorldServer specific methods.
	 */
	public final Server server = new Server();
		
	protected World() {}
		
	/**
	 * Retrieves the underlying minecraft World object used by this world.
	 * Use this for access to methods that may not be normally available.
	 * 
	 * @return a Minecraft World
	 */
	public abstract T unwrap();
	
	/**
	 * Returns true if this world is safe to use.
	 */
	public boolean isUsable() {
		return unwrap() != null;
	}
	
	/**
	 * Returns this world to the pool of possible worlds.
	 * <br>
	 * If you have gotten this world instance yourself it is recommended that you always call this after you are done with a world to allow other processes to use it.
	 * <p>
	 * Because worlds are recycled if you hold on to one for too long there is a chance that it will get changed to reference another world for use elsewhere.
	 */
	public void pool() {
		WorldPool.pool(this);
	}
	
	/*
	 * World Methods
	 */
	
	/**
	 * @return This world's profiler
	 */
	public Profiler getProfiler() {
		return unwrap().theProfiler;
	}
	
	/**
	 * Returns true if this world is the client-side remote version of the server's world.
	 * <p>
	 * i.e isRemote() == true means that this world is communication with an actual world running on a remote/integrated server.
	 * 
	 * @return true if on the client.
	 */
	public boolean isRemote() {
		return unwrap().isRemote;
	}
	
	/**
	 * Returns true if this is a client world.
	 * <p>
	 * Note that this has nothing to do with the type of game you are running but rather whether the underlying object
	 * is an instance of WorldServer or WorldClient
	 * 
	 * @return true if the underlying world Object is not an instance of WorldServer. This is done because we have no guarantee that WorldClient exists.
	 */
	public boolean isClient() {
		return !isServer();
	}
	
	/**
	 * Returns true if this is a server world. 
 	 * <p>
	 * Note that this has nothing to do with the type of game you are running but rather whether the underlying object
	 * is an instance of WorldServer or WorldClient
	 * 
	 * @return true if the underlying world Object is an instance of WorldServer
	 */
	public boolean isServer() {
		return unwrap() instanceof WorldServer;
	}
	
	/**
	 * Returns true if this a demo world.
	 * <br>
	 * @return true if the underlying world object is an instance of DemoWorldServer
	 */
	public boolean isDemo() {
		return unwrap() instanceof DemoWorldServer;
	}
	
	/**
	 * Returns true if this as a world for any dimensions other than the Overworld.
	 * <br>
	 * i.e The dimension index is not zero.
	 * @return true if the underlying world instance is a WorldServerMulti
	 */
	public boolean isMulti() {
		return unwrap() instanceof WorldServerMulti;
	}
	
	/**
	 * Returns true if this a debug world.
	 * <p>
	 * Checks if the underlying world object's terrain is of type WorldType.DEBUG_WORLD
	 */
	public boolean isDebug() {
		return getWorldType() == WorldType.DEBUG_WORLD;
	}
	
	/**
	 * Gets the world information for this world.
	 * <p>
	 * Will contain various information for the world such as terrain type, spawn position, world name, time, ...
	 */
	public WorldInfo getWorldInfo() {
		return unwrap().getWorldInfo();
	}
	
	/**
	 * The world provider responsible for generating terrain.
	 */
	public WorldProvider getWorldProvider() {
		return unwrap().provider;
	}
	
	/**
	 * The chunk provider used by this world. Is responsible for loading/unloading and creating chunks as needed.
	 */
	public IChunkProvider getChunkProvider() {
		return unwrap().getChunkProvider();
	}
	
	/**
	 * Gets the village collection for this world.
	 */
	public VillageCollection getVillageCollection() {
		return unwrap().getVillageCollection();
	}
		
	/**
	 * Gets a readout of the current loaded entities for display in the debug screen.
	 */
	public String getDebugLoadedEntities() {
		return unwrap().getDebugLoadedEntities();
	}
	
	/**
	 * Gets the name of the world provider.
	 * <p>
	 * Eg: Nether, Overworld, Flat, etc...
	 */
	public String getProviderName() {
		return unwrap().getProviderName();
	}
	
	/**
	 * Gets the type of world.
	 * <p>
	 * Flat, Normal, Nether, End, etc.
	 * 
	 * @obfuscated This method uses a default MCP name and will be obfuscated at runtime
	 */
	@Override
	public WorldType getWorldType() {
		return unwrap().getWorldType();
	}
	
	/**
	 * The display name for this world
	 */
	public String getName() {
		return unwrap().getWorldInfo().getWorldName();
	}
	
	/**
	 * The world difficulty
	 */
	public EnumDifficulty getDifficulty() {
		return unwrap().getDifficulty();
	}
	
	/**
	 * Sets this world's difficulty. No change will be made if the difficulty is locked.
	 * @param difficulty	The difficulty level
	 */
	public void setDifficulty(EnumDifficulty difficulty) {
		if (!isDifficultyLocked()) {
			getWorldInfo().setDifficulty(difficulty);
		}
	}
	
	/**
	 * Returns true if this is a hard core world. 
	 */
	public boolean isHardCore() {
		return getWorldInfo().isHardcoreModeEnabled();
	}
	
	/**
	 * Sets whether this world must be deleted after the player dies.
	 */
	public void setHardCore(boolean hardCore) {
		getWorldInfo().setHardcore(hardCore);
	}
	
	/**
	 * Returns true if this world does not allow difficulty changes.
	 */
	public boolean isDifficultyLocked() {
		return getWorldInfo().isDifficultyLocked();
	}
	
	/**
	 * Locks/Unlocks this world's difficulty
	 */
	public void setDifficultyLocked(boolean locked) {
		getWorldInfo().setDifficultyLocked(locked);
	}
	
	/**
	 * A difficulty instance dependent on the chosen location. Difficulty may change based on the player's movement and how long he stays in one place.
	 * 
	 * @param pos	Position
	 * @return	local difficulty
	 */
	public DifficultyInstance getDifficultyForLocation(BlockPos pos) {
		return unwrap().getDifficultyForLocation(pos);
	}
	
	/**
	 * The border for this world.
	 */
	public WorldBorder getWorldBorder() {
		return unwrap().getWorldBorder();
	}
	
	/**
	 * Chacks if the given entity is inside this world's border.
	 * 
	 * @param entity	The entity to check
	 * @return	true if it is inside the world
	 */
	public boolean isInsideBorder(Entity entity) {
		return unwrap().isInsideBorder(getWorldBorder(), entity);
	}
	
	/**
	 * The scoreboard
	 */
	public Scoreboard getScoreboard() {
		return unwrap().getScoreboard();
	}
	
	/**
	 * Gets the world's numeric seed.
	 */
	public long getSeed() {
		return unwrap().getSeed();
	}
	
	/**
	 * Sets a random seed.
	 * 
	 * @returns the resulting Random generator
	 */
	public Random setRandomSeed(int scaleX, int scaleY, int scaleZ) {
		return unwrap().setRandomSeed(scaleX, scaleY, scaleZ);
	}
	
	/**
	 * Gets the height of chunks in this world. Effectively the 'maximum' height allowed.
	 */
	public int getMaxHeight() {
		return unwrap().getHeight();
	}
	
	/**
	 * Gets the height to which this world generates terrain. 256 for overworld, 128 for nether.
	 */
	public int getHeight() {
		return unwrap().getActualHeight();
	}
	
	/**
	 * Gets the height of terrain at this x and y coordinates.
	 * 
	 * @param pos	A position in the colomn to check
	 * 
	 * @return The position corresponding to the height of terrain in this colomn.
	 */
	public BlockPos getTerrainHeight(BlockPos pos) {
		return unwrap().getHeight(pos);
	}
	
	/**
	 * Gets the horizon position for this world.
	 */
	public double getHorizon() {
		return unwrap().getHorizon();
	}
	
	/**
	 * Gets the current cloud colour as a function of the world's time and minecraft's partial tick time.
	 *  
	 * @param partialTicks	Minecraft partial ticks
	 * 
	 * @return A vec3 with the RGB components for the cloud colour
	 */
	public Vec3 getCloudColour(float partialTicks) {
		return unwrap().getCloudColour(partialTicks);
	}
	
	/**
	 * Gets the current fog colour as a function of the world's time and minecraft's partial tick time.
	 *  
	 * @param partialTicks	Minecraft partial ticks
	 * 
	 * @return A vec3 with the RGB components for the fog colour
	 */
	public Vec3 getFogColour(float partialTicks) {
		return unwrap().getFogColor(partialTicks);
	}
	
	/**
	 * Gets the current sky colour as a function of the world's time and minecraft's partial tick time.
	 *  
	 * @param partialTicks	Minecraft partial ticks
	 * 
	 * @return A vec3 with the RGB components for the sky colour
	 */
	public Vec3 getSkyColour(Entity entityIn, float partialTicks) {
		return unwrap().getSkyColor(entityIn, partialTicks);
	}
	
	/**
	 * Gets the brightness of the sun based on this world's time of day and the given Minecraft partial tick time.
	 * 
	 * @param partialTicks	Minecraft partial ticks
	 * 
	 * @return A float brightness from 0-1
	 */
	public float getSunBrightness(float partialTicks) {
		return unwrap().getSunBrightness(partialTicks);
	}
	
	/**
	 * Gets the brightness of the world's stars based on this world's time of day and the given Minecraft partial tick time.
	 * 
	 * @param partialTicks	Minecraft partial ticks
	 * 
	 * @return A float brightness from 0-1
	 */
	public float getStarBrightness(float partialTicks) {
		return unwrap().getStarBrightness(partialTicks);
	}
	
	/**
	 * Sets the world's spawn point to the default near the coordinates 8,64,8
	 */
	public void setInitialSpawnPoint() {
		unwrap().setInitialSpawnLocation();
	}
	
	/**
	 * Sets the world's spawn point to the given location.
	 */
	public void setSpawnPoint(BlockPos pos) {
		unwrap().setSpawnPoint(pos);
	}
	
	/**
	 * Gets the world's spawn point.
	 */
	public BlockPos getSpawnPoint() {
		return unwrap().getSpawnPoint();
	}
	
	/**
	 * Checks if the chunk at the given coordinates contains the world's spawn point. 
	 */
	public boolean isSpawnChunk(int chunkX, int chunkZ) {
		return unwrap().isSpawnChunk(chunkX, chunkZ);
	}
	
	/**
	 * Returns true if the world has no spawn point or is currently searching for one.
	 */
	public boolean isFindingSpawnPoint() {
		return unwrap().isFindingSpawnPoint();
	}
	
	/**
	 * Gets a tileEntity for the given coordinate if one exists, otherwise returns null.
	 * 
	 * @param pos	The coordinate
	 * @obfuscated This method uses a default MCP name and will be obfuscated at runtime
	 */
	@Override
	public TileEntity getTileEntity(BlockPos pos) {
		return unwrap().getTileEntity(pos);
	}
	
	/**
	 * Gets the type and metadata for a block at the given coordinate
	 * 
	 * @param pos	The coordinate
	 * @obfuscated This method uses a default MCP name and will be obfuscated at runtime
	 * @return the BlockState from the given location
	 */
	@Override
	public IBlockState getBlockState(BlockPos pos) {
		return unwrap().getBlockState(pos);
	}
	
	/**
	 * Sets the block at the given coordinate to air.
	 * 
	 * @param pos	The coordinate
	 * 
	 * @return true if the operation was succesful
	 */
	public boolean setBlockToAir(BlockPos pos) {
		return setBlockState(pos, Blocks.air.getDefaultState());
	}
	
	/**
	 * Chacks if the block at the given coordinate is a water block
	 * 
	 * @param pos	The coordinate
	 * @return true if the block at the location has a Material of water
	 */
	public boolean isWaterBlock(BlockPos pos) {
		return getBlockState(pos).getBlock().getMaterial() == Material.water;
	}

	/**
	 * Checks if the block at the given coordinate is an air block.
	 * 
	 * <p>This will treat any block with a it's material set to air so may return true for certain mod blocks as well. For a more strict result try isBlockEmpty.
	 * 
	 * @param pos	The coordinate
	 * @obfuscated This method uses a default MCP name and will be obfuscated at runtime
	 * 
	 * @return true if the block at the location has a Material of air
	 */
	@Override
	public boolean isAirBlock(BlockPos pos) {
		return getBlockState(pos).getBlock().getMaterial() == Material.air;
	}
	
	/**
	 * Checks if the block at the given coordinate is an air block.
	 * 
	 * <p>Will only return true if the class of the block at the given location is a subclass of BlockAir.
	 * 
	 * @param pos	The coordinate
	 * @return true if the block at the location is equivalent to air.
	 */
	public boolean isBlockEmpty(BlockPos pos) {
		return getBlockState(pos).getBlock() instanceof BlockAir;
	}
	
	/**
	 * Sets the state for a block at the given coordinates,
	 * 
	 * @param pos	The coordinate
	 * @param state	The new blockstate
	 * 
	 * @return true if the operation was successful
	 */
	public boolean setBlockState(BlockPos pos, IBlockState state) {
		return unwrap().setBlockState(pos, state);
	}
	
	/**
	 * Sets the state for a block at the given coordinates,
	 * 
	 * @param pos			The coordinate
	 * @param state			The new blockstate
	 * @param notifyFlag	Notification flags
	 * 
	 * @return true if the operation was successful
	 */
	public boolean setBlockState(BlockPos pos, IBlockState newState, UpdateType notifyFlag) {
		return unwrap().setBlockState(pos, newState, notifyFlag.getValue());
	}
	
	/**
	 * Sets the block at the given location to air and plays the standard effects for when a block is broken.
	 * 
	 * @param pos		The location
	 * @param dropItems	Whether the block must drop itself and contents as items.
	 * 
	 * @return true if the operation was successful
	 */
	public boolean destroyBlock(BlockPos pos, boolean dropItems) {
		return unwrap().destroyBlock(pos, dropItems);
	}
	
	/**
	 * Chacks if there is a tick pending for the given block at the given location.
	 * 
	 * @param pos			The location
	 * @param block			The type of block
	 * @return
	 */
    public boolean isBlockTickPending(BlockPos pos, Block block) {
    	return unwrap().isBlockTickPending(pos, block);
    }
	
    /**
     * Gets the biome for the given location.
     * @obfuscated This method uses a default MCP name and will be obfuscated at runtime
     * 
     * @param the location
     */
	@Override
	public BiomeGenBase getBiomeGenForCoords(BlockPos pos) {
		return unwrap().getBiomeGenForCoords(pos);
	}
	
	/**
	 * Sets the types of creatures allowed to span in this world.
	 * 
	 * @param hostile	Allow monsters
	 * @param peaceful	Allow animals/neutral things
	 */
	public void setAllowedSpawnTypes(boolean hostile, boolean peaceful) {
		unwrap().setAllowedSpawnTypes(hostile, peaceful);
	}
	
	/**
	 * Checks if there are player sleeping and updates the internal flag accordingly. 
	 */
	public void updateAllPlayersSleepingFlag() {
		if (!unwrap().isRemote) {
			unwrap().updateAllPlayersSleepingFlag();
		}
	}
	
	/**
	 * Gets a location above this world's sea level.
	 * 
	 * @param pos	the location
	 * @return A new location on land above sea level
	 */
	public Block getGroundAboveSeaLevel(BlockPos pos) {
		return unwrap().getGroundAboveSeaLevel(pos);
	}
	
	/**
	 * Gets the level of redstone provided by a strong power source from the given direction.
	 * 
	 * @param pos		the location
	 * @param direction	the direction to check relative to the supplied location
	 * @obfuscated This method uses a default MCP name and will be obfuscated at runtime
	 * 
	 * @return the level of redstone power provided by that block
	 */
	@Override
	public int getStrongPower(BlockPos pos, EnumFacing direction) {
		return power.getStrongPower(pos, direction);
	}
	
    /**
     * Gets the location of the nearest structure identified by its name.
     * 
     * @param name	Name of structure
     * @param pos	Current location
     * @return The location of the structure of null if none were found.
     */
	public BlockPos getStructurePosition(String name, BlockPos pos) {
		return unwrap().getStrongholdPos(name, pos);
	}
	
	/**
	 * Extinguishes fire in the world.
	 * 
	 * @param player	The player that wants to get rid of fire
	 * @param pos		The location of the fire block to remove
	 * @param side		The side the fire block is attached to
	 * @return true if the operation was successful.
	 */
	public boolean extinguishFire(EntityPlayer player, BlockPos pos, EnumFacing side) {
		return unwrap().extinguishFire(player, pos, side);
	}
	
	/**
	 * Creates a new explosion.
	 * 
	 * @param entityIn		The source of the explosion
	 * @param x				X coordinate
	 * @param y				Y coordinate
	 * @param z				Z coordinate
	 * @param strength		The magnitude of explosion (used to calculate block breakage)
	 * @param isFlaming		True to set blocks on fire
	 * @param isSmoking		True to create extra smoke affects
	 * @return The resulting explosion entity
	 */
	public Explosion createExplosion(Entity entityIn, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking) {
		return unwrap().newExplosion(entityIn, x, y, z, strength, isFlaming, isSmoking);
	}
	
	/**
	 * Creates a firework effect.
	 * @param x				X coordinate
	 * @param y				Y coordinate
	 * @param z				Z coordinate
	 * @param motionX		X velocity
	 * @param motionY		Y velocity
	 * @param motionZ		Z velocity
	 * @param compound		An NBTTag containing extra information used to create the firework
	 */
	public void createFirework(double x, double y, double z, double motionX, double motionY, double motionZ, NBTTagCompound compound) {
		unwrap().makeFireworks(x, y, z, motionX, motionY, motionZ, compound);
	}
	
	/**
	 * Spawns a particle in the world
	 * @param particle	ParticleData for the particle to spawn
	 */
	public void spawnParticle(ParticleData particle) {
		ApiParticles.spawnParticle(particle, unwrap());
	}
	
    /**
     * Play sound and particle effect for a block being broken without actually removing the block
     * 
     * @blazeloader This is part of the BlazeLoader API specification
     * @param pos The position of the block.
     */
	public void playBlockDestructionEffect(BlockPos pos) {
		ApiWorld.playBlockDestructionEffect(unwrap(), pos);
	}
	
    /**
     * Spawns dispenser particles in front of the block at the given location in the indicated direction
     * 
     * @blazeloader This is part of the BlazeLoader API specification
     * @param w  		World
     * @param pos 		The position of the block.
     * @param direction	The direction the block is facing
     */
	public void spawnDispenserParticles(BlockPos pos, EnumFacing direction) {
		ApiWorld.spawnDispenserParticles(unwrap(), pos, direction);
	}
	
	/**
	 * Checks if the player can change blocks at the given location
	 * 
	 * @param player	The player
	 * @param pos		The location
	 * @return true if the player can make changes
	 */
	public boolean isBlockModifiable(EntityPlayer player, BlockPos pos) {
		return unwrap().isBlockModifiable(player, pos);
	}
	
	/**
	 * Checks if a block can be placed at the given location
	 * 
	 * @param block				The block
	 * @param pos				The position
	 * @param ignoreEntities	True if it must not check for entities crossing the block's bounds
	 * @param side				The side you are placing against
	 * @param entity			The entity placing the block
	 * @param itemStackIn		The itemstack used to place the block
	 * 
	 * @return True if the block can be placed
	 */
	public boolean canBlockBePlaced(Block block, BlockPos pos, boolean ignoreEntities, EnumFacing side, Entity entity, ItemStack itemStackIn) {
		return unwrap().canBlockBePlaced(block, pos, ignoreEntities, side, entity, itemStackIn);
	}
	
	/**
	 * Adds a custom event to be triggered for a block. If we are running on the client the event is triggered immediately.
	 * 
	 * @param pos			The position
	 * @param block			The block type
	 * @param eventID		An identifier for the event being triggered
	 * @param eventParam	Additional parameters for the event, smooshed together into an int.
	 */
	public void addBlockEvent(BlockPos pos, Block block, int eventID, int eventParam) {
		unwrap().addBlockEvent(pos, block, eventID, eventParam);
	}
    
	/**
	 * Gets the density of blocks in a given area defined by the given bounding box.
	 * 
	 * @param vec			The direction to look
	 * @param boundingbox	The bounding box for the area
	 * 
	 * @return a float density from 0-1
	 */
    public float getBlockDensity(Vec3 vec, AxisAlignedBB boundingbox) {
    	return unwrap().getBlockDensity(vec, boundingbox);
    }
    
    /**
     * Checks if the block at the given location as a direct line of sight with the sky. That is there are no other blocks above it other than air.
     * <p>
     * More technically you can say it will use the chunk's heightmap to determine if there are any blocks above.
     * 
     * @param pos the location
     * 
     * @return true if the block is open to the sky.
     */
    public boolean canSeeSky(BlockPos pos) {
    	return unwrap().canSeeSky(pos);
    }
    
    /**
     * Checks if a block has an indirect line of sight with the sky. Is simmilar to canSeeSky except that this will not count any transparant blocks
     * 
     * @param pos the location
     * 
     * @return true if the block is recieving skylight from directly above, either through an opening of through transparant blocks
     */
    public boolean canBlockSeeSky(BlockPos pos) {
    	return unwrap().canBlockSeeSky(pos);
    }
    
    /**
     * Gets the highest block at the given x and z coordinates
     * 
     * @param pos	the location
     * @return	Location of the top block.
     */
    public BlockPos getTopSolidOrLiquidBlock(BlockPos pos) {
    	return unwrap().getTopSolidOrLiquidBlock(pos);
    }
    
    /**
     * Checks if the boundingbox for the block at the given location extends past a normal 1m cude.
     * 
     * @param pos	the location
     * @return true if the block occupies more than its own space
     */
    public boolean getBlockFillsExraSpace(BlockPos pos) {
    	return unwrap().func_175665_u(pos);
    }
    
    /**
     * Gets the amount of light a block at the given location allows through.
     * 
     * @forge This is part of the Forge API specification.
     * @param pos	the location
     * @return a light level allowed through the block.
     */
    public int getBlockLightOpacity(BlockPos pos) {
    	return ForgeWorld.getBlockLightOpacity(unwrap(), pos);
    }
    
    /**
     * Returns true if the block is both normal and a full 1m cude
     * 
     * @param pos		The location
     * @param def		A default value
     * @return true if the block is a normal, 1m cube. Otherwise returns def.
     */
    public boolean isBlockNormalCube(BlockPos pos, boolean def) {
    	return unwrap().isBlockNormalCube(pos, def);
    }
    
	/**
	 * Checks if the given side of a block is solid.
	 * <p>
	 * Warning: This method depends on the Forge API for full functionality. Without that it may only return correctly for the top or bottom faces.
	 * 
	 * @forge This is part of the Forge API specification
	 * @param pos	The location
	 * @param side	The side to check
	 * @return True if it is solid
	 */
	public boolean isSideSolid(BlockPos pos, EnumFacing side) {
		return isSideSolid(pos, side, false);
	}
	
    /**
     * Determine if the given block is considered solid on the
     * specified side.  Used by placement logic.
     * <p>
	 * Warning: This method depends on the Forge API for full functionality. Without that it may only return correctly for the top or bottom faces.
     * 
     * @forge This is part of the Forge API specification
     * @param pos	The location
     * @param side	The Side in question
     * @param  def	A default value to return
     * @return True if the side is solid or the default
     */
    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean def) {
    	return ApiWorld.isSideSolid(unwrap(), pos, side, def);
    }
    
    /**
     * Checks if a block has a solid top surface.
     * 
     * @param pos	The location
     * @return true if the top side of the block is solid.
     */
    public boolean doesBlockHaveSolidTopSurface(BlockPos pos) {
    	return unwrap().doesBlockHaveSolidTopSurface(unwrap(), pos);
    }
	
    /**
     * Check if the given BlockPos has valid coordinates
     */
    public boolean isPositionValid(BlockPos pos) {
    	return unwrap().isValid(pos);
    }
    
    /**
     * Chacks if a block can freeze.
     * 
     * @param pos			The location
     * @param waterAdjacent	True if the block has water against any of its faces
     * @return
     */
    public boolean canBlockFreeze(BlockPos pos, boolean waterAdjacent) {
        return unwrap().canBlockFreeze(pos, !waterAdjacent);
    }
    
	/**
	 * Schedules an update at the given location and block after a certain number of ticks has passed
	 *  
	 * @param pos		The location
	 * @param block		The block
	 * @param delay		A delay to wait before sending the update
	 */
    public void scheduleBlockUpdate(BlockPos pos, Block block, int delay) {
    	unwrap().scheduleUpdate(pos, block, delay);
    }
    
	/**
	 * Schedules an update at the given location and block after a certain number of ticks has passed
	 *  
	 * @param pos		The location
	 * @param block		The block
	 * @param delay		A delay to wait before sending the update
	 * @param priority	Lower numbers will be executed first
	 */
    public void scheduleBlockUpdate(BlockPos pos, Block block, int delay, int priority) {
    	unwrap().scheduleBlockUpdate(pos, block, delay, priority);
    }
    
    public void updateBlockTick(BlockPos pos, Block block, int tickDelay, int priority) {
    	unwrap().updateBlockTick(pos, block, tickDelay, priority);
    }    
    
    /**
     * Adds an acceleration to the entity based on the given material.
     * 
     * @param material	The material the entity is walking on
     * @param entity	An entity to apply an acceleration to
     * @return true if an action was taken
     */
    public boolean applyAcceleration(Material material, Entity entity) {
    	return unwrap().handleMaterialAcceleration(entity.getBoundingBox(), material, entity);
    }
    
    /**
     * Adds an acceleration to the entity based on the given material.
     * 
     * @param material				The material the entity is walking on
     * @param entity				An entity to apply an acceleration to
     * @param expandedBoundingBox	The boundingbox used to check whether the entity collides with any blocks of the given material and should recieve an acceleration
     * @return true if an action was taken
     */
    public boolean applyAcceleration(Material material, Entity entity, AxisAlignedBB expandedBoundingBox) {
    	return unwrap().handleMaterialAcceleration(expandedBoundingBox, material, entity);
    }
    
    /**
     * Performs a raytrace to find any blocks that intercept the given vectors.
     * 
     * @param pos	The observer's location
     * @param look	A vector in the direction the observer is facing extended to the distance to which you want to perform the raytrace
     * @return A ovingObjectPosition for the first block to cross the ray, or null if none were found
     */
    public MovingObjectPosition rayTraceBlocks(Vec3 pos, Vec3 look) {
		return unwrap().rayTraceBlocks(pos, look);
	}
	
    /**
     * Performs a raytrace to find any blocks that intercept the given vectors.
     * <p>
     * This raytrace will always ignore liquid
     * 
     * @param pos	The observer's location
     * @param look	A vector in the direction the observer is facing extended to the distance to which you want to perform the raytrace
     * @return A ovingObjectPosition for the first block to cross the ray, or null if none were found
     */
	public MovingObjectPosition rayTraceBlocks(Vec3 pos, Vec3 look, boolean stopOnLiquid) {
		return unwrap().rayTraceBlocks(pos, look, stopOnLiquid);
	}
	
    /**
     * Performs a raytrace to find any blocks that intercept the given vectors.
     * 
     * @param pos			The observer's location
     * @param look			A vector in the direction the observer is facing extended to the distance to which you want to perform the raytrace
     * @param stopOnLiquid	True to stop when it hits liquid
     * @param ignoreBlockWithoutBoundingBox True to not stop on blocks that have no collision box (ie cannot be collided with)
     * @return A ovingObjectPosition for the first block to cross the ray, or null if none were found
     */
	public MovingObjectPosition rayTraceBlocks(Vec3 pos, Vec3 look, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
		return unwrap().rayTraceBlocks(pos, look, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
	}
    
	/**
	 * Gets the full light value for a given location.
	 * <p>
	 * Takes into account light emmited by neighbouring blocks.
	 * 
	 * @param pos			The location
	 * @param lightValue	The basic light level for this block
	 * @obfuscated This method uses a default MCP name and will be obfuscated at runtime
	 * 
	 * @return a new lightValue combining the given one and any light from surrounding blocks
	 */
	@Override
	public int getCombinedLight(BlockPos pos, int lightValue) {
		return unwrap().getCombinedLight(pos, lightValue);
	}
	
	/**
	 * Gets the subtracted skylight value
	 */
	public int getSkylightSubtracted() {
		return unwrap().getSkylightSubtracted();
	}
	
	/**
	 * Sets the subtracted skylight value
	 */
	public void setSkylightSubtracted(int value) {
		unwrap().setSkylightSubtracted(value);
	}
	
	/**
	 * Gets the total light from neighboring blocks.
	 * 
	 * @param pos	The location
	 * 
	 * @return The atmospheric light level
	 */
	public int getLightFromNeighbors(BlockPos pos) {
		return unwrap().getLightFromNeighbors(pos);
	}
	
	/**
	 * 
	 * Gets the total light from neighboring blocks for the given lighting type.
	 * 
	 * @param pos	The location
	 * @param type	The type of lighting being applied
	 * 
	 * @return The atmospheric light level
	 */
	public int getLightFromNeighbors(BlockPos pos, EnumSkyBlock type) {
		return unwrap().getLightFromNeighborsFor(type, pos);
	}
	
	/**
	 * Gets the raw light value for a block.
	 * 
	 * @param pos The location
	 * @return a non-combined light value for the block
	 */
	public int getLight(BlockPos pos) {
		return unwrap().getLight(pos);
	}
	
	/**
	 * Gets the light value for a block.
	 * 
	 * @param pos				The location
	 * @param checkNeighbors	True to check neighbors and add their light value to this one's.
	 * @return a light value for the block
	 */
	public int getLight(BlockPos pos, boolean checkNeighbors) {
		return unwrap().getLight(pos, checkNeighbors);
	}
	
	/**
	 * 
	 * Gets the light value for a block for the given lighting type.
	 * 
	 * @param pos	The location
	 * @param type	The type of lighting being applied
	 * 
	 * @return a light value for the block
	 */
	public int getLight(BlockPos pos, EnumSkyBlock type) {
		return unwrap().getLightFor(type, pos);
	}
	
	/**
	 * Sets the lighting for a block.
	 * 
	 * @param pos			The location
	 * @param type			The type of lighting being applied
	 * @param lightValue
	 */
	public void setLight(BlockPos pos, EnumSkyBlock type, int lightValue) {
		unwrap().setLightFor(type, pos, lightValue);
	}
	
	/**
	 * Returns true if the lighting at a location is still valid.
	 * 
	 * @param pos			The location
	 */
	public boolean checkLight(BlockPos pos) {
		return unwrap().checkLight(pos);
	}
	
	/**
	 * Returns true if the lighting at a location for the given lighting type is still valid
	 * 
	 * @param pos			The location
	 * @param type			The type of lighting being applied
	 */
	public boolean checkLight(BlockPos pos, EnumSkyBlock lightType) {
		return unwrap().checkLightFor(lightType, pos);
	}
	
	/**
	 * Forces the world to perform an update for a block regardless of whether it needs it.
	 * 
	 * @param pos		The location
	 * @param block		The type of block
	 * @param random	A random generator used for whater the block may need it for.
	 */
	public void forceBlockUpdateTick(BlockPos pos, Block block, Random random) {
		unwrap().forceBlockUpdateTick(block, pos, random);
	}
	
	/**
	 * Marks a single block to recieve an update
	 */
	public void markBlockForUpdate(BlockPos pos) {
		unwrap().markBlockForUpdate(pos);
	}
	
	/**
	 * Margs a rang of blocks, marked out by the given block locations, to be rerendered.
	 * 
	 * @param rangeMin	Min coordinate
	 * @param rangeMax	Max coordinate
	 */
	public void markBlockRangeForRenderUpdate(BlockPos rangeMin, BlockPos rangeMax) {
		unwrap().markBlockRangeForRenderUpdate(rangeMin, rangeMax);
	}
	
	/**
	 * Notify all neighbours except for one of an update.
	 * 
	 * @param pos		The location
	 * @param block		The type of block
	 * @param skipSide	The side that must no be notified
	 */
	public void notifyNeighborsOfStateExcept(BlockPos pos, Block block, EnumFacing skipSide) {
		unwrap().notifyNeighborsOfStateExcept(pos, block, skipSide);
	}
	
	/**
	 * Notify all neighbours of an update.
	 * 
	 * @param pos		The location
	 * @param block		The type of block
	 */
	public void notifyNeighborsOfStateChange(BlockPos pos, Block block) {
		unwrap().notifyNeighborsOfStateChange(pos, block);
	}
	
	/**
	 * Notifies a block of a state change.
	 * 
	 * @param pos			The location
	 * @param block			The type of block
	 * @param ignoreDebug	Set to true if notification must still be sent on the debug world
	 */
	public void notifyBlockOfStateChange(BlockPos pos, final Block block, boolean ignoreDebug) {
		if (ignoreDebug) {
			unwrap().notifyBlockOfStateChange(pos, block);
		} else {
			unwrap().notifyNeighborsRespectDebug(pos, block);
		}
	}
	
	/**
	 * Gets a list of block updates scheduled
	 * 
	 * @param chunk		The chunk
	 * @param remove	True if the updates must be unscheduled		
	 */
	public List<NextTickListEntry> getPendingBlockUpdates(Chunk chunk, boolean remove) {
		List result = unwrap().getPendingBlockUpdates(chunk, remove);
		if (result == null) return new ArrayList<NextTickListEntry>();
        return result;
	}
	
	/**
	 * Gets a list of block updates scheduled
	 * 
	 * @param structure	A boundingbox for a given area
	 * @param remove	True if the updates must be unscheduled		
	 */
    public List<NextTickListEntry> getPendingBlockChanges(StructureBoundingBox structure, boolean remove) {
        List result = unwrap().func_175712_a(structure, remove);
        if (result == null) return new ArrayList<NextTickListEntry>();
        return result;
    }
	
    /**
     * Checks if this IWorldAccess has additional chunk levels. Default implementation only uses this in the ChunkCache.
     * Worlds always return false.
     * 
     * @obfuscated This method uses a default MCP name and will be obfuscated at runtime
     */
	@Override
	public boolean extendedLevelsInChunkCache() {
		return unwrap().extendedLevelsInChunkCache();
	}
	
	public boolean equals(Object other) {
		if (other instanceof World) {
			return ((World)other).unwrap().equals(unwrap());
		}
		if (other instanceof net.minecraft.world.World) {
			return unwrap().equals(other);
		}
		return super.equals(other);
	}
	
	
	/**
	 * Checks if animals can spawn
	 * @return True if animals are allowed to spawn
	 */
	public boolean getCanSpawnAnimals() {
		MinecraftServer s = server.getServer();
		return s == null ? true : s.getCanSpawnAnimals();
	}
	
	/**
	 * Checks if NPCs can spawn
	 * @return True if npcs are allowed to spawn
	 */
	public boolean getCanSpawnNPCs() {
		MinecraftServer s = server.getServer();
		return s == null ? true : s.getCanSpawnNPCs();
	}
	
	/**
	 * Checks if monsters are allowed to spawn in this world.
	 * @return True if monsters are allowed to spawn
	 */
	public boolean getCanSpawnMonsters() {
		return getDifficulty() != EnumDifficulty.PEACEFUL;
	}
	
	/**
	 * Checks if mod spawning is enabled.
	 * @return True if mobs can spawn.
	 */
	public boolean getCanSpawnMobs() {
		return gamerules.getBool("doMobSpawning");
	}
	
	/**
	 * Selects a random spawn list entry for the given creature type and location
	 * 
	 * @blazeloader This is part of the BlazeLoader API specification
	 * @param creatureType	The type of creature to spawn
	 * @param pos			The location
	 */
	public BiomeGenBase.SpawnListEntry getSpawnListEntryForTypeAt(EnumCreatureType creatureType, BlockPos pos) {
        return ApiWorld.getSpawnListEntryForTypeAt(unwrap(), creatureType, pos);
	}
	
	/**
	 * Checks if th given spawn list entry is allowed as a possible spawn for the given creature type and location
	 * 
	 * @blazeloader This is part of the BlazeLoader API specification
	 * @param creatureType		The type of creature to spawn
	 * @param spawnListEntry	The entry we would like to spawn
	 * @param pos				The location we would like to spawn at
	 * @return	True if we can spawn here
	 */
	public boolean canSpawnHere(EnumCreatureType creatureType, BiomeGenBase.SpawnListEntry spawnListEntry, BlockPos pos) {
        return ApiWorld.canSpawnHere(unwrap(), creatureType, spawnListEntry, pos);
	}
	
	/*
	 * Subclasses
	 */
	
	private class Category {
		/**
		 * Gets the world context
		 */
		public World<T> getWorld() {
			return World.this;
		}
		
		public boolean equals(Object other) {
			if (other instanceof World.Category) {
				return unwrap().equals(((World.Category)other).getWorld().unwrap());
			}
			return super.equals(other);
		}
	}
	
	protected abstract class Entities {
		protected World<T> worldObj;
		
		protected Entities(World<T> w) {
			worldObj = w;
		}
		
		/**
		 * Gets a list of all entities currently loaded into the world.
		 */
		public List<Entity> getLoadedEntityList() {
			return worldObj.unwrap().loadedEntityList;
		}
		
		/**
		 * Gets a list of all tileentities currently loaded into the world.
		 */
		public List<TileEntity> getLoadedTileEntityList() {
			return worldObj.unwrap().loadedTileEntityList;
		}
		
		/**
		 * Gets an entity by it's in world id.
		 */
		public Entity getByID(int id) {
			return worldObj.unwrap().getEntityByID(id);
		}
		
		/**
		 * Gets an entity by its Unique Identifier
		 * 
		 * @blazeloader This is part of the BlazeLoader API specification
		 * @param uuid	A uuid for the entity
		 * @return The matching entity or null if none were found
		 */
		public Entity getByUUId(UUID uuid) {
			return ApiWorld.getEntityByUUId(worldObj.unwrap(), uuid);
		}
		
		/**
		 * Sets the state of an entity.
		 */
		public void setState(Entity entityIn, byte state) {
			worldObj.unwrap().setEntityState(entityIn, state);
		}
		
		/**
		 * Marks all entities in the supplied collection to be unloaded.
		 */
		public void unload(Collection<Entity> entityCollection) {
			worldObj.unwrap().unloadEntities(entityCollection);
		}
		
		/**
		 * Loads all entities in the supplied collection.
		 */
		public void load(Collection<Entity> entityCollection) {
			worldObj.unwrap().loadEntities(entityCollection);
		}
		
		/**
		 * Removes an entity from the world.
		 */
		public void remove(Entity entity) {
			worldObj.unwrap().removeEntity(entity);
		}
		
		/**
		 * Adds an entity to the world and performs additional setup. 
		 */
		public boolean spawnInWorld(Entity entity) {
			return worldObj.unwrap().spawnEntityInWorld(entity);
		}
		
		/**
		 * Gets the tileentity located at the given location or null if none exist.
		 */
		public TileEntity getTileEntity(BlockPos pos) {
			return worldObj.unwrap().getTileEntity(pos);
		}
		
		/**
		 * Places a tileentity at the given location
		 */
		public void setTileEntity(BlockPos pos, TileEntity entity) {
			worldObj.unwrap().setTileEntity(pos, entity);
		}
		
	    /**
	     * Adds a single TileEntity to the world.
	     * 
	     * @forge This is part of the Forge API Specification
	     * @param entity The TileEntity to be added.
	     */
	    public void addTileEntity(TileEntity entity) {
    		worldObj.unwrap().addTileEntity(entity);
	    }
		
		/**
		 * Adds all the supplied tilenetities to the set of loaded tileentities
		 */
		public void addTileEntities(Collection tileEntityCollection) {
			worldObj.unwrap().addTileEntities(tileEntityCollection);
		}
		
		/**
		 * Removes the tileentity found at the given location
		 */
		public void removeTileEntity(BlockPos pos) {
			worldObj.unwrap().removeTileEntity(pos);
		}
		
		/**
		 * Marks a tile entity to be removed on the next tick.
		 */
		public void markTileEntityForRemoval(TileEntity entity) {
			worldObj.unwrap().markTileEntityForRemoval(entity);
		}
		
		/**
		 * Adds an entity to the world and loads surrounding chunks.
		 */
		public void joinEntityInSurroundings(Entity entityIn) {
			worldObj.unwrap().joinEntityInSurroundings(entityIn);
		}
		
	    /**
	     * Gets all entities in the world within a certain radius from a given point
	     * 
	     * @blazeloader This is part of the BlazeLoader API specification
	     * @param x   XCoordinate
	     * @param y   YCoordinate
	     * @param z   ZCoordinate
	     * @param rad Maximum Radius
	     * @return List of matching Entities
	     */
		public List<Entity> getNear(World w, int x, int y, int z, int rad) {
			return ApiWorld.getEntitiesNear(worldObj.unwrap(), x, y, z, rad);
		}
		
	    /**
	     * Returns a count of entities that classify themselves as the specified creature type.
	     * 
	     * @forge This is part of the Forge API specification (see World.countEntities)
	     * @param type				The creature type to check for
	     * @param forSpawnCount		True if we are checking spawn count caps. Only used when running with forge.
	     */
	    public int count(EnumCreatureType type, boolean forSpawnCount) {
	    	return ForgeWorld.countEntities(worldObj.unwrap(), type, forSpawnCount);
	    }
	    
	    /**
	     * Gets the maximum entity size used when searching for an entity within a boundingbox area.
	     * 
	     * @forge This is part of the Forge API specification (see: World.MAX_ENTITY_RADIUS)
	     * @return The maximum assumed radius for an entity (default 2)
	     */
	    public double getMaxEntitySize() {
	    	if (Versions.isForgeInstalled()) {
	    		return max_entity_size = ForgeWorld.getMaxEntitySize(worldObj.unwrap(), max_entity_size);
	    	}
	    	return max_entity_size;
	    }
	    
	    /**
	     * Sets the maximum entity size used when searching for an entity within a boundingbox area.
	     * 
	     * @forge This is part of the Forge API specification (see: World.MAX_ENTITY_RADIUS)
	     */
	    public void setMaxEntitySize(double size) {
	    	if (Versions.isForgeInstalled()) {
	    		ForgeWorld.setMaxEntitySize(worldObj.unwrap(), max_entity_size);
	    	}
	    	max_entity_size = size;
	    }
	    
		/**
		 * Widens the selection to allow any entity type from anywhere in the world.
		 */
		public EntitySelection<T> unfiltered() {
			return worldObj.entities;
		}
		
		/**
		 * Gets the world context for this entities view
		 */
		public World<T> getWorld() {
			return worldObj;
		}
		
		public boolean equals(Object other) {
			if (other instanceof World.Entities) {
				return worldObj.equals(((Entities)other).worldObj);
			}
			return super.equals(other);
		}
	}
	
	public final class Storage extends Category {
		
		/**
		 * Gets the world chunk manager responsible for loading/saving chunks.
		 * @return
		 */
		public WorldChunkManager getChunkManager() {
			return unwrap().getWorldChunkManager();
		}
		
		/**
		 * Gets the world's save handler.
		 */
		public ISaveHandler getSaveHandler() {
			return unwrap().getSaveHandler();
		}
		
		/**
		 * Flushes the save handler essentially saving all changes in the world.
		 * <br>
		 * Will only have an effect on server worlds.
		 */
		public void flush() {
			if (isServer()) getSaveHandler().flush();
		}
		
		/**
		 * Gets the map storage responsible for saving/loading additional data such as villages.
		 */
		public MapStorage getMapStorage() {
			return unwrap().getMapStorage();
		}
		
		/**
		 * Gets the per-world map storage introduced by forge, otherwise returns the regular mapstorage.
		 * 
		 * @forge This is part of the Forge API specification
		 */
		public MapStorage getPerWorldStorage() {
			return ForgeWorld.getPerWorldStorage(unwrap());
		}
		
		/**
		 * Gets the lowest block in a chunk.
		 * 
		 * @param x	Chunk X coordinate
		 * @param z	Chunk Y coordinate
		 * 
		 * @return the height of the lowest block in the chunk
		 */
	    public int getChunksLowestHorizon(int x, int z) {
	    	return unwrap().getChunksLowestHorizon(x, z);
	    }
	    
		/**
		 * Checks if a chunk exists at the given chunk coordinates
		 * 
		 * @param x				Chunk X
		 * @param z				Chunk Z
		 * @param allowEmpty	true to not allow generating a chunk if there is not one
		 * 
		 * @return True if the chunk exists and it not empty
		 */
		public boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
	        return getChunkProvider().chunkExists(x, z) && (allowEmpty || !getChunkProvider().provideChunk(x, z).isEmpty());
		}
		
	    /**
	     * Returns true if a chunk is loaded for the given block location
	     * @param pos	The location
	     */
		public boolean isBlockLoaded(BlockPos pos) {
			return unwrap().isBlockLoaded(pos);
		}
		
	    /**
	     * Returns true if a chunk is loaded for the given block location
	     * @param pos			The location
	     * @param allowEmpty	true to not allow generating a chunk if there is not one
	     */
		public boolean isBlockLoaded(BlockPos pos, boolean allowEmpty) {
			return unwrap().isBlockLoaded(pos, allowEmpty);
		}
		
		/**
		 * Returns true of every block within the given radius of the center is loaded.
		 * 
		 * @param pos		The location
		 * @param radius	Maximum radius to check
		 */
		public boolean isAreaLoaded(BlockPos center, int radius) {
			return unwrap().isAreaLoaded(center, radius);
		}
		
		/**
		 * Returns true of every block within the given radius of the center is loaded.
		 * 
		 * @param pos			The location
		 * @param radius		Maximum radius to check
		 * @param allowEmpty	true to not allow generating a chunk if there is not one
		 */
		public boolean isAreaLoaded(BlockPos center, int radius, boolean allowEmpty) {
			return unwrap().isAreaLoaded(center, radius, allowEmpty);
		}
		
		/**
		 * Checks if every block between two points is loaded.
		 * 
		 * @param from	Start position
		 * @param to	End position
		 */
		public boolean isAreaLoaded(BlockPos from, BlockPos to) {
			return unwrap().isAreaLoaded(from, to);
		}
		
		/**
		 * Checks if every block between two points is loaded.
		 * 
		 * @param from			Start position
		 * @param to			End position
		 * @param allowEmpty	true to not allow generating a chunk if there is not one
		 */
		public boolean isAreaLoaded(BlockPos from, BlockPos to, boolean allowEmpty) {
			return unwrap().isAreaLoaded(from, to, allowEmpty);
		}
		
		/**
		 * Returns true if every block inside the given structure is loaded.
		 * 
		 * @param box	The structure box
		 */
		public boolean isAreaLoaded(StructureBoundingBox box) {
			return unwrap().isAreaLoaded(box);
		}
		
		/**
		 * Returns true if every block inside the given structure is loaded.
		 * 
		 * @param box			The structure box
		 * @param allowEmpty	true to not allow generating a chunk if there is not one
		 */
		public boolean isAreaLoaded(StructureBoundingBox box, boolean allowEmpty) {
			return unwrap().isAreaLoaded(box, allowEmpty);
		}
		
		/**
		 * Marks an area of vertical colomns as 'dirty' to be saved/resent from the server
		 * 
		 * @param minX	Minimum X coordinate
		 * @param minZ	Minimum Z coordinate
		 * @param maxX	Maximum X coordinate
		 * @param maxZ	Maximum Z coordinate
		 */
		public void markBlocksDirtyVertical(int minX, int minZ, int maxX, int maxZ) {
			unwrap().markBlocksDirtyVertical(minX, minZ, maxX, maxZ);
		}
		
		/**
		 * Marks an entire chunk as 'dirty' to be saved/resent from the server
		 * 
		 * @param pos		Location of a block in the chunk
		 * @param sender	The tileentity requesting a mark dirty.
		 */
		public void markChunkDirty(BlockPos pos, TileEntity sender) {
			unwrap().markChunkDirty(pos, sender);
		}
		
		/**
		 * Gets the chunk that contains the given block location.
		 */
		public Chunk getChunkFromBlockCoords(BlockPos pos) {
			return unwrap().getChunkFromBlockCoords(pos);
		}
		
		/**
		 * Gets a chunk object at the given chunk coordinates. If one does not exist it will be created.
		 */
		public Chunk getChunkFromChunkCoords(int chunkX, int chunkZ) {
			return unwrap().getChunkFromChunkCoords(chunkX, chunkZ);
		}
		
		/**
		 * Gets a set of chunks persisted by forge's chunk loader. Will always be empty on other implementations.
		 * 
		 * @forge	This is part of the Forge API specification
		 * @param <Ticket> A forge chunk manager ticket. 
		 * @return	An ImmutableSetMultimap of chunk coordinate pairs mapped to ForgeChunkManager Ticket objects.
		 */
		
		public <Ticket> ImmutableSetMultimap<ChunkCoordIntPair, Ticket> getPersistentChunks() {
			return ForgeWorld.getPersistentChunks(unwrap());
		}
		
		/**
		 * Sets an item of data to be stored with this world.
		 * @param dataID			A string identifier for the item
		 * @param worldSavedDataIn	Some save data to be stored with the world
		 */
		public void setItemData(String dataID, WorldSavedData worldSavedDataIn) {
			unwrap().setItemData(dataID, worldSavedDataIn);
		}
		
		/**
		 * Loads item data.
		 * 
		 * @param clazz		Type of container used for the data
		 * @param dataID	The string identifier for the data
		 * @return
		 */
		public WorldSavedData loadItemData(Class clazz, String dataID) {
			return unwrap().loadItemData(clazz, dataID);
		}
		
		/**
		 * Gets a unique integer id for a data item stored with this world
		 * 
		 * @param key	String identifier for the item
		 */
		public int getUniqueDataId(String key) {
			return unwrap().getUniqueDataId(key);
		}
		
		/**
		 * Adds a world access to this world's list of world accesses.
		 */
		public void addWorldAccess(IWorldAccess worldAccess) {
			unwrap().addWorldAccess(worldAccess);
		}
		
		/**
		 * Removes a world access from this world's list of world accesses.
		 */
		public void removeWorldAccess(IWorldAccess worldAccess) {
			unwrap().removeWorldAccess(worldAccess);
		}
		
		/**
		 * Checks if the world's save has been locked by another process.
		 * 
		 * @throws MinecraftException if the session is locked.
		 */
		public void checkSessionLock() throws MinecraftException {
			unwrap().checkSessionLock();
		}
	}
	
	public final class Players extends Category {
		/**
		 * Gets the list of all player entities loaded in the world
		 */
		public List<EntityPlayer> getPlayerEntityList() {
			return unwrap().playerEntities;
		}
		
		/**
		 * Checks if there are any players within tracking range of the given position
		 * 
		 * @param x			X coordinate
		 * @param y			Y coordinate
		 * @param z			Z coordinate
		 * @param range		The tracking range
		 * @return true if a player is within range
		 */
		public boolean isAnyPlayerWithinRangeAt(double x, double y, double z, double range) {
			return unwrap().isAnyPlayerWithinRangeAt(x, y, z, range);
		}
		
		/**
		 * Gets a list of players of the given type and that matches the given predicate
		 * 
		 * @param playerType	The type of player
		 * @param filter		The predicate
		 * @return All matching player entities
		 */
		public <P extends EntityPlayer> List<P> getPlayers(Class<P> playerType, Predicate filter) {
			return unwrap().getPlayers(playerType, filter);
		}
		
		/**
		 * Gets a player by their display name.
		 */
		public EntityPlayer getByName(String name) {
			return unwrap().getPlayerEntityByName(name);
		}
		
		/**
		 * Gets a player entity by their unique user identifier.
		 */
		public EntityPlayer getByUUID(UUID uuid) {
			return unwrap().getPlayerEntityByUUID(uuid);
		}
		
		/**
		 * Gets the closest player within a certain distance of a given point or null if none were found.
		 * 
		 * @param x			X coordinate
		 * @param y			Y coordinate
		 * @param z			Z coordinate
		 * @param range		The tracking range
		 * @return The closest player of null if none were found
		 */
		public EntityPlayer getClosest(double x, double y, double z, double range) {
			return unwrap().getClosestPlayer(x, y, z, range);
		}
		
		/**
		 * Gets the closest player to the given entity within a given distance or null if none were found.
		 * 
		 * @param entity	The entity
		 * @param distance	The distance upto which to search
		 * @return The closest player or null if none were found
		 */
		public EntityPlayer getClosestToEntity(Entity entity, double distance) {
			return unwrap().getClosestPlayerToEntity(entity, distance);
		}
	}
	
	public final class Weather extends Category {
		/**
		 * Gets the position rain is falling at the given x and z coordinates.
		 * @param pos	The location
		 * @return A position where there is rain falling
		 */
		public BlockPos getPrecipitationHeight(BlockPos pos) {
			return unwrap().getPrecipitationHeight(pos);
		}
		
		/**
		 * Gets the strength of storm currently occuring in the world.
		 * 
		 * @param delta		Minecraft partial ticks, used as a scaling factor for how much the thundering strength must change since the previous
		 * @return	The thunder strength
		 */
		public float getThunderStrength(float delta) {
			return unwrap().getThunderStrength(delta);
		}
		
		/**
		 * Sets the thunder storm strength and resets the delta to 0.
		 */
		public void setThunderStrength(float strength) {
			unwrap().setThunderStrength(strength);
		}
		
		/**
		 * Gets the rain intensity in the world.
		 * @param delta		Minecraft partial ticks, used as a scaling factor for how much the rain strength must change since the previous
		 * @return	The rain strength
		 */
		public float getRainStrength(float delta) {
			return unwrap().getRainStrength(delta);
		}
		
		/**
		 * Sets the rain intensity and resets the delta to 0.
		 */
		public void setRainStrength(float strength) {
			unwrap().setRainStrength(strength);
		}
		
		/**
		 * Returns true if a thunderstorm is in progress.
		 */
		public boolean isThundering() {
			return unwrap().isThundering();
		}
		
		/**
		 * Returns true if it is raining.
		 */
		public boolean isRaining() {
			return unwrap().isRaining();
		}
		
		/**
		 * Chacks if the conditions are sufficient for lighting to strike at the given location
		 * 
		 * @param pos	The location
		 * @return True if lightning can strike
		 */
		public boolean canLightningStrike(BlockPos pos) {
			return unwrap().canLightningStrike(pos);
		}
		
		/**
		 * Gets the amount of time that has passed since lightning last striked.
		 * @return The time since the last trike in ticks.
		 */
		public int getLastLightningBolt() {
			return unwrap().getLastLightningBolt();
		}
		
		/**
		 * Sets the number of ticks since a lightningbold has striked.
		 */
		public void setLastLightningBolt(int ticksSince) {
			unwrap().setLastLightningBolt(ticksSince);
		}
		
		/**
		 * Checks if the humidity at the given location is 'high'
		 * <p>
		 * The point at which humidty is considered 'high' depends on the biome at the location.
		 * 
		 * @param pos	The location
		 * @return True if it is humid.
		 */
		public boolean isHighHumidity(BlockPos pos) {
			return unwrap().isBlockinHighHumidity(pos);
		}
		
		/**
		 * Checks is the conditions are right for snow to fall.
		 *  
		 * @param pos			The location
		 * @param checkLight	True to also check for lighting that would warm up the environment
		 * 
		 * @return True if it can snow here
		 */
		public boolean canSnowAt(BlockPos pos, boolean checkLight) {
			return unwrap().canSnowAt(pos, checkLight);
		}
		
		/**
		 * Adds a weather effect to the world (usually lightningbolts).
		 * 
		 * @return true is the operation was successful
		 */
		public boolean addWeatherEffect(EntityWeatherEffect weatherEffect) {
			return unwrap().addWeatherEffect(weatherEffect);
		}
	}
	
	public final class SoundEffects extends Category {
		/**
		 * Plays a sound to everyone in the world.
		 * 
		 * @param soundType		Integer id for the type of sound.
		 * @param pos			The source location of the sound
		 * @param volume		Volume
		 */
		public void playBroadcastSound(int soundType, BlockPos pos, int volume) {
			unwrap().playBroadcastSound(soundType, pos, volume);
		}
		
		/**
		 * Plays a sound effect to all entities nearby except the given player.
		 * 
		 * @param player	Player not to recieve the sound (usually the sender)
		 * @param name		The sound name
		 * @param volume	Volume
		 * @param pitch		Pitch
		 */
		public void playSoundToNearExcept(EntityPlayer player, String name, float volume, float pitch) {
			unwrap().playSoundToNearExcept(player, name, volume, pitch);
		}
		
		/**
		 * Plays a standard sound effect at the given location.
		 * 
		 * @blazeloader This is part of the BlazeLoader API specification
		 * @param soundType	The effect to spawn 
		 * @param pos		The location
		 * @param volume	Volume
		 */
		public void playAuxSFX(AuxilaryEffects soundType, BlockPos pos, int volume) {
			ApiWorld.playAuxSFX(unwrap(), soundType, pos, volume);
		}
		
		/**
		 * Plays a standard sound effect at the given location.
		 *  
		 * @param soundType	Integer id for the type of sound. 
		 * @param pos		The location
		 * @param volume	Volume
		 */
		public void playAuxSFX(int soundType, BlockPos pos, int volume) {
			unwrap().playAuxSFX(soundType, pos, volume);
		}
		
		/**
		 * Plays a standard sound effect at the given location to the given player.
		 * 
		 * @blazeloader This is part of the BlazeLoader API specification
		 * @param player	The player to recieve the sound
		 * @param soundType	The effect to spawn
		 * @param pos		The location
		 * @param volume	Volume
		 */
		public void playAuxSFX(EntityPlayer player, AuxilaryEffects soundType, BlockPos pos, int volume) {
			ApiWorld.playAuxSFX(unwrap(), player, soundType, pos, volume);
		}
		
		/**
		 * Plays a standard sound effect at the given location to the given player.
		 *  
		 * @param player	The player to recieve the sound
		 * @param soundType	Integer id for the type of sound. 
		 * @param pos		The location
		 * @param volume	Volume
		 */
		public void playAuxSFX(EntityPlayer player, int soundType, BlockPos pos, int volume) {
			unwrap().playAuxSFXAtEntity(player, soundType, pos, volume);
		}
		
		/**
		 * Plays a sound effect in the world.
		 * 
		 * @param x				X coordinate
		 * @param y				Y coordinate
		 * @param z				Z coordinate
		 * @param soundName		The type of sound
		 * @param volume		Volume
		 * @param pitch			Pitch
		 */
		public void playSoundEffect(double x, double y, double z, String soundName, float volume, float pitch) {
			unwrap().playSoundEffect(x, y, z, soundName, volume, pitch);
		}
		
		/**
		 * Plays a sound effect in the world.
		 * 
		 * @param x				X coordinate
		 * @param y				Y coordinate
		 * @param z				Z coordinate
		 * @param soundName		The type of sound
		 * @param volume		Volume
		 * @param pitch			Pitch
		 * @param distanceDelay False to play the sound to everyone instantly
		 */
		public void playSound(double x, double y, double z, String soundName, float volume, float pitch, boolean distanceDelay) {
			unwrap().playSound(x, y, z, soundName, volume, pitch, distanceDelay);
		}
		
		/**
		 * Plays a sound effect near the given entity.
		 * 
		 * @param source		The entity to act as thr source of the sound.
		 * @param soundName		The type of sound
		 * @param volume		Volume
		 * @param pitch			Pitch
		 */
		public void playSound(Entity source, String soundName, float volume, float pitch) {
			unwrap().playSoundAtEntity(source, soundName, volume, pitch);
		}
		
		/**
		 * Plays a record.
		 * 
		 * @param pos		Location of the record player
		 * @param name		Name of record to play
		 */
		public void playRecord(BlockPos pos, String name) {
			unwrap().playRecord(pos, name);
		}
	}
	
	public final class Time extends Category {
		
		/**
		 * Gets a calender reflecting the current in-game time.
		 */
		public Calendar getCurrentDate() {
			return unwrap().getCurrentDate();
		}
		
		/**
		 * Gets the world's current time
		 */
		public long getTime() {
			return unwrap().getWorldTime();
		}
		
		/**
		 * Sets the world's current time
		 */
		public void setTime(long time) {
			unwrap().setWorldTime(time);
		}
		
		/**
		 * Sets the time in ticks since the world was created.
		 */
		public void setAge(long worldTime) {
			unwrap().setTotalWorldTime(worldTime);
		}
		
		/**
		 * Gets the number of ticks the world has existed.
		 */
		public long getAge() {
			return unwrap().getTotalWorldTime();
		}
		
		/**
		 * Returns true if it is day.
		 */
		public boolean isDay() {
			return unwrap().isDaytime();
		}
		
		/**
		 * Gets the current moon phase.
		 */
		public MoonPhase getMoonPhase() {
			return ApiWorld.getMoonPhase(unwrap());
		}
		
		/**
		 * Gets a float factor for the world's current moon phase.
		 */
		public float getCurrentMoonPhaseFactor() {
			return unwrap().getCurrentMoonPhaseFactor();
		}
		
		/**
		 * Gets the angle of the sun/moon based on the world's current time.
		 * 
		 * @param partialTicks	Minecraft partial ticks
		 * @return Celestial angle in degrees
		 */
		public float getCelestialAngle(float partialTicks) {
			return unwrap().getCelestialAngle(partialTicks);
		}
		
		/**
		 * Gets the angle of the sun/moon based on the world's current time.
		 * <p>
		 * Note: pi/2 radians = 180 deg
		 * 
		 * @param partialTicks	Minecraft partial ticks
		 * @return Celestial angle in radians
		 */
		public float getCelestialAngleRadians(float partialTicks) {
			return unwrap().getCelestialAngleRadians(partialTicks);
		}
	}
	
	public final class Power extends Category {
		/**
		 * Gets the level of direct redstone power being emmited in the given direction.
		 * 
		 * @param pos		The location
		 * @param facing	The direction we are checking for redstone signal
		 * @return	Level of redstone being emmited in this direction
		 */
		public int getRedstonePower(BlockPos pos, EnumFacing facing) {
			return unwrap().getRedstonePower(pos, facing);
		}
		
		/**
		 * Notifies comparitors around to block to update their computed output signal. 
		 * 
		 * @param pos		Location of the block
		 * @param block		The type of block
		 */
		public void updateComparatorOutputLevel(BlockPos pos, Block block) {
			unwrap().updateComparatorOutputLevel(pos, block);
		}
		
		/**
		 * Checks if the side if a block is recieving redstone power.
		 * 
		 * @param pos	The location
		 * @param side	The side that we are checking for redstone input
		 * @return true if the block is recieving power from that direction
		 */
		public boolean isSidePowered(BlockPos pos, EnumFacing side) {
			return unwrap().isSidePowered(pos, side);
		}
		
		/**
		 * Returns true if a block is recieving redstone signal from any direction
		 * 
		 * @param pos	The location
		 * @return true if it is powered
		 */
		public boolean isBlockPowered(BlockPos pos) {
			return unwrap().isBlockPowered(pos);
		}
		
		/**
		 * Checks if a block is getting power, either through a neighbour or directly. 
		 * 
		 * @param pos	The location
		 * @return true if the block is powered
		 */
		public boolean isBlockPoweredIndirectly(BlockPos pos) {
			return unwrap().isBlockIndirectlyGettingPowered(pos) > 0;
		}
		
		/**
		 * Gets the level of power a block is recieving either directly or through a neighbouring block.
		 * 
		 * @param pos	The location
		 * 
		 * @return	the level of redstone power a block is recieving
		 */
		public int getBlockPower(BlockPos pos) {
			return unwrap().isBlockIndirectlyGettingPowered(pos);
		}
		
		/**
		 * Gets the level of redstone power a block is recieving directly. 
		 * 
		 * @param pos		The location
		 * 
		 * @return the level of power being recieved
		 */
		public int getStrongPower(BlockPos pos) {
			return unwrap().getStrongPower(pos);
		}
		
		/**
		 * Gets the level of redstone power a block is recieving directly from the given side.
		 * 
		 * @param pos		The location
		 * @param direction	The side to check
		 * 
		 * @return the level of power being recieved on that side
		 */
		public int getStrongPower(BlockPos pos, EnumFacing direction) {
			return unwrap().getStrongPower(pos, direction);
		}
	}
	
    public final class BoundingBoxes extends Category {
    	/**
    	 * Checks if there are any flammable blocks withing the given boundingbox
    	 * 
    	 * @param boundingBox	The area to check
    	 * @return true if there was a flammable block found
    	 */
    	public boolean isFlammableWithin(AxisAlignedBB boundingBox) {
    		return unwrap().isFlammableWithin(boundingBox);
    	}
    	
    	/**
    	 * Checks if there is any liquid blocks withing the given boundingbox
    	 * 
    	 * @param boundingBox	The area to check
    	 * @return true if any liquid was found
    	 */
    	public boolean isAnyLiquidWithin(AxisAlignedBB boundingBox) {
    		return unwrap().isAnyLiquid(boundingBox);
    	}
    	
    	/**
    	 * Checks if there are any blocks of the given material type inside the given boundingbox
    	 * 
    	 * @param material		The block material
    	 * @param boundingBox	The area to check
    	 * 
    	 * @return true if any blocks with material N were found
    	 */
        public boolean isMaterialIn(Material material, AxisAlignedBB boundingBox) {
        	return unwrap().isMaterialInBB(boundingBox, material);
        }
        
        /**
         * Checks if the given boundingbox is inside a block of the given material.
         * 
         * @param boundingBox	The bounding box to check
         * @param material		Material of the containing block
         * 
         * @return true if a block of given material contains this boundingbox
         */
        public boolean isInMaterial(AxisAlignedBB boundingBox, Material material) {
        	return unwrap().isAABBInMaterial(boundingBox, material);
        }
        
        /**
         * Gets a list of boundingboxes for blocks and entities that intersect the given boundingbox.
         * 
         * @param boundingBox	The area to check
         * 
         * @return a list of boundingboxes for all matched objects
         */
        public List<AxisAlignedBB> getCollidingBlockBoundingBoxes(AxisAlignedBB boundingBox) {
        	return unwrap().func_147461_a(boundingBox);
        }
        
        /**
         * Gets a list of boundingboxes for blocks that intersect the given boundingbox but do not intersect the boundingbox of the given entity.
         * 
         * @param entity		The entity who's bounding box is to exclude
         * @param boundingBox	The area to check
         * 
         * @return a list of boundingboxes for all matched blocks
         */
        public List<AxisAlignedBB> getCollidingBoundingBoxes(Entity entity, AxisAlignedBB boundingBox) {
        	return unwrap().getCollidingBoundingBoxes(entity, boundingBox);
        }
        
        /**
         * Returns true if there are blocks who's boundingbox intersects the given area.
         * 
         * @param boundingBox	Area to check
         * 
         * @return true if there were blocks found
         */
        public boolean checkBlockCollision(AxisAlignedBB boundingBox) {
        	return unwrap().checkBlockCollision(boundingBox);
        }
        
        /**
         * Returns true if there are any entities who's boundingbox intersect the given area and who allow collisions.
         * 
         * @param boundingBox	Area to check
         * 
         * @return true if there were entities found
         */
        public boolean checkNoEntityCollision(AxisAlignedBB boundingBox) {
        	return unwrap().checkNoEntityCollision(boundingBox, null);
        }
        
        /**
         * Returns true if there are any entities who's boundingbox intersect the given area,
         * who allow collisions, and do not intersect the boundingbox of the given entity
         * 
         * @param boundingBox	Area to check
         * @param entity		The entity who's boundingbox is to exclude
         * 
         * @return true if there were entities found
         */
        public boolean checkNoEntityCollision(AxisAlignedBB boundingBox, Entity entity) {
        	return unwrap().checkNoEntityCollision(boundingBox, entity);
        }
    }
	
	public final class Network extends Category {
		
		/**
		 * Sends a disconnecting packet if this is a multiplayer world.
		 */
		public void sendQuittingDisconnectingPacket() {
			unwrap().sendQuittingDisconnectingPacket();
		}
		
		/**
		 * Updates the progress on a block's damage.
		 * 
		 * @param breakerId		Id of the entity mining the block
		 * @param pos			The location of the block
		 * @param progress		How much the block is broken. (-1 for broken)
		 */
		public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
			unwrap().sendBlockBreakProgress(breakerId, pos, progress);
		}
	}
	
	public final class GameRuleSet extends Category implements Iterable {
		
		/**
		 * Gets the gamerules set for this world.
		 */
		public GameRules getGameRules() {
			return unwrap().getGameRules();
		}
		
		/**
		 * Gets an array of all game rule entries for this world.
		 * 
		 * @blazeloader This is part of the BlazeLoader API specification
		 */
		public GameRule[] getAllGameRules() {
			return ApiGameRule.getAllGameRules(unwrap());
		}
		
		/**
		 * Gets a single gamerule for this world.
		 * @blazeloader This is part of the BlazeLoader API specification
		 * @param key	The string key to identify the desired gamerule
		 * 
		 * @return A gamerule with the given key
		 */
		public GameRule getGameRule(String key) {
			return ApiGameRule.getGameRule(unwrap(), key);
		}
		
		/**
		 * Adds a new gamerule
		 * 
		 * @blazeloader This is part of the BlazeLoader API specification
		 * @param name			Name used to identity the gamerule
		 * @param defaultValue	A default initial value
		 * @param type			The type of value this rule accepts
		 */
		public void add(String name, String defaultValue, GameRules.ValueType type) {
			ApiGameRule.addGameRule(unwrap(), name, defaultValue, type);
		}
		
		/**
		 * Sets a gamerule value. Will create one that accepts any time if the gamerule was not found
		 * 
		 * @param name			Name used to identity the gamerule
		 * @param value			A value to assign to it
		 */
		public void set(String name, String value) {
			getGameRules().setOrCreateGameRule(name, value);
		}
		
		/**
		 * Gets the value of a gamerule
		 * @param name			Name used to identity the gamerule
		 */
		public String get(String name) {
			return getGameRules().getGameRuleStringValue(name);
		}
		
		/**
		 * Gets the value of a gamerule parse to a boolean
		 * @param name			Name used to identity the gamerule
		 */
		public boolean getBool(String name) {
			return getGameRules().getGameRuleBooleanValue(name);
		}
		
		/**
		 * Gets the value of a gamerule parsed to an integer
		 * @param name			Name used to identity the gamerule
		 */
		public int getInt(String name) {
			return getGameRules().getInt(name);
		}
		
		/**
		 * Gets the value of a gamerule parsed to a double
		 * @param name			Name used to identity the gamerule
		 */
		public double getDouble(String name) {
			try {
				return Double.parseDouble(get(name));
			} catch (NumberFormatException e) {}
			return (double)getInt(name);
		}
		
		/**
		 * Gets an array of all gamerule names.
		 */
		public String[] keys() {
			return getGameRules().getRules();
		}
		
		/**
		 * Gets an array of all gamerules.
		 * 
		 * @blazeloader This is part of the BlazeLoader API specification
		 */
		public GameRule[] values() {
			return ApiGameRule.getAllGameRules(unwrap());
		}
		
		public Iterator iterator() {
			return Lists.newArrayList(values()).iterator();
		}
	}
	
	public final class Server extends Category implements IThreadListener {
		/**
		 * Gets the underlying MinecraftServer for this world if it exists
		 * @return The current running MinecraftWorld
		 */
		public MinecraftServer getServer() {
			if (isServer()) {
				return ((WorldServer)unwrap()).getMinecraftServer();
			}
			return MinecraftServer.getServer();
		}
		
		/**
		 * Adds a task to the world.
		 * <p>
		 * Only works if the underlying world is an instance of WorldServer
		 */
		public ListenableFuture addScheduledTask(Runnable runnableToSchedule) {
			if (unwrap() instanceof IThreadListener) {
				return ((IThreadListener)unwrap()).addScheduledTask(runnableToSchedule);
			}
			return null;
		}
		
		/**
		 * Checks if the underlying world is being called from the main minecraft thread.
		 * <p>
		 * WorldClient worlds will always return true
		 */
		public boolean isCallingFromMinecraftThread() {
			if (unwrap() instanceof IThreadListener) {
				return ((IThreadListener)unwrap()).isCallingFromMinecraftThread();
			}
			return true;
		}
	}
}
