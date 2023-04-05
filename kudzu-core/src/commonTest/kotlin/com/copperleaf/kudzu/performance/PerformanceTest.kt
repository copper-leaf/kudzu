@file:OptIn(ExperimentalTime::class)

package com.copperleaf.kudzu.performance

import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime
import kotlin.time.toDuration

fun performanceTest(
    iterations: Int,
    warmups: Int,
    test: () -> Unit,
) {
    val histogram = Histogram(iterations, warmups)

    for (i in 1..(iterations + warmups)) {
        histogram.runAndMeasure(i, test)
    }

    histogram.print()
}

@OptIn(ExperimentalTime::class)
private class Histogram(
    val iterations: Int,
    val warmups: Int,
) {
    private val measurements: MutableList<Pair<Int, Duration>> = mutableListOf()

    inline fun runAndMeasure(iteration: Int, test: () -> Unit) {
        val time = measureTime(test)

        if (iteration > warmups) {
            measurements.add(iteration to time)
        }
    }

    fun print() {
        val allDurations = measurements.map { it.second }
        val allMillis = allDurations.map { it.inWholeMilliseconds }

        val totalDuration = allDurations.reduce { acc, d -> acc + d }
        val mean = totalDuration / allMillis.size
        val fastestRun = allDurations.minByOrNull { it }
        val slowestRun = allDurations.maxByOrNull { it }
        val median = allDurations[allMillis.size / 2]
        val varianceMicro = allDurations
            .map { it - mean } // subtract the mean
            .map { it.inWholeMicroseconds * it.inWholeMicroseconds } // square the result
            .average()
        val stdDeviationMicro = sqrt(varianceMicro)
        val stdDeviationDuration = stdDeviationMicro.toDuration(DurationUnit.MICROSECONDS)

        println(
            """
            |Total duration of ${allMillis.size} runs: $totalDuration
            |Mean test duration: $mean
            |Test duration spread: [$fastestRun, $median, $slowestRun]
            |standard deviation: $stdDeviationDuration
            """.trimMargin()
        )
    }
}
