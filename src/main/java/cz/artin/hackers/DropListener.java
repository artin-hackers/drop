package cz.artin.hackers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Hashtable;
import java.util.UUID;
import java.util.logging.Logger;

public class DropListener implements Listener {
    private static final Logger LOGGER = Logger.getLogger(DropListener.class.getName());
    private static final int LEVEL_RESTORE_DELAY = 1;
    private static JavaPlugin plugin;
    private static Hashtable<UUID, DropPlayer> dropPlayers;
    private static BukkitTask timer; // TODO: Change to array

    DropListener(JavaPlugin plugin, Hashtable<UUID, DropPlayer> dropPlayers) {
        LOGGER.finer("DropListener");
        DropListener.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        DropListener.dropPlayers = dropPlayers;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity().getPlayer();
        if (player != null) {
            LOGGER.info("onPlayerDeath: Player " + player.getName() + " died");
            DropPlayer dropPlayer = dropPlayers.get(player.getUniqueId());
            dropPlayer.addDeath();
            dropPlayer.setLevel(player.getLevel());
            LOGGER.info("onPlayerDeath: " + dropPlayer.getName() + ", level " + dropPlayer.getLevel() + ", " + dropPlayer.getKills() + "/" + dropPlayer.getDeaths());
            Player killer = player.getKiller();
            if (killer != null) {
                LOGGER.info("onPlayerDeath: Player " + killer.getName() + ", level " + killer.getLevel() + ", killed " + player.getName());
                DropPlayer dropKiller = dropPlayers.get(killer.getUniqueId());
                dropKiller.addKill();
                killer.setLevel(killer.getLevel() + 1);
                dropKiller.setLevel(killer.getLevel());
                LOGGER.info("onPlayerDeath: Killer " + dropKiller.getName() + ", level " + dropKiller.getLevel() + ", " + dropKiller.getKills() + "/" + dropKiller.getDeaths());
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        LOGGER.info("onPlayerRespawn: Player " + event.getPlayer().getName() + " respawned");
        Player player = event.getPlayer();
        DropPlayer dropPlayer = dropPlayers.get(player.getUniqueId());
        LOGGER.info("onPlayerRespawn: " + dropPlayer.getName() + ", level " + dropPlayer.getLevel() + ", " + dropPlayer.getKills() + "/" + dropPlayer.getDeaths());
        player.setLevel(dropPlayer.getLevel());

        timer = Bukkit.getScheduler().runTaskTimer(DropListener.plugin, () -> {
            Bukkit.getScheduler().cancelTask(timer.getTaskId());
            player.setLevel(dropPlayer.getLevel());
        }, LEVEL_RESTORE_DELAY * 20L, 1L);
    }
}
