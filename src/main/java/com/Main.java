package com;

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
    SrcManager srcM;
    BackManager backM;
    ResManager resM;


    @Override
    public void start(Stage stage) throws IOException {

        // 文本框
        TextField tfSrc = new TextField();  // 源目录路径
        TextField tfBackupSave = new TextField(); // 保存时的备份文件路径
        TextField tfBackupSel = new TextField(); // 恢复时的备份文件路径
        TextField tfRestore = new TextField(); // 恢复目录路径

        // 页面整体风格
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        stage.setTitle("数据恢复软件");

        // 左侧的功能框
        VBox leftMenu = new VBox();
        VBox.setVgrow(leftMenu, Priority.ALWAYS);

        // 右侧的内容框以及初始化
        initRightPage(rightPages, leftMenuInfo, tfSrc, tfBackupSave, tfBackupSel, tfRestore);

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
        Scene scene = new Scene(rootSp);
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
    void initRightPage(VBox[] rightPages, String[] leftMenuInfo, TextField tfSrc,
                       TextField tfBackupSave, TextField tfBackupSel, TextField tfRestore) throws IOException {
        // 对所有功能进行遍历，初始化对应内容界面
        for (int i = 0; i < numMenu; i++) {
            String info = leftMenuInfo[i];
            HBox hb;
            Button btnSubmit;
            switch (info){
                case "备份":  // 文件备份界面
                    // 页面初始化
                    rightPages[i] = PageFactory.initBackupPage(tfSrc, tfBackupSave);
                    // 提交按钮
                    hb = (HBox) rightPages[i].getChildren().get(2);
                    btnSubmit = (Button) hb.getChildren().get(0);
                    // 提交按钮的触发效果
                    btnSubmit.setOnMouseClicked(mouseEvent -> { // 点击
                        // 错误：路径为空
                        if(tfIsEmpty(tfSrc) || tfIsEmpty(tfBackupSave)){
                            Alert errorMsg = createErrorAlert("源路径或备份路径为空！");
                            errorMsg.show();
                        } else {
                                // 信息确认
                                Alert confirmMsg = createConfirmAlert("是否确认信息");
                                Optional<ButtonType> res = confirmMsg.showAndWait();
                            if(res.get().getText().equals("Yes")){
                                try {
                                    // 源文件管理器和备份文件管理器初始化
                                    srcM = new SrcManager(tfSrc.getText());
                                    backM = new BackManager(tfBackupSave.getText(), "", "");
                                    // 备份文件提取
                                    boolean backFlag = backM.fileExtract(srcM.getFilePathSet(), srcM.getSrcDir());
                                    // 提示窗口
                                    if(backFlag)
                                        createPopup("文件备份成功", rootSp); // 备份成功
                                    else
                                        createPopup("文件备份失败！备份目录不存在！", rootSp); // 备份失败
                                    // 清空文本框，准备下次备份
                                    tfSrc.clear();
                                    tfBackupSave.clear();
                                } catch (InterruptedException e) {
                                    // 错误：超时
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    });
                    break;
                case "恢复": // 文件恢复界面
                    // 页面初始化
                    rightPages[i] = PageFactory.initRestorePage(tfBackupSel, tfRestore);
                    // 提交按钮
                    hb = (HBox) rightPages[i].getChildren().get(2);
                    btnSubmit = (Button) hb.getChildren().get(0);
                    // 提交按钮的触发效果
                    btnSubmit.setOnMouseClicked(mouseEvent -> {
                        // 错误：路径为空
                        if(tfIsEmpty(tfBackupSel) || tfIsEmpty(tfRestore)){
                            Alert errorMsg = createErrorAlert("备份路径或恢复路径为空！");
                            errorMsg.show();
                        } else {
                                // 信息确认窗口
                                Alert confirmMsg = createConfirmAlert("是否确认信息");
                                Optional<ButtonType> res = confirmMsg.showAndWait();
                                if(res.get().getText().equals("Yes")){
                                    // 恢复文件管理器初始化
                                    resM = new ResManager(tfRestore.getText(), "", "");
                                    try {
                                        // 恢复备份文件
                                        ArrayList<String> errorFileList = resM.fileRestore(tfBackupSel.getText());
                                        if(errorFileList.isEmpty()){
                                            // 提示窗口: 恢复成功
                                            createPopup("文件恢复成功", rootSp);
                                            tfBackupSel.clear();
                                            tfRestore.clear();
                                        }
                                        else{
                                            StringBuilder sbErrorFiles = new StringBuilder();
                                            for(String errorFile : errorFileList){
                                                sbErrorFiles.append(errorFile);
                                                sbErrorFiles.append('\n');
                                            }
                                            createPopup("出现损坏文件！损坏文件为：\n"+sbErrorFiles, rootSp);
                                        }
                                    } catch (IOException | InterruptedException e) {
                                        throw new RuntimeException(e);
                                    } catch (ClassNotFoundException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                    });
                    break;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}