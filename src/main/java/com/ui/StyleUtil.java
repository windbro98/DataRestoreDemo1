package com.ui;

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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.LocalDateTimeStringConverter;
import javafx.util.converter.LongStringConverter;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.net.URL;

import static com.ui.UIConstants.*;
import static com.util.FileToolUtil.fileConcat;

public class StyleUtil {
    // 菜单项的正常状态
    public static void setNormalMenu(Button btn){
        btn.setFont(Font.font(null, FontWeight.NORMAL, MENU_FONT_SIZE));
        btn.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
    }

    // 菜单项的hover状态，背景设为灰色，字体加粗
    public static void setHoverMenu(Button btn){
        btn.setFont(Font.font(null, FontWeight.BOLD, MENU_FONT_SIZE));
        btn.setBackground(new Background(new BackgroundFill(HOVER_COLOR, null, null)));
    }

    // 菜单项的clicked状态，背景设为灰色
    public static void setClickedMenu(Button btn){
        btn.setFont(Font.font(null, FontWeight.NORMAL, MENU_FONT_SIZE));
        btn.setBackground(new Background(new BackgroundFill(HOVER_COLOR, null, null)));
    }

    // 提交确认窗口
    protected static Alert createConfirmAlert (String content){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认信息");
        alert.setHeaderText(null);
        alert.setContentText(content);

        ButtonType btnYes = new ButtonType("Yes", ButtonBar.ButtonData.YES); // 确认提交
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO); // 取消提交
        alert.getButtonTypes().setAll(btnYes, btnNo);
        return alert;
    }

    // 错误警报窗口, content为警告具体内容
    protected static Alert createErrorAlert(String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("发生错误");
        alert.setHeaderText(null);
        alert.setContentText(content);

        ButtonType btnYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        alert.getButtonTypes().setAll(btnYes);
        return alert;
    }

    // 创建弹窗，提示备份或恢复成功
    protected static void createPopup(String content, StackPane rootSp) throws InterruptedException {
        // 窗口初始化
        Notification msg = new Notification(content, new FontIcon(POPUP_CHECK_ICON)); // icon
        msg.getStyleClass().addAll(Styles.ACCENT, Styles.ELEVATED_1); // 风格
        msg.setPrefHeight(Region.USE_PREF_SIZE); // 预设高度
        msg.setMaxHeight(Region.USE_PREF_SIZE); // 最大高度

        // 动画效果设置
        var in = Animations.slideInDown(msg, Duration.millis(1000)); // 进入时间
        var out = Animations.fadeOut(msg, Duration.millis(3000)); // 消失时间
        // 页面设置
        StackPane.setAlignment(msg, Pos.TOP_CENTER); // 向上居中对齐
        StackPane.setMargin(msg, new Insets(10, 10, 0, 0)); // 边缘框
        // 加入页面
        if(!rootSp.getChildren().contains(msg))
            rootSp.getChildren().add(msg);
        // 显示动画
        in.playFromStart();
        // 鼠标点击时，立即删除文本框
        msg.setOnClose(event -> {
            rootSp.getChildren().remove(msg);
        });
        out.setOnFinished(f -> rootSp.getChildren().remove(msg));
        out.playFromStart();
    }
    // 密码输入窗口
    protected static String createPasswordDialog(String prompt, StackPane sp){
        // 消息窗口设置
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("密码输入窗口");
        dialog.setHeaderText(prompt);
        dialog.setContentText("请输入密码：");
        // 密码输入框
        PasswordField pf = new PasswordField();
        // 将密码输入框加入消息窗口
        dialog.getEditor().setText(pf.getText());
        dialog.getDialogPane().setContent(pf);
        dialog.initOwner(sp.getScene().getWindow());
        dialog.showAndWait();
        return pf.getText();
    }
    // 自定义文本框，可定义文本框的内容、宽度和是否可编辑
    protected static TextField createCustomField(String content, double radio, boolean editable){
        TextField tf = new TextField();
        tf.setText(content);
        tf.setEditable(editable);
        tf.setPrefWidth(WIDTH*radio);
        return tf;
    }
    // 自定义文本区域，可定义文本框的内容、宽度和是否可编辑
    protected static TextArea createCustomArea(String content, double radio, boolean editable){
        TextArea ta = new TextArea();
        ta.setText(content);
        ta.setEditable(editable);
        ta.setPrefWidth(WIDTH*radio);
        return ta;
    }
    // 创建选择组件, strList为选择项
    protected static ChoiceBox<String> createChoiceBox(String[] strList, double radio){
        ChoiceBox<String> cob = new ChoiceBox<>();
        cob.setPrefWidth(WIDTH*radio);
        cob.getItems().addAll(strList);
        return cob;
    }
    // 选择下拉窗口，choices为选择项
    protected static InputGroup createChoiceGroup(String prompt, ObservableList<String> choices){
        ComboBox<String> cmb = new ComboBox<>();
        CheckBox cb = createCheckBox(prompt, PROMPT_WIDTH_RATIO); // 选择矿口

        cmb.setItems(choices);
        cmb.getSelectionModel().selectFirst();

        return new InputGroup(cb, cmb);
    }
    // 勾选框初始化, prompt为提示词，默认不勾选
    protected static CheckBox createCheckBox(String prompt, double radio){
        CheckBox cb = new CheckBox(prompt);

        cb.setPrefWidth(WIDTH*radio);
        cb.setSelected(false);

        return cb;
    }
    // 两个按钮的布局，主要添加了填充和按钮间空白
    protected static HBox createButtonLayout(Button btn1, Button btn2){
        HBox hb = new HBox();
        hb.getChildren().addAll(btn1, btn2);

        // 按钮均匀分布
        int btnCount = 2;
        btn1.prefWidthProperty().bind(hb.widthProperty().divide(btnCount));
        btn2.prefWidthProperty().bind(hb.widthProperty().divide(btnCount));
        // 填充
        hb.setPadding(new Insets(HEIGHT* PADDING_RATIO, WIDTH* PADDING_RATIO, HEIGHT* PADDING_RATIO,
                WIDTH* PADDING_RATIO));
        // 按钮间空白
        hb.setSpacing(WIDTH* SPACE_RATIO);
        return hb;
    }
    // 创建文本，bold表示是否加粗
    protected static VBox createText(String words, boolean bold){
        VBox vb = new VBox();
        Text text = new Text(words);
        // 加粗设置
        if(bold)
            text.setFont(Font.font("", FontWeight.BOLD, CONTENT_FONT_SIZE));
        else
            text.setFont(Font.font("", FontWeight.NORMAL, CONTENT_FONT_SIZE));
        // 文本上方填充
        vb.setPadding(new Insets(HEIGHT* PADDING_RATIO *0.5, 0, 0, 0));
        vb.getChildren().add(text);
        return vb;
    }
    // 创建图片组件
    public static ImageView createImageView(URL imageUrl, double radio){
        Image image = new Image(imageUrl.getPath().substring(1));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(radio*WIDTH);
        imageView.setPreserveRatio(true);
        return imageView;
    }
    // 创建时间输入框
    protected static InputGroup createTimeFilterGroup(String prompt){
        InputGroup res = new InputGroup();
        CheckBox cb = createCheckBox(prompt, PROMPT_WIDTH_RATIO); // 勾选框
        // 起始时间提示词
        TextField timeStartPrompt = createCustomField("起始", INNER_PROMPT_WIDTH_RATIO, false);
        // 终止时间提示词
        TextField timeEndPrompt = createCustomField("终止", INNER_PROMPT_WIDTH_RATIO, false);
        // 起始时间输入框
        TextField timeStart = createCustomField("", TIME_WIDTH_RATIO, true);
        // 终止时间输入框
        TextField timeEnd = createCustomField("", TIME_WIDTH_RATIO, true);
        // 筛选类型选择，默认为排除
        ChoiceBox<String> cob = createChoiceBox(new String[]{"包含", "排除"}, BUTTON_WIDTH_RATIO);
        cob.setValue("排除");
        // 设置时间输入格式
        timeStart.setTextFormatter(new TextFormatter<>(new LocalDateTimeStringConverter(TIME_FORMAT, null)));
        timeEnd.setTextFormatter(new TextFormatter<>(new LocalDateTimeStringConverter(TIME_FORMAT, null)));

        res.getChildren().addAll(cb, timeStartPrompt, timeStart, timeEndPrompt, timeEnd, cob);
        return res;
    }

    // 文件大小输入框
    protected static InputGroup createSizeFilterGroup(){
        CheckBox cb = createCheckBox("文件大小", PROMPT_WIDTH_RATIO);
        // 最小文件大小提示词
        TextField sizeMinPrompt = createCustomField("最小", INNER_PROMPT_WIDTH_RATIO, false);
        // 最大文件大小提示词
        TextField sizeMaxPrompt = createCustomField("最大", INNER_PROMPT_WIDTH_RATIO, false);
        // 最小文件大小输入框
        TextField sizeMin = createCustomField("", SIZE_WIDTH_RATIO, true);
        // 最大文件大小输入框
        TextField sizeMax = createCustomField("", SIZE_WIDTH_RATIO, true);
        // 文件大小单位(KB)
        TextField sizeUnit1 = createCustomField("KB", INNER_UNIT_WIDTH_RATIO, false);
        TextField sizeUnit2 = createCustomField("KB", INNER_UNIT_WIDTH_RATIO, false);

        // 设置文件大小输入框格式，不可编辑，仅可输入数字
        sizeUnit1.setEditable(false);
        sizeUnit2.setEditable(false);
        sizeMin.setTextFormatter(new TextFormatter<>(new LongStringConverter()));
        sizeMax.setTextFormatter(new TextFormatter<>(new LongStringConverter()));
        // 筛选类型，默认为排除
        ChoiceBox<String> cob = createChoiceBox(new String[]{"包含", "排除"}, BUTTON_WIDTH_RATIO);
        cob.setValue("排除");

        return new InputGroup(cb, sizeMinPrompt, sizeMin, sizeUnit1, sizeMaxPrompt, sizeMax, sizeUnit2, cob);
    }

    // 文本框组合，使用TextField
    protected static InputGroup createAddrGroup(String prompt, boolean isFile){
        // 提示文本框
        TextField tfPrompt = createCustomField(prompt, PROMPT_WIDTH_RATIO, false);
        // 内容显示文本框
        TextField tf = createCustomField("", CONTENT_WIDTH_RATIO, false);
        // 文件选择按钮
        Button btn = createBtnFileChoose(tf, isFile);

        return new InputGroup(tfPrompt, tf, btn);
    }

    // 文本框组合，使用TextArea
    protected static InputGroup createTextFilterGroup(String prompt){
        // 提示文本框
        CheckBox cb = createCheckBox(prompt, PROMPT_WIDTH_RATIO);
        // 内容显示文本框
        TextArea ta = createCustomArea("", CONTENT_WIDTH_RATIO, true);
        // 筛选类型
        ChoiceBox<String> cob = createChoiceBox(new String[]{"包含", "排除"}, BUTTON_WIDTH_RATIO);
        cob.setValue("排除");

        return new InputGroup(cb, ta, cob);
    }
    // 文件筛选输入框，isFile表示查找的是文件(true)还是目录(false)
    protected static InputGroup createFileFilterGroup(String prompt, boolean isFile){
        // 提示词
        CheckBox cb = createCheckBox(prompt, PROMPT_WIDTH_RATIO);
        // 文本框
        TextArea ta = createCustomArea("", CONTENT_WIDTH_RATIO, true);
        // 文件选择组件
        Button btn=createBtnFileChoose(ta, isFile);
        return new InputGroup(cb, ta, btn);
    }
    // 创建文件选择组件，isFile表示选择的是文件(true)还是目录(false)
    protected static Button createBtnFileChoose(TextInputControl tc, boolean isFile){
        Stage stage = new Stage();
        // 按钮初始化
        Button btn = new Button("", new FontIcon(BUTTON_FILE_ICON));
        btn.setPrefWidth(WIDTH* BUTTON_WIDTH_RATIO);
        btn.getStyleClass().addAll(Styles.BUTTON_ICON);

        if(isFile){ // 文件选择
            // 文件选择组件
            FileChooser fc = new FileChooser();
            File projectDir = new File(PROJECT_DIR); // 初始位置为项目所在位置
            fc.setInitialDirectory(projectDir);
            // 点击按钮时，开始选择文件
            btn.setOnMouseClicked(mouseEvent -> {
                File file = fc.showOpenDialog(stage);
                if(!(file==null)){
                    if(tc instanceof TextField)
                        tc.setText(file.getAbsolutePath());
                    else if(tc instanceof TextArea)
                        tc.appendText(file.getAbsolutePath()+'\n');
                }
            });
        }else { // 目录选择
            DirectoryChooser dc = new DirectoryChooser();
            File projectDir = new File(PROJECT_DIR); // 初始位置为项目所在位置
            dc.setInitialDirectory(projectDir);
            // 点击按钮时，开始选择目录
            btn.setOnMouseClicked(mouseEvent -> {
                File file = dc.showDialog(stage);
                if(!(file==null)){
                    if(tc instanceof TextField){
                        tc.setText(file.getAbsolutePath());
                    }
                    else if(tc instanceof TextArea)
                        tc.appendText(file.getAbsolutePath()+'\n');
                }
            });
        }

        return btn;
    }
}
