package com.example.quotify.HttpHandler

data class TagQuotesData(
    val count: Int,
    val lastItemIndex: Int,
    val page: Int,
    val results: List<ResultTag>,
    val totalCount: Int,
    val totalPages: Int
)