package cofh.lib.util.position;

import cofh.lib.BlockFacing;

public interface IRotateableTile {

	public boolean canRotate();

	public boolean canRotate(BlockFacing axis);

	public void rotate(BlockFacing axis);

	public void rotateDirectlyTo(int facing);

	public BlockFacing getDirectionFacing();

}
