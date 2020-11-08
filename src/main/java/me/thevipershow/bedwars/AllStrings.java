package me.thevipershow.bedwars;

import org.bukkit.ChatColor;

public enum AllStrings {

    PERMISSION_ADMIN_USE("bedwars.admin.use"),
    PERMISSION_ADMIN_REMOVE("bedwars.admin.remove"),
    PERMISSION_USER_QUIT("bedwars.users.quit"),
    PERMISSION_USER_JOIN("bedwars.users.join"),
    PERMISSION_USER_EXP("bedwars.users.exp"),
    PERMISSION_ADMIN_EXP_VIEW("bedwars.admin.exp.view"),
    PERMISSION_ADMIN_EXP_GIVE("bedwars.admin.exp.give"),

    SUBCOMMAND_ILLEGAL_EXECUTOR("You cannot execute this command from here."),
    SUBCOMMAND_MISSING_PERM("You do not have enough permissions."),
    SUBCOMMAND_WRONG_ARGS("Wrong number of args: "),
    ATTEMPT_LOAD("attempt-load"),

    INVALID_GAMEMODE("Invalid gamemode named: \""),
    NOT_IN_QUEUE("You are not in any queue."),
    LEFT_QUEUE("You left the queue."),
    NO_GAME_FOUND_FOR_GAMEMODE("No game could be found for %s gamemode."),
    GAME_ALREADY_JOINED("You have already joined a game!"),
    NO_EXPERIENCE_POINTS("§7You still don't have any experience points."),
    NO_EXPERIENCE_POINTS_HE("§7He still doesn't have any experience points."),

    YOUR_CURRENT_EXP("\n    §7Your current experience: §6§l"),
    YOUR_CURRENT_LEVEL("    §7Your current bedwars level: §6§l"),
    YOU_OWN("    §7You own §6§l%d§8\\§6§l%d EXP §r§7required to reach §3§lLevel %d"),
    HIS_CURRENT_EXP("\n    §7%s's current experience: §6§l"),
    HIS_CURRENT_LEVEL("    §7%s's current bedwars level: §6§l"),
    HE_OWNS("    §7%s owns §6§l%d§8\\§6§l%d EXP §r§7required to reach §3§lLevel %d"),

    VIEW("view"),
    ADD("add"),
    EXP_ADDED_MSG("§7If that player was present in the database, his exp increased by §6§l"),
    LAST_ARG_NOT_NUMBER("§7The last argument is not a number."),
    UNKNOWN_ARG("Unknown command argument \""),

    SET("set"),
    REMOVE("remove"),
    JOIN("join"),
    QUIT("quit"),
    EXP("exp"),

    SETUP_QUEUE_VILLAGER("§eYou successfully setup a %s bedwars queue villager"),
    REMOVED_VILLAGER_FROM_DATABASE("§eYou successfully removed that villager from database."),
    VILLAGER_NOT_PRESENT_IN_DATABASE("§eThat villager was not associated with a database."),
    NOT_LOOKING_AT_VILLAGER("§eYou were not looking at villagers."),

    YML_PREFIX(".yml"),
    SOLO("solo"),
    DUO("duo"),
    QUAD("quad"),

    DB_USER("settings.database.username"),
    DB_PASSWORD("settings.database.password"),
    DB_ADDRESS("settings.database.address"),
    DB_PORT("settings.database.port"),
    DB_NAME("settings.database.db-name"),
    LOBBY_NAME("settings.lobby.world-name"),

    COULD_NOT_SAVE_CONFIG("Could not save config to"),
    FILE_NULL("File cannot be null."),
    CANNOT_LOAD("Cannot load "),

    LOADING_YML("&3Loading YAML config file for &a"),

    LEVEL("level"),
    MATERIAL("material"),
    ITEM_NAME("item-name"),
    BUY_WITH("buy-with"),
    PRICE("price"),
    LORE("lore"),
    ENCHANTS("enchantments"),
    SLOT("slot"),
    AMOUNT("amount"),
    LEVELS("levels"),
    TEAM("team"),
    X("x"), Y("y"), Z("z"),
    TYPE("type"),
    LOCATION("location"),
    DROP_AMOUNT("drop-amount"),
    DROP_DELAY("drop-delay"),
    INVISIBLE("invisible"),

