package cofh.lib.util.helpers;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Contains various helper functions to assist with {@link Block} and Block-related manipulation and interaction.
 *
 * @author King Lemming
 *
 */
public final class BlockHelper {

	private BlockHelper() {

	}

	public static int MAX_ID = 1024;
	public static byte[] rotateType = new byte[MAX_ID];
	public static final int[][] SIDE_COORD_MOD = { { 0, -1, 0 }, { 0, 1, 0 }, { 0, 0, -1 }, { 0, 0, 1 }, { -1, 0, 0 }, { 1, 0, 0 } };
	public static float[][] SIDE_COORD_AABB = { { 1, -2, 1 }, { 1, 2, 1 }, { 1, 1, 1 }, { 1, 1, 2 }, { 1, 1, 1 }, { 2, 1, 1 } };
	public static final byte[] SIDE_LEFT = { 4, 5, 5, 4, 2, 3 };
	public static final byte[] SIDE_RIGHT = { 5, 4, 4, 5, 3, 2 };
	public static final byte[] SIDE_OPPOSITE = { 1, 0, 3, 2, 5, 4 };
	public static final byte[] SIDE_ABOVE = { 3, 2, 1, 1, 1, 1 };
	public static final byte[] SIDE_BELOW = { 2, 3, 0, 0, 0, 0 };

	// These assume facing is towards negative - looking AT side 1, 3, or 5.
	public static final byte[] ROTATE_CLOCK_Y = { 0, 1, 4, 5, 3, 2 };
	public static final byte[] ROTATE_CLOCK_Z = { 5, 4, 2, 3, 0, 1 };
	public static final byte[] ROTATE_CLOCK_X = { 2, 3, 1, 0, 4, 5 };

	public static final byte[] ROTATE_COUNTER_Y = { 0, 1, 5, 4, 2, 3 };
	public static final byte[] ROTATE_COUNTER_Z = { 4, 5, 2, 3, 1, 0 };
	public static final byte[] ROTATE_COUNTER_X = { 3, 2, 0, 1, 4, 5 };

	public static final byte[] INVERT_AROUND_Y = { 0, 1, 3, 2, 5, 4 };
	public static final byte[] INVERT_AROUND_Z = { 1, 0, 2, 3, 5, 4 };
	public static final byte[] INVERT_AROUND_X = { 1, 0, 3, 2, 4, 5 };

	// Map which gives relative Icon to use on a block which can be placed on any side.
	public static final byte[][] ICON_ROTATION_MAP = new byte[6][];

	static {
		ICON_ROTATION_MAP[0] = new byte[] { 0, 1, 2, 3, 4, 5 };
		ICON_ROTATION_MAP[1] = new byte[] { 1, 0, 2, 3, 4, 5 };
		ICON_ROTATION_MAP[2] = new byte[] { 3, 2, 0, 1, 4, 5 };
		ICON_ROTATION_MAP[3] = new byte[] { 3, 2, 1, 0, 5, 4 };
		ICON_ROTATION_MAP[4] = new byte[] { 3, 2, 5, 4, 0, 1 };
		ICON_ROTATION_MAP[5] = new byte[] { 3, 2, 4, 5, 1, 0 };
	}

	public static final class RotationType {

		public static final int PREVENT = -1;
		public static final int FOUR_WAY = 1;
		public static final int SIX_WAY = 2;
		public static final int RAIL = 3;
		public static final int PUMPKIN = 4;
		public static final int STAIRS = 5;
		public static final int REDSTONE = 6;
		public static final int LOG = 7;
		public static final int SLAB = 8;
		public static final int CHEST = 9;
		public static final int LEVER = 10;
		public static final int SIGN = 11;
	}

