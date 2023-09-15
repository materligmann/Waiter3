package centmoinshuitstudio.waiter.Models

data class QuantityTable(val sections: ArrayList<QuantitySection>)
data class QuantitySection(val entries: ArrayList<QuantityItem>, val title: String)
data class  QuantityItem(val item: Item, var quantity: Int, var notes: ArrayList<String>?)