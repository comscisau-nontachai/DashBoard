package com.strubber.dashboard.model

data class ShippingDataModel(val traceID:String, val order:String, val customerName:String, val shippingDate:String, val value:String,val diffDate:Int,val saleID:String)
data class CheckBillDataModel(val ivID :String,val no:String,val date:String,val cusName:String,val value:String,val upName:String,val tsCreate:String,val remark:String,val saleID:String)
data class ReceiveProductDataModel(val partID:String,val id:String,val partnumber:String,val billcard:String,val orderNumber:String,val customerName:String,
                                   val quantity:String,val tsName:String,val tsCreate:String,val func:String,val qtyRemain:String,val dateDiff:Int)
