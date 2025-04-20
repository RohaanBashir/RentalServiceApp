import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.apartmentrentalsmobileapp.features.retailer.entity.Apartment

@Entity(tableName = "cached_apartments")
data class CachedApartment(
    @PrimaryKey val id: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val areaSize: String = "",
    val rooms: String = "",
    val pricePerMonth: String = "",
    val ownerId: String = "",
    val lastUpdated: Long = System.currentTimeMillis() // For ordering
)

fun CachedApartment.toApartment(): Apartment {
    return Apartment(
        id = id,
        imageUrl = imageUrl,
        description = description,
        areaSize = areaSize,
        rooms = rooms,
        pricePerMonth = pricePerMonth,
        ownerId = ownerId,
        lastUpdated = lastUpdated
    )
}

fun List<CachedApartment>.toApartmentList(): List<Apartment> {
    return this.map { it.toApartment() }
}