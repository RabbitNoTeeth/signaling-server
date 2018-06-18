package `fun`.bookish.webrtc.signaling.task

import `fun`.bookish.webrtc.signaling.core.NameSpace
import io.vertx.core.Vertx
import io.vertx.core.http.ServerWebSocket
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

object HeartBeatPeriodTask {

    private val heartBeatLogger = LoggerFactory.getLogger("heartBeat")

    private val records = ConcurrentHashMap<ServerWebSocket, LocalDateTime>()

    fun addRecord(dateTime: LocalDateTime, client: ServerWebSocket){
        heartBeatLogger.info("更新客户端 ${client.remoteAddress()} 心跳记录")
        records[client] = dateTime
    }

    fun start(vertx: Vertx){
        heartBeatLogger.info("启动定时任务：检测客户端心跳状态")
        vertx.setPeriodic(20000){
            heartBeatLogger.info("-- 开始客户端心跳检测 --")
            val now = LocalDateTime.now()
            var count = 0
            for((client, time) in records){
                if(time.plusSeconds(20).isBefore(now)){
                    heartBeatLogger.info("客户端 ${client.remoteAddress()} 心跳超时, 踢出客户端")
                    records.remove(client)
                    NameSpace.getRoom(client.path()).removeMember(client)
                    count++
                }
            }
            heartBeatLogger.info("-- 客户端心跳检测结束, 异常客户端数：$count --")
        }
    }

}