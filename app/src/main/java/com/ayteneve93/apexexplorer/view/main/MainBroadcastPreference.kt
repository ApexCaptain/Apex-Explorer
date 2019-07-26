package com.ayteneve93.apexexplorer.view.main

object MainBroadcastPreference {

    object MainToFragment {
        object Action {
            const val FRAGMENT_SELECTED = "view#main#MainBroadcastPreference#MainToFragment#Action#FRAGMENT_SELECTED"
            const val FRAGMENT_UNSELECTED = "view#main#MainBroadcastPreference#MainToFragment#Action#FRAGMENT_UNSELECTED"
            const val BACK_BUTTON_PRESSED = "view#main#MainBroadcastPreference#MainToFragment#Action#BACK_BUTTON_PRESSED"
            const val PATH_CLICKED = "view#main#MainBroadcastPreference#MainToFragment#Action#PATH_CLICKED"
        }
        object Who {
            const val KEY = "view#main#MainBroadcastPreference#MainToFragment#Who#KEY"
            object Values {
                const val FILE_LIST = "view#main#MainBroadcastPreference#MainToFragment#Who#Values#FILE_LIST"
                const val FAVORITE = "view#main#MainBroadcastPreference#MainToFragment#Who#Values#FAVORITE"
            }
        }
        object Path {
            const val KEY = "view#main#MainBroadcastPreference#MainToFragment#Path#KEY"
        }
    }

    object FragmentToFragment {
        object Action {
            const val FAVORITE_LIST_CHANGED = "view#main#MainBroadcastPreference#FragmentToFragment#Action#FAVORITE_LIST_CHANGED"
            const val FAVORITE_LIST_EMPTY = "view#main#MainBroadcastPreference#FragmentToFragment#Action#FAVORITE_LIST_EMPTY"
        }
        object Who {
            const val KEY = "view#main#MainBroadcastPreference#FragmentToFragment#Who#Key"
            object Values {
                const val FILE_LIST = "view#main#MainBroadcastPreference#FragmentToFragment#Who#Values#FILE_LIST"
                const val FAVORITE = "view#main#MainBroadcastPreference#FragmentToFragment#Who#Values#FAVORITE"
            }
        }
    }

    object FragmentToAll {
        object Action {
            const val FAVORITE_ITEM_SELECTED = "view#main#MainBroadcastPreference#FragmentToAll#Action#FAVORITE_ITEM_SELECTED"
        }
        object FilePath {
            const val Key = "view#main#MainBroadcastPreference#FragmentToAll#FilePath#Key"
        }
    }

}