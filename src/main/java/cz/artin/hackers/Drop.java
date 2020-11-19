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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class Drop extends JavaPlugin implements Listener {
    private static final Logger LOGGER = Logger.getLogger(Drop.class.getName());
    private static final boolean DEBUG_STICK_ALLOWED = true;
    private static final List<DropPlayer> dropPlayers = new ArrayList<>();
    private static final List<ItemAdd> items = new ArrayList<>();
    private static final int DEFAULT_DUMMY_COUNT = 10;  // REFACTORING: Move to a sub-class
    private static final int DEFAULT_DUMMY_RADIUS = 10;  // REFACTORING: Move to a sub-class
    private static Location PORTAL_EXIT = null;  // REFACTORING: Move to a sub-class

    @Override
    public void onEnable() {
        LOGGER.info("Loading DROP plugin...");
        getServer().getPluginManager().registerEvents(this, this);

        if (DEBUG_STICK_ALLOWED) {
            items.add(new DebugStick(this));
        }
        items.add(new ZireaelSword(this));
        items.add(new FilipAxe(this));

        Objects.requireNonNull(getServer().getWorld("world")).setTime(1000);  // Development setup, remove in release version
        Objects.requireNonNull(getServer().getWorld("world")).setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);  // Development setup, remove in release version

        for (Player player : Bukkit.getOnlinePlayers()) {
            dropPlayers.add(new DropPlayer(player));
        }

        new BukkitRunnable() {
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Effect.addMana(player, Mana.Colour.BLUE, 1);
                    Effect.addMana(player, Mana.Colour.RED, 1);
                }
            }
        }.runTaskTimer(this, 20 * 5L, 20 * 10L);

        LOGGER.info("...plugin successfully loaded.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {  // REFACTORING: Review
        if (label.equalsIgnoreCase("setPortalExit")) {
            return setPortalExit(sender);
        } else if (label.equalsIgnoreCase("spawnDummies")) {
            return spawnDummies(sender, args);
        } else if (label.equalsIgnoreCase("teleport")) {
            return teleport(sender);
        } else if (label.equalsIgnoreCase("sethometown")) {
            getLogger().info("sethometown");
            return setHometown(sender);
        } else if (label.equalsIgnoreCase("buildObelisk")) {
            getLogger().info("buildObelisk()");
            return buildObelisk(sender);
        } else if (label.equalsIgnoreCase("createArena")) {
            return createArena(sender);
        } else if (label.equalsIgnoreCase("showscore")) {
            return showScore(sender);
        } else {
            return false;
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        LOGGER.info("A new player, " + event.getPlayer().getName() + ", just joined the fray.");
        dropPlayers.add(new DropPlayer(event.getPlayer()));
        event.getPlayer().setGameMode(GameMode.SURVIVAL);
        for (ItemAdd item : items) {
            item.add(event.getPlayer());
        }

        Zdenkovahulka(event.getPlayer());  // REFACTORING: Move to items
        createbow(event.getPlayer());  // REFACTORING: Move to items

        (new Mana()).add(event.getPlayer(), Mana.Colour.BLUE, 3);
        (new Mana()).add(event.getPlayer(), Mana.Colour.GREEN, 3);
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

        Zdenkovahulka(event.getPlayer());  // REFACTORING: Move to items
        createbow(event.getPlayer());  // REFACTORING: Move to items

        (new Mana()).add(event.getPlayer(), Mana.Colour.BLUE, 3);
        (new Mana()).add(event.getPlayer(), Mana.Colour.GREEN, 3);
    }

    // REFACTORING: Review from here

    private boolean showScore(CommandSender sender) {
        LOGGER.info("showScore");
        Player player = (Player) sender;
        if (player != null) {
            for (DropPlayer dropPlayer : dropPlayers) {
                sender.sendMessage(dropPlayer.getName() + ": " + dropPlayer.getKills() + "/" + dropPlayer.getDeaths());
            }
        }
        return true;
    }

    private boolean createArena(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location location = player.getLocation();
            for (int x = -50; x <= 50; x++) {
                for (int y = 0; y <= 50; y++) {
                    for (int z = -50; z <= 50; z++) {
                        Location blockLocation = new Location(
                                player.getWorld(),
                                location.getX() + x,
                                location.getY() + y,
                                location.getZ() + z);
                        blockLocation.getBlock().setType(Material.AIR);
                    }
                }
            }
            for (int x = -50; x <= 50; x++) {
                for (int z = -50; z <= 50; z++) {
                    Location blockLocation = new Location(
                            player.getWorld(),
                            location.getX() + x,
                            location.getY() - 1,
                            location.getZ() + z);
                    blockLocation.getBlock().setType(Material.GRASS_BLOCK);

                }
            }
            return true;
        } else {
            return false;
        }

    }

    private boolean Zdenkovahulka(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Player me = (Player) sender;
            ItemStack wand = new ItemStack(Material.BLAZE_ROD, 1);
            ItemMeta meta = wand.getItemMeta();
            meta.setDisplayName("Zdenkovahulka");
            wand.setItemMeta(meta);
            me.getInventory().addItem(wand);
        }

        return true;
    }

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
    public void onHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            LOGGER.info("Arrow hit something");
            setGroundFire(event.getEntity().getLocation(), 2);
        }
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
            if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if (itemInMainHand != null && itemInMainHand.getItemMeta() != null) {
                    if (itemInMainHand.getItemMeta().getDisplayName().equals("Zdenkovahulka")) {
                        Mana mana = new Mana();
                        if (mana.remove(event.getPlayer(), Mana.Colour.GREEN, 1)) {
                            creategauge(event.getPlayer());
                        } else {
                            event.getPlayer().sendMessage("no mana");
                        }
                    }
                }
            }
            if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if (itemInMainHand != null && itemInMainHand.getItemMeta() != null) {
                    if (itemInMainHand.getItemMeta().getDisplayName().equals("Zdenkovahulka")) {
                        Mana mana = new Mana();
                        createhole(event.getPlayer());
                        mana.add(event.getPlayer(), Mana.Colour.GREEN, 1);
                        Hulkazivota2(event.getPlayer());
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

    private boolean creategauge(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location playerLocation = player.getLocation();
            Location playergroundLocation = playerLocation.add(0, -1, 0);
            Material material = playergroundLocation.getBlock().getType();
            Set<Material> all_materials = new HashSet<>();
            all_materials.add(Material.GOLD_ORE);
            for (Material mat : Material.values()) {
                all_materials.add(mat);
            }
            List<Block> sight = player.getLineOfSight(all_materials, 10);
//            for (int i = 0; i < sight.size(); i++) {
//                if (i > sight.size() / 4) {
//                    sight.get(i).setType(material);
//                }
//            }
            Location wallCentre = sight.get(sight.size() - 1).getLocation();
            wallCentre.getBlock().setType(Material.GOLD_BLOCK);
            for (int x = -2; x <= 2; x++) {
                for (int y = -2; y <= 2; y++) {
                    for (int z = -2; z <= 2; z++) {
                        final Location wallBlock = new Location(
                                player.getWorld(),
                                wallCentre.getX() + x,
                                wallCentre.getY() + y,
                                wallCentre.getZ() + z);
                        wallBlock.getBlock().setType(material);
                    }
                }
            }
        }
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

    private boolean createhole(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            List<Block> sight = player.getLineOfSight(null, 20);
            Location holeCentre = sight.get(sight.size() - 1).getLocation();
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        final Location wallBlock = new Location(
                                player.getWorld(),
                                holeCentre.getX() + x,
                                holeCentre.getY() + y,
                                holeCentre.getZ() + z);
                        wallBlock.getBlock().setType(Material.AIR);
                    }
                }
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

    private boolean setGroundFire(Location location, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                final Location currentLocation = new Location(
                        location.getWorld(),
                        location.getX() + x,
                        location.getY(),
                        location.getZ() + z);
                currentLocation.getBlock().setType(Material.FIRE);
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

    private boolean teleport(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (PORTAL_EXIT != null) {
                player.teleport(PORTAL_EXIT);
            } else {
                player.teleport(player.getWorld().getSpawnLocation());
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
