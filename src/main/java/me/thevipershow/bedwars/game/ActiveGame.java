package me.thevipershow.bedwars.game;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import me.thevipershow.bedwars.AllStrings;
import me.thevipershow.bedwars.Bedwars;
import me.thevipershow.bedwars.bedwars.Gamemode;
import me.thevipershow.bedwars.bedwars.objects.BedwarsTeam;
import me.thevipershow.bedwars.bedwars.objects.shops.MerchantType;
import me.thevipershow.bedwars.bedwars.objects.spawners.SpawnerType;
import me.thevipershow.bedwars.config.objects.BedwarsGame;
import me.thevipershow.bedwars.config.objects.Merchant;
import me.thevipershow.bedwars.config.objects.PotionItem;
import me.thevipershow.bedwars.config.objects.Shop;
import me.thevipershow.bedwars.config.objects.ShopItem;
import me.thevipershow.bedwars.config.objects.SpawnPosition;
import me.thevipershow.bedwars.config.objects.TeamSpawnPosition;
import me.thevipershow.bedwars.config.objects.UpgradeItem;
import me.thevipershow.bedwars.config.objects.UpgradeLevel;
import me.thevipershow.bedwars.config.objects.upgradeshop.DragonBuffUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.HealPoolUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.IronForgeUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.ManiacMinerUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.ReinforcedArmorUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.SharpnessUpgrade;
import me.thevipershow.bedwars.config.objects.upgradeshop.TrapUpgrades;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeShop;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeShopItem;
import me.thevipershow.bedwars.config.objects.upgradeshop.UpgradeType;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.AlarmTrap;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.BlindnessAndPoisonTrap;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.CounterOffensiveTrap;
import me.thevipershow.bedwars.config.objects.upgradeshop.traps.MinerFatigueTrap;
import me.thevipershow.bedwars.game.shop.ShopCategory;
import me.thevipershow.bedwars.game.upgrades.ActiveHealPool;
import me.thevipershow.bedwars.game.upgrades.ActiveTrap;
import me.thevipershow.bedwars.listeners.UnregisterableListener;
import me.thevipershow.bedwars.listeners.game.*;
import me.thevipershow.bedwars.storage.sql.tables.GlobalStatsTableUtils;
import me.thevipershow.bedwars.worlds.WorldsManager;
import me.tigerhix.lib.scoreboard.common.EntryBuilder;
import me.tigerhix.lib.scoreboard.type.Entry;
import me.tigerhix.lib.scoreboard.type.Scoreboard;
import me.tigerhix.lib.scoreboard.type.ScoreboardHandler;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Bed;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitTask;

public abstract class ActiveGame {

    protected final String associatedWorldFilename;
    protected final BedwarsGame bedwarsGame;
    protected final World associatedWorld;
    protected final World lobbyWorld;
    protected final Location cachedLobbySpawnLocation;
    protected final Plugin plugin;
    protected final AbstractQueue<Player> associatedQueue;
    protected final Location cachedWaitingLocation;
    protected final Map<BedwarsTeam, List<Player>> assignedTeams;
    protected final Set<ActiveSpawner> activeSpawners;
    protected final Inventory defaultUpgradeInv;
    protected final Inventory defaultTrapsInv;
    protected final AbstractDeathmatch abstractDeathmatch;
    protected final ExperienceManager experienceManager;
    protected final QuestManager questManager;
    protected final GameTrapTriggerer gameTrapTriggerer;
    protected final KillTracker killTracker;
    protected final ShopInventories shopInventories;
    protected final EnderchestManager enderchestManager;
    protected ActiveSpawner diamondSampleSpawner = null;
    protected ActiveSpawner emeraldSampleSpawner = null;
    protected final SwordUpgrades swordUpgrades = new SwordUpgrades();
    protected final List<BedwarsTeam> destroyedTeams = new ArrayList<>();
    protected final List<Player> playersOutOfGame = new ArrayList<>();
    protected final List<Player> playersRespawning = new ArrayList<>();
    protected final List<AbstractActiveMerchant> activeMerchants = new ArrayList<>();
    protected final Set<Block> playerPlacedBlocks = new HashSet<>();
    protected final List<UnregisterableListener> unregisterableListeners = new ArrayList<>();
    protected final List<ActiveHealPool> healPools = new ArrayList<>();
    protected final List<BukkitTask> announcementsTasks = new ArrayList<>();
    protected final List<BukkitTask> emeraldBoostDrops = new ArrayList<>();
    protected final List<Player> immuneTrapPlayers = new ArrayList<>();
    protected final List<Player> hiddenPlayers = new ArrayList<>();
    protected final Map<Player, ArmorSet> playerSetMap = new HashMap<>();
    protected final Map<String, Integer> topKills = new HashMap<>();
    protected final Map<UUID, EnumMap<ShopCategory, Inventory>> playerShop = new HashMap<>();
    protected final Map<UUID, Inventory> associatedUpgradeGUI = new HashMap<>();
    protected final Map<UUID, Inventory> associatedTrapsGUI = new HashMap<>();
    protected final Map<UUID, UUID> placedTntMap = new HashMap<>();
    protected final Map<Player, Map<UpgradeItem, Integer>> playerUpgradeLevelsMap = new HashMap<>();
    protected final Map<Player, Scoreboard> activeScoreboards = new HashMap<>();
    protected final EnumMap<UpgradeType, Map<BedwarsTeam, Integer>> upgradesLevelsMap = new EnumMap<>(UpgradeType.class);
    protected final EnumMap<BedwarsTeam, LinkedList<ActiveTrap>> teamActiveTrapsList = new EnumMap<>(BedwarsTeam.class);
    protected final EnumMap<BedwarsTeam, Long> lastActivatedTraps = new EnumMap<>(BedwarsTeam.class);

