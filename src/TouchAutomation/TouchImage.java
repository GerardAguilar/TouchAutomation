package TouchAutomation;

import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.TouchEvent;

//from: https://docs.oracle.com/javafx/2/events/touch_events.htm
public class TouchImage extends ImageView {
    private long touchId = -1;
    double touchx, touchy;

    public TouchImage(int x, int y, Image img) {
        super(img);
        setTranslateX(x);
        setTranslateY(y);

        setOnTouchPressed(new EventHandler<TouchEvent>() {
            @Override public void handle(TouchEvent event) {
                if (touchId == -1) {
                    touchId = event.getTouchPoint().getId();
                    touchx = event.getTouchPoint().getSceneX() - getTranslateX();
                    touchy = event.getTouchPoint().getSceneY() - getTranslateY();
                }
                event.consume();
            }
        });

        setOnTouchReleased(new EventHandler<TouchEvent>() {
            @Override public void handle(TouchEvent event) {
                if (event.getTouchPoint().getId() == touchId) {
                    touchId = -1;
                }
                event.consume();
            }
        });

        setOnTouchMoved(new EventHandler<TouchEvent>() {
            @Override public void handle(TouchEvent event) {
                if (event.getTouchPoint().getId() == touchId) {
                    setTranslateX(event.getTouchPoint().getSceneX() - touchx);
                    setTranslateY(event.getTouchPoint().getSceneY() - touchy);
                }
                event.consume();
            }
        });
    }
}
