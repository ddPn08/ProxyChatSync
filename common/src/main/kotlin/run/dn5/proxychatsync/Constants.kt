package run.dn5.proxychatsync

object Constants {
    const val CHANNEL_ID: String = "proxychatsync"
    const val CHANNEL_NAME: String = "message"
    const val CHANNEL_FULL: String = "${CHANNEL_ID}:${CHANNEL_NAME}"
    enum class SUB_P_TO_S(val channel: String){
        CHAT_SYNC("chatsync"),
        SERVER_SWITCH("serverswitch")
    }
    enum class SUB_S_TO_P(val channel: String) {
        CHAT_SYNC("chatsync"),
        PLAYER_JOIN("playerjoin"),
        VANISH_PLAYER_HIDE("vanish_playerhide"),
        VANISH_PLAYER_SHOW("vanish_playershow")
    }
}