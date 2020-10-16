package me.thevipershow.bedwars.config.objects;

import java.util.HashMap;
import java.util.Map;
import me.thevipershow.bedwars.bedwars.objects.shops.MerchantType;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.EntityType;

@SerializableAs("Merchant")
public class Merchant implements ConfigurationSerializable {

    private final MerchantType merchantType;
    private final EntityType merchantEntity;
    private final String merchantName;
    private final SpawnPosition merchantPosition;

    public Merchant(EntityType merchantEntity,
                    String merchantName,
                    SpawnPosition merchantPosition,
                    MerchantType merchantType) {
        this.merchantEntity = merchantEntity;
        this.merchantName = merchantName;
        this.merchantPosition = merchantPosition;
        this.merchantType = merchantType;
    }

    public EntityType getMerchantEntity() {
        return merchantEntity;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public SpawnPosition getMerchantPosition() {
        return merchantPosition;
    }

    public MerchantType getMerchantType() {
        return merchantType;
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new HashMap<>();
        map.put("name", merchantName);
        map.put("type", merchantType.name());
        map.put("location.x", merchantPosition.getX());
        map.put("location.y", merchantPosition.getY());
        map.put("location.z", merchantPosition.getZ());
        return map;
    }

    public static Merchant deserialize(Map<String, Object> objectMap) {
        String name = (String) objectMap.get("name");
        String type = (String) objectMap.get("type");
        MerchantType merchantType = MerchantType.valueOf(type);
        SpawnPosition spawnPosition = SpawnPosition.deserialize((Map<String, Object>) objectMap.get("location"));
        return new Merchant(EntityType.VILLAGER, name, spawnPosition, merchantType);
    }
}
