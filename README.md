# ProxyChatSync🧬
Velocity, BungeeCord配下のサーバー間でのチャットを同期します。  
※現在はVelocityのみ対応しております。

Synchronize chats between servers under Velocity, BungeeCord.  
※Currently, only Velocity is supported.


# Usage
VelocityとBukkitサーバーの両方にプラグインのjarファイルを配置します。  
Place the plugin jar file on both the Velocity and Bukkit servers.  

# Configuration

```yaml
#
# Discordサーバーとチャットを同期する場合には以下の項目を編集してください。
# Edit the following items to synchronize chat with Discord server.
#
discord:
  # Discordとチャットを同期するかどうか。
  # Whether to sync chat with Discord
  enable: false
  # Discord Bot の token
  # Token of Discord Bot
  token: ''
  # 同期するチャンネルのID
  # ID of channels to sync
  channelId: '000000000000000000'
```