	static { // TODO: review which of these can be removed in favor of the vanilla handler
		rotateType[Block.getIdFromBlock(Blocks.BED)] = RotationType.PREVENT;

		rotateType[Block.getIdFromBlock(Blocks.STONE_SLAB)] = RotationType.SLAB;
		rotateType[Block.getIdFromBlock(Blocks.WOODEN_SLAB)] = RotationType.SLAB;

		rotateType[Block.getIdFromBlock(Blocks.RAIL)] = RotationType.RAIL;
		rotateType[Block.getIdFromBlock(Blocks.GOLDEN_RAIL)] = RotationType.RAIL;
		rotateType[Block.getIdFromBlock(Blocks.DETECTOR_RAIL)] = RotationType.RAIL;
		rotateType[Block.getIdFromBlock(Blocks.ACTIVATOR_RAIL)] = RotationType.RAIL;

		rotateType[Block.getIdFromBlock(Blocks.PUMPKIN)] = RotationType.PUMPKIN;
		rotateType[Block.getIdFromBlock(Blocks.LIT_PUMPKIN)] = RotationType.PUMPKIN;

		rotateType[Block.getIdFromBlock(Blocks.FURNACE)] = RotationType.FOUR_WAY;
		rotateType[Block.getIdFromBlock(Blocks.LIT_FURNACE)] = RotationType.FOUR_WAY;
		rotateType[Block.getIdFromBlock(Blocks.ENDER_CHEST)] = RotationType.FOUR_WAY;

		rotateType[Block.getIdFromBlock(Blocks.TRAPPED_CHEST)] = RotationType.CHEST;
		rotateType[Block.getIdFromBlock(Blocks.CHEST)] = RotationType.CHEST;

		rotateType[Block.getIdFromBlock(Blocks.DISPENSER)] = RotationType.SIX_WAY;
		rotateType[Block.getIdFromBlock(Blocks.STICKY_PISTON)] = RotationType.SIX_WAY;
		rotateType[Block.getIdFromBlock(Blocks.PISTON)] = RotationType.SIX_WAY;
		rotateType[Block.getIdFromBlock(Blocks.HOPPER)] = RotationType.SIX_WAY;
		rotateType[Block.getIdFromBlock(Blocks.DROPPER)] = RotationType.SIX_WAY;

		rotateType[Block.getIdFromBlock(Blocks.UNPOWERED_REPEATER)] = RotationType.REDSTONE;
		rotateType[Block.getIdFromBlock(Blocks.UNPOWERED_COMPARATOR)] = RotationType.REDSTONE;
		rotateType[Block.getIdFromBlock(Blocks.POWERED_REPEATER)] = RotationType.REDSTONE;
		rotateType[Block.getIdFromBlock(Blocks.POWERED_COMPARATOR)] = RotationType.REDSTONE;

		rotateType[Block.getIdFromBlock(Blocks.LEVER)] = RotationType.LEVER;

		rotateType[Block.getIdFromBlock(Blocks.STANDING_SIGN)] = RotationType.SIGN;

		rotateType[Block.getIdFromBlock(Blocks.OAK_STAIRS)] = RotationType.STAIRS;
		rotateType[Block.getIdFromBlock(Blocks.STONE_STAIRS)] = RotationType.STAIRS;
		rotateType[Block.getIdFromBlock(Blocks.BRICK_STAIRS)] = RotationType.STAIRS;
		rotateType[Block.getIdFromBlock(Blocks.STONE_BRICK_STAIRS)] = RotationType.STAIRS;
		rotateType[Block.getIdFromBlock(Blocks.NETHER_BRICK_STAIRS)] = RotationType.STAIRS;
		rotateType[Block.getIdFromBlock(Blocks.SANDSTONE_STAIRS)] = RotationType.STAIRS;
		rotateType[Block.getIdFromBlock(Blocks.SPRUCE_STAIRS)] = RotationType.STAIRS;
		rotateType[Block.getIdFromBlock(Blocks.BIRCH_STAIRS)] = RotationType.STAIRS;
		rotateType[Block.getIdFromBlock(Blocks.JUNGLE_STAIRS)] = RotationType.STAIRS;
		rotateType[Block.getIdFromBlock(Blocks.QUARTZ_STAIRS)] = RotationType.STAIRS;
	}

	public static int getMicroBlockAngle(int side, float hitX, float hitY, float hitZ) {

		int direction = side ^ 1;
		float degreeCenter = 0.32f / 2;

		float x = 0, y = 0;
		switch (side >> 1) {
		case 0:
			x = hitX;
			y = hitZ;
			break;
		case 1:
			x = hitX;
			y = hitY;
			break;
		case 2:
			x = hitY;
			y = hitZ;
			break;
		}
		x -= .5f;
		y -= .5f;

		if (x * x + y * y > degreeCenter * degreeCenter) {

			int a = (int) ((Math.atan2(x, y) + Math.PI) * 4 / Math.PI);
			a = ++a & 7;
			switch (a >> 1) {
			case 0:
			case 4:
				direction = 2;
				break;
			case 1:
				direction = 4;
				break;
			case 2:
				direction = 3;
				break;
			case 3:
				direction = 5;
				break;
			}
		}
		return direction;
	}

