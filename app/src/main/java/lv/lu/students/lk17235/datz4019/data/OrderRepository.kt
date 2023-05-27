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
    val photoName: String
)

class OrderRepository {

    private val orderDocs = Firebase.firestore.collection("orders")
    private val orderStorage = Firebase.storage.reference.child("orders")

    suspend fun addOrder(order: OrderModel, photoUri: Uri?): Boolean {
        return try {
            if (photoUri != null) {
                orderStorage
                    .child(order.photoName)
                    .putFile(photoUri)
                    .await()
            }

            orderDocs
                .add(
                    OrderFirebaseData(
                        userId = order.userId,
                        name = order.name,
                        description = order.description,
                        photoName = order.photoName
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
            if (photoUri != null && order.photoName != photoUri.lastPathSegment) {
                orderStorage
                    .child(order.photoName)
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
                        photoName = order.photoName
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
                doc.toObject(OrderModel::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getOrderPhotoUri(photoName: String): Uri? {
        return try {
            orderStorage
                .child(photoName)
                .downloadUrl
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}