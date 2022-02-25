package phonebook

import java.io.File
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

fun timeConverter(time: Long): String {
    var timeString = (time / 60000).toString() + " min. "
    timeString += ((time % 60000) / 1000).toString() + " sec. "
    timeString += (time % 1000).toString() + " ms."
    return timeString
}

fun linearSearch(toFindList: List<String>, directory: List<String>): Pair<Int, Long> {
    var found = 0
    val startTime = System.currentTimeMillis()
    for (person in toFindList)
        for (entry in directory) {
            if (person in entry) {
                found++
                break
            }
        }
    val time = System.currentTimeMillis() - startTime
    return Pair(found, time)
}

fun bubbleSort(directory: List<String>, linearTime: Long): Triple<Boolean, Long, List<String>> {
    val sorted = directory.toMutableList()
    var timeout = false
    val startTime = System.currentTimeMillis()
    var lastIndex = directory.lastIndex
    while (lastIndex > 0) {
        var swapIndex = 0
        for (i in 0 until lastIndex) {
            if (sorted[i].substring(sorted[i].indexOf(" ")) > sorted[i + 1].substring(sorted[i + 1].indexOf(" "))) {
                sorted[i] = sorted[i + 1].also { sorted[i + 1] = sorted[i] }
                swapIndex = i
            }
        }
        lastIndex = swapIndex
        if (System.currentTimeMillis() - startTime > 10 * linearTime) {
            timeout = true
            break
        }
    }
    val time = System.currentTimeMillis() - startTime
    return Triple(timeout, time, sorted)
}

fun jumpSearch(person: String, sorted: List<String>): Boolean {
    val jump = sqrt(sorted.size.toDouble()).toInt()
    var index = -jump
    do {
        index = min(index + jump, sorted.lastIndex)
        if (person in sorted[index]) return true
        if (person < sorted[index].substring(sorted[index].indexOf(" ") + 1)) break
    } while (index < sorted.lastIndex)
    for (i in index - 1 downTo max(index - jump + 1, 1)) {
        if (person in sorted[i]) return true
    }
    return false
}

fun jumpSearchList(toFindList: List<String>, sortedDirectory: List<String>): Pair<Int, Long> {
    var found = 0
    val startTime = System.currentTimeMillis()
    for (person in toFindList) {
        if (jumpSearch(person, sortedDirectory)) found++
    }
    val time = System.currentTimeMillis() - startTime
    return Pair(found, time)
}

fun getData(): Pair<List<String>, List<String>> {
    val path = "C:/Users/calle/Downloads"
    val toFindList = File("$path/find.txt").readLines()
    val directory = File("$path/directory.txt").readLines()
    return Pair(toFindList, directory)
}

fun linear(toFindList: List<String>, directory: List<String>): Long {
    println("Start searching (linear search)...")
    val (found, time) = linearSearch(toFindList, directory)
    println("Found $found / ${toFindList.size} entries. Time taken: ${timeConverter(time)}")
    return time
}

fun bubble(toFindList: List<String>, directory: List<String>, linearTime: Long) {
    println("\nStart searching (bubble sort + jump search)...")
    val (timeout, sortTime, sortedDirectory) = bubbleSort(directory, linearTime)
    if (!timeout) {
        val (jfound, searchTime) = jumpSearchList(toFindList, sortedDirectory)
        println("Found $jfound / ${toFindList.size} entries. Time taken: ${timeConverter(sortTime + searchTime)}")
        println("Sorting time: ${timeConverter(sortTime)}")
        println("Searching time: ${timeConverter(searchTime)}")
    } else {
        val (found, time) = linearSearch(toFindList, directory)
        println("Found $found / ${toFindList.size} entries. Time taken: ${timeConverter(time + sortTime)}")
        println("Sorting time: ${timeConverter(sortTime)} - STOPPED, moved to linear search")
        println("Searching time: ${timeConverter(time)}")
    }
}

fun qSort(directory: List<String>): List<String> {
    if (directory.size < 2) return directory
    val left = emptyList<String>().toMutableList()
    val right = emptyList<String>().toMutableList()
    val pivot = directory.last()
    for (item in directory.dropLast(1)) {
        if (item.substring(item.indexOf(" ")) < pivot.substring(pivot.indexOf(" "))) {
            left.add(item)
        } else {
            right.add(item)
        }
    }
    return qSort(left) + listOf(pivot) + qSort(right)
}

fun binSearch(person: String, sortedDirectory: List<String>): Boolean {
    var left = 0
    var right = sortedDirectory.lastIndex
    while (left <= right) {
        val middle = (left + right) / 2
        if (person in sortedDirectory[middle]) return true
        if (person < sortedDirectory[middle].substring(sortedDirectory[middle].indexOf(" ") + 1)) {
            right = middle - 1
        } else {
            left = middle + 1
        }
    }
    return false
}

fun binSearchList(toFindList: List<String>, sortedDirectory: List<String>): Pair<Int, Long> {
    var found = 0
    val startTime = System.currentTimeMillis()
    for (person in toFindList) {
        if (binSearch(person, sortedDirectory)) found ++
    }
    val searchTime = System.currentTimeMillis() - startTime
    return Pair(found, searchTime)
}

fun quick(toFindList: List<String>, directory: List<String>) {
    println("\nStart searching (quick sort + binary search)...")
    val startTime = System.currentTimeMillis()
    val sortedDirectory = qSort(directory)
    val sortTime = System.currentTimeMillis() - startTime
    val (found, searchTime) = binSearchList(toFindList, sortedDirectory)
    println("Found $found / ${toFindList.size} entries. Time taken: ${timeConverter(sortTime + searchTime)}")
    println("Sorting time: ${timeConverter(sortTime)}")
    println("Searching time: ${timeConverter(searchTime)}")
}

fun hash(toFindList: List<String>, directory: List<String>) {
    println("\nStart searching (hash table)...")
    val startCreateTime = System.currentTimeMillis()
    val hashedDirectory = emptyMap<String, String>().toMutableMap()
    for (person in directory) {
        val (number, name) = person.split(" ", limit = 2)
        hashedDirectory[name] = number
    }
    val creatingTime = System.currentTimeMillis() - startCreateTime
    var found = 0
    val startSearchTime = System.currentTimeMillis()
    for (person in toFindList) {
        if (person in hashedDirectory.keys) found++
    }
    val searchTime = System.currentTimeMillis() - startSearchTime
    println("Found $found / ${toFindList.size} entries. Time taken: ${timeConverter(creatingTime + searchTime)}")
    println("Creating time: ${timeConverter(creatingTime)}")
    println("Searching time: ${timeConverter(searchTime)}")
}

fun main() {
    val (toFindList, directory) = getData()
//    val linearTime = linear(toFindList, directory)
    bubble(toFindList, directory, linear(toFindList, directory))
    quick(toFindList, directory)
    hash(toFindList, directory)
}