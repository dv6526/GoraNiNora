package si.uni_lj.fri.pbd.sensecontext

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import si.uni_lj.fri.pbd.sensecontext.data.rules.MatchedRule
import si.uni_lj.fri.pbd.sensecontext.fragments.WarningsFragment
import si.uni_lj.fri.pbd.sensecontext.ui.DetailsActivity
import si.uni_lj.fri.pbd.sensecontext.ui.MainViewModel

class RecyclerViewAdapter(mViewModel: MainViewModel, fragmentRef: WarningsFragment, contextRef: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemsList: List<MatchedRule>? = null
    private var mViewModel = mViewModel
    private var fragment = fragmentRef
    private var context = contextRef
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
                holder2.spinner.adapter = adapter

            }
            3 -> {

            }
            else -> {
                val item = itemsList!![position-4]
                val holder5: ViewHolderWarning = holder as ViewHolderWarning
                holder5.header.text = item.name
                holder5.desc.text = item.text

                if (item.hiking)
                    holder5.image.setImageResource(R.drawable.ic_trenutno_opozorilo)

                if (mViewModel.user_hiking?.value!! && item.hiking || !item.hiking) {
                    holder5.card.visibility = View.VISIBLE
                } else {
                    holder5.card.visibility = View.GONE
                }

                if (item.read) {
                    holder5.read.visibility = View.VISIBLE
                }

                holder5.card.setOnClickListener {

                    val intent = Intent(context, DetailsActivity::class.java).apply {
                        putExtra("header", item.name)
                        putExtra("desc", item.text)
                    }

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
        var linear_layour: LinearLayout = itemView.findViewById(R.id.layout_card)
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