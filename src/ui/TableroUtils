package ui;

import javafx.geometry.Point2D;
import model.HexCoord;

public class TableroUtils {
    public static final double TAMAÑO = 50.0;

    public static double[] hexToPixel(HexCoord coord) {
        double x = TAMAÑO * 3.0/2.0 * coord.q;
        double y = TAMAÑO * Math.sqrt(3.0) * (coord.r + coord.q / 2.0);
        return new double[]{x + 500, y + 500};
    }

    public static Point2D getEsquinaHexagono(double centroX, double centroY, int esquina) {
        double angle_rad = Math.toRadians(60 * esquina);
        return new Point2D(centroX + 50.0 * Math.cos(angle_rad),
                centroY + 50.0 * Math.sin(angle_rad));
    }
}
