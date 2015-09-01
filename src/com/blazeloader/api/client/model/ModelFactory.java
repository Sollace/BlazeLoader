package com.blazeloader.api.client.model;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

/**
 * Utility class for making it easier to build entity models.
 */
public class ModelFactory {
	
	private List<ModelFactory> children = null;
	private List<ModelComponent> boxes = null;
	
	private boolean isInBuild = false;
	private ModelRenderer buildResult = null;
	
	private ModelBase modelBase;
	
	private boolean mirror = false;
	private boolean hidden = false;
	
	private float[] offset = null;
	private float[] rotation = null;
	private float[] rotationPoint = null;
	
	private int[] texSize = null;
	private int[] texOffset = new int[2];
	
	private ModelFactory(ModelBase model) {
		modelBase = model;
	}
	
	/**
	 * Builds a model renderer based on the settings defined in this factory.
	 * <p>
	 * You would call this at the end to get back a completed ModelRenderer. 
	 */
	public ModelRenderer build() {
		if (isInBuild) throw new RuntimeException("Circular parent-child relationship detected! Can only perform one build at a time.");
		if (buildResult != null) return buildResult;
		
		buildResult = new ModelRenderer(modelBase, texOffset[0], texOffset[1]);
		buildResult.mirror = mirror;
		if (rotation != null) {
			buildResult.rotateAngleX = rotation[0];
			buildResult.rotateAngleY = rotation[1];
			buildResult.rotateAngleZ = rotation[2];
		}
		if (offset != null) {
			buildResult.offsetX = offset[0];
			buildResult.offsetY = offset[1];
			buildResult.offsetZ = offset[2];
		}
		if (rotationPoint != null) {
			buildResult.setRotationPoint(rotationPoint[0], rotationPoint[1], rotationPoint[2]);
		}
		if (texSize != null) {
			buildResult.setTextureSize(texSize[0], texSize[1]);
		}
		buildResult.setTextureOffset(texOffset[0], texOffset[1]);
		buildResult.isHidden = hidden;
		isInBuild = true;
		if (boxes != null) {
			for (ModelComponent i : boxes) {
				i.build(buildResult, texOffset[0], texOffset[1]);
			}
		}
		if (children != null) {
			for (ModelFactory i : children) {
				buildResult.addChild(i.build());
			}
		}
		isInBuild = false;
		return buildResult;
	}
	
	/**
	 * Adds another ModelFactory as a child of this one.
	 * 
	 * @return Returns this model factory
	 */
	public ModelFactory append(ModelFactory other) {
		if (children == null) children = new ArrayList<ModelFactory>();
		children.add(other);
		return this;
	}
	
	/**
	 * Creates a new ModelFactory and initialises it as a child of this one
	 *  
	 * @param texOffsetX	Texture offset X
	 * @param texOffsetY	Texture offset Y
	 * @param rotX			Rotation angle X in radians
	 * @param rotY			Rotation angle Y in radians
	 * @param rotZ			Rotation angle Z in radians
	 * 
	 * @return The child ModelFactory
	 */
	public ModelFactory child(int texOffsetX, int texOffsetY, float rotX, float rotY, float rotZ) {
		ModelFactory child = ModelFactory.create(modelBase, texOffsetX, texOffsetY, rotX, rotY, rotZ);
		append(child);
		return child;
	}
	
	/**
	 * Adds a box to this ModelRactory.
	 * 
	 * @param oriX			Box origin X
	 * @param oriY			Box origin Y
	 * @param oriZ			Box origin Z
	 * @param width			Box width (z)
	 * @param height		Box height (y)
	 * @param depth			Box depth (x)
	 * @param scaleFactor	An addition scale factor to offset the size of the box. Used when scaling a model.
	 * 
	 * @return	This ModelFactory
	 */
	public ModelFactory addBox(float oriX, float oriY, float oriZ, int width, int height, int depth, float scaleFactor) {
		if (boxes == null) boxes = new ArrayList<ModelComponent>();
		boxes.add(new Box(oriX, oriY, oriZ, width, height, depth, scaleFactor));
		return this;
	}
	
