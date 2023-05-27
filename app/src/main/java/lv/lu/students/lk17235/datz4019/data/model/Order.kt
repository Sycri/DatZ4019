package lv.lu.students.lk17235.datz4019.data.model

data class Order(
    val id: String,
    val userId: String,
    val name: String,
    val description: String,
    val photoFileName: String?,
)
