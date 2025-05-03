package org.trixder.musicPlayer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEventListener implements Listener {
    static MusicPlayer musicPlayer;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = new Player(e.getPlayer());
        MusicPlayer.MusicPlayers.put(e.getPlayer(), player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        MusicPlayer.MusicPlayers.get(e.getPlayer()).Clear();
        MusicPlayer.MusicPlayers.remove(e.getPlayer());
        //Song.ClearAll();
    }
}
