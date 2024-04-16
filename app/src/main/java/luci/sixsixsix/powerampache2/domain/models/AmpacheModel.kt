package luci.sixsixsix.powerampache2.domain.models

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
            // remove repeating items
            mainList.forEach { mainListItem ->
                val id = mainListItem.id
                if (mappedToAppend.containsKey(id)) {
                    listToAppend.remove(mappedToAppend[id])
                }
            }
            mainList.addAll(listToAppend)
        }
    }
}