package `fun`.bookish.webrtc.signaling.handler.websocket

import `fun`.bookish.webrtc.signaling.core.NameSpace
import `fun`.bookish.webrtc.signaling.core.Room
import `fun`.bookish.webrtc.signaling.task.HeartBeatPeriodTask
import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory
import java.time.LocalDateTime


class WebSocketHandler: Handler<ServerWebSocket> {

    private val mainLogger = LoggerFactory.getLogger("main")

    override fun handle(webSocket: ServerWebSocket) {

        val path = webSocket.path()
        val room = NameSpace.getRoom(path)

        if(room.size() == 2){
            // 限制房间人数最多为2人
            webSocket.reject()
        }else{
            connect(webSocket,room)
        }

    }

    private fun connect(webSocket: ServerWebSocket, room: Room) {
        /* 测试连通性 */
        webSocket.writePing(Buffer.buffer("ping"))

        /* 连接建立时回调 */
        webSocket.pongHandler {
            val pong = it.toString("UTF-8")
            when(pong){
                "ping" -> {
                    // 加入房间
                    room.addMember(webSocket)
                    mainLogger.info("客户端 ${webSocket.remoteAddress()} 加入房间：${room.namespace} - ${room.name}, 当前房间人数: ${room.size()}")
                }
                else -> {
                    // ignore
                }
            }

        }

        /* 收到消息时回调 */
        webSocket.textMessageHandler {
            val message = JsonObject(it)
            when(message.getString("type")){
                "keepAlive" -> {
                    HeartBeatPeriodTask.addRecord(LocalDateTime.now(), webSocket)
                }
                else -> {
                    mainLogger.info("收到来自客户端 ${webSocket.remoteAddress()} 的信令消息, type = ${message.getString("type")}")
                    room.send(webSocket, it)
                }
            }
        }

        /* 连接关闭时回调 */
        webSocket.closeHandler {
            // 退出房间
            room.removeMember(webSocket)
            mainLogger.info("客户端 ${webSocket.remoteAddress()} 离开房间：${room.namespace} - ${room.name}, 当前房间人数: ${room.size()}")
        }
    }

}