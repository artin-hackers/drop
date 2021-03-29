package cz.artin.hackers;

import com.google.common.collect.Iterables;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.logging.Logger;

public class Drop extends JavaPlugin implements Listener {
    private static final Logger LOGGER = Logger.getLogger(Drop.class.getName());
    private static final List<DropPlayer> dropPlayers = new ArrayList<>();
    private static final List<ItemAdd> weapons = new ArrayList<>();
    private static final boolean DEBUG_STICK_ALLOWED = false;
    private static final int DEFAULT_START_TIME = 1000;
    private static final int DEFAULT_PLAYER_LEVEL = 0;
    private static final float DEFAULT_WALK_SPEED = 0.2F;
    private static final int DEFAULT_RESOURCE_AMOUNT = 5;

    // TODO: Review chaotic variables below

    private static final int DEFAULT_COUNTDOWN = 3;
    private static final int DEFAULT_MATCH_LENGTH = 300;
    private static final int DEFAULT_CLEAR_AREA = 100;
    private static final Location PORTAL_EXIT = null;
    private static BukkitTask matchTaskId;
    private static Arena arena;
    private static int countDown;

    /**
     * Server initialisation
     */
    @Override
    public void onEnable() {
        LOGGER.info("Loading DROP plugin...");

        getServer().getPluginManager().registerEvents(this, this);

        Objects.requireNonNull(getServer().getWorld("world")).setTime(DEFAULT_START_TIME);
        Objects.requireNonNull(getServer().getWorld("world")).setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        Objects.requireNonNull(getServer().getWorld("world")).setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, false);

        arena = new Arena();
        if (!arena.isInitialised() && Bukkit.getOnlinePlayers().size() != 0) {
            arena.setArenaCenter((Iterables.get(Bukkit.getOnlinePlayers(), 0)).getWorld().getSpawnLocation());
        }

        initialiseWeapons();
        initialiseResourceGenerator();
        registerPlayers(Bukkit.getOnlinePlayers());

