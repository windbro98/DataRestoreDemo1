package com.util;

import atlantafx.base.controls.Notification;
import atlantafx.base.layout.InputGroup;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

import java.net.URL;

import static com.util.FileToolUtil.fileConcat;
import static com.util.UIConstants.*;

public class StyleUtil {
    // 正常状态
    public static void setNormalMenu(Button btn){
        btn.setFont(Font.font(null, FontWeight.NORMAL, MENU_FONT_SIZE));
        btn.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
    }

    // hover状态
    public static void setHoverMenu(Button btn){
        btn.setFont(Font.font(null, FontWeight.BOLD, MENU_FONT_SIZE));
        btn.setBackground(new Background(new BackgroundFill(HOVER_COLOR, null, null)));
    }

    // 点击后状态
    public static void setClickedMenu(Button btn){
        btn.setFont(Font.font(null, FontWeight.NORMAL, MENU_FONT_SIZE));
        btn.setBackground(new Background(new BackgroundFill(HOVER_COLOR, null, null)));
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

    public static String createPasswordDialog(String prompt, StackPane sp){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("密码输入窗口");
        dialog.setHeaderText(prompt);
        dialog.setContentText("请输入密码：");
        PasswordField pf = new PasswordField();
        dialog.getEditor().setText(pf.getText());
        dialog.getDialogPane().setContent(pf);
        dialog.initOwner(sp.getScene().getWindow());
        dialog.showAndWait();
        return pf.getText();
    }
    public static TextField createCustomField(String content, double radio, boolean editable){
        TextField tf = new TextField();
        tf.setText(content);
        tf.setEditable(editable);
        tf.setPrefWidth(WIDTH*radio);
        return tf;
    }
    public static TextArea createCustomArea(String content, double radio, boolean editable){
        TextArea ta = new TextArea();
        ta.setText(content);
        ta.setEditable(editable);
        ta.setPrefWidth(WIDTH*radio);
        return ta;
    }

    public static ChoiceBox<String> createChoiceBox(String[] strList, double radio){
        ChoiceBox<String> cob = new ChoiceBox<>();
        cob.setPrefWidth(WIDTH*radio);
        cob.getItems().addAll(strList);
        return cob;
    }

    // 选择框初始化
    public static InputGroup createChoiceGroup(String prompt, ObservableList<String> choices){
        ComboBox<String> cmb = new ComboBox<>();
        CheckBox cb = createCheckBox(prompt, PROMPT_WIDTH_RADIO);

        cmb.setItems(choices);
        cmb.getSelectionModel().selectFirst();

        return new InputGroup(cb, cmb);
    }
    // 勾选框初始化
    public static CheckBox createCheckBox(String prompt, double radio){
        CheckBox cb = new CheckBox(prompt);

        cb.setPrefWidth(WIDTH*radio);
        cb.setSelected(false);

        return cb;
    }
    public static HBox createButtonLayout(Button btn1, Button btn2){
        HBox hb = new HBox();
        hb.getChildren().addAll(btn1, btn2);

        int btnCount = 2;
        btn1.prefWidthProperty().bind(hb.widthProperty().divide(btnCount));
        btn2.prefWidthProperty().bind(hb.widthProperty().divide(btnCount));
        hb.setPadding(new Insets(HEIGHT*PADDING_RADIO, WIDTH*PADDING_RADIO, HEIGHT*PADDING_RADIO, WIDTH*PADDING_RADIO));
        hb.setSpacing(WIDTH*SPACE_RADIO);
        return hb;
    }
    public static VBox createText(String words){
        VBox vb = new VBox();
        Text text = new Text(words);
        text.setFont(Font.font("", FontWeight.BOLD, CONTENT_FONT_SIZE));
        vb.setPadding(new Insets(HEIGHT*PADDING_RADIO*0.5, 0, 0, 0));
        vb.getChildren().add(text);
        return vb;
    }
    public static ImageView createImageView(URL imageUrl, double radio){
        Image image = new Image(imageUrl.getPath().substring(1));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(radio*WIDTH);
        imageView.setPreserveRatio(true);
        return imageView;
    }
}
