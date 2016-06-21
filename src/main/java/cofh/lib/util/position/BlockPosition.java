package cofh.lib.util.position;

import cofh.lib.BlockFacing;
import cofh.lib.util.helpers.BlockHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class BlockPosition implements Comparable<BlockPosition>, Serializable {

	private static final long serialVersionUID = 8671402745765780610L;

	public int x;
	public int y;
	public int z;
	public BlockFacing orientation;
	
	private BlockPos pos = null;
	private boolean posUpToDate = false;

	public BlockPosition(int x, int y, int z) {
		this(x,y,z,BlockFacing.UNKNOWN);
	}

	public BlockPosition(int x, int y, int z, BlockFacing orientation) {
		this.x = x;
		this.y = y;
		this.z = z;
		updatePos();
		
		this.orientation = orientation;
	}

	public BlockPosition(BlockPosition p) {
		x = p.x;
		y = p.y;
		z = p.z;
		updatePos();
		
		orientation = p.orientation;
	}

	public BlockPosition(NBTTagCompound tag) {
		
		x = tag.getInteger("bp_i");
		y = tag.getInteger("bp_j");
		z = tag.getInteger("bp_k");
		updatePos();
		
		if (!tag.hasKey("bp_dir")) orientation = BlockFacing.UNKNOWN;
		else orientation = BlockFacing.fromIndex(tag.getByte("bp_dir"));
	}

	public BlockPosition(TileEntity tile) {
		BlockPos pos = tile.getPos();
		x = pos.getX();
		y = pos.getY();
		z = pos.getZ();
		updatePos();
		
		if (tile instanceof IRotateableTile) orientation = ((IRotateableTile) tile).getDirectionFacing();
		else orientation = BlockFacing.UNKNOWN;
	}
	
	private void updatePos(){
		this.pos = new BlockPos(x,y,z);
		this.posUpToDate = true;
	}
	
	public BlockPos getBlockPos(){
		if(!posUpToDate) updatePos();
		return pos;
	}

	public static <T extends TileEntity & IRotateableTile> BlockPosition fromRotateableTile(T te) {
		return new BlockPosition(te);
	}

	public BlockPosition copy() {
		return new BlockPosition(x, y, z, orientation);
	}

	public BlockPosition copy(BlockFacing orientation) {
		return new BlockPosition(x, y, z, orientation);
	}

	public BlockPosition setOrientation(BlockFacing o) {
		orientation = o;
		return this;
	}

	public BlockPosition step(int dir) {

		int[] d = BlockHelper.SIDE_COORD_MOD[dir];
		x += d[0];
		y += d[1];
		z += d[2];
		return this;
	}

	public BlockPosition step(int dir, int dist) {

		int[] d = BlockHelper.SIDE_COORD_MOD[dir];
		x += d[0] * dist;
		y += d[1] * dist;
		z += d[2] * dist;
		return this;
	}

	public BlockPosition step(BlockFacing dir) {
		Vec3i v = dir.getDirectionVec();
		x += v.getX();
		y += v.getY();
		z += v.getZ();
		return this;
	}

	public BlockPosition step(BlockFacing dir, int dist) {
		Vec3i v = dir.getDirectionVec();
		x += v.getX() * dist;
		y += v.getY() * dist;
		z += v.getZ() * dist;
		return this;
	}

	public BlockPosition moveForwards(int step) {
		switch (orientation) {
		case UP:
			y = y + step;
			break;
		case DOWN:
			y = y - step;
			break;
		case SOUTH:
			z = z + step;
			break;
		case NORTH:
			z = z - step;
			break;
		case EAST:
			x = x + step;
			break;
		case WEST:
			x = x - step;
			break;
		default:
		}
		return this;
	}

	public BlockPosition moveBackwards(int step) {

		return moveForwards(-step);
	}

	public BlockPosition moveRight(int step) {

		switch (orientation) {
		case UP:
		case SOUTH:
			x = x - step;
			break;
		case DOWN:
		case NORTH:
			x = x + step;
			break;
		case EAST:
			z = z + step;
			break;
		case WEST:
			z = z - step;
			break;
		default:
			break;
		}
		return this;
	}

	public BlockPosition moveLeft(int step) {

		return moveRight(-step);
	}

	public BlockPosition moveUp(int step) {

		switch (orientation) {
		case EAST:
		case WEST:
		case NORTH:
		case SOUTH:
			y = y + step;
			break;
		case UP:
			z = z - step;
			break;
		case DOWN:
			z = z + step;
		default:
			break;
		}
		return this;
	}

	public BlockPosition moveDown(int step) {

		return moveUp(-step);
	}

	public void writeToNBT(NBTTagCompound tag) {

		tag.setInteger("bp_i", x);
		tag.setInteger("bp_j", y);
		tag.setInteger("bp_k", z);
		tag.setByte("bp_dir", (byte) orientation.ordinal());
	}

	@Override
	public String toString() {

		if (orientation == null) {
			return "{" + x + ", " + y + ", " + z + "}";
		}
		return "{" + x + ", " + y + ", " + z + ";" + orientation.toString() + "}";
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof BlockPosition)) {
			return false;
		}
		BlockPosition bp = (BlockPosition) obj;
		return bp.x == x & bp.y == y & bp.z == z & bp.orientation == orientation;
	}

	// so compiler will optimize
	public boolean equals(BlockPosition bp) {

		return bp != null && bp.x == x & bp.y == y & bp.z == z & bp.orientation == orientation;
	}

	@Override
	public int hashCode() {

		return (x & 0xFFF) | (y & 0xFF << 8) | (z & 0xFFF << 12);
	}

	public BlockPosition min(BlockPosition p) {

		return new BlockPosition(p.x > x ? x : p.x, p.y > y ? y : p.y, p.z > z ? z : p.z);
	}

	public BlockPosition max(BlockPosition p) {

		return new BlockPosition(p.x < x ? x : p.x, p.y < y ? y : p.y, p.z < z ? z : p.z);
	}

	public List<BlockPosition> getAdjacent(boolean includeVertical) {

		List<BlockPosition> a = new ArrayList<BlockPosition>(4 + (includeVertical ? 2 : 0));
		a.add(copy(BlockFacing.EAST).moveForwards(1));
		a.add(copy(BlockFacing.WEST).moveForwards(1));
		a.add(copy(BlockFacing.SOUTH).moveForwards(1));
		a.add(copy(BlockFacing.NORTH).moveForwards(1));
		if (includeVertical) {
			a.add(copy(BlockFacing.UP).moveForwards(1));
			a.add(copy(BlockFacing.DOWN).moveForwards(1));
		}
		return a;
	}

	public boolean blockExists(World world) {
		return BlockHelper.blockExists(world, getBlockPos());
	}

	public TileEntity getTileEntity(World world) {
		return world.getTileEntity(getBlockPos());
	}

	public Block getBlock(World world) {
		return world.getBlockState(getBlockPos()).getBlock();
	}

	@SuppressWarnings("unchecked")
	public <T> T getTileEntity(World world, Class<T> targetClass) {
		TileEntity te = world.getTileEntity(getBlockPos());
		if (targetClass.isInstance(te)) {
			return (T) te;
		} else {
			return null;
		}
	}

	public static BlockFacing getDirection(int xS, int yS, int zS, int x, int y, int z) {

		int dir = 0;
		if (y < yS) {
			dir |= 1;
		} else if (y != yS) {
			dir |= 2;
		}
		if (z < zS) {
			dir |= 4;
		} else if (z != zS) {
			dir |= 8;
		}
		if (x < xS) {
			dir |= 16;
		} else if (x != xS) {
			dir |= 32;
		}
		switch (dir) {
		case 2:
			return BlockFacing.UP;
		case 1:
			return BlockFacing.DOWN;
		case 4:
			return BlockFacing.WEST;
		case 8:
			return BlockFacing.EAST;
		case 16:
			return BlockFacing.NORTH;
		case 32:
			return BlockFacing.SOUTH;
		default:
			return BlockFacing.UNKNOWN;
		}
	}
	
	/**@deprecated Use {@link #getTileEntityRaw(World world, BlockPos pos)} instead.*/
	@Deprecated
	public static TileEntity getTileEntityRaw(World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		return getTileEntityRaw(world, pos);
	}
	
	public static TileEntity getTileEntityRaw(World world, BlockPos pos) {
		if (!BlockHelper.blockExists(world, pos)) return null;
        ChunkPos chunkposition = new ChunkPos(pos);
        Chunk chunk = world.getChunkFromBlockCoords(pos);
        TileEntity tileentity = (TileEntity)chunk.getTileEntityMap().get(chunkposition);
		return tileentity == null || tileentity.isInvalid() ? null : tileentity;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getTileEntityRaw(World world, int x, int y, int z, Class<T> targetClass) {
		TileEntity te = getTileEntityRaw(world, x, y, z);
		if (targetClass.isInstance(te)) {
			return (T) te;
		} else {
			return null;
		}
	}

	public static boolean blockExists(TileEntity start, BlockFacing dir) {
		Vec3i v = dir.getDirectionVec();
		BlockPos pos = start.getPos().add(v.getX(), v.getY(), v.getZ());
		return BlockHelper.blockExists(start.getWorld(), pos);
	}

	public static TileEntity getAdjacentTileEntity(TileEntity start, BlockFacing dir) {
		Vec3i v = dir.getDirectionVec();
		BlockPos pos = start.getPos().add(v.getX(), v.getY(), v.getZ());
		return getTileEntityRaw(start.getWorld(), pos);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getAdjacentTileEntity(TileEntity start, BlockFacing direction, Class<T> targetClass) {
		TileEntity te = getAdjacentTileEntity(start, direction);
		if (targetClass.isInstance(te)) {
			return (T) te;
		} else {
			return null;
		}
	}

	/* Comparable */
	@Override
	public int compareTo(BlockPosition other) {
		return this.x == other.x ? this.y == other.y ? this.z - other.z : this.y - other.y : this.x - other.x;
	}

}
