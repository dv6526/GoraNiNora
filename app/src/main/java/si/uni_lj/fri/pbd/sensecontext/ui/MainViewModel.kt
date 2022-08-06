package si.uni_lj.fri.pbd.sensecontext.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import si.uni_lj.fri.pbd.sensecontext.data.ApplicationDatabase
import si.uni_lj.fri.pbd.sensecontext.data.Repository
import si.uni_lj.fri.pbd.sensecontext.data.rules.MatchedRule
import java.util.*

class MainViewModel(application: Application?): AndroidViewModel(application!!) {
    var matchedRules: LiveData<List<MatchedRule>>
    private val repository: Repository
    init {
        val db = ApplicationDatabase.getDatabase(application!!.applicationContext)
        val dao = db.dao()
        repository = Repository(dao)
        val cal1 = Calendar.getInstance()
        cal1.set(Calendar.HOUR_OF_DAY, 0)
        cal1.set(Calendar.MINUTE, 0)
        cal1.set(Calendar.SECOND, 0)
        cal1.set(Calendar.MILLISECOND, 0)
        matchedRules = repository.matchedRulesToday(cal1.time)
    }


}