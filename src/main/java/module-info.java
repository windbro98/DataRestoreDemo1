module com.ui.datarestoredemo1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.material2;
    requires org.kordamp.ikonli.feather;
    requires com.alibaba.fastjson2;
    requires com.google.zxing;
    requires junit;


    opens com.ui to javafx.fxml;
    exports com.ui;
    exports com.test;
    opens com.test to javafx.fxml;
    exports com.util;
    opens com.util to javafx.fxml;
    exports com;
    opens com to javafx.fxml;
    exports com.util.encrypt;
    opens com.util.encrypt to javafx.fxml;
    exports com.util.compress;
    opens com.util.compress to javafx.fxml;
    exports com.util.page;
    opens com.util.page to javafx.fxml;
    exports com.util.redundancyCheck;
    opens com.util.redundancyCheck to javafx.fxml;
}