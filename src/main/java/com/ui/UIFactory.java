package com.ui;

import atlantafx.base.layout.InputGroup;
import com.entity.BackManager;
import com.entity.ResManager;
import com.entity.SrcManager;
import com.google.zxing.common.reedsolomon.ReedSolomonException;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static com.util.FileToolUtil.tfIsEmpty;
import static com.ui.StyleUtil.*;
import static com.ui.StyleUtil.createPopup;
import static com.ui.UIConstants.*;

// UI设计
public class UIFactory {




    // 构造方法私有化
    private UIFactory(){
    }

    // 备份页面初始化
    public static VBox initBackupPage(StackPane rootSp){
        VBox backupPage = new VBox();

        // 地址设置文本框
        VBox textAddr = createText("地址设置", true);
        // “源地址”文本框
        InputGroup srcGroup = createAddrGroup("源地址", false); // 源目录
        // “备份地址”文本框
        InputGroup backupGroup = createAddrGroup("备份地址", false); //备份文件路径
        // 筛选文本框
        VBox textFilter = createText("文件筛选", true);
        // 文件类型选择
        InputGroup formatGroup = createTextFilterGroup("文件类型");
        // 文件名选择
        InputGroup nameGroup = createTextFilterGroup("文件名");
        // 文件大小选择
        InputGroup sizeGroup = createSizeFilterGroup();
        // 时间格式
        VBox textTime = createText("(输入时间格式：yyyy-MM-dd HH:mm:ss. 例: 2024-01-01 10:00:00)", false);
        // 文件时间选择
        InputGroup createTimeGroup = createTimeFilterGroup("创建时间");
        InputGroup modifiedTimeGroup = createTimeFilterGroup("修改时间");
        InputGroup accessTimeGroup = createTimeFilterGroup("访问时间");
        // 文件与目录路径选择
        InputGroup fileGroup = createFileFilterGroup("排除文件路径", true);
        InputGroup dirGroup = createFileFilterGroup("排除目录路径", false);
        // 压缩与加密文本框
        VBox textPro = createText("压缩与加密", true);
        // 压缩方式选择
        InputGroup compressGroup = createChoiceGroup("压缩方式", FXCollections.observableArrayList("无", "Huffman", "LZ77", "LZ77Pro"));
        // 加密方式选择
        InputGroup encryptGroup = createChoiceGroup("加密方式", FXCollections.observableArrayList("无", "AES256"));
        // "提交"与"清空"按钮
        Button btnSubmit = new Button("提交");
        Button btnClear = new Button("清空");
        HBox hb = createButtonLayout(btnSubmit, btnClear);
        // 将所有组件添加到页面中
        backupPage.getChildren().addAll(textAddr, srcGroup, backupGroup, textFilter, formatGroup, nameGroup, sizeGroup,
                textTime, createTimeGroup, modifiedTimeGroup, accessTimeGroup, fileGroup, dirGroup, textPro, compressGroup,
                encryptGroup, hb);
        // 获取源目录和备份目录
        TextField tfSrc = (TextField) srcGroup.getChildren().get(1);
        TextField tfBackupSave = (TextField) backupGroup.getChildren().get(1);
        // 当用户点击提交时，根据各个组件的值，对SrcManager, BackManager进行初始化
        btnBackup(btnSubmit, btnClear, tfSrc, tfBackupSave, formatGroup, nameGroup, sizeGroup, createTimeGroup,
                modifiedTimeGroup, accessTimeGroup, fileGroup, dirGroup, compressGroup, encryptGroup, rootSp);
        return backupPage;
    };

    // 恢复页面初始化
    public static VBox initRestorePage(StackPane rootSp){
        // 恢复页面
        VBox restorePage = new VBox();
        // 文本框
        VBox textAddr = createText("地址设置", true);
        InputGroup backupGroup = createAddrGroup("备份地址", true); // 备份文件路径
        InputGroup resGroup = createAddrGroup("恢复地址", false); // 恢复路径
        // 按钮“提交”与“清空”
        Button btnSubmit = new Button("提交");
        Button btnClear = new Button("清空");
        HBox hb = createButtonLayout(btnSubmit, btnClear);
        restorePage.getChildren().addAll(textAddr, backupGroup, resGroup, hb);
        // 用户点击提交时，对BackManager和ResManager进行初始化
        btnRestore(btnSubmit, btnClear, (TextField) (backupGroup.getChildren().get(1)),
                (TextField) resGroup.getChildren().get(1), rootSp);
        return restorePage;
    }

