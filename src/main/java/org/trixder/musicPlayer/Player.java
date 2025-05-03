package org.trixder.musicPlayer;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Player {
    static MusicPlayer musicPlayer;
    private final org.bukkit.entity.Player owner;
    private final List<BukkitTask> noteQueue = new ArrayList<>();
    static Map<String, Song> loadedSongs = new Hashtable<>();
    private BukkitTask loop;

    public Player(org.bukkit.entity.Player owner) {
        this.owner = owner;
    }

    public static void PlayAll(String songName, boolean playInLoop) {
        for (Player player : MusicPlayer.MusicPlayers.values()) player.Play(songName, playInLoop);
    }

    /*public void Play(String songName, boolean playInLoop) {
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
    }*/

    public Song LoadSong(String path) throws IOException, ParseException {
        Song song = new Song();

        Object object = new JSONParser().parse(new FileReader(path));

        JSONObject jsonObject = (JSONObject) object;

        Object duration = jsonObject.get("duration");
        Object slow = jsonObject.get("slowModifier");

        song.duration = ToInt(duration);
        song.slow = ToInt(slow);

        JSONArray notesArray = (JSONArray) jsonObject.get("notes");
        List<Note> notes = new ArrayList<>();

        for (Object o : notesArray) {
            JSONObject noteObj = (JSONObject) o;
            Note note = new Note();
            note.type = Sound.valueOf(String.valueOf(noteObj.get("type")));
            note.volume = ToFloat(noteObj.get("volume"));
            note.pitch = ToFloat(noteObj.get("pitch"));
            note.delay = ToInt(noteObj.get("delay"));
            notes.add(note);
        }

        song.notes = notes;

        loadedSongs.put((String)jsonObject.get("name"), song);

        return song;
    }

    private int ToInt(Object obj) {
        if (obj instanceof Long) {
            return (int) ((long) obj);
        } else if (obj instanceof Integer) {
            return (int) obj;
        } else if (obj instanceof Double) {
            return (int) ((double) obj);
        } else if (obj instanceof Float) {
            return (int) ((float) obj);
        } else if (obj instanceof String) {
            return Integer.parseInt((String) obj);
        }

        return 0;
    }

    private float ToFloat(Object obj) {
        if (obj instanceof Long) {
            return (float) ((long) obj);
        } else if (obj instanceof Integer) {
            return (float) ((int) obj);
        } else if (obj instanceof Double) {
            return (float) ((double) obj);
        } else if (obj instanceof Float) {
            return (float) obj;
        } else if (obj instanceof String) {
            return Float.parseFloat((String) obj);
        }

        return 0;
    }

    public void Play(String songName, boolean playInLoop) {
        if (MusicPlayer.songs.isEmpty()) return;

        if (!MusicPlayer.songs.containsKey(songName)) {
            owner.sendMessage("song not found");
            return;
        } else if (!noteQueue.isEmpty()) {
            owner.sendMessage("a song is already playing");
            return;
        }

        Song song = loadedSongs.get(songName);

        if (song == null) {
            try {
                song = LoadSong(MusicPlayer.songs.get(songName));
            } catch (IOException | ParseException e) {
                owner.sendMessage(e.getMessage());
                owner.sendMessage("song couldn't load");
                return;
            }
        }

        long delay = 0;

        for (Note note : song.notes) {
            noteQueue.add(new BukkitRunnable() {
                @Override
                public void run() {
                    owner.playSound(owner.getLocation(), note.type, note.volume, note.pitch);
                    for (BukkitTask task : noteQueue) {
                        if (task.getTaskId() == this.getTaskId()) {
                            noteQueue.remove(task);
                            break;
                        }
                    }
                }
            }.runTaskLater(musicPlayer, delay));
            delay += (long) note.delay * song.slow;
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

    /*public static void ListSongs(CommandSender sender) {
        sender.sendMessage("Songs:");

        String yml = musicPlayer.getConfig().saveToString();

        Yaml yaml = new Yaml();
        Map<String, Object> obj = yaml.load(yml);

        obj = (Map<String, Object>) obj.get("songs");

        for (String song : obj.keySet()) {
            sender.sendMessage(song);
        }
    }*/

    public static void ListSongs(CommandSender sender) {
        sender.sendMessage("Songs:");

        for (String song : MusicPlayer.songs.keySet()) {
            sender.sendMessage(song);
        }
    }

    public static void ClearAll() {
        for (Player player : MusicPlayer.MusicPlayers.values()) player.Clear();
    }

    public void Clear() {
        if (loop != null) loop.cancel();

        for (BukkitTask task : noteQueue) {
            task.cancel();
        }

        noteQueue.clear();
    }
}
