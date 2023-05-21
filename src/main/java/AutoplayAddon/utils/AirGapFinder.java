package AutoplayAddon.utils;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import static meteordevelopment.meteorclient.MeteorClient.mc;
import java.util.ArrayList;
import java.util.List;

public class AirGapFinder {



    public static List<Vec3d> findClosestValidPos(List<Block> targetBlocks, double searchRadius, double maxAirGapDistance) {
            World world = mc.player.getEntityWorld();
            BlockPos playerPos = mc.player.getBlockPos();
            for (int dy = (int) -searchRadius; dy <= searchRadius; dy++) {
                for (int r = 0; r <= searchRadius + 4; r++) {
                    for (int dx = -r; dx <= r; dx++) {
                        for (int dz = -r; dz <= r; dz++) {
                            if(Math.abs(dx) != r && Math.abs(dz) != r) continue;
                            BlockPos currentPos = playerPos.add(dx, dy, dz);
                            Block currentBlock = world.getBlockState(currentPos).getBlock();
                            if (targetBlocks.contains(currentBlock)) {
                                if (PlayerUtils.distanceTo(currentPos) <= searchRadius) {
                                    Vec3d airGapPos = findAirGapNearBlock(currentPos, maxAirGapDistance);
                                    if (airGapPos != null) {
                                        List<Vec3d> result = new ArrayList<>();
                                        result.add(currentPos.toCenterPos());
                                        result.add(airGapPos);
                                        return result;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }

    public static Vec3d findAirGapNearBlock(BlockPos targetPos, double maxAirGapDistance) {
        World world = mc.player.getEntityWorld();
        BlockPos closestValidPos = null;
        double minDistance = Double.MAX_VALUE;

        for (BlockPos pos : BlockPos.iterate(targetPos.add((int) -maxAirGapDistance, (int) -maxAirGapDistance, (int) -maxAirGapDistance),
            targetPos.add((int) maxAirGapDistance, (int) maxAirGapDistance, (int) maxAirGapDistance))) {
            if (world.isAir(pos) && world.isAir(pos.up())) {
                double distance = distanceBetweenBlockPos(targetPos, pos);
                if (distance <= maxAirGapDistance && distance < minDistance) {
                    closestValidPos = pos.toImmutable();
                    minDistance = distance;
                }
            }
        }

        if (closestValidPos == null) {
            return null;
        }

        double offsetX = closestValidPos.getX() < targetPos.getX() ? 0.7 : (closestValidPos.getX() > targetPos.getX() ? 0.3 : 0.5);
        double offsetZ = closestValidPos.getZ() < targetPos.getZ() ? 0.7 : (closestValidPos.getZ() > targetPos.getZ() ? 0.3 : 0.5);

        return new Vec3d(closestValidPos.getX() + offsetX, closestValidPos.getY(), closestValidPos.getZ() + offsetZ);
    }

    private static double distanceBetweenBlockPos(BlockPos pos1, BlockPos pos2) {
        double dx = pos1.getX() - pos2.getX();
        double dy = pos1.getY() - pos2.getY();
        double dz = pos1.getZ() - pos2.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
