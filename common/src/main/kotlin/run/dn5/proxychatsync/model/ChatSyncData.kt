package run.dn5.proxychatsync.model


class ChatSyncData(
    var uuid: String? = null,
    var username: String? = null,

    var message: String? = null,
    var japanized: String? = null,

    var prefix: String? = null,
    var suffix: String? = null,

    var server: String? = null
)