module de.greluc.sc.sckillmonitor {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires atlantafx.base;
    requires org.jetbrains.annotations;
    requires static lombok;
    requires java.prefs;

    opens de.greluc.sc.sckillmonitor to javafx.fxml;
    exports de.greluc.sc.sckillmonitor;
    exports de.greluc.sc.sckillmonitor.controller;
    opens de.greluc.sc.sckillmonitor.controller to javafx.fxml;
    exports de.greluc.sc.sckillmonitor.data;
    opens de.greluc.sc.sckillmonitor.data to javafx.fxml;
}