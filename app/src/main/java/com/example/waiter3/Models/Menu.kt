package com.example.waiter3.Models

data class Menu(val menuSections: ArrayList<MenuSection>)

data class  MenuSection(val title: String, val items: ArrayList<Item>)