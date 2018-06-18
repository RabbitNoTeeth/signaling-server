package `fun`.bookish.webrtc.signaling.core

import io.vertx.core.http.ServerWebSocket
import io.vertx.core.impl.ConcurrentHashSet

/**
 * 房间：一种映射关系的概念，当一个客户端发出信令或消息时，所有于它处于同一房间内的客户端都能收到信令或者消息
 */
class Room(val namespace: String, val name:String) {

    private val clients = ConcurrentHashSet<ServerWebSocket>()

    /**
     * 添加成员
     */
    fun addMember(client: ServerWebSocket){
        val source = client.query()
        // 如果是app端连接，那么先清空房间
        if(source.isNotBlank() && source == "app"){
            clients.clear()
        }
        clients.add(client)
    }

    /**
     * 删除成员
     */
    fun removeMember(client: ServerWebSocket){
        clients.remove(client)
    }

    /**
     * 在房间内广播消息
     */
    fun send(source: ServerWebSocket, data: String){
        clients.forEach{
            if(it != source){
                it.writeTextMessage(data)
            }
        }
    }

    fun getMembers(): Set<ServerWebSocket>{
        return clients
    }

    fun size():Int {
        return clients.size
    }

}