package com.ui;

import atlantafx.base.layout.InputGroup;
import atlantafx.base.theme.Styles;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.kordamp.ikonli.feather.Feather;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Material;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class PageFactory {
    public static VBox initBackupPage(TextField tfSrc, TextField tfBackup){
        VBox backupPage = new VBox();
        Button btnSubmit = new Button("提交");
        Button btnClear = new Button("清空");
        HBox hb = new HBox(btnSubmit, btnClear);
        btnClear.setOnMouseClicked(mouseEvent -> {
            tfSrc.clear();
            tfBackup.clear();
        });
        String srcPrompt = "源地址";
        String backupPrompt = "备份地址";
        InputGroup srcGroup = createInputGroup(srcPrompt, tfSrc, false);
        InputGroup backupGroup = createInputGroup(backupPrompt, tfBackup, false);
        backupPage.getChildren().addAll(srcGroup, backupGroup, hb);
        return backupPage;
    };

    public static VBox initRestorePage(TextField tfBackup, TextField tfRes){
        VBox restorePage = new VBox();
        Button btnSubmit = new Button("提交");
        Button btnClear = new Button("清空");
        HBox hb = new HBox(btnSubmit, btnClear);
        btnClear.setOnMouseClicked(mouseEvent -> {
            tfBackup.clear();
            tfRes.clear();
        });
        String backupPrompt = "备份地址";
        String resPrompt = "恢复地址";
        InputGroup backupGroup = createInputGroup(backupPrompt, tfBackup, true);
        InputGroup resGroup = createInputGroup(resPrompt, tfRes, false);
        restorePage.getChildren().addAll(backupGroup, resGroup, hb);
        return restorePage;
    }

    private static InputGroup createInputGroup(String prompt, TextField tf, boolean fileType){
        TextField tfPrompt = new TextField(prompt);
        tfPrompt.setEditable(false);
        tf.setEditable(false);
        Button btn = new Button("", new FontIcon(Material2MZ.PAGEVIEW));
        btn.getStyleClass().addAll(Styles.BUTTON_ICON);
        Stage stage = new Stage();
        if(fileType){
            FileChooser fc = new FileChooser();
            File projectDir = new File(System.getProperty("user.dir"));
            fc.setInitialDirectory(projectDir);
            btn.setOnMouseClicked(mouseEvent -> {
            File file = fc.showOpenDialog(stage);
            if(!(file==null))
                tf.setText(file.toString());
            });
        }else {
            DirectoryChooser dc = new DirectoryChooser();
            File projectDir = new File(System.getProperty("user.dir"));
            dc.setInitialDirectory(projectDir);
            btn.setOnMouseClicked(mouseEvent -> {
                File file = dc.showDialog(stage);
                if(!(file==null))
                    tf.setText(file.toString());
            });
        }

        return new InputGroup(tfPrompt, tf, btn);
    }
}
