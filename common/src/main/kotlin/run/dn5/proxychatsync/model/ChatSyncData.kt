package run.dn5.proxychatsync.model

data class ChatSyncData(
    var uuid: String = "",
    var username: String = "",

    var message: String = "",
    var japanized: String = "",

    var prefix: String = "",
    var suffix: String = "",

    var server: String = ""
)