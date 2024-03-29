package com.ui;

import atlantafx.base.layout.InputGroup;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.IntegerStringConverter;
import com.entity.BackManager;
import com.entity.ResManager;
import com.entity.SrcManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.util.converter.DateTimeStringConverter;
import javafx.util.converter.LocalDateTimeStringConverter;
import javafx.util.converter.LongStringConverter;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static com.util.FileToolUtil.tfIsEmpty;
import static com.util.StyleUtil.*;
import static com.util.StyleUtil.createPopup;

// UI设计
public class PageFactory {

    public static Material2MZ btnFileIcon = Material2MZ.PAGEVIEW;
    public static DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    // 构造方法私有化
    private PageFactory(){
    }

    // 备份页面初始化
    public static VBox initBackupPage(StackPane rootSp){
        VBox backupPage = new VBox();

        // “源地址”文本框
        InputGroup srcGroup = createInputGroup("源地址", false); // 源目录
        // “备份地址”文本框
        InputGroup backupGroup = createInputGroup("备份地址", false); //备份文件路径
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
        // "提交"与"清空"按钮
        Button btnSubmit = new Button("提交");
        Button btnClear = new Button("清空");
        HBox hb = new HBox(btnSubmit, btnClear);
        backupPage.getChildren().addAll(srcGroup, backupGroup, formatGroup, nameGroup, sizeGroup, createTimeGroup,
                modifiedTimeGroup, accessTimeGroup, fileGroup, dirGroup, hb);
        //
        TextField tfSrc = (TextField) srcGroup.getChildren().get(1);
        TextField tfBackupSave = (TextField) backupGroup.getChildren().get(1);

        btnBackup(btnSubmit, btnClear, tfSrc, tfBackupSave, formatGroup, nameGroup, sizeGroup, createTimeGroup,
                modifiedTimeGroup, accessTimeGroup, fileGroup, dirGroup,  rootSp);
        return backupPage;
    };

    // 恢复页面初始化
    public static VBox initRestorePage(StackPane rootSp){
        VBox restorePage = new VBox();

        // 文本框
        InputGroup backupGroup = createInputGroup("备份地址", true); // 备份文件路径
        InputGroup resGroup = createInputGroup("恢复地址", false); // 恢复路径
        // 按钮“提交”与“清空”
        Button btnSubmit = new Button("提交");
        Button btnClear = new Button("清空");
        HBox hb = new HBox(btnSubmit, btnClear);
        restorePage.getChildren().addAll(backupGroup, resGroup, hb);

        btnRestore(btnSubmit, btnClear, (TextField) (backupGroup.getChildren().get(1)), (TextField) resGroup.getChildren().get(1), rootSp);
        return restorePage;
    }

    // 时间输入框
    private static InputGroup createTimeFilterGroup(String prompt){
        InputGroup res = new InputGroup();
        CheckBox cb = new CheckBox(prompt);
        TextField timeStart = new TextField();
        TextField timeEnd = new TextField();
        ChoiceBox<String> cob = createChoiceBox(new String[]{"包含", "排除"});

        cob.setValue("排除");
        res.getChildren().add(cb);
        timeStart.setTextFormatter(new TextFormatter<>(new LocalDateTimeStringConverter(timeFormat, null)));
        timeEnd.setTextFormatter(new TextFormatter<>(new LocalDateTimeStringConverter(timeFormat, null)));
        res.getChildren().addAll(timeStart, timeEnd, cob);

        return res;
    }

    // 大小输入框
    private static InputGroup createSizeFilterGroup(){
        CheckBox cb = new CheckBox("文件大小");
        TextField sizeMin = new TextField();
        TextField sizeMax = new TextField();
        TextField sizeUnit1 = new TextField("KB");
        TextField sizeUnit2 = new TextField("KB");

        sizeUnit1.setEditable(false);
        sizeUnit2.setEditable(false);
        sizeMin.setTextFormatter(new TextFormatter<>(new LongStringConverter()));
        sizeMax.setTextFormatter(new TextFormatter<>(new LongStringConverter()));
        ChoiceBox<String> cob = createChoiceBox(new String[]{"包含", "排除"});
        cob.setValue("排除");

        return new InputGroup(cb, sizeMin, sizeUnit1, sizeMax, sizeUnit2, cob);
    }

    // 文本框组合，使用的是textField
    private static InputGroup createInputGroup(String prompt, boolean isFile){
        // 提示文本框
        TextField tfPrompt = new TextField(prompt);
        tfPrompt.setEditable(false);
        // 内容显示文本框
        TextField tf = new TextField();
        tf.setEditable(false);
        // 文件选择按钮
        Button btn = createBtnFileChoose(tf, isFile);

        return new InputGroup(tfPrompt, tf, btn);
    }

    // 文本框组合，使用的是
    // fileType: 0-目录；1-文件；2-目录+文件
    public static InputGroup createTextFilterGroup(String prompt){
        TextArea ta = new TextArea();
        CheckBox cb = new CheckBox(prompt);

        ChoiceBox cob = createChoiceBox(new String[]{"包含", "排除"});
        cob.setValue("排除");

        return new InputGroup(cb, ta, cob);
    }

