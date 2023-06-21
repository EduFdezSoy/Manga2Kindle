package es.edufdezsoy.manga2kindle.data.model

data class Status(
    var id: String,
    var status: String,
    var percentage: Int,
) {
    companion object {
        const val REGISTERED = "registered"
        const val UPLOADING = "uploading"
        const val AWAITING = "awaiting"
        const val PROCESSING = "processing"
        const val CONVERTING = "converting"
        const val SENDING = "sending"
        const val SENT = "sent"
        const val DONE = "done"
        const val ERROR = "error"
    }
}