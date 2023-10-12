package com.example.quotify.Model

import com.example.quotify.Model.ResultRandom

data class AllQuotesData(
    val count: Int,
    val lastItemIndex: Int,
    val page: Int,
    val results: List<ResultRandom>,
    val totalCount: Int,
    val totalPages: Int
)