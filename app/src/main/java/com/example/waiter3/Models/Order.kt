package com.example.waiter3.Models

import java.util.Date

data class Orders(var entries: ArrayList<Order>)
data class Order(val id: String, val date: Date, val quantityItems: ArrayList<QuantityItem>, val table: Int?, var check: Boolean)



