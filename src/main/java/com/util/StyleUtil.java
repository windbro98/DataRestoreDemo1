package com.util;

import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class StyleUtil {
    // 正常状态
    public static void setNormalMenu(Button btn){
        btn.setFont(Font.font(null, FontWeight.NORMAL, -1));
        btn.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
    }

    // hover状态
    public static void setHoverMenu(Button btn){
        btn.setFont(Font.font(null, FontWeight.BOLD, -1));
        btn.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
    }

    // 点击后状态
    public static void setClickedMenu(Button btn){
        btn.setFont(Font.font(null, FontWeight.NORMAL, -1));
        btn.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
    }
}
