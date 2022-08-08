package si.uni_lj.fri.pbd.sensecontext.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import si.uni_lj.fri.pbd.sensecontext.R
import si.uni_lj.fri.pbd.sensecontext.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {

    private var binding: ActivityDetailsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        val header = intent.getStringExtra("header")
        val text = intent.getStringExtra("desc")
        binding!!.textHeader.text = header
        binding!!.textDesc.text = text
        binding!!.buttonLayout.setOnClickListener({view ->
            super.onBackPressed()
        })

    }

}