    MAP_FILENAME("map-filename"),
    MIN_GAMES("minimum-games"),
    MAX_GAMES("maximum-games"),
    PLAYERS("players"),
    DEATHMATCH_START("deathmatch-start"),
    MIN_PLAYERS("min-players"),
    START_TIMER("start-timer"),
    TNT_FUSE("tnt-fuse"),
    TEAMS("teams"),
    MAP_LOBBY_SPAWN("map-lobby-spawn"),
    MAP_SPAWNS("map-spawns"),
    SPAWNERS("spawners"),
    MERCHANTS("merchants"),
    BEDS_POS("beds-locations"),
    SHOP("shop"),
    UPGRADES("upgrades"),
    SPAWN_PROTECTION("spawn-protection"),
    TITLE("title"),
    SLOTS("slots"),
    ITEMS("items"),
    GLASS("glass"),
    COLOR("color"),
    UPGRADABLE_ITEMS("upgradable-items"),
    POTIONS("potions"),
    LENGTH("length"),
    NAME("name"),
    SHOP_CATEGORY("shop-category"),

    BLOCKS_CATEGORY("§bBlocks"),
    MELEE_CATEGORY("§bMelee"),
    ARMOR_CATEGORY("§bArmor"),
    TOOLS_CATEGORY("§bTools"),
    RANGED_CATEGORY("§bRanged"),
    POTIONS_CATEGORY("§bPotions"),
    UTILITY_CATEGORY("§bUtility"),
    QUICK_BUY("§bQuick Buy"),

    SHARPNESS("sharpness"),
    REINFORCED_ARMOR("reinforced-armor"),
    MANIAC_MINER("maniac-miner"),
    IRON_FORGE("iron-forge"),
    HEAL_POOL("heal-pool"),
    DRAGON_BUFF("dragon-buff"),
    TRAPS("traps"),
    ALARM("alarm"),
    BLINDNESS_POISON("blindness-poison"),
    COUNTER_OFFENSIVE("counter-offensive"),
    MINER_FATIGUE("miner-fatigue"),

    HEAL_RADIUS("heal-radius"),
    HEAL_FREQUENCY("heal-frequency"),
    HEAL_AMOUNT("heal-amount"),

    DRAGONS_RELEASED("§6The Ender Dragons have been released!"),

    DAILY_FIRST_GAME_MESSAGE("§7-----------------------------------\n" +
            "            §a★ §6DAILY QUEST COMPLETED! §a★\n" +
            "         §eYou have won your first game of the day§8!\n" +
            "         §e          +§6§l250 EXP\n" +
            "§7-----------------------------------"),

    DAILY_GAMES_PLAYER_MESSAGE("§7-----------------------------------\n" +
            "            §a★ §6DAILY QUEST COMPLETED! §a★\n" +
            "         §eYou have played two daily games§8!\n" +
            "         §e          +§6§l250 EXP\n" +
            "§7-----------------------------------"),

    WEEKLY_BROKEN_BEDS_MESSAGE("§7-----------------------------------\n" +
            "            §a★ §6WEEKLY QUEST COMPLETED! §a★\n" +
            "         §eYou have broken 25 beds§8!\n" +
            "         §e          +§6§l5000 EXP\n" +
            "§7-----------------------------------"),

    TOP_3_KILL(" §7Top 3 kill scores:"),
    TOP_3_FINAL_KILL(" §7Top 3 final kill scores:"),
    GAME_STARTING("§aGame starting in §e"),

    SPAWNER_NAME("§f§lLevel§r§7: §r§f§e%s§7|§f Drop in: §e§l%d§fs"),
    TEAMING_PROHIBITED("§c§lRemember that TEAMING between different teams is strictly PROHIBITED!"),

    BEDWARS_SHOP_TITLE("§7[§eBedwars§7] §eShop"),
    BEDWARS_TRAPS_TITLE("§7[§eBedwars§7] Traps"),
    BEDWARS_UPGRADE_TITLE("§7[§eBedwars§7] §eUpgrades"),

