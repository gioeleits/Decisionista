package com.example.decisionista.model

// Import DecisionMethod from its new location
import com.example.decisionista.model.DecisionMethod

data class SavedDecision(
    val id: Int,
    val options: List<String>,
    val result: String,
    val method: DecisionMethod,
    val timestamp: Long
)
