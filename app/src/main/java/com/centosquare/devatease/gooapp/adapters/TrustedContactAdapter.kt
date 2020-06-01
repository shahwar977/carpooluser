import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.centosquare.devatease.gooapp.R
import com.centosquare.devatease.gooapp.fragments.TripDetailsFragment
import com.centosquare.devatease.gooapp.models.TrustedContactModel
import java.util.ArrayList

class TrustedContactAdapter(private val context: Context,private val  trustedContactList:ArrayList<TrustedContactModel>) :
    RecyclerView.Adapter<TrustedContactAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrustedContactAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.trusted_contact_item_layout, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TrustedContactAdapter.MyViewHolder, position: Int) {


        //  holder.orderId.text = "Order # " + orderHistoryList[position].getOrderId()

        holder.contactNameTextView.text = trustedContactList[position].name
        holder.contactNumberTextView.text = trustedContactList[position].number



    }

    override fun getItemCount(): Int {
        return trustedContactList.size
    }

    inner class MyViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {

         internal var contactNameTextView: TextView
         internal var contactNumberTextView: TextView


        init {

                contactNameTextView = view.findViewById(R.id.contact_name_textview)
                contactNumberTextView = view.findViewById(R.id.contact_number_textview)
        }

    }


}