    // 备份页面的提交按钮
    public static void btnBackup(Button btnSubmit, Button btnClear, TextField tfSrc, TextField tfBackupSave,
                                 InputGroup formatGroup, InputGroup nameGroup, InputGroup sizeGroup,
                                 InputGroup createTimeGroup, InputGroup modifiedTimeGroup, InputGroup accessTimeGroup,
                                 InputGroup fileGroup, InputGroup dirGroup, InputGroup compressGroup,
                                 InputGroup encryptGroup, StackPane rootSp){
        SrcManager srcM = SrcManager.getInstance();
        BackManager backM = BackManager.getInstance();
        // 清空
        btnClear.setOnMouseClicked(mouseEvent -> {
            backupClear(tfSrc, tfBackupSave, formatGroup, nameGroup, sizeGroup, createTimeGroup, modifiedTimeGroup,
                    accessTimeGroup, fileGroup, dirGroup, compressGroup, encryptGroup);
        });

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
                        initFilter(formatGroup, nameGroup, sizeGroup, createTimeGroup,
                                modifiedTimeGroup, accessTimeGroup, fileGroup, dirGroup);
                        initEncoder(compressGroup, encryptGroup, rootSp);
                        srcM.initSrcManager(tfSrc.getText());
                        backM.initBackManager(tfBackupSave.getText());
                        // 备份文件提取
                        boolean backFlag = backM.fileExtract(srcM.getFilePathSet(), srcM.getSrcDir());
                        // 提示窗口
                        if(backFlag)
                            createPopup("文件备份成功", rootSp, true); // 备份成功
                        else
                            createPopup("文件备份失败！备份目录不存在！", rootSp, false); // 备份失败
                        // 清空文本框，准备下次备份
                        backupClear(tfSrc, tfBackupSave, formatGroup, nameGroup, sizeGroup, createTimeGroup, modifiedTimeGroup,
                                accessTimeGroup, fileGroup, dirGroup, compressGroup, encryptGroup);
                    } catch (InterruptedException e) {
                        // 错误：超时
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InvalidAlgorithmParameterException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchPaddingException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalBlockSizeException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    } catch (BadPaddingException e) {
                        throw new RuntimeException(e);
                    } catch (InvalidKeySpecException e) {
                        throw new RuntimeException(e);
                    } catch (InvalidKeyException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    // 备份界面的清空按钮
    public static void backupClear(TextField tfSrc, TextField tfBackupSave, InputGroup formatGroup, InputGroup nameGroup,
                                   InputGroup sizeGroup, InputGroup createTimeGroup, InputGroup modifiedTimeGroup,
                                   InputGroup accessTimeGroup, InputGroup fileGroup, InputGroup dirGroup,
                                   InputGroup compressGroup, InputGroup encryptGroup){
        ArrayList<InputGroup> taArr = new ArrayList<>();
        ArrayList<InputGroup> timeArr = new ArrayList<>();
        CheckBox cb;
        ComboBox<String> cmb;

        taArr.addAll(Arrays.asList(formatGroup, nameGroup, fileGroup, dirGroup));
        timeArr.addAll(Arrays.asList(createTimeGroup, modifiedTimeGroup, accessTimeGroup));
        // 文本框
        tfSrc.clear();
        tfBackupSave.clear();
        // textArea型InputGroup
        for(InputGroup ig:taArr){
            cb = (CheckBox) ig.getChildren().get(0);
            TextArea ta = (TextArea) ig.getChildren().get(1);
            ta.clear();
            if(cb.isSelected()) cb.setSelected(false);
        }
        // time型InputGroup
        for(InputGroup ig:timeArr){
            cb = (CheckBox) ig.getChildren().get(0);
            TextField tfStart = (TextField) ig.getChildren().get(2);
            TextField tfEnd = (TextField) ig.getChildren().get(4);
            tfStart.clear();
            tfEnd.clear();
            if(cb.isSelected()) cb.setSelected(false);
        }
        // 文件大小InputGroup
        cb = (CheckBox) sizeGroup.getChildren().get(0);
        TextField tfMin = (TextField) sizeGroup.getChildren().get(2);
        TextField tfMax = (TextField) sizeGroup.getChildren().get(5);
        tfMin.clear();
        tfMax.clear();
        if(cb.isSelected()) cb.setSelected(false);
        // 压缩方式
        cb = (CheckBox) compressGroup.getChildren().get(0);
        cmb = (ComboBox<String>) compressGroup.getChildren().get(1);
        cmb.getSelectionModel().selectFirst();
        if(cb.isSelected()) cb.setSelected(false);
        // 加密方式
        cb = (CheckBox) encryptGroup.getChildren().get(0);
        cmb = (ComboBox<String>) encryptGroup.getChildren().get(1);
        cmb.getSelectionModel().selectFirst();
        if(cb.isSelected()) cb.setSelected(false);
        // 将srcM的文件筛选器重置
        SrcManager srcM = SrcManager.getInstance();
        srcM.resetFilter();
    }

    // 恢复提交按钮触发效果
    public static void btnRestore(Button btnSubmit, Button btnClear, TextField tfBackupRes, TextField tfRes, StackPane rootSp){
        ResManager resM = ResManager.getInstance();
        // 清空按钮
        btnClear.setOnMouseClicked(mouseEvent -> {
            tfBackupRes.clear();
            tfRes.clear();
        });
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
                if(res.get().getText().equals("Yes")){ // 用户确认提交
                    // 获取备份文件和对应的head文件
                    String backFilePath = tfBackupRes.getText();
                    File backFile = new File(backFilePath);
                    File headFile = new File(backFilePath+"_head");
                    // 备份文件存在性检验
                    if(!backFile.exists()){ // 检验输入的备份文件是否存在，
                        try {
                            createPopup("恢复失败！备份文件不存在！", rootSp, false);
                            return;
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else if(!headFile.exists()){ // 检验输入的备份文件是否存在对应的head文件
                        try {
                            createPopup("恢复失败！备份文件缺乏对应head文件！", rootSp, false);
                            return;
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    // 已验证备份文件和对应head文件存在，开始进行文件恢复
                    // 恢复文件管理器初始化
                    try {
                        resM.initResManager(tfRes.getText(), backFilePath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    // 判断备份文件是否加密，是则提示用户输入密码
                    if(resM.encryptType != 0){
                        String prompt = "该备份文件是加密文件，请输入密码：";
                        String password = createPasswordDialog(prompt, rootSp);
                        resM.setPassword(password);
                    }
                    try {
                        // 恢复备份文件
                        ArrayList<String> errorFileList = resM.fileRestore(tfBackupRes.getText());
                        if(errorFileList.isEmpty()){ // 无损坏文件
                            // 提示窗口: 恢复成功
                            createPopup("文件恢复成功", rootSp, true);
                            tfBackupRes.clear();
                            tfRes.clear();
                        }
                        else{ // 有损坏文件，弹出窗口提示损坏文件名
                            StringBuilder sbErrorFiles = new StringBuilder();
                            for(String errorFile : errorFileList){
                                sbErrorFiles.append(errorFile);
                                sbErrorFiles.append('\n');
                            }
                            createPopup("出现损坏文件！损坏文件为：\n"+sbErrorFiles, rootSp, false);
                        }
                    }catch (Exception e) {
                        try { // 备份文件为加密文件且输入密码错误时，弹窗提示
                            createPopup("备份文件密码错误！", rootSp, false);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        });
    }
    // 文件压缩和加密初始化
    public static void initEncoder(InputGroup compressGroup, InputGroup encryptGroup, StackPane sp){
        // 勾选框
        CheckBox cbCompress = (CheckBox) compressGroup.getChildren().get(0);
        CheckBox cbEncrypt = (CheckBox) encryptGroup.getChildren().get(0);
        // 文件备份管理器
        BackManager backM = BackManager.getInstance();

        if(cbCompress.isSelected()) // 用户确认压缩
        {
            // 获取压缩方式并初始化
            ComboBox<String> cmb = (ComboBox<String>) compressGroup.getChildren().get(1);
            String compressType = cmb.getSelectionModel().getSelectedItem();
            backM.setCompressType(compressType);
        }
        if(cbEncrypt.isSelected()){ // 用户确认加密
            // 获取加密方式
            ComboBox<String> cmb = (ComboBox<String>) encryptGroup.getChildren().get(1);
            String encryptType = cmb.getSelectionModel().getSelectedItem();
            // 提示框，提示用户输入密码，并进行初始化
            if(!encryptType.equals("无")){
                String prompt = "您选择了文件加密，请输入您的密码（密码长度不大于32位）:";
                String password = createPasswordDialog(prompt, sp);
                backM.setPassword(password);
            }
            // 加密方式初始化
            backM.setEncryptType(encryptType);
        }
    }
    // 文件筛选器初始化
    public static void initFilter(InputGroup formatGroup, InputGroup nameGroup, InputGroup sizeGroup,
                                  InputGroup createTimeGroup, InputGroup modifiedTimeGroup, InputGroup accessTimeGroup,
                                  InputGroup fileGroup, InputGroup dirGroup){
        // 筛选器勾选框
        CheckBox cbFormat = (CheckBox) formatGroup.getChildren().get(0); // 文件格式
        CheckBox cbName = (CheckBox) nameGroup.getChildren().get(0); // 文件名
        CheckBox cbSize = (CheckBox) sizeGroup.getChildren().get(0); // 文件大小
        CheckBox cbCreateTime = (CheckBox) createTimeGroup.getChildren().get(0); // 创建时间
        CheckBox cbModifiedTime = (CheckBox) modifiedTimeGroup.getChildren().get(0); // 最后修改时间
        CheckBox cbAccessTime = (CheckBox) accessTimeGroup.getChildren().get(0); // 最后访问时间
        CheckBox cbFile = (CheckBox) fileGroup.getChildren().get(0); // 选择文件
        CheckBox cbDir = (CheckBox) dirGroup.getChildren().get(0); // 选择目录
        // 源文件管理器初始化
        SrcManager srcM = SrcManager.getInstance();
        // 文本框
        TextArea ta;
        TextField tfStart, tfEnd;
        // 选择框
        ChoiceBox cob;

        // 用户确认对文件格式筛选
        if(cbFormat.isSelected()){
            // 获取文件格式的筛选方式和筛选类型，并进行筛选器初始化
            ta = (TextArea) formatGroup.getChildren().get(1);
            cob = (ChoiceBox) formatGroup.getChildren().get(2);
            srcM.setFilterFormat(ta.getText(), (String) cob.getValue());
        }
        // 用户确认对文件名称筛选
        if(cbName.isSelected()){
            // 获取文件格式的筛选方式和筛选类型，并进行筛选器初始化
            ta = (TextArea) nameGroup.getChildren().get(1);
            cob = (ChoiceBox) nameGroup.getChildren().get(2);
            srcM.setFilterName(ta.getText(), (String) cob.getValue());
        }
        // 用户确认对文件大小筛选
        if(cbSize.isSelected()){
            // 获取文件大小的筛选方式和筛选类型，并进行筛选器初始化
            tfStart = (TextField) sizeGroup.getChildren().get(2);
            tfEnd = (TextField) sizeGroup.getChildren().get(5);
            cob = (ChoiceBox) sizeGroup.getChildren().get(7);
            srcM.setFilterSize(Long.parseLong(tfStart.getText()), Long.parseLong(tfEnd.getText()), (String) cob.getValue());
        }
        // 用户确认对文件创建时间筛选
        if(cbCreateTime.isSelected()){
            // 获取文件创建时间的筛选方式和筛选类型，并进行筛选器初始化
            tfStart = (TextField) createTimeGroup.getChildren().get(2);
            tfEnd = (TextField) createTimeGroup.getChildren().get(4);
            cob = (ChoiceBox) createTimeGroup.getChildren().get(5);
            srcM.setFilterTime(LocalDateTime.from(TIME_FORMAT.parse(tfStart.getText())),
                    LocalDateTime.from(TIME_FORMAT.parse(tfEnd.getText())), "create", (String) cob.getValue());
        }
        // 用户确认对文件最后修改时间筛选
        if(cbModifiedTime.isSelected()){
            // 获取文件最后修改时间的筛选方式和筛选类型，并进行筛选器初始化
            tfStart = (TextField) modifiedTimeGroup.getChildren().get(2);
            tfEnd = (TextField) modifiedTimeGroup.getChildren().get(4);
            cob = (ChoiceBox) modifiedTimeGroup.getChildren().get(5);
            srcM.setFilterTime(LocalDateTime.from(TIME_FORMAT.parse(tfStart.getText())),
                    LocalDateTime.from(TIME_FORMAT.parse(tfEnd.getText())), "modified", (String) cob.getValue());
        }
        // 用户确认对文件最后访问时间筛选
        if(cbAccessTime.isSelected()){
            // 获取文件最后访问时间的筛选方式和筛选类型，并进行筛选器初始化
            tfStart = (TextField) accessTimeGroup.getChildren().get(2);
            tfEnd = (TextField) accessTimeGroup.getChildren().get(4);
            cob = (ChoiceBox) accessTimeGroup.getChildren().get(5);
            srcM.setFilterTime(LocalDateTime.from(TIME_FORMAT.parse(tfStart.getText())),
                    LocalDateTime.from(TIME_FORMAT.parse(tfEnd.getText())), "access", (String) cob.getValue());
        }
        // 用户确认对选中文件进行排除
        if(cbFile.isSelected()){
            // 获取排除的文件，并进行筛选器初始化
            ta = (TextArea) fileGroup.getChildren().get(1);
            srcM.setFilterFile(ta.getText());
        }
        // 用户确认对选中目录进行排除
        if(cbDir.isSelected()){
            // 获取排除的目录，并进行筛选器初始化
            ta = (TextArea) dirGroup.getChildren().get(1);
            srcM.setFilterDir(ta.getText());
        }

    }
}
