module pet_class {
    requires com.dustinredmond.fxtrayicon;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires com.alibaba.fastjson2;

    exports pet_class to javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    opens org.example.demo2 to javafx.fxml;
    exports org.example.demo2;
}
