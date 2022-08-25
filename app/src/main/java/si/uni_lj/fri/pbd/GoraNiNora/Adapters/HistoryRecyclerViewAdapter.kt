package si.uni_lj.fri.pbd.GoraNiNora.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import si.uni_lj.fri.pbd.GoraNiNora.R
import si.uni_lj.fri.pbd.GoraNiNora.data.rules.MatchedRule
import si.uni_lj.fri.pbd.GoraNiNora.ui.DetailsActivity
import si.uni_lj.fri.pbd.GoraNiNora.ui.MainViewModel
import java.text.SimpleDateFormat

class HistoryRecyclerViewAdapter(mViewModel: MainViewModel, context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itemsList: List<MatchedRule>? = null
    private var mViewModel = mViewModel
    private var context = context

    fun setItemList(items: List<MatchedRule>?) {
        itemsList = items?.sortedBy { it.date }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        if (itemsList == null) {
            return 0
        } else
            return itemsList!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_grid_item, parent, false)
        return ViewHolderWarning(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = itemsList!![position]
        val holder: ViewHolderWarning = holder as ViewHolderWarning
        holder.header.text = item.name
        holder.desc.text = item.text
        val pattern1 = "dd-MM-yyyy"
        val pattern2 = "HH"
        val date1: String = SimpleDateFormat(pattern1).format(item.date)
        val date2: String = SimpleDateFormat(pattern2).format(item.date)
        holder.date_text.text = date1 + " ob " + date2 + "ih"

        if (item.hiking)
            holder.image.setImageResource(R.drawable.ic_trenutno_opozorilo)
        else
            holder.image.setImageResource(R.drawable.ic_bell_solid)

        if (item.read) {
            holder.read.visibility = View.VISIBLE
        } else {
            holder.read.visibility = View.GONE
        }

        holder.card.setOnClickListener {

            val intent = Intent(context, DetailsActivity::class.java).apply {
                putExtra("header", item.name)
                putExtra("desc", item.text)
            }

            //označi, da je bilo praivlo prebrano
            mViewModel.ruleRead(item.rule_id)
            context.startActivity(intent)

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



}