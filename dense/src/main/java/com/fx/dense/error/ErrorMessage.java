package com.fx.dense.error;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import lombok.Data;

import java.util.Objects;

/**
 * @author Administrator
 */
@Data
public class ErrorMessage {

    public static String errorMes(String e){
        return e;
    }
}
