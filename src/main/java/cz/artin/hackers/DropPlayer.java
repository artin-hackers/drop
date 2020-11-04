package cz.artin.hackers;

import org.bukkit.entity.Player;

public class DropPlayer {
    private final Player player;
    private int kills;
    private int deaths;

    DropPlayer(Player player) {
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

    public int getDeaths() {
        return this.deaths;
    }

    public void addKill() {
        this.kills++;
    }

    public void addDeath() {
        this.deaths++;
    }
}
