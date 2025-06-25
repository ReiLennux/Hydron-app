package com.undefined.hydron.domain.models

data class GenericCatalogModel(
    var id: String,
    var value: String,
)

abstract class BaseCatalogModel {
    abstract val id: String
    abstract val name: String
}

fun <T: BaseCatalogModel> List<T>.convertToGenericCatalog(): MutableList<GenericCatalogModel> {
    val list: MutableList<GenericCatalogModel> = mutableListOf()
    this.forEach { item ->
        list.add(GenericCatalogModel(id = item.id, value = item.name))
    }
    return list
}