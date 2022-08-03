package si.uni_lj.fri.pbd.sensecontext.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [WeatherHour::class, Location::class, Rule::class, WeatherDescription::class, DangerRule::class, PatternRule::class, ProblemRule::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ApplicationDatabase: RoomDatabase() {

    abstract fun dao(): DatabaseDao

    companion object {
        private var INSTANCE: ApplicationDatabase? = null

        fun getDatabase(context: Context): ApplicationDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ApplicationDatabase::class.java,
                    "weather_database"
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                return instance
            }
        }
    }

}