    public static InputGroup createFileFilterGroup(String prompt, boolean isFile){
        TextArea ta = new TextArea();
        CheckBox cb = new CheckBox(prompt);
        Button btn=createBtnFileChoose(ta, isFile);
        return new InputGroup(cb, ta, btn);
    }

    public static Button createBtnFileChoose(TextInputControl tc, boolean isFile){
        Button btn = new Button("", new FontIcon(btnFileIcon));
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

    public static ChoiceBox<String> createChoiceBox(String[] strList){
        ChoiceBox<String> cob = new ChoiceBox<>();
        cob.getItems().addAll(strList);
        return cob;
    }

    // 备份页面的提交按钮
    public static void btnBackup(Button btnSubmit, Button btnClear, TextField tfSrc, TextField tfBackupSave,
                                 InputGroup formatGroup, InputGroup nameGroup, InputGroup sizeGroup,
                                 InputGroup createTimeGroup, InputGroup modifiedTimeGroup, InputGroup accessTimeGroup,
                                 InputGroup fileGroup, InputGroup dirGroup, StackPane rootSp){
        SrcManager srcM = SrcManager.getInstance();
        BackManager backM = BackManager.getInstance();
        // 清空
        btnClear.setOnMouseClicked(mouseEvent -> {
            backupClear(tfSrc, tfBackupSave, formatGroup, nameGroup, sizeGroup, createTimeGroup, modifiedTimeGroup,
                    accessTimeGroup, fileGroup, dirGroup);
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
                        srcM.initSrcManager(tfSrc.getText());
                        backM.initBackManager(tfBackupSave.getText(), "", "");
                        // 备份文件提取
                        boolean backFlag = backM.fileExtract(srcM.getFilePathSet(), srcM.getSrcDir());
                        // 提示窗口
                        if(backFlag)
                            createPopup("文件备份成功", rootSp); // 备份成功
                        else
                            createPopup("文件备份失败！备份目录不存在！", rootSp); // 备份失败
                        // 清空文本框，准备下次备份
                        backupClear(tfSrc, tfBackupSave, formatGroup, nameGroup, sizeGroup, createTimeGroup, modifiedTimeGroup,
                                accessTimeGroup, fileGroup, dirGroup);
                    } catch (InterruptedException e) {
                        // 错误：超时
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    public static void backupClear(TextField tfSrc, TextField tfBackupSave, InputGroup formatGroup, InputGroup nameGroup,
                                   InputGroup sizeGroup, InputGroup createTimeGroup, InputGroup modifiedTimeGroup,
                                   InputGroup accessTimeGroup, InputGroup fileGroup, InputGroup dirGroup){
        ArrayList<InputGroup> taArr = new ArrayList<>();
        ArrayList<InputGroup> timeArr = new ArrayList<>();
        CheckBox cb;

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
            TextField tfStart = (TextField) ig.getChildren().get(1);
            TextField tfEnd = (TextField) ig.getChildren().get(2);
            tfStart.clear();
            tfEnd.clear();
            if(cb.isSelected()) cb.setSelected(false);
        }
        // long型InputGroup
        cb = (CheckBox) sizeGroup.getChildren().get(0);
        TextField tfMin = (TextField) sizeGroup.getChildren().get(1);
        TextField tfMax = (TextField) sizeGroup.getChildren().get(3);
        tfMin.clear();
        tfMax.clear();
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
            tfStart = (TextField) sizeGroup.getChildren().get(1);
            tfEnd = (TextField) sizeGroup.getChildren().get(3);
            cob = (ChoiceBox) sizeGroup.getChildren().get(5);
            srcM.setFilterSize(Long.parseLong(tfStart.getText()), Long.parseLong(tfEnd.getText()), (String) cob.getValue());
        }
        if(cbCreateTime.isSelected()){
            tfStart = (TextField) createTimeGroup.getChildren().get(1);
            tfEnd = (TextField) createTimeGroup.getChildren().get(2);
            cob = (ChoiceBox) createTimeGroup.getChildren().get(3);
            srcM.setFilterTime(LocalDateTime.from(timeFormat.parse(tfStart.getText())),
                    LocalDateTime.from(timeFormat.parse(tfStart.getText())), "create", (String) cob.getValue());
        }
        if(cbModifiedTime.isSelected()){
            tfStart = (TextField) modifiedTimeGroup.getChildren().get(1);
            tfEnd = (TextField) modifiedTimeGroup.getChildren().get(2);
            cob = (ChoiceBox) modifiedTimeGroup.getChildren().get(3);
            srcM.setFilterTime(LocalDateTime.from(timeFormat.parse(tfStart.getText())),
                    LocalDateTime.from(timeFormat.parse(tfStart.getText())), "modified", (String) cob.getValue());
        }
        if(cbAccessTime.isSelected()){
            tfStart = (TextField) accessTimeGroup.getChildren().get(1);
            tfEnd = (TextField) accessTimeGroup.getChildren().get(2);
            cob = (ChoiceBox) accessTimeGroup.getChildren().get(3);
            srcM.setFilterTime(LocalDateTime.from(timeFormat.parse(tfStart.getText())),
                    LocalDateTime.from(timeFormat.parse(tfStart.getText())), "access", (String) cob.getValue());
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
