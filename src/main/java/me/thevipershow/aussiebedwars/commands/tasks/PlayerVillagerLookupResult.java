package me.thevipershow.aussiebedwars.commands.tasks;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

public final class PlayerVillagerLookupResult extends PlayerEntityLookupResult<Villager> {
    public PlayerVillagerLookupResult(Player interested) {
        super(interested);
    }

    @Override
    public List<Villager> filteredNearbyEntities(final double x, final double y, final double z) {
        return getInterested().getNearbyEntities(x, y, z)
                .stream()
                .filter(entity -> (entity instanceof Villager))
                .map(entity -> (Villager) entity)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Villager> getLookupResult() {
        List<Villager> filteredNearbyEntities = filteredNearbyEntities(10.d, 10.d, 10.d);

        List<Block> sight = getInterested().getLineOfSight((Set<Material>) null, 10); //Get the blocks in the player's line of sight (the Set is null to not ignore any blocks)
        for (Block block : sight) { //For each block in the list
            if (block.getType() != Material.AIR) break; //If the block is not air -> obstruction reached, exit loop.
            Location low = block.getLocation(); //Lower corner of the block
            Location high = low.clone().add(1.d, 1.d, 1.d); //Higher corner of the block
            AxisAlignedBB blockBoundingBox = AxisAlignedBB.a(low.getX(), low.getY(), low.getZ(), high.getX(), high.getY(), high.getZ()); //The bounding or collision box of the block
            for (Villager entity : filteredNearbyEntities) { //For every living entity in the player's range
                //If the entity is truly close enough and the bounding box of the block (1x1x1 box) intersects with the entity's bounding box, return it
                if (entity.getLocation().distance(getInterested().getEyeLocation()) <= 10 && ((CraftEntity) entity).getHandle().getBoundingBox().b(blockBoundingBox)) {
                    return Optional.of(entity);
                }
            }
        }
        return Optional.empty();
    }
}
