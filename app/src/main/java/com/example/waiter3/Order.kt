package centmoinshuitstudio.waiter.Models

import java.time.LocalDateTime
import java.util.Date
import java.util.UUID

data class Orders(var entries: ArrayList<Order>)
data class Order(val id: String, val date: Date, val quantityItems: ArrayList<QuantityItem>, val table: Int?, var check: Boolean)



