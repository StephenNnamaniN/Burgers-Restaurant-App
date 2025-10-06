package com.stephennnamani.burgerrestaurantapp.feature.home.domain

enum class CustomDrawerState {
    Opened,
    Closed
}

fun CustomDrawerState.isOpened(): Boolean {
    return this == CustomDrawerState.Opened
}
fun CustomDrawerState.reverse(): CustomDrawerState {
    return if (this == CustomDrawerState.Opened) CustomDrawerState.Closed
    else CustomDrawerState.Opened
}