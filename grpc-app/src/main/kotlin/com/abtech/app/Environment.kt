package com.abtech.app

enum class Environment(val value: String) {
    ACCEPTANCE("acceptance"),
    DEV("dev"),
    TEST("test"),
    STAGE("stage"),
    PROD("prod"),
}

fun parse(value: String): Environment = try {
    Environment.valueOf(value.uppercase())
} catch (ex: Exception) {
    throw RuntimeException("unknown environment $value. available envs=${Environment.values().map { it }}", ex)
}