	/**
	 * Adds a box with side independent uv mapping to this ModelRactory.
	 * 
	 * @param oriX			Box origin X
	 * @param oriY			Box origin Y
	 * @param oriZ			Box origin Z
	 * @param width			Box width (z)
	 * @param height		Box height (y)
	 * @param depth			Box depth (x)
	 * @param scaleFactor	An addition scale factor to offset the size of the box. Used when scaling a model.
	 * 
	 * @return	the box so modders may adjust uv mappings
	 */
	public BoxUV addBoxUV(float oriX, float oriY, float oriZ, int width, int height, int depth, float scaleFactor) {
		if (boxes == null) boxes = new ArrayList<ModelComponent>();
		BoxUV box = new BoxUV(oriX, oriY, oriZ, width, height, depth, scaleFactor);
		boxes.add(box);
		return box;
	}
	
	/**
	 * Adds a plane to this ModelRactory.
	 * <p>
	 * A plane in this definition is essentially a box that can have any (or all) of its sides disabled.
	 * 
	 * @param oriX			Box origin X
	 * @param oriY			Box origin Y
	 * @param oriZ			Box origin Z
	 * @param width			Box width (z)
	 * @param height		Box height (y)
	 * @param depth			Box depth (x)
	 * @param scaleFactor	An addition scale factor to offset the size of the box. Used when scaling a model.
	 * 
	 * @return	the plane to modders may toggle visibility of sides
	 */
	public Plane addPlane(float oriX, float oriY, float oriZ, int width, int height, int depth, float scaleFactor) {
		if (boxes == null) boxes = new ArrayList<ModelComponent>();
		Plane box = new Plane(oriX, oriY, oriZ, width, height, depth, scaleFactor);
		boxes.add(box);
		return box;
	}
	
	/**
	 * Marks the ModelRenderer generated by this factory to have mirrored textures.
	 * 
	 * @return	This ModelFactory 
	 */
	public ModelFactory mirror() {
		mirror = true;
		return this;
	}
	
	/**
	 * Set an offset amount.
	 * 
	 * @param x		Offset X
	 * @param y		Offset Y
	 * @param z		Offset Z
	 * 
	 * @return	This ModelFactory	
	 */
	public ModelFactory offset(float x, float y, float z) {
		if (offset == null) offset = new float[3];
    	offset[0] = x;
    	offset[1] = y;
    	offset[2] = z;
		return this;
	}
	
	/**
	 * Set rotation angles
	 * 
	 * @param x		Rotate X
	 * @param y		Rotate Y
	 * @param z		Rotate Z
	 * 
	 * @return	This ModelFactory
	 */
	public ModelFactory rotate(float x, float y, float z) {
		if (rotation == null) rotation = new float[3];
		rotation[0] = x;
		rotation[1] = y;
		rotation[2] = z;
		return this;
	}
	
	/**
	 * Set the texture offset distance. Relative to the top-left corner of the texture file.
	 * <p>
	 * Units are measured in texture units, pixel units assuming a 64x texture file.
	 * <br>
	 * x: 0 to texture width
	 * <br>
	 * y: 0 to texture-height
	 * 
	 * @param texX	Texture Offset X
	 * @param texY	Texture Offset Y
	 * 
	 * @return	This ModelFactory
	 */
	public ModelFactory setTextureOffset(int texX, int texY) {
		texOffset[0] = texX;
		texOffset[1] = texY;
		return this;
	}
	
	/**
	 * Sets the texture size for this model renderer. This is the actual size of the texture file used.
	 * <p>
	 * Units are measured in texture units, pixel units assuming a 64x texture file.
	 * 
	 * @param width		Texture Width (default: 64)
	 * @param height	Texture Height (default: 32)
	 * 
	 * @return	This ModelFactory
	 */
	public ModelFactory setTextureSize(int width, int height) {
		if (texSize == null) texSize = new int[2];
		texSize[0] = width;
		texSize[1] = height;
		return this;
	}
	
