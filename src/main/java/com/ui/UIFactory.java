package com.ui;

import atlantafx.base.layout.InputGroup;
import atlantafx.base.theme.Styles;
import com.entity.BackManager;
import com.entity.ResManager;
import com.entity.SrcManager;
import com.google.zxing.common.reedsolomon.ReedSolomonException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.util.converter.LocalDateTimeStringConverter;
import javafx.util.converter.LongStringConverter;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static com.util.FileToolUtil.tfIsEmpty;
import static com.util.StyleUtil.*;
import static com.util.StyleUtil.createPopup;
import static com.util.UIConstants.*;

// UI设计
public class UIFactory {

    public static Material2MZ btnFileIcon = Material2MZ.PAGEVIEW;
    public static DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    // 构造方法私有化
    private UIFactory(){
    }

    // 备份页面初始化
    public static VBox initBackupPage(StackPane rootSp){
        VBox backupPage = new VBox();

        // 地址设置文本框
        VBox textAddr = createText("地址设置");
        // “源地址”文本框
        InputGroup srcGroup = createAddrGroup("源地址", false); // 源目录
        // “备份地址”文本框
        InputGroup backupGroup = createAddrGroup("备份地址", false); //备份文件路径
        // 筛选文本框
        VBox textFilter = createText("文件筛选");
        // 文件类型选择
        InputGroup formatGroup = createTextFilterGroup("文件类型");
        // 文件名选择
        InputGroup nameGroup = createTextFilterGroup("文件名");
        // 文件大小选择
        InputGroup sizeGroup = createSizeFilterGroup();
        // 文件时间选择
        InputGroup createTimeGroup = createTimeFilterGroup("创建时间");
        InputGroup modifiedTimeGroup = createTimeFilterGroup("修改时间");
        InputGroup accessTimeGroup = createTimeFilterGroup("访问时间");
        // 文件与目录路径选择
        InputGroup fileGroup = createFileFilterGroup("排除文件路径", true);
        InputGroup dirGroup = createFileFilterGroup("排除目录路径", false);
        // 压缩与加密文本框
        VBox textPro = createText("压缩与加密");
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
                createTimeGroup, modifiedTimeGroup, accessTimeGroup, fileGroup, dirGroup, textPro, compressGroup,
                encryptGroup, hb);
        // 获取源目录和备份目录
        TextField tfSrc = (TextField) srcGroup.getChildren().get(1);
        TextField tfBackupSave = (TextField) backupGroup.getChildren().get(1);
        // 当用户点击提交时，对各个manager进行赋值
        btnBackup(btnSubmit, btnClear, tfSrc, tfBackupSave, formatGroup, nameGroup, sizeGroup, createTimeGroup,
                modifiedTimeGroup, accessTimeGroup, fileGroup, dirGroup, compressGroup, encryptGroup, rootSp);
        return backupPage;
    };

    // 恢复页面初始化
    public static VBox initRestorePage(StackPane rootSp){
        VBox restorePage = new VBox();

        // 文本框
        VBox textAddr = createText("地址设置");
        InputGroup backupGroup = createAddrGroup("备份地址", true); // 备份文件路径
        InputGroup resGroup = createAddrGroup("恢复地址", false); // 恢复路径
        // 按钮“提交”与“清空”
        Button btnSubmit = new Button("提交");
        Button btnClear = new Button("清空");
        HBox hb = createButtonLayout(btnSubmit, btnClear);
        restorePage.getChildren().addAll(textAddr, backupGroup, resGroup, hb);

        btnRestore(btnSubmit, btnClear, (TextField) (backupGroup.getChildren().get(1)),
                (TextField) resGroup.getChildren().get(1), rootSp);
        return restorePage;
    }

    // 时间输入框
    private static InputGroup createTimeFilterGroup(String prompt){
        InputGroup res = new InputGroup();
        CheckBox cb = createCheckBox(prompt, PROMPT_WIDTH_RADIO);
        TextField timeStartPrompt = createCustomField("起始", INNER_PROMPT_WIDTH_RADIO, false);
        TextField timeEndPrompt = createCustomField("终止", INNER_PROMPT_WIDTH_RADIO, false);
        TextField timeStart = createCustomField("", TIME_WIDTH_RADIO, true);
        TextField timeEnd = createCustomField("", TIME_WIDTH_RADIO, true);
        ChoiceBox<String> cob = createChoiceBox(new String[]{"包含", "排除"}, BUTTON_WIDTH_RADIO);

        cob.setValue("排除");
        timeStart.setTextFormatter(new TextFormatter<>(new LocalDateTimeStringConverter(timeFormat, null)));
        timeEnd.setTextFormatter(new TextFormatter<>(new LocalDateTimeStringConverter(timeFormat, null)));
        res.getChildren().addAll(cb, timeStartPrompt, timeStart, timeEndPrompt, timeEnd, cob);

        return res;
    }

    // 大小输入框
    private static InputGroup createSizeFilterGroup(){
        CheckBox cb = createCheckBox("文件大小", PROMPT_WIDTH_RADIO);
        TextField sizeMinPrompt = createCustomField("最小", INNER_PROMPT_WIDTH_RADIO, false);
        TextField sizeMaxPrompt = createCustomField("最大", INNER_PROMPT_WIDTH_RADIO, false);
        TextField sizeMin = createCustomField("", SIZE_WIDTH_RADIO, true);
        TextField sizeMax = createCustomField("", SIZE_WIDTH_RADIO, true);
        TextField sizeUnit1 = createCustomField("KB", INNER_UNIT_WIDTH_RADIO, false);
        TextField sizeUnit2 = createCustomField("KB", INNER_UNIT_WIDTH_RADIO, false);

        sizeUnit1.setEditable(false);
        sizeUnit2.setEditable(false);
        sizeMin.setTextFormatter(new TextFormatter<>(new LongStringConverter()));
        sizeMax.setTextFormatter(new TextFormatter<>(new LongStringConverter()));
        ChoiceBox<String> cob = createChoiceBox(new String[]{"包含", "排除"}, BUTTON_WIDTH_RADIO);
        cob.setValue("排除");

        return new InputGroup(cb, sizeMinPrompt, sizeMin, sizeUnit1, sizeMaxPrompt, sizeMax, sizeUnit2, cob);
    }

    // 文本框组合，使用的是textField
    private static InputGroup createAddrGroup(String prompt, boolean isFile){
        // 提示文本框
        TextField tfPrompt = createCustomField(prompt, PROMPT_WIDTH_RADIO, false);
        // 内容显示文本框
        TextField tf = createCustomField("", CONTENT_WIDTH_RADIO, false);
        // 文件选择按钮
        Button btn = createBtnFileChoose(tf, isFile);

        return new InputGroup(tfPrompt, tf, btn);
    }

    // 文本框组合，使用的是
    // fileType: 0-目录；1-文件；2-目录+文件
    public static InputGroup createTextFilterGroup(String prompt){
        CheckBox cb = createCheckBox(prompt, PROMPT_WIDTH_RADIO);
        TextArea ta = createCustomArea("", CONTENT_WIDTH_RADIO, true);

        ChoiceBox cob = createChoiceBox(new String[]{"包含", "排除"}, BUTTON_WIDTH_RADIO);
        cob.setValue("排除");

        return new InputGroup(cb, ta, cob);
    }

    public static InputGroup createFileFilterGroup(String prompt, boolean isFile){
        CheckBox cb = createCheckBox(prompt, PROMPT_WIDTH_RADIO);
        TextArea ta = createCustomArea("", CONTENT_WIDTH_RADIO, true);
        Button btn=createBtnFileChoose(ta, isFile);
        return new InputGroup(cb, ta, btn);
    }



    public static Button createBtnFileChoose(TextInputControl tc, boolean isFile){
        Button btn = new Button("", new FontIcon(btnFileIcon));
        btn.setPrefWidth(WIDTH*BUTTON_WIDTH_RADIO);
        btn.getStyleClass().addAll(Styles.BUTTON_ICON);
        Stage stage = new Stage();

        if(isFile){ // 文件选择
            FileChooser fc = new FileChooser();
            File projectDir = new File(System.getProperty("user.dir"));
            fc.setInitialDirectory(projectDir);
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
            File projectDir = new File(System.getProperty("user.dir"));
            dc.setInitialDirectory(projectDir);
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
                            createPopup("文件备份成功", rootSp); // 备份成功
                        else
                            createPopup("文件备份失败！备份目录不存在！", rootSp); // 备份失败
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
                if(res.get().getText().equals("Yes")){
                    String backFilePath = tfBackupRes.getText();
                    File backFile = new File(backFilePath);
                    File headFile = new File(backFilePath+"_head");
                    // 备份文件存在性检验
                    if(!backFile.exists()){ // 检验输入的备份文件是否存在
                        try {
                            createPopup("恢复失败！备份文件不存在！", rootSp);
                            return;
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else if(!headFile.exists()){ // 检验输入的备份文件是否存在对应的head文件
                        try {
                            createPopup("恢复失败！备份文件缺乏对应head文件！", rootSp);
                            return;
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    // 恢复文件管理器初始化
                    resM.initResManager(tfRes.getText());
                    try {
                        resM.initHead(backFilePath);
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
                    } catch (ReedSolomonException e) {
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
                        try {
                            createPopup("备份文件密码错误！", rootSp);
                            tfBackupRes.clear();
                            tfRes.clear();
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    } catch (InvalidKeySpecException e) {
                        throw new RuntimeException(e);
                    } catch (InvalidKeyException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    public static void initEncoder(InputGroup compressGroup, InputGroup encryptGroup, StackPane sp){
        CheckBox cbCompress = (CheckBox) compressGroup.getChildren().get(0);
        CheckBox cbEncrypt = (CheckBox) encryptGroup.getChildren().get(0);
        BackManager backM = BackManager.getInstance();

        if(cbCompress.isSelected())
        {
            ComboBox<String> cmb = (ComboBox<String>) compressGroup.getChildren().get(1);
            String compressType = cmb.getSelectionModel().getSelectedItem();
            backM.setCompressType(compressType);
        }
        if(cbEncrypt.isSelected()){
            ComboBox<String> cmb = (ComboBox<String>) encryptGroup.getChildren().get(1);
            String encryptType = cmb.getSelectionModel().getSelectedItem();
            if(!encryptType.equals("无")){
                String prompt = "您选择了文件加密，请输入您的密码（密码长度不大于32位）:";
                String password = createPasswordDialog(prompt, sp);
                backM.setPassword(password);
            }
            backM.setEncryptType(encryptType);
        }
    }

    public static void initFilter(InputGroup formatGroup, InputGroup nameGroup, InputGroup sizeGroup,
                                  InputGroup createTimeGroup, InputGroup modifiedTimeGroup, InputGroup accessTimeGroup,
                                  InputGroup fileGroup, InputGroup dirGroup){
        CheckBox cbFormat = (CheckBox) formatGroup.getChildren().get(0);
        CheckBox cbName = (CheckBox) nameGroup.getChildren().get(0);
        CheckBox cbSize = (CheckBox) sizeGroup.getChildren().get(0);
        CheckBox cbCreateTime = (CheckBox) createTimeGroup.getChildren().get(0);
        CheckBox cbModifiedTime = (CheckBox) modifiedTimeGroup.getChildren().get(0);
        CheckBox cbAccessTime = (CheckBox) accessTimeGroup.getChildren().get(0);
        CheckBox cbFile = (CheckBox) fileGroup.getChildren().get(0);
        CheckBox cbDir = (CheckBox) dirGroup.getChildren().get(0);
        SrcManager srcM = SrcManager.getInstance();
        TextArea ta;
        TextField tfStart, tfEnd;
        ChoiceBox cob;

        if(cbFormat.isSelected()){
            ta = (TextArea) formatGroup.getChildren().get(1);
            cob = (ChoiceBox) formatGroup.getChildren().get(2);
            srcM.setFilterFormat(ta.getText(), (String) cob.getValue());
        }
        if(cbName.isSelected()){
            ta = (TextArea) nameGroup.getChildren().get(1);
            cob = (ChoiceBox) nameGroup.getChildren().get(2);
            srcM.setFilterName(ta.getText(), (String) cob.getValue());
        }
        if(cbSize.isSelected()){
            tfStart = (TextField) sizeGroup.getChildren().get(2);
            tfEnd = (TextField) sizeGroup.getChildren().get(5);
            cob = (ChoiceBox) sizeGroup.getChildren().get(7);
            srcM.setFilterSize(Long.parseLong(tfStart.getText()), Long.parseLong(tfEnd.getText()), (String) cob.getValue());
        }
        if(cbCreateTime.isSelected()){
            tfStart = (TextField) createTimeGroup.getChildren().get(2);
            tfEnd = (TextField) createTimeGroup.getChildren().get(4);
            cob = (ChoiceBox) createTimeGroup.getChildren().get(5);
            srcM.setFilterTime(LocalDateTime.from(timeFormat.parse(tfStart.getText())),
                    LocalDateTime.from(timeFormat.parse(tfEnd.getText())), "create", (String) cob.getValue());
        }
        if(cbModifiedTime.isSelected()){
            tfStart = (TextField) modifiedTimeGroup.getChildren().get(2);
            tfEnd = (TextField) modifiedTimeGroup.getChildren().get(4);
            cob = (ChoiceBox) modifiedTimeGroup.getChildren().get(5);
            srcM.setFilterTime(LocalDateTime.from(timeFormat.parse(tfStart.getText())),
                    LocalDateTime.from(timeFormat.parse(tfEnd.getText())), "modified", (String) cob.getValue());
        }
        if(cbAccessTime.isSelected()){
            tfStart = (TextField) accessTimeGroup.getChildren().get(2);
            tfEnd = (TextField) accessTimeGroup.getChildren().get(4);
            cob = (ChoiceBox) accessTimeGroup.getChildren().get(5);
            srcM.setFilterTime(LocalDateTime.from(timeFormat.parse(tfStart.getText())),
                    LocalDateTime.from(timeFormat.parse(tfEnd.getText())), "access", (String) cob.getValue());
        }

        if(cbFile.isSelected()){
            ta = (TextArea) fileGroup.getChildren().get(1);
            srcM.setFilterFile(ta.getText());
        }
        if(cbDir.isSelected()){
            ta = (TextArea) dirGroup.getChildren().get(1);
            srcM.setFilterDir(ta.getText());
        }

    }
}
