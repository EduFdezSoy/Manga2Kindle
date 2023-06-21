package es.edufdezsoy.manga2kindle.network.adapters

data class PrepareQueryParamsAdapter(
    val propertyName: String,
    val value: String
) {
    var res = "($propertyName=\"$value\""

    fun add(propertyName: String, value: String) {
        res += " && $propertyName=\"$value\""
    }

    override fun toString(): String {
        res += ")"
        return res
    }
}