package cz.artin.hackers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Hashtable;
import java.util.UUID;
import java.util.logging.Logger;

public class DropListener implements Listener {
    private static final Logger LOGGER = Logger.getLogger(DropListener.class.getName());
    private static Hashtable<UUID, DropPlayer> dropPlayers;

    DropListener(JavaPlugin plugin, Hashtable<UUID, DropPlayer> dropPlayers) {
        LOGGER.finer("DropListener");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        DropListener.dropPlayers = dropPlayers;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        LOGGER.info("onPlayerDeath");
        LOGGER.info("onPlayerDeath: Player " + event.getEntity().getPlayer().getName() + " died");
        DropPlayer dropPlayer = dropPlayers.get(event.getEntity().getPlayer().getUniqueId());
        dropPlayer.addDeath();
        LOGGER.info("onPlayerDeath: " + dropPlayer.getName() + ", " + dropPlayer.getKills() + "/" + dropPlayer.getDeaths());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        LOGGER.info("onPlayerRespawn");
        LOGGER.info("onPlayerDeath: Player " + event.getPlayer().getName() + " respawned");
    }
}
