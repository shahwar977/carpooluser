import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.centosquare.devatease.gooapp.R
import com.centosquare.devatease.gooapp.fragments.TripDetailsFragment
import com.centosquare.devatease.gooapp.models.LocationLatLng
import com.centosquare.devatease.gooapp.models.RideTypeModel
import com.centosquare.devatease.gooapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.ArrayList
import com.centosquare.devatease.gooapp.interfaces.RecyclerViewClickListener



class ChooseRideAdapter(private val context: Context,private val rideList:ArrayList<Int>,private val itemListener:RecyclerViewClickListener) :
    RecyclerView.Adapter<ChooseRideAdapter.MyViewHolder>() {

    private var selectedPos = 0
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var rideRef: DatabaseReference
    private  var firebaseUser: FirebaseUser? = null
    private lateinit var userId:String
    private lateinit var rideTypeModel: RideTypeModel





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseRideAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_choose_ride, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        initObjectsAndRefrences()
        when(position){


            0 -> {

                holder.imgvRide.setImageResource(R.drawable.ic_motor_bike)
                holder.tvRideName.text = context.resources.getString(R.string.tv_motor_bike)
                holder.tvRidePrice.text = "Rs:"+rideList[position].toString()+"/="







            }
            1 -> {
                holder.imgvRide.setImageResource(R.drawable.ic_auto)
                holder.tvRideName.text = context.resources.getString(R.string.tv_auto)
                holder.tvRidePrice.text = "Rs:"+rideList[position].toString()+"/="





            }
            2->{

                holder.imgvRide.setImageResource(R.drawable.ic_car)
                holder.tvRideName.text = context.resources.getString(R.string.tv_car)
                holder.tvRidePrice.text = "Rs:"+rideList[position].toString()+"/="



            }





        }
        holder.itemViewClick.setOnClickListener {


            selectedPos = position
            notifyDataSetChanged()



        }

        if (selectedPos == position){

            rideRef.child(userId).child(Constants.rideType)
            when(selectedPos){
                0 -> {

                    rideTypeModel = RideTypeModel("MotorBike",rideList[position],"Is simply dummy text of the printing and type setting industry")
                    rideRef.setValue(rideTypeModel)
                    itemListener.recyclerViewListClicked(holder.tvRideName.text.toString(),rideList[position])

                }
                1-> {
                    rideTypeModel = RideTypeModel("Auto",rideList[position],"Is simply dummy text of the printing and type setting industry")
                    rideRef.setValue(rideTypeModel)
                    itemListener.recyclerViewListClicked(holder.tvRideName.text.toString(),rideList[position])

                }
                2-> {

                    rideTypeModel = RideTypeModel("Car",rideList[position],"Is simply dummy text of the printing and type setting industry")
                    rideRef.setValue(rideTypeModel)
                    itemListener.recyclerViewListClicked(holder.tvRideName.text.toString(),rideList[position])
                }

            }

            holder.itemViewClick.setBackgroundColor(context.resources.getColor(R.color.background_curren_loc))

        }else{

            holder.itemViewClick.setBackgroundColor(context.resources.getColor(R.color.white))

        }


    }

    override fun getItemCount(): Int {
        return rideList.size
    }

    inner class MyViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {

         internal val imgvRide: ImageView
         internal val tvRideName: TextView
         internal val tvRidePrice: TextView
         internal val tvRideDesc: TextView
         internal val layoutRide:ConstraintLayout
         internal val itemViewClick:View


        init {

            imgvRide = view.findViewById(R.id.imgv_ride_type)
            tvRideName = view.findViewById(R.id.tv_label_ride_type_name)
            tvRidePrice = view.findViewById(R.id.tv_ride_type_price)
            tvRideDesc = view.findViewById(R.id.tv_ride_type_desc)
            layoutRide = view.findViewById(R.id.layout_ride_type)
            itemViewClick = view


        }



    }
    private fun initObjectsAndRefrences(){


        firebaseUser = FirebaseAuth.getInstance().currentUser
        userId = firebaseUser!!.uid
        firebaseDatabase = FirebaseDatabase.getInstance()
        rideRef = firebaseDatabase.getReference(Constants.ride).child(userId)
        //  destinationLocationRef = firebaseDatabase.getReference(Constants.rideLocation)


    }






}
