package com.example.decisionista.model

enum class DecisionMethod(
    val title: String,
    val description: String,
    val emoji: String
) {
    RANDOM("Scelta Casuale", "Il mago lancia i dadi del destino", "🎲"),
    ELIMINATION("Eliminazione", "Elimina opzioni una alla volta", "⚡️"),
    DUEL("Duello", "Le opzioni si sfidano a coppie", "⚔️"),
    WHEEL("Ruota Magica", "La ruota del destino decide", "🎡"),
    WEIGHTED("Scelta Ponderata", "Considera l'importanza di ogni opzione", "⚖️")
}
