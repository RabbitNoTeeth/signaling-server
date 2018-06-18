package `fun`.bookish.webrtc.signaling.core

import java.util.concurrent.ConcurrentHashMap

/**
 * 名称空间：类似于房间的一种概念，主要用于管理多个房间，便于后期多个项目共用该信令服务器
 */
object NameSpace {

    private const val DEFAULT_NAMESPACE_KEY = "DEFAULT_NAMESPACE"
    private const val DEFAULT_ROOM_KEY = "DEFAULT_ROOM"
    private val NAMESPACES_MAP = ConcurrentHashMap<String, ConcurrentHashMap<String, Room>>()
    init {
        val defaultRoom = ConcurrentHashMap<String, Room>().apply {
            put(DEFAULT_ROOM_KEY, Room(DEFAULT_NAMESPACE_KEY, DEFAULT_ROOM_KEY))
        }
        NAMESPACES_MAP[DEFAULT_NAMESPACE_KEY] = defaultRoom
    }

    /**
     * 根据websocket客户端连接的path查找对应的room
     */
    fun getRoom(path: String): Room {
        val (namespace, roomName) = parsePath(path)

        var rooms = NAMESPACES_MAP[namespace]
        if(rooms == null){
            val newRooms = ConcurrentHashMap<String, Room>()
            rooms = if(NAMESPACES_MAP.putIfAbsent(namespace, newRooms) == null){
                 newRooms
            }else{
                NAMESPACES_MAP[namespace]
            }
        }

        var room = rooms!![roomName]
        if(room == null){
            val newRoom = Room(namespace, roomName)
            room = if(rooms.putIfAbsent(roomName, newRoom) == null){
                newRoom
            }else{
                rooms[roomName]
            }
        }

        return room!!
    }

    /**
     * 查询客户端是否在线(即是否存在对应的房间，并且房间人数大于1)
     */
    fun containsClient(path: String): Boolean{
        val (namespace, roomName) = parsePath(path)
        val count = NAMESPACES_MAP[namespace]?.get(roomName)?.size()?: 0
        return count >= 1
    }

    /**
     * 解析path路径，获取namespace和room
     */
    private fun parsePath(path: String): ParseResult {
        val list = path.split("/")
        return when(list.size){
            2 -> {
                val namespace = if(list[1].isBlank()){
                    DEFAULT_NAMESPACE_KEY
                }else{
                    list[1]
                }
                val room = DEFAULT_ROOM_KEY
                ParseResult(namespace,room)
            }
            else -> {
                val namespace = if(list[1].isBlank()){
                    DEFAULT_NAMESPACE_KEY
                }else{
                    list[1]
                }
                val room = if(list[2].isBlank()){
                    DEFAULT_ROOM_KEY
                }else{
                    list[2]
                }
                ParseResult(namespace,room)
            }
        }
    }

    /**
     * 用于解构声明的辅助数据类(kotlin最多支持解构一个对象的前5个元素)
     */
    private data class ParseResult(val namespace:String,val room:String)

}


