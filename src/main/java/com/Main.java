package com;

import atlantafx.base.layout.InputGroup;
import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Styles;
import com.entity.BackManager;
import com.entity.ResManager;
import com.entity.SrcManager;
import com.ui.PageFactory;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static com.util.FileToolUtil.taIsEmpty;
import static com.util.FileToolUtil.tfIsEmpty;
import static com.util.StyleUtil.*;
import static com.util.DataUtil.getIndexForArray;

// 主程序，也是GUI设计界面
public class Main extends Application {
    StackPane rootSp = new StackPane();
    private int currentMenuId=-1;
    private int tmpMenuId=-1;
    String[] leftMenuInfo = {"备份", "恢复"};
    int numMenu = leftMenuInfo.length;
    VBox[] rightPages = new VBox[numMenu];
    Button[] menuBtns = new Button[numMenu];

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
        sp.setDividerPositions(0.3);
        // 左边框初始化
        initLeftMenu(leftMenu, leftMenuInfo, rightPages, sp, menuBtns);
        // 页面的拓扑结构
        rootSp.getChildren().add(sp);
        Scene scene = new Scene(rootSp, 900, 450);
        stage.setScene(scene);

        stage.show();
    }

    // 左边框初始化
    void initLeftMenu(VBox leftMenu, String[] leftMenuInfo, Node[] rightPages, SplitPane sp, Button[] menuBtns){
        for (int i = 0; i < leftMenuInfo.length; i++) {
            // 每个功能对应的页面
            Node page = rightPages[i];
            // 每个功能对应的按钮
            String info = leftMenuInfo[i];
            Button btn = new Button(info);
            setNormalMenu(btn);
            // 按钮风格
            btn.getStyleClass().add(Styles.FLAT);
            // 按钮的触发效果
            btn.setOnMouseClicked(mouseEvent -> {   // 点击
                tmpMenuId = currentMenuId;
                currentMenuId = getIndexForArray(leftMenuInfo, btn.getText());
                if(tmpMenuId!=currentMenuId && tmpMenuId>=0){
                    Button btnTmp = menuBtns[tmpMenuId];
                    setNormalMenu(btnTmp);
                }
                sp.getItems().set(1, page);
            });
            btn.setOnMouseMoved(mouseEvent -> {   // 悬浮
                setHoverMenu(btn);
            });
            btn.setOnMouseExited(mouseEvent -> {   // 离开
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
                    rightPages[i] = PageFactory.initBackupPage(rootSp);
                    break;
                case "恢复": // 文件恢复界面
                    // 页面初始化
                    rightPages[i] = PageFactory.initRestorePage(rootSp);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}