package si.uni_lj.fri.pbd.sensecontext

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import si.uni_lj.fri.pbd.sensecontext.data.rules.MatchedRule
import si.uni_lj.fri.pbd.sensecontext.fragments.WarningsFragment
import si.uni_lj.fri.pbd.sensecontext.ui.MainViewModel

class RecyclerViewAdapter(mViewModel: MainViewModel, fragmentRef: WarningsFragment): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemsList: List<MatchedRule>? = null
    private var mViewModel = mViewModel
    private var fragment = fragmentRef
    private var showUserIsHiking: Boolean = false
    fun setItemList(items: List<MatchedRule>?) {
        itemsList = items
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

            }
            1 -> {
                val holder1: ViewHolderSwitch = holder as ViewHolderSwitch
                holder1.switch.setChecked(mViewModel.user_hiking?.value!!)

                holder1.switch.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        fragment.startLocationUpdates()
                    } else {
                        fragment.repository.user_hiking.postValue(false)
                        fragment.stopLocationUpdates()
                    }
                }
            }
            2 -> {

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

                holder5.image.setOnClickListener {
                    /*
                    val intent = Intent(item.context, DetailsActivity::class.java).apply {
                        putExtra("ID", itemsList!![position].idDrink)
                        putExtra("SEARCH", instantiatedFromSearch.toString())
                    }
                    item.context.startActivity(intent)

                     */
                }
            }
        }



    }


    class ViewHolderWarning(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var header: TextView =  itemView.findViewById(R.id.main_text)
        var image: ImageView = itemView.findViewById(R.id.icon)
        var desc: TextView = itemView.findViewById(R.id.desc_text)
        var date_text: TextView = itemView.findViewById(R.id.date_text)
    }

    class ViewHolderWarningHiking(itemView: View): RecyclerView.ViewHolder(itemView) {

    }

    class ViewHolderSwitch(itemView: View): RecyclerView.ViewHolder(itemView) {
        val switch: SwitchMaterial = itemView.findViewById(R.id.switch1)

    }

    class ViewHolderDropdown(itemView: View): RecyclerView.ViewHolder(itemView) {

    }

    class ViewHolderLegend(itemView: View): RecyclerView.ViewHolder(itemView) {

    }



}