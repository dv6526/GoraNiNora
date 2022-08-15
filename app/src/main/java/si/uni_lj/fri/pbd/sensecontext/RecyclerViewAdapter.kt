package si.uni_lj.fri.pbd.sensecontext

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import si.uni_lj.fri.pbd.sensecontext.data.rules.MatchedRule
import si.uni_lj.fri.pbd.sensecontext.fragments.WarningsFragment
import si.uni_lj.fri.pbd.sensecontext.ui.DetailsActivity
import si.uni_lj.fri.pbd.sensecontext.ui.MainViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RecyclerViewAdapter(mViewModel: MainViewModel, fragmentRef: WarningsFragment, contextRef: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemsList: List<MatchedRule>? = null
    private var mViewModel = mViewModel
    private var fragment = fragmentRef
    private var context = contextRef
    private var selected_position: Int = 0
    fun setItemList(items: List<MatchedRule>?) {
        // filtriraj data
        itemsList = items?.let { filterData(it) }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (itemsList == null) 4 else itemsList!!.size+4
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun  onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            0 -> {
                //opozorilo aplikacija je zaznala hojo v hribe
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_hiking_warning, parent, false)
                return ViewHolderWarningHiking(view)
            }
            1 -> {
                //switch nahajam se v hribih
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_switch, parent, false)
                return ViewHolderSwitch(view)
            }
            2 -> {
                //izberi območje
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_dropdown, parent, false)
                return ViewHolderDropdown(view)

            }
            3-> {
                //legenda
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_legend, parent, false)
                return ViewHolderLegend(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_grid_item, parent, false)
                return ViewHolderWarning(view)
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        when (holder.itemViewType) {
            0 -> {
                val holder0: ViewHolderWarningHiking = holder as ViewHolderWarningHiking
                if (!mViewModel.user_hiking?.value!!) {
                    holder0.card.visibility = View.GONE
                } else {
                    holder0.card.visibility = View.VISIBLE
                }
            }
            1 -> {
                val holder1: ViewHolderSwitch = holder as ViewHolderSwitch
                holder1.switch.setChecked(mViewModel.user_hiking?.value!!)

                holder1.switch.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        mViewModel.user_hiking!!.setValue(true)
                        fragment.startLocationUpdates()
                    } else {
                        mViewModel.user_hiking!!.setValue(false)
                        fragment.stopLocationUpdates()
                    }
                }
            }
            2 -> {
                val holder2: ViewHolderDropdown = holder as ViewHolderDropdown
                var areas = arrayOf("Izberi območje", "Južni in zahodni Julijci", "Osrednji Julijci in zahodne Karavanke", "Kamniško-Savinjske Alpe in V Karavanke")
                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(context, R.layout.selected_item, areas)
                adapter.setDropDownViewResource(R.layout.dropdown_item)
                val sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
                selected_position = sharedPreferences.getInt("selected_spinner_position", 0)
                holder2.spinner.adapter = adapter
                holder2.spinner.setSelection(selected_position, false)
                //holder2.spinner.setSelection(0, false)
                holder2.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {

                        val editor = sharedPreferences.edit()
                        editor.putInt("selected_spinner_position", position)
                        editor.commit()
                        selected_position = position
                        notifyItemRangeChanged(4, itemCount-4)

                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }


            }
            3 -> {

            }
            else -> {
                val item = itemsList!![position-4]
                val holder5: ViewHolderWarning = holder as ViewHolderWarning
                holder5.header.text = item.name
                holder5.desc.text = item.text

                val pattern1 = "dd-MM-yyyy"
                val pattern2 = "HH"
                val date1: String = SimpleDateFormat(pattern1).format(item.date)
                val date2: String = SimpleDateFormat(pattern2).format(item.date)
                holder5.date_text.text = date1 + " ob " + date2 + "ih"


                if (item.hiking)
                    holder5.image.setImageResource(R.drawable.ic_trenutno_opozorilo)
                else
                    holder5.image.setImageResource(R.drawable.ic_bell_solid)

                if (mViewModel.user_hiking?.value!! && item.hiking || !item.hiking  && item.area_id == selected_position+1) {
                    holder5.card.visibility = View.VISIBLE
                } else {
                    holder5.card.visibility = View.GONE
                }

                if (item.read) {
                    holder5.read.visibility = View.VISIBLE
                } else {
                    holder5.read.visibility = View.GONE
                }

                holder5.card.setOnClickListener {

                    val intent = Intent(context, DetailsActivity::class.java).apply {
                        putExtra("header", item.name)
                        putExtra("desc", item.text)
                    }

                    //označi, da je bilo praivlo prebrano
                    mViewModel.ruleRead(item.rule_id)
                    context.startActivity(intent)

                }
            }
        }



    }


    class ViewHolderWarning(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var header: TextView =  itemView.findViewById(R.id.main_text)
        var image: ImageView = itemView.findViewById(R.id.icon)
        var desc: TextView = itemView.findViewById(R.id.desc_text)
        var card: CardView = itemView.findViewById(R.id.card)
        var read: ImageView = itemView.findViewById(R.id.read)
        var date_text: TextView = itemView.findViewById(R.id.date_text)
    }

    class ViewHolderWarningHiking(itemView: View): RecyclerView.ViewHolder(itemView) {
        var card: CardView = itemView.findViewById(R.id.card)
    }

    class ViewHolderSwitch(itemView: View): RecyclerView.ViewHolder(itemView) {
        val switch: SwitchMaterial = itemView.findViewById(R.id.switch1)

    }

    class ViewHolderDropdown(itemView: View): RecyclerView.ViewHolder(itemView) {
        val spinner: Spinner = itemView.findViewById(R.id.spinner)
    }

    class ViewHolderLegend(itemView: View): RecyclerView.ViewHolder(itemView) {

    }

    fun filterData(items: List<MatchedRule>): List<MatchedRule> {
        val filtered: MutableList<MatchedRule> = arrayListOf()
        var filt1 = items.filter {
            it.hiking && !it.read
        }?.sortedBy {
            it.date
        }
        if (filt1 != null) {
            filtered.addAll(filt1)
        }

        var filt2 = items.filter {
            !it.hiking && !it.read
        }?.sortedBy {
            it.date
        }
        if (filt2 != null) {
            filtered.addAll(filt2)
        }

        var filt3 = items.filter {
            it.read
        }?.sortedBy {
            it.date
        }
        if (filt3 != null) {
            filtered.addAll(filt3)
        }


        return filtered.toList()
    }




}