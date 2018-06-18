package `fun`.bookish.webrtc.signaling.router

import `fun`.bookish.webrtc.signaling.core.NameSpace
import `fun`.bookish.webrtc.signaling.extend.getNowDateTime
import `fun`.bookish.webrtc.signaling.extend.getRequestParams
import `fun`.bookish.webrtc.signaling.handler.http.MyCorsHandler
import `fun`.bookish.webrtc.signaling.handler.http.RequestParamsHandler
import `fun`.bookish.webrtc.signaling.handler.http.ResponsePostHandler
import `fun`.bookish.webrtc.signaling.handler.http.ResponsePreHandler
import `fun`.bookish.webrtc.signaling.model.JsonResult
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.asyncsql.AsyncSQLClient
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.StaticHandler
import java.lang.StringBuilder
import java.util.*


class RouterFactory(private val mySQLClient: AsyncSQLClient) {

    fun get(vertx: Vertx): Router{

        val router = Router.router(vertx)

        router.route()
                .handler(MyCorsHandler())
                .handler(BodyHandler.create())
                .handler(RequestParamsHandler())
                .handler(ResponsePreHandler())
                .handler(StaticHandler.create().setIndexPage("index.html").setWebRoot("dist"))

        router.post("/device").handler(this::addDevice)
        router.delete("/device").handler(this::deleteDevice)
        router.get("/devices").handler(this::getDevices)

        router.route()
                .handler(ResponsePostHandler())

        return router
    }

    /**
     * 添加设备信息
     */
    private fun addDevice(ctx: RoutingContext){
        val params = ctx.getRequestParams()
        val values = JsonArray()
                .add(UUID.randomUUID().toString())
                .add(getNowDateTime())
                .add(params.getString("code"))
                .add(params.getString("install_address"))
                .add(params.getString("install_time"))
                .add(params.getString("description")?:"")
        mySQLClient.updateWithParams("insert into t_devices(id,create_time,code,install_address,install_time,description) values(?,?,?,?,?,?)",values){
            if(it.succeeded() && it.result().updated > 0){
                ctx.response().write(Json.encode(JsonResult(message = "添加成功")))
                ctx.next()
            }else{
                ctx.response().write(Json.encode(JsonResult(success = false, message = "添加失败")))
                ctx.next()
            }
        }
    }

    /**
     * 删除设备
     */
    private fun deleteDevice(ctx: RoutingContext){

    }

    /**
     * 加载设备列表
     */
    private fun getDevices(ctx: RoutingContext){
        val params = ctx.getRequestParams()
        val countSql = StringBuilder("select count(*) from t_devices where 1=1")
        val querySql = StringBuilder("select * from t_devices where 1=1")
        params.getString("code")?.let {
            if(it.isNotBlank()){
                countSql.append(" and code='$it'")
                querySql.append(" and code='$it'")
            }
        }
        val page = params.getString("page")?.toInt()?:1
        val pageSize = params.getString("pageSize")?.toInt()?:10
        querySql.append(" order by create_time desc limit ${(page - 1)*pageSize},$pageSize")

        mySQLClient.query(countSql.toString()){
            if(it.succeeded()){
                val totalCount = it.result().results[0].getInteger(0)
                mySQLClient.query(querySql.toString()) {
                    if(it.succeeded()){
                        val rows = it.result().rows
                        rows.forEach{
                            if(NameSpace.containsClient(it.getString("code"))){
                                it.put("online","y")
                            }else{
                                it.put("online","n")
                            }
                        }
                        val online = params.getString("online")?:"a"
                        val finalRows = when(online){
                            "y" -> {
                                rows.filter { it.getString("online") == "y" }
                            }
                            "n" -> {
                                rows.filter { it.getString("online") == "n" }
                            }
                            else -> {
                                rows
                            }
                        }
                        val result = JsonObject().put("total",totalCount).put("rows",finalRows)
                        ctx.response().write(Json.encode(JsonResult(message = "设备列表获取成功", data = result)))
                        ctx.next()
                    }else{
                        ctx.response().write(Json.encode(JsonResult(success = false, message = "设备列表获取失败")))
                        it.cause().printStackTrace()
                        ctx.next()
                    }
                }
            }else{
                ctx.response().write(Json.encode(JsonResult(success = false, message = "设备列表获取失败")))
                it.cause().printStackTrace()
                ctx.next()
            }
        }
    }
}
