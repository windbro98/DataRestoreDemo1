package com;

import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Styles;
import com.ui.UIFactory;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static com.ui.StyleUtil.*;
import static com.util.DataUtil.getIndexForArray;
import static com.ui.UIConstants.*;

// 主程序，也是GUI设计界面
public class Main extends Application {
    StackPane rootSp = new StackPane(); // 整体平面
    private int currentMenuId=-1; // 当前选中的菜单项
    private int tmpMenuId=-1; // 上一次选中的菜单项
    String[] leftMenuInfo = {"备份", "恢复"}; // 左侧所有菜单项
    int numMenu = leftMenuInfo.length; // 菜单项数目
    VBox[] rightPages = new VBox[numMenu]; // 所有菜单项对应的右侧功能区的集合
    Button[] menuBtns = new Button[numMenu]; // 菜单项对应的按钮

    @Override
    public void start(Stage stage) throws IOException {
        // 页面整体风格
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        stage.setTitle("数据恢复软件");
        // 左侧的功能框
        VBox leftMenu = new VBox();
        VBox.setVgrow(leftMenu, Priority.ALWAYS);
        // 右侧的内容框以及初始化
        initRightPage(rightPages, leftMenuInfo);
        // 软件整体布局
        SplitPane sp = new SplitPane(
                leftMenu,
                rightPages[0]
        );
        sp.setDividerPositions(MENU_WIDTH_RATIO);
        // 左边框初始化
        initLeftMenu(leftMenu, leftMenuInfo, rightPages, sp, menuBtns);
        // 页面的拓扑结构
        rootSp.getChildren().add(sp);
        Scene scene = new Scene(rootSp, WIDTH, HEIGHT);
        stage.setScene(scene);

        // 运行
        stage.show();
    }

    // 左边框初始化
    void initLeftMenu(VBox leftMenu, String[] leftMenuInfo, Node[] rightPages, SplitPane sp, Button[] menuBtns){
        for (int i = 0; i < leftMenuInfo.length; i++) {
            // 每个功能对应的页面
            Node page = rightPages[i];
            // 每个功能对应的按钮种类（"备份", "恢复"）
            String info = leftMenuInfo[i];
            // 创建该功能对应的按钮图片
            String imageName = String.format("left%d.png", i);
            ImageView imageView = createImageView(imageName, IMAGE_WIDTH_RATIO);

            // 创建该功能对应的按钮
            Button btn = new Button(info, imageView);
            btn.setContentDisplay(ContentDisplay.TOP);
            setNormalMenu(btn);
            // 按钮风格
            btn.getStyleClass().add(Styles.FLAT);
            btn.setPrefWidth(MENU_WIDTH_RATIO *WIDTH);
            // 按钮的触发效果
            btn.setOnMouseClicked(mouseEvent -> {   // 点击，背景变为灰色
                tmpMenuId = currentMenuId;
                currentMenuId = getIndexForArray(leftMenuInfo, btn.getText());
                if(tmpMenuId!=currentMenuId && tmpMenuId>=0){
                    Button btnTmp = menuBtns[tmpMenuId];
                    setNormalMenu(btnTmp);
                }
                sp.getItems().set(1, page);
            });
            btn.setOnMouseMoved(mouseEvent -> {   // 悬浮，背景变为灰色，且字体加粗
                setHoverMenu(btn);
            });
            btn.setOnMouseExited(mouseEvent -> {   // 离开，恢复正常字体
                if(currentMenuId==getIndexForArray(leftMenuInfo, btn.getText())){
                    setClickedMenu(btn);
                }else{
                    setNormalMenu(btn);
                }
            });

            // 将按钮加入功能区
            leftMenu.getChildren().add(btn);
            menuBtns[i] = btn;
        }
    }

    // 右侧页面初始化
    void initRightPage(VBox[] rightPages, String[] leftMenuInfo) throws IOException {
        // 对所有功能进行遍历，初始化对应内容界面
        for (int i = 0; i < numMenu; i++) {
            String info = leftMenuInfo[i];
            switch (info){
                case "备份":  // 文件备份界面
                    ArrayList<Integer> srcDirLenCum = new ArrayList<>();
                    // 页面初始化
                    rightPages[i] = UIFactory.initBackupPage(rootSp);
                    break;
                case "恢复": // 文件恢复界面
                    // 页面初始化
                    rightPages[i] = UIFactory.initRestorePage(rootSp);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}