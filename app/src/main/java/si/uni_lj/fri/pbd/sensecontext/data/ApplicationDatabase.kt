package si.uni_lj.fri.pbd.sensecontext.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import si.uni_lj.fri.pbd.sensecontext.JsonObjects.Bulletin.Danger
import si.uni_lj.fri.pbd.sensecontext.data.bulletin.AvalancheBulletin
import si.uni_lj.fri.pbd.sensecontext.data.bulletin.DangerBulletin
import si.uni_lj.fri.pbd.sensecontext.data.bulletin.PatternBulletin
import si.uni_lj.fri.pbd.sensecontext.data.bulletin.ProblemBulletin
import si.uni_lj.fri.pbd.sensecontext.data.rules.DangerRule
import si.uni_lj.fri.pbd.sensecontext.data.rules.PatternRule
import si.uni_lj.fri.pbd.sensecontext.data.rules.ProblemRule
import si.uni_lj.fri.pbd.sensecontext.data.rules.Rule

@Database(entities = [WeatherHour::class, Location::class, Rule::class, WeatherDescription::class, DangerRule::class, PatternRule::class, ProblemRule::class, AvalancheBulletin::class, DangerBulletin::class, PatternBulletin::class, ProblemBulletin::class], version = 1, exportSchema = false)
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