package com.centosquare.devatease.gooapp.fragments

import TrustedContactAdapter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.centosquare.devatease.gooapp.R
import com.centosquare.devatease.gooapp.dialogs.AddContactDialog
import com.centosquare.devatease.gooapp.models.ProfileModel
import com.centosquare.devatease.gooapp.models.TrustedContactModel
import com.centosquare.devatease.gooapp.utils.Constants
import com.centosquare.devatease.gooapp.utils.SharedManager
import com.centosquare.sochlay.sochlaypartner.activities.DeleteAddressDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class TrustedContactsFragment : Fragment(), View.OnClickListener {


    private lateinit var fragemntView: View
    private lateinit var toolbar: Toolbar
    private lateinit var addContactButton: ConstraintLayout
    private lateinit var trustedContactRecyclerView: RecyclerView
    private lateinit var adapter:TrustedContactAdapter

    private lateinit var firebaseDatabase: FirebaseDatabase
    private  var firebaseUser: FirebaseUser? = null
    private lateinit var trustedContactReference: DatabaseReference
    private lateinit var userId:String
    private lateinit var trustedContactModel: TrustedContactModel
    private lateinit var trustedContactListener: ValueEventListener


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        fragemntView = inflater.inflate(R.layout.fragment_trusted_contacts, container, false)


        // initialize all view components
        viewComponents()
        viewClickListners()
        initObjectsAndReferences()
        firebaseListeners()


        // for toolbar name
        var activityContext = activity!! as AppCompatActivity
        activityContext.setSupportActionBar(toolbar)
        toolbar.title = resources.getString(R.string.title_fragment_trusted_contacts)

        // for back button
        (activity as AppCompatActivity).getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).getSupportActionBar()!!.setDisplayShowHomeEnabled(true)

       // for back button transaction
        toolbar.setNavigationOnClickListener {

            activity!!.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,AccountSettingsFragment()).commit()

        }


        trustedContactReference.addValueEventListener(trustedContactListener)






        return fragemntView
    }

    fun viewComponents(){

        toolbar = fragemntView.findViewById(R.id.toolbar)
        addContactButton = fragemntView.findViewById(R.id.add_contact_layout)
        trustedContactRecyclerView = fragemntView.findViewById(R.id.trusted_contact_recycler_view)
        trustedContactRecyclerView.layoutManager = LinearLayoutManager(activity!!)


    }

    fun viewClickListners(){

        addContactButton.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {

        when(p0!!.id){

            /* R.id.btn_confirm_destination -> {

                 activity!!.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,ChooseRideFragment()).commit()

             }*/

            R.id.add_contact_layout -> {



                val addContactDialog = AddContactDialog(activity!!)
                addContactDialog.setCancelable(true)
                addContactDialog.show()

                val back = ColorDrawable(Color.WHITE)
                val inset = InsetDrawable(back, 50)
                val window = addContactDialog.getWindow()!!
                window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                window.setBackgroundDrawable(inset)

            }


        }

    }

    fun initObjectsAndReferences(){

        firebaseUser = FirebaseAuth.getInstance().currentUser
        userId = firebaseUser!!.uid
        firebaseDatabase = FirebaseDatabase.getInstance()
        trustedContactReference = firebaseDatabase.getReference(Constants.trustedContactReference).child(userId)




    }

    fun firebaseListeners() {

        trustedContactListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                var list:ArrayList<TrustedContactModel> = ArrayList()
                for(snapshot in dataSnapshot.children){


                   var  model:TrustedContactModel? = snapshot.getValue(TrustedContactModel::class.java)

                    list.add(model!!)

                }

                if(list.size >=5){

                    addContactButton.setClickable(false)
                }
                else{

                    addContactButton.setClickable(true)
                }

                adapter = TrustedContactAdapter(activity!!,list)
                trustedContactRecyclerView.setAdapter(adapter)





            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }







    }

}