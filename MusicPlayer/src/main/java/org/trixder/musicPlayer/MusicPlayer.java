package org.trixder.musicPlayer;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public final class MusicPlayer extends JavaPlugin {
    static Map<org.bukkit.entity.Player, Player> MusicPlayers = new Hashtable<>();
    static Map<String, String> songs = new Hashtable<>();

    private void Load() {
        songs.clear();

        for (Song song: Player.loadedSongs.values()) {
            song.notes.clear();
        }

        Player.loadedSongs.clear();

        Path folderPath = Paths.get(getDataFolder().getAbsolutePath(), "songs");
        File folder = folderPath.toFile();

        //This part doesnt work on linux
        if (!folder.exists()){
            folder.mkdirs();
        }

        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".json")) {
                    songs.put(file.getName().replace(".json", ""), file.getAbsolutePath());
                }
            }
        }
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        Bukkit.getPluginManager().registerEvents(new PlayerEventListener(), this);
        PlayerEventListener.musicPlayer = this;
        Player.musicPlayer = this;

        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            Player song = new Player(player);
            MusicPlayer.MusicPlayers.put(player, song);
        }

        Load();

        getLogger().info("Started MusicPlayer");
    }

    @Override
    public void onDisable() {
        Player.ClearAll();
        getLogger().info("Stopping MusicPlayer");
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        switch (args.length) {
            case 2 -> {
                if (command.getName().equalsIgnoreCase("playmusic")) {
                    org.bukkit.entity.Player player = (org.bukkit.entity.Player) sender;

                    boolean loop = "true".equals(args[1]);

                    MusicPlayers.get(player).Play(args[0], loop);
                } else if (command.getName().equalsIgnoreCase("playmusicall")) {
                    boolean loop = "true".equals(args[1]);
                    Player.PlayAll(args[0], loop);
                }

                return true;
            }
            case 0 -> {
                if (command.getName().equalsIgnoreCase("musicreload")) {
                    this.reloadConfig();
                    Player.ClearAll();
                    Load();
                    sender.sendMessage("§aLogin config reloaded!");
                } else if (command.getName().equalsIgnoreCase("listmusic")) {
                    Player.ListSongs(sender);
                } else if (command.getName().equalsIgnoreCase("musicstop")) {
                    org.bukkit.entity.Player player = (org.bukkit.entity.Player) sender;
                    MusicPlayers.get(player).Clear();
                } else if (command.getName().equalsIgnoreCase("musicstopall")) {
                    Player.ClearAll();
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