    ///////////////////////////////////////////////////
    // Internal ActiveGame fields                    //
    //-----------------------------------------------//
    //                                               //
    protected boolean hasStarted = false;            //
    protected boolean winnerDeclared = false;        //
    protected BukkitTask timerTask = null;           //
    protected final GameLobbyTicker gameLobbyTicker; //
    protected long startTime;                        //
    //-----------------------------------------------//

    public ActiveGame(String associatedWorldFilename, BedwarsGame bedwarsGame, World lobbyWorld, Plugin plugin) {
        this.associatedWorldFilename = associatedWorldFilename;
        this.bedwarsGame = bedwarsGame;
        this.lobbyWorld = lobbyWorld;
        this.associatedWorld = Bukkit.getWorld(associatedWorldFilename);
        this.plugin = plugin;
        this.cachedLobbySpawnLocation = this.lobbyWorld.getSpawnLocation();
        this.associatedQueue = new MatchmakingQueue(bedwarsGame.getPlayers());
        this.cachedWaitingLocation = bedwarsGame.getLobbySpawn().toLocation(associatedWorld);
        this.assignedTeams = new HashMap<>();
        this.gameLobbyTicker = new GameLobbyTicker(this);
        this.activeSpawners = bedwarsGame.getSpawners()
                .stream()
                .map(spawner -> new ActiveSpawner(spawner, this))
                .collect(Collectors.toSet());
        registerMapListeners();
        gameLobbyTicker.startTicking();
        //    this.defaultShopInv = Objects.requireNonNull(setupShopGUIs());
        this.defaultUpgradeInv = Objects.requireNonNull(setupUpgradeGUIs());
        this.defaultTrapsInv = Objects.requireNonNull(setupTrapsGUIs());
        this.abstractDeathmatch = GameUtils.deathmatchFromGamemode(bedwarsGame.getGamemode(), this);
        this.experienceManager = new ExperienceManager(this);
        this.questManager = new QuestManager(this.experienceManager);
        this.gameTrapTriggerer = new GameTrapTriggerer(this);
        this.killTracker = new KillTracker(this);
        this.shopInventories = new ShopInventories(bedwarsGame.getShop());
        this.enderchestManager = new EnderchestManager(this);
    }

    protected final void setupUpgradeLevelsMap() {
        associatedQueue.perform(p -> {
            final Map<UpgradeItem, Integer> emptyMap = new HashMap<>();

            for (final UpgradeItem upgradeItem : bedwarsGame.getShop().getUpgradeItems()) {
                emptyMap.put(upgradeItem, -1);
            }
            this.playerUpgradeLevelsMap.put(p, emptyMap);
        });
    }

    protected final void announceNoTeaming() {
        associatedQueue.perform(p -> p.sendMessage(Bedwars.PREFIX + AllStrings.TEAMING_PROHIBITED.get()));
    }

    public void hidePlayer(final Player player) {
        final BedwarsTeam playerTeam = getPlayerTeam(player);
        for (Map.Entry<BedwarsTeam, List<Player>> entry : assignedTeams.entrySet()) {
            if (entry.getKey() != playerTeam) {
                for (final Player p : entry.getValue()) {
                    if (p.isOnline() && !isOutOfGame(p)) {
                        p.hidePlayer(player);
                    }
                }
            }
        }
        hiddenPlayers.add(player);
    }

    public void showPlayer(final Player player) {
   //     player.spigot().getHiddenPlayers()
        for (final List<Player> players : assignedTeams.values()) {
            for (final Player p : players) {
                if (p.isOnline() && !isOutOfGame(p)) {
                    p.showPlayer(player);
                }
            }
        }
        hiddenPlayers.remove(player);
    }

    public void showAll() {
        associatedWorld.getPlayers().forEach(this::showPlayer);
    }

    public final Inventory setupTrapsGUIs() {
        final Inventory trapsInv = Bukkit.createInventory(null, 0x24, AllStrings.BEDWARS_TRAPS_TITLE.get());
        final TrapUpgrades trapUpgrades = bedwarsGame.getUpgradeShop().getTrapUpgrades();
        final BlindnessAndPoisonTrap blindnessAndPoisonTrap = trapUpgrades.getBlindnessAndPoisonTrap();
        final CounterOffensiveTrap counterOffensiveTrap = trapUpgrades.getCounterOffensiveTrap();
        final AlarmTrap alarmTrap = trapUpgrades.getAlarmTrap();
        final MinerFatigueTrap minerFatigueTrap = trapUpgrades.getMinerFatigueTrap();

        final ShopItem blindnessAndPoisonItem = blindnessAndPoisonTrap.getShopItem();
        final ShopItem alarmTrapItem = alarmTrap.getShopItem();
        final ShopItem minerFatigueItem = minerFatigueTrap.getShopItem();
        final ShopItem counterOffensiveItem = counterOffensiveTrap.getShopItem();

        trapsInv.setItem(blindnessAndPoisonItem.getSlot(), blindnessAndPoisonItem.getCachedFancyStack());
        trapsInv.setItem(alarmTrapItem.getSlot(), alarmTrapItem.getCachedFancyStack());
        trapsInv.setItem(minerFatigueItem.getSlot(), minerFatigueItem.getCachedFancyStack());
        trapsInv.setItem(counterOffensiveItem.getSlot(), counterOffensiveItem.getCachedFancyStack());

        for (int i = 1; i <= 3; i++) {
            final ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, i);
            final ItemMeta glassMeta = glass.getItemMeta();
            glassMeta.setDisplayName("");
            glass.setItemMeta(glassMeta);
            trapsInv.setItem(29 + i, glass);
        }

