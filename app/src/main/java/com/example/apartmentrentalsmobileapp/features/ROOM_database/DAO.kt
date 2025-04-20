import androidx.room.*
import com.example.apartmentrentalsmobileapp.features.retailer.entity.Apartment

@Dao
interface CachedApartmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApartments(apartments: List<CachedApartment>)

    @Query("SELECT * FROM cached_apartments ORDER BY lastUpdated DESC LIMIT 10")
    suspend fun getLast10Apartments(): List<CachedApartment>

    @Query("DELETE FROM cached_apartments")
    suspend fun clearAll()
}