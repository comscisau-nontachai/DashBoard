package com.strubber.dashboard.adapter

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import com.strubber.dashboard.BillCheckActivity
import com.strubber.dashboard.R
import com.strubber.dashboard.ReceiveProductActivity
import com.strubber.dashboard.ShippingActivity
import com.strubber.dashboard.model.App

class ItemAdapter(val context:Context,val list:List<App>,val step :Int,val recyclerView:RecyclerView) : RecyclerView.Adapter<ItemAdapter.ViewHolder>(){

    lateinit var snapHelper: SnapHelper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_adapter,parent,false)
        snapHelper = GravitySnapHelper(Gravity.START)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int = list.size  //return mApps.size() == 0 ? 0 : Integer.MAX_VALUE;

    override fun onBindViewHolder(holder: ItemAdapter.ViewHolder, position: Int) {
        //App app = mApps.get(position % mApps.size());
        //final App app = mApps.get(position);

        holder.imageView.setImageResource(list[position].drawable)
        holder.txtTitle.text = list[position].name

        snapHelper.attachToRecyclerView(recyclerView)

        var lastTimeClick:Long = 0
        holder.cardView.setOnClickListener {
            if(SystemClock.elapsedRealtime() - lastTimeClick < 1000)
                return@setOnClickListener
            lastTimeClick = SystemClock.elapsedRealtime()


            when(step){
                0 ->{//department 1
                    when(position){
                        0 ->{//shipping date
                            val intent = Intent(context,ShippingActivity::class.java).apply {
                                putExtra("TITLE_NAME",list[position].name)
                            }
                            context.startActivity(intent)
                        }
                        1 ->{//receive product
                            val intent = Intent(context,ReceiveProductActivity::class.java).apply {
                                putExtra("TITLE_NAME",list[position].name)
                            }
                            context.startActivity(intent)
                        }
                        2 ->{//bill check
                            val intent = Intent(context,BillCheckActivity::class.java).apply {
                                putExtra("TITLE_NAME",list[position].name)
                            }
                            context.startActivity(intent)
                        }
                    }
                }
            }
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<ImageView>(R.id.img_select_item)
        val txtTitle = itemView.findViewById<TextView>(R.id.txt_title_item)
        val cardView = itemView.findViewById<CardView>(R.id.card_view_item)
    }
}