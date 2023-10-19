package com;

import atlantafx.base.controls.Notification;
import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Styles;
import com.domain.BackManager;
import com.domain.ResManager;
import com.domain.SrcManager;
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

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static com.util.FileToolUtil.fileExistEval;
import static com.util.FileToolUtil.tfIsEmpty;
import static com.util.StyleUtil.*;
import static com.util.DataUtils.getIndexForArray;

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

        TextField tfSrc = new TextField();
        TextField tfBackupSave = new TextField();
        TextField tfBackupSel = new TextField();
        TextField tfRestore = new TextField();

        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        VBox leftMenu = new VBox();
        VBox.setVgrow(leftMenu, Priority.ALWAYS);

        stage.setTitle("数据恢复软件");

        initRightPage(rightPages, leftMenuInfo, tfSrc, tfBackupSave, tfBackupSel, tfRestore);

        SplitPane sp = new SplitPane(
                leftMenu,
                rightPages[0]
        );
        sp.setDividerPositions(0.3);

        initLeftMenu(leftMenu, leftMenuInfo, rightPages, sp, menuBtns);

        rootSp.getChildren().add(sp);
        Scene scene = new Scene(rootSp);
        stage.setScene(scene);
        stage.show();
    }

    void initLeftMenu(VBox leftMenu, String[] leftMenuInfo, Node[] rightPages, SplitPane sp, Button[] menuBtns){
        for (int i = 0; i < leftMenuInfo.length; i++) {
            String info = leftMenuInfo[i];
            Node page = rightPages[i];
            Button btn = new Button(info);
            setNormalMenu(btn);
            btn.getStyleClass().add(Styles.FLAT);
            btn.setOnMouseClicked(mouseEvent -> {
                tmpMenuId = currentMenuId;
                currentMenuId = getIndexForArray(leftMenuInfo, btn.getText());
                if(tmpMenuId!=currentMenuId && tmpMenuId>=0){
                    Button btnTmp = menuBtns[tmpMenuId];
                    setNormalMenu(btnTmp);
                }
                sp.getItems().set(1, page);
            });
            btn.setOnMouseMoved(mouseEvent -> {
                setHoverMenu(btn);
            });
            btn.setOnMouseExited(mouseEvent -> {
                if(currentMenuId==getIndexForArray(leftMenuInfo, btn.getText())){
                    setClickedMenu(btn);
                }else{
                    setNormalMenu(btn);
                }
            });

            leftMenu.getChildren().add(btn);
            menuBtns[i] = btn;
        }
    }

    void initRightPage(VBox[] rightPages, String[] leftMenuInfo, TextField tfSrc,
                       TextField tfBackupSave, TextField tfBackupSel, TextField tfRestore) throws IOException {
        for (int i = 0; i < numMenu; i++) {
            String info = leftMenuInfo[i];
            HBox hb;
            Button btnSubmit;
            switch (info){
                case "备份":
                    rightPages[i] = PageFactory.initBackupPage(tfSrc, tfBackupSave);
                    hb = (HBox) rightPages[i].getChildren().get(2);
                    btnSubmit = (Button) hb.getChildren().get(0);
                    btnSubmit.setOnMouseClicked(mouseEvent -> {
                        if(tfIsEmpty(tfSrc) || tfIsEmpty(tfBackupSave)){
                            Alert errorMsg = createErrorAlert("源路径或备份路径为空！");
                            errorMsg.show();
                        } else {
                                Alert confirmMsg = createConfirmAlert("是否确认信息");
                                Optional<ButtonType> res = confirmMsg.showAndWait();
                            if(res.get().getText().equals("Yes")){
                                try {
                                    srcM = new SrcManager(tfSrc.getText());
                                    backM = new BackManager(tfBackupSave.getText(), "", "");
                                    String backFilePath = backM.fileExtract(srcM.getFilePathSet(), srcM.getSrcDir(), srcM.getSrcSize());
                                    backM.setBackFilePath(backFilePath);
                                    createPopup("文件备份成功", rootSp);
                                    tfSrc.clear();
                                    tfBackupSave.clear();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    });
                    break;
                case "恢复":
                    rightPages[i] = PageFactory.initRestorePage(tfBackupSel, tfRestore);
                    hb = (HBox) rightPages[i].getChildren().get(2);
                    btnSubmit = (Button) hb.getChildren().get(0);
                    btnSubmit.setOnMouseClicked(mouseEvent -> {
                        if(tfIsEmpty(tfBackupSel) || tfIsEmpty(tfRestore)){
                            Alert errorMsg = createErrorAlert("备份路径或恢复路径为空！");
                            errorMsg.show();
                        } else {
                            try {
                                File jsonBackup = new File(tfBackupSel.getText()+".json");
                                if (!fileExistEval(jsonBackup, false)) {
                                    Alert errorMsg = createErrorAlert("备份文件错误！");
                                    tfBackupSel.clear();
                                    errorMsg.show();
                                } else{
                                    Alert confirmMsg = createConfirmAlert("是否确认信息");
                                    Optional<ButtonType> res = confirmMsg.showAndWait();
                                    if(res.get().getText().equals("Yes")){
                                        resM = new ResManager(tfRestore.getText(), "", "");
                                        try {
                                            resM.fileRestore(tfBackupSel.getText());
                                            createPopup("文件恢复成功", rootSp);
                                            tfBackupSel.clear();
                                            tfRestore.clear();
                                        } catch (IOException | InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
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