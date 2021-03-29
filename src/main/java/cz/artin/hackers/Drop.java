package cz.artin.hackers;

import com.google.common.collect.Iterables;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
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
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class Drop extends JavaPlugin implements Listener {
    private static final Logger LOGGER = Logger.getLogger(Drop.class.getName());
    private static final List<DropPlayer> dropPlayers = new ArrayList<>();
    private static final List<ItemAdd> weapons = new ArrayList<>();
    private static final boolean DEBUG_STICK_ALLOWED = false;
    private static final int DEFAULT_START_TIME = 1000;
    private static final int DEFAULT_COUNTDOWN = 3;
    private static final int DEFAULT_MATCH_LENGTH = 300;
    private static final int DEFAULT_DUMMY_COUNT = 10;
    private static final int DEFAULT_DUMMY_RADIUS = 10;
    private static final int DEFAULT_CLEAR_AREA = 100;
    private static final int DEFAULT_PLAYER_LEVEL = 0;
    private static final float DEFAULT_WALK_SPEED = 0.2F;
    private static BukkitTask matchTaskId;
    private static Arena arena;
    private static Location PORTAL_EXIT = null;
    private static int countDown;

    /**
     * Server initialisation
     */
    @Override
    public void onEnable() {
        LOGGER.info("Loading DROP plugin...");

        getServer().getPluginManager().registerEvents(this, this);

        Objects.requireNonNull(getServer().getWorld("world")).setTime(DEFAULT_START_TIME);
        Objects.requireNonNull(getServer().getWorld("world")).setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        Objects.requireNonNull(getServer().getWorld("world")).setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, false);

        arena = new Arena();
        if (!arena.isInitialised() && Bukkit.getOnlinePlayers().size() != 0) {
            arena.setArenaCenter((Iterables.get(Bukkit.getOnlinePlayers(), 0)).getWorld().getSpawnLocation());
        }

        resetWeapons();
        resetResources();

        resetPlayers(Bukkit.getOnlinePlayers());

        LOGGER.info("...plugin successfully loaded.");
    }

    /**
     * Minecraft in-game command line handling
     *
     * @param commandSender Source object which is issuing/executing the command
     * @param command       The command executor
     * @param label         The command alias
     * @param arguments     All arguments passed to the command
     * @return If the command is known and executed successfully true, otherwise false
     */
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {
        Player player;
        if (commandSender instanceof Player) {
            player = (Player) commandSender;
        } else {
            return false;
        }

        if (label.equalsIgnoreCase("createLobby")) {
            return handleCommandCreateLobby();
        } else if (label.equalsIgnoreCase("startMatch")) {
            return handleCommandStartMatch(player, arguments);
        } else if (label.equalsIgnoreCase("endMatch")) {
            return handleCommandEndMatch(player);
        } else if (label.equalsIgnoreCase("showScore")) {
            return handleCommandShowScore(player);
        } else if (label.equalsIgnoreCase("clearArea")) {
            return handleCommandClearArea(player);
        } else if (label.equalsIgnoreCase("dropInventory")) {
            return handleCommandDropInventory(player);
        } else if (label.equalsIgnoreCase("setLevel")) {
            return handleCommandSetLevel(player, arguments);
        } else {
            return false;
        }
    }

    private boolean handleCommandCreateLobby() {
        arena.buildArena();
        arena.createLobby();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(arena.getLobbyRandomLocation());
            dropInventory(player);
        }
        return true;
    }

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
        return dropInventory(player);
    }

    private boolean handleCommandSetLevel(Player player, String[] arguments) {
        return setLevel(player, arguments);
    }

    /* Event Handlers */

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

        resetPlayer(event.getPlayer());
        event.getPlayer().teleport(arena.getArenaCenter());
    }

    /**
     * Handle leaving players
     *
     * @param event Source event
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        dropPlayers.removeIf(dropPlayer -> dropPlayer.getPlayer().equals(event.getPlayer()));
    }

    /**
     * Handle kicked players
     *
     * @param event Source event
     */
    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        dropPlayers.removeIf(dropPlayer -> dropPlayer.getPlayer().equals(event.getPlayer()));
    }

    /**
     * Handle respawning players
     *
     * @param event Source event
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        clearEffects(event.getPlayer());
        armPlayer(event.getPlayer());
        event.setRespawnLocation(arena.getArenaCenter());
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
        dropInventory(player);
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

    /* Implementation */

    /**
     * Reset players (mode, level, equipment, ...)
     *
     * @param players Players to be reset
     */
    private void resetPlayers(Collection<? extends Player> players) {
        for (Player player : players) {
            resetPlayer(player);
        }
    }

    /**
     * Reset player (mode, level, equipment, ...)
     *
     * @param player Player to be reset
     */
    private void resetPlayer(Player player) {
        dropPlayers.add(new DropPlayer(player));
        player.setGameMode(GameMode.SURVIVAL);
        player.setLevel(DEFAULT_PLAYER_LEVEL);
        clearEffects(player);
        armPlayer(player);
    }

    /**
     * Reset the weapons wielded by the players
     */
    private void resetWeapons() {
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
     * Reset the resources used by the players
     */
    private void resetResources() { // TODO: Review and refactor
        new BukkitRunnable() {
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Effect.addMana(player, Mana.Colour.BLACK, 1);
                    Effect.addMana(player, Mana.Colour.BLUE, 1);
                    Effect.addMana(player, Mana.Colour.RED, 1);
                    Effect.addMana(player, Mana.Colour.WHITE, 1);
                    Effect.addMana(player, Mana.Colour.GREEN, 1);
                }
                healPlayer();
            }
        }.runTaskTimer(this, 20 * 5L, 20 * 5L);

        matchTaskId = null;
    }

    /**
     * Clear all positive and negative effects from the player
     *
     * @param player Player to be cleared
     */
    private void clearEffects(Player player) {
        player.setWalkSpeed(DEFAULT_WALK_SPEED);
        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
    }

    /**
     * Equip the player with weapons and resources
     *
     * @param player Player which receives the equipment
     */
    private void armPlayer(Player player) { // TODO: Review and refactor
        dropInventory(player);

        for (ItemAdd item : weapons) {
            item.add(player);
        }

        player.getInventory().addItem(new ItemStack(Material.ARROW, 10));

        (new Mana()).add(player, Mana.Colour.BLACK, 5);
        (new Mana()).add(player, Mana.Colour.BLUE, 5);
        (new Mana()).add(player, Mana.Colour.GREEN, 5);
        (new Mana()).add(player, Mana.Colour.RED, 5);
        (new Mana()).add(player, Mana.Colour.WHITE, 5);
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
                for (DropPlayer player : dropPlayers) {
                    player.setKills(0);
                    player.setDeaths(0);
                }
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.setHealth(20);
                    onlinePlayer.setLevel(DEFAULT_PLAYER_LEVEL);
                    onlinePlayer.teleport(arena.getArenaRandomLocation());
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

                // Send players to lobby
                handleCommandCreateLobby();
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

    public boolean speedHack(CommandSender sender) {
        if (!(sender instanceof Player)) {
            LOGGER.warning("Unexpected use of Drop.speedHack");
            return false;
        }

        Player player = (Player) sender;
        float speed = player.getWalkSpeed();
        player.setWalkSpeed(speed * 2);
        return true;
    }


    private boolean buildArena(CommandSender sender) {
        return arena.buildArena();
    }

    private int getValueInt(String[] arguments, int index, int default_value) {
        if (index >= 0 && index < arguments.length) {
            return Integer.parseInt(arguments[index]);
        } else {
            return default_value;
        }
    }

    private boolean setHometown(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PORTAL_EXIT = player.getLocation().clone();
            Location plattformCenter = player.getLocation().clone();
            plattformCenter.add(0, -1, 0);
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    Location plattformBlockPosition = new Location(
                            player.getWorld(),
                            plattformCenter.getX() + x,
                            plattformCenter.getY(),
                            plattformCenter.getZ() + z);
                    plattformBlockPosition.getBlock().setType(Material.GLASS);
                    plattformBlockPosition.add(0, 4, 0);
                    plattformBlockPosition.getBlock().setType(Material.GOLD_BLOCK);

                }
            }
            for (int y = 0; y < 3; y++) {
                Location columnBlockPosition = player.getLocation().clone();
                columnBlockPosition.add(-2, y, 0);
                columnBlockPosition.getBlock().setType(Material.EMERALD_BLOCK);
                columnBlockPosition = player.getLocation().clone();
                columnBlockPosition.add(2, y, 0);
                columnBlockPosition.getBlock().setType(Material.EMERALD_BLOCK);
            }
        }
        return true;
    }

    private boolean setPortalExit(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PORTAL_EXIT = player.getLocation().clone();
        }
        return true;
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

    private boolean buildObelisk(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location obeliskLocation = player.getLocation().clone();
            obeliskLocation.add(5, 0, 0);
            obeliskLocation.getBlock().setType(Material.BLACK_CONCRETE);
            obeliskLocation.add(0, 1, 0);
            obeliskLocation.getBlock().setType(Material.BLACK_CONCRETE);
            obeliskLocation.add(0, 1, 0);
            obeliskLocation.getBlock().setType(Material.DIAMOND_BLOCK);
        }
        return true;
    }

    private boolean spawnDummies(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location playerLocation = player.getLocation().clone();
            int count = getValueInt(args, 0, DEFAULT_DUMMY_COUNT);
            int radius = getValueInt(args, 1, DEFAULT_DUMMY_RADIUS);
            for (int i = 0; i < count; i++) {
                int randomX = ThreadLocalRandom.current().nextInt(-radius, radius);
                int randomZ = ThreadLocalRandom.current().nextInt(-radius, radius);
                final Location dummyLocation = new Location(
                        player.getWorld(),
                        playerLocation.getX() + randomX,
                        playerLocation.getY(),
                        playerLocation.getZ() + randomZ);
                player.getWorld().spawn(dummyLocation, Zombie.class);
            }
        }
        return true;
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

    private boolean dropInventory(Player player) {
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack == null) {
                continue;
            }
            itemStack.setAmount(0);
        }
        return true;
    }

    private boolean setLevel(Player player, String[] arguments) {
        int level = getValueInt(arguments, 0, DEFAULT_PLAYER_LEVEL);
        player.setLevel(level);
        return true;
    }

    private DropPlayer getDropPlayer(UUID uuid) {
        for (DropPlayer player : dropPlayers) {
            if (uuid == player.getPlayerUuid()) {
                return player;
            }
        }
        return null;
    }

    private void healPlayer() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
            if (itemInMainHand.getItemMeta() != null) {
                String itemDisplayName = itemInMainHand.getItemMeta().getDisplayName();
                if (itemDisplayName.equals("cz.artin.hackers.Trident")) {
                    Effect.addHealth(player, 2);
                }
            }
        }
    }

    public interface ItemAdd {
        void add(Player player);
    }
}
