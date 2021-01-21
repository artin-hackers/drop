package cz.artin.hackers;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class Drop extends JavaPlugin implements Listener {
    private static final Logger LOGGER = Logger.getLogger(Drop.class.getName());
    private static final boolean DEBUG_STICK_ALLOWED = false;
    private static final List<DropPlayer> dropPlayers = new ArrayList<>();
    private static final List<ItemAdd> items = new ArrayList<>();
    private static final int DEFAULT_DUMMY_COUNT = 10;
    private static final int DEFAULT_DUMMY_RADIUS = 10;
    private static Arena arena;
    private static Location PORTAL_EXIT = null;
    private static BukkitTask taskId;
    private static int countDown;

    @Override
    public void onEnable() {  // TODO: Refactor
        LOGGER.info("Loading DROP plugin...");

        getServer().getPluginManager().registerEvents(this, this);

        Objects.requireNonNull(getServer().getWorld("world")).setTime(1000);  // Development setup, remove in release version
        Objects.requireNonNull(getServer().getWorld("world")).setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);  // Development setup, remove in release version

        for (Player player : Bukkit.getOnlinePlayers()) {
            dropPlayers.add(new DropPlayer(player));
        }

        arena = new Arena();

        if (DEBUG_STICK_ALLOWED) {
            items.add(new DebugStick(this));
        }
        items.add(new ZireaelSword(this));
        items.add(new FilipAxe(this));
        items.add(new ZdenekWand(this));
        items.add(new InvulnerabilityTrident());
        items.add(new Bow(this));

        new BukkitRunnable() {
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Effect.addMana(player, Mana.Colour.BLUE, 1);
                    Effect.addMana(player, Mana.Colour.RED, 1);
                }
            }
        }.runTaskTimer(this, 20 * 5L, 20 * 10L);

        taskId = null;

        LOGGER.info("...plugin successfully loaded.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {  // TODO: Refactor
        if (label.equalsIgnoreCase("startMatch")) {
            return startMatch(sender);
        } else if (label.equalsIgnoreCase("showScore")) {
            return showScore(sender);
        } else if (label.equalsIgnoreCase("buildArena")) {
            return buildArena(sender);
        } else if (label.equalsIgnoreCase("setPortalExit")) {
            return setPortalExit(sender);
        } else if (label.equalsIgnoreCase("spawnDummies")) {
            return spawnDummies(sender, args);
        } else if (label.equalsIgnoreCase("sethometown")) {
            getLogger().info("sethometown");
            return setHometown(sender);
        } else if (label.equalsIgnoreCase("buildObelisk")) {
            getLogger().info("buildObelisk()");
            return buildObelisk(sender);
        } else if (label.equalsIgnoreCase("speedHack")) {
            return speedHack(sender);
        } else {
            return false;
        }
    }

    private boolean startMatch(CommandSender commandSender) {  // TODO: Refactor
        if (!(commandSender instanceof Player)) {
            LOGGER.warning("Unexpected use of Drop.startMatch");
            return false;
        }

        if (taskId != null) {
            Bukkit.broadcastMessage("Match already in progress");
            LOGGER.warning("Unexpected use of Drop.startMatch");
            return false;
        }

        Bukkit.broadcastMessage("Match will start in...");
        countDown = 5;
        taskId = Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (countDown > 0) {
                Bukkit.broadcastMessage("..." + countDown);
            } else {
                Bukkit.getScheduler().cancelTask(taskId.getTaskId());
                Bukkit.broadcastMessage("FIGHT!");

                for (DropPlayer player : dropPlayers) {
                    player.setKills(0);
                    player.setDeaths(0);
                }

                arena.buildArena(commandSender);

                finishMatch();
            }
            countDown--;
        }, 20L, 20L);

        return true;
    }

    private boolean showScore(CommandSender commandSender) {
        LOGGER.info("showScore");

        if (!(commandSender instanceof Player)) {
            LOGGER.warning("Unexpected use of Drop.showScore");
            return false;
        }

        for (DropPlayer dropPlayer : dropPlayers) {
            commandSender.sendMessage(dropPlayer.getName() + ": " + dropPlayer.getKills() + "/" + dropPlayer.getDeaths());
        }

        return true;
    }

    // TODO: Refactor from here down

    public boolean speedHack(CommandSender sender) {
        if (!(sender instanceof Player)) {
            LOGGER.warning("Unexpected use of Drop.speedHack");
            return false;
        }

        Player player = (Player) sender;
        float speed = player.getWalkSpeed();
        player.setWalkSpeed(speed*2);
        return true;
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        LOGGER.info("A new player, " + event.getPlayer().getName() + ", just joined the fray.");
        dropPlayers.add(new DropPlayer(event.getPlayer()));
        event.getPlayer().setGameMode(GameMode.SURVIVAL);
        for (ItemAdd item : items) {
            item.add(event.getPlayer());
        }

        event.getPlayer().getInventory().addItem(new ItemStack(Material.ARROW, 5));

        (new Mana()).add(event.getPlayer(), Mana.Colour.BLUE, 3);
        (new Mana()).add(event.getPlayer(), Mana.Colour.GREEN, 3);

        event.getPlayer().teleport(new Location(event.getPlayer().getWorld(), -100, 70, 100));
        event.getPlayer().setWalkSpeed(0.2F);
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
        for (ItemAdd item : items) {
            item.add(event.getPlayer());
        }

        event.getPlayer().getInventory().addItem(new ItemStack(Material.ARROW, 5));

        (new Mana()).add(event.getPlayer(), Mana.Colour.BLUE, 3);
        (new Mana()).add(event.getPlayer(), Mana.Colour.GREEN, 3);

        event.setRespawnLocation(new Location(event.getPlayer().getWorld(), -100, 70, 100));
        event.getPlayer().setWalkSpeed(0.2F);
    }

