package com.cryptoloanfront.factory;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FieldFactory {

    public TextField createTextField(String fieldName) {
        return createTextField(fieldName,fieldName);
    }

    public TextField createTextField(String fieldName, String placeHolder) {
        TextField field = new TextField(fieldName);
        field.setPlaceholder(placeHolder);
        field.setId("inputField");
        return field;
    }

    public NumberField createNumberField(String fieldName, String placeHolder) {
        NumberField field = new NumberField(fieldName);
        field.setPlaceholder(placeHolder);
        field.setId("inputField");
        return field;
    }

    public NumberField createSmallNumberField(String fieldName, String placeholder) {
        NumberField numberField = new NumberField(fieldName);
        numberField.setClassName("amountBox");
        numberField.setId("inputField");
        numberField.setPlaceholder(placeholder);
        return numberField;
    }

    public NumberField createIdNumberField() {
        return createIdNumberField("ID *","0");
    }

    public NumberField createDeleteIdNumberField() {
        return createIdNumberField("ID","0");
    }

    public NumberField createIdNumberField(String fieldName, String placeHolder) {
        NumberField field = new NumberField(fieldName);
        field.setId("idInput");
        field.setPlaceholder(placeHolder);
        return field;
    }

    public Button createButton(String fieldName) {
        return new Button(fieldName);
    }

    public Button createAddButton() {
        return createButton("Add");
    }

    public Button createUpdateButton() {
        return createButton("Update");
    }

    public Button createClearButton() {
        return createButton("Clear");
    }

    public Button createDeleteButton() {
        Button button = createButton("Delete");
        button.setId("deleteButton");
        return button;
    }

    public ComboBox<String> createComboBox(String fieldName, List<String> items, String placeholder, String className) {
        ComboBox<String> box = new ComboBox<>(fieldName);
        box.setItems(items);
        box.setPlaceholder(placeholder);
        box.setClassName(className);
        box.setId("inputField");
        return box;
    }

    public DatePicker createDatePicker(String fieldName) {
        DatePicker date = new DatePicker(fieldName);
        date.setPlaceholder("1.12.2000");
        date.setClassName("dateBox");
        date.setId("inputField");
        return date;
    }

}
