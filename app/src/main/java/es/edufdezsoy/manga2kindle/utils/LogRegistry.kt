package es.edufdezsoy.manga2kindle.utils

class LogRegistry {
    val log = ArrayList<String>()

    companion object : SingletonHolder<LogRegistry, Void?>({
        LogRegistry()
    })
}
