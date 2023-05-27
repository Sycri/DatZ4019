package lv.lu.students.lk17235.datz4019.data

import android.net.Uri
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import lv.lu.students.lk17235.datz4019.data.model.OrderModel

private data class OrderFirebaseData(
    val userId: String,
    val name: String,
    val description: String,
    val photoFileName: String?
)

class OrderRepository {
    private val orderDocs = Firebase.firestore.collection("orders")
    private val orderStorage = Firebase.storage.reference.child("orders")

    suspend fun addOrder(order: OrderModel, photoUri: Uri?): Boolean {
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
                        name = order.name,
                        description = order.description,
                        photoFileName = order.photoFileName
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

    suspend fun updateOrder(order: OrderModel, photoUri: Uri?): Boolean {
        return try {
            if (order.photoFileName != null && photoUri != null && order.photoFileName != photoUri.lastPathSegment) {
                orderStorage
                    .child(order.photoFileName)
                    .putFile(photoUri)
                    .await()
            }

            orderDocs
                .document(order.id)
                .set(
                    OrderFirebaseData(
                        userId = order.userId,
                        name = order.name,
                        description = order.description,
                        photoFileName = order.photoFileName
                    )
                )
                .await()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getOrder(documentId: String): OrderModel? {
        return try {
            orderDocs
                .document(documentId)
                .get()
                .await()
                .toObject(OrderModel::class.java)
                ?.copy(id = documentId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getOrders(userId: String?, pageSize: Int, lastVisibleOrderId: String?): List<OrderModel> {
        return try {
            val querySnapshot = if (userId != null) {
                orderDocs
                    .whereEqualTo("userId", userId)
                    .orderBy("name")
                    .startAfter(lastVisibleOrderId)
                    .limit(pageSize.toLong())
                    .get()
                    .await()
            } else {
                orderDocs
                    .orderBy("name")
                    .startAfter(lastVisibleOrderId)
                    .limit(pageSize.toLong())
                    .get()
                    .await()

            }

            querySnapshot.documents.mapNotNull { doc ->
                val id = doc.id
                val userId = doc.getString("userId") ?: ""
                val name = doc.getString("name") ?: ""
                val description = doc.getString("description") ?: ""
                val photoFileName = doc.getString("photoFileName")

                OrderModel(id, userId, name, description, photoFileName)
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
