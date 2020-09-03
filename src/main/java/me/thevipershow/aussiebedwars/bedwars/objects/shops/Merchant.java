package me.thevipershow.aussiebedwars.bedwars.objects.shops;

import java.util.Map;
import me.thevipershow.aussiebedwars.bedwars.objects.SpawnPosition;
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
        return null;
    }

    public static Merchant deserialize(Map<String, Object> objectMap) {
        return null; //TODO: Finish
    }
}
