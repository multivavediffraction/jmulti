package org.structureviewer;

import com.aeonium.javafx.validation.FXAbstractValidator;
import com.aeonium.javafx.validation.annotations.FXNumber;
import com.aeonium.javafx.validation.exceptions.ValidationException;
import javafx.scene.control.TextInputControl;
import javafx.util.converter.NumberStringConverter;

import java.text.ParseException;

public class NumberFormatterValidator extends FXAbstractValidator<TextInputControl, FXNumber> {
    @Override
    public void validate(TextInputControl control, FXNumber annotation) throws ValidationException {
        NumberStringConverter conv = new NumberStringConverter();

        try {
            conv.fromString(control.getText());
            this.isValid.set(true);
        } catch (Exception ex) {
            this.isValid.set(false);
            throw new ValidationException(annotation.message());
        }
    }
}
