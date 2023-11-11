package com.util;

import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

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

    // 信息确认
    public static Alert createConfirmAlert (String content){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认信息");
        alert.setHeaderText(null);
        alert.setContentText(content);

        ButtonType btnYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(btnYes, btnNo);
        return alert;
    }

    // 错误警报
    public static Alert createErrorAlert(String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("发生错误");
        alert.setHeaderText(null);
        alert.setContentText(content);

        ButtonType btnYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        alert.getButtonTypes().setAll(btnYes);
        return alert;
    }

    // 创建弹出窗口
    public static void createPopup(String content, StackPane rootSp) throws InterruptedException {
        Notification msg = new Notification(content, new FontIcon(Material2OutlinedAL.HELP_OUTLINE));
        msg.getStyleClass().addAll(Styles.ACCENT, Styles.ELEVATED_1);
        msg.setPrefHeight(Region.USE_PREF_SIZE);
        msg.setMaxHeight(Region.USE_PREF_SIZE);

        var in = Animations.slideInDown(msg, Duration.millis(1000));
        var out = Animations.fadeOut(msg, Duration.millis(3000));
        StackPane.setAlignment(msg, Pos.TOP_CENTER);
        StackPane.setMargin(msg, new Insets(10, 10, 0, 0));
        if(!rootSp.getChildren().contains(msg))
            rootSp.getChildren().add(msg);
        in.playFromStart();
        msg.setOnClose(event -> {
            rootSp.getChildren().remove(msg);
        });
        out.setOnFinished(f -> rootSp.getChildren().remove(msg));
        out.playFromStart();
    }
}
