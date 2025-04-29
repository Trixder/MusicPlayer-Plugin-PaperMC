package org.trixder.musicPlayer;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Hashtable;
import java.util.Map;

public final class MusicPlayer extends JavaPlugin {
    static Map<Player, Song> MusicPlayers = new Hashtable<Player, Song>();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        Bukkit.getPluginManager().registerEvents(new PlayerEventListener(), this);
        PlayerEventListener.musicPlayer = this;
        Song.musicPlayer = this;

        for (Player player : Bukkit.getOnlinePlayers()) {
            Song song = new Song(player);
            MusicPlayer.MusicPlayers.put(player, song);
        }

        getLogger().info("Starting MusicPlayer");
    }

    @Override
    public void onDisable() {
        Song.ClearAll();
        getLogger().info("Stopping MusicPlayer");
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        switch (args.length) {
            case 2 -> {
                if (command.getName().equalsIgnoreCase("playmusic")) {
                    Player player = (Player) sender;

                    boolean loop = "true".equals(args[1]);

                    MusicPlayers.get(player).Play(args[0], loop);
                } else if (command.getName().equalsIgnoreCase("playmusicall")) {
                    boolean loop = "true".equals(args[1]);
                    Song.PlayAll(args[0], loop);
                }

                return true;
            }
            case 0 -> {
                if (command.getName().equalsIgnoreCase("musicreload")) {
                    this.reloadConfig();
                    sender.sendMessage("§aLogin config reloaded!");
                } else if (command.getName().equalsIgnoreCase("listmusic")) {
                    Song.ListSongs(sender);
                } else if (command.getName().equalsIgnoreCase("musicstop")) {
                    Player player = (Player) sender;
                    MusicPlayers.get(player).Clear();
                } else if (command.getName().equalsIgnoreCase("musicstopall")) {
                    Song.ClearAll();
                }

                return true;
            }
            default -> {
                sender.sendMessage("§aCommand with this amount of arguments is not recognized.");
                return true;
            }
        }
    }
}
