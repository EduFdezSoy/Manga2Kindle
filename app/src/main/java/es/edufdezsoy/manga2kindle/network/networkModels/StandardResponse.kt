package es.edufdezsoy.manga2kindle.network.networkModels

class StandardResponse<T> (
    val page: Int?,
    val perPage: Int?,
    val totalItems: Int?,
    val totalPages: Int?,
    val items: Array<T>
) {
    constructor(items: Array<T>) : this(null, null, null, null, items)
}
