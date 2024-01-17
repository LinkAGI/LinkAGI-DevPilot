package com.hisun.codeassistant.utils;

import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;

import javax.swing.*;
import java.awt.*;

public class BalloonAlertUtils {
    public static void showErrorAlert(String alertMsg, int dx, int dy, Balloon.Position position) {
        crateBalloonAlert(alertMsg, dx, dy, MessageType.ERROR, position);
    }
    private static void crateBalloonAlert(String alertMsg, int dx, int dy, MessageType message, Balloon.Position position) {
        JLabel label = new JLabel(alertMsg);
        // Balloon alert is displayed near the mouse.
        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
        mouseLocation.translate(dx, dy);
        Balloon balloon = JBPopupFactory.getInstance()
                .createBalloonBuilder(label)
                .setFillColor(message.getPopupBackground())
                .setBorderColor(message.getBorderColor())
                .setHideOnAction(true)
                .setHideOnFrameResize(true)
                .setHideOnKeyOutside(true)
                .setFadeoutTime(2000)
                .createBalloon();
        balloon.show(RelativePoint.fromScreen(mouseLocation), position);
    }
}