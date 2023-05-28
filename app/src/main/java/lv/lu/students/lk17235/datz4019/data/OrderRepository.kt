package lv.lu.students.lk17235.datz4019.data

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import lv.lu.students.lk17235.datz4019.data.model.Order

private data class OrderFirebaseData(
    val userId: String = "",
    val address: String = "",
    val comment: String = "",
    val pickupTime: Timestamp? = null,
    val photoFileName: String? = null,
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

class OrderRepository {
    private val orderDocs = Firebase.firestore.collection("orders")
    private val orderStorage = Firebase.storage.reference.child("orders")

    suspend fun addOrder(order: Order, photoUri: Uri?): Boolean {
        return try {
            if (order.photoFileName != null && photoUri != null) {
                orderStorage
                    .child(order.photoFileName)
                    .putFile(photoUri)
                    .await()
            }

            orderDocs
                .add(
                    OrderFirebaseData(
                        userId = order.userId,
                        address = order.address,
                        comment = order.comment,
                        pickupTime = order.pickupTime,
                        photoFileName = order.photoFileName,
                        createdAt = null,
                        updatedAt = null
                    )
                )
                .await()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteOrder(id: String): Boolean {
        return try {
            orderDocs
                .document(id)
                .delete()
                .await()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateOrder(order: Order, photoUri: Uri?): Boolean {
        return try {
            val prevData = getOrder(order.id!!)!!

            if (prevData.photoFileName != order.photoFileName) {
                if (prevData.photoFileName != null) {
                    orderStorage
                        .child(prevData.photoFileName)
                        .delete()
                        .await()
                }

                if (order.photoFileName != null && photoUri != null) {
                    orderStorage
                        .child(order.photoFileName)
                        .putFile(photoUri)
                        .await()
                }
            }

            orderDocs
                .document(order.id)
                .update(
                    mapOf(
                        "userId" to order.userId,
                        "address" to order.address,
                        "comment" to order.comment,
                        "pickupTime" to order.pickupTime,
                        "photoFileName" to order.photoFileName,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getOrder(documentId: String): Order? {
        return try {
            orderDocs
                .document(documentId)
                .get()
                .await()
                .toObject(OrderFirebaseData::class.java)
                ?.let {
                    Order(
                        id = documentId,
                        userId = it.userId,
                        address = it.address,
                        comment = it.comment,
                        pickupTime = it.pickupTime,
                        photoFileName = it.photoFileName,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt
                    )
                }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getOrders(userId: String?, pageSize: Int, lastVisibleOrder: Order?): List<Order> {
        return try {
            val querySnapshot = if (userId != null) {
                orderDocs
                    .whereEqualTo("userId", userId)
                    .orderBy("createdAt")
                    .startAfter(
                        lastVisibleOrder?.createdAt
                    )
                    .limit(pageSize.toLong())
                    .get()
                    .await()
            } else {
                orderDocs
                    .orderBy("createdAt")
                    .startAfter(
                        lastVisibleOrder?.createdAt
                    )
                    .limit(pageSize.toLong())
                    .get()
                    .await()

            }

            querySnapshot.documents.mapNotNull {
                val id = it.id
                val userId = it.getString("userId") ?: ""
                val address = it.getString("address") ?: ""
                val comment = it.getString("comment") ?: ""
                val pickupTime = it.getTimestamp("pickupTime")
                val photoFileName = it.getString("photoFileName")
                val createdAt = it.getTimestamp("createdAt")
                val updatedAt = it.getTimestamp("updatedAt")

                Order(id, userId, address, comment, pickupTime, photoFileName, createdAt, updatedAt)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getOrderPhotoUri(photoFileName: String): Uri? {
        return try {
            orderStorage
                .child(photoFileName)
                .downloadUrl
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
