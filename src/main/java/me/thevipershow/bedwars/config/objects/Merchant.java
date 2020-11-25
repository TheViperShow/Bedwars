package me.thevipershow.bedwars.config.objects;

import java.util.Map;
import static me.thevipershow.bedwars.AllStrings.LOCATION;
import static me.thevipershow.bedwars.AllStrings.NAME;
import static me.thevipershow.bedwars.AllStrings.TYPE;
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

    public final EntityType getMerchantEntity() {
        return merchantEntity;
    }

    public final String getMerchantName() {
        return merchantName;
    }

    public final SpawnPosition getMerchantPosition() {
        return merchantPosition;
    }

    public final MerchantType getMerchantType() {
        return merchantType;
    }

    @Override
    public final Map<String, Object> serialize() {
        throw new UnsupportedOperationException();
    }

    public static Merchant deserialize(Map<String, Object> objectMap) {
        String name = (String) objectMap.get(NAME.get());
        String type = (String) objectMap.get(TYPE.get());
        MerchantType merchantType = MerchantType.valueOf(type);
        SpawnPosition spawnPosition = SpawnPosition.deserialize((Map<String, Object>) objectMap.get(LOCATION.get()));
        return new Merchant(EntityType.VILLAGER, name, spawnPosition, merchantType);
    }
}
