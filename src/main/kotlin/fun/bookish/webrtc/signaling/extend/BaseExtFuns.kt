package `fun`.bookish.webrtc.signaling.extend

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
private val dateTimeFormatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
private val dateTimeFormatter3 = DateTimeFormatter.ofPattern("HH-mm-ss")

fun getNowDate():String = LocalDate.now().format(dateTimeFormatter)

fun getNowDateTime():String = LocalDateTime.now().format(dateTimeFormatter2)

fun getNowTime():String = LocalDateTime.now().format(dateTimeFormatter3)