package cz.artin.hackers;

import org.bukkit.*;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class Drop extends JavaPlugin implements Listener {
    private static final Logger LOGGER = Logger.getLogger(Drop.class.getName());
    private static final List<DropPlayer> dropPlayers = new ArrayList<>();
    private static final List<ItemAdd> weapons = new ArrayList<>();
    private static final boolean DEBUG_STICK_ALLOWED = false;
    private static final int DEFAULT_COUNTDOWN = 3;
    private static final int DEFAULT_MATCH_LENGTH = 300;
    private static final int DEFAULT_DUMMY_COUNT = 10;
    private static final int DEFAULT_DUMMY_RADIUS = 10;
    private static final int DEFAULT_CLEAR_AREA = 100;
    private static BukkitTask matchTaskId;
    private static Arena arena;
    private static Location PORTAL_EXIT = null;
    private static int countDown;

    @Override
    public void onEnable() {
        LOGGER.info("Loading DROP plugin...");

        getServer().getPluginManager().registerEvents(this, this);

        Objects.requireNonNull(getServer().getWorld("world")).setTime(1000);
        Objects.requireNonNull(getServer().getWorld("world")).setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        Objects.requireNonNull(getServer().getWorld("world")).setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, false);

        arena = new Arena();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (arena.getArenaLocation() == null) {
                arena.setArenaLocation(player.getWorld().getSpawnLocation());
            }
            dropPlayers.add(new DropPlayer(player));
        }

        if (DEBUG_STICK_ALLOWED) {
            weapons.add(new DebugStick(this));
        }
        weapons.add(new ZireaelSword(this));
        weapons.add(new FilipAxe(this));
        weapons.add(new ZdenekWand(this));
        weapons.add(new Trident(this));
        weapons.add(new Bow(this));
        weapons.add(new SwordOfTheDamned(this));

        new BukkitRunnable() {
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Effect.addMana(player, Mana.Colour.BLACK, 1);
                    Effect.addMana(player, Mana.Colour.BLUE, 1);
                    Effect.addMana(player, Mana.Colour.RED, 1);
                    Effect.addMana(player, Mana.Colour.WHITE, 1);
                }
                healPlayer();
            }
        }.runTaskTimer(this, 20 * 5L, 20 * 5L);

        matchTaskId = null;

        LOGGER.info("...plugin successfully loaded.");
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

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("startMatch")) {
            return startMatch(commandSender, args);
        } else if (label.equalsIgnoreCase("endMatch")) {
            return endMatch(commandSender);
        } else if (label.equalsIgnoreCase("showScore")) {
            return showScore(commandSender);
        } else if (label.equalsIgnoreCase("debugClearArea")) {
            return clearArea(commandSender);
        } else if (label.equalsIgnoreCase("debugBuildLobby")) {
            return buildLobby();
        } else if (label.equalsIgnoreCase("debugDropInventory")) {
            return dropInventory((Player) commandSender);
        } else if (label.equalsIgnoreCase("buildArena")) {
            return buildArena(commandSender);
        } else if (label.equalsIgnoreCase("setPortalExit")) {
            return setPortalExit(commandSender);
        } else if (label.equalsIgnoreCase("spawnDummies")) {
            return spawnDummies(commandSender, args);
        } else if (label.equalsIgnoreCase("sethometown")) {
            getLogger().info("sethometown");
            return setHometown(commandSender);
        } else if (label.equalsIgnoreCase("buildObelisk")) {
            getLogger().info("buildObelisk()");
            return buildObelisk(commandSender);
        } else if (label.equalsIgnoreCase("speedHack")) {
            return speedHack(commandSender);
        } else {
            return false;
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        LOGGER.info("A new player, " + event.getPlayer().getName() + ", just joined the fray");

        if (arena.getArenaLocation() == null) {
            arena.setArenaLocation(event.getPlayer().getWorld().getSpawnLocation());
        }

        dropPlayers.add(new DropPlayer(event.getPlayer()));
        event.getPlayer().setGameMode(GameMode.SURVIVAL);
        dropInventory(event.getPlayer());
        for (ItemAdd item : weapons) {
            item.add(event.getPlayer());
        }

        event.getPlayer().getInventory().addItem(new ItemStack(Material.ARROW, 5));

        (new Mana()).add(event.getPlayer(), Mana.Colour.BLACK, 5);
        (new Mana()).add(event.getPlayer(), Mana.Colour.BLUE, 3);
        (new Mana()).add(event.getPlayer(), Mana.Colour.GREEN, 3);
        (new Mana()).add(event.getPlayer(), Mana.Colour.WHITE, 3);
        event.getPlayer().teleport(new Location(event.getPlayer().getWorld(), -100, 70, 100));
        event.getPlayer().setWalkSpeed(0.2F);
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
                arena.buildArena(commandSender);
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.setHealth(20);
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
                buildLobby();
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

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        dropPlayers.removeIf(dropPlayer -> dropPlayer.getPlayer().equals(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        dropPlayers.removeIf(dropPlayer -> dropPlayer.getPlayer().equals(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        dropInventory(event.getPlayer());
        for (ItemAdd item : weapons) {
            item.add(event.getPlayer());
        }

        event.getPlayer().getInventory().addItem(new ItemStack(Material.ARROW, 5));

        (new Mana()).add(event.getPlayer(), Mana.Colour.BLACK, 5);
        (new Mana()).add(event.getPlayer(), Mana.Colour.BLUE, 3);
        (new Mana()).add(event.getPlayer(), Mana.Colour.GREEN, 3);

        event.setRespawnLocation(new Location(event.getPlayer().getWorld(), -100, 70, 100));
        event.getPlayer().setWalkSpeed(0.2F);
    }

    private boolean buildArena(CommandSender sender) {
        return arena.buildArena(sender);
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

    private int getValueInt(String[] args, int index, int default_value) {
        if (index < 0 || index >= args.length) {
            return default_value;
        } else return Integer.parseInt(args[index]);
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
                    plattformBlockPosition.getBlock().setType(Material.GREEN_WOOL);

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

    private boolean buildLobby() {
        arena.buildLobby();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(arena.getLobbyRandomLocation());
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

    public interface ItemAdd {
        void add(Player player);
    }
}
