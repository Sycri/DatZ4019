package lv.lu.students.lk17235.datz4019.data.model

import com.google.firebase.Timestamp

data class Order(
    val id: String?,
    val userId: String,
    val address: String,
    val comment: String,
    val pickupTime: Timestamp?,
    val photoFileName: String?,
    val createdAt: Timestamp?,
    val updatedAt: Timestamp?
)
