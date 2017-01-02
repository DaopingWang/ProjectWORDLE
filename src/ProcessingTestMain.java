/**
 * Created by wang.daoping on 02.01.2017.
 */

import processing.core.PApplet;

public class ProcessingTestMain extends PApplet{


        public void settings(){
            size(200, 200);
        }

        public void draw(){
            background(0);
            ellipse(mouseX, mouseY, 20, 20);
        }

        public static void main(String... args){
            PApplet.main("ProcessingTestMain");
        }
}