    TEAM_SCOREBOARD("✦ §7Team §"),
    SERVER_BRAND(" cloudcombat.com"),
    YOUR_BED_BROKEN("§c§lYour bed has been broken!"),
    BED_BROKEN_BY(" §7team's bed has been broken by §"),
    JOINED_QUEUE(" §ehas joined §7§l"),
    QUEUE_STATUS("§eStatus §7[§a%d§8/§a%d§7]"),

    TELEPORT_ERROR("Something went wrong when teleporting you to waiting room."),
    YOU_WON_GAME("§a§lYou have won this game!"),
    CONGRATULATIONS("§aCongratulations."),
    TEAM_WON("§7Team §"),
    HAS_WON_THE_GAME(" §cYou have lost the game!"),
    RETURNING_LOBBY("§7Returning to lobby in 15s"),

    DIAMOND_UPGRADE("The §b§lDIAMOND §7spawners have been upgraded to lvl. §e"),
    EMERALD_UPGRADE("The §a§lEMERALD §7spawners have been upgraded to lvl. §e"),
    DESTROY_MAP_ERROR("Something went wrong while destroying map of "),

    GRAPHIC_CROSS("§l§c✘"),
    GRAPHIC_TICK("§l§a✓"),

    STARTING_IN("§eStarting in §7§l[§r"),
    STARTING_END("§7§l] §6"),
    SECONDS(" §eseconds"),
    MISSING("§7Missing §e"),

    GENERATE_PRICE_LORE("§7- Price§f: §e§l"),
    GENERATE_BUY_LORE("§7- Buy with§f: §e§l"),

    YOUR("§eYour §a§l"),
    TRAP_ACTIVATED(" §r§etrap has been activated!"),
    WE_COULD_NOT_FIND_GAME("§eWe could not find a game, try clicking again."),

    ALREADY_BOUGHT_MAX_LVL("You already have bought maximum level"),
    CANNOT_AFFORD_UPGRADE("You cannot afford this upgrade!"),
    SUCCESSFULLY_UPGRADED("You successfully upgraded: §e"),
    CANNOT_AFFORD_TRAP("You cannot afford this trap!"),
    SUCCESSFULLY_ENABLED_TRAP("You successfully enabled trap: §e"),

    DRAGON_BUFF_DISPLAY("Dragon Buff"),
    HEAL_POOL_DISPLAY("Heal Pool"),
    IRON_FORGE_DISPLAY("Iron Forge"),
    MANIAC_MINER_DISPLAY("Maniac Miner"),
    REINFORCED_ARMOR_DISPLAY("Reinforced Armor"),
    SHARPENED_SWORDS_DISPLAY("Sharpened Swords"),
    ALARM_TRAP_DISPLAY("Alarm Trap"),
    BP_TRAP_DISPLAY("Blindness-Poison Trap"),
    CO_TRAP_DISPLAY("Counter-Offensive Trap"),
    MF_TRAP_DISPLAY("Miner-Fatigue Trap"),

    MAX_TRAPS_LIMIT("You have reached maximum traps limit."),
    SWORD("SWORD"),
    ALREADY_SWORD_LEVEL("§7You have already picked this sword level."),
    YOU_DID_NOT_HAVE_ENOUGH("§7You did not have enough "),
    YOU_ALREADY_HAVE_HIGHEST_UPGRADE_AVAILABLE("§7You already have the highest upgrade available."),
    SUCCESSFULLY_UPGRADED_TO_LVL("§7You successfully upgraded this item to §eLvl. "),
    HAS_LEFT_GAME(" §ehas left this game."),

    RETURN_LOBBY("§c§lReturn to lobby"),

    COMPASS_LORE_1("§7- You can use this compass to return to the server's lobby"),
    COMPASS_LORE_2("§7  You can simply click on it and you will be automatically teleported."),
    COMPASS_LORE_3("§7  Remember that you can't join this game once you've left."),

