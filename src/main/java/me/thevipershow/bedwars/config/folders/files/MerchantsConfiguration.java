package me.thevipershow.bedwars.config.folders.files;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static me.thevipershow.bedwars.AllStrings.MERCHANTS;
import me.thevipershow.bedwars.config.folders.ConfigFiles;
import me.thevipershow.bedwars.config.objects.Merchant;

public final class MerchantsConfiguration extends AbstractFileConfig {

    private final List<Merchant> merchantsList;

    public MerchantsConfiguration(File file) {
        super(file, ConfigFiles.MERCHANTS_FILE);
        final List<Map<String, Object>> merchantsSection = (List<Map<String, Object>>) getConfiguration().get(MERCHANTS.get());
        merchantsList = merchantsSection.stream().map(Merchant::deserialize).collect(Collectors.toList());
    }

    public List<Merchant> getMerchantsList() {
        return merchantsList;
    }
}
