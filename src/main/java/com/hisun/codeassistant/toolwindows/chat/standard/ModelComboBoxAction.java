package com.hisun.codeassistant.toolwindows.chat.standard;

import com.hisun.codeassistant.HiCodeAssistantIcons;
import com.hisun.codeassistant.conversations.ConversationService;
import com.hisun.codeassistant.conversations.ConversationsState;
import com.hisun.codeassistant.enums.ModelEnum;
import com.hisun.codeassistant.llms.client.openai.completion.OpenAIChatCompletionModel;
import com.hisun.codeassistant.settings.service.ServiceType;
import com.hisun.codeassistant.settings.state.OpenAISettingsState;
import com.hisun.codeassistant.settings.state.SelfHostedLanguageModelSettingsState;
import com.hisun.codeassistant.settings.state.SettingsState;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

public class ModelComboBoxAction extends ComboBoxAction {
    private final Runnable onAddNewTab;
    private final SettingsState settings;
    private final OpenAISettingsState openAISettings;
    private final SelfHostedLanguageModelSettingsState selfHostedLanguageModelSettings;

    public ModelComboBoxAction(Runnable onAddNewTab, ServiceType selectedService) {
        this.onAddNewTab = onAddNewTab;
        settings = SettingsState.getInstance();
        openAISettings = OpenAISettingsState.getInstance();
        selfHostedLanguageModelSettings = SelfHostedLanguageModelSettingsState.getInstance();
        updateTemplatePresentation(selectedService);
    }

    public JComponent createCustomComponent(@NotNull String place) {
        return createCustomComponent(getTemplatePresentation(), place);
    }

    @NotNull
    @Override
    public JComponent createCustomComponent(
            @NotNull Presentation presentation,
            @NotNull String place) {
        ComboBoxButton button = createComboBoxButton(presentation);
        button.setBorder(null);
        return button;
    }

    @Override
    protected @NotNull DefaultActionGroup createPopupActionGroup(@NotNull JComponent button, @NotNull DataContext dataContext) {
        var presentation = ((ComboBoxButton) button).getPresentation();
        var actionGroup = new DefaultActionGroup();
        actionGroup.addSeparator("OpenAI");
        List.of(
                        OpenAIChatCompletionModel.GPT_4_1106_128k,
                        OpenAIChatCompletionModel.GPT_3_5_1106_16k,
                        OpenAIChatCompletionModel.GPT_4_32k,
                        OpenAIChatCompletionModel.GPT_4,
                        OpenAIChatCompletionModel.GPT_3_5)
                .forEach(
                        model -> actionGroup.add(createOpenAIModelAction(model, presentation)));
        actionGroup.addSeparator("HiCodeAssistant");
        List.of(ModelEnum.values()).forEach(modelEnum -> actionGroup.add(createSelfHostedLanguageModelAction(modelEnum, presentation)));
        return actionGroup;
    }

    @Override
    protected boolean shouldShowDisabledActions() {
        return true;
    }

    private void updateTemplatePresentation(ServiceType selectedService) {
        var templatePresentation = getTemplatePresentation();
        switch (selectedService) {
            case OPENAI:
                templatePresentation.setIcon(HiCodeAssistantIcons.OPENAI_ICON);
                templatePresentation.setText(OpenAIChatCompletionModel.findByCode(openAISettings.getModel()).getDescription());
                break;
            case SELF_HOSTED:
                templatePresentation.setIcon(HiCodeAssistantIcons.SYSTEM_ICON);
                templatePresentation.setText(ModelEnum.fromName(selfHostedLanguageModelSettings.getModel()).getDisplayName());
                break;
            default:
        }
    }

    private AnAction createModelAction(
            ServiceType serviceType,
            String label,
            Icon icon,
            Presentation comboBoxPresentation) {
        return new AnAction(label, "", icon) {
            @Override
            public void update(@NotNull AnActionEvent event) {
                var presentation = event.getPresentation();
                presentation.setEnabled(!presentation.getText().equals(comboBoxPresentation.getText()));
            }

            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                handleProviderChange(serviceType, label, icon, comboBoxPresentation);
            }
        };
    }

    private void handleProviderChange(
            ServiceType serviceType,
            String label,
            Icon icon,
            Presentation comboBoxPresentation) {
        settings.setSelectedService(serviceType);
        comboBoxPresentation.setIcon(icon);
        comboBoxPresentation.setText(label);

        var currentConversation = ConversationsState.getCurrentConversation();
        if (currentConversation != null && !currentConversation.getMessages().isEmpty()) {
            onAddNewTab.run();
        } else {
            ConversationService.getInstance().startConversation();
        }
    }

    private AnAction createOpenAIModelAction(
            OpenAIChatCompletionModel model,
            Presentation comboBoxPresentation) {
        createModelAction(ServiceType.OPENAI, model.getDescription(), HiCodeAssistantIcons.OPENAI_ICON,
                comboBoxPresentation);
        return new AnAction(model.getDescription(), "", HiCodeAssistantIcons.OPENAI_ICON) {
            @Override
            public void update(@NotNull AnActionEvent event) {
                var presentation = event.getPresentation();
                presentation.setEnabled(!presentation.getText().equals(comboBoxPresentation.getText()));
            }

            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                openAISettings.setModel(model.getCode());
                handleProviderChange(
                        ServiceType.OPENAI,
                        model.getDescription(),
                        HiCodeAssistantIcons.OPENAI_ICON,
                        comboBoxPresentation);
            }
        };
    }

    private AnAction createSelfHostedLanguageModelAction(
            ModelEnum model,
            Presentation comboBoxPresentation) {
        createModelAction(ServiceType.SELF_HOSTED, model.getDisplayName(), HiCodeAssistantIcons.SYSTEM_ICON,
                comboBoxPresentation);
        return new AnAction(model.getDisplayName(), "", HiCodeAssistantIcons.SYSTEM_ICON) {
            @Override
            public void update(@NotNull AnActionEvent event) {
                var presentation = event.getPresentation();
                presentation.setEnabled(!presentation.getText().equals(comboBoxPresentation.getText()));
            }

            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                selfHostedLanguageModelSettings.setModel(model.getName());
                handleProviderChange(
                        ServiceType.SELF_HOSTED,
                        model.getDisplayName(),
                        HiCodeAssistantIcons.SYSTEM_ICON,
                        comboBoxPresentation);
            }
        };
    }
}