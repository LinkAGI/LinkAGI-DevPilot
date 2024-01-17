package com.hisun.codeassistant.toolwindows.components.code;

import com.hisun.codeassistant.toolwindows.components.EditorInfo;
import com.hisun.codeassistant.utils.EditorUtils;
import com.intellij.openapi.project.Project;
import com.intellij.ui.SimpleColoredComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.intellij.ui.SimpleTextAttributes.GRAY_ATTRIBUTES;
import static com.intellij.ui.SimpleTextAttributes.REGULAR_ATTRIBUTES;

public class GoToCode extends JPanel {
    public GoToCode(Project project, EditorInfo editorInfo) {

        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setOpaque(false);
        this.setToolTipText(editorInfo.getFilePresentableUrl());
        this.add(createSimpleColoredComponent(editorInfo));
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                EditorUtils.openFileAndSelectLines(project, editorInfo.getFileUrl(),
                        editorInfo.getSelectedStartLine(), editorInfo.getSelectedEndLine());
            }
        });
    }

    private SimpleColoredComponent createSimpleColoredComponent(EditorInfo editorInfo) {
        SimpleColoredComponent simpleColoredComponent = new SimpleColoredComponent();
        simpleColoredComponent.setOpaque(false);
        simpleColoredComponent.setIcon(editorInfo.getFileIcon());
        simpleColoredComponent.append(editorInfo.getFileName(), REGULAR_ATTRIBUTES);
        simpleColoredComponent.append(":" + editorInfo.getSelectedStartLine() + "-" + editorInfo.getSelectedEndLine(),
                GRAY_ATTRIBUTES);
        return simpleColoredComponent;
    }
}