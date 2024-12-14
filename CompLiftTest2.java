package december14Code;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp (name = "2ndCompLiftTest")
public class CompLiftTest2 extends OpMode {
    Servo basketServo;

    @Override public void init() {
        basketServo = hardwareMap.get(Servo.class, "liftServo");
    }

    public void lift() {
        boolean liftUp = gamepad1.dpad_up;
        boolean liftDown = gamepad1.dpad_down;

        if (liftUp) {
            basketServo.setPosition(.5);
        }
        else if (liftDown) {
            basketServo.setPosition(-.25);
        }
    }

    @Override public void loop() {
        lift();
    }
}
