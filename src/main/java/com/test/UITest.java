package com.test;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class UITest extends Application {
    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FileChooser fc = new FileChooser();
        Button btn = new Button("选择文件：");
        Group root = new Group();
        fc.setTitle("请选择您所需要的文件");
        btn.setOnMouseClicked(mouseEvent -> {
            Stage stage1 = new Stage();
            File file = fc.showOpenDialog(stage1);
            System.out.println(file);
        });
        root.getChildren().add(btn);
        Scene sc = new Scene(root);
        stage.setScene(sc);
        stage.show();
    }
}
