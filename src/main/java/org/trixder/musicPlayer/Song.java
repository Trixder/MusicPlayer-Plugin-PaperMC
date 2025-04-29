package org.trixder.musicPlayer;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.yaml.snakeyaml.Yaml;

import java.util.*;

public class Song {
    static MusicPlayer musicPlayer;
    private final Player owner;
    private final List<BukkitTask> noteQueue = new ArrayList<BukkitTask>();
    private BukkitTask loop;

    public Song(Player owner) {
        this.owner = owner;
    }

    public static void PlayAll(String songName, boolean playInLoop) {
        for (Song song : MusicPlayer.MusicPlayers.values()) song.Play(songName, playInLoop);
    }

    public void Play(String songName, boolean playInLoop) {
        if (!musicPlayer.getConfig().contains("songs." + songName)) {
            owner.sendMessage("song not found");
            return;
        } else if (!noteQueue.isEmpty()) {
            owner.sendMessage("a song is already playing");
            return;
        }

        String yml = musicPlayer.getConfig().saveToString();

        Yaml yaml = new Yaml();
        Map<String, Object> obj = yaml.load(yml);

        obj = (Map<String, Object>) obj.get("songs");

        List<Map<String, Object>> notes = (List<Map<String, Object>>) obj.get(songName);

        int delay = 0;
        int delayToAdd = 0;

        for (Map<String, Object> note : notes) {
            Note noteObj = new Note();

            for (String noteKey : note.keySet()) {
                if (noteKey.equals("type")) noteObj.type = Sound.valueOf(String.valueOf(note.get(noteKey)));
                else if (noteKey.equals("volume")) noteObj.volume = Float.parseFloat(String.valueOf(note.get(noteKey)));
                else if (noteKey.equals("pitch")) noteObj.pitch = Float.parseFloat(String.valueOf(note.get(noteKey)));
                else if (noteKey.equals("delay")) delayToAdd = Integer.parseInt(String.valueOf(note.get(noteKey)));
            }

            noteQueue.add(new BukkitRunnable() {
                @Override
                public void run() {
                    owner.playSound(owner.getLocation(), noteObj.type, noteObj.volume, noteObj.pitch);
                    for (BukkitTask task : noteQueue) {
                        if (task.getTaskId() == this.getTaskId()) {
                            noteQueue.remove(task);
                            break;
                        }
                    }
                }
            }.runTaskLater(musicPlayer, delay));

            delay += delayToAdd;
        }

        if (playInLoop) {
            loop = new BukkitRunnable() {
                @Override
                public void run() {
                    Play(songName, true);
                }
            }.runTaskLater(musicPlayer, delay);
        }
    }

    public static void ListSongs(CommandSender sender) {
        sender.sendMessage("Songs:");

        String yml = musicPlayer.getConfig().saveToString();

        Yaml yaml = new Yaml();
        Map<String, Object> obj = yaml.load(yml);

        obj = (Map<String, Object>) obj.get("songs");

        for (String song : obj.keySet()) {
            sender.sendMessage(song);
        }
    }

    public static void ClearAll() {
        for (Song song : MusicPlayer.MusicPlayers.values()) song.Clear();
    }

    public void Clear() {
        if (loop != null) loop.cancel();

        for (BukkitTask task : noteQueue) {
            task.cancel();
        }

        noteQueue.clear();
    }
}
