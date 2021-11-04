package com.fx.dense.utils;

import javafx.scene.Node;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-11-04 11:12
 **/
public class GetScreenUtil {
    public GetScreenUtil() {
    }

    public static double getScreenX(Node control) {
        return control.getScene().getWindow().getX() + control.getScene().getX() + control.localToScene(0.0D, 0.0D).getX();
    }

    public static double getScreenY(Node control) {
        return control.getScene().getWindow().getY() + control.getScene().getY() + control.localToScene(0.0D, 0.0D).getY();
    }

    public static double getWidth(Node control) {
        return control.getBoundsInParent().getWidth();
    }

    public static double getHeight(Node control) {
        return control.getBoundsInParent().getHeight();
    }
}
