module Catan {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens ui to javafx.graphics, javafx.fxml;
    opens logic to javafx.base;

    exports ui;
    exports logic;
    exports structures;
}
