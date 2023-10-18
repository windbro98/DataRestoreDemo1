module com.ui.datarestoredemo1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.material2;
    requires org.kordamp.ikonli.feather;
    requires com.alibaba.fastjson2;


    opens com.ui to javafx.fxml;
    exports com.ui;
    exports com.test;
    opens com.test to javafx.fxml;
    exports com.util;
    opens com.util to javafx.fxml;
    exports com;
    opens com to javafx.fxml;
}