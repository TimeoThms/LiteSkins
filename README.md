# LiteSkins
<img src="liteskins_banner_logo.png" alt="Plugin's banner" width=512/>

---
## Overview
LiteSkins is an alternative to SkinsRestorer. This plugin is made to be as simple and lightweight as possible.
Please note that this plugin **only works on Paper and its forks**.\
With LiteSkins you can:
- Restore players skins automatically from their names on servers where `online-mode` is disabled
- Reset, set skin from a Minecraft Account name or from a URL
- Change skin of other players

> [!WARNING]  
> This plugin had not been fully tested on a large scale.
> The plugin might not work in older versions, as it was created in 1.21. Don't hesitate to give it a try and let me know if there are any errors!

---
## Commands & Permissions
### 1. Commands
- `/skin <clear/username/URL>`: Clears or changes the skin of the player who executed the command
- `/skin <clear/username/URL> <player>`: Same thing but on another player
- `/liteskins`: Displays information about the plugin
- `/liteskins reload`: Reloads the plugin

### 2. Permissions
- `liteskins.restore`: Allows a player to have his skins restored automatically
- `liteskins.skin.username`: Allows /skin <clear/username>
- `liteskins.skin.url`: Allows /skin <clear/URL>
- `liteskins.skin.other`: Allows /skin <...> <player>
- `liteskins.skin.*`: Allows /skin <clear/username/URL> <player>
- `liteskins.bypassblacklist`: Allows the player to wear a blacklisted skin
- `liteskins.reload`: Allows the player to reload the plugin

---
## Plugin setup
### 1. Installation
Just drop `LiteSkins.jar` in your `plugins` folder!
### 2. Configuration
Here is the main configuration located it `plugins/LiteSkins/config.yml`\
```yml
restore-permission: false

mineskin:
  enabled: false
  api-key: ""

blacklist:
  enabled: false
  blacklisted-skins:
    - Notch
    - Jeb_
```
- `restore-permission`: If enabled, players will need to have 'liteskins.restore' permission to see their skin restored automatically.
- `mineskin`: LiteSkins uses MineSkin.org API to allow players to upload a skin from a URL with /skin <URL>. An API key is not required, but it is recommended, in order to increase the default limits on requests frequency. You can leave "api-key" empty, or paste your MineSkin API key (https://mineskin.org/apikey)
- `blacklist`: Allows you to prevent players from wearing certain skins

### 3. Messages
All messages and colors are customizable in `plugins/LiteSkins/messages.yml`. Default messages are:
```yml
prefix: "&6&l[LiteSkins]"
skin-command-usage: "&c/skin <clear/username/URL>"
skin-command-usage-other: "&c/skin <clear/username/URL> [player]"
skin-changed: "&aYour skin has been changed successfully."
skin-clear: "&aYour skin has been cleared successfully."
unknown-player: "&cThere is no player with this username!"
skin-change-error: "&cAn error occurred while trying to change your skin."
invalid-url: "&cThis URL or this image is not valid. The URL must point to a 64x64 PNG image."
blacklisted-skin: "&cThis skin is blacklisted."
permission-skin: "&cYou are not allowed to change your skin."
permission-url: "&cYou are not allowed to upload a skin from a URL."
permission-username: "&cYou are not allowed to change your skin from a premium username."
player-not-found: "&cThat player does not exist or is offline."
skin-changed-other: "&aYou changed %target% skin."
reload: "&aPlugin reloaded successfully!"
```

---
# Support
If you need any help, you can contact me on discord : `pseudo_original`