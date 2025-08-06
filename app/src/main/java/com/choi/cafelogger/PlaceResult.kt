package com.choi.cafelogger

data class NearbySearchResponse(
    val results: List<Result>,
    val status: String
)

data class Result(
    val name: String,
    val geometry: Geometry
)

data class Geometry(
    val location: LocationJson
)

data class LocationJson(
    val lat: Double,
    val lng: Double
)
