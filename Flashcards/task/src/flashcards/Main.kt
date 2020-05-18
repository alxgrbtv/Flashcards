package flashcards

import java.io.File

data class Card(val term: String, val definition: String, var mistakes: Int = 0)

val cards = mutableListOf<Card>()
val log = mutableListOf<String>()

fun main(args: Array<String>) {
    val argsMap = getArgsMap(args)
    argsMap["-import"]?.let { import(it) }
    play()
    argsMap["-export"]?.let { export(it) }
}

fun play() {
    do {
        val action = log(readLine()!!)
        println(log("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):"))
        when (action) {
            "add" -> add()
            "remove" -> remove()
            "import" -> import(selectFile())
            "export" -> export(selectFile())
            "ask" -> ask()
            "log" -> saveLog()
            "hardest card" -> showHardestCard()
            "reset stats" -> resetStats()
            "exit" -> println(log("Bye bye!"))
            else -> println("Sorry, are you sure?")
        }
        println(log(""))
    } while (action != "exit")
}

fun add() {
    println(log("The card:"))
    val term = log(readLine()!!)
    if (cards.find { it.term == term } != null) {
        println(log("The card \"$term\" already exists."))
        return
    }
    println(log("The definition of the card:"))
    val definition = log(readLine()!!)
    if (cards.find { it.definition == definition } != null) {
        println(log("The definition \"$definition\" already exists."))
        return
    }
    cards.add(Card(term, definition, 0))
    println(log("The pair (\"$term\":\"$definition\") has been added."))
}

fun remove() {
    println(log("The card:"))
    val term = log(readLine()!!)
    val foundCard = cards.find { it.term == term }
    if (foundCard != null) {
        cards.remove(foundCard)
        println(log("The card has been removed."))
    } else {
        println(log("Can't remove \"$term\": there is no such card."))
    }
}

fun selectFile(): String {
    println(log("File name:"))
    return log(readLine()!!)
}

fun export(fileName: String) {
    File(fileName).createNewFile()
    File(fileName).writeText(cards.joinToString("\n") { "${it.term} / ${it.definition} / ${it.mistakes}" })
    val numberOfCards = cards.size
    println(log("$numberOfCards cards have been saved."))
}

fun import(fileName: String) {
    val file = File(fileName)
    if (file.exists()) {
        val lines = file.readLines()
        lines.forEach { line ->
            line.split(" / ").let {
                val importingCard = Card(it[0], it[1], it[2].toInt())
                val existingCard = cards.find { card -> card.term == importingCard.term }
                if (existingCard != null) cards.remove(existingCard)
                cards.add(importingCard)
            }
        }
        println(log("${lines.size} cards have been loaded."))
    } else {
        println(log("File not found"))
    }
}

fun ask() {
    println(log("How many times to ask?"))
    repeat(log(readLine()!!).toInt()) {
        val card = cards.random()
        println(log("Print the definition of \"${card.term}\":"))
        when (val answer = log(readLine()!!)) {
            card.definition -> println(log("Correct answer."))
            else -> {
                val cardWithCorrectAnswer = cards.find { it.definition == answer }
                val textEnd = cardWithCorrectAnswer?.let { ", you've just written the definition of \"${it.term}\"" } ?: ""
                println(log("Wrong answer. The correct one is \"${card.definition}\"$textEnd."))
                card.mistakes ++
            }
        }
    }
}

fun showHardestCard() {
    val hardestCard = cards.maxBy { it.mistakes }
    if (hardestCard == null || hardestCard.mistakes == 0) {
        println(log("There are no cards with errors."))
    } else {
        val hardestCards = cards.filter { it.mistakes == hardestCard.mistakes }.map(Card::term).joinToString()
        println(log("The hardest card is \"${hardestCards}\". You have ${hardestCard.mistakes} errors answering it."))
    }
}

fun resetStats() {
    cards.forEach { it.mistakes = 0 }
    println(log("Card statistics has been reset."))
}

fun log(line: String): String {
    log.add(line)
    return line
}

fun saveLog() {
    println("File name:")
    val fileName = readLine()!!
    File(fileName).createNewFile()
    File(fileName).writeText(log.joinToString("\n"))
    println("The log has been saved.")
}

private fun getArgsMap(args: Array<String>): MutableMap<String, String> {
    val argsMap = mutableMapOf<String, String>()
    for (i in args.indices step 2) {
        argsMap[args[i]] = args[i + 1]
    }
    return argsMap
}