        return trapsInv;
    }

    public final Inventory setupUpgradeGUIs() {
        final Inventory upgradeInv = Bukkit.createInventory(null, bedwarsGame.getUpgradeShop().getSlots(), AllStrings.BEDWARS_UPGRADE_TITLE.get());
        final UpgradeShop upgradeShop = bedwarsGame.getUpgradeShop();

        final DragonBuffUpgrade dragonBuffUpgrade = upgradeShop.getDragonBuffUpgrade();
        final HealPoolUpgrade healPoolUpgrade = upgradeShop.getHealPoolUpgrade();
        final IronForgeUpgrade ironForgeUpgrade = upgradeShop.getIronForgeUpgrade();
        final ManiacMinerUpgrade maniacMinerUpgrade = upgradeShop.getManiacMinerUpgrade();
        final ReinforcedArmorUpgrade reinforcedArmorUpgrade = upgradeShop.getReinforcedArmorUpgrade();
        final SharpnessUpgrade sharpnessUpgrade = upgradeShop.getSharpnessUpgrade();

        final ShopItem dragonBuffItem = dragonBuffUpgrade.getShopItem();
        final ShopItem healPoolItem = healPoolUpgrade.getItem();
        final UpgradeShopItem ironForgeItem = ironForgeUpgrade.getLevels().get(0);
        final UpgradeShopItem maniacMinerItem = maniacMinerUpgrade.getLevels().get(0);
        final UpgradeShopItem reinforcedArmorItem = reinforcedArmorUpgrade.getLevels().get(0);
        final ShopItem sharpnessItem = sharpnessUpgrade.getItem();
        final TrapUpgrades trapUpgrades = upgradeShop.getTrapUpgrades();

        upgradeInv.setItem(dragonBuffItem.getSlot(), dragonBuffItem.getCachedFancyStack());
        upgradeInv.setItem(healPoolItem.getSlot(), healPoolItem.getCachedFancyStack());
        upgradeInv.setItem(ironForgeUpgrade.getSlot(), ironForgeItem.getCachedFancyStack());
        upgradeInv.setItem(maniacMinerUpgrade.getSlot(), maniacMinerItem.getCachedFancyStack());
        upgradeInv.setItem(reinforcedArmorUpgrade.getSlot(), reinforcedArmorItem.getCachedFancyStack());
        upgradeInv.setItem(sharpnessItem.getSlot(), sharpnessItem.getCachedFancyStack());
        upgradeInv.setItem(trapUpgrades.getSlot(), trapUpgrades.getFancyItemStack());

        for (int i = 1; i <= 3; i++) {
            final ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, i);
            final ItemMeta glassMeta = glass.getItemMeta();
            glassMeta.setDisplayName("");
            glass.setItemMeta(glassMeta);
            upgradeInv.setItem(29 + i, glass);
        }

        return upgradeInv;
    }


    protected final ScoreboardHandler scoreboardHandler = new ScoreboardHandler() {

        @Override
        public String getTitle(final Player player) {
            return Bedwars.PREFIX;
        }

        @Override
        public List<Entry> getEntries(final Player player) {
            final EntryBuilder builder = new EntryBuilder();
            builder.next("§7   " + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            builder.blank();

            if (diamondSampleSpawner != null && emeraldSampleSpawner != null) {
                final String diamondText = GameUtils.generateScoreboardMissingTimeSpawners(getDiamondSampleSpawner());
                final String emeraldText = GameUtils.generateScoreboardMissingTimeSpawners(getEmeraldSampleSpawner());
                builder.next(diamondText);
                builder.next(emeraldText);
                builder.blank();
            }

            if (abstractDeathmatch.isRunning()) {
                builder.next(GameUtils.generateDeathmatch(abstractDeathmatch));
                builder.next(GameUtils.generateDragons(abstractDeathmatch));
            }

            for (final BedwarsTeam team : assignedTeams.keySet()) {
                builder.next(AllStrings.TEAM_SCOREBOARD.get() + team.getColorCode() + "§l" + team.name() + getTeamChar(team));
            }

            builder.blank();
            builder.next(" §e" + AllStrings.SERVER_BRAND.get());
            return builder.build();
        }

    };

    @SuppressWarnings("deprecation")
    public void destroyTeamBed(final BedwarsTeam destroyed, final Player destroyer) {
        questManager.breakBedReward(destroyer);

        associatedQueue.perform(p -> {
            if (p.isOnline() && p.getWorld().equals(associatedWorld)) {
                if (getPlayerTeam(p) == destroyed) {
                    p.sendTitle("", AllStrings.YOUR_BED_BROKEN.get());
                } else {
                    final BedwarsTeam destroyerTeam = getPlayerTeam(destroyer);
                    p.sendMessage(Bedwars.PREFIX + "§" + destroyed.getColorCode() + destroyed.name() + AllStrings.BED_BROKEN_BY.get() + destroyerTeam.getColorCode() + destroyer.getName());
                }
                p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 9.0f, 1.0f);
            }
        });
    }

    public final void cleanAllAndFinalize() {
        gameTrapTriggerer.stop();
        unregisterAllListeners();
        associatedQueue.cleanQueue();
        assignedTeams.clear();
        activeSpawners.forEach(ActiveSpawner::despawn);
        activeSpawners.clear();
        activeScoreboards.values().forEach(Scoreboard::deactivate);
        activeScoreboards.clear();
        destroyedTeams.clear();
        playersOutOfGame.clear();
        activeMerchants.forEach(AbstractActiveMerchant::delete);
        activeMerchants.clear();
        playerPlacedBlocks.clear();
        playerSetMap.clear();
        topKills.clear();
        gameLobbyTicker.stopTicking();
        abstractDeathmatch.stop();
        healPools.forEach(ActiveHealPool::stop);
        healPools.clear();
        upgradesLevelsMap.clear();
        playerUpgradeLevelsMap.clear();
        announcementsTasks.forEach(BukkitTask::cancel);
        announcementsTasks.clear();
        lastActivatedTraps.clear();
        associatedTrapsGUI.clear();
        hiddenPlayers.clear();
        emeraldBoostDrops.forEach(BukkitTask::cancel);
        emeraldBoostDrops.clear();
        experienceManager.stopRewardTask();
        destroyMap();
        try {
            finalize();
        } catch (final Throwable e) {
            e.printStackTrace();
        }
    }

    public final SpawnPosition getTeamSpawn(final BedwarsTeam bedwarsTeam) {
        for (final TeamSpawnPosition mapSpawn : bedwarsGame.getMapSpawns()) {
            if (mapSpawn.getBedwarsTeam() == bedwarsTeam) {
                return mapSpawn;
            }
        }
        return null;
    }

    public List<ActiveSpawner> getTeamSpawners(final BedwarsTeam team) {
        double squaredDistanceGold = -1;
        double squaredDistanceIron = -1;
        ActiveSpawner nearestGold = null;
        ActiveSpawner nearestIron = null;
        for (ActiveSpawner activeSpawner : activeSpawners) {
            if (activeSpawner.getType() == SpawnerType.GOLD) {
                final double tempDistance = activeSpawner.getSpawner().getSpawnPosition().squaredDistance(getTeamSpawn(team));
                if (nearestGold == null) {
                    nearestGold = activeSpawner;
                    squaredDistanceGold = tempDistance;
                } else if (squaredDistanceGold > tempDistance) {
                    squaredDistanceGold = tempDistance;
                    nearestGold = activeSpawner;
                }
            } else if (activeSpawner.getType() == SpawnerType.IRON) {
                final double tempDistance = activeSpawner.getSpawner().getSpawnPosition().squaredDistance(getTeamSpawn(team));
                if (nearestIron == null) {
                    nearestIron = activeSpawner;
                    squaredDistanceIron = tempDistance;
                } else if (squaredDistanceIron > tempDistance) {
                    squaredDistanceIron = tempDistance;
                    nearestIron = activeSpawner;
                }
            }
        }

        final List<ActiveSpawner> s = new ArrayList<>();
        s.add(nearestIron);
        s.add(nearestGold);
        return s;
    }

    public void handleError(final String text) {
        associatedQueue.perform(p -> p.sendMessage(Bedwars.PREFIX + "§c" + text));
    }

    public void moveToLobby(final Player player) {
        player.teleport(cachedLobbySpawnLocation);
    }

    public void moveAllToLobby() {
        associatedWorld.getPlayers().forEach(p -> {
            GameUtils.clearAllEffects(p);
            p.setGameMode(GameMode.SURVIVAL);
            p.setFallDistance(0.100f);
            GameUtils.clearArmor(p);
            p.getInventory().clear();
            p.teleport(getCachedLobbySpawnLocation());
            p.setAllowFlight(false);
            p.setFlying(false);
        });
    }

    public void connectedToQueue(final Player player) {
        associatedQueue.perform(p -> {
            p.sendMessage(Bedwars.PREFIX + player.getName() + AllStrings.JOINED_QUEUE.get() + getAssociatedWorld().getName() + " §r§equeue");
            p.sendMessage(Bedwars.PREFIX + String.format(AllStrings.QUEUE_STATUS.get(), getAssociatedQueue().queueSize() + 1, getBedwarsGame().getPlayers()));
        });
    }

    public void registerMapListeners() {
        final UnregisterableListener mapProtectionListener = new MapProtectionListener(this);
        final UnregisterableListener mapIllegalMovementsListener = new MapIllegalMovementsListener(this);
        final UnregisterableListener bedDestroyListener = new BedBreakListener(this);
        final UnregisterableListener lobbyCompassListener = new LobbyCompassListener(this);
        final UnregisterableListener deathListener = new PlayerDeathListener2(this);
        final UnregisterableListener quitListener = new PlayerQuitDuringGameListener(this);
        final UnregisterableListener merchantListener = new ShopMerchantListener(this);
        final UnregisterableListener entityDamageListener = new EntityDamageListener(this);
        final UnregisterableListener hungerLossListener = new HungerLossListener(this);
        final UnregisterableListener spectatorInteractListener = new SpectatorsInteractListener(this);
        final UnregisterableListener tntPlaceListener = new TNTPlaceListener(this);
        final UnregisterableListener fireballInteract = new PlayerFireballInteractListener(this);
        final UnregisterableListener explosionListener = new ExplosionListener(this);
        final UnregisterableListener shopListener = new ShopInteractListener(this);
        final UnregisterableListener upgradeMerchantListener = new UpgradeMerchantListener(this);
        final UnregisterableListener upgradeInteractListener = new UpgradeInteractListener(this);
        final UnregisterableListener spawnersMultigiveListener = new SpawnersMultigiveListener(this);
        final UnregisterableListener itemDegradeListener = new ItemDegradeListener(this);
        final UnregisterableListener playerSpectatePlayerListener = new PlayerSpectatePlayerListener(this);
        final UnregisterableListener potionModifyListener = new PotionModifyListener(this);
        final UnregisterableListener chestInteractListener = new ChestInteractListener(this);
        final UnregisterableListener dingSound = new KillSoundListener(this);

        final PluginManager pluginManager = plugin.getServer().getPluginManager();

        pluginManager.registerEvents(mapProtectionListener, plugin);
        pluginManager.registerEvents(mapIllegalMovementsListener, plugin);
        pluginManager.registerEvents(bedDestroyListener, plugin);
        pluginManager.registerEvents(lobbyCompassListener, plugin);
        pluginManager.registerEvents(deathListener, plugin);
        pluginManager.registerEvents(quitListener, plugin);
        pluginManager.registerEvents(merchantListener, plugin);
        pluginManager.registerEvents(entityDamageListener, plugin);
        pluginManager.registerEvents(hungerLossListener, plugin);
        pluginManager.registerEvents(spectatorInteractListener, plugin);
        pluginManager.registerEvents(tntPlaceListener, plugin);
        pluginManager.registerEvents(fireballInteract, plugin);
        pluginManager.registerEvents(explosionListener, plugin);
        pluginManager.registerEvents(shopListener, plugin);
        pluginManager.registerEvents(upgradeMerchantListener, plugin);
        pluginManager.registerEvents(upgradeInteractListener, plugin);
        pluginManager.registerEvents(spawnersMultigiveListener, plugin);
        pluginManager.registerEvents(itemDegradeListener, plugin);
        pluginManager.registerEvents(playerSpectatePlayerListener, plugin);
        pluginManager.registerEvents(potionModifyListener, plugin);
        pluginManager.registerEvents(chestInteractListener, plugin);
        pluginManager.registerEvents(dingSound, plugin);

        unregisterableListeners.add(mapIllegalMovementsListener);
        unregisterableListeners.add(mapProtectionListener);
        unregisterableListeners.add(bedDestroyListener);
        unregisterableListeners.add(lobbyCompassListener);
        unregisterableListeners.add(deathListener);
        unregisterableListeners.add(quitListener);
        unregisterableListeners.add(merchantListener);
        unregisterableListeners.add(entityDamageListener);
        unregisterableListeners.add(hungerLossListener);
        unregisterableListeners.add(spectatorInteractListener);
        unregisterableListeners.add(tntPlaceListener);
        unregisterableListeners.add(fireballInteract);
        unregisterableListeners.add(explosionListener);
        unregisterableListeners.add(shopListener);
        unregisterableListeners.add(upgradeMerchantListener);
        unregisterableListeners.add(upgradeInteractListener);
        unregisterableListeners.add(spawnersMultigiveListener);
        unregisterableListeners.add(itemDegradeListener);
        unregisterableListeners.add(playerSpectatePlayerListener);
        unregisterableListeners.add(potionModifyListener);
        unregisterableListeners.add(chestInteractListener);
        unregisterableListeners.add(dingSound);
    }

    public final void unregisterAllListeners() {
        for (final UnregisterableListener unregisterableListener : unregisterableListeners) {
            if (!unregisterableListener.isUnregistered()) {
                unregisterableListener.unregister();
            }
        }
    }

    public void moveToWaitingRoom(final Player player) {
        if (cachedWaitingLocation != null) {
            if (player.teleport(cachedWaitingLocation)) {
                connectedToQueue(player);
                player.setGameMode(GameMode.ADVENTURE);
            }
        } else
            player.sendMessage(Bedwars.PREFIX + AllStrings.TELEPORT_ERROR.get());
    }

    public BedwarsTeam getPlayerTeam(final Player player) {
        for (final Map.Entry<BedwarsTeam, List<Player>> entry : assignedTeams.entrySet()) {
            if (entry.getValue().contains(player)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public boolean isMerchantVillager(final Villager villager) {
        for (AbstractActiveMerchant activeMerchant : getActiveMerchants())
            if (activeMerchant.getVillager() == villager)
                return true;
        return false;
    }

    private void createPlayerShops() {
        for (final Map.Entry<BedwarsTeam, List<Player>> entry : assignedTeams.entrySet()) {
            for (Player p : entry.getValue()) {
                final EnumMap<ShopCategory, Inventory> pInvMap = new EnumMap<>(ShopCategory.class);
                for (ShopCategory value : ShopCategory.values()) {
                    pInvMap.put(value, cloneInventory(shopInventories.getFromCategory(value)));
                }
                this.playerShop.put(p.getUniqueId(), pInvMap);
            }
        }
    }


    public void start() {
        setHasStarted(true);
        if (associatedQueue.queueSize() >= bedwarsGame.getMinPlayers()) {
            if (associatedWorld == null) {
                handleError(AllStrings.TELEPORT_ERROR.get());
                return;
            }

            assignTeams();
            createPlayerShops();
            cleanAll();
            assignScoreboards();
            createSpawners();
            createMerchants();
            moveTeamsToSpawns();
            fillUpgradeMaps();
            setupUpgradeLevelsMap();
            announceNoTeaming();
            healAll();
            giveAllDefaultSet();
            fillTraps();
            fillTrapsDelayMap();
            breakNonPlayingBeds();
            showAll();
            gameTrapTriggerer.start();
            abstractDeathmatch.start();
            experienceManager.startRewardTask();
            this.startTime = System.currentTimeMillis();
        }
    }

    public void cleanAll() {
        assignedTeams.values().stream().flatMap(Collection::stream).forEach(p -> {
            if (p.isOnline()) {
                p.getInventory().clear();
            }
        });
    }

    public final void fillTrapsDelayMap() {
        final long time = System.currentTimeMillis();
        for (final Map.Entry<BedwarsTeam, List<Player>> entry : assignedTeams.entrySet()) {
            getLastActivatedTraps().put(entry.getKey(), time);
        }
    }

    public final void fillTraps() {
        for (final Map.Entry<BedwarsTeam, List<Player>> entry : assignedTeams.entrySet()) {
            this.getTeamActiveTrapsList().put(entry.getKey(), new LinkedList<>());
        }
    }

    public final void fillUpgradeMaps() {

        for (final UpgradeType upgradeType : UpgradeType.values()) {
            final Map<BedwarsTeam, Integer> teamIntegerMap = new HashMap<>();
            for (final BedwarsTeam bedwarsTeam : assignedTeams.keySet())
                teamIntegerMap.put(bedwarsTeam, 0x00);
            this.upgradesLevelsMap.put(upgradeType, teamIntegerMap);
        }

    }

    public final void healAll() {
        associatedWorld.getPlayers().forEach(p -> {
            p.setHealth(p.getMaxHealth());
            p.setFoodLevel(0x14);
        });
    }

    public abstract void moveTeamsToSpawns();

    public void givePlayerDefaultSet(final Player player) {

        final ArmorSet startingSet = new ArmorSet(Objects.requireNonNull(getPlayerTeam(player)));
        startingSet.getArmorSet().forEach((k, v) -> Slots.setArmorPiece(k, player, startingSet.getArmorSet().get(k)));
        GameUtils.giveStackToPlayer(new ItemStack(Material.WOOD_SWORD, 1), player, player.getInventory().getContents());
        this.playerSetMap.put(player, startingSet);
    }

    public void giveAllDefaultSet() {
        associatedQueue.perform(this::givePlayerDefaultSet);
    }

    public void removePlayer(final Player p) {
        associatedQueue.removeFromQueue(p);
    }

    public BedwarsTeam findWinningTeam() {
        if (teamsLeft().size() == 1) {
            return teamsLeft().stream().findAny().get();
        } else {
            return null;
        }
    }

    public List<Player> getTeamPlayers(final BedwarsTeam bedwarsTeam) {
        for (final Map.Entry<BedwarsTeam, List<Player>> entry : assignedTeams.entrySet()) {
            if (entry.getKey() == bedwarsTeam)
                return entry.getValue();
        }
        return null;
    }

    public final Set<BedwarsTeam> teamsLeft() {
        if (playersOutOfGame.isEmpty()) return Collections.emptySet();
        final Set<BedwarsTeam> bedwarsTeams = new HashSet<>();
        for (final Map.Entry<BedwarsTeam, List<Player>> entry : assignedTeams.entrySet()) {
            final BedwarsTeam team = entry.getKey();
            final List<Player> playerSet = entry.getValue();
            final boolean isTeamOutOfGame = playersOutOfGame.containsAll(playerSet);
            if (!isTeamOutOfGame) {
                bedwarsTeams.add(team);
            }
        }
        return bedwarsTeams;
    }

    public void breakNonPlayingBeds() {
        final Set<BedwarsTeam> bedwarsTeams = assignedTeams.keySet();
        for (final BedwarsTeam team : BedwarsTeam.values()) {
            if (!bedwarsTeams.contains(team)) {

                final SpawnPosition posOfTeam = bedwarsGame.spawnPosOfTeam(team);
                SpawnPosition nearestBed = null;
                double sqdDist = -1;
                for (final SpawnPosition bedSpawnPosition : bedwarsGame.getBedSpawnPositions()) {
                    final double tempSqdDist = posOfTeam.squaredDistance(bedSpawnPosition);
                    if (nearestBed == null) {
                        nearestBed = bedSpawnPosition;
                        sqdDist = tempSqdDist;
                    } else if (tempSqdDist <= sqdDist) {
                        nearestBed = bedSpawnPosition;
                        sqdDist = tempSqdDist;
                    }
                }
                if (nearestBed != null) {
                    final Location bedLoc = nearestBed.toLocation(associatedWorld);
                    final Block block = bedLoc.getBlock();
                    if (block != null && block.getType() == Material.BED) {
                        final Bed b = (Bed) block.getState().getData();
                        final Block facing = block.getRelative(b.getFacing());
                        block.setType(Material.AIR);
                        facing.setType(Material.AIR);
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void declareWinner(final BedwarsTeam team) {
        if (winnerDeclared) {
            return;
        }

        final UnregisterableListener goodGameListener = new GoodGameListener(this);
        plugin.getServer().getPluginManager().registerEvents(goodGameListener, plugin);
        unregisterableListeners.add(goodGameListener);

        associatedQueue.perform(p -> {
            if (p.isOnline()) {
                final BedwarsTeam pTeam = getPlayerTeam(p);
                if (pTeam == team) {
                    p.sendTitle(AllStrings.YOU_WON_GAME.get(), AllStrings.CONGRATULATIONS.get());
                    p.playSound(p.getLocation(), Sound.FIREWORK_TWINKLE, 7.50f, 1.00f);
                    ExperienceManager.rewardPlayer(bedwarsGame.getGamemode() == Gamemode.QUAD ? 50 : 100, p, this);
                    questManager.winDailyFirstGame(p);
                    GlobalStatsTableUtils.increaseWin(bedwarsGame.getGamemode(), plugin, p.getUniqueId());
                } else {
                    p.sendTitle(AllStrings.HAS_WON_THE_GAME.get(), AllStrings.RETURNING_LOBBY.get());
                }
                questManager.gamePlayedReward(p);
            }
        });

        killTracker.announceTopThreeScores();

        this.winnerDeclared = true;
    }

    public void stop() {
        moveAllToLobby();
        cleanAllAndFinalize();
    }

    public abstract void assignTeams();

    public abstract void assignScoreboards();

    public void createSpawners() {
        for (final ActiveSpawner activeSpawner : getActiveSpawners()) {
            activeSpawner.spawn();
            if (this.diamondSampleSpawner == null) {
                if (activeSpawner.getType() == SpawnerType.DIAMOND) {
                    this.diamondSampleSpawner = activeSpawner;
                }
            }
            if (this.emeraldSampleSpawner == null) {
                if (activeSpawner.getType() == SpawnerType.EMERALD) {
                    this.emeraldSampleSpawner = activeSpawner;
                }
            }
        }
        if (this.diamondSampleSpawner != null && this.emeraldSampleSpawner != null) {
            diamondSampleSpawner.getSpawner()
                    .getSpawnerLevels()
                    .stream()
                    .filter(lvl -> lvl.getLevel() != 1)
                    .forEach(i -> this.announcementsTasks.add(plugin.getServer().getScheduler()
                            .runTaskLater(plugin, () -> getAssociatedWorld()
                                    .getPlayers()
                                    .forEach(p -> p.sendMessage(Bedwars.PREFIX + AllStrings.DIAMOND_UPGRADE.get() + i.getLevel())), i.getAfterSeconds() * 20L)));

            emeraldSampleSpawner.getSpawner()
                    .getSpawnerLevels()
                    .stream()
                    .filter(lvl -> lvl.getLevel() != 1)
                    .forEach(i -> this.announcementsTasks.add(plugin.getServer().getScheduler()
                            .runTaskLater(plugin, () -> getAssociatedWorld()
                                    .getPlayers()
                                    .forEach(p -> p.sendMessage(Bedwars.PREFIX + AllStrings.EMERALD_UPGRADE.get() + i.getLevel())), i.getAfterSeconds() * 20L)));
        }
    }

    public void upgradePlayerArmorSet(final Player player, final String type) {
        final ArmorSet pSet = playerSetMap.get(player);
        pSet.upgradeAll(type);
        pSet.getArmorSet().forEach((k, v) -> Slots.setArmorPiece(k, player, pSet.getArmorSet().get(k)));
    }

    public void downgradePlayerTools(final Player player) {
        final Map<UpgradeItem, Integer> map = this.getPlayerUpgradeLevelsMap().get(player);

        out:
        for (Map.Entry<UpgradeItem, Integer> entry : map.entrySet()) {
            final UpgradeItem item = entry.getKey();
            final int level = entry.getValue();
            if (level > 0) {
                final List<UpgradeLevel> lvls = item.getLevels();
                map.put(item, level - 1);
                final ItemStack originalShopItem = lvls.get(level).getCachedFancyStack();
                final ItemStack downgradedItem = lvls.get(level - 1).getCachedGameStack();
                GameUtils.upgradePlayerStack(player, lvls.get(level).getCachedGameStack(), downgradedItem);
                for (Inventory inventory : playerShop.get(player.getUniqueId()).values()) {
                    final ItemStack[] contents = inventory.getContents();
                    for (int i = 0; i < contents.length; i++) {
                        final ItemStack content = contents[i];
                        if (content != null && content.isSimilar(originalShopItem)) {
                            inventory.setItem(i, lvls.get(level - 1).getCachedFancyStack());
                            continue out;
                        }
                    }
                }
            }
            player.updateInventory();
        }
    }

    public boolean isOutOfGame(final Player p) {
        if (p != null) {
            return playersOutOfGame.contains(p);
        }
        return true;
    }

    public void destroyMap() {
        final File wDir = associatedWorld.getWorldFolder();
        try {
            plugin.getServer().unloadWorld(associatedWorld, false);
            WorldsManager.getInstanceUnsafe().getActiveGameList().remove(this);
            FileUtils.deleteDirectory(wDir);
        } catch (IOException e) {
            plugin.getLogger().severe(AllStrings.DESTROY_MAP_ERROR.get() + associatedWorldFilename);
            e.printStackTrace();
        }
    }

    public void createMerchants() {
        for (final Merchant merchant : bedwarsGame.getMerchants()) {
            final AbstractActiveMerchant aMerchant = GameUtils.fromMerchant(merchant, this);
            if (aMerchant == null) {
                continue;
            }
            aMerchant.spawn();
            this.activeMerchants.add(aMerchant);
        }
    }

    protected String getTeamChar(final BedwarsTeam t) {
        if (destroyedTeams.contains(t) || abstractDeathmatch.isRunning()) {
            final List<Player> teamMembers = getTeamPlayers(t);
            if (teamMembers.stream().allMatch(this::isOutOfGame)) {
                return AllStrings.GRAPHIC_CROSS.get();
            } else {
                return " §f§l" + teamMembers.stream().filter(p -> !isOutOfGame(p)).count();
            }
        } else {
            return AllStrings.GRAPHIC_TICK.get();
        }
    }

    public ShopActiveMerchant getTeamShopActiveMerchant(final Villager clickedVillager) {
        for (final AbstractActiveMerchant activeMerchant : activeMerchants) {
            if (activeMerchant.getMerchant().getMerchantType() == MerchantType.SHOP) {
                if (activeMerchant.getVillager().getUniqueId().equals(clickedVillager.getUniqueId())) {
                    return (ShopActiveMerchant) activeMerchant;
                }
            }
        }
        return null;
    }

    public UpgradeActiveMerchant getTeamUpgradeActiveMerchant(final Villager clickedVillager) {
        for (final AbstractActiveMerchant activeMerchant : activeMerchants) {
            if (activeMerchant.getMerchant().getMerchantType() == MerchantType.UPGRADE) {
                if (activeMerchant.getVillager().getUniqueId().equals(clickedVillager.getUniqueId())) {
                    return (UpgradeActiveMerchant) activeMerchant;
                }
            }
        }
        return null;
    }

    public static Inventory cloneInventory(final Inventory inv) {
        final Inventory inventory = Bukkit.createInventory(null, inv.getSize(), inv.getTitle());
        final ItemStack[] originalContents = inv.getContents();
        for (int i = 0; i < originalContents.length; i++) {
            final ItemStack o = originalContents[i];
            if (o == null) {
                continue;
            }
            inventory.setItem(i, o.clone());
        }
        return inventory;
    }

    public void openUpgrade(final Player player) {
        if (!this.associatedUpgradeGUI.containsKey(player.getUniqueId())) {
            final Inventory associated = cloneInventory(Objects.requireNonNull(this.defaultUpgradeInv));
            associatedUpgradeGUI.put(player.getUniqueId(), associated);
        }
        player.openInventory(this.associatedUpgradeGUI.get(player.getUniqueId()));
    }

    public void openTraps(final Player player) {
        if (!this.associatedTrapsGUI.containsKey(player.getUniqueId())) {
            final Inventory associated = cloneInventory(Objects.requireNonNull(this.defaultTrapsInv));
            associatedTrapsGUI.put(player.getUniqueId(), associated);
        }
        player.openInventory(this.associatedTrapsGUI.get(player.getUniqueId()));
    }

    public final List<UnregisterableListener> getUnregisterableListeners() {
        return unregisterableListeners;
    }

    public final BedwarsGame getBedwarsGame() {
        return bedwarsGame;
    }

    public final String getAssociatedWorldFilename() {
        return associatedWorldFilename;
    }

    public final Location getCachedWaitingLocation() {
        return cachedWaitingLocation;
    }

    public final World getAssociatedWorld() {
        return associatedWorld;
    }

    public final Set<Block> getPlayerPlacedBlocks() {
        return playerPlacedBlocks;
    }

    public final World getLobbyWorld() {
        return lobbyWorld;
    }

    public final List<Player> getPlayersOutOfGame() {
        return playersOutOfGame;
    }

    public final SwordUpgrades getSwordUpgrades() {
        return swordUpgrades;
    }

    public final Location getCachedLobbySpawnLocation() {
        return cachedLobbySpawnLocation;
    }

    public final Plugin getPlugin() {
        return plugin;
    }

    public final AbstractQueue<Player> getAssociatedQueue() {
        return associatedQueue;
    }

    public final Set<ActiveSpawner> getActiveSpawners() {
        return activeSpawners;
    }

    public final List<AbstractActiveMerchant> getActiveMerchants() {
        return activeMerchants;
    }

    public final List<BedwarsTeam> getDestroyedTeams() {
        return destroyedTeams;
    }

    public final boolean isWinnerDeclared() {
        return winnerDeclared;
    }

    public final boolean isHasStarted() {
        return hasStarted;
    }

    public final Map<BedwarsTeam, List<Player>> getAssignedTeams() {
        return assignedTeams;
    }

    public Map<Player, Scoreboard> getActiveScoreboards() {
        return activeScoreboards;
    }

    public final BukkitTask getTimerTask() {
        return timerTask;
    }

    public final void setHasStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }

    public final Map<Player, ArmorSet> getPlayerSetMap() {
        return playerSetMap;
    }

    public final GameLobbyTicker getGameLobbyTicker() {
        return gameLobbyTicker;
    }

    public final Map<String, Integer> getTopKills() {
        return topKills;
    }

    public EnumMap<UpgradeType, Map<BedwarsTeam, Integer>> getUpgradesLevelsMap() {
        return upgradesLevelsMap;
    }

    public final ScoreboardHandler getScoreboardHandler() {
        return scoreboardHandler;
    }

    public Map<UUID, EnumMap<ShopCategory, Inventory>> getPlayerShop() {
        return playerShop;
    }

    public final Map<Player, Map<UpgradeItem, Integer>> getPlayerUpgradeLevelsMap() {
        return playerUpgradeLevelsMap;
    }

    public final Inventory getDefaultUpgradeInv() {
        return defaultUpgradeInv;
    }

    public final Map<UUID, Inventory> getAssociatedUpgradeGUI() {
        return associatedUpgradeGUI;
    }

    public final AbstractDeathmatch getAbstractDeathmatch() {
        return abstractDeathmatch;
    }

    public final ActiveSpawner getDiamondSampleSpawner() {
        return this.diamondSampleSpawner;
    }

    public final ActiveSpawner getEmeraldSampleSpawner() {
        return this.emeraldSampleSpawner;
    }

    public final long getStartTime() {
        return startTime;
    }

    public final List<ActiveHealPool> getHealPools() {
        return healPools;
    }

    public final Inventory getDefaultTrapsInv() {
        return defaultTrapsInv;
    }

    public final List<BukkitTask> getAnnouncementsTasks() {
        return announcementsTasks;
    }

    public final Map<UUID, Inventory> getAssociatedTrapsGUI() {
        return associatedTrapsGUI;
    }

    public final EnumMap<BedwarsTeam, LinkedList<ActiveTrap>> getTeamActiveTrapsList() {
        return teamActiveTrapsList;
    }

    public final EnumMap<BedwarsTeam, Long> getLastActivatedTraps() {
        return lastActivatedTraps;
    }

    public final List<Player> getImmuneTrapPlayers() {
        return immuneTrapPlayers;
    }

    public final List<Player> getPlayersRespawning() {
        return playersRespawning;
    }

    public final List<Player> getHiddenPlayers() {
        return hiddenPlayers;
    }

    public final GameTrapTriggerer getGameTrapTriggerer() {
        return gameTrapTriggerer;
    }

    public final QuestManager getQuestManager() {
        return questManager;
    }

    public final ExperienceManager getExperienceManager() {
        return experienceManager;
    }

    public final EnderchestManager getEnderchestManager() {
        return enderchestManager;
    }

    public final List<BukkitTask> getEmeraldBoostDrops() {
        return emeraldBoostDrops;
    }

    public final Map<UUID, UUID> getPlacedTntMap() {
        return placedTntMap;
    }

    public final KillTracker getKillTracker() {
        return killTracker;
    }
}
