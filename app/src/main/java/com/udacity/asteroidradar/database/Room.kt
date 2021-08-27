package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.udacity.asteroidradar.utils.Constants
import java.util.*
import java.util.concurrent.Executors

@Dao
interface AsteroidsDao{
    @Query("Select * from asteroids")
    fun getAllAsteroids(): LiveData<List<AsteroidDatabaseEntity>>

    @Query("SELECT * FROM asteroids WHERE closeApproachDate >= :startDate AND closeApproachDate <= :endDate ORDER BY closeApproachDate ASC")
    fun getAsteroidsByDateRange(startDate: String, endDate: String): LiveData<List<AsteroidDatabaseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: AsteroidDatabaseEntity)

    @Query("DELETE FROM asteroids WHERE closeApproachDate < :today")
    abstract fun deleteAsteroidsFrom(today: String): Int
}

private val IO_EXECUTOR = Executors.newSingleThreadExecutor()
fun ioThread(f : () -> Unit) {
    IO_EXECUTOR.execute(f)
}

val INITIAL_DATA = arrayOf<AsteroidDatabaseEntity>(
    AsteroidDatabaseEntity(
        id=null,
        codename = "test",
        closeApproachDate = Date().toString(),
        absoluteMagnitude = 200.0,
        estimatedDiameter = 10000.0,
        relativeVelocity = 0.100,
        distanceFromEarth = 100000.0,
        isPotentiallyHazardous = true
    ),
    AsteroidDatabaseEntity(
        id=null,
        codename = "test2",
        closeApproachDate = Date().toString(),
        absoluteMagnitude = 200.0,
        estimatedDiameter = 10000.0,
        relativeVelocity = 0.100,
        distanceFromEarth = 100000.0,
        isPotentiallyHazardous = false
    )
)

@Database(entities = [AsteroidDatabaseEntity::class], version = 1)
abstract class AsteroidsDatabase: RoomDatabase(){
    abstract val asteroidsDao: AsteroidsDao

    companion object {

        private var INSTANCE: AsteroidsDatabase? = null
        @Synchronized fun getInstance(context: Context) : AsteroidsDatabase{
            return INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
        }

        private fun buildDatabase(context: Context): AsteroidsDatabase {
            //context.deleteDatabase(Constants.DATABASE_NAME)
            return Room.databaseBuilder(
                context.applicationContext,
                AsteroidsDatabase::class.java, Constants.DATABASE_NAME
            )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
//                        ioThread {
//                            getInstance(context).asteroidsDao.insertAll(*INITIAL_DATA)
//                        }

                    }
                })
                .fallbackToDestructiveMigration().build()
            }
    }
}


