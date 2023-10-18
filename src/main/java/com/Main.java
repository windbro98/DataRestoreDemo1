package com;

import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Styles;
import com.domain.BackManager;
import com.domain.ResManager;
import com.domain.SrcManager;
import com.ui.PageFactory;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

import static com.util.StyleUtil.*;
import static com.util.DataUtils.getIndexForArray;

public class Main extends Application {
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

        Scene scene = new Scene(sp);
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
                sp.getItems().remove(1);
                sp.getItems().add(1, page);
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
                        srcM = new SrcManager(tfSrc.getText());
                        backM = new BackManager(tfBackupSave.getText(), "", "");
                        String backFilePath = backM.fileExtract(srcM.getFilePathSet(), srcM.getSrcDir(), srcM.getSrcSize());
                        backM.setBackFilePath(backFilePath);
                    });
                    break;
                case "恢复":
                    rightPages[i] = PageFactory.initRestorePage(tfBackupSel, tfRestore);
                    hb = (HBox) rightPages[i].getChildren().get(2);
                    btnSubmit = (Button) hb.getChildren().get(0);
                    btnSubmit.setOnMouseClicked(mouseEvent -> {
                        resM = new ResManager(tfRestore.getText(), "", "");
                        try {
                            resM.fileRestore(tfBackupSel.getText());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
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