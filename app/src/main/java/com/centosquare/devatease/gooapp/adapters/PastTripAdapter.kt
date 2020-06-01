import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.centosquare.devatease.gooapp.R
import com.centosquare.devatease.gooapp.fragments.TripDetailsFragment
import com.centosquare.devatease.gooapp.models.UserPastRides
import kotlin.collections.ArrayList

class PastTripAdapter(
    private val context: Context,
    private val pastTripList: ArrayList<UserPastRides>
) :
    RecyclerView.Adapter<PastTripAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastTripAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_past_trip, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder:MyViewHolder, position: Int) {


        holder.riderNameTxtview.text = pastTripList.get(position).riderName
        holder.fareEstimatation.text = pastTripList.get(position).fare+"/="
        holder.vehicleNoTxtview.text = pastTripList.get(position).vehicleNo
        holder.rideTypeTxtview.text = pastTripList.get(position).rideType


    }

    override fun getItemCount(): Int {
        return pastTripList.size
    }

    inner class MyViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {

        internal var riderNameTxtview:TextView
        internal var fareEstimatation:TextView
        internal var vehicleNoTxtview:TextView
        internal var rideTypeTxtview:TextView

        init {


            riderNameTxtview = view.findViewById(R.id.tv_riderName_past_trip)
            rideTypeTxtview =  view.findViewById(R.id.tv_rideType_past_trip)
            fareEstimatation = view.findViewById(R.id.tv_price_past_trip)
            vehicleNoTxtview = view.findViewById(R.id.tv_vehicleNo_past_trip)


        }

    }


}
