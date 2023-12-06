package com.ui;

import atlantafx.base.layout.InputGroup;
import atlantafx.base.theme.Styles;
import com.entity.BackManager;
import com.entity.ResManager;
import com.entity.SrcManager;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.kordamp.ikonli.feather.Feather;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Material;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static com.util.FileToolUtil.tfIsEmpty;
import static com.util.StyleUtil.*;
import static com.util.StyleUtil.createPopup;

// UI设计
public class PageFactory {

    // 构造方法私有化
    private PageFactory(){
    }

    // 备份页面初始化
    // todo: 这里可以将textprompt等内容都修改进来，使用getChildren.get来获取相关的组件
    public static VBox initBackupPage(SrcManager srcM, BackManager backM, StackPane rootSp){
        VBox backupPage = new VBox();
        TextField tfSrc = new TextField();
        TextField tfBackup = new TextField();
        // 按钮"提交"与"清空"
        Button btnSubmit = new Button("提交");
        Button btnClear = new Button("清空");
        HBox hb = new HBox(btnSubmit, btnClear);
        // 清空
        btnClear.setOnMouseClicked(mouseEvent -> {
            tfSrc.clear();
            tfBackup.clear();
        });
        String srcPrompt = "源地址";
        String backupPrompt = "备份地址";
        // 文本框
//        InputGroup srcGroup = createInputGroup(srcPrompt, taSrc, srcDirLenCum, false); // 源目录
        InputGroup srcGroup = createInputGroup(srcPrompt, tfSrc, false); // 源目录
        InputGroup backupGroup = createInputGroup(backupPrompt, tfBackup, false); //备份文件路径
        backupPage.getChildren().addAll(srcGroup, backupGroup, hb);
        btnBackup(btnSubmit, btnClear, tfSrc, tfBackup, srcM, backM, rootSp);
        return backupPage;
    };

    // 恢复页面初始化
    public static VBox initRestorePage(BackManager backM, ResManager resM, StackPane rootSp){
        VBox restorePage = new VBox();
        TextField tfBackup = new TextField();
        TextField tfRes = new TextField();
        // 按钮“提交”与“清空”
        Button btnSubmit = new Button("提交");
        Button btnClear = new Button("清空");
        HBox hb = new HBox(btnSubmit, btnClear);
        // 清空
        btnClear.setOnMouseClicked(mouseEvent -> {
            tfBackup.clear();
            tfRes.clear();
        });
        String backupPrompt = "备份地址";
        String resPrompt = "恢复地址";
        // 文本框
        InputGroup backupGroup = createInputGroup(backupPrompt, tfBackup, true); // 备份文件路径
        InputGroup resGroup = createInputGroup(resPrompt, tfRes, false); // 恢复路径
        restorePage.getChildren().addAll(backupGroup, resGroup, hb);
        btnRestore(btnSubmit, btnClear, tfBackup, tfRes, backM, resM, rootSp);
        return restorePage;
    }

//    public static InputGroup createFilterGroup(String prompt){
//        CheckBox cb = new CheckBox(prompt);
//    }

    // 文本框组合，使用的是textField
    private static InputGroup createInputGroup(String prompt, TextField tf, boolean fileType){
        // 提示文本框
        TextField tfPrompt = new TextField(prompt);
        tfPrompt.setEditable(false);
        tf.setEditable(false);
        // 文件选择按钮
        Button btn = new Button("", new FontIcon(Material2MZ.PAGEVIEW));
        btn.getStyleClass().addAll(Styles.BUTTON_ICON);
        Stage stage = new Stage();
        if(fileType){ // 文件选择
            FileChooser fc = new FileChooser();
            File projectDir = new File(System.getProperty("user.dir"));
            fc.setInitialDirectory(projectDir);
            btn.setOnMouseClicked(mouseEvent -> {
            File file = fc.showOpenDialog(stage);
            if(!(file==null))
                tf.setText(file.getAbsolutePath());
            });
        }else { // 目录选择
            DirectoryChooser dc = new DirectoryChooser();
            File projectDir = new File(System.getProperty("user.dir"));
            dc.setInitialDirectory(projectDir);
            btn.setOnMouseClicked(mouseEvent -> {
                File file = dc.showDialog(stage);
                if(!(file==null))
                    tf.setText(file.getAbsolutePath());
            });
        }

        return new InputGroup(tfPrompt, tf, btn);
    }

    // 文本框组合，使用的是textArea
    private static InputGroup createInputGroup(String prompt, TextArea ta, ArrayList<Integer> srcDirLenCum, boolean fileType){
        // 提示文本框
        TextField tfPrompt = new TextField(prompt);
        tfPrompt.setEditable(false);
        ta.setEditable(false);
        // 文件选择按钮
        Button btn = new Button("", new FontIcon(Material2MZ.PAGEVIEW));
        btn.getStyleClass().addAll(Styles.BUTTON_ICON);
        Stage stage = new Stage();
        if(fileType){ // 文件选择
            FileChooser fc = new FileChooser();
            File projectDir = new File(System.getProperty("user.dir"));
            fc.setInitialDirectory(projectDir);
            btn.setOnMouseClicked(mouseEvent -> {
                File file = fc.showOpenDialog(stage);
                if(!(file==null)){
                    ta.appendText(file.getPath());
                    srcDirLenCum.add(ta.getLength());
                }
            });
        }else { // 目录选择
            DirectoryChooser dc = new DirectoryChooser();
            File projectDir = new File(System.getProperty("user.dir"));
            dc.setInitialDirectory(projectDir);
            btn.setOnMouseClicked(mouseEvent -> {
                File file = dc.showDialog(stage);
                if(!(file==null)){
                    ta.appendText(file.toString()+'\n');
                    srcDirLenCum.add(ta.getLength());
                }
            });
        }

        return new InputGroup(tfPrompt, ta, btn);
    }

    // 备份页面的提交按钮
    public static void btnBackup(Button btnSubmit, Button btnClear, TextField tfSrc, TextField tfBackupSave, SrcManager srcM, BackManager backM, StackPane rootSp){
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
                        srcM.initSrcManager(tfSrc.getText());
                        backM.initBackManager(tfBackupSave.getText(), "", "");
                        // 备份文件提取
                        boolean backFlag = backM.fileExtract(srcM.getSelFilePath(), srcM.getsrcDir());
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
    }

    // 恢复提交按钮触发效果
    public static void btnRestore(Button btnSubmit, Button btnClear, TextField tfBackupRes, TextField tfRes, BackManager backM, ResManager resM, StackPane rootSp){
        // 清空按钮

        // 提交按钮
        btnSubmit.setOnMouseClicked(mouseEvent -> {
            // 错误：路径为空
            if(tfIsEmpty(tfBackupRes) || tfIsEmpty(tfRes)){
                Alert errorMsg = createErrorAlert("备份路径或恢复路径为空！");
                errorMsg.show();
            } else {
                // 信息确认窗口
                Alert confirmMsg = createConfirmAlert("是否确认信息");
                Optional<ButtonType> res = confirmMsg.showAndWait();
                if(res.get().getText().equals("Yes")){
                    // 恢复文件管理器初始化
                    resM.initResManager(tfRes.getText(), "", "");
                    try {
                        // 恢复备份文件
                        ArrayList<String> errorFileList = resM.fileRestore(tfBackupRes.getText());
                        if(errorFileList.isEmpty()){
                            // 提示窗口: 恢复成功
                            createPopup("文件恢复成功", rootSp);
                            tfBackupRes.clear();
                            tfRes.clear();
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
    }
}
