package cz.artin.hackers;

import org.bukkit.entity.Player;

public class DropPlayer {
    private final Player player;

    public String name;
    public int score;
    public int deaths;

    DropPlayer(Player player) {
        this.player = player;
        this.name = player.getName();
        this.score = 0;
        this.deaths = 0;
    }

    public Player getPlayer() {
        return this.player;
    }
}