	public static EnumFacing getMicroBlockAngle(EnumFacing side, float hitX, float hitY, float hitZ) {
		return EnumFacing.VALUES[getMicroBlockAngle(side.ordinal(), hitX, hitY, hitZ)];
	}

	public static int getHighestY(World world, int x, int z) {
		return world.getChunkFromBlockCoords(new BlockPos(x,0,z)).getTopFilledSegment() + 16;
	}
	
	public static int getHighestY(World world, BlockPos pos) {
		return world.getChunkFromBlockCoords(pos).getTopFilledSegment() + 16;
	}

	public static int getSurfaceBlockY(World world, int x, int z) {
		BlockPos bp = new BlockPos(x,0,z);
		int y = getHighestY(world, bp);
		bp = bp.add(0, y, 0);
		
		Block block;
		IBlockState ibs;
		do {
			
			if (--y < 0) break;
			bp = bp.add(0, -1,0);
			ibs =  world.getBlockState(bp);
			block = ibs.getBlock();
			
		} while (	block.isAir(ibs, world, bp) 
				|| 	block.isReplaceable(world, bp) 
				|| 	block.isLeaves(ibs, world, bp) 
				|| 	block.isFoliage(world, bp)
				|| 	block.canBeReplacedByLeaves(ibs, world, bp)
				);
		return y;
	}

	public static int getTopBlockY(World world, int x, int z) {
		BlockPos bp = new BlockPos(x,0,z);
		int y = world.getChunkFromBlockCoords(bp).getTopFilledSegment() + 16;
		bp = bp.add(0, y, 0);
		
		Block block;
		IBlockState ibs;
		do {
			if (--y < 0) break;
			bp = bp.add(0, -1, 0);
			ibs = world.getBlockState(bp);
			block = ibs.getBlock();
		} while (block.isAir(ibs, world, bp));
		
		return y;
	}

	public static RayTraceResult getCurrentMovingObjectPosition(EntityPlayer player, double distance, boolean fluid) {
		Vec3d posVec = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
		Vec3d lookVec = player.getLook(1);
		lookVec = posVec.addVector(lookVec.xCoord * distance, lookVec.yCoord * distance, lookVec.zCoord * distance);
		return player.worldObj.rayTraceBlocks(posVec, lookVec, fluid);
	}

	public static RayTraceResult getCurrentMovingObjectPosition(EntityPlayer player, double distance) {
		return getCurrentMovingObjectPosition(player, distance, false);
	}

	public static RayTraceResult getCurrentMovingObjectPosition(EntityPlayer player, boolean fluid) {
		return getCurrentMovingObjectPosition(player, player.capabilities.isCreativeMode ? 5.0F : 4.5F, fluid);
	}

	public static RayTraceResult getCurrentMovingObjectPosition(EntityPlayer player) {
		return getCurrentMovingObjectPosition(player, player.capabilities.isCreativeMode ? 5.0F : 4.5F, false);
	}

	public static EnumFacing getCurrentMousedOverSide(EntityPlayer player) {
		RayTraceResult mouseOver = getCurrentMovingObjectPosition(player);
		return mouseOver == null ? EnumFacing.DOWN : mouseOver.sideHit; //TODO 0 -> DOWN ? 
	}

