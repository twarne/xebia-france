package fr.xebia.demo.wicket.blog.view.util;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DateTimeField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;


public class CustomDateTimeField extends DateTimeField {

    private static final long serialVersionUID = 1L;

    public CustomDateTimeField(String id, IModel model) {
        super(id, model);
    }

    @Override
    protected DateTextField newDateTextField(String id, PropertyModel dateFieldModel) {
        DateTextField newDateTextField = super.newDateTextField(id, dateFieldModel);
        newDateTextField.add(new AttributeModifier("class", true, new Model("formFields")));
        return newDateTextField;
    }
}
