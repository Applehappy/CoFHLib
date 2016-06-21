package cofh.lib;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.Vec3i;

public enum BlockFacing implements IStringSerializable
{
    DOWN(	0, 1, "down", new Vec3i(0, -1, 0), 	EnumFacing.DOWN),
    UP(		1, 0, "up", new Vec3i(0, 1, 0),		EnumFacing.UP),
    NORTH(	2, 3, "north", new Vec3i(0, 0, -1),	EnumFacing.NORTH),
    SOUTH(	3, 2, "south", new Vec3i(0, 0, 1), 	EnumFacing.SOUTH),
    WEST(	4, 5, "west", new Vec3i(-1, 0, 0),	EnumFacing.WEST),
    EAST(	5, 4, "east", new Vec3i(1, 0, 0),	EnumFacing.EAST),
    UNKNOWN(6, 6, "unknown", new Vec3i(0, 0, 0), null);
    
    /** Ordering index for D-U-N-S-W-E */
    private final int index;
    /** Index of the opposite Facing in the VALUES array */
    private final int opposite;
    private final String name;
    /** Normalized Vector that points in the direction of this Facing */
    private final Vec3i directionVec;
    /** All facings in D-U-N-S-W-E order */
    public static final BlockFacing[] VALUES = new BlockFacing[]{DOWN,UP,NORTH,SOUTH,WEST,EAST};
    
    public final EnumFacing enumFacing;

	private BlockFacing(int indexIn, int oppositeIn, String nameIn, Vec3i directionVecIn, EnumFacing facing) {
		this.index = indexIn;
		this.opposite = oppositeIn;
		this.name = nameIn;
		this.directionVec = directionVecIn;
		this.enumFacing = facing;
	}
    
    public static BlockFacing fromEnumFacing(EnumFacing ef){
    	return BlockFacing.VALUES[ef.getIndex()];
    }
    
    public EnumFacing toEnumFacing(){
    	if(this == UNKNOWN) return null;
    	return EnumFacing.VALUES[getIndex()];
    }
    
    public static BlockFacing fromIndex(int index){
    	return VALUES[index];
    }

    /**
     * Get the Index of this Facing (0-5). The order is D-U-N-S-W-E
     */
	public int getIndex() {
		return this.index;
	}

    /**
     * Get the opposite Facing (e.g. DOWN => UP)
     */
    public BlockFacing getOpposite(){
    	if(this == UNKNOWN) return this;
    	return VALUES[opposite];
    }

    /**
     * Rotate this Facing around the given axis clockwise. If this facing cannot be rotated around the given axis,
     * returns this facing without rotating.
     */
	public BlockFacing rotateAround(EnumFacing.Axis axis) {
		switch (axis) {
			case X:
	
				if (this != WEST && this != EAST) {
					return this.rotateX();
				}
	
				return this;
			case Y:
	
				if (this != UP && this != DOWN) {
					return this.rotateY();
				}
	
				return this;
			case Z:
	
				if (this != NORTH && this != SOUTH) {
					return this.rotateZ();
				}
	
				return this;
			default:
				return UNKNOWN;
		}
	}

    /**
     * Rotate this Facing around the Y axis clockwise (NORTH => EAST => SOUTH => WEST => NORTH)
     */
    public BlockFacing rotateY()
    {
        switch (this)
        {
            case NORTH:
                return EAST;
            case EAST:
                return SOUTH;
            case SOUTH:
                return WEST;
            case WEST:
                return NORTH;
            default:
                return UNKNOWN;
        }
    }

    /**
     * Rotate this Facing around the X axis (NORTH => DOWN => SOUTH => UP => NORTH)
     */
    private BlockFacing rotateX()
    {
        switch (this)
        {
            case NORTH: return DOWN;
            case SOUTH: return UP;
            case UP: return NORTH;
            case DOWN: return SOUTH;
            case EAST: case WEST: default: return UNKNOWN;
        }
    }

    /**
     * Rotate this Facing around the Z axis (EAST => DOWN => WEST => UP => EAST)
     */
    private BlockFacing rotateZ()
    {
        switch (this)
        {
            case EAST: return DOWN;
            case WEST: return UP;
            case UP: return EAST;
            case DOWN: return WEST;
            
            case SOUTH: default: return UNKNOWN;
        }
    }

    /**
     * Rotate this Facing around the Y axis counter-clockwise (NORTH => WEST => SOUTH => EAST => NORTH)
     */
    public BlockFacing rotateYCCW()
    {
        switch (this)
        {
            case NORTH: return WEST;
            case EAST: return NORTH;
            case SOUTH: return EAST;
            case WEST: return SOUTH;
            default: return UNKNOWN;
        }
    }

	public String toString() {
		return this.name;
	}

	public String getName() {
		return this.name;
	}

    /**
     * Get a normalized Vector that points in the direction of this Facing.
     */
	public Vec3i getDirectionVec() {
		return this.directionVec;
	}
}