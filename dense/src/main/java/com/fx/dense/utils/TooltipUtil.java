package com.fx.dense.utils;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.stage.Window;
import javafx.util.Duration;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.controlsfx.control.Notifications;
import org.controlsfx.tools.Utils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-11-04 10:26
 **/
public class TooltipUtil {

    public TooltipUtil() {
    }

    public static void showToast(String message) {
        showToast((Node) null, message);
    }

    public static void showToast(Node node, String message) {
        Window window = Utils.getWindow(node);
        double x = 0.0D;
        double y = 0.0D;
        if (node != null) {
            x = GetScreenUtil.getScreenX(node) + GetScreenUtil.getWidth(node) / 2.0D;
            y = GetScreenUtil.getScreenY(node) + GetScreenUtil.getHeight(node);
        } else {
            x = window.getX() + window.getWidth() / 2.0D;
            y = window.getY() + window.getHeight();
        }

        showToast(window, message, 3000L, x, y);
    }

    public static void showToast(Window window, String message, long time, double x, double y) {
        final Tooltip tooltip = new Tooltip(message);
        tooltip.setAutoHide(true);
        tooltip.setOpacity(0.9D);
        tooltip.setWrapText(true);
        tooltip.show(window, x, y);
        tooltip.setAnchorX(tooltip.getAnchorX() - tooltip.getWidth() / 2.0D);
        tooltip.setAnchorY(tooltip.getAnchorY() - tooltip.getHeight());
        if (time > 0L) {
            // 多线程
            ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
                    new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());
            executorService.scheduleAtFixedRate(() ->
                    Platform.runLater(() -> {
                        tooltip.hide();
                    }), 1, 2, TimeUnit.SECONDS);
        }

    }

    public static void showToast(String message, Pos pos) {
        showToast((String) null, message, (Node) null, 3.0D, pos, (EventHandler) null, (Object) null, true, true);
    }

    public static void showToast(String title, String message) {
        showToast(title, message, (Node) null, 3.0D, Pos.BOTTOM_CENTER, (EventHandler) null, (Object) null, true, true);
    }

    public static void showToast(String title, String message, Pos pos) {
        showToast(title, message, (Node) null, 3.0D, pos, (EventHandler) null, (Object) null, true, true);
    }

    public static void showToast(String title, String message, Node graphic, double hideTime, Pos pos, EventHandler<ActionEvent> onAction, Object owner, boolean isHideCloseButton, boolean isDarkStyle) {
        Notifications notificationBuilder = Notifications.create().title(title).text(message).graphic(graphic).hideAfter(Duration.seconds(hideTime)).position(pos).onAction(onAction);
        if (owner != null) {
            notificationBuilder.owner(owner);
        }

        if (isHideCloseButton) {
            notificationBuilder.hideCloseButton();
        }

        if (isDarkStyle) {
            notificationBuilder.darkStyle();
        }

        Platform.runLater(() -> {
            notificationBuilder.show();
        });
    }

}
