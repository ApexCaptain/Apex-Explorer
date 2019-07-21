package com.ayteneve93.apexexplorer.view.main

enum class MainFragmentState {
    FILE_LIST, FAVORITE;
    companion object {
        fun getState(position : Int) : MainFragmentState = values().find { it.ordinal == position }!!
        fun getCount() : Int = values().size
    }
}