package com.ayteneve93.apexexplorer.view.main

object MainBroadcastPreference {

    object MainToFragment {
        object Action {
            const val FRAGMENT_SELECTED = "view#main#MainBroadcastPreference#MainToFragment#Action#FRAGMENT_SELECTED"
            const val FRAGMENT_UNSELECTED = "view#main#MainBroadcastPreference#MainToFragment#Action#FRAGMENT_UNSELECTED"
        }
        object Who {
            const val KEY = "view#main#MainBroadcastPreference#MainToFragment#Who#KEY"
            object Values {
                const val FILE_LIST = "view#main#MainBroadcastPreference#MainToFragment#Who#Values#FILE_LIST"
                const val FAVORITE = "view#main#MainBroadcastPreference#MainToFragment#Who#Values#FAVORITE"
            }
        }
    }

}