import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CachedApartment::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cachedApartmentDao(): CachedApartmentDao
}