        LOGGER.info("...plugin successfully loaded.");
    }

    /**
     * Minecraft in-game command line handling
     *
     * @param sender    Source object which is issuing/executing the command
     * @param command   The command executor
     * @param label     The command alias
     * @param arguments All arguments passed to the command
     * @return If the command is known and executed successfully true, otherwise false
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if (!(sender instanceof Player)) {
            return false;
        }

        if (label.equalsIgnoreCase("createLobby")) {
            return handleCommandCreateLobby();
        } else if (label.equalsIgnoreCase("startMatch")) {
            return handleCommandStartMatch((Player) sender, arguments);
        } else if (label.equalsIgnoreCase("endMatch")) {
            return handleCommandEndMatch((Player) sender);
        } else if (label.equalsIgnoreCase("showScore")) {
            return handleCommandShowScore((Player) sender);
        } else if (label.equalsIgnoreCase("clearArea")) {
            return handleCommandClearArea((Player) sender);
        } else if (label.equalsIgnoreCase("dropInventory")) {
            return handleCommandDropInventory((Player) sender);
        } else if (label.equalsIgnoreCase("setLevel")) {
            return handleCommandSetLevel((Player) sender, arguments);
        } else {
            return false;
        }
    }

    /* Command handling */

    /**
     * Handle the command "createLobby"
     *
     * @return Always true
     */
    private boolean handleCommandCreateLobby() {
        arena.buildArena();
        arena.createLobby();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(arena.getLobbyRandomLocation());
            restorePlayer(player);
            removePlayerInventory(player);
        }
        return true;
    }

    /* Event handling */

    /**
     * Handle newly joining players
     *
     * @param event Source event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        LOGGER.info("A new player, " + event.getPlayer().getName() + ", just joined the fray");

        if (!arena.isInitialised()) {
            arena.setArenaCenter(event.getPlayer().getWorld().getSpawnLocation());
        }

        registerPlayer(event.getPlayer());
        resetPlayer(event.getPlayer());
        restorePlayer(event.getPlayer());
        restorePlayerInventory(event.getPlayer());
        event.getPlayer().teleport(arena.getArenaCenter());
    }

    /**
     * Handle leaving players
     *
     * @param event Source event
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        unregisterPlayer(event.getPlayer());
    }

    /**
     * Handle kicked players
     *
     * @param event Source event
     */
    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        unregisterPlayer(event.getPlayer());
    }

    /**
     * Handle respawning players
     *
     * @param event Source event
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        restorePlayer(event.getPlayer());
        restorePlayerInventory(event.getPlayer());
        event.setRespawnLocation(arena.getArenaCenter());
    }

    /* Other */

    /**
     * Initialise the weapons wielded by the players
     */
    private void initialiseWeapons() {
        if (DEBUG_STICK_ALLOWED) {
            weapons.add(new DebugStick(this));
        }
        weapons.add(new ZireaelSword(this));
        weapons.add(new FilipAxe(this));
        weapons.add(new ZdenekWand(this));
        weapons.add(new Trident(this));
        weapons.add(new Bow(this));
        weapons.add(new SwordOfTheDamned(this));
        weapons.add(new FrostAxe(this));
    }

    /**
     * Initialise the resource generator
     */
    private void initialiseResourceGenerator() { // TODO: Move to DropPlayer, every player will have his own timer
        new BukkitRunnable() {
            public void run() {
                if (matchTaskId != null) { // TODO: Create Match class
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        Effect.addMana(player, Mana.Colour.BLACK, 1);
                        Effect.addMana(player, Mana.Colour.BLUE, 1);
                        Effect.addMana(player, Mana.Colour.RED, 1);
                        Effect.addMana(player, Mana.Colour.WHITE, 1);
                        Effect.addMana(player, Mana.Colour.GREEN, 1);
                    }
                    healPlayer(); // TODO: Refactor
                }
            }
        }.runTaskTimer(this, 20 * 5L, 20 * 5L);
    }

    /**
     * Register players for the game
     *
     * @param players Players to be registered
     */
    private void registerPlayers(Collection<? extends Player> players) {
        for (Player player : players) {
            registerPlayer(player);
        }
    }

    /**
     * Register new player for the game
     *
     * @param player Player to be registered
     */
    private void registerPlayer(Player player) {
        dropPlayers.add(new DropPlayer(player));
        player.setGameMode(GameMode.SURVIVAL);
    }

    /**
     * Unregister player from the game
     *
     * @param player Player to be unregistered
     */
    private void unregisterPlayer(Player player) {
        dropPlayers.removeIf(dropPlayer -> dropPlayer.getPlayer().equals(player));
    }

    /**
     * Reset player progress and statistics - level, kill/death count, ...
     *
     * @param player Player to be reset
     */
    private void resetPlayer(Player player) {
        player.setLevel(DEFAULT_PLAYER_LEVEL);
        Objects.requireNonNull(getDropPlayer(player.getUniqueId())).setKills(0);
        Objects.requireNonNull(getDropPlayer(player.getUniqueId())).setDeaths(0);
    }

    /**
     * Restore player attributes - restore health, clear (de)buffs
     *
     * @param player Player to be restored
     */
    private void restorePlayer(Player player) {
        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
        player.setWalkSpeed(DEFAULT_WALK_SPEED);
    }

    /**
     * Restore player's inventory
     *
     * @param player The player whose inventory gets restored
     */
    private void restorePlayerInventory(Player player) {
        removePlayerInventory(player);

        for (ItemAdd item : weapons) {
            item.add(player);
        }

        player.getInventory().addItem(new ItemStack(Material.ARROW, DEFAULT_RESOURCE_AMOUNT));
        (new Mana()).add(player, Mana.Colour.BLACK, DEFAULT_RESOURCE_AMOUNT);
        (new Mana()).add(player, Mana.Colour.BLUE, DEFAULT_RESOURCE_AMOUNT);
        (new Mana()).add(player, Mana.Colour.GREEN, DEFAULT_RESOURCE_AMOUNT);
        (new Mana()).add(player, Mana.Colour.RED, DEFAULT_RESOURCE_AMOUNT);
        (new Mana()).add(player, Mana.Colour.WHITE, DEFAULT_RESOURCE_AMOUNT);
    }

    /**
     * Remove player's whole inventory
     *
     * @param player The player whose inventory gets removed
     */
    private void removePlayerInventory(Player player) {
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack != null) {
                itemStack.setAmount(0);
            }
        }
    }

    /**
     * Get matching DROP player instance for UUID
     *
     * @param uuid UUID of the player
     * @return DROP player instance, null if there is no match
     */
    private DropPlayer getDropPlayer(UUID uuid) {
        for (DropPlayer player : dropPlayers) {
            if (player.getPlayerUuid() == uuid) {
                return player;
            }
        }
        return null;
    }

    // TODO: Review the utter chaos below

    private boolean handleCommandStartMatch(Player player, String[] arguments) {
        return startMatch(player, arguments);
    }

    private boolean handleCommandEndMatch(Player player) {
        return endMatch(player);
    }

    private boolean handleCommandShowScore(Player player) {
        return showScore(player);
    }

    private boolean handleCommandClearArea(Player player) {
        return clearArea(player);
    }

    private boolean handleCommandDropInventory(Player player) {
        removePlayerInventory(player);
        return true;
    }

    private boolean handleCommandSetLevel(Player player, String[] arguments) {
        return setLevel(player, arguments);
    }

    @EventHandler
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED || event.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN)
            event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity().getPlayer();
        removePlayerInventory(player);
        if (player != null) {
            for (DropPlayer dropPlayer : dropPlayers) {
                if (dropPlayer.getName().equals(player.getName())) {
                    dropPlayer.addDeath();
                }
                Player killer = player.getKiller();
                killer.setLevel(killer.getLevel() + 1);
                if (killer != null) {
                    if (dropPlayer.getName().equals(killer.getName())) {
                        dropPlayer.addKill();
                    }
                }
                Bukkit.broadcastMessage(dropPlayer.getName() + ": " + dropPlayer.getKills() + "/" + dropPlayer.getDeaths());
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack itemInMainHand = event.getPlayer().getInventory().getItemInMainHand();
        if (itemInMainHand.getItemMeta() != null) {
            String itemDisplayName = itemInMainHand.getItemMeta().getDisplayName();
            if (itemDisplayName.equals("RifleWand")) {
                if (event.getAction().equals(Action.LEFT_CLICK_AIR)
                        || event.getAction().equals(Action.LEFT_CLICK_BLOCK)
                ) {
                    shootRifleWand(event.getPlayer());
                }
            }

            if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if (Objects.requireNonNull(event.getClickedBlock()).getType().equals(Material.DIAMOND_BLOCK)) {
                    if (PORTAL_EXIT != null) {
                        event.getPlayer().teleport(PORTAL_EXIT);
                    } else {
                        event.getPlayer().teleport(event.getPlayer().getWorld().getSpawnLocation());
                    }
                }
            }
        }
    }

    private boolean startMatch(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof Player)) {
            return false;
        }

        if (matchTaskId != null) {
            commandSender.sendMessage("Match is already in progress");
            return false;
        }

        Bukkit.broadcastMessage("Match will start in...");

        countDown = DEFAULT_COUNTDOWN;
        matchTaskId = Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (countDown == 0) {
                Bukkit.getScheduler().cancelTask(matchTaskId.getTaskId());
                Bukkit.broadcastMessage("FIGHT!");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    resetPlayer(player);
                    restorePlayer(player);
                    restorePlayerInventory(player);
                    player.teleport(arena.getArenaRandomLocation());
                }
                runMatch(getValueInt(args, 0, DEFAULT_MATCH_LENGTH));
            } else {
                Bukkit.broadcastMessage("..." + countDown);
                countDown--;
            }
        }, 20L, 20L);

        return true;
    }

    private void runMatch(int matchLength) {
        matchTaskId = Bukkit.getScheduler().runTaskTimer(this, () -> {
            Bukkit.getScheduler().cancelTask(matchTaskId.getTaskId());
            endMatch();
        }, 20L * matchLength, 20L);
    }

    private void endMatch() {
        Bukkit.broadcastMessage("Match will end in...");
        countDown = DEFAULT_COUNTDOWN;
        matchTaskId = Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (countDown == 0) {
                Bukkit.getScheduler().cancelTask(matchTaskId.getTaskId());
                Bukkit.broadcastMessage("Match has ended!");

                arena.createLobby();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.teleport(arena.getLobbyRandomLocation());
                    restorePlayer(player);
                    removePlayerInventory(player);
                }
                for (DropPlayer player : dropPlayers) {
                    Bukkit.broadcastMessage(player.getName() + ": " + player.getKills() + "/" + player.getDeaths());
                }
                matchTaskId = null;
            } else {
                Bukkit.broadcastMessage("..." + countDown);
                countDown--;
            }
        }, 20L, 20L);
    }

    private boolean endMatch(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            return false;
        }

        if (matchTaskId == null) {
            commandSender.sendMessage("Cannot end match, no match in progress");
            return false;
        }

        Bukkit.getScheduler().cancelTask(matchTaskId.getTaskId());
        endMatch();

        return true;
    }

    private boolean showScore(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            return false;
        }

        for (DropPlayer dropPlayer : dropPlayers) {
            commandSender.sendMessage(dropPlayer.getName() + ": " + dropPlayer.getKills() + "/" + dropPlayer.getDeaths());
        }

        return true;
    }

    private int getValueInt(String[] arguments, int index, int default_value) {
        if (index >= 0 && index < arguments.length) {
            return Integer.parseInt(arguments[index]);
        } else {
            return default_value;
        }
    }

    private void shootRifleWand(Player player) {
        getLogger().info("Drop.shootRifleWand()");
        Entity target = getNearestLivingEntityInSight(player, 20);
        if (target != null) {
            getLogger().info("Target: " + target.getName());
            if (target instanceof LivingEntity) {
                ((LivingEntity) target).damage(999999999, player);
            }
        }
    }

    public Entity getNearestLivingEntityInSight(Player player, int range) {
        ArrayList<Entity> nearbyEntities = (ArrayList<Entity>) player.getNearbyEntities(range, range, range);
        ArrayList<Entity> nearbyLivingEntities = new ArrayList<>();
        for (Entity entity : nearbyEntities) {
            if (entity instanceof LivingEntity) {
                nearbyLivingEntities.add(entity);
            }
        }
        ArrayList<Block> sightBlocks = (ArrayList<Block>) player.getLineOfSight(null, range);
        for (Block block : sightBlocks) {
            Location location = block.getLocation();
            for (Entity entity : nearbyLivingEntities) {
                if (Math.abs(entity.getLocation().getX() - location.getX()) < 1.3
                        && Math.abs(entity.getLocation().getY() - location.getY()) < 1.5
                        && Math.abs(entity.getLocation().getZ() - location.getZ()) < 1.3
                ) {
                    return entity;
                }
            }
        }
        return null;
    }

    private boolean clearArea(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            return false;
        }

        Location playerLocation = Objects.requireNonNull(((Player) commandSender).getPlayer()).getLocation();
        for (int x = -(DEFAULT_CLEAR_AREA >> 1); x < (DEFAULT_CLEAR_AREA >> 1); x++) {
            for (int y = 0; y < DEFAULT_CLEAR_AREA; y++) {
                for (int z = -(DEFAULT_CLEAR_AREA >> 1); z < (DEFAULT_CLEAR_AREA >> 1); z++) {
                    (new Location(playerLocation.getWorld(), playerLocation.getX() + x, playerLocation.getY() + y, playerLocation.getZ() + z)).getBlock().setType(Material.AIR);
                }
            }
        }

        return true;
    }

    private boolean setLevel(Player player, String[] arguments) {
        int level = getValueInt(arguments, 0, DEFAULT_PLAYER_LEVEL);
        player.setLevel(level);
        return true;
    }

    private void healPlayer() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
            if (itemInMainHand.getItemMeta() != null) {
                String itemDisplayName = itemInMainHand.getItemMeta().getDisplayName();
                if (itemDisplayName.equals("cz.artin.hackers.Trident")) {
                    Effect.addHealth(player, 6);
                }
            }
        }
    }

    public interface ItemAdd {
        void add(Player player);
    }
}