	/**
	 * Sets the rotation point for this model.
	 * 
	 * @param x		Rotation-point X
	 * @param y		Rotation-point Y
	 * @param z		Rotation-point Z
	 * 
	 * @return	This ModelFactory
	 */
	public ModelFactory setRotationPoint(float x, float y, float z) {
		if (rotationPoint == null) rotationPoint = new float[3];
		rotationPoint[0] = x;
		rotationPoint[1] = y;
		rotationPoint[2] = z;
		return this;
	}
	
	/**
	 * Sets the visibility of the model. Whether it is hidden or not.
	 */
	public void setVisibility(boolean visible) {
		hidden = !visible;
	}
	
	/**
	 * Creates a new ModelFactory and initialises it with a texture offset and rotation point.
	 * <p>
	 * After this you would call addBox to add the first cube.
	 * 
	 * @param modelBase		The ModelBase for which we are constructing cubes 
	 * @param texOffsetX	Texture Offset X
	 * @param texOffsetY	Texture Offset Y
	 * @param rotX			Rotation-point X
	 * @param rotY			Rotation-point Y
	 * @param rotZ			Rotation-point Z
	 * 
	 * @return A newly created ModelFactory
	 */
	public static ModelFactory create(ModelBase modelBase, int texOffsetX, int texOffsetY, float rotX, float rotY, float rotZ) {
		return new ModelFactory(modelBase).setTextureOffset(texOffsetX, texOffsetY).setRotationPoint(rotX, rotY, rotZ);
	}
	
	private interface ModelComponent {
		public void build(ModelRenderer renderer, int texX, int texY);
	}
	
	public static class Box implements ModelComponent {
		float[] origin = new float[3];
		int[] size = new int[3];
		float scaleFactor = 0;
		
		Box(float oriX, float oriY, float oriZ, int width, int height, int depth, float scale) {
			origin[0] = oriX;
			origin[1] = oriY;
			origin[2] = oriZ;
			size[0] = width;
			size[1] = height;
			size[2] = depth;
			scaleFactor = scale;
		}
		
		public void build(ModelRenderer renderer, int texX, int texY) {
			renderer.addBox(origin[0], origin[1], origin[2], size[0], size[1], size[2], scaleFactor);
		}
	}
	
	public static class BoxUV implements ModelComponent {
		float[] origin = new float[3];
		int[] size = new int[3];
		float scaleFactor = 0;
		
		Integer[][] uvmappings;
		
		BoxUV(float oriX, float oriY, float oriZ, int width, int height, int depth, float scale) {
			origin[0] = oriX;
			origin[1] = oriY;
			origin[2] = oriZ;
			size[0] = width;
			size[1] = height;
			size[2] = depth;
			scaleFactor = scale;
			uvmappings = new Integer[2][6];
		}
		
		public BoxUV setSideUV(int side, int u, int v) {
			uvmappings[side] = new Integer[] {u,v};
			return this;
		}
		
		public void build(ModelRenderer renderer, int texX, int texY) {
			ModelUVMappedBox box = new ModelUVMappedBox(renderer, texX, texY, origin[0], origin[1], origin[2], size[0], size[1], size[2], scaleFactor);
			for (int i = 0; i < uvmappings.length; i++) {
				if (uvmappings[i] != null) box.setFaceUV(i, uvmappings[i][0], uvmappings[i][1]);
			}
			renderer.cubeList.add(box);
		}
	}
	
	public static class Plane extends Box {
		boolean[] hiddenSides = new boolean[6];
		
		Plane(float oriX, float oriY, float oriZ, int width, int height, int depth, float scale) {
			super(oriX, oriY, oriZ, width, height, depth, scale);
		}
		
		public void setSideVisibility(int side, boolean visible) {
			hiddenSides[side] = !visible;
		}
		
		public void build(ModelRenderer renderer, int texX, int texY) {
			renderer.cubeList.add((new ModelPlane(renderer, texX, texY, origin[0], origin[1], origin[2], size[0], size[1], size[2], scaleFactor)).setHiddenSides(hiddenSides));
		}
	}
}
