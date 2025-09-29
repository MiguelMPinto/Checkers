package isel.tds.model

@JvmInline
value class Name(val value: String) {
    init { require( isValid(value) ) { "Invalid name $value" } }
    override fun toString() = value
    companion object {
        fun isValid(value: String) =
            value.isNotBlank() && value.all { it.isLetterOrDigit() }
    }
}
fun String.toName(): Name {
    require(Name.isValid(this)) { "Invalid name $this" }
    return Name(this)
}