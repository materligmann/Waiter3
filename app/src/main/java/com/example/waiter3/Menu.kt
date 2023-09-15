package centmoinshuitstudio.waiter.Models

data class Menu(val menuSections: ArrayList<MenuSection>)

data class  MenuSection(val title: String, val items: ArrayList<Item>)