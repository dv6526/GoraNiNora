package si.uni_lj.fri.pbd.sensecontext

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
import si.uni_lj.fri.pbd.sensecontext.data.rules.MatchedRule

class RecyclerViewAdapter(private val itemLayout: Int): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    private var itemsList: List<MatchedRule>? = null
    fun setItemList(items: List<MatchedRule>?) {
        itemsList = items
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (itemsList == null) 0 else itemsList!!.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(itemLayout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemsList!![position]
        holder.header.text = item.name
        holder.desc.text = item.text

        if (item.hiking)
            holder.image.setImageResource(R.drawable.ic_trenutno_opozorilo)



        holder.image.setOnClickListener {
            /*
            val intent = Intent(item.context, DetailsActivity::class.java).apply {
                putExtra("ID", itemsList!![position].idDrink)
                putExtra("SEARCH", instantiatedFromSearch.toString())
            }
            item.context.startActivity(intent)

             */
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var header: TextView =  itemView.findViewById(R.id.main_text)
        var image: ImageView = itemView.findViewById(R.id.icon)
        var desc: TextView = itemView.findViewById(R.id.desc_text)
        var date_text: TextView = itemView.findViewById(R.id.date_text)

    }
}