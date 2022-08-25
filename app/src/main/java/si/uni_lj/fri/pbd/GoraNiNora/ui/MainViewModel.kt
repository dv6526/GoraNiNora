package si.uni_lj.fri.pbd.GoraNiNora.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import si.uni_lj.fri.pbd.GoraNiNora.Services.LocationUpdatesService
import si.uni_lj.fri.pbd.GoraNiNora.data.ApplicationDatabase
import si.uni_lj.fri.pbd.GoraNiNora.data.Repository
import si.uni_lj.fri.pbd.GoraNiNora.data.rules.MatchedRule
import java.util.*

class MainViewModel(application: Application?): AndroidViewModel(application!!) {
    var matchedRules: LiveData<List<MatchedRule>>
    var matchedRulesHistory: LiveData<List<MatchedRule>>
    var user_hiking: MutableLiveData<Boolean>? = null
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
        matchedRulesHistory = repository.matchedRulesHistory(cal1.time)
        user_hiking = repository.user_hiking
        repository.user_hiking.value = LocationUpdatesService.user_is_hiking
        //user_hiking?.value = LocationUpdatesService.user_is_hiking
    }

    fun ruleRead(ruleid: Long) {
        repository.updateMatchedRuleIsRead(ruleid)
    }


    override fun onCleared() {
        super.onCleared()
        user_hiking = null
    }






}