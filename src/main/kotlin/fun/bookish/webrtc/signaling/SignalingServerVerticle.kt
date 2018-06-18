package `fun`.bookish.webrtc.signaling

import `fun`.bookish.webrtc.signaling.handler.websocket.WebSocketHandler
import `fun`.bookish.webrtc.signaling.router.RouterFactory
import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpServerOptions
import org.slf4j.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.ext.asyncsql.MySQLClient


class SignalingServerVerticle: AbstractVerticle() {

    private val mainLogger = LoggerFactory.getLogger("main")

    override fun start() {

        val mysqlConfig = JsonObject().apply {
            put("host","127.0.0.1")
            put("port",3306)
            put("maxPoolSize",20)
            put("database","signaling_devices")
            put("username","liuxindong")
            put("password","123456")
        }

        val mySQLClient = MySQLClient.createShared(this.vertx, mysqlConfig)

        val port = 9238
        val httpServerOptions = HttpServerOptions().setPort(port)
        val router = RouterFactory(mySQLClient).get(this.vertx)

        this.vertx
                .createHttpServer(httpServerOptions)
                .websocketHandler(WebSocketHandler())
                .requestHandler(router::accept)
                .listen{
                    if(it.succeeded()){
                        mainLogger.info("sunvua信令服务器启动成功, 端口号: $port")
                    }else{
                        mainLogger.error("sunvua信令服务器启动失败",it.cause())
                    }
                }

    }

}