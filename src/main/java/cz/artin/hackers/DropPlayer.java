package cz.artin.hackers;

import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class DropPlayer {
    private static final Logger LOGGER = Logger.getLogger(DropPlayer.class.getName());
    private final Player player;
    private int kills;
    private int deaths;

    DropPlayer(Player player) {
        LOGGER.finer("DropPlayer");
        this.player = player;
        this.kills = 0;
        this.deaths = 0;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getName() {
        return this.player.getName();
    }

    public int getKills() {
        return this.kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return this.deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void addKill() {
        this.kills++;
    }

    public void addDeath() {
        this.deaths++;
    }
}