	public static int determineXZPlaceFacing(EntityLivingBase living) {
		int quadrant = MathHelper.floor(living.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		switch (quadrant) {
		case 0:
			return 2;
		case 1:
			return 5;
		case 2:
			return 3;
		case 3:
			return 4;
		}
		return 3;
	}

	public static boolean isEqual(Block blockA, Block blockB) {

		if (blockA == blockB) {
			return true;
		}
		if (blockA == null | blockB == null) {
			return false;
		}
		return blockA.equals(blockB) || blockA.isAssociatedBlock(blockB);
	}

	/* UNSAFE Tile Entity Retrieval */
	// public static TileEntity getAdjacentTileEntityUnsafe(World world, int x, int y, int z, ForgeDirection dir) {
	//
	// if (world == null) {
	// return null;
	// }
	// Chunk chunk = world.getChunkFromBlockCoords(x + dir.offsetX, z + dir.offsetZ);
	// return chunk == null ? null : chunk.getChunkBlockTileEntityUnsafe((x + dir.offsetX) & 0xF, y + dir.offsetY, (z + dir.offsetZ) & 0xF);
	// }
	//
	// public static TileEntity getAdjacentTileEntityUnsafe(World world, int x, int y, int z, int side) {
	//
	// return world == null ? null : getAdjacentTileEntityUnsafe(world, x, y, z, ForgeDirection.values()[side]);
	// }
	//
	// public static TileEntity getAdjacentTileEntityUnsafe(TileEntity refTile, ForgeDirection dir) {
	//
	// return refTile == null ? null : getAdjacentTileEntityUnsafe(refTile.worldObj, refTile.xCoord, refTile.yCoord, refTile.zCoord, dir);
	// }
	//
	// public static TileEntity getAdjacentTileEntityUnsafe(TileEntity refTile, int side) {
	//
	// return refTile == null ? null : getAdjacentTileEntityUnsafe(refTile.worldObj, refTile.xCoord, refTile.yCoord, refTile.zCoord,
	// ForgeDirection.values()[side]);
	// }
	
	@Deprecated
	public static boolean blockExists(World world, int x, int y, int z){
		return blockExists(world, new BlockPos(x,y,z));
	}

	public static boolean blockExists(World world, BlockPos bp){
		return world.getBlockState(bp) == Blocks.AIR;
	}

	public static Block getAdjacentBlock(World world, int x, int y, int z, EnumFacing dir) {
		if(world == null) return Blocks.AIR;
		
		x += dir.getFrontOffsetX();
		y += dir.getFrontOffsetY();
		z += dir.getFrontOffsetZ();
		
		BlockPos bp = new BlockPos(x, y, z);
		IBlockState ibs = world.getBlockState(bp); //Can't return null -> No more checks necessary
		
		return ibs.getBlock();
	}

	@Deprecated
	public static Block getAdjacentBlock(World world, int x, int y, int z, int side) {
		return world == null ? Blocks.AIR : getAdjacentBlock(world, x, y, z, EnumFacing.VALUES[side]); //UNSAFE
	}

	/* Safe Tile Entity Retrieval */
	public static TileEntity getAdjacentTileEntity(World world, int x, int y, int z, EnumFacing dir) {
		return getAdjacentTileEntity(world, new BlockPos(x, y, z), dir);
	}
	
	/* Safe Tile Entity Retrieval */
	public static TileEntity getAdjacentTileEntity(World world, BlockPos bp, EnumFacing dir) {
		if(world == null) return null;
		bp = bp.offset(dir);
		return !blockExists(world,bp) ? null : world.getTileEntity(bp);
	}
	
	@Deprecated
	public static TileEntity getAdjacentTileEntity(World world, int x, int y, int z, int side) {
		return world == null ? null : getAdjacentTileEntity(world, x, y, z, EnumFacing.VALUES[side]); //UNSAFE
	}
	
	public static TileEntity getAdjacentTileEntity(TileEntity refTile, EnumFacing dir) {
		return refTile == null ? null : getAdjacentTileEntity(refTile.getWorld(), refTile.getPos(), dir);
	}
	
	@Deprecated
	public static TileEntity getAdjacentTileEntity(TileEntity refTile, int side) {
		return refTile == null ? null : getAdjacentTileEntity(refTile.getWorld(), refTile.getPos(), EnumFacing.VALUES[side]);
	}

	public static int determineAdjacentSide(TileEntity refTile, int x, int y, int z) {
		BlockPos bp = refTile.getPos();
		return y > bp.getY() ? 1 : y < bp.getY() ? 0 : z > bp.getZ() ? 3 : z < bp.getZ() ? 2 : x > bp.getX()? 5 : 4;
	}

	public static int[] getAdjacentCoordinatesForSide(RayTraceResult pos) {
		BlockPos bp = pos.getBlockPos();
		return getAdjacentCoordinatesForSide(bp.getX(), bp.getY(), bp.getZ(), pos.sideHit);
	}
	
	@Deprecated
	public static int[] getAdjacentCoordinatesForSide(int x, int y, int z, int side) {
		return new int[] { x + SIDE_COORD_MOD[side][0], y + SIDE_COORD_MOD[side][1], z + SIDE_COORD_MOD[side][2] };
	}
	
	public static int[] getAdjacentCoordinatesForSide(int x, int y, int z, EnumFacing ef) {
		return new int[] { x + ef.getFrontOffsetX(), y + ef.getFrontOffsetY(), z + ef.getFrontOffsetZ() };
	}

	public static AxisAlignedBB getAdjacentAABBForSide(RayTraceResult pos) {
		BlockPos bp = pos.getBlockPos();
		return getAdjacentAABBForSide(bp.getX(), bp.getY(), bp.getZ(), pos.sideHit);
	}
	
	@Deprecated
	public static AxisAlignedBB getAdjacentAABBForSide(int x, int y, int z, int side) {
		return new AxisAlignedBB(x + SIDE_COORD_MOD[side][0], y + SIDE_COORD_MOD[side][1], z + SIDE_COORD_MOD[side][2], x + SIDE_COORD_AABB[side][0], y + SIDE_COORD_AABB[side][1], z + SIDE_COORD_AABB[side][2]);
	}
	
	public static AxisAlignedBB getAdjacentAABBForSide(int x, int y, int z, EnumFacing ef) {
		return new AxisAlignedBB(x + ef.getFrontOffsetX(), y + ef.getFrontOffsetY(), z + ef.getFrontOffsetZ(), x + ef.getFrontOffsetX(), y + ef.getFrontOffsetY(), z + ef.getFrontOffsetZ());
	}

	public static int getLeftSide(int side) {

		return SIDE_LEFT[side];
	}

	public static int getRightSide(int side) {

		return SIDE_RIGHT[side];
	}

	public static int getOppositeSide(int side) {

		return SIDE_OPPOSITE[side];
	}

	public static int getAboveSide(int side) {

		return SIDE_ABOVE[side];
	}

	public static int getBelowSide(int side) {

		return SIDE_BELOW[side];
	}

	/* BLOCK ROTATION */
	public static boolean canRotate(Block block) {
		int bId = Block.getIdFromBlock(block);
		return bId < MAX_ID ? rotateType[Block.getIdFromBlock(block)] != 0 : false;
	}
	
	public static int getBlockMetadata(World world, BlockPos bp){
		IBlockState ibs = world.getBlockState(bp);
		return ibs.getBlock().getMetaFromState(ibs);
	}

	@SuppressWarnings("deprecation")
	public static int rotateVanillaBlock(World world, Block block, int x, int y, int z) {
		BlockPos bp = new BlockPos(x, y, z);
		IBlockState ibs = world.getBlockState(bp);
		
		int bId = Block.getIdFromBlock(block), bMeta = ibs.getBlock().getMetaFromState(ibs);
		switch (rotateType[bId]) {
		case RotationType.FOUR_WAY:
			return SIDE_LEFT[bMeta];
		case RotationType.SIX_WAY:
			if (bMeta < 6) {
				return ++bMeta % 6;
			}
			return bMeta;
		case RotationType.RAIL:
			if (bMeta < 2) {
				return ++bMeta % 2;
			}
			return bMeta;
		case RotationType.PUMPKIN:
			return ++bMeta % 4;
		case RotationType.STAIRS:
			return ++bMeta % 8;
		case RotationType.REDSTONE:
			int upper = bMeta & 0xC;
			int lower = bMeta & 0x3;
			return upper + ++lower % 4;
		case RotationType.LOG:
			return (bMeta + 4) % 12;
		case RotationType.SLAB:
			return (bMeta + 8) % 16;
		case RotationType.CHEST:
			for (int i = 2; i < 6; i++) {
				if (isEqual(ibs.getBlock(), block)) {
					world.setBlockState(bp, block.getStateFromMeta(SIDE_OPPOSITE[bMeta]));
					return SIDE_OPPOSITE[bMeta];
				}
			}
			return SIDE_LEFT[bMeta];
		case RotationType.LEVER:
			int shift = 0;
			if (bMeta > 7) {
				bMeta -= 8;
				shift = 8;
			}
			if (bMeta == 5) {
				return 6 + shift;
			} else if (bMeta == 6) {
				return 5 + shift;
			} else if (bMeta == 7) {
				return 0 + shift;
			} else if (bMeta == 0) {
				return 7 + shift;
			}
			return bMeta + shift;
		case RotationType.SIGN:
			return ++bMeta % 16;
		case RotationType.PREVENT:
		default:
			return bMeta;
		}
	}

	@SuppressWarnings("deprecation")
	public static int rotateVanillaBlockAlt(World world, Block block, int x, int y, int z) {
		BlockPos bp = new BlockPos(x, y, z);
		IBlockState ibs = world.getBlockState(bp);
		
		int bId = Block.getIdFromBlock(block), bMeta = ibs.getBlock().getMetaFromState(ibs);
		
		switch (rotateType[bId]) {
		case RotationType.FOUR_WAY:
			return SIDE_RIGHT[bMeta];
		case RotationType.SIX_WAY:
			if (bMeta < 6) {
				return (bMeta + 5) % 6;
			}
			return bMeta;
		case RotationType.RAIL:
			if (bMeta < 2) {
				return ++bMeta % 2;
			}
			return bMeta;
		case RotationType.PUMPKIN:
			return (bMeta + 3) % 4;
		case RotationType.STAIRS:
			return (bMeta + 7) % 8;
		case RotationType.REDSTONE:
			int upper = bMeta & 0xC;
			int lower = bMeta & 0x3;
			return upper + (lower + 3) % 4;
		case RotationType.LOG:
			return (bMeta + 8) % 12;
		case RotationType.SLAB:
			return (bMeta + 8) % 16;
		case RotationType.CHEST:
			for (int i = 2; i < 6; i++) {
				if (isEqual(ibs.getBlock(), block)) {
					ibs.getBlock().getStateFromMeta(SIDE_OPPOSITE[bMeta]);
					world.setBlockState(bp, ibs);
					return SIDE_OPPOSITE[bMeta];
				}
			}
			return SIDE_RIGHT[bMeta];
		case RotationType.LEVER:
			int shift = 0;
			if (bMeta > 7) {
				bMeta -= 8;
				shift = 8;
			}
			if (bMeta == 5) {
				return 6 + shift;
			} else if (bMeta == 6) {
				return 5 + shift;
			} else if (bMeta == 7) {
				return 0 + shift;
			} else if (bMeta == 0) {
				return 7 + shift;
			}
		case RotationType.SIGN:
			return ++bMeta % 16;
		case RotationType.PREVENT:
		default:
			return bMeta;
		}
	}

	public static List<ItemStack> breakBlock(World worldObj, int x, int y, int z, Block block, int fortune, boolean doBreak, boolean silkTouch) {
		return breakBlock(worldObj, null, x, y, z, block, fortune, doBreak, silkTouch);
	}
	
	public static List<ItemStack> breakBlock(World worldObj, EntityPlayer player, int x, int y, int z, Block block, int fortune, boolean doBreak, boolean silkTouch) {
		return breakBlock(worldObj, player, new BlockPos(x,y,z), block, fortune, doBreak, silkTouch);
	}

	@SuppressWarnings("deprecation")
	public static List<ItemStack> breakBlock(World worldObj, EntityPlayer player, BlockPos bp, Block block, int fortune, boolean doBreak, boolean silkTouch) {
		IBlockState ibs = worldObj.getBlockState(bp);
		if (block.getBlockHardness(ibs, worldObj, bp) == -1) {
			return new LinkedList<ItemStack>();
		}
		
		int meta = ibs.getBlock().getMetaFromState(ibs);
		
		List<ItemStack> stacks = null;
		if (silkTouch && block.canSilkHarvest(worldObj, bp, ibs, player)) {
			stacks = new LinkedList<ItemStack>();
			stacks.add(createStackedBlock(block, meta));
		} else {
			stacks = block.getDrops(worldObj, bp, ibs, fortune);
		}
		if (!doBreak) {
			return stacks;
		}
		worldObj.playSound(player, bp, new SoundEvent(new ResourceLocation("block.stone.break")), SoundCategory.BLOCKS, 1.0f, 1.0f); //TODO SoundFix
		//worldObj.playAuxSFXAtEntity(player, 2001, bp, Block.getIdFromBlock(block) + (meta << 12)); OLD
		worldObj.setBlockToAir(bp);
		
		List<EntityItem> result = worldObj.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(bp.getX() - 2, bp.getY() - 2, bp.getZ() - 2, bp.getX() + 3, bp.getY() + 3, bp.getZ() + 3));
		for (int i = 0; i < result.size(); i++) {
			EntityItem entity = result.get(i);
			if (entity.isDead || entity.getEntityItem().stackSize <= 0) {
				continue;
			}
			stacks.add(entity.getEntityItem());
			entity.worldObj.removeEntity(entity);
		}
		return stacks;
	}

	public static ItemStack createStackedBlock(Block block, int bMeta) {
		Item item = Item.getItemFromBlock(block);
		if (item.getHasSubtypes()) return new ItemStack(item, 1, bMeta);
		return new ItemStack(item, 1, 0);
	}

}
