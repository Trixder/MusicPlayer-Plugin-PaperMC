# Music Player Plugin – PaperMC Project [In Development]

This is a Minecraft server plugin built using the PaperMC API. It adds option to play custom songs saved in config file. The plugin aims to enhance player immersion by adding background music or special soundtracks for different events.

## Overview

The Music Player Plugin allows server admins to configure and play custom music tracks for all players or specific individuals (For now this can be done using the /sudo command). Songs can be triggered via commands, and the plugin includes options for looping, stopping, and listing available tracks.

## Features

- **Global commands** – Commands that allow you to use events for all players.
- **Song Looping** – Option to loop songs indefinitely.

## Commands

- **/musicreload** - Reloads the plugin’s configuration file.
- **/listmusic** - Displays a list of available songs.
- **/musicstop** - Stops the currently playing song for the player who used the command.
- **/musicstopall** - Stops all songs currently playing on the server.
- **/playmusic name loop-boolean** - Plays a song with a name for the player with the option to loop. The "loop-boolean" argument determines if the song should loop; enter "true" for looping or anything else for one replay only.
- **/playmusicall name loop-boolean** - Plays a specific song for all players on the server with the option to loop. The "loop-boolean" argument determines if the song should loop; enter "true" for looping or anything else for one replay only.

# Default Config
```yaml
songs:
  song-1:
    - note:
      type: "entity.villager.yes"
      volume: 1
      pitch: 0.707107
      delay: 10
    - note:
      type: "entity.villager.yes"
      volume: 1
      pitch: 0.943874
      delay: 10
    - note:
      type: "entity.villager.yes"
      volume: 1
      pitch: 0.707107
      delay: 10
  song-2:
    - note:
      type: "entity.villager.yes"
      volume: 1
      pitch: 0.707107
      delay: 10
    - note:
      type: "entity.villager.yes"
      volume: 1
      pitch: 0.943874
      delay: 10
    - note:
      type: "entity.villager.yes"
      volume: 1
      pitch: 0.707107
      delay: 10
```