//    @EventHandler
//    public void onHit(EntityDamageByEntityEvent event) {
//        if (event.getEntity() instanceof Player) {
//            if (event.getEntity().getName().equals("Arcifrajer") || event.getEntity().getName().equals("u56975")) {
//                event.setDamage(0);
//            }
//        }
//    }

    // @EventHandler
    // public void onDamage(EntityDamageByEntityEvent event) {
    //     LOGGER.warning("Drop.onDamage");
    //     if (event.getEntity() instanceof Player) {
    //         if (event.getEntity().getName().equals("Arcifrajer") || event.getEntity().getName().equals("u56975")) {
    //             event.setDamage(0);
    //         }
    //     }
    // }

//    @EventHandler
//    public void onPlayerDamage(EntityDamageEvent event) {
//        if (event.getEntity() instanceof Player) {
//            if (event.getEntity().getName().equals("Arcifrajer") || event.getEntity().getName().equals("u56975")) {
//                event.setCancelled(true);
//            }
//        }
//    }

    private boolean buildArena(CommandSender sender) {
        return arena.buildArena(sender);
    }

    private void finishMatch() {
        countDown = 300;
        taskId = Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (countDown == 6) {
                Bukkit.broadcastMessage("Match will end in...");
            } else if (countDown < 6 && countDown > 0) {
                Bukkit.broadcastMessage("..." + countDown);
            } else {
                Bukkit.getScheduler().cancelTask(taskId.getTaskId());
                Bukkit.broadcastMessage("Match has ended.");

                for (DropPlayer player : dropPlayers) {
                    Bukkit.broadcastMessage(player.getName() + ": " + player.getKills() + "/" + player.getDeaths());
                }
                taskId = null;
            }
            countDown--;
        }, 20L * 240, 20L);
    }

    // REFACTORING: Review from here

    private boolean equipRifleWand(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack wand = new ItemStack(Material.STICK, 1);
            ItemMeta meta = wand.getItemMeta();
            meta.setDisplayName("RifleWand");
            wand.setItemMeta(meta);
            player.getInventory().addItem(wand);
        }
        return true;
    }

    @EventHandler
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED || event.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN)
            event.setCancelled(true);
    }




    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity().getPlayer();
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
        if (itemInMainHand != null && itemInMainHand.getItemMeta() != null) {
            String itemDisplayName = itemInMainHand.getItemMeta().getDisplayName();
            if (itemDisplayName.equals("RifleWand")) {
                if (event.getAction().equals(Action.LEFT_CLICK_AIR)
                        || event.getAction().equals(Action.LEFT_CLICK_BLOCK)
                ) {
                    shootRifleWand(event.getPlayer());
                }
            }

            if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if (event.getClickedBlock().getType().equals(Material.DIAMOND_BLOCK)) {
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
        } else {
            return Integer.valueOf(args[index]);
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

    private boolean createbow(CommandSender sender) {
        ItemStack bow = new ItemStack(Material.BOW, 1);
        ItemStack arrows = new ItemStack(Material.ARROW, 5);
        Player player = (Player) sender;
        player.getInventory().addItem(bow);
        player.getInventory().addItem(arrows);
        return true;
    }



    private boolean shootRifleWand(Player player) {
        getLogger().info("Drop.shootRifleWand()");
        Entity target = getNearestLivingEntityInSight(player, 20);
        if (target != null) {
            getLogger().info("Target: " + target.getName());
            if (target instanceof LivingEntity) {
                ((LivingEntity) target).damage(999999999, player);
            }
        }
        return true;
    }



    public Location putInView(CommandSender sender, int distance) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location location = player.getLocation().clone();
            directions direction = getDirection(sender);
            if (direction == directions.NORTH) {
                location.add(0, 0, -distance);
            } else if (direction == directions.EAST) {
                location.add(distance, 0, 0);
            } else if (direction == directions.SOUTH) {
                location.add(0, 0, distance);
            } else if (direction == directions.WEST) {
                location.add(-distance, 0, 0);
            } else {
                getLogger().info("Error: putInView()");
                return null;
            }
            return location;
        }
        return null;
    }

    private boolean Hulkazivota2(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location playerLocation = player.getLocation();
            for (int x = -2; x <= 2; x++) {
                for (int y = 0; y <= 2; y++) {
                    for (int z = -2; z <= 2; z++) {
                        final Location wallBlock = new Location(
                                player.getWorld(),
                                playerLocation.getX() + x,
                                playerLocation.getY() + y,
                                playerLocation.getZ() + z);
                        wallBlock.getBlock().setType(Material.AIR);
                    }
                }
            }
        }
        return true;
    }



    public directions getDirection(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            int rotation = Math.round(player.getLocation().getYaw() + 270) % 360;
            if (rotation >= 45 && rotation < 135) {
                return directions.NORTH;
            } else if (rotation >= 135 && rotation < 225) {
                return directions.EAST;
            } else if (rotation >= 225 && rotation < 315) {
                return directions.SOUTH;
            } else {
                return directions.WEST;
            }
        }
        return null;
    }

    private boolean buildObelisk(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location playerLocation = player.getLocation().clone();
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
            // REFACTORING: Raise an exception if count is negative
            // REFACTORING: Raise an exception if radius is zero or negative
            for (int i = 0; i < count; i++) {
                int randomX = ThreadLocalRandom.current().nextInt(-radius, radius);
                int randomZ = ThreadLocalRandom.current().nextInt(-radius, radius);
                final Location dummyLocation = new Location(
                        player.getWorld(),
                        playerLocation.getX() + randomX,
                        playerLocation.getY(),
                        playerLocation.getZ() + randomZ);
                Zombie dummy = player.getWorld().spawn(dummyLocation, Zombie.class);
            }
        }
        return true;
    }

    public Entity getNearestLivingEntityInSight(Player player, int range) {
        ArrayList<Entity> nearbyEntities = (ArrayList<Entity>) player.getNearbyEntities(range, range, range);
        ArrayList<Entity> nearbyLivingEntities = new ArrayList<Entity>();
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

    private enum directions {
        NORTH,
        EAST,
        SOUTH,
        WEST
    }

    public interface ItemAdd {
        void add(Player player);
    }
}
