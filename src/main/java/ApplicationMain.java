import fun.bookish.webrtc.signaling.task.HeartBeatPeriodTask;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class ApplicationMain {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HeartBeatPeriodTask.INSTANCE.start(vertx);
        vertx.deployVerticle("fun.bookish.webrtc.signaling.SignalingServerVerticle",
                                new DeploymentOptions().setInstances(4));
    }
}