    KILL_1("§%c%s §7was slashed by §%c%s."),
    KILL_2("§%c%s's §7TNT killed §%c%s."),
    KILL_3("§%c%s's §7arrow perforated §%c%s."),
    KILL_4("§%c%s §7was killed by §%c%s."),
    FINAL_KILL_1("§%c%s §7was slashed by §%c%s. §c§lFINAL KILL!"),
    FINAL_KILL_2("§%c%s's §7TNT killed §%c%s. §c§lFINAL KILL!"),
    FINAL_KILL_3("§%c%s's §7arrow perforated §%c%s. §c§lFINAL KILL!"),
    FINAL_KILL_4("§%c%s §7was killed by §%c%s. §c§lFINAL KILL!"),

    YOU_HAVE_BEEN_ELIMINATED("§cYou have been eliminated."),

    KILL_5("believed the ground was soft."),
    KILL_6("entered Dante's inferno."),
    KILL_7("got perfectly grilled."),
    KILL_8("proved to be flammable."),
    KILL_9("§o\"intentionally\"§r§7 voided."),
    KILL_10("forgot he wasn't a fish."),
    KILL_11("exploded into pieces"),
    KILL_12("forgot how to breathe."),
    KILL_13("got hit by a projectile."),
    KILL_14("CUSTOM DAMAGE???"),
    KILL_15("died in a mysterious way."),
    FINAL_KILL_EXT("§c§l FINAL KILL!"),

    LEVEL_UP("§7--------------------------------\n" +
            "            §a♫ §6LEVEL UP! §a♫\n" +
            "         §eYou are now §3Level %d\n" +
            "§7--------------------------------"),

    GG("gg"),

    CANNOT_BREAK_OWN_BED("§eYou cannot destroy your own bed."),

    DEATHMATCH_BEDS_CANNOT_BE_BROKEN("§6Beds cannot be broken during §6§lDEATHMATCH"),
    DEATHMATCH_BEDS_CANNOT_BE_BROKEN_2("§6You must kill all of your enemies to win!"),

    PREFIX("§7[§eCloudCombat§7]: "),
    DRIVER_PATH("com.mysql.jdbc.Driver"),
    SUCCESSFULLY_ADDED_PAPI_EXPANSION("&eSuccessfully added expansion to &aPlaceHolderAPI"),
    PAPI_PLUGIN("PlaceholderAPI"),
    MAIN_COMMAND("bedwars"),

    // MySQL Section below:
    CREATE_TABLE_STATEMENT("CREATE TABLE IF NOT EXISTS %s (%s);"),
    ATTEMPTING_CONNECTION("&eAttempting MySQL Connection to address ->&a"),
    JDBC_MYSQL_URL("jdbc:mysql://%s:%s/%s"),
    REMOVE_CLEAN_TASK("&eRemoving clean task: &e"),


    // World loading section below:
    COULD_NOT_FIND_WORLD_FOLDER("&cCould not find a world folder named &f[&e%s&f]"),
    ATTEMPT_COPY("&3Attempting to copy directory of Bukkit World &f[&e%s&f]."),
    ATTEMPT_CREATE("&3Attempting to create instance of Bukkit World &f[&e%s&f]."),
    LOADING_ACTIVE_GAME("&3Loading &f[&e%s&f] &3into active games..."),
    SUCCESSFULLY_CREATED_ACTIVE_GAME("&3Successfully created a world with name &f[&e%s&f]."),
    ERROR_CREATE_ACTIVE_GAME("&cCould not create ActiveGame for &f[&e%s&f]"),
    ADDED_NEW_ACTIVE_GAME("&3Added new ActiveGame &f[&e"),
    SOMETHING_WENT_WRONG_DURING_CREATION("&cSomething went wrong when creating world &f[&e%s&f]."),

    ANNOUNCE_DEATHMATCH("§6§lDEATHMATCH §r§6Mode has started§7...\n        §6Kill all of your enemies to win!"),
    ;

    private final String s;

    AllStrings(final String s) {
        this.s = s;
    }

    public String get() {
        return ChatColor.translateAlternateColorCodes('§', this.s);
        //     return EncryptUtils.decrypt(this.s, EncryptUtils.alphabet.toString(), 27, 3, EncryptUtils.Direction.BACKWARDS, EncryptUtils.Direction.BACKWARDS);
    }
}
