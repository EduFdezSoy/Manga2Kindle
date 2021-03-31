package es.edufdezsoy.manga2kindle.data.model

data class Status(
    var id: Int,
    var status: Int
) {
    companion object {
        const val LOCAL_QUEUE = 1
        const val REGISTERED = 10
        const val UPLOADING = 20
        const val UPLOADED = 30
        const val ENQUEUED = 40
        const val PROCESSING = 50
        const val CONVERTING = 60
        const val SENDING = 70
        const val DONE = 80
        const val ERROR = 90
    }
}