package luci.sixsixsix.powerampache2.domain.models

import luci.sixsixsix.mrlog.L

interface AmpacheModel {
    val id: String

    companion object {
        fun mapModel(list: List<AmpacheModel>) = LinkedHashMap<String, AmpacheModel>().apply {
            list.forEach {
                put(it.id, it)
            }
        }

        fun appendToList(listToAppend: MutableList<AmpacheModel>, mainList: MutableList<AmpacheModel>) {
            val mappedToAppend = mapModel(listToAppend)
            // remove repeating items = all items that are already in the main list
            mainList.forEach { mainListItem ->
                val id = mainListItem.id
                if (mappedToAppend.containsKey(id)) {
                    listToAppend.remove(mappedToAppend[id])
                    mappedToAppend.remove(id)
                }
            }
            mainList.addAll(listToAppend)
        }

        /**
         * only keep elements in common
         * list to append is the source of truth
         * main list determines the order
         */
        fun appendToListExclusive(newList: MutableList<AmpacheModel>, mainList: List<AmpacheModel>): List<AmpacheModel> {
            if (mainList.isEmpty()) return newList

            val mappedNewList = mapModel(newList)
            val resultList: MutableList<AmpacheModel> = mutableListOf()

            mainList.forEach { mainListItem ->
                // if the item in the main list is also present in the listToA
                mappedNewList[mainListItem.id]?.let {
                    resultList.add(it)
                    newList.remove(it)
                    mappedNewList.remove(it.id)
                }
            }
            // add the remaining not in common
            resultList.addAll(newList)
            return resultList
        }

        fun listsEqual(list1: List<AmpacheModel>, list2: List<AmpacheModel>): Boolean {
            if (list1.size != list2.size) return false
            list1.forEachIndexed { index, ampacheModel ->
                if(ampacheModel.id != list2[index].id) {
                    L("aaaa", "lists not equal")
                    return false
                }
            }
            return true
        }

        fun listsHaveSameElements(list1: List<AmpacheModel>, list2: List<AmpacheModel>): Boolean {
            if (list1.size != list2.size) return false
            val mappedList2 = mapModel(list2)

            list1.forEach { ampacheModel ->
                if(!mappedList2.containsKey(ampacheModel.id)) {
                    return false
                }
            }
            return true
        }
    }
}
