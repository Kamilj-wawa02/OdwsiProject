package com.example.pw_odwsi_project.service;

import com.example.pw_odwsi_project.util.WebUtils;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service
public class BindingErrorHandler {

    public static final String BINDING_ERROR_MSG = "Some of the provided data are invalid. Available additional information: ";

    public void handleBindingError(BindingResult bindingResult, Model model) {
        model.addAttribute(WebUtils.MSG_ERROR, formatErrorMessage(bindingResult));
    }

    public void handleBindingError(BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, formatErrorMessage(bindingResult));
    }

    public String formatErrorMessage(BindingResult bindingResult) {
        final String[] msg = {BINDING_ERROR_MSG};
        bindingResult.getAllErrors()
                .stream().filter(error -> error.getDefaultMessage() != null)
                .forEach(error -> msg[0] += (msg[0].endsWith(BINDING_ERROR_MSG) ? "" : ", ") + error.getDefaultMessage());
        return msg[0];
    